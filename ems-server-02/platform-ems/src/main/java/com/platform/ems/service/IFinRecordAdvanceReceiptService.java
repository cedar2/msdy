package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinRecordAdvanceReceipt;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户业务台账-预收Service接口
 *
 * @author linhongwei
 * @date 2021-06-16
 */
public interface IFinRecordAdvanceReceiptService extends IService<FinRecordAdvanceReceipt>{
    /**
     * 查询客户业务台账-预收
     *
     * @param recordAdvanceReceiptSid 客户业务台账-预收ID
     * @return 客户业务台账-预收
     */
    FinRecordAdvanceReceipt selectFinRecordAdvanceReceiptById(Long recordAdvanceReceiptSid);

    /**
     * 查询客户业务台账-预收列表
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 客户业务台账-预收集合
     */
    List<FinRecordAdvanceReceipt> selectFinRecordAdvanceReceiptList(FinRecordAdvanceReceipt finRecordAdvanceReceipt);

    /**
     * 新增客户业务台账-预收
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 结果
     */
    int insertFinRecordAdvanceReceipt(FinRecordAdvanceReceipt finRecordAdvanceReceipt);

    /**
     * 修改客户业务台账-预收
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 结果
     */
    int updateFinRecordAdvanceReceipt(FinRecordAdvanceReceipt finRecordAdvanceReceipt);

    /**
     * 变更客户业务台账-预收
     *
     * @param finRecordAdvanceReceipt 客户业务台账-预收
     * @return 结果
     */
    int changeFinRecordAdvanceReceipt(FinRecordAdvanceReceipt finRecordAdvanceReceipt);

    /**
     * 批量删除客户业务台账-预收
     *
     * @param recordAdvanceReceiptSids 需要删除的客户业务台账-预收ID
     * @return 结果
     */
    int deleteFinRecordAdvanceReceiptByIds(List<Long>  recordAdvanceReceiptSids);

    /**
     * 更改确认状态
     * @param finRecordAdvanceReceipt
     * @return
     */
    int check(FinRecordAdvanceReceipt finRecordAdvanceReceipt);

    /**
     * 查询报表
     * @param entity
     * @return
     */
    List<FinRecordAdvanceReceipt> getReportForm(FinRecordAdvanceReceipt entity);

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
    int addForm(List<FinRecordAdvanceReceipt> request);
}
