package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.DevMakeSampleFormAttach;
import com.platform.ems.mapper.DevMakeSampleFormAttachMapper;
import com.platform.ems.service.IDevMakeSampleFormAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 打样准许单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2022-03-24
 */
@Service
@SuppressWarnings("all")
public class DevMakeSampleFormAttachServiceImpl extends ServiceImpl<DevMakeSampleFormAttachMapper, DevMakeSampleFormAttach> implements IDevMakeSampleFormAttachService {
    @Autowired
    private DevMakeSampleFormAttachMapper devMakeSampleFormAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "打样准许单-附件";

    /**
     * 查询打样准许单-附件
     *
     * @param attachmentSid 打样准许单-附件ID
     * @return 打样准许单-附件
     */
    @Override
    public DevMakeSampleFormAttach selectDevMakeSampleFormAttachById(Long attachmentSid) {
        DevMakeSampleFormAttach devMakeSampleFormAttach = devMakeSampleFormAttachMapper.selectDevMakeSampleFormAttachById(attachmentSid);
        MongodbUtil.find(devMakeSampleFormAttach);
        return devMakeSampleFormAttach;
    }

    /**
     * 查询打样准许单-附件列表
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 打样准许单-附件
     */
    @Override
    public List<DevMakeSampleFormAttach> selectDevMakeSampleFormAttachList(DevMakeSampleFormAttach devMakeSampleFormAttach) {
        return devMakeSampleFormAttachMapper.selectDevMakeSampleFormAttachList(devMakeSampleFormAttach);
    }

    /**
     * 新增打样准许单-附件
     * 需要注意编码重复校验
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevMakeSampleFormAttach(DevMakeSampleFormAttach devMakeSampleFormAttach) {
        int row = devMakeSampleFormAttachMapper.insert(devMakeSampleFormAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(devMakeSampleFormAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改打样准许单-附件
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevMakeSampleFormAttach(DevMakeSampleFormAttach devMakeSampleFormAttach) {
        DevMakeSampleFormAttach response = devMakeSampleFormAttachMapper.selectDevMakeSampleFormAttachById(devMakeSampleFormAttach.getAttachmentSid());
        int row = devMakeSampleFormAttachMapper.updateById(devMakeSampleFormAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(devMakeSampleFormAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, devMakeSampleFormAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更打样准许单-附件
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevMakeSampleFormAttach(DevMakeSampleFormAttach devMakeSampleFormAttach) {
        DevMakeSampleFormAttach response = devMakeSampleFormAttachMapper.selectDevMakeSampleFormAttachById(devMakeSampleFormAttach.getAttachmentSid());
        int row = devMakeSampleFormAttachMapper.updateAllById(devMakeSampleFormAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(devMakeSampleFormAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, devMakeSampleFormAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除打样准许单-附件
     *
     * @param attachmentSids 需要删除的打样准许单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevMakeSampleFormAttachByIds(List<Long> attachmentSids) {
        return devMakeSampleFormAttachMapper.deleteBatchIds(attachmentSids);
    }

}
