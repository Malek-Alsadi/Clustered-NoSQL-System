package com.example.worker.clusterControl.httpRequest;

import java.io.IOException;
import java.net.URL;

public abstract class RequestSender {
    protected abstract StringBuilder sendGetOrDeleteRequest(URL url, String method) throws IOException;
    protected abstract StringBuilder sendPostRequest(URL url, String requestBody);

    public StringBuilder sendHttpRequest(URL url, String method, String requestBody) throws IOException {
        if (method.equals("GET") || method.equals("DELETE")) {
            return sendGetOrDeleteRequest(url,method);
        } else if (method.equals("POST")) {
            return sendPostRequest(url, requestBody);
        }
        return new StringBuilder();
    }
}
