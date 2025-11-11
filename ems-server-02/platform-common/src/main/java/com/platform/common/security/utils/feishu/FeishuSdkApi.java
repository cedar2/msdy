package com.platform.common.security.utils.feishu;

import com.lark.oapi.Client;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReq;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenReqBody;
import com.lark.oapi.service.authen.v1.model.CreateAccessTokenResp;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;

/**
 * 封装飞书的官方api，不涉及业务逻辑 <br>
 * feishu包下的类的关系: <br>
 * FeishuPushUtil --调用--> FeishuClientService --调用--> FeishuSdkApi
 *
 * @author Straw
 * @date 2023/1/4
 */
public class FeishuSdkApi {

    public static Client createClient(String appId, String appSecret) {
        return Client.newBuilder(appId, appSecret)
                     .openBaseUrl(BaseUrlEnum.FeiShu) // 设置域名，默认为飞书
                     .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers,body 等信息。
                     .build();
    }

    public static CreateAccessTokenReq createAccessTokenReq(String code) {
        return CreateAccessTokenReq
                .newBuilder()
                .createAccessTokenReqBody(CreateAccessTokenReqBody.newBuilder()
                                                                  .grantType("authorization_code")
                                                                  .code(code)
                                                                  .build())
                .build();
    }

    /**
     * 创建 CreateMessageReq
     *
     * @param receiveIdType 消息接收者的id的类型
     * @param receiveId     消息接收者的id
     * @param msgType       消息的类型
     * @param msgContent    消息的具体内容
     * @return CreateMessageReq 对象
     */
    public static CreateMessageReq createMessageReq(String receiveIdType,
                                                    String receiveId,
                                                    String msgType,
                                                    String msgContent) {
        return CreateMessageReq.newBuilder()
                               .receiveIdType(receiveIdType)
                               .createMessageReqBody(createMessageReqBody(receiveId, msgType, msgContent))
                               .build();
    }

    public static CreateMessageReqBody createMessageReqBody(String receiveId, String msgType, String msgContent) {
        return CreateMessageReqBody.newBuilder()
                                   .receiveId(receiveId)
                                   .msgType(msgType)
                                   .content(msgContent)
                                   .build();
    }

    public static CreateMessageResp getMessageResp(CreateMessageReq req, Client client) {
        try {
            return client.im()
                         .message()
                         .create(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static CreateAccessTokenResp getAccessTokenResp(CreateAccessTokenReq req, Client client) {
        try {
            return client.authen()
                         .accessToken()
                         .create(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
