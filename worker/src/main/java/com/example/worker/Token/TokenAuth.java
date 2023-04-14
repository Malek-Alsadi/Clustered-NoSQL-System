package com.example.worker.Token;

import io.jsonwebtoken.ExpiredJwtException;

public class TokenAuth implements iTokenAuth {
    private static TokenAuth instance;
    public static TokenAuth getInstance(){
        if(instance == null)
            instance = new TokenAuth();
        return instance;
    }
    @Override
    public String generate(String payload) {
        String token = AES.encrypt(payload,"Al-Sadi");
        return token;
    }

    @Override
    public String verify(String token, String secretKey) {
        String id = null;
        id = AES.decrypt(token,secretKey);
        return id;
    }
}
