package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.DelOutsourceMaterialIssueNoteAttachmentMapper;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteAttachment;
import com.platform.ems.service.IDelOutsourceMaterialIssueNoteAttachmentService;

/**
 * 外发加工发料单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class DelOutsourceMaterialIssueNoteAttachmentServiceImpl extends ServiceImpl<DelOutsourceMaterialIssueNoteAttachmentMapper,DelOutsourceMaterialIssueNoteAttachment>  implements IDelOutsourceMaterialIssueNoteAttachmentService {
    @Autowired
    private DelOutsourceMaterialIssueNoteAttachmentMapper delOutsourceMaterialIssueNoteAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工发料单-附件";
    /**
     * 查询外发加工发料单-附件
     *
     * @param issueNoteAttachmentSid 外发加工发料单-附件ID
     * @return 外发加工发料单-附件
     */
    @Override
    public DelOutsourceMaterialIssueNoteAttachment selectDelOutsourceMaterialIssueNoteAttachmentById(Long issueNoteAttachmentSid) {
        DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment = delOutsourceMaterialIssueNoteAttachmentMapper.selectDelOutsourceMaterialIssueNoteAttachmentById(issueNoteAttachmentSid);
        MongodbUtil.find(delOutsourceMaterialIssueNoteAttachment);
        return  delOutsourceMaterialIssueNoteAttachment;
    }

    /**
     * 查询外发加工发料单-附件列表
     *
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 外发加工发料单-附件
     */
    @Override
    public List<DelOutsourceMaterialIssueNoteAttachment> selectDelOutsourceMaterialIssueNoteAttachmentList(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment) {
        return delOutsourceMaterialIssueNoteAttachmentMapper.selectDelOutsourceMaterialIssueNoteAttachmentList(delOutsourceMaterialIssueNoteAttachment);
    }

    /**
     * 新增外发加工发料单-附件
     * 需要注意编码重复校验
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment) {
        int row= delOutsourceMaterialIssueNoteAttachmentMapper.insert(delOutsourceMaterialIssueNoteAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工发料单-附件
     *
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment) {
        DelOutsourceMaterialIssueNoteAttachment response = delOutsourceMaterialIssueNoteAttachmentMapper.selectDelOutsourceMaterialIssueNoteAttachmentById(delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSid());
        int row=delOutsourceMaterialIssueNoteAttachmentMapper.updateById(delOutsourceMaterialIssueNoteAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSid(), BusinessType.UPDATE.getValue(), response,delOutsourceMaterialIssueNoteAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工发料单-附件
     *
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment) {
        DelOutsourceMaterialIssueNoteAttachment response = delOutsourceMaterialIssueNoteAttachmentMapper.selectDelOutsourceMaterialIssueNoteAttachmentById(delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSid());
                                                        int row=delOutsourceMaterialIssueNoteAttachmentMapper.updateAllById(delOutsourceMaterialIssueNoteAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSid(), BusinessType.CHANGE.getValue(), response,delOutsourceMaterialIssueNoteAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工发料单-附件
     *
     * @param issueNoteAttachmentSids 需要删除的外发加工发料单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelOutsourceMaterialIssueNoteAttachmentByIds(List<Long> issueNoteAttachmentSids) {
        return delOutsourceMaterialIssueNoteAttachmentMapper.deleteBatchIds(issueNoteAttachmentSids);
    }

    /**
     *更改确认状态
     * @param delOutsourceMaterialIssueNoteAttachment
     * @return
     */
    @Override
    public int check(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment){
        int row=0;
        Long[] sids=delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                delOutsourceMaterialIssueNoteAttachment.setIssueNoteAttachmentSid(id);
                row=delOutsourceMaterialIssueNoteAttachmentMapper.updateById( delOutsourceMaterialIssueNoteAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(delOutsourceMaterialIssueNoteAttachment.getIssueNoteAttachmentSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
