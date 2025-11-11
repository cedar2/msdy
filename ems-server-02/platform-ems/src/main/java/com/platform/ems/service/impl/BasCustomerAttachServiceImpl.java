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
import com.platform.ems.mapper.BasCustomerAttachMapper;
import com.platform.ems.domain.BasCustomerAttach;
import com.platform.ems.service.IBasCustomerAttachService;

/**
 * 客户档案-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-15
 */
@Service
@SuppressWarnings("all")
public class BasCustomerAttachServiceImpl extends ServiceImpl<BasCustomerAttachMapper,BasCustomerAttach>  implements IBasCustomerAttachService {
    @Autowired
    private BasCustomerAttachMapper basCustomerAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户档案-附件";
    /**
     * 查询客户档案-附件
     *
     * @param attachmentSid 客户档案-附件ID
     * @return 客户档案-附件
     */
    @Override
    public BasCustomerAttach selectBasCustomerAttachById(Long attachmentSid) {
        BasCustomerAttach basCustomerAttach = basCustomerAttachMapper.selectBasCustomerAttachById(attachmentSid);
        MongodbUtil.find(basCustomerAttach);
        return  basCustomerAttach;
    }

    /**
     * 查询客户档案-附件列表
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 客户档案-附件
     */
    @Override
    public List<BasCustomerAttach> selectBasCustomerAttachList(BasCustomerAttach basCustomerAttach) {
        return basCustomerAttachMapper.selectBasCustomerAttachList(basCustomerAttach);
    }

    /**
     * 新增客户档案-附件
     * 需要注意编码重复校验
     * @param basCustomerAttach 客户档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCustomerAttach(BasCustomerAttach basCustomerAttach) {
        int row= basCustomerAttachMapper.insert(basCustomerAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(basCustomerAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改客户档案-附件
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasCustomerAttach(BasCustomerAttach basCustomerAttach) {
        BasCustomerAttach response = basCustomerAttachMapper.selectBasCustomerAttachById(basCustomerAttach.getAttachmentSid());
        int row=basCustomerAttachMapper.updateById(basCustomerAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basCustomerAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,basCustomerAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更客户档案-附件
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasCustomerAttach(BasCustomerAttach basCustomerAttach) {
        BasCustomerAttach response = basCustomerAttachMapper.selectBasCustomerAttachById(basCustomerAttach.getAttachmentSid());
        int row=basCustomerAttachMapper.updateAllById(basCustomerAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basCustomerAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,basCustomerAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户档案-附件
     *
     * @param attachmentSids 需要删除的客户档案-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCustomerAttachByIds(List<Long> attachmentSids) {
        return basCustomerAttachMapper.deleteBatchIds(attachmentSids);
    }

}
