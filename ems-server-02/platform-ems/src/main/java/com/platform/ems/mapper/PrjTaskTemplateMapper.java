package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PrjTaskTemplate;
import com.platform.ems.domain.dto.request.form.PrjTaskTemplateFormRequest;
import com.platform.ems.domain.dto.response.form.PrjTaskTemplateFormResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目任务模板Mapper接口
 *
 * @author chenkw
 * @date 2022-12-07
 */
public interface PrjTaskTemplateMapper extends BaseMapper<PrjTaskTemplate> {

    PrjTaskTemplate selectPrjTaskTemplateById(Long taskTemplateSid);

    List<PrjTaskTemplate> selectPrjTaskTemplateList(PrjTaskTemplate prjTaskTemplate);

    /**
     * 添加多个
     *
     * @param list List PrjTaskTemplate
     * @return int
     */
    int inserts(@Param("list") List<PrjTaskTemplate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjTaskTemplate
     * @return int
     */
    int updateAllById(PrjTaskTemplate entity);

    /**
     * 更新多个
     *
     * @param list List PrjTaskTemplate
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjTaskTemplate> list);

    List<PrjTaskTemplateFormResponse> selectPrjTaskTemplateForm(PrjTaskTemplateFormRequest prjTaskTemplate);

}
