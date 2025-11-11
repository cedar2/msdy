package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.AssAssetRecordAttachMapper;
import com.platform.ems.domain.AssAssetRecordAttach;
import com.platform.ems.service.IAssAssetRecordAttachService;

/**
 * 资产台账-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class AssAssetRecordAttachServiceImpl extends ServiceImpl<AssAssetRecordAttachMapper, AssAssetRecordAttach> implements IAssAssetRecordAttachService {
    @Autowired
    private AssAssetRecordAttachMapper assAssetRecordAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "资产台账-附件";

    /**
     * 查询资产台账-附件
     *
     * @param assetAttachmentSid 资产台账-附件ID
     * @return 资产台账-附件
     */
    @Override
    public AssAssetRecordAttach selectAssAssetRecordAttachById(Long assetAttachmentSid) {
        AssAssetRecordAttach assAssetRecordAttach = assAssetRecordAttachMapper.selectAssAssetRecordAttachById(assetAttachmentSid);
        MongodbUtil.find(assAssetRecordAttach);
        return assAssetRecordAttach;
    }

    /**
     * 查询资产台账-附件列表
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 资产台账-附件
     */
    @Override
    public List<AssAssetRecordAttach> selectAssAssetRecordAttachList(AssAssetRecordAttach assAssetRecordAttach) {
        return assAssetRecordAttachMapper.selectAssAssetRecordAttachList(assAssetRecordAttach);
    }

    /**
     * 新增资产台账-附件
     * 需要注意编码重复校验
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAssAssetRecordAttach(AssAssetRecordAttach assAssetRecordAttach) {
        int row = assAssetRecordAttachMapper.insert(assAssetRecordAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new AssAssetRecordAttach(), assAssetRecordAttach);
            MongodbUtil.insertUserLog(assAssetRecordAttach.getAssetAttachmentSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改资产台账-附件
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAssAssetRecordAttach(AssAssetRecordAttach assAssetRecordAttach) {
        AssAssetRecordAttach response = assAssetRecordAttachMapper.selectAssAssetRecordAttachById(assAssetRecordAttach.getAssetAttachmentSid());
        int row = assAssetRecordAttachMapper.updateById(assAssetRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(assAssetRecordAttach.getAssetAttachmentSid(), BusinessType.UPDATE.getValue(), response, assAssetRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更资产台账-附件
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeAssAssetRecordAttach(AssAssetRecordAttach assAssetRecordAttach) {
        AssAssetRecordAttach response = assAssetRecordAttachMapper.selectAssAssetRecordAttachById(assAssetRecordAttach.getAssetAttachmentSid());
        int row = assAssetRecordAttachMapper.updateAllById(assAssetRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(assAssetRecordAttach.getAssetAttachmentSid(), BusinessType.CHANGE.getValue(), response, assAssetRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除资产台账-附件
     *
     * @param assetAttachmentSids 需要删除的资产台账-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAssAssetRecordAttachByIds(List<Long> assetAttachmentSids) {
        assetAttachmentSids.forEach(sid -> {
            AssAssetRecordAttach assAssetRecordAttach = assAssetRecordAttachMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(assAssetRecordAttach, new AssAssetRecordAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return assAssetRecordAttachMapper.deleteBatchIds(assetAttachmentSids);
    }

}
