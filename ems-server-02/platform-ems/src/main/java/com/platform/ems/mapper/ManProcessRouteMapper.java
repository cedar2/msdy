package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManMonthManufacturePlanProcess;
import com.platform.ems.domain.ManProcessRoute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工艺路线Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-26
 */
public interface ManProcessRouteMapper extends BaseMapper<ManProcessRoute> {


    ManProcessRoute selectManProcessRouteById(Long processRouteSid);

    List<ManMonthManufacturePlanProcess> addItem(@Param("processRouteSid") Long processRouteSid
             ,@Param("manufactureOrderSid") Long manufactureOrderSid
             ,@Param("workCenterSid") Long workCenterSid
    );

    List<ManProcessRoute> selectManProcessRouteList(ManProcessRoute manProcessRoute);

    /**
     * 添加多个
     *
     * @param list List ManProcessRoute
     * @return int
     */
    int inserts(@Param("list") List<ManProcessRoute> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManProcessRoute
     * @return int
     */
    int updateAllById(ManProcessRoute entity);

    /**
     * 更新多个
     *
     * @param list List ManProcessRoute
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProcessRoute> list);

    /**
     * 款项类别下拉框列表
     */
    List<ManProcessRoute> getManProcessRouteList(ManProcessRoute manProcessRoute);
}
