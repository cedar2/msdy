package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureCompleteNote;

/**
 * 生产完工确认单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManManufactureCompleteNoteMapper  extends BaseMapper<ManManufactureCompleteNote> {


    ManManufactureCompleteNote selectManManufactureCompleteNoteById(Long manufactureCompleteNoteSid);

    List<ManManufactureCompleteNote> selectManManufactureCompleteNoteList(ManManufactureCompleteNote manManufactureCompleteNote);

    /**
     * 添加多个
     * @param list List ManManufactureCompleteNote
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureCompleteNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureCompleteNote
    * @return int
    */
    int updateAllById(ManManufactureCompleteNote entity);

    /**
     * 更新多个
     * @param list List ManManufactureCompleteNote
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureCompleteNote> list);

}
