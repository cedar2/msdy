package com.platform.ems.constant;

/**
 * ems工序表
 *
 * @author chenkw
 */
public class ConstantsProcess {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     *  单据类型_生产订单  按库生产
     */
    public static final String MAN_DOC_ANKU = "MTS";

    /**
     *  单据类型_生产订单  按单生产
     */
    public static final String MAN_DOC_ANDAN = "MTO";

    /**
     *  裁床
     */
    public static final String PROCESS_NAME_CC = "裁床";

    /**
     *  车缝
     */
    public static final String PROCESS_NAME_CF = "车缝";

    /**
     *  洗水
     */
    public static final String PROCESS_NAME_XS = "洗水";

    /**
     *  后整
     */
    public static final String PROCESS_NAME_HZ = "后整";

    /**
     *  印花
     */
    public static final String PROCESS_NAME_YH = "印花";

    /**
     *  印绣花
     */
    public static final String PROCESS_NAME_YXH = "印绣花";

    /**
     *  熨烫
     */
    public static final String PROCESS_NAME_YT = "熨烫";

    /**
     *  参考工序所引用数量类型 - 接收量
     */
    public static final String QUANTITY_TYPE_REFER_PROCESS_JS = "JS";

    /**
     *  参考工序所引用数量类型 - 完成量
     */
    public static final String QUANTITY_TYPE_REFER_PROCESS_WC = "WC";

    /**
     *  参考工序所引用数量类型 - 发料量
     */
    public static final String QUANTITY_TYPE_REFER_PROCESS_FL = "FL";

    /**
     *  录入方式 - 关联生产订单
     */
    public static final String JIXIN_ENTER_MODE_GLSC = "GLSC";

    /**
     *  录入方式 - 不关联生产订单
     */
    public static final String JIXIN_ENTER_MODE_BGLSC = "BGLSC";

    /**
     *  计件结算申报状态
     */
    public static final String JIXIN_REPORT_STATUS_WSB = "WSB";

    /**
     *  计件结算申报状态
     */
    public static final String JIXIN_REPORT_STATUS_SBZ = "SBZ";

    /**
     *  计件结算申报状态
     */
    public static final String JIXIN_REPORT_STATUS_YSB = "YSB";

    /**
     *  录入方式(生产进度日报) - 按工厂
     */
    public static final String DAY_MAN_PRO_GC = "GC";

    /**
     *  录入方式(生产进度日报) - 按班组
     */
    public static final String DAY_MAN_PRO_BZ = "BZ";

    /**
     *  录入方式(生产进度日报) - 按操作部门
     */
    public static final String DAY_MAN_PRO_CZBM = "CZBM";

    /**
     *  生产订单工序特殊工序标识 - 出货
     */
    public static final String MAN_PROCESS_SPECIAL_FLAG_CHUH = "CHUH";

    /**
     *  生产订单工序特殊工序标识 - 收洗水
     */
    public static final String MAN_PROCESS_SPECIAL_FLAG_SHOUXS = "SHOUXS";

    /**
     *  生产订单工序特殊工序标识 - 印绣花
     */
    public static final String MAN_PROCESS_SPECIAL_FLAG_YXH = "YXH";

    /**
     *  生产订单工序特殊工序标识 - 裁床
     */
    public static final String MAN_PROCESS_SPECIAL_FLAG_CAIC = "CAIC";
}
