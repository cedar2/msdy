package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaSpecraftCheckProducts;

import java.util.List;

/**
 * 特殊工艺检测单-款明细Service接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface IQuaSpecraftCheckProductsService extends IService<QuaSpecraftCheckProducts> {
    /**
     * 查询特殊工艺检测单-款明细
     *
     * @param specraftCheckProductsSid 特殊工艺检测单-款明细ID
     * @return 特殊工艺检测单-款明细
     */
    public QuaSpecraftCheckProducts selectQuaSpecraftCheckProductsById(Long specraftCheckProductsSid);

    /**
     * 查询特殊工艺检测单-款明细列表
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 特殊工艺检测单-款明细集合
     */
    public List<QuaSpecraftCheckProducts> selectQuaSpecraftCheckProductsList(QuaSpecraftCheckProducts quaSpecraftCheckProducts);

    /**
     * 新增特殊工艺检测单-款明细
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 结果
     */
    public int insertQuaSpecraftCheckProducts(QuaSpecraftCheckProducts quaSpecraftCheckProducts);

    /**
     * 修改特殊工艺检测单-款明细
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 结果
     */
    public int updateQuaSpecraftCheckProducts(QuaSpecraftCheckProducts quaSpecraftCheckProducts);

    /**
     * 变更特殊工艺检测单-款明细
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 结果
     */
    public int changeQuaSpecraftCheckProducts(QuaSpecraftCheckProducts quaSpecraftCheckProducts);

    /**
     * 批量删除特殊工艺检测单-款明细
     *
     * @param specraftCheckProductsSids 需要删除的特殊工艺检测单-款明细ID
     * @return 结果
     */
    public int deleteQuaSpecraftCheckProductsByIds(List<Long> specraftCheckProductsSids);

}
