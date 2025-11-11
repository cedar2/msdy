package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvInventoryAdjustReportRequest;
import com.platform.ems.domain.dto.request.InvCrossColorReportRequest;
import com.platform.ems.domain.dto.response.InvCrossColorReportResponse;
import com.platform.ems.domain.dto.response.InvInventoryAdjustReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryAdjustItem;

/**
 * 库存调整单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
public interface InvInventoryAdjustItemMapper  extends BaseMapper<InvInventoryAdjustItem> {


    List<InvInventoryAdjustItem>  selectInvInventoryAdjustItemById(Long inventoryAdjustItemSid);

    List<InvInventoryAdjustItem> selectInvInventoryAdjustItemList(InvInventoryAdjustItem invInventoryAdjustItem);
    /**
     * 库存调整单明细报表
     */
    List<InvInventoryAdjustReportResponse> reportInvInventoryAdjust(InvInventoryAdjustReportRequest invInventoryAdjustReportRequest);
    /**
     * 串色串码明细报表
     */
    List<InvCrossColorReportResponse> reportInvCrossColor(InvCrossColorReportRequest invCrossColorReportRequest);
    /**
     * 添加多个
     * @param list List InvInventoryAdjustItem
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryAdjustItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryAdjustItem
    * @return int
    */
    int updateAllById(InvInventoryAdjustItem entity);

    /**
     * 更新多个
     * @param list List InvInventoryAdjustItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryAdjustItem> list);


    void deleteInvInventoryAdjustItemByIds(@Param("array") Long[] inventoryAdjustSids);
}
