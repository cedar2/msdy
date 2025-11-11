package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * 客户预收待收流水报表 FinRecordAdvanceReceiptFormResponse
 *
 * @author chenkaiwen
 * @date 2021-09-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinRecordAdvanceReceiptFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
}
