package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasCustomerTagItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户标签(分组)明细Mapper接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface BasCustomerTagItemMapper extends BaseMapper<BasCustomerTagItem> {


    BasCustomerTagItem selectBasCustomerTagItemById(Long customerTagItemSid);

    List<BasCustomerTagItem> selectBasCustomerTagItemList(BasCustomerTagItem basCustomerTagItem);

    /**
     * 添加多个
     *
     * @param list List BasCustomerTagItem
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerTagItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasCustomerTagItem
     * @return int
     */
    int updateAllById(BasCustomerTagItem entity);

    /**
     * 更新多个
     *
     * @param list List BasCustomerTagItem
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomerTagItem> list);


}
