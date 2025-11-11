package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusDaixysMapper;
import com.platform.ems.domain.RepFinanceStatusDaixys;
import com.platform.ems.service.IRepFinanceStatusDaixysService;

/**
 * 财务状况-客户-待销已收Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusDaixysServiceImpl extends ServiceImpl<RepFinanceStatusDaixysMapper, RepFinanceStatusDaixys> implements IRepFinanceStatusDaixysService {
    @Autowired
    private RepFinanceStatusDaixysMapper repFinanceStatusDaixysMapper;

    /**
     * 查询财务状况-客户-待销已收
     *
     * @param dataRecordSid 财务状况-客户-待销已收ID
     * @return 财务状况-客户-待销已收
     */
    @Override
    public RepFinanceStatusDaixys selectRepFinanceStatusDaixysById(Long dataRecordSid) {
        RepFinanceStatusDaixys repFinanceStatusDaixys = repFinanceStatusDaixysMapper.selectRepFinanceStatusDaixysById(dataRecordSid);
        return repFinanceStatusDaixys;
    }

    /**
     * 查询财务状况-客户-待销已收列表
     *
     * @param repFinanceStatusDaixys 财务状况-客户-待销已收
     * @return 财务状况-客户-待销已收
     */
    @Override
    public List<RepFinanceStatusDaixys> selectRepFinanceStatusDaixysList(RepFinanceStatusDaixys repFinanceStatusDaixys) {
        return repFinanceStatusDaixysMapper.selectRepFinanceStatusDaixysList(repFinanceStatusDaixys);
    }

    /**
     * 新增财务状况-客户-待销已收
     * 需要注意编码重复校验
     *
     * @param repFinanceStatusDaixys 财务状况-客户-待销已收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatusDaixys(RepFinanceStatusDaixys repFinanceStatusDaixys) {
        int row = repFinanceStatusDaixysMapper.insert(repFinanceStatusDaixys);
        return row;
    }

    /**
     * 批量删除财务状况-客户-待销已收
     *
     * @param dataRecordSids 需要删除的财务状况-客户-待销已收ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusDaixysByIds(List<Long> dataRecordSids) {
        return repFinanceStatusDaixysMapper.deleteBatchIds(dataRecordSids);
    }

}
