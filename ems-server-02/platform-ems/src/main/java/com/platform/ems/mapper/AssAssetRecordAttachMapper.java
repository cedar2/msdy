package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.AssAssetRecordAttach;

/**
 * 资产台账-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface AssAssetRecordAttachMapper extends BaseMapper<AssAssetRecordAttach> {


    AssAssetRecordAttach selectAssAssetRecordAttachById(Long assetAttachmentSid);

    List<AssAssetRecordAttach> selectAssAssetRecordAttachList(AssAssetRecordAttach assAssetRecordAttach);

    /**
     * 添加多个
     *
     * @param list List AssAssetRecordAttach
     * @return int
     */
    int inserts(@Param("list") List<AssAssetRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity AssAssetRecordAttach
     * @return int
     */
    int updateAllById(AssAssetRecordAttach entity);

    /**
     * 更新多个
     *
     * @param list List AssAssetRecordAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<AssAssetRecordAttach> list);


}
