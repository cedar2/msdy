package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.DelDeliveryNoteAttachmentMapper;
import com.platform.ems.domain.DelDeliveryNoteAttachment;
import com.platform.ems.service.IDelDeliveryNoteAttachmentService;

/**
 * 交货单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
@Service
@SuppressWarnings("all")
public class DelDeliveryNoteAttachmentServiceImpl extends ServiceImpl<DelDeliveryNoteAttachmentMapper,DelDeliveryNoteAttachment>  implements IDelDeliveryNoteAttachmentService {
    @Autowired
    private DelDeliveryNoteAttachmentMapper delDeliveryNoteAttachmentMapper;

    /**
     * 查询交货单-附件
     * 
     * @param deliveryNoteAttachmentSid 交货单-附件ID
     * @return 交货单-附件
     */
    @Override
    public DelDeliveryNoteAttachment selectDelDeliveryNoteAttachmentById(Long deliveryNoteAttachmentSid) {
        return delDeliveryNoteAttachmentMapper.selectDelDeliveryNoteAttachmentById(deliveryNoteAttachmentSid);
    }

    /**
     * 查询交货单-附件列表
     * 
     * @param delDeliveryNoteAttachment 交货单-附件
     * @return 交货单-附件
     */
    @Override
    public List<DelDeliveryNoteAttachment> selectDelDeliveryNoteAttachmentList(DelDeliveryNoteAttachment delDeliveryNoteAttachment) {
        return delDeliveryNoteAttachmentMapper.selectDelDeliveryNoteAttachmentList(delDeliveryNoteAttachment);
    }

    /**
     * 新增交货单-附件
     * 需要注意编码重复校验
     * @param delDeliveryNoteAttachment 交货单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelDeliveryNoteAttachment(DelDeliveryNoteAttachment delDeliveryNoteAttachment) {
        return delDeliveryNoteAttachmentMapper.insert(delDeliveryNoteAttachment);
    }

    /**
     * 修改交货单-附件
     * 
     * @param delDeliveryNoteAttachment 交货单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelDeliveryNoteAttachment(DelDeliveryNoteAttachment delDeliveryNoteAttachment) {
        return delDeliveryNoteAttachmentMapper.updateById(delDeliveryNoteAttachment);
    }

    /**
     * 批量删除交货单-附件
     * 
     * @param deliveryNoteAttachmentSids 需要删除的交货单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelDeliveryNoteAttachmentByIds(List<Long> deliveryNoteAttachmentSids) {
        return delDeliveryNoteAttachmentMapper.deleteBatchIds(deliveryNoteAttachmentSids);
    }


}
