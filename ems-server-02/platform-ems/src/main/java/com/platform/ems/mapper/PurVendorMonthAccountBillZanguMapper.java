package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookPaymentEstimation;
import com.platform.ems.domain.PurVendorMonthAccountBillZangu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商对账单-暂估明细Mapper接口
 *
 * @author xfzz
 */
public interface PurVendorMonthAccountBillZanguMapper extends BaseMapper<PurVendorMonthAccountBillZangu> {


    PurVendorMonthAccountBillZangu selectPurVendorMonthAccountBillZanguById(Long vendorMonthAccountBillSid);

    List<PurVendorMonthAccountBillZangu> selectPurVendorMonthAccountBillZanguList(PurVendorMonthAccountBillZangu purVendorMonthAccountBillZangu);

    /**
     * 添加多个
     *
     * @param list List PurVendorMonthAccountBillZangu
     * @return int
     */
    int inserts(@Param("list") List<PurVendorMonthAccountBillZangu> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurVendorMonthAccountBillZangu
     * @return int
     */
    int updateAllById(PurVendorMonthAccountBillZangu entity);

    /**
     * 更新多个
     *
     * @param list List PurVendorMonthAccountBillZangu
     * @return int
     */
    int updatesAllById(@Param("list") List<PurVendorMonthAccountBillZangu> list);

    List<PurVendorMonthAccountBillZangu> getReportForm(PurVendorMonthAccountBillZangu entity);

}
