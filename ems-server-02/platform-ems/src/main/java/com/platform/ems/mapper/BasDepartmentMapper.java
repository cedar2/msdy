package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasDepartment;

/**
 * 部门档案Mapper接口
 *
 * @author qhq
 * @date 2021-04-09
 */
public interface BasDepartmentMapper  extends BaseMapper<BasDepartment> {


    BasDepartment selectBasDepartmentById(Long departmentSid);

    List<BasDepartment> selectBasDepartmentList(BasDepartment basDepartment);

    /**
     * 添加多个
     * @param list List BasDepartment
     * @return int
     */
    int inserts(@Param("list") List<BasDepartment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasDepartment
    * @return int
    */
    int updateAllById(BasDepartment entity);

    /**
     * 更新多个
     * @param list List BasDepartment
     * @return int
     */
    int updatesAllById(@Param("list") List<BasDepartment> list);

    List<BasDepartment> getDeptList(BasDepartment basDepartment);

}
