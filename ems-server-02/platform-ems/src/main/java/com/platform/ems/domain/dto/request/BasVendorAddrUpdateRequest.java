package com.platform.ems.domain.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 编辑客户联系方式
 * @author chr
 * @date 2021-01-31
 */
@Data
@Accessors( chain = true)
public class BasVendorAddrUpdateRequest {

    /** 档案sid  */
    private Long vendorSid;
    /** 联系方式sid */
    private Long customerContactSid;
    /** 创建者账号 */
    private String updaterAccount;
    private CustomerAddrRequest vendorAddrRequest;
}
