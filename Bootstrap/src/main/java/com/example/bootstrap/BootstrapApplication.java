package com.example.bootstrap;

import com.example.bootstrap.httpRequest.GetRequest;
import com.example.bootstrap.httpRequest.RequestSender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
        try {
            URL url = new URL("http://bootstrap:8080/setUp");

            RequestSender sender = new GetRequest();
            StringBuilder response = sender.sendHttpRequest(url,"GET","");

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
