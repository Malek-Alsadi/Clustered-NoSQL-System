package com.example.worker.synch_key;

import java.util.Objects;

public class Collection_key extends Database_Key {
    private String Collection;

    public Collection_key(String Database,String collection) {
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
    public Collection_key getKey(){
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collection_key that)) return false;
        if (!super.equals(o)) return false;

        Database_Key key = ((Collection_key) o).getKey();
        return Objects.equals(getCollection(), that.getCollection()) && key.equals(super.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCollection());
    }
}
