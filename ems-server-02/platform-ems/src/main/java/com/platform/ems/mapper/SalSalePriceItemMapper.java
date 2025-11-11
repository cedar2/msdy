package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SalSalePrice;
import com.platform.ems.domain.SalSalePriceItem;
import com.platform.ems.domain.dto.response.SaleReportResponse;

import java.util.List;

/**
 * 销售价信息-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-05
 */
public interface SalSalePriceItemMapper extends BaseMapper<SalSalePriceItem> {
    /**
     * 查询销售价信息-明细
     *
     * @param salePriceSid 销售价信息-明细ID
     * @return 销售价信息-明细
     */
    public List<SalSalePriceItem> selectSalSalePriceItemById(Long salePriceSid);

    /**

     * 全量更新

     * null字段也会进行更新，慎用

     * @param entity SalSalePriceItem

     * @return int

     */

    int updateAllById(SalSalePriceItem entity);

    int updateRe(SalSalePriceItem entity);

    /**
     * 销售价报表
     *
     *
     * @return 销售价信息
     */
    public List<SaleReportResponse> saleReport(SaleReportResponse saleReportResponse);

}
