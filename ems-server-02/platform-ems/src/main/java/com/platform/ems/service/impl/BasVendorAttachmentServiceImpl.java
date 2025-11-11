package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.BasVendorAttachmentMapper;
import com.platform.ems.domain.BasVendorAttachment;
import com.platform.ems.service.IBasVendorAttachmentService;

/**
 * 供应商-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-13
 */
@Service
@SuppressWarnings("all")
public class BasVendorAttachmentServiceImpl extends ServiceImpl<BasVendorAttachmentMapper,BasVendorAttachment>  implements IBasVendorAttachmentService {
    @Autowired
    private BasVendorAttachmentMapper basVendorAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商-附件";
    /**
     * 查询供应商-附件
     *
     * @param vendorAttachmentSid 供应商-附件ID
     * @return 供应商-附件
     */
    @Override
    public BasVendorAttachment selectBasVendorAttachmentById(Long vendorAttachmentSid) {
        BasVendorAttachment basVendorAttachment = basVendorAttachmentMapper.selectBasVendorAttachmentById(vendorAttachmentSid);
        MongodbUtil.find(basVendorAttachment);
        return  basVendorAttachment;
    }

    /**
     * 查询供应商-附件列表
     *
     * @param basVendorAttachment 供应商-附件
     * @return 供应商-附件
     */
    @Override
    public List<BasVendorAttachment> selectBasVendorAttachmentList(BasVendorAttachment basVendorAttachment) {
        return basVendorAttachmentMapper.selectBasVendorAttachmentList(basVendorAttachment);
    }

    /**
     * 新增供应商-附件
     * 需要注意编码重复校验
     * @param basVendorAttachment 供应商-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorAttachment(BasVendorAttachment basVendorAttachment) {
        int row= basVendorAttachmentMapper.insert(basVendorAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(basVendorAttachment.getVendorAttachmentSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商-附件
     *
     * @param basVendorAttachment 供应商-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorAttachment(BasVendorAttachment basVendorAttachment) {
        BasVendorAttachment response = basVendorAttachmentMapper.selectBasVendorAttachmentById(basVendorAttachment.getVendorAttachmentSid());
        int row=basVendorAttachmentMapper.updateById(basVendorAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basVendorAttachment.getVendorAttachmentSid(), BusinessType.UPDATE.getValue(), response,basVendorAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商-附件
     *
     * @param basVendorAttachment 供应商-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorAttachment(BasVendorAttachment basVendorAttachment) {
        BasVendorAttachment response = basVendorAttachmentMapper.selectBasVendorAttachmentById(basVendorAttachment.getVendorAttachmentSid());
        int row=basVendorAttachmentMapper.updateAllById(basVendorAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basVendorAttachment.getVendorAttachmentSid(), BusinessType.CHANGE.getValue(), response,basVendorAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商-附件
     *
     * @param vendorAttachmentSids 需要删除的供应商-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorAttachmentByIds(List<Long> vendorAttachmentSids) {
        return basVendorAttachmentMapper.deleteBatchIds(vendorAttachmentSids);
    }
}
