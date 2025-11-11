package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinBookReceiptPayment;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-收款Service接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IFinBookReceiptPaymentService extends IService<FinBookReceiptPayment>{

    /**
     * 新建
     */
    int insertFinBookReceiptPayment(FinBookReceiptPayment payment);

    /**
     * 流水报表查询
     * @param entity
     * @return
     */
    List<FinBookReceiptPayment> getReportForm(FinBookReceiptPayment entity);

    /**
     * 导入
     * @param file
     * @return
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 记账
     * @param request
     * @return
     */
    int addForm(List<FinBookReceiptPayment> request);
}
