package com.platform.ems.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.FinSaleInvoice;
import com.platform.ems.domain.dto.request.form.FinBookAccountReceivableFormRequest;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.FinSaleInvoiceMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookAccountReceivableItem;
import com.platform.ems.mapper.FinBookAccountReceivableItemMapper;
import com.platform.ems.service.IFinBookAccountReceivableItemService;

/**
 * 财务流水账-明细-应收Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-11
 */
@Service
@SuppressWarnings("all")
public class FinBookAccountReceivableItemServiceImpl extends ServiceImpl<FinBookAccountReceivableItemMapper,FinBookAccountReceivableItem>  implements IFinBookAccountReceivableItemService {
    @Autowired
    private FinBookAccountReceivableItemMapper finBookAccountReceivableItemMapper;
    @Autowired
    private FinSaleInvoiceMapper finSaleInvoiceMapper;

    private static final String TITLE = "财务流水账-明细-应收";

    /**
     * 设置到期日前的校验
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyValidDate(FinBookAccountReceivableFormRequest request){
        List<FinBookAccountReceivableItem> itemList = finBookAccountReceivableItemMapper.selectFinBookAccountReceivableItemList(
                new FinBookAccountReceivableItem().setBookAccountReceivableItemSidList(request.getBookAccountReceivableItemSidList())
                        .setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX,ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()}));
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != request.getBookAccountReceivableItemSidList().length){
            throw new BaseException("仅核销状态为“未核销”或“部分核销”，处理状态为“已确认”的数据可以点击该按钮");
        }
        Long[] invoiceSidList = itemList.stream().map(FinBookAccountReceivableItem::getReferDocSid).toArray(Long[]::new);
        itemList = itemList.stream().filter(o->HandleStatus.REDDASHED.getCode().equals(o.getHandleStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(itemList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
        List<FinSaleInvoice> invoiceList = finSaleInvoiceMapper.selectList(new QueryWrapper<FinSaleInvoice>().lambda()
                .in(FinSaleInvoice::getSaleInvoiceSid, invoiceSidList)
                .eq(FinSaleInvoice::getInvoiceCategory, ConstantsFinance.INVOICE_CATE_HX));
        if (CollectionUtils.isNotEmpty(invoiceList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
    }

    /**
     * 设置到期日
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setValidDate(FinBookAccountReceivableFormRequest request){
        if (request.getAccountValidDate() == null){
            throw new BaseException("请选择到期日");
        }
        int row = 0;
        List<FinBookAccountReceivableItem> itemList = finBookAccountReceivableItemMapper.selectFinBookAccountReceivableItemList(
                new FinBookAccountReceivableItem().setBookAccountReceivableItemSidList(request.getBookAccountReceivableItemSidList())
                        .setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX,ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()}));
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != request.getBookAccountReceivableItemSidList().length){
            throw new BaseException("仅核销状态为“未核销”或“部分核销”，处理状态为“已确认”的数据可以点击该按钮");
        }
        Long[] invoiceSidList = itemList.stream().map(FinBookAccountReceivableItem::getReferDocSid).toArray(Long[]::new);
        itemList = itemList.stream().filter(o->HandleStatus.REDDASHED.getCode().equals(o.getHandleStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(itemList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
        List<FinSaleInvoice> invoiceList = finSaleInvoiceMapper.selectList(new QueryWrapper<FinSaleInvoice>().lambda()
                .in(FinSaleInvoice::getSaleInvoiceSid, invoiceSidList)
                .eq(FinSaleInvoice::getInvoiceCategory, ConstantsFinance.INVOICE_CATE_HX));
        if (CollectionUtils.isNotEmpty(invoiceList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
        UpdateWrapper<FinBookAccountReceivableItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(FinBookAccountReceivableItem::getBookAccountReceivableItemSid, request.getBookAccountReceivableItemSidList());
        if (request.getAccountValidDate() != null){
            updateWrapper.lambda().set(FinBookAccountReceivableItem::getAccountValidDate, request.getAccountValidDate());
        }
        else {
            updateWrapper.lambda().set(FinBookAccountReceivableItem::getAccountValidDate, null);
        }
        row = finBookAccountReceivableItemMapper.update(null, updateWrapper);
        return row;
    }

}
