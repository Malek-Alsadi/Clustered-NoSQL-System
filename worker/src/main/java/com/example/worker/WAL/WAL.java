package com.example.worker.WAL;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//Write-Ahead-Logs
public class WAL {
    private String filePath;
    private BufferedWriter writer;
    private BufferedReader reader;

    public WAL(String filePath) {
        this.filePath = filePath;
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));
            reader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLog(Log log) {
        try {
            writer.write(log.getOperation() + "," + log.getKey() + "," + log.getValue() + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Log> readLogs() {
        List<Log> logs = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Log.Operation op = Log.Operation.valueOf(parts[0]);
                String key = parts[1];
                Object value = null;
                if (parts.length == 3) {
                    value = parts[2];
                }
                logs.add(new Log(op, key, value));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public void close() {
        try {
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

