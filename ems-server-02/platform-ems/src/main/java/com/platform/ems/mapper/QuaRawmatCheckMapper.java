package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaRawmatCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面辅料检测单-主Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface QuaRawmatCheckMapper extends BaseMapper<QuaRawmatCheck> {


    QuaRawmatCheck selectQuaRawmatCheckById(Long rawmatCheckSid);

    List<QuaRawmatCheck> selectQuaRawmatCheckList(QuaRawmatCheck quaRawmatCheck);

    /**
     * 添加多个
     *
     * @param list List QuaRawmatCheck
     * @return int
     */
    int inserts(@Param("list") List<QuaRawmatCheck> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaRawmatCheck
     * @return int
     */
    int updateAllById(QuaRawmatCheck entity);

    /**
     * 更新多个
     *
     * @param list List QuaRawmatCheck
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaRawmatCheck> list);


}
