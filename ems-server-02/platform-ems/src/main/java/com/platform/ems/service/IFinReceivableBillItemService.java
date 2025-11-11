package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItem;

/**
 * 收款单-明细Service接口
 *
 * @author linhongwei
 * @date 2021-04-22
 */
public interface IFinReceivableBillItemService extends IService<FinReceivableBillItem>{

    /**
     * 查询收款单-明细
     *
     * @param receivableBillItemSid 收款单-明细ID
     * @return 收款单-明细
     */
    public FinReceivableBillItem selectFinReceivableBillItemById(Long receivableBillItemSid);

    /**
     * 查询收款单-明细列表
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 收款单-明细集合
     */
    public List<FinReceivableBillItem> selectFinReceivableBillItemList(FinReceivableBillItem finReceivableBillItem);

    /**
     * 新增收款单-明细
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    public int insertFinReceivableBillItem(FinReceivableBillItem finReceivableBillItem);

    /**
     * 修改收款单-明细
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    public int updateFinReceivableBillItem(FinReceivableBillItem finReceivableBillItem);

    /**
     * 变更收款单-明细
     *
     * @param finReceivableBillItem 收款单-明细
     * @return 结果
     */
    public int changeFinReceivableBillItem(FinReceivableBillItem finReceivableBillItem);

    /**
     * 批量删除收款单-明细
     *
     * @param receivableBillItemSids 需要删除的收款单-明细ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemByIds(List<Long>  receivableBillItemSids);

    /**
     * 批量新增
     */
    public int insertByList(FinReceivableBill bill);

    /**
     * 批量修改
     */
    public int updateByList(FinReceivableBill bill);

    /**
     * 批量删除
     */
    public int deleteByList(List<FinReceivableBillItem> itemList);
}
