package com.example.bootstrap.Services;

import com.example.bootstrap.Cache.LFUCache;
import com.example.bootstrap.Cache.id_Type;
import com.example.bootstrap.Database.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final DAO dao;
    @Autowired
    public UsersService(DAO dao){
        this.dao = dao;
    }

    public void addUser(String JSON, String type){
        dao.addUser(JSON,type);
    }
    public boolean exist(String id, String type){
        LFUCache cache = LFUCache.getInstance();
        id_Type key = new id_Type(id,type);
        if(cache.get(key) != null)
            return true;
        return dao.userExist(id, type);
    }
}
