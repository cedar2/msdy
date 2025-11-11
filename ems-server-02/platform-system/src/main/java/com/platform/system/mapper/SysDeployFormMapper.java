package com.platform.system.mapper;

import java.util.List;

import com.platform.system.domain.SysDeployForm;
import com.platform.system.domain.dto.FlowProcDefDto;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 单与流程实例关联Mapper接口
 *
 * @author qhq
 * @date 2021-09-03
 */
public interface SysDeployFormMapper  extends BaseMapper<SysDeployForm> {

    SysDeployForm selectSysDeployFormById(Long formProcessRelatSid);

    List<FlowProcDefDto> selectDeployList(@Param("name") String name);

    List<SysDeployForm> selectSysDeployFormList(SysDeployForm sysDeployForm);

    /**
     * 添加多个
     * @param list List SysDeployForm
     * @return int
     */
    int inserts(@Param("list") List<SysDeployForm> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysDeployForm
    * @return int
    */
    int updateAllById(SysDeployForm entity);

    /**
     * 更新多个
     * @param list List SysDeployForm
     * @return int
     */
    int updatesAllById(@Param("list") List<SysDeployForm> list);
}
