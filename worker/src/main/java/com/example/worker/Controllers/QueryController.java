package com.example.worker.Controllers;

import com.example.worker.Services.AuthenticationService;
import com.example.worker.Services.QueryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/get")
public class QueryController {
    private final QueryService queryService;
    private final AuthenticationService authenticationService;
    @Autowired
    public QueryController(QueryService queryService, AuthenticationService authenticationService){
        this.queryService = queryService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/{db_name}/{Collection}/{Property}/{value}")
    @ResponseBody
    public List<JsonNode> getByPropertyValue(@PathVariable("db_name") String database,
                                      @PathVariable("Collection") String Collection,
                                      @PathVariable("Property") String Property,
                                      @PathVariable("value") String value,
                                      @RequestHeader("authorization") String authHeader){

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new ArrayList<>();
        }

        List<JsonNode> JsonArr = null;
        try {
            JsonArr = queryService.getByPropertyValue(database,Collection,Property,value).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        ;
        return JsonArr;
    }

    @GetMapping("/{db_name}/{Collection}/{id}")
    @ResponseBody
    public JsonNode getById(@PathVariable("db_name") String database,
                            @PathVariable("Collection") String Collection,
                            @PathVariable("id") String id,
                            @RequestHeader("Authorization") String authHeader) {

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.valueToTree("Invalid Credentials");
        }
        ObjectNode objectNode = null;
        try {
            objectNode = queryService.getById(database,Collection,id).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        JsonNode jsonNode = (JsonNode) objectNode;
        return jsonNode;
    }

    @GetMapping("/allDatabases")
    public List<String> getDatabases(@RequestHeader("authorization") String authHeader){
        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new ArrayList<>();
        }
        try {
            return queryService.getDatabases().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/allCollections/{db_name}")
    public List<String> getCollection(@PathVariable("db_name") String Database,@RequestHeader("authorization") String authHeader){
        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new ArrayList<>();
        }
        try {
            return queryService.getCollections(Database).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("CheckConnection")
    public boolean checkConnection(@RequestHeader("authorization") String authHeader){
        String[] credentials = authenticationService.extractCredentials(authHeader);
        return authenticationService.generalAuthenticated(credentials[0],credentials[1]);
    }

    @GetMapping("/AllRecords/{db_name}/{Collection}")
    public ArrayNode getAll(@PathVariable("db_name") String database,
                            @PathVariable("Collection") String Collection,
                            @RequestHeader("Authorization") String authHeader) throws IOException, ExecutionException, InterruptedException {
        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.createArrayNode();
        }

        return queryService.getAll(database,Collection).get();
    }



}
