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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 劳动合同
 * @author xfzz
 * @date 2024/5/7
 */

@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_hr_labor_contract")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrLaborContract extends EmsBaseEntity implements Serializable {

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
     * 系统SID-劳动合同
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-劳动合同")
    private Long laborContractSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] laborContractSidList;

    /**
     * 劳动合同信息sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "劳动合同信息sids")
    private List<Long> laborContractSids;

    /**
     * 劳动合同记录号
     */
    @Excel(name = "合同记录号")
    @ApiModelProperty(value = "合同记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long laborContractNum;

    /**
     * 员工姓名
     */
    @NotNull(message = "员工姓名不能为空")
    @Excel(name = "员工姓名")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    /**
     * 员工编号
     */
    @Excel(name = "员工编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工编号")
    private String staffCode;


    /**
     * 系统SID-公司
     */
    @NotNull(message = "公司不能为空")
    @ApiModelProperty(value = "系统SID-公司")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司档案sid")
    private Long[] companySidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    /**
     * 有效期(起)
     */
    @NotNull(message = "有效期(起)不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期(起)")
    private Date startDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String contractStartBeginTime;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String contractStartEndTime;

    /**
     * 有效期(至)
     */
    @NotNull(message = "有效期(至)不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期(至)")
    private Date endDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String contractEndBeginTime;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String contractEndEndTime;

    /**
     * 合同到期剩余天数
     */
    @TableField(exist = false)
    @Excel(name = "合同到期剩余天数")
    @ApiModelProperty(value = "合同到期剩余天数")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractRemainingDate;

    /**
     * 合同签约日期
     */
    @NotNull(message = "签约日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "签约日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同签约日期")
    private Date contractSignDate;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String contractSignBeginTime;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private String contractSignEndTime;

    /**
     * 电签平台签署ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "电签平台签署ID")
    private String esignId;

    @Excel(name = "签署状态",dictType = "s_esign_platform")
    @ApiModelProperty(value = "电签平台签署状态")
    private String esignStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "电签平台签署状态说明")
    private String esignDesc;

    /**
     * 签收状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "签收状态",dictType = "s_sign_status")
    @ApiModelProperty(value = "签收状态")
    private String signInStatus;

    /**
     * 屡约状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "屡约状态",dictType = "s_lvyue_status")
    @ApiModelProperty(value = "屡约状态")
    private String lvyueStatus;

    /**
     * 离职日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "离职日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "离职日期")
    private Date dimissionDate;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 电签平台流程完结时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "电签平台流程完结时间")
    private Date esignEndTime;

    /**
     * 公司法定代表人
     */
    @NotNull(message = "法定代表人不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司法定代表人")
    private String ownerName;

    /**
     * 统一社会信用代码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    /**
     * 公司邮箱
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司邮箱")
    private String emailCompany;

    /**
     * 公司通讯地址
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司通讯地址")
    private String officeAddr;



    /**
     * 员工身份证号
     */
    @NotNull(message = "身份证号不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工身份证号")
    private String identityCard;

    /**
     * 员工联系电话
     */
    @NotNull(message = "联系电话不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工联系电话")
    private String mobphone;

    /**
     * 员工邮箱
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工邮箱")
    private String emailStaff;

    /**
     * 员工居住地址
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工居住地址")
    private String homeAddr;

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
    private List<HrLaborContractAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "导入")
    private String importType;
}
