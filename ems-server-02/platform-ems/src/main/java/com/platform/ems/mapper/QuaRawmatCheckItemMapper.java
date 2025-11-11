package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaRawmatCheckItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面辅料检测单-检测项目Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface QuaRawmatCheckItemMapper extends BaseMapper<QuaRawmatCheckItem> {


    QuaRawmatCheckItem selectQuaRawmatCheckItemById(Long rawmatCheckItemSid);

    List<QuaRawmatCheckItem> selectQuaRawmatCheckItemList(QuaRawmatCheckItem quaRawmatCheckItem);

    /**
     * 添加多个
     *
     * @param list List QuaRawmatCheckItem
     * @return int
     */
    int inserts(@Param("list") List<QuaRawmatCheckItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaRawmatCheckItem
     * @return int
     */
    int updateAllById(QuaRawmatCheckItem entity);

    /**
     * 更新多个
     *
     * @param list List QuaRawmatCheckItem
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaRawmatCheckItem> list);


}
