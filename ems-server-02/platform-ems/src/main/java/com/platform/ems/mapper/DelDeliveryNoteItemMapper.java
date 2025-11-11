package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.DelDeliveryNoteItemOutResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelDeliveryNoteItem;

/**
 * 交货单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-21
 */
public interface DelDeliveryNoteItemMapper  extends BaseMapper<DelDeliveryNoteItem> {


    DelDeliveryNoteItem selectDelDeliveryNoteItemById(String clientId);

    List<DelDeliveryNoteItemOutResponse> getOutDelDeliveryNoteItemById(Long deliveryNoteSid);

    List<DelDeliveryNoteItem> selectDelDeliveryNoteItemList(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 添加多个
     * @param list List DelDeliveryNoteItem
     * @return int
     */
    int inserts(@Param("list") List<DelDeliveryNoteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelDeliveryNoteItem
    * @return int
    */
    int updateAllById(DelDeliveryNoteItem entity);



    void deleteDelDeliveryNoteItemByIds(@Param("array")Long[] deliveryNoteSids);

    List<DelDeliveryNoteItem> selectShipmentsNoteItemList(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 采购交货单明细报表
     */
    List<DelDeliveryNoteItem> getDeliveryItemList(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 销售发货单明细报表
     */
    List<DelDeliveryNoteItem> getShipmentsItemList(DelDeliveryNoteItem delDeliveryNoteItem);
}
