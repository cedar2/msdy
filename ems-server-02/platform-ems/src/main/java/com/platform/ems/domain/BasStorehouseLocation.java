package com.platform.ems.domain;

import java.util.Date;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 仓库-库位信息对象 s_bas_storehouse_location
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_storehouse_location")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasStorehouseLocation extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-仓库库位信息 */
    @TableId
    @ApiModelProperty(value = "系统ID-仓库库位信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storehouseLocationSid;

    @TableField(exist = false)
    private Long[] storehouseLocationSidList;

    /** 系统ID-仓库档案 */
    @ApiModelProperty(value = "系统ID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long storehouseSid;

    public void setLocationCode(String locationCode) {
        if (StrUtil.isNotBlank(locationCode)){
            locationCode = locationCode.replaceAll("\\s*", "");
        }
        this.locationCode = locationCode;
    }

    public void setLocationName(String locationName) {
        if (StrUtil.isNotBlank(locationName)){
            locationName = locationName.trim();
        }
        this.locationName = locationName;
    }

    @TableField(exist = false)
    @Excel(name = "仓库编码")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @TableField(exist = false)
    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    /** 库位编码 */
    @Excel(name = "库位编码")
    @NotBlank(message = "库位编码不能为空")
    @Length(max = 8, message = "库位编码长度不能大于8位")
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @ApiModelProperty(value = "外围系统仓库编码(WMS)")
    private String otherSystemStorehouseCode;

    /** 库位名称 */
    @Excel(name = "库位名称")
    @NotBlank(message = "库位名称不能为空")
    @Length(max = 300, message = "库位名称长度不能大于300位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @ApiModelProperty(value = "库位货品标识（数据字典的键值或配置档案的编码）")
    private String materialFlag;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    /** 是否虚拟库 */
    @Excel(name = "是否虚拟库", dictType = "s_yesno_flag")
    @NotBlank(message = "请选择是否虚拟库")
    @ApiModelProperty(value = "是否虚拟库")
    private String isVirtual;

    @NotBlank(message = "请选择库位的可用类型")
    @ApiModelProperty(value = "库存可用类型（数据字典键值）")
    private String usableType;

    /** 库位管理员 */
    @Length(max = 60, message = "库位名称长度不能大于60位")
    @ApiModelProperty(value = "库位管理员")
    private String administrator;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位管理员")
    private String[] administratorList;

    @TableField(exist = false)
    @Excel(name = "库位管理员")
    @ApiModelProperty(value = "库位管理员")
    private String administratorName;

    /** 库位地址 */
    @Excel(name = "库位地址")
    @Length(max = 300, message = "库位地址长度不能大于300位")
    @ApiModelProperty(value = "库位地址")
    private String address;

    /** 是否WM库 */
    @ApiModelProperty(value = "是否WM库")
    private String isWm;


    /** 库位类型 */
    @ApiModelProperty(value = "库位类型")
    private String locationType;

    /** 备注 */
    @Excel(name = "库位备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 创建人账号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人名称")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "库存类型")
    private String stockType;


}
