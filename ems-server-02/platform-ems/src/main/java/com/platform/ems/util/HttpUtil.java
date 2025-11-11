package com.platform.ems.util;


import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.common.core.redis.RedisCache;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;


public class HttpUtil {
    @Autowired
    private static RedisCache redisService;

    private static void init() {
        redisService = SpringUtil.getBean(RedisCache.class);
    }
    public static String doPost(String url, JSONObject param,String authorization) {
        HttpPost httpPost = null;
        String result = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            if (param != null) {
                StringEntity se = new StringEntity(param.toString(), "utf-8");
                httpPost.setEntity(se); // post方法中，加入json数据
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Authorization",authorization);
            }
            HttpResponse response = client.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    public static String  getToken(){
        init();
        for (int i=0;i<5;i++){
            Object authorization = redisService.getCacheObject("ckw_authorization");
            if(authorization==null){
                String url="http://192.168.2.63:80/dev-api/auth/login";
                JSONObject jsonObject=new JSONObject();
                jsonObject.putOpt("clientId","10001");
                jsonObject.putOpt("username","yangqz");
                jsonObject.putOpt("password","123456");
                jsonObject.putOpt("code","yangqz");
                jsonObject.putOpt("uuid","1");
                String result = HttpUtil.doPost(url, jsonObject, null);
                JSONObject object = JSONUtil.parseObj(result);
                Object data = object.get("data");
                JSONObject dataCode = JSONUtil.parseObj(data);
                Object access_token = dataCode.get("access_token");
                if(access_token!=null){
                    System.out.println(access_token.toString());
                    redisService.setCacheObject("ckw_authorization",access_token,1L, TimeUnit.MINUTES);
                    return access_token.toString();
                }
            }else{
                return authorization.toString();
            }
        }
        return null;
    }
}
