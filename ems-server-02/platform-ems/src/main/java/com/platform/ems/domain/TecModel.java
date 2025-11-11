package com.platform.ems.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 版型档案对象 s_tec_model
 *
 * @author olive
 * @date 2021-01-30
 */
@Data
@Accessors(chain = true)
@TableName("s_tec_model")
@ApiModel
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModel extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-版型档案
     */
    @TableId
    @ApiModelProperty(value = "系统ID-版型档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelSid;

    @ApiModelProperty(value = "版型档案id数组")
    @TableField(exist = false)
    private Long[] modelSidList;

    public void setModelCode(String modelCode) {
        if (StrUtil.isNotBlank(modelCode)) {
            modelCode = modelCode.replaceAll("\\s*", "");
        }
        this.modelCode = modelCode;
    }

    public void setModelName(String modelName) {
        if (StrUtil.isNotBlank(modelName)) {
            modelName = modelName.trim();
        }
        this.modelName = modelName;
    }

    /**
     * 版型编码
     */
    @ApiModelProperty(value = "版型编码")
    @NotNull(message = "编码不能为空")
    @Length(min = 1, max = 8, message = "编码最大长度为8")
    @Excel(name = "版型编码")
    private String modelCode;

    /**
     * 版型名称
     */
    @NotEmpty(message = "版型名称不能为空")
    @Excel(name = "版型名称")
    @Length(max = 300, message = "版型名称不能超过300个字符")
    @ApiModelProperty(value = "版型名称")
    private String modelName;

    /**
     * 版型类型编码
     */
    @Excel(name = "版型类型", dictType = "s_model_type")
    @ApiModelProperty(value = "版型类型编码")
    private String modelType;

    @Excel(name = "是否已建版型线用量", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否已建版型线用量")
    private String isCreateModelLine;

    /**
     * 上下装编码
     */
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装编码")
    private String upDownSuit;

    /**
     * 男女装标识编码
     */
    @Excel(name = "男女装", dictType = "s_male_female")
    @ApiModelProperty(value = "男女装标识编码")
    private String maleFemaleFlag;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "客方品牌")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户品牌名称")
    private String customerBrandName;

    public void setCustomerModelCode(String customerModelCode) {
        if (StrUtil.isNotBlank(customerModelCode)) {
            customerModelCode = customerModelCode.replaceAll("\\s*", "");
        }
        this.customerModelCode = customerModelCode;
    }

    @Excel(name = "客方版型编号")
    @Length(max = 8, message = "客户版型编码不能超过8个字符")
    @ApiModelProperty(value = "客方版型编码")
    private String customerModelCode;

    @Excel(name = "尺码组")
    @ApiModelProperty(value = "sku组名称")
    @TableField(exist = false)
    private String skuGroupName;

    @Excel(name = "基准尺码")
    @TableField(exist = false)
    @ApiModelProperty(value = "基准尺码名称")
    private String skuName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "查询：版型类型编码")
    @TableField(exist = false)
    private String[] modelTypeList;

    /**
     * 尺码组sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "尺码组sid")
    private Long skuGroupSid;

    @ApiModelProperty(value = "sku组编码")
    @TableField(exist = false)
    private String skuGroupCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "基准尺码编码")
    private String skuCode;

    /**
     * 基准尺码编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "基准尺码编码")
    private Long standardSku;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "尺码组sid")
    private Long downSkuGroupSid;

    @ApiModelProperty(value = "sku组名称")
    @TableField(exist = false)
    private String downSkuGroupName;

    @ApiModelProperty(value = "下装sku组编码")
    @TableField(exist = false)
    private String downSkuGroupCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "下装基准尺码名称")
    private String downSkuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "下装基准尺码编码")
    private String downSkuCode;

    /**
     * 基准尺码编码
     */
    @ApiModelProperty(value = "下装基准尺码编码")
    private Long downStandardSku;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "查询：上下装编码")
    @TableField(exist = false)
    private String[] upDownSuitList;

    /**
     * 客户编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户编码")
    private Long customerSid;

    @ApiModelProperty(value = "查询：客户")
    @TableField(exist = false)
    private String[] customerSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "男女装标识编码")
    @TableField(exist = false)
    private String[] maleFemaleFlagList;

    /**
     * 老少幼标识编码
     */
    @ApiModelProperty(value = "老少幼标识编码")
    private String oldYoungFlag;

    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    /**
     * 创建人账号
     */
    @ApiModelProperty(value = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师账号")
    private String designerAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @ApiModelProperty(value = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
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
     * 数据源系统
     */
    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    /**
     * 公司品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    /**
     * 客户品牌sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户品牌sid")
    private Long customerBrandSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件列表")
    private List<TecModelAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "部位信息列表")
    private List<TecModelPosInfor> posInforList;

    @TableField(exist = false)
    @ApiModelProperty(value = "下装部位信息列表")
    private List<TecModelPosInforDown> posInforDownList;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型-线部位对象")
    private List<TecModelLinePos> tecModelLinePosList;

}
