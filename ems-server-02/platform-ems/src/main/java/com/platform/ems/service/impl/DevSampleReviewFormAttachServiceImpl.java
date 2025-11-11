package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.DevSampleReviewFormAttach;
import com.platform.ems.mapper.DevSampleReviewFormAttachMapper;
import com.platform.ems.service.IDevSampleReviewFormAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 样品评审单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2022-03-23
 */
@Service
@SuppressWarnings("all")
public class DevSampleReviewFormAttachServiceImpl extends ServiceImpl<DevSampleReviewFormAttachMapper, DevSampleReviewFormAttach> implements IDevSampleReviewFormAttachService {
    @Autowired
    private DevSampleReviewFormAttachMapper devSampleReviewFormAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "样品评审单-附件";

    /**
     * 查询样品评审单-附件
     *
     * @param attachmentSid 样品评审单-附件ID
     * @return 样品评审单-附件
     */
    @Override
    public DevSampleReviewFormAttach selectDevSampleReviewFormAttachById(Long attachmentSid) {
        DevSampleReviewFormAttach devSampleReviewFormAttach = devSampleReviewFormAttachMapper.selectDevSampleReviewFormAttachById(attachmentSid);
        MongodbUtil.find(devSampleReviewFormAttach);
        return devSampleReviewFormAttach;
    }

    /**
     * 查询样品评审单-附件列表
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 样品评审单-附件
     */
    @Override
    public List<DevSampleReviewFormAttach> selectDevSampleReviewFormAttachList(DevSampleReviewFormAttach devSampleReviewFormAttach) {
        return devSampleReviewFormAttachMapper.selectDevSampleReviewFormAttachList(devSampleReviewFormAttach);
    }

    /**
     * 新增样品评审单-附件
     * 需要注意编码重复校验
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevSampleReviewFormAttach(DevSampleReviewFormAttach devSampleReviewFormAttach) {
        int row = devSampleReviewFormAttachMapper.insert(devSampleReviewFormAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(devSampleReviewFormAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改样品评审单-附件
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevSampleReviewFormAttach(DevSampleReviewFormAttach devSampleReviewFormAttach) {
        DevSampleReviewFormAttach response = devSampleReviewFormAttachMapper.selectDevSampleReviewFormAttachById(devSampleReviewFormAttach.getAttachmentSid());
        int row = devSampleReviewFormAttachMapper.updateById(devSampleReviewFormAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(devSampleReviewFormAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, devSampleReviewFormAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更样品评审单-附件
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevSampleReviewFormAttach(DevSampleReviewFormAttach devSampleReviewFormAttach) {
        DevSampleReviewFormAttach response = devSampleReviewFormAttachMapper.selectDevSampleReviewFormAttachById(devSampleReviewFormAttach.getAttachmentSid());
        int row = devSampleReviewFormAttachMapper.updateAllById(devSampleReviewFormAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(devSampleReviewFormAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, devSampleReviewFormAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除样品评审单-附件
     *
     * @param attachmentSids 需要删除的样品评审单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevSampleReviewFormAttachByIds(List<Long> attachmentSids) {
        return devSampleReviewFormAttachMapper.deleteBatchIds(attachmentSids);
    }

}
