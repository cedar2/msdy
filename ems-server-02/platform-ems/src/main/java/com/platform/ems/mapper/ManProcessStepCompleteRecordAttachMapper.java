package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProcessStepCompleteRecordAttach;

/**
 * 商品道序完成量台账-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-10-20
 */
public interface ManProcessStepCompleteRecordAttachMapper extends BaseMapper<ManProcessStepCompleteRecordAttach> {

    ManProcessStepCompleteRecordAttach selectManProcessStepCompleteRecordAttachById(Long attachmentSid);

    List<ManProcessStepCompleteRecordAttach> selectManProcessStepCompleteRecordAttachList(ManProcessStepCompleteRecordAttach manProcessStepCompleteRecordAttach);

    /**
     * 添加多个
     *
     * @param list List ManProcessStepCompleteRecordAttach
     * @return int
     */
    int inserts(@Param("list") List<ManProcessStepCompleteRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManProcessStepCompleteRecordAttach
     * @return int
     */
    int updateAllById(ManProcessStepCompleteRecordAttach entity);

    /**
     * 更新多个
     *
     * @param list List ManProcessStepCompleteRecordAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProcessStepCompleteRecordAttach> list);


}
