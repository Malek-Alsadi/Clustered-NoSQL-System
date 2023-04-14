package com.example.bootstrap.Controllers;

import com.example.bootstrap.Services.UserAuth;
import com.example.bootstrap.Services.UsersService;
import com.example.bootstrap.Token.AES;
import com.example.bootstrap.broadcast.ClusterBroadcast;
import com.example.bootstrap.broadcast.UserBroadcast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController implements AES {
    private final UsersService usersService;
    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }
    @GetMapping("/setUp")
    public String setUP(){
        ClusterBroadcast broadcast = new ClusterBroadcast();
        return broadcast.setUp();
    }

    @GetMapping("/connect")
    public Map<String, String> ConnectUser(@RequestParam("type") String type, @RequestHeader("Authorization") String authHeader) {
        UserAuth userAuth = new UserAuth();
        String[] credentials = userAuth.extractCredentials(authHeader);
        String token = encrypt(credentials[0], "Al-Sadi");
        String JsonString = userAuth.buildJSON(credentials[0], credentials[1]);

        Map<String, String> response = new HashMap<>();
        if(usersService.exist(credentials[0],type)){
            response.put("URL","FAILED");
            response.put("Token","FAILED");
            return response;
        }

        usersService.addUser(JsonString,type);
        ClusterBroadcast clusterBroadcast = new ClusterBroadcast();
        int urlIdx = ClusterBroadcast.getIdx();
        clusterBroadcast.sendCredentials(JsonString,type);

        UserBroadcast userBroadcast = new UserBroadcast();
        String url = userBroadcast.chooseUrl(urlIdx);

        response.put("URL", url);
        response.put("Token", token);

        return response;
    }

}
