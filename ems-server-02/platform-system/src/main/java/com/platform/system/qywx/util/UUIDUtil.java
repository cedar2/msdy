package com.platform.system.qywx.util;

import java.util.UUID;

/**
 * UUID工具类用于生成session
 * 8-4-4-4-12的32个字符
 * 生成通用唯一识别码
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 获取一个8位的UUID
     *
     * @return
     */
    public static String get8UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0];
    }

    /**
     * 获取一个16位的UUID
     *
     * @return
     */
    public static String get16UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2];
    }


    /**
     * 获取一个24位的UUID
     *
     * @return
     */
    public static String get24UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[4];
    }

    /**
     * 获取一个28位的UUID
     *
     * @return
     */
    public static String get28UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[4];
    }

    /**
     * 获取一个32位的UUID
     *
     * @return
     */
    public static String get32UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3] + idd[4];
    }
}
