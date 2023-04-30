package com.example.worker.WAL;

public class Log {
    public enum Operation { ADD_DB, DELETE_BD, ADD_COLLECTION, DELETE_COLLECTION, ADD_PROPERTY, UPDATE, DELETE_PROPERTY};

    private Operation op;
    private String key;
    private Object value;

    public Log(Operation op, String key, Object value) {
        this.op = op;
        this.key = key;
        this.value = value;
    }

    public Operation getOperation() {
        return op;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
