package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.RepManufactureStatusDaipai;
import com.platform.ems.mapper.RepManufactureStatusDaipaiMapper;
import com.platform.ems.service.IRepManufactureStatusDaipaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 生产状况-待排产Service业务层处理
 *
 * @author c
 * @date 2022-03-17
 */
@Service
@SuppressWarnings("all")
public class RepManufactureStatusDaipaiServiceImpl extends ServiceImpl<RepManufactureStatusDaipaiMapper, RepManufactureStatusDaipai> implements IRepManufactureStatusDaipaiService {
    @Autowired
    private RepManufactureStatusDaipaiMapper repManufactureStatusDaipaiMapper;

    /**
     * 查询生产状况-待排产
     *
     * @param dataRecordSid 生产状况-待排产ID
     * @return 生产状况-待排产
     */
    @Override
    public RepManufactureStatusDaipai selectRepManufactureStatusDaipaiById(Long dataRecordSid) {
        RepManufactureStatusDaipai repManufactureStatusDaipai = repManufactureStatusDaipaiMapper.selectRepManufactureStatusDaipaiById(dataRecordSid);
        return repManufactureStatusDaipai;
    }

    /**
     * 查询生产状况-待排产列表
     *
     * @param repManufactureStatusDaipai 生产状况-待排产
     * @return 生产状况-待排产
     */
    @Override
    public List<RepManufactureStatusDaipai> selectRepManufactureStatusDaipaiList(RepManufactureStatusDaipai repManufactureStatusDaipai) {
        return repManufactureStatusDaipaiMapper.selectRepManufactureStatusDaipaiList(repManufactureStatusDaipai);
    }

    /**
     * 新增生产状况-待排产
     * 需要注意编码重复校验
     *
     * @param repManufactureStatusDaipai 生产状况-待排产
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepManufactureStatusDaipai(RepManufactureStatusDaipai repManufactureStatusDaipai) {
        int row = repManufactureStatusDaipaiMapper.insert(repManufactureStatusDaipai);
        return row;
    }

    /**
     * 批量删除生产状况-待排产
     *
     * @param dataRecordSids 需要删除的生产状况-待排产ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepManufactureStatusDaipaiByIds(List<Long> dataRecordSids) {
        return repManufactureStatusDaipaiMapper.deleteBatchIds(dataRecordSids);
    }
}
