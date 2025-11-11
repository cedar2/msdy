package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ReqPurchaseRequireAttachmentMapper;
import com.platform.ems.domain.ReqPurchaseRequireAttachment;
import com.platform.ems.service.IReqPurchaseRequireAttachmentService;

/**
 * 申购单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
@Service
@SuppressWarnings("all")
public class ReqPurchaseRequireAttachmentServiceImpl extends ServiceImpl<ReqPurchaseRequireAttachmentMapper,ReqPurchaseRequireAttachment>  implements IReqPurchaseRequireAttachmentService {
    @Autowired
    private ReqPurchaseRequireAttachmentMapper reqPurchaseRequireAttachmentMapper;

    /**
     * 查询申购单-附件
     * 
     * @param purchaseRequireAttachmentSid 申购单-附件ID
     * @return 申购单-附件
     */
    @Override
    public ReqPurchaseRequireAttachment selectReqPurchaseRequireAttachmentById(Long purchaseRequireAttachmentSid) {
        return reqPurchaseRequireAttachmentMapper.selectReqPurchaseRequireAttachmentById(purchaseRequireAttachmentSid);
    }

    /**
     * 查询申购单-附件列表
     * 
     * @param reqPurchaseRequireAttachment 申购单-附件
     * @return 申购单-附件
     */
    @Override
    public List<ReqPurchaseRequireAttachment> selectReqPurchaseRequireAttachmentList(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) {
        return reqPurchaseRequireAttachmentMapper.selectReqPurchaseRequireAttachmentList(reqPurchaseRequireAttachment);
    }

    /**
     * 新增申购单-附件
     * 需要注意编码重复校验
     * @param reqPurchaseRequireAttachment 申购单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReqPurchaseRequireAttachment(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) {
        return reqPurchaseRequireAttachmentMapper.insert(reqPurchaseRequireAttachment);
    }

    /**
     * 修改申购单-附件
     * 
     * @param reqPurchaseRequireAttachment 申购单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReqPurchaseRequireAttachment(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) {
        return reqPurchaseRequireAttachmentMapper.updateById(reqPurchaseRequireAttachment);
    }

    /**
     * 批量删除申购单-附件
     * 
     * @param purchaseRequireAttachmentSids 需要删除的申购单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReqPurchaseRequireAttachmentByIds(List<Long> purchaseRequireAttachmentSids) {
        return reqPurchaseRequireAttachmentMapper.deleteBatchIds(purchaseRequireAttachmentSids);
    }


}
