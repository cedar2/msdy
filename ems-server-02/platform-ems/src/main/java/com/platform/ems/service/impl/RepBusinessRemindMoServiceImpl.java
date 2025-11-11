package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepBusinessRemindMoMapper;
import com.platform.ems.domain.RepBusinessRemindMo;
import com.platform.ems.service.IRepBusinessRemindMoService;

/**
 * 已逾期/即将到期-生产订单Service业务层处理
 *
 * @author chenkw
 * @date 2022-04-26
 */
@Service
@SuppressWarnings("all")
public class RepBusinessRemindMoServiceImpl extends ServiceImpl<RepBusinessRemindMoMapper, RepBusinessRemindMo> implements IRepBusinessRemindMoService {
    @Autowired
    private RepBusinessRemindMoMapper repBusinessRemindMoMapper;

    /**
     * 查询已逾期/即将到期-生产订单
     *
     * @param dataRecordSid 已逾期/即将到期-生产订单ID
     * @return 已逾期/即将到期-生产订单
     */
    @Override
    public RepBusinessRemindMo selectRepBusinessRemindMoById(Long dataRecordSid) {
        RepBusinessRemindMo repBusinessRemindMo = repBusinessRemindMoMapper.selectRepBusinessRemindMoById(dataRecordSid);
        return repBusinessRemindMo;
    }

    /**
     * 查询已逾期/即将到期-生产订单列表
     *
     * @param repBusinessRemindMo 已逾期/即将到期-生产订单
     * @return 已逾期/即将到期-生产订单
     */
    @Override
    public List<RepBusinessRemindMo> selectRepBusinessRemindMoList(RepBusinessRemindMo repBusinessRemindMo) {
        return repBusinessRemindMoMapper.selectRepBusinessRemindMoList(repBusinessRemindMo);
    }

    /**
     * 新增已逾期/即将到期-生产订单
     * 需要注意编码重复校验
     *
     * @param repBusinessRemindMo 已逾期/即将到期-生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepBusinessRemindMo(RepBusinessRemindMo repBusinessRemindMo) {
        int row = repBusinessRemindMoMapper.insert(repBusinessRemindMo);
        return row;
    }

    /**
     * 批量删除已逾期/即将到期-生产订单
     *
     * @param dataRecordSids 需要删除的已逾期/即将到期-生产订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepBusinessRemindMoByIds(List<Long> dataRecordSids) {
        return repBusinessRemindMoMapper.deleteBatchIds(dataRecordSids);
    }

    /**
     * 查询已逾期/即将到期生产订单统计报表
     *
     * @param repBusinessRemindMo 已逾期/即将到期生产订单报表
     * @return 已逾期/即将到期生产订单
     */
    @Override
    public List<RepBusinessRemindMo> getCountForm(RepBusinessRemindMo repBusinessRemindMo) {
        return repBusinessRemindMoMapper.getCountForm(repBusinessRemindMo);
    }

}
