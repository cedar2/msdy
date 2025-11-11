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
import com.platform.ems.mapper.DelOutsourceDeliveryNoteAttachmentMapper;
import com.platform.ems.domain.DelOutsourceDeliveryNoteAttachment;
import com.platform.ems.service.IDelOutsourceDeliveryNoteAttachmentService;

/**
 * 外发加工交货单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class DelOutsourceDeliveryNoteAttachmentServiceImpl extends ServiceImpl<DelOutsourceDeliveryNoteAttachmentMapper,DelOutsourceDeliveryNoteAttachment>  implements IDelOutsourceDeliveryNoteAttachmentService {
    @Autowired
    private DelOutsourceDeliveryNoteAttachmentMapper delOutsourceDeliveryNoteAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工交货单-附件";
    /**
     * 查询外发加工交货单-附件
     *
     * @param deliveryNoteAttachmentSid 外发加工交货单-附件ID
     * @return 外发加工交货单-附件
     */
    @Override
    public DelOutsourceDeliveryNoteAttachment selectDelOutsourceDeliveryNoteAttachmentById(Long deliveryNoteAttachmentSid) {
        DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment = delOutsourceDeliveryNoteAttachmentMapper.selectDelOutsourceDeliveryNoteAttachmentById(deliveryNoteAttachmentSid);
        MongodbUtil.find(delOutsourceDeliveryNoteAttachment);
        return  delOutsourceDeliveryNoteAttachment;
    }

    /**
     * 查询外发加工交货单-附件列表
     *
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 外发加工交货单-附件
     */
    @Override
    public List<DelOutsourceDeliveryNoteAttachment> selectDelOutsourceDeliveryNoteAttachmentList(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment) {
        return delOutsourceDeliveryNoteAttachmentMapper.selectDelOutsourceDeliveryNoteAttachmentList(delOutsourceDeliveryNoteAttachment);
    }

    /**
     * 新增外发加工交货单-附件
     * 需要注意编码重复校验
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment) {
        int row= delOutsourceDeliveryNoteAttachmentMapper.insert(delOutsourceDeliveryNoteAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工交货单-附件
     *
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment) {
        DelOutsourceDeliveryNoteAttachment response = delOutsourceDeliveryNoteAttachmentMapper.selectDelOutsourceDeliveryNoteAttachmentById(delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSid());
        int row=delOutsourceDeliveryNoteAttachmentMapper.updateById(delOutsourceDeliveryNoteAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSid(), BusinessType.UPDATE.getValue(), response,delOutsourceDeliveryNoteAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工交货单-附件
     *
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment) {
        DelOutsourceDeliveryNoteAttachment response = delOutsourceDeliveryNoteAttachmentMapper.selectDelOutsourceDeliveryNoteAttachmentById(delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSid());
                                                        int row=delOutsourceDeliveryNoteAttachmentMapper.updateAllById(delOutsourceDeliveryNoteAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSid(), BusinessType.CHANGE.getValue(), response,delOutsourceDeliveryNoteAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工交货单-附件
     *
     * @param deliveryNoteAttachmentSids 需要删除的外发加工交货单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelOutsourceDeliveryNoteAttachmentByIds(List<Long> deliveryNoteAttachmentSids) {
        return delOutsourceDeliveryNoteAttachmentMapper.deleteBatchIds(deliveryNoteAttachmentSids);
    }

    /**
     *更改确认状态
     * @param delOutsourceDeliveryNoteAttachment
     * @return
     */
    @Override
    public int check(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment){
        int row=0;
        Long[] sids=delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                delOutsourceDeliveryNoteAttachment.setDeliveryNoteAttachmentSid(id);
                row=delOutsourceDeliveryNoteAttachmentMapper.updateById( delOutsourceDeliveryNoteAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(delOutsourceDeliveryNoteAttachment.getDeliveryNoteAttachmentSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
