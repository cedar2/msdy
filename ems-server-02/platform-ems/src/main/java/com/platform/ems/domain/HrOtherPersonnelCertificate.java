package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 其它人事证明
 * @author xfzz
 * @date 2024/5/9
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_hr_other_personnel_certificate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrOtherPersonnelCertificate extends EmsBaseEntity implements Serializable {

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-其它人事证明
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-其它人事证明")
    private Long otherPersonnelCertificateSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] otherPersonnelCertificateSidList;

    /**
     * 其它人事证明信息sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "其它人事证明信息sids")
    private List<Long> otherPersonnelCertificateSids;

    /**
     * 其它人事证明记录号
     */
    @Excel(name = "其它人事证明记录号")
    @ApiModelProperty(value = "其它人事证明记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long otherPersonnelCertificateCode;

    /**
     * 系统SID-员工档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    private String staffSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工编号")
    private String staffCode;

    @TableField(exist = false)
    @Excel(name = "员工姓名")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @TableField(exist = false)
    @Excel(name = "文件类型(人事)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "文件类型(人事)（数据字典的键值或配置档案的编码）")
    private String documentType;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "文件类型(人事)列表")
    private String[] documentTypeList;

    /**
     * 系统SID-公司
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司档案sid")
    private Long[] companySidList;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @Excel(name = "部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @Excel(name = "岗位")
    @TableField(exist = false)
    @ApiModelProperty(value = "岗位")
    private String positionName;

    /**
     *  入职日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "入职日期")
    private Date checkInDate;

    /**
     * 电签平台签署ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "电签平台签署ID")
    private String esignId;

    @Excel(name = "签署状态",dictType = "s_esign_status")
    @ApiModelProperty(value = "电签平台签署状态")
    private String esignStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "电签平台签署状态列表")
    private String[] esignStatusList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "电签平台签署状态说明")
    private String esignDesc;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 签收状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "签收状态",dictType = "s_sign_status")
    @ApiModelProperty(value = "签收状态")
    private String signInStatus;

    /**
     * 电签平台流程完结时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "电签平台流程完结时间")
    private Date esignEndTime;


    /**
     * 电签平台（数据字典的键值或配置档案的编码）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "电签平台")
    private String esignPlatform;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "处理状态",dictType = "s_handle_status")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<HrOtherPersonnelCertificateAttach> attachmentList;
}
