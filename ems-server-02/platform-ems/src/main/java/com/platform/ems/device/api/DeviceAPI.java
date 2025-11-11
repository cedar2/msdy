package com.platform.ems.device.api;

import com.google.gson.Gson;
import com.platform.ems.device.response.DeviceResp;
import com.platform.ems.device.response.StaffSyncResp;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author Straw
 * @since 2023/3/22
 */
public interface DeviceAPI {

    String DOMAIN = "http://xfmonitorapi.xmmyrj.com";

    String REDIS_KEY_PREFIX = "device:sign-verify:";

    String API_TOKEN = "/v1/sign-verify";

    String API_STAFF_SYNC = "/v1/staff/sync";

    String API_STATIONS_SYNC = "/v1/stations/sync";

    String API_SCHEDULING_SYNC = "/v1/production/sync";

    Gson G = new Gson();

    static HttpPost jsonPostReq(String json, String token, String API) {
        HttpPost post = new HttpPost(DOMAIN + API);
        post.setHeader("Content-type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return post;
    }

    static DeviceResp post(String json, String token, String API) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(jsonPostReq(json, token, API))) {
            return DeviceResp.wrap(response, StaffSyncResp.class);
        }
    }
}
