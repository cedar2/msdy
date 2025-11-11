package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.SalSalesOrderAttachment;

/**
 * 销售订单-附件Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface ISalSalesOrderAttachmentService extends IService<SalSalesOrderAttachment> {
    /**
     * 查询销售订单-附件
     *
     * @param salesOrderAttachmentSid 销售订单-附件ID
     * @return 销售订单-附件
     */
    public SalSalesOrderAttachment selectSalSalesOrderAttachmentById(Long salesOrderAttachmentSid);

    /**
     * 查询销售订单-附件列表
     *
     * @param salSalesOrderAttachment 销售订单-附件
     * @return 销售订单-附件集合
     */
    public List<SalSalesOrderAttachment> selectSalSalesOrderAttachmentList(SalSalesOrderAttachment salSalesOrderAttachment);

    /**
     * 新增销售订单-附件
     *
     * @param salSalesOrderAttachment 销售订单-附件
     * @return 结果
     */
    public int insertSalSalesOrderAttachment(SalSalesOrderAttachment salSalesOrderAttachment);

    /**
     * 修改销售订单-附件
     *
     * @param salSalesOrderAttachment 销售订单-附件
     * @return 结果
     */
    public int updateSalSalesOrderAttachment(SalSalesOrderAttachment salSalesOrderAttachment);

    /**
     * 批量删除销售订单-附件
     *
     * @param salesOrderAttachmentSids 需要删除的销售订单-附件ID
     * @return 结果
     */
    public int deleteSalSalesOrderAttachmentByIds(List<Long> salesOrderAttachmentSids);

    /**
     * 查询页面上传附件前的校验
     *
     * @param salSalesOrderAttachment
     * @return
     */
    AjaxResult check(SalSalesOrderAttachment salSalesOrderAttachment);
}
