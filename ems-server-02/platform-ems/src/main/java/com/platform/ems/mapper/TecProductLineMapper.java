package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecProductLine;
import com.platform.ems.domain.dto.request.EstimateLineReportRequest;
import com.platform.ems.domain.dto.response.EstimateLineReportResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品线Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-21
 */
public interface TecProductLineMapper extends BaseMapper<TecProductLine> {


    TecProductLine selectTecProductLineById(Long productLineSid);

    List<TecProductLine> selectTecProductLineList(TecProductLine tecProductLine);

    List<EstimateLineReportResponse> getEstimateLine(EstimateLineReportRequest request);

    /**
     * 添加多个
     *
     * @param list List TecProductLine
     * @return int
     */
    int inserts(@Param("list") List<TecProductLine> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecProductLine
     * @return int
     */
    int updateAllById(TecProductLine entity);

    /**
     * 更新多个
     *
     * @param list List TecProductLine
     * @return int
     */
    int updatesAllById(@Param("list") List<TecProductLine> list);


}
