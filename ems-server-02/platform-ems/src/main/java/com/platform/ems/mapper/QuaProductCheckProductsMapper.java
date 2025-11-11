package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaProductCheckProducts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成衣检测单-款明细Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface QuaProductCheckProductsMapper extends BaseMapper<QuaProductCheckProducts> {


    QuaProductCheckProducts selectQuaProductCheckProductsById(Long productCheckProductsSid);

    List<QuaProductCheckProducts> selectQuaProductCheckProductsList(QuaProductCheckProducts quaProductCheckProducts);

    /**
     * 添加多个
     *
     * @param list List QuaProductCheckProducts
     * @return int
     */
    int inserts(@Param("list") List<QuaProductCheckProducts> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaProductCheckProducts
     * @return int
     */
    int updateAllById(QuaProductCheckProducts entity);

    /**
     * 更新多个
     *
     * @param list List QuaProductCheckProducts
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaProductCheckProducts> list);


}
