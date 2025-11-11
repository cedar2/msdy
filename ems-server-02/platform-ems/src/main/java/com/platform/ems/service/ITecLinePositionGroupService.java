package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecLinePositionGroup;

import java.util.List;

/**
 * 线部位组档案Service接口
 *
 * @author hjj
 * @date 2021-08-19
 */
public interface ITecLinePositionGroupService extends IService<TecLinePositionGroup> {
    /**
     * 查询线部位组档案
     *
     * @param groupSid 线部位组档案ID
     * @return 线部位组档案
     */
    public TecLinePositionGroup selectTecLinePositionGroupById(Long groupSid);

    /**
     * 查询线部位组档案列表
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 线部位组档案集合
     */
    public List<TecLinePositionGroup> selectTecLinePositionGroupList(TecLinePositionGroup tecLinePositionGroup);

    /**
     * 新增线部位组档案
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 结果
     */
    public int insertTecLinePositionGroup(TecLinePositionGroup tecLinePositionGroup);

    /**
     * 修改线部位组档案
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 结果
     */
    public int updateTecLinePositionGroup(TecLinePositionGroup tecLinePositionGroup);

    /**
     * 变更线部位组档案
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 结果
     */
    public int changeTecLinePositionGroup(TecLinePositionGroup tecLinePositionGroup);

    /**
     * 批量删除线部位组档案
     *
     * @param groupSids 需要删除的线部位组档案ID
     * @return 结果
     */
    public int deleteTecLinePositionGroupByIds(List<Long> groupSids);

    /**
     * 启用/停用
     *
     * @param tecLinePositionGroup
     * @return
     */
    int changeStatus(TecLinePositionGroup tecLinePositionGroup);

    /**
     * 更改确认状态
     *
     * @param tecLinePositionGroup
     * @return
     */
    int check(TecLinePositionGroup tecLinePositionGroup);

}
