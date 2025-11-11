package com.platform.ems.domain.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 版型档案
 *
 * @author olive
 */
@Data
public class GetInfoModelInfoResponse implements Serializable {
    private TecModelPositionResponse modelPositionResponse;
    private List<GetInfoModelSizeResponse> sizeResponse;
    private String serialNum;
    private String modelPositionSid;
    private String deviation;
    private Integer unit;
    private String remark;
}
