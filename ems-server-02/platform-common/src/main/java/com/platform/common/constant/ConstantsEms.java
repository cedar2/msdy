package com.platform.common.constant;

/**
 * ems通用常量信息
 *
 * @author platform
 */
public class ConstantsEms {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";
    /**
     * 租户ID-10000
     */
    public static final String CLIENT_ID_10000 = "10000";
    /**
     * 租户ID-10001
     */
    public static final String CLIENT_ID_10001 = "10001";
    /**
     * 系统状态-正常
     */
    public static final String SYS_COMMON_STATUS_Y = "0";
    /**
     * 系统状态-停用
     */
    public static final String SYS_COMMON_STATUS_N = "1";
    /**
     * 租户类型-工厂
     */
    public static final String CLIENT_TYPE_GC = "GC";
    /**
     * 租户类型-品牌/贸易
     */
    public static final String CLIENT_TYPE_PPMY = "PPMY";
    /**
     * 租户类型-工贸一体
     */
    public static final String CLIENT_TYPE_GMYT = "GMYT";
    /**
     * 数据填写要求  必填
     */
    public static final String DATA_ENTER_REQUEST_BT = "BT";
    /**
     * 数据填写要求  提示
     */
    public static final String DATA_ENTER_REQUEST_TS = "TS";
    /**
     * 数据填写要求  无
     */
    public static final String DATA_ENTER_REQUEST_WU = "WU";
    /**e
     * 消息显示类型  报错 / 提示  报错
     */
    public static final String S_MESSAGE_DISPLAT_TYPE_BC = "E";

    /**
     * 消息显示类型  报错 / 提示  提示
     */
    public static final String S_MESSAGE_DISPLAT_TYPE_TS = "M";

    /**
     * 消息显示类型  报错 / 提示  无
     */
    public static final String S_MESSAGE_DISPLAT_TYPE_WU = "W";
    /**
     * 财务报表看板类型：资金
     */
    public static final String STATISTIC_TYPE_ZJ = "ZJ";
    /**
     * 财务报表看板类型：资产
     */
    public static final String STATISTIC_TYPE_ZC = "ZC";
    /**
     * 财务报表看板类型：应收
     */
    public static final String STATISTIC_TYPE_YS = "YS";
    /**
     * 财务报表看板类型：应付
     */
    public static final String STATISTIC_TYPE_YF = "YF";
    /**
     * 用户账号类型-员工
     */
    public static final String USER_ACCOUNT_TYPE_YG = "YG";
    /**
     * 用户账号类型-供应商
     */
    public static final String USER_ACCOUNT_TYPE_GYS = "GYS";
    /**
     * 用户账号类型-客户
     */
    public static final String USER_ACCOUNT_TYPE_KH = "KH";
    /**
     * 编码方式-自动
     */
    public static final String CODE_MODE_ZD = "ZD";

    /**
     * 返回值成功
     */
    public static final String CODE_SUCESS= "200";

    /**
     * 编码方式-手动
     */
    public static final String CODE_MODE_SG = "SG";

    /**
     * 编码维度-数据对象
     */
    public static final String CODE_STANDARD_SJDX = "SJDX";
    /**
     * 编码维度-业务类别
     */
    public static final String CODE_STANDARD_YWLB = "YWLB";
    /**
     * 数据对象类别-采购合同
     */
    public static final String DATA_OBJECT_CONTRACT_S = "SaleContract";
    /**
     * 数据对象类别-采购合同
     */
    public static final String DATA_OBJECT_CONTRACT_P = "PurchaseContract";
    /**
     * 数据对象类别-物料/商品/服务
     */
    public static final String DATA_OBJECT_MATERIAL = "Material";
    /**
     * 员工在职状态
     */
    public static final String IS_ON_JOB_ZZ = "ZZ";
    /**
     * 供应商银行账户-基本户
     */
    public static final String BANK_ACCOUNT_TYPE_JBH = "JBH";
    /**
     * 供应商银行账户-一般户
     */
    public static final String BANK_ACCOUNT_TYPE_YBH = "YBH";
    /**
     *正负/增减标识-正
     */
    public static final String PLUS_FLAG = "ZS";
    /**
     *正负/增减标识-负
     */
    public static final String MINUS_FLAG = "FS";
    /**
     * 收付款方式组合 付款
     */
    public static final String SHOUFUKUAN_TYPE_FK = "FK";
    /**
     * 收付款方式组合 收款
     */
    public static final String SHOUFUKUAN_TYPE_SK = "SK";
    /**
     * 合同特殊用途-临时过渡
     */
    public static final String CONTRACT_PURPOSE_LS = "LSGD";
    /**
     * 附件类型 工艺单
     */
    public static final String FILE_TYPE_SPEC = "GYD";
    /**
     * 附件类型 采购合同电子版
     */
    public static final String FILE_TYPE_CGHT = "CGHT";
    /**
     * 附件类型 销售合同电子版
     */
    public static final String FILE_TYPE_XSHT = "XSHT";
    /**
     * 附件类型 销售合同电子版(待签署)
     */
    public static final String FILE_TYPE_XSHT_DQS = "XSHTDQS";
    /**
     * 库存可用类型 可用
     */
    public static final String USABLE_TYPE_KY = "KY";
    /**
     * 附件类型 版型尺寸表
     */
    public static final String FILE_TYPE_BXCCB = "BXCCB";
    /**
     * 通知类型 工艺单
     */
    public static final String BCST_TYPE_SPEC = "GYDSC";

