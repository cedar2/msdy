package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepPurchaseStatusOutsourceProcessMapper;
import com.platform.ems.domain.RepPurchaseStatusOutsourceProcess;
import com.platform.ems.service.IRepPurchaseStatusOutsourceProcessService;

/**
 * 采购状况-外发加工结算Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepPurchaseStatusOutsourceProcessServiceImpl extends ServiceImpl<RepPurchaseStatusOutsourceProcessMapper, RepPurchaseStatusOutsourceProcess> implements IRepPurchaseStatusOutsourceProcessService {
    @Autowired
    private RepPurchaseStatusOutsourceProcessMapper repPurchaseStatusOutsourceProcessMapper;

    /**
     * 查询采购状况-外发加工结算
     *
     * @param dataRecordSid 采购状况-外发加工结算ID
     * @return 采购状况-外发加工结算
     */
    @Override
    public RepPurchaseStatusOutsourceProcess selectRepPurchaseStatusOutsourceProcessById(Long dataRecordSid) {
        RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess = repPurchaseStatusOutsourceProcessMapper.selectRepPurchaseStatusOutsourceProcessById(dataRecordSid);
        return repPurchaseStatusOutsourceProcess;
    }

    /**
     * 查询采购状况-外发加工结算列表
     *
     * @param repPurchaseStatusOutsourceProcess 采购状况-外发加工结算
     * @return 采购状况-外发加工结算
     */
    @Override
    public List<RepPurchaseStatusOutsourceProcess> selectRepPurchaseStatusOutsourceProcessList(RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess) {
        return repPurchaseStatusOutsourceProcessMapper.selectRepPurchaseStatusOutsourceProcessList(repPurchaseStatusOutsourceProcess);
    }

    /**
     * 新增采购状况-外发加工结算
     * 需要注意编码重复校验
     *
     * @param repPurchaseStatusOutsourceProcess 采购状况-外发加工结算
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepPurchaseStatusOutsourceProcess(RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess) {
        int row = repPurchaseStatusOutsourceProcessMapper.insert(repPurchaseStatusOutsourceProcess);
        return row;
    }

    /**
     * 批量删除采购状况-外发加工结算
     *
     * @param dataRecordSids 需要删除的采购状况-外发加工结算ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepPurchaseStatusOutsourceProcessByIds(List<Long> dataRecordSids) {
        return repPurchaseStatusOutsourceProcessMapper.deleteBatchIds(dataRecordSids);
    }

}
