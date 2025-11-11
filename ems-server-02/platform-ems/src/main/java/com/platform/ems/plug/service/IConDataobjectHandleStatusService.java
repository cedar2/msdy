package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDataobjectHandleStatus;

/**
 * 数据对象类别与处理状态Service接口
 *
 * @author linhongwei
 * @date 2022-06-23
 */
public interface IConDataobjectHandleStatusService extends IService<ConDataobjectHandleStatus> {
    /**
     * 查询数据对象类别与处理状态
     *
     * @param sid 数据对象类别与处理状态ID
     * @return 数据对象类别与处理状态
     */
    public ConDataobjectHandleStatus selectConDataobjectHandleStatusById(Long sid);

    /**
     * 查询数据对象类别与处理状态列表
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 数据对象类别与处理状态集合
     */
    public List<ConDataobjectHandleStatus> selectConDataobjectHandleStatusList(ConDataobjectHandleStatus conDataobjectHandleStatus);

    /**
     * 查询数据对象类别与处理状态分组按数据对象类别
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 数据对象类别与处理状态集合
     */
    public List<ConDataobjectHandleStatus> selectConDataobjectHandleStatusGroup(ConDataobjectHandleStatus conDataobjectHandleStatus);

    /**
     * 新增数据对象类别与处理状态
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 结果
     */
    public int insertConDataobjectHandleStatus(ConDataobjectHandleStatus conDataobjectHandleStatus);

    /**
     * 修改数据对象类别与处理状态
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 结果
     */
    public int updateConDataobjectHandleStatus(ConDataobjectHandleStatus conDataobjectHandleStatus);

    /**
     * 变更数据对象类别与处理状态
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 结果
     */
    public int changeConDataobjectHandleStatus(ConDataobjectHandleStatus conDataobjectHandleStatus);

    /**
     * 批量删除数据对象类别与处理状态
     *
     * @param sids 需要删除的数据对象类别与处理状态ID
     * @return 结果
     */
    public int deleteConDataobjectHandleStatusByIds(List<Long> sids);

    /**
     * 获取下拉框
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 数据对象类别与处理状态集合
     */
    public List<ConDataobjectHandleStatus> getList(ConDataobjectHandleStatus conDataobjectHandleStatus);

}
