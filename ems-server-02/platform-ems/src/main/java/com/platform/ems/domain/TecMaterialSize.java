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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 商品尺寸对象 s_tec_material_size
 *
 * @author olive
 * @date 2021-02-21
 */
@TableName("s_tec_material_size")
@Data
@ApiModel
@Accessors( chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecMaterialSize  extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value ="租户id")
    private String clientId;

    @TableField(exist = false)
    @ApiModelProperty(value ="版型名称")
    private String modelName;

    @TableField(exist = false)
    @ApiModelProperty(value ="版型sid")
    private String modelSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="版型sid")
    private String[] modelSidList;

    /**
     * 系统ID-商品尺寸表信息
     */
    @Excel(name = "系统ID-商品尺寸表信息")
    @TableId
    @ApiModelProperty(value ="商品尺寸表信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSizeSid;

    /**
     * 系统ID-物料档案
     */
    @ApiModelProperty(value ="商品档案id")
    @NotNull(message = "商品档案不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @NotEmpty
    @ApiModelProperty(value ="上装基准尺码")
    private String standardSku;

    @ApiModelProperty(value ="下装基准尺码")
    private String downStandardSku;
    /**
     * 图片路径
     */
    @ApiModelProperty(value ="图片路径")
    @Excel(name = "图片路径")
    private String picturePath;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    @NotEmpty(message = "状态不能为空")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    @NotEmpty(message = "处理状态不能为空")
    private String handleStatus;

    @ApiModelProperty(value = "风格编码")
    @TableField(exist = false)
    private String style;
    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    @ApiModelProperty(value ="创建人账号")
    @TableField(fill = FieldFill.INSERT)
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value ="创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @Excel(name = "更新人")
    @ApiModelProperty(value ="更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    @Excel(name = "确认人")
    @ApiModelProperty(value ="确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value ="创建时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value ="更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    private String updaterAccount;

    /**
     * 更新时间
     */
    @ApiModelProperty(value ="更新时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    @TableField(exist = false)
    @ApiModelProperty(value ="商品档案sku列表")
    private List<BasMaterialSku> materialSkuList;
    /**
     * 确认人账号
     */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value ="确认人账号")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @ApiModelProperty(value ="确认时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @ApiModelProperty(value ="数据源系统")
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.UPDATE)
    private String dataSourceSys;

    @ApiModelProperty(value ="备注")
    @Excel(name = "备注")
    private String remark;

    @TableField(exist = false)
    @Excel(name = "商品档案编码")
    @ApiModelProperty(value ="商品档案编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="样衣号")
    @Excel(name = "样衣号")
    private String sampleCodeSelf;

    @TableField(exist = false)
    @ApiModelProperty(value ="商品名称")
    @Excel(name = "商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value ="设计师账号")
    @Excel(name = "设计师账号")
    private String designerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value ="设计师")
    @Excel(name = "设计师")
    private String designerAccountName;

    /** 上下装/套装（数据字典的键值） */
    @Excel(name = "上下装/套装（数据字典的键值）")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    @TableField(exist = false)
    private String upDownSuit;

    @TableField(exist = false)
    @Excel(name = "是否套装",dictType = "s_yesno_flag")
    @ApiModelProperty(value ="是否套装")
    private String isSuit;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value ="产品季id")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="产品季id")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value ="产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="产品季名称")
    @Excel(name = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value ="商品类型")
    @Excel(name = "商品类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value ="商品类型名称")
    @Excel(name = "商品类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="商品分类")
    private String materialClass;

    @TableField(exist = false)
    @ApiModelProperty(value ="分类名称")
    @Excel(name = "商品分类")
    private String className;

    @TableField(exist = false)
    @ApiModelProperty(value ="公司Sid")
    private String companySid;

    @TableField(exist = false)
    @ApiModelProperty(value ="公司Sid")
    private String[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value ="公司名称")
    @Excel(name = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value ="公司品牌名称")
    @Excel(name = "公司品牌名称")
    private String companyBrandName;

    @TableField(exist = false)
    @ApiModelProperty(value ="客户品牌名称")
    @Excel(name = "客户品牌名称")
    private String customerBrandName;

    @TableField(exist = false)
    @ApiModelProperty(value ="客方样衣号")
    private String sampleCodeCustomer;

    @TableField(exist = false)
    @ApiModelProperty(value ="客户品牌sid")
    private String customerBrandSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="客户品牌编码")
    @Excel(name = "客户品牌编码")
    private String customerBrandCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="版型部位名称")
    @Excel(name = "版型部位名称")
    private String modelPositionName;

    @TableField(exist = false)
    @ApiModelProperty(value ="生产工艺类型")
    @Excel(name = "生产工艺类型",dictType = "s_product_technique_type")
    private String productTechniqueType;

    @TableField(exist = false)
    @ApiModelProperty(value ="生产工艺类型名称")
    @Excel(name = "生产工艺类型")
    private String productTechniqueTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value ="风格")
    @Excel(name = "风格",dictType = "s_style")
    private String styleDetail;

    @TableField(exist = false)
    @ApiModelProperty(value ="客户名称")
    @Excel(name = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value ="客户sid")
    private String customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value ="客方品牌编码")
    @Excel(name = "客方品牌编码")
    private String customerProductCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="部位列表")
    @Valid
    private List<TecMaterialPosInfor> posInforList;

    @TableField(exist = false)
    @ApiModelProperty(value ="下装部位列表")
    @Valid
    private List<TecMaterialPosInforDown> posInforDownList;

    @TableField(exist = false)
    @ApiModelProperty(value ="Sid数组")
    private Long[] materialSizeSidList;

    @ApiModelProperty(value = "处理状态数组")
    @TableField(exist = false)
    private String[] handleStatusList;

    @TableField(exist = false)
    private String beginTime;
    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;


}
