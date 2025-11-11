package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务导出 BasMaterialFwExportResponse
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialFwExportResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "服务编码")
    @ApiModelProperty(value = "服务编码")
    private String materialCode;

    @Excel(name = "服务名称")
    @ApiModelProperty(value = "服务名称")
    private String materialName;

    @Excel(name = "服务分类")
    @ApiModelProperty(value = "服务分类名称（商品/商品/服务）")
    private String materialClassName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "供应商(默认)")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "工价项")
    private String defalut;

    @Excel(name = "类型")
    @ApiModelProperty(value = "类型名称（商品/商品/服务）")
    private String materialTypeName;

    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;


}
