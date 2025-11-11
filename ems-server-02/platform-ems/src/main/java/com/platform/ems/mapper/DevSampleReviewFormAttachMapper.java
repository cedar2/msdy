package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.DevSampleReviewFormAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 样品评审单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2022-03-23
 */
public interface DevSampleReviewFormAttachMapper extends BaseMapper<DevSampleReviewFormAttach> {


    DevSampleReviewFormAttach selectDevSampleReviewFormAttachById(Long attachmentSid);

    List<DevSampleReviewFormAttach> selectDevSampleReviewFormAttachList(DevSampleReviewFormAttach devSampleReviewFormAttach);

    /**
     * 添加多个
     *
     * @param list List DevSampleReviewFormAttach
     * @return int
     */
    int inserts(@Param("list") List<DevSampleReviewFormAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevSampleReviewFormAttach
     * @return int
     */
    int updateAllById(DevSampleReviewFormAttach entity);

    /**
     * 更新多个
     *
     * @param list List DevSampleReviewFormAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<DevSampleReviewFormAttach> list);


}
