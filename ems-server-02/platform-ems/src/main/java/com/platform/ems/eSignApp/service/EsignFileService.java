package com.platform.ems.eSignApp.service;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.platform.ems.eSignApp.comm.EsignHttpCfgHelper;
import com.platform.ems.eSignApp.comm.EsignHttpHelper;
import com.platform.ems.eSignApp.comm.EsignHttpResponse;
import com.platform.ems.eSignApp.constant.SignApiPath;
import com.platform.ems.eSignApp.constant.SignAppInfo;
import com.platform.ems.eSignApp.constant.SignConstants;
import com.platform.ems.eSignApp.domain.EsignFile;
import com.platform.ems.eSignApp.util.SignFileUtil;
import com.platform.ems.eSignApp.util.SignResponseUtil;
import com.platform.ems.eSignApp.enums.EsignRequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenkw
 * @since 2024-02-21
 */
@Slf4j
@Service
public class EsignFileService {

    @Resource
    private SignFileUtil signFileUtil;

    private static final String E_SIGN_HOST = SignApiPath.E_SIGN_HOST;

    /**
     * 先获取到服务端的上传地址fileUploadUrl和文件fileId;
     */
    public EsignFile getFileUploadUrl(String filePath, String fileName) throws Exception {
        Gson gson = new Gson();

        // 获取文件上传地址
        String apiAuthUrl = SignApiPath.FILE_UPLOAD_URL;
        // 请求方式
        EsignRequestType requestType = EsignRequestType.POST;
        // 文件 contentMd5的值
        EsignFile esignFile = new EsignFile();
        // 临时文件
        File pdf = signFileUtil.getTempFile(filePath, fileName);
        // 计算文件的Content-MD5
        String contentMd5 = signFileUtil.getFileContentMD5(pdf);
        esignFile.setContentMd5(contentMd5);
        // 获得文件的大小
        esignFile.setFileSize(Integer.parseInt(String.valueOf(pdf.length())));

        boolean convertToPDF = false;
        // 如果不是.pdf文件
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            String result = fileName.substring(lastDotIndex + 1);
            if (!"pdf".equals(result)) {
                convertToPDF = true;
            }
        }

        // body参数
        String json = "{\n" +
                "\"contentMd5\":\"" + esignFile.getContentMd5() + "\",\n" +
                "\"contentType\":\"" + SignConstants.APPLICATION_PDF + "\",\n" +
                "\"fileName\":\"" + fileName + "\",\n" +
                "\"fileSize\":" + esignFile.getFileSize() + ",\n" +
                "\"convertToPDF\":" + convertToPDF + ",\n" +
                "\"convertToHTML\":" + false + "\n" +
                "}";
        System.out.println("获取文件上传地址请求参数：" + json);
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                SignAppInfo.E_SIGN_APP_SECRET, json, requestType.name(), apiAuthUrl, false);
        System.out.println("获取文件上传地址请求头header：" + header);
        // 发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiAuthUrl, requestType, json, header, false);
        System.out.println("获取文件上传地址返回：" + esignHttpResponse.getRespData());
        // 得到信息
        JsonObject jsonObject = gson.fromJson(esignHttpResponse.getRespData(), JsonObject.class);
        if (SignResponseUtil.codeAndMessage(jsonObject) && SignConstants.CODE_0.equals(jsonObject.get(SignConstants.CODE).getAsString())
                && SignConstants.MESSAGE_CG.equals(jsonObject.get(SignConstants.MESSAGE).getAsString()) ) {
            esignFile.setFileId(jsonObject.getAsJsonObject("data").get("fileId").getAsString())
                    .setFileUploadUrl(jsonObject.getAsJsonObject("data").get("fileUploadUrl").getAsString());

            // 执行上传
            execFileUpload(esignFile, pdf);

            // 删除临时文件
            pdf.deleteOnExit();

            return esignFile;
        }
        else {
            throw new Exception(jsonObject.get(SignConstants.MESSAGE).getAsString() + ": " + jsonObject.get(SignConstants.CODE).getAsString());
        }
    }

    /**
     * 上传文件流
     */
    public EsignFile execFileUpload(EsignFile esignFile, File file) throws IOException {
        if (esignFile != null && StrUtil.isNotBlank(esignFile.getFileUploadUrl())) {
            Gson gson = new Gson();
            // 文件上传接口地址
            String fileUploadUrl = esignFile.getFileUploadUrl();
            System.out.println("文件上传请求地址：" + fileUploadUrl);
            // 文件流数组
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            // 请求头
            Map<String, String> header = new HashMap<>();
            header.put("Content-MD5", esignFile.getContentMd5());
            header.put("Content-Type", SignConstants.APPLICATION_PDF);
            System.out.println("文件上传请求头header：" + header);
            // 发起接口请求
            EsignHttpResponse esignHttpResponse =  EsignHttpCfgHelper.sendHttp(EsignRequestType.PUT, fileUploadUrl, header, fileBytes, false);
            System.out.println("文件上传返回：" + esignHttpResponse.getRespData());
            // 得到信息
            JsonObject jsonObject = gson.fromJson(esignHttpResponse.getRespData(), JsonObject.class);
            if (SignResponseUtil.errCodeAndMsg(jsonObject) && !SignConstants.CODE_0.equals(jsonObject.get(SignConstants.ERRCODE).getAsString())) {
                log.error("e签宝上传文件流接口调用失败 ：" + jsonObject.get(SignConstants.MSG).getAsString());
            }
        }
        else {
            log.warn("e签宝上传文件方法接收到的上传地址为空");
        }
        return esignFile;
    }

    /**
     * 查询文件上传状态
     */
    public EsignFile getFileStatus(String fileId) throws Exception {
        if (StrUtil.isBlank(fileId)) {
            log.warn("查询文件上传状态的文件ID为空");
            return new EsignFile().setFileId(fileId);
        }
        Gson gson = new Gson();
        // 查询文件上传状态
        String apiAuthUrl = SignApiPath.FILE_STATUS + fileId;
        // 请求方式
        EsignRequestType requestType = EsignRequestType.GET;
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                SignAppInfo.E_SIGN_APP_SECRET, null, requestType.name(), apiAuthUrl, false);
        // 发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiAuthUrl, requestType, null, header, false);
        System.out.println("获取文件上传地址返回：" + esignHttpResponse.getRespData());
        // 得到信息
        JsonObject jsonObject = gson.fromJson(esignHttpResponse.getRespData(), JsonObject.class);
        if (SignResponseUtil.codeAndMessage(jsonObject) && SignConstants.CODE_0.equals(jsonObject.get(SignConstants.CODE).getAsString())
                && SignConstants.MESSAGE_CG.equals(jsonObject.get(SignConstants.MESSAGE).getAsString()) ) {
            return gson.fromJson(jsonObject.getAsJsonObject(SignConstants.DATA), EsignFile.class);
        }
        else {
            throw new Exception(jsonObject.get(SignConstants.MESSAGE).getAsString() + ": " + jsonObject.get(SignConstants.CODE).getAsString());
        }
    }

}
