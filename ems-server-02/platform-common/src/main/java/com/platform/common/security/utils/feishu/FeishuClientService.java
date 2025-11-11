package com.platform.common.security.utils.feishu;

import cn.hutool.core.map.MapUtil;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.BaseResponse;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReq;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenRespBody;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageRespBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.function.Function;

/**
 * feishu包下的类的关系: <br>
 * FeishuPushUtil --调用--> FeishuClientService --调用--> FeishuSdkApi
 *
 * @author Straw
 * @date 2023/1/4
 */
@Slf4j
public class FeishuClientService {

    Client feishuClient;

    public FeishuClientService(String appId, String appSecret) {
        this.feishuClient = FeishuSdkApi.createClient(appId, appSecret);
    }


    /*===============获取openId的方法===============*/

    @Nullable
    public String getOpenIdByCode(@Nullable String code) {
        // 检查入参
        if (code == null) {
            return null;
        }

        // 创建请求对象
        CreateAccessTokenReq req = FeishuSdkApi.createAccessTokenReq(code);
        // 使用飞书client发送请求
        CreateAccessTokenRespBody resp = useFeishuClient(
                client -> FeishuSdkApi.getAccessTokenResp(req, client),
                "获取用户openId"
        );
        // 处理响应
        return resp == null ? null : resp.getOpenId();
    }

    /*===============发送消息的方法===============*/
    /*
    关于构造发送消息内容，
    参考：https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/im-v1/message/create_json
     */

    /**
     * sendMessageAsTextByOpenId
     *
     * @param openId 要发给哪个用户，传那个用户openid
     * @param text   要发送的消息。例如，text="abc"，用户就会收到信息"abc"
     * @return 如果失败返回null，如果成功返回非空
     */
    @Nullable
    public CreateMessageRespBody sendMsgAsTextByOpenId(String openId, String text) {
        // 构造消息内容
        String msgContent = Jsons.DEFAULT.toJson(MapUtil.of("text", text));
        // 创建请求对象
        CreateMessageReq req = FeishuSdkApi.createMessageReq(
                "open_id", openId,
                "text", msgContent
        );
        // 发送请求，返回响应体
        return sendMessage(req);
    }

    @Nullable
    public CreateMessageRespBody sendMsgAsContentByOpenId(String openId, String content) {
        // 创建请求对象
        CreateMessageReq req = FeishuSdkApi.createMessageReq(
                "open_id", openId,
                "post", content
        );
        // 发送请求，返回响应体
        return sendMessage(req);
    }


    /*===============私有方法===============*/

    /**
     * 真正发送请求和处理响应
     *
     * @return 响应对象
     */
    @Nullable
    private CreateMessageRespBody sendMessage(CreateMessageReq req) {
        return useFeishuClient(client -> FeishuSdkApi.getMessageResp(req, client), "发送消息");
    }

    @Nullable
    private <T> T useFeishuClient(Function<Client, BaseResponse<T>> acceptClient,
                                  String message) {
        // 发起请求
        BaseResponse<T> resp;
        try {
            resp = acceptClient.apply(feishuClient);
        } catch (Exception e) {
            log.error("飞书【" + message + "】失败", e);
            return null;
        }

        // 处理服务端错误
        T data = resp.getData();
        if (!resp.success()) {
            log.error("飞书【" + message + "】失败: code=[{}], msg=[{}], reqId=[{}]",
                      resp.getCode(),
                      resp.getMsg(),
                      resp.getRequestId());
        }

        // 业务数据处理
        return data;
    }

}

