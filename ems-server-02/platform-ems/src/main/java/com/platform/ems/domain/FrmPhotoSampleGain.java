package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.flowable.domain.vo.FlowTaskVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 视觉设计单对象 s_frm_photo_sample_gain
 *
 * @author chenkw
 * @date 2022-12-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_photo_sample_gain")
public class FrmPhotoSampleGain extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-视觉设计单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-视觉设计单")
    private Long photoSampleGainSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] photoSampleGainSidList;

    /**
     * 视觉设计单号
     */
    @Excel(name = "视觉设计单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "视觉设计单号")
    private Long photoSampleGainCode;

    /**
     * 系统SID-开发计划
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-开发计划")
    private Long developPlanSid;

    /**
     * 开发计划号
     */
    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    /**
     * 开发计划名称
     */
    @TableField(exist = false)
    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    /**
     * 商品款号code
     */
    @TableField(exist = false)
    @Excel(name = "商品款号/SPU号")
    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    /**
     * 商品SKU条码code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @TableField(exist = false)
    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    /**
     * 系统SID-大类
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-大类")
    private Long bigClassSid;

    /**
     * 系统SID-大类 (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-大类 (多选)")
    private Long[] bigClassSidList;

    /**
     * 大类
     */
    @TableField(exist = false)
    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    /**
     * 开发类型
     */
    @TableField(exist = false)
    @Excel(name = "开发类型", dictType = "s_develop_type")
    @ApiModelProperty(value = "开发类型")
    private String developType;

    /**
     * 开发类型 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发类型 多选")
    private String[] developTypeList;

    /**
     * 销售站点；支持多值保存，每个值用英文分号";"隔开
     */
    @Excel(name = "销售站点")
    @ApiModelProperty(value = "销售站点；支持多值保存，每个值用英文分号;隔开")
    private String saleStation;

    /**
     * 销售站点 (数组)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售站点 数组")
    private String[] saleStationList;

    /**
     * 评分
     */
    @Excel(name = "评分")
    @Digits(integer = 3,
            fraction = 2,
            message = "评分整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "运营人员 Code")
    private String operator;

    @Excel(name = "运营人员")
    @ApiModelProperty(value = "运营人员（名称）")
    @TableField(exist = false)
    private String operatorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "运营人员（名称） 多选")
    private String[] operatorList;

    /**
     * 产品负责人
     */
    @ApiModelProperty(value = "产品负责人")
    private String productLeader;

    @Excel(name = "产品负责人")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品负责人（名称）")
    private String productLeaderName;

    /**
     * 产品负责人 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品负责人 多选")
    private String[] productLeaderList;

    /**
     * 视觉设计交付日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "视觉设计交付日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "视觉设计交付日期")
    private Date sampleDeliverDate;

    /**
     * 视觉设计交付日期 起
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "视觉设计交付日期 起")
    private String sampleDeliverDateBegin;

    /**
     * 视觉设计交付日期 至
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "视觉设计交付日期 至")
    private String sampleDeliverDateEnd;

    /**
     * 组别
     */
    @TableField(exist = false)
    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String[] groupTypeList;

    @TableField(exist = false)
    @Excel(name = "所属年月(项目)")
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)起")
    private String yearmonthProjectBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)止")
    private String yearmonthProjectEnd;

    /**
     * 系统SID-项目档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    /**
     * 项目编号
     */
    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案任务明细")
    private Long projectTaskSid;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 样品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品sid")
    private Long sampleSid;

    /**
     * 样品号
     */
    @Excel(name = "样品号")
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String comment;

    /**
     * 当前审批节点名称
     */
    @TableField(exist = false)
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    /**
     * 当前审批人
     */
    @TableField(exist = false)
    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户账号）多选")
    private String[] creatorAccountList;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 附件列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件列表")
    private List<FrmPhotoSampleGainAttach> attachmentList;

    /**
     * 工作流任务相关--请求参数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工作流任务相关--请求参数")
    private FlowTaskVo flowTaskVo;

    /**
     * 操作类型（提交/审批通过/审批驳回）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "操作类型（提交/审批通过/审批驳回）")
    private String businessType;
}
