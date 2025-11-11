package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDiscountType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConOutsourceProcessItemCategory;

/**
 * 行类别_外发加工发料单/收货单Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-19
 */
public interface ConOutsourceProcessItemCategoryMapper  extends BaseMapper<ConOutsourceProcessItemCategory> {


    ConOutsourceProcessItemCategory selectConOutsourceProcessItemCategoryById(Long sid);

    List<ConOutsourceProcessItemCategory> selectConOutsourceProcessItemCategoryList(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);

    /**
     * 添加多个
     * @param list List ConOutsourceProcessItemCategory
     * @return int
     */
    int inserts(@Param("list") List<ConOutsourceProcessItemCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConOutsourceProcessItemCategory
    * @return int
    */
    int updateAllById(ConOutsourceProcessItemCategory entity);

    /**
     * 更新多个
     * @param list List ConOutsourceProcessItemCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConOutsourceProcessItemCategory> list);

    /** 获取下拉列表 */
    List<ConOutsourceProcessItemCategory> getConOutsourceProcessItemCategoryList();
}