    /**
     * 通知类型 工艺单未上传提醒
     */
    public static final String BCST_TYPE_SPECWSC = "GYDWSCTX";

    /**
     * 通知数据对象类别 物料商品服务
     */
    public static final String BCST_OBJECT_MATERIAL = "Material";

    /**
     * 审核状态 确认
     */
    public static final String CHECK_STATUS = "5";

    /**
     * 审核状态 变更审批中
     */
    public static final String CHANGE_STATUS = "2";

    /**
     * 审核状态 保存
     */
    public static final String SUBMIT_STATUS = "3";

    /**
     * 审核状态 保存
     */
    public static final String SAVA_STATUS = "1";

    /**
     * 审核状态 作废
     */
    public static final String INVALID_STATUS = "8";

    /**
     * 审核状态 已退回
     */
    public static final String BACK_STATUS = "4";

    /**
     * 状态 启用
     */
    public static final String ENABLE_STATUS = "1";

    /**
     * 状态 停用
     */
    public static final String DISENABLE_STATUS = "2";

    /**
     * 是
     */
    public static final String YES = "Y";
    /**
     * 否
     */
    public static final String NO = "N";

    /**
     * 节点类型 公司
     */
    public static final String NODE_TYPE_GS = "GS";

    /**
     * 节点类型 部门
     */
    public static final String NODE_TYPE_BM = "BM";

    /**
     * 节点类型 员工
     */
    public static final String NODE_TYPE_YG = "YG";

    /**
     * 商品工价类型 大货
     */
    public static final String PRODUCT_PRICE_TYPE_DH = "DH";

    /**
     * 价格录入方式 含税
     */
    public static final String ENTER_MODEL_TAX = "HS";
    /**
     * 价格录入方式 不含税
     */
    public static final String ENTER_MODEL_NO_TAX = "BHS";

    /**
     * 价格录入方式 出库
     */
    public static final String CHU_KU = "1";

    /**
     * 价格录入方式 入库
     */
    public static final String RU_KU = "2";
    /**
     * 特殊库存 甲供料库存（提供给供应商委外加工物料）
     */
    public static final String VEN_RA = "JGL";

    /**
     * 特殊库存 供应商寄售库存
     */
    public static final String VEN_CU = "GJS";

    /**
     * 特殊库存 客供料库存（客户提供的委外加工物料）
     */
    public static final String CUS_RA = "KGL";

    /**
     * 特殊库存 客户寄售库存
     */
    public static final String CUS_VE = "KJS";

    /**
     * 物料商品样品 库存价核算方式 - 加权平均价
     */
    public static final String INVENTORY_PRICE_METHOD_JQPJJ = "JQPJJ";

    /**
     * 物料商品样品 库存价核算方式 - 固定价
     */
    public static final String INVENTORY_PRICE_METHOD_GDJ = "GDJ";

    /**
     * 物料类别 物料
     */
    public static final String MATERIAL_CATEGORY_WL = "WL";

    /**
     * 物料类别 商品
     */
    public static final String MATERIAL_CATEGORY_SP = "SP";

    /**
     * 物料类别 服务
     */
    public static final String MATERIAL_CATEGORY_FW = "FW";

    /**
     * 物料类别 外采样
     */
    public static final String MATERIAL_CATEGORY_WCY = "WCY";

