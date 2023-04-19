package com.example.worker.synch_locks;

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
        if (!(o instanceof Database_lock)) return false;
        if (!(o instanceof Record_lock)) {
            return super.equals(o);
        }

        Record_lock that = (Record_lock) o;
        return Objects.equals(getId(), that.getId())
                && getCollection().equals(that.getCollection())
                && getDatabase().equals(that.getDatabase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
