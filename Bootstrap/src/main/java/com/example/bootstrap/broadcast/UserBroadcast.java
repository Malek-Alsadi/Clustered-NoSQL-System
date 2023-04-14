package com.example.bootstrap.broadcast;

import org.springframework.stereotype.Service;

@Service
public class UserBroadcast implements Broadcast{
    private final static String host = "http://localhost:";
    private static final String []urls = {host + "8090",host + "8091", host + "8092", host + "8093"};

    @Override
    public String chooseUrl(int idx){
        return urls[idx];
    }
}
