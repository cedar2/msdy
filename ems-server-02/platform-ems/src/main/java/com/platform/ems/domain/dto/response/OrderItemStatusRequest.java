package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 面辅料状态、客供料状态
 *
 * @author yangqz
 * @date 2021-4-11
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderItemStatusRequest {

    @ApiModelProperty(value = "原材料_需购状态（数据字典的键值或配置档案的编码）")
    private String yclXugouStatus;

    @ApiModelProperty(value = "原材料_备料状态（数据字典的键值或配置档案的编码）")
    private String yclBeiliaoStatus;

    @ApiModelProperty(value = "原材料_采购下单状态（数据字典的键值或配置档案的编码）")
    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "原材料_齐套状态（数据字典的键值或配置档案的编码）")
    private String yclQitaoStatus;

    @ApiModelProperty(value = "客供料_申请状态（数据字典的键值或配置档案的编码）")
    private String kglShenqingStatus;

    @ApiModelProperty(value = "客供料_到料状态（数据字典的键值或配置档案的编码）")
    private String kglDaoliaoStatus;

    @ApiModelProperty(value = "甲供料_供料状态（数据字典的键值或配置档案的编码）")
    private String jglGongliaoStatus;

    @ApiModelProperty(value = "原材料_齐套说明")
    private String yclQitaoRemark;

    @ApiModelProperty(value = "修改面辅料传MFL,修改甲供料传JGL，修改客供料传KGL")
    private String type;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所选择订单明细sid")
    @TableId
    private Long[] orderItemSidList;
}
