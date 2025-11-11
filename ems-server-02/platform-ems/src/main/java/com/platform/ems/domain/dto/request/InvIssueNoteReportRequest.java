package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 发货单明细报表对象请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvIssueNoteReportRequest extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "系统SID-发货单号")
    private String noteCode;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "出入库状态（数据字典的键值或配置档案的编码）")
    private String[] inOutStockStatusList;

    @ApiModelProperty(value = "查询：特殊库存")
    private String[] specialStockList;

    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：单据日期起")
    private String documentBeginTime;

    @ApiModelProperty(value = "查询：单据日期至")
    private String documentEndTime;

    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vendorSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> itemSidList;

    @ApiModelProperty(value = "查询：客户")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] customerSidList;

    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：仓库")
    private Long storehouseSid;

}
