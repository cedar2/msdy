package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.RepManufactureStatusZaicha;
import com.platform.ems.mapper.RepManufactureStatusZaichaMapper;
import com.platform.ems.service.IRepManufactureStatusZaichaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 生产状况-在产Service业务层处理
 *
 * @author c
 * @date 2022-03-17
 */
@Service
@SuppressWarnings("all")
public class RepManufactureStatusZaichaServiceImpl extends ServiceImpl<RepManufactureStatusZaichaMapper, RepManufactureStatusZaicha> implements IRepManufactureStatusZaichaService {
    @Autowired
    private RepManufactureStatusZaichaMapper repManufactureStatusZaichaMapper;

    /**
     * 查询生产状况-在产
     *
     * @param dataRecordSid 生产状况-在产ID
     * @return 生产状况-在产
     */
    @Override
    public RepManufactureStatusZaicha selectRepManufactureStatusZaichaById(Long dataRecordSid) {
        RepManufactureStatusZaicha repManufactureStatusZaicha = repManufactureStatusZaichaMapper.selectRepManufactureStatusZaichaById(dataRecordSid);
        return repManufactureStatusZaicha;
    }

    /**
     * 查询生产状况-在产列表
     *
     * @param repManufactureStatusZaicha 生产状况-在产
     * @return 生产状况-在产
     */
    @Override
    public List<RepManufactureStatusZaicha> selectRepManufactureStatusZaichaList(RepManufactureStatusZaicha repManufactureStatusZaicha) {
        return repManufactureStatusZaichaMapper.selectRepManufactureStatusZaichaList(repManufactureStatusZaicha);
    }

    /**
     * 新增生产状况-在产
     * 需要注意编码重复校验
     *
     * @param repManufactureStatusZaicha 生产状况-在产
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepManufactureStatusZaicha(RepManufactureStatusZaicha repManufactureStatusZaicha) {
        int row = repManufactureStatusZaichaMapper.insert(repManufactureStatusZaicha);
        return row;
    }

    /**
     * 批量删除生产状况-在产
     *
     * @param dataRecordSids 需要删除的生产状况-在产ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepManufactureStatusZaichaByIds(List<Long> dataRecordSids) {
        return repManufactureStatusZaichaMapper.deleteBatchIds(dataRecordSids);
    }

}
