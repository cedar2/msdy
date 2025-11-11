package com.platform.ems.eSignApp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.platform.ems.eSignApp.constant.SignConstants;
import com.platform.ems.eSignApp.domain.InitRequest;
import com.platform.ems.eSignApp.domain.dto.request.ESignFlowRequest;
import com.platform.ems.eSignApp.domain.dto.response.ESignDownloadResponse;
import com.platform.ems.eSignApp.domain.dto.response.ESignFlowInfosResponse;
import com.platform.ems.eSignApp.comm.EsignHttpHelper;
import com.platform.ems.eSignApp.comm.EsignHttpResponse;
import com.platform.ems.eSignApp.constant.SignApiPath;
import com.platform.ems.eSignApp.constant.SignAppInfo;
import com.platform.ems.eSignApp.enums.EsignRequestType;
import com.platform.ems.eSignApp.exception.EsignDemoException;
import com.platform.ems.eSignApp.util.SignResponseUtil;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author duya
 * @since 2022-09-20
 */
@Service
public class JssdkService {

    private static final String E_SIGN_HOST = SignApiPath.E_SIGN_HOST;

    /**
     * 判断企业有没有认证
     *   已认证：获取发起签署的可视化页面；
     *   未认证：获取机构认证&授权页面链接
     */
    public EsignHttpResponse getInitData(InitRequest request) {
        Gson gson = new Gson();

        // 查询机构认证信息
        String apiAuthUrl = SignApiPath.ORG_IDENTITY_INFO + (String) request.getOrgAuthConfig().get("orgName");
        //获取机构认证信息
        EsignRequestType requestType = EsignRequestType.GET;
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                SignAppInfo.E_SIGN_APP_SECRET, null, requestType.name(), apiAuthUrl, false);
        //发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiAuthUrl, requestType, null, header, false);
        System.out.println("授权信息为：" + esignHttpResponse.getRespData());
        //判断是否授权了
        JsonObject getOrgIdentityInfoObject = gson.fromJson(esignHttpResponse.getRespData(), JsonObject.class);
        //授权状态authorizeUserInfo 是否授权身份信息给当前应用 true - 已授权，false - 未授权
        String authorizeUserInfo = getOrgIdentityInfoObject.getAsJsonObject("data").get("authorizeUserInfo").getAsString();
        if ("true".equals(authorizeUserInfo)) {
            //1.1已经授权,直接跳转签署界面
            // 通过页面发起签署 通过此接口开发者将获取到一个发起签署可视化页面（免登录），由用户在此页面中选择签署文件、签署方等，并设置签署区位置等相关信息，发起签署流程。
            String apiSignFlowUrl = SignApiPath.SIGN_FLOW_INITIATE_URL_BY_FILE;
            requestType = EsignRequestType.POST;
            // 参数
            String UUID = java.util.UUID.randomUUID().toString();
            String json = "{\n" +
                    "    \"initiatePageConfig\": {\n" +
                    "        \"customBizNum\": \"" + UUID + "\",\n" +
                    "        \"uneditableFields\": [\n" +
                    "            \"copiers\"\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    \"signFlowConfig\": {\n" +
                    "        \"signFlowTitle\": \"发起合同签署\",\n" +
                    "        \"autoFinish\": true,\n" +
                    "        \"noticeConfig\": {\n" +
                    "            \"noticeTypes\": \"1\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            //请求方法
            System.out.println("json:" + json);
            //生成签名鉴权方式的的header
            header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                    SignAppInfo.E_SIGN_APP_SECRET, json, requestType.name(), apiSignFlowUrl, false);
            // 返回 data : signFlowInitiateUrl 发起签署页面链接
            EsignHttpResponse mm = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiSignFlowUrl, requestType, json, header, false);
            return mm;
        } else {
            //1.2没有授权，直接跳转认证界面
            //请求参数body体,json格式
            String reqBody = new Gson().toJson(request);
            System.out.println("开发者前端传参：" + reqBody);
            //请求方法
            requestType = EsignRequestType.POST;
            // 获取机构认证&授权页面链接
            String apiUrl = SignApiPath.ORG_AUTH_URL;
            //生成签名鉴权方式的的header
            header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                    SignAppInfo.E_SIGN_APP_SECRET, reqBody, requestType.name(), apiUrl, false);
            //发起接口请求
            EsignHttpResponse response = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiUrl, requestType, reqBody, header, false);
            // code 业务码，0表示成功，非0表示异常。
            // data 里返回 authUrl 机构认证授权长链接， authShortUrl 机构认证授权短链接， authFlowId 本次认证授权流程ID
            // （开发者请注意保管流程ID，可用于【查询认证授权流程详情】）
            return response;
        }
    }

    /**
     * 查询签署流程列表
     */
    public ESignFlowInfosResponse selectESignFlowList(ESignFlowRequest eSignFlowRequest) {
        EsignHttpResponse signFlowList = signFlowList(eSignFlowRequest);
        System.out.println("signFlowList :" + JSON.toJSONString(signFlowList));
        return JSONObject.parseObject(signFlowList.getRespData(), new TypeReference<ESignFlowInfosResponse>() {});
    }

    /**
     * 查询签署流程列表的方法
     */
    public static EsignHttpResponse signFlowList(ESignFlowRequest eSignFlowRequest) throws EsignDemoException {
        // 查询签署流程列表
        String apiaddr = SignApiPath.SIGN_FLOW_LIST;

        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null

        long startTime = 0L;
        long endTime = 0L;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(eSignFlowRequest.getBeginTime());
            startTime = date.getTime();
            date = simpleDateFormat.parse(eSignFlowRequest.getEndTime());
            endTime = date.getTime();
        } catch (Exception e) {
        }

        String orgArgValue = "";
        // 机构签署方信息 或者 签署操作人
        if ("org".equals(eSignFlowRequest.getOrgType())) {
            orgArgValue = "    \"organization\": {\n" +
                    "        \"orgName\": \"" + eSignFlowRequest.getCompanyName() + "\",\n" +
                    "        \"orgId\": \"\"\n" +
                    "    },\n";
        } else {      //个人
            orgArgValue = "    \"operator\": {\n" +
                    "        \"psnAccount\": \"" + eSignFlowRequest.getCompanyName() + "\",\n" +
                    "        \"psnId\": \"\"\n" +
                    "    },\n";
        }

        String jsonParm = "{\n" +
                orgArgValue +
                "    \"pageNum\": " + eSignFlowRequest.getPageNum() + ",\n" +
                "    \"pageSize\": " + eSignFlowRequest.getPageSize() + ",\n" +
                "    \"signFlowStartTimeFrom\": " + startTime + ",\n" +
                "    \"signFlowStartTimeTo\": " + endTime + ",\n" +
                "    \"signFlowStatus\": [\n" +
                "        " + eSignFlowRequest.getHandleContractList() + "]\n" +
                "}";
        //请求方法
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                SignAppInfo.E_SIGN_APP_SECRET, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiaddr, requestType, jsonParm, header, true);
    }

    /**
     * 查询签署文件地址
     */
    public ESignDownloadResponse selectDownloadUrl(String signFlowId) throws Exception {
        EsignHttpResponse downloadList = fileDownloadUrl(signFlowId);
        System.out.println("downloadUrl :" + JSON.toJSONString(downloadList));
        return JSONObject.parseObject(downloadList.getRespData(), new TypeReference<ESignDownloadResponse>() {});
    }

    /**
     * 下载已签署文件及附属材料     *
     */
    public static EsignHttpResponse fileDownloadUrl(String signFlowId) throws Exception {
        String apiaddr = SignApiPath.V3_SIGN_FLOW + signFlowId + SignApiPath.FILE_DOWNLOAD_URL;
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = null;
        //请求方法
        EsignRequestType requestType = EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                SignAppInfo.E_SIGN_APP_SECRET, jsonParm, requestType.name(), apiaddr, true);
        //发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiaddr, requestType, jsonParm, header, true);
        System.out.println("获取文件上传地址返回：" + esignHttpResponse.getRespData());
        // 得到信息
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(esignHttpResponse.getRespData(), JsonObject.class);
        if (SignResponseUtil.codeAndMessage(jsonObject) && SignConstants.CODE_0.equals(jsonObject.get(SignConstants.CODE).getAsString())
                && SignConstants.MESSAGE_CG.equals(jsonObject.get(SignConstants.MESSAGE).getAsString()) ) {
            return esignHttpResponse;
        }
        else {
            throw new Exception(jsonObject.get(SignConstants.MESSAGE).getAsString() + ": " + jsonObject.get(SignConstants.CODE).getAsString());
        }
    }
}
