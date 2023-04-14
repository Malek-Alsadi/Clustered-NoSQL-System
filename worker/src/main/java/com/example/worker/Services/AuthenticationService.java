package com.example.worker.Services;

import com.example.worker.DB.DAO;
import com.example.worker.Token.TokenAuth;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthenticationService {
    private final DAO dao;
    @Autowired
    public AuthenticationService(DAO dao){
        this.dao = dao;
    }

    @Async
    public boolean generalAuthenticated(String token,String password){
        if(token == null || token.length() == 0)
            return false;

        TokenAuth tokenAuth = TokenAuth.getInstance();
        String id = tokenAuth.verify(token,"Al-Sadi");
        if(id == null)
            return false;

        boolean users =  dao.getById("main","users",id) != null;
        boolean managers = dao.getById("main","managers",id) != null;

        if(users){
            ObjectNode objectNode = dao.getById("main","users",id);
            String pass = objectNode.get("Password").asText();
            users = pass.equals(password);
        }
        if(managers){
            ObjectNode objectNode = dao.getById("main","managers",id);
            String pass = objectNode.get("Password").asText();
            managers = pass.equals(password);
        }

        return users||managers;
    }
    @Async
    public boolean managerAuthenticated(String token,String password){
        if(token == null || token.length() == 0)
            return false;

        TokenAuth tokenAuth = TokenAuth.getInstance();
        String id = tokenAuth.verify(token,"Al-Sadi");
        if(id == null)
            return false;

        ObjectNode objectNode = dao.getById("main","managers",id);
        String pass = objectNode.get("Password").asText();
        boolean managers = pass.equals(password);

        return managers;
    }
    @Async
    public String[] extractCredentials(String authorizationHeader) {
        // Remove the "Basic " prefix from the header value
        String encodedCredentials = authorizationHeader.substring("Basic ".length());

        // Decode the Base64-encoded credentials
        String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));

        // Split the credentials into username and password
        String[] credentials = decodedCredentials.split(":", 2);

        return credentials;
    }
}
