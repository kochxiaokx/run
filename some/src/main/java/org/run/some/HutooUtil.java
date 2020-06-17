package org.run.some;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import org.run.util.result.FastJsonUtils;
import org.run.util.result.Result;

import java.util.HashMap;
import java.util.Map;

public class HutooUtil {
    private static final String USER_NAME = "333";
    private static final String PASSWORD = "333";
    private final static String LOGIN_URL = "http://127.0.0.1:8083/bxibs/user/login";
    private static String apiToken = null;
    private final static String RELOAD_URL  = "http://127.0.0.1:8083/bxibs/user/reload";
     public static void test(){
         Map<String,Object> paramMap = new HashMap<>();
      //   paramMap.put("userName",USER_NAME);
      //   paramMap.put("password",PASSWORD);
         String result2 = HttpRequest.get(RELOAD_URL)
                 .header("apiToken", "4bd1105dd2ed499eba2d2b884fb90376")//头信息，多个头信息多次调用此方法即可
                 .form(null)//表单内容
                 .timeout(20000)//超时，毫秒
                 .execute()
                 .body();
         System.out.println(result2);
     }


     public static String getData(){
         return getBaseData(RELOAD_URL,null,true);
     }

     public static void login(){
         Map<String,Object> paramMap = new HashMap<>();
           paramMap.put("userName",USER_NAME);
           paramMap.put("password",PASSWORD);
         String json = getBaseData(LOGIN_URL,paramMap,false);
         Result result = FastJsonUtils.toBean(json, Result.class);
         apiToken = result.getData().toString();
     }

     public static String getBaseData(String url,Map<String,Object> paramMap,boolean isGET){
         HttpRequest  httpRequest = null;
         if(!isGET){
             httpRequest = HttpRequest.post(url);
         }else{
             httpRequest = HttpRequest.get(url);
         }
         String result = httpRequest.header("apiToken", apiToken)//头信息，多个头信息多次调用此方法即可
                 .form(paramMap)//表单内容
                 .timeout(20000)//超时，毫秒
                 .execute()
                 .body();
         return result;
     }

//4bd1105dd2ed499eba2d2b884fb90376
    public static void main(String[] args) {
        login();
        String data = getData();
        System.out.println(data);
    }
}
