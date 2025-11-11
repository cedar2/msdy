package com.platform.ems.eSignApp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.platform.ems.eSignApp.comm.EsignEncryption;
import com.platform.ems.eSignApp.domain.Result;
import com.platform.ems.eSignApp.constant.SignAppInfo;
import com.platform.ems.eSignApp.domain.SignFlowCallback;
import com.platform.ems.service.ISalSaleContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 澄泓
 * 接收e签宝服务端回调通知示例
 * @date 2022/9/27 19:15
 */
@Slf4j
@RestController
@RequestMapping("/eSignPartner")
public class CallbackController {

    @Autowired
    private ISalSaleContractService saleContractService;

    public final static String ACTION_SIGN_FLOW_COMPLETE = "SIGN_FLOW_COMPLETE";

    // 验证签名
    public boolean isSuccess(String timestamp, String signture, String body) {
        // 请求回调地址中的query参数值,根据key排序后把参数值拼接
        String requestQuery = "";
        System.out.println("拼接的query值:" + requestQuery);
        System.out.println("时间戳:" + timestamp);
        System.out.println("签名值:" + signture);
        System.out.println("回调bod:y" + body);
        // 对e签宝服务端推送的数据进行验签
        return EsignEncryption.callBackCheck(timestamp, requestQuery, body, SignAppInfo.E_SIGN_APP_SECRET, signture);
    }

    @PostMapping(value = "/callback")
    public Result callBack(@RequestHeader(name = "X-Tsign-Open-TIMESTAMP") String timestamp,
                           @RequestHeader(name = "X-Tsign-Open-SIGNATURE") String signture,
                           @RequestBody String body) {
        //验签
        if (isSuccess(timestamp, signture, body)) {
            //验签通过
            System.out.println("验签通过，根据不同的action事件做自己的业务处理...");
            Gson gson = new Gson();
            JsonObject bodyJson = gson.fromJson(body, JsonObject.class);
            String action = bodyJson.get("action").getAsString();
            String signFlowId = bodyJson.get("signFlowId").getAsString();

            // 签署完成流程结束回调
            if (ACTION_SIGN_FLOW_COMPLETE.equals(action)) {
                // 请求参数
                SignFlowCallback signFlowCallback = gson.fromJson(body, SignFlowCallback.class);
                return Result.success(saleContractService.addContractAttachEsign(signFlowCallback));
            }

//            // 比如：下载已签署文件及附属材料
//            EsignHttpResponse fileDownloadUrl = SignDemo.fileDownloadUrl(signFlowId);
//            JsonObject fileDownloadUrlJsonObject = gson.fromJson(fileDownloadUrl.getBody(),JsonObject.class);
//            JsonObject fileDownloadUrlArray = fileDownloadUrlJsonObject.getAsJsonObject("data");
//            System.out.println(fileDownloadUrlArray);
//
//
//            // 比如：查询签署流程详情
//            EsignHttpResponse signFlowDetail = SignDemo.signFlowDetail(signFlowId);
//            JsonObject signFlowDetailJsonObject = gson.fromJson(signFlowDetail.getBody(),JsonObject.class);
//            System.out.println(signFlowDetailJsonObject);

        } else {
            log.warn("验签未通过");
            return Result.error("验签未通过");
        }
        return Result.success();
    }

}
