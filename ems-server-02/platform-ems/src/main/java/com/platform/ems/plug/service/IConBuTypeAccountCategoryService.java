package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;

/**
 * 业务类型对应款项类别Service接口
 *
 * @author chenkw
 * @date 2022-06-22
 */
public interface IConBuTypeAccountCategoryService extends IService<ConBuTypeAccountCategory> {
    /**
     * 查询业务类型对应款项类别
     *
     * @param sid 业务类型对应款项类别ID
     * @return 业务类型对应款项类别
     */
    public ConBuTypeAccountCategory selectConBuTypeAccountCategoryById(Long sid);

    /**
     * 查询业务类型对应款项类别列表
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别集合
     */
    public List<ConBuTypeAccountCategory> selectConBuTypeAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 新增业务类型对应款项类别
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 结果
     */
    public int insertConBuTypeAccountCategory(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 修改业务类型对应款项类别
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 结果
     */
    public int updateConBuTypeAccountCategory(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 变更业务类型对应款项类别
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 结果
     */
    public int changeConBuTypeAccountCategory(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 批量删除业务类型对应款项类别
     *
     * @param sids 需要删除的业务类型对应款项类别ID
     * @return 结果
     */
    public int deleteConBuTypeAccountCategoryByIds(List<Long> sids);

    /**
     * 更改确认状态
     *
     * @param conBuTypeAccountCategory
     * @return
     */
    int check(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 获取下拉框接口
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别集合
     */
    public List<ConBuTypeAccountCategory> getConBuTypeAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 获取款项类别下拉框接口
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别集合
     */
    public List<ConBuTypeAccountCategory> getAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 获取流水类型下拉框接口
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别集合
     */
    public List<ConBuTypeAccountCategory> getBookTypeList(ConBuTypeAccountCategory conBuTypeAccountCategory);

}
