package com.example.bootstrap.httpRequest;

import java.net.URL;

public abstract class RequestSender {
    protected abstract StringBuilder sendGetRequest(URL url, String method);
    protected abstract StringBuilder sendPostRequest(URL url, String requestBody);

    public StringBuilder sendHttpRequest(URL url, String method, String requestBody) {
        if (method.equals("GET") ) {
            return sendGetRequest(url,method);
        } else if (method.equals("POST") ) {
            return sendPostRequest(url, requestBody);
        }
        return new StringBuilder("anything");
    }
}
