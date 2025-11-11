package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.ConTaskTemplateCompare;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author platform
 * @date 2023-11-03
 */
public interface ConTaskTemplateCompareMapper  extends BaseMapper<ConTaskTemplateCompare> {

    /**
     * 查询详情
     * @param taskTemplateCompareSid 单据sid
     * @return ConTaskTemplateCompare
     */
    ConTaskTemplateCompare selectConTaskTemplateCompareById(Long taskTemplateCompareSid);

    /**
     * 查询列表
     * @param conTaskTemplateCompare ConTaskTemplateCompare
     * @return List
     */
    List<ConTaskTemplateCompare> selectConTaskTemplateCompareList(ConTaskTemplateCompare conTaskTemplateCompare);

    /**
     * 添加多个
     * @param list List ConTaskTemplateCompare
     * @return int
     */
    int inserts(@Param("list") List<ConTaskTemplateCompare> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConTaskTemplateCompare
     * @return int
     */
    int updateAllById(ConTaskTemplateCompare entity);

    /**
     * 更新多个
     * @param list List ConTaskTemplateCompare
     * @return int
     */
    int updatesAllById(@Param("list") List<ConTaskTemplateCompare> list);

}

