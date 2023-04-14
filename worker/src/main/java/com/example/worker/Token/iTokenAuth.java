package com.example.worker.Token;

public interface iTokenAuth {
    public String generate(String payload);
    public String verify(String token, String secretKey);
}
