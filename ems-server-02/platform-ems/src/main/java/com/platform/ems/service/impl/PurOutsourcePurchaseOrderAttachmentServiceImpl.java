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
import com.platform.ems.mapper.PurOutsourcePurchaseOrderAttachmentMapper;
import com.platform.ems.domain.PurOutsourcePurchaseOrderAttachment;
import com.platform.ems.service.IPurOutsourcePurchaseOrderAttachmentService;

/**
 * 外发加工单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class PurOutsourcePurchaseOrderAttachmentServiceImpl extends ServiceImpl<PurOutsourcePurchaseOrderAttachmentMapper,PurOutsourcePurchaseOrderAttachment>  implements IPurOutsourcePurchaseOrderAttachmentService {
    @Autowired
    private PurOutsourcePurchaseOrderAttachmentMapper purOutsourcePurchaseOrderAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工单-附件";
    /**
     * 查询外发加工单-附件
     *
     * @param outsourcePurchaseOrderAttachmentSid 外发加工单-附件ID
     * @return 外发加工单-附件
     */
    @Override
    public PurOutsourcePurchaseOrderAttachment selectPurOutsourcePurchaseOrderAttachmentById(Long outsourcePurchaseOrderAttachmentSid) {
        PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment = purOutsourcePurchaseOrderAttachmentMapper.selectPurOutsourcePurchaseOrderAttachmentById(outsourcePurchaseOrderAttachmentSid);
        MongodbUtil.find(purOutsourcePurchaseOrderAttachment);
        return  purOutsourcePurchaseOrderAttachment;
    }

    /**
     * 查询外发加工单-附件列表
     *
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 外发加工单-附件
     */
    @Override
    public List<PurOutsourcePurchaseOrderAttachment> selectPurOutsourcePurchaseOrderAttachmentList(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment) {
        return purOutsourcePurchaseOrderAttachmentMapper.selectPurOutsourcePurchaseOrderAttachmentList(purOutsourcePurchaseOrderAttachment);
    }

    /**
     * 新增外发加工单-附件
     * 需要注意编码重复校验
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment) {
        int row= purOutsourcePurchaseOrderAttachmentMapper.insert(purOutsourcePurchaseOrderAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工单-附件
     *
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment) {
        PurOutsourcePurchaseOrderAttachment response = purOutsourcePurchaseOrderAttachmentMapper.selectPurOutsourcePurchaseOrderAttachmentById(purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSid());
        int row=purOutsourcePurchaseOrderAttachmentMapper.updateById(purOutsourcePurchaseOrderAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSid(), BusinessType.UPDATE.ordinal(), response,purOutsourcePurchaseOrderAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工单-附件
     *
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment) {
        PurOutsourcePurchaseOrderAttachment response = purOutsourcePurchaseOrderAttachmentMapper.selectPurOutsourcePurchaseOrderAttachmentById(purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSid());
                                                        int row=purOutsourcePurchaseOrderAttachmentMapper.updateAllById(purOutsourcePurchaseOrderAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSid(), BusinessType.CHANGE.ordinal(), response,purOutsourcePurchaseOrderAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工单-附件
     *
     * @param outsourcePurchaseOrderAttachmentSids 需要删除的外发加工单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourcePurchaseOrderAttachmentByIds(List<Long> outsourcePurchaseOrderAttachmentSids) {
        return purOutsourcePurchaseOrderAttachmentMapper.deleteBatchIds(outsourcePurchaseOrderAttachmentSids);
    }

    /**
     *更改确认状态
     * @param purOutsourcePurchaseOrderAttachment
     * @return
     */
    @Override
    public int check(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment){
        int row=0;
        Long[] sids=purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                purOutsourcePurchaseOrderAttachment.setOutsourcePurchaseOrderAttachmentSid(id);
                row=purOutsourcePurchaseOrderAttachmentMapper.updateById( purOutsourcePurchaseOrderAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(purOutsourcePurchaseOrderAttachment.getOutsourcePurchaseOrderAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
