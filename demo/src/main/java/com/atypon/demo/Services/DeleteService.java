package com.atypon.demo.Services;

import com.atypon.demo.Database.DAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteService {
    private final DAO dao;
    @Autowired
    public DeleteService(DAO dao) {
        this.dao = dao;
    }

    public String DeleteBranch(String DB, String Token, String Password, String Url){
        return dao.DeleteDatabase(DB,Token,Password,Url);
    }
    public String DeleteRecord(String DB, String Collection,
                             String id,String Token,
                             String Password, String Url){
        return dao.DeleteRecord(DB,Collection,id,Token,Password,Url);
    }

}
