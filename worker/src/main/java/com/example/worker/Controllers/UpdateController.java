package com.example.worker.Controllers;

import com.example.worker.FeedBack;
import com.example.worker.Services.QueryService;
import com.example.worker.Services.UpdateService;
import com.example.worker.clusterControl.Broadcast;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/update")
public class UpdateController {

    private final UpdateService updateService;
    @Autowired
    public UpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping("/{db_name}/{collection}/{id}/{property}")
    @ResponseBody
    public FeedBack Update(@PathVariable("db_name") String Database,
                           @PathVariable("collection") String Collection,
                           @PathVariable("id") String id,
                           @PathVariable("property") String Property,
                           @RequestParam("value") String value) {

        return updateService.update(Database,Collection,id,Property,value);
    }
    @PutMapping("/{db_name}/{collection}/{id}")
    @ResponseBody
    public FeedBack Update(@PathVariable("db_name") String Database,
                           @PathVariable("collection") String Collection,
                           @PathVariable("id") String id,
                           @RequestBody JsonNode requestBody) {
        // Extract the Property and value parameters from the request body
        String Property = requestBody.get("Property").asText();
        String value = requestBody.get("value").asText();

        return updateService.update(Database, Collection, id, Property, value);
    }

    @GetMapping("/affinity/{db_name}/{collection}/{id}/{property}")
    @ResponseBody
    public FeedBack affinityCheckerUpdate(@PathVariable("db_name") String Database,
                                          @PathVariable("collection") String Collection,
                                          @PathVariable("id") String id,
                                          @PathVariable("property") String Property,
                                          @RequestParam("value") String value) {
        Broadcast broadcast = new Broadcast();
        return broadcast.updateRecord(Database,Collection,id,Property,value);
    }

}
