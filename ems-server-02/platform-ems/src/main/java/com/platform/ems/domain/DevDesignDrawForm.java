package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.annotation.Excels;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;


/**
 * 图稿批复单对象 s_dev_design_draw_form
 *
 * @author qhq
 * @date 2021-11-05
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_dev_design_draw_form")
public class DevDesignDrawForm extends EmsBaseEntity {
	/**
	 * 租户ID
	 */
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "租户ID")
	private String clientId;

	/**
	 * 系统SID-图稿批复单
	 */
	@TableId
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "系统SID-图稿批复单")
	private Long designDrawFormSid;
	@ApiModelProperty(value = "sid数组")
	@TableField(exist = false)
	private Long[] designDrawFormSidList;

	@Excel(name = "图稿批复单号", sort = 1)
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "图稿批复单号")
	private Long designDrawFormCode;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "样品/商品sid")
	private Long productSid;

	@ApiModelProperty(value = "批复结果（数据字典的键值或配置档案的编码）")
	private String approveStatus;

	/**
	 * 评语
	 */
	@ApiModelProperty(value = "评语")
	private String approveComment;

	/**
	 * 处理状态（数据字典的键值或配置档案的编码）
	 */
	@Excel(name = "处理状态", dictType = "s_handle_status", sort = 18)
	@NotEmpty(message = "状态不能为空")
	@ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
	private String handleStatus;

	@TableField(exist = false)
	private String[] handleStatusList;

	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建人账号（用户账号）")
	private String creatorAccount;

	@TableField(exist = false)
	private List<String> creatorAccountList;

	@TableField(exist = false)
	@ApiModelProperty(value = "创建人名称")
	@Excel(name = "创建人", sort = 19)
	private String creatorAccountName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd", sort = 20)
	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新人账号（用户账号）")
	private String updaterAccount;

	@TableField(exist = false)
	@ApiModelProperty(value = "更新人名称")
	@Excel(name = "更改人", sort = 21)
	private String updaterAccountName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd", sort = 22)
	@TableField(fill = FieldFill.UPDATE)
	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	@ApiModelProperty(value = "确认人账号（用户账号）")
	private String confirmerAccount;

	@TableField(exist = false)
	@ApiModelProperty(value = "确认人名称")
	private String confirmerAccountName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "确认时间")
	private Date confirmDate;

	@TableField(fill = FieldFill.INSERT)
	@ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
	private String dataSourceSys;

	@TableField(exist = false)
	@ApiModelProperty("附件")
	private List<DevDesignDrawFormAttach> athList;

	@ApiModelProperty("档案")
	@TableField(exist = false)
	@Excels({
			@Excel(name = "我司样品号", targetAttr = "sampleCodeSelf", sort = 2),
			@Excel(name = "产品季", targetAttr = "productSeasonName", sort = 3),
			@Excel(name = "季节", targetAttr = "season", dictType = "s_season", sort = 4),
			@Excel(name = "设计师", targetAttr = "designerAccountName", sort = 5),
			@Excel(name = "样品分类", targetAttr = "nodeName", sort = 6),
			@Excel(name = "款式", targetAttr = "kuanType", dictType = "s_kuan_type", sort = 7),
			@Excel(name = "风格", targetAttr = "style", dictType = "s_style", sort = 8),
			@Excel(name = "系列", targetAttr = "series", dictType = "s_series", sort = 9),
			@Excel(name = "版型", targetAttr = "modelName", sort = 10),
			@Excel(name = "版型类型", targetAttr = "modelType", dictType = "s_model_type", sort = 11),
			@Excel(name = "公司", targetAttr = "companyName", sort = 12),
			@Excel(name = "品牌", targetAttr = "companyBrandName", sort = 13),
			@Excel(name = "品标", targetAttr = "companyBrandMarkName", sort = 14),
			@Excel(name = "是否主推款", targetAttr = "isPopularizeProduct", dictType = "sys_yes_no", sort = 15)
	})
	private BasMaterial material;

	@ApiModelProperty("产品季名称")
	@TableField(exist = false)
	private String seasonName;

	@ApiModelProperty("我司样品号")
	@TableField(exist = false)
	private String sampleCodeSelf;

	@TableField(exist = false)
	@ApiModelProperty(value = "产品季sid数组")
	private Long[] productSeasonSidList;

	@TableField(exist = false)
	@ApiModelProperty(value = "季节编码数组")
	private String[] seasonList;

	@TableField(exist = false)
	@ApiModelProperty(value = "版型类型数组")
	private String[] modelTypeList;

	@TableField(exist = false)
	@ApiModelProperty(value = "设计师账号")
	private String designerAccount;

	@TableField(exist = false)
	private List<String> designerAccountList;

	@ApiModelProperty(value = "当前审批节点名称")
	@TableField(exist = false)
	@Excel(name="当前审批节点",sort = 16)
	private String approvalNode;

	@ApiModelProperty(value = "当前审批人")
	@TableField(exist = false)
	@Excel(name="当前审批人",sort = 17)
	private String approvalUserName;

	@TableField(exist = false)
	private String approvalUserId;

	@ApiModelProperty(value ="备注")
//	@Excel(name="备注",sort = 19)
	private String remark;
}
