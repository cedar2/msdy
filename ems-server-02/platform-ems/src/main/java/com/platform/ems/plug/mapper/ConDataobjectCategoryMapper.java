package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDataobjectCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据对象类别Mapper接口
 *
 * @author c
 * @date 2021-09-06
 */
public interface ConDataobjectCategoryMapper extends BaseMapper<ConDataobjectCategory> {


    ConDataobjectCategory selectConDataobjectCategoryById(Long sid);

    List<ConDataobjectCategory> selectConDataobjectCategoryList(ConDataobjectCategory conDataobjectCategory);

    /**
     * 添加多个
     *
     * @param list List ConDataobjectCategory
     * @return int
     */
    int inserts(@Param("list") List<ConDataobjectCategory> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDataobjectCategory
     * @return int
     */
    int updateAllById(ConDataobjectCategory entity);

    /**
     * 更新多个
     *
     * @param list List ConDataobjectCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDataobjectCategory> list);

    /**
     * 数据对象类别下拉接口
     */
    List<ConDataobjectCategory> getDataobjectCategoryList(ConDataobjectCategory conDataobjectCategory);

    /**
     * 数据库表名下拉框
     *
     * @param table 实体
     * @return 结果
     */
    @SqlParser(filter=true)
    List<ConDataobjectCategory> selectDbTableList(ConDataobjectCategory table);
}
