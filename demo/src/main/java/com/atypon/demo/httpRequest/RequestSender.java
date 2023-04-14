package com.atypon.demo.httpRequest;

import java.net.URL;

public abstract class RequestSender {
    protected abstract StringBuilder sendGetOrDeleteRequest(URL url, String method,String requestBody);
    protected abstract StringBuilder sendPostRequest(URL url,String method, String requestBody);

    public StringBuilder sendHttpRequest(URL url, String method, String requestBody) {
        if (method.equals("GET") || method.equals("DELETE")) {
            return sendGetOrDeleteRequest(url,method,requestBody);
        } else {
            return sendPostRequest(url,method, requestBody);
        }
    }
}
