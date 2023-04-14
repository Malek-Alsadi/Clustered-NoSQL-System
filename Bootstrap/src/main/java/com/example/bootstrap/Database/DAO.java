package com.example.bootstrap.Database;

import com.example.bootstrap.Cache.LFUCache;
import com.example.bootstrap.Cache.id_Type;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Repository
public class DAO implements Constants{
    String path = "./";
    public void addUser(String JSON, String userType){
        File managers = new File(path + userType);
        if( !managers.exists() ) {
            managers.mkdir();
            File json = new File(path + userType + '/' + userType + ".json");
            createEmptyJsonArray(json);
        }

        File schemaFile = new File(path + "Schema.json");
        if(!schemaFile.exists()){
            try {
                FileWriter writer = new FileWriter(schemaFile);
                writer.write(getSchema());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        SchemaCheck check = new SchemaCheck(JSON,schemaFile);
        if(!check.checkJson()){
            return;
        }

        try {
            File json = new File(path + userType + '/' + userType + ".json");
            String jsonPath = json.getPath();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
            ArrayNode myArray = (ArrayNode) root.get("myArray");
            if (myArray == null) {
                myArray = objectMapper.createArrayNode();
                root.set("myArray", myArray);
            }
            JsonNode jsonNode = objectMapper.readTree(JSON);

            myArray.add(jsonNode);
            FileWriter writer = new FileWriter(jsonPath);
            objectMapper.writeValue(writer, root);
            writer.close();

            ObjectNode objectNode = jsonNode.deepCopy();
            LFUCache cache = LFUCache.getInstance();
            String id = objectNode.get("Id").asText();
            cache.put(id,true);

        } catch (Exception e) {
            throw new RuntimeException(e);
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
    public boolean userExist(String id, String userType){
        try {
            File file = new File(path + userType);
            if(!file.exists())
                return false;
            File json = new File(path + userType + '/' + userType + ".json");
            if(!json.exists())
                return false;

            String jsonPath = json.getPath();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode root = null;
            root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
            ArrayNode myArray = (ArrayNode) root.get("myArray");
            return search(id,myArray,userType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public boolean search(String token , ArrayNode myArray,String userType){
        for (JsonNode node : myArray) {
            ObjectNode objectNode = node.deepCopy();
            if(node.has("Id")) {
                String str = objectNode.get("Id").asText();
                if (str != null && str.equals(token)) {
                    id_Type key = new id_Type(str,userType);
                    LFUCache cache = LFUCache.getInstance();
                    cache.put(key,true);
                    return true;
                }
            }
        }
        return false;
    }

}
