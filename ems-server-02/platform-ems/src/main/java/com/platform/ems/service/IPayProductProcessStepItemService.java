package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProductProcessStepItem;

import java.util.List;

/**
 * 商品道序-明细Service接口
 *
 * @author c
 * @date 2021-09-08
 */
public interface IPayProductProcessStepItemService extends IService<PayProductProcessStepItem> {
    /**
     * 查询商品道序-明细
     *
     * @param stepItemSid 商品道序-明细ID
     * @return 商品道序-明细
     */
    public PayProductProcessStepItem selectPayProductProcessStepItemById(Long stepItemSid);

    /**
     * 查询商品道序-明细列表
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细集合
     */
    public List<PayProductProcessStepItem> selectPayProductProcessStepItemList(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 查询商品道序-明细报表
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细集合
     */
    List<PayProductProcessStepItem> selectPayProductProcessStepItemForm(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 查询商品道序-明细   (主要用于计薪量明细查询的接口)
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细
     */
    List<PayProductProcessStepItem> selectPayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 新增商品道序-明细
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 结果
     */
    public int insertPayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 修改商品道序-明细
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 结果
     */
    public int updatePayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 变更商品道序-明细
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 结果
     */
    public int changePayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 批量删除商品道序-明细
     *
     * @param stepItemSids 需要删除的商品道序-明细ID
     * @return 结果
     */
    public int deletePayProductProcessStepItemByIds(List<Long> stepItemSids);

    /**
     * 完工量申报查询明细， 新
     * @param payProductProcessStepItem 主要用来分页
     */
    List<PayProductProcessStepItem> getManOrderItemList(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 校验明细是否可删除
     */
    int verifyItem(Long[] stepItemSids);
}
