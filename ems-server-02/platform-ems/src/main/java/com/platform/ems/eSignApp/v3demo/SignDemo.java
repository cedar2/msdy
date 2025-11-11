package com.platform.ems.eSignApp.v3demo;

import com.platform.ems.eSignApp.comm.EsignHttpHelper;
import com.platform.ems.eSignApp.comm.EsignHttpResponse;
import com.platform.ems.eSignApp.enums.EsignRequestType;
import com.platform.ems.eSignApp.exception.EsignDemoException;

import java.util.Map;

/**
 *
 * @author  澄泓
 * @date  2022/9/30 18:19
 * @version
 */
public class SignDemo {

    private static String eSignAppId="5111773530";

    private static String eSignAppSecret="3ecc6e70a13e25945133cd7f412b373e";
    //沙箱模拟环境 https://smlopenapi.esign.cn
    //正式生产环境 https://openapi.esign.cn
    private static String eSignHost="https://openapi.esign.cn";

    /**
     * 查询签署流程详情
     * @return
     */
    public static EsignHttpResponse signFlowDetail(String signFlowId) throws EsignDemoException {
        String apiaddr= "/v3/sign-flow/"+ signFlowId + "/detail";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm = null;
        //请求方法
        EsignRequestType requestType = EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId,eSignAppSecret,jsonParm,requestType.name(),apiaddr,true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr,requestType , jsonParm, header,true);
    }

    /**
     * 下载已签署文件及附属材料     *
     * @return
     * @throws EsignDemoException
     */
    public static EsignHttpResponse fileDownloadUrl(String signFlowId) throws EsignDemoException {
        String apiaddr = "/v3/sign-flow/"+ signFlowId +"/file-download-url";
        //请求参数body体,json格式。get或者delete请求时jsonString传空json:"{}"或者null
        String jsonParm=null;
        //请求方法
        EsignRequestType requestType= EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(eSignAppId,eSignAppSecret,jsonParm,requestType.name(),apiaddr,true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(eSignHost, apiaddr,requestType , jsonParm, header,true);
    }
}
