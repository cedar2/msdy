package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.FinRecordAdvanceReceiptAttachment;
import com.platform.ems.mapper.FinRecordAdvanceReceiptAttachmentMapper;
import com.platform.ems.service.IFinRecordAdvanceReceiptAttachmentService;
import com.platform.ems.util.MongodbUtil;

/**
 * 客户业务台账-附件-预收Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@Service
@SuppressWarnings("all")
public class FinRecordAdvanceReceiptAttachmentServiceImpl extends ServiceImpl<FinRecordAdvanceReceiptAttachmentMapper,FinRecordAdvanceReceiptAttachment>  implements IFinRecordAdvanceReceiptAttachmentService {
    @Autowired
    private FinRecordAdvanceReceiptAttachmentMapper finRecordAdvanceReceiptAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户业务台账-附件-预收";
    /**
     * 查询客户业务台账-附件-预收
     *
     * @param recordAdvanceReceiptAttachmentSid 客户业务台账-附件-预收ID
     * @return 客户业务台账-附件-预收
     */
    @Override
    public FinRecordAdvanceReceiptAttachment selectFinRecordAdvanceReceiptAttachmentById(Long recordAdvanceReceiptAttachmentSid) {
        FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment = finRecordAdvanceReceiptAttachmentMapper.selectFinRecordAdvanceReceiptAttachmentById(recordAdvanceReceiptAttachmentSid);
        MongodbUtil.find(finRecordAdvanceReceiptAttachment);
        return  finRecordAdvanceReceiptAttachment;
    }

    /**
     * 查询客户业务台账-附件-预收列表
     *
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 客户业务台账-附件-预收
     */
    @Override
    public List<FinRecordAdvanceReceiptAttachment> selectFinRecordAdvanceReceiptAttachmentList(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment) {
        return finRecordAdvanceReceiptAttachmentMapper.selectFinRecordAdvanceReceiptAttachmentList(finRecordAdvanceReceiptAttachment);
    }

    /**
     * 新增客户业务台账-附件-预收
     * 需要注意编码重复校验
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinRecordAdvanceReceiptAttachment(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment) {
        int row= finRecordAdvanceReceiptAttachmentMapper.insert(finRecordAdvanceReceiptAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改客户业务台账-附件-预收
     *
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinRecordAdvanceReceiptAttachment(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment) {
        FinRecordAdvanceReceiptAttachment response = finRecordAdvanceReceiptAttachmentMapper.selectFinRecordAdvanceReceiptAttachmentById(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid());
        int row=finRecordAdvanceReceiptAttachmentMapper.updateById(finRecordAdvanceReceiptAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid(), BusinessType.UPDATE.ordinal(), response,finRecordAdvanceReceiptAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更客户业务台账-附件-预收
     *
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinRecordAdvanceReceiptAttachment(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment) {
        FinRecordAdvanceReceiptAttachment response = finRecordAdvanceReceiptAttachmentMapper.selectFinRecordAdvanceReceiptAttachmentById(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid());
                                                        int row=finRecordAdvanceReceiptAttachmentMapper.updateAllById(finRecordAdvanceReceiptAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid(), BusinessType.CHANGE.ordinal(), response,finRecordAdvanceReceiptAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户业务台账-附件-预收
     *
     * @param recordAdvanceReceiptAttachmentSids 需要删除的客户业务台账-附件-预收ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinRecordAdvanceReceiptAttachmentByIds(List<Long> recordAdvanceReceiptAttachmentSids) {
        return finRecordAdvanceReceiptAttachmentMapper.deleteBatchIds(recordAdvanceReceiptAttachmentSids);
    }

    /**
    * 启用/停用
    * @param finRecordAdvanceReceiptAttachment
    * @return
    */
    @Override
    public int changeStatus(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment){
        int row=0;
        Long[] sids=finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvanceReceiptAttachment.setRecordAdvanceReceiptAttachmentSid(id);
                row=finRecordAdvanceReceiptAttachmentMapper.updateById( finRecordAdvanceReceiptAttachment);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finRecordAdvanceReceiptAttachment.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,"");
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finRecordAdvanceReceiptAttachment
     * @return
     */
    @Override
    public int check(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment){
        int row=0;
        Long[] sids=finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvanceReceiptAttachment.setRecordAdvanceReceiptAttachmentSid(id);
                row=finRecordAdvanceReceiptAttachmentMapper.updateById( finRecordAdvanceReceiptAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finRecordAdvanceReceiptAttachment.getRecordAdvanceReceiptAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
