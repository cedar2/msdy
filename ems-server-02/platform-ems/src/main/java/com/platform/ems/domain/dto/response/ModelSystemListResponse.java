package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author olive
 */
@Data
public class ModelSystemListResponse implements Serializable {

    /**
     * 客户端口号
     */
    private String clientId;

    /**
     * 系统ID-版型档案
     */
    @Excel(name = "系统ID-版型档案")
    private String modelSid;

    /**
     * 版型编码
     */
    private String modelCode;

    /**
     * 版型名称
     */
    @Excel(name = "版型名称")
    private String modelName;
    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用状态")
    private String validStatus;
    /**
     * 版型类型编码
     */
    @Excel(name = "版型类型编码")
    private String modelType;

    /**
     * 尺码组sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "尺码组sid")
    private Long skuGroupSid;

    /**
     * 基准尺码编码
     */
    @Excel(name = "基准尺码编码")
    private Long standardSku;


    /**
     * 处理状态
     */
    @Excel(name = "处理状态")
    private String handleStatus;

    /**
     * 客户编码
     */
    @Excel(name = "客户编码")
    private Long customer;
    /**
     * 上下装编码
     */
    @Excel(name = "上下装编码")
    private String upDownSuit;


    /**
     * 男女装标识编码
     */
    @Excel(name = "男女装标识编码")
    private String maleFemaleFlag;

    /**
     * 老少幼标识编码
     */
    @Excel(name = "老少幼标识编码")
    private String oldYoungFlag;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 创建人账号
     */
    @Excel(name = "创建人账号")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /**
     * 更新人账号
     */
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /**
     * 图片路径
     */
    @Excel(name = "图片路径")
    private String picturePath;
    /**
     * 数据源系统
     */
    @Excel(name = "数据源系统")
    private String dataSourceSys;
}