    /**
     * 物料类别 样品
     */
    public static final String MATERIAL_CATEGORY_YP = "YP";
    /**
     * 出库（调拨单）
     */
    public static final String CHU_KU_TR = "SC08";
    /**
     * 入库（调拨单）
     */
    public static final String RU_KU_TR = "SR08";
    /**
     * 移库
     */
    public static final String TRANSFER = "SY01";

    /**
     * 盘点
     */
    public static final String PROFIT = "SP01";

    /**
     * 盘亏
     */
    public static final String OVERLINK = "SP02";

    /**
     * 盈亏持平
     */
    public static final String COMMON = "SP03";
    /**
     * 采购订单入库
     */
    public static final String PURCHASE_ORDER_CHK = "SR01";
    /**
     * 销售退货订单
     */
    public static final String SALE_ORDER_RU_R = "SR03";
    /**
     * 销售退货发货单
     */
    public static final String SALE_ORDER_RU_L = "SR04";
    /**
     * 销售发货单
     */
    public static final String SALE_ORDER_RU = "SC02";

    /**
     * 销售发货单-直发
     */
    public static final String SALE_ORDER_RU_DIRECT= "SC021";
    /**
     * 销售订单
     */
    public static final String SALE_ORDER = "SC01";
    /**
     * 销售订单
     */
    public static final String TODO_SALE_ORDER = "销售订单";
    /**
     * 物料销售订单详情
     */
    public static final String TODO_SALE_ORDER_MENU_WL = "物料销售订单详情";
    /**
     * 商品销售订单详情
     */
    public static final String TODO_SALE_ORDER_MENU_SP = "商品销售订单详情";
    /**
     * 销售合同
     */
    public static final String  TODO_SALE_CONTRACT= "销售合同";
    /**
     * 销售合同
     */
    public static final String  TODO_SALE_CONTRACT_MENU= "销售合同详情";
    /**
     * 销售价
     */
    public static final String TODO_SALE_PRICE = "销售价";
    /**
     * 销售价-详情
     */
    public static final String TODO_SALE_PRICE_MENU = "销售价详情";
    /**
     * 商品道序详情
     */
    public static final String TODO_PRO_STEP_INFO_MENU_NAME = "商品道序详情";
    /**
     * 商品道序变更详情
     */
    public static final String TODO_UP_PRO_STEP_INFO_MENU_NAME = "商品道序变更详情";
    /**
     * 查询类型-销售
     */
    public static final String ORDER_TYPE_sale = "1";
    /**
     * 采购退货订单
     */
    public static final String PURCHASE_ORDER_RU_L = "SC03";
    /**
     * 采购退货交货单
     */
    public static final String PURCHASE_ORDER_RU_R = "SC04";
    /**
     * 采购交货单
     */
    public static final String PURCHASE_ORDER_RU_P = "SR02";

    /**
     * 期初库存
     */
    public static final String STARTLOCATION = "SR99";
    /**
     * 领料单-供应商寄售(出库)
     */
    public static final String MATRIL_RU = "SC072";
    /**
     * 退料单-供应商寄售(入库)
     */
    public static final String MATRIL_CHK = "SR071";
    /**
     * 销售退货订单
     */
    public static final String SALE_BACK = "SR03";
    /**
     * 甲供料方式:无
     */
    public static final String RAW_MATERIAL_MODE_WU = "WU";
    /**
     * 甲供料方式:采卖
     */
    public static final String RAW_MATERIAL_MODE_CM = "CM";

    /**
     * 甲供料方式:委外
     */
    public static final String RAW_MATERIAL_MODE_WW = "WW";

    /**
     * 预收款/付款结算方式:无
     */
    public static final String ADVANCE_SETTLE_MODE_WU = "WU";

    /**
     * 预收款/付款结算方式:按发票
     */
    public static final String ADVANCE_SETTLE_MODE_FP = "FAP";

    /**
     * 预收款/付款结算方式:按订单
     */
    public static final String ADVANCE_SETTLE_MODE_DD = "DINGD";

    /**
     * 预收款/付款结算方式:按合同
     */
    public static final String ADVANCE_SETTLE_MODE_HT = "HET";

    /**
     * 合同类型:标准合同
     */
    public static final String CONTRACT_TYPE_BZ = "BZHT";

    /**
     * 合同类型:框架协议
     */
    public static final String CONTRACT_TYPE_KJ = "KJXY";

    /**
     * 合同类型:补充协议
     */
    public static final String CONTRACT_TYPE_BC = "BCXY";

