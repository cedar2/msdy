package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * BOM部位档案对象 s_tec_bom_position
 *
 * @author linhongwei
 * @date 2022-07-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_bom_position")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecBomPosition extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bomPositionSidList;
    /**
     * 系统ID-BOM部位档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-BOM部位档案")
    private Long bomPositionSid;

    /**
     * BOM部位编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "BOM部位编码")
    @ApiModelProperty(value = "BOM部位编码")
    private Long bomPositionCode;

    /**
     * BOM部位名称
     */
    @Excel(name = "BOM部位名称")
    @ApiModelProperty(value = "BOM部位名称")
    private String bomPositionName;

    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装")
    private String upDownSuit;

    @Excel(name = "部位说明")
    @ApiModelProperty(value = "部位说明")
    private String positionDescription;

    /**
     * 启用/停用状态
     */
    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用" , dictType = "s_valid_flag")
    private String status;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * BOM部位类型编码
     */
    @ApiModelProperty(value = "BOM部位类型编码")
    private String bomPositionType;

    @Excel(name = "备注")
    @ApiModelProperty(value = "BOM部位备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy/MM/dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;



    @TableField(exist = false)
    private String[] upDownSuitList;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;


    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}
