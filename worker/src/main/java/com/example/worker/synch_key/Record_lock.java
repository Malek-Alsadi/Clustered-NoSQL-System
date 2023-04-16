package com.example.worker.synch_key;

import java.util.Objects;

public class Record_lock extends Collection_lock {
    private String Id;

    public Record_lock(String Database, String collection, String Id) {
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
        if (!(o instanceof Record_lock recordKey)) return false;
        if (!super.equals(o)) return false;

        Collection_lock key = ((Record_lock) o).getKey();
        return Objects.equals(getId(), recordKey.getId()) && key.equals(super.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
