package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConManufactureDepartment;

/**
 * 生产操作部门Mapper接口
 *
 * @author zhuangyz
 * @date 2022-07-25
 */
public interface ConManufactureDepartmentMapper extends BaseMapper<ConManufactureDepartment> {


    ConManufactureDepartment selectConManufactureDepartmentById(Long sid);

    List<ConManufactureDepartment> selectConManufactureDepartmentList(ConManufactureDepartment conManufactureDepartment);

    /**
     * 添加多个
     *
     * @param list List ConManufactureDepartment
     * @return int
     */
    int inserts(@Param("list") List<ConManufactureDepartment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConManufactureDepartment
     * @return int
     */
    int updateAllById(ConManufactureDepartment entity);

    /**
     * 更新多个
     *
     * @param list List ConManufactureDepartment
     * @return int
     */
    int updatesAllById(@Param("list") List<ConManufactureDepartment> list);


}
