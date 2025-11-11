package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureCompleteNoteAttach;

/**
 * 生产完工确认单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManManufactureCompleteNoteAttachMapper  extends BaseMapper<ManManufactureCompleteNoteAttach> {


    ManManufactureCompleteNoteAttach selectManManufactureCompleteNoteAttachById(Long manufactureCompleteNoteAttachSid);

    List<ManManufactureCompleteNoteAttach> selectManManufactureCompleteNoteAttachList(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach);

    /**
     * 添加多个
     * @param list List ManManufactureCompleteNoteAttach
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureCompleteNoteAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureCompleteNoteAttach
    * @return int
    */
    int updateAllById(ManManufactureCompleteNoteAttach entity);

    /**
     * 更新多个
     * @param list List ManManufactureCompleteNoteAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureCompleteNoteAttach> list);


    void deleteManManufactureCompleteNoteAttachByIds(@Param("list") List<Long> manufactureCompleteNoteSids);
}
