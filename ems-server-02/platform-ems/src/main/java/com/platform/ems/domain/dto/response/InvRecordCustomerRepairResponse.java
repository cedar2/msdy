package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户返修台账明细报表实体
 *
 * @author yangqz
 * @date 2021-12-1
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvRecordCustomerRepairResponse {

    @Excel(name = "客户返修台账流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户返修台账流水号")
    private Long customerRepairCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户返修台账流水号")
    private Long customerRepairSid;

    @ApiModelProperty(value = "客户")
    @Excel(name = "客户")
    private String customerName;

    @ApiModelProperty(value = "公司")
    @Excel(name = "公司")
    private String companyName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value ="商品/物料名称")
    private String materialName;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value ="商品/物料编码")
    private String materialCode;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @Excel(name = "返修量")
    @ApiModelProperty(value = "返修量")
    private BigDecimal repairQuantity;

    @Excel(name = "退回量")
    @ApiModelProperty(value = "退还量")
    private BigDecimal returnQuantity;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String unitBaseName;

    @Excel(name = "退回状态",dictType = "s_return_status")
    @ApiModelProperty(value = "退还状态（数据字典的键值或配置档案的编码）")
    private String returnStatus;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划退回日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划退还日期")
    private Date planReturnDate;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

}
