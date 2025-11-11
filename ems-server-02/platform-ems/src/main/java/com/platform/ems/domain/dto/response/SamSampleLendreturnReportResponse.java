package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 样品借还明细报表
 *
 * @author yangqz
 * @date 2022-3-31
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SamSampleLendreturnReportResponse {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品借还单")
    private Long lendreturnSid;

    @Excel(name = "样品借还单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品借还单号")
    private Long lendreturnCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-样品借还单明细")
    private Long lendreturnItemSid;

    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "样品编码")
    private String materialCode;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @Excel(name = "样品名称")
    @ApiModelProperty(value = "样品名称")
    private String materialName;


    @Excel(name = "颜色")
    @ApiModelProperty(value = "颜色")
    private String sku1Name;

    @Excel(name = "尺码")
    @ApiModelProperty(value = "尺码")
    private String sku2Name;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "借出数量")
    private Integer quantityJ;

    @ApiModelProperty(value = "本次借出量/归还数量")
    @Excel(name = "借出（归还）量")
    private Integer quantity;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "已归还量")
    @Excel(name = "已归还量")
    private Integer quantityYG;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "待归还量")
    @Excel(name = "待归还量")
    private Integer quantityDG;

    @ApiModelProperty(value = "仓库")
    @Excel(name = "仓库")
    private String storehouseName;

    @ApiModelProperty(value = "库位")
    @Excel(name = "库位")
    private String locationName;

    @ApiModelProperty(value = "供应商")
    @Excel(name = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "客户")
    @Excel(name = "客户")
    private String customerName;

    @ApiModelProperty(value = "借出人")
    @Excel(name = "借出人")
    private String lenderName;

    @ApiModelProperty(value = "归还人")
    @Excel(name = "归还人")
    private String returnerName;

    @ApiModelProperty(value = "关联业务单号")
    @Excel(name = "关联业务单号")
    private Long referLendreturnCode;

    @ApiModelProperty(value = "关联业务单行号")
    @Excel(name = "关联业务单行号")
    private Long referItemNum;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value ="备注")
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
}
