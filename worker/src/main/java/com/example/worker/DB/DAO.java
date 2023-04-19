package com.example.worker.DB;

import com.example.worker.FeedBack;
import com.example.worker.Indexing.DatabaseIndex;
import com.example.worker.synch_locks.Collection_lock;
import com.example.worker.synch_locks.Database_lock;
import com.example.worker.synch_locks.Record_lock;
import com.example.worker.synch_locks.locks_key;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.HashMap;
import java.util.List;

@Repository
public class DAO {
    private String DirPath = "./";
    private String getPath(String database, boolean schema){
        if(schema){
            return DirPath + database + "/schemas";
        }else
            return DirPath + database;
    }
    public FeedBack createDatabase(String database) {
        Database_lock lock = new Database_lock(database);
        locks_key chain = locks_key.getInstance();
        Object key = chain.getLock(lock);
        synchronized (key) {
            File dbDir = new File(getPath(database, false));
            if (dbDir.exists() && dbDir.isDirectory()) {
                chain.dropLock(lock);
                return new FeedBack("Database already exists.", 300);
            }

            if (!dbDir.mkdirs()) {
                chain.dropLock(lock);
                return new FeedBack("Error creating database.", 400);
            } else {
                File schemasDir = new File(getPath(database, true));
                schemasDir.mkdirs();
                chain.dropLock(lock);
                return new FeedBack("Database created successfully.", 201);
            }
        }
    }

