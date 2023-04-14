package com.atypon.demo.Database;


import com.atypon.demo.Cache.LFUCache;
import com.atypon.demo.Cache.Token_Type;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

@Repository
public class Auth_DAO implements JsonArraymaker{
    public StringBuilder connectNewUser(String id, String password, boolean isManager){
        try {
            String type = isManager ? "managers" : "users";
            URL url = new URL("http://localhost:8080/connect?type=" + type);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            String userCredentials = id + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);

            int responseCode = con.getResponseCode();

            InputStream inputStream = null;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            inputStream.close();
            con.disconnect();
            return response;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public void addUser(JsonNode JSON,boolean isManager) throws IOException {
        String type = isManager?"managers":"users";
        File folder = new File("./Users");
        if(!folder.exists())
            folder.mkdir();

        File jsonfile = new File("./Users/" + type + ".json");
        if(!jsonfile.exists())
            createEmptyJsonArray( jsonfile );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPath = jsonfile.getPath();
        ObjectNode root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
        ArrayNode myArray = (ArrayNode) root.get("myArray");
        if (myArray == null) {
            myArray = objectMapper.createArrayNode();
            root.set("myArray", myArray);
        }
        myArray.add(JSON);
        FileWriter writer = new FileWriter(jsonPath);
        objectMapper.writeValue(writer, root);
        writer.close();
    }
    public JsonNode search(String Token , boolean isManager){
        String type = isManager?"managers":"users";
        File folder = new File("./Users");
        if(!folder.exists())
            return null;

        File jsonfile = new File("./Users/" + type + ".json");
        if(!jsonfile.exists())
            return null;

        ArrayNode myArray = getArray(jsonfile);
        if(myArray == null)
            return null;
        for(JsonNode Json: myArray){
            if(Json.get("Token").asText().equals(Token))
                return Json;
        }
        return null;
    }

    public ArrayNode getArray(File JsonFile){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPath = JsonFile.getPath();
            ObjectNode root = (ObjectNode) objectMapper.readTree(new File(jsonPath));
            ArrayNode myArray = (ArrayNode) root.get("myArray");
            if (myArray == null) {
                myArray = objectMapper.createArrayNode();
                root.set("myArray", myArray);
            }
            return myArray;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getURL(String Token , boolean isManager){
        String type = isManager?"managers":"users";

        LFUCache cache = LFUCache.getInstance();
        Token_Type key = new Token_Type(Token,type);
        if(cache.get(key) != null)
            return cache.get(key).toString();

        JsonNode json = search(Token,isManager);
        if(json == null || !json.has("URL"))    return null;
        String url = json.get("URL").asText();
        cache.put(key,url);

        return url;

    }
    public boolean checkUser(String Token, String Password, boolean isManager, String Url) {
        try {
            String type = isManager?"managers":"users";
            URL url = new URL(Url + "/api/get/CheckConnection");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            String userCredentials = Token + ":" + Password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            return false;
        }
    }
}
