package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsaleResultPriceScheme;

/**
 * 试销结果单-定价方案Mapper接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface FrmTrialsaleResultPriceSchemeMapper extends BaseMapper<FrmTrialsaleResultPriceScheme> {

    FrmTrialsaleResultPriceScheme selectFrmTrialsaleResultPriceSchemeById(Long trialsaleResultPriceSchemeSid);

    List<FrmTrialsaleResultPriceScheme> selectFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsaleResultPriceScheme
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsaleResultPriceScheme> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsaleResultPriceScheme
     * @return int
     */
    int updateAllById(FrmTrialsaleResultPriceScheme entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsaleResultPriceScheme
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsaleResultPriceScheme> list);

}
