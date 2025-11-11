package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinBookReceiptEstimation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应收暂估Service接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface IFinBookReceiptEstimationService extends IService<FinBookReceiptEstimation>{

    /**
     * 新增财务流水账-应收暂估
     */
    public int insertFinBookReceiptEstimation(FinBookReceiptEstimation finBookReceiptEstimation);

    /**
     * 查报表
     */
    List<FinBookReceiptEstimation> getReportForm(FinBookReceiptEstimation entity);

    /**
     * 导入
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 初始化记账
     */
    int addForm(List<FinBookReceiptEstimation> request);

    /**
     * 新增财务流水账-应付暂估
     */
    int insertEstimation(FinBookReceiptEstimation finBookPaymentEstimation);
}
