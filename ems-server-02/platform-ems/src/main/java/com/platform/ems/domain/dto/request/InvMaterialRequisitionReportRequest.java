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
 * 领退料明细报表对象请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvMaterialRequisitionReportRequest extends EmsBaseEntity implements Serializable{

    @ApiModelProperty(value ="创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "领退料单号")
    private Long materialRequisitionCode;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @ApiModelProperty(value = "查询：创建人")
    private String[] creatorAccountList;

    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String[] documentTypeList;

    @ApiModelProperty(value = "单据类型（数据字典的键值）")
    private String documentType;

    @ApiModelProperty(value = "领料人")
    private  String materialReceiver;

    @ApiModelProperty(value = "查询：领料人")
    private  String[] materialReceiverList;

    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @ApiModelProperty(value = "出入库状态（数据字典的键值或配置档案的编码）")
    private String inOutStockStatus;

    @ApiModelProperty(value = "出入库状态（数据字典的键值或配置档案的编码）")
    private String[] inOutStockStatusList;

    @ApiModelProperty(value = "查询：作业类型")
    private String[] movementTypeList;

    @ApiModelProperty(value = "查询：需求日期起")
    private String demandDateBeginTime;

    @ApiModelProperty(value = "查询：需求日期至")
    private String demandDateEndTime;

    @ApiModelProperty(value = "查询：处理状态")
    private String[] handleStatusList;

    @ApiModelProperty(value = "查询：库位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] storehouseLocationSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：仓库")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> materialRequisitionItemSidList;
}
