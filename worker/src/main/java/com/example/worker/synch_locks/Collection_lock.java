package com.example.worker.synch_locks;

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
        if (!(o instanceof Database_lock)) return false;
        if (!(o instanceof Record_lock)) {
            return super.equals(o);
        }

        Collection_lock that = (Collection_lock) o;
        return Objects.equals(getCollection(), that.getCollection())
                && getDatabase().equals(that.getDatabase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCollection());
    }
}
