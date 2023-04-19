package com.example.worker.synch_locks;

import java.util.HashMap;

public class locks_key {
    private static locks_key instance;
    public static locks_key getInstance(){
        if(instance == null)
            instance = new locks_key();
        return instance;
    }
    HashMap < iLock , Object > map = new HashMap<>();
    public Object getLock(iLock lock){
        if(map.containsKey(lock)){
            return map.get(lock);
        }
        Object object = new Object();
        map.put(lock,object);
        return map.get(lock);
    }
    public void dropLock(iLock lock){
        if(map.containsKey(lock))
            map.remove(lock);
    }
}
