package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 员工工作状况报表 s_bas_staff
 *
 * @author chenkw
 * @date 2023-02-06
 */
@Data
@Accessors(chain = true)
@ApiModel
public class BasStaffConditionForm {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属公司sid")
    private Long companySid;

    @ApiModelProperty(value = "主属公司sid 多选")
    private Long[] companySidList;

    @ApiModelProperty(value = "主属公司编码")
    private String companyCode;

    @ApiModelProperty(value = "主属公司名称")
    private String companyName;

    @ApiModelProperty(value = "主属公司简称")
    private String companyShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属部门sid")
    private Long departmentSid;

    @ApiModelProperty(value = "主属部门sid 多选")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "主属部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "主属部门名称")
    private String departmentName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属岗位sid")
    private Long positionSid;

    @ApiModelProperty(value = "主属岗位sid 多选")
    private Long[] positionSidList;

    @ApiModelProperty(value = "主属岗位编码")
    private String positionCode;

    @ApiModelProperty(value = "主属岗位名称")
    private String positionName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户id")
    private Long[] userIdList;

    @ApiModelProperty(value = "租户id")
    private String clientId;

    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    @Excel(name = "用户昵称")
    @ApiModelProperty(value = "用户昵称")
    private String userNickName;

    @Excel(name = "已逾期项数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "已逾期项数")
    private BigDecimal overdueNum;

    @Excel(name = "即将到期项数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "即将到期项数")
    private BigDecimal toexpireNum;

    @Excel(name = "待办项数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待办项数")
    private BigDecimal dbNum;

    @Excel(name = "待批项数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "待批项数")
    private BigDecimal dpNum;

    @Excel(name = "员工姓名")
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @Excel(name = "员工号")
    @ApiModelProperty(value = "员工号")
    private String staffCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    public Integer getPageBegin() {
        if (pageSize != null && pageNum != null){
            return pageSize*(pageNum-1);
        }else {
            return pageBegin;
        }
    }

    public void setPageBegin(Integer pageBegin) {
        if (pageSize != null && pageNum != null){
            this.pageBegin = this.pageSize*(this.pageNum-1);
        }else {
            this.pageBegin = pageBegin;
        }
    }

}
