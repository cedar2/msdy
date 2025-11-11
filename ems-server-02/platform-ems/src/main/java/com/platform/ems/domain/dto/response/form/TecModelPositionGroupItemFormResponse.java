package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 版型部位组明细报表 TecModelPositionGroupItemFormResponse
 *
 * @author chenkaiwen
 * @date 2021-11-12
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelPositionGroupItemFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型部位组明细")
    private Long groupItemSid;

    @Excel(name = "版型部位组编码")
    @ApiModelProperty(value = "版型部位组编码（人工编码）")
    private String groupCode;

    @Excel(name = "版型部位组名称")
    @ApiModelProperty(value = "版型部位组名称")
    private String groupName;

    @Excel(name = "版型部位编码")
    @ApiModelProperty(value = "版型部位档案编码")
    private String modelPositionCode;

    @Excel(name = "版型部位名称")
    @ApiModelProperty(value = "版型部位档案名称")
    private String modelPositionName;

    @Excel(name = "上下装/套装",dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值或配置档案的编码）")
    private String upDownSuit;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name = "启用/停用",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private String confirmDate;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}
