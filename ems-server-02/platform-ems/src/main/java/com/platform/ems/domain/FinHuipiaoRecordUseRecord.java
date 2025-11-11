package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 汇票台账-使用记录表对象 s_fin_huipiao_record_use_record
 *
 * @author platform
 * @date 2024-03-12
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_huipiao_record_use_record")
public class FinHuipiaoRecordUseRecord extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-汇票台账使用记录表
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-汇票台账使用记录表")
    private Long huipiaoRecordUseRecordSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] huipiaoRecordUseRecordSidList;

    /**
     * 系统SID-汇票台账
     */
    @Excel(name = "系统SID-汇票台账")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-汇票台账")
    private Long huipiaoRecordSid;

    /**
     * 汇票使用日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "汇票使用日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "汇票使用日期")
    private Date useDate;

    /**
     * 使用金额
     */
    @Excel(name = "使用金额")
    @ApiModelProperty(value = "使用金额")
    private BigDecimal useAmount;

    /**
     * 接收方
     */
    @Excel(name = "接收方")
    @ApiModelProperty(value = "接收方")
    private String receiver;

    /**
     * 汇票使用说明
     */
    @Excel(name = "汇票使用说明")
    @ApiModelProperty(value = "汇票使用说明")
    private String useRemark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
