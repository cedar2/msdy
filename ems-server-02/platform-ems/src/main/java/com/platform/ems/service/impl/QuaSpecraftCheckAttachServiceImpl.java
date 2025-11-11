package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaSpecraftCheckAttach;
import com.platform.ems.mapper.QuaSpecraftCheckAttachMapper;
import com.platform.ems.service.IQuaSpecraftCheckAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 特殊工艺检测单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-12
 */
@Service
@SuppressWarnings("all")
public class QuaSpecraftCheckAttachServiceImpl extends ServiceImpl<QuaSpecraftCheckAttachMapper, QuaSpecraftCheckAttach> implements IQuaSpecraftCheckAttachService {
    @Autowired
    private QuaSpecraftCheckAttachMapper quaSpecraftCheckAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "特殊工艺检测单-附件";

    /**
     * 查询特殊工艺检测单-附件
     *
     * @param attachmentSid 特殊工艺检测单-附件ID
     * @return 特殊工艺检测单-附件
     */
    @Override
    public QuaSpecraftCheckAttach selectQuaSpecraftCheckAttachById(Long attachmentSid) {
        QuaSpecraftCheckAttach quaSpecraftCheckAttach = quaSpecraftCheckAttachMapper.selectQuaSpecraftCheckAttachById(attachmentSid);
        MongodbUtil.find(quaSpecraftCheckAttach);
        return quaSpecraftCheckAttach;
    }

    /**
     * 查询特殊工艺检测单-附件列表
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 特殊工艺检测单-附件
     */
    @Override
    public List<QuaSpecraftCheckAttach> selectQuaSpecraftCheckAttachList(QuaSpecraftCheckAttach quaSpecraftCheckAttach) {
        return quaSpecraftCheckAttachMapper.selectQuaSpecraftCheckAttachList(quaSpecraftCheckAttach);
    }

    /**
     * 新增特殊工艺检测单-附件
     * 需要注意编码重复校验
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaSpecraftCheckAttach(QuaSpecraftCheckAttach quaSpecraftCheckAttach) {
        int row = quaSpecraftCheckAttachMapper.insert(quaSpecraftCheckAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaSpecraftCheckAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改特殊工艺检测单-附件
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaSpecraftCheckAttach(QuaSpecraftCheckAttach quaSpecraftCheckAttach) {
        QuaSpecraftCheckAttach response = quaSpecraftCheckAttachMapper.selectQuaSpecraftCheckAttachById(quaSpecraftCheckAttach.getAttachmentSid());
        int row = quaSpecraftCheckAttachMapper.updateById(quaSpecraftCheckAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheckAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, quaSpecraftCheckAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更特殊工艺检测单-附件
     *
     * @param quaSpecraftCheckAttach 特殊工艺检测单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaSpecraftCheckAttach(QuaSpecraftCheckAttach quaSpecraftCheckAttach) {
        QuaSpecraftCheckAttach response = quaSpecraftCheckAttachMapper.selectQuaSpecraftCheckAttachById(quaSpecraftCheckAttach.getAttachmentSid());
        int row = quaSpecraftCheckAttachMapper.updateAllById(quaSpecraftCheckAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheckAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, quaSpecraftCheckAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除特殊工艺检测单-附件
     *
     * @param attachmentSids 需要删除的特殊工艺检测单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaSpecraftCheckAttachByIds(List<Long> attachmentSids) {
        return quaSpecraftCheckAttachMapper.deleteBatchIds(attachmentSids);
    }

}
