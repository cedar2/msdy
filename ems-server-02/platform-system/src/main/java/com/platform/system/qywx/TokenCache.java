package com.platform.system.qywx;

public class TokenCache {

    /**
     * token
     */
    private String token;

    /**
     * token有效期
     */
    private Integer period;

    /**
     * 获取时间（时间戳）
     */
    private Long obtainTime;

    private final int TIME = 1000;

    private final int RESERVED = 10;

    public TokenCache() {
    }

    public TokenCache(String token, Integer period) {
        this.token = token;
        this.period = period - RESERVED;
        this.obtainTime = System.currentTimeMillis() / TIME;
    }

    /**
     * 判断token是否过期
     *
     * @return
     */
    public boolean isOverdue() {
        if ((((System.currentTimeMillis() / TIME) - obtainTime) - period) < 0) {
            return true;
        }
        return false;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period - RESERVED;
    }

    public Long getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime() {
        this.obtainTime = System.currentTimeMillis() / TIME;
    }

    @Override
    public String toString() {
        return "TokenCache{" +
                "token='" + token + '\'' +
                ", period=" + period +
                ", obtainTime=" + obtainTime +
                '}';
    }
}