    /**
     * 合同类型:标准合同(框架式)
     */
    public static final String CONTRACT_TYPE_BZHTKJS = "BZHTKJS";

    /**
     * 特殊业务类别（采购/销售）：客户寄售结算
     */
    public static final String CUSTOMER_SPECIAL_BUS_CATEGORY = "CC";

    /**
     * 特殊业务类别（采购/销售）：供应商寄售结算
     */
    public static final String VENDOR_SPECIAL_BUS_CATEGORY = "VC";
    /**
     * 销售成本核算
     */
    public static final String COST_BUSINESS_TYPE_SA = "SC";

    /**
     * 尺码类型
     */
    public static final String SKUTYP_CM = "CM";
    /**
     * 颜色
     */
    public static final String SKUTYP_YS = "YS";

    /**
     * 来源单据类别:销售订单
     */
    public static final String REFER_DOC_CATEGORY = "SO";
    /**
     * 全部入库
     */
    public static final String IN_STORE_STATUS = "QBRK";
    /**
     * 全部出库
     */
    public static final String OUT_STORE_STATUS = "QBCK";

    /**
     * 部分入库
     */
    public static final String IN_STORE_STATUS_LI = "BFRK";
    /**
     * 部分出库
     */
    public static final String OUT_STORE_STATUS_LI = "BFCK";
    /**
     * 未入库
     */
    public static final String IN_STORE_STATUS_NOT = "WRK";
    /**
     * 未出库
     */
    public static final String OUT_STORE_STATUS_NOT = "WCK";

    /**
     * 过账
     */
    public static final String POSTING = "2";
    /**
     * 按收货单-甲供料退料
     */
    public static final String RECIPT_V = "SR093";
    /**
     * 按收货单-客户寄售退货
     */
    public static final String RECIPT_C = "SR094";
    /**
     * 按发货单-甲供料
     */
    public static final String ISSUE_V = "SC091";
    /**
     * 按发货单-客户寄售
     */
    public static final String ISSUE_C = "SC092";
    /**
     * 盘点 -过账
     */
    public static final String CONUNT_STATUS_R = "YGZ";
    /**
     * 出入库状态 -保存
     */
    public static final String CONUNT_STATUS_B = "BC";

    /**
     * 交货类型——采购
     */
    public static final String delivery_Category_CG = "1";

    /**
     * 交货类型-销售
     */
    public static final String delivery_Category_XS = "2";
    /**
     * 甲供料模式 无
     */
    public static final String RAW_w = "WU";
    /**
     * 采购模式常规
     */
    public static final String PURCHASE_MODE_COM = "1";
    /**
     * 串色串码
     */
    public static final String CHANGE_COLOR = "CSCM";

    /**
     * 库存调整-调量减少
     */
    public static final String ADJUST_RED = "ST012";
    /**
     * 库存调整-调量增加
     */
    public static final String ADJUST_ADD = "ST011";
    /**
     * 库存调整-调金额减少
     */
    public static final String ADJUST_RED_MO = "ST032";
    /**
     * 采购交货类型-按交货单
     */
    public static final String DELIEVER_type_JHD = "JHD";
    /**
     * 采购交货类型-按发货单
     */
    public static final String DELIEVER_type_FHD = "FHD";
    /**
     * 采购交货类型-按订单
     */
    public static final String DELIEVER_type_DD = "DD";
    /**
     * 长度类型
     */
    public static final String SKUTYPE_LE = "CD";

    /**
     * 拉链标识-链胚
     */
    public static final String ZIPPER_P = "1";

    /**
     * 拉链标识-链胚
     */
    public static final String ZIPPER_Z = "2";

    /**
     * 拉链标识-链胚
     */
    public static final String ZIPPER_T = "3";

    /**
     * 申报周期-按天
     */
    public static final String REPORT_CYCLE_DAY = "day";

    /**
     * 申报周期-按月
     */
    public static final String REPORT_CYCLE_MONTH = "month";

    /**
     * 客供-委外来料式
     */
    public static final String PURCHASE_TYPE_KGLL = "KGLL";
    /**
     * 采购入库
     */
    public static final String PURCHASE_CATEGORY = "POGR";
    /**
     * 采购退货
     */
    public static final String PURCHASE_DOCUEMTN_BACK = "RPO";
    /**
     * 采购退货出库
     */
    public static final String PURCHASE_CATEGORY_BACK = "RPOGI";
    /**
     * 销售出库
     */
    public static final String SALE_CATEGORY = "SOGI";
    /**
     * 销售出库
     */
    public static final String SALE_CATEGORY_BACK = "RSOGR";

