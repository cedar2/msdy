package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBookSourceCategory;

/**
 * 流水来源类别_财务Mapper接口
 *
 * @author chenkw
 * @date 2021-08-03
 */
public interface ConBookSourceCategoryMapper extends BaseMapper<ConBookSourceCategory> {


    ConBookSourceCategory selectConBookSourceCategoryById(Long sid);

    List<ConBookSourceCategory> selectConBookSourceCategoryList(ConBookSourceCategory conBookSourceCategory);

    /**
     * 添加多个
     *
     * @param list List ConBookSourceCategory
     * @return int
     */
    int inserts(@Param("list") List<ConBookSourceCategory> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBookSourceCategory
     * @return int
     */
    int updateAllById(ConBookSourceCategory entity);

    /**
     * 更新多个
     *
     * @param list List ConBookSourceCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBookSourceCategory> list);

    /**
     * 下拉框列表
     */
    List<ConBookSourceCategory> getConBookSourceCategoryList();
}
