package com.platform.ems.eSignApp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 签署流程配置项
 */
@Data
@Accessors(chain = true)
public class SignFlowConfig {

    /**
     *是必填
     *签署流程主题（将展示在签署通知和签署页的任务信息中）
     *【注】主题名称不可含有以下9个特殊字符：/ \ : * " < > | ？以及所有emoji表情
     */
    private String signFlowTitle;

    /**
     * 签署截止时间， unix时间戳（毫秒）格式，
     * 补充说明：
     * 默认在签署流程创建后的90天时截止（指定值最大不能超过90天，只能指定90天内的时间戳）。签署中如需延期请调用【延期签署截止时间】接口。
     */
    private long signFlowExpireTime;

    /**
     * 所有签署方签署完成后流程自动完结，默认值 false
     * true - 自动完结
     * false - 非自动完结，需调用【完结签署流程】接口完结
     * 补充说明：
     * 设置了自动完结的流程中不允许再追加签署区、抄送方，点击这里了解更多流程状态说明。
     */
    private boolean autoFinish;

    /**
     * 接收相关回调通知的Web地址，详见【签署回调通知接收说明】
     */
    private String notifyUrl;

    /**
     * 重定向配置项
     */
    private RedirectConfig redirectConfig;

    @Data
    @Accessors(chain = true)
    public static class RedirectConfig {
        /**
         * 签署完成后跳转页面（需符合 https /http 协议地址）
         */
        private String redirectUrl;
    }

}
