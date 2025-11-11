package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.CosCostLaborTemplate;

/**
 * 商品成本核算-工价成本模板-主Mapper接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface CosCostLaborTemplateMapper  extends BaseMapper<CosCostLaborTemplate> {


    CosCostLaborTemplate selectCosCostLaborTemplateById(Long costLaborTemplateSid);

    List<CosCostLaborTemplate> selectCosCostLaborTemplateList(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 添加多个
     * @param list List CosCostLaborTemplate
     * @return int
     */
    int inserts(@Param("list") List<CosCostLaborTemplate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity CosCostLaborTemplate
     * @return int
     */
    int updateAllById(CosCostLaborTemplate entity);

    /**
     * 更新多个
     * @param list List CosCostLaborTemplate
     * @return int
     */
    int updatesAllById(@Param("list") List<CosCostLaborTemplate> list);


}
