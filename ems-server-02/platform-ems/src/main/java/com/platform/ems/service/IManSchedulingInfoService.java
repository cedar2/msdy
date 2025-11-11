package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManSchedulingInfo;

/**
 * 生产排程信息Service接口
 *
 * @author chenkw
 * @date 2023-05-24
 */
public interface IManSchedulingInfoService extends IService<ManSchedulingInfo> {
    /**
     * 查询生产排程信息
     *
     * @param schedulingInfoSid 生产排程信息ID
     * @return 生产排程信息
     */
    public ManSchedulingInfo selectManSchedulingInfoById(Long schedulingInfoSid);

    /**
     * 查询生产排程信息列表
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 生产排程信息集合
     */
    public List<ManSchedulingInfo> selectManSchedulingInfoList(ManSchedulingInfo manSchedulingInfo);

    /**
     * 新增生产排程信息
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    public int insertManSchedulingInfo(ManSchedulingInfo manSchedulingInfo);

    /**
     * 新增生产排程信息 根据商品道序
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    public int insertManSchedulingInfoByProcessStep(ManSchedulingInfo manSchedulingInfo);

    /**
     * 修改生产排程信息
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    public int updateManSchedulingInfo(ManSchedulingInfo manSchedulingInfo);

    /**
     * 变更生产排程信息
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    public int changeManSchedulingInfo(ManSchedulingInfo manSchedulingInfo);

    /**
     * 批量删除生产排程信息
     *
     * @param schedulingInfoSids 需要删除的生产排程信息ID
     * @return 结果
     */
    public int deleteManSchedulingInfoByIds(List<Long> schedulingInfoSids);

    /**
     * 更改确认状态
     *
     * @param manSchedulingInfo
     * @return
     */
    int check(ManSchedulingInfo manSchedulingInfo);

}
