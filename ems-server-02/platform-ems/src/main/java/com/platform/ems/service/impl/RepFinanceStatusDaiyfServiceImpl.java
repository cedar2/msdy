package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusDaiyfMapper;
import com.platform.ems.domain.RepFinanceStatusDaiyf;
import com.platform.ems.service.IRepFinanceStatusDaiyfService;

/**
 * 财务状况-供应商-待预付Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusDaiyfServiceImpl extends ServiceImpl<RepFinanceStatusDaiyfMapper, RepFinanceStatusDaiyf> implements IRepFinanceStatusDaiyfService {
    @Autowired
    private RepFinanceStatusDaiyfMapper repFinanceStatusDaiyfMapper;

    /**
     * 查询财务状况-供应商-待预付
     *
     * @param dataRecordSid 财务状况-供应商-待预付ID
     * @return 财务状况-供应商-待预付
     */
    @Override
    public RepFinanceStatusDaiyf selectRepFinanceStatusDaiyfById(Long dataRecordSid) {
        RepFinanceStatusDaiyf repFinanceStatusDaiyf = repFinanceStatusDaiyfMapper.selectRepFinanceStatusDaiyfById(dataRecordSid);
        return repFinanceStatusDaiyf;
    }

    /**
     * 查询财务状况-供应商-待预付列表
     *
     * @param repFinanceStatusDaiyf 财务状况-供应商-待预付
     * @return 财务状况-供应商-待预付
     */
    @Override
    public List<RepFinanceStatusDaiyf> selectRepFinanceStatusDaiyfList(RepFinanceStatusDaiyf repFinanceStatusDaiyf) {
        return repFinanceStatusDaiyfMapper.selectRepFinanceStatusDaiyfList(repFinanceStatusDaiyf);
    }

    /**
     * 新增财务状况-供应商-待预付
     * 需要注意编码重复校验
     *
     * @param repFinanceStatusDaiyf 财务状况-供应商-待预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatusDaiyf(RepFinanceStatusDaiyf repFinanceStatusDaiyf) {
        int row = repFinanceStatusDaiyfMapper.insert(repFinanceStatusDaiyf);
        return row;
    }

    /**
     * 批量删除财务状况-供应商-待预付
     *
     * @param dataRecordSids 需要删除的财务状况-供应商-待预付ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusDaiyfByIds(List<Long> dataRecordSids) {
        return repFinanceStatusDaiyfMapper.deleteBatchIds(dataRecordSids);
    }

}
