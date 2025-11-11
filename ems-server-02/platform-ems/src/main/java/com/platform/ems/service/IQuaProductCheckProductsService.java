package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaProductCheckProducts;

import java.util.List;

/**
 * 成衣检测单-款明细Service接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface IQuaProductCheckProductsService extends IService<QuaProductCheckProducts> {
    /**
     * 查询成衣检测单-款明细
     *
     * @param productCheckProductsSid 成衣检测单-款明细ID
     * @return 成衣检测单-款明细
     */
    public QuaProductCheckProducts selectQuaProductCheckProductsById(Long productCheckProductsSid);

    /**
     * 查询成衣检测单-款明细列表
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 成衣检测单-款明细集合
     */
    public List<QuaProductCheckProducts> selectQuaProductCheckProductsList(QuaProductCheckProducts quaProductCheckProducts);

    /**
     * 新增成衣检测单-款明细
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 结果
     */
    public int insertQuaProductCheckProducts(QuaProductCheckProducts quaProductCheckProducts);

    /**
     * 修改成衣检测单-款明细
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 结果
     */
    public int updateQuaProductCheckProducts(QuaProductCheckProducts quaProductCheckProducts);

    /**
     * 变更成衣检测单-款明细
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 结果
     */
    public int changeQuaProductCheckProducts(QuaProductCheckProducts quaProductCheckProducts);

    /**
     * 批量删除成衣检测单-款明细
     *
     * @param productCheckProductsSids 需要删除的成衣检测单-款明细ID
     * @return 结果
     */
    public int deleteQuaProductCheckProductsByIds(List<Long> productCheckProductsSids);

}
