package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepSalesTopProduct;

/**
 * 销售TOP10款Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepSalesTopProductService extends IService<RepSalesTopProduct> {
    /**
     * 查询销售TOP10款
     *
     * @param dataRecordSid 销售TOP10款ID
     * @return 销售TOP10款
     */
    public RepSalesTopProduct selectRepSalesTopProductById(Long dataRecordSid);

    /**
     * 查询销售TOP10款列表
     *
     * @param repSalesTopProduct 销售TOP10款
     * @return 销售TOP10款集合
     */
    public List<RepSalesTopProduct> selectRepSalesTopProductList(RepSalesTopProduct repSalesTopProduct);

    /**
     * 新增销售TOP10款
     *
     * @param repSalesTopProduct 销售TOP10款
     * @return 结果
     */
    public int insertRepSalesTopProduct(RepSalesTopProduct repSalesTopProduct);

    /**
     * 批量删除销售TOP10款
     *
     * @param dataRecordSids 需要删除的销售TOP10款ID
     * @return 结果
     */
    public int deleteRepSalesTopProductByIds(List<Long> dataRecordSids);

}
