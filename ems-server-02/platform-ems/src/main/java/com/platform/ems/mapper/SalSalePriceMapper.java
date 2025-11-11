package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.PurPurchasePriceItem;
import com.platform.ems.domain.SalSalePrice;
import com.platform.ems.domain.SalSalePriceItem;
import com.platform.ems.domain.dto.request.SalePriceActionRequest;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 销售价信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-05
 */
public interface SalSalePriceMapper extends BaseMapper<SalSalePrice> {
    /**
     * 查询销售价信息
     *
     * @param salePriceSid 销售价信息ID
     * @return 销售价信息
     */
    public SalSalePrice selectSalSalePriceById(Long salePriceSid);

    /**
     * 查询销售价信息列表
     *
     * @param salSalePrice 销售价信息
     * @return 销售价信息集合
     */
    public List<SalSalePrice> selectSalSalePriceList(SalSalePrice salSalePrice);

    public List<SalSalePrice> getCostList(SalSalePrice salSalePrice);


    /**
     * 新增销售价信息
     *
     * @param salSalePrice 销售价信息
     * @return 结果
     */
    public int insertSalSalePrice(SalSalePrice salSalePrice);

    /**
     * 修改销售价信息
     *
     * @param salSalePrice 销售价信息
     * @return 结果
     */
    public int updateSalSalePrice(SalSalePrice salSalePrice);

    /**
     * 删除销售价信息
     *
     * @param salePriceSid 销售价信息ID
     * @return 结果
     */
    public int deleteSalSalePriceById(@Param("salePriceSid") String salePriceSid);


    /**
     * 根据信息编码获取销售价信息
     *
     * @param salePriceCode
     * @return 结果
     */
    public SalSalePrice selecteSalSalePriceByCode(String salePriceCode);


    /**
     * 根据查询条件获取销售价信息
     *
     * @param salSalePrice
     * @return 结果
     */
    public List<SalSalePrice> getList(SalSalePrice salSalePrice);
    /**
     * 修改销售价信息状态
     *
     * @param salePriceActionRequest
     * @return 结果
     */
    public int updateHandleStatus(SalePriceActionRequest salePriceActionRequest);
    /**
     * 全量更新
     *
     * @param
     * @return 结果
     */
    public int updateAllById(SalSalePrice salSalePrice);

    /**
     * 按色获取采购价
     */
    public SalSalePriceItem getSalSalePriceTaxK1(SalSalePrice salSalePrice);
    /**
     * 按款获取采购价
     */
    public SalSalePriceItem getSalSalePriceTaxK(SalSalePrice salSalePrice);

}
