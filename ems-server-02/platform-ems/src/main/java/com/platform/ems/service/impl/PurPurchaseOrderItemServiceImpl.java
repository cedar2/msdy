package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.OrderProgressRequest;
import com.platform.ems.domain.dto.request.OrderTotalRequest;
import com.platform.ems.domain.dto.response.OrderProgressItemResponse;
import com.platform.ems.domain.dto.response.OrderProgressResponse;
import com.platform.ems.domain.dto.response.OrderTotalResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPurPurchaseOrderItemService;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 采购订单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseOrderItemServiceImpl extends ServiceImpl<PurPurchaseOrderItemMapper,PurPurchaseOrderItem>  implements IPurPurchaseOrderItemService {
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private PurPurchaseOrderDataSourceMapper purPurchaseOrderDataSourceMapper;
    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;

    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;

    String[] handle={ConstantsEms.OUT_STORE_STATUS_NOT,ConstantsEms.IN_STORE_STATUS_NOT};

    /**
     * 查询采购订单-明细
     *
     * @param clientId 采购订单-明细ID
     * @return 采购订单-明细
     */
    @Override
    public PurPurchaseOrderItem selectPurPurchaseOrderItemById(String clientId) {
        return null;
    }

    /**
     * 查询采购订单-明细列表
     *
     * @param purPurchaseOrderItem 采购订单-明细
     * @return 采购订单-明细
     */
    @Override
    public List<PurPurchaseOrderItem> selectPurPurchaseOrderItemList(PurPurchaseOrderItem purPurchaseOrderItem) {
        return purPurchaseOrderItemMapper.selectPurPurchaseOrderItemList(purPurchaseOrderItem);
    }

    /**
     *采购状况交期报表 主
     */
    @Override
    public List<OrderProgressResponse>  getDeliveryProcess(OrderProgressRequest request){
        List<OrderProgressResponse> deliveryProcess = purPurchaseOrderItemMapper.getDeliveryProcess(request);
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
     *采购统计报表 主
     */
    @Override
    public List<OrderTotalResponse> getTotal(OrderTotalRequest request){
        return purPurchaseOrderItemMapper.getTotal(request);
    }

    /**
     *采购统计报表 明细
     */
    @Override
    public List<OrderTotalResponse> getTotalItem(OrderTotalRequest request){
        return purPurchaseOrderItemMapper.getTotalItem(request);
    }
    /**
     *采购状况交期报表 明细
     */
    @Override
    public List<OrderProgressItemResponse>  getDeliveryProcessItem(OrderProgressRequest request){
        List<OrderProgressItemResponse> deliveryProcess = purPurchaseOrderItemMapper.getDeliveryProcessItem(request);
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
     * 数据来源列表
     * @param dataSource
     * @return
     */
    @Override
    public List<PurPurchaseOrderDataSource> selectPurPurchaseOrderDataSourceList(PurPurchaseOrderDataSource dataSource) {
        return purPurchaseOrderDataSourceMapper.selectPurPurchaseOrderDataSourceList(dataSource);
    }

    /**
     * 数据来源列表修改
     * @param dataSourceList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity updatePurPurchaseOrderDataSourceList(List<PurPurchaseOrderDataSource> dataSourceList, String keep) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(dataSourceList)) {
            if ("false".equals(keep)) {
                // 根据配置校验是要提醒还是报错
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                for (int i = 0; i < dataSourceList.size(); i++) {
                    PurPurchaseOrderDataSource item = dataSourceList.get(i);
                    // 本次下单量
                    BigDecimal one = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
                    // 已下单量不含本单
                    BigDecimal two = item.getQuantityReferOtherSum() == null ? BigDecimal.ZERO : item.getQuantityReferOtherSum();
                    // 单据量
                    BigDecimal three = item.getPurchaseRequireItem() == null ? BigDecimal.ZERO :
                            item.getPurchaseRequireItem().getQuantity() == null ? BigDecimal.ZERO : item.getPurchaseRequireItem().getQuantity();
                    if (one.add(two).compareTo(three) > 0) {
                        if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(settingClient.getNoticeTypePurRequireToOrderExcess())) {
                            return EmsResultEntity.warning(new ArrayList<>(), "本次下单量”与“已下单量(不含本单）”之和，已超过单据量，是否继续更新？");
                        }
                        else if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(settingClient.getNoticeTypePurRequireToOrderExcess())) {
                            return EmsResultEntity.error(new ArrayList<>(), "“本次下单量”与“已下单量(不含本单）”之和，已超过单据量，无法更新！");
                        }
                    }
                }
            }
            row = purPurchaseOrderDataSourceMapper.updatesAllById(dataSourceList);
            if (row > 0) {
                Map<Long, List<PurPurchaseOrderDataSource>> mapList = dataSourceList.stream()
                        .collect(Collectors.groupingBy(e -> e.getPurchaseOrderItemSid()));
                for (Long key : mapList.keySet()) {
                    BigDecimal quantitySum = mapList.get(key).stream().map(PurPurchaseOrderDataSource::getQuantity).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                    // 更新采购订单明细
                    UpdateWrapper<PurPurchaseOrderItem> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().set(PurPurchaseOrderItem::getQuantity, quantitySum)
                            .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, key);
                    purPurchaseOrderItemMapper.update(null, updateWrapper);
                }
            }
        }
        return EmsResultEntity.success(row);
    }

    /**
     * 移动端采购进度
     */
    @Override
    public List<PurPurchaseOrderItem> selectMobProcessList(PurPurchaseOrderItem order) {
        return purPurchaseOrderItemMapper.selectMobProcessList(order);
    }

    /**
     * 订单明细更新来源数量时得到新的订单量返回前端
     * @param dataSourceList
     * @return
     */
    @Override
    public EmsResultEntity getPurPurchaseOrderItemQuantityByDataSource(List<PurPurchaseOrderDataSource> dataSourceList, String keep) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(dataSourceList)) {
            if ("false".equals(keep)) {
                List<PurPurchaseOrderDataSource> isNullList = dataSourceList.stream().filter(o->o.getNewQuantity() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(isNullList)) {
                    // 根据配置校验是要提醒还是报错
                    SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                            .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                    for (int i = 0; i < isNullList.size(); i++) {
                        PurPurchaseOrderDataSource item = isNullList.get(i);
                        // 本次下单量(变更中)
                        BigDecimal one = item.getNewQuantity() == null ? BigDecimal.ZERO : item.getNewQuantity();
                        // 已下单量不含本单
                        BigDecimal two = item.getQuantityReferOtherSum() == null ? BigDecimal.ZERO : item.getQuantityReferOtherSum();
                        // 单据量
                        BigDecimal three = item.getPurchaseRequireItem() == null ? BigDecimal.ZERO :
                                item.getPurchaseRequireItem().getQuantity() == null ? BigDecimal.ZERO : item.getPurchaseRequireItem().getQuantity();
                        if (one.add(two).compareTo(three) > 0) {
                            if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(settingClient.getNoticeTypePurRequireToOrderExcess())) {
                                return EmsResultEntity.warning(new ArrayList<>(), "“新本次下单量(变更中)”与“已下单量(不含本单）”之和，已超过单据量，是否继续更新？");
                            }
                            else if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(settingClient.getNoticeTypePurRequireToOrderExcess())) {
                                return EmsResultEntity.error(new ArrayList<>(), "“新本次下单量(变更中)”与“已下单量(不含本单）”之和，已超过单据量，无法更新！");
                            }
                        }
                    }
                }
            }
            row = purPurchaseOrderDataSourceMapper.updatesAllById(dataSourceList);
            if (row > 0) {
                BigDecimal quantitySum = dataSourceList.stream().map(o-> o.getNewQuantity() == null ? o.getQuantity() : o.getNewQuantity()).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                return EmsResultEntity.success(quantitySum);
            }
        }
        return EmsResultEntity.success();
    }

    @Override
    public List<OrderProgressItemResponse> sortProgressItem(List<OrderProgressItemResponse> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            List<OrderProgressItemResponse> skuExit = salSalesOrderItemList.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit.forEach(li -> {
                        String skuName = li.getSku2Name();
                        String[] nameSplit = skuName.split("/");
                        if (nameSplit.length == 1) {
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        } else {
                            String[] name2split = nameSplit[1].split("\\(");
                            if (name2split.length == 2) {
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                                li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                            } else {
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                            }
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        }
                    });
                    List<OrderProgressItemResponse> allList = new ArrayList<>();
                    List<OrderProgressItemResponse> allThirdList = new ArrayList<>();
                    List<OrderProgressItemResponse> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<OrderProgressItemResponse> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<OrderProgressItemResponse> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<OrderProgressItemResponse> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
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
     * 新增采购订单-明细
     * 需要注意编码重复校验
     * @param purPurchaseOrderItem 采购订单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseOrderItem(PurPurchaseOrderItem purPurchaseOrderItem) {
        return purPurchaseOrderItemMapper.insert(purPurchaseOrderItem);
    }

    /**
     * 修改采购订单-明细
     *
     * @param purPurchaseOrderItem 采购订单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseOrderItem(PurPurchaseOrderItem purPurchaseOrderItem) {
        return purPurchaseOrderItemMapper.updateById(purPurchaseOrderItem);
    }

    /**
     * 批量删除采购订单-明细
     *
     * @param clientIds 需要删除的采购订单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseOrderItemByIds(List<String> clientIds) {
        return purPurchaseOrderItemMapper.deleteBatchIds(clientIds);
    }

    /**
     * 采购订单明细报表
     */
    @Override
    public List<PurPurchaseOrderItem> getItemList(PurPurchaseOrderItem purPurchaseOrderItem) {
        List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.getItemList(purPurchaseOrderItem);
        return itemList;
    }
    @Override
    public List<PurPurchaseOrderItem> handleIndex(List<PurPurchaseOrderItem> itemList){
        if (CollectionUtil.isEmpty(itemList)) {
            return itemList;
        }
        List<PurPurchaseOrderItem>  purchaseList=itemList.stream().filter(li->!ConstantsEms.PURCHASE_SHIP.equals(li.getDeliveryType())).collect(Collectors.toList());
        itemList=itemList.stream().filter(li-> ConstantsEms.PURCHASE_SHIP.equals(li.getDeliveryType())).collect(Collectors.toList());
        //按交货单
        if(CollectionUtil.isNotEmpty(itemList)){
            List<Long> sidItems = itemList.stream().map(li -> li.getPurchaseOrderSid()).collect(Collectors.toList());
            Long[] sidList = itemList.stream().map(li -> li.getPurchaseOrderItemSid()).toArray(Long[]::new);
            //获取相关联的发货单明细
            List<DelDeliveryNoteItem> delDeliveryAll = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList
                    (new DelDeliveryNoteItem().setPurchaseOrderItemSidList(sidList).setHandleStatus(ConstantsEms.CHECK_STATUS));
            if(CollectionUtil.isNotEmpty(delDeliveryAll)){
                itemList.forEach(item->{
                    List<DelDeliveryNoteItem> deItem = delDeliveryAll.stream().filter(li -> li.getPurchaseOrderItemSid().toString().equals(item.getPurchaseOrderItemSid().toString())).collect(Collectors.toList());
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
            //获取可用库存量
//            getInvQuantily(itemList);
            List<DelDeliveryNote> deliveryNoteList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>().lambda()
                    .in(DelDeliveryNote::getPurchaseOrderSid, sidItems)
                    .in(DelDeliveryNote::getInOutStockStatus,handle)
                    .eq(DelDeliveryNote::getHandleStatus, HandleStatus.CONFIRMED.getCode())

            );
            List<Long> longs = deliveryNoteList.stream().map(li -> li.getDeliveryNoteSid()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(longs)){
                //确认状态下的 未出库的销售发货单
                List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                        .in(DelDeliveryNoteItem::getPurchaseOrderItemSid, sidList)
                        .in(DelDeliveryNoteItem::getDeliveryNoteSid,longs)
                );
                itemList.forEach(item->{
                    List<DelDeliveryNoteItem> deItem = delDeliveryNoteItems.stream().filter(li -> li.getPurchaseOrderItemSid().toString().equals(item.getPurchaseOrderItemSid().toString())).collect(Collectors.toList());
                    BigDecimal sum = deItem.stream().map(li -> li.getDeliveryQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    //已发货未出入库量
                    item.setInWQuantity(sum);
                });
            }
        }
        if(CollectionUtil.isNotEmpty(purchaseList)){
            Long[] longs = purchaseList.stream().map(li -> li.getPurchaseOrderItemSid()).toArray(Long[]::new);
            List<InvInventoryDocumentItem> documentItems = invInventoryDocumentItemMapper.selectInvInventoryDocumentItemList
                    (new InvInventoryDocumentItem().setReferDocumentItemSidList(longs).setHandleStatus(HandleStatus.POSTING.getCode())
                            .setDocumentType(ConstantsInventory.BUSINESS_FLAG_CG));
            if(CollectionUtil.isNotEmpty(documentItems)){
                purchaseList.forEach(li->{
                    List<InvInventoryDocumentItem> items = documentItems.stream().filter(m -> m.getReferDocumentItemSid().toString().equals(li.getPurchaseOrderItemSid().toString())).collect(Collectors.toList());
                    BigDecimal sum = items.stream().map(m -> {
                        if(m.getPriceQuantity()!=null){
                            return m.getPriceQuantity();
                        }else{
                            return BigDecimal.ZERO;
                        }
                    }).reduce(BigDecimal.ZERO,BigDecimal::add);
                    li.setInQuantity(sum);
                    if(sum!=null&&li.getQuantity()!=null){
                        li.setPartQuantity(li.getQuantity().subtract(sum));
                    }
                });
            }
        }
        List<PurPurchaseOrderItem> All = new ArrayList<>();
        All.addAll(purchaseList);
        All.addAll(itemList);
        All=All.stream().sorted(Comparator.comparing(PurPurchaseOrderItem::getPurchaseOrderCode).reversed()).collect(Collectors.toList());
        All.forEach(li->{
            if(li.getPartQuantity()==null){
                li.setPartQuantity(li.getQuantity());
            }
            if(li.getQuantity()!=null&&li.getPurchasePriceTax()!=null){
                li.setPriceTax((li.getQuantity().multiply(li.getPurchasePriceTax())).divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
            }
            if(li.getQuantity()!=null&&li.getPurchasePrice()!=null){
                li.setPrice((li.getQuantity().multiply(li.getPurchasePrice())).divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
            }
            if(ConstantsEms.NO.equals(li.getFreeFlag())){
                li.setFreeFlag(null);
            }
        });
        // 重新排序
        try {
            All = All.stream().sorted(Comparator.comparing(PurPurchaseOrderItem::getPurchaseOrderCode, Comparator.nullsLast(String::compareTo)
                            .thenComparing(Comparator.comparingLong(Long::parseLong)).reversed())
                    .thenComparing(PurPurchaseOrderItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(PurPurchaseOrderItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                    .thenComparing(PurPurchaseOrderItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(PurPurchaseOrderItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                    .thenComparing(PurPurchaseOrderItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(PurPurchaseOrderItem::getContractDate, Comparator.nullsLast(Date::compareTo))).collect(toList());
        } catch (Exception e) {
            log.error("订单明细报表计算数值后重新排序后报错");
        }
        return All;
    }

    @Override
    public PurPurchaseOrder handleIndexDelievery(PurPurchaseOrder purPurchaseOrder){
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrder.getPurPurchaseOrderItemList();
        purPurchaseOrderItemList.forEach(item-> {
                    item.setInQuantity(BigDecimal.ZERO)
                            .setInWQuantity(BigDecimal.ZERO);
                    List<DelDeliveryNoteItem> delDeliveryAll = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                            .in(DelDeliveryNoteItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid()));
                    if (CollectionUtil.isNotEmpty(delDeliveryAll)) {
                        List<DelDeliveryNoteItem> InOutStockQuantityList = delDeliveryAll.stream().filter(li -> li.getInOutStockQuantity() != null).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(InOutStockQuantityList)) {
                            //已出库量
                            BigDecimal sum = InOutStockQuantityList.stream().map(li -> li.getInOutStockQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            item.setInQuantity(sum);
                        }
                        List<DelDeliveryNote> deliveryNoteList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>().lambda()
                                .eq(DelDeliveryNote::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid())
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
        purPurchaseOrder.setPurPurchaseOrderItemList(purPurchaseOrderItemList);
        return purPurchaseOrder;
    }
    /**
     * 获取可用库存量
     *
     */
    public void getInvQuantily(List<PurPurchaseOrderItem> items){
        if(CollectionUtil.isNotEmpty(items)){
            items.forEach(li->{
                List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>()
                        .lambda()
                        .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                );
                if(CollectionUtil.isNotEmpty(invInventoryLocations)){
                    BigDecimal sum=BigDecimal.ZERO;
                    if(ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(li.getSpecialBusCategory())){
                        sum = invInventoryLocations.stream().map(m -> m.getVendorConsignQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }else{
                        sum = invInventoryLocations.stream().map(m -> m.getUnlimitedQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    li.setInvQuantity(sum);
                }
            });
        }
    }
}
