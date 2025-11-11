package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBookSourceCategory;

/**
 * 流水来源类别_财务Service接口
 *
 * @author chenkw
 * @date 2021-08-03
 */
public interface IConBookSourceCategoryService extends IService<ConBookSourceCategory> {
    /**
     * 查询流水来源类别_财务
     *
     * @param sid 流水来源类别_财务ID
     * @return 流水来源类别_财务
     */
    public ConBookSourceCategory selectConBookSourceCategoryById(Long sid);

    /**
     * 查询流水来源类别_财务列表
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 流水来源类别_财务集合
     */
    public List<ConBookSourceCategory> selectConBookSourceCategoryList(ConBookSourceCategory conBookSourceCategory);

    /**
     * 新增流水来源类别_财务
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 结果
     */
    public int insertConBookSourceCategory(ConBookSourceCategory conBookSourceCategory);

    /**
     * 修改流水来源类别_财务
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 结果
     */
    public int updateConBookSourceCategory(ConBookSourceCategory conBookSourceCategory);

    /**
     * 变更流水来源类别_财务
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 结果
     */
    public int changeConBookSourceCategory(ConBookSourceCategory conBookSourceCategory);

    /**
     * 批量删除流水来源类别_财务
     *
     * @param sids 需要删除的流水来源类别_财务ID
     * @return 结果
     */
    public int deleteConBookSourceCategoryByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBookSourceCategory
     * @return
     */
    int changeStatus(ConBookSourceCategory conBookSourceCategory);

    /**
     * 更改确认状态
     *
     * @param conBookSourceCategory
     * @return
     */
    int check(ConBookSourceCategory conBookSourceCategory);

    /**
     * 下拉框列表
     */
    List<ConBookSourceCategory> getConBookSourceCategoryList();
}
