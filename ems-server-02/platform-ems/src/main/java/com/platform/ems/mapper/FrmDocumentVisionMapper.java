package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmDocumentVision;

/**
 * 文案脚本单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface FrmDocumentVisionMapper extends BaseMapper<FrmDocumentVision> {

    FrmDocumentVision selectFrmDocumentVisionById(Long documentVisionSid);

    List<FrmDocumentVision> selectFrmDocumentVisionList(FrmDocumentVision frmDocumentVision);

    /**
     * 添加多个
     *
     * @param list List FrmDocumentVision
     * @return int
     */
    int inserts(@Param("list") List<FrmDocumentVision> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmDocumentVision
     * @return int
     */
    int updateAllById(FrmDocumentVision entity);

    /**
     * 更新多个
     *
     * @param list List FrmDocumentVision
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmDocumentVision> list);

}
