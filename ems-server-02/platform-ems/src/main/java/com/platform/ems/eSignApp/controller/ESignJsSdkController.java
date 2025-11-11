package com.platform.ems.eSignApp.controller;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.eSignApp.domain.*;
import com.platform.ems.eSignApp.domain.dto.request.ESignFlowRequest;
import com.platform.ems.eSignApp.domain.dto.response.ESignDownloadResponse;
import com.platform.ems.eSignApp.domain.dto.response.ESignFlowInfosResponse;
import com.platform.ems.eSignApp.service.EsignFileService;
import com.platform.ems.eSignApp.service.EsignFlowService;
import com.platform.ems.eSignApp.service.JssdkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 澄泓
 * 为前端 eSignPartner 组件初始化而封装的接口，用于获取 jsSdkTicket 授权票据等数据
 * @since 2022-09-19
 */
@RestController
@RequestMapping("/eSignPartner")
@Api(tags = "e签宝信息")
//跨域处理
//@CrossOrigin(origins = "*")
public class ESignJsSdkController extends BaseController {

    @Resource
    JssdkService jssdkService;
    @Resource
    EsignFileService esignFileService;
    @Resource
    EsignFlowService esignFlowService;

    @PostMapping("/file/upload")
    public AjaxResult uploadFile(EsignFile file) throws Exception {
        EsignFile esignFile = esignFileService.getFileUploadUrl(file.getFilePath(), file.getFileName());
        return AjaxResult.success(esignFile);
    }

    @PostMapping("/file/upload/status")
    public AjaxResult uploadStatus(String fileId) throws Exception {
        EsignFile esignFile = esignFileService.getFileStatus(fileId);
        return AjaxResult.success(esignFile);
    }

    @PostMapping("/sign/flow/psn/byfile")
    public AjaxResult signFlowPsn(@RequestBody Map<String, Object> requestBody) throws Exception {
        Gson gson = new Gson();
        EsignFile esignFile = gson.fromJson(gson.toJson(requestBody.get("esignFile")), EsignFile.class);
        SignerPsnInfo signerPsnInfo = gson.fromJson(gson.toJson(requestBody.get("psnSignerInfo")), SignerPsnInfo.class);
        SignFlowConfig signFlowConfig = new SignFlowConfig();
        signFlowConfig.setSignFlowTitle("发起签署");
        List<SignerPsnInfo> signerPsnInfoList = new ArrayList<>();
        signerPsnInfoList.add(signerPsnInfo);
        String signFlowId = esignFlowService.execSignFlowPsn(esignFile, signFlowConfig, signerPsnInfoList);
        return AjaxResult.success(null, signFlowId);
    }

    /**
     * @description: 获取机构授权信息信息，判断是否已经授权，可以直接发起签署，如果还没有授权，跳转授权信息的地方
     * @author: lgt
     * @date: 2024/1/29 13:50
     **/
    @ApiOperation(value = "获取e签宝授权或者发布签署合同的界面", notes = "获取e签宝授权或者发布签署合同的界面")
    @RequestMapping("/init")
    public Result getInitData(@RequestBody InitRequest request) {
        // 获取 e签宝 相关界面
        String respData = jssdkService.getInitData(request).getRespData();
        System.out.println("e签宝 相关界面:" + respData);
        // 将获取的 e签宝 相关界面数据原封返回给前端
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(respData, JsonObject.class);

        JsonObject data = jsonObject.get("data").getAsJsonObject();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("signFlowInitiateUrl", data.get("signFlowInitiateUrl").getAsString());
        dataMap.put("signFlowInitiateLongUrl", data.get("signFlowInitiateLongUrl").getAsString());

        return Result.success(jsonObject.get("code").getAsInt(), jsonObject.get("message").getAsString(), dataMap);
    }

    /**
     * 查询合同列表
     */
    @PostMapping("/getFlowList")
    @ApiOperation(value = "查询合同列表", notes = "查询合同列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult getFlowList(@RequestBody ESignFlowRequest eSignFlowRequest) {
        startPage(eSignFlowRequest);
        ESignFlowInfosResponse flowInfos = jssdkService.selectESignFlowList(eSignFlowRequest);
        return AjaxResult.success(flowInfos.getData());
    }

    /**
     * 下载已签署文件及附属材料
     */
    @GetMapping(value = "/downloadEsignFile/{signFlowId}")
    @ApiOperation(value = "下载已签署文件及附属材料", notes = "下载已签署文件及附属材料")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult downloadEsignFile(@PathVariable("signFlowId") String signFlowId) throws Exception {
        ESignDownloadResponse fileDownloadUrl = jssdkService.selectDownloadUrl(signFlowId);
        return AjaxResult.success(fileDownloadUrl.getData());
    }

}
