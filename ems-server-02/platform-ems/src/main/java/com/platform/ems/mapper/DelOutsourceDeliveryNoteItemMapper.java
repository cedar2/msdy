package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelOutsourceDeliveryNoteItem;

/**
 * 外发加工收货单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface DelOutsourceDeliveryNoteItemMapper  extends BaseMapper<DelOutsourceDeliveryNoteItem> {


    DelOutsourceDeliveryNoteItem selectDelOutsourceDeliveryNoteItemById(Long deliveryNoteItemSid);

    List<DelOutsourceDeliveryNoteItem> selectDelOutsourceDeliveryNoteItemList(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);

    /**
     * 添加多个
     * @param list List DelOutsourceDeliveryNoteItem
     * @return int
     */
    int inserts(@Param("list") List<DelOutsourceDeliveryNoteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelOutsourceDeliveryNoteItem
    * @return int
    */
    int updateAllById(DelOutsourceDeliveryNoteItem entity);

    /**
     * 更新多个
     * @param list List DelOutsourceDeliveryNoteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<DelOutsourceDeliveryNoteItem> list);


    void deleteOutsourceDeliveryNoteItemByIds(@Param("list") List<Long> outsourceDeliveryNoteSids);

    /**
     * 外发加工收货单明细报表
     */
    List<DelOutsourceDeliveryNoteItem> getItemList(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);
}