    /**
     * 价格维度-按款
     */
    public static final String PRICE_K = "K";
    /**
     * 价格维度-按色
     */
    public static final String PRICE_K1 = "K1";
    /**
     * 全部领料
     */
    public static final String IN_DRAW_STATUS = "QLL";
    /**
     * 全部退料
     */
    public static final String OUT_DRAW_STATUS = "QTL";

    /**
     * 部分领料
     */
    public static final String IN_DRAW_STATUS_LI = "BFLL";
    /**
     * 部分退料
     */
    public static final String OUT_DRAW_STATUS_LI = "BFTL";
    /**
     * 未领料
     */
    public static final String IN_DRAW_STATUS_NOT = "WLL";
    /**
     * 未退料
     */
    public static final String OUT_DRAW_STATUS_NOT = "WTL";
    /**
     * 单据类型_领退料单-领料单
     */
    public static final String IN_MATERIAL_REQUISITION = "MR";
    /**
     * 单据类型_领退料单-退料单
     */
    public static final String OUT_MATERIAL_REQUISITION = "MRR";
    /**
     * 作业类型——甲供料结算
     */
    public static final String ARMOR_FOR_MATERIALS = "SC099";
    /**
     * 库存凭证类别——甲供料结算
     */
    public static final String DOCUMENT_CATEGORY_OWNER_FEED = "RMM";
    /**
     * 是否是主面料 -1
     */
    public static final String IS_MATERIAL = "1";
    /**
     * 价格录入方式 按含税价
     */
    public static final String PRICE_INTER_MODER_TAX = "HS";
    /**
     * 待办通知
     */
    public static final String TODO_TASK_DB = "DB";
    /**
     * 待批通知
     */
    public static final String TODO_TASK_DP = "DP";
    /**
     * 待办表 业务类型 创建意向协议
     */
    public static final String TODO_BUSINESS_TYPE_CJYXXY = "CJYXXY";
    /**
     * 待办表 业务类型 创建租赁合同
     */
    public static final String TODO_BUSINESS_TYPE_CJZLHT = "CJZLHT";
    /**
     * 待办表 业务类型 上传附件
     */
    public static final String TODO_BUSINESS_TYPE_SCFJ = "SCFJ";
    /**
     * 待办表 业务类型 上传解约附件
     */
    public static final String TODO_BUSINESS_TYPE_SCJYFJ = "SCJYFJ";
    /**
     * 待办表 业务类型 签约
     */
    public static final String TODO_BUSINESS_TYPE_QY = "QY";
    /**
     * 待办表 业务类型 待办
     */
    public static final String TODO_BUSINESS_TYPE_DB = "DB";
    /**
     * 待办表 业务类型 分配任务处理人
     */
    public static final String TODO_BUSINESS_TYPE_FPRWCLR = "FPRWCLR";
    /**
     * 待办表 业务类型 分配事项处理人
     */
    public static final String TODO_BUSINESS_TYPE_FPSXCLR = "FPSXCLR";
    /**
     * 工序
     */
    public static final String TABLE_PROCESS = "s_man_process";
    /**
     * 工作中心
     */
    public static final String TABLE_WORK_CENTER = "s_man_work_center";
    /**
     * 生产订单
     */
    public static final String TABLE_MANUFACTURE_ORDER = "s_man_manufacture_order";
    /**
     * 生产订单工序
     */
    public static final String TABLE_MANUFACTURE_ORDER_PROCESS = "s_man_manufacture_order_process";
    /**
     *采购订单
     */
    public static final String TABLE_PURCHASE_ORDER = "s_pur_purchase_order";
    /**
     *销售订单
     */
    public static final String TABLE_SALEORDER_ORDER = "s_sal_sales_order";
    /**
     * 外发加工发料单
     */
    public static final String TABLE_OUTSOURCE_ISSUE_NOTE = "s_del_outsource_material_issue_note";
    /**
     * 外发加工收料单
     */
    public static final String TABLE_OUTSOURCE_DELIVERY_NOTE = "s_del_outsource_delivery_note";
    /**
     * 生产月计划
     */
    public static final String TABLE_MONTH_MANUFACTURE_PLAN = "s_man_month_manufacture_plan";
    /**
     * 生产周计划
     */
    public static final String TABLE_WEEK_MANUFACTURE_PLAN = "s_man_week_manufacture_plan";
    /**
     * 生产进度日报
     */
    public static final String TABLE_DAY_MANUFACTURE_PROGRESS = "s_man_day_manufacture_progress";
    /**
     * 生产完工确认单
     */
    public static final String TABLE_MANUFACTURE_COMPLETE_NOTE = "s_man_manufacture_complete_note";
    /**
     * 外发加工费结算单
     */
    public static final String TABLE_MANUFACTURE_OUTSOURCE_SETTLE = "s_man_manufacture_outsource_settle";
    /**
     * 采购合同
     */
    public static final String TABLE_PURCHASE_CONTRACT = "s_pur_purchase_contract";
    /**
     * 需求单
     */
    public static final String TABLE_REQUIRE_DOC = "s_req_require_doc";
    /**
     * 技术转移
     */
    public static final String TABLE_RECORD_TECHTRANSFER = "s_tec_record_techtransfer";
    /**
     * 封样记录
     */
    public static final String TABLE_RECORD_FENGYANG = "s_tec_record_fengyang";
    /**
     * 通用道序
     */
    public static final String TABLE_PAY_PROCESS_STEP = "s_pay_process_step";
    /**
     * 商品道序
     */
    public static final String TABLE_PRODUCT_PROCESS_STEP = "s_pay_product_process_step";
    /**
     * 商品道序变更
     */
    public static final String TABLE_PRODUCT_PROCESS_STEP_UPDATE = "s_pay_update_product_process_step";
    /**
     * 完工量申报
     */
    public static final String TABLE_PROCESS_STEP_COMPLETE = "s_pay_process_step_complete";
    /**
     * 考勤信息
     */
    public static final String TABLE_WORKATTEND_RECORD = "s_pay_workattend_record";
    /**
     * 工资单
     */
    public static final String TABLE_PAY_SALARY_BILL = "s_pay_salary_bill";
    /**
     * 线部位组
     */
    public static final String TABLE_LINE_POSITION_GROUP = "s_tec_line_position_group";
    /**
     * 版型线
     */
    public static final String TABLE_MODEL_LINE = "s_tec_model_line";
    /**
     * 商品线
     */
    public static final String PRODUCT_LINE = "s_tec_product_line";
    /**
     * 生产次品台账
     */
    public static final String SCCPTZ = "s_man_manufacture_defective";
    /**
     * 外采样报销单
     */
    public static final String WCYBXD = "s_sam_osb_sample_reimburse";
    /**
     * 供应商分组
     */
    public static final String GYSFZ = "s_bas_vendor_tag";
    /**
     * 客户分组
     */
    public static final String KHFZ = "s_bas_customer_tag";
    /**
     * 实验室
     */
    public static final String SYS = "s_bas_laboratory";
    /**
     * 店铺
     */
    public static final String DP = "s_bas_shop";
    /**
     * 面辅料检测单
     */
    public static final String MFLJC = "s_qua_rawmat_check";
    /**
     * 特殊工艺检测单
     */
    public static final String TSGYJCD = "s_qua_specraft_check";
    /**
     * 成衣检测单
     */
    public static final String CYJCD = "s_qua_product_check";
    /**
     * 组合拉链
     */
    public static final String ZIPPER_ZH = "ZHLL";
    /**
     * 组合拉链
     */
    public static final String ZIPPER_ZT = "ZTLL";
    /**
     * 链胚
     */
    public static final String ZIPPER_LP = "LIANP";
    /**
     * 供应商寄售 来源类别
     */
    public static final String VEN_INV = "VCPO";
    /**
     * 客户寄售结算 来源类别
     */
    public static final String CUS_INV = "CCSO";
    /**
     * 提示语句：批量删除
     */
    public static final String DELETE_PROMPT_STATEMENT = "仅保存状态才充许删除";
    /**
     * 提示语句：批量删除(工作流)
     */
    public static final String DELETE_PROMPT_STATEMENT_APPROVE = "仅保存或已退回状态才充许删除";
    /**
     * 提示语句：批量确认
     */
    public static final String CHECK_PROMPT_STATEMENT = "仅保存状态才充许确认";
    /**
     * 提示语句：提交
     */
    public static final String SUBMIT_PROMPT_STATEMENT = "仅保存或已退回状态才充许提交";
    /**
     * 提示语句：确认操作(新增、编辑、变更、批量确认)
     */
    public static final String CONFIRM_PROMPT_STATEMENT = "明细行为空，无法确认！";
    /**
     * 提示语句：明细行校验——提交
     */
    public static final String SUBMIT_DETAIL_LINE_STATEMENT = "明细行为空，无法提交！";
    /**
     * 提示语句：编码重复
     */
    public static final String CODE_REPETITION = "编码已存在，请核实";
    /**
     * 提示语句：名称重复
     */
    public static final String NAME_REPETITION = "名称已存在，请核实";
    /**
     * 提示语句：作废状态校验
     */
    public static final String CONFIRM_CANCELLATION = "所选数据非'已确认'状态，无法作废！";
    /**
     * 取整方式 四舍五入
     */
    public static final String QZFS_MID = "SSWR";
    /**
     * 取整方式 向上取整
     */
    public static final String QZFS_UP = "XSQZ";
    /**
     * 取整方式 向下
     */
    public static final String QZFS_DOWN = "XXQZ";
    /**
     * 常规
     */
    public static final String DOCUMNET_TYPE_ZG = "CG";
    /**
     * 冲销
     */
    public static final String DOCUMNET_TYPE_CX = "CX";
    /**
     * 处理状态-作废
     */
    public static final String HANDLE_IM = "8";
    /**
     * 处理状态-冲销
     */
    public static final String HANDLE_CX = "A";
    /**
     * 导入
     */
    public static final String IMPORT = "1";

