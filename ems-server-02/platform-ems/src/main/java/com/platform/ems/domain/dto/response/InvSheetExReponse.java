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
 * 盘点 导出
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvSheetExReponse implements Serializable {

    @ApiModelProperty(value = "盘点状态（数据字典的键值）")
    private String countStatus;

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
    @Excel(name = "开单日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开单日期")
    private Date documentDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "计划盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划盘点日期")
    private Date planCountDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "盘点结果录入日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "盘点结果录入日期")
    private Date countResultEnterDate;

    @Excel(name = "冻结出入库作业",dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否冻结出入库作业（数据字典的键值）")
    private String isFreezeStock;

    @Excel(name = "是否记录账面库存",dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否记录账面库存（数据字典的键值）")
    private String isQuantityRecord;

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

    @Excel(name = "特殊库存")
    @ApiModelProperty(value = "特殊库存")
    private String specialStockName;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "盘点库存凭证编码")
    @ApiModelProperty(value = "盘点库存凭证编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inventoryDocumentCode;

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

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
}
