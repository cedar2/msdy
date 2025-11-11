package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorAccountBalanceBillItem;

/**
 * 供应商账互抵单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface FinVendorAccountBalanceBillItemMapper  extends BaseMapper<FinVendorAccountBalanceBillItem> {


    FinVendorAccountBalanceBillItem selectFinVendorAccountBalanceBillItemById(Long vendorAccountBalanceBillItemSid);

    List<FinVendorAccountBalanceBillItem> selectFinVendorAccountBalanceBillItemList(FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem);

    /**
     * 添加多个
     * @param list List FinVendorAccountBalanceBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinVendorAccountBalanceBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorAccountBalanceBillItem
    * @return int
    */
    int updateAllById(FinVendorAccountBalanceBillItem entity);

    /**
     * 更新多个
     * @param list List FinVendorAccountBalanceBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorAccountBalanceBillItem> list);


}
