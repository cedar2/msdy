package com.platform.ems.domain.dto.response;

import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel
@Data
public class PurOutsourcePurchasePriceResponse extends EmsBaseEntity {

    @ApiModelProperty(value = "生产订单工序")
    private List<ManManufactureOrderProcess> processList;

    @NotNull
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

}
