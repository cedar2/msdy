package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinSaleInvoiceDiscountMapper;
import com.platform.ems.domain.FinSaleInvoiceDiscount;
import com.platform.ems.service.IFinSaleInvoiceDiscountService;

/**
 * 销售发票-折扣Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinSaleInvoiceDiscountServiceImpl extends ServiceImpl<FinSaleInvoiceDiscountMapper,FinSaleInvoiceDiscount>  implements IFinSaleInvoiceDiscountService {
    @Autowired
    private FinSaleInvoiceDiscountMapper finSaleInvoiceDiscountMapper;

    /**
     * 查询销售发票-折扣
     * 
     * @param saleInvoiceDiscountSid 销售发票-折扣ID
     * @return 销售发票-折扣
     */
    @Override
    public FinSaleInvoiceDiscount selectFinSaleInvoiceDiscountById(Long saleInvoiceDiscountSid) {
        return finSaleInvoiceDiscountMapper.selectFinSaleInvoiceDiscountById(saleInvoiceDiscountSid);
    }

    /**
     * 查询销售发票-折扣列表
     * 
     * @param finSaleInvoiceDiscount 销售发票-折扣
     * @return 销售发票-折扣
     */
    @Override
    public List<FinSaleInvoiceDiscount> selectFinSaleInvoiceDiscountList(FinSaleInvoiceDiscount finSaleInvoiceDiscount) {
        return finSaleInvoiceDiscountMapper.selectFinSaleInvoiceDiscountList(finSaleInvoiceDiscount);
    }

    /**
     * 新增销售发票-折扣
     * 需要注意编码重复校验
     * @param finSaleInvoiceDiscount 销售发票-折扣
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinSaleInvoiceDiscount(FinSaleInvoiceDiscount finSaleInvoiceDiscount) {
        return finSaleInvoiceDiscountMapper.insert(finSaleInvoiceDiscount);
    }

    /**
     * 修改销售发票-折扣
     * 
     * @param finSaleInvoiceDiscount 销售发票-折扣
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinSaleInvoiceDiscount(FinSaleInvoiceDiscount finSaleInvoiceDiscount) {
        return finSaleInvoiceDiscountMapper.updateById(finSaleInvoiceDiscount);
    }

    /**
     * 批量删除销售发票-折扣
     * 
     * @param saleInvoiceDiscountSids 需要删除的销售发票-折扣ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinSaleInvoiceDiscountByIds(List<Long> saleInvoiceDiscountSids) {
        return finSaleInvoiceDiscountMapper.deleteBatchIds(saleInvoiceDiscountSids);
    }


}
