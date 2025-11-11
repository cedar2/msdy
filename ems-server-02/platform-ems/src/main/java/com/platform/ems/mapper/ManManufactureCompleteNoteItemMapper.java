package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureCompleteNoteItem;

/**
 * 生产完工确认单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManManufactureCompleteNoteItemMapper  extends BaseMapper<ManManufactureCompleteNoteItem> {


    ManManufactureCompleteNoteItem selectManManufactureCompleteNoteItemById(Long manufactureCompleteNoteItemSid);

    List<ManManufactureCompleteNoteItem> selectManManufactureCompleteNoteItemList(ManManufactureCompleteNoteItem manManufactureCompleteNoteItem);

    /**
     * 添加多个
     * @param list List ManManufactureCompleteNoteItem
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureCompleteNoteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureCompleteNoteItem
    * @return int
    */
    int updateAllById(ManManufactureCompleteNoteItem entity);

    /**
     * 更新多个
     * @param list List ManManufactureCompleteNoteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureCompleteNoteItem> list);

}
