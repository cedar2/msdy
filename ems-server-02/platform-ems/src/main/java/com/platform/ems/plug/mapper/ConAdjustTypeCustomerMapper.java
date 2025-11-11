package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConAccountCategory;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConAdjustTypeCustomer;

/**
 * 调账类型_客户Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConAdjustTypeCustomerMapper  extends BaseMapper<ConAdjustTypeCustomer> {


    ConAdjustTypeCustomer selectConAdjustTypeCustomerById(Long sid);

    List<ConAdjustTypeCustomer> selectConAdjustTypeCustomerList(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 添加多个
     * @param list List ConAdjustTypeCustomer
     * @return int
     */
    int inserts(@Param("list") List<ConAdjustTypeCustomer> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConAdjustTypeCustomer
    * @return int
    */
    int updateAllById(ConAdjustTypeCustomer entity);

    /**
     * 更新多个
     * @param list List ConAdjustTypeCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<ConAdjustTypeCustomer> list);

    /**
     * 款项类别下拉框列表
     */
    List<ConAdjustTypeCustomer> getConAdjustTypeCustomerList();
}
