package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecProductLineposMat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品线部位-线料Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-21
 */
public interface TecProductLineposMatMapper extends BaseMapper<TecProductLineposMat> {


    TecProductLineposMat selectTecProductLineposMatById(Long lineposMatSid);

    List<TecProductLineposMat> selectTecProductLineposMatList(TecProductLineposMat tecProductLineposMat);

    /**
     * 添加多个
     *
     * @param list List TecProductLineposMat
     * @return int
     */
    int inserts(@Param("list") List<TecProductLineposMat> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecProductLineposMat
     * @return int
     */
    int updateAllById(TecProductLineposMat entity);

    /**
     * 更新多个
     *
     * @param list List TecProductLineposMat
     * @return int
     */
    int updatesAllById(@Param("list") List<TecProductLineposMat> list);


}
