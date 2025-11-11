package com.platform.ems.eSignApp.service;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.platform.ems.eSignApp.comm.EsignHttpHelper;
import com.platform.ems.eSignApp.comm.EsignHttpResponse;
import com.platform.ems.eSignApp.constant.SignApiPath;
import com.platform.ems.eSignApp.constant.SignAppInfo;
import com.platform.ems.eSignApp.constant.SignConstants;
import com.platform.ems.eSignApp.domain.EsignFile;
import com.platform.ems.eSignApp.domain.SignFlowConfig;
import com.platform.ems.eSignApp.domain.SignerPsnInfo;
import com.platform.ems.eSignApp.enums.EsignRequestType;
import com.platform.ems.eSignApp.util.SignResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author chenkw
 * @since 2024-02-23
 */
@Slf4j
@Service
public class EsignFlowService {

    private static final String E_SIGN_HOST = SignApiPath.E_SIGN_HOST;

    /**
     * 开发者可基于 已上传的合同文件 或 模板所填充生成的文件 来发起签署流程。
     */
    public String execSignFlowPsn(EsignFile esignFile, SignFlowConfig signFlowConfig,
                                  List<SignerPsnInfo> signerPsnInfos) throws Exception {

        Gson gson = new Gson();

        // 请求文件签署地址
        String apiAuthUrl = SignApiPath.SIGN_FLOW_CREATE_BY_FILE;

        // 请求方式
        EsignRequestType requestType = EsignRequestType.POST;

        String signerStr = "";
        for (SignerPsnInfo signer : signerPsnInfos) {
            if (StrUtil.isNotBlank(signerStr)) {
                signerStr = signerStr + ",\n";
            }
            signerStr = signerStr +
                    "        {\n" +
                    "            \"noticeConfig\": {\n" +
                    // 短信通知
                    "                \"noticeTypes\": \"" + 1 + "\"\n" +
                    "            },\n" +
                    // 签署方类型，0 - 个人，1 - 机构，2 - 法定代表人，3 - 经办人
                    "            \"signerType\": " + 0 + ",\n" +
                    "            \"psnSignerInfo\": {\n" +
                    "                \"psnAccount\": \"" + signer.getPsnAccount() + "\",\n" +
                    "                \"psnInfo\": {\n" +
                    "                    \"psnName\": \"" + signer.getPsnInfo().getPsnName() + "\"\n" +
                    "                }\n" +
                    "            },\n" +
                    "            \"signFields\": [\n" +
                    "                {\n" +
                    "                    \"fileId\": \"" + esignFile.getFileId() + "\",\n" +
                    "                    \"normalSignFieldConfig\": {\n" +
                    "                        \"freeMode\": " + true + "\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" ;
        }

        String signers =
                "    \"signers\": [\n" +
                        signerStr +
                "    ]\n" ;


        // 请求参数
        String json = "{\n" +
                "    \"docs\": [\n" +
                "        {\n" +
                "            \"fileId\": \"" + esignFile.getFileId() + "\",\n" +
                "            \"fileName\": \"" + esignFile.getFileName() + "\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"signFlowConfig\": {\n" +
                "        \"signFlowTitle\": \"" + signFlowConfig.getSignFlowTitle() + "\",\n" +
                "        \"notifyUrl\": \"" + "https://wap.dcscm.vip/prod-api/ems/eSignPartner/callback" + "\",\n" +
                "        \"autoFinish\": " + true + "\n" +
                "    },\n" +
                    signers +
                "}";
        System.out.println("获取文件签署地址请求参数：" + json);
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(SignAppInfo.E_SIGN_APP_ID,
                SignAppInfo.E_SIGN_APP_SECRET, json, requestType.name(), apiAuthUrl, false);
        System.out.println("获取文件签署地址请求头header：" + header);
        // 发起接口请求
        EsignHttpResponse esignHttpResponse = EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiAuthUrl, requestType, json, header, true);
        System.out.println("获取文件签署地址返回：" + esignHttpResponse.getRespData());
        // 得到信息
        JsonObject jsonObject = gson.fromJson(esignHttpResponse.getRespData(), JsonObject.class);
        if (SignResponseUtil.codeAndMessage(jsonObject) && SignConstants.CODE_0.equals(jsonObject.get(SignConstants.CODE).getAsString())
                && SignConstants.MESSAGE_CG.equals(jsonObject.get(SignConstants.MESSAGE).getAsString()) ) {
            // 返回签署流程ID（建议开发者保存此流程ID）
            return jsonObject.getAsJsonObject("data").get("signFlowId").getAsString();
        }
        else {
            throw new Exception(jsonObject.get(SignConstants.MESSAGE).getAsString() + ": " + jsonObject.get(SignConstants.CODE).getAsString());
        }
    }


}
