package com.platform.ems.domain.dto.request;

import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/*
 SBFEIYAOHOUDUANGEIZHEGCANSHU、DUOCIYIJU
 */
@Data
@Accessors(chain = true)
public class PayProcessStepCompleteVerifyRequest {

    private List<PayProcessStepCompleteItem> newList;

    PayProcessStepComplete payProcessStepComplete;

}
