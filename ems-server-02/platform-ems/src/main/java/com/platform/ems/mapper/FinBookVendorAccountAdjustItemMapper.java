package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookVendorAccountAdjust;
import com.platform.ems.domain.FinBookVendorAccountAdjustItem;

/**
 * 财务流水账-明细-供应商调账Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-02
 */
public interface FinBookVendorAccountAdjustItemMapper  extends BaseMapper<FinBookVendorAccountAdjustItem> {


    FinBookVendorAccountAdjustItem selectFinBookVendorAccountAdjustItemById(Long bookAccountAdjustItemSid);

    List<FinBookVendorAccountAdjustItem> selectFinBookVendorAccountAdjustItemList(FinBookVendorAccountAdjustItem finBookVendorAccountAdjustItem);

    /**
     * 添加多个
     * @param list List FinBookVendorAccountAdjustItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorAccountAdjustItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorAccountAdjustItem
    * @return int
    */
    int updateAllById(FinBookVendorAccountAdjustItem entity);

    /**
     * 更新多个
     * @param list List FinBookVendorAccountAdjustItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorAccountAdjustItem> list);

    List<FinBookVendorAccountAdjustItem> getItemList(FinBookVendorAccountAdjust entity);
}
