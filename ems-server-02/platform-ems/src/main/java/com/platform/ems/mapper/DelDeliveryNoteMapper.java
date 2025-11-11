package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.DelDeliveryNoteItem;
import com.platform.ems.domain.dto.response.DelDeliveryNoteOutResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelDeliveryNote;

/**
 * 交货单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface DelDeliveryNoteMapper  extends BaseMapper<DelDeliveryNote> {


    DelDeliveryNote selectDelDeliveryNoteById(Long deliveryNoteSid);
    List<DelDeliveryNoteOutResponse> getOutDelDeliveryNoteById(Long purchaseOrderSid);

    //获取销售订单明细行关联的所有的信息
    List<DelDeliveryNoteItem> getConnectSaleSid(@Param("salesOrderItemSid") Long salesOrderItemSid, @Param("handleStatus") String handleStatus);
    //获取采购订单明细行关联的所有的信息
    List<DelDeliveryNoteItem> getConnectPurSid(@Param("purchaseOrderItemSid")  Long purchaseOrderItemSid, @Param("handleStatus") String handleStatus);
    List<DelDeliveryNote> selectDelDeliveryNoteList(DelDeliveryNote delDeliveryNote);

    /**
     * 添加多个
     * @param list List DelDeliveryNote
     * @return int
     */
    int inserts(@Param("list") List<DelDeliveryNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelDeliveryNote
    * @return int
    */
    int updateAllById(DelDeliveryNote entity);
    int updateAllZf(DelDeliveryNote entity);
    /**
     * 更新多个
     * @param list List DelDeliveryNote
     * @return int
     */
    int updatesAllById(@Param("list") List<DelDeliveryNote> list);


    int countByDomain(DelDeliveryNote params);

    int deleteDelDeliveryNoteByIds(@Param("array") Long[] deliveryNoteSids);

    int confirm(DelDeliveryNote delDeliveryNote);

    List<DelDeliveryNote> selectShipmentsNoteList(DelDeliveryNote delDeliveryNote);
}
