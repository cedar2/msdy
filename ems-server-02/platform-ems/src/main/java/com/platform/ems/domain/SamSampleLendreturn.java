package com.platform.ems.domain;

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

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 样品借还单-主对象 s_sam_sample_lendreturn
 *
 * @author linhongwei
 * @date 2021-12-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sam_sample_lendreturn")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SamSampleLendreturn extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-样品借还单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品借还单")
    private Long lendreturnSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] lendreturnSidList;
    /**
     * 样品借还单号
     */
    @Excel(name = "样品借还单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品借还单号")
    private Long lendreturnCode;

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 单据类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "单据类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "单据类型（数据字典的键值或配置档案的编码）")
    private String documentType;


    /**
     * 仓库sid
     */
    @Excel(name = "仓库sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "仓库sid")
    private Long storehouseSid;

    /**
     * 库位sid
     */
    @Excel(name = "库位sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库位sid")
    private Long storehouseLocationSid;

    /**
     * 借出人（用户账号）
     */
    @Excel(name = "借出人（用户账号）")
    @ApiModelProperty(value = "借出人（用户账号）")
    private String lender;

    @TableField(exist = false)
    @ApiModelProperty(value = "借出人（用户账号）")
    private String lenderName;

    /**
     * 归还人（用户账号）
     */
    @Excel(name = "归还人（用户账号）")
    @ApiModelProperty(value = "归还人（用户账号）")
    private String returner;

    @TableField(exist = false)
    @ApiModelProperty(value = "借出人（用户账号）")
    private String returnerName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更改人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    List<SamSampleLendreturnItem>  listSamSampleLendreturnItem;


    @TableField(exist = false)
    List<SamSampleLendreturnAttach>  listSamSampleLendreturnAttach;

    @ApiModelProperty(value = "查询：单据类型")
    @TableField(exist = false)
    private String[] documentTypeList;

    @ApiModelProperty(value = "查询: 处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询: 供应商")
    @TableField(exist = false)
    private String[] vendorSidList;

    @ApiModelProperty(value = "查询: 客户")
    @TableField(exist = false)
    private String[] customerSidList;

    @ApiModelProperty(value = "查询:库位")
    @TableField(exist = false)
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：关联业务单号")
    @Excel(name = "关联业务单号")
    @TableField(exist = false)
    private Long referLendreturnCode;

    @ApiModelProperty(value = "单据日期-起")
    @TableField(exist = false)
    private String documentDateBegin;

    @ApiModelProperty(value = "单据日期-至")
    @TableField(exist = false)
    private String documentDateEnd;

    @ApiModelProperty(value = "仓库")
    @TableField(exist = false)
    private String storehouseName;

    @ApiModelProperty(value = "库位")
    @TableField(exist = false)
    private String locationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @ApiModelProperty(value = "样品名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "我司样衣号")
    @TableField(exist = false)
    private String sampleCodeSelf;
}
