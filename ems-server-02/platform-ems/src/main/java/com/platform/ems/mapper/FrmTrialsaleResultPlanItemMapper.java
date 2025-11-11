package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsaleResultPlanItem;

/**
 * 试销结果单-计划项Mapper接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface FrmTrialsaleResultPlanItemMapper extends BaseMapper<FrmTrialsaleResultPlanItem> {

    FrmTrialsaleResultPlanItem selectFrmTrialsaleResultPlanItemById(Long trialsaleResultPlanItemSid);

    List<FrmTrialsaleResultPlanItem> selectFrmTrialsaleResultPlanItemList(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsaleResultPlanItem
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsaleResultPlanItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsaleResultPlanItem
     * @return int
     */
    int updateAllById(FrmTrialsaleResultPlanItem entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsaleResultPlanItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsaleResultPlanItem> list);

}
