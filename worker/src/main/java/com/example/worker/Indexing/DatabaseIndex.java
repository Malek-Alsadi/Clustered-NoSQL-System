package com.example.worker.Indexing;

import com.example.worker.WAL.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseIndex {
    private static DatabaseIndex instance;
    public static DatabaseIndex getInstance(){
        if(instance == null)
            instance = new DatabaseIndex();
        return instance;
    }
    public DatabaseIndex(){
        File log = new File("index.wal");
        wal = new WAL("index.wal");
        RestoreFromWal();
    }
    ConcurrentHashMap<String , CollectionIndex> map = new ConcurrentHashMap<>();
    private WAL wal;
    public void initIndex(String Database){
        CollectionIndex index = new CollectionIndex();
        wal.writeLog( new Log( Log.Operation.ADD_DB, Database, "") );
        map.put(Database,index);
    }
    public boolean addCollection(String Database, String Collection, String []Properties){
        if(!map.containsKey(Database))
            return false;
        StringBuilder prop = new StringBuilder();
        for(String Property : Properties) {
            prop.append('-').append(Property);
        }
        String collection_value = Collection + prop;
        wal.writeLog(new Log( Log.Operation.ADD_COLLECTION, Database, collection_value));
        return map.get(Database).initIndexForCollection(Collection,Properties);
    }
    public boolean isIndexed(String Database,String Collection){
        if(!map.containsKey(Database))
            return false;

        return map.get(Database).isIndexed(Collection);
    }
    public boolean isIndexed(String Database){
        return map.containsKey(Database);
    }


    public void indexProperties(String Database, String Collection, String Property, String value, int idx){
        if(!map.containsKey(Database))
            return;
        String key = Database + '-' + Collection + '-' + Property + '-' + value;
        wal.writeLog( new Log(Log.Operation.ADD_PROPERTY,key,idx));
        map.get(Database).indexProperties(Collection,Property,value,idx);
    }

    public HashMap<String, List<Integer>> getMap(String Database, String Collection, String Property) {
        return map.get(Database).getMap(Collection,Property);
    }

    public void deleteByIdx(String Database,String Collection,int idx){
        if(!map.containsKey(Database))
            return;
        String key = Database + '-' + Collection;
        wal.writeLog( new Log(Log.Operation.DELETE_PROPERTY,key,idx) );
        map.get(Database).deleteByIdx(Collection,idx);
    }
    public void addToValue(String Database, String Collection, String Property, String value, int idx){
        if(!map.containsKey(Database))
            return;

        String key = Database + '-' + Collection + '-' + Property + '-' + value;
        wal.writeLog( new Log(Log.Operation.ADD_PROPERTY,key,idx) );
        map.get(Database).addToValue(Collection,Property,value,idx);
    }
    public void DropCollection(String Database, String Collection){
        if(!map.containsKey(Database))
            return;

        wal.writeLog( new Log(Log.Operation.DELETE_COLLECTION,Database,Collection) );
        map.get(Database).dropCollection(Collection);
    }
    public void DropDatabase(String Database){
        if(!map.containsKey(Database))
            return;
        wal.writeLog( new Log(Log.Operation.DELETE_BD,Database,"") );
        map.remove(Database);
    }
    public List<String> getCollections(String Database){
        if(!map.containsKey(Database))
            return new ArrayList<>();
        return map.get(Database).getCollections();
    }
    public List<String> getDatabases(){
        if(map == null)
            return new ArrayList<>();

        List<String> list = new ArrayList<>();
        for (Map.Entry<String, CollectionIndex> entry : map.entrySet())
            list.add(entry.getKey());
        return list;
    }
    private void RestoreFromWal(){
        List<Log> logs = wal.readLogs();
        for(Log log : logs){
            switch (log.getOperation()){
                case ADD_DB:
                    map.put(log.getKey(),new CollectionIndex());
                    break;
                case ADD_COLLECTION:
                    String str = (String) log.getValue();
                    String[] arr = str.split("-");
                    String Collection = arr[0];
                    String []properties = new String[arr.length-1];
                    for(int i=0;i<properties.length;i++){
                        properties[i] = arr[i+1];
                    }
                    map.get(log.getKey()).initIndexForCollection(Collection,properties);
                    break;
                case ADD_PROPERTY:
                    arr = log.getKey().split("-");
                    String Database = arr[0];
                    Collection = arr[1];
                    String Property = arr[2];
                    String value = arr[3];
                    map.get(Database).indexProperties(Collection,Property,value,(int)log.getValue());
                    break;
                case DELETE_BD:
                    map.remove(log.getKey());
                    break;
                case DELETE_COLLECTION:
                    map.get(log.getKey()).dropCollection((String)log.getValue());
                    break;
                case DELETE_PROPERTY:
                    arr = log.getKey().split("-");
                    map.get(arr[0]).deleteByIdx(arr[1],(int)log.getValue());
            }
        }
    }
}
