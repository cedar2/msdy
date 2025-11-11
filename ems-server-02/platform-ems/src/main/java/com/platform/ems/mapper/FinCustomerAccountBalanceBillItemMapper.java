package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerAccountBalanceBillItem;

/**
 * 客户账互抵单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface FinCustomerAccountBalanceBillItemMapper  extends BaseMapper<FinCustomerAccountBalanceBillItem> {


    FinCustomerAccountBalanceBillItem selectFinCustomerAccountBalanceBillItemById(Long customerAccountBalanceBillItemSid);

    List<FinCustomerAccountBalanceBillItem> selectFinCustomerAccountBalanceBillItemList(FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem);

    /**
     * 添加多个
     * @param list List FinCustomerAccountBalanceBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerAccountBalanceBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerAccountBalanceBillItem
    * @return int
    */
    int updateAllById(FinCustomerAccountBalanceBillItem entity);

    /**
     * 更新多个
     * @param list List FinCustomerAccountBalanceBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerAccountBalanceBillItem> list);


}
