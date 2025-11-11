package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelOutsourceDeliveryNote;

/**
 * 外发加工收货单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface DelOutsourceDeliveryNoteMapper  extends BaseMapper<DelOutsourceDeliveryNote> {


    DelOutsourceDeliveryNote selectDelOutsourceDeliveryNoteById(Long deliveryNoteSid);

    List<DelOutsourceDeliveryNote> selectDelOutsourceDeliveryNoteList(DelOutsourceDeliveryNote delOutsourceDeliveryNote);

    /**
     * 添加多个
     * @param list List DelOutsourceDeliveryNote
     * @return int
     */
    int inserts(@Param("list") List<DelOutsourceDeliveryNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelOutsourceDeliveryNote
    * @return int
    */
    int updateAllById(DelOutsourceDeliveryNote entity);

    /**
     * 更新多个
     * @param list List DelOutsourceDeliveryNote
     * @return int
     */
    int updatesAllById(@Param("list") List<DelOutsourceDeliveryNote> list);

}
