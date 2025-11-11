package com.platform.ems.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 仓库档案对象 s_bas_storehouse
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_storehouse")
public class BasStorehouse extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-仓库档案
     */
    @TableId
    @ApiModelProperty(value = "系统ID-仓库档案")
    private String storehouseSid;

    public void setStorehouseCode(String storehouseCode) {
        if (StrUtil.isNotBlank(storehouseCode)) {
            storehouseCode = storehouseCode.replaceAll("\\s*", "");
        }
        this.storehouseCode = storehouseCode;
    }

    public void setStorehouseName(String storehouseName) {
        if (StrUtil.isNotBlank(storehouseName)) {
            storehouseName = storehouseName.trim();
        }
        this.storehouseName = storehouseName;
    }

    /**
     * 仓库编码
     */
    @Excel(name = "仓库编码")
    @NotBlank(message = "仓库编码不能为空")
    @Length(max = 8, message = "仓库编码长度不能大于8位")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称")
    @NotBlank(message = "仓库名称不能为空")
    @Length(max = 300, message = "仓库编码长度不能大于300位")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "仓库模糊查询")
    private String storehouseCodeName;

    /**
     * 所属公司
     */
    @NotBlank(message = "请选择所属公司")
    @ApiModelProperty(value = "所属公司")
    private String companySid;

    /**
     * 所属公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "所属公司")
    @ApiModelProperty(value = "所属公司简称")
    private String companyShortName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid(多选)")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 仓库属性
     */
    @Excel(name = "仓库属性", dictType = "s_storehouse_attribute")
    @ApiModelProperty(value = "仓库属性")
    private String storehouseAttribute;

    /**
     * 是否虚拟仓
     */
    @Excel(name = "是否虚拟仓", dictType = "sys_yes_no")
    @NotBlank(message = "请选择是否虚拟仓")
    @ApiModelProperty(value = "是否虚拟仓")
    private String isVirtual;

    @Excel(name = "工厂简称")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 启用/停用仓库
     */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用仓库")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 仓库主管
     */
    @Length(max = 60, message = "仓库主管长度不能大于60位")
    @ApiModelProperty(value = "仓库主管")
    private String supervisor;

    @TableField(exist = false)
    @ApiModelProperty(value = "仓库主管(用户对应ID)")
    private Long supervisorId;

    @Excel(name = "仓库主管")
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库主管")
    private String supervisorName;

    /**
     * 所属供应商
     */
    @ApiModelProperty(value = "所属供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private String vendorSid;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "所属供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 所在区域
     */
    @Excel(name = "所属区域")
    @ApiModelProperty(value = "所在区域")
    private String countryRegion;

    /**
     * 仓库地址
     */
    @Excel(name = "仓库地址")
    @Length(max = 300, message = "仓库地址长度不能大于300位")
    @ApiModelProperty(value = "仓库地址")
    private String address;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    /**
     * 经营类型
     */
    @ApiModelProperty(value = "经营类型")
    private String businessType;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    @TableField(exist = false)
    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "页容量")
    private Integer pageSize;

    /**
     * 仓库属性list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库属性list")
    private String[] storehouseAttributeList;

    /**
     * 是否虚拟仓list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否虚拟仓list")
    private String[] isVirtualList;

    /**
     * 所属公司list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属公司list")
    private String[] companySidList;

    /**
     * 所属供应商list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属供应商list")
    private String[] vendorSidList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 系统ID-仓库档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-仓库档案")
    private String[] storehouseSidList;

    /**
     * 仓库-库位信息对象
     */
    @TableField(exist = false)
    @Valid
    @ApiModelProperty(value = "仓库-库位信息对象")
    private List<BasStorehouseLocation> basStorehouseLocationList;

    /**
     * 仓库-联系方式对象
     */
    @TableField(exist = false)
    @Valid
    @ApiModelProperty(value = "仓库-联系方式对象")
    private List<BasStorehouseAddr> addrList;

    @ApiModelProperty(value = "附件清单")
    @TableField(exist = false)
    private List<BasStorehouseAttach> basStorehouseAttachList;

    @ApiModelProperty(value = "库位货品标识（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String materialFlag;
}
