package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourceInquiryAttach;

/**
 * 加工询价单-附件Service接口
 *
 * @author chenkw
 * @date 2022-01-11
 */
public interface IPurOutsourceInquiryAttachService extends IService<PurOutsourceInquiryAttach> {
    /**
     * 查询加工询价单-附件
     *
     * @param outsourceInquiryattachmentSid 加工询价单-附件ID
     * @return 加工询价单-附件
     */
    public PurOutsourceInquiryAttach selectPurOutsourceInquiryAttachById(Long outsourceInquiryattachmentSid);

    /**
     * 查询加工询价单-附件列表
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 加工询价单-附件集合
     */
    public List<PurOutsourceInquiryAttach> selectPurOutsourceInquiryAttachList(PurOutsourceInquiryAttach purOutsourceInquiryAttach);

    /**
     * 新增加工询价单-附件
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 结果
     */
    public int insertPurOutsourceInquiryAttach(PurOutsourceInquiryAttach purOutsourceInquiryAttach);

    /**
     * 修改加工询价单-附件
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 结果
     */
    public int updatePurOutsourceInquiryAttach(PurOutsourceInquiryAttach purOutsourceInquiryAttach);

    /**
     * 变更加工询价单-附件
     *
     * @param purOutsourceInquiryAttach 加工询价单-附件
     * @return 结果
     */
    public int changePurOutsourceInquiryAttach(PurOutsourceInquiryAttach purOutsourceInquiryAttach);

    /**
     * 批量删除加工询价单-附件
     *
     * @param outsourceInquiryattachmentSids 需要删除的加工询价单-附件ID
     * @return 结果
     */
    public int deletePurOutsourceInquiryAttachByIds(List<Long> outsourceInquiryattachmentSids);


}