    /**
     * 核销状态-未核销
     */
    public static final String CLEAR_STATUS_WHX = "WHX";

    /**
     * 签收状态-未签收
     */
    public static final String SIGN_IN_STATUS_WQS = "WQS";
    /**
     * 签收状态-已签收
     */
    public static final String SIGN_IN_STATUS_YQS = "YQS";

    /**
     * 租户信息：用户昵称
     */
    public static final String NICK_NAME = "租户管理员";
    /**
     * 封样类型：标准封样
     */
    public static final String FENGYANG_TYPE_BZFY = "BZFY";
    /**
     * 封样类型：产前封样
     */
    public static final String FENGYANG_TYPE_CQFY = "CQFY";
    /**
     * 面料
     */
    public static final String MATERIAL_M = "ML";
    /**
     * 预留状态-全部预留
     */
    public static final String RE_STATUS_QB = "QBYL";
    /**
     * 预留状态-部分预留
     */
    public static final String RE_STATUS_BF = "BFYL";
    /**
     * 预留状态-无预留
     */
    public static final String RE_STATUS_WY = "WYL";
    /**
     * 库存 -不进行出入库
     */
    public static final String INV_CTROL_BGX = "BGX";
    /**
     * 辅料
     */
    public static final String MATERIAL_F= "FL";
    /**
     * 商品类型：服饰
     */
    public static final String MATERIAL_TYPE_FS= "FS";
    /**
     * 行业领域-鞋服
     */
    public static final String INDUSTRY_FIELD_XIEF= "XIEF";
    /**
     * 退还状态-未退回
     */
    public static final String RETURN_W= "WTH";
    /**
     * 退还状态-全部退回
     */
    public static final String RETURN_Q= "QBTH";
    /**
     * 退还状态-部分退回
     */
    public static final String RETURN_B= "BFTH";
    /**
     * 完工状态：未完工
     */
    public static final String COMPLETE_STATUS_WKS = "WKS";
    /**
     * 完工状态：已完工
     */
    public static final String COMPLETE_STATUS_YWG = "YWG";
    /**
     * 完工状态：部分完工
     */
    public static final String COMPLETE_STATUS_JXZ = "JXZ";

