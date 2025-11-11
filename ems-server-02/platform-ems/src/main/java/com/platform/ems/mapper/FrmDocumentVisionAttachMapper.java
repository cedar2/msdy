package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmDocumentVisionAttach;

/**
 * 文案视觉单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface FrmDocumentVisionAttachMapper extends BaseMapper<FrmDocumentVisionAttach> {

    FrmDocumentVisionAttach selectFrmDocumentVisionAttachById(Long documentVisionAttachSid);

    List<FrmDocumentVisionAttach> selectFrmDocumentVisionAttachList(FrmDocumentVisionAttach frmDocumentVisionAttach);

    /**
     * 添加多个
     *
     * @param list List FrmDocumentVisionAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmDocumentVisionAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmDocumentVisionAttach
     * @return int
     */
    int updateAllById(FrmDocumentVisionAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmDocumentVisionAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmDocumentVisionAttach> list);

}
