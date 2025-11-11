package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvInventoryDocumentReportRequest;
import com.platform.ems.domain.dto.response.InvInventoryDocumentReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryDocumentItem;

/**
 * 库存凭证-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-16
 */
public interface InvInventoryDocumentItemMapper  extends BaseMapper<InvInventoryDocumentItem> {

    List<InvInventoryDocumentItem> selectReport(InvInventoryDocumentItem invInventoryDocumentItem);

    List<InvInventoryDocumentItem> selectInvInventoryDocumentItemById(Long inventoryDocumentItemSid);

    List<InvInventoryDocumentItem> selectInvInventoryDocumentItemList(InvInventoryDocumentItem invInventoryDocumentItem);

    /**
     * 简单查询明细表 通过 创建日期进行 降序
     */
    List<InvInventoryDocumentItem> getInvInventoryDocumentItemNewPrice(InvInventoryDocumentItem invInventoryDocumentItem);

    /**
     * 库存凭证明/甲供料结算单细报表
     */
    List<InvInventoryDocumentReportResponse> selectDocumentReport(InvInventoryDocumentReportRequest invInventoryDocumentReportRequest);

    /**
     * 添加多个
     * @param list List InvInventoryDocumentItem
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryDocumentItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryDocumentItem
    * @return int
    */
    int updateAllById(InvInventoryDocumentItem entity);

    /**
     * 更新多个
     * @param list List InvInventoryDocumentItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryDocumentItem> list);


    void deleteInvInventoryDocumentItemByIds(@Param("array") Long[] inventoryDocumentSids);
}
