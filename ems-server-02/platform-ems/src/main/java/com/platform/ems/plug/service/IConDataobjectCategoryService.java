package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDataobjectCategory;

import java.util.List;

/**
 * 数据对象类别Service接口
 *
 * @author c
 * @date 2021-09-06
 */
public interface IConDataobjectCategoryService extends IService<ConDataobjectCategory> {
    /**
     * 查询数据对象类别
     *
     * @param sid 数据对象类别ID
     * @return 数据对象类别
     */
    public ConDataobjectCategory selectConDataobjectCategoryById(Long sid);

    /**
     * 查询数据对象类别列表
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 数据对象类别集合
     */
    public List<ConDataobjectCategory> selectConDataobjectCategoryList(ConDataobjectCategory conDataobjectCategory);

    /**
     * 新增数据对象类别
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 结果
     */
    public int insertConDataobjectCategory(ConDataobjectCategory conDataobjectCategory);

    /**
     * 修改数据对象类别
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 结果
     */
    public int updateConDataobjectCategory(ConDataobjectCategory conDataobjectCategory);

    /**
     * 变更数据对象类别
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 结果
     */
    public int changeConDataobjectCategory(ConDataobjectCategory conDataobjectCategory);

    /**
     * 批量删除数据对象类别
     *
     * @param sids 需要删除的数据对象类别ID
     * @return 结果
     */
    public int deleteConDataobjectCategoryByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDataobjectCategory
     * @return
     */
    int changeStatus(ConDataobjectCategory conDataobjectCategory);

    /**
     * 更改确认状态
     *
     * @param conDataobjectCategory
     * @return
     */
    int check(ConDataobjectCategory conDataobjectCategory);

    /**
     * 数据对象类别下拉接口
     */
    List<ConDataobjectCategory> getDataobjectCategoryList(ConDataobjectCategory conDataobjectCategory);
}
