package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 库存预留对象明细报表请求实体
 *
 * @author yangqz
 * @date 2021-12-1
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvReserveInventoryRequest {

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联业务单号")
    private Long businessOrderCode;

    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：客户")
    private Long[] customerSidList;

    @ApiModelProperty(value = "查询：库位")
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：特殊库存")
    private String[] specialStockList;

    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;

}
