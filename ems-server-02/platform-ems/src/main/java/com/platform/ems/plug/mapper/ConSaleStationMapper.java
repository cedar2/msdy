package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSaleStation;

/**
 * 销售站点Mapper接口
 *
 * @author chenkw
 * @date 2023-01-02
 */
public interface ConSaleStationMapper extends BaseMapper<ConSaleStation> {

    ConSaleStation selectConSaleStationById(Long sid);

    List<ConSaleStation> selectConSaleStationList(ConSaleStation conSaleStation);

    /**
     * 添加多个
     *
     * @param list List ConSaleStation
     * @return int
     */
    int inserts(@Param("list") List<ConSaleStation> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConSaleStation
     * @return int
     */
    int updateAllById(ConSaleStation entity);

    /**
     * 更新多个
     *
     * @param list List ConSaleStation
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSaleStation> list);

}
