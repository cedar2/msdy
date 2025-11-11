package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaSpecraftCheckItem;

import java.util.List;

/**
 * 特殊工艺检测单-检测项目Service接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface IQuaSpecraftCheckItemService extends IService<QuaSpecraftCheckItem> {
    /**
     * 查询特殊工艺检测单-检测项目
     *
     * @param specraftCheckItemSid 特殊工艺检测单-检测项目ID
     * @return 特殊工艺检测单-检测项目
     */
    public QuaSpecraftCheckItem selectQuaSpecraftCheckItemById(Long specraftCheckItemSid);

    /**
     * 查询特殊工艺检测单-检测项目列表
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 特殊工艺检测单-检测项目集合
     */
    public List<QuaSpecraftCheckItem> selectQuaSpecraftCheckItemList(QuaSpecraftCheckItem quaSpecraftCheckItem);

    /**
     * 新增特殊工艺检测单-检测项目
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 结果
     */
    public int insertQuaSpecraftCheckItem(QuaSpecraftCheckItem quaSpecraftCheckItem);

    /**
     * 修改特殊工艺检测单-检测项目
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 结果
     */
    public int updateQuaSpecraftCheckItem(QuaSpecraftCheckItem quaSpecraftCheckItem);

    /**
     * 变更特殊工艺检测单-检测项目
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 结果
     */
    public int changeQuaSpecraftCheckItem(QuaSpecraftCheckItem quaSpecraftCheckItem);

    /**
     * 批量删除特殊工艺检测单-检测项目
     *
     * @param specraftCheckItemSids 需要删除的特殊工艺检测单-检测项目ID
     * @return 结果
     */
    public int deleteQuaSpecraftCheckItemByIds(List<Long> specraftCheckItemSids);

}
