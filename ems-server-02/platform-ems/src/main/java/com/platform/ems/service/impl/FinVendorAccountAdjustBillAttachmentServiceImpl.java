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
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FinVendorAccountAdjustBillAttachment;
import com.platform.ems.mapper.FinVendorAccountAdjustBillAttachmentMapper;
import com.platform.ems.service.IFinVendorAccountAdjustBillAttachmentService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商调账单-附件Service业务层处理
 *
 * @author qhq
 * @date 2021-05-26
 */
@Service
@SuppressWarnings("all")
public class FinVendorAccountAdjustBillAttachmentServiceImpl extends ServiceImpl<FinVendorAccountAdjustBillAttachmentMapper,FinVendorAccountAdjustBillAttachment>  implements IFinVendorAccountAdjustBillAttachmentService {
    @Autowired
    private FinVendorAccountAdjustBillAttachmentMapper finVendorAccountAdjustBillAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商调账单-附件";
    /**
     * 查询供应商调账单-附件
     *
     * @param adjustBillAttachmentSid 供应商调账单-附件ID
     * @return 供应商调账单-附件
     */
    @Override
    public FinVendorAccountAdjustBillAttachment selectFinVendorAccountAdjustBillAttachmentById(Long adjustBillAttachmentSid) {
        FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment = finVendorAccountAdjustBillAttachmentMapper.selectFinVendorAccountAdjustBillAttachmentById(adjustBillAttachmentSid);
        MongodbUtil.find(finVendorAccountAdjustBillAttachment);
        return  finVendorAccountAdjustBillAttachment;
    }

    /**
     * 查询供应商调账单-附件列表
     *
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 供应商调账单-附件
     */
    @Override
    public List<FinVendorAccountAdjustBillAttachment> selectFinVendorAccountAdjustBillAttachmentList(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment) {
        return finVendorAccountAdjustBillAttachmentMapper.selectFinVendorAccountAdjustBillAttachmentList(finVendorAccountAdjustBillAttachment);
    }

    /**
     * 新增供应商调账单-附件
     * 需要注意编码重复校验
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorAccountAdjustBillAttachment(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment) {
        int row= finVendorAccountAdjustBillAttachmentMapper.insert(finVendorAccountAdjustBillAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商调账单-附件
     *
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorAccountAdjustBillAttachment(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment) {
        FinVendorAccountAdjustBillAttachment response = finVendorAccountAdjustBillAttachmentMapper.selectFinVendorAccountAdjustBillAttachmentById(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid());
        int row=finVendorAccountAdjustBillAttachmentMapper.updateById(finVendorAccountAdjustBillAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid(), BusinessType.UPDATE.ordinal(), response,finVendorAccountAdjustBillAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商调账单-附件
     *
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorAccountAdjustBillAttachment(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment) {
        FinVendorAccountAdjustBillAttachment response = finVendorAccountAdjustBillAttachmentMapper.selectFinVendorAccountAdjustBillAttachmentById(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid());
                                                        int row=finVendorAccountAdjustBillAttachmentMapper.updateAllById(finVendorAccountAdjustBillAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid(), BusinessType.CHANGE.ordinal(), response,finVendorAccountAdjustBillAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商调账单-附件
     *
     * @param adjustBillAttachmentSids 需要删除的供应商调账单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorAccountAdjustBillAttachmentByIds(List<Long> adjustBillAttachmentSids) {
        return finVendorAccountAdjustBillAttachmentMapper.deleteBatchIds(adjustBillAttachmentSids);
    }

    /**
    * 启用/停用
    * @param finVendorAccountAdjustBillAttachment
    * @return
    */
    @Override
    public int changeStatus(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment){
        int row=0;
        Long[] sids=finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finVendorAccountAdjustBillAttachment.setAdjustBillAttachmentSid(id);
                row=finVendorAccountAdjustBillAttachmentMapper.updateById( finVendorAccountAdjustBillAttachment);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finVendorAccountAdjustBillAttachment.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,"");
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finVendorAccountAdjustBillAttachment
     * @return
     */
    @Override
    public int check(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment){
        int row=0;
        Long[] sids=finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finVendorAccountAdjustBillAttachment.setAdjustBillAttachmentSid(id);
                row=finVendorAccountAdjustBillAttachmentMapper.updateById( finVendorAccountAdjustBillAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finVendorAccountAdjustBillAttachment.getAdjustBillAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
