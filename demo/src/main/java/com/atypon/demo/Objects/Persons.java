package com.atypon.demo.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Persons {
    private String Id;
    private String Name;

    public Persons(String Id, String Name) {
        this.Id = Id;
        this.Name = Name;
    }

    public Persons() {
    }

    @JsonProperty("Id")
    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
