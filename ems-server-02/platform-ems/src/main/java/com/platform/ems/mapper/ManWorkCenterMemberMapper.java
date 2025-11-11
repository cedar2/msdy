package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManWorkCenterMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作中心-成员Mapper接口
 *
 * @author c
 * @date 2022-03-21
 */
public interface ManWorkCenterMemberMapper extends BaseMapper<ManWorkCenterMember> {


    ManWorkCenterMember selectManWorkCenterMemberById(Long workCenterMemberSid);

    List<ManWorkCenterMember> selectManWorkCenterMemberList(ManWorkCenterMember manWorkCenterMember);

    /**
     * 添加多个
     *
     * @param list List ManWorkCenterMember
     * @return int
     */
    int inserts(@Param("list") List<ManWorkCenterMember> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManWorkCenterMember
     * @return int
     */
    int updateAllById(ManWorkCenterMember entity);

    /**
     * 更新多个
     *
     * @param list List ManWorkCenterMember
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWorkCenterMember> list);


}
