package com.rwb;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpClientTest {
    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8082/append");
        HttpGet httpGet1 = new HttpGet("http://localhost:8081/append");

        CloseableHttpResponse response = null;
        CloseableHttpResponse response1 = null;
        try {

            for (int i = 0; i < 3; i++) {
                response = httpClient.execute(httpGet);
                System.out.println(response.getStatusLine().getStatusCode());
                response1 = httpClient.execute(httpGet1);
                System.out.println(response1.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response1 != null) {
                try {
                    response1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
