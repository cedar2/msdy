package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 加工议价单明细报表请求实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
public class PurOutsourceQuoteBargainRequest {

    @Excel(name = "物料/商品 编码")
    @ApiModelProperty(value = "物料/商品 编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品 名称")
    @ApiModelProperty(value = "物料/商品 名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSids;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司编码（公司档案的sid）")
    private Long[] companySids;

    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long[] productSeasonSids;

    @ApiModelProperty(value = "查询：物料类型")
    private String[] materialTypeList;

    @ApiModelProperty(value = "查询：报价更新时间起")
    private String quoteUpdateDateBeginTime;

    @ApiModelProperty(value = "查询：报价更新时间至")
    private String quoteUpdateDateEndTime;

    @ApiModelProperty(value = "查询：核定价更新时间起")
    private String checkUpdateDateBeginTime;

    @ApiModelProperty(value = "查询：核定价更新时间至")
    private String checkUpdateDateEndTime;

    @ApiModelProperty(value = "查询：议价更新时间起")
    private String confirmDateBeginTime;

    @ApiModelProperty(value = "查询：议价更新时间至")
    private String confirmDateDateEndTime;

    @ApiModelProperty(value = "备注")
    private String remark;

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

    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @ApiModelProperty(value = "明细行sid")
    @TableField(exist = false)
    List<Long> itemSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：采购组")
    private String[] purchaseGroupList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @Excel(name = "当前所属阶段", dictType = "s_baoheyi_stage")
    @ApiModelProperty(value = "当前所属阶段（单选）")
    private String currentStage;

    @ApiModelProperty(value = "当前所属阶段（多选）")
    private String[] currentStageList;

    @ApiModelProperty(value = "建单所属阶段（单选）")
    private String createdStage;

    @ApiModelProperty(value = "建单所属阶段（多选）")
    private String[] createdStageList;

    @ApiModelProperty(value = "查询页面的所属阶段")
    private String stage;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String[] checkerList;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checkerName;


}
