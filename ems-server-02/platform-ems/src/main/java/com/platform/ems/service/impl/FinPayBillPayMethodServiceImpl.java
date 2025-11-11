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
import com.platform.ems.mapper.FinPayBillPayMethodMapper;
import com.platform.ems.domain.FinPayBillPayMethod;
import com.platform.ems.service.IFinPayBillPayMethodService;

/**
 * 付款单-支付方式明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-06-23
 */
@Service
@SuppressWarnings("all")
public class FinPayBillPayMethodServiceImpl extends ServiceImpl<FinPayBillPayMethodMapper, FinPayBillPayMethod> implements IFinPayBillPayMethodService {
    @Autowired
    private FinPayBillPayMethodMapper finPayBillPayMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "付款单-支付方式明细";

    /**
     * 查询付款单-支付方式明细
     *
     * @param payBillPayMethodSid 付款单-支付方式明细ID
     * @return 付款单-支付方式明细
     */
    @Override
    public FinPayBillPayMethod selectFinPayBillPayMethodById(Long payBillPayMethodSid) {
        FinPayBillPayMethod finPayBillPayMethod = finPayBillPayMethodMapper.selectFinPayBillPayMethodById(payBillPayMethodSid);
        MongodbUtil.find(finPayBillPayMethod);
        return finPayBillPayMethod;
    }

    /**
     * 查询付款单-支付方式明细列表
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 付款单-支付方式明细
     */
    @Override
    public List<FinPayBillPayMethod> selectFinPayBillPayMethodList(FinPayBillPayMethod finPayBillPayMethod) {
        return finPayBillPayMethodMapper.selectFinPayBillPayMethodList(finPayBillPayMethod);
    }

    /**
     * 新增付款单-支付方式明细
     * 需要注意编码重复校验
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBillPayMethod(FinPayBillPayMethod finPayBillPayMethod) {
        int row = finPayBillPayMethodMapper.insert(finPayBillPayMethod);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finPayBillPayMethod.getPayBillPayMethodSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改付款单-支付方式明细
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBillPayMethod(FinPayBillPayMethod finPayBillPayMethod) {
        FinPayBillPayMethod response = finPayBillPayMethodMapper.selectFinPayBillPayMethodById(finPayBillPayMethod.getPayBillPayMethodSid());
        int row = finPayBillPayMethodMapper.updateById(finPayBillPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finPayBillPayMethod.getPayBillPayMethodSid(), BusinessType.UPDATE.getValue(), response, finPayBillPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 变更付款单-支付方式明细
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBillPayMethod(FinPayBillPayMethod finPayBillPayMethod) {
        FinPayBillPayMethod response = finPayBillPayMethodMapper.selectFinPayBillPayMethodById(finPayBillPayMethod.getPayBillPayMethodSid());
        int row = finPayBillPayMethodMapper.updateAllById(finPayBillPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finPayBillPayMethod.getPayBillPayMethodSid(), BusinessType.CHANGE.getValue(), response, finPayBillPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款单-支付方式明细
     *
     * @param payBillPayMethodSids 需要删除的付款单-支付方式明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillPayMethodByIds(List<Long> payBillPayMethodSids) {
        return finPayBillPayMethodMapper.deleteBatchIds(payBillPayMethodSids);
    }

}
