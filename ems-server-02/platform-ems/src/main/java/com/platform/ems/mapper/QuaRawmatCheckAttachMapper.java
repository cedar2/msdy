package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaRawmatCheckAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面辅料检测单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface QuaRawmatCheckAttachMapper extends BaseMapper<QuaRawmatCheckAttach> {


    QuaRawmatCheckAttach selectQuaRawmatCheckAttachById(Long attachmentSid);

    List<QuaRawmatCheckAttach> selectQuaRawmatCheckAttachList(QuaRawmatCheckAttach quaRawmatCheckAttach);

    /**
     * 添加多个
     *
     * @param list List QuaRawmatCheckAttach
     * @return int
     */
    int inserts(@Param("list") List<QuaRawmatCheckAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaRawmatCheckAttach
     * @return int
     */
    int updateAllById(QuaRawmatCheckAttach entity);

    /**
     * 更新多个
     *
     * @param list List QuaRawmatCheckAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaRawmatCheckAttach> list);


}
