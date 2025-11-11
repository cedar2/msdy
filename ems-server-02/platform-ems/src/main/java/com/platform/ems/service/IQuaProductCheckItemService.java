package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaProductCheckItem;

import java.util.List;

/**
 * 成衣检测单-检测项目Service接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface IQuaProductCheckItemService extends IService<QuaProductCheckItem> {
    /**
     * 查询成衣检测单-检测项目
     *
     * @param productCheckItemSid 成衣检测单-检测项目ID
     * @return 成衣检测单-检测项目
     */
    public QuaProductCheckItem selectQuaProductCheckItemById(Long productCheckItemSid);

    /**
     * 查询成衣检测单-检测项目列表
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 成衣检测单-检测项目集合
     */
    public List<QuaProductCheckItem> selectQuaProductCheckItemList(QuaProductCheckItem quaProductCheckItem);

    /**
     * 新增成衣检测单-检测项目
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 结果
     */
    public int insertQuaProductCheckItem(QuaProductCheckItem quaProductCheckItem);

    /**
     * 修改成衣检测单-检测项目
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 结果
     */
    public int updateQuaProductCheckItem(QuaProductCheckItem quaProductCheckItem);

    /**
     * 变更成衣检测单-检测项目
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 结果
     */
    public int changeQuaProductCheckItem(QuaProductCheckItem quaProductCheckItem);

    /**
     * 批量删除成衣检测单-检测项目
     *
     * @param productCheckItemSids 需要删除的成衣检测单-检测项目ID
     * @return 结果
     */
    public int deleteQuaProductCheckItemByIds(List<Long> productCheckItemSids);

}
