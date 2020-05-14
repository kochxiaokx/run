package org.run.some;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 模拟htttp请求
 */
public class HttpRequest {
    private URL url = null;
    HttpURLConnection httpsConn = null;
   // private static String apiToken = null;
    public void init(String requestUrl,String requestMethod){
        try {
            url = new URL(requestUrl);
            httpsConn =  (HttpURLConnection)url.openConnection();
            httpsConn.setRequestMethod(requestMethod);
            httpsConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        //    httpsConn.setRequestProperty("apiToken",apiToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  String getJson(){
        BufferedReader br = null;
        String dataStr = null;
        StringBuilder builder = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(
                    httpsConn.getInputStream(), "UTF-8"));
            while ((dataStr = br.readLine()) != null){
                builder.append(dataStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.close(br);
        }
        return  builder.toString();
    }

    public void close(Reader reader){
        try {
            if(reader != null){
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
