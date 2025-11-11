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
import com.platform.ems.mapper.PurOutsourceInquiryAttachMapper;
import com.platform.ems.domain.PurOutsourceInquiryAttach;
import com.platform.ems.service.IPurOutsourceInquiryAttachService;

/**
 * 加工询价单-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Service
@SuppressWarnings("all")
public class PurOutsourceInquiryAttachServiceImpl extends ServiceImpl<PurOutsourceInquiryAttachMapper, PurOutsourceInquiryAttach> implements IPurOutsourceInquiryAttachService {
    @Autowired
    private PurOutsourceInquiryAttachMapper purOutsourceInquiryAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "加工询价单-附件";

    /**
     * 查询加工询价单-附件
     *
     * @param outsourceInquiryAttachmentSid 加工询价单-附件ID
     * @return 加工询价单-附件
     */
    @Override
    public PurOutsourceInquiryAttach selectPurOutsourceInquiryAttachById(Long outsourceInquiryAttachmentSid) {
        PurOutsourceInquiryAttach purOutsourceInquiryAttach = purOutsourceInquiryAttachMapper.selectPurOutsourceInquiryAttachById(outsourceInquiryAttachmentSid);
        MongodbUtil.find(purOutsourceInquiryAttach);
        return purOutsourceInquiryAttach;
    }

    /**
     * 查询加工询价单-附件列表
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 加工询价单-附件
     */
    @Override
    public List<PurOutsourceInquiryAttach> selectPurOutsourceInquiryAttachList(PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        return purOutsourceInquiryAttachMapper.selectPurOutsourceInquiryAttachList(purOutsourceInquiryAttach);
    }

    /**
     * 新增加工询价单-附件
     * 需要注意编码重复校验
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceInquiryAttach(PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        int row = purOutsourceInquiryAttachMapper.insert(purOutsourceInquiryAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purOutsourceInquiryAttach.getOutsourceInquiryAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改加工询价单-附件
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceInquiryAttach(PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        PurOutsourceInquiryAttach response = purOutsourceInquiryAttachMapper.selectPurOutsourceInquiryAttachById(purOutsourceInquiryAttach.getOutsourceInquiryAttachmentSid());
        purOutsourceInquiryAttach.setUpdaterAccount(null).setUpdateDate(null);
        int row = purOutsourceInquiryAttachMapper.updateById(purOutsourceInquiryAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purOutsourceInquiryAttach.getOutsourceInquiryAttachmentSid(), BusinessType.UPDATE.ordinal(), response, purOutsourceInquiryAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更加工询价单-附件
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourceInquiryAttach(PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        PurOutsourceInquiryAttach response = purOutsourceInquiryAttachMapper.selectPurOutsourceInquiryAttachById(purOutsourceInquiryAttach.getOutsourceInquiryAttachmentSid());
        int row = purOutsourceInquiryAttachMapper.updateAllById(purOutsourceInquiryAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purOutsourceInquiryAttach.getOutsourceInquiryAttachmentSid(), BusinessType.CHANGE.ordinal(), response, purOutsourceInquiryAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除加工询价单-附件
     *
     * @param outsourceInquiryAttachmentSids 需要删除的加工询价单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceInquiryAttachByIds(List<Long> outsourceInquiryAttachmentSids) {
        return purOutsourceInquiryAttachMapper.deleteBatchIds(outsourceInquiryAttachmentSids);
    }
}
