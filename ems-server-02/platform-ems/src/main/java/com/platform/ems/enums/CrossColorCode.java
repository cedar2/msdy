package com.platform.ems.enums;
/**
 * 串色串码对应的code
 *
 * @author yangqz
 */
public enum CrossColorCode {

    CROSS_SIZE("ST041","库存调整（串码）"),
    CROSS_COLOR("ST042","库存调整（串色）"),
    CROSS_COLOR_SIZE("ST043","库存调整（串色串码）"),
    CROSS_COLOR_SIZE_STYLE("ST044","库存调整（串款串色串码）");
    private final String code;
    private final String info;

    CrossColorCode(String code, String info) {
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
