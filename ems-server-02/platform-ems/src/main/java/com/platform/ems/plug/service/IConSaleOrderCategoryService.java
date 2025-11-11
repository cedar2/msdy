package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConSaleOrderCategory;

/**
 * 销售订单类别Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConSaleOrderCategoryService extends IService<ConSaleOrderCategory>{
    /**
     * 查询销售订单类别
     * 
     * @param sid 销售订单类别ID
     * @return 销售订单类别
     */
    public ConSaleOrderCategory selectConSaleOrderCategoryById(Long sid);

    /**
     * 查询销售订单类别列表
     * 
     * @param conSaleOrderCategory 销售订单类别
     * @return 销售订单类别集合
     */
    public List<ConSaleOrderCategory> selectConSaleOrderCategoryList(ConSaleOrderCategory conSaleOrderCategory);

    /**
     * 新增销售订单类别
     * 
     * @param conSaleOrderCategory 销售订单类别
     * @return 结果
     */
    public int insertConSaleOrderCategory(ConSaleOrderCategory conSaleOrderCategory);

    /**
     * 修改销售订单类别
     * 
     * @param conSaleOrderCategory 销售订单类别
     * @return 结果
     */
    public int updateConSaleOrderCategory(ConSaleOrderCategory conSaleOrderCategory);

    /**
     * 变更销售订单类别
     *
     * @param conSaleOrderCategory 销售订单类别
     * @return 结果
     */
    public int changeConSaleOrderCategory(ConSaleOrderCategory conSaleOrderCategory);

    /**
     * 批量删除销售订单类别
     * 
     * @param sids 需要删除的销售订单类别ID
     * @return 结果
     */
    public int deleteConSaleOrderCategoryByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conSaleOrderCategory
    * @return
    */
    int changeStatus(ConSaleOrderCategory conSaleOrderCategory);

    /**
     * 更改确认状态
     * @param conSaleOrderCategory
     * @return
     */
    int check(ConSaleOrderCategory conSaleOrderCategory);

}
