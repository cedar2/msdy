package com.platform.ems.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 线部位档案对象 s_tec_line_position
 *
 * @author hjj
 * @date 2021-08-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_line_position")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecLinePosition extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-线部位档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位档案")
    private Long linePositionSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] linePositionSidList;

    public void setLinePositionCode(String linePositionCode) {
        if (StrUtil.isNotBlank(linePositionCode)) {
            linePositionCode = linePositionCode.replaceAll("\\s*", "");
        }
        this.linePositionCode = linePositionCode;
    }

    public void setLinePositionName(String linePositionName) {
        if (StrUtil.isNotBlank(linePositionName)) {
            linePositionName = linePositionName.trim();
        }
        this.linePositionName = linePositionName;
    }

    /**
     * 线部位编码（人工编码）
     */
//    @NotEmpty(message = "线部位编码不能为空")
//    @Length(max = 8, message = "线部位编码不能超过8个字符")
    @Excel(name = "线部位编码")
    @ApiModelProperty(value = "线部位编码（人工编码）")
    private String linePositionCode;

    /**
     * 线部位名称
     */
    @NotEmpty(message = "线部位名称不能为空")
    @Length(max = 300, message = "线部位名称不能超过300个字符")
    @Excel(name = "线部位名称")
    @ApiModelProperty(value = "线部位名称")
    private String linePositionName;

    /**
     * 上下装/套装（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值或配置档案的编码）")
    private String upDownSuit;

    /**
     * 度量方法说明
     */
    @Length(max = 600, message = "度量方法说明不能超过600个字符")
    @Excel(name = "度量方法说明")
    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

    /**
     * 计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unit;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 线部位类别（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "线部位类别不能为空")
    @Excel(name = "线部位类别", dictType = "s_line_position_category")
    @ApiModelProperty(value = "线部位类别（数据字典的键值或配置档案的编码）")
    private String linePositionCategory;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 上下装/套装list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装list")
    private String[] upDownSuitList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @TableField(exist = false)
    private List<Long> linePositionSids;

    /**
     * 线部位编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "线部位编码list")
    private List<String> linePositionCodeList;

    /**
     * 线部位名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "线部位名称list")
    private List<String> linePositionNameList;

    /**
     * 度量方法说明
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "度量方法说明list")
    private List<String> measureDescriptionList;
}
