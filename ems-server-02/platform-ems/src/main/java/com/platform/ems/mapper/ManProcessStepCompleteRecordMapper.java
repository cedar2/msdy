package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProcessStepCompleteRecord;

/**
 * 商品道序完成量台账-主Mapper接口
 *
 * @author chenkw
 * @date 2022-10-20
 */
public interface ManProcessStepCompleteRecordMapper extends BaseMapper<ManProcessStepCompleteRecord> {


    ManProcessStepCompleteRecord selectManProcessStepCompleteRecordById(Long stepCompleteRecordSid);

    List<ManProcessStepCompleteRecord> selectManProcessStepCompleteRecordList(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 添加多个
     *
     * @param list List ManProcessStepCompleteRecord
     * @return int
     */
    int inserts(@Param("list") List<ManProcessStepCompleteRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManProcessStepCompleteRecord
     * @return int
     */
    int updateAllById(ManProcessStepCompleteRecord entity);

    /**
     * 更新多个
     *
     * @param list List ManProcessStepCompleteRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProcessStepCompleteRecord> list);


}
