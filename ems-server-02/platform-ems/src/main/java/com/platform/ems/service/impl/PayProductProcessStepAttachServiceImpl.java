package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PayProductProcessStepAttach;
import com.platform.ems.mapper.PayProductProcessStepAttachMapper;
import com.platform.ems.service.IPayProductProcessStepAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品道序-附件Service业务层处理
 *
 * @author c
 * @date 2021-09-08
 */
@Service
@SuppressWarnings("all")
public class PayProductProcessStepAttachServiceImpl extends ServiceImpl<PayProductProcessStepAttachMapper, PayProductProcessStepAttach> implements IPayProductProcessStepAttachService {
    @Autowired
    private PayProductProcessStepAttachMapper payProductProcessStepAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "商品道序-附件";

    /**
     * 查询商品道序-附件
     *
     * @param attachmentSid 商品道序-附件ID
     * @return 商品道序-附件
     */
    @Override
    public PayProductProcessStepAttach selectPayProductProcessStepAttachById(Long attachmentSid) {
        PayProductProcessStepAttach payProductProcessStepAttach = payProductProcessStepAttachMapper.selectPayProductProcessStepAttachById(attachmentSid);
        MongodbUtil.find(payProductProcessStepAttach);
        return payProductProcessStepAttach;
    }

    /**
     * 查询商品道序-附件列表
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 商品道序-附件
     */
    @Override
    public List<PayProductProcessStepAttach> selectPayProductProcessStepAttachList(PayProductProcessStepAttach payProductProcessStepAttach) {
        return payProductProcessStepAttachMapper.selectPayProductProcessStepAttachList(payProductProcessStepAttach);
    }

    /**
     * 新增商品道序-附件
     * 需要注意编码重复校验
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayProductProcessStepAttach(PayProductProcessStepAttach payProductProcessStepAttach) {
        int row = payProductProcessStepAttachMapper.insert(payProductProcessStepAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payProductProcessStepAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改商品道序-附件
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProductProcessStepAttach(PayProductProcessStepAttach payProductProcessStepAttach) {
        PayProductProcessStepAttach response = payProductProcessStepAttachMapper.selectPayProductProcessStepAttachById(payProductProcessStepAttach.getAttachmentSid());
        int row = payProductProcessStepAttachMapper.updateById(payProductProcessStepAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProductProcessStepAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, payProductProcessStepAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更商品道序-附件
     *
     * @param payProductProcessStepAttach 商品道序-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProductProcessStepAttach(PayProductProcessStepAttach payProductProcessStepAttach) {
        PayProductProcessStepAttach response = payProductProcessStepAttachMapper.selectPayProductProcessStepAttachById(payProductProcessStepAttach.getAttachmentSid());
        int row = payProductProcessStepAttachMapper.updateAllById(payProductProcessStepAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProductProcessStepAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, payProductProcessStepAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品道序-附件
     *
     * @param attachmentSids 需要删除的商品道序-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProductProcessStepAttachByIds(List<Long> attachmentSids) {
        return payProductProcessStepAttachMapper.deleteBatchIds(attachmentSids);
    }
}
