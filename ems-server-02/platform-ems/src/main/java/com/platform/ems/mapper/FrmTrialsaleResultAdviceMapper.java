package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsaleResultAdvice;

/**
 * 试销结果单-优化建议Mapper接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface FrmTrialsaleResultAdviceMapper extends BaseMapper<FrmTrialsaleResultAdvice> {

    FrmTrialsaleResultAdvice selectFrmTrialsaleResultAdviceById(Long trialsaleResultAdviceSid);

    List<FrmTrialsaleResultAdvice> selectFrmTrialsaleResultAdviceList(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsaleResultAdvice
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsaleResultAdvice> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsaleResultAdvice
     * @return int
     */
    int updateAllById(FrmTrialsaleResultAdvice entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsaleResultAdvice
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsaleResultAdvice> list);

}
