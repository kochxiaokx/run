package org.run.client.request;

import com.alibaba.fastjson.JSONObject;
import org.run.util.result.FastJsonUtils;
import org.run.util.result.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * 模拟http请求
 */
public class HttpRequest {
    private URL url = null;
    HttpURLConnection httpsConn = null;
    private static String apiToken = null;
    private static final String USER_NAME = "333";
    private static final String PASSWORD = "333";
    private static final String VERSION = "2";
    private final static String REQUEST_URL = "http://127.0.0.1:8083/bxibs/user/login?userName="
                                            +USER_NAME+"&password="+PASSWORD+"&version="+VERSION;
    private final static String RELOAD_URL  = "http://127.0.0.1:8083/bxibs/user/reload";
    private final static String POST = "POST";
    private final static String GET = "GET";
    public static void main(String[] args) {
        HttpRequest httpRequest =  new HttpRequest();
        apiToken = httpRequest.login();
        String json = httpRequest.reload(RELOAD_URL,GET);
    }


    public  String login(){
        init(REQUEST_URL,POST);
        String json = getJson();
        Result result = FastJsonUtils.toBean(json,Result.class);
        Map<String,Object> params =  (JSONObject)result.getData();
        System.out.println(params);
        String apiToken = params.get("apiToken").toString();
        apiToken = apiToken;
        return apiToken;
    }
    public void init(String requestUrl,String requestMethod){
        try {
            url = new URL(requestUrl);
            httpsConn =  (HttpURLConnection)url.openConnection();
            httpsConn.setRequestMethod(requestMethod);
            httpsConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpsConn.setRequestProperty("apiToken",apiToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String reload(String requestUrl,String requestMethod){
        init(requestUrl,requestMethod);
        return getJson();
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
