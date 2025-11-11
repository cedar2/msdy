package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.FinSaleInvoiceItemMapper;
import com.platform.ems.domain.FinSaleInvoiceItem;
import com.platform.ems.service.IFinSaleInvoiceItemService;

/**
 * 销售发票明细报表Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinSaleInvoiceItemServiceImpl extends ServiceImpl<FinSaleInvoiceItemMapper, FinSaleInvoiceItem> implements IFinSaleInvoiceItemService {

    @Autowired
    private FinSaleInvoiceItemMapper finSaleInvoiceItemMapper;


    private static final String TITLE = "销售发票明细报表";
    /**
     * 查询销售发票明细报表
     *
     * @param saleInvoiceItemSid 销售发票明细报表ID
     * @return 销售发票明细报表
     */
    @Override
    public FinSaleInvoiceItem selectFinSaleInvoiceItemById(Long saleInvoiceItemSid) {
        FinSaleInvoiceItem finSaleInvoiceItem = finSaleInvoiceItemMapper.selectFinSaleInvoiceItemById(saleInvoiceItemSid);
        return  finSaleInvoiceItem;
    }

    /**
     * 查询销售发票明细报表列表
     *
     * @param finSaleInvoiceItem 销售发票明细报表
     * @return 销售发票明细报表
     */
    @Override
    public List<FinSaleInvoiceItem> selectFinSaleInvoiceItemList(FinSaleInvoiceItem finSaleInvoiceItem) {
        return finSaleInvoiceItemMapper.selectFinSaleInvoiceItemList(finSaleInvoiceItem);
    }


    /**
     * 查询销售发票-明细报表
     *
     * @param finSaleInvoiceItem 销售发票-明细
     * @return 采购发票-明细
     */
    @Override
    public List<FinSaleInvoiceItem> getReportForm(FinSaleInvoiceItem finSaleInvoiceItem) {
        List<FinSaleInvoiceItem> itemList = finSaleInvoiceItemMapper.getReportForm(finSaleInvoiceItem);
        return itemList;
    }

}
