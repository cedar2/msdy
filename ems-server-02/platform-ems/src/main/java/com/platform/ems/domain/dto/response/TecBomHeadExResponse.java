package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * bom导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadExResponse implements Serializable {

    @ApiModelProperty(value = "物料档案编码")
    @Excel(name = "商品编码(款号)")
    private String materialCode ;

    @ApiModelProperty(value = "商品名称")
    @Excel(name = "商品名称")
    private String materialName;

    @Excel(name = "商品类型")
    @ApiModelProperty(value = "商品类型")
    private String materialTypeName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "商品分类")
    @Excel(name = "商品分类")
    private String materialClassName;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "设计师账号")
    @Excel(name = "设计师")
    private String designerAccountName;

    @Excel(name = "版型")
    @ApiModelProperty(value = "版型")
    @TableField(exist = false)
    private String modelName;

    @Excel(name = "上下装/套装",dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装（数据字典的键值）")
    private String upDownSuit;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "查询：客方商品编码")
    @Excel(name = "客方商品编码")
    private String customerProductCode;

    @Excel(name = "客方样衣号")
    @ApiModelProperty(value = "客方样衣号")
    private String sampleCodeCustomer;

    @Excel(name = "快速编码")
    @ApiModelProperty(value = "快速编码")
    private String simpleCode;

    @Excel(name = "生产工艺类型")
    @ApiModelProperty(value = "生产工艺类型")
    private String productTechniqueTypeName;

    @Excel(name = "男女装",dictType = "s_suit_gender")
    @ApiModelProperty(value = "男女装标识")
    private String maleFemaleFlag;

    @Excel(name = "风格", dictType = "s_style")
    @ApiModelProperty(value = "风格编码")
    @TableField(exist = false)
    private String style;

    @Excel(name = "款式", dictType = "s_kuan_type")
    @ApiModelProperty(value = "款式编码")
    @TableField(exist = false)
    private String kuanType;

    @Excel(name = "研产销阶段",dictType = "s_cycle_stage")
    @ApiModelProperty(value = "研产销阶段")
    private String cycleStage;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "品牌")
    @ApiModelProperty(value = "品牌")
    @TableField(exist = false)
    private String companyBrandName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "提交人")
    @ApiModelProperty(value = "提交人")
    private String submitUserName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提交日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    private Date submitDate;

    @Excel(name = "当前工作节点")
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @Excel(name = "当前处理人")
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

}
