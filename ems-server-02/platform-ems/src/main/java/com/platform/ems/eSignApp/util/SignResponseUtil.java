package com.platform.ems.eSignApp.util;

import com.google.gson.JsonObject;
import com.platform.ems.eSignApp.constant.SignConstants;

/**
 * @author chenkw
 * @since 2024-02-21
 */
public class SignResponseUtil {

    /**
     * code 和 message 必须要有值
     */
    public static boolean codeAndMessage(JsonObject jsonObject) {
        if (jsonObject.get(SignConstants.CODE) == null || jsonObject.get(SignConstants.MESSAGE) == null) {
            return false;
        }
        return true;
    }

    /**
     * errCode 和 msg 必须要有值
     */
    public static boolean errCodeAndMsg(JsonObject jsonObject) {
        if (jsonObject.get(SignConstants.ERRCODE) == null || jsonObject.get(SignConstants.MSG) == null) {
            return false;
        }
        return true;
    }

}
