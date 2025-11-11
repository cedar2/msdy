package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookCustomerAccountBalanceItem;

/**
 * 财务流水账-明细-客户账互抵Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-11
 */
public interface FinBookCustomerAccountBalanceItemMapper  extends BaseMapper<FinBookCustomerAccountBalanceItem> {


    FinBookCustomerAccountBalanceItem selectFinBookCustomerAccountBalanceItemById(Long bookAccountBalanceItemSid);

    List<FinBookCustomerAccountBalanceItem> selectFinBookCustomerAccountBalanceItemList(FinBookCustomerAccountBalanceItem finBookCustomerAccountBalanceItem);

    /**
     * 添加多个
     * @param list List FinBookCustomerAccountBalanceItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerAccountBalanceItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerAccountBalanceItem
    * @return int
    */
    int updateAllById(FinBookCustomerAccountBalanceItem entity);

    /**
     * 更新多个
     * @param list List FinBookCustomerAccountBalanceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerAccountBalanceItem> list);


}
