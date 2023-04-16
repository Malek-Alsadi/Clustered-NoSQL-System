package com.example.worker.synch_key;

import java.util.Objects;

public class Collection_lock extends Database_lock {
    private String Collection;

    public Collection_lock(String Database, String collection) {
        super(Database);
        Collection = collection;
    }

    public void setCollection(String collection) {
        Collection = collection;
    }

    public String getCollection() {
        return Collection;
    }

    @Override
    public Collection_lock getKey(){
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collection_lock that)) return false;
        if (!super.equals(o)) return false;

        Database_lock key = ((Collection_lock) o).getKey();
        return Objects.equals(getCollection(), that.getCollection()) && key.equals(super.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCollection());
    }
}
