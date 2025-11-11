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
import com.platform.ems.domain.dto.request.BasBomColorRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

/**
 * 物料清单（BOM）主对象 s_tec_bom_head
 *
 * @author qhq
 * @date 2021-03-15
 */

@Data
@ApiModel
@Accessors(chain = true)
@TableName(value = "s_tec_bom_head")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecBomHead  extends EmsBaseEntity {

	/** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-物料BOM档案 */
    @Excel(name = "系统ID-物料BOM档案")
    @ApiModelProperty(value = "系统ID-商品BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long bomSid;

    /** 物料编码 */
    @ApiModelProperty(value = "商品编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    /** bom导出 */
    @ApiModelProperty(value = "bom导出")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long[] materialSids;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    /** 我司样衣号 */
    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    /** 物料SKU1档案 */
    @Excel(name = "物料SKU1档案")
    @ApiModelProperty(value = "物料SKU1档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1编码")
    private String sku1Code;

    /** 物料SKU2档案 */
    @Excel(name = "物料SKU2档案")
    @ApiModelProperty(value = "物料SKU2档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku2Sid;

    /** BOM用途 */
    @Excel(name = "BOM用途")
    @ApiModelProperty(value = "BOM用途")
    private String bomUsage;

    /** BOM状态 */
    @Excel(name = "BOM状态")
    @ApiModelProperty(value = "BOM状态")
    private String bomStatus;

    /** BOM类型 */
    @Excel(name = "BOM类型")
    @ApiModelProperty(value = "BOM类型")
    private String bomType;

    /** BOM版本号 */
    @Excel(name = "BOM版本号")
    @ApiModelProperty(value = "BOM版本号")
    private String bomVersionId;

    /** BOM版本号（上一版本） */
    @Excel(name = "BOM版本号（上一版本）")
    @ApiModelProperty(value = "BOM版本号（上一版本）")
    private String bomVersionIdPre;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案编码")
    private String materialCode ;

    /** 附件list */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件list")
    private List<TecBomAttachment> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品附件list")
    private List<BasMaterialAttachment> attachmentMaterialList;

    /** 组件清单对象list */
    @TableField(exist = false)
    @ApiModelProperty(value = "组件清单对象list")
    private List<TecBomItem> itemList;

    /** 商品详情 */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品详情")
    private BasMaterial material;

    /** sids 用于批量操作 */
    @TableField(exist = false)
    @ApiModelProperty(value = "sids 用于批量操作")
    private List<Long> bomSids;

    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "图片路径")
    @TableField(exist = false)
    private String picturePath;

    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    private String beginTime;

    @TableField(exist = false)
    private String endTime;

    private String remark;

    @ApiModelProperty(value = "流程状态：1（待填充数据）,2（待确认）,3（结束）")
    private String processType;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前用户是否具有流程权限")
    private boolean isJurisdictionByUser;

    @ApiModelProperty(value = "流程ID")
    private String instanceId;

    @ApiModelProperty(value = "商品编码sids")
    @TableField(exist = false)
    private List<Long> materialSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品类型")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private String vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供方样衣号")
    private String sampleCodeVendor;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品分类")
    private String materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品分类")
    private String materialClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师账号")
    private String designerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：产品季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    private Long[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：版型")
    private Long[] modelSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：客方商品编码")
    private String customerProductCode;

    @ApiModelProperty(value = "基准尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long standardSku;

    @ApiModelProperty(value = "基准尺码")
    @TableField(exist = false)
    private String standardSkuName;

    /** 客户（定制）sid） */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户（定制）sid）")
    @TableField(exist = false)
    private Long customerSid;

    /** 客户名称 */
    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /** 公司编码（公司档案的sid） */
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    /** 公司名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /** 系统ID-产品季档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-产品季档案")
    private Long productSeasonSid;

    /** 产品季名称 */
    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "我司样衣号（初始）")
    @ApiModelProperty(value = "我司样衣号（初始）")
    @TableField(exist = false)
    private String sampleCodeSelfInitial;

    @Excel(name = "上下装/套装（数据字典的键值）")
    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    @TableField(exist = false)
    @Length(max = 30,message = "客方样衣号不能超过30个字符")
    private String sampleCodeCustomer;

    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    @TableField(exist = false)
    @Length(max = 20,message = "快速编码不能超过20个字符")
    private String simpleCode;

    @Excel(name = "男女装标识")
    @ApiModelProperty(value = "男女装标识")
    @TableField(exist = false)
    private String maleFemaleFlag;

    @TableField(exist = false)
    private String productTechniqueType;

    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    @TableField(exist = false)
    private String style;

    @Excel(name = "款式", dictType = "s_kuan_type")
    @ApiModelProperty(value = "款式编码")
    @TableField(exist = false)
    private String kuanType;

    @Excel(name = "生产工艺类型名称")
    @ApiModelProperty(value = "生产工艺类型")
    @TableField(exist = false)
    private String productTechniqueTypeName;

    @Excel(name = "研产销阶段")
    @ApiModelProperty(value = "研产销阶段")
    @TableField(exist = false)
    private String cycleStage;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "物料类别编码")
    @TableField(exist = false)
    private String materialCategory;

    @ApiModelProperty(value = "物料类别编码")
    @TableField(exist = false)
    private String[] materialCategoryList;

    @Excel(name = "版型")
    @ApiModelProperty(value = "版型")
    @TableField(exist = false)
    private String modelName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    @TableField(exist = false)
    private String companyBrandName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    /** 当前审批节点名称 */
    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /** 当前审批人 */
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /** 提交人 */
    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;


    @ApiModelProperty(value = "采购类型")
    @TableField(exist = false)
    private String purchaseType;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询上下装/套装")
    private String[] upDownSuitList;

    @Excel(name = "sku颜色")
    @TableField(exist = false)
    private List<BasBomColorRequest> SkuSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @ApiModelProperty(value = "sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批节点")
    private String taskCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批节点")
    private String[] taskCodeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否跳过校验")
    private String isSkipJudge;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否跳过校验")
    private String isSkipSatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "BOM用途")
    private String bomUsageName;
}
