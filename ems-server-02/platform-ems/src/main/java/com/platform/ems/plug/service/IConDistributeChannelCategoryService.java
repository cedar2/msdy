package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.domain.ConDistributeChannelCategory;

/**
 * 分销渠道类别Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDistributeChannelCategoryService extends IService<ConDistributeChannelCategory>{
    /**
     * 查询分销渠道类别
     *
     * @param sid 分销渠道类别ID
     * @return 分销渠道类别
     */
    public ConDistributeChannelCategory selectConDistributeChannelCategoryById(Long sid);

    /**
     * 查询分销渠道类别列表
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 分销渠道类别集合
     */
    public List<ConDistributeChannelCategory> selectConDistributeChannelCategoryList(ConDistributeChannelCategory conDistributeChannelCategory);

    /**
     * 新增分销渠道类别
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 结果
     */
    public int insertConDistributeChannelCategory(ConDistributeChannelCategory conDistributeChannelCategory);

    /**
     * 修改分销渠道类别
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 结果
     */
    public int updateConDistributeChannelCategory(ConDistributeChannelCategory conDistributeChannelCategory);

    /**
     * 变更分销渠道类别
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 结果
     */
    public int changeConDistributeChannelCategory(ConDistributeChannelCategory conDistributeChannelCategory);

    /**
     * 批量删除分销渠道类别
     *
     * @param sids 需要删除的分销渠道类别ID
     * @return 结果
     */
    public int deleteConDistributeChannelCategoryByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDistributeChannelCategory
    * @return
    */
    int changeStatus(ConDistributeChannelCategory conDistributeChannelCategory);

    /**
     * 更改确认状态
     * @param conDistributeChannelCategory
     * @return
     */
    int check(ConDistributeChannelCategory conDistributeChannelCategory);

    /**  获取下拉列表 */
    List<ConDistributeChannelCategory> getConDistributeChannelCategoryList();
}
