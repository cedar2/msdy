package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurQuoteBargainAttach;

/**
 * 询报议价单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface IPurQuoteBargainAttachService extends IService<PurQuoteBargainAttach>{
    /**
     * 查询询报议价单-附件
     * 
     * @param requestQuotationAttachmentSid 询报议价单-附件ID
     * @return 询报议价单-附件
     */
    public PurQuoteBargainAttach selectPurRequestQuotationAttachmentById(Long requestQuotationAttachmentSid);

    /**
     * 查询询报议价单-附件列表
     * 
     * @param purQuoteBargainAttach 询报议价单-附件
     * @return 询报议价单-附件集合
     */
    public List<PurQuoteBargainAttach> selectPurRequestQuotationAttachmentList(PurQuoteBargainAttach purQuoteBargainAttach);

    /**
     * 新增询报议价单-附件
     * 
     * @param purQuoteBargainAttach 询报议价单-附件
     * @return 结果
     */
    public int insertPurRequestQuotationAttachment(PurQuoteBargainAttach purQuoteBargainAttach);

    /**
     * 修改询报议价单-附件
     * 
     * @param purQuoteBargainAttach 询报议价单-附件
     * @return 结果
     */
    public int updatePurRequestQuotationAttachment(PurQuoteBargainAttach purQuoteBargainAttach);

    /**
     * 批量删除询报议价单-附件
     * 
     * @param requestQuotationAttachmentSids 需要删除的询报议价单-附件ID
     * @return 结果
     */
    public int deletePurRequestQuotationAttachmentByIds(List<Long> requestQuotationAttachmentSids);

}
