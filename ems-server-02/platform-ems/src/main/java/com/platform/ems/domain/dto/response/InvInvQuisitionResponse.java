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

import java.io.Serializable;
import java.util.Date;

/**
 * 领退料 导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInvQuisitionResponse implements Serializable {

    @Excel(name = "领退料单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "领退料单号")
    private Long materialRequisitionCode;

    @ApiModelProperty(value = "作业类型名称")
    @Excel(name = "作业类型")
    private String movementTypeName;

    @ApiModelProperty(value = "单据类型")
    @Excel(name = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessTypeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "需求部门")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "需求部门")
    private String requireDepartmentName;

    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "班组")
    @ApiModelProperty(value = "班组")
    private String workCenterName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
}
