package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManManufactureOrderAttach;
import com.platform.ems.mapper.ManManufactureOrderAttachMapper;
import com.platform.ems.service.IManManufactureOrderAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产订单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-20
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderAttachServiceImpl extends ServiceImpl<ManManufactureOrderAttachMapper, ManManufactureOrderAttach> implements IManManufactureOrderAttachService {
    @Autowired
    private ManManufactureOrderAttachMapper manManufactureOrderAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产订单-附件";

    /**
     * 查询生产订单-附件
     *
     * @param attachmentSid 生产订单-附件ID
     * @return 生产订单-附件
     */
    @Override
    public ManManufactureOrderAttach selectManManufactureOrderAttachById(Long attachmentSid) {
        ManManufactureOrderAttach manManufactureOrderAttach = manManufactureOrderAttachMapper.selectManManufactureOrderAttachById(attachmentSid);
        MongodbUtil.find(manManufactureOrderAttach);
        return manManufactureOrderAttach;
    }

    /**
     * 查询生产订单-附件列表
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 生产订单-附件
     */
    @Override
    public List<ManManufactureOrderAttach> selectManManufactureOrderAttachList(ManManufactureOrderAttach manManufactureOrderAttach) {
        return manManufactureOrderAttachMapper.selectManManufactureOrderAttachList(manManufactureOrderAttach);
    }

    /**
     * 新增生产订单-附件
     * 需要注意编码重复校验
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOrderAttach(ManManufactureOrderAttach manManufactureOrderAttach) {
        int row = manManufactureOrderAttachMapper.insert(manManufactureOrderAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manManufactureOrderAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产订单-附件
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrderAttach(ManManufactureOrderAttach manManufactureOrderAttach) {
        ManManufactureOrderAttach response = manManufactureOrderAttachMapper.selectManManufactureOrderAttachById(manManufactureOrderAttach.getAttachmentSid());
        int row = manManufactureOrderAttachMapper.updateById(manManufactureOrderAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manManufactureOrderAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, manManufactureOrderAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更生产订单-附件
     *
     * @param manManufactureOrderAttach 生产订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureOrderAttach(ManManufactureOrderAttach manManufactureOrderAttach) {
        ManManufactureOrderAttach response = manManufactureOrderAttachMapper.selectManManufactureOrderAttachById(manManufactureOrderAttach.getAttachmentSid());
        int row = manManufactureOrderAttachMapper.updateAllById(manManufactureOrderAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manManufactureOrderAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, manManufactureOrderAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产订单-附件
     *
     * @param attachmentSids 需要删除的生产订单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOrderAttachByIds(List<Long> attachmentSids) {
        return manManufactureOrderAttachMapper.deleteBatchIds(attachmentSids);
    }

}
