package com.platform.ems.enums;
/**
 * 单据类别
 *
 * @author yangqz
 */
public enum DocCategory {
    SALE_RU("SDN","销售发货"),
    SALE_CHK("PDN","采购交货"),
    ALLOCAT("ITN","调拨单"),
    RECIPIT("GRN","收货单"),
    SHIP("GIN","发货单"),
    SALE_ORDER("SO","销售订单"),
    JI_SHOU("CSB","寄售结算单"),
    JI_SHOU_RETURN("RCPO","寄售退货单"),
    PURCHASE_ORDER("PO","采购订单"),
    PRODUCTION_ORDER("MO","生产订单"),
    REQUESTION_RU("MRR","退料单"),
    REQUESTION_CHK("MR","领料单"),
    RETURN_BACK_SALE("RSO","销售退货订单"),
    RETURN_BACK_SALE_RECEPIT("RSDN","销售退货发货单"),
    RETURN_BACK_PURCHASE("RPO","采购退货订单"),
    RETURN_BACK_PURCHASE_R("RPDN","采购退货交货单"),
    SALE_RETURN("CSO","寄售订单"),
    SALE_JI_RETURN("RCSO","寄售销售退货单"),
    PURCHAASE_JI_SHOU("CPO","寄售订单"),
    INV_DOCUMENT("INVD","库存凭证"),
    PURCHAASE_JI_SHOU_RETURN("RCPO","寄售退货订单");




    private final String code;
    private final String info;

    DocCategory(String code, String info) {
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
