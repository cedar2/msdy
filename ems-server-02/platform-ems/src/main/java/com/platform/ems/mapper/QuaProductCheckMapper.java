package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaProductCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成衣检测单-主Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface QuaProductCheckMapper extends BaseMapper<QuaProductCheck> {


    QuaProductCheck selectQuaProductCheckById(Long productCheckSid);

    List<QuaProductCheck> selectQuaProductCheckList(QuaProductCheck quaProductCheck);

    /**
     * 添加多个
     *
     * @param list List QuaProductCheck
     * @return int
     */
    int inserts(@Param("list") List<QuaProductCheck> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaProductCheck
     * @return int
     */
    int updateAllById(QuaProductCheck entity);

    /**
     * 更新多个
     *
     * @param list List QuaProductCheck
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaProductCheck> list);


}
