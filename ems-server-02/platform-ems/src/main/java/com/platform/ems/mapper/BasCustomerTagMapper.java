package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasCustomerTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户标签(分组)Mapper接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface BasCustomerTagMapper extends BaseMapper<BasCustomerTag> {


    BasCustomerTag selectBasCustomerTagById(Long customerTagSid);

    List<BasCustomerTag> selectBasCustomerTagList(BasCustomerTag basCustomerTag);

    /**
     * 添加多个
     *
     * @param list List BasCustomerTag
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerTag> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasCustomerTag
     * @return int
     */
    int updateAllById(BasCustomerTag entity);

    /**
     * 更新多个
     *
     * @param list List BasCustomerTag
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomerTag> list);


}
