package com.platform.ems.constant;

/**
 * ems库存常量
 *
 * @author chenkw
 */
public class ConstantsInventory {
    /**
     * WU 无
     */
    public static final String WU = "WU";
    /**
     * 出入库 出库
     */
    public static final String DOCUMENT_CATEGORY_OUT = "CK";
    /**
     * 出入库 、入库
     */
    public static final String DOCUMENT_CATEGORY_IN = "RK";
    /**
     * 出入库 移库
     */
    public static final String DOCUMENT_CATEGORY_YK = "YK";
    /**
     * 出入库 特常库存转移
     */
    public static final String DOCUMENT_CATEGORY_TCZY = "TCZY";
    /**
     * 出入库 调拨
     */
    public static final String DOCUMENT_CATEGORY_DB = "ITN";
    /**
     * 出入库 库存调整
     */
    public static final String DOCUMENT_CATEGORY_KCTZ = "IAN";
    /**
     * 出入库 领料
     */
    public static final String DOCUMENT_CATEGORY_LL = "MR";
    /**
     * 出入库 退料
     */
    public static final String DOCUMENT_CATEGORY_TL = "MRR";
    /**
     * 出入库 收货
     */
    public static final String DOCUMENT_CATEGORY_SH = "GRN";
    /**
     * 出入库 发货
     */
    public static final String DOCUMENT_CATEGORY_FH = "GIN";
    /**
     * 出入库 盘点
     */
    public static final String DOCUMENT_CATEGORY_PD = "IS";
    /**
     * 出入库 串色串码
     */
    public static final String DOCUMENT_CATEGORY_CSCM = "CSCM";
    /**
     * 出入库 甲供料结算
     */
    public static final String DOCUMENT_CATEGORY_JGLJS = "RMM";
    /**
     * 其它出库-常规/自采物料
     */
    public static final String MOVEMENT_TYPE_CODE_SC30 = "SC30";
    /**
     * 其它入库-常规/自采
     */
    public static final String MOVEMENT_TYPE_CODE_SR30 = "SR30";
    /**
     * 作业类型 按采购订单
     */
    public static final String MOVEMENT_TYPE_SR01 = "SR01";
    /**
     * 作业类型 采购交货单
     */
    public static final String MOVEMENT_TYPE_SR02 = "SR02";
    /**
     * 作业类型 按销售退货订单
     */
    public static final String MOVEMENT_TYPE_SR03 = "SR03";
    /**
     * 作业类型
     */
    public static final String MOVEMENT_TYPE_SR04 = "SR04";
    /**
     * 作业类型 按生产订单
     */
    public static final String MOVEMENT_TYPE_SR05 = "SR05";
    /**
     * 作业类型 免费采购-常规/自采
     */
    public static final String MOVEMENT_TYPE_SR21 = "SR21";
    /**
     * 作业类型 生产入库-无生产订单
     */
    public static final String MOVEMENT_TYPE_SR23 = "SR23";
    /**
     * 作业类型 调拨-常规/自采物料
     */
    public static final String MOVEMENT_TYPE_SR08 = "SR08";
    /**
     * 作业类型 调拨-甲供料
     */
    public static final String MOVEMENT_TYPE_SR081 = "SR081";
    /**
     * 作业类型 调拨-客供料
     */
    public static final String MOVEMENT_TYPE_SR082 = "SR082";
    /**
     * 作业类型 调拨-供应商寄售
     */
    public static final String MOVEMENT_TYPE_SR083 = "SR083";
    /**
     * 作业类型 调拨-客户寄售
     */
    public static final String MOVEMENT_TYPE_SR084 = "SR084";
    /**
     * 作业类型 按销售订单
     */
    public static final String MOVEMENT_TYPE_SC01 = "SC01";
    /**
     * 作业类型 按销售发货单
     */
    public static final String MOVEMENT_TYPE_SC02 = "SC02";
    /**
     * 作业类型 按采购退货订单
     */
    public static final String MOVEMENT_TYPE_SC03 = "SC03";
    /**
     * 作业类型 按采购退货发货单
     */
    public static final String MOVEMENT_TYPE_SC04 = "SC04";
    /**
     * 作业类型 免费销售-常规/自采
     */
    public static final String MOVEMENT_TYPE_SC21 = "SC21";
    /**
     * 作业类型 调拨-常规/自采物料
     */
    public static final String MOVEMENT_TYPE_SC08 = "SC08";
    /**
     * 作业类型 调拨-甲供料
     */
    public static final String MOVEMENT_TYPE_SC081 = "SC081";
    /**
     * 作业类型 调拨-客供料
     */
    public static final String MOVEMENT_TYPE_SC082 = "SC082";
    /**
     * 作业类型 调拨-供应商寄售
     */
    public static final String MOVEMENT_TYPE_SC083 = "SC083";
    /**
     * 作业类型 调拨-客户寄售
     */
    public static final String MOVEMENT_TYPE_SC084 = "SC084";
    /**
     * 作业类型 调拨-常规/自采(一步)
     */
    public static final String MOVEMENT_TYPE_SC085   = "SC085";
    /**
     * 作业类型 调拨-客供料(一步)
     */
    public static final String MOVEMENT_TYPE_SC086 = "SC086";
    /**
     * 作业类型 领料-常规/自采物料
     */
    public static final String MOVEMENT_TYPE_SC07 = "SC07";
    /**
     * 作业类型 领料-客供料
     */
    public static final String MOVEMENT_TYPE_SC071   = "SC071";
    /**
     * 作业类型 领料-供应商寄售
     */
    public static final String MOVEMENT_TYPE_SC072 = "SC072";
    /**
     * 业务标识 采购
     */
    public static final String BUSINESS_FLAG_CG = "CG";
    /**
     * 业务标识 采购退货
     */
    public static final String BUSINESS_FLAG_CGTH = "CGTH";
    /**
     * 业务标识 销售
     */
    public static final String BUSINESS_FLAG_XS = "XS";
    /**
     * 业务标识 销售退货
     */
    public static final String BUSINESS_FLAG_XSTH = "XSTH";
    /**
     * 业务标识 生产领料
     */
    public static final String BUSINESS_FLAG_SCLL = "SCLL";
    /**
     * 业务标识 生产退料
     */
    public static final String BUSINESS_FLAG_SCTL = "SCTL";
    /**
     * 移库方式 一步法（出库即入库）
     */
    public static final String STOCK_TRANSFER_MODE_LB = "LB";
    /**
     * 移库方式 两步法（出库再入库）
     */
    public static final String STOCK_TRANSFER_MODE_YB = "YB";
    /**
     * 出入库状态  未入库
     */
    public static final String IN_OUT_STORE_STATUS_WRK = "WRK";
    /**
     * 出入库状态  部分入库
     */
    public static final String IN_OUT_STORE_STATUS_BFRK = "BFRK";
    /**
     * 出入库状态  全部入库
     */
    public static final String IN_OUT_STORE_STATUS_QBRK = "QBRK";
    /**
     * 出入库状态  未出库
     */
    public static final String IN_OUT_STORE_STATUS_WCK = "WCK";
    /**
     * 出入库状态  部分出库
     */
    public static final String IN_OUT_STORE_STATUS_BFCK = "BFCK";
    /**
     * 出入库状态  全部出库
     */
    public static final String IN_OUT_STORE_STATUS_QBCK = "QBCK";
    /**
     * 使用频率标识  常用
     */
    public static final String USAGE_FREQUENCY_FLAG_CY = "CY";
    /**
     * 特殊库存  甲供料
     */
    public static final String SPECIAL_STOCK_JGL = "JGL";
    /**
     * 特殊库存  供应商寄售
     */
    public static final String SPECIAL_STOCK_GJS = "GJS";
    /**
     * 特殊库存  客供料
     */
    public static final String SPECIAL_STOCK_KGL = "KGL";
    /**
     * 特殊库存  客户寄售
     */
    public static final String SPECIAL_STOCK_KJS = "KJS";

    /**
     * 业务单据类别 调拨单
     */
    public static final String REFER_DOC_CAT_DBD = "ITN";

    /**
     * 业务单据类别 无
     */
    public static final String REFER_DOC_CAT_WU = "WU";

    /**
     * 业务单据类别 采购订单
     */
    public static final String REFER_DOC_CAT_PO = "PO";

    /**
     * 业务单据类别 销售订单
     */
    public static final String REFER_DOC_CAT_SO = "SO";

    /**
     * 库存管理方式 更新库存
     */
    public static final String INV_CONTROL_MODE_GX = "GX";

    /**
     * 库存管理方式 不更新库存
     */
    public static final String INV_CONTROL_MODE_BGX = "BGX";

    /**
     * 业务类型 销售发货单/采购交货单 实物仓出库
     */
    public static final String BU_TYPE_DEL_NOTE_SWC = "SWC";

    /**
     * 业务类型 销售发货单/采购交货单 生产直发出库
     */
    public static final String BU_TYPE_DEL_NOTE_SCZF = "SCZF";

    /**
     * 业务类型 销售发货单/采购交货单 未定义
     */
    public static final String BU_TYPE_DEL_NOTE_WDY = "未定义";

}
