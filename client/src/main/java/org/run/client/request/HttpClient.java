package org.run.client.request;

import org.junit.Test;
import org.run.some.httpClient.GetRequest;

public class HttpClient {

    @Test
    public void testGetRequest(){
        GetRequest getRequest = new GetRequest();
        String json = getRequest.getJson();
        System.out.println(json);
    }

    public static void main(String[] args) {
        GetRequest getRequest = new GetRequest();
        String json = getRequest.getJson();
        System.out.println(json);
    }
}
