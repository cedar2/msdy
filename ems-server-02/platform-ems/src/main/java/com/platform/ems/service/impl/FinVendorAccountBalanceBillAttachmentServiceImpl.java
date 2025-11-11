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
import com.platform.ems.domain.FinVendorAccountBalanceBillAttachment;
import com.platform.ems.mapper.FinVendorAccountBalanceBillAttachmentMapper;
import com.platform.ems.service.IFinVendorAccountBalanceBillAttachmentService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商账互抵单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-27
 */
@Service
@SuppressWarnings("all")
public class FinVendorAccountBalanceBillAttachmentServiceImpl extends ServiceImpl<FinVendorAccountBalanceBillAttachmentMapper,FinVendorAccountBalanceBillAttachment>  implements IFinVendorAccountBalanceBillAttachmentService {
    @Autowired
    private FinVendorAccountBalanceBillAttachmentMapper finVendorAccountBalanceBillAttachmentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商账互抵单-附件";
    /**
     * 查询供应商账互抵单-附件
     *
     * @param vendorAccountBalanceBillAttachmentSid 供应商账互抵单-附件ID
     * @return 供应商账互抵单-附件
     */
    @Override
    public FinVendorAccountBalanceBillAttachment selectFinVendorAccountBalanceBillAttachmentById(Long vendorAccountBalanceBillAttachmentSid) {
        FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment = finVendorAccountBalanceBillAttachmentMapper.selectFinVendorAccountBalanceBillAttachmentById(vendorAccountBalanceBillAttachmentSid);
        MongodbUtil.find(finVendorAccountBalanceBillAttachment);
        return  finVendorAccountBalanceBillAttachment;
    }

    /**
     * 查询供应商账互抵单-附件列表
     *
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 供应商账互抵单-附件
     */
    @Override
    public List<FinVendorAccountBalanceBillAttachment> selectFinVendorAccountBalanceBillAttachmentList(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment) {
        return finVendorAccountBalanceBillAttachmentMapper.selectFinVendorAccountBalanceBillAttachmentList(finVendorAccountBalanceBillAttachment);
    }

    /**
     * 新增供应商账互抵单-附件
     * 需要注意编码重复校验
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorAccountBalanceBillAttachment(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment) {
        int row= finVendorAccountBalanceBillAttachmentMapper.insert(finVendorAccountBalanceBillAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改供应商账互抵单-附件
     *
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorAccountBalanceBillAttachment(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment) {
        FinVendorAccountBalanceBillAttachment response = finVendorAccountBalanceBillAttachmentMapper.selectFinVendorAccountBalanceBillAttachmentById(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid());
        int row=finVendorAccountBalanceBillAttachmentMapper.updateById(finVendorAccountBalanceBillAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid(), BusinessType.UPDATE.ordinal(), response,finVendorAccountBalanceBillAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更供应商账互抵单-附件
     *
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorAccountBalanceBillAttachment(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment) {
        FinVendorAccountBalanceBillAttachment response = finVendorAccountBalanceBillAttachmentMapper.selectFinVendorAccountBalanceBillAttachmentById(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid());
                                                        int row=finVendorAccountBalanceBillAttachmentMapper.updateAllById(finVendorAccountBalanceBillAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid(), BusinessType.CHANGE.ordinal(), response,finVendorAccountBalanceBillAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商账互抵单-附件
     *
     * @param vendorAccountBalanceBillAttachmentSids 需要删除的供应商账互抵单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorAccountBalanceBillAttachmentByIds(List<Long> vendorAccountBalanceBillAttachmentSids) {
        return finVendorAccountBalanceBillAttachmentMapper.deleteBatchIds(vendorAccountBalanceBillAttachmentSids);
    }

    /**
    * 启用/停用
    * @param finVendorAccountBalanceBillAttachment
    * @return
    */
    @Override
    public int changeStatus(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment){
        int row=0;
        Long[] sids=finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finVendorAccountBalanceBillAttachment.setAccountBalanceBillAttachmentSid(id);
                row=finVendorAccountBalanceBillAttachmentMapper.updateById( finVendorAccountBalanceBillAttachment);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=finVendorAccountBalanceBillAttachment.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,"");
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param finVendorAccountBalanceBillAttachment
     * @return
     */
    @Override
    public int check(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment){
        int row=0;
        Long[] sids=finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                finVendorAccountBalanceBillAttachment.setAccountBalanceBillAttachmentSid(id);
                row=finVendorAccountBalanceBillAttachmentMapper.updateById( finVendorAccountBalanceBillAttachment);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(finVendorAccountBalanceBillAttachment.getAccountBalanceBillAttachmentSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
