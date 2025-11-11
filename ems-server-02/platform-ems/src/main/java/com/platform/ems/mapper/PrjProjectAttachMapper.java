package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PrjProjectAttach;

/**
 * 项目档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-08
 */
public interface PrjProjectAttachMapper extends BaseMapper<PrjProjectAttach> {

    PrjProjectAttach selectPrjProjectAttachById(Long projectAttachSid);

    List<PrjProjectAttach> selectPrjProjectAttachList(PrjProjectAttach prjProjectAttach);

    /**
     * 添加多个
     *
     * @param list List PrjProjectAttach
     * @return int
     */
    int inserts(@Param("list") List<PrjProjectAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjProjectAttach
     * @return int
     */
    int updateAllById(PrjProjectAttach entity);

    /**
     * 更新多个
     *
     * @param list List PrjProjectAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjProjectAttach> list);

}
