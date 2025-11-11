package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosCostLaborTemplateItem;

/**
 * 商品成本核算-工价成本模板-明细Mapper接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface CosCostLaborTemplateItemMapper  extends BaseMapper<CosCostLaborTemplateItem> {


    CosCostLaborTemplateItem selectCosCostLaborTemplateItemById(Long costLaborTemplateItemSid);

    List<CosCostLaborTemplateItem> selectCosCostLaborTemplateItemList(CosCostLaborTemplateItem cosCostLaborTemplateItem);

    /**
     * 添加多个
     * @param list List CosCostLaborTemplateItem
     * @return int
     */
    int inserts(@Param("list") List<CosCostLaborTemplateItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity CosCostLaborTemplateItem
    * @return int
    */
    int updateAllById(CosCostLaborTemplateItem entity);

    /**
     * 更新多个
     * @param list List CosCostLaborTemplateItem
     * @return int
     */
    int updatesAllById(@Param("list") List<CosCostLaborTemplateItem> list);


}
