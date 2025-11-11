package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品导出 BasMaterialSpExportResponse
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSpExportResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "商品类型")
    @ApiModelProperty(value = "商品类型名称（商品/商品/服务）")
    private String materialTypeName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "商品分类")
    @ApiModelProperty(value = "商品分类名称（商品/商品/服务）")
    private String materialClassName;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @Excel(name = "设计师")
    @ApiModelProperty(value = "设计师")
    private String designerAccountName;

    @Excel(name = "工艺单是否上传",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "生产制造单附件是否上传（数据字典的键值或配置档案的编码）")
    private String zhizaodanUploadStatus;

    @Excel(name = "工艺单更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生产制造单附件最新上传时间(保存时分秒)")
    private Date zhizaodanUploadDate;

    @Excel(name = "版型名称")
    @ApiModelProperty(value = "版型名称")
    private String modelName;

    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客方商品编码")
    @ApiModelProperty(value = "客方编码（物料/商品/服务）")
    private String customerProductCode;

    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @Excel(name = "生产工艺类型", dictType = "s_product_technique_typ")
    @ApiModelProperty(value = "生产工艺类型(编织方法)名称")
    private String productTechniqueTypeName;

    @Excel(name = "男女装", dictType = "s_suit_gender")
    @ApiModelProperty(value = "男女装标识")
    private String maleFemaleFlag;

    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    private String style;

    @Excel(name = "款式", dictType = "s_kuan_type")
    @ApiModelProperty(value = "款式编码")
    private String kuanType;

    @Excel(name = "研产销阶段")
    @ApiModelProperty(value = "研产销阶段编码")
    private String cycleStage;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "公司品牌名称")
    private String companyBrandName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "是否创建BOM",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建BOM")
    private String isCreateBom;

    @Excel(name = "BOM处理状态")
    @ApiModelProperty(value = "bom处理状态")
    private String bomHandleStatus;

    @Excel(name = "是否创建产前成本核算",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建产前成本核算")
    private String isCreateProductcost;

    @Excel(name = "产前成本核算处理状态")
    @ApiModelProperty(value = "产前成本核算处理状态")
    private String costHandleStatus;

    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "是否创建商品线用量",dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否创建商品线用量")
    private String isCreateProductLine;

    @Excel(name = "我方跟单员")
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Excel(name = "供方业务员")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    @Excel(name = "客方业务员")
    @ApiModelProperty(value = "客方业务员")
    private String buOperatorCustomer;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
