package com.platform.ems.domain.dto.request.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 工资单明细报表 PaySalaryBillItemFormRequest
 *
 * @author chenkaiwen
 * @date 2022-08-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaySalaryBillItemFormRequest extends EmsBaseEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @ApiModelProperty(value = "公司sid（多选）")
    private Long[] companySidList;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @ApiModelProperty(value = "工厂sid（多选）")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    private Long staffSid;

    @ApiModelProperty(value = "系统SID-员工档案（多选）")
    private Long[] staffSidList;

    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @ApiModelProperty(value = "员工编号")
    private String staffCode;

    @ApiModelProperty(value = "工资单号")
    private String salaryBillCode;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

}
