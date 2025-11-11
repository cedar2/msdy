package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManManufactureOrderConcernTaskAttach;
import com.platform.ems.mapper.ManManufactureOrderConcernTaskAttachMapper;
import com.platform.ems.service.IManManufactureOrderConcernTaskAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产订单关注事项-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-20
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderConcernTaskAttachServiceImpl extends ServiceImpl<ManManufactureOrderConcernTaskAttachMapper, ManManufactureOrderConcernTaskAttach> implements IManManufactureOrderConcernTaskAttachService {
    @Autowired
    private ManManufactureOrderConcernTaskAttachMapper ManManufactureOrderConcernTaskAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产订单关注事项-附件";

    /**
     * 查询生产订单关注事项-附件
     *
     * @param attachmentSid 生产订单关注事项-附件ID
     * @return 生产订单关注事项-附件
     */
    @Override
    public ManManufactureOrderConcernTaskAttach selectManManufactureOrderConcernTaskAttachById(Long attachmentSid) {
        ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach = ManManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachById(attachmentSid);
        MongodbUtil.find(ManManufactureOrderConcernTaskAttach);
        return ManManufactureOrderConcernTaskAttach;
    }

    /**
     * 查询生产订单关注事项-附件列表
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 生产订单关注事项-附件
     */
    @Override
    public List<ManManufactureOrderConcernTaskAttach> selectManManufactureOrderConcernTaskAttachList(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach) {
        return ManManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachList(ManManufactureOrderConcernTaskAttach);
    }

    /**
     * 新增生产订单关注事项-附件
     * 需要注意编码重复校验
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach) {
        int row = ManManufactureOrderConcernTaskAttachMapper.insert(ManManufactureOrderConcernTaskAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(ManManufactureOrderConcernTaskAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产订单关注事项-附件
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach) {
        ManManufactureOrderConcernTaskAttach response = ManManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachById(ManManufactureOrderConcernTaskAttach.getAttachmentSid());
        int row = ManManufactureOrderConcernTaskAttachMapper.updateById(ManManufactureOrderConcernTaskAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(ManManufactureOrderConcernTaskAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, ManManufactureOrderConcernTaskAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更生产订单关注事项-附件
     *
     * @param ManManufactureOrderConcernTaskAttach 生产订单关注事项-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach) {
        ManManufactureOrderConcernTaskAttach response = ManManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachById(ManManufactureOrderConcernTaskAttach.getAttachmentSid());
        int row = ManManufactureOrderConcernTaskAttachMapper.updateAllById(ManManufactureOrderConcernTaskAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(ManManufactureOrderConcernTaskAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, ManManufactureOrderConcernTaskAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产订单关注事项-附件
     *
     * @param attachmentSids 需要删除的生产订单关注事项-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOrderConcernTaskAttachByIds(List<Long> attachmentSids) {
        return ManManufactureOrderConcernTaskAttachMapper.deleteBatchIds(attachmentSids);
    }

}
