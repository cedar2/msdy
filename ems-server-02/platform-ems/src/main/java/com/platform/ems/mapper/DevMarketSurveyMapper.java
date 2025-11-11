package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.DevMarketSurvey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 市场调研Mapper接口
 *
 * @author chenkw
 * @date 2022-12-08
 */
public interface DevMarketSurveyMapper extends BaseMapper<DevMarketSurvey> {


    DevMarketSurvey selectDevMarketSurveyById(Long marketSurveySid);

    List<DevMarketSurvey> selectDevMarketSurveyListOrderByDesc(DevMarketSurvey devMarketSurvey);

    /**
     * 添加多个
     *
     * @param list List DevMarketSurvey
     * @return int
     */
    int inserts(@Param("list") List<DevMarketSurvey> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevMarketSurvey
     * @return int
     */
    int updateAllById(DevMarketSurvey entity);

    /**
     * 更新多个
     *
     * @param list List DevMarketSurvey
     * @return int
     */
    int updatesAllById(@Param("list") List<DevMarketSurvey> list);


    List<DevMarketSurvey> selectByYearAndCompanySidAndBrandCode(
            @Param("year") String year,
            @Param("companySid") Long companySid,
            @Param("brandCode") String brandCode,
            @Param("groupType") String groupType
    );

}
