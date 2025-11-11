package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PurVendorMonthAccountBillAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商对账单-附件Mapper接口
 *
 */
public interface PurVendorMonthAccountBillAttachMapper extends BaseMapper<PurVendorMonthAccountBillAttach> {


    PurVendorMonthAccountBillAttach selectPurVendorMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    List<PurVendorMonthAccountBillAttach> selectPurVendorMonthAccountBillAttachList(PurVendorMonthAccountBillAttach purVendorMonthAccountBillAttach);

    /**
     * 添加多个
     *
     * @param list List PurVendorMonthAccountBillAttach
     * @return int
     */
    int inserts(@Param("list") List<PurVendorMonthAccountBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurVendorMonthAccountBillAttach
     * @return int
     */
    int updateAllById(PurVendorMonthAccountBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List PurVendorMonthAccountBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PurVendorMonthAccountBillAttach> list);


}