    /**
     * 完成状态：未完成
     */
    public static final String END_STATUS_WKS = "WKS";
    /**
     * 完成状态：已完成
     */
    public static final String END_STATUS_YWC = "YWC";
    /**
     * 完成状态：部分完成
     */
    public static final String END_STATUS_JXZ = "JXZ";
    /**
     * 完成状态：暂搁
     */
    public static final String END_STATUS_ZG = "ZG";
    /**
     * 完成状态：取消
     */
    public static final String END_STATUS_QX = "QX";

    /**
     * 出入库方式-来源
     */
    public static final String INV_SOURCE = "order";
    /**
     * 外采样报销状态-未报销
     */
    public static final String REIMBURSE_STATUS_WBX = "WBX";
    /**
     * 外采样报销状态-报销中
     */
    public static final String REIMBURSE_STATUS_BXZ = "BXZ";
    /**
     * 外采样报销状态-已报销
     */
    public static final String REIMBURSE_STATUS_YBX = "YBX";
    /**
     * 纸质合同上传状态-未上传
     */
    public static final String CHUKU_CATEGORY_WSC = "SWC";
    /**
     * 纸质合同上传状态-未上传
     */
    public static final String CONTRACT_UPLOAD_STATUS = "WSC";
    /**
     * 纸质合同上传状态-已上传
     */
    public static final String CONTRACT_UPLOAD_STATUS_Y = "YSC";
    /**
     * 纸质合同签收状态-未签收
     */
    public static final String CONTRACT_SIGNIN_STATUS = "WQS";
    /**
     * 纸质合同签收状态-已签收
     */
    public static final String CONTRACT_SIGNIN_STATUS_Y = "YQS";
    /**
     * 业务类别-销售发货单
     */
    public static final String SALE_SHIP = "FHD";
    /**
     * 业务类别-采购交货单
     */
    public static final String PURCHASE_SHIP = "JHD";
    /**
     * 单据类型_样品借还-借出
     */
    public static final String SAMPLE_J = "JC";
    /**
     * 单据类型_样品借还-归还
     */
    public static final String SAMPLE_G = "GH";
    /**
     * 单据类型_样品借还-遗失
     */
    public static final String SAMPLE_Y = "YC";
    /**
     * 配送到仓
     */
    public static final String SHIP_TYPR_D = "CK";
    /**
     * 类别-发货
     */
    public static final String DEL_CATEGORY_SD="SD";
    /**
     * 配送到客户
     */
    public static final String SHIP_TYPR_C = "KH";
    /**
     * 配置至门店
     */
    public static final String SHIP_TYPR_M = "MD";
    /**
     * 配置至加工供应商
     */
    public static final String SHIP_TYPR_V= "JGS";
    /**
     * 其它出库-客供料
     */
    public static final String ORHER_MOVE_TYPE_CHK= "SC31";

