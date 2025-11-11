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
import com.platform.ems.domain.FinVendorDeductionBillAttachment;
import com.platform.ems.mapper.FinVendorDeductionBillAttachmentMapper;
import com.platform.ems.service.IFinVendorDeductionBillAttachmentService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商扣款单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-31
 */
@Service
@SuppressWarnings("all")
public class FinVendorDeductionBillAttachmentServiceImpl extends ServiceImpl<FinVendorDeductionBillAttachmentMapper,FinVendorDeductionBillAttachment>  implements IFinVendorDeductionBillAttachmentService {
    @Autowired
    private FinVendorDeductionBillAttachmentMapper finVendorDeductionBillAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商扣款单-附件";
    /**
     * 查询供应商扣款单-附件
     *
     * @param deductionBillAttachmentSid 供应商扣款单-附件ID
     * @return 供应商扣款单-附件
     */
    @Override
    public FinVendorDeductionBillAttachment selectFinVendorDeductionBillAttachmentById(Long deductionBillAttachmentSid) {
        FinVendorDeductionBillAttachment finVendorDeductionBillAttachment = finVendorDeductionBillAttachmentMapper.selectFinVendorDeductionBillAttachmentById(deductionBillAttachmentSid);
        MongodbUtil.find(finVendorDeductionBillAttachment);
        return  finVendorDeductionBillAttachment;
    }

    /**
     * 查询供应商扣款单-附件列表
     *
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 供应商扣款单-附件
     */
    @Override
    public List<FinVendorDeductionBillAttachment> selectFinVendorDeductionBillAttachmentList(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment) {
        return finVendorDeductionBillAttachmentMapper.selectFinVendorDeductionBillAttachmentList(finVendorDeductionBillAttachment);
    }

    /**
     * 新增供应商扣款单-附件
     * 需要注意编码重复校验
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorDeductionBillAttachment(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment) {
        int row= finVendorDeductionBillAttachmentMapper.insert(finVendorDeductionBillAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商扣款单-附件
     *
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorDeductionBillAttachment(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment) {
        FinVendorDeductionBillAttachment response = finVendorDeductionBillAttachmentMapper.selectFinVendorDeductionBillAttachmentById(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid());
        int row=finVendorDeductionBillAttachmentMapper.updateById(finVendorDeductionBillAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid(), BusinessType.UPDATE.ordinal(), response,finVendorDeductionBillAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商扣款单-附件
     *
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorDeductionBillAttachment(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment) {
        FinVendorDeductionBillAttachment response = finVendorDeductionBillAttachmentMapper.selectFinVendorDeductionBillAttachmentById(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid());
                                                        int row=finVendorDeductionBillAttachmentMapper.updateAllById(finVendorDeductionBillAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid(), BusinessType.CHANGE.ordinal(), response,finVendorDeductionBillAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商扣款单-附件
     *
     * @param deductionBillAttachmentSids 需要删除的供应商扣款单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorDeductionBillAttachmentByIds(List<Long> deductionBillAttachmentSids) {
        return finVendorDeductionBillAttachmentMapper.deleteBatchIds(deductionBillAttachmentSids);
    }

    /**
    * 启用/停用
    * @param finVendorDeductionBillAttachment
    * @return
    */
    @Override
    public int changeStatus(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment){
        int row=0;
        Long[] sids=finVendorDeductionBillAttachment.getDeductionBillAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finVendorDeductionBillAttachment.setDeductionBillAttachmentSid(id);
                row=finVendorDeductionBillAttachmentMapper.updateById( finVendorDeductionBillAttachment);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finVendorDeductionBillAttachment.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,"");
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finVendorDeductionBillAttachment
     * @return
     */
    @Override
    public int check(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment){
        int row=0;
        Long[] sids=finVendorDeductionBillAttachment.getDeductionBillAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finVendorDeductionBillAttachment.setDeductionBillAttachmentSid(id);
                row=finVendorDeductionBillAttachmentMapper.updateById( finVendorDeductionBillAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finVendorDeductionBillAttachment.getDeductionBillAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
