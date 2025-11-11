package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import com.platform.ems.domain.BasMaterial;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料&商品&服务档案对象 s_bas_material
 *
 * @author linhongwei
 * @date 2021-01-21
")*/
@Data
@ApiModel
public class BasMaterialPosSearchResponse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户端口号")
    @NotBlank(message = "客户端口号不能为空") private String clientId;

    @ApiModelProperty(value = "系统ID-物料档案")
    @Excel(name = "系统ID-物料档案")
    @NotBlank(message = "系统ID-物料档案不能为空")
    private String materialSid;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "物料（商品/服务）编码")
    @NotBlank(message = "物料（商品/服务）编码不能为空")
    private String materialCode;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "物料名称")
    @NotBlank(message = "物料（商品/服务）名称不能为空")
    private String materialName;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "处理状态",readConverterExp = "1=保存,2=已提交,3=审核通过,4=已退回,5=已确认,6=已完成,7=已关闭,8=已作废")
    @NotBlank(message = "处理状态不能为空")
    private String handleStatus;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "SKU1类型编码")
    private String sku1Type;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "SKU2类型编码")
    private String sku2Type;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "启用/停用状态",readConverterExp="0=停用,1=正常,2=删除")
    @NotBlank(message = "启用/停用状态不能为空")
    private String status;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "幅宽") private String width;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "克重") private String gramWeight;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "规格") private String specification;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "材质") private String materialComposition;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "物料分类编码") private String materialType;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "物料分类")
    @NotBlank(message = "物料（商品/服务）分类编码不能为空")
    private String materialClass;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "供应商名称") private String vendorName;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "供方编码") private String supplierProductCode;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "报价(含税)") private BigDecimal quotePriceTax;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "报价(不含税)") private BigDecimal quotePrice;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "采购价(含税)") private BigDecimal purchasePriceTax;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "采购价(不含税)") private BigDecimal purchasePrice;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "税率")
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "图片路径") private String picturePath;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "备注") private String remark;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "创建人账号")
    @NotBlank(message = "创建人账号不能为空")
    private String creatorAccount;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "创建时间不能为空")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "更新人账号") private String updaterAccount;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @Excel(name = "确认人账号") private String confirmerAccount;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

}
