package com.platform.ems.domain.dto.request.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 品类规划明细报表查询请求
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevCategoryPlanItemFormRequest {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-品类规划明细")
    private Long categoryPlanItemSid;

    @ApiModelProperty(value = "品类规划明细sid数组")
    private Long[] categoryPlanItemSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-品类规划")
    private Long categoryPlanSid;

    @ApiModelProperty(value = "品类规划sid数组")
    private Long[] categoryPlanSidList;

    @ApiModelProperty(value = "年度（单选）")
    private String year;

    @ApiModelProperty(value = "年度（多选）")
    private String[] yearList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司（单选）")
    private Long companySid;

    @ApiModelProperty(value = "公司（多选）")
    private Long[] companySidList;

    @ApiModelProperty(value = "品牌编码（单选）")
    private String brandCode;

    @ApiModelProperty(value = "品牌编码（多选）")
    private String[] brandCodeList;

    @ApiModelProperty(value = "计划类型（单选）")
    private String planType;

    @ApiModelProperty(value = "计划类型（多选）")
    private String[] planTypeList;

    @ApiModelProperty(value = "创建人账号（下拉框）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人昵称（输入框）")
    private String creatorAccountName;

    @ApiModelProperty(value = "处理状态（单选）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类（单选）")
    private Long bigClassSid;

    @ApiModelProperty(value = "大类（单选）")
    private Long[] bigClassSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类（单选）")
    private Long middleClassSid;

    @ApiModelProperty(value = "中类（单选）")
    private Long[] middleClassSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类（单选）")
    private Long smallClassSid;

    @ApiModelProperty(value = "小类（单选）")
    private Long[] smallClassSidList;

    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String groupType;

    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String[] groupTypeList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "下一步负责人sid")
    private Long nextReceiverSid;

    @ApiModelProperty(value = "下一步负责人编码")
    private String nextReceiverCode;

    @ApiModelProperty(value = "下一步负责人名称")
    private String nextReceiverName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
