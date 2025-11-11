package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusDaixyfMapper;
import com.platform.ems.domain.RepFinanceStatusDaixyf;
import com.platform.ems.service.IRepFinanceStatusDaixyfService;

/**
 * 财务状况-供应商-待销已付Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusDaixyfServiceImpl extends ServiceImpl<RepFinanceStatusDaixyfMapper, RepFinanceStatusDaixyf> implements IRepFinanceStatusDaixyfService {
    @Autowired
    private RepFinanceStatusDaixyfMapper repFinanceStatusDaixyfMapper;

    /**
     * 查询财务状况-供应商-待销已付
     *
     * @param dataRecordSid 财务状况-供应商-待销已付ID
     * @return 财务状况-供应商-待销已付
     */
    @Override
    public RepFinanceStatusDaixyf selectRepFinanceStatusDaixyfById(Long dataRecordSid) {
        RepFinanceStatusDaixyf repFinanceStatusDaixyf = repFinanceStatusDaixyfMapper.selectRepFinanceStatusDaixyfById(dataRecordSid);
        return repFinanceStatusDaixyf;
    }

    /**
     * 查询财务状况-供应商-待销已付列表
     *
     * @param repFinanceStatusDaixyf 财务状况-供应商-待销已付
     * @return 财务状况-供应商-待销已付
     */
    @Override
    public List<RepFinanceStatusDaixyf> selectRepFinanceStatusDaixyfList(RepFinanceStatusDaixyf repFinanceStatusDaixyf) {
        return repFinanceStatusDaixyfMapper.selectRepFinanceStatusDaixyfList(repFinanceStatusDaixyf);
    }

    /**
     * 新增财务状况-供应商-待销已付
     * 需要注意编码重复校验
     *
     * @param repFinanceStatusDaixyf 财务状况-供应商-待销已付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatusDaixyf(RepFinanceStatusDaixyf repFinanceStatusDaixyf) {
        int row = repFinanceStatusDaixyfMapper.insert(repFinanceStatusDaixyf);
        return row;
    }

    /**
     * 批量删除财务状况-供应商-待销已付
     *
     * @param dataRecordSids 需要删除的财务状况-供应商-待销已付ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusDaixyfByIds(List<Long> dataRecordSids) {
        return repFinanceStatusDaixyfMapper.deleteBatchIds(dataRecordSids);
    }

}
