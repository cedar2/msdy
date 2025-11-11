package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.common.core.domain.entity.SysOrg;
import org.apache.ibatis.annotations.Param;

/**
 * 组织架构信息Mapper接口
 *
 * @author qhq
 * @date 2021-03-18
 */
public interface SysOrgMapper extends BaseMapper<SysOrg> {

    SysOrg selectSysOrgById(Long nodeSid);

    List<SysOrg> selectSysOrgList(SysOrg sysOrg);

    /**
     * 添加多个
     * @param list List SysOrg
     * @return int
     */
    int inserts(@Param("list") List<SysOrg> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity SysOrg
     * @return int
     */
    int updateAllById(SysOrg entity);

    /**
     * 更新多个
     * @param list List SysOrg
     * @return int
     */
    int updatesAllById(@Param("list") List<SysOrg> list);




}
