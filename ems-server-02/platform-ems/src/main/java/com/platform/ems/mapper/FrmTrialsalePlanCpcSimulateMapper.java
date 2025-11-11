package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsalePlanCpcSimulate;

/**
 * 新品试销计划单-CPC模拟数据Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmTrialsalePlanCpcSimulateMapper extends BaseMapper<FrmTrialsalePlanCpcSimulate> {

    FrmTrialsalePlanCpcSimulate selectFrmTrialsalePlanCpcSimulateById(Long trialsalePlanCpcSimulationSid);

    List<FrmTrialsalePlanCpcSimulate> selectFrmTrialsalePlanCpcSimulateList(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsalePlanCpcSimulate
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsalePlanCpcSimulate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsalePlanCpcSimulate
     * @return int
     */
    int updateAllById(FrmTrialsalePlanCpcSimulate entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsalePlanCpcSimulate
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsalePlanCpcSimulate> list);

}
