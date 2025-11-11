package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaRawmatCheckAttach;
import com.platform.ems.mapper.QuaRawmatCheckAttachMapper;
import com.platform.ems.service.IQuaRawmatCheckAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 面辅料检测单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@Service
@SuppressWarnings("all")
public class QuaRawmatCheckAttachServiceImpl extends ServiceImpl<QuaRawmatCheckAttachMapper, QuaRawmatCheckAttach> implements IQuaRawmatCheckAttachService {
    @Autowired
    private QuaRawmatCheckAttachMapper quaRawmatCheckAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "面辅料检测单-附件";

    /**
     * 查询面辅料检测单-附件
     *
     * @param attachmentSid 面辅料检测单-附件ID
     * @return 面辅料检测单-附件
     */
    @Override
    public QuaRawmatCheckAttach selectQuaRawmatCheckAttachById(Long attachmentSid) {
        QuaRawmatCheckAttach quaRawmatCheckAttach = quaRawmatCheckAttachMapper.selectQuaRawmatCheckAttachById(attachmentSid);
        MongodbUtil.find(quaRawmatCheckAttach);
        return quaRawmatCheckAttach;
    }

    /**
     * 查询面辅料检测单-附件列表
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 面辅料检测单-附件
     */
    @Override
    public List<QuaRawmatCheckAttach> selectQuaRawmatCheckAttachList(QuaRawmatCheckAttach quaRawmatCheckAttach) {
        return quaRawmatCheckAttachMapper.selectQuaRawmatCheckAttachList(quaRawmatCheckAttach);
    }

    /**
     * 新增面辅料检测单-附件
     * 需要注意编码重复校验
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaRawmatCheckAttach(QuaRawmatCheckAttach quaRawmatCheckAttach) {
        int row = quaRawmatCheckAttachMapper.insert(quaRawmatCheckAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaRawmatCheckAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改面辅料检测单-附件
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaRawmatCheckAttach(QuaRawmatCheckAttach quaRawmatCheckAttach) {
        QuaRawmatCheckAttach response = quaRawmatCheckAttachMapper.selectQuaRawmatCheckAttachById(quaRawmatCheckAttach.getAttachmentSid());
        int row = quaRawmatCheckAttachMapper.updateById(quaRawmatCheckAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, quaRawmatCheckAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更面辅料检测单-附件
     *
     * @param quaRawmatCheckAttach 面辅料检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaRawmatCheckAttach(QuaRawmatCheckAttach quaRawmatCheckAttach) {
        QuaRawmatCheckAttach response = quaRawmatCheckAttachMapper.selectQuaRawmatCheckAttachById(quaRawmatCheckAttach.getAttachmentSid());
        int row = quaRawmatCheckAttachMapper.updateAllById(quaRawmatCheckAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, quaRawmatCheckAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除面辅料检测单-附件
     *
     * @param attachmentSids 需要删除的面辅料检测单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaRawmatCheckAttachByIds(List<Long> attachmentSids) {
        return quaRawmatCheckAttachMapper.deleteBatchIds(attachmentSids);
    }

}
