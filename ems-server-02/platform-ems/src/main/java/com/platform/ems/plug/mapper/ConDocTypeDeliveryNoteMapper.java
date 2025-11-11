package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeDeliveryNote;

/**
 * 单据类型_采购交货单/销售发货单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeDeliveryNoteMapper  extends BaseMapper<ConDocTypeDeliveryNote> {


    ConDocTypeDeliveryNote selectConDocTypeDeliveryNoteById(Long sid);

    List<ConDocTypeDeliveryNote> selectConDocTypeDeliveryNoteList(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 添加多个
     * @param list List ConDocTypeDeliveryNote
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeDeliveryNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeDeliveryNote
    * @return int
    */
    int updateAllById(ConDocTypeDeliveryNote entity);

    /**
     * 更新多个
     * @param list List ConDocTypeDeliveryNote
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeDeliveryNote> list);

    /**
     * 单据类型_采购交货单/销售发货单下拉列表
     */
    List<ConDocTypeDeliveryNote> getList(ConDocTypeDeliveryNote conDocTypeDeliveryNote);
}
