package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.FinPurchaseInvoice;
import com.platform.ems.domain.dto.request.form.FinBookAccountPayableFormRequest;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.FinPurchaseInvoiceMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.FinBookAccountPayableItem;
import com.platform.ems.mapper.FinBookAccountPayableItemMapper;
import com.platform.ems.service.IFinBookAccountPayableItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 财务流水账-明细-应付Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-03
 */
@Service
@SuppressWarnings("all")
public class FinBookAccountPayableItemServiceImpl extends ServiceImpl<FinBookAccountPayableItemMapper,FinBookAccountPayableItem>  implements IFinBookAccountPayableItemService {
    @Autowired
    private FinBookAccountPayableItemMapper finBookAccountPayableItemMapper;
    @Autowired
    private FinPurchaseInvoiceMapper finPurchaseInvoiceMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "财务流水账-明细-应付";


    /**
     * 设置到期日前的校验
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyValidDate(FinBookAccountPayableFormRequest request){
        List<FinBookAccountPayableItem> itemList = finBookAccountPayableItemMapper.selectFinBookAccountPayableItemList(
                new FinBookAccountPayableItem().setBookAccountPayableItemSidList(request.getBookAccountPayableItemSidList())
                        .setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX,ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()}));
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != request.getBookAccountPayableItemSidList().length){
            throw new BaseException("仅核销状态为“未核销”或“部分核销”，处理状态为“已确认”的数据可以点击该按钮");
        }
        Long[] invoiceSidList = itemList.stream().map(FinBookAccountPayableItem::getReferDocSid).toArray(Long[]::new);
        itemList = itemList.stream().filter(o->HandleStatus.REDDASHED.getCode().equals(o.getHandleStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(itemList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
        List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectList(new QueryWrapper<FinPurchaseInvoice>().lambda()
                .in(FinPurchaseInvoice::getPurchaseInvoiceSid, invoiceSidList)
                .eq(FinPurchaseInvoice::getInvoiceCategory, ConstantsFinance.INVOICE_CATE_HX));
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
    public int setValidDate(FinBookAccountPayableFormRequest request){
        if (request.getAccountValidDate() == null){
            throw new BaseException("请选择到期日");
        }
        int row = 0;
        List<FinBookAccountPayableItem> itemList = finBookAccountPayableItemMapper.selectFinBookAccountPayableItemList(
                new FinBookAccountPayableItem().setBookAccountPayableItemSidList(request.getBookAccountPayableItemSidList())
                        .setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX,ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()}));
        if (CollectionUtils.isEmpty(itemList) || itemList.size() != request.getBookAccountPayableItemSidList().length){
            throw new BaseException("仅核销状态为“未核销”或“部分核销”，处理状态为“已确认”的数据可以点击该按钮");
        }
        Long[] invoiceSidList = itemList.stream().map(FinBookAccountPayableItem::getReferDocSid).toArray(Long[]::new);
        itemList = itemList.stream().filter(o->HandleStatus.REDDASHED.getCode().equals(o.getHandleStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(itemList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
        List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectList(new QueryWrapper<FinPurchaseInvoice>().lambda()
                .in(FinPurchaseInvoice::getPurchaseInvoiceSid, invoiceSidList)
                .eq(FinPurchaseInvoice::getInvoiceCategory, ConstantsFinance.INVOICE_CATE_HX));
        if (CollectionUtils.isNotEmpty(invoiceList)){
            throw new BaseException("存在红冲发票生成的流水，无需此操作，请核实！");
        }
        UpdateWrapper<FinBookAccountPayableItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(FinBookAccountPayableItem::getBookAccountPayableItemSid, request.getBookAccountPayableItemSidList());
        if (request.getAccountValidDate() != null){
            updateWrapper.lambda().set(FinBookAccountPayableItem::getAccountValidDate, request.getAccountValidDate());
        }
        else {
            updateWrapper.lambda().set(FinBookAccountPayableItem::getAccountValidDate, null);
        }
        row = finBookAccountPayableItemMapper.update(null, updateWrapper);
        return row;
    }

}
