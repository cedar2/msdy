package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.dto.response.form.BasMaterialSaleStationCategoryForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialSaleStation;

/**
 * 商品-网店运营信息Mapper接口
 *
 * @author chenkw
 * @date 2023-01-13
 */
public interface BasMaterialSaleStationMapper extends BaseMapper<BasMaterialSaleStation> {

    BasMaterialSaleStation selectBasMaterialSaleStationById(Long materialSaleStationSid);

    List<BasMaterialSaleStation> selectBasMaterialSaleStationList(BasMaterialSaleStation basMaterialSaleStation);

    /**
     * 添加多个
     *
     * @param list List BasMaterialSaleStation
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialSaleStation> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasMaterialSaleStation
     * @return int
     */
    int updateAllById(BasMaterialSaleStation entity);

    /**
     * 更新多个
     *
     * @param list List BasMaterialSaleStation
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialSaleStation> list);

    /**
     * 报表中心类目明细报表
     *
     * @param request BasMaterialSaleStationCategoryForm
     * @return 报表中心类目明细报表
     */
    @SqlParser(filter=true)
    List<BasMaterialSaleStationCategoryForm> selectMaterialSaleStationCategoryForm(BasMaterialSaleStationCategoryForm request);
}
