package com.sky.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

//@SpringBootTest
public class HttpClientTest {
    /*
    测试通过httpclient发送送GET方式的请求
     */
    @Test
    public void testGET() throws IOException {
        //创建httlpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建请求对象
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

        //发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //获取服务端返回的状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("服务端返回的状态码为："+statusCode);

        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        System.out.println("服务端返回的数据："+body);

        //关闭资源
        response.close();
        httpClient.close();
    }

    /*
    测试通过HttpClient发送POST方式的请求
     */
    @Test
    public void testPOST() throws IOException {
        //创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建请求对象
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username","admin");
        jsonObject.put("password","123456");
        //发送请求
        StringEntity stringEntity = new StringEntity(jsonObject.toString());
        //指定请求的编码方式
        stringEntity.setContentEncoding("UTF-8");
        //数据格式
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        //返回码
        int  statusCode = response.getStatusLine().getStatusCode();
        System.out.println("服务端返回的状态码为："+statusCode);

        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        System.out.println("服务端返回的数据："+body);

        //关闭资源
        response.close();
        httpClient.close();
    }
}
