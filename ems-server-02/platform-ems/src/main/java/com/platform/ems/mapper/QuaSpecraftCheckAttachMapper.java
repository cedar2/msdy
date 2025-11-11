package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaSpecraftCheckAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 特殊工艺检测单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface QuaSpecraftCheckAttachMapper extends BaseMapper<QuaSpecraftCheckAttach> {


    QuaSpecraftCheckAttach selectQuaSpecraftCheckAttachById(Long attachmentSid);

    List<QuaSpecraftCheckAttach> selectQuaSpecraftCheckAttachList(QuaSpecraftCheckAttach quaSpecraftCheckAttach);

    /**
     * 添加多个
     *
     * @param list List QuaSpecraftCheckAttach
     * @return int
     */
    int inserts(@Param("list") List<QuaSpecraftCheckAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaSpecraftCheckAttach
     * @return int
     */
    int updateAllById(QuaSpecraftCheckAttach entity);

    /**
     * 更新多个
     *
     * @param list List QuaSpecraftCheckAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaSpecraftCheckAttach> list);


}
