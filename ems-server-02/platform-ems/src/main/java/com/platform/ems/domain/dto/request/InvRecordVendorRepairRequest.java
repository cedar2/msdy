package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 供应商返修台账对象明细报表请求实体
 *
 * @author yangqz
 * @date 2021-12-1
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvRecordVendorRepairRequest {

    @Excel(name = "供应商返修台账流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商返修台账流水号")
    private Long vendorRepairCode;

    @ApiModelProperty(value = "查询：供应商")
    @TableField(exist = false)
    private Long[] vendorSidList;

    @ApiModelProperty(value = "查询：公司")
    @TableField(exist = false)
    private Long[] companySidList;

    @ApiModelProperty(value = "查询：处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value ="商品/物料名称")
    private String materialName;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value ="商品/物料编码")
    private String materialCode;

    @Excel(name = "退还状态")
    @ApiModelProperty(value = "退还状态")
    private String[] returnStatusList;

    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @ApiModelProperty(value = "计划退还日期起")
    private String planReturnDateBegin;

    @ApiModelProperty(value = "计划退还日期至")
    private String planReturnDateEnd;

    @ApiModelProperty(value ="创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    @TableField(exist = false)
    private Integer pageSize;
}
