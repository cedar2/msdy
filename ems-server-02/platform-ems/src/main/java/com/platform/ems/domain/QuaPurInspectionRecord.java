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
import org.hibernate.validator.constraints.Length;

/**
 * 采购验货问题台账对象 s_qua_pur_inspection_record
 *
 * @author cedar2
 * @date 2024-09-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_qua_pur_inspection_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuaPurInspectionRecord extends EmsBaseEntity{
    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购验货记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购验货记录")
    private Long purInspectionRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] purInspectionRecordSidList;

    /**
     * 采购验货记录编号
     */
    @Excel(name = "记录编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购验货记录编号")
    private Long purInspectionRecordCode;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    /**
     * 供应商编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码")
    private Long vendorCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "供应商")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 系统SID-公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 系统SID-商品
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品/物料")
    private Long materialSid;

    /**
     * 商品编码
     */
    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "商品/物料编码")
    private String materialCode;

    /**
     * 商品名称
     */
    @TableField(exist = false)
    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "商品/物料名称")
    private String materialName;

    /**
     * 验货方式（s_inspection_method）
     */
    @Excel(name = "验货方式", dictType = "s_inspection_method")
    @ApiModelProperty(value = "验货方式（数据字典的键值或配置档案的编码）")
    private String inspectionMethod;

    @TableField(exist = false)
    @ApiModelProperty(value = "验货方式（数据字典的键值或配置档案的编码）")
    private String[] inspectionMethodList;

    /**
     * 验货方类型（s_inspection_partner_type）
     */
    @Excel(name = "验货方类型", dictType = "s_inspection_partner_type")
    @ApiModelProperty(value = "验货方类型（数据字典的键值或配置档案的编码）")
    private String inspectionPartnerType;

    /**
     * 验货结果（s_inspection_result）
     */
    @Excel(name = "验货结果", dictType = "s_inspection_result")
    @ApiModelProperty(value = "验货结果（数据字典的键值或配置档案的编码）")
    private String result;

    @TableField(exist = false)
    @ApiModelProperty(value = "验货结果（数据字典的键值或配置档案的编码）")
    private String[] resultList;

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
     * 解决说明
     */
    @Excel(name = "解决说明")
    @ApiModelProperty(value = "解决说明")
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
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonName;

    /**
     * 验货批次
     */
    @Excel(name = "验货批次")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "验货批次")
    private Long inspectionBatch;

    /**
     * 验货数量
     */
    @Excel(name = "验货数量", cellType = Excel.ColumnType.NUMERIC)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "验货数量")
    private Long quantity;

    /**
     * 不合格数量
     */
    @Excel(name = "不合格数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "不合格数量")
    private Long bhgQuantity;

    /**
     * 重验次数
     */
    @Excel(name = "重验次数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "重验次数")
    private Long repeatInspectionNum;

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
     * 验货说明
     */
    @Excel(name = "验货说明")
    @ApiModelProperty(value = "验货说明")
    private String inspectionRemark;

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
     * 采购订单号
     */
    @Excel(name = "采购订单号")
    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    /**
     * 采购合同号
     */
    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String purchaseContractCode;

    /**
     * 货运方（承运商）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "货运方（承运商）")
    private String carrier;

    /**
     * 货运方名称
     */

    @Excel(name = "承运方")
    @ApiModelProperty(value = "承运方名称")
    private String carrierName;

    /**
     * 货运方编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "承运方编码")
    private String carrierCode;

    /**
     * 货运单号
     */
    @Length(max = 20, message = "货运单号长度不能超过20位字符")
    @Excel(name = "货运单号")
    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    /**
     * 司机
     */
    @Excel(name = "司机")
    @ApiModelProperty(value = "司机")
    private String driver;

    /**
     * 联系电话(司机)
     */
    @Excel(name = "联系电话(司机)")
    @Length(max = 60, message = "电话不能超过60位")
    @ApiModelProperty(value = "联系电话(司机)")
    private String driverPhone;

    /**
    * 到货日期
    * */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "到货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "到货日期")
    private Date arrivalDate;

    @ApiModelProperty(value = "到货日期(起)")
    @TableField(exist = false)
    private String arrivalDateStart;

    @ApiModelProperty(value = "到货日期(至)")
    @TableField(exist = false)
    private String arrivalDateEnd;

    /**
     * 验货图片路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "验货图片路径，可放多个链接，每个链接用”;“隔开")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "验货图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    /**
     * 验货视频路径，可放多个链接，每个链接用”;“隔开
     */
    @ApiModelProperty(value = "验货视频路径，可放多个链接，每个链接用”;“隔开")
    private String videoPath;

    @TableField(exist = false)
    @ApiModelProperty(value = "验货视频路径，可放多个链接，每个链接用”;“隔开")
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
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
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

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
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

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
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
    private List<QuaPurInspectionRecordAttach> attachmentList;

}
