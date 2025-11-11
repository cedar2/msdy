package com.platform.ems.constant;

/**
 * 订单合同常量信息
 *
 * @author chenkw
 */
public class ConstantsOrder {

    /**
     * 特殊用途(合同) 临时过渡
     */
    public static final String CONTRACT_PURPOSE_LSGD = "LSGD";

    /**
     * 销售订单合同(盖章版)
     */
    public static final String PAPER_CONTRACT_XSDDHT = "XSDDHT";

    /**
     * 销售意向订单合同(盖章版)
     */
    public static final String PAPER_CONTRACT_XSYXDDHT = "XSYXDDHT";

    /**
     * 采购订单合同(盖章版)
     */
    public static final String PAPER_CONTRACT_XSDDHT_PUR = "CGDDHT";

    /**
     * 合同号录入方式 选择
     */
    public static final String CONTRACT_ENTER_MODE_XZ = "XZ";

    /**
     * 合同号录入方式 手工
     */
    public static final String CONTRACT_ENTER_MODE_SG = "SG";

    /**
     * 交货类别 采购交货单
     */
    public static final String DELIVERY_NOTE_CG = "PD";

    /**
     * 交货类别 销售发货单
     */
    public static final String DELIVERY_NOTE_XS = "SD";

    /**
     * 采购模式 寄售
     */
    public static final String PURCHASE_SALE_MODE_JS = "JS";

    /**
     * 采购/销售模式 常规/买断
     */
    public static final String PURCHASE_SALE_MODE_CG = "CG";

    /**
     * 交货类型 按订单
     */
    public static final String DELIVERY_TYPE_DD = "DD";

    /**
     * 交货类型 按发货单
     */
    public static final String DELIVERY_TYPE_FHD = "FHD";

    /**
     * 订单的单据类型 采购订单(买断)
     */
    public static final String ORDER_DOC_TYPE_PO = "PO";

    /**
     * 订单的单据类型 销售订单(买断)
     */
    public static final String ORDER_DOC_TYPE_SO = "SO";

    /**
     * 订单的单据类型 销售退货单(买断)
     */
    public static final String ORDER_DOC_TYPE_RSO = "RSO";

    /**
     * 订单的单据类型 采购退货单(买断)
     */
    public static final String ORDER_DOC_TYPE_RPO = "RPO";

    /**
     * 订单的单据类型 销售订单(寄售/代销)
     */
    public static final String ORDER_DOC_TYPE_CSO = "CSO";

    /**
     * 订单的单据类型 销售退货单(寄售/代销)
     */
    public static final String ORDER_DOC_TYPE_RCSO = "RCSO";

    /**
     * 订单的单据类型 结算单(寄售/代销)
     */
    public static final String ORDER_DOC_TYPE_CSB = "CSB";

    /**
     * 订单的单据类型 备货意向单
     */
    public static final String ORDER_DOC_TYPE_BLTZD = "BLTZD";

    /**
     * 订单的单据类型 采购退货单(寄售/代销)
     */
    public static final String ORDER_DOC_TYPE_RCPO = "RCPO";

    /**
     * 采购交货单/销售发货单 单据类型 采购交货单
     */
    public static final String DEL_ORDER_DOC_TYPE_PDN = "PDN";

    /**
     * 采购交货单/销售发货单 单据类型 销售发货单
     */
    public static final String DEL_ORDER_DOC_TYPE_SDN = "SDN";

    /**
     * 采购交货单/销售发货单 单据类型 采购退货发货单
     */
    public static final String DEL_ORDER_DOC_TYPE_RPDN = "RPDN";

    /**
     * 采购交货单/销售发货单 单据类型 销售退货收货单
     */
    public static final String DEL_ORDER_DOC_TYPE_RSDN = "RSDN";

}
