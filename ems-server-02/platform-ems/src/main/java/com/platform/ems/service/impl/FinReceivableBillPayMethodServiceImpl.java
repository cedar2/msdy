package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.FinReceivableBillPayMethodMapper;
import com.platform.ems.domain.FinReceivableBillPayMethod;
import com.platform.ems.service.IFinReceivableBillPayMethodService;

/**
 * 收款单-支付方式明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-06-23
 */
@Service
@SuppressWarnings("all")
public class FinReceivableBillPayMethodServiceImpl extends ServiceImpl<FinReceivableBillPayMethodMapper, FinReceivableBillPayMethod> implements IFinReceivableBillPayMethodService {
    @Autowired
    private FinReceivableBillPayMethodMapper finReceivableBillPayMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "收款单-支付方式明细";

    /**
     * 查询收款单-支付方式明细
     *
     * @param receivableBillPayMethodSid 收款单-支付方式明细ID
     * @return 收款单-支付方式明细
     */
    @Override
    public FinReceivableBillPayMethod selectFinReceivableBillPayMethodById(Long receivableBillPayMethodSid) {
        FinReceivableBillPayMethod finReceivableBillPayMethod = finReceivableBillPayMethodMapper.selectFinReceivableBillPayMethodById(receivableBillPayMethodSid);
        MongodbUtil.find(finReceivableBillPayMethod);
        return finReceivableBillPayMethod;
    }

    /**
     * 查询收款单-支付方式明细列表
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 收款单-支付方式明细
     */
    @Override
    public List<FinReceivableBillPayMethod> selectFinReceivableBillPayMethodList(FinReceivableBillPayMethod finReceivableBillPayMethod) {
        return finReceivableBillPayMethodMapper.selectFinReceivableBillPayMethodList(finReceivableBillPayMethod);
    }

    /**
     * 新增收款单-支付方式明细
     * 需要注意编码重复校验
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBillPayMethod(FinReceivableBillPayMethod finReceivableBillPayMethod) {
        int row = finReceivableBillPayMethodMapper.insert(finReceivableBillPayMethod);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finReceivableBillPayMethod.getReceivableBillPayMethodSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改收款单-支付方式明细
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBillPayMethod(FinReceivableBillPayMethod finReceivableBillPayMethod) {
        FinReceivableBillPayMethod response = finReceivableBillPayMethodMapper.selectFinReceivableBillPayMethodById(finReceivableBillPayMethod.getReceivableBillPayMethodSid());
        int row = finReceivableBillPayMethodMapper.updateById(finReceivableBillPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillPayMethod.getReceivableBillPayMethodSid(), BusinessType.UPDATE.getValue(), response, finReceivableBillPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 变更收款单-支付方式明细
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBillPayMethod(FinReceivableBillPayMethod finReceivableBillPayMethod) {
        FinReceivableBillPayMethod response = finReceivableBillPayMethodMapper.selectFinReceivableBillPayMethodById(finReceivableBillPayMethod.getReceivableBillPayMethodSid());
        int row = finReceivableBillPayMethodMapper.updateAllById(finReceivableBillPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBillPayMethod.getReceivableBillPayMethodSid(), BusinessType.CHANGE.getValue(), response, finReceivableBillPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单-支付方式明细
     *
     * @param receivableBillPayMethodSids 需要删除的收款单-支付方式明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillPayMethodByIds(List<Long> receivableBillPayMethodSids) {
        return finReceivableBillPayMethodMapper.deleteBatchIds(receivableBillPayMethodSids);
    }

}
