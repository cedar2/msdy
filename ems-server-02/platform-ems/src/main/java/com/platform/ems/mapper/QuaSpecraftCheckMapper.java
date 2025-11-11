package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaSpecraftCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 特殊工艺检测单-主Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface QuaSpecraftCheckMapper extends BaseMapper<QuaSpecraftCheck> {


    QuaSpecraftCheck selectQuaSpecraftCheckById(Long specraftCheckSid);

    List<QuaSpecraftCheck> selectQuaSpecraftCheckList(QuaSpecraftCheck quaSpecraftCheck);

    /**
     * 添加多个
     *
     * @param list List QuaSpecraftCheck
     * @return int
     */
    int inserts(@Param("list") List<QuaSpecraftCheck> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaSpecraftCheck
     * @return int
     */
    int updateAllById(QuaSpecraftCheck entity);

    /**
     * 更新多个
     *
     * @param list List QuaSpecraftCheck
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaSpecraftCheck> list);


}
