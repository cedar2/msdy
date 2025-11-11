package com.platform.common.security.utils.feishu;

import cn.hutool.core.util.StrUtil;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.im.v1.model.CreateMessageRespBody;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysUser;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * feishu包下的类的关系: <br>
 * FeishuPushUtil --调用--> FeishuClientService --调用--> FeishuSdkApi
 *
 * @author Straw
 * @date 2023/1/6
 */
@Slf4j
public class FeishuPushUtil {

    public static void main(String[] args) {
        push("ou_5baf346d10d58be3c197344a915e9e71",
             "cli_a320f0beb7fa500b",
             "OkZhcBtaVzssQ1AYuuvtTdfIFJUQLUHC",
             "标题-测试",
             "内容第一行-可以收到消息吗？",
             "内容第二行-可以收到消息吗？",
             "内容第三行-可以收到消息吗？"
        );
    }

    public static void push(SysUser user, SysClient client, String title, String... text) {
        push(user.feishuOpenId, client.getFeishuAppId(), client.getFeishuAppSecret(), title, text);
    }

    public static void push(String feishuOpenId, String appId, String appSecret, String title, String... text) {
        if (StrUtil.hasEmpty(feishuOpenId,
                             appId,
                             appSecret)) {
            log.error("无法推送飞书消息，存在空字段: {}, {}, {}",
                      feishuOpenId,
                      appId,
                      appSecret);
            return;
        }
        FeishuClientService service = new FeishuClientService(appId, appSecret);
        String message = buildMessage(feishuOpenId, title, text);
        CreateMessageRespBody resData = service.sendMsgAsContentByOpenId(feishuOpenId, message);

        // 判断是否发送成功
        if (resData != null) {
            // 发送成功了，直接返回
            return;
        }

        // 发送失败处理
        log.error("飞书消息推送失败");
    }

    private static String buildMessage(String feishuOpenId, String title, String... text) {
        MessageBuilder builder = new MessageBuilder();

        builder.buildTitle(title);
        for (String line : text) {
            builder.buildLine(line);
        }

        builder.buildAtSomebody(feishuOpenId);
        return builder.buildAsJson();
    }

    public static class MessageBuilder {
        Map<String, Object> messageMap;
        HashMap<Object, Object> zhCnMap;
        List<List<Map<String, String>>> contentMap;

        public MessageBuilder() {
            this.messageMap = new HashMap<>();
            this.zhCnMap = new HashMap<>();
            this.contentMap = new ArrayList<>();
            this.zhCnMap.put("content", contentMap);
            this.messageMap.put("zh_cn", zhCnMap);
        }

        private static Map<String, String> createMapDsl(String[] args) {
            Map<String, String> map = new HashMap<>();
            if (args.length % 2 != 0) {
                throw new RuntimeException(args.length + " % 2 != 0");
            }

            for (int i = 0; i < args.length; i += 2) {
                map.put(args[i], args[i + 1]);
            }
            return map;
        }

        public void buildLine(String text) {
            appendContent("tag", "text",
                          "text", text);
        }

        public void buildTitle(String title) {
            zhCnMap.put("title", title);
        }

        public void buildAtSomebody(String openId) {
            appendContent("tag", "at",
                          "user_id", openId,
                          "user_name", "");
        }

        private void appendContent(String... args) {
            Map<String, String> map = createMapDsl(args);
            contentMap.add(Collections.singletonList(map));
        }

        public String buildAsJson() {
            return Jsons.DEFAULT.toJson(messageMap);
        }
    }

}
