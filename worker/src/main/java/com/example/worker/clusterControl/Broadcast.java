package com.example.worker.clusterControl;

import com.example.worker.Affinity.affinitySetter;
import com.example.worker.FeedBack;
import com.example.worker.clusterControl.httpRequest.GetDeleteRequest;
import com.example.worker.clusterControl.httpRequest.PostRequest;
import com.example.worker.clusterControl.httpRequest.RequestSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Broadcast implements affinitySetter {
    private static final int PORT_NUMBER = 8090;
    private static final String []urls = {"http://worker1:" + PORT_NUMBER , "http://worker2:" + PORT_NUMBER, "http://worker3:" + PORT_NUMBER , "http://worker4:" + PORT_NUMBER};
    public static String getUrl(int i){
        return urls[i];
    }

    public boolean DBCheck(String Database){
        boolean valid = true;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/cluster/check/db/" + Database);
                RequestSender sender = new GetDeleteRequest();
                StringBuilder response = sender.sendHttpRequest(url,"GET","");
                valid = valid & Boolean.parseBoolean(response.toString());

            } catch (Exception e) {
                return false;
            }
        }
        return valid;
    }
    public FeedBack buildDB(String Database){
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/create/db/" + Database);
                RequestSender sender = new GetDeleteRequest();
                response = sender.sendHttpRequest(url,"GET","");
                ObjectMapper mapper = new ObjectMapper();
                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);

                feedbacks.add(feedback);
            } catch (Exception e) {
                return new FeedBack("Build Database failed: broadcast stage",600);
            }
        }
        for(FeedBack feedBack: feedbacks)
            if(feedBack.getStatusCode() >= 300)
                return feedBack;

        return new FeedBack("Build Database done: broadcast stage",210);
    }
    public boolean checkCollection(String Database, String Collection){
        boolean valid = true;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/cluster/check/collection/" + Database + '/' + Collection);
                RequestSender sender = new GetDeleteRequest();
                StringBuilder response = sender.sendHttpRequest(url,"GET","");

                valid = valid & Boolean.parseBoolean(response.toString());

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return valid;
    }
    public FeedBack buildCollection(String Database, String Collection, String Json){
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;
        for(String Url : urls) {
            try {
                URL url = new URL(Url + "/create/collection/" + Database + '/' + Collection);
                RequestSender sender = new PostRequest();
                response = sender.sendHttpRequest(url,"POST",Json);
                ObjectMapper mapper = new ObjectMapper();
                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);

                feedbacks.add(feedback);
            } catch (Exception e) {
                return new FeedBack("Build Collection failed: broadcast stage",600);
            }
        }
        for (FeedBack feedback : feedbacks)
            if (feedback.getStatusCode() >= 300)
                return feedback;

        return new FeedBack("Build Collection done: broadcast stage",211);
    }
    public boolean checkRecord(String Database, String Collection, String Json){
        boolean valid = true;
        for(String Url : urls) {
            try {
                URL url = new URL(Url + "/cluster/check/record/" + Database + '/' + Collection);
                RequestSender sender = new PostRequest();
                StringBuilder response = sender.sendHttpRequest(url,"POST",Json);

                valid = valid & Boolean.parseBoolean(response.toString());

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return false;
            }
        }
        return valid;
    }
    public boolean checkId(String Database, String Collection, String id){
        boolean valid = true;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/cluster/check/id/" + Database + '/' + Collection + '/' + id);
                RequestSender sender = new GetDeleteRequest();
                StringBuilder response = sender.sendHttpRequest(url,"GET","");

                valid = valid & Boolean.parseBoolean(response.toString());

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return false;
            }
        }
        return valid;
    }
    public FeedBack addRecord(String Database, String Collection, String Json){
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;
        for(String Url : urls){
            try {
                URL url = new URL(Url + "/add/record/" + Database + '/' + Collection);
                RequestSender sender = new PostRequest();
                response = sender.sendHttpRequest(url,"POST",Json);
                ObjectMapper mapper = new ObjectMapper();
                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);

                feedbacks.add(feedback);
            } catch (Exception e) {
                return new FeedBack("Add Record failed: broadcast stage",600);
            }
        }
        for (FeedBack feedback : feedbacks) {
            if (feedback.getStatusCode() >= 300) {
                return feedback;
            }
        }
        return new FeedBack("Add Record done: broadcast stage",213);
    }
    public FeedBack deleteRecord(String Database, String Collection, String id){
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;
        for(String Url : urls){
            try {
                URL url = new URL( Url+ "/delete/" + Database + '/' + Collection + '/' + id);
                RequestSender sender = new GetDeleteRequest();
                response = sender.sendHttpRequest(url,"DELETE","");
                ObjectMapper mapper = new ObjectMapper();
                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);

                feedbacks.add(feedback);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return new FeedBack("Delete Record failed: broadcast stage , " + response.toString(),600);
            }
        }
        for (FeedBack feedback : feedbacks) {
            if (feedback.getStatusCode() >= 300) {
                return feedback;
            }
        }
        return new FeedBack("Delete Record done: broadcast",212);
    }
    public FeedBack deleteCollection(String Database, String Collection){
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/delete/" + Database + '/' + Collection);
                RequestSender sender = new GetDeleteRequest();
                response = sender.sendHttpRequest(url,"DELETE","");
                ObjectMapper mapper = new ObjectMapper();
                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);

                feedbacks.add(feedback);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return new FeedBack("Delete Collection failed: broadcast stage",600);
            }
        }
        for (FeedBack feedback : feedbacks) {
            if (feedback.getStatusCode() >= 300) {
                return feedback;
            }
        }
        return new FeedBack("Delete Collection done: broadcast",212);
    }
    public FeedBack deleteDB(String Database){
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/delete/" + Database);
                RequestSender sender = new GetDeleteRequest();
                response = sender.sendHttpRequest(url,"DELETE","");
                ObjectMapper mapper = new ObjectMapper();
                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);

                feedbacks.add(feedback);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                return new FeedBack("Delete Record failed: broadcast",600);
            }
        }
        for (FeedBack feedback : feedbacks) {
            if (feedback.getStatusCode() >= 300) {
                return feedback;
            }
        }
        return new FeedBack("Delete Database done: broadcast",212);
    }
    public FeedBack updateRecord(String Database, String Collection,
                                 String id, String Property, String value) {
        List<FeedBack> feedbacks = new ArrayList<>();
        StringBuilder response = null;

        boolean OK = true;
        try {
            // Construct the JSON payload
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode payload = mapper.createObjectNode();
            payload.put("Property", Property);
            payload.put("value", value);

            // Send the request to each URL
            for(String Url : urls) {
                URL url = new URL(Url + "/update/" + Database + '/' + Collection + '/' + id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Write the payload to the request body
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(mapper.writeValueAsString(payload));
                out.flush();

                // Read the response
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                FeedBack feedback = mapper.readValue(response.toString(), FeedBack.class);
                feedbacks.add(feedback);
            }

            makeAffinitiesSame(getAffinity()+1);
            for (FeedBack feedback : feedbacks) {
                if (feedback.getStatusCode() >= 300) {
                    return feedback;
                }
            }
            return new FeedBack("Update Record done: broadcast stage " + feedbacks.toString(), 214);
        } catch (Exception e) {
            System.out.printf("Error: " + e.getMessage());
            return new FeedBack("Update Record failed: broadcast stage", 600);
        }
    }


    public FeedBack searchAffinity (String Database, String Collection,
                                    String id, String Property, String value,String Url) {
            try {
                URL url = new URL( Url+ "/update/affinity/" + Database + '/' + Collection + '/' + id + '/' + Property + "?value=" + value);
                RequestSender sender = new GetDeleteRequest();
                StringBuilder response = sender.sendHttpRequest(url,"GET","");

                System.out.println(response.toString());

            } catch (Exception e) {
                System.out.printf("Error: " + e.getMessage());
                return new FeedBack("Checking affinity failed: broadcast stage",600);
            }
        return new FeedBack("Checking affinity done: broadcast stage",215);
    }

    public FeedBack makeAffinitiesSame(int No){
        for(String Url : urls){
            try {
                URL url = new URL( Url+ "/cluster/set/affinity?no=" + No);

                RequestSender sender = new GetDeleteRequest();
                StringBuilder response = sender.sendHttpRequest(url,"GET","");

                System.out.println(response.toString());

            } catch (Exception e) {
                System.out.printf("Error: " + e.getMessage());
                return new FeedBack("Checking affinity failed: broadcast stage",600);
            }
        }
        return new FeedBack("Checking affinity done: broadcast stage",215);
    }

    public boolean checkToUpdate(String Database, String Collection , String id, String Property, String oldValue){
        boolean valid = true;
        for(String Url: urls) {
            try {
                URL url = new URL(Url + "/cluster/check/update/" + Database + '/' + Collection + '/' + id +'/' + Property + '/' + oldValue);

                RequestSender sender = new GetDeleteRequest();
                StringBuilder response = sender.sendHttpRequest(url,"GET","");

                valid = valid & Boolean.parseBoolean(response.toString());

            } catch (Exception e) {
                return false;
            }
        }
        return valid;
    }
}
