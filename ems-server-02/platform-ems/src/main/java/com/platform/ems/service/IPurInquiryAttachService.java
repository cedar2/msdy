package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurInquiryAttach;

/**
 * 物料询价单-附件Service接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface IPurInquiryAttachService extends IService<PurInquiryAttach> {
    /**
     * 查询物料询价单-附件
     *
     * @param inquiryAttachmentSid 物料询价单-附件ID
     * @return 物料询价单-附件
     */
    public PurInquiryAttach selectPurInquiryAttachById(Long inquiryAttachmentSid);

    /**
     * 查询物料询价单-附件列表
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 物料询价单-附件集合
     */
    public List<PurInquiryAttach> selectPurInquiryAttachList(PurInquiryAttach purInquiryAttach);

    /**
     * 新增物料询价单-附件
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 结果
     */
    public int insertPurInquiryAttach(PurInquiryAttach purInquiryAttach);

    /**
     * 修改物料询价单-附件
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 结果
     */
    public int updatePurInquiryAttach(PurInquiryAttach purInquiryAttach);

    /**
     * 变更物料询价单-附件
     *
     * @param purInquiryAttach 物料询价单-附件
     * @return 结果
     */
    public int changePurInquiryAttach(PurInquiryAttach purInquiryAttach);

    /**
     * 批量删除物料询价单-附件
     *
     * @param inquiryattachmentSids 需要删除的物料询价单-附件ID
     * @return 结果
     */
    public int deletePurInquiryAttachByIds(List<Long> inquiryattachmentSids);

}
