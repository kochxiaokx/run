package org.run.some.httpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.run.some.httpClient.util.HttpClientUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class GetRequest {

    public String getJson(){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8083/bxibs/user/reload");
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }finally {
            HttpClientUtils.close(httpClient,response);
        }
        return null;
    }

    public static void test() throws Exception{
        HttpHost target = new HttpHost("127.0.0.1", 8083, "http");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials("333", "333"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate DIGEST scheme object, initialize it and add it to the local
            // auth cache
            DigestScheme digestAuth = new DigestScheme();
            // Suppose we already know the realm name
            digestAuth.overrideParamter("userName", "333");
            // Suppose we already know the expected nonce value
            digestAuth.overrideParamter("password", "333");
            digestAuth.overrideParamter("version", "2");
            authCache.put(target, digestAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpPost httpget = new HttpPost("http://127.0.0.1:8083/bxibs/user/login");

            System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);
        //    for (int i = 0; i < 3; i++) {
                CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
                try {
                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    System.out.println(EntityUtils.toString(response.getEntity()));
                } finally {
                    response.close();
                }
      //      }
        } finally {
            httpclient.close();
        }
    }

    public static void  test2(){
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String entityStr = null;
        CloseableHttpResponse response = null;

        try {
            /*
             * 由于GET请求的参数都是拼装在URL地址后方，所以我们要构建一个URL，带参数
             */
            URIBuilder uriBuilder = new URIBuilder("http://127.0.0.1:8083/bxibs/user/login");
            /** 第一种添加参数的形式 */
        uriBuilder.addParameter("userName", "333");
        uriBuilder.addParameter("password", "333");
         /*   *//** 第二种添加参数的形式 *//*
            List<NameValuePair> list = new LinkedList<>();
            BasicNameValuePair param1 = new BasicNameValuePair("userName", "333");
            BasicNameValuePair param2 = new BasicNameValuePair("password", "333");
            list.add(param1);
            list.add(param2);*/
          //  uriBuilder.setParameters(list);

            // 根据带参数的URI对象构建GET请求对象
            HttpPost httpGet = new HttpPost(uriBuilder.build());

            /*
             * 添加请求头信息
             */
            // 浏览器表示
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
            // 传输的类型
            httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");

            // 执行请求
            response = httpClient.execute(httpGet);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            entityStr = EntityUtils.toString(entity, "UTF-8");
        } catch (ClientProtocolException e) {
            System.err.println("Http协议出现问题");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("解析错误");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("URI解析异常");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO异常");
            e.printStackTrace();
        } finally {
            // 释放连接
            if (null != response) {
                try {
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    System.err.println("释放连接出错");
                    e.printStackTrace();
                }
            }
        }

        // 打印响应内容
        System.out.println(entityStr);

    }

    public static void main(String[] args) throws Exception {
        test2();
    }
}
