package com.atypon.demo.Database;

import com.atypon.demo.Objects.Customers;
import com.atypon.demo.Objects.Employee;
import com.atypon.demo.httpRequest.GetDeleteRequest;
import com.atypon.demo.httpRequest.PostRequest;
import com.atypon.demo.httpRequest.RequestSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Repository
public class DAO {
    public String createDB(String DB,String Token, String Password, String Url){
        try {
            URL url = new URL(Url + "/api/create/db/" + DB);
            RequestSender sender = new GetDeleteRequest();
            String userCredentials = Token + ":" + Password;
            StringBuilder response = sender.sendHttpRequest(url,"GET",userCredentials);
            return response.toString();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return "Something Went Wrong";
    }
    public String createCollection(String DB,String Token,String Password,String Url){
        try {
            URL url = new URL(Url + "/api/create/collection/" + DB + "/costumers");
            RequestSender sender = new PostRequest();
            String userCredentials = Token + ":" + Password;


            File SchemaFile = new File("./schema1.json");
            String path = SchemaFile.getPath();
            String SchemaString = new String( Files.readAllBytes(Paths.get(path)) );
            StringBuilder response1 = sender.sendHttpRequest(url,userCredentials,SchemaString);


            url = new URL(Url + "/api/create/collection/" + DB + "/employee");
            SchemaFile = new File("./schema2.json");
            path = SchemaFile.getPath();
            SchemaString = new String( Files.readAllBytes(Paths.get(path)) );
            StringBuilder response2 = sender.sendHttpRequest(url,userCredentials,SchemaString);

            String response = response1.toString() + '\n' + response2.toString();

            return response;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Something Went Wrong";
    }
    public String addRecordCostumer(String DB, Customers customer , String Token, String Password, String Url){
        try {
            URL url = new URL(Url + "/api/add/record/" + DB + "/costumers");
            RequestSender sender = new PostRequest();
            String userCredentials = Token + ":" + Password;
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(customer);
            StringBuilder response = sender.sendHttpRequest(url,userCredentials,json);

            return response.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Something Went Wrong";
    }
    public String addRecordEmployee(String DB, Employee employee , String Token, String Password, String Url){
        try {
            URL url = new URL(Url + "/api/add/record/" + DB + "/employee");
            RequestSender sender = new PostRequest();
            String userCredentials = Token + ":" + Password;
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(employee);
            StringBuilder response = sender.sendHttpRequest(url,userCredentials,json);

            return response.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Something Went Wrong";
    }
    public String DeleteRecord(String DB, String Collection, String id, String Token, String Password, String Url){
        try {
            URL url = new URL(Url + "/api/delete/record/" + DB + "/" + Collection + "/" + id );
            RequestSender sender = new GetDeleteRequest();
            String userCredentials = Token + ":" + Password;
            StringBuilder response = sender.sendHttpRequest(url,"DELETE",userCredentials);

            return response.toString();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return "Something Went Wrong";
    }
    public String DeleteDatabase(String DB, String Token , String Password, String Url){
        try{
            URL url = new URL( Url + "/api/delete/db/" + DB);
            RequestSender sender = new GetDeleteRequest();
            String userCredentials = Token + ":" + Password;
            StringBuilder response = sender.sendHttpRequest(url,"DELETE",userCredentials);

            return response.toString();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        return "Something Went Wrong";
    }
    public List<Employee> getAllEmployees(String Database, String Token, String Password, String Url) throws MalformedURLException, JsonProcessingException {
        URL url = new URL( Url + "/api/get/AllRecords/" + Database + "/employee");
        RequestSender sender = new GetDeleteRequest();
        String userCredentials = Token + ":" + Password;
        StringBuilder response = sender.sendHttpRequest(url,"GET",userCredentials);

        ObjectMapper mapper = new ObjectMapper();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, Employee.class);
        List<Employee> employees = mapper.readValue(response.toString(), listType);
        return employees;
    }
    public JsonNode getById(String Database, String Collection,String Id, String Token, String Password, String Url){
        try {
            URL url = new URL( Url + "/api/get/" + Database + "/" + Collection + "/" + Id);
            String userCredentials = Token + ":" + Password;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method
            conn.setRequestMethod("GET");
            userCredentials = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
            conn.setRequestProperty("authorization", userCredentials);

            // Send HTTP request and receive response
            int responseCode = conn.getResponseCode();
            JsonNode jsonNode = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Parse JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                jsonNode = objectMapper.readTree(conn.getInputStream());
            } else {
                System.out.println("HTTP error code: " + responseCode);
            }

            // Close HTTP connection
            conn.disconnect();
            return jsonNode;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayNode getByProperty(String Database, String Collection, String Property, String value, String Token, String Password, String Url){
        try {
            URL url = new URL(Url + "/api/get/" + Database + '/' + Collection + '/' + Property + '/' + value);
            String userCredentials = Token + ":" + Password;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method
            conn.setRequestMethod("GET");
            userCredentials = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
            conn.setRequestProperty("authorization", userCredentials);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // Parse the JSON response as an ArrayNode
            ObjectMapper mapper = new ObjectMapper();
            return (ArrayNode) mapper.readTree(responseBuilder.toString());

        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void Update(String Database, String Id,double value ,
                       String Token, String Password, String Url) {

        try {
            URL url = new URL(Url + "/api/update/" + Database + '/' + "costumers" + '/' + Id);

            String userCredentials = Token + ":" + Password;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");
            userCredentials = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
            conn.setRequestProperty("authorization", userCredentials);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode payload = mapper.createObjectNode();
            payload.put("Property", "Balance");
            payload.put("value", value);
            // Write the payload to the request body
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(mapper.writeValueAsString(payload));
            out.flush();

            // Read the response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = null;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println(response);
            in.close();
            conn.disconnect();
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
