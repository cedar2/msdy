package com.platform.ems.domain.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * @author olive
 */
@Data
public class GetInfoModelAttachmentResponse {
    private String modelAttachmentSid;
    private String fileType;
    private String fileName;
    private String remark;
    private String creatorCode;
    private Date creatorDate;
    private String updaterAccount;
    private Date updaterDate;
}
