package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.SalSaleContractAttachment;

/**
 * 销售合同信息-附件Service接口
 *
 * @author linhongwei
 * @date 2021-05-18
 */
public interface ISalSaleContractAttachmentService extends IService<SalSaleContractAttachment>{
    /**
     * 查询销售合同信息-附件
     *
     * @param saleContractAttachmentSid 销售合同信息-附件ID
     * @return 销售合同信息-附件
     */
    public SalSaleContractAttachment selectSalSaleContractAttachmentById(Long saleContractAttachmentSid);

    /**
     * 查询销售合同信息-附件列表
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 销售合同信息-附件集合
     */
    public List<SalSaleContractAttachment> selectSalSaleContractAttachmentList(SalSaleContractAttachment salSaleContractAttachment);

    /**
     * 新增销售合同信息-附件
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 结果
     */
    public int insertSalSaleContractAttachment(SalSaleContractAttachment salSaleContractAttachment);

    /**
     * 修改销售合同信息-附件
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 结果
     */
    public int updateSalSaleContractAttachment(SalSaleContractAttachment salSaleContractAttachment);

    /**
     * 变更销售合同信息-附件
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 结果
     */
    public int changeSalSaleContractAttachment(SalSaleContractAttachment salSaleContractAttachment);

    /**
     * 批量删除销售合同信息-附件
     *
     * @param saleContractAttachmentSids 需要删除的销售合同信息-附件ID
     * @return 结果
     */
    public int deleteSalSaleContractAttachmentByIds(List<Long> saleContractAttachmentSids);

    /**
     * 销售合同查询页面上传附件前的校验
     * @param salSaleContractAttachment
     * @return
     */
    AjaxResult check(SalSaleContractAttachment salSaleContractAttachment);

}
