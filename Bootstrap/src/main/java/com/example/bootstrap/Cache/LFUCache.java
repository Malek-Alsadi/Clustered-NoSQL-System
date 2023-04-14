package com.example.bootstrap.Cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache<K, V> {

    private static LFUCache instance;
    public static LFUCache getInstance(){
        if( instance == null )
            instance = new LFUCache(500);
        return instance;
    }
    private int capacity;
    private Map<K, V> cache;
    private Map<K, Integer> freq;
    private Map<Integer, LinkedHashSet<K>> freqKeys;
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        freq = new HashMap<>();
        freqKeys = new HashMap<>();
        minFreq = 0;
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        int oldFreq = freq.get(key);
        freq.put(key, oldFreq + 1);
        freqKeys.get(oldFreq).remove(key);
        if (oldFreq == minFreq && freqKeys.get(oldFreq).isEmpty()) {
            minFreq++;
        }
        if (!freqKeys.containsKey(oldFreq + 1)) {
            freqKeys.put(oldFreq + 1, new LinkedHashSet<>());
        }
        freqKeys.get(oldFreq + 1).add(key);
        return cache.get(key);
    }

    public void put(K key, V value) {
        if (capacity == 0) {
            return;
        }
        if (cache.containsKey(key)) {
            cache.put(key, value);
            get(key);
            return;
        }
        if (cache.size() == capacity) {
            K evictKey = freqKeys.get(minFreq).iterator().next();
            freqKeys.get(minFreq).remove(evictKey);
            cache.remove(evictKey);
            freq.remove(evictKey);
        }
        cache.put(key, value);
        freq.put(key, 1);
        minFreq = 1;
        if (!freqKeys.containsKey(1)) {
            freqKeys.put(1, new LinkedHashSet<>());
        }
        freqKeys.get(1).add(key);
    }
}

