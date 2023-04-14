package com.atypon.demo.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

public class GetDeleteRequest extends RequestSender{
    @Override
    protected StringBuilder sendGetOrDeleteRequest(URL url, String method,String requestBody) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(requestBody.getBytes()));
            conn.setRequestProperty("authorization", basicAuth);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response;

        } catch (Exception e) {
            return new StringBuilder(e.getMessage());
        }
    }

    @Override
    protected StringBuilder sendPostRequest(URL url,String method, String requestBody) {
        throw new UnsupportedOperationException();
    }
}
