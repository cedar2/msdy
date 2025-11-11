package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasImageAttach;

/**
 * 图案档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-14
 */
public interface BasImageAttachMapper extends BaseMapper<BasImageAttach> {

    BasImageAttach selectBasImageAttachById(Long imageAttachSid);

    List<BasImageAttach> selectBasImageAttachList(BasImageAttach basImageAttach);

    /**
     * 添加多个
     *
     * @param list List BasImageAttach
     * @return int
     */
    int inserts(@Param("list") List<BasImageAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasImageAttach
     * @return int
     */
    int updateAllById(BasImageAttach entity);

    /**
     * 更新多个
     *
     * @param list List BasImageAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasImageAttach> list);

}
