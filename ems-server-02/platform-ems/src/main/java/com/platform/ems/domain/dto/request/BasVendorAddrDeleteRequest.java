package com.platform.ems.domain.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 删除客户联系方式
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class BasVendorAddrDeleteRequest {

    /** 要删除的供应商联系方式sId */
    @NotBlank(message = "字段为必填项，不能为空")
    private String vendorAddrSids;
}
