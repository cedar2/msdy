package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务导出实体
 *
 * @author yangqz
 * @date 2021-7-14
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialSResponse implements Serializable {

    @Excel(name = "服务编码")
    @ApiModelProperty(value = "服务（商品/服务）编码")
    private String materialCode;

    @Excel(name = "服务名称")
    @ApiModelProperty(value = "服务（商品/服务）名称")
    private String materialName;

    @Excel(name = "服务类型")
    @ApiModelProperty(value = "商品类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @Excel(name = "类型")
    @ApiModelProperty(value = "类型")
    private String materialType;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用状态" ,dictType = "s_valid_flag")
    private String status;


    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

}
