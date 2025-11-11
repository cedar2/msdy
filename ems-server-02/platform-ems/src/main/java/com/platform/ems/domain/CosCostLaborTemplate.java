package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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


/**
 * 商品成本核算-工价成本模板-主对象
 *
 * @author qhq
 * @date 2021-04-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_cost_labor_template")
public class CosCostLaborTemplate extends EmsBaseEntity {
    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-物料成本核算模板（主表）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料成本核算模板（主表）")
    private Long costLaborTemplateSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] costLaborTemplateSidList;
    /**
     * 公司编码（公司档案的sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long companySid;

    /**
     * 物料（商品/服务）类型编码
     */
    @ApiModelProperty(value = "物料（商品/服务）类型编码")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）类型编码")
    private String[] materialTypeList;

    /**
     * 模板編碼
     */
    @Excel(name = "工价模板编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工价模板编码")
    private String costLaborTemplateCode;

    /**
     * 物料成本核算模板名称
     */
    @NotEmpty(message = "名称不可为空")
    @Excel(name = "工价模板名称")
    @ApiModelProperty(value = "物料成本核算模板名称")
    private String costLaborTemplateName;

    /**
     * 生产工艺类型(编织方法)（数据字典的键值）
     */
    @ApiModelProperty(value = "生产工艺类型(编织方法)")
    private String productTechniqueType;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产工艺类型")
    private String[] productTechniqueTypeList;

    /**
     * 生产工艺类型(编织方法)（数据字典的键值）
     */
    @Excel(name = "生产工艺类型")
    @ApiModelProperty(value = "生产工艺类型(编织方法)名称")
    @TableField(exist = false)
    private String productTechniqueTypeName;

    /**
     * 价格录入方式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "价格录入方式", dictType = "s_price_enter_mode")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值或配置档案的编码）")
    private String priceEnterMode;

    /**
     * 成本组织（数据字典的键值）
     */
    @ApiModelProperty(value = "成本组织（数据字典的键值）")
    private String costOrg;

    @Excel(name = "业务类型", dictType = "s_cost_business_type")
    @NotBlank(message = "业务类型不能为空")
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型（查询多选）")
    private String[] businessTypeList;


    /**
     * 物料（商品/服务）类型编码
     */
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料（商品/服务）类型名称")
    @TableField(exist = false)
    private String materialTypeName;

    /**
     * 公司编码（公司档案的sid）
     */
    @Excel(name = "公司")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码名称（公司档案的sid）")
    @TableField(exist = false)
    private String companyName;


    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

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
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateDate;

    /**
     * 确认人
     */
    @ApiModelProperty(value = "确认人")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 确认日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmerDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    @TableField(exist = false)
    @ApiModelProperty(value = "工价项明细列表")
    private List<CosCostLaborTemplateItem> itemList;

}
