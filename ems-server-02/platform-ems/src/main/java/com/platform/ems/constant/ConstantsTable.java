package com.platform.ems.constant;

/**
 * ems数据库表名常量信息
 *
 * @author chenkw
 */
public class ConstantsTable {

    /**
     * SKU档案-主表
     */
    public static final String TABLE_BAS_SKU = "s_bas_sku";

    /**
     * SKU组档案-主表
     */
    public static final String TABLE_BAS_SKU_GROUP = "s_bas_sku_group";

    /**
     * 供应商-主表
     */
    public static final String TABLE_BAS_VENDOR = "s_bas_vendor";

    /**
     * 采购报核议价-主表
     */
    public static final String TABLE_PUR_QUOTE_BARGAIN = "s_pur_quote_bargain";

    /**
     * 加工报核议价-主表
     */
    public static final String TABLE_PUR_OUT_QUOTE_BARGAIN = "s_pur_outsource_quote_bargain";

    /**
     * 申购单
     */
    public static final String TABLE_PURCHASE_REQUIRE = "s_req_purchase_require";

    /**
     * 采购合同
     */
    public static final String TABLE_PURCHASE_CONTRACT = "s_pur_purchase_contract";

    /**
     * 销售合同
     */
    public static final String TABLE_SALE_CONTRACT = "s_sal_sale_contract";

    /**
     * 销售订单
     */
    public static final String TABLE_SALE_ORDER = "s_sal_sales_order";

    /**
     * 采购订单
     */
    public static final String TABLE_PURCHASE_ORDER = "s_pur_purchase_order";

    /**
     * 供应商对账单
     */
    public static final String TABLE_VENDOR_MONTH_ACCOUNT_BILL ="s_pur_vendor_month_account_bill";

    /**
     * 客户对账单
     */
    public static final String TABLE_CUSTOMER_MONTH_ACCOUNT_BILL ="s_sal_customer_month_account_bill";

    /**
     * 生产订单
     */
    public static final String TABLE_MANUFACTURE_ORDER = "s_man_manufacture_order";

    /**
     * 生产订单商品明细
     */
    public static final String TABLE_MANUFACTURE_ORDER_PRODUCT = "s_man_manufacture_order_product";

    /**
     * 生产订单工序
     */
    public static final String TABLE_MANUFACTURE_ORDER_PROCESS = "s_man_manufacture_order_process";

    /**
     * 生产订单事项
     */
    public static final String TABLE_MANUFACTURE_ORDER_CONCERN_TASK = "s_man_manufacture_order_concern_task";

    /**
     * 班组日出勤
     */
    public static final String TABLE_PAY_TEAM_WORKATTEND_DAY = "s_pay_team_workattend_day";

    /**
     * 商品道序主表
     */
    public static final String TABLE_PRODUCT_PROCESS_STEP = "s_pay_product_process_step";

    /**
     * 商品道序明细表
     */
    public static final String TABLE_PRODUCT_PROCESS_STEP_ITEM = "s_pay_product_process_step_item";

    /**
     * 计薪量申报单
     */
    public static final String TABLE_PROCESS_STEP_COMPLETE = "s_pay_process_step_complete";

    /**
     * 库存凭证表
     */
    public static final String TABLE_INV_INVENTORY_DOCUMENT = "s_inv_inventory_document";

    /**
     * 商品道序完成量台账
     */
    public static final String TABLE_MAN_PROCESS_STEP_COMPELETE_RECORD = "s_man_process_step_complete_record";

    /**
     * 商品道序完成量台账 明细
     */
    public static final String TABLE_MAN_PROCESS_STEP_COMPELETE_RECORD_ITEM = "s_man_process_step_complete_record_item";

    /**
     * 销售意向单
     */
    public static final String TABLE_SAL_SALES_INTENT_ORDER = "s_sal_sales_intent_order";

    /**
     * 销售意向单明细表
     */
    public static final String TABLE_SAL_SALES_INTENT_ORDER_ITEM = "s_sal_sales_intent_order_item";

    /**
     * 外发加工费结算单
     */
    public static final String TABLE_MANUFACTURE_OUTSOURCE_SETTLE = "s_man_manufacture_outsource_settle";

    /**
     * 客户押金
     */
    public static final String TABLE_FIN_CUSTOEMR_CASH_PLEDGE_BILL = "s_fin_customer_cash_pledge_bill";

    /**
     * 供应商押金
     */
    public static final String TABLE_FIN_VENDOR_CASH_PLEDGE_BILL = "s_fin_vendor_cash_pledge_bill";

    /**
     * 客户暂押款
     */
    public static final String TABLE_FIN_CUSTOMER_FUNDS_FREEZE_BILL = "s_fin_customer_funds_freeze_bill";

    /**
     * 供应商暂押款
     */
    public static final String TABLE_FIN_VENDOR_FUNDS_FREEZE_BILL = "s_fin_vendor_funds_freeze_bill";

    /**
     * 客户调账单
     */
    public static final String TABLE_FIN_CUSTOMER_ACCOUNT_ADJUST_BILL = "s_fin_customer_account_adjust_bill";

