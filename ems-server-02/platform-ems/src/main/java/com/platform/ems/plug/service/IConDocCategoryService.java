package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocCategory;

/**
 * 单据类别Service接口
 *
 * @author chenkw
 * @date 2021-08-02
 */
public interface IConDocCategoryService extends IService<ConDocCategory> {
    /**
     * 查询单据类别
     *
     * @param sid 单据类别ID
     * @return 单据类别
     */
    public ConDocCategory selectConDocCategoryById(Long sid);

    /**
     * 查询单据类别列表
     *
     * @param conDocCategory 单据类别
     * @return 单据类别集合
     */
    public List<ConDocCategory> selectConDocCategoryList(ConDocCategory conDocCategory);

    /**
     * 新增单据类别
     *
     * @param conDocCategory 单据类别
     * @return 结果
     */
    public int insertConDocCategory(ConDocCategory conDocCategory);

    /**
     * 修改单据类别
     *
     * @param conDocCategory 单据类别
     * @return 结果
     */
    public int updateConDocCategory(ConDocCategory conDocCategory);

    /**
     * 变更单据类别
     *
     * @param conDocCategory 单据类别
     * @return 结果
     */
    public int changeConDocCategory(ConDocCategory conDocCategory);

    /**
     * 批量删除单据类别
     *
     * @param sids 需要删除的单据类别ID
     * @return 结果
     */
    public int deleteConDocCategoryByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDocCategory
     * @return
     */
    int changeStatus(ConDocCategory conDocCategory);

    /**
     * 更改确认状态
     *
     * @param conDocCategory
     * @return
     */
    int check(ConDocCategory conDocCategory);

    /**
     * 获取下拉列表
     */
    List<ConDocCategory> getConDocCategoryList();
}
