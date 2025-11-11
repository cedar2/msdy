package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.DevMakeSampleForm;
import com.platform.ems.domain.DevSampleReviewForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 样品评审单Mapper接口
 *
 * @author linhongwei
 * @date 2022-03-23
 */
public interface DevSampleReviewFormMapper extends BaseMapper<DevSampleReviewForm> {


    DevSampleReviewForm selectDevSampleReviewFormById(Long sampleReviewFormSid);

    List<DevSampleReviewForm> selectDevSampleReviewFormList(DevSampleReviewForm devSampleReviewForm);

    /**
     * 添加多个
     *
     * @param list List DevSampleReviewForm
     * @return int
     */
    int inserts(@Param("list") List<DevSampleReviewForm> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevSampleReviewForm
     * @return int
     */
    int updateAllById(DevSampleReviewForm entity);

    /**
     * 更新多个
     *
     * @param list List DevSampleReviewForm
     * @return int
     */
    int updatesAllById(@Param("list") List<DevSampleReviewForm> list);


    int updateHandleStatus(DevSampleReviewForm devSampleReviewForm);
}
