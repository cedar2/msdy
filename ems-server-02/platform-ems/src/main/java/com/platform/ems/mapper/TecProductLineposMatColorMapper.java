package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecProductLineposMatColor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品线部位-款色线色Mapper接口
 *
 * @author linhongwei
 * @date 2021-08-23
 */
public interface TecProductLineposMatColorMapper extends BaseMapper<TecProductLineposMatColor> {


    TecProductLineposMatColor selectTecProductLineposMatColorById(Long lineposMatColor);

    List<TecProductLineposMatColor> selectTecProductLineposMatColorList(TecProductLineposMatColor tecProductLineposMatColor);

    /**
     * 添加多个
     *
     * @param list List TecProductLineposMatColor
     * @return int
     */
    int inserts(@Param("list") List<TecProductLineposMatColor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecProductLineposMatColor
     * @return int
     */
    int updateAllById(TecProductLineposMatColor entity);

    /**
     * 更新多个
     *
     * @param list List TecProductLineposMatColor
     * @return int
     */
    int updatesAllById(@Param("list") List<TecProductLineposMatColor> list);


}
