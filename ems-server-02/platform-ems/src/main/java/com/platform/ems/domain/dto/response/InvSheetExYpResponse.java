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
 * 样品盘点 导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvSheetExYpResponse implements Serializable {

    @Excel(name = "盘点单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "盘点单号")
    private Long inventorySheetCode;

    @Excel(name = "年度")
    @ApiModelProperty(value = "年度")
    private String year;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "计划盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划盘点日期")
    private Date planCountDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "盘点结果录入日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "盘点结果录入日期")
    private Date countResultEnterDate;


    @Excel(name = "参考盘点作业单号")
    @ApiModelProperty(value = "参考单号")
    private String referDocument;

    @Excel(name = "盘点过账人")
    @ApiModelProperty(value = "过账人名称")
    private String accountorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "盘点过账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "过账日期(盘点过账日期)")
    private Date accountDate;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "盘点库存凭证编码")
    @ApiModelProperty(value = "盘点库存凭证编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryDocumentCode;

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


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
}