    public FeedBack addCollection(String Database, String Collection, String schema) {
        Collection_lock lock = new Collection_lock(Database,Collection);
        locks_key chain = locks_key.getInstance();
        Object key = chain.getLock(lock);
        synchronized (key) {
            try {
                File dbDir = new File(getPath(Database, false));
                if (!dbDir.exists()) {
                    return new FeedBack("Database: " + Database + " not exist", 302);
                }

                File schemaFile = new File(getPath(Database, true) + '/' + Collection + ".json");
                FileWriter writer = new FileWriter(schemaFile);
                writer.write(schema);
                writer.close();

                File jsonFile = new File(getPath(Database, false) + '/' + Collection + ".json");
                createEmptyJsonArray(jsonFile);
                chain.dropLock(lock);
                return new FeedBack("JSON saved successfully.", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void createEmptyJsonArray(File file){
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("myArray", arrayNode);
        try {
            objectMapper.writeValue(file, objectNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FeedBack dropCollection(String Database, String Collection){
        Collection_lock lock = new Collection_lock(Database,Collection);
        locks_key chain = locks_key.getInstance();
        Object key = chain.getLock(lock);
        synchronized (key) {
            File dbFile = new File(getPath(Database, false));
            if (!dbFile.exists() || !dbFile.isDirectory()) {
                chain.dropLock(lock);
                return new FeedBack("Database: " + Database + " not exist", 302);
            }
            File schemaFile = new File(getPath(Database, true) + '/' + Collection + ".json");
            File jsonFile = new File(getPath(Database, false) + '/' + Collection + ".json");
            if (!schemaFile.exists() || !jsonFile.exists()) {
                chain.dropLock(lock);
                return new FeedBack("Collection: " + Collection + " not exist", 302);
            }
            schemaFile.delete();
            jsonFile.delete();
            chain.dropLock(lock);
            return new FeedBack("dropping Collection " + Collection + " done", 202);
        }
    }
    public FeedBack dropDatabase(String Database){
        Database_lock lock = new Database_lock(Database);
        locks_key chain = locks_key.getInstance();
        Object key = chain.getLock(lock);
        synchronized (key) {
            File dbFile = new File(getPath(Database, false));
            if (!dbFile.exists() || !dbFile.isDirectory()) {
                chain.dropLock(lock);
                return new FeedBack("Database " + Database + " not exist", 302);
            }
            try {
                FileUtils.deleteDirectory(dbFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chain.dropLock(lock);
            return new FeedBack("dropping Database " + Database + " done", 202);
        }
    }
    public FeedBack addRecord(String Database, String Collection, String Json){

        File dbDir = new File(getPath(Database,false));
        File collFile = new File(getPath(Database,false) + '/' + Collection + ".json");
        String currentDir = System.getProperty("user.dir");
        if (!dbDir.exists() )
            return new FeedBack("Database: " + Database + " does not exist ,we're in " + currentDir , 302);
        if(!collFile.exists()) {
            return new FeedBack("Collection:" + Collection + " does not exist", 302);
        }
        File schemaFile = new File(getPath(Database,true) + '/' + Collection + ".json");

        schemaCheck checker = new schemaCheck(Json,schemaFile);
        if( checker.checkJson() ){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonPath = getPath(Database,false) + '/' + Collection + ".json";
                ObjectNode root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
                ArrayNode myArray = (ArrayNode) root.get("myArray");
                if (myArray == null) {
                    myArray = objectMapper.createArrayNode();
                    root.set("myArray", myArray);
                }


                JsonNode jsonNode = objectMapper.readTree(Json);

                String Id = jsonNode.get("Id").asText();
                Record_lock lock = new Record_lock(Database,Collection,Id);
                locks_key chain = locks_key.getInstance();
                Object key = chain.getLock(lock);
                synchronized ( key ) {

                    myArray.add(jsonNode);

                    FileWriter writer = new FileWriter(jsonPath);
                    objectMapper.writeValue(writer, root);
                    writer.close();
                    chain.dropLock(lock);
                }

            } catch (Exception e) {
                return new FeedBack(e.getMessage(),305);
            }
            return new FeedBack("Adding done successfully",203);
        }

        return new FeedBack("JSON doesn't match schema",306);
    }

    public ObjectNode getById(String Database, String Collection, String id) {


        String jsonPath = getPath(Database,false) + '/' + Collection + ".json";

        File dbDir = new File(getPath(Database,false));
        if (!dbDir.exists()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = null;
        try {
            root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
            ArrayNode myArray = (ArrayNode) root.get("myArray");

            ObjectNode result = null;
            int idx = indexOfId(Database, Collection, id);
            if(idx == -1)
                return null;

            JsonNode jsonNode = myArray.get( idx );
            result = jsonNode.deepCopy();

            return result;
        } catch (IOException e) {
            return objectMapper.valueToTree(e.getMessage());
        }
    }

    public boolean checkDatabase(String database){
        File dbDir = new File( getPath(database,false) );
        return dbDir.isDirectory();
    }
    public boolean checkCollection(String Database, String Collection){
        File document = new File( getPath(Database,false) + '/' + Collection + ".json");
        File schema = new File(getPath(Database,true) + '/' + Collection + ".json");
        return document.exists() && schema.exists();
    }
    public FeedBack Update(String Database, String Collection, String Id, String Property, String value) {

        String jsonPath = getPath(Database,false) + '/' + Collection + ".json";
        File dbDir = new File(getPath(Database,false));
        if (!dbDir.exists()) {
            return new FeedBack("Database: " + Database + "not exist", 302);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode root = null;
        try {
            root = objectMapper.readValue(new File(jsonPath), ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayNode myArray = (ArrayNode) root.get("myArray");
        if (myArray == null) {
            return new FeedBack("myArray not found", 302);
        }

        int idx = indexOfId(Database,Collection, Id);
        JsonNode nodeToUpdate = null;
        if(idx != -1)
            nodeToUpdate = myArray.get( indexOfId(Database,Collection, Id) );


        if (nodeToUpdate == null) {
            return new FeedBack("Id: " + Id + " not found", 302);
        }
        Record_lock lock = new Record_lock(Database,Collection,Id);
        locks_key chain = locks_key.getInstance();
        Object key = chain.getLock(lock);
        synchronized (key) {

            ((ObjectNode) nodeToUpdate).put(Property, value);

            try {
                objectMapper.writeValue(new File(jsonPath), root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chain.dropLock(lock);
        }

        return new FeedBack("update done", 204);
    }

    private int indexOfId(String Database, String Collection, String id){
        DatabaseIndex index = DatabaseIndex.getInstance();
        if(!index.isIndexed(Database,Collection))
            return -1;
        HashMap<String, List<Integer>> map = index.getMap(Database, Collection, "Id");
        if(!map.containsKey(id) || map.get(id).isEmpty())
            return -1;
        int idx = map.get(id).get(0);
        return idx;
    }

    public FeedBack deleteWithId(String database, String Collection, String id) {
        String jsonPath = getPath(database,false) + '/' + Collection + ".json";
        File dbDir = new File(getPath(database,false));

        DatabaseIndex index = DatabaseIndex.getInstance();
        if(!index.isIndexed(database,Collection)) {
            return new FeedBack("Database or Collection does not exist", 302);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode root = null;
        try {
            root = objectMapper.readValue(new File(jsonPath), ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayNode myArray = (ArrayNode) root.get("myArray");
        if (myArray == null) {
            return new FeedBack("myArray not found", 302);
        }

        int idx = indexOfId(database,Collection,id);
        if(idx == -1)
            return new FeedBack("id: " + id + " not found" , 302);

        Record_lock lock = new Record_lock(database,Collection,id);
        locks_key chain = locks_key.getInstance();
        Object key = chain.getLock(lock);
        synchronized (key) {
            myArray.remove(idx);

            try {
                objectMapper.writeValue(new File(jsonPath), root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chain.dropLock(lock);
        }

        return new FeedBack("delete done", 202);
    }
    public int arrSize(String database, String Collection){
        String jsonPath = getPath(database,false) + '/' + Collection + ".json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode root = null;
            root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
            ArrayNode myArray = (ArrayNode) root.get("myArray");
            return myArray.size();
        } catch (IOException e) {
            return 0;
        }
    }
    public ObjectNode objectAt(String database, String Collection, int idx){
        String jsonPath = getPath(database,false) + '/' + Collection + ".json";
        File dbDir = new File(getPath(database,false));
        if (!dbDir.exists()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode root = null;
        try {
            root = objectMapper.readValue(new File(jsonPath), ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayNode myArray = (ArrayNode) root.get("myArray");
        if (myArray == null || idx < 0 || idx >= myArray.size()) {
            return null;
        }
        return myArray.get(idx).deepCopy();
    }
    public ArrayNode getAllFrom(String Database, String Collection) {
        String jsonPath = getPath(Database,false) + '/' + Collection + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = null;
        try {
            root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayNode myArray = (ArrayNode) root.get("myArray");

        return myArray;
    }
}
