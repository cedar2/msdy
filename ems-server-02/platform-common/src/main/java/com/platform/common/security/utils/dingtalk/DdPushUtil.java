package com.platform.common.security.utils.dingtalk;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.security.utils.wx.GetWeiXinCode;
import com.platform.common.utils.file.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * 钉钉推送工具类
 *
 * @author h
 */
@SuppressWarnings("all")
public class DdPushUtil {


    /**
     * 推送文字消息
     *
     * @param touser  目标用户
     * @param agentid 应用id
     * @param text    内容
     * @return
     */
    public static JSONObject SendQyMsgText(String touser, Integer agentid, String text) {
        String accessToken = GetDingtalkCode.getDdAccessToken();
        String currentOpenIdurl = GetDingtalkCode.getDdapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", DingtalkConstants.MSG_TYPE_TEXT);
        object.put("agentid", agentid);
        JSONObject textJson = new JSONObject();
        textJson.put("content", text);
        object.put("text", textJson);
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    /**
     * 推送markdown消息
     *
     * @param touser  目标用户
     * @param agentid 应用id
     * @param text    内容
     * @return
     */
    public static JSONObject SendDdMsgMarkdown(String touser, Integer agentId, String content, String title) {
        String accessToken = GetDingtalkCode.getDdAccessToken();
        String currentOpenIdurl = GetDingtalkCode.getDdapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("agent_id", agentId);
        object.put("userid_list", touser);
        object.put("to_all_user", false);
        JSONObject textJson = new JSONObject();
        textJson.put("msgtype", DingtalkConstants.MSG_TYPE_MARK_DOWN);
        JSONObject markdownJson = new JSONObject();
        markdownJson.put("text", content);
        markdownJson.put("title", title);
        textJson.put("markdown", markdownJson);
        object.put("msg", textJson);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    /**
     * 推送OA消息
     *
     * @param touser  目标用户
     * @param agentid 应用id
     * @param text    内容
     * @return
     */
    public static JSONObject SendDdMsgOA(SysUser user, Integer agentid, JSONObject content) {
        if (StrUtil.isBlank(user.getTouser()) || StrUtil.isBlank(user.getDingtalkAppkey()) || StrUtil.isBlank(user.getDingtalkAppsecret())) {
            throw new BaseException("租户未配置");
        }
        String accessToken = GetDingtalkCode.getDdAuthAccessToken(user);
        String currentOpenIdurl = GetDingtalkCode.getDdapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("agent_id", agentid);
        object.put("userid_list", user.getTouser());
        object.put("to_all_user", false);
        object.put("msg", content);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    public static JSONObject SendDdMsg(SysUser user, JSONObject content) {
        if (StrUtil.isBlank(user.getDingtalkAgentid()) || StrUtil.isBlank(user.getDingtalkAppkey()) || StrUtil.isBlank(user.getDingtalkAppsecret())) {
            throw new BaseException("租户未配置");
        }
        String accessToken = GetDingtalkCode.getDdAuthAccessTokenDirectly(user);
        String currentOpenIdurl = GetDingtalkCode.getDdapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("agent_id", user.getDingtalkAgentid());
        object.put("userid_list", user.getDingtalkOpenid());
        object.put("to_all_user", false);
        object.put("msg", content);
        System.out.println("钉钉消息提醒： " + object);
        RestTemplate restTemplate = new RestTemplate();
//        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);

        //
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
//        String response = restTemplate.postForObject(currentOpenIdurl, entity, String.class);

        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JSONObject> entity = new HttpEntity<>(object, headers);
        JSONObject response = restTemplate.postForObject(currentOpenIdurl, entity, JSONObject.class);

        System.out.println("解析推送返回的obj：" + response);

        return response;
    }

    public static JSONObject SendDdMsgOADirectly(SysUser user, Integer agentid, JSONObject content) {
        if (StrUtil.isBlank(user.getTouser()) || StrUtil.isBlank(user.getDingtalkAppkey()) || StrUtil.isBlank(user.getDingtalkAppsecret())) {
            throw new BaseException("租户未配置");
        }
        String accessToken = GetDingtalkCode.getDdAuthAccessTokenDirectly(user);
        String currentOpenIdurl = GetDingtalkCode.getDdapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("agent_id", agentid);
        object.put("userid_list", user.getTouser());
        object.put("to_all_user", false);
        object.put("msg", content);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    public static JSONObject SendQyMsgMarkdown(String accessToken, String touser, Integer agentid, String text) {
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", DingtalkConstants.MSG_TYPE_MARK_DOWN);
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

    /**
     * 推送视频消息
     *
     * @param touser      目标用户
     * @param agentid     应用id
     * @param file        文件
     * @param description 内容
     * @param title       标题
     * @return
     */
    public static JSONObject SendQyMsgVideo(String touser, Integer agentid, File file, String description, String title) {
        String accessToken = GetWeiXinCode.getQyAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", DingtalkConstants.MSG_TYPE_VIDEO);
        object.put("agentid", agentid);
        JSONObject textJson = new JSONObject();
        String mediaId = GetWeiXinCode.uploadFile(file, DingtalkConstants.FILE_TYPE_VIDEO);
        textJson.put("media_id", mediaId);
        textJson.put("title", title);
        textJson.put("description", description);
        object.put("video", textJson);
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

    /**
     * 推送视频消息
     *
     * @param touser      目标用户
     * @param agentid     应用id
     * @param fileUrl     文件地址
     * @param description 内容
     * @param title       标题
     * @return
     */
    public static JSONObject SendQyMsgVideo(String touser, Integer agentid, String fileUrl, String description, String title) {
        String accessToken = GetWeiXinCode.getQyAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getqyapiSendUrl(accessToken);
        JSONObject object = new JSONObject();
        object.put("touser", touser);
        object.put("msgtype", DingtalkConstants.MSG_TYPE_VIDEO);
        object.put("agentid", agentid);
        JSONObject textJson = new JSONObject();
        File file = null;
        try {
            file = FileUtils.getFileByUrl(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("获取文件失败");
        }
        String mediaId = GetWeiXinCode.uploadFile(file, DingtalkConstants.FILE_TYPE_VIDEO);
        textJson.put("media_id", mediaId);
        textJson.put("title", title);
        textJson.put("description", description);
        object.put("video", textJson);
        object.put("safe", 0);
        object.put("enable_id_trans", 0);
        object.put("enable_duplicate_check", 0);
        object.put("duplicate_check_interval", 1800);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(currentOpenIdurl, object, JSONObject.class);
        return result;
    }

}
