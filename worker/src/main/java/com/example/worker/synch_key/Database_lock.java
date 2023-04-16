package com.example.worker.synch_key;

import java.util.Objects;

public class Database_lock {
    private String Database;

    public Database_lock(String Database) {
        this.Database = Database;
    }

    public String getDatabase() {
        return Database;
    }

    public void setDatabase(String Database) {
        this.Database = Database;
    }

    public Database_lock getKey(){
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Database_lock that)) return false;
        return Objects.equals(getDatabase(), that.getDatabase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabase());
    }
}
