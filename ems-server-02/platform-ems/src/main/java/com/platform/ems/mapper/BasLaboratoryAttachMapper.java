package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasLaboratoryAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实验室-附件Mapper接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface BasLaboratoryAttachMapper extends BaseMapper<BasLaboratoryAttach> {


    BasLaboratoryAttach selectBasLaboratoryAttachById(Long attachmentSid);

    List<BasLaboratoryAttach> selectBasLaboratoryAttachList(BasLaboratoryAttach basLaboratoryAttach);

    /**
     * 添加多个
     *
     * @param list List BasLaboratoryAttach
     * @return int
     */
    int inserts(@Param("list") List<BasLaboratoryAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasLaboratoryAttach
     * @return int
     */
    int updateAllById(BasLaboratoryAttach entity);

    /**
     * 更新多个
     *
     * @param list List BasLaboratoryAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasLaboratoryAttach> list);


}
