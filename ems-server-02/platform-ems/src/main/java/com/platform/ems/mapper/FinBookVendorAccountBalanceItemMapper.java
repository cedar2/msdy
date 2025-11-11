package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookVendorAccountBalanceItem;

/**
 * 财务流水账-明细-供应商账互抵Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-18
 */
public interface FinBookVendorAccountBalanceItemMapper  extends BaseMapper<FinBookVendorAccountBalanceItem> {


    FinBookVendorAccountBalanceItem selectFinBookVendorAccountBalanceItemById(Long bookAccountBalanceItemSid);

    List<FinBookVendorAccountBalanceItem> selectFinBookVendorAccountBalanceItemList(FinBookVendorAccountBalanceItem finBookVendorAccountBalanceItem);

    /**
     * 添加多个
     * @param list List FinBookVendorAccountBalanceItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorAccountBalanceItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorAccountBalanceItem
    * @return int
    */
    int updateAllById(FinBookVendorAccountBalanceItem entity);

    /**
     * 更新多个
     * @param list List FinBookVendorAccountBalanceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorAccountBalanceItem> list);


}
