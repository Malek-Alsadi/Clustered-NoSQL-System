package com.example.worker.Controllers;

import com.example.worker.Affinity.affinitySetter;
import com.example.worker.FeedBack;
import com.example.worker.Services.AuthenticationService;
import com.example.worker.Services.QueryService;
import com.example.worker.clusterControl.Broadcast;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api")
public class ApiController implements affinitySetter{
    private final AuthenticationService authenticationService;
    private final QueryService queryService;

    @Autowired
    public ApiController(AuthenticationService authenticationService, QueryService queryService) {
        this.authenticationService = authenticationService;
        this.queryService = queryService;
    }

    @GetMapping("/create/db/{db_name}")
    public FeedBack createDatabase(@PathVariable("db_name") String dbName,
                                   @RequestHeader("authorization") String authHeader) {
        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }

        Broadcast broadcast = new Broadcast();
        if(!broadcast.DBCheck(dbName)){
            return broadcast.buildDB(dbName);
        }
        return new FeedBack("building failed",605);
    }

    @PostMapping("/create/collection/{db_name}/{collection}")
    public FeedBack createCollection(@PathVariable("db_name") String Database,
                                     @PathVariable("collection") String Collection,
                                     @RequestBody String Json,
                                     @RequestHeader("authorization") String authHeader) {

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }

        Broadcast broadcast = new Broadcast();
        if(!broadcast.checkCollection(Database,Collection)){
            return broadcast.buildCollection(Database,Collection,Json);
        }
        return new FeedBack("building failed",605);
    }

    @PostMapping("/add/record/{db_name}/{Collection}")
    public FeedBack addRecord(@PathVariable("db_name") String Database,
                              @PathVariable("Collection") String Collection,
                              @RequestBody String Json,
                              @RequestHeader("authorization") String authHeader){

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }

        Broadcast broadcast = new Broadcast();
        if(broadcast.checkRecord(Database,Collection,Json))
            return broadcast.addRecord(Database,Collection,Json);

        return new FeedBack("checking DB,Collection, Json to add record failed",605);
    }

    @DeleteMapping("/delete/record/{db_name}/{Collection}/{Id}")
    public FeedBack deleteWithId(@PathVariable("db_name") String Database,
                                 @PathVariable("Collection") String Collection,
                                 @PathVariable("Id") String id,
                                 @RequestHeader("authorization") String authHeader){

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.managerAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }
        Broadcast broadcast = new Broadcast();
        if(broadcast.checkId(Database,Collection,id)){
            return broadcast.deleteRecord(Database,Collection,id);
        }
        return new FeedBack("Checking Id: " + id + " to Delete failed",605);
    }

    @DeleteMapping("/delete/collection/{db_name}/{Collection}")
    public FeedBack deleteCollection(@PathVariable("db_name") String Database,
                                     @PathVariable("Collection") String Collection,
                                     @RequestHeader("authorization") String authHeader){

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.managerAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }

        Broadcast broadcast = new Broadcast();
        if(broadcast.checkCollection(Database,Collection))
            return broadcast.deleteCollection(Database,Collection);
        return new FeedBack("Checking Collection to Delete, Collection name: " + Collection + " failed",605);
    }

    @DeleteMapping("/delete/db/{db_name}")
    public FeedBack deleteDatabase(@PathVariable("db_name") String Database,
                                   @RequestHeader("authorization") String authHeader){

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.managerAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }

        Broadcast broadcast = new Broadcast();
        if(broadcast.DBCheck(Database))
            return broadcast.deleteDB(Database);
        return new FeedBack("Check Database to Delete, DB name: " + Database + " failed",605);
    }

    @PutMapping("/update/{db_name}/{Collection}/{Id}")
    @ResponseBody
    public FeedBack Update(@PathVariable("db_name") String Database,
                           @PathVariable("Collection") String Collection,
                           @PathVariable("Id") String id,
                           @RequestBody Map<String, Object> requestMap,
                           @RequestHeader("authorization") String authHeader) {

        String[] credentials = authenticationService.extractCredentials(authHeader);
        if(!authenticationService.generalAuthenticated(credentials[0],credentials[1])){
            return new FeedBack("Authentication failed",606);
        }

        String Property = (String) requestMap.get("Property");
        String value = (String) (requestMap.get("value") + "");

        ObjectNode obj = null;
        try {
            obj = queryService.getById(Database,Collection,id).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        String oldValue = "";
        if(obj != null)
            oldValue = obj.get(Property).asText();

        Broadcast broadcast = new Broadcast();
        if(broadcast.checkId(Database,Collection,id) && broadcast.checkToUpdate(Database,Collection,id,Property,oldValue)){
            return broadcast.searchAffinity(Database,Collection,id,Property,value,buildPort());
        }
        return new FeedBack("Checking Record to Update failed: broadcast stage",605);
    }

}
