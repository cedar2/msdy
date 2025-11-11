package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProduceConcernTaskGroupItem;

/**
 * 生产关注事项组-明细Service接口
 *
 * @author zhuangyz
 * @date 2022-08-02
 */
public interface IManProduceConcernTaskGroupItemService extends IService<ManProduceConcernTaskGroupItem> {
    /**
     * 查询生产关注事项组-明细
     *
     * @param concernTaskGroupItemSid 生产关注事项组-明细ID
     * @return 生产关注事项组-明细
     */
    public ManProduceConcernTaskGroupItem selectManProduceConcernTaskGroupItemById(Long concernTaskGroupItemSid);

    /**
     * 查询生产关注事项组-明细列表
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 生产关注事项组-明细集合
     */
    public List<ManProduceConcernTaskGroupItem> selectManProduceConcernTaskGroupItemList(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

    /**
     * 新增生产关注事项组-明细
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 结果
     */
    public int insertManProduceConcernTaskGroupItem(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

    /**
     * 修改生产关注事项组-明细
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 结果
     */
    //public int updateManProduceConcernTaskGroupItem(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

    /**
     * 变更生产关注事项组-明细
     *
     * @param manProduceConcernTaskGroupItem 生产关注事项组-明细
     * @return 结果
     */
    //public int changeManProduceConcernTaskGroupItem(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

    /**
     * 批量删除生产关注事项组-明细
     *
     * @param concernTaskGroupItemSids 需要删除的生产关注事项组-明细ID
     * @return 结果
     */
    public int deleteManProduceConcernTaskGroupItemByIds(List<Long> concernTaskGroupItemSids);

    /**
     * 启用/停用
     *
     * @param manProduceConcernTaskGroupItem
     * @return
     */
    //int changeStatus(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

    /**
     * 更改确认状态
     *
     * @param manProduceConcernTaskGroupItem
     * @return
     */
    //int check(ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem);

}
