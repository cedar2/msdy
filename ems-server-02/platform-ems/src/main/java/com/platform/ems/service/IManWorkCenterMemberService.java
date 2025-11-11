package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManWorkCenterMember;

import java.util.List;

/**
 * 工作中心-成员Service接口
 *
 * @author c
 * @date 2022-03-21
 */
public interface IManWorkCenterMemberService extends IService<ManWorkCenterMember> {
    /**
     * 查询工作中心-成员
     *
     * @param workCenterMemberSid 工作中心-成员ID
     * @return 工作中心-成员
     */
    public ManWorkCenterMember selectManWorkCenterMemberById(Long workCenterMemberSid);

    /**
     * 查询工作中心-成员列表
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 工作中心-成员集合
     */
    public List<ManWorkCenterMember> selectManWorkCenterMemberList(ManWorkCenterMember manWorkCenterMember);

    /**
     * 新增工作中心-成员
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 结果
     */
    public int insertManWorkCenterMember(ManWorkCenterMember manWorkCenterMember);

    /**
     * 修改工作中心-成员
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 结果
     */
    public int updateManWorkCenterMember(ManWorkCenterMember manWorkCenterMember);

    /**
     * 变更工作中心-成员
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 结果
     */
    public int changeManWorkCenterMember(ManWorkCenterMember manWorkCenterMember);

    /**
     * 批量删除工作中心-成员
     *
     * @param workCenterMemberSids 需要删除的工作中心-成员ID
     * @return 结果
     */
    public int deleteManWorkCenterMemberByIds(List<Long> workCenterMemberSids);

}
