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
import com.platform.ems.mapper.FinRecordAdvancePaymentAttachmentMapper;
import com.platform.ems.domain.FinRecordAdvancePaymentAttachment;
import com.platform.ems.service.IFinRecordAdvancePaymentAttachmentService;

/**
 * 供应商业务台账-附件-预付Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-29
 */
@Service
@SuppressWarnings("all")
public class FinRecordAdvancePaymentAttachmentServiceImpl extends ServiceImpl<FinRecordAdvancePaymentAttachmentMapper,FinRecordAdvancePaymentAttachment>  implements IFinRecordAdvancePaymentAttachmentService {
    @Autowired
    private FinRecordAdvancePaymentAttachmentMapper finRecordAdvancePaymentAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商业务台账-附件-预付";
    /**
     * 查询供应商业务台账-附件-预付
     *
     * @param recordAdvancePaymentAttachmentSid 供应商业务台账-附件-预付ID
     * @return 供应商业务台账-附件-预付
     */
    @Override
    public FinRecordAdvancePaymentAttachment selectFinRecordAdvancePaymentAttachmentById(Long recordAdvancePaymentAttachmentSid) {
        FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment = finRecordAdvancePaymentAttachmentMapper.selectFinRecordAdvancePaymentAttachmentById(recordAdvancePaymentAttachmentSid);
        MongodbUtil.find(finRecordAdvancePaymentAttachment);
        return  finRecordAdvancePaymentAttachment;
    }

    /**
     * 查询供应商业务台账-附件-预付列表
     *
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 供应商业务台账-附件-预付
     */
    @Override
    public List<FinRecordAdvancePaymentAttachment> selectFinRecordAdvancePaymentAttachmentList(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment) {
        return finRecordAdvancePaymentAttachmentMapper.selectFinRecordAdvancePaymentAttachmentList(finRecordAdvancePaymentAttachment);
    }

    /**
     * 新增供应商业务台账-附件-预付
     * 需要注意编码重复校验
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinRecordAdvancePaymentAttachment(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment) {
        int row= finRecordAdvancePaymentAttachmentMapper.insert(finRecordAdvancePaymentAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商业务台账-附件-预付
     *
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinRecordAdvancePaymentAttachment(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment) {
        FinRecordAdvancePaymentAttachment response = finRecordAdvancePaymentAttachmentMapper.selectFinRecordAdvancePaymentAttachmentById(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid());
        int row=finRecordAdvancePaymentAttachmentMapper.updateById(finRecordAdvancePaymentAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid(), BusinessType.UPDATE.ordinal(), response,finRecordAdvancePaymentAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商业务台账-附件-预付
     *
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinRecordAdvancePaymentAttachment(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment) {
        FinRecordAdvancePaymentAttachment response = finRecordAdvancePaymentAttachmentMapper.selectFinRecordAdvancePaymentAttachmentById(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid());
                                                        int row=finRecordAdvancePaymentAttachmentMapper.updateAllById(finRecordAdvancePaymentAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid(), BusinessType.CHANGE.ordinal(), response,finRecordAdvancePaymentAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商业务台账-附件-预付
     *
     * @param recordAdvancePaymentAttachmentSids 需要删除的供应商业务台账-附件-预付ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinRecordAdvancePaymentAttachmentByIds(List<Long> recordAdvancePaymentAttachmentSids) {
        return finRecordAdvancePaymentAttachmentMapper.deleteBatchIds(recordAdvancePaymentAttachmentSids);
    }

    /**
    * 启用/停用
    * @param finRecordAdvancePaymentAttachment
    * @return
    */
    @Override
    public int changeStatus(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment){
        int row=0;
        Long[] sids=finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvancePaymentAttachment.setRecordAdvancePaymentAttachmentSid(id);
                row=finRecordAdvancePaymentAttachmentMapper.updateById( finRecordAdvancePaymentAttachment);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finRecordAdvancePaymentAttachment.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
//                MongodbUtil.insertUserLog(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finRecordAdvancePaymentAttachment
     * @return
     */
    @Override
    public int check(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment){
        int row=0;
        Long[] sids=finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finRecordAdvancePaymentAttachment.setRecordAdvancePaymentAttachmentSid(id);
                row=finRecordAdvancePaymentAttachmentMapper.updateById( finRecordAdvancePaymentAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finRecordAdvancePaymentAttachment.getRecordAdvancePaymentAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
