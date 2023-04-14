package com.atypon.demo.Services;

import com.atypon.demo.Database.Auth_DAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final Auth_DAO authDao;
    @Autowired
    public AuthService(Auth_DAO authDao){
        this.authDao = authDao;
    }
    public JsonNode connectUser(String id, String password,boolean isManager){
        StringBuilder response = authDao.connectNewUser(id,password,isManager);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            if(rootNode.has("Token") && !rootNode.get("Token").asText().equals("FAILED"))
                authDao.addUser(rootNode,isManager);

            return rootNode;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean checkCredentials(String Token, String Password, boolean isManager){
        String url = authDao.getURL(Token,isManager);
        if(url == null) return false;
        return authDao.checkUser(Token,Password,isManager,url);
    }
    public String getURL(String Token, boolean isManager){
        return authDao.getURL(Token,isManager);
    }

}
