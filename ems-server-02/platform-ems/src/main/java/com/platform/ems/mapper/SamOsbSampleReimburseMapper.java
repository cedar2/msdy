package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SamOsbSampleReimburse;
import com.platform.ems.domain.dto.request.SamOsbSampleReimburseReportRequert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外采样报销单-主Mapper接口
 *
 * @author qhq
 * @date 2021-12-28
 */
public interface SamOsbSampleReimburseMapper  extends BaseMapper<SamOsbSampleReimburse> {


    SamOsbSampleReimburse selectSamOsbSampleReimburseById (Long reimburseSid);

    List<SamOsbSampleReimburse> selectSamOsbSampleReimburseList (SamOsbSampleReimburse samOsbSampleReimburse);

    /**
     * 添加多个
     * @param list List SamOsbSampleReimburse
     * @return int
     */
    int inserts (@Param("list") List<SamOsbSampleReimburse> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SamOsbSampleReimburse
    * @return int
    */
    int updateAllById (SamOsbSampleReimburse entity);

    /**
     * 更新多个
     * @param list List SamOsbSampleReimburse
     * @return int
     */
    int updatesAllById (@Param("list") List<SamOsbSampleReimburse> list);

    List<SamOsbSampleReimburseReportRequert> selectReport(SamOsbSampleReimburseReportRequert requert);
}
