package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaRawmatCheckProducts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面辅料检测单-款明细Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface QuaRawmatCheckProductsMapper extends BaseMapper<QuaRawmatCheckProducts> {


    QuaRawmatCheckProducts selectQuaRawmatCheckProductsById(Long RawmatCheckProductsSid);

    List<QuaRawmatCheckProducts> selectQuaRawmatCheckProductsList(QuaRawmatCheckProducts quaRawmatCheckProducts);

    /**
     * 添加多个
     *
     * @param list List QuaRawmatCheckProducts
     * @return int
     */
    int inserts(@Param("list") List<QuaRawmatCheckProducts> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaRawmatCheckProducts
     * @return int
     */
    int updateAllById(QuaRawmatCheckProducts entity);

    /**
     * 更新多个
     *
     * @param list List QuaRawmatCheckProducts
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaRawmatCheckProducts> list);


}
