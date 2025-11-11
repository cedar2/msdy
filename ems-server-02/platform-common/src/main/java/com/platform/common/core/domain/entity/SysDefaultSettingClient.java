package com.platform.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 系统默认设置_租户级对象 s_sys_default_setting_client
 *
 * @author chenkw
 * @date 2022-04-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_default_setting_client")
public class SysDefaultSettingClient extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @NotBlank(message = "租户ID不能为空")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private String[] clientIdList;

    /**
     * 研发季（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "研发季（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "研发季（数据字典的键值或配置档案的编码）")
    private String productSeasonYanfa;

    @TableField(exist = false)
    @ApiModelProperty(value = "研发季（多选）")
    private String[] productSeasonYanfaList;

    /**
     * 采购季（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "采购季（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "采购季（数据字典的键值或配置档案的编码）")
    private String productSeasonCaigou;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购季（多选）")
    private String[] productSeasonCaigouList;

    /**
     * 销售季（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "销售季（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "销售季（数据字典的键值或配置档案的编码）")
    private String productSeasonXiaoshou;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售季（多选）")
    private String[] productSeasonXiaoshouList;

    /**
     * 生产季（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "生产季（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "生产季（数据字典的键值或配置档案的编码）")
    private String productSeasonShengchan;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产季（多选）")
    private String[] productSeasonShengchanList;

    /**
     *  销售财务对接人员（多选）
     */
    @Excel(name = "销售财务对接人员（用户账号）")
    @ApiModelProperty(value = "销售财务对接人员（用户账号）")
    private String saleFinanceAccount;
    @TableField(exist = false)
    @ApiModelProperty(value = "销售财务对接人员（用户账号）")
    private String[] saleFinanceAccountList;

    /**
     *  采购财务对接人员（多选）
     */
    @Excel(name = "采购财务对接人员（用户账号）")
    @ApiModelProperty(value = "采购财务人员（用户账号）")
    private String purchaseFinanceAccount;
    @TableField(exist = false)
    @ApiModelProperty(value = "采购财务人员（用户账号）")
    private String[] purchaseFinanceAccountList;


    @ApiModelProperty(value = "其它入库是否自动带价格（数据字典的键值或配置档案的编码）")
    private String isAutodisplayPriceInStockOther;

    @ApiModelProperty(value = "其它出库是否自动带价格（数据字典的键值或配置档案的编码）")
    private String isAutodisplayPriceOutStockOther;

    @ApiModelProperty(value = "盘点是否启用审批（数据字典的键值或配置档案的编码）")
    private String isPandianApproval;

    @ApiModelProperty(value = "供应商填写要求(其它入库)（数据字典的键值或配置档案的编码）")
    private String vendorEnterRequestInStockOther;

    @ApiModelProperty(value = "客户填写要求(其它入库)（数据字典的键值或配置档案的编码）")
    private String customerEnterRequestInStockOther;

    @ApiModelProperty(value = "供应商填写要求(其它出库)（数据字典的键值或配置档案的编码）")
    private String vendorEnterRequestOutStockOther;

    @ApiModelProperty(value = "客户填写要求(其它出库)（数据字典的键值或配置档案的编码）")
    private String customerEnterRequestOutStockOther;

    @ApiModelProperty(value = "价格填写要求(其它入库)（数据字典的键值或配置档案的编码）")
    private String priceEnterRequestInStockOther;

    @ApiModelProperty(value = "价格填写要求(其它出库)（数据字典的键值或配置档案的编码）")
    private String priceEnterRequestOutStockOther;

    @ApiModelProperty(value = "次品扣款价(范围内)默认等于加工价（是否数据字典）")
    private String isInDefectivePriceTaxEqualToPriceTax;

    @ApiModelProperty(value = "生产订单的工序总览班组是否可编辑/隐藏（是否数据字典）")
    private String isProcessWorkCenterEditable;

    @ApiModelProperty(value = "生产订单的商品明细合同交期是否需相同（是否数据字典）")
    private String isProductContractDateIdentical;

    @ApiModelProperty(value = "生产订单新建是否默认带最近生产订单信息（是否数据字典）")
    private String isAssociateLatestManufactureOrder;

    @ApiModelProperty(value = "生产订单关注事项是否必填（数据字典的键值或配置档案的编码）")
    private String isRequiredConcernTask;

    @ApiModelProperty(value = "商品SKU编码(ERP)录入方式(项目)")
    private String erpMaterialSkuEnterModeProject;

    @ApiModelProperty(value = "商品款号/SPU号录入方式(项目)")
    private String productCodeEnterModeProject;

    @ApiModelProperty(value = "采购单据合同号录入方式（数据字典的键值或配置档案的编码）")
    private String purchaseOrderContractEnterMode;

    @ApiModelProperty(value = "销售单据合同号录入方式")
    private String saleOrderContractEnterMode;

    @ApiModelProperty(value = "外发加工费结算单合同号录入方式")
    private String manOutsourceSettleContractEnterMode;

    @ApiModelProperty(value = "工作台报表是否允许查询其它用户(数据字典的键值或配置档案的编码)")
    private Integer isQueryOtherUser;

    @ApiModelProperty(value = "长时间未操作限时(分钟)")
    private Integer logonTimeout;

    @ApiModelProperty(value = "是否定时自动更新工序完成状态（是否数据字典）")
    private String isAutoUpdateProcessStatus;

    @ApiModelProperty(value = "是否定时自动更新事项完成状态（是否数据字典）")
    private String isAutoUpdateConcernTaskStatus;

    @ApiModelProperty(value = "是否鞋服行业（是否数据字典）")
    private String isXiefuIndustry;

    @ApiModelProperty(value = "商品未排产提醒天数")
    private Integer wpcRemindDays;

    @ApiModelProperty(value = "人事管理通知人员(用户账号)")
    private String personnelMgtNoticeAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "人事管理通知人员(用户账号)")
    private String[] personnelMgtNoticeAccountList;

    @ApiModelProperty(value = "销售待排产通知人员(用户账号)")
    private String saleDpcNoticeAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售待排产通知人员(用户账号)")
    private String saleDpcNoticeAccountId;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售待排产通知人员(用户账号)")
    private String[] saleDpcNoticeAccountList;

    /**
     *  电签超量提醒人员（多选）
     */
    @Excel(name = "电签超量提醒人员（用户账号）")
    @ApiModelProperty(value = "电签超量提醒人员（用户账号）")
    private String dianqianExceedNoticeAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "电签超量提醒人员（用户账号）")
    private String[] dianqianExceedNoticeAccountList;

    /**
     * 即将到期预警天数(劳动合同)
     */
    @Excel(name = "即将到期预警天数(劳动合同)")
    @ApiModelProperty(value = "即将到期预警天数(劳动合同)")
    private BigDecimal toexpireDaysLdht;

    /**
     * 即将到期预警天数(试用期)
     */
    @Excel(name = "即将到期预警天数(试用期)")
    @ApiModelProperty(value = "即将到期预警天数(试用期)")
    private BigDecimal toexpireDaysSyq;

    /**
     * 即将到期预警天数(雇主责任险)
     */
    @Excel(name = "即将到期预警天数(雇主责任险) ")
    @ApiModelProperty(value = "即将到期预警天数(雇主责任险) ")
    private Integer toexpireDaysGzzrx;

    /**
     * 即将到期预警天数(采购合同)
     */
    @Excel(name = "即将到期预警天数(采购合同) ")
    @ApiModelProperty(value = "即将到期预警天数(采购合同) ")
    private BigDecimal toexpireDaysCght;

    /**
     * 即将到期预警天数(采购订单)
     */
    @Excel(name = "即将到期预警天数(采购订单) ")
    @ApiModelProperty(value = "即将到期预警天数(采购订单) ")
    private BigDecimal toexpireDaysCgdd;

    @ApiModelProperty(value = "即将到期预警天数(采购订单) -系统")
    @TableField(exist = false)
    private BigDecimal toexpireDaysCgddSys;

    /**
     * 即将到期预警天数(销售合同)
     */
    @Excel(name = "即将到期预警天数(销售合同) ")
    @ApiModelProperty(value = "即将到期预警天数(销售合同) ")
    private BigDecimal toexpireDaysXsht;

    /**
     * 即将到期预警天数(销售订单)
     */
    @Excel(name = "即将到期预警天数(销售订单) ")
    @ApiModelProperty(value = "即将到期预警天数(销售订单) ")
    private BigDecimal toexpireDaysXsdd;

    @TableField(exist = false)
    @ApiModelProperty(value = "即将到期预警天数(销售订单)-系统 ")
    private BigDecimal toexpireDaysXsddSys;

    /**
     * 即将到期预警天数(生产订单)
     */
    @Excel(name = "即将到期预警天数(生产订单) ")
    @ApiModelProperty(value = "即将到期预警天数(生产订单) ")
    private BigDecimal toexpireDaysScdd;

    /**
     * 即将到期预警天数(生产订单-工序)
     */
    @Excel(name = "即将到期预警天数(生产订单-工序) ")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-工序) ")
    private BigDecimal toexpireDaysScddGx;

    /**
     * 即将到期预警天数(生产订单-商品)
     */
    @Excel(name = "即将到期预警天数(生产订单-商品) ")
    @ApiModelProperty(value = "即将到期预警天数(生产订单-商品) ")
    private BigDecimal toexpireDaysScddSp;

    /**
     * 即将到期预警天数(生产订单-事项)
     */
    @ApiModelProperty(value = "即将到期预警天数(生产订单-事项)")
    private BigDecimal toexpireDaysScddSx;

    /**
     * 即将到期预警天数(项目)
     */
    @Excel(name = "即将到期预警天数(项目)")
    @ApiModelProperty(value = "即将到期预警天数(项目)")
    private BigDecimal toexpireDaysProject;

    /**
     * 项目任务执行提醒天数
     */
    @Excel(name = "项目任务执行提醒天数")
    @ApiModelProperty(value = "项目任务执行提醒天数")
    private BigDecimal toexecuteNoticeDaysPrjTask;

    /**
     * 前置任务未完成提示方式
     */
    @Excel(name = "前置任务未完成提示方式", dictType = "s_message_display_type")
    @ApiModelProperty(value = "前置任务未完成提示方式")
    private String noticeTypePreTaskIncomplete;

    /**
     * 商品道序工价不一致提示方式
     */
    @Excel(name = "商品道序工价不一致提示方式", dictType = "s_message_display_type")
    @ApiModelProperty(value = "商品道序工价不一致提示方式")
    private String noticeTypeProcessPriceInconsistent;

    /**
     * 采购申请单转采购订单超量提示方式
     */
    @Excel(name = "采购申请单转采购订单超量提示方式", dictType = "s_message_display_type")
    @ApiModelProperty(value = "采购申请单转采购订单超量提示方式")
    private String noticeTypePurRequireToOrderExcess;

    /**
     * 项目状态是否自动设置已完成
     */
    @Excel(name = "项目状态是否自动设置已完成")
    @ApiModelProperty(value = "项目状态是否自动设置已完成")
    private String isAutoSetProjectStatus;

    @ApiModelProperty(value = "项目任务状态是否自动设置进行中（数据字典的键值或配置档案的编码）")
    private String isAutoSetProjectTaskStatus;

    @ApiModelProperty(value = "产品季是否必填(商品档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredProductSeasonProduct;

    @ApiModelProperty(value = "上下装/套装是否必填(商品档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredUpDownSuitProduct;

    @ApiModelProperty(value = "版型是否必填(商品档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredModelProduct;

    @ApiModelProperty(value = "生产工艺类型是否必填(商品档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredProductTechniqueTypeProduct;

    @ApiModelProperty(value = "尺码组是否必填(商品档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredSku2GroupProduct;

    @ApiModelProperty(value = "供应商是否必填(物料/商品档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredVendorMaterial;

    @ApiModelProperty(value = "用量计量单位是否必填(物料档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredUnitQuantityMaterial;

    @ApiModelProperty(value = "报价是否必填(物料档案)（数据字典的键值或配置档案的编码）")
    private String isRequiredQuotePriceTaxMaterial;

    @ApiModelProperty(value = "采购类订单合同号是否必填（是否数据字典）")
    private String isRequiredPurchaseOrderContract;

    @ApiModelProperty(value = "报价关联维度(采购价)（数据字典的键值或配置档案的编码）")
    private String quotePriceAssociateDimensionPurchasePrice;

    @ApiModelProperty(value = "销售类订单合同号是否必填（是否数据字典）")
    private String isRequiredSaleOrderContract;

    @ApiModelProperty(value = "是否启用审批流(采购申请单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowPurchaseRequire;

    @ApiModelProperty(value = "是否启用审批流(图稿绘制单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowTughzd;

    @ApiModelProperty(value = "是否启用审批流(样品初审单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowYangpcsd;

    @ApiModelProperty(value = "是否启用审批流(样品终审单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowYangpzsd;

    @ApiModelProperty(value = "是否启用审批流(视觉设计单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowShijsjd;

    @ApiModelProperty(value = "是否启用审批流(文案拍摄单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowWenapsd;

    @ApiModelProperty(value = "是否启用审批流(新品试销计划单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowShixjhd;

    @ApiModelProperty(value = "是否启用审批流(试销结果单)（数据字典的键值或配置档案的编码）")
    private String isWorkflowShixjgd;

    @ApiModelProperty(value = "供应商对账单物料类型是否必填（数据字典的键值或配置档案的编码）")
    private String isRequiredMaterialTypeGysdzd;

    @ApiModelProperty(value = "客户对账单物料类型是否必填（数据字典的键值或配置档案的编码）")
    private String isRequiredMaterialTypeKhdzd;

    @ApiModelProperty(value = "销售价默认关联出客户")
    private String isSalePriceAssociateCustomer;

    @ApiModelProperty(value = "是否集成电签软件")
    private String isEnableEsign;

    @ApiModelProperty(value = "收款核销方式(s_shoukuan_account_clear_way)")
    private String shoukuanAccountClearWay;

    @ApiModelProperty(value = "付款核销方式(s_fukuan_account_clear_way)")
    private String fukuanAccountClearWay;

    @ApiModelProperty(value = "收款单附件是否必填")
    private String isAttachRequiredShoukuan;

    @ApiModelProperty(value = "付款单附件是否必填")
    private String isAttachRequiredFukuan;

    @ApiModelProperty(value = "是否启用审批流(付款)")
    private String isWorkflowFk;

    @ApiModelProperty(value = "是否启用审批流(收款)")
    private String isWorkflowSk;

    @ApiModelProperty(value = "是否启用审批流(客户扣款单)")
    private String isWorkflowKhkk;

    @ApiModelProperty(value = "是否启用审批流(供应商扣款单)")
    private String isWorkflowGyskk;

    @ApiModelProperty(value = "是否启用审批流(客户押金)")
    private String isWorkflowKhyj;

    @ApiModelProperty(value = "是否启用审批流(供应商押金)")
    private String isWorkflowGysyj;

    @ApiModelProperty(value = "是否启用审批流(客户暂押款)")
    private String isWorkflowKhzyk;

    @ApiModelProperty(value = "是否启用审批流(供应商暂押款)")
    private String isWorkflowGyszyk;

    @ApiModelProperty(value = "是否启用审批流(应付暂估调价量单)")
    private String isWorkflowYfzgtjld;

    @ApiModelProperty(value = "是否启用审批流(应收暂估调价量单)")
    private String isWorkflowYszgtjld;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @NotBlank(message = "创建人不能为空")
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（用户昵称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @NotNull(message = "创建时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人（用户昵称）")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 即将到期预警天数(任务)
     */
    @ApiModelProperty(value = "即将到期预警天数(任务)")
    private Integer toexpireDaysPrjTask;

    /**
     * 待办提醒天数(任务)
     */
    @ApiModelProperty(value = "待办提醒天数(任务)")
    private Integer todoDaysPrjTask;
}
