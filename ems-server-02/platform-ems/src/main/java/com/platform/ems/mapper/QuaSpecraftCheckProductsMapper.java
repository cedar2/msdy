package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaSpecraftCheckProducts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 特殊工艺检测单-款明细Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface QuaSpecraftCheckProductsMapper extends BaseMapper<QuaSpecraftCheckProducts> {


    QuaSpecraftCheckProducts selectQuaSpecraftCheckProductsById(Long specraftCheckProductsSid);

    List<QuaSpecraftCheckProducts> selectQuaSpecraftCheckProductsList(QuaSpecraftCheckProducts quaSpecraftCheckProducts);

    /**
     * 添加多个
     *
     * @param list List QuaSpecraftCheckProducts
     * @return int
     */
    int inserts(@Param("list") List<QuaSpecraftCheckProducts> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaSpecraftCheckProducts
     * @return int
     */
    int updateAllById(QuaSpecraftCheckProducts entity);

    /**
     * 更新多个
     *
     * @param list List QuaSpecraftCheckProducts
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaSpecraftCheckProducts> list);


}
