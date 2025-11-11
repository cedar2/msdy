package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasLaboratoryAttach;
import com.platform.ems.mapper.BasLaboratoryAttachMapper;
import com.platform.ems.service.IBasLaboratoryAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验室-附件Service业务层处理
 *
 * @author c
 * @date 2022-03-31
 */
@Service
@SuppressWarnings("all")
public class BasLaboratoryAttachServiceImpl extends ServiceImpl<BasLaboratoryAttachMapper, BasLaboratoryAttach> implements IBasLaboratoryAttachService {
    @Autowired
    private BasLaboratoryAttachMapper basLaboratoryAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "实验室-附件";

    /**
     * 查询实验室-附件
     *
     * @param attachmentSid 实验室-附件ID
     * @return 实验室-附件
     */
    @Override
    public BasLaboratoryAttach selectBasLaboratoryAttachById(Long attachmentSid) {
        BasLaboratoryAttach basLaboratoryAttach = basLaboratoryAttachMapper.selectBasLaboratoryAttachById(attachmentSid);
        MongodbUtil.find(basLaboratoryAttach);
        return basLaboratoryAttach;
    }

    /**
     * 查询实验室-附件列表
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 实验室-附件
     */
    @Override
    public List<BasLaboratoryAttach> selectBasLaboratoryAttachList(BasLaboratoryAttach basLaboratoryAttach) {
        return basLaboratoryAttachMapper.selectBasLaboratoryAttachList(basLaboratoryAttach);
    }

    /**
     * 新增实验室-附件
     * 需要注意编码重复校验
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasLaboratoryAttach(BasLaboratoryAttach basLaboratoryAttach) {
        int row = basLaboratoryAttachMapper.insert(basLaboratoryAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basLaboratoryAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改实验室-附件
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasLaboratoryAttach(BasLaboratoryAttach basLaboratoryAttach) {
        BasLaboratoryAttach response = basLaboratoryAttachMapper.selectBasLaboratoryAttachById(basLaboratoryAttach.getAttachmentSid());
        int row = basLaboratoryAttachMapper.updateById(basLaboratoryAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basLaboratoryAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, basLaboratoryAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更实验室-附件
     *
     * @param basLaboratoryAttach 实验室-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasLaboratoryAttach(BasLaboratoryAttach basLaboratoryAttach) {
        BasLaboratoryAttach response = basLaboratoryAttachMapper.selectBasLaboratoryAttachById(basLaboratoryAttach.getAttachmentSid());
        int row = basLaboratoryAttachMapper.updateAllById(basLaboratoryAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basLaboratoryAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, basLaboratoryAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除实验室-附件
     *
     * @param attachmentSids 需要删除的实验室-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasLaboratoryAttachByIds(List<Long> attachmentSids) {
        return basLaboratoryAttachMapper.deleteBatchIds(attachmentSids);
    }

}
