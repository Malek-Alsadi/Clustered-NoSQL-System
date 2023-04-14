package com.example.worker.Affinity;

import com.example.worker.clusterControl.Broadcast;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.worker.FeedBack;
import org.springframework.scheduling.annotation.Async;

import java.io.*;

public interface affinitySetter {
    default JsonNode setAffinity(JsonNode Json){
        try {
            int worker = getAffinity();
            ObjectNode JSON = Json.deepCopy();
            JSON.put("Affinity",worker);
            worker++;
            worker %= 4;
            setAffinityFile(worker);
            return JSON;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default FeedBack setAffinityFile(int No){
        File Affinity = new File("./Affinity");
        if(!Affinity.exists() || !Affinity.isDirectory())
            Affinity.mkdir();

        File workerNo = new File("./Affinity/workerNo.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(workerNo);
            No %= 4;
            writer.write(String.valueOf(No));
            writer.close();
            return new FeedBack("set affinity done" , 215);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    default int getAffinity(){
        File workerNo = new File("./Affinity/workerNo.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(workerNo));
            String line = reader.readLine();
            int worker = Integer.parseInt(line);
            reader.close();
            return worker;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    default String buildPort(){
        int n = getAffinity();
        return Broadcast.getUrl(n);
    }
}
