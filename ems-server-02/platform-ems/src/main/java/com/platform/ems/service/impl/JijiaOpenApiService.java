package com.platform.ems.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.ems.domain.JijiaOpenApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 积加开放接口
 *
 * @author chenkw
 * @date 2023-03-06
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class JijiaOpenApiService {

    @Autowired
    private RestTemplate restTemplate;

    private final static String JIJIA_APPID = "39e47910072d48ccb7a6f01f4e3f9cc5";

    private final static String JIJIA_APPKEY = "64eeae9ee4b0f7a96ba19a63";

    private final static String GET_TOKEN_URL = "https://prodopenflat.apist.gerpgo.com/open-api/api_token";

    /**
     * 获取token
     *
     */
    public String getToken() {

        // 获取积加ERP的token
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("appId", JIJIA_APPID);
        jsonObject.putOnce("appKey", JIJIA_APPKEY);

        // 接口请求
        org.springframework.http.HttpEntity<JSONObject> getToken = new org.springframework.http.HttpEntity<>(jsonObject);
        JSONObject tokenResponse = restTemplate.postForObject(GET_TOKEN_URL, getToken, JSONObject.class);

        // 得到返回的数据，token在data.accessToken中
        JSONObject tokenResponseData = (JSONObject) tokenResponse.get("data");
        String token = String.valueOf(tokenResponseData.get("accessToken"));

        return token;
    }

    /**
     * 获取数据
     *
     */
    public List<JijiaOpenApi> selectList(JijiaOpenApi jijia, String token, String url) {

        // 接口请求参数  转为json格式
        JSONObject requestBody = new JSONObject(jijia);

        // 请求参数后面拼接appKey
        String bodyParam = requestBody.toString();
        String sign = bodyParam + JIJIA_APPKEY;

        // 将上述字段用md5加密（32位小写）
        String md5String = DigestUtil.md5Hex(sign);

        // header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("accessToken", token);
        httpHeaders.add("sign", md5String);

        // 调用积加ERP获取xxx数据的接口
        org.springframework.http.HttpEntity<String> httpEntity = new org.springframework.http.HttpEntity<>(bodyParam, httpHeaders);
        String test = restTemplate.postForObject(url, httpEntity, String.class);

        // 得到接口返回体中的数据
        JSONObject jsonStr = JSONUtil.parseObj(test);

        JSONObject data = (JSONObject) jsonStr.get("data");

        List<JijiaOpenApi> jijiaList = new ArrayList<>();

        try {
            if (data.get("rows") != null) {
                JSONArray rows = (JSONArray) data.get("rows");

                //循环将json对象数组放到对象中
                jijiaList = JSONUtil.toList(rows, JijiaOpenApi.class);
            }
        } catch (Exception e) {}

        return jijiaList;
    }

    /**
     * 采购订单
     *
     */
    public List<JijiaOpenApi> selectPurchaseFlag(JijiaOpenApi jijia, String token) {
        jijia.setPage(1).setPagesize(1000);
        String url = "https://prodopenflat.apist.gerpgo.com/open-api/api/supply/srm/purchase/procure/dashboardDataGrid";
        return selectList(jijia, token, url);
    }

    /**
     * 一次采购到货通知
     *
     */
    public List<JijiaOpenApi> selectArrivalNoticeFlagFirstPurchase(JijiaOpenApi jijia, String token) {
        jijia.setPage(1).setPagesize(500);
        HashMap<String,Object> model = new HashMap<>();
        LocalDate localDateBegin = LocalDate.now().minusDays(30);
        LocalDate localDateEnd = LocalDate.now();
        model.put("beginReportDate", localDateBegin.toString());
        model.put("endReportDate", localDateEnd.toString());
        model.put("mskus", jijia.getMskus());
        model.put("eventTypes", jijia.getEventTypes());
        jijia.setModel(model).setSkus(null).setEventTypes(null);
        String url = "https://prodopenflat.apist.gerpgo.com/open-api/api/v2/supply/gip/warehouse/storageLedger/detailList";
        return selectList(jijia, token, url);
    }

    /**
     * 查询商品信息列表
     *
     */
    public List<JijiaOpenApi> selectProductList(JijiaOpenApi jijia, String token) {
        jijia.setPage(1).setPagesize(500).setSort("addDate").setOrder("ascend");
        String url = "https://prodopenflat.apist.gerpgo.com/open-api/api/v2/channel/selling";
        return selectList(jijia, token, url);
    }

}
