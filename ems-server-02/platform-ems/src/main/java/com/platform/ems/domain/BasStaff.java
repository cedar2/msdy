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
import com.platform.ems.device.api.CompanyFactoryInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 员工档案对象 s_bas_staff
 *
 * @author qhq
 * @date 2021-04-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_staff")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasStaff extends EmsBaseEntity implements CompanyFactoryInfo {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-员工档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-员工档案")
    private Long staffSid;

    public void setStaffCode(String staffCode) {
        if (StrUtil.isNotBlank(staffCode)){
            staffCode = staffCode.replaceAll("\\s*", "");
        }
        this.staffCode = staffCode;
    }

    public void setStaffName(String staffName) {
        if (StrUtil.isNotBlank(staffName)){
            staffName = staffName.trim();
        }
        this.staffName = staffName;
    }

    /**
     * 员工编号
     */
    @Excel(name = "员工编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工编号")
    @Length(max = 8, message = "编码最大长度为8")
    private String staffCode;

    @ApiModelProperty(value = "籍贯-国家区域sid")
    private Long countryRegionNative;

    @ApiModelProperty(value = "居住地-国家区域sid")
    private Long countryRegionReside;

    /**
     * 员工姓名
     */
    @Excel(name = "员工姓名")
    @ApiModelProperty(value = "员工姓名")
    private String staffName;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工编码姓名")
    private String staffCodeName;

    /**
     * 性别
     */
    @Excel(name = "性别", dictType = "s_gender")
    @ApiModelProperty(value = "性别")
    private String gender;

    /**
     * 入职日期
     */
    @Excel(name = "入职日期")
    @ApiModelProperty(value = "入职日期")
    private String checkInDate;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "启停状态不能为空")
//    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 出生日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出生日期")
    private Date brithday;

    /**
     * 移动电话
     */
    @Excel(name = "移动电话")
    @ApiModelProperty(value = "移动电话")
    @Length(max = 11, message = "移动电话最大长度为11")
    private String mobphone;

    /**
     * 电子邮箱（企业）
     */
    @Excel(name = "企业邮箱")
    @ApiModelProperty(value = "电子邮箱（企业）")
    @Length(max = 50, message = "电子邮箱最大长度为50")
    private String emailEnterprise;

    /**
     * 电子邮箱（个人）
     */
    @Excel(name = "个人邮箱")
    @ApiModelProperty(value = "电子邮箱（个人）")
    @Length(max = 50, message = "电子邮箱最大长度为50")
    private String emailPersonal;

    /**
     * 主属公司名称
     */
    @ApiModelProperty(value = "主属公司名称")
    @TableField(exist = false)
    private String companyName;

    @Excel(name = "公司(主属)")
    @ApiModelProperty(value = "主属公司简称")
    @TableField(exist = false)
    private String companyShortName;

    @ApiModelProperty(value = "公司开票电话")
    @TableField(exist = false)
    private String invoiceTel;

    /**
     * 主属部门名称
     */
    @Excel(name = "部门(主属)")
    @ApiModelProperty(value = "主属部门名称")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工资成本分摊类型")
    private String salaryCostAllocateType;

    /**
     * 主属岗位名称
     */
    @Excel(name = "岗位(主属)")
    @ApiModelProperty(value = "主属岗位名称")
    @TableField(exist = false)
    private String positionName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long defaultPlantSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂 (多选)")
    private Long[] defaultPlantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心/班组")
    private Long workCenterSid;

    @TableField(exist = false)
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @TableField(exist = false)
    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    /**
     * 员工类型
     */
    @Excel(name = "员工类型", dictType = "s_staff_type")
    @ApiModelProperty(value = "员工类型")
    private String staffType;

    /**
     * 员工状态
     */
    @Excel(name = "员工状态", dictType = "s_staff_status")
    @ApiModelProperty(value = "员工状态")
    private String staffStatus;

    /**
     * 在离职状态
     */
    @Excel(name = "在离职状态", dictType = "s_is_on_job")
    @ApiModelProperty(value = "在离职状态")
    private String isOnJob;

    /**
     * 职级编码
     */
//    @Excel(name = "职级", dictType = "s_staff_level")
    @ApiModelProperty(value = "职级编码")
    private String levelCode;

    //    @Excel(name = "岗位类型", dictType = "s_position_type")
    @ApiModelProperty(value = "岗位类型")
    private String positionType;

    /**
     * 是否复聘
     */
//    @Excel(name = "是否复聘", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否复聘")
    private String isReappointment;

    /**
     * 月薪（当前）（元）
     */
    @Excel(name = "月薪(当前)")
    @ApiModelProperty(value = "月薪（当前）（元）")
    private BigDecimal permonthWagePresent;

    /**
     * 月薪（转正）（元）
     */
    @Excel(name = "月薪(转正)")
    @ApiModelProperty(value = "月薪（转正）（元）")
    private BigDecimal permonthWageFormal;

    /**
     * 月薪（试用）（元）
     */
    @Excel(name = "月薪(试用)")
    @ApiModelProperty(value = "月薪（试用）（元）")
    private BigDecimal permonthWageProbation;

    /**
     * 身份证号
     */
    @Excel(name = "身份证号")
    @ApiModelProperty(value = "身份证号")
    @Length(max = 30, message = "身份证号最大长度为30")
    private String identityCard;

    /**
     * 居住地-所属城市编码sid（冗余）
     */
