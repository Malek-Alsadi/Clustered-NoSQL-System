package com.atypon.demo.Cache;

import java.util.Objects;

public class Token_Type {
    private String Token;
    private String Type;

    public Token_Type(String token, String type) {
        Token = token;
        Type = type;
    }

    public Token_Type() {
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token_Type tokenType)) return false;
        return Objects.equals(getToken(), tokenType.getToken()) && Objects.equals(getType(), tokenType.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getType());
    }
}
