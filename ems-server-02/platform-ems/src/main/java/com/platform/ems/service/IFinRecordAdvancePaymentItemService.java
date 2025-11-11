package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinRecordAdvancePaymentItem;

/**
 * 供应商业务台账-明细-预付Service接口
 * 
 * @author linhongwei
 * @date 2021-05-29
 */
public interface IFinRecordAdvancePaymentItemService extends IService<FinRecordAdvancePaymentItem>{
    /**
     * 查询供应商业务台账-明细-预付
     * 
     * @param recordAdvancePaymentItemSid 供应商业务台账-明细-预付ID
     * @return 供应商业务台账-明细-预付
     */
    public FinRecordAdvancePaymentItem selectFinRecordAdvancePaymentItemById(Long recordAdvancePaymentItemSid);

    /**
     * 查询供应商业务台账-明细-预付列表
     * 
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 供应商业务台账-明细-预付集合
     */
    public List<FinRecordAdvancePaymentItem> selectFinRecordAdvancePaymentItemList(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 新增供应商业务台账-明细-预付
     * 
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 结果
     */
    public int insertFinRecordAdvancePaymentItem(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 修改供应商业务台账-明细-预付
     * 
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 结果
     */
    public int updateFinRecordAdvancePaymentItem(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 变更供应商业务台账-明细-预付
     *
     * @param finRecordAdvancePaymentItem 供应商业务台账-明细-预付
     * @return 结果
     */
    public int changeFinRecordAdvancePaymentItem(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 批量删除供应商业务台账-明细-预付
     * 
     * @param recordAdvancePaymentItemSids 需要删除的供应商业务台账-明细-预付ID
     * @return 结果
     */
    public int deleteFinRecordAdvancePaymentItemByIds(List<Long>  recordAdvancePaymentItemSids);

    /**
    * 启用/停用
    * @param finRecordAdvancePaymentItem
    * @return
    */
    int changeStatus(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 更改确认状态
     * @param finRecordAdvancePaymentItem
     * @return
     */
    int check(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 设置到期日
     * @param request
     * @return
     */
    int setValidDate(FinRecordAdvancePaymentItem request);

}
