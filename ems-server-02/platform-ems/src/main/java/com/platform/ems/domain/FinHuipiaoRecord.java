package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

/**
 * 汇票台账表对象 s_fin_huipiao_record
 *
 * @author platform
 * @date 2024-03-12
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_huipiao_record")
public class FinHuipiaoRecord extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-汇票台账
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-汇票台账")
    private Long huipiaoRecordSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] huipiaoRecordSidList;

    /**
     * 汇票台账号
     */
    @Excel(name = "汇票台账流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "汇票台账号")
    private Long huipiaoRecordCode;

    @Excel(name = "收付款类型", dictType = "s_shoufukuan_type")
    @ApiModelProperty(value = "收付款类型")
    private String shoufukuanType;

    /**
     * 汇票号（纸质汇票）
     */
    @Excel(name = "票据号码")
    @ApiModelProperty(value = "汇票号")
    private String huipiaoNum;

    /**
     * 票面金额(含税)
     */
    @Digits(integer = 13, fraction = 2, message = "票面金额整数位上限为13位，小数位上限为2位")
    @Excel(name = "票面金额", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "票面金额(含税)")
    private BigDecimal currencyAmountTax;

    /**
     * 票面余额
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "票面余额")
    private BigDecimal balanceAmount;

    /**
     * 出票日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "出票日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "出票日期")
    private Date huipiaoDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "出票日期 起")
    private String huipiaoDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "出票日期 止")
    private String huipiaoDateEnd;

    /**
     * 汇票到期日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "汇票到期日", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "汇票到期日")
    private Date terminateDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇票到期日 起")
    private String terminateDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇票到期日 止")
    private String terminateDateEnd;

    /**
     * 出票人-开户银行
     */
    @Excel(name = "出票人-开户银行")
    @ApiModelProperty(value = "出票人-开户银行")
    private String drawerName;

    /**
     * 出票人-账号
     */
    @Excel(name = "出票人-账号")
    @ApiModelProperty(value = "出票人-账号")
    private String drawerBankAccount;

    /**
     * 年度（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度")
    private String year;

    @TableField(exist = false)
    @ApiModelProperty(value = "年度")
    private String[] yearList;

    /**
     * 季节（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "季节", dictType = "s_season")
    @ApiModelProperty(value = "季节")
    private String season;

    @TableField(exist = false)
    @ApiModelProperty(value = "季节")
    private String[] seasonList;

    /**
     * 系统SID-客户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

    /**
     * 客户编码
     */
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 系统SID-初始公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-初始公司")
    private Long companySidInitial;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-初始公司")
    private Long[] companySidInitialList;

    /**
     * 初始公司编码
     */
    @ApiModelProperty(value = "初始公司编码")
    private String companyCodeInitial;

    @TableField(exist = false)
    @ApiModelProperty(value = "初始公司名称")
    private String companyNameInitial;

    @TableField(exist = false)
    @Excel(name = "初始归属公司")
    @ApiModelProperty(value = "初始公司简称")
    private String companyShortNameInitial;

    /**
     * 系统SID-当前公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-当前公司")
    private Long companySidNew;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-当前公司")
    private Long[] companySidNewList;

    /**
     * 当前公司编码
     */
    @ApiModelProperty(value = "当前公司编码")
    private String companyCodeNew;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前公司名称")
    private String companyNameNew;

    @TableField(exist = false)
    @Excel(name = "当前归属公司")
    @ApiModelProperty(value = "当前公司简称")
    private String companyShortNameNew;

    /**
     * 汇票用途（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "汇票用途", dictType = "s_huipiao_purpose")
    @ApiModelProperty(value = "汇票用途")
    private String huipiaoPurpose;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇票用途")
    private String[] huipiaoPurposeList;

    /**
     * 汇票使用状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "汇票使用状态", dictType = "s_use_status")
    @ApiModelProperty(value = "汇票使用状态")
    private String huipiaoUseStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇票使用状态")
    private String[] huipiaoUseStatusList;

    /**
     * 币种（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 收付款摘要
     */
    @ApiModelProperty(value = "收付款摘要")
    private String shoufukuanRemark;

    /**
     * 使用说明
     */
    @ApiModelProperty(value = "使用说明")
    private String useRemark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认人
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人昵称")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 出票方-全称
     */
    @ApiModelProperty(value = "出票方-全称")
    private String drawer;

    /**
     * 收款人-全称
     */
    @ApiModelProperty(value = "收款人-全称")
    private String shoukuanrenName;

    /**
     * 收款人-账号
     */
    @ApiModelProperty(value = "收款人-账号")
    private String shoukuanrenBankcode;

    /**
     * 收款人-开户银行
     */
    @ApiModelProperty(value = "收款人-开户银行")
    private String shoukuanrenBankname;

    /**
     * 承兑人-全称
     */
    @ApiModelProperty(value = "承兑人-全称")
    private String chengduirenName;

    /**
     * 承兑人-开户银行
     */
    @ApiModelProperty(value = "承兑人-开户银行")
    private String chengduirenBankname;

    /**
     * 是否可以转让（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否可以转让（数据字典的键值或配置档案的编码）")
    private String isZhuanrang;

    /**
     * 是否可以分包（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否可以分包（数据字典的键值或配置档案的编码）")
    private String isFenbao;

    /**
     * 承兑日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "承兑日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "承兑日期")
    private Date chengduiDate;

    /**
     * 资金账户
     */
    @ApiModelProperty(value = "资金账户")
    private String fundAccount;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    /**
     * 子票区间
     */
    @ApiModelProperty(value = "子票区间")
    private String zipiaoQujian;

    /**
     * 流通标志（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "流通标志（数据字典的键值或配置档案的编码）")
    private String liutongFlag;

    /**
     * 票据状态（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "票据状态（数据字典的键值或配置档案的编码）")
    private String huipiaoStatus;

    /**
     * 流通标志是否更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "流通标志是否更改")
    private String planLiutongFlag;

    /**
     * 票据状态更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "票据状态更改")
    private String planHuipiaoStatus;

    /**
     * 是否可以分包是否更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否可以分包是否更改")
    private String planIsFenbao;

    /**
     * 是否可以转让是否更改
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否可以转让是否更改")
    private String planIsZhuanrang;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件清单")
    private List<FinHuipiaoRecordAttach> attachmentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇票台账-使用记录表对象")
    private List<FinHuipiaoRecordUseRecord> useRecordList;

    /**
     * 是否是导入
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为导入")
    private String importStatus;
}
