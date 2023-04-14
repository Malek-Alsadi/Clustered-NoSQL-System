package com.example.worker.Services;

import com.example.worker.DB.DAO;
import com.example.worker.Indexing.DatabaseIndex;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class QueryService {
    private final DAO dao;
    @Autowired
    public QueryService(DAO dao){
        this.dao = dao;
    }

    @Async
    public Future< ObjectNode > getById(String Database, String Collection, String id){
        ObjectNode obj = dao.getById(Database, Collection, id);
        return CompletableFuture.completedFuture(obj);
    }
    @Async
    public Future< ArrayNode > getAll(String Database, String Collection){
        Future empty = CompletableFuture.completedFuture(new ArrayList<>());
        ArrayNode myArray = dao.getAllFrom(Database,Collection);
        if(myArray == null)
            return empty;

        return CompletableFuture.completedFuture(myArray);
    }
    @Async
    public Future< List<JsonNode> > getByPropertyValue(String Database, String Collection , String Property, String value){
        Future empty = CompletableFuture.completedFuture(new ArrayList<>());
        DatabaseIndex index = DatabaseIndex.getInstance();
        if(!index.isIndexed(Database,Collection))
            return empty;
        HashMap<String, List<Integer>> map = null;
        try {
            map = index.getMap(Database, Collection, Property);
        } catch (Exception e) {
            return empty;
        }

        if(!map.containsKey(value))
            return empty;

        List <Integer> indexes = map.get(value);
        if(indexes == null)
            return empty;

        List<JsonNode> ans = new ArrayList<>();
        for(int idx : indexes){
            JsonNode jsonNode = (JsonNode) dao.objectAt(Database,Collection,idx);
            ans.add(jsonNode);
        }

        return CompletableFuture.completedFuture(ans);
    }

    @Async
    public Future< List<String> > getCollections(String Database){
        DatabaseIndex index = DatabaseIndex.getInstance();
        return CompletableFuture.completedFuture( index.getCollections(Database) );
    }
    @Async
    public Future< List<String> > getDatabases(){
        DatabaseIndex index = DatabaseIndex.getInstance();
        return CompletableFuture.completedFuture( index.getDatabases() );
    }


}