//    @Excel(name = "居住城市")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "居住地-所属城市编码sid（冗余）")
    private Long city;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "出生日期开始时间")
    @TableField(exist = false)
    private String brithdayStartTime;

    @ApiModelProperty(value = "出生日期结束时间")
    @TableField(exist = false)
    private String brithdayEndTime;

    /**
     * 主属岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属岗位sid")
    private Long defaultPosition;

    @TableField(exist = false)
    @ApiModelProperty(value = "主属岗位编码")
    private String defaultPositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "主属岗位编码")
    private String[] defaultPositionCodeList;

    /**
     * 主属部门sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属部门sid")
    private Long defaultDepartmentSid;

    @ApiModelProperty(value = "查询：主属部门sid")
    @TableField(exist = false)
    private Long[] defaultDepartmentSids;

    /**
     * 主属公司sid
     */
    @NotNull(message = "公司(主属)不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属公司sid")
    private Long defaultCompanySid;

    @ApiModelProperty(value = "查询：主属岗位sid")
    @TableField(exist = false)
    private Long[] defaultPositions;


    /**
     * 固定电话
     */
    @ApiModelProperty(value = "固定电话")
    @Length(max = 30, message = "固定电话最大长度为30")
    private String telephone;

    /**
     * 微信账号
     */
    @ApiModelProperty(value = "微信账号")
    @Length(max = 60, message = "微信账号最大长度为60")
    private String wechatId;

    /**
     * 企业微信账号
     */
    @ApiModelProperty(value = "企业微信账号")
    @Length(max = 60, message = "企业微信账号最大长度为60")
    private String workWechatId;

    /**
     * 钉钉账号
     */
    @ApiModelProperty(value = "钉钉账号")
    @Length(max = 60, message = "钉钉账号最大长度为60")
    private String dingtalkId;

    /**
     * SCM供应链系统账号
     */
    @ApiModelProperty(value = "SCM供应链系统账号")
    @Length(max = 60, message = "SCM供应链系统账号最大长度为60")
    private String scmId;

    /**
     * 传真
     */
    @ApiModelProperty(value = "传真")
    private String fax;

    /**
     * 紧急联系人-1
     */
    @ApiModelProperty(value = "紧急联系人-1")
    @Length(max = 60, message = "紧急联系人最大长度为60")
    private String emergencyContactName1;

    /**
     * 紧急联系人1电话
     */
    @ApiModelProperty(value = "紧急联系人1电话")
    @Length(max = 30, message = "紧急联系人电话最大长度为30")
    private String emergencyContactTel1;

    /**
     * 紧急联系人-2
     */
    @ApiModelProperty(value = "紧急联系人-2")
    @Length(max = 60, message = "紧急联系人最大长度为60")
    private String emergencyContactName2;

    /**
     * 紧急联系人2电话
     */
    @ApiModelProperty(value = "紧急联系人2电话")
    @Length(max = 30, message = "紧急联系人电话最大长度为30")
    private String emergencyContactTel2;

    /**
     * 联系地址
     */
    @ApiModelProperty(value = "联系地址（办公）")
    private String addressOffice;

    @ApiModelProperty(value = "联系地址（住所）")
    private String addressHome;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

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
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @TableField(exist = false)
    @Excel(name = "确认人")
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
    @ApiModelProperty(value = "员工附件对象")
    private List<BasStaffAttachment> attachmentList;

    /**
     * 居住地-所属国家编码sid（冗余）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "居住地-所属国家编码sid（冗余）")
    private Long country;

    /**
     * 居住地-所属省份编码sid（冗余）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "居住地-所属省份编码sid（冗余）")
    private Long province;

    /**
     * 月薪（试用）（币种）（数据字典的键值）
     */
    @ApiModelProperty(value = "月薪（试用）（币种）（数据字典的键值）")
    private String currencyProbation;

    /**
     * 月薪（转正）（币种）（数据字典的键值）
     */
    @ApiModelProperty(value = "月薪（转正）（币种）（数据字典的键值）")
    private String currencyFormal;

    @ApiModelProperty(value = "月薪（币种）")
    private String currencyPresent;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] staffSidList;

    @ApiModelProperty(value = "查询：岗位类型")
    @TableField(exist = false)
    private String[] positionTypes;

    /**
     * 在离职状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "在离职状态list")
    private String[] isOnJobList;

    /**
     * 主属公司list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "主属公司list")
    private Long[] defaultCompanySidList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 员工类型list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "员工类型list")
    private String[] staffTypeList;

    /**
     * 性别list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "性别list")
    private String[] genderList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "试用期(至)")
    private Date probationPeriodToDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "雇主责任险到期日")
    private Date employerLiabilityInsuranceDate;

    /**
     * 员工编号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "员工编号list")
    private List<String> staffCodeList;

    /**
     * 身份证号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "身份证号list")
    private List<String> identityCardList;

    @TableField(exist = false)
    private String namePlusCode;

    @ApiModelProperty(value = "系统账号")
    @TableField(exist = false)
    private String systemAccount;

    @TableField(exist = false)
    private String member;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工sid关联的用户id")
    private Long userId;


    @TableField(exist = false)
    String companyCode;

    @Override
    public String getName() {
        return this.getStaffName();
    }
}
