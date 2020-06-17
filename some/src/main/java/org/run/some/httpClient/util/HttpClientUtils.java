package org.run.some.httpClient.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

public class HttpClientUtils {

    public static void close(CloseableHttpClient httpClient, CloseableHttpResponse response){
            try {
                if(httpClient != null){
                httpClient.close();
                }
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
