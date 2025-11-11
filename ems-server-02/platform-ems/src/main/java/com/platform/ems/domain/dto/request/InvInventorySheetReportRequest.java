package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 盘点单明细报表对象请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventorySheetReportRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "盘点单号")
    private String inventorySheetCode;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "是否冻结出入库作业（数据字典的键值）")
    private String isFreezeStock;

    @ApiModelProperty(value = "是否记录账面库存（数据字典的键值）")
    private String isQuantityRecord;

    @ApiModelProperty(value = "参考单号")
    private String countTaskDocument;

    @ApiModelProperty(value = "盘点状态（数据字典的键值）")
    private String[] countStatusList;

    @ApiModelProperty(value = "盘点过账人（用户名称）")
    private String accountor;

    @ApiModelProperty(value = "查询：特殊库存")
    private String[] specialStockList;

    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：过账日期(盘点过账日期)起")
    private String accountDateBeginTime;

    @ApiModelProperty(value = "查询：过账日期(盘点过账日期)至")
    private String accountDateEndTime;

    @ApiModelProperty(value = "查询：计划盘点日期起")
    private String planCountDateBeginTime;

    @ApiModelProperty(value = "查询：计划盘点日期至")
    private String planCountDateEndTime;

    @ApiModelProperty(value = "查询：处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：盘点结果")
    private String[] stockCountResultList;

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

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;
}
