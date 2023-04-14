package com.atypon.demo.Services;

import com.atypon.demo.Database.Auth_DAO;
import com.atypon.demo.Database.DAO;
import com.atypon.demo.Objects.Customers;
import com.atypon.demo.Objects.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddingService {
    private final DAO dao;
    @Autowired
    public AddingService(DAO dao,Auth_DAO authDao) {
        this.dao = dao;
    }

    public String addingBranchs(String DB, String Token, String Password,String url){
       return dao.createDB(DB,Token,Password,url);
    }
    public String addingCollection(String db, String Token , String password, String url){
        return dao.createCollection(db, Token , password,url);
    }
    public String addingRecordCustomer(String db ,Customers customer ,String Token , String password, String url){
        return dao.addRecordCostumer(db,customer,Token,password,url);
    }
    public String addingRecordEmployee(String db , Employee employee , String Token , String password, String url){
        return dao.addRecordEmployee(db,employee,Token,password,url);
    }


}
