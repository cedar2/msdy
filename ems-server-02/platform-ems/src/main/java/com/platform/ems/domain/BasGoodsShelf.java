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
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.ems.domain.base.HandleStatusInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 货架档案对象 s_bas_goods_shelf
 *
 * @author straw
 * @date 2023-02-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_goods_shelf")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasGoodsShelf extends EmsBaseEntity implements HandleStatusInfo {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    String clientId;

    /**
     * 系统SID-货架档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-货架档案")
    Long goodsShelfSid;

    @ApiModelProperty(value = "sid数组")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    Long[] goodsShelfSidList;


    @Excel(name = "货架编号")
    @NotBlank
    @ApiModelProperty(value = "货架编号（人工编码：不能有空格和中文）")
    String goodsShelfCode;


    @Excel(name = "货架名称")
    @ApiModelProperty(value = "货架名称")
    String goodsShelfName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    Long storehouseSid;

    @ApiModelProperty(value = "系统SID-仓库档案-多选")
    @TableField(exist = false)
    Long[] storehouseSidList;


    @ApiModelProperty(value = "仓库编码（人工编码）")
    String storehouseCode;

    /**
     * 库位SID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库位SID")
    Long storehouseLocationSid;


    @Excel(name = "仓库")
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库名称")
    String storehouseName;


    @Excel(name = "库位")
    @TableField(exist = false)
    @ApiModelProperty(value = "库位名称")
    String locationName;

    /**
     * 物料sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料sid")
    Long materialSid;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料分类（名称）")
    @TableField(exist = false)
    String materialClassName;

    @ApiModelProperty(value = "库位SID-多选")
    @TableField(exist = false)
    Long[] storehouseLocationSidList;


    /**
     * 库位CODE
     */
    @ApiModelProperty(value = "库位CODE")
    String locationCode;


    /**
     * 启用/停用状态
     */
    @NotBlank(message = "确认状态不能为空")
    @Excel(name = "启用/停用",
           dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    String status;


    /**
     * 处理状态
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "处理状态",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    String handleStatus;

    @Excel(name = "存放位置")
    @ApiModelProperty(value = "存放位置")
    String address;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态-多选")
    String[] handleStatusList;


    @TableField
    @ApiModelProperty(value = "创建人账号")
    String creatorAccount;


    @TableField
    @ApiModelProperty(value = "更新人账号")
    String updaterAccount;


    @ApiModelProperty(value = "确认人账号")
    String confirmerAccount;


    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    String dataSourceSys;

    // ---------------创_更_确的字段（excel）--------------------------------------


    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    @TableField(exist = false)
    String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField
    @ApiModelProperty(value = "创建日期")
    Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人")
    @TableField(exist = false)
    String updaterAccountName;

    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    String confirmerAccountName;


    @Excel(name = "确认日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    Date confirmDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "前端的查询条件之sid")
    Long materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类对象数组")
    List<ConMaterialClass> materialClassList;

}
// 01:33:20.107 [main] INFO  com.alibaba.druid.pool.DruidDataSource - [init,930] - {dataSource-1} inited
// 01:33:42.268 [main] INFO  org.mongodb.driver.cluster - [info,71] - Cluster created with settings {hosts=[47.119.171.200:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}
// 117: 20 + 30
