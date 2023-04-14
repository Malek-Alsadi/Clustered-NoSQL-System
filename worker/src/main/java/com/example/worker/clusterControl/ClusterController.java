package com.example.worker.clusterControl;


import com.example.worker.Controllers.ApiController;
import com.example.worker.FeedBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cluster")
public class ClusterController {
    private final ClusterService clusterService;

    @Autowired
    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }
    @GetMapping("/check/db/{db_name}")
    public boolean CheckDB(@PathVariable("db_name") String Database){
        return clusterService.checkDB(Database);
    }
    @GetMapping("/check/collection/{db_name}/{collection}")
    public boolean CheckCollection(@PathVariable("db_name") String Database,
                                   @PathVariable("collection") String Collection){
        return clusterService.checkCollection(Database,Collection);
    }
    @PostMapping("/check/record/{db_name}/{collection}")
    public boolean checkJson(@PathVariable("db_name") String Database,
                             @PathVariable("collection") String Collection,
                             @RequestBody String Json){
        return clusterService.checkJson(Database,Collection,Json);
    }
    @GetMapping("/check/id/{db_name}/{collection}/{id}")
    public boolean checkId(@PathVariable("db_name") String Database,
                           @PathVariable("collection") String Collection,
                           @PathVariable("id") String id){
        return clusterService.checkId(Database,Collection,id);
    }

    @GetMapping("/check/update/{db_name}/{collection}/{id}/{property}/{old}")
    public boolean checkProperty(@PathVariable("db_name") String Database,
                                 @PathVariable("collection") String Collection,
                                 @PathVariable("id") String id,
                                 @PathVariable("property") String Property,
                                 @PathVariable("old") String oldValue){
        return clusterService.checkProperty(Database,Collection,id,Property,oldValue);
    }

    @GetMapping("set/affinity")
    public FeedBack setAffinity(@RequestParam("no") int num){
        return clusterService.setAffinityFile(num);
    }

}
