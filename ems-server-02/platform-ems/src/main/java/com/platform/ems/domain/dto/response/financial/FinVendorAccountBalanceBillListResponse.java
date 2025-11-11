package com.platform.ems.domain.dto.response.financial;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 供应商互抵单列表返回实体 FinVendorAccountBalanceBillListResponse
 *
 * @author chenkaiwen
 * @date 2021-07-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinVendorAccountBalanceBillListResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "数据源系统（数据字典）")
    private String dataSourceSys;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商账互抵单")
    private Long accountBalanceBillSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] accountBalanceBillSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商账互抵单号 ")
    private Long accountBalanceBillCode;

    @ApiModelProperty(value = "公司档案")
    private String vendorName;

    @ApiModelProperty(value = "供应商档案")
    private String companyName;

    @ApiModelProperty(value = "产品季档案")
    private String productSeasonName;

    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

}
