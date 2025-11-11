package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;

/**
 * 业务类型对应款项类别Mapper接口
 *
 * @author chenkw
 * @date 2022-06-22
 */
public interface ConBuTypeAccountCategoryMapper extends BaseMapper<ConBuTypeAccountCategory> {


    ConBuTypeAccountCategory selectConBuTypeAccountCategoryById(Long sid);

    List<ConBuTypeAccountCategory> selectConBuTypeAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeAccountCategory
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeAccountCategory> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeAccountCategory
     * @return int
     */
    int updateAllById(ConBuTypeAccountCategory entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeAccountCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeAccountCategory> list);

    /**
     * 获取下拉框字段
     *
     * @param conBuTypeAccountCategory ConBuTypeAccountCategory
     * @return int
     */
    List<ConBuTypeAccountCategory> getConBuTypeAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 获取款项类别下拉框字段
     *
     * @param conBuTypeAccountCategory ConBuTypeAccountCategory
     * @return int
     */
    List<ConBuTypeAccountCategory> getAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory);

    /**
     * 获取流水类型下拉框字段
     *
     * @param conBuTypeAccountCategory ConBuTypeAccountCategory
     * @return int
     */
    List<ConBuTypeAccountCategory> getBookTypeList(ConBuTypeAccountCategory conBuTypeAccountCategory);

}
