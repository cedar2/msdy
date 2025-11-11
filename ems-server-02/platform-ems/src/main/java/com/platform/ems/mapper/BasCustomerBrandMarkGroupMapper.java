package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCustomerBrandMarkGroup;

/**
 * 客户-品牌-品标组合信息Mapper接口
 *
 * @author c
 * @date 2021-11-17
 */
public interface BasCustomerBrandMarkGroupMapper extends BaseMapper<BasCustomerBrandMarkGroup> {


    BasCustomerBrandMarkGroup selectBasCustomerBrandMarkGroupById(Long cbbmGroupSid);

    List<BasCustomerBrandMarkGroup> selectBasCustomerBrandMarkGroupList(BasCustomerBrandMarkGroup basCustomerBrandMarkGroup);

    /**
     * 添加多个
     * @param list List BasCustomerBrandMarkGroup
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerBrandMarkGroup> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasCustomerBrandMarkGroup
     * @return int
     */
    int updateAllById(BasCustomerBrandMarkGroup entity);

    /**
     * 更新多个
     * @param list List BasCustomerBrandMarkGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomerBrandMarkGroup> list);


}