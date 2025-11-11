package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinHuipiaoRecordUseRecord;

/**
 * 汇票台账-使用记录表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinHuipiaoRecordUseRecordMapper extends BaseMapper<FinHuipiaoRecordUseRecord> {

    /**
     * 查询详情
     *
     * @param huipiaoRecordUseRecordSid 单据sid
     * @return FinHuipiaoRecordUseRecord
     */
    FinHuipiaoRecordUseRecord selectFinHuipiaoRecordUseRecordById(Long huipiaoRecordUseRecordSid);

    /**
     * 查询列表
     *
     * @param finHuipiaoRecordUseRecord FinHuipiaoRecordUseRecord
     * @return List
     */
    List<FinHuipiaoRecordUseRecord> selectFinHuipiaoRecordUseRecordList(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord);

    /**
     * 添加多个
     *
     * @param list List FinHuipiaoRecordUseRecord
     * @return int
     */
    int inserts(@Param("list") List<FinHuipiaoRecordUseRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinHuipiaoRecordUseRecord
     * @return int
     */
    int updateAllById(FinHuipiaoRecordUseRecord entity);

    /**
     * 更新多个
     *
     * @param list List FinHuipiaoRecordUseRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<FinHuipiaoRecordUseRecord> list);

}
