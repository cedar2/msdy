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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 常规辅料包-主对象 s_bas_material_package
 *
 * @author linhongwei
 * @date 2021-03-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_package")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialPackage extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-常规辅料包档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-常规辅料包档案")
    @TableId
    private Long materialPackageSid;

    public void setPackageCode(String packageCode) {
        if (StrUtil.isNotBlank(packageCode)) {
            packageCode = packageCode.replaceAll("\\s*", "");
        }
        this.packageCode = packageCode;
    }

    public void setPackageName(String packageName) {
        if (StrUtil.isNotBlank(packageName)) {
            packageName = packageName.trim();
        }
        this.packageName = packageName;
    }

    /**
     * 辅料包编码
     */
    @Excel(name = "物料包编码")
    @ApiModelProperty(value = "物料包编码")
    @NotNull(message = "编码不能为空")
    @Length(min = 1, max = 8, message = "编码最大长度为8")
    private String packageCode;

    /**
     * 辅料包名称
     */
    @NotNull(message = "名称不能为空")
    @Length(min = 1, max = 300, message = "名称最大长度为300")
    @Excel(name = "物料包名称")
    @ApiModelProperty(value = "物料包名称")
    private String packageName;

    @ApiModelProperty(value = " 物料包类别")
    private String packageCategory;

    /**
     * 产品季编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季编码")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季名称")
    @TableField(exist = false)
    @Excel(name = "产品季")
    private String productSeasonName;

    /**
     * 品牌编码
     */
    @ApiModelProperty(value = "品牌编码")
    private Long companyBrandSid;

    @ApiModelProperty(value = "公司品牌")
    @TableField(exist = false)
    private String companyBrandName;

    /**
     * 公司
     */
    @ApiModelProperty(value = "公司")
    private Long companySid;

    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    /**
     * 客户编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户编码")
    private Long customerSid;

    @ApiModelProperty(value = "客户名称")
    @TableField(exist = false)
    @Excel(name = "客户")
    private String customerName;

    @ApiModelProperty(value = "客户简称")
    @TableField(exist = false)
    private String customerShortName;

    /**
     * 客方品牌编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客方品牌编码")
    private Long customerBrandSid;

    @ApiModelProperty(value = "客方品牌名称")
    @TableField(exist = false)
    @Excel(name = "客方品牌")
    private String customerBrandName;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @Valid
    @ApiModelProperty(value = "明细表")
    @TableField(exist = false)
    List<BasMaterialPackageItem> listBasMaterialPackageItem;

    /*********************查询参数*********************/
    @ApiModelProperty(value = "创建日期起")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateStart;

    /**
     * 创建日期至
     */
    @ApiModelProperty(value = "创建日期至")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDateEnd;

    @ApiModelProperty(value = "查询：客户")
    @TableField(exist = false)
    private String[] customerSidList;

    @ApiModelProperty(value = "查询：产品季")
    @TableField(exist = false)
    private String[] productSeasonSidList;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private String[] companySidList;

    @ApiModelProperty(value = "查询：品牌")
    @TableField(exist = false)
    private String[] companyBrandSidList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：启用/停用")
    @TableField(exist = false)
    private String[] statusList;

    @ApiModelProperty(value = "查询：sku类型1")
    @TableField(exist = false)
    private String sku1Type;

    @ApiModelProperty(value = "查询：sku类型2")
    @TableField(exist = false)
    private String sku2Type;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "拉链标识")
    @TableField(exist = false)
    private String zipperFlag = "";

}
