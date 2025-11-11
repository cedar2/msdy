package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.RepManufactureStatusCipin;
import com.platform.ems.mapper.RepManufactureStatusCipinMapper;
import com.platform.ems.service.IRepManufactureStatusCipinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 生产状况-次品Service业务层处理
 *
 * @author c
 * @date 2022-03-17
 */
@Service
@SuppressWarnings("all")
public class RepManufactureStatusCipinServiceImpl extends ServiceImpl<RepManufactureStatusCipinMapper, RepManufactureStatusCipin> implements IRepManufactureStatusCipinService {
    @Autowired
    private RepManufactureStatusCipinMapper repManufactureStatusCipinMapper;

    /**
     * 查询生产状况-次品
     *
     * @param dataRecordSid 生产状况-次品ID
     * @return 生产状况-次品
     */
    @Override
    public RepManufactureStatusCipin selectRepManufactureStatusCipinById(Long dataRecordSid) {
        RepManufactureStatusCipin repManufactureStatusCipin = repManufactureStatusCipinMapper.selectRepManufactureStatusCipinById(dataRecordSid);
        return repManufactureStatusCipin;
    }

    /**
     * 查询生产状况-次品列表
     *
     * @param repManufactureStatusCipin 生产状况-次品
     * @return 生产状况-次品
     */
    @Override
    public List<RepManufactureStatusCipin> selectRepManufactureStatusCipinList(RepManufactureStatusCipin repManufactureStatusCipin) {
        return repManufactureStatusCipinMapper.selectRepManufactureStatusCipinList(repManufactureStatusCipin);
    }

    /**
     * 新增生产状况-次品
     * 需要注意编码重复校验
     *
     * @param repManufactureStatusCipin 生产状况-次品
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepManufactureStatusCipin(RepManufactureStatusCipin repManufactureStatusCipin) {
        int row = repManufactureStatusCipinMapper.insert(repManufactureStatusCipin);
        return row;
    }

    /**
     * 批量删除生产状况-次品
     *
     * @param dataRecordSids 需要删除的生产状况-次品ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepManufactureStatusCipinByIds(List<Long> dataRecordSids) {
        return repManufactureStatusCipinMapper.deleteBatchIds(dataRecordSids);
    }

}
