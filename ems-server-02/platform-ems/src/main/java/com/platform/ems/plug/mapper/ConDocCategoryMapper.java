package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocCategory;

import java.util.List;

/**
 * 单据类别Mapper接口
 *
 * @author chenkw
 * @date 2021-08-02
 */
public interface ConDocCategoryMapper extends BaseMapper<ConDocCategory> {


    ConDocCategory selectConDocCategoryById(Long sid);

    List<ConDocCategory> selectConDocCategoryList(ConDocCategory conDocCategory);

    /**
     * 添加多个
     *
     * @param list List ConDocCategory
     * @return int
     */
    int inserts(@Param("list") List<ConDocCategory> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocCategory
     * @return int
     */
    int updateAllById(ConDocCategory entity);

    /**
     * 更新多个
     *
     * @param list List ConDocCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocCategory> list);

    /**
     * 获取下拉列表
     */
    List<ConDocCategory> getConDocCategoryList();

}
