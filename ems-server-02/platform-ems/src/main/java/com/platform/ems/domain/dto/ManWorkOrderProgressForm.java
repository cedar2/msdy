package com.platform.ems.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表中心班组生产进度报表 ManWorkOrderProgressFormData
 *
 * @author chenkaiwen
 * @date 2022-08-25
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ManWorkOrderProgressForm {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    private Long workCenterSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDaysDefalut;

    @ApiModelProperty(value = "指示灯：空白-1红色0绿色1橙黄2蓝色3")
    private String light;

    @ApiModelProperty(value = "工作中心/班组编码")
    private String workCenterCode;

    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "图片路径，可放多个链接，每个链接用”;“隔开")
    private String[] picturePathList;

    @ApiModelProperty(value = "视频路径")
    private String videoPath;

    @ApiModelProperty(value = "视频路径，可放多个链接，每个链接用”;“隔开")
    private String[] videoPathList;

    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划投产日期")
    private Date planStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完工日期")
    private Date planEndDate;

    @ApiModelProperty(value = "计划产量(整单)")
    private BigDecimal quantity;

    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @ApiModelProperty(value = "已完工量")
    private BigDecimal completeQuantity;

    @ApiModelProperty(value = "报表的已完工量")
    private BigDecimal totalCompleteQuantity;

    @ApiModelProperty(value = "完工状态（数据字典的键值或配置档案的编码）")
    private String completeStatus;

    @ApiModelProperty(value = "系统自增长ID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderSid;

    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-操作部门")
    private Long departmentSid;

    @JsonIgnore
    @ApiModelProperty(value = "系统SID-操作部门")
    private Long[] departmentSidList;

    @ApiModelProperty(value = "操作部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @ApiModelProperty(value = "基本计量编码")
    private String unitBase;

    @ApiModelProperty(value = "基本计量名称")
    private String unitBaseName;

    @ApiModelProperty(value = "工序明细")
    private List<ManWorkOrderProgressFormProcess> processList;

    @ApiModelProperty(value = "关注事项明细")
    private List<ManWorkOrderProgressFormConcern> concernList;

    @JsonIgnore
    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @JsonIgnore
    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @JsonIgnore
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @JsonIgnore
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @JsonIgnore
    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    @JsonIgnore
    @ApiModelProperty(value ="数据权限过滤参数")
    private Map<String, Object> params;


    @TableField(exist = false)
    @ApiModelProperty(value = "sku名称（颜色）")
    private String skuName;

    public Integer getPageBegin() {
        if (pageSize != null && pageNum != null){
            return pageSize*(pageNum-1);
        }else {
            return pageBegin;
        }
    }

    public void setPageBegin(Integer pageBegin) {
        if (pageSize != null && pageNum != null){
            this.pageBegin = this.pageSize*(this.pageNum-1);
        }else {
            this.pageBegin = pageBegin;
        }
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }


}
