package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevMarketSurveyAttach;

/**
 * 市场调研-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-08
 */
public interface DevMarketSurveyAttachMapper extends BaseMapper<DevMarketSurveyAttach> {


    DevMarketSurveyAttach selectDevMarketSurveyAttachById(Long marketSurveyAttachSid);

    List<DevMarketSurveyAttach> selectDevMarketSurveyAttachList(DevMarketSurveyAttach devMarketSurveyAttach);

    /**
     * 添加多个
     *
     * @param list List DevMarketSurveyAttach
     * @return int
     */
    int inserts(@Param("list") List<DevMarketSurveyAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevMarketSurveyAttach
     * @return int
     */
    int updateAllById(DevMarketSurveyAttach entity);

    /**
     * 更新多个
     *
     * @param list List DevMarketSurveyAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<DevMarketSurveyAttach> list);


}
