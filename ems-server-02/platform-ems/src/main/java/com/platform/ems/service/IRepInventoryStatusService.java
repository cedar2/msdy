package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepInventoryStatus;

/**
 * 库存状况Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepInventoryStatusService extends IService<RepInventoryStatus> {
    /**
     * 查询库存状况
     *
     * @param dataRecordSid 库存状况ID
     * @return 库存状况
     */
    public RepInventoryStatus selectRepInventoryStatusById(Long dataRecordSid);

    /**
     * 查询库存状况列表
     *
     * @param repInventoryStatus 库存状况
     * @return 库存状况集合
     */
    public List<RepInventoryStatus> selectRepInventoryStatusList(RepInventoryStatus repInventoryStatus);

    /**
     * 新增库存状况
     *
     * @param repInventoryStatus 库存状况
     * @return 结果
     */
    public int insertRepInventoryStatus(RepInventoryStatus repInventoryStatus);

    /**
     * 批量删除库存状况
     *
     * @param dataRecordSids 需要删除的库存状况ID
     * @return 结果
     */
    public int deleteRepInventoryStatusByIds(List<Long> dataRecordSids);

}
