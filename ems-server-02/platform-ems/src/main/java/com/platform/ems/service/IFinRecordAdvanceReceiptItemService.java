package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinRecordAdvanceReceiptItem;

/**
 * 客户业务台账-明细-预收Service接口
 * 
 * @author linhongwei
 * @date 2021-06-16
 */
public interface IFinRecordAdvanceReceiptItemService extends IService<FinRecordAdvanceReceiptItem>{
    /**
     * 查询客户业务台账-明细-预收
     * 
     * @param recordAdvanceReceiptItemSid 客户业务台账-明细-预收ID
     * @return 客户业务台账-明细-预收
     */
    public FinRecordAdvanceReceiptItem selectFinRecordAdvanceReceiptItemById(Long recordAdvanceReceiptItemSid);

    /**
     * 查询客户业务台账-明细-预收列表
     * 
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 客户业务台账-明细-预收集合
     */
    public List<FinRecordAdvanceReceiptItem> selectFinRecordAdvanceReceiptItemList(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 新增客户业务台账-明细-预收
     * 
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 结果
     */
    public int insertFinRecordAdvanceReceiptItem(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 修改客户业务台账-明细-预收
     * 
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 结果
     */
    public int updateFinRecordAdvanceReceiptItem(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 变更客户业务台账-明细-预收
     *
     * @param finRecordAdvanceReceiptItem 客户业务台账-明细-预收
     * @return 结果
     */
    public int changeFinRecordAdvanceReceiptItem(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 批量删除客户业务台账-明细-预收
     * 
     * @param recordAdvanceReceiptItemSids 需要删除的客户业务台账-明细-预收ID
     * @return 结果
     */
    public int deleteFinRecordAdvanceReceiptItemByIds(List<Long>  recordAdvanceReceiptItemSids);

    /**
    * 启用/停用
    * @param finRecordAdvanceReceiptItem
    * @return
    */
    int changeStatus(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 更改确认状态
     * @param finRecordAdvanceReceiptItem
     * @return
     */
    int check(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 设置到期日
     * @param request
     * @return
     */
    int setValidDate(FinRecordAdvanceReceiptItem request);

}
