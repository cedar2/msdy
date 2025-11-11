package com.platform.system.qywx.config;

public class JWTConfig {

    /**
     * 密钥
     */
    private static final String key = "62X+mS1m)!PNJ6nquH$";

    //  用户
    private static final String userCode = "aud";

    //  到期时间
    private static final String exp = "exp";

    //  发布时间
    private static final String iat = "iat";

    //  ID用于标识该JWT
    private static final String jti = "jti";

    private static final String userToken = "mobile:user:";

    private static final String tokenKey = "mobile:token:key:";

    private static final String orchardistToken = "mobile:orchardistToken:";

    private static final String aut = "Authorization";

    /**
     * 过期时间
     */
    private static final Long expireTime = 5 * 24 * 60 * 60L;

    public static String getKey() {
        return key;
    }

    public static Long getExpireTime() {
        return expireTime;
    }

    public static String getUserCode() {
        return userCode;
    }

    public static String getUserToken() {
        return userToken;
    }

    public static String getAut() {
        return aut;
    }

    public static String getExp() {
        return exp;
    }

    public static String getIat() {
        return iat;
    }

    public static String getJti() {
        return jti;
    }

    public static String getTokenKey() {
        return tokenKey;
    }

    public static String getOrchardistToken() {
        return orchardistToken;
    }
}
