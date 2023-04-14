package com.example.worker.Controllers;

import com.example.worker.FeedBack;
import com.example.worker.Services.ModificationService;
import com.example.worker.Services.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class ModifyController {
    private final ModificationService modificationService;

    @Autowired
    public ModifyController(ModificationService modificationService, QueryService queryService){
        this.modificationService = modificationService;
    }

    @GetMapping("/create/db/{db_name}")
    @ResponseBody
    public FeedBack createDatabase(@PathVariable("db_name") String dbName) {
        return modificationService.initDatabase(dbName);
    }

    @PostMapping("/create/collection/{db_name}/{Collection}")
    @ResponseBody
    public FeedBack createCollection(@PathVariable("db_name") String Database,
                                     @PathVariable("Collection") String Collection,
                                     @RequestBody String Json) {
        return modificationService.initCollection(Database,Collection,Json);
    }

    @PostMapping("/add/record/{db_name}/{Collection}")
    @ResponseBody
    public FeedBack addRecord(@PathVariable("db_name") String Database,
                              @PathVariable("Collection") String Collection,
                              @RequestBody String Json){

        return modificationService.addRecord(Database,Collection,Json);
    }

    @DeleteMapping("/delete/{db_name}/{Collection}/{Id}")
    @ResponseBody
    public FeedBack deleteWithId(@PathVariable("db_name") String Database,
                                 @PathVariable("Collection") String Collection,
                                 @PathVariable("Id") String id){
        return modificationService.deleteById(Database, Collection, id);
    }

    @DeleteMapping("/delete/{db_name}/{Collection}")
    @ResponseBody
    public FeedBack deleteCollection(@PathVariable("db_name") String Database,
                                     @PathVariable("Collection") String Collection){

        return modificationService.dropCollection(Database,Collection);
    }

    @DeleteMapping("/delete/{db_name}")
    @ResponseBody
    public FeedBack deleteDatabase(@PathVariable("db_name") String Database){

        return modificationService.dropDatabase(Database);
    }
    @DeleteMapping("delete/property/{Database}/{Collection}/{Property}/{value}")
    @ResponseBody
    public FeedBack deleteAllOfValue( @PathVariable("Database") String Database,
                                      @PathVariable("Collection") String Collection,
                                      @PathVariable("Property") String Property,
                                      @PathVariable("value") String value){
        return modificationService.deleteAllOfValue(Database,Collection,Property,value);
    }

}
