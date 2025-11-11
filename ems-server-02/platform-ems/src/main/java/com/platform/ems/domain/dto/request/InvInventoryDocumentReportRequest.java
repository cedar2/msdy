package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 库存凭证/甲供料结算单 报表对象请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentReportRequest extends EmsBaseEntity implements Serializable {


    @ApiModelProperty(value = "所勾选的明细行")
    private Long[] inventoryDocumentItemSidList;

    @ApiModelProperty(value = "库存凭证号")
    private String inventoryDocumentCode;

    @ApiModelProperty(value = "公司（多选）")
    private Long[] companySidList;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "出入库状态（数据字典的键值或配置档案的编码）")
    private String inOutStockStatus;

    @ApiModelProperty(value = "查询：特殊库存")
    private String[] specialStockList;

    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：盘点结果查询")
    private String[] stockCountResultList;

    @ApiModelProperty(value = "查询：过账日期")
    private String accountDate;

    @ApiModelProperty(value = "查询：过账日期起")
    private String accountDateBeginTime;

    @ApiModelProperty(value = "查询：过账日期至")
    private String accountDateEndTime;

    @ApiModelProperty(value = "查询：库存凭证类别")
    private String documentCategory;

    @ApiModelProperty(value = "出入库操作人（用户名称）")
    private String[] storehouseOperatorList;

    @ApiModelProperty(value = "查询：单据类别")
    private String[] referDocCategoryList;

    @ApiModelProperty(value = "查询：关联业务单号")
    private String referDocumentCode;

    @ApiModelProperty(value = "查询：处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：处理状态")
    private String[] documentCategoryList;

    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：客户")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] customerSidList;

    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：仓库")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：目的仓库")
    private Long destStorehouseSid;

    @ApiModelProperty(value = "查询：目的库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] destStorehouseLocationSidList;

    @ApiModelProperty(value = "商品编码备注(适用于物料对应的商品)，如合格证、洗唛；可以保存多个")
    private String productCodes;

    @ApiModelProperty(value = "其它出入库对应的业务标识（数据字典的键值或配置档案的编码）")
    private String businessFlag;

    @ApiModelProperty(value = "其它出入库对应的业务标识（数据字典的键值或配置档案的编码）")
    private String[] businessFlagList;

    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String businessType;

    @ApiModelProperty(value = "其它出入库所属业务类型（数据字典的键值或配置档案的编码）")
    private String[] businessTypeList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季sid")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "供方送货单号（供方交货单号）")
    private String supplierDeliveryCode;

    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @ApiModelProperty(value = "查询：sku1(多选)")
    private Long[] sku1SidList;

    @ApiModelProperty(value = "查询：sku2(多选)")
    private Long[] sku2SidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：sku1")
    private Long sku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：sku1")
    private Long sku2Sid;

    @ApiModelProperty(value = "查询：sku1")
    private String sku1Name;

    @ApiModelProperty(value = "查询：sku1")
    private String sku2Name;

}
