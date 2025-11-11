package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManManufactureDefectiveAttach;
import com.platform.ems.mapper.ManManufactureDefectiveAttachMapper;
import com.platform.ems.service.IManManufactureDefectiveAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产次品台账-附件Service业务层处理
 *
 * @author c
 * @date 2022-03-02
 */
@Service
@SuppressWarnings("all")
public class ManManufactureDefectiveAttachServiceImpl extends ServiceImpl<ManManufactureDefectiveAttachMapper, ManManufactureDefectiveAttach> implements IManManufactureDefectiveAttachService {
    @Autowired
    private ManManufactureDefectiveAttachMapper manManufactureDefectiveAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产次品台账-附件";

    /**
     * 查询生产次品台账-附件
     *
     * @param attachmentSid 生产次品台账-附件ID
     * @return 生产次品台账-附件
     */
    @Override
    public ManManufactureDefectiveAttach selectManManufactureDefectiveAttachById(Long attachmentSid) {
        ManManufactureDefectiveAttach manManufactureDefectiveAttach = manManufactureDefectiveAttachMapper.selectManManufactureDefectiveAttachById(attachmentSid);
        MongodbUtil.find(manManufactureDefectiveAttach);
        return manManufactureDefectiveAttach;
    }

    /**
     * 查询生产次品台账-附件列表
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 生产次品台账-附件
     */
    @Override
    public List<ManManufactureDefectiveAttach> selectManManufactureDefectiveAttachList(ManManufactureDefectiveAttach manManufactureDefectiveAttach) {
        return manManufactureDefectiveAttachMapper.selectManManufactureDefectiveAttachList(manManufactureDefectiveAttach);
    }

    /**
     * 新增生产次品台账-附件
     * 需要注意编码重复校验
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureDefectiveAttach(ManManufactureDefectiveAttach manManufactureDefectiveAttach) {
        int row = manManufactureDefectiveAttachMapper.insert(manManufactureDefectiveAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manManufactureDefectiveAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产次品台账-附件
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureDefectiveAttach(ManManufactureDefectiveAttach manManufactureDefectiveAttach) {
        ManManufactureDefectiveAttach response = manManufactureDefectiveAttachMapper.selectManManufactureDefectiveAttachById(manManufactureDefectiveAttach.getAttachmentSid());
        int row = manManufactureDefectiveAttachMapper.updateById(manManufactureDefectiveAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manManufactureDefectiveAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, manManufactureDefectiveAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更生产次品台账-附件
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureDefectiveAttach(ManManufactureDefectiveAttach manManufactureDefectiveAttach) {
        ManManufactureDefectiveAttach response = manManufactureDefectiveAttachMapper.selectManManufactureDefectiveAttachById(manManufactureDefectiveAttach.getAttachmentSid());
        int row = manManufactureDefectiveAttachMapper.updateAllById(manManufactureDefectiveAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manManufactureDefectiveAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, manManufactureDefectiveAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产次品台账-附件
     *
     * @param attachmentSids 需要删除的生产次品台账-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureDefectiveAttachByIds(List<Long> attachmentSids) {
        return manManufactureDefectiveAttachMapper.deleteBatchIds(attachmentSids);
    }
}
