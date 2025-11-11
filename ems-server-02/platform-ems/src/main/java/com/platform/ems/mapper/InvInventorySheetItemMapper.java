package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvInventorySheetReportRequest;
import com.platform.ems.domain.dto.response.InvInventorySheetReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventorySheetItem;

/**
 * 盘点单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface InvInventorySheetItemMapper  extends BaseMapper<InvInventorySheetItem> {


    List<InvInventorySheetItem> selectInvInventorySheetItemById(Long inventorySheetSid);

    List<InvInventorySheetItem> selectInvInventorySheetItemList(InvInventorySheetItem invInventorySheetItem);
    /**
     * 获取盘点单明细报表
     */
    List<InvInventorySheetReportResponse> reportInvInventorySheet(InvInventorySheetReportRequest invInventorySheetReportRequest);

    /**
     * 添加多个
     * @param list List InvInventorySheetItem
     * @return int
     */
    int inserts(@Param("list") List<InvInventorySheetItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventorySheetItem
    * @return int
    */
    int updateAllById(InvInventorySheetItem entity);

    /**
     * 更新多个
     * @param list List InvInventorySheetItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventorySheetItem> list);


    void deleteInvInventorySheetItemByIds(@Param("array") Long[] inventorySheetSids);
}
