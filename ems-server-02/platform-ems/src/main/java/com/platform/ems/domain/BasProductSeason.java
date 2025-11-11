package com.platform.ems.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 产品季档案对象 s_bas_product_season
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_product_season")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasProductSeason extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-产品季档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-产品季档案")
    @TableId
    private Long productSeasonSid;

    /** 产品季编码 */
    @Excel(name = "产品季编码",sort = 1)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /** 产品季名称 */
    @Excel(name = "产品季名称",sort = 2)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /** 年份（年份的编码） */
    @Excel(name = "年份",dictType = "s_year",sort = 3)
    @ApiModelProperty(value = "年份（年份的编码）")
    private String year;

    /** 季度（季度的编码） */
    @Excel(name = "季节",dictType = "s_season",sort = 4)
    @ApiModelProperty(value = "季度（季度的编码）")
    private String seasonCode;

    /** 产品季所属阶段编码 */
    @Excel(name = "所属阶段" ,dictType = "s_season_stage",sort = 5)
    @ApiModelProperty(value = "产品季所属阶段编码")
    private String productSeasonStage;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用",dictType = "s_valid_flag",sort = 6)
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /** 处理状态 */
    @Excel(name = "处理状态",dictType = "s_handle_status",sort = 7)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name="备注",sort = 8)
    private String remark;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建人账号 */
    @Excel(name = "创建人",sort = 9)
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /** 创建日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd",sort = 10)
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新人账号 */
    @Excel(name = "更改人",sort = 11)
    @ApiModelProperty(value = "更新人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /** 更新日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd",sort = 12)
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateDate;

    /** 确认人账号 */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认人账号 */
    @Excel(name = "确认人",sort = 13)
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /** 确认日期 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd",sort = 14)
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /** 数据源系统 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "年份数组")
    @TableField(exist = false)
    private String[] yearList;

    @ApiModelProperty(value = "季节数组")
    @TableField(exist = false)
    private String[] seasonCodeList;

    @ApiModelProperty(value = "处理状态数组")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "所属阶段数组")
    @TableField(exist = false)
    private String[] productSeasonStageList;



}
