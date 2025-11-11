package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmSampleReview;

/**
 * 样品评审单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-12
 */
public interface FrmSampleReviewMapper extends BaseMapper<FrmSampleReview> {

    FrmSampleReview selectFrmSampleReviewById(Long sampleReviewSid);

    List<FrmSampleReview> selectFrmSampleReviewList(FrmSampleReview frmSampleReview);

    /**
     * 添加多个
     *
     * @param list List FrmSampleReview
     * @return int
     */
    int inserts(@Param("list") List<FrmSampleReview> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmSampleReview
     * @return int
     */
    int updateAllById(FrmSampleReview entity);

    /**
     * 更新多个
     *
     * @param list List FrmSampleReview
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmSampleReview> list);

}
