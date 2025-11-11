package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaPurInspectionRecordAttach;

/**
 * 采购验货问题台账-附件Mapper接口
 *
 * @author platform
 * @date 2024-09-25
 */
public interface QuaPurInspectionRecordAttachMapper  extends BaseMapper<QuaPurInspectionRecordAttach> {

    /**
     * 查询详情
     * @param purInspectionRecordAttachSid 单据sid
     * @return QuaPurInspectionRecordAttach
     */
    QuaPurInspectionRecordAttach selectQuaPurInspectionRecordAttachById(Long purInspectionRecordAttachSid);

    /**
     * 查询列表
     * @param quaPurInspectionRecordAttach QuaPurInspectionRecordAttach
     * @return List
     */
    List<QuaPurInspectionRecordAttach> selectQuaPurInspectionRecordAttachList(QuaPurInspectionRecordAttach quaPurInspectionRecordAttach);

    /**
     * 添加多个
     * @param list List QuaPurInspectionRecordAttach
     * @return int
     */
    int inserts(@Param("list") List<QuaPurInspectionRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity QuaPurInspectionRecordAttach
     * @return int
     */
    int updateAllById(QuaPurInspectionRecordAttach entity);

    /**
     * 更新多个
     * @param list List QuaPurInspectionRecordAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaPurInspectionRecordAttach> list);

}
