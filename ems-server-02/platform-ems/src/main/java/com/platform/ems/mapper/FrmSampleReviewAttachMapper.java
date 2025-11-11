package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmSampleReviewAttach;

/**
 * 样品评审单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-12
 */
public interface FrmSampleReviewAttachMapper extends BaseMapper<FrmSampleReviewAttach> {

    FrmSampleReviewAttach selectFrmSampleReviewAttachById(Long sampleReviewAttachSid);

    List<FrmSampleReviewAttach> selectFrmSampleReviewAttachList(FrmSampleReviewAttach frmSampleReviewAttach);

    /**
     * 添加多个
     *
     * @param list List FrmSampleReviewAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmSampleReviewAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmSampleReviewAttach
     * @return int
     */
    int updateAllById(FrmSampleReviewAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmSampleReviewAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmSampleReviewAttach> list);

}
