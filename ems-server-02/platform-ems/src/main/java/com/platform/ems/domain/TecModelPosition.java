package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 版型部位档案对象 s_tec_model_position
 *
 * @author ChenPinzhen
 * @date 2021-01-25
")*/
@Data
@Accessors( chain = true)
@ApiModel
@TableName(value = "s_tec_model_position")
public class TecModelPosition extends EmsBaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @TableId
    @ApiModelProperty(value = "系统ID-版型部位档案")
    private String modelPositionSid;

    public void setModelPositionCode(String modelPositionCode) {
        if (StrUtil.isNotBlank(modelPositionCode)){
            modelPositionCode = modelPositionCode.replaceAll("\\s*", "");
        }
        this.modelPositionCode = modelPositionCode;
    }

    public void setModelPositionName(String modelPositionName) {
        if (StrUtil.isNotBlank(modelPositionName)){
            modelPositionName = modelPositionName.trim();
        }
        this.modelPositionName = modelPositionName;
    }

    @ApiModelProperty(value = "版型部位编码")
    @Length(min = 1, max = 8, message = "编码最大长度为8")
    @Excel(name = "版型部位编码")
    private String modelPositionCode;

    @ApiModelProperty(value = "版型部位名称")
    @Excel(name = "版型部位名称")
    private String modelPositionName;

    @ApiModelProperty(value = "版型部位类型编码")
    private String modelPositionType;

    @ApiModelProperty(value = "上下装/套装")
    @Excel(name = "上下装/套装",dictType = "s_up_down_suit")
    private String upDownSuit;

    @ApiModelProperty(value = "度量方法说明")
    @Excel(name = "度量方法说明")
    private String measureDescription;

    @ApiModelProperty(value = "客户档案sid")
    private String customerSid;

    @ApiModelProperty(value = "客户名字")
    @Excel(name = "客户")
    private String customerName;

    public void setCustomerPositionCode(String customerPositionCode) {
        if (StrUtil.isNotBlank(customerPositionCode)){
            customerPositionCode = customerPositionCode.replaceAll("\\s*", "");
        }
        this.customerPositionCode = customerPositionCode;
    }

    public void setCustomerPositionName(String customerPositionName) {
        if (StrUtil.isNotBlank(customerPositionName)){
            customerPositionName = customerPositionName.trim();
        }
        this.customerPositionName = customerPositionName;
    }

    @ApiModelProperty(value = "客方版型部位编码")
    @Excel(name = "客方部位编码")
    private String customerPositionCode;

    @ApiModelProperty(value = "客方版型部位名称")
    private String customerPositionName;

    @ApiModelProperty(value = "计量单位编码")
    private String unit;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用" ,dictType = "s_valid_flag")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "启用/停用状态,前端传 停用的：2，用来过滤停用的数据")
    private String statusNot;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态" , dictType = "s_handle_status")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    @ApiModelProperty(value = "确认时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    @ApiModelProperty(value = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-版型部位档案")
    private String[] modelPositionSidList;

    @ApiModelProperty(value = "处理状态数组")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "客户档案sid")
    private String[] customerSidList;

    @ApiModelProperty(value = "上下装/套装")
    private String[] upDownSuitList;

    /**
     * 版型部位编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "版型部位编码list")
    private List<String> modelPositionCodeList;

    /**
     * 版型部位名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "版型部位名称list")
    private List<String> modelPositionNameList;

    /**
     * 度量方法说明
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "度量方法说明list")
    private List<String> measureDescriptionList;
}
