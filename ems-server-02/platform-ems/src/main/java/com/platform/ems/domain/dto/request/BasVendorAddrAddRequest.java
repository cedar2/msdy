package com.platform.ems.domain.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 新增客户联系方式
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class BasVendorAddrAddRequest {

    /** 档案sid  */
    private Long vendorSid;
    /** 创建者账号 */
    private String creatorAccount;
    private CustomerAddrRequest vendorAddrRequest;
}
