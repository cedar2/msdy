package com.platform.ems.domain.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 工序修改状态请求信息
 *
 * @author yangqize
 * @date 2021-03-08
 */
@Data
public class ManProcessActionRequest {
    /** 工序表id */
    private List<Long> processSids;
    /** 处理状态 */
    private String handleStatus;
    /** 启用/停用 */
    private String status;

}