    /**
     * 其它入库-客供料
     */
    public static final String ORHER_MOVE_TYPE_RU= "SR31";
    /**
     * 更新库存
     */
    public static final String INV_RESH= "GX";

    /**
     * 价格维度-按款
     */
    public static final String JGWD_K= "K";

    /**
     * 价格维度-按色
     */
    public static final String JGWD_K1= "K1";
    /**
     * 是
     */
    public static final String YES_OR_NO_Y= "Y";

    /**
     * 是否-否
     */
    public static final String YES_OR_NO_N= "N";

    /**
     * 账实持平
     */
    public static final String STOCK_P= "XD";

    /**
     * 盘盈
     */
    public static final String STOCK_Y= "PY";

    /**
     * 盘亏
     */
    public static final String STOCK_K= "PK";

    /**
     * 库存调整-调金额
     */
    public static final String ADJUST_CUR = "ST021";

    /**
     * 库存调整-调价
     */
    public static final String ADJUST_PRI = "ST011";

    /**
     * 全部排产
     */
    public static final String ALL_PC = "QBSC";
    /**
     * 币种-人民币
     */
    public static final String RMB = "CNY";
    /**
     * 货币单位-元
     */
    public static final String YUAN = "YUAN";
    /**
     * 生产直发出库
     */
    public static final String CK_CATEGORY = "SCZF";
    /**
     * 完工类型-常规计薪
     */
    public static final String JXCG = "JXCG";

    /**
     * 线部位类别-专用
     */
    public static final String ZY = "ZY";
    /**
     * 自产
     */
    public static final String PROCEE_ZC = "ZC";

    /**
     * 预警类型编码 已逾期
     */
    public static final String YYQ = "YYQ";

    /**
     *  预警类型编码 即将到期
     */
    public static final String JJDQ = "JJDQ";

    /**
     *  样品入库
     */
    public static final String MOVE_TYPE_RK_YP = "SR61";

    /**
     *  单据类别 销售发货单
     */
    public static final String Delivery_Note = "DeliveryNote";
    /**
     *  样品出库
     */
    public static final String MOVE_TYPE_CHk_YP = "SC61";

    /**
     *  评审阶段
     */
    public static final String PSJD = "s_review_stage";

    /**
     *  FALSE
     */
    public static final String FALSE = "FALSE";

}
