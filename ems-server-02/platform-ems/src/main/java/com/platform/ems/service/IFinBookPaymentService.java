package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinBookPayment;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-付款Service接口
 *
 * @author linhongwei
 * @date 2021-06-07
 */
public interface IFinBookPaymentService extends IService<FinBookPayment>{

    /**
     * 新建
     */
    int insertFinBookPayment(FinBookPayment payment);

    /**
     * 查报表
     * @param entity
     * @return
     */
    List<FinBookPayment> getReportForm(FinBookPayment entity);

    /**
     * 导入
     * @param file
     * @return
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 初始化记账
     * @param request
     * @return
     */
    int addForm(List<FinBookPayment> request);
}
