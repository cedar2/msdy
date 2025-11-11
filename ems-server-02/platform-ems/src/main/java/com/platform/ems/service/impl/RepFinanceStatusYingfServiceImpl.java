package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusYingfMapper;
import com.platform.ems.domain.RepFinanceStatusYingf;
import com.platform.ems.service.IRepFinanceStatusYingfService;

/**
 * 财务状况-供应商-应付Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusYingfServiceImpl extends ServiceImpl<RepFinanceStatusYingfMapper, RepFinanceStatusYingf> implements IRepFinanceStatusYingfService {
    @Autowired
    private RepFinanceStatusYingfMapper repFinanceStatusYingfMapper;

    /**
     * 查询财务状况-供应商-应付
     *
     * @param dataRecordSid 财务状况-供应商-应付ID
     * @return 财务状况-供应商-应付
     */
    @Override
    public RepFinanceStatusYingf selectRepFinanceStatusYingfById(Long dataRecordSid) {
        RepFinanceStatusYingf repFinanceStatusYingf = repFinanceStatusYingfMapper.selectRepFinanceStatusYingfById(dataRecordSid);
        return repFinanceStatusYingf;
    }

    /**
     * 查询财务状况-供应商-应付列表
     *
     * @param repFinanceStatusYingf 财务状况-供应商-应付
     * @return 财务状况-供应商-应付
     */
    @Override
    public List<RepFinanceStatusYingf> selectRepFinanceStatusYingfList(RepFinanceStatusYingf repFinanceStatusYingf) {
        return repFinanceStatusYingfMapper.selectRepFinanceStatusYingfList(repFinanceStatusYingf);
    }

    /**
     * 新增财务状况-供应商-应付
     * 需要注意编码重复校验
     *
     * @param repFinanceStatusYingf 财务状况-供应商-应付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatusYingf(RepFinanceStatusYingf repFinanceStatusYingf) {
        int row = repFinanceStatusYingfMapper.insert(repFinanceStatusYingf);
        return row;
    }

    /**
     * 批量删除财务状况-供应商-应付
     *
     * @param dataRecordSids 需要删除的财务状况-供应商-应付ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusYingfByIds(List<Long> dataRecordSids) {
        return repFinanceStatusYingfMapper.deleteBatchIds(dataRecordSids);
    }

}
