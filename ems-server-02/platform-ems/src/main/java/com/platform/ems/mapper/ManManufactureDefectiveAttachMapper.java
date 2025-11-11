package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureDefectiveAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产次品台账-附件Mapper接口
 *
 * @author c
 * @date 2022-03-02
 */
public interface ManManufactureDefectiveAttachMapper extends BaseMapper<ManManufactureDefectiveAttach> {


    ManManufactureDefectiveAttach selectManManufactureDefectiveAttachById(Long attachmentSid);

    List<ManManufactureDefectiveAttach> selectManManufactureDefectiveAttachList(ManManufactureDefectiveAttach manManufactureDefectiveAttach);

    /**
     * 添加多个
     *
     * @param list List ManManufactureDefectiveAttach
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureDefectiveAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManManufactureDefectiveAttach
     * @return int
     */
    int updateAllById(ManManufactureDefectiveAttach entity);

    /**
     * 更新多个
     *
     * @param list List ManManufactureDefectiveAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureDefectiveAttach> list);


}
