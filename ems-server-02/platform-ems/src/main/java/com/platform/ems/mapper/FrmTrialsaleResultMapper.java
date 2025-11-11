package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsaleResult;

/**
 * 试销结果单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface FrmTrialsaleResultMapper extends BaseMapper<FrmTrialsaleResult> {

    FrmTrialsaleResult selectFrmTrialsaleResultById(Long trialsaleResultSid);

    List<FrmTrialsaleResult> selectFrmTrialsaleResultList(FrmTrialsaleResult frmTrialsaleResult);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsaleResult
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsaleResult> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsaleResult
     * @return int
     */
    int updateAllById(FrmTrialsaleResult entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsaleResult
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsaleResult> list);

}
