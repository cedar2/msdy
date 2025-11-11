package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.PrjProjectTaskAttach;
import org.apache.ibatis.annotations.Param;

/**
 * 项目档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2023-04-14
 */
public interface PrjProjectTaskAttachMapper extends BaseMapper<PrjProjectTaskAttach> {

    PrjProjectTaskAttach selectPrjProjectTaskAttachById(Long projectTaskAttachSid);

    List<PrjProjectTaskAttach> selectPrjProjectTaskAttachList(PrjProjectTaskAttach prjProjectTaskAttach);

    /**
     * 添加多个
     *
     * @param list List PrjProjectAttach
     * @return int
     */
    int inserts(@Param("list") List<PrjProjectTaskAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjProjectAttach
     * @return int
     */
    int updateAllById(PrjProjectTaskAttach entity);

    /**
     * 更新多个
     *
     * @param list List PrjProjectTaskAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjProjectTaskAttach> list);

}
