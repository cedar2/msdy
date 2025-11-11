package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysClientMovementType;

/**
 * 作业类型_租户级Mapper接口
 *
 * @author chenkw
 * @date 2022-06-17
 */
public interface SysClientMovementTypeMapper extends BaseMapper<SysClientMovementType> {


    SysClientMovementType selectSysClientMovementTypeById(Long clientMovementTypeSid);

    List<SysClientMovementType> selectSysClientMovementTypeList(SysClientMovementType sysClientMovementType);

    /**
     * 添加多个
     *
     * @param list List SysClientMovementType
     * @return int
     */
    int inserts(@Param("list") List<SysClientMovementType> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysClientMovementType
     * @return int
     */
    int updateAllById(SysClientMovementType entity);

    /**
     * 更新多个
     *
     * @param list List SysClientMovementType
     * @return int
     */
    int updatesAllById(@Param("list") List<SysClientMovementType> list);


}
