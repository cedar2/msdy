package com.platform.ems.eSignApp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 个人签署方信息
 */
@Data
@Accessors(chain = true)
public class SignerPsnInfo {

    /**
     * 必填
     * 个人账号标识（手机号或邮箱）用于登录e签宝官网的凭证
     * 【注】个人用户签署时，该参数为必传项。为了保证签署人准确，建议配合姓名信息传入。
     */
    private String psnAccount;

    /**
     * 个人签署方身份信息  非必填
     */
    private PsnInfo psnInfo;

    /**
     * 个人签署方身份信息
     * 补充说明：
     * 已实名用户，若传入的psnInfo与在e签宝绑定的psnAccount一致，则无需重复实名，签署页直接进行签署意愿认证；
     * 已实名用户，若传入的psnInfo与在e签宝绑定的psnAccount不一致，则接口将会报错，建议核实用户身份信息后重新发起流程；
     * 未实名用户，签署页将根据传入的身份信息进行用户实名认证。
     */
    @Data
    @Accessors(chain = true)
    public static class PsnInfo {

        /**
         * 个人姓名（强烈建议传入签署人姓名） 非必填
         */
        private String psnName;

        /**
         * 个人证件号 非必填
         */
        private String psnIDCardNum;

        /**
         * 个人证件类型，可选值如下： 非必填
         * CRED_PSN_CH_IDCARD - 中国大陆居民身份证（默认值）
         * CRED_PSN_CH_HONGKONG - 香港来往大陆通行证（回乡证）
         * CRED_PSN_CH_MACAO - 澳门来往大陆通行证（回乡证）
         * CRED_PSN_CH_TWCARD - 台湾来往大陆通行证（台胞证）
         * CRED_PSN_PASSPORT - 护照
         */
        private String psnIDCardType;

    }

}
