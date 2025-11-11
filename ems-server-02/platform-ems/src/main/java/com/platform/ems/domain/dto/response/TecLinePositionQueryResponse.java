package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 线部位档案对象 s_tec_line_position
 *
 * @author hjj
 * @date 2021-08-19
 */
@Data
@ApiModel
public class TecLinePositionQueryResponse implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位档案")
    private Long linePositionSid;

    @ApiModelProperty(value = "线部位编码（人工编码）")
    private String linePositionCode;

    @ApiModelProperty(value = "线部位名称")
    private String linePositionName;

    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

    @ApiModelProperty(value = "上下装/套装（数据字典的键值或配置档案的编码）")
    private String upDownSuit;

    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @ApiModelProperty(value ="备注")
    private String remark;
}
