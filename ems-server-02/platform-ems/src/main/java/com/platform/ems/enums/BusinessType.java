package com.platform.ems.enums;

/**
 * @description:
 * @author: Hu JJ
 * @date: 2021-04-07
 */
public enum BusinessType {

    CATEGORY_MATERIAL("1", "物料"),
    CATEGORY_COMMODITY("2", "商品"),
    CATEGORY_SERVICE("3", "服务"),
    MATERIAL("1","物料/商品/服务"),
    MATERIALCERTIFICATE("2","合格证洗唛信息"),
    BOM("3", "BOM清单"),
    PRODUCT_PROCESS_STEP("4", "商品道序"),
    DELIVERY("PD","交货"),
    SHIPMENTS("SD","发货");

    private final String code;
    private final String info;

    BusinessType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }}
