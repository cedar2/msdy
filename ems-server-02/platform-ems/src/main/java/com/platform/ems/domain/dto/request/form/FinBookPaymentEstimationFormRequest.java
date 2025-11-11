package com.platform.ems.domain.dto.request.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 应付暂估流水报表 FinBookPaymentEstimationFormRequest
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookPaymentEstimationFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "采购交货单号")
    private String deliveryNoteCode;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @ApiModelProperty(value = "产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "采购员")
    private String buyer;

    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @ApiModelProperty(value = "物料商品编码")
    private String materialCode;

    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    @TableField(exist = false)
    @ApiModelProperty(value ="出入库日期(起)")
    private String accountDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value ="出入库日期(止)")
    private String accountDateEnd;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusMoneyList;

    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @ApiModelProperty(value = "采购模式")
    private String[] materialTypeList;
}
