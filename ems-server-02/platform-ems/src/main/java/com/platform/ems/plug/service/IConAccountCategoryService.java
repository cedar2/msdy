package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAccountCategory;

import java.util.List;

/**
 * 款项类别Service接口
 *
 * @author linhongwei
 * @date 2021-06-22
 */
public interface IConAccountCategoryService extends IService<ConAccountCategory>{
    /**
     * 查询款项类别
     *
     * @param sid 款项类别ID
     * @return 款项类别
     */
    public ConAccountCategory selectConAccountCategoryById(Long sid);

    /**
     * 查询款项类别列表
     *
     * @param conAccountCategory 款项类别
     * @return 款项类别集合
     */
    public List<ConAccountCategory> selectConAccountCategoryList(ConAccountCategory conAccountCategory);

    /**
     * 新增款项类别
     *
     * @param conAccountCategory 款项类别
     * @return 结果
     */
    public int insertConAccountCategory(ConAccountCategory conAccountCategory);

    /**
     * 修改款项类别
     *
     * @param conAccountCategory 款项类别
     * @return 结果
     */
    public int updateConAccountCategory(ConAccountCategory conAccountCategory);

    /**
     * 变更款项类别
     *
     * @param conAccountCategory 款项类别
     * @return 结果
     */
    public int changeConAccountCategory(ConAccountCategory conAccountCategory);

    /**
     * 批量删除款项类别
     *
     * @param sids 需要删除的款项类别ID
     * @return 结果
     */
    public int deleteConAccountCategoryByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conAccountCategory
    * @return
    */
    int changeStatus(ConAccountCategory conAccountCategory);

    /**
     * 更改确认状态
     * @param conAccountCategory
     * @return
     */
    int check(ConAccountCategory conAccountCategory);

    /**
     * 款项类别下拉框列表
     */
    List<ConAccountCategory> getConAccountCategoryList();
}
