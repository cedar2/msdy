package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 售后质量问题台账对象 s_qua_shouhou_record
 *
 * @author admin
 * @date 2024-03-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_qua_shouhou_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuaShouhouRecord extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-售后质量记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-售后质量记录")
    private Long shouhouRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] shouhouRecordSidList;

    /**
     * 售后质量记录编号
     */
    @Excel(name = "记录编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "售后质量记录编号")
    private Long shouhouRecordCode;

    /**
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    /**
     * 客户编码
     */
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 系统SID-商品
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品")
    private Long materialSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /**
     * 颜色
     */
    @Excel(name = "颜色")
    @ApiModelProperty(value = "颜色")
    private String colorName;

    /**
     * 退货类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "退货类型", dictType = "s_returns_type")
    @ApiModelProperty(value = "退货类型（数据字典的键值或配置档案的编码）")
    private String returnsType;

    @TableField(exist = false)
    @ApiModelProperty(value = "退货类型（数据字典的键值或配置档案的编码）")
    private String[] returnsTypeList;

    /**
     * 售后处理方式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "售后处理方式", dictType = "s_shouhou_type")
    @ApiModelProperty(value = "售后处理方式（数据字典的键值或配置档案的编码）")
    private String shouhouType;

    @TableField(exist = false)
    @ApiModelProperty(value = "售后处理方式（数据字典的键值或配置档案的编码）")
    private String[] shouhouTypeList;

    /**
     * 问题类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "问题类型", dictType = "s_defect_type")
    @ApiModelProperty(value = "问题类型（数据字典的键值或配置档案的编码）")
    private String defectType;

    @TableField(exist = false)
    @ApiModelProperty(value = "问题类型（数据字典的键值或配置档案的编码）")
    private String[] defectTypeList;

    /**
     * 问题描述
     */
    @Excel(name = "问题描述")
    @ApiModelProperty(value = "问题描述")
    private String defectDescription;

    /**
     * 解决状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "解决状态", dictType = "s_resolve_status")
    @ApiModelProperty(value = "解决状态（数据字典的键值或配置档案的编码）")
    private String resolveStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "解决状态（数据字典的键值或配置档案的编码）")
    private String[] resolveStatusList;

    /**
     * 处理结果
     */
    @Excel(name = "处理结果")
    @ApiModelProperty(value = "处理结果")
    private String solutionRemark;

    /**
     * 系统SID-产品季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    /**
     * 数量
     */
    @Excel(name = "数量", cellType = Excel.ColumnType.NUMERIC)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数量")
    private Long quantity;

    /**
     * 责任归属（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "责任归属方", dictType = "s_charger")
    @ApiModelProperty(value = "责任归属（数据字典的键值或配置档案的编码）")
    private String charger;

    @TableField(exist = false)
    @ApiModelProperty(value = "责任归属（数据字典的键值或配置档案的编码）")
    private String[] chargerList;

    /**
     * 系统SID-工厂
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 验货人
     */
    @Excel(name = "验货人")
    @ApiModelProperty(value = "验货人")
    private String inspector;

    /**
     * 验货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "验货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "验货日期")
    private Date inspectionDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "验货日期 起")
    private String inspectionDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "验货日期 止")
    private String inspectionDateEnd;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 售后图片路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "售后图片路径，可放多个链接，每个链接用”;“隔开")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "售后图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    /**
     * 售后视频路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "售后视频路径，可放多个链接，每个链接用”;“隔开")
    private String videoPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "售后视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
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
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
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
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<QuaShouhouRecordAttach> attachmentList;


}
