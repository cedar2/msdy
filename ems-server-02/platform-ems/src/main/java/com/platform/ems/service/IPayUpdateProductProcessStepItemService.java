package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayUpdateProductProcessStep;
import com.platform.ems.domain.PayUpdateProductProcessStepItem;

/**
 * 商品道序变更-明细Service接口
 * 
 * @author chenkw
 * @date 2022-11-08
 */
public interface IPayUpdateProductProcessStepItemService extends IService<PayUpdateProductProcessStepItem>{
    /**
     * 查询商品道序变更-明细
     * 
     * @param updateStepItemSid 商品道序变更-明细ID
     * @return 商品道序变更-明细
     */
    public PayUpdateProductProcessStepItem selectPayUpdateProductProcessStepItemById(Long updateStepItemSid);

    /**
     * 查询商品道序变更-明细列表
     * 
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 商品道序变更-明细集合
     */
    public List<PayUpdateProductProcessStepItem> selectPayUpdateProductProcessStepItemList(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem);

    /**
     * 新增商品道序变更-明细
     * 
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 结果
     */
    public int insertPayUpdateProductProcessStepItem(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem);

    /**
     * 修改商品道序变更-明细
     * 
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 结果
     */
    public int updatePayUpdateProductProcessStepItem(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem);

    /**
     * 变更商品道序变更-明细
     *
     * @param payUpdateProductProcessStepItem 商品道序变更-明细
     * @return 结果
     */
    public int changePayUpdateProductProcessStepItem(PayUpdateProductProcessStepItem payUpdateProductProcessStepItem);

    /**
     * 批量删除商品道序变更-明细
     * 
     * @param updateStepItemSids 需要删除的商品道序变更-明细ID
     * @return 结果
     */
    public int deletePayUpdateProductProcessStepItemByIds(List<Long>  updateStepItemSids);

    /**
     * 查询商品道序变更-明细
     *
     * @param updateProductProcessStepSid 商品道序变更-主表ID
     * @return 商品道序变更-明细
     */
    public List<PayUpdateProductProcessStepItem> selectPayUpdateProductProcessStepItemListById(Long updateProductProcessStepSid);

    /**
     * 查询商品道序变更-明细 标志位为删除的明细
     *
     * @param updateProductProcessStepSid 商品道序变更-主表ID
     * @return 商品道序变更-明细
     */
    public List<PayUpdateProductProcessStepItem> selectDeleteListById(Long updateProductProcessStepSid);

    /**
     * 批量新增商品道序变更-明细
     *
     * @param step 商品道序变更
     * @return 结果
     */
    public int insertPayUpdateProductProcessStepItemList(PayUpdateProductProcessStep step);

    /**
     * 批量修改商品道序变更-明细
     *
     * @param step 商品道序变更
     * @return 结果
     */
    public int updatePayUpdateProductProcessStepItemList(PayUpdateProductProcessStep step);

    /**
     * 批量删除商品道序变更-明细
     *
     * @param itemList 需要删除的商品道序变更-明细列表
     * @return 结果
     */
    public int deletePayUpdateProductProcessStepItemByList(List<PayUpdateProductProcessStepItem> itemList);

    /**
     * 批量删除商品道序变更-明细 根据主表sids
     *
     * @param updateProductProcessStepSidList 需要删除的商品道序变更sids
     * @return 结果
     */
    public int deletePayUpdateProductProcessStepItemByStep(List<Long> updateProductProcessStepSidList);

}
