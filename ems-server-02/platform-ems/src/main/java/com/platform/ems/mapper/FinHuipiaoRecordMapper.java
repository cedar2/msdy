package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinHuipiaoRecord;

/**
 * 汇票台账表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinHuipiaoRecordMapper extends BaseMapper<FinHuipiaoRecord> {

    /**
     * 查询详情
     *
     * @param huipiaoRecordSid 单据sid
     * @return FinHuipiaoRecord
     */
    FinHuipiaoRecord selectFinHuipiaoRecordById(Long huipiaoRecordSid);

    /**
     * 查询列表
     *
     * @param finHuipiaoRecord FinHuipiaoRecord
     * @return List
     */
    List<FinHuipiaoRecord> selectFinHuipiaoRecordList(FinHuipiaoRecord finHuipiaoRecord);

    /**
     * 添加多个
     *
     * @param list List FinHuipiaoRecord
     * @return int
     */
    int inserts(@Param("list") List<FinHuipiaoRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinHuipiaoRecord
     * @return int
     */
    int updateAllById(FinHuipiaoRecord entity);

    /**
     * 更新多个
     *
     * @param list List FinHuipiaoRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<FinHuipiaoRecord> list);

}
