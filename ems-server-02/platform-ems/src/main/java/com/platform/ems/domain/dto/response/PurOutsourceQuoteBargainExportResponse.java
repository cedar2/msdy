package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 加工采购议价单导出响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurOutsourceQuoteBargainExportResponse {

    @Excel(name = "加工议价单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询报议价单号")
    private Long outsourceQuoteBargainCode;

    @Excel(name = "加工商")
    private String vendorName;

    @ApiModelProperty(value = "查询：物料编码")
    @Excel(name = "商品编码")
    private String materialCode;

    @Excel(name = "我司样衣号")
    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "查询：物料名称")
    @Excel(name = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "加工项")
    @Excel(name = "加工项")
    private String processName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    @NotEmpty(message = "有效期（起）不能为空")
    private Date startDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（至）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（至）")
    @NotEmpty(message = "有效期（至）不能为空")
    private Date endDate;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    @Excel(name = "客方确认价(含税)")
    @ApiModelProperty(value = "客方确认价(含税)")
    private BigDecimal customerPriceTax;

    @ApiModelProperty(value = "采购价单位名称")
    @Excel(name = "采购价单位")
    private String unitPriceName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ApiModelProperty(value = "税率名称")
    @Excel(name = "税率")
    private BigDecimal taxRate;

    @Excel(name = "价格维度",dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度")
    private String priceDimension;


    @Excel(name = "备注")
    @ApiModelProperty(value ="备注")
    private String remark;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "采购员")
    private String buyerName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "议价日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "议价日期")
    private Date dateConfirm;

    @Excel(name = "采购组")
    @ApiModelProperty(value = "采购组名称")
    private String purchaseGroupName;


    @Excel(name = "采购员电话")
    @ApiModelProperty(value = "采购员电话")
    private String buyerTelephone;

    @Excel(name = "采购员邮箱")
    @ApiModelProperty(value = "采购员邮箱")
    @Email
    private String buyerEmail;
    @ApiModelProperty(value = "当前审批节点名称")
    @Excel(name = "当前审批节点")
    private String approvalNode;

    /** 当前审批人 */
    @ApiModelProperty(value = "当前审批人")
    @Excel(name = "当前审批人")
    private String approvalUserName;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
}
