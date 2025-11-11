package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaPurInspectionRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购验货问题台账Mapper接口
 *
 * @author admin
 * @date 2024-09-20
 */
public interface QuaPurInspectionRecordMapper extends BaseMapper<QuaPurInspectionRecord> {


    /**
     * 查询详情
     * @param purInspectionRecordSid 单据sid
     * @return QuaPurInspectionRecord
     */
    QuaPurInspectionRecord selectQuaPurInspectionRecordById(Long purInspectionRecordSid);

    /**
     * 查询列表
     * @param QuaPurInspectionRecord QuaPurInspectionRecord
     * @return List
     */
    List<QuaPurInspectionRecord> selectQuaPurInspectionRecordList(QuaPurInspectionRecord QuaPurInspectionRecord);


    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaPurInspectionRecord> list);


    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity QuaPurInspectionRecord
     * @return int
     */
    int updateAllById(QuaPurInspectionRecord entity);

    /**
     * 更新多个
     * @param list List QuaPurInspectionRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaPurInspectionRecord> list);
}
