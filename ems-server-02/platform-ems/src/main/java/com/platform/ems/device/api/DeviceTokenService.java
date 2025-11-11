package com.platform.ems.device.api;

import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.exception.CheckedException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.device.response.DeviceResp;
import com.platform.ems.device.response.TokenResp;
import com.platform.ems.service.impl.SystemClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.platform.ems.device.api.DeviceAPI.*;

/**
 * @author Straw
 * @since 2023/3/22
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Slf4j
@Service
public class DeviceTokenService {

    @Autowired
    SystemClientServiceImpl clientService;
    @Autowired
    StringRedisTemplate redis;

    public static String newToken(String appId, String appSecret) throws Exception {
        HttpPost post = new HttpPost(DOMAIN + API_TOKEN);
        post.setEntity(new UrlEncodedFormEntity(getParams(appId, appSecret)));
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {
            return DeviceResp.wrap(response, TokenResp.class).getTokenOrThrow();
        }
    }

    private static List<NameValuePair> getParams(String appId, String appSecret) throws Exception {
        // Create a two-dimensional array to store the parameters
        String[][] paramArray = {
                {"method", "get_info"},
                {"timestamp", String.valueOf(System.currentTimeMillis())},
                {"format", "json"},
                {"app_id", appId},
                {"sign_type", "HMAC-SHA1"}
        };

        TreeMap<String, String> sortedParams = new TreeMap<>();
        for (String[] pair : paramArray) {
            sortedParams.put(pair[0], pair[1]);
        }
        String sign = SignUtil.sign(appSecret, sortedParams);
        // 到这里，参数准备完毕

        // Create a list to store the name-value pairs
        List<NameValuePair> params = new ArrayList<>();
        for (String[] pair : paramArray) {
            params.add(new BasicNameValuePair(pair[0], pair[1]));
        }
        params.add(new BasicNameValuePair("sign", sign));

        return params;
    }

    /**
     * 获取token，有缓存。
     * 并发场景下会有token不一致问题
     */
    public String getToken(String clientId) {
        final String key = REDIS_KEY_PREFIX + clientId;
        String tokenCache = redis.opsForValue().get(key);
        if (tokenCache != null) {
            return tokenCache;
        } else {
            SysClient client = this.clientService.selectSysClientById(clientId);
            try {
                tokenCache = newToken(client.getDeviceAppId(), client.getDeviceAppSecret());
            } catch (Exception e) {
                log.error("获取物联设备token失败", e);
                throw new CheckedException("获取物联设备token失败: " + e);
            }

            redis.opsForValue().set(key, tokenCache, 12, TimeUnit.HOURS);
        }

        return tokenCache;
    }

    public String getTokenOfLoginClient() {
        return getToken(ApiThreadLocalUtil.getLoginUserClientId());
    }

}
