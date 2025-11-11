package com.platform.common.security.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.security.utils.dingtalk.DdPushUtil;
import com.platform.common.security.utils.dingtalk.DingtalkConstants;
import com.platform.common.security.utils.feishu.FeishuPushUtil;
import com.platform.common.security.utils.wx.QiYePushUtil;
import com.platform.common.security.utils.wx.WxConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Straw
 * @date 2023/1/12
 */
@SuppressWarnings("SpellCheckingInspection")
public class PushMessageTest {

    static SysUser user;
    static SysClient client;


    static {
        initAppIdAndAppSecret();
        user = new SysUser();
        // 斌哥
        String[] openIds = {
                "ou_b3c610f7f589d02664a01119b5380ab4",
                "manager6871",
                "ChenMingBin",
                "oDBJ45iqEooKfGUM3qyKSV4irj3I"
        };

        if (1 == 1) {
            openIds = new String[]{
                    "ou_ee69bc957998cd419590ad7cfde6fe4c",
                    "071950654026322849",
                    "LinXinQi",
                    "oDBJ45i7OZpySSiBFR9stgm4H6ls"
            };
        }

        user.feishuOpenId = openIds[0];
        user.setDingtalkOpenid(openIds[1]);
        user.setWorkWechatOpenid(openIds[2]);
        user.setWxGzhOpenid(openIds[3]);
    }


    public static void main(String[] args) {

        /* 飞书 */
        FeishuPushUtil.push(
                user,
                client,
                "工艺单更新通知",
                "款号：test_231",
                "款名称：工艺单测试666",
                "产品季：",
                "上传人：林新祺",
                "上传时间：2023-01-06 18:13:50",
                "备注：请按最新工艺单组织生产，谢谢！"
        );

        /* 钉钉 */
        dingTalkPush();

        /* 企微 */
        workWechatPush();

        /* 公众号 */
        gzhPush();
    }

    private static void gzhPush() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("first", "test");
        data.put("keyword1", "test");
        data.put("keyword2", "test");
        data.put("keyword3", "test");
        data.put("keyword4", "test");
        data.put("keyword5", "test");
        data.put("remark", "test");
        // 工艺单的templateId
        String templateId = "IiAqirFND7ugn9zt6HeCZYuWCl5M-1rYBklJi39CG48";
        System.out.println(QiYePushUtil.SendWxgzhMsgMarkdownDirectly(
                user.getWxGzhOpenid(),
                templateId,
                data)
        );
    }

    private static void workWechatPush() {
        String markdown = "<font color=\"warning\">工艺单更新通知</font> \n" +
                "款号：<font color=\"info\">" + "test_111" + "</font> \n" +
                "款名称：<font color=\"info\">" + "工艺单测试-企业微信" + "</font> \n" +
                "产品季：<font color=\"info\">" + "" + "</font> \n" +
                "上传人：<font color=\"info\">" + "林新祺" + "</font> \n" +
                "上传时间：<font color=\"info\">" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "</font> \n" +
                "备注：<font color=\"info\">" + "请按最新工艺单组织排产，谢谢！" + "</font>";
        user.setWorkWechatAppkey(client.getWorkWechatAppkey());
        user.setWorkWechatAppsecret(client.getWorkWechatAppsecret());
        user.setTouser(user.getWorkWechatOpenid());
        System.out.println(QiYePushUtil.SendQyMsgMarkdownDirectly(user, WxConstants.SCM_AGENT_ID, markdown));
    }

    private static void dingTalkPush() {
        String title = "工艺单更新通知";
        JSONObject textJson = new JSONObject();
        textJson.put("msgtype", DingtalkConstants.MSG_TYPE_OA);
        JSONObject oaJson = new JSONObject();
        oaJson.put("message_url", "");
        JSONObject oaJson1 = new JSONObject();
        oaJson1.put("bgcolor", "FF0097FF");
        oaJson1.put("text", "");
        oaJson.put("head", oaJson1);
        JSONObject oaJson2 = new JSONObject();
        oaJson2.put("title", title);
        JSONObject oaJson3 = new JSONObject();
        oaJson3.put("key", "款号：");
        oaJson3.put("value", "test_231");
        JSONObject oaJson4 = new JSONObject();
        oaJson4.put("key", "款名称：");
        oaJson4.put("value", "工艺单测试-钉钉");
        JSONObject oaJson5 = new JSONObject();
        oaJson5.put("key", "产品季：");
        oaJson5.put("value", "");
        JSONObject oaJson6 = new JSONObject();
        oaJson6.put("key", "上传人：");
        oaJson6.put("value", "chenmb");
        JSONObject oaJson7 = new JSONObject();
        oaJson7.put("key", "上传时间：");
        oaJson7.put("value", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        JSONObject oaJson8 = new JSONObject();
        oaJson8.put("key", "备注：");
        oaJson8.put("value", "请按最新工艺单组织排产，谢谢！");
        List<JSONObject> list = new ArrayList<>();
        list.add(oaJson3);
        list.add(oaJson4);
        list.add(oaJson5);
        list.add(oaJson6);
        list.add(oaJson7);
        list.add(oaJson8);
        oaJson2.put("form", list);
        oaJson.put("body", oaJson2);
        textJson.put("oa", oaJson);
        user.setDingtalkAppkey(client.getDingtalkAppkey());
        user.setDingtalkAppsecret(client.getDingtalkAppsecret());
        user.setTouser(user.getDingtalkOpenid());
        System.out.println(DdPushUtil.SendDdMsgOADirectly(user, DingtalkConstants.SCM_AGENT_ID, textJson));
    }

    private static void initAppIdAndAppSecret() {
        client = new SysClient();
        client.setFeishuAppId("cli_a320f0beb7fa500b");
        client.setFeishuAppSecret("OkZhcBtaVzssQ1AYuuvtTdfIFJUQLUHC");
        client.setWorkWechatAppkey("ww3e6804c2405881e1");
        client.setWorkWechatAppsecret("9pgveoM02S86O3hqLtQGIJkimfEP0Rc6sWK_RDqNLX8");
        client.setDingtalkAppkey("dinge9rozhgtuishgt8i");
        client.setDingtalkAppsecret("PGYtV9Qh6KTg4HyIJFK-icPQ2KKq4Rys-wnj2CA6fzMdEK0jfyMOlOFIpLMfR-tn");
    }

}
