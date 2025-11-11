package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ReqRequireDocAttachment;

/**
 * 需求单附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-02
 */
public interface IReqRequireDocAttachmentService extends IService<ReqRequireDocAttachment>{
    /**
     * 查询需求单附件
     * 
     * @param requireDocAttachmentSid 需求单附件ID
     * @return 需求单附件
     */
    public ReqRequireDocAttachment selectReqRequireDocAttachmentById(Long requireDocAttachmentSid);

    /**
     * 查询需求单附件列表
     * 
     * @param reqRequireDocAttachment 需求单附件
     * @return 需求单附件集合
     */
    public List<ReqRequireDocAttachment> selectReqRequireDocAttachmentList(ReqRequireDocAttachment reqRequireDocAttachment);

    /**
     * 新增需求单附件
     * 
     * @param reqRequireDocAttachment 需求单附件
     * @return 结果
     */
    public int insertReqRequireDocAttachment(ReqRequireDocAttachment reqRequireDocAttachment);

    /**
     * 修改需求单附件
     * 
     * @param reqRequireDocAttachment 需求单附件
     * @return 结果
     */
    public int updateReqRequireDocAttachment(ReqRequireDocAttachment reqRequireDocAttachment);

    /**
     * 批量删除需求单附件
     * 
     * @param requireDocAttachmentSids 需要删除的需求单附件ID
     * @return 结果
     */
    public int deleteReqRequireDocAttachmentByIds(List<Long> requireDocAttachmentSids);

}
