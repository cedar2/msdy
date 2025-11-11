package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusYingsMapper;
import com.platform.ems.domain.RepFinanceStatusYings;
import com.platform.ems.service.IRepFinanceStatusYingsService;

/**
 * 财务状况-客户-应收Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusYingsServiceImpl extends ServiceImpl<RepFinanceStatusYingsMapper, RepFinanceStatusYings> implements IRepFinanceStatusYingsService {
    @Autowired
    private RepFinanceStatusYingsMapper repFinanceStatusYingsMapper;

    /**
     * 查询财务状况-客户-应收
     *
     * @param dataRecordSid 财务状况-客户-应收ID
     * @return 财务状况-客户-应收
     */
    @Override
    public RepFinanceStatusYings selectRepFinanceStatusYingsById(Long dataRecordSid) {
        RepFinanceStatusYings repFinanceStatusYings = repFinanceStatusYingsMapper.selectRepFinanceStatusYingsById(dataRecordSid);
        return repFinanceStatusYings;
    }

    /**
     * 查询财务状况-客户-应收列表
     *
     * @param repFinanceStatusYings 财务状况-客户-应收
     * @return 财务状况-客户-应收
     */
    @Override
    public List<RepFinanceStatusYings> selectRepFinanceStatusYingsList(RepFinanceStatusYings repFinanceStatusYings) {
        return repFinanceStatusYingsMapper.selectRepFinanceStatusYingsList(repFinanceStatusYings);
    }

    /**
     * 新增财务状况-客户-应收
     * 需要注意编码重复校验
     *
     * @param repFinanceStatusYings 财务状况-客户-应收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatusYings(RepFinanceStatusYings repFinanceStatusYings) {
        int row = repFinanceStatusYingsMapper.insert(repFinanceStatusYings);
        return row;
    }

    /**
     * 批量删除财务状况-客户-应收
     *
     * @param dataRecordSids 需要删除的财务状况-客户-应收ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusYingsByIds(List<Long> dataRecordSids) {
        return repFinanceStatusYingsMapper.deleteBatchIds(dataRecordSids);
    }

}
