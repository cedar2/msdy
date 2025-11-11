package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *上下装尺码对照查询请求信息
 */
@Data
public class MaterialBottomsListRequest {
    /** 类型 */
    private String bottomsType;

    /** 号型版型编码 */
    private String sizePatternType;

    /** 尺码编码 */
    private Long skuSid;

    /** 套装下身尺码 */
    private String sizeBottoms;

    /** 启用/停用状态 */
    private String status;

    /** 处理状态 */
    private String handleStatus;

    /** 创建人账号 */
    private String creatorAccount;
    /** 创建日期起 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDateStart;
    /** 创建日期至 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDateEnd;
}
