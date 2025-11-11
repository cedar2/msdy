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
public class GetModelSystemResponse implements Serializable {
    private GetInfoModelSystemResponse modelSystemResponse;
    private List<GetInfoModelAttachmentResponse> modelAttachmentResponseList;
    private List<GetInfoModelInfoResponse> modelInfoResponseList;
}
