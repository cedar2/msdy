package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasShopAttach;
import com.platform.ems.mapper.BasShopAttachMapper;
import com.platform.ems.service.IBasShopAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺-附件Service业务层处理
 *
 * @author c
 * @date 2022-03-31
 */
@Service
@SuppressWarnings("all")
public class BasShopAttachServiceImpl extends ServiceImpl<BasShopAttachMapper, BasShopAttach> implements IBasShopAttachService {
    @Autowired
    private BasShopAttachMapper basShopAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "店铺-附件";

    /**
     * 查询店铺-附件
     *
     * @param attachmentSid 店铺-附件ID
     * @return 店铺-附件
     */
    @Override
    public BasShopAttach selectBasShopAttachById(Long attachmentSid) {
        BasShopAttach basShopAttach = basShopAttachMapper.selectBasShopAttachById(attachmentSid);
        MongodbUtil.find(basShopAttach);
        return basShopAttach;
    }

    /**
     * 查询店铺-附件列表
     *
     * @param basShopAttach 店铺-附件
     * @return 店铺-附件
     */
    @Override
    public List<BasShopAttach> selectBasShopAttachList(BasShopAttach basShopAttach) {
        return basShopAttachMapper.selectBasShopAttachList(basShopAttach);
    }

    /**
     * 新增店铺-附件
     * 需要注意编码重复校验
     *
     * @param basShopAttach 店铺-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasShopAttach(BasShopAttach basShopAttach) {
        int row = basShopAttachMapper.insert(basShopAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basShopAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改店铺-附件
     *
     * @param basShopAttach 店铺-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasShopAttach(BasShopAttach basShopAttach) {
        BasShopAttach response = basShopAttachMapper.selectBasShopAttachById(basShopAttach.getAttachmentSid());
        int row = basShopAttachMapper.updateById(basShopAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basShopAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, basShopAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更店铺-附件
     *
     * @param basShopAttach 店铺-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasShopAttach(BasShopAttach basShopAttach) {
        BasShopAttach response = basShopAttachMapper.selectBasShopAttachById(basShopAttach.getAttachmentSid());
        int row = basShopAttachMapper.updateAllById(basShopAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basShopAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, basShopAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除店铺-附件
     *
     * @param attachmentSids 需要删除的店铺-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasShopAttachByIds(List<Long> attachmentSids) {
        return basShopAttachMapper.deleteBatchIds(attachmentSids);
    }

}
