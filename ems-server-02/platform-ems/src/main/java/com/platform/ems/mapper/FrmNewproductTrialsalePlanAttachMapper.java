package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmNewproductTrialsalePlanAttach;

/**
 * 新品试销计划单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmNewproductTrialsalePlanAttachMapper extends BaseMapper<FrmNewproductTrialsalePlanAttach> {

    FrmNewproductTrialsalePlanAttach selectFrmNewproductTrialsalePlanAttachById(Long newproductTrialsalePlanAttachSid);

    List<FrmNewproductTrialsalePlanAttach> selectFrmNewproductTrialsalePlanAttachList(FrmNewproductTrialsalePlanAttach frmNewproductTrialsalePlanAttach);

    /**
     * 添加多个
     *
     * @param list List FrmNewproductTrialsalePlanAttach
     * @return int
     */
    int inserts(@Param("list") List<FrmNewproductTrialsalePlanAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmNewproductTrialsalePlanAttach
     * @return int
     */
    int updateAllById(FrmNewproductTrialsalePlanAttach entity);

    /**
     * 更新多个
     *
     * @param list List FrmNewproductTrialsalePlanAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmNewproductTrialsalePlanAttach> list);

}
