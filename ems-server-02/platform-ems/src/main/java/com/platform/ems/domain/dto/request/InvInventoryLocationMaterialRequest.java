package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.BasMaterial;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 样品添加 获取数量和库存价
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryLocationMaterialRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @ApiModelProperty(value = "选中行信息")
    private List<BasMaterial> BasMaterialList;

    @Excel(name = "特殊库存（数据字典的键值）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值）")
    private String specialStock;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid")
    private Long vendorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid")
    private Long customerSid;
}
