package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.AssAssetRecord;

/**
 * 资产台账Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface AssAssetRecordMapper extends BaseMapper<AssAssetRecord> {


    AssAssetRecord selectAssAssetRecordById(Long assetSid);

    List<AssAssetRecord> selectAssAssetRecordList(AssAssetRecord assAssetRecord);

    List<AssAssetRecord> selectAssAssetStatisticalRecordList(AssAssetRecord assAssetRecord);

    List<AssAssetRecord> selectAssAssetStatisticalRecordListDetail(AssAssetRecord assAssetRecord);

    /**
     * 添加多个
     *
     * @param list List AssAssetRecord
     * @return int
     */
    int inserts(@Param("list") List<AssAssetRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity AssAssetRecord
     * @return int
     */
    int updateAllById(AssAssetRecord entity);

    /**
     * 更新多个
     *
     * @param list List AssAssetRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<AssAssetRecord> list);


}
