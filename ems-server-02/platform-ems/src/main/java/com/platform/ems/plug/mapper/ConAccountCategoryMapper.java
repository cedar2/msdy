package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConAccountCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 款项类别Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-22
 */
public interface ConAccountCategoryMapper  extends BaseMapper<ConAccountCategory> {


    ConAccountCategory selectConAccountCategoryById(Long sid);

    List<ConAccountCategory> selectConAccountCategoryList(ConAccountCategory conAccountCategory);

    /**
     * 添加多个
     * @param list List ConAccountCategory
     * @return int
     */
    int inserts(@Param("list") List<ConAccountCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConAccountCategory
    * @return int
    */
    int updateAllById(ConAccountCategory entity);

    /**
     * 更新多个
     * @param list List ConAccountCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConAccountCategory> list);

    /**
     * 款项类别下拉框列表
     */
    List<ConAccountCategory> getConAccountCategoryList();
}
