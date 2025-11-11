package com.platform.ems.domain.dto.request.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 应收流水报表 FinBookAccountPayableFormResponse
 *
 * @author chenkaiwen
 * @date 2021-07-12
 */
@Data
@Accessors(chain = true)
@ApiModel
public class FinBookAccountReceivableFormRequest extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @ApiModelProperty(value = "核销状态过滤")
    private String clearStatusNot;

    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    @ApiModelProperty(value = "业务类型")
    private String buTypeCode;

    @ApiModelProperty(value = "流水类型")
    private String bookType;

    @ApiModelProperty(value = "流水来源类别")
    private String bookSourceCategory;

    @ApiModelProperty(value = "销售合同号")
    private Long saleContractCode;

    @ApiModelProperty(value = "销售订单号")
    private Long saleOrderCode;

    @ApiModelProperty(value = "销售发票号")
    private Long saleInvoiceCode;

    @ApiModelProperty(value = "收款单的款项类别")
    private String accountCategory;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "系统SID-流水类型")
    private String[] bookTypeList;

    @ApiModelProperty(value = "系统SID-流水来源类别")
    private String[] bookSourceCategoryList;

    @ApiModelProperty(value = "系统SID-核销状态")
    private String[] clearStatusList;

    @ApiModelProperty(value = "明细sid数组")
    private Long[] bookAccountReceivableItemSidList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期")
    private Date accountValidDate;

    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）\n")
    private String isFinanceVerify;
}
