package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 甲供料结算单明细报表对象请求实体
 *
 * @author yangqz
 * @date 2021-12-1
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvOwnerMaterialSettleRequest {

    @Excel(name = "甲供料结算单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "甲供料结算单号")
    private Long settleCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vendorSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @ApiModelProperty(value = "查询：特殊库存")
    @TableField(exist = false)
    private String[] specialStockList;

    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value ="创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;
}
