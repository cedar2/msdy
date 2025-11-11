package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusDaiysMapper;
import com.platform.ems.domain.RepFinanceStatusDaiys;
import com.platform.ems.service.IRepFinanceStatusDaiysService;

/**
 * 财务状况-客户-待预收Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusDaiysServiceImpl extends ServiceImpl<RepFinanceStatusDaiysMapper, RepFinanceStatusDaiys> implements IRepFinanceStatusDaiysService {
    @Autowired
    private RepFinanceStatusDaiysMapper repFinanceStatusDaiysMapper;

    /**
     * 查询财务状况-客户-待预收
     *
     * @param dataRecordSid 财务状况-客户-待预收ID
     * @return 财务状况-客户-待预收
     */
    @Override
    public RepFinanceStatusDaiys selectRepFinanceStatusDaiysById(Long dataRecordSid) {
        RepFinanceStatusDaiys repFinanceStatusDaiys = repFinanceStatusDaiysMapper.selectRepFinanceStatusDaiysById(dataRecordSid);
        return repFinanceStatusDaiys;
    }

    /**
     * 查询财务状况-客户-待预收列表
     *
     * @param repFinanceStatusDaiys 财务状况-客户-待预收
     * @return 财务状况-客户-待预收
     */
    @Override
    public List<RepFinanceStatusDaiys> selectRepFinanceStatusDaiysList(RepFinanceStatusDaiys repFinanceStatusDaiys) {
        return repFinanceStatusDaiysMapper.selectRepFinanceStatusDaiysList(repFinanceStatusDaiys);
    }

    /**
     * 新增财务状况-客户-待预收
     * 需要注意编码重复校验
     *
     * @param repFinanceStatusDaiys 财务状况-客户-待预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatusDaiys(RepFinanceStatusDaiys repFinanceStatusDaiys) {
        int row = repFinanceStatusDaiysMapper.insert(repFinanceStatusDaiys);
        return row;
    }

    /**
     * 批量删除财务状况-客户-待预收
     *
     * @param dataRecordSids 需要删除的财务状况-客户-待预收ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusDaiysByIds(List<Long> dataRecordSids) {
        return repFinanceStatusDaiysMapper.deleteBatchIds(dataRecordSids);
    }

}
