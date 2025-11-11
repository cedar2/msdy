package com.platform.ems.enums;
/**
 * 库存凭证类别
 *
 * @author yangqz
 */
public enum DocumentCategory {

    RECEIPT("GRN","收货"),
    ISSUE("GIN","发货"),
    MATERIAL_REQUISITION("MR","领料"),
    MATERIAL_BACK("MRR","退料"),
    ADJUST("ITN","调拨"),
    RU("RK","入库"),
    CHK("CK","出库"),
    YK("YK","移库"),
    CTKZ("TCZY","常特库存"),
    SHEET("IS","盘点");


    private final String code;
    private final String info;

    DocumentCategory(String code, String info) {
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
