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

import java.util.Date;

/**
 * 样品借还导出
 *
 * @author yangqz
 * @date 2022-3-31
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SamSampleLendreturnExResponse {

    @Excel(name = "借还单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品借还单号")
    private Long lendreturnCode;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;


    @ApiModelProperty(value = "仓库")
    @Excel(name = "仓库")
    private String storehouseName;

    @ApiModelProperty(value = "库位")
    @Excel(name = "库位")
    private String locationName;

    @Excel(name = "借出人")
    private String lenderName;

    @Excel(name = "归还人")
    private String returnerName;

    @ApiModelProperty(value = "当前审批节点名称")
    @Excel(name = "当前审批节点")
    private String approvalNode;

    @ApiModelProperty(value = "当前审批人")
    @Excel(name = "当前审批人")
    private String approvalUserName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

}
