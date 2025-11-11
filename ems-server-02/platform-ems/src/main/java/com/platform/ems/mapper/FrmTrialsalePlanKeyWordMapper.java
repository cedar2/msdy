package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsalePlanKeyWord;

/**
 * 新品试销计划单-关键词分析Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmTrialsalePlanKeyWordMapper extends BaseMapper<FrmTrialsalePlanKeyWord> {

    FrmTrialsalePlanKeyWord selectFrmTrialsalePlanKeyWordById(Long trialsalePlanKeyWordSid);

    List<FrmTrialsalePlanKeyWord> selectFrmTrialsalePlanKeyWordList(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsalePlanKeyWord
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsalePlanKeyWord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsalePlanKeyWord
     * @return int
     */
    int updateAllById(FrmTrialsalePlanKeyWord entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsalePlanKeyWord
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsalePlanKeyWord> list);

}
