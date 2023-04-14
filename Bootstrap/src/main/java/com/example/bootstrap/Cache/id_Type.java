package com.example.bootstrap.Cache;

import java.util.Objects;

public class id_Type {
    private String Id;
    private String Type;

    public id_Type(String id, String type) {
        Id = id;
        Type = type;
    }

    public id_Type() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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
        if (!(o instanceof id_Type idType)) return false;
        return Objects.equals(getId(), idType.getId()) && Objects.equals(getType(), idType.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType());
    }
}
