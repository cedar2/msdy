package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinBookPaymentEstimation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应付暂估Service接口
 *
 * @author qhq
 * @date 2021-05-31
 */
public interface IFinBookPaymentEstimationService extends IService<FinBookPaymentEstimation>{

    /**
     * 查询财务流水账-应付暂估列表
     */
    public List<FinBookPaymentEstimation> selectFinBookPaymentEstimationList(FinBookPaymentEstimation finBookPaymentEstimation);

    /**
     * 新增财务流水账-应付暂估
     */
    public int insertFinBookPaymentEstimation(FinBookPaymentEstimation finBookPaymentEstimation);

    /**
     * 报表查询
     */
    List<FinBookPaymentEstimation> getReportForm(FinBookPaymentEstimation request);

    /**
     * 导入
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 初始化记账
     */
    int addForm(List<FinBookPaymentEstimation> request);

    /**
     * 新增财务流水账-应付暂估
     */
    int insertEstimation(FinBookPaymentEstimation finBookPaymentEstimation);
}
