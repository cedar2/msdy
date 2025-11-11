package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinHuipiaoRecordAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 汇票台账-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface FinHuipiaoRecordAttachMapper extends BaseMapper<FinHuipiaoRecordAttach> {

    FinHuipiaoRecordAttach selectFinHuipiaoRecordAttachById(Long huipiaoRecordAttachSid);

    List<FinHuipiaoRecordAttach> selectFinHuipiaoRecordAttachList(FinHuipiaoRecordAttach finHuipiaoRecordAttach);

    /**
     * 添加多个
     *
     * @param list List FinHuipiaoRecordAttach
     * @return int
     */
    int inserts(@Param("list") List<FinHuipiaoRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinHuipiaoRecordAttach
     * @return int
     */
    int updateAllById(FinHuipiaoRecordAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinHuipiaoRecordAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinHuipiaoRecordAttach> list);


}
