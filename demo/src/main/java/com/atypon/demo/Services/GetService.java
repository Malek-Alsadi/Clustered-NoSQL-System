package com.atypon.demo.Services;

import com.atypon.demo.Database.DAO;
import com.atypon.demo.Objects.Customers;
import com.atypon.demo.Objects.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetService {
    private final DAO dao;

    public GetService(DAO dao) {
        this.dao = dao;
    }
    public List<Employee> getAll(String Database,String Token, String Password, String Url) throws MalformedURLException, JsonProcessingException {
        return dao.getAllEmployees(Database,Token,Password,Url);
    }
    public Customers getCostumerById(String Database, String Id, String Token, String Password, String Url){
        JsonNode jsonNode = dao.getById(Database,"costumers",Id,Token,Password,Url);
        if(jsonNode == null)
            return null;
        String id = jsonNode.get("Id").asText();
        String name = jsonNode.get("Name").asText();
        double balance = jsonNode.get("Balance").asDouble();
        return new Customers(id,name,balance);
    }
    public Employee getEmployeeById(String Database, String Id, String Token, String Password, String Url){
        JsonNode jsonNode = dao.getById(Database,"employee",Id,Token,Password,Url);
        if(jsonNode == null)
            return null;
        String id = jsonNode.get("Id").asText();
        String name = jsonNode.get("Name").asText();
        String role = jsonNode.get("Role").asText();
        double Salary = jsonNode.get("Salary").asDouble();
        return new Employee(id,name,role,Salary);
    }
    public List<Customers> allWithName(String Database, String value, String Token, String Password, String Url){
        ArrayNode arrayNode = dao.getByProperty(Database,"costumers","Name",value,Token,Password,Url);
        if(arrayNode == null)
            return new ArrayList<>();
        List<Customers> list = new ArrayList<>();
        for(JsonNode jsonNode : arrayNode){
            String id = jsonNode.get("Id").asText();
            String name = jsonNode.get("Name").asText();
            double balance = jsonNode.get("Balance").asDouble();
            Customers customers =  new Customers(id,name,balance);
            list.add(customers);
        }
        return list;
    }
    public void Update(String Database, String Id, double value,
                       String Token, String Password, String Url){
        dao.Update(Database,Id,value,Token,Password,Url);
    }
}
