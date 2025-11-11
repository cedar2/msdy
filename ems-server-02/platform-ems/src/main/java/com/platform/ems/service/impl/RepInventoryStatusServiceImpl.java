package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.ems.mapper.RepInventoryStatusMapper;
import com.platform.ems.domain.RepInventoryStatus;
import com.platform.ems.service.IRepInventoryStatusService;

/**
 * 库存状况Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepInventoryStatusServiceImpl extends ServiceImpl<RepInventoryStatusMapper, RepInventoryStatus> implements IRepInventoryStatusService {
    @Autowired
    private RepInventoryStatusMapper repInventoryStatusMapper;

    /**
     * 查询库存状况
     *
     * @param dataRecordSid 库存状况ID
     * @return 库存状况
     */
    @Override
    public RepInventoryStatus selectRepInventoryStatusById(Long dataRecordSid) {
        RepInventoryStatus repInventoryStatus = repInventoryStatusMapper.selectRepInventoryStatusById(dataRecordSid);
        return repInventoryStatus;
    }

    /**
     * 查询库存状况列表
     *
     * @param repInventoryStatus 库存状况
     * @return 库存状况
     */
    @Override
    public List<RepInventoryStatus> selectRepInventoryStatusList(RepInventoryStatus repInventoryStatus) {
        return repInventoryStatusMapper.selectRepInventoryStatusList(repInventoryStatus);
    }

    /**
     * 新增库存状况
     * 需要注意编码重复校验
     *
     * @param repInventoryStatus 库存状况
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepInventoryStatus(RepInventoryStatus repInventoryStatus) {
        int row = repInventoryStatusMapper.insert(repInventoryStatus);
        return row;
    }

    /**
     * 批量删除库存状况
     *
     * @param dataRecordSids 需要删除的库存状况ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepInventoryStatusByIds(List<Long> dataRecordSids) {
        return repInventoryStatusMapper.deleteBatchIds(dataRecordSids);
    }

}
