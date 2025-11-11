package com.platform.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platform.common.annotation.Excel;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.common.utils.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 租户信息对象 s_sys_client
 *
 * @author linhongwei
 * @date 2021-09-30
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_client")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysClient extends EmsBaseEntity {

    String deviceAppId;
    String deviceAppSecret;
    /**
     * 租户ID（客户端口号）
     */
    @TableField(fill = FieldFill.INSERT)
    @TableId
    @Excel(name = "租户ID")
    @NotBlank(message = "租户ID不能为空")
    @Length(max = 5,
            message = "租户ID不能超过5个字符")
    @ApiModelProperty(value = "租户ID（客户端口号）")
    private String clientId;
    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private String[] clientIdList;
    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    @Length(max = 300,
            message = "租户名称不能超过300个字符")
    @Excel(name = "租户名称")
    @ApiModelProperty(value = "租户名称")
    private String clientName;
    /**
     * 租户编码
     */
    @Excel(name = "租户编码")
    @NotBlank(message = "租户编码不能为空")
    @Length(max = 8,
            message = "租户编码不能超过8个字符")
    @ApiModelProperty(value = "租户编码")
    private String clientCode;

    @ApiModelProperty(value = "是否盘点审批")
    private String isPandianApproval;
    /**
     * 租户类型
     */
    @NotEmpty(message = "租户类型不能为空")
    @Excel(name = "租户类型",
            dictType = "s_client_type")
    @ApiModelProperty(value = "租户类型")
    private String clientType;
    /**
     * 是否业财一体化
     */
    @Excel(name = "是否自动生成暂估流水",
           dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否业财一体化/是否自动生成暂估流水")
    private String isBusinessFinance;
    /**
     * 最近缴费金额（元）
     */
    @Excel(name = "最近缴费金额（元）")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @Digits(integer = 8,
            fraction = 2,
            message = "最近缴费金额整数位上限为8位，小数位上限为2位")
    @ApiModelProperty(value = "最近缴费金额（元）")
    private BigDecimal payduesLatestAmount;
    /**
     * 租户规模
     */
    @NotEmpty(message = "租户规模不能为空")
    @Excel(name = "租户规模",
            dictType = "s_client_scale")
    @ApiModelProperty(value = "租户规模")
    private String clientScale;
    /**
     * 系统简称（租户）
     */
    @Excel(name = "系统简称")
    @Length(max = 8,
            message = "系统简称不能超过8个字符")
    @ApiModelProperty(value = "系统简称（租户）")
    private String sysShortName;
    /**
     * 租户有效期(起)
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @Excel(name = "租户有效期(起)",
            width = 30,
            dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "租户有效期(起)")
    private Date startDate;
    /**
     * 租户有效期(至)
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @Excel(name = "租户有效期(至)",
            width = 30,
            dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "租户有效期(至)")
    private Date endDate;
    /**
     * 最近缴费日期
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @Excel(name = "最近缴费日期",
            width = 30,
            dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近缴费日期")
    private Date payduesLatestDate;
    /**
     * 行业领域编码
     */
    @NotBlank(message = "所属行业不能为空")
    @Excel(name = "所属行业",
            dictType = "s_industry")
    @ApiModelProperty(value = "行业领域编码")
    private String industryField;
    /**
     * 法人/负责人姓名
     */
    @Length(max = 120,
            message = "法人/负责人不能超过120个字符")
    @Excel(name = "法人/负责人")
    @ApiModelProperty(value = "法人/负责人姓名")
    private String ownerName;


    @TableField(exist = false)
    @ApiModelProperty(value = "到期")
    private Date endDateY;

    @TableField(exist = false)
    @ApiModelProperty(value = "未到期")
    private Date endDateN;
    /**
     * 统一信用代码证号
     */
    @Length(max = 30,
            message = "统一信用代码证号不能超过30个字符")
    @Excel(name = "统一信用代码证号")
    @ApiModelProperty(value = "统一信用代码证号")
    private String creditCode;
    /**
     * 租户子域名
     */
    @Length(max = 20,
            message = "子域名编码不能超过20个字符")
    @ApiModelProperty(value = "租户子域名")
    private String subdomain;
    /**
     * 年营业额(万元）
     */
    @Digits(integer = 9,
            fraction = 6,
            message = "销售量整数位上限为9位，小数位上限为6位")
    @ApiModelProperty(value = "年营业额(万元）")
    private String annualTurnover;

    /**
     * 登录页背景图路径（租户）
     */
    @ApiModelProperty(value = "登录页背景图路径（租户）")
    private String logonPicturePath;

    /**
     * LOGO图路径（租户）
     */
    @ApiModelProperty(value = "LOGO图路径（租户）")
    private String logoPicturePath;
    /**
     * 营业执照-有效期起始
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期起始")
    private Date creditStartDate;
    /**
     * 营业执照-有效期结束
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "营业执照-有效期结束")
    private Date creditEndDate;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;
    /**
     * 处理状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态",
            dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccountName;
    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用",
            dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 租户所使用IM工具
     */
    @Excel(name = "租户所使用IM工具",
            dictType = "s_im_software")
    @ApiModelProperty(value = "租户所使用IM工具")
    private String imSoftware;
    /**
     * 授权电签数
     */
    @Excel(name = "授权电签数")
    @ApiModelProperty(value = "授权电签数")
    private Integer licenseDianqianNum;

    /**
     * 电签数有效期(起)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "电签数有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "电签数有效期(起)")
    private Date dianqianStartDate;

    /**
     * 电签数有效期(至)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "电签数有效期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "电签数有效期(至)")
    private Date dianqianEndDate;

    /**
     * 当期已使用电签数
     */
    @Excel(name = "当期已使用电签数")
    @ApiModelProperty(value = "当期已使用电签数")
    private Integer useDianqianNum;

    /**
     * 累计已使用电签数
     */
    @Excel(name = "累计已使用电签数")
    @ApiModelProperty(value = "累计已使用电签数")
    private Integer totalUseDianqianNum;
    /**
     * 锁定电签数
     */
    @Excel(name = "锁定电签数")
    @ApiModelProperty(value = "锁定电签数")
    private Integer lockDianqianNum;
    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认人账号（用户名称）
     */
    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccountName;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期",
            width = 30,
            dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "是否到期 Y到期 N未到期")
    @TableField(exist = false)
    private String isExpire;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 是否自动生成预付款/应收款台账
     */
    @ApiModelProperty(value = "是否自动生成预付款/应收款台账")
    private String isAutoAdvanceAccount;

    /**
     * 是否自动生成应付暂估/应收暂估
     */
    @ApiModelProperty(value = "是否自动生成应付暂估/应收暂估")
    private String isAutoEstimationAccount;

    /**
     * 经营类别（数据字典的键值或配置档案的编码）：工厂、品牌、贸易、工贸一体
     */
    @ApiModelProperty(value = "经营类别（数据字典的键值或配置档案的编码）：工厂、品牌、贸易、工贸一体")
    private String operateCategory;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期",
            width = 30,
            dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8",
            pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期",
            width = 30,
            dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 飞书appId
     */
    @ApiModelProperty(value = "飞书appId")
    private String feishuAppId;

    /**
     * 飞书的appSecret
     */
    @ApiModelProperty(value = "飞书的appSecret")
    private String feishuAppSecret;

    /**
     * 钉钉公匙
     */
    @ApiModelProperty(value = "钉钉公匙")
    private String dingtalkAppkey;

    /**
     * 钉钉私匙
     */
    @ApiModelProperty(value = "钉钉私匙")
    private String dingtalkAppsecret;

    /**
     * 钉钉agentid
     */
    @ApiModelProperty(value = "钉钉agentid")
    private String dingtalkAgentid;

    /**
     * 企微公匙
     */
    @ApiModelProperty(value = "企微公匙")
    private String workWechatAppkey;

    /**
     * 企微私匙
     */
    @ApiModelProperty(value = "企微私匙")
    private String workWechatAppsecret;

    /**
     * 企微agentid
     */
    @ApiModelProperty(value = "企微agentid")
    private String workWechatAgentid;

    /**
     * 微信公众号公匙
     */
    @ApiModelProperty(value = "微信公众号公匙")
    private String wxGzhAppkey;

    /**
     * 微信公众号私匙
     */
    @ApiModelProperty(value = "微信公众号私匙")
    private String wxGzhAppsecret;

    /**
     * 企业全称
     */
    @ApiModelProperty(value = "企业全称")
    private String companyName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String telephone;

    /**
     * 账号数限制类型（数据字典的键值或配置档案的编码） s_account_num_limit_type
     */
    @ApiModelProperty(value = "账号数限制类型（数据字典的键值或配置档案的编码）")
    private String accountNumLimitType;

    /**
     * 系统收费类型（数据字典的键值或配置档案的编码） s_system_fee_type
     */
    @ApiModelProperty(value = "系统收费类型（数据字典的键值或配置档案的编码）")
    private String systemFeeType;

    /**
     * 授权账号数
     */
    @ApiModelProperty(value = "授权账号数")
    private Integer authorizeAccountNum;

    /**
     * 微信公众号应用id
     */
    @ApiModelProperty(value = "微信公众号应用id")
    private String wxGzhAgentid;

    /**
     * 飞书应用id
     */
    @ApiModelProperty(value = "飞书应用id")
    private String feishuAppAgentid;


    /* 简道云配置 */
    String jiandaoyunUrl;
    String jiandaoyunAcs;
    String jiandaoyunIssuer;
    String jiandaoyunSecret;
    /**
     * 缴费币种（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "缴费币种（数据字典的键值或配置档案的编码）")
    private String currency;
    /**
     * 缴费货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "缴费货币单位（数据字典的键值或配置档案的编码）")
    private String currencyUnit;
}
