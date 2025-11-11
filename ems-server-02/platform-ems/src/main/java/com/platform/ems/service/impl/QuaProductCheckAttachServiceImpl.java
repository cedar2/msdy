package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaProductCheckAttach;
import com.platform.ems.mapper.QuaProductCheckAttachMapper;
import com.platform.ems.service.IQuaProductCheckAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 成衣检测单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-13
 */
@Service
@SuppressWarnings("all")
public class QuaProductCheckAttachServiceImpl extends ServiceImpl<QuaProductCheckAttachMapper, QuaProductCheckAttach> implements IQuaProductCheckAttachService {
    @Autowired
    private QuaProductCheckAttachMapper quaProductCheckAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "成衣检测单-附件";

    /**
     * 查询成衣检测单-附件
     *
     * @param attachmentSid 成衣检测单-附件ID
     * @return 成衣检测单-附件
     */
    @Override
    public QuaProductCheckAttach selectQuaProductCheckAttachById(Long attachmentSid) {
        QuaProductCheckAttach quaProductCheckAttach = quaProductCheckAttachMapper.selectQuaProductCheckAttachById(attachmentSid);
        MongodbUtil.find(quaProductCheckAttach);
        return quaProductCheckAttach;
    }

    /**
     * 查询成衣检测单-附件列表
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 成衣检测单-附件
     */
    @Override
    public List<QuaProductCheckAttach> selectQuaProductCheckAttachList(QuaProductCheckAttach quaProductCheckAttach) {
        return quaProductCheckAttachMapper.selectQuaProductCheckAttachList(quaProductCheckAttach);
    }

    /**
     * 新增成衣检测单-附件
     * 需要注意编码重复校验
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaProductCheckAttach(QuaProductCheckAttach quaProductCheckAttach) {
        int row = quaProductCheckAttachMapper.insert(quaProductCheckAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaProductCheckAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改成衣检测单-附件
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaProductCheckAttach(QuaProductCheckAttach quaProductCheckAttach) {
        QuaProductCheckAttach response = quaProductCheckAttachMapper.selectQuaProductCheckAttachById(quaProductCheckAttach.getAttachmentSid());
        int row = quaProductCheckAttachMapper.updateById(quaProductCheckAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheckAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, quaProductCheckAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更成衣检测单-附件
     *
     * @param quaProductCheckAttach 成衣检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaProductCheckAttach(QuaProductCheckAttach quaProductCheckAttach) {
        QuaProductCheckAttach response = quaProductCheckAttachMapper.selectQuaProductCheckAttachById(quaProductCheckAttach.getAttachmentSid());
        int row = quaProductCheckAttachMapper.updateAllById(quaProductCheckAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheckAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, quaProductCheckAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除成衣检测单-附件
     *
     * @param attachmentSids 需要删除的成衣检测单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaProductCheckAttachByIds(List<Long> attachmentSids) {
        return quaProductCheckAttachMapper.deleteBatchIds(attachmentSids);
    }

}
