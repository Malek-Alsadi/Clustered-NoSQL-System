package com.example.worker.synch_key;

import java.util.Objects;

public class Database_Key {
    private String Database;

    public Database_Key(String Database) {
        this.Database = Database;
    }

    public String getDatabase() {
        return Database;
    }

    public void setDatabase(String Database) {
        this.Database = Database;
    }

    public Database_Key getKey(){
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Database_Key that)) return false;
        return Objects.equals(getDatabase(), that.getDatabase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabase());
    }
}
