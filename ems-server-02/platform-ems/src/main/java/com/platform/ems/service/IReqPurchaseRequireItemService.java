package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ReqPurchaseRequire;
import com.platform.ems.domain.ReqPurchaseRequireItem;

/**
 * 申购单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface IReqPurchaseRequireItemService extends IService<ReqPurchaseRequireItem>{
    /**
     * 查询申购单-明细
     * 
     * @param purchaseRequireItemSid 申购单-明细ID
     * @return 申购单-明细
     */
    public ReqPurchaseRequireItem selectReqPurchaseRequireItemById(Long purchaseRequireItemSid);

    /**
     * 查询申购单-明细列表
     * 
     * @param reqPurchaseRequireItem 申购单-明细
     * @return 申购单-明细集合
     */
    public List<ReqPurchaseRequireItem> selectReqPurchaseRequireItemList(ReqPurchaseRequireItem reqPurchaseRequireItem);

    /**
     * 新增申购单-明细
     * 
     * @param reqPurchaseRequireItem 申购单-明细
     * @return 结果
     */
    public int insertReqPurchaseRequireItem(ReqPurchaseRequireItem reqPurchaseRequireItem);

    /**
     * 修改申购单-明细
     * 
     * @param reqPurchaseRequireItem 申购单-明细
     * @return 结果
     */
    public int updateReqPurchaseRequireItem(ReqPurchaseRequireItem reqPurchaseRequireItem);

    /**
     * 批量删除申购单-明细
     * 
     * @param purchaseRequireItemSids 需要删除的申购单-明细ID
     * @return 结果
     */
    public int deleteReqPurchaseRequireItemByIds(List<Long> purchaseRequireItemSids);

    List<ReqPurchaseRequireItem> getItemList(ReqPurchaseRequireItem reqPurchaseRequireItem);
}
