package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ReqRequireDocAttachmentMapper;
import com.platform.ems.domain.ReqRequireDocAttachment;
import com.platform.ems.service.IReqRequireDocAttachmentService;

/**
 * 需求单附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class ReqRequireDocAttachmentServiceImpl extends ServiceImpl<ReqRequireDocAttachmentMapper,ReqRequireDocAttachment>  implements IReqRequireDocAttachmentService {
    @Autowired
    private ReqRequireDocAttachmentMapper reqRequireDocAttachmentMapper;

    /**
     * 查询需求单附件
     * 
     * @param requireDocAttachmentSid 需求单附件ID
     * @return 需求单附件
     */
    @Override
    public ReqRequireDocAttachment selectReqRequireDocAttachmentById(Long requireDocAttachmentSid) {
        return reqRequireDocAttachmentMapper.selectReqRequireDocAttachmentById(requireDocAttachmentSid);
    }

    /**
     * 查询需求单附件列表
     * 
     * @param reqRequireDocAttachment 需求单附件
     * @return 需求单附件
     */
    @Override
    public List<ReqRequireDocAttachment> selectReqRequireDocAttachmentList(ReqRequireDocAttachment reqRequireDocAttachment) {
        return reqRequireDocAttachmentMapper.selectReqRequireDocAttachmentList(reqRequireDocAttachment);
    }

    /**
     * 新增需求单附件
     * 需要注意编码重复校验
     * @param reqRequireDocAttachment 需求单附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReqRequireDocAttachment(ReqRequireDocAttachment reqRequireDocAttachment) {
        return reqRequireDocAttachmentMapper.insert(reqRequireDocAttachment);
    }

    /**
     * 修改需求单附件
     * 
     * @param reqRequireDocAttachment 需求单附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReqRequireDocAttachment(ReqRequireDocAttachment reqRequireDocAttachment) {
        return reqRequireDocAttachmentMapper.updateById(reqRequireDocAttachment);
    }

    /**
     * 批量删除需求单附件
     * 
     * @param requireDocAttachmentSids 需要删除的需求单附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReqRequireDocAttachmentByIds(List<Long> requireDocAttachmentSids) {
        return reqRequireDocAttachmentMapper.deleteBatchIds(requireDocAttachmentSids);
    }


}
