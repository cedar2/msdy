package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaRawmatCheckProducts;

import java.util.List;

/**
 * 面辅料检测单-款明细Service接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface IQuaRawmatCheckProductsService extends IService<QuaRawmatCheckProducts> {
    /**
     * 查询面辅料检测单-款明细
     *
     * @param RawmatCheckProductsSid 面辅料检测单-款明细ID
     * @return 面辅料检测单-款明细
     */
    public QuaRawmatCheckProducts selectQuaRawmatCheckProductsById(Long RawmatCheckProductsSid);

    /**
     * 查询面辅料检测单-款明细列表
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 面辅料检测单-款明细集合
     */
    public List<QuaRawmatCheckProducts> selectQuaRawmatCheckProductsList(QuaRawmatCheckProducts quaRawmatCheckProducts);

    /**
     * 新增面辅料检测单-款明细
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 结果
     */
    public int insertQuaRawmatCheckProducts(QuaRawmatCheckProducts quaRawmatCheckProducts);

    /**
     * 修改面辅料检测单-款明细
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 结果
     */
    public int updateQuaRawmatCheckProducts(QuaRawmatCheckProducts quaRawmatCheckProducts);

    /**
     * 变更面辅料检测单-款明细
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 结果
     */
    public int changeQuaRawmatCheckProducts(QuaRawmatCheckProducts quaRawmatCheckProducts);

    /**
     * 批量删除面辅料检测单-款明细
     *
     * @param RawmatCheckProductsSids 需要删除的面辅料检测单-款明细ID
     * @return 结果
     */
    public int deleteQuaRawmatCheckProductsByIds(List<Long> RawmatCheckProductsSids);

}
