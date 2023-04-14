package com.example.worker.clusterControl;

import com.example.worker.Affinity.affinitySetter;
import com.example.worker.DB.DAO;
import com.example.worker.DB.schemaCheck;
import com.example.worker.FeedBack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ClusterService implements affinitySetter {
    private final DAO dbHandler;
    @Autowired
    public ClusterService(DAO dbHandler){
        this.dbHandler = dbHandler;
    }

    public boolean checkDB(String database){
        return dbHandler.checkDatabase(database);
    }

    public boolean checkCollection(String Database, String Collection){
        return dbHandler.checkCollection(Database,Collection);
    }

    public boolean checkJson(String Database, String Collection, String Json){
        File schemaFile = new File("./" + Database + "/schemas/" + Collection + ".json");
        schemaCheck checker = new schemaCheck(Json,schemaFile);
        if(!checker.checkJson())
            return false;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = null;
        try {
            objectNode = objectMapper.readTree(Json).deepCopy();
        } catch (JsonProcessingException e) {
            return false;
        }
        String id = objectNode.get("Id").asText();
        return dbHandler.getById(Database,Collection,id) == null;
    }

    public boolean checkId(String Database, String Collection, String id){
        return dbHandler.getById(Database,Collection,id) != null;
    }

    public boolean checkProperty(String Database, String Collection, String id, String Property, String oldValue){
        ObjectNode obj = dbHandler.getById(Database,Collection,id);
        if(obj == null)
            return false;
        String curValue = obj.get(Property).asText();
        return oldValue.equals(curValue);
    }
}
