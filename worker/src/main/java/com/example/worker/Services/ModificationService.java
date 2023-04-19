package com.example.worker.Services;

import com.example.worker.FeedBack;
import com.example.worker.DB.DAO;
import com.example.worker.Indexing.DatabaseIndex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class ModificationService {
    private final DAO dao;
    @Autowired
    public ModificationService(DAO dao){
        this.dao = dao;
    }

    public FeedBack initDatabase(String database){
        return dao.createDatabase(database);
    }

    public FeedBack initCollection(String Database , String Collection, String schema){
        return dao.addCollection(Database,Collection,schema);
    }

    public FeedBack addRecord(String Database, String Collection, String json){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = null;
        try {
            objectNode = objectMapper.readTree(json).deepCopy();
            String id = objectNode.get("Id").asText();
            DatabaseIndex index = DatabaseIndex.getInstance();
            if( index.isIndexed(Database,Collection) ){
                HashMap<String, List<Integer>> map = index.getMap(Database,Collection,"Id");
                if(map.containsKey(id))
                    return new FeedBack("Id " + id + " is used",300);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        FeedBack response = dao.addRecord(Database,Collection,json);

        if(response.getStatusCode() >= 300)
            return response;

        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.convertValue(objectMapper.readTree(json),
                    new TypeReference<>() {
                    });
        } catch (JsonProcessingException e) {
            return new FeedBack("json unable to be read", 500);
        }

        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            list.add(entry.getKey());
        }

        DatabaseIndex index = DatabaseIndex.getInstance();
        if( !index.isIndexed(Database))
            index.initIndex(Database);

        if( !index.isIndexed(Database,Collection) )
            index.addCollection(Database,Collection,list.toArray(new String[0]));


        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            index.indexProperties(Database, Collection, entry.getKey(), entry.getValue().toString(), (dao.arrSize(Database,Collection)-1));
        }
        return response;
    }
    public FeedBack dropCollection(String Database, String Collection){
        DatabaseIndex index = DatabaseIndex.getInstance();
        index.DropCollection(Database,Collection);
        return dao.dropCollection(Database,Collection);
    }
    public FeedBack dropDatabase(String Database){
        DatabaseIndex index = DatabaseIndex.getInstance();
        index.DropDatabase(Database);
        return dao.dropDatabase(Database);
    }
    public FeedBack deleteById(String database, String Collection, String id){
        FeedBack response = dao.deleteWithId(database, Collection, id);
        if(response.getStatusCode() >= 300)
            return response;

        DatabaseIndex index = DatabaseIndex.getInstance();
        HashMap<String, List<Integer>> map1 = index.getMap(database, Collection, "Id");
        int idx = map1.get(id).get(0);
        index.deleteByIdx(database,Collection,idx);
        return response;
    }

    public FeedBack deleteAllOfValue(String Database, String Collection, String Property, String value){
        DatabaseIndex index = DatabaseIndex.getInstance();
        HashMap<String, List<Integer>> map = index.getMap(Database,Collection,Property);
        if(!map.containsKey(value)) {
            List<Integer> list = map.get(value);

            for(int X : list){
                ObjectNode obj = dao.objectAt(Database,Collection,X);
                String id = obj.get("Id").asText();
                deleteById(Database,Collection,id);
            }

        }
        return new FeedBack("deleting all " +Property +':' + value +" done",203);
    }

}