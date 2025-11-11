package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepFinanceStatusMapper;
import com.platform.ems.domain.RepFinanceStatus;
import com.platform.ems.service.IRepFinanceStatusService;

/**
 * 财务状况Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepFinanceStatusServiceImpl extends ServiceImpl<RepFinanceStatusMapper, RepFinanceStatus> implements IRepFinanceStatusService {
    @Autowired
    private RepFinanceStatusMapper repFinanceStatusMapper;

    /**
     * 查询财务状况
     *
     * @param dataRecordSid 财务状况ID
     * @return 财务状况
     */
    @Override
    public RepFinanceStatus selectRepFinanceStatusById(Long dataRecordSid) {
        RepFinanceStatus repFinanceStatus = repFinanceStatusMapper.selectRepFinanceStatusById(dataRecordSid);
        return repFinanceStatus;
    }

    /**
     * 查询财务状况列表
     *
     * @param repFinanceStatus 财务状况
     * @return 财务状况
     */
    @Override
    public List<RepFinanceStatus> selectRepFinanceStatusList(RepFinanceStatus repFinanceStatus) {
        return repFinanceStatusMapper.selectRepFinanceStatusList(repFinanceStatus);
    }

    /**
     * 新增财务状况
     * 需要注意编码重复校验
     *
     * @param repFinanceStatus 财务状况
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepFinanceStatus(RepFinanceStatus repFinanceStatus) {
        int row = repFinanceStatusMapper.insert(repFinanceStatus);
        return row;
    }

    /**
     * 批量删除财务状况
     *
     * @param dataRecordSids 需要删除的财务状况ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepFinanceStatusByIds(List<Long> dataRecordSids) {
        return repFinanceStatusMapper.deleteBatchIds(dataRecordSids);
    }

}
