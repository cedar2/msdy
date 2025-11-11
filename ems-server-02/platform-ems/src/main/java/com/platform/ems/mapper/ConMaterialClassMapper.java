package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.ConMaterialClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料分类Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-29
 */
public interface ConMaterialClassMapper extends BaseMapper<ConMaterialClass> {


    ConMaterialClass selectConMaterialClassById(Long materialClassSid);

    List<ConMaterialClass> selectConMaterialClassList(ConMaterialClass conMaterialClass);

    /**
     * 添加多个
     *
     * @param list List ConMaterialClass
     * @return int
     */
    int inserts(@Param("list") List<ConMaterialClass> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConMaterialClass
     * @return int
     */
    int updateAllById(ConMaterialClass entity);

    /**
     * 更新多个
     *
     * @param list List ConMaterialClass
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMaterialClass> list);

    /**
     * 根据父级查询所有子孙级
     *
     * @param materialClassSid
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ConMaterialClass> selectConMaterialClassListByParentId(@Param("materialClassSid") Long materialClassSid);

    /**
     * 根据sid查询所有父级
     *
     * @param materialClassSid
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ConMaterialClass> selectConMaterialClassParentsListySon(@Param("materialClassSid") Long materialClassSid);

    /**
     * 获取物料分类下拉列表
     */
    List<ConMaterialClass> getConMaterialClassList(ConMaterialClass conMaterialClass);
}
