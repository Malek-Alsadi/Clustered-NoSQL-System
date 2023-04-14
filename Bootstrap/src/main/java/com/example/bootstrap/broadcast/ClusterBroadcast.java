package com.example.bootstrap.broadcast;

import com.example.bootstrap.Database.Constants;
import com.example.bootstrap.httpRequest.GetRequest;
import com.example.bootstrap.httpRequest.PostRequest;
import com.example.bootstrap.httpRequest.RequestSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
public class ClusterBroadcast implements Broadcast ,Constants{
    private static final int PORT_NUMBER = 8090;
    private final String []hosts = {"http://worker1:","http://worker2:","http://worker3:","http://worker4:"};

    private static int idx = 3;
    public static int getIdx(){
        idx++;
        idx %= 4;
        return idx;
    }
    public String setUp(){
        String response = "";
        for(String url: hosts) {
            try {
                String host = url + PORT_NUMBER;


//                set up affinity to 0
                URL dist = new URL(host + "/cluster/set/affinity?no=0");
                RequestSender sender = new GetRequest();
                response += sender.sendHttpRequest(dist,"GET","").toString();


//                create main database for managers and users
                dist = new URL(host + "/create/db/main");
                response += sender.sendHttpRequest(dist,"GET","").toString();


//                create managers collection
                dist = new URL(host + "/create/collection/main/managers");
                sender = new PostRequest();
                String schema = getSchema();
                response += sender.sendHttpRequest(dist,"POST", schema).toString();


//                create users collection
                dist = new URL(host + "/create/collection/main/users");
                response += sender.sendHttpRequest(dist,"POST",schema).toString();


            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return response;
    }
    public void sendCredentials(String JSON, String type) {
        try {
            URL urlObj = new URL(hosts[idx] + PORT_NUMBER + "/add/record/main/" + type);

            RequestSender sender = new PostRequest();
            StringBuilder response = sender.sendHttpRequest(urlObj, "POST", JSON);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String chooseUrl(int idx) {
        return hosts[idx];
    }
}
