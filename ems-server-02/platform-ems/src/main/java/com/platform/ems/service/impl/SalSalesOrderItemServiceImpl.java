package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.OrderBestSellingRequest;
import com.platform.ems.domain.dto.request.OrderProgressRequest;
import com.platform.ems.domain.dto.request.OrderTotalRequest;
import com.platform.ems.domain.dto.request.SaleOrderProgressRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.SalSaleProductCostForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISalSalesOrderService;
import com.platform.ems.util.LightUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.ISalSalesOrderItemService;


import static java.util.stream.Collectors.toList;

/**
 * 销售订单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class SalSalesOrderItemServiceImpl extends ServiceImpl<SalSalesOrderItemMapper, SalSalesOrderItem> implements ISalSalesOrderItemService {
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private ISalSalesOrderService slSalesOrderService;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private ManManufactureOrderProductMapper manManufactureOrderProductMapper;
    @Autowired
    private  ISalSalesOrderService salSalesOrderService;
    final  String DIMENSION_WZ="merge";
    final  String DIMENSION_MX="spread";
    String[] handle={ConstantsEms.OUT_STORE_STATUS_NOT,ConstantsEms.IN_STORE_STATUS_NOT};
    /**
     * 查询销售订单-明细
     *
     * @param clientId 销售订单-明细ID
     * @return 销售订单-明细
     */
    @Override
    public SalSalesOrderItem selectSalSalesOrderItemById(Long salesOrderItemSid) {
        return salSalesOrderItemMapper.selectSalSalesOrderItemById(salesOrderItemSid);
    }

    /**
     * 查询销售订单-明细列表
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 销售订单-明细
     */
    @Override
    public List<SalSalesOrderItem> selectSalSalesOrderItemList(SalSalesOrderItem salSalesOrderItem) {
        return salSalesOrderItemMapper.selectSalSalesOrderItemList(salSalesOrderItem);
    }
    /**
     *销售状况交期报表 主
     */
    @Override
    public List<OrderProgressResponse>  getDeliveryProcess(OrderProgressRequest request){
        List<OrderProgressResponse> deliveryProcess = salSalesOrderItemMapper.getDeliveryProcess(request);
        if(CollectionUtil.isNotEmpty(deliveryProcess)){
            deliveryProcess.forEach(li->{
                if(li.getJjdqPriceTax()!=null){
                    li.setJjdqPriceTax(li.getJjdqPriceTax().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
                if(li.getYyqPriceTax()!=null){
                    li.setYyqPriceTax(li.getYyqPriceTax().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
            });
        }
        return deliveryProcess;
    }


    /**
     *销售状况交期报表 明细
     */
    @Override
    public List<OrderProgressItemResponse>  getDeliveryProcessItem(OrderProgressRequest request){
        List<OrderProgressItemResponse> list = salSalesOrderItemMapper.getDeliveryProcessItem(request);
        decimalProcess(list);
        return list;
    }
    public void decimalProcess(List<OrderProgressItemResponse> list){
        if(CollectionUtil.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getJjdqPriceTax()!=null){
                    li.setJjdqPriceTax(li.getJjdqPriceTax().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
                if(li.getYyqPriceTax()!=null){
                    li.setYyqPriceTax(li.getYyqPriceTax().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
            });
        }
    }

    @Override
    public List<OrderProgressItemResponse> sortProgressItem(List<OrderProgressItemResponse> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            List<OrderProgressItemResponse> skuExit = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                skuExit = skuExit.stream().sorted(Comparator.comparing(OrderProgressItemResponse::getSku2Name, Comparator.nullsLast(String::compareTo)
                        .thenComparing(Collator.getInstance(Locale.CHINA)))).collect(toList());
            }
            List<OrderProgressItemResponse> skuExitNo = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<OrderProgressItemResponse> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            salSalesOrderItemList=itemArrayListAll.stream().filter(li->li.getMaterialCode()!=null&&li.getSku1Name()!=null&&li.getContractDate()!=null)
                    .sorted(Comparator.comparing(OrderProgressItemResponse::getContractDate, Comparator.nullsLast(Date::compareTo))
                    .thenComparing(OrderProgressItemResponse::getMaterialCode)
                    .thenComparing(OrderProgressItemResponse::getSku1Name, Comparator.nullsLast(String::compareTo))
            ).collect(Collectors.toList());

            return salSalesOrderItemList;
        }
        return new ArrayList<>();
    }
    /**
     *销售统计报表 主
     */
    @Override
    public List<OrderTotalResponse> getTotal(OrderTotalRequest request){
        List<OrderTotalResponse> total = salSalesOrderItemMapper.getTotal(request);
        decimalTotal(total);
        return total;
    }

    public void decimalTotal(List<OrderTotalResponse> list){
        if(CollectionUtil.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getInvTotalPrice()!=null){
                    li.setInvTotalPrice(li.getInvTotalPrice().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
                if(li.getOrderTotalPrice()!=null){
                    li.setOrderTotalPrice(li.getOrderTotalPrice().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
            });
        }
    }
    /**
     *销售畅销报表 主
     */
    @Override
    public List<OrderBestSellingResponse> getBestSell(OrderBestSellingRequest request){
        if(request.getDimension()==null){
            request.setDimension("quantity");
        }
        List<OrderBestSellingResponse> list = salSalesOrderItemMapper.getBestSell(request);
        decimal(list);
        Integer pageNum = request.getPageNum();
        if(CollectionUtil.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                int sort=(pageNum-1)*10+i+1;
                list.get(i).setSort(sort);
            }
        }
        String materialCode = request.getMaterialCode();
        if(materialCode!=null){
            List<OrderBestSellingResponse> results = list.stream().filter(li -> li.getMaterialCode().contains(materialCode)).collect(Collectors.toList());
            return  results;
        }
        return list;
    }
    public void decimal(List<OrderBestSellingResponse> list){
        if(CollectionUtil.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getInvTotalPrice()!=null){
                    li.setInvTotalPrice(li.getInvTotalPrice().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
                if(li.getOrderTotalPrice()!=null){
                    li.setOrderTotalPrice(li.getOrderTotalPrice().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
            });
        }
    }

    /**
     *销售畅销报表 明细
     */
    @Override
    public List<OrderBestSellingResponse> getBestSellItem(OrderBestSellingRequest request){
        List<OrderBestSellingResponse> list = salSalesOrderItemMapper.getBestSellItem(request);
        decimal(list);
        return list;
    }
    /**
     *销售统计报表 明细
     */
    @Override
    public List<OrderTotalResponse> getTotalItem(OrderTotalRequest request){
        List<OrderTotalResponse> list = salSalesOrderItemMapper.getTotalItem(request);
        decimalTotalItem(list);
        return list;
    }
    public void decimalTotalItem(List<OrderTotalResponse> list){
        if(CollectionUtil.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getInvTotalPrice()!=null){
                    li.setInvTotalPrice(li.getInvTotalPrice().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
                if(li.getOrderTotalPrice()!=null){
                    li.setOrderTotalPrice(li.getOrderTotalPrice().divide(new BigDecimal("10000"),2,BigDecimal.ROUND_HALF_UP));
                }
            });
        }
    }
    /**
     * 新增销售订单-明细
     * 需要注意编码重复校验
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesOrderItem(SalSalesOrderItem salSalesOrderItem) {
        return salSalesOrderItemMapper.insert(salSalesOrderItem);
    }

    /**
     * 修改销售订单-明细
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesOrderItem(SalSalesOrderItem salSalesOrderItem) {
        return salSalesOrderItemMapper.updateById(salSalesOrderItem);
    }
    @Override
    public List<SaleOrderProgressResponse> getProcessHead(SaleOrderProgressRequest request) {
        return salSalesOrderItemMapper.getProcessHead(request);
    }

    /**
     * 销售订单进度报表 明细
     */
    @Override
    public List<SaleOrderProgressItemResponse> getProcessItem(SaleOrderProgressRequest request){
        return salSalesOrderItemMapper.getProcessItem(request);
    }
    /**
     * 批量删除销售订单-明细
     *
     * @param clientIds 需要删除的销售订单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesOrderItemByIds(List<String> clientIds) {
        return salSalesOrderItemMapper.deleteBatchIds(clientIds);
    }

    /**
     * 销售订单明细报表
     */
    @Override
    public List<SalSalesOrderItem> getItemList(SalSalesOrderItem salSalesOrderItem) {
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemList(salSalesOrderItem);
        if (CollectionUtil.isNotEmpty(itemList)) {
            // 已排产量
            Long[] sids = itemList.stream().map(SalSalesOrderItem::getSalesOrderItemSid).toArray(Long[]::new);
            List<ManManufactureOrderProduct> list = manManufactureOrderProductMapper.getPaichanQuantity(new ManManufactureOrderProduct().setSalesOrderItemSidList(sids));
            Map<Long, BigDecimal> map = new HashMap<>();
            if (CollectionUtil.isNotEmpty(list)) {
                 map = list.stream().collect(Collectors.toMap(ManManufactureOrderProduct::getSalesOrderItemSid, ManManufactureOrderProduct::getQuantity));
            }
            BigDecimal allready = BigDecimal.ZERO;
            BigDecimal notQuantity = BigDecimal.ZERO;
            for (SalSalesOrderItem o : itemList) {
                allready = BigDecimal.ZERO;
                notQuantity = BigDecimal.ZERO;
                // 预警灯
                Integer days = 0;
                if (o.getToexpireDays() != null) {
                    days = Integer.parseInt(o.getToexpireDays().toString());
                }
                LightUtil.setLight(o, o.getContractDate(), null, days);
                // 排产量
                if (map.containsKey(o.getSalesOrderItemSid())) {
                    allready = map.get(o.getSalesOrderItemSid());
                    if (allready == null) {
                        allready =  BigDecimal.ZERO;
                    }
                }
                if (o.getQuantity() != null) {
                    notQuantity = o.getQuantity();
                }
                o.setAlreadyQuantity(allready);
                o.setNotQuantity(notQuantity.subtract(allready));
            }
        }
        return itemList;
    }

    /**
     * 销售订单排产明细报表
     */
    @Override
    public List<SalSalesOrderItem> getItemListProduct(SalSalesOrderItem salSalesOrderItem) {
        String dimension = salSalesOrderItem.getDimension();
        if(dimension==null){
            salSalesOrderItem.setDimension(DIMENSION_WZ);
        }
        if(ConstantsEms.NO.equals(salSalesOrderItem.getIsSkipZero())){
            salSalesOrderItem.setIsSkipZero(null);
        }
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemProductList(salSalesOrderItem);
        if(salSalesOrderItem.getDimension().equals(DIMENSION_WZ)){
            if(CollectionUtil.isNotEmpty(itemList)){
                itemList.forEach(item->
                {
                    SalSalesOrderItem order = new SalSalesOrderItem();
                    BeanCopyUtils.copyProperties(salSalesOrderItem,order);
                    if(item.getContractDate()==null){
                        order.setDimension(DIMENSION_MX)
                                .setMaterialCode(item.getMaterialCode())
                                .setIsNullContractDate(ConstantsEms.YES)
                                .setSku1Name(item.getSku1Name());
                    }else{
                        order.setDimension(DIMENSION_MX)
                                .setMaterialCode(item.getMaterialCode())
                                .setContractDate(item.getContractDate())
                                .setSku1Name(item.getSku1Name());
                    }
                    List<SalSalesOrderItem> itemProductList = salSalesOrderItemMapper.getItemProductList(order);
                    List<SalSalesOrderItemTotalResponse> list = BeanCopyUtils.copyListProperties(itemProductList, SalSalesOrderItemTotalResponse::new);
                    list = list.stream().sorted(Comparator.comparing(SalSalesOrderItemTotalResponse::getSku2Name, Comparator.nullsLast(String::compareTo)).
                            thenComparing(SalSalesOrderItemTotalResponse::getSalesOrderCode)
                            .thenComparing(SalSalesOrderItemTotalResponse::getItemNum, Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());
                    item.setItemlist(list);
                });
            }
        }
        return itemList;
    }

    /**
     * 销售订单排产明细报表
     */
    @Override
    public List<SalSalesOrderItem> mobPaichan(SalSalesOrderItem salSalesOrderItem) {
        return salSalesOrderItemMapper.mobPaichan(salSalesOrderItem);
    }

    /**
     * 销售订单排采明细报表
     */
    @Override
    public List<SalSalesOrderItem> getItemListPC(SalSalesOrderItem salSalesOrderItem) {
        String dimension = salSalesOrderItem.getDimension();
        if(dimension==null){
            salSalesOrderItem.setDimension(DIMENSION_WZ);
        }
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemPCList(salSalesOrderItem);
        if(salSalesOrderItem.getDimension().equals(DIMENSION_WZ)){
            if(CollectionUtil.isNotEmpty(itemList)){
                itemList.forEach(item->
                {
                    SalSalesOrderItem order = new SalSalesOrderItem();
                    BeanCopyUtils.copyProperties(salSalesOrderItem,order);
                    if(item.getContractDate()==null){
                        order.setDimension(DIMENSION_MX)
                                .setMaterialCode(item.getMaterialCode())
                                .setIsNullContractDate(ConstantsEms.YES)
                                .setSku1Name(item.getSku1Name());
                    }else{
                        order.setDimension(DIMENSION_MX)
                                .setMaterialCode(item.getMaterialCode())
                                .setContractDate(item.getContractDate())
                                .setSku1Name(item.getSku1Name());
                    }
                    List<SalSalesOrderItem> itemProductList = salSalesOrderItemMapper.getItemPCList(order);
                    List<SalSalesOrderItemTotalResponse> list = BeanCopyUtils.copyListProperties(itemProductList, SalSalesOrderItemTotalResponse::new);
                    list = list.stream().sorted(Comparator.comparing(SalSalesOrderItemTotalResponse::getSku2Name, Comparator.nullsLast(String::compareTo)).
                            thenComparing(SalSalesOrderItemTotalResponse::getSalesOrderCode)
                            .thenComparing(SalSalesOrderItemTotalResponse::getItemNum, Comparator.nullsLast(Integer::compareTo))).collect(Collectors.toList());
                    item.setItemlist(list);
                });
            }
        }
        return itemList;
    }
    @Override
    public List<SalSalesOrderItem> handleIndex(List<SalSalesOrderItem> itemList){
        if (CollectionUtil.isEmpty(itemList)) {
            return itemList;
        }
        List<SalSalesOrderItem> saleitemList = itemList.stream().filter(li->!ConstantsEms.SALE_SHIP.equals(li.getDeliveryType())).collect(Collectors.toList());
        itemList = itemList.stream().filter(li->ConstantsEms.SALE_SHIP.equals(li.getDeliveryType())).collect(Collectors.toList());
        //按发货单
        if(CollectionUtil.isNotEmpty(itemList)){
            List<Long> sidItems = itemList.stream().map(li -> li.getSalesOrderSid()).collect(Collectors.toList());
            Long[] sidList = itemList.stream().map(li -> li.getSalesOrderItemSid()).toArray(Long[]::new);
            //获取相关联的发货单明细
            List<DelDeliveryNoteItem> delDeliveryAll = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList
                    (new DelDeliveryNoteItem().setSalesOrderItemSidList(sidList).setHandleStatus(ConstantsEms.CHECK_STATUS));
            if(CollectionUtil.isNotEmpty(delDeliveryAll)){
                itemList.forEach(item->{
                    List<DelDeliveryNoteItem> deItem = delDeliveryAll.stream().filter(li -> li.getSalesOrderItemSid().toString().equals(item.getSalesOrderItemSid().toString())).collect(Collectors.toList());
                    deItem=deItem.stream().filter(li->li.getInOutStockQuantity()!=null).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(deItem)){
                        //已出库量
                        BigDecimal sum = deItem.stream().map(li -> li.getInOutStockQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                        item.setInQuantity(sum);
                    }
                    if(item.getInQuantity()!=null){
                        //待出库量
                        item.setPartQuantity(item.getQuantity().subtract(item.getInQuantity()));
                    }
                });
            }
            List<DelDeliveryNote> deliveryNoteList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>().lambda()
                    .in(DelDeliveryNote::getSalesOrderSid, sidItems)
                    .in(DelDeliveryNote::getInOutStockStatus,handle)
                    .eq(DelDeliveryNote::getHandleStatus, HandleStatus.CONFIRMED.getCode())

            );
            List<Long> longs = deliveryNoteList.stream().map(li -> li.getDeliveryNoteSid()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(longs)){
                //确认状态下的 未出库的销售发货单
                List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                        .in(DelDeliveryNoteItem::getSalesOrderItemSid, sidList)
                        .in(DelDeliveryNoteItem::getDeliveryNoteSid,longs)
                );
                itemList.forEach(item->{
                    List<DelDeliveryNoteItem> deItem = delDeliveryNoteItems.stream().filter(li -> li.getSalesOrderItemSid().toString().equals(item.getSalesOrderItemSid().toString())).collect(Collectors.toList());
                    BigDecimal sum = deItem.stream().map(li -> li.getDeliveryQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    //已发货未出入库量
                    item.setInWQuantity(sum);
                });
            }
        }
        if(CollectionUtil.isNotEmpty(saleitemList)){
            Long[] longs = saleitemList.stream().map(li -> li.getSalesOrderItemSid()).toArray(Long[]::new);
            List<InvInventoryDocumentItem> documentItems = invInventoryDocumentItemMapper.selectInvInventoryDocumentItemList
                            (new InvInventoryDocumentItem().setReferDocumentItemSidList(longs)
                                    .setDocumentType(ConstantsInventory.BUSINESS_FLAG_CG)
                                    .setHandleStatus(HandleStatus.POSTING.getCode()));

            if(CollectionUtil.isNotEmpty(documentItems)){
                saleitemList.forEach(li->{
                    List<InvInventoryDocumentItem> items = documentItems.stream().filter(m -> m.getReferDocumentItemSid().toString().equals(li.getSalesOrderItemSid().toString())).collect(Collectors.toList());
                    BigDecimal sum = items.stream().map(m -> {
                        if(m.getPriceQuantity()!=null){
                            return m.getPriceQuantity();
                        }else{
                            return BigDecimal.ZERO;
                        }
                    }).reduce(BigDecimal.ZERO,BigDecimal::add);
                    li.setInQuantity(sum);
                    if(sum!=null){
                        li.setPartQuantity(li.getQuantity().subtract(sum));
                    }
                });
            }
        }
        List<SalSalesOrderItem> All = new ArrayList<>();
        All.addAll(itemList);
        All.addAll(saleitemList);
        All.forEach(li->{
            if(li.getPartQuantity()==null){
                li.setPartQuantity(li.getQuantity());
            }
            if(li.getQuantity()!=null&&li.getSalePriceTax()!=null){
                li.setPriceTax((li.getQuantity().multiply(li.getSalePriceTax())).divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
            }
            if(li.getQuantity()!=null&&li.getSalePrice()!=null){
                li.setPrice((li.getQuantity().multiply(li.getSalePrice())).divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
            }
            if(ConstantsEms.NO.equals(li.getFreeFlag())){
                li.setFreeFlag(null);
            }
        });
        //获取可用库存量
        getInvQuantily(All);
        // 重新排序
        try {
            All = All.stream().sorted(Comparator.comparing(SalSalesOrderItem::getSalesOrderCode, Comparator.nullsLast(String::compareTo)
                            .thenComparing(Comparator.comparingLong(Long::parseLong)).reversed())
                    .thenComparing(SalSalesOrderItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(SalSalesOrderItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                    .thenComparing(SalSalesOrderItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(SalSalesOrderItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                    .thenComparing(SalSalesOrderItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(SalSalesOrderItem::getContractDate, Comparator.nullsLast(Date::compareTo))).collect(toList());
        } catch (Exception e) {
            log.error("订单明细报表计算数值后重新排序后报错");
        }
        return All;
    }
    @Override
    public List<SalSalesOrderItem> handleIndexPro(List<SalSalesOrderItem> itemList){
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(item->{
                List<ManManufactureOrderProduct> manManufactureOrderProducts = manManufactureOrderProductMapper.selectList(new QueryWrapper<ManManufactureOrderProduct>().lambda()
                        .eq(ManManufactureOrderProduct::getSalesOrderItemSid, item.getSalesOrderItemSid())
                );
                if(CollectionUtil.isNotEmpty(manManufactureOrderProducts)){
                    BigDecimal sum = manManufactureOrderProducts.stream().map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    item.setNotQuantity(item.getQuantity().subtract(sum));
                    item.setAlreadyQuantity(sum);
                    if(sum.compareTo(BigDecimal.ZERO)==0){
                        item.setQuantityStatus("WSC");
                    }else if(sum.compareTo(item.getQuantity())!=-1){
                        item.setQuantityStatus("QBSC");
                    }else{
                        item.setQuantityStatus("BFSC");
                    }
                }else{
                    item.setQuantityStatus("WSC");
                    item.setNotQuantity(item.getQuantity());
                }
            });
        }
        return itemList;
    }

    @Override
    public SalSalesOrder handleIndexDelievery(SalSalesOrder salSalesOrder){
        List<SalSalesOrderItem> salSalesOrderItemItemList = salSalesOrder.getSalSalesOrderItemList();
        salSalesOrderItemItemList.forEach(item-> {
                    item.setInQuantity(BigDecimal.ZERO)
                            .setInWQuantity(BigDecimal.ZERO);
                    List<DelDeliveryNoteItem> delDeliveryAll = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                            .in(DelDeliveryNoteItem::getSalesOrderItemSid, item.getSalesOrderItemSid()));
                    if (CollectionUtil.isNotEmpty(delDeliveryAll)) {
                        List<DelDeliveryNoteItem> InOutStockQuantityList = delDeliveryAll.stream().filter(li -> li.getInOutStockQuantity() != null).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(InOutStockQuantityList)) {
                            //已出库量
                            BigDecimal sum = InOutStockQuantityList.stream().map(li -> li.getInOutStockQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            item.setInQuantity(sum);
                        }
                        List<DelDeliveryNote> deliveryNoteList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>().lambda()
                                .eq(DelDeliveryNote::getSalesOrderSid, salSalesOrder.getSalesOrderSid())
                                .in(DelDeliveryNote::getInOutStockStatus,handle)
                        );
                        if(CollectionUtil.isNotEmpty(deliveryNoteList)){
                            List<Long> AllSidList = delDeliveryAll.stream().map(m -> m.getDeliveryNoteItemSid()).collect(Collectors.toList());
                            List<DelDeliveryNote> notInOutStockQuantityList = deliveryNoteList.stream().filter(li -> !HandleStatus.INVALID.getCode().equals(li.getHandleStatus())).collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(notInOutStockQuantityList)){
                                List<Long> sidList = notInOutStockQuantityList.stream().map(li -> li.getDeliveryNoteSid()).collect(Collectors.toList());
                                List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                                        .in(DelDeliveryNoteItem::getDeliveryNoteSid,sidList)
                                        .in(DelDeliveryNoteItem::getDeliveryNoteItemSid,AllSidList)
                                );
                                BigDecimal sum = delDeliveryNoteItems.stream().map(li -> li.getDeliveryQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                                //已发货未出入库量
                                item.setInWQuantity(sum);
                            }
                        }
                        item.setDeliveryQuantity(item.getQuantity().subtract(item.getInQuantity()).subtract(item.getInWQuantity()));
                    }
                    if(item.getDeliveryQuantity()==null){
                        item.setDeliveryQuantity(item.getQuantity().subtract(item.getInQuantity()).subtract(item.getInWQuantity()));
                    }
                }
        );
        salSalesOrder.setSalSalesOrderItemList(salSalesOrderItemItemList);
        return salSalesOrder;
    }
    /**
     * 获取可用库存量
     *
     */
    public void getInvQuantily(List<SalSalesOrderItem> items){
        if(CollectionUtil.isNotEmpty(items)){
            items.forEach(li->{
                List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>()
                        .lambda()
                        .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                );
                if(CollectionUtil.isNotEmpty(invInventoryLocations)){
                    BigDecimal sum=BigDecimal.ZERO;
                    if(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(li.getSpecialBusCategory())){
                        sum = invInventoryLocations.stream().map(m -> m.getCustomerConsignQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }else{
                        sum = invInventoryLocations.stream().map(m -> m.getUnlimitedQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    li.setInvQuantity(sum);
                }
            });
        }
    }

    /**
     * 查询商品销售成本报表
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 销售订单-明细集合
     */
    @Override
    public List<SalSaleProductCostForm> selectSalSalesProductCostList(SalSaleProductCostForm salSalesOrderItem) {
        salSalesOrderItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        return salSalesOrderItemMapper.selectSalSalesProductCostList(salSalesOrderItem);
    }

    /**
     * 移动端销售进度
     */
    @Override
    public List<SalSalesOrderItem> selectMobProcessList(SalSalesOrderItem order) {
        return salSalesOrderItemMapper.selectMobProcessList(order);
    }

}
