package com.platform.common.security.utils.wx;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

/**
 * 企业微信推送工具类
 * @author c
 */
@SuppressWarnings("all")
public class QiYePushUtil {


    /**
     * 推送文字消息
     * @param touser 目标用户
     * @param agentid 应用id
     * @param text 内容
     * @return
     */
    public static JSONObject SendQyMsgText(String touser, Integer agentid, String text){
        String accessToken=GetWeiXinCode.getQyAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object=new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", WxConstants.MSG_TYPE_TEXT);
        object.put("agentid", agentid);
        JSONObject textJson=new JSONObject();
        textJson.put("content", text);
        object.put("text",textJson );
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject  result = restTemplate.postForObject(currentOpenIdurl,object,JSONObject.class);
        return result;
    }

    /**
     * 推送markdown消息
     * @param touser 目标用户
     * @param agentid 应用id
     * @param text 内容
     * @return
     */
    public static JSONObject SendQyMsgMarkdown(SysUser user, Integer agentid, String text){
        if (StrUtil.isBlank(user.getTouser()) || StrUtil.isBlank(user.getWorkWechatAppkey()) || StrUtil.isBlank(user.getWorkWechatAppsecret())) {
            throw new BaseException("租户未配置");
        }
        String accessToken=GetWeiXinCode.getQyAuthAccessToken(user);
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object=new JSONObject();
        object.put("touser", user.getTouser());
        object.put("msgtype", WxConstants.MSG_TYPE_MARK_DOWN);
        object.put("agentid", agentid);
        JSONObject textJson=new JSONObject();
        textJson.put("content", text);
        object.put("markdown", textJson);
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    /**
     * 推送图文卡片消息
     *
     * @param touser 目标用户
     * @param title  标题
     * @param desc   内容
     * @param url    链接
     * @return
     */
    public static JSONObject sendQyMsgTextCard(SysUser user, String title, String description, String url) {
        if (StrUtil.isBlank(user.getWorkWechatAgentid()) || StrUtil.isBlank(user.getWorkWechatAppkey()) || StrUtil.isBlank(user.getWorkWechatAppsecret())) {
            throw new BaseException("租户未配置");
        }
        String token = GetWeiXinCode.getQyAuthAccessToken(user);
        String sendUrl = GetWeiXinCode.getqyapiSendUrl(token);
        ;
        JSONObject object = new JSONObject();
        object.put("touser", user.getWorkWechatOpenid());
        object.put("msgtype", WxConstants.MSG_TYPE_TEXT_CARD);
        object.put("agentid", user.getWorkWechatAgentid());
        JSONObject textJson = new JSONObject();
        textJson.put("title", title);
        textJson.put("description", description);
        textJson.put("url", url);
        textJson.put("btntxt", "查看详情");
        object.put("textcard", textJson);
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        System.out.println("》=====参数=====《" + object);;
        RestTemplate restTemplate = new RestTemplate();

        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> entity = new HttpEntity<>(object, headers);
        JSONObject result = restTemplate.postForObject(sendUrl, entity, JSONObject.class);

        System.out.println("解析推送返回的obj：" + result);

        //JSONObject result = restTemplate.postForObject(sendUrl, entity, JSONObject.class);

        //System.out.println("解析推送返回的obj：" + result);

        return result;
    }

    public static JSONObject SendQyMsgMarkdownDirectly(SysUser user, Integer agentid, String text) {
        if (StrUtil.isBlank(user.getTouser()) || StrUtil.isBlank(user.getWorkWechatAppkey()) || StrUtil.isBlank(user.getWorkWechatAppsecret())) {
            throw new BaseException("租户未配置");
        }
        String accessToken = GetWeiXinCode.getQyAuthAccessTokenDirectly(user);
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", user.getTouser());
        object.put("msgtype", WxConstants.MSG_TYPE_MARK_DOWN);
        object.put("agentid", agentid);
        JSONObject textJson = new JSONObject();
        textJson.put("content", text);
        object.put("markdown", textJson);
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }


    public static JSONObject SendQyMsgMarkdown(String accessToken, String touser, Integer agentid, String text) {
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", WxConstants.MSG_TYPE_MARK_DOWN);
        object.put("agentid", agentid);
        JSONObject textJson = new JSONObject();
        textJson.put("content", text);
        object.put("markdown",textJson );
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject  result = restTemplate.postForObject(currentOpenIdurl,object,JSONObject.class);
        return result;
    }

    /**
     * 推送视频消息
     * @param touser 目标用户
     * @param agentid 应用id
     * @param file 文件
     * @param description 内容
     * @param title 标题
     * @return
     */
    public static JSONObject SendQyMsgVideo(String touser, Integer agentid, File file, String description, String title){
        String accessToken=GetWeiXinCode.getQyAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object=new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", WxConstants.MSG_TYPE_VIDEO);
        object.put("agentid", agentid);
        JSONObject textJson=new JSONObject();
        String mediaId=GetWeiXinCode.uploadFile(file, WxConstants.FILE_TYPE_VIDEO);
        textJson.put("media_id", mediaId);
        textJson.put("title", title);
        textJson.put("description", description);
        object.put("video",textJson );
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject  result = restTemplate.postForObject(currentOpenIdurl,object,JSONObject.class);
        return result;
    }

    /**
     * 推送视频消息
     * @param touser 目标用户
     * @param agentid 应用id
     * @param fileUrl 文件地址
     * @param description 内容
     * @param title 标题
     * @return
     */
    public static JSONObject SendQyMsgVideo(String touser, Integer agentid, String fileUrl, String description, String title){
        String accessToken=GetWeiXinCode.getQyAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object=new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", WxConstants.MSG_TYPE_VIDEO);
        object.put("agentid", agentid);
        JSONObject textJson=new JSONObject();
        File file=null;
        try {
            file= FileUtils.getFileByUrl(fileUrl);
        }catch (Exception e){
             e.printStackTrace();
             throw new BaseException("获取文件失败");
        }
        String mediaId=GetWeiXinCode.uploadFile(file, WxConstants.FILE_TYPE_VIDEO);
        textJson.put("media_id", mediaId);
        textJson.put("title", title);
        textJson.put("description", description);
        object.put("video",textJson );
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject  result = restTemplate.postForObject(currentOpenIdurl,object,JSONObject.class);
        return result;
    }

    /**
     * 推送公众号模板消息
     * @param touser 目标用户
     * @param template 模板id
     * @param text 内容
     * @return
     */
    public static JSONObject SendWxgzhMsgMarkdown(String touser, String template, Map<String, Object> data) {
        String accessToken = GetWeiXinCode.getWxgzhAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getWxgzhDYSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("template_id", template);
        object.put("data", data);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    public static JSONObject SendWxgzhMsgMarkdownDirectly(String touser, String template, Map<String, Object> data) {
        String accessToken = GetWeiXinCode.getWxgzhAccessTokenDirectly();
        String currentOpenIdurl = GetWeiXinCode.getWxgzhDYSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("template_id", template);
        object.put("data", data);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }
}
