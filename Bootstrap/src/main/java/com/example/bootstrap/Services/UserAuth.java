package com.example.bootstrap.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Base64;

public class UserAuth {
    public String buildJSON( String Id, String Password){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("Id", Id);
        objectNode.put("Password", Password);
        String jsonString = objectNode.toString();
        return jsonString;
    }

    public String[] extractCredentials(String authorizationHeader) {
        String encodedCredentials = authorizationHeader.substring("Basic ".length());

        String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));

        String[] credentials = decodedCredentials.split(":", 2);

        return credentials;
    }
}
