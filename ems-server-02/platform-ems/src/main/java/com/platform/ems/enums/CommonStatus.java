package com.platform.ems.enums;

/**
 * 状态
 *
 * @author platform
 */
public enum CommonStatus {
    DISABLE("0", "停用"),
    OK("1", "正常"),
    DELETED("2", "删除");

    private final String code;
    private final String info;

    CommonStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
