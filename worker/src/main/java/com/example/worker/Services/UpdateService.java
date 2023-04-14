package com.example.worker.Services;

import com.example.worker.FeedBack;
import com.example.worker.DB.DAO;
import com.example.worker.Indexing.DatabaseIndex;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UpdateService {
    int port;
    private final DAO dbHandler;
    private final ServerProperties serverProperties;
    @Autowired
    public UpdateService(DAO dbHandler, ServerProperties serverProperties) {
        this.dbHandler = dbHandler;
        this.serverProperties = serverProperties;
    }

    public FeedBack update(String Database, String Collection , String id, String Property, String value){

        ObjectNode obj = dbHandler.getById(Database,Collection,id);

        FeedBack response =  dbHandler.Update(Database,Collection,id,Property,value);
        if(response.getStatusCode() >= 300)
            return response;


        DatabaseIndex index = DatabaseIndex.getInstance();
        HashMap<String, List<Integer>> map1 = index.getMap(Database,Collection,"Id");
        int idx = map1.get(id).get(0);
        HashMap<String, List<Integer>> map = index.getMap(Database,Collection,Property);
        String oldValue = obj.get(Property).asText();
        map.get(oldValue).remove(idx);
        index.addToValue(Database,Collection,Property,value,idx);

        return response;
    }
    public boolean affinityCheck(String Database, String Collection, String id){
        ObjectNode objectNode = dbHandler.getById(Database,Collection,id);
        int affinity = objectNode.get("Affinity").asInt();
        return affinity == serverProperties.getPort()%10;
    }
}
