package com.platform.ems.domain.dto.request;

import lombok.Data;

@Data
public class EditHandleStatusRequest {
    private String productSeasonSid;
    private String handleStatus;
    private String updaterAccount;
    private String confirmerAccount;

}
