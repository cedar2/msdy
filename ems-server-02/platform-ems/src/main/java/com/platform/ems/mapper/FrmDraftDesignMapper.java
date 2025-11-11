package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FrmDraftDesign;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图稿绘制单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-12
 */
public interface FrmDraftDesignMapper extends BaseMapper<FrmDraftDesign> {

    FrmDraftDesign selectFrmDraftDesignById(Long draftDesignSid);

    List<FrmDraftDesign> selectFrmDraftDesignListOrderByDesc(FrmDraftDesign frmDraftDesign);

    /**
     * 添加多个
     *
     * @param list List FrmDraftDesign
     * @return int
     */
    int inserts(@Param("list") List<FrmDraftDesign> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmDraftDesign
     * @return int
     */
    int updateAllById(FrmDraftDesign entity);

    /**
     * 更新多个
     *
     * @param list List FrmDraftDesign
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmDraftDesign> list);

}
