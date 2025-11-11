package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 加工报核议价明细报表对象请求实体
 *
 * @author yangqz
 * @date 2021-7-16
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurOutsourceQuotationRequest extends EmsBaseEntity implements Serializable{

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工项（加工项的sid）")
    private Long[] processSidList;

    @ApiModelProperty(value = "加工询报议价单号")
    private String outsourceQuoteBargainCode;

    @ApiModelProperty(value = "当前审批人ID")
    @TableField(exist = false)
    private String approvalUserId;

    @ApiModelProperty(value = "采购组（数据字典的键值）")
    private String[] purchaseGroupList;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季档案")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String[] materialTypeList;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：供应商")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "报价员（用户名称）")
    private String quoter;

    @ApiModelProperty(value = "查询：供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vendorSid;

    @ApiModelProperty(value = "查询：公司")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] companySidList;

    @Excel(name = "采购员（用户名称）")
    @ApiModelProperty(value = "采购员（用户名称）")
    private String buyer;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员（用户名称）")
    private String[] buyerList;

    @ApiModelProperty(value = "明细行sid")
    @TableField(exist = false)
    List<Long> itemSidList;

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

    @ApiModelProperty(value = "阶段（多选）")
    private String[] stageList;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String[] checkerList;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checkerName;

}
