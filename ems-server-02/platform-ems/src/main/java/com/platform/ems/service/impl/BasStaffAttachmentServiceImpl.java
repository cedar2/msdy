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
import com.platform.ems.mapper.BasStaffAttachmentMapper;
import com.platform.ems.domain.BasStaffAttachment;
import com.platform.ems.service.IBasStaffAttachmentService;

/**
 * 员工-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-13
 */
@Service
@SuppressWarnings("all")
public class BasStaffAttachmentServiceImpl extends ServiceImpl<BasStaffAttachmentMapper,BasStaffAttachment>  implements IBasStaffAttachmentService {
    @Autowired
    private BasStaffAttachmentMapper basStaffAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "员工-附件";
    /**
     * 查询员工-附件
     *
     * @param staffAttachmentSid 员工-附件ID
     * @return 员工-附件
     */
    @Override
    public BasStaffAttachment selectBasStaffAttachmentById(Long staffAttachmentSid) {
        BasStaffAttachment basStaffAttachment = basStaffAttachmentMapper.selectBasStaffAttachmentById(staffAttachmentSid);
        MongodbUtil.find(basStaffAttachment);
        return  basStaffAttachment;
    }

    /**
     * 查询员工-附件列表
     *
     * @param basStaffAttachment 员工-附件
     * @return 员工-附件
     */
    @Override
    public List<BasStaffAttachment> selectBasStaffAttachmentList(BasStaffAttachment basStaffAttachment) {
        return basStaffAttachmentMapper.selectBasStaffAttachmentList(basStaffAttachment);
    }

    /**
     * 新增员工-附件
     * 需要注意编码重复校验
     * @param basStaffAttachment 员工-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStaffAttachment(BasStaffAttachment basStaffAttachment) {
        int row= basStaffAttachmentMapper.insert(basStaffAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(basStaffAttachment.getStaffAttachmentSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改员工-附件
     *
     * @param basStaffAttachment 员工-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasStaffAttachment(BasStaffAttachment basStaffAttachment) {
        BasStaffAttachment response = basStaffAttachmentMapper.selectBasStaffAttachmentById(basStaffAttachment.getStaffAttachmentSid());
        int row=basStaffAttachmentMapper.updateById(basStaffAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basStaffAttachment.getStaffAttachmentSid(), BusinessType.UPDATE.getValue(), response,basStaffAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更员工-附件
     *
     * @param basStaffAttachment 员工-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasStaffAttachment(BasStaffAttachment basStaffAttachment) {
        BasStaffAttachment response = basStaffAttachmentMapper.selectBasStaffAttachmentById(basStaffAttachment.getStaffAttachmentSid());
        int row=basStaffAttachmentMapper.updateAllById(basStaffAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basStaffAttachment.getStaffAttachmentSid(), BusinessType.CHANGE.getValue(), response,basStaffAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除员工-附件
     *
     * @param staffAttachmentSids 需要删除的员工-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasStaffAttachmentByIds(List<Long> staffAttachmentSids) {
        return basStaffAttachmentMapper.deleteBatchIds(staffAttachmentSids);
    }
}
