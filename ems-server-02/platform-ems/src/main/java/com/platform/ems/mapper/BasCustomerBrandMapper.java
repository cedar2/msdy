package com.platform.ems.mapper;
import java.util.List;

import com.platform.ems.domain.BasCustomerBrandMark;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasCustomerBrand;

/**
 * 客户-客方品牌信息Mapper接口
 *
 * @author qhq
 * @date 2021-03-24
 */
public interface BasCustomerBrandMapper  extends BaseMapper<BasCustomerBrand> {


    BasCustomerBrand selectBasCustomerBrandById(String customerBrandSid);

    List<BasCustomerBrand> selectBasCustomerBrandList(BasCustomerBrand basCustomerBrand);


    List<BasCustomerBrand> selectBasCustomerBrandByCustomerSid(Long customerSid);

    /**
     * 添加多个
     * @param list List BasCustomerBrand
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerBrand> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasCustomerBrand
    * @return int
    */
    int updateAllById(BasCustomerBrand entity);

    /**
     * 更新多个
     * @param list List BasCustomerBrand
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomerBrand> list);

    int deleteByCustomerSid(Long customerSid);

}
