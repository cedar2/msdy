package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepSalesTopProductMapper;
import com.platform.ems.domain.RepSalesTopProduct;
import com.platform.ems.service.IRepSalesTopProductService;

/**
 * 销售TOP10款Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepSalesTopProductServiceImpl extends ServiceImpl<RepSalesTopProductMapper, RepSalesTopProduct> implements IRepSalesTopProductService {
    @Autowired
    private RepSalesTopProductMapper repSalesTopProductMapper;

    /**
     * 查询销售TOP10款
     *
     * @param dataRecordSid 销售TOP10款ID
     * @return 销售TOP10款
     */
    @Override
    public RepSalesTopProduct selectRepSalesTopProductById(Long dataRecordSid) {
        RepSalesTopProduct repSalesTopProduct = repSalesTopProductMapper.selectRepSalesTopProductById(dataRecordSid);
        return repSalesTopProduct;
    }

    /**
     * 查询销售TOP10款列表
     *
     * @param repSalesTopProduct 销售TOP10款
     * @return 销售TOP10款
     */
    @Override
    public List<RepSalesTopProduct> selectRepSalesTopProductList(RepSalesTopProduct repSalesTopProduct) {
        return repSalesTopProductMapper.selectRepSalesTopProductList(repSalesTopProduct);
    }

    /**
     * 新增销售TOP10款
     * 需要注意编码重复校验
     *
     * @param repSalesTopProduct 销售TOP10款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepSalesTopProduct(RepSalesTopProduct repSalesTopProduct) {
        int row = repSalesTopProductMapper.insert(repSalesTopProduct);
        return row;
    }

    /**
     * 批量删除销售TOP10款
     *
     * @param dataRecordSids 需要删除的销售TOP10款ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepSalesTopProductByIds(List<Long> dataRecordSids) {
        return repSalesTopProductMapper.deleteBatchIds(dataRecordSids);
    }

}
