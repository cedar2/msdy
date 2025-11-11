package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsObject;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.service.IBasMaterialUpdateService;
import com.platform.ems.util.MongodbUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物料商品更新其它表单
 * @author chenkw
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class BasMaterialUpdateServiceImpl implements IBasMaterialUpdateService {

    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private BasMaterialPackageItemMapper materialPackageItemMapper;
    @Autowired
    private TecBomItemMapper bomItemMapper;
    @Autowired
    private PurPurchasePriceMapper purchasePriceMapper;
    @Autowired
    private PurPurchasePriceItemMapper purchasePriceItemMapper;
    @Autowired
    private SalSalePriceMapper salePriceMapper;
    @Autowired
    private SalSalePriceItemMapper salePriceItemMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purchaseOrderItemMapper;
    @Autowired
    private SalSalesOrderItemMapper saleOrderItemMapper;
    @Autowired
    private SalSalesIntentOrderItemMapper saleIntentOrderItemMapper;
    @Autowired
    private DelDeliveryNoteItemMapper deliveryNoteItemMapper;
    @Autowired
    private ManManufactureOrderMapper manufactureOrderMapper;
    @Autowired
    private ManManufactureOrderProductMapper manufactureOrderProductMapper;
    @Autowired
    private ManDayManufactureProgressItemMapper dayManufactureProgressItemMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private InvCusSpecialInventoryMapper cusSpecialInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper venSpecialInventoryMapper;
    @Autowired
    private InvIntransitInventoryMapper intransitInventoryMapper;
    @Autowired
    private InvReserveInventoryMapper reserveInventoryMapper;
    @Autowired
    private InvInventoryDocumentItemMapper inventoryDocumentItemMapper;
    @Autowired
    private InvInventoryTransferItemMapper inventoryTransferItemMapper;
    @Autowired
    private InvInventorySheetItemMapper inventorySheetItemMapper;
    @Autowired
    private InvOwnerMaterialSettleItemMapper ownerMaterialSettleItemMapper;
    @Autowired
    private InvMaterialRequisitionItemMapper materialRequisitionItemMapper;

    final String materialKey = "materialSid";

    /**
     * 修改其它单据同步物料商品档案
     *
     * @param map 物料&商品&服务档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFromMaterial(HashMap<String, String> map) {
        if (map.get(materialKey) == null) {
            throw new BaseException("请选择行！");
        }
        Long materialSid = Long.valueOf(map.get(materialKey));
        BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
        if (basMaterial == null) {
            throw new BaseException("数据丢失，请重新查询列表");
        }
        // 得到基本计量单位的键值
        List<ConMeasureUnit> unitList = conMeasureUnitMapper.selectList(new QueryWrapper<>());
        Map<String, String> unitMap = unitList.stream().collect(HashMap::new, (k, v) -> k.put(v.getCode(), v.getName()),HashMap::putAll);
        String unitBase = basMaterial.getUnitBase();
        // 勾选物料包
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_MATERIAL_PACKAGE))) {
            materialPackage(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选bom
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_BOM))) {
            bom(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选采购价
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_PURCHASE_PRICE))) {
            purchasePrice(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选销售价
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_SALE_PRICE))) {
            salePrice(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选采购订单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_PURCHASE_ORDER))) {
            purchaseOrder(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选销售订单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_SALE_ORDER))) {
            salesOrder(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选销售意向单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_SALE_INTENT_ORDER))) {
            salesIntentOrder(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选采购交货单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_DEL_NOTE_CG))) {
            deliveryNote(materialSid, basMaterial.getMaterialCode(), unitBase, "PD", "采购交货单", unitMap);
        }
        // 勾选销售发货单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_DEL_NOTE_XS))) {
            deliveryNote(materialSid, basMaterial.getMaterialCode(), unitBase, "SD", "销售发货单", unitMap);
        }
        // 勾选生产订单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_MAN_ORDER))) {
            manufactureOrder(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选班组生产日报
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_MAN_DAY_PROG))) {
            dayManufactureProgress(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选库存
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_INVENTORY))) {
            inventory(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选库存凭证
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_INVENTORY_DOCUMENT))) {
            inventoryDocument(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选调拨
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_INVENTORY_TRANSFER))) {
            inventoryTransfer(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选盘点
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_INVENTORY_SHEET))) {
            inventorySheet(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选甲供料结算单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_INV_OWNER_MAT_SET))) {
            inventoryOwnManSettle(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        // 勾选领退料单
        if (ConstantsEms.YES.equals(map.get(ConstantsObject.DATA_OBJECT_INV_MAT_REQUISITION))) {
            invManRequisition(materialSid, basMaterial.getMaterialCode(), unitBase, unitMap);
        }
        return 1;
    }

    private String unitBaseRemark(String one, String two, Map<String, String> unitMap, String code) {
        String oldData = StrUtil.isBlank(one) ? "" : unitMap.getOrDefault(one, one);
        String newData = StrUtil.isBlank(two) ? "" : unitMap.getOrDefault(two, two);
        code = code == null ? "" : code;
        return  "更新物料/商品" + code +  "“基本计量单位”字段值（变更前：" + oldData + "，变更后：" + newData + "）";
    }

    private void bom(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<TecBomItem> bomItemList = bomItemMapper.selectList(new QueryWrapper<TecBomItem>().lambda()
                .eq(TecBomItem::getBomMaterialSid, materialSid)
                .ne(TecBomItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(bomItemList)) {
            List<Long> bomItemSidList = bomItemList.stream().map(TecBomItem::getBomItemSid).collect(Collectors.toList());
            bomItemMapper.update(null, new UpdateWrapper<TecBomItem>().lambda()
                    .in(TecBomItem::getBomItemSid, bomItemSidList)
                    .set(TecBomItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (TecBomItem item : bomItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getBomItemSid())) {
                    map.put(item.getBomItemSid(), 1);
                    MongodbUtil.insertUserLog(item.getBomSid(), BusinessType.QITA.getValue(), null, "bom", remark);
                }
                MongodbUtil.insertUserLog(item.getBomItemSid(), BusinessType.QITA.getValue(), null, "bom明细", remark);
            }
        }
    }

    private void materialPackage(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<BasMaterialPackageItem> packageItemList = materialPackageItemMapper.selectList(new QueryWrapper<BasMaterialPackageItem>().lambda()
                .eq(BasMaterialPackageItem::getMaterialSid, materialSid)
                .ne(BasMaterialPackageItem::getUnit, unitBase));
        if (CollectionUtil.isNotEmpty(packageItemList)) {
            List<Long> packageItemSidList = packageItemList.stream().map(BasMaterialPackageItem::getMaterialPackItemSid).collect(Collectors.toList());
            materialPackageItemMapper.update(null, new UpdateWrapper<BasMaterialPackageItem>().lambda()
                    .in(BasMaterialPackageItem::getMaterialPackItemSid, packageItemSidList)
                    .set(BasMaterialPackageItem::getUnit, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (BasMaterialPackageItem item : packageItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getMaterialPackageSid())) {
                    map.put(item.getMaterialPackageSid(), 1);
                    MongodbUtil.insertUserLog(item.getMaterialPackageSid(), BusinessType.QITA.getValue(), null, "物料包", remark);
                }
                MongodbUtil.insertUserLog(item.getMaterialPackItemSid(), BusinessType.QITA.getValue(), null, "物料包明细", remark);
            }
        }
    }

    private void purchasePrice(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<PurPurchasePrice> priceList = purchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                .eq(PurPurchasePrice::getMaterialSid, materialSid));
        if (CollectionUtil.isNotEmpty(priceList)) {
            List<Long> priceSidList = priceList.stream().map(PurPurchasePrice::getPurchasePriceSid).collect(Collectors.toList());
            // 查明细
            List<PurPurchasePriceItem> priceItemList = purchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>().lambda()
                    .in(PurPurchasePriceItem::getPurchasePriceSid, priceSidList)
                    .ne(PurPurchasePriceItem::getUnitBase, unitBase));
            if (CollectionUtil.isNotEmpty(priceItemList)) {
                List<Long> priceItemSidList = priceItemList.stream().map(PurPurchasePriceItem::getPurchasePriceItemSid).collect(Collectors.toList());
                purchasePriceItemMapper.update(null, new UpdateWrapper<PurPurchasePriceItem>().lambda()
                        .in(PurPurchasePriceItem::getPurchasePriceItemSid, priceItemSidList)
                        .set(PurPurchasePriceItem::getUnitBase, unitBase));
                HashMap<Long, Integer> map = new HashMap<>();
                // 操作日志
                for (PurPurchasePriceItem item : priceItemList) {
                    String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                    if (!map.containsKey(item.getPurchasePriceSid())) {
                        map.put(item.getPurchasePriceSid(), 1);
                        MongodbUtil.insertUserLog(item.getPurchasePriceSid(), BusinessType.QITA.getValue(), null, "采购价明细", remark);
                    }
                    MongodbUtil.insertUserLog(item.getPurchasePriceItemSid(), BusinessType.QITA.getValue(), null, "采购价明细", remark);
                }
            }
        }
    }

    private void salePrice(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<SalSalePrice> priceList = salePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                .eq(SalSalePrice::getMaterialSid, materialSid));
        if (CollectionUtil.isNotEmpty(priceList)) {
            List<Long> priceSidList = priceList.stream().map(SalSalePrice::getSalePriceSid).collect(Collectors.toList());
            // 查明细
            List<SalSalePriceItem> priceItemList = salePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                    .in(SalSalePriceItem::getSalePriceSid, priceSidList)
                    .ne(SalSalePriceItem::getUnitBase, unitBase));
            if (CollectionUtil.isNotEmpty(priceItemList)) {
                List<Long> priceItemSidList = priceItemList.stream().map(SalSalePriceItem::getSalePriceItemSid).collect(Collectors.toList());
                salePriceItemMapper.update(null, new UpdateWrapper<SalSalePriceItem>().lambda()
                        .in(SalSalePriceItem::getSalePriceItemSid, priceItemSidList)
                        .set(SalSalePriceItem::getUnitBase, unitBase));
                HashMap<Long, Integer> map = new HashMap<>();
                // 操作日志
                for (SalSalePriceItem item : priceItemList) {
                    String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                    if (!map.containsKey(item.getSalePriceSid())) {
                        map.put(item.getSalePriceSid(), 1);
                        MongodbUtil.insertUserLog(item.getSalePriceSid(), BusinessType.QITA.getValue(), null, "销售价", remark);
                    }
                    MongodbUtil.insertUserLog(item.getSalePriceItemSid(), BusinessType.QITA.getValue(), null, "销售价明细", remark);
                }
            }
        }
    }

    private void purchaseOrder(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<PurPurchaseOrderItem> orderItemList = purchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                .eq(PurPurchaseOrderItem::getMaterialSid, materialSid)
                .ne(PurPurchaseOrderItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> orderItemSidList = orderItemList.stream().map(PurPurchaseOrderItem::getPurchaseOrderItemSid).collect(Collectors.toList());
            purchaseOrderItemMapper.update(null, new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                    .in(PurPurchaseOrderItem::getPurchaseOrderItemSid, orderItemSidList)
                    .set(PurPurchaseOrderItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (PurPurchaseOrderItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getPurchaseOrderSid())) {
                    map.put(item.getPurchaseOrderSid(), 1);
                    MongodbUtil.insertUserLog(item.getPurchaseOrderSid(), BusinessType.QITA.getValue(), null, "采购订单", remark);
                }
                MongodbUtil.insertUserLog(item.getPurchaseOrderItemSid(), BusinessType.QITA.getValue(), null, "采购订单明细", remark);
            }
        }
    }

    private void salesOrder(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<SalSalesOrderItem> orderItemList = saleOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                .eq(SalSalesOrderItem::getMaterialSid, materialSid)
                .ne(SalSalesOrderItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> orderItemSidList = orderItemList.stream().map(SalSalesOrderItem::getSalesOrderItemSid).collect(Collectors.toList());
            saleOrderItemMapper.update(null, new UpdateWrapper<SalSalesOrderItem>().lambda()
                    .in(SalSalesOrderItem::getSalesOrderItemSid, orderItemSidList)
                    .set(SalSalesOrderItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (SalSalesOrderItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getSalesOrderSid())) {
                    map.put(item.getSalesOrderSid(), 1);
                    MongodbUtil.insertUserLog(item.getSalesOrderSid(), BusinessType.QITA.getValue(), null, "销售订单", remark);
                }
                MongodbUtil.insertUserLog(item.getSalesOrderItemSid(), BusinessType.QITA.getValue(), null, "销售订单明细", remark);
            }
        }
    }

    private void salesIntentOrder(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<SalSalesIntentOrderItem> orderItemList = saleIntentOrderItemMapper.selectList(new QueryWrapper<SalSalesIntentOrderItem>().lambda()
                .eq(SalSalesIntentOrderItem::getMaterialSid, materialSid)
                .ne(SalSalesIntentOrderItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> orderItemSidList = orderItemList.stream().map(SalSalesIntentOrderItem::getSalesIntentOrderItemSid).collect(Collectors.toList());
            saleIntentOrderItemMapper.update(null, new UpdateWrapper<SalSalesIntentOrderItem>().lambda()
                    .in(SalSalesIntentOrderItem::getSalesIntentOrderItemSid, orderItemSidList)
                    .set(SalSalesIntentOrderItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (SalSalesIntentOrderItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getSalesIntentOrderSid())) {
                    map.put(item.getSalesIntentOrderSid(), 1);
                    MongodbUtil.insertUserLog(item.getSalesIntentOrderSid(), BusinessType.QITA.getValue(), null, "销售意向单", remark);
                }
                MongodbUtil.insertUserLog(item.getSalesIntentOrderItemSid(), BusinessType.QITA.getValue(), null, "销售意向单明细", remark);
            }
        }
    }

    private void deliveryNote(Long materialSid, String materialCode, String unitBase, String deliveryCategory, String name, Map<String, String> unitMap) {
        List<DelDeliveryNoteItem> orderItemList = deliveryNoteItemMapper.selectDelDeliveryNoteItemList(new DelDeliveryNoteItem()
                .setMaterialSid(materialSid).setDeliveryCategory(deliveryCategory));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            if (unitBase == null) {
                orderItemList = orderItemList.stream().filter(o-> o.getUnitBase() != null).collect(Collectors.toList());
            }
            else {
                orderItemList = orderItemList.stream().filter(o-> !unitBase.equals(o.getUnitBase())).collect(Collectors.toList());
            }
        }
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> noteItemSidList = orderItemList.stream().map(DelDeliveryNoteItem::getDeliveryNoteItemSid).collect(Collectors.toList());
            deliveryNoteItemMapper.update(null, new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                    .in(DelDeliveryNoteItem::getDeliveryNoteItemSid, noteItemSidList)
                    .set(DelDeliveryNoteItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (DelDeliveryNoteItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getDeliveryNoteSid())) {
                    map.put(item.getDeliveryNoteSid(), 1);
                    MongodbUtil.insertUserLog(item.getDeliveryNoteSid(), BusinessType.QITA.getValue(), null, name, remark);
                }
                MongodbUtil.insertUserLog(item.getDeliveryNoteItemSid(), BusinessType.QITA.getValue(), null, name + "明细", remark);
            }
        }
    }

    private void manufactureOrder(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        HashMap<Long, Integer> map = new HashMap<>();
        // 主表
        List<ManManufactureOrder> orderList = manufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>().lambda()
                .eq(ManManufactureOrder::getMaterialSid, materialSid)
                .ne(ManManufactureOrder::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderList)) {
            List<Long> orderSidList = orderList.stream().map(ManManufactureOrder::getManufactureOrderSid).collect(Collectors.toList());
            manufactureOrderMapper.update(null, new UpdateWrapper<ManManufactureOrder>().lambda()
                    .in(ManManufactureOrder::getManufactureOrderSid, orderSidList)
                    .set(ManManufactureOrder::getUnitBase, unitBase));
            // 操作日志
            for (ManManufactureOrder item : orderList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getManufactureOrderSid())) {
                    map.put(item.getManufactureOrderSid(), 1);
                    MongodbUtil.insertUserLog(item.getManufactureOrderSid(), BusinessType.QITA.getValue(), null, "生产订单", remark);
                }
            }
        }
        // 明细表
        List<ManManufactureOrderProduct> orderItemList = manufactureOrderProductMapper.selectList(new QueryWrapper<ManManufactureOrderProduct>().lambda()
                .eq(ManManufactureOrderProduct::getMaterialSid, materialSid)
                .ne(ManManufactureOrderProduct::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> orderItemSidList = orderItemList.stream().map(ManManufactureOrderProduct::getManufactureOrderProductSid).collect(Collectors.toList());
            manufactureOrderProductMapper.update(null, new UpdateWrapper<ManManufactureOrderProduct>().lambda()
                    .in(ManManufactureOrderProduct::getManufactureOrderProductSid, orderItemSidList)
                    .set(ManManufactureOrderProduct::getUnitBase, unitBase));
            // 操作日志
            for (ManManufactureOrderProduct item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getManufactureOrderSid())) {
                    map.put(item.getManufactureOrderSid(), 1);
                    MongodbUtil.insertUserLog(item.getManufactureOrderSid(), BusinessType.QITA.getValue(), null, "生产订单", remark);
                }
                MongodbUtil.insertUserLog(item.getManufactureOrderProductSid(), BusinessType.QITA.getValue(), null, "生产订单产品明细", remark);
            }
        }
    }

    private void dayManufactureProgress(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<ManDayManufactureProgressItem> orderItemList = dayManufactureProgressItemMapper.selectList(new QueryWrapper<ManDayManufactureProgressItem>().lambda()
                .eq(ManDayManufactureProgressItem::getMaterialSid, materialSid)
                .ne(ManDayManufactureProgressItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> progressItemSidList = orderItemList.stream().map(ManDayManufactureProgressItem::getDayManufactureProgressItemSid).collect(Collectors.toList());
            dayManufactureProgressItemMapper.update(null, new UpdateWrapper<ManDayManufactureProgressItem>().lambda()
                    .in(ManDayManufactureProgressItem::getDayManufactureProgressItemSid, progressItemSidList)
                    .set(ManDayManufactureProgressItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (ManDayManufactureProgressItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getDayManufactureProgressSid())) {
                    map.put(item.getDayManufactureProgressSid(), 1);
                    MongodbUtil.insertUserLog(item.getDayManufactureProgressSid(), BusinessType.QITA.getValue(), null, "班组生产日报", remark);
                }
                MongodbUtil.insertUserLog(item.getDayManufactureProgressItemSid(), BusinessType.QITA.getValue(), null, "班组生产日报明细", remark);
            }
        }
    }

    private void inventory(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        // s_inv_inventory_location
        List<InvInventoryLocation> locateList = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                .eq(InvInventoryLocation::getMaterialSid, materialSid)
                .ne(InvInventoryLocation::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(locateList)) {
            List<Long> locateSidList = locateList.stream().map(InvInventoryLocation::getLocationStockSid).collect(Collectors.toList());
            invInventoryLocationMapper.update(null, new UpdateWrapper<InvInventoryLocation>().lambda()
                    .in(InvInventoryLocation::getLocationStockSid, locateSidList)
                    .set(InvInventoryLocation::getUnitBase, unitBase));
            // 操作日志
            for (InvInventoryLocation item : locateList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                MongodbUtil.insertUserLog(item.getLocationStockSid(), BusinessType.QITA.getValue(), null, "仓库库位库存", remark);
            }
        }
        // s_inv_cus_special_inventory
        List<InvCusSpecialInventory> cusSpecialList = cusSpecialInventoryMapper.selectList(new QueryWrapper<InvCusSpecialInventory>().lambda()
                .eq(InvCusSpecialInventory::getMaterialSid, materialSid)
                .ne(InvCusSpecialInventory::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(cusSpecialList)) {
            List<Long> cusSpecialSidList = cusSpecialList.stream().map(InvCusSpecialInventory::getCustomerSpecialStockSid).collect(Collectors.toList());
            cusSpecialInventoryMapper.update(null, new UpdateWrapper<InvCusSpecialInventory>().lambda()
                    .in(InvCusSpecialInventory::getCustomerSpecialStockSid, cusSpecialSidList)
                    .set(InvCusSpecialInventory::getUnitBase, unitBase));
            // 操作日志
            for (InvCusSpecialInventory item : cusSpecialList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                MongodbUtil.insertUserLog(item.getCustomerSpecialStockSid(), BusinessType.QITA.getValue(), null, "客户特殊库存", remark);
            }
        }
        // s_inv_ven_special_inventory
        List<InvVenSpecialInventory> venSpecialList = venSpecialInventoryMapper.selectList(new QueryWrapper<InvVenSpecialInventory>().lambda()
                .eq(InvVenSpecialInventory::getMaterialSid, materialSid)
                .ne(InvVenSpecialInventory::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(venSpecialList)) {
            List<Long> venSpecialSidList = venSpecialList.stream().map(InvVenSpecialInventory::getVendorSpecialStockSid).collect(Collectors.toList());
            venSpecialInventoryMapper.update(null, new UpdateWrapper<InvVenSpecialInventory>().lambda()
                    .in(InvVenSpecialInventory::getVendorSpecialStockSid, venSpecialSidList)
                    .set(InvVenSpecialInventory::getUnitBase, unitBase));
            // 操作日志
            for (InvVenSpecialInventory item : venSpecialList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                MongodbUtil.insertUserLog(item.getVendorSpecialStockSid(), BusinessType.QITA.getValue(), null, "供应商特殊库存", remark);
            }
        }
        // s_inv_intransit_inventory
        List<InvIntransitInventory> intransitList = intransitInventoryMapper.selectList(new QueryWrapper<InvIntransitInventory>().lambda()
                .eq(InvIntransitInventory::getMaterialSid, materialSid)
                .ne(InvIntransitInventory::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(intransitList)) {
            List<Long> intransitSidList = intransitList.stream().map(InvIntransitInventory::getIntransitStockSid).collect(Collectors.toList());
            intransitInventoryMapper.update(null, new UpdateWrapper<InvIntransitInventory>().lambda()
                    .in(InvIntransitInventory::getIntransitStockSid, intransitSidList)
                    .set(InvIntransitInventory::getUnitBase, unitBase));
            // 操作日志
            for (InvIntransitInventory item : intransitList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                MongodbUtil.insertUserLog(item.getIntransitStockSid(), BusinessType.QITA.getValue(), null, "调拨在途库存", remark);
            }
        }
        // s_inv_reserve_inventory
        List<InvReserveInventory> reserveList = reserveInventoryMapper.selectList(new QueryWrapper<InvReserveInventory>().lambda()
                .eq(InvReserveInventory::getMaterialSid, materialSid)
                .ne(InvReserveInventory::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(reserveList)) {
            List<Long> reserveSidList = reserveList.stream().map(InvReserveInventory::getReserveStockSid).collect(Collectors.toList());
            reserveInventoryMapper.update(null, new UpdateWrapper<InvReserveInventory>().lambda()
                    .in(InvReserveInventory::getReserveStockSid, reserveSidList)
                    .set(InvReserveInventory::getUnitBase, unitBase));
            // 操作日志
            for (InvReserveInventory item : reserveList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                MongodbUtil.insertUserLog(item.getReserveStockSid(), BusinessType.QITA.getValue(), null, "预留库存", remark);
            }
        }
    }

    private void inventoryDocument(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<InvInventoryDocumentItem> orderItemList = inventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                .eq(InvInventoryDocumentItem::getMaterialSid, materialSid)
                .ne(InvInventoryDocumentItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> progressItemSidList = orderItemList.stream().map(InvInventoryDocumentItem::getInventoryDocumentItemSid).collect(Collectors.toList());
            inventoryDocumentItemMapper.update(null, new UpdateWrapper<InvInventoryDocumentItem>().lambda()
                    .in(InvInventoryDocumentItem::getInventoryDocumentItemSid, progressItemSidList)
                    .set(InvInventoryDocumentItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (InvInventoryDocumentItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getInventoryDocumentSid())) {
                    map.put(item.getInventoryDocumentSid(), 1);
                    MongodbUtil.insertUserLog(item.getInventoryDocumentSid(), BusinessType.QITA.getValue(), null, "库存凭证", remark);
                }
                MongodbUtil.insertUserLog(item.getInventoryDocumentItemSid(), BusinessType.QITA.getValue(), null, "库存凭证明细", remark);
            }
        }
    }

    private void inventoryTransfer(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<InvInventoryTransferItem> orderItemList = inventoryTransferItemMapper.selectList(new QueryWrapper<InvInventoryTransferItem>().lambda()
                .eq(InvInventoryTransferItem::getMaterialSid, materialSid)
                .ne(InvInventoryTransferItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> progressItemSidList = orderItemList.stream().map(InvInventoryTransferItem::getInventoryTransferItemSid).collect(Collectors.toList());
            inventoryTransferItemMapper.update(null, new UpdateWrapper<InvInventoryTransferItem>().lambda()
                    .in(InvInventoryTransferItem::getInventoryTransferItemSid, progressItemSidList)
                    .set(InvInventoryTransferItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (InvInventoryTransferItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getInventoryTransferSid())) {
                    map.put(item.getInventoryTransferSid(), 1);
                    MongodbUtil.insertUserLog(item.getInventoryTransferSid(), BusinessType.QITA.getValue(), null, "调拨单", remark);
                }
                MongodbUtil.insertUserLog(item.getInventoryTransferItemSid(), BusinessType.QITA.getValue(), null, "调拨单明细", remark);
            }
        }
    }

    private void inventorySheet(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<InvInventorySheetItem> orderItemList = inventorySheetItemMapper.selectList(new QueryWrapper<InvInventorySheetItem>().lambda()
                .eq(InvInventorySheetItem::getMaterialSid, materialSid)
                .ne(InvInventorySheetItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> progressItemSidList = orderItemList.stream().map(InvInventorySheetItem::getInventorySheetItemSid).collect(Collectors.toList());
            inventorySheetItemMapper.update(null, new UpdateWrapper<InvInventorySheetItem>().lambda()
                    .in(InvInventorySheetItem::getInventorySheetItemSid, progressItemSidList)
                    .set(InvInventorySheetItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (InvInventorySheetItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getInventorySheetSid())) {
                    map.put(item.getInventorySheetSid(), 1);
                    MongodbUtil.insertUserLog(item.getInventorySheetSid(), BusinessType.QITA.getValue(), null, "盘点单", remark);
                }
                MongodbUtil.insertUserLog(item.getInventorySheetItemSid(), BusinessType.QITA.getValue(), null, "盘点单明细", remark);
            }
        }
    }

    private void inventoryOwnManSettle(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<InvOwnerMaterialSettleItem> orderItemList = ownerMaterialSettleItemMapper.selectList(new QueryWrapper<InvOwnerMaterialSettleItem>().lambda()
                .eq(InvOwnerMaterialSettleItem::getMaterialSid, materialSid)
                .ne(InvOwnerMaterialSettleItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> progressItemSidList = orderItemList.stream().map(InvOwnerMaterialSettleItem::getSettleItemSid).collect(Collectors.toList());
            ownerMaterialSettleItemMapper.update(null, new UpdateWrapper<InvOwnerMaterialSettleItem>().lambda()
                    .in(InvOwnerMaterialSettleItem::getSettleItemSid, progressItemSidList)
                    .set(InvOwnerMaterialSettleItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (InvOwnerMaterialSettleItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getSettleSid())) {
                    map.put(item.getSettleSid(), 1);
                    MongodbUtil.insertUserLog(item.getSettleSid(), BusinessType.QITA.getValue(), null, "甲供料结算单", remark);
                }
                MongodbUtil.insertUserLog(item.getSettleItemSid(), BusinessType.QITA.getValue(), null, "甲供料结算单明细", remark);
            }
        }
    }

    private void invManRequisition(Long materialSid, String materialCode, String unitBase, Map<String, String> unitMap) {
        List<InvMaterialRequisitionItem> orderItemList = materialRequisitionItemMapper.selectList(new QueryWrapper<InvMaterialRequisitionItem>().lambda()
                .eq(InvMaterialRequisitionItem::getMaterialSid, materialSid)
                .ne(InvMaterialRequisitionItem::getUnitBase, unitBase));
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            List<Long> progressItemSidList = orderItemList.stream().map(InvMaterialRequisitionItem::getMaterialRequisitionItemSid).collect(Collectors.toList());
            materialRequisitionItemMapper.update(null, new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                    .in(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, progressItemSidList)
                    .set(InvMaterialRequisitionItem::getUnitBase, unitBase));
            HashMap<Long, Integer> map = new HashMap<>();
            // 操作日志
            for (InvMaterialRequisitionItem item : orderItemList) {
                String remark = unitBaseRemark(item.getUnitBase(), unitBase, unitMap, materialCode);
                if (!map.containsKey(item.getMaterialRequisitionSid())) {
                    map.put(item.getMaterialRequisitionSid(), 1);
                    MongodbUtil.insertUserLog(item.getMaterialRequisitionSid(), BusinessType.QITA.getValue(), null, "领退料单", remark);
                }
                MongodbUtil.insertUserLog(item.getMaterialRequisitionItemSid(), BusinessType.QITA.getValue(), null, "领退料单明细", remark);
            }
        }
    }

}
