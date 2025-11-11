package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasImage;

/**
 * 图案档案Mapper接口
 *
 * @author chenkw
 * @date 2022-12-14
 */
public interface BasImageMapper extends BaseMapper<BasImage> {

    BasImage selectBasImageById(Long imageSid);

    List<BasImage> selectBasImageList(BasImage basImage);

    /**
     * 添加多个
     *
     * @param list List BasImage
     * @return int
     */
    int inserts(@Param("list") List<BasImage> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasImage
     * @return int
     */
    int updateAllById(BasImage entity);

    /**
     * 更新多个
     *
     * @param list List BasImage
     * @return int
     */
    int updatesAllById(@Param("list") List<BasImage> list);

}
