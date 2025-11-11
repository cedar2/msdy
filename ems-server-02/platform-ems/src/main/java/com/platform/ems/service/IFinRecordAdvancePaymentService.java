package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinRecordAdvancePayment;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商业务台账-预付Service接口
 *
 * @author linhongwei
 * @date 2021-05-29
 */
public interface IFinRecordAdvancePaymentService extends IService<FinRecordAdvancePayment>{
    /**
     * 查询供应商业务台账-预付
     *
     * @param recordAdvancePaymentSid 供应商业务台账-预付ID
     * @return 供应商业务台账-预付
     */
    public FinRecordAdvancePayment selectFinRecordAdvancePaymentById(Long recordAdvancePaymentSid);

    /**
     * 查询供应商业务台账-预付列表
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 供应商业务台账-预付集合
     */
    public List<FinRecordAdvancePayment> selectFinRecordAdvancePaymentList(FinRecordAdvancePayment finRecordAdvancePayment);

    /**
     * 新增供应商业务台账-预付
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 结果
     */
    public int insertFinRecordAdvancePayment(FinRecordAdvancePayment finRecordAdvancePayment);

    /**
     * 修改供应商业务台账-预付
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 结果
     */
    public int updateFinRecordAdvancePayment(FinRecordAdvancePayment finRecordAdvancePayment);

    /**
     * 变更供应商业务台账-预付
     *
     * @param finRecordAdvancePayment 供应商业务台账-预付
     * @return 结果
     */
    public int changeFinRecordAdvancePayment(FinRecordAdvancePayment finRecordAdvancePayment);

    /**
     * 批量删除供应商业务台账-预付
     *
     * @param recordAdvancePaymentSids 需要删除的供应商业务台账-预付ID
     * @return 结果
     */
    public int deleteFinRecordAdvancePaymentByIds(List<Long>  recordAdvancePaymentSids);

    /**
     * 更改确认状态
     * @param finRecordAdvancePayment
     * @return
     */
    int check(FinRecordAdvancePayment finRecordAdvancePayment);


    /**
     * 获取报表
     * @param finRecordAdvancePayment
     * @return
     */
    List<FinRecordAdvancePayment> getReportForm(FinRecordAdvancePayment finRecordAdvancePayment);

    /**
     * 导入初始化报表
     * @param file
     * @return
     */
    AjaxResult importData(MultipartFile file);

    /**
     * 初始化记账
     * @param request
     * @return
     */
    int addForm(List<FinRecordAdvancePayment> request);
}
