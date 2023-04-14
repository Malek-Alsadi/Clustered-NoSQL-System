package com.example.worker.clusterControl.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class GetDeleteRequest extends RequestSender{
    @Override
    protected StringBuilder sendGetOrDeleteRequest(URL url, String method) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response;
    }

    @Override
    protected StringBuilder sendPostRequest(URL url, String requestBody) {
        throw new UnsupportedOperationException();
    }
}
