package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorAttachment;

/**
 * 供应商-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-13
 */
public interface IBasVendorAttachmentService extends IService<BasVendorAttachment>{
    /**
     * 查询供应商-附件
     *
     * @param vendorAttachmentSid 供应商-附件ID
     * @return 供应商-附件
     */
    public BasVendorAttachment selectBasVendorAttachmentById(Long vendorAttachmentSid);

    /**
     * 查询供应商-附件列表
     *
     * @param basVendorAttachment 供应商-附件
     * @return 供应商-附件集合
     */
    public List<BasVendorAttachment> selectBasVendorAttachmentList(BasVendorAttachment basVendorAttachment);

    /**
     * 新增供应商-附件
     *
     * @param basVendorAttachment 供应商-附件
     * @return 结果
     */
    public int insertBasVendorAttachment(BasVendorAttachment basVendorAttachment);

    /**
     * 修改供应商-附件
     *
     * @param basVendorAttachment 供应商-附件
     * @return 结果
     */
    public int updateBasVendorAttachment(BasVendorAttachment basVendorAttachment);

    /**
     * 变更供应商-附件
     *
     * @param basVendorAttachment 供应商-附件
     * @return 结果
     */
    public int changeBasVendorAttachment(BasVendorAttachment basVendorAttachment);

    /**
     * 批量删除供应商-附件
     *
     * @param vendorAttachmentSids 需要删除的供应商-附件ID
     * @return 结果
     */
    public int deleteBasVendorAttachmentByIds(List<Long>  vendorAttachmentSids);


}
