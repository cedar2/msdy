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

import com.platform.ems.mapper.PurInquiryAttachMapper;
import com.platform.ems.domain.PurInquiryAttach;
import com.platform.ems.service.IPurInquiryAttachService;

/**
 * 物料询价单-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Service
@SuppressWarnings("all")
public class PurInquiryAttachServiceImpl extends ServiceImpl<PurInquiryAttachMapper, PurInquiryAttach> implements IPurInquiryAttachService {
    @Autowired
    private PurInquiryAttachMapper purInquiryAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "物料询价单-附件";

    /**
     * 查询物料询价单-附件
     *
     * @param inquiryAttachmentSid 物料询价单-附件ID
     * @return 物料询价单-附件
     */
    @Override
    public PurInquiryAttach selectPurInquiryAttachById(Long inquiryAttachmentSid) {
        PurInquiryAttach purInquiryAttach = purInquiryAttachMapper.selectPurInquiryAttachById(inquiryAttachmentSid);
        MongodbUtil.find(purInquiryAttach);
        return purInquiryAttach;
    }

    /**
     * 查询物料询价单-附件列表
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 物料询价单-附件
     */
    @Override
    public List<PurInquiryAttach> selectPurInquiryAttachList(PurInquiryAttach purInquiryAttach) {
        return purInquiryAttachMapper.selectPurInquiryAttachList(purInquiryAttach);
    }

    /**
     * 新增物料询价单-附件
     * 需要注意编码重复校验
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurInquiryAttach(PurInquiryAttach purInquiryAttach) {
        int row = purInquiryAttachMapper.insert(purInquiryAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purInquiryAttach.getInquiryAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改物料询价单-附件
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurInquiryAttach(PurInquiryAttach purInquiryAttach) {
        PurInquiryAttach response = purInquiryAttachMapper.selectPurInquiryAttachById(purInquiryAttach.getInquiryAttachmentSid());
        purInquiryAttach.setUpdaterAccount(null).setUpdateDate(null);
        int row = purInquiryAttachMapper.updateById(purInquiryAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purInquiryAttach.getInquiryAttachmentSid(), BusinessType.UPDATE.ordinal(), response, purInquiryAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更物料询价单-附件
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurInquiryAttach(PurInquiryAttach purInquiryAttach) {
        PurInquiryAttach response = purInquiryAttachMapper.selectPurInquiryAttachById(purInquiryAttach.getInquiryAttachmentSid());
        int row = purInquiryAttachMapper.updateAllById(purInquiryAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purInquiryAttach.getInquiryAttachmentSid(), BusinessType.CHANGE.ordinal(), response, purInquiryAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料询价单-附件
     *
     * @param inquiryAttachmentSids 需要删除的物料询价单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurInquiryAttachByIds(List<Long> inquiryAttachmentSids) {
        return purInquiryAttachMapper.deleteBatchIds(inquiryAttachmentSids);
    }

}
