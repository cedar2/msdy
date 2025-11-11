package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsalePlanProfitSimulate;

/**
 * 新品试销计划单-利润模拟Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmTrialsalePlanProfitSimulateMapper extends BaseMapper<FrmTrialsalePlanProfitSimulate> {

    FrmTrialsalePlanProfitSimulate selectFrmTrialsalePlanProfitSimulateById(Long trialsalePlanProfitSimulationSid);

    List<FrmTrialsalePlanProfitSimulate> selectFrmTrialsalePlanProfitSimulateList(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsalePlanProfitSimulate
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsalePlanProfitSimulate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsalePlanProfitSimulate
     * @return int
     */
    int updateAllById(FrmTrialsalePlanProfitSimulate entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsalePlanProfitSimulate
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsalePlanProfitSimulate> list);

}
