package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasCustomer;

/**
 * 客户档案Mapper接口
 *
 * @author qhq
 * @date 2021-03-22
 */
public interface BasCustomerMapper  extends BaseMapper<BasCustomer> {


    BasCustomer selectBasCustomerById(Long customerSid);

    List<BasCustomer> selectBasCustomerList(BasCustomer basCustomer);

    /**
     * 添加多个
     * @param list List BasCustomer
     * @return int
     */
    int inserts(@Param("list") List<BasCustomer> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasCustomer
    * @return int
    */
    int updateAllById(BasCustomer entity);

    /**
     * 更新多个
     * @param list List BasCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomer> list);

    /**
     * 批量启用停用
     */
    int editStatus(BasCustomer basCustomer);

    /**
     * 批量确认
     */
    int editHandleStatus(BasCustomer basCustomer);

    List<BasCustomer> getCustomerList(BasCustomer basCustomer);

}

