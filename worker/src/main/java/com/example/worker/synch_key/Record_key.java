package com.example.worker.synch_key;

import java.util.Objects;

public class Record_key extends Collection_key{
    private String Id;

    public Record_key(String Database, String collection, String Id) {
        super(Database, collection);
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record_key recordKey)) return false;
        if (!super.equals(o)) return false;

        Collection_key key = ((Record_key) o).getKey();
        return Objects.equals(getId(), recordKey.getId()) && key.equals(super.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
