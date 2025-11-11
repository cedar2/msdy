package com.platform.ems.controller;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.JijiaOpenApi;
import com.platform.ems.task.pdm.PrjProjectWarningTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;


/**
 * 测试调用积加接口服务
 *
 * @author chenkw
 * @date 2023-03-02
 */
@Slf4j
@RestController
@RequestMapping("/aaa/test")
@Api(tags = "测试调用积加接口服务")
public class AtestController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PrjProjectWarningTask warningTask;

    private final static String JIJIA_APPID = "39e47910072d48ccb7a6f01f4e3f9cc5";

    private final static String JIJIA_APPKEY = "63ea113ce4b0246117ea4198";

    private final static String GET_TOKEN_URL = "https://prodopenflat.apist.gerpgo.com/open-api/api_token";

    private final static String GET_TEST_LIST = "https://prodopenflat.apist.gerpgo.com/open-api/api/supply/srm/purchase/procure/dashboardDataGrid";

    private final static String GET_TEST_DAOHUO = "https://prodopenflat.apist.gerpgo.com/open-api/api/v2/supply/gip/warehouse/storageLedger/detailList";

    @ApiOperation(value = "测试即将逾期已逾期", notes = "测试即将逾期已逾期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/testProject")
    public AjaxResult testProject() {
        warningTask.warningProject();
        return AjaxResult.success();
    }

    @ApiOperation(value = "测试自动更新项目状态", notes = "测试自动更新项目状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/testAuto")
    public AjaxResult testAuto() {
        warningTask.autoSetProjectStatus();
        return AjaxResult.success();
    }

    @ApiOperation(value = "测试正常的定时一次采购", notes = "测试正常的定时一次采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/testTask/first")
    public AjaxResult testTaskFirst() {
        String token = warningTask.getToken();
        warningTask.firstPurchaseFlag(token);
        return AjaxResult.success();
    }

    @ApiOperation(value = "测试正常的定时二次采购", notes = "测试正常的定时二次采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/testTask/second")
    public AjaxResult testTaskSecond() {
        String token = warningTask.getToken();
        warningTask.secondPurchaseFlag(token);
        return AjaxResult.success();
    }

    @ApiOperation(value = "测试正常的定时采购到货通知", notes = "测试正常的定时采购到货通知")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/testTask/notice")
    public AjaxResult testTaskNotice() {
        String token = warningTask.getToken();
        warningTask.firstPurchaseArrivalNotice(token);
        return AjaxResult.success();
    }

    @ApiOperation(value = "测试获取数据", notes = "测试获取数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getList")
    public AjaxResult getList(@RequestBody JijiaOpenApi erpCode) {

        // 获取积加ERP的token

        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("appId", JIJIA_APPID);
        jsonObject.putOnce("appKey", JIJIA_APPKEY);

        // 得到返回的数据，token在data.accessToken中
        org.springframework.http.HttpEntity<JSONObject> getToken = new org.springframework.http.HttpEntity<>(jsonObject);
        JSONObject tokenResponse = restTemplate.postForObject(GET_TOKEN_URL, getToken, JSONObject.class);

        // 得到token
        JSONObject tokenResponseData = (JSONObject) tokenResponse.get("data");
        String token = String.valueOf(tokenResponseData.get("accessToken"));

        // 接口请求参数

        // 转为json格式
        JSONObject requestBody = new JSONObject(erpCode);

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
        String test = restTemplate.postForObject(GET_TEST_LIST, httpEntity, String.class);

        // 得到接口返回体中的数据

        JSONObject jsonStr = JSONUtil.parseObj(test);

        JSONObject data = (JSONObject) jsonStr.get("data");
        JSONArray rows = (JSONArray) data.get("rows");

        //循环将json对象数组放到对象中
        List<JijiaOpenApi> jijiaList = JSONUtil.toList(rows, JijiaOpenApi.class);

        return AjaxResult.success(jijiaList);
    }

    @ApiOperation(value = "daohuo", notes = "daohuo")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/daohuo")
    public AjaxResult daohuo(@RequestBody JijiaOpenApi erpCode) {

        // 获取积加ERP的token

        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("appId", JIJIA_APPID);
        jsonObject.putOnce("appKey", JIJIA_APPKEY);

        // 得到返回的数据，token在data.accessToken中
        org.springframework.http.HttpEntity<JSONObject> getToken = new org.springframework.http.HttpEntity<>(jsonObject);
        JSONObject tokenResponse = restTemplate.postForObject(GET_TOKEN_URL, getToken, JSONObject.class);

        // 得到token
        JSONObject tokenResponseData = (JSONObject) tokenResponse.get("data");
        String token = String.valueOf(tokenResponseData.get("accessToken"));

        // 接口请求参数

        // 转为json格式
        JSONObject requestBody = new JSONObject(erpCode);

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
        String test = restTemplate.postForObject(GET_TEST_DAOHUO, httpEntity, String.class);

        // 得到接口返回体中的数据

        JSONObject jsonStr = JSONUtil.parseObj(test);

        JSONObject data = (JSONObject) jsonStr.get("data");
        JSONArray rows = (JSONArray) data.get("rows");

        //循环将json对象数组放到对象中
        List<JijiaOpenApi> jijiaList = JSONUtil.toList(rows, JijiaOpenApi.class);

        return AjaxResult.success(jijiaList);
    }

}