    /**
     * 供应商调账单
     */
    public static final String TABLE_FIN_VENDOR_ACCOUNT_ADJUST_BILL = "s_fin_vendor_account_adjust_bill";

    /**
     * 劳动合同
     */
    public static final String TABLE_HR_LABOR_CONTRACT = "s_hr_labor_contract";


    /**
     * 劳动合同
     */
    public static final String TABLE_BAS_STAFF = "s_bas_staff";

    /**
     * 应付暂估调价量单对象
     */
    public static final String TABLE_FIN_PAY_EST_ADJ_BILL = "s_fin_payment_estimation_adjust_bill";

    /**
     * 应收暂估调价量单对象
     */
    public static final String TABLE_FIN_REC_EST_ADJ_BILL = "s_fin_receipt_estimation_adjust_bill";

    /**
     * 付款对象
     */
    public static final String TABLE_FIN_PAY_BILL = "s_fin_pay_bill";

    /**
     * 收款对象
     */
    public static final String TABLE_FIN_RECEIVABLE_BILL = "s_fin_receivable_bill";

    /**
     * PDM-图案档案表
     */
    public static final String TABLE_BAS_IMAGE = "s_bas_image";

    /**
     * PDM-任务节点
     */
    public static final String TABLE_PRJ_TASK = "s_prj_task";

    /**
     * 客户扣款
     */
    public static final String TABLE_FIN_CUSTOEMR_DEDUCTION_BILL = "s_fin_customer_deduction_bill";

    /**
     * 供应商扣款
     */
    public static final String TABLE_FIN_VENDOR_DEDUCTION_BILL = "s_fin_vendor_deduction_bill";

    /**
     * 客户发票台账
     */
    public static final String TABLE_FIN_CUSTOMER_INVOICE_RECORD = "s_fin_customer_invoice_record";

    /**
     * 供应商发票台账
     */
    public static final String TABLE_FIN_VENDOR_INVOICE_RECORD = "s_fin_vendor_invoice_record";

    /**
     * PDM-项目任务模板对象
     */
    public static final String TABLE_PRJ_TASK_TEMPLATE = "s_prj_task_template";

    /**
     * PDM-项目任务模板对象明细
     */
    public static final String TABLE_PRJ_TASK_TEMPLATE_ITEM = "s_prj_task_template_item";

    /**
     * PDM-开发计划
     */
    public static final String TABLE_DEV_DEVELOP_PLAN = "s_dev_develop_plan";

    /**
     * PDM-市场调研
     */
    public static final String TABLE_DEV_MARKET_SURVEY = "s_dev_market_survey";

    /**
     * PDM-项目档案
     */
    public static final String TABLE_PRJ_PROJECT = "s_prj_project";

    /**
     * PDM-项目档案任务明细
     */
    public static final String TABLE_PRJ_PROJECT_TASK = "s_prj_project_task";

    /**
     * PDM-品类规划
     */
    public static final String TABLE_DEV_CATEGORY_PLAN = "s_dev_category_plan";

    /**
     * PDM-品类规划明细
     */
    public static final String TABLE_DEV_CATEGORY_PLAN_ITEM = "s_dev_category_plan_item";

    /**
     * PDM-图稿绘制
     */
    public static final String TABLE_FRM_DRAFT_DESIGN = "s_frm_draft_design";

    /**
     * PDM-样品评审单表
     */
    public static final String TABLE_FRM_SAMPLE_REVIEW = "s_frm_sample_review";

    /**
     * PDM-拍照样获取单表
     */
    public static final String TABLE_FRM_PHOTO_SAMPLE_GAIN = "s_frm_photo_sample_gain";

    /**
     * PDM-文案视觉单表
     */
    public static final String TABLE_FRM_DOCUMENT_VISION = "s_frm_document_vision";

    /**
     * PDM-到货通知单表
     */
    public static final String TABLE_FRM_ARRIVAL_NOTICE = "s_frm_arrival_notice";

    /**
     * PDM-新品试销计划单表
     */
    public static final String TABLE_FRM_NEWPRODUCT_TRIALSALE_PLAN = "s_frm_newproduct_trialsale_plan";

    /**
     * PDM-新品试销计划单-关键词分析表
     */
    public static final String TABLE_FRM_TRIALSALE_PLAN_KEY_WROD = "s_frm_trialsale_plan_key_word";

    /**
     * PDM-新品试销计划单-类目分析表
     */
    public static final String TABLE_FRM_TRIALSALE_PLAN_CATE_ANALYSIS = "s_frm_trialsale_plan_category_analysis";

    /**
     * PDM-新品试销结果单
     */
    public static final String TABLE_FRM_TRIALSALE_RESULT = "s_frm_trialsale_result";

    public static final String TABLE_S_BAS_GOODS_SHELF = "s_bas_goods_shelf";

    /**
     * PDM-事项清单
     */
    public static final String TABLE_PRJ_MATTER_LIST = "s_prj_matter_list";

}
