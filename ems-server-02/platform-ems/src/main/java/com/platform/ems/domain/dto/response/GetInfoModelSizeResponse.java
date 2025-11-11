package com.platform.ems.domain.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 版型档案
 *
 * @author olive
 */
@Data
public class GetInfoModelSizeResponse implements Serializable {
    private String modelPositionSizeSid;
    private String skuSid;
    private String sizeValue;
}
