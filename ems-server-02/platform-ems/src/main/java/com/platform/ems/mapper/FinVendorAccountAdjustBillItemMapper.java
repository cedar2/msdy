package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinVendorAccountAdjustBillItem;

/**
 * 供应商调账单-明细Mapper接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface FinVendorAccountAdjustBillItemMapper  extends BaseMapper<FinVendorAccountAdjustBillItem> {


    FinVendorAccountAdjustBillItem selectFinVendorAccountAdjustBillItemById(Long adjustBillItemSid);

    List<FinVendorAccountAdjustBillItem> selectFinVendorAccountAdjustBillItemList(FinVendorAccountAdjustBillItem finVendorAccountAdjustBillItem);

    /**
     * 添加多个
     * @param list List FinVendorAccountAdjustBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinVendorAccountAdjustBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorAccountAdjustBillItem
    * @return int
    */
    int updateAllById(FinVendorAccountAdjustBillItem entity);

    /**
     * 更新多个
     * @param list List FinVendorAccountAdjustBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorAccountAdjustBillItem> list);


}
