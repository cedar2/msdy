package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.HashMap;
import java.util.List;

import com.platform.ems.domain.PrjTaskTemplate;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PrjTaskTemplateItem;

/**
 * 项目任务模板-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-12-07
 */
public interface PrjTaskTemplateItemMapper extends BaseMapper<PrjTaskTemplateItem> {

    PrjTaskTemplateItem selectPrjTaskTemplateItemById(Long taskTemplateItemSid);

    List<PrjTaskTemplateItem> selectPrjTaskTemplateItemList(PrjTaskTemplateItem prjTaskTemplateItem);

    /**
     * 添加多个
     *
     * @param list List PrjTaskTemplateItem
     * @return int
     */
    int inserts(@Param("list") List<PrjTaskTemplateItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjTaskTemplateItem
     * @return int
     */
    int updateAllById(PrjTaskTemplateItem entity);

    /**
     * 更新多个
     *
     * @param list List PrjTaskTemplateItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjTaskTemplateItem> list);

    /**
     * 根据主表sid查出对应主表下的明细数量
     *
     * @param entity Long[]
     * @return int
     */
    @MapKey("taskTemplateSid")
    HashMap<Long, PrjTaskTemplate> selectItemCountGroupByTemplateSid(PrjTaskTemplateItem entity);

}
