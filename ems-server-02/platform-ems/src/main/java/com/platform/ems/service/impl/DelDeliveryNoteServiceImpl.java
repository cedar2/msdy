package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.DelDeliveryNoteCreateRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.enums.BusinessType;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConBuTypeDeliveryNote;
import com.platform.ems.plug.domain.ConDocTypeDeliveryNote;
import com.platform.ems.plug.mapper.ConBuTypeDeliveryNoteMapper;
import com.platform.ems.plug.mapper.ConDocTypeDeliveryNoteMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.BarcodeUtils;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.impl.WorkFlowServiceImpl;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.Collator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 交货单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Service
@SuppressWarnings("all")
public class DelDeliveryNoteServiceImpl extends ServiceImpl<DelDeliveryNoteMapper, DelDeliveryNote> implements IDelDeliveryNoteService {
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;
    @Autowired
    private DelDeliveryNoteAttachmentMapper delDeliveryNoteAttachmentMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private DelDeliveryNotePartnerMapper delDeliveryNotePartnerMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private IPurPurchasePriceService purPurchasePriceService;
    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private WorkFlowServiceImpl workFlowService;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private IBasSkuGroupService  basSkuGroupService;
    @Autowired
    private InvReserveInventoryMapper invReserveInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private  BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private InvStorehouseMaterialMapper invStorehouseMaterialMapper;
    @Autowired
    private ConDocTypeDeliveryNoteMapper conDocTypeDeliveryNoteMapper;
    @Autowired
    private ConBuTypeDeliveryNoteMapper conBuTypeDeliveryNoteMapper;

    private static final String TITLE = "交货单";
    private static final String TABLE = "s_del_delivery_note";

    /**
     * 查询交货单
     *
     * @param deliveryNoteSid 交货单ID
     * @return 交货单
     */
    @Override
    public DelDeliveryNote selectDelDeliveryNoteById(Long deliveryNoteSid, String deliveryType) {
        DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectDelDeliveryNoteById(deliveryNoteSid);
        if (delDeliveryNote == null) {
            return null;
        }
        //交货单-明细
        DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
        delDeliveryNoteItem.setDeliveryNoteSid(deliveryNoteSid);
        List<DelDeliveryNoteItem> delDeliveryNoteItemList = new ArrayList<>();
        //采购交货明细
        if (BusinessType.DELIVERY.getCode().equals(delDeliveryNote.getDeliveryCategory())) {
            List<DelDeliveryNoteItem> shipmentsNoteItems = delDeliveryNoteItemMapper.selectShipmentsNoteItemList(delDeliveryNoteItem);
            delDeliveryNoteItemList.addAll(shipmentsNoteItems);
        }
        //销售发货明细
        if (BusinessType.SHIPMENTS.getCode().equals(delDeliveryNote.getDeliveryCategory())) {
            List<DelDeliveryNoteItem> items = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
            //明细页签汇总
            List<DelDeliveryNoteItem> isNUllGroup = items.stream().filter(li -> li.getSku2GroupSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(isNUllGroup)){
                //计算总共金额 总数量
                if(CollectionUtil.isNotEmpty(items)){
                    BigDecimal sumQu = items.stream().map( li->{if(li.getDeliveryQuantity()==null){
                        return BigDecimal.ZERO;
                    }else{
                        return li.getDeliveryQuantity();
                    }}).reduce(BigDecimal.ZERO, BigDecimal::add);
                    delDeliveryNote.setSumQuantity(sumQu);
                }
                List<Long> Sku2GroupSidList = items.stream().map(m -> m.getSku2GroupSid()).distinct().collect(Collectors.toList());
                //明细汇总
                List<SalSalesOrderTotalResponse> itemTotalList = new ArrayList<>();
                List<SalSalesOrderSku2GroupResponse> Sku2GroupList = new ArrayList<>();
                HashMap<Long, List<String>> Sku2GroupHashMap = new HashMap<>();
                //sku数组最大长度
                int max=0;
                for (Long li : Sku2GroupSidList) {
                    BasSkuGroup basSkuGroup = basSkuGroupService.selectBasSkuGroupById(li);
                    List<BasSkuGroupItem> itemList = basSkuGroup.getItemList();
                    List<String> skuList = itemList.stream().map(m -> m.getSkuName()).collect(Collectors.toList());
                    SalSalesOrderSku2GroupResponse sku2 = new SalSalesOrderSku2GroupResponse();
                    sku2.setSku2NameList(skuList)
                            .setSku2GroupName(basSkuGroup.getSkuGroupName());
                    Sku2GroupList.add(sku2);
                    Sku2GroupHashMap.put(li,skuList);
                    int size = skuList.size();
                    max=max>size?max:size;
                }
                int maxSize=max;
                //按包含的sku个数降序
                Sku2GroupList = Sku2GroupList.stream().sorted(Comparator.comparing(li -> li.getSku2NameList().size())).collect(Collectors.toList());
                Sku2GroupList.forEach(li->{
                    int size = li.getSku2NameList().size();
                    for (int i = 0; i <maxSize-size; i++) {
                        li.getSku2NameList().add(null);
                    }
                });
                //分组按商品编码+颜色+
                Map<String, List<DelDeliveryNoteItem>> listMap = items.stream().collect(Collectors.groupingBy(v -> v.getMaterialCode() + "_"  + v.getSku1Sid() ));
                listMap.keySet().stream().forEach(h->{
                    List<DelDeliveryNoteItem> delDeliveryNoteItems = listMap.get(h);
                    Long sku2GroupSid = delDeliveryNoteItems.get(0).getSku2GroupSid();
                    DelDeliveryNoteItem orderItem = delDeliveryNoteItems.get(0);
                    //尺码组
                    ArrayList<SalSalesOrderTotalSku2Response> sku2QuantityList = new ArrayList<>();
                    //获取对应的sku2组的名称-且统一取最长的尺码长度
                    List<String> sku2NameList = Sku2GroupHashMap.get(sku2GroupSid);
                    for (int i = 0; i <maxSize ; i++) {
                        int size = sku2NameList.size();
                        SalSalesOrderTotalSku2Response sku2Quantity = new SalSalesOrderTotalSku2Response();
                        if(i+1<=size){
                            sku2Quantity.setSku2Name(sku2NameList.get(i));
                        }
                        sku2QuantityList.add(sku2Quantity);
                    }
                    //再次分组按尺码
                    Map<String, List<DelDeliveryNoteItem>> itemTempsku2List = delDeliveryNoteItems.stream().collect(Collectors.groupingBy(v -> v.getSku2Name()));
                    itemTempsku2List.keySet().stream().forEach(li->{
                        List<DelDeliveryNoteItem> tems = itemTempsku2List.get(li);
                        BigDecimal quantity = tems.stream().map(m -> m.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        sku2QuantityList.forEach(n->{
                            //匹配尺码对应的 数量
                            if(n.getSku2Name()!=null){
                                if(n.getSku2Name().equals(li)){
                                    n.setSku2Quantity(quantity);
                                }
                            }
                        });
                    });
                    //计算数量小计 总金额
                    BigDecimal sum = delDeliveryNoteItems.stream().map(li -> li.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    SalSalesOrderTotalResponse totalItem = new SalSalesOrderTotalResponse();
                    BeanCopyUtils.copyProperties(orderItem,totalItem);
                    totalItem
                            .setSumQuantity(sum)
                            .setSku2TotalList(sku2QuantityList);
                    itemTotalList.add(totalItem);
                });
                List<SalSalesOrderTotalResponse> sortList = itemTotalList.stream().sorted(Comparator.comparing(SalSalesOrderTotalResponse::getMaterialCode)
                        .thenComparing(SalSalesOrderTotalResponse::getSku1Name)).collect(Collectors.toList());
                delDeliveryNote.setItemTotalList(sortList);
                int size = Sku2GroupList.size();
                if(size<3){
                    for (int i = 0; i <3-size; i++) {
                        SalSalesOrderSku2GroupResponse sku2Group = new SalSalesOrderSku2GroupResponse();
                        List<String> sku2NameExList = new ArrayList<>();
                        for (int n = 0; n < maxSize; n++) {
                            sku2NameExList.add(null);
                        }
                        sku2Group.setSku2NameList(sku2NameExList);
                        Sku2GroupList.add(sku2Group);
                    }
                }
                delDeliveryNote.setSku2GroupList(Sku2GroupList);
            }
            delDeliveryNoteItemList.addAll(items);
        }
        if (CollectionUtil.isNotEmpty(delDeliveryNoteItemList)) {
            delDeliveryNoteItemList = delDeliveryNoteItemList.stream().sorted(
                    Comparator.comparing(DelDeliveryNoteItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(DelDeliveryNoteItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(DelDeliveryNoteItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(DelDeliveryNoteItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(DelDeliveryNoteItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
        }
        //交货单-附件
        DelDeliveryNoteAttachment delDeliveryNoteAttachment = new DelDeliveryNoteAttachment();
        delDeliveryNoteAttachment.setDeliveryNoteSid(deliveryNoteSid);
        List<DelDeliveryNoteAttachment> delDeliveryNoteAttachmentList =
                delDeliveryNoteAttachmentMapper.selectDelDeliveryNoteAttachmentList(delDeliveryNoteAttachment);
        //交货单-合作伙伴
        DelDeliveryNotePartner delDeliveryNotePartner = new DelDeliveryNotePartner();
        delDeliveryNotePartner.setDeliveryNoteSid(deliveryNoteSid);
        List<DelDeliveryNotePartner> delDeliveryNotePartnerList =
                delDeliveryNotePartnerMapper.selectDelDeliveryNotePartnerList(delDeliveryNotePartner);
        delDeliveryNote.setDelDeliveryNoteItemList(delDeliveryNoteItemList);
        delDeliveryNote.setAttachmentList(delDeliveryNoteAttachmentList);
        delDeliveryNote.setDelDeliveryNotePartnerList(delDeliveryNotePartnerList);
        MongodbUtil.find(delDeliveryNote);
        return delDeliveryNote;
    }

    //交货单打印
    @Override
    public DelDeliveryNote getPrint(Long[] sids){
        DelDeliveryNote delDeliveryNote = new DelDeliveryNote();
        delDeliveryNote.setDeliveryNoteSids(sids);
        List<DelDeliveryNote> deliveryNoteList = delDeliveryNoteMapper.selectDelDeliveryNoteList(delDeliveryNote);
        HashSet<Long> set = new HashSet<>();
        DelDeliveryNote note = deliveryNoteList.get(0);
        // 若多选数据，图示字段显示收货地址不为空的那笔数据的字段（若收货地址都为空，随机选择一笔数据进行显示）
        if (sids != null && sids.length > 1) {
            List<DelDeliveryNote> delDeliveryNoteList = deliveryNoteList.stream().filter(o -> o.getConsigneeAddr() != null).collect(toList());
            if (CollectionUtil.isNotEmpty(delDeliveryNoteList)) {
                // 收货人，收货联系电话，收货联系地址，物流公司，收货方说明，备注
                note.setConsigneeAddr(delDeliveryNoteList.get(0).getConsigneeAddr()).setConsigneePhone(delDeliveryNoteList.get(0).getConsigneePhone())
                        .setConsignee(delDeliveryNoteList.get(0).getConsignee()).setContactPartyRemark(delDeliveryNoteList.get(0).getContactPartyRemark())
                        .setRemark(delDeliveryNoteList.get(0).getRemark()).setCarrier(delDeliveryNoteList.get(0).getCarrier())
                        .setCarrierCode(delDeliveryNoteList.get(0).getCarrierCode()).setCarrierName(delDeliveryNoteList.get(0).getCarrierName());
            }
        }
        if (sids != null && sids.length == 1) {
            try {
                String codeBase = null;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BarcodeUtils.generateBarCode128(String.valueOf(note.getDeliveryNoteCode()), 10.0, 0.3, true, false, outputStream);
                // 将流转成数组
                byte[] bytes1 = outputStream.toByteArray();
                String base64Encoded = Base64.getEncoder().encodeToString(bytes1);
                codeBase = "data:image/png;base64," + base64Encoded;
                note.setQrCode(codeBase);
            } catch (Exception e) {
                log.warn("生成条码错误");
            }
        }
        DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
        delDeliveryNoteItem.setDeliveryNoteSids(sids);
        List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
        if(CollectionUtil.isNotEmpty(delDeliveryNoteItems)){
            List<DelDeliveryNoteItem> noteItems = sort(delDeliveryNoteItems, null);
            delDeliveryNoteItems=noteItems.stream().sorted(Comparator.comparing(DelDeliveryNoteItem::getPurchaseOrderCode)
                    .thenComparing(DelDeliveryNoteItem::getItemNum)
            ).collect(Collectors.toList());
        }
        setItemNum(note, delDeliveryNoteItems);
        note.setDelDeliveryNoteItemList(delDeliveryNoteItems);
        return note;
    }

    /**
     * 生成二维码
     */
    @Override
    public List<DelDeliveryNote> getQr(List<DelDeliveryNote> list) {
        list.forEach(li -> {
            String codeBase = QrCodeUtil.generateAsBase64(li.getDeliveryNoteCode().toString(), QrConfig.create().setWidth(80).setHeight(80).setMargin(0), "png");
            li.setQrCode(codeBase);
        });
        return list;
    }

    @Override
    public List<DelDeliveryNoteItem> sort(List<DelDeliveryNoteItem> orderItemList,String type){
        if(CollectionUtil.isNotEmpty(orderItemList)){
            List<DelDeliveryNoteItem> skuExit = orderItemList.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit = skuExit.stream().sorted(Comparator.comparing(DelDeliveryNoteItem::getSku2Name, Comparator.nullsLast(String::compareTo)
                            .thenComparing(Collator.getInstance(Locale.CHINA)))).collect(toList());
                }
            }
            List<DelDeliveryNoteItem> skuExitNo = orderItemList.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            List<DelDeliveryNoteItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                orderItemList=itemArrayListAll.stream().sorted(Comparator.comparing(DelDeliveryNoteItem::getDeliveryNoteCode).reversed()
                        .thenComparing(DelDeliveryNoteItem::getMaterialCode)
                        .thenComparing(DelDeliveryNoteItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                orderItemList=itemArrayListAll.stream().sorted(Comparator.comparing(DelDeliveryNoteItem::getMaterialCode)
                        .thenComparing(DelDeliveryNoteItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return orderItemList;
        }
        return new ArrayList<>();
    }

    /**
     * 查询交货单列表
     *
     * @param delDeliveryNote 交货单
     * @return 交货单
     */
    @Override
    public List<DelDeliveryNote> selectDelDeliveryNoteList(DelDeliveryNote delDeliveryNote) {
        List<DelDeliveryNote> list = delDeliveryNoteMapper.selectDelDeliveryNoteList(delDeliveryNote);
        return list;
    }
    /**
     * 根据单号获取对应的商品编码
     */
    @Override
    public List<String> getMaterialCode(Long code,String type){
        List<String> list = new ArrayList<>();
        if(ConstantsEms.ORDER_TYPE_sale.equals(type)){
            return salSalesOrderMapper.getCode(code);
        }else{
            return purPurchaseOrderMapper.getCode(code);
        }
    }

    //采购交货单-创建销售订单
    @Override
    public SalSalesOrder createSaleOrder(DelDeliveryNoteCreateRequest request){
        List<Long> sidLIst = request.getDeliveryNoteSidList();
        String materialCategory = request.getMaterialCategory();
        List<DelDeliveryNoteItem> delDeliveryNoteItemList=new ArrayList<>();
        List<DelDeliveryNote> delDeliveryList=new ArrayList<>();
        HashSet<Long> storehouseSet = new HashSet<>();
        HashSet<Long> storehouseLocationSet = new HashSet<>();
        HashSet<Long> customerSet = new HashSet<>();
        HashSet<Long> companySet = new HashSet<>();
        HashSet<Long> productSeasonSet = new HashSet<>();
        HashSet<Long> receiverOrgSet = new HashSet<>();
        HashSet<String> shipSet = new HashSet<>();
        sidLIst.forEach(item->{
            DelDeliveryNote delDeliveryNote = selectDelDeliveryNoteById(item, BusinessType.DELIVERY.getCode());
            delDeliveryList.add(delDeliveryNote);
            String shipmentType = delDeliveryNote.getShipmentType();
            if(!ConstantsEms.YES.equals(delDeliveryNote.getIsVirtual())){
                throw new CustomException("库位必须为虚拟库位，请核查！");
            }
            //商品
            if(ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory)){
                if(ConstantsEms.SHIP_TYPR_V.equals(shipmentType)){
                    throw new CustomException("存在配送至加工供应商的数据，请核查");
                }
            }
            //配送到仓
            if(ConstantsEms.SHIP_TYPR_D.equals(shipmentType)){
                throw new CustomException("存在配送到仓的数据，请核查");
            }
            String isDirectTransportFollowup = delDeliveryNote.getIsDirectTransportFollowup();
            if(ConstantsEms.YES.equals(isDirectTransportFollowup)){
                throw new CustomException("存在已处理的直发数据，请核查");
            }
            List<DelDeliveryNoteItem> list = delDeliveryNote.getDelDeliveryNoteItemList();
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(li->{
                    li.setReferDocSid(delDeliveryNote.getDeliveryNoteSid())
                            .setReferDocCode(delDeliveryNote.getDeliveryNoteCode().toString())
                            .setReferDocItemSid(li.getDeliveryNoteItemSid())
                            .setReferDocCategory("DeliveryNote")
                            .setCreateDate(null)
                            .setCreatorAccount(null)
                            .setReferDocItemNum(li.getItemNum());
                });
                delDeliveryNoteItemList.addAll(list);
            }
            //仓库
            storehouseSet.add(delDeliveryNote.getStorehouseSid());
            //库位
            storehouseLocationSet.add(delDeliveryNote.getStorehouseLocationSid());
            companySet.add(delDeliveryNote.getCompanySid());
            productSeasonSet.add(delDeliveryNote.getShipmentsProductSeasonSid());
            shipSet.add(shipmentType);
            if(ConstantsEms.SHIP_TYPR_V.equals(shipmentType)){
                Long receiverOrg = delDeliveryNote.getReceiverOrg();
                if(receiverOrg!=null){
                    BasVendor basVendor = basVendorMapper.selectById(receiverOrg);
                    receiverOrgSet.add(basVendor.getCustomerSid());
                }
            }else if (ConstantsEms.SHIP_TYPR_C.equals(shipmentType)){
                receiverOrgSet.add(delDeliveryNote.getReceiverOrg());
            }
        });
        if(shipSet.size()>1){
            throw new CustomException("存在不一样的配送方式，请核查");
        }
        if(receiverOrgSet.size()>1){
            throw new CustomException("存在不一样的收货方，请核查");
        }
        if(storehouseSet.size()>1||storehouseLocationSet.size()>1){
            throw new CustomException("存在不一样的仓库或库位，请核查");
        }
        if(companySet.size()>1){
            throw new CustomException("存在不一样的公司，请核查");
        }
        if(productSeasonSet.size()>1){
            throw new CustomException("存在不一样的产品季，请核查");
        }
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        DelDeliveryNote delDeliveryNote = delDeliveryList.get(0);
        String shipmentType = delDeliveryNote.getShipmentType();
        if(ConstantsEms.SHIP_TYPR_V.equals(shipmentType)){
            Long receiverOrg = delDeliveryNote.getReceiverOrg();
            if(receiverOrg!=null){
                BasVendor basVendor = basVendorMapper.selectById(receiverOrg);
                delDeliveryNote.setCustomerSid(basVendor.getCustomerSid());
            }
        }else if (ConstantsEms.SHIP_TYPR_C.equals(shipmentType)){
            delDeliveryNote.setCustomerSid(delDeliveryNote.getReceiverOrg());
        }
        String businessType=null;
        //商品
        if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
            businessType="WLZF";
        }
        BeanCopyUtils.copyProperties(delDeliveryNote,salSalesOrder);
        salSalesOrder.setDocumentType(DocCategory.SALE_ORDER.getCode())
                .setBusinessType(businessType)
                .setProductSeasonSid(delDeliveryNote.getShipmentsProductSeasonSid())
                .setSalePerson(ApiThreadLocalUtil.get().getUsername())
                .setDocumentDate(new Date())
                .setMaterialCategory(materialCategory)
                .setRawMaterialMode("WU")
                .setInOutStockStatus(null)
                .setUpdateDate(null)
                .setIsConsignmentSettle(ConstantsEms.NO)
                .setUpdaterAccount(null)
                .setHandleStatus(null)
                .setConfirmDate(null)
                .setConsignee(null)
                .setConsigneeAddr(null)
                .setConsigneePhone(null)
                .setConfirmerAccount(null)
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setCreateDate(null)
                .setSaleMode("CG")
                .setCurrency("CNY")
                .setCurrencyUnit("YUAN");
        List<SalSalesOrderItem> salSalesOrderItems=new ArrayList<>();
        delDeliveryNoteItemList.forEach(item->{
            SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
            BeanCopyUtils.copyProperties(item,salSalesOrderItem);
            salSalesOrderItem.setQuantity(item.getDeliveryQuantity())
                    .setUnitConversionRate(null)
                    .setUnitBase(null)
                    .setUnitBaseName(null)
                    .setUnitPrice(null)
                    .setUnitPriceName(null)
                    .setSalePrice(null)
                    .setSalePriceTax(null)
                    .setCreatorAccountName(null)
                    .setCreatorAccount(null)
                    .setCreateDate(null)
                    .setFreeFlag(ConstantsEms.NO);
            salSalesOrderItems.add(salSalesOrderItem);
        });
        salSalesOrder.setSalSalesOrderItemList(salSalesOrderItems);
        return salSalesOrder;
    }

    //采购交货单-创建常特转移单
    @Override
    public InvInventoryDocument createInvSpec(List<Long> sidList){
        List<DelDeliveryNoteItem> delDeliveryNoteItemList=new ArrayList<>();
        List<DelDeliveryNote> delDeliveryList=new ArrayList<>();
        HashSet<Long> storehouseSet = new HashSet<>();
        HashSet<Long> storehouseLocationSet = new HashSet<>();
        HashSet<Long> customerSet = new HashSet<>();
        HashSet<Long> companySet = new HashSet<>();
        HashSet<Long> productSeasonSet = new HashSet<>();
        HashSet<Long> receiverOrgSet = new HashSet<>();
        List<Long> receiverOrgSids = new ArrayList<>();
        HashSet<String> shipSet = new HashSet<>();
        sidList.forEach(item->{
            DelDeliveryNote delDeliveryNote = selectDelDeliveryNoteById(item, BusinessType.DELIVERY.getCode());
            delDeliveryList.add(delDeliveryNote);
            String shipmentType = delDeliveryNote.getShipmentType();
            if(!ConstantsEms.YES.equals(delDeliveryNote.getIsVirtual())){
                throw new CustomException("库位必须为虚拟库位，请核查！");
            }
            if (!ConstantsEms.SHIP_TYPR_V.equals(shipmentType)) {
                throw new CustomException("存在不是配送至加工供应商的数据，请核查");
            }
            String isDirectTransportFollowup = delDeliveryNote.getIsDirectTransportFollowup();
            if(ConstantsEms.YES.equals(isDirectTransportFollowup)){
                throw new CustomException("存在已处理的直发数据，请核查");
            }
            List<DelDeliveryNoteItem> list = delDeliveryNote.getDelDeliveryNoteItemList();
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(li->{
                    li.setReferDocSid(delDeliveryNote.getDeliveryNoteSid())
                            .setReferDocCode(delDeliveryNote.getDeliveryNoteCode().toString())
                            .setReferDocItemSid(li.getDeliveryNoteItemSid())
                            .setCreateDate(null)
                            .setCreatorAccount(null)
                            .setReferDocItemNum(li.getItemNum());
                });
                delDeliveryNoteItemList.addAll(list);
            }
            //仓库
            storehouseSet.add(delDeliveryNote.getStorehouseSid());
            //库位
            storehouseLocationSet.add(delDeliveryNote.getStorehouseLocationSid());
            companySet.add(delDeliveryNote.getCompanySid());
            productSeasonSet.add(delDeliveryNote.getShipmentsProductSeasonSid());
            shipSet.add(shipmentType);
            receiverOrgSet.add(delDeliveryNote.getReceiverOrg());
            receiverOrgSids.add(delDeliveryNote.getReceiverOrg());
        });
        if(shipSet.size()>1){
            throw new CustomException("存在不一样的配送方式，请核查");
        }
        if(receiverOrgSet.size()>1){
            throw new CustomException("存在不一样的收货方，请核查");
        }
        if(storehouseSet.size()>1||storehouseLocationSet.size()>1){
            throw new CustomException("存在不一样的仓库或库位，请核查");
        }
        DelDeliveryNote delDeliveryNote = delDeliveryList.get(0);
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        BeanCopyUtils.copyProperties(delDeliveryNote,invInventoryDocument);
        invInventoryDocument.setMovementType("ST52")//常规库存转甲供料
                .setDestStorehouseSid(delDeliveryNote.getStorehouseSid())
                .setDestStorehouseName(delDeliveryNote.getStorehouseName())
                .setVendorSid(receiverOrgSids.get(0))
                .setCreatorAccount(null)
                .setHandleStatus(null)
                .setSpecialStock(ConstantsEms.VEN_RA)
                .setCreatorAccountName(null)
                .setIsFinanceBookYszg(null)
                .setIsFinanceBookYfzg(null)
                .setIsReturnGoods(null)
                .setReferDocumentSid(delDeliveryNote.getDeliveryNoteSid())
                .setDocumentDate(new Date())
                .setReferDocumentCode(delDeliveryNote.getDeliveryNoteCode()!=null?delDeliveryNote.getDeliveryNoteCode().toString():null)
                .setDestStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid())
                .setDestLocationName(delDeliveryNote.getLocationName())
                .setType(ConstantsEms.CHU_KU);
        List<InvInventoryDocumentItem> itemList=new ArrayList<>();
        delDeliveryNoteItemList.forEach(item->{
            InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
            BeanCopyUtils.copyProperties(item,invInventoryDocumentItem);
            invInventoryDocumentItem.setQuantity(item.getDeliveryQuantity())
                    .setReferDocumentSid(delDeliveryNote.getDeliveryNoteSid())
                    .setReferDocumentItemSid(item.getDeliveryNoteItemSid())
                    .setReferDocumentCode(delDeliveryNote.getDeliveryNoteCode()!=null?delDeliveryNote.getDeliveryNoteCode().toString():null)
                    .setReferDocCategory("DeliveryNote")
                    .setCreateDate(null)
                    .setCreatorAccount(null)
                    .setCreatorAccountName(null)
                    .setReferDocumentItemNum(item.getItemNum()!=null?item.getItemNum().intValue():null);
            itemList.add(invInventoryDocumentItem);
        });

        invInventoryDocument.setInvInventoryDocumentItemList(itemList);
        return invInventoryDocument;
    }

    /**
     * 新增交货单
     * 需要注意编码重复校验
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelDeliveryNote(DelDeliveryNote delDeliveryNote) {
        ConDocTypeDeliveryNote conDocTypeDeliveryNote = conDocTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConDocTypeDeliveryNote>().lambda()
                .eq(ConDocTypeDeliveryNote::getCode, delDeliveryNote.getDocumentType())
        );
        if(conDocTypeDeliveryNote!=null){
            delDeliveryNote.setDeliveryCategory(conDocTypeDeliveryNote.getDeliveryCategory());
        }
        setConfirmInfo(delDeliveryNote);
        //交货单-明细对象
        List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNote.getDelDeliveryNoteItemList();
        // 校验
        if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
            // map的时候key不能为空，所以在这里处理一下
            List<DelDeliveryNoteItem> nullDocument = delDeliveryNoteItemList.stream().filter(o->o.getDocumentType() == null).collect(toList());
            if (nullDocument != null && nullDocument.size() != 0 && nullDocument.size() != delDeliveryNoteItemList.size()) {
                throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
            }
            List<DelDeliveryNoteItem> nullMode = delDeliveryNoteItemList.stream().filter(o->o.getInventoryControlMode() == null).collect(toList());
            if (nullMode != null && nullMode.size() != 0 && nullMode.size() != delDeliveryNoteItemList.size()) {
                throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
            }
            if (CollectionUtil.isEmpty(nullDocument) && CollectionUtil.isEmpty(nullMode)) {
                Map<String, List<DelDeliveryNoteItem>> map1 = delDeliveryNoteItemList.stream().collect(Collectors.groupingBy(e -> e.getDocumentType()));
                Map<String, List<DelDeliveryNoteItem>> map2 = delDeliveryNoteItemList.stream().collect(Collectors.groupingBy(e -> e.getInventoryControlMode()));
                if ((map1 != null && map1.size() > 1) || map2 != null && map2.size() > 1) {
                    throw new BaseException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
                }
            }
            String mode = delDeliveryNoteItemList.get(0).getSaleMode() == null ? delDeliveryNoteItemList.get(0).getPurchaseMode() : delDeliveryNoteItemList.get(0).getSaleMode();
            delDeliveryNote.setIsReturnGoods(delDeliveryNoteItemList.get(0).getIsReturnGoods())
                    .setIsConsignmentSettle(delDeliveryNoteItemList.get(0).getIsConsignmentSettle())
                    .setInventoryControlMode(delDeliveryNoteItemList.get(0).getInventoryControlMode())
                    .setSalePurchaseMode(mode).setIsFinanceBookYfzg(delDeliveryNoteItemList.get(0).getIsFinanceBookYfzg())
                    .setIsFinanceBookYszg(delDeliveryNoteItemList.get(0).getIsFinanceBookYszg());
        }
        // 若“单据类型”为“销售发货单”，“出入库状态”默认存值“未出库”；若“单据类型”为“销售退货收货单”，“出入库状态”默认存值“未入库”
        if (ConstantsOrder.DEL_ORDER_DOC_TYPE_SDN.equals(delDeliveryNote.getDocumentType())
                ||  ConstantsOrder.DEL_ORDER_DOC_TYPE_RPDN.equals(delDeliveryNote.getDocumentType())) {
            delDeliveryNote.setInOutStockStatus(ConstantsInventory.IN_OUT_STORE_STATUS_WCK);
        }
        if (ConstantsOrder.DEL_ORDER_DOC_TYPE_RSDN.equals(delDeliveryNote.getDocumentType())
                || ConstantsOrder.DEL_ORDER_DOC_TYPE_PDN.equals(delDeliveryNote.getDocumentType())) {
            delDeliveryNote.setInOutStockStatus(ConstantsInventory.IN_OUT_STORE_STATUS_WRK);
        }
        delDeliveryNoteMapper.insert(delDeliveryNote);
        if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
            setItemNum(delDeliveryNote, delDeliveryNoteItemList);
            addDelDeliveryNoteItem(delDeliveryNote, delDeliveryNoteItemList);
        }
        // 提交操作校验
        List<Long> sid = new ArrayList<>();
        sid.add(delDeliveryNote.getDeliveryNoteSid());
        if (ConstantsEms.CHECK_STATUS.equals(delDeliveryNote.getHandleStatus())
                || ConstantsEms.SUBMIT_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            submitInvCheck(delDeliveryNote.getInventoryControlMode(), delDeliveryNote.getIsReturnGoods(),
                    delDeliveryNote.getDocumentType(), delDeliveryNote.getBusinessType(), delDeliveryNote);
        }
        //交货单-附件对象
        List<DelDeliveryNoteAttachment> delDeliveryNoteAttachmentList = delDeliveryNote.getAttachmentList();
        if (CollectionUtils.isNotEmpty(delDeliveryNoteAttachmentList)) {
            addDelDeliveryNoteAttachment(delDeliveryNote, delDeliveryNoteAttachmentList);
        }
        //交货单-合作伙伴对象
        List<DelDeliveryNotePartner> delDeliveryNotePartnerList = delDeliveryNote.getDelDeliveryNotePartnerList();
        if (CollectionUtils.isNotEmpty(delDeliveryNotePartnerList)) {
            addDelDeliveryNotePartner(delDeliveryNote, delDeliveryNotePartnerList);
        }
        //待办通知
        DelDeliveryNote note = delDeliveryNoteMapper.selectById(delDeliveryNote.getDeliveryNoteSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(note.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(TABLE)
                    .setDocumentSid(note.getDeliveryNoteSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                String type=null;
                if (BusinessType.DELIVERY.getCode().equals(delDeliveryNote.getDeliveryCategory())){//采购交货
                    sysTodoTask.setMenuId(ConstantsWorkbench.s_del_delivery_note_p);
                    type="采购交货单";
                }else{
                    sysTodoTask.setMenuId(ConstantsWorkbench.s_del_delivery_note_s);
                    type="销售发货单";
                }
                sysTodoTask.setTitle(type+ note.getDeliveryNoteCode() + "当前是保存状态，请及时处理！")
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        }
        else {
            //校验是否存在待办
            checkTodoExist(note);
        }
        //插入日志
        MongodbUtil.insertUserLog(delDeliveryNote.getDeliveryNoteSid(), com.platform.common.log.enums.BusinessType.INSERT.getValue(),TITLE);
        // 提交即确认
        if (ConstantsEms.CHECK_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            DelDeliveryNote request = new DelDeliveryNote();
            request.setDeliveryNoteSids(sid.toArray(new Long[sid.size()]))
                    .setHandleStatus(delDeliveryNote.getHandleStatus());
            try {
                confirm(request);
            } catch (Exception e) {
                MongodbUtil.removeBySid(delDeliveryNote.getDeliveryNoteSid());
                throw e;
            }
        }
        else if (ConstantsEms.SUBMIT_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            try {
                Submit submit = new Submit();
                List<FormParameter> formParameters = new ArrayList<>();
                FormParameter formParameter = new FormParameter();
                formParameter.setFormCode(String.valueOf(note.getDeliveryNoteCode()));
                formParameter.setFormId(String.valueOf(note.getDeliveryNoteSid()));
                formParameter.setParentId(String.valueOf(note.getDeliveryNoteSid()));
                formParameters.add(formParameter);
                submit.setFormParameters(formParameters);
                submit.setFormType("DeliveryNote");
                submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getSysUser().getUserId()));
                workFlowService.submitByItem(submit);
            } catch (Exception e) {
                MongodbUtil.removeBySid(note.getDeliveryNoteSid());
                throw e;
            }
        }
        return 1;
    }

    /**
     *
     * 外部系统获取交货单
     */
    @Override
    public List<DelDeliveryNoteOutResponse> getOutDelDeliveryNote(Long sid){
        List<DelDeliveryNoteOutResponse>  noteList = delDeliveryNoteMapper.getOutDelDeliveryNoteById(sid);
        List<DelDeliveryNoteOutResponse> list = new ArrayList<>();
        noteList.forEach(item->{
            DelDeliveryNoteOutResponse delDeliveryNote = new DelDeliveryNoteOutResponse();
            BeanCopyUtils.copyProperties(item,delDeliveryNote);
            List<DelDeliveryNoteItemOutResponse> itemList = delDeliveryNoteItemMapper.getOutDelDeliveryNoteItemById(item.getDeliveryNoteSid());
            List<DelDeliveryNoteItemOutResponse> listCopy = BeanCopyUtils.copyListProperties(itemList, DelDeliveryNoteItemOutResponse::new);
            delDeliveryNote.setItemList(listCopy);
            list.add(delDeliveryNote);
        });
        return list;
    }

    /**
     * 行号赋值
     */
    public void  setItemNum(DelDeliveryNote delDeliveryNote, List<DelDeliveryNoteItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(Long.valueOf(i));
                if (ConstantsOrder.DELIVERY_NOTE_CG.equals(delDeliveryNote.getDeliveryCategory())) {
                    list.get(i-1).setPrice(list.get(i-1).getPurchasePrice());
                    list.get(i-1).setPriceTax(list.get(i-1).getPurchasePriceTax());
                } else if (ConstantsOrder.DELIVERY_NOTE_XS.equals(delDeliveryNote.getDeliveryCategory())) {
                    list.get(i-1).setPrice(list.get(i-1).getSalePrice());
                    list.get(i-1).setPriceTax(list.get(i-1).getSalePriceTax());
                }
            }
        }
    }
    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(DelDeliveryNote delDeliveryNote) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, delDeliveryNote.getDeliveryNoteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, delDeliveryNote.getDeliveryNoteSid()));
        }
    }
    /**
     * 设置确认信息
     */
    private void setConfirmInfo(DelDeliveryNote o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())
                || HandleStatus.SUBMIT.getCode().equals(o.getHandleStatus())) {
            if (CollUtil.isEmpty(o.getDelDeliveryNoteItemList())) {
                throw new BaseException("明细列表不存在");
            }
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
                o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                o.setConfirmDate(new Date());
            }
            //校验采购订单
            check(o.getDeliveryCategory(),o.getDelDeliveryNoteItemList());
        }
    }

    /**
     * 交货单-明细对象
     */
    private void addDelDeliveryNoteItem(DelDeliveryNote delDeliveryNote, List<DelDeliveryNoteItem> delDeliveryNoteItemList) {
        delDeliveryNoteItemMapper.delete(
                new UpdateWrapper<DelDeliveryNoteItem>()
                        .lambda()
                        .eq(DelDeliveryNoteItem::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid())
        );
        int i = 1;
        for (DelDeliveryNoteItem item : delDeliveryNoteItemList) {
            if (item.getBarcodeSid() == null) {
                throw new BaseException("编码为" + item.getMaterialCode() + "的商品未生成商品条码，请重新添加！");
            }
            item.setDeliveryNoteSid(delDeliveryNote.getDeliveryNoteSid());
//            o.setCreateDate(new Date());
            delDeliveryNoteItemMapper.insert(item);
        }
//        delDeliveryNoteItemList.forEach(o -> {
//            if (o.getBarcodeSid() == null) {
//                throw new BaseException("编码为" + o.getMaterialCode() + "的商品未生成商品条码，请重新添加！");
//            }
//            o.setDeliveryNoteSid(delDeliveryNote.getDeliveryNoteSid());
////            o.setCreateDate(new Date());
//            delDeliveryNoteItemMapper.insert(o);
//        });
    }

    /**
     * 交货单-附件对象
     */
    private void addDelDeliveryNoteAttachment(DelDeliveryNote delDeliveryNote, List<DelDeliveryNoteAttachment> delDeliveryNoteAttachmentList) {
        delDeliveryNoteAttachmentMapper.delete(
                new UpdateWrapper<DelDeliveryNoteAttachment>()
                        .lambda()
                        .eq(DelDeliveryNoteAttachment::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid())
        );
        delDeliveryNoteAttachmentList.forEach(o -> {
            o.setDeliveryNoteSid(delDeliveryNote.getDeliveryNoteSid());
            delDeliveryNoteAttachmentMapper.insert(o);
        });
    }

    /**
     * 交货单-合作伙伴对象
     */
    private void addDelDeliveryNotePartner(DelDeliveryNote delDeliveryNote, List<DelDeliveryNotePartner> delDeliveryNotePartnerList) {
        delDeliveryNotePartnerMapper.delete(
                new UpdateWrapper<DelDeliveryNotePartner>()
                        .lambda()
                        .eq(DelDeliveryNotePartner::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid())
        );
        delDeliveryNotePartnerList.forEach(o -> {
            o.setDeliveryNoteSid(delDeliveryNote.getDeliveryNoteSid());
            delDeliveryNotePartnerMapper.insert(o);
        });
    }

    /**
     * 撤回保存前的校验
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    @Override
    public int backSaveVerify(DelDeliveryNote delDeliveryNote) {
        int row = 1;
        if (delDeliveryNote.getDeliveryNoteSid() == null) {
            throw new BaseException("请选择单据");
        }
        DelDeliveryNote deliveryNote = delDeliveryNoteMapper.selectById(delDeliveryNote.getDeliveryNoteSid());
        if (deliveryNote != null && ConstantsEms.CHECK_STATUS.equals(deliveryNote.getHandleStatus())) {
            List<InvInventoryDocumentItem> documentItemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>()
                    .lambda().eq(InvInventoryDocumentItem::getReferDocumentSid, delDeliveryNote.getDeliveryNoteSid()));
            if (CollectionUtil.isNotEmpty(documentItemList)) {
                Long[] sids = documentItemList.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).toArray(Long[]::new);
                List<InvInventoryDocument> documentList = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>()
                        .lambda().in(InvInventoryDocument::getInventoryDocumentSid, sids).eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                        .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG));
                if (CollectionUtil.isNotEmpty(documentList)) {
                    throw new BaseException("该销售发货单已存在出入库数据，无法撤回！");
                }
            }
        }
        else {
            throw new BaseException("已确认的销售发货单才可进行此操作！");
        }
        return row;
    }

    /**
     * 撤回保存
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    @Override
    public int backSave(DelDeliveryNote delDeliveryNote) {
        int row = 0;
        if (delDeliveryNote.getDeliveryNoteSid() == null) {
            throw new BaseException("请选择单据");
        }
        if (StrUtil.isBlank(delDeliveryNote.getComment())) {
            throw new CheckedException("撤回说明不能为空");
        }
        this.backSaveVerify(delDeliveryNote);
        row = delDeliveryNoteMapper.update(null, new UpdateWrapper<DelDeliveryNote>().lambda()
                .set(DelDeliveryNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .eq(DelDeliveryNote::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid()));
        // 操作日志
        MongodbUtil.insertUserLog(delDeliveryNote.getDeliveryNoteSid(), com.platform.common.log.enums.BusinessType.QITA.getValue(), null, TITLE, "撤回说明：" + delDeliveryNote.getComment());
        return row;
    }

    /**
     * 维护物流信息
     *
     * @param delDeliveryNote 订单
     * @return 结果
     */
    public int setCarrier(DelDeliveryNote delDeliveryNote) {
        int row = 0;
        if (delDeliveryNote.getDeliveryNoteSid() == null) {
            throw new BaseException("请选择行！");
        }
        if (delDeliveryNote.getCarrier() != null) {
            BasVendor vendor = basVendorMapper.selectById(delDeliveryNote.getCarrier());
            if (vendor != null) {
                delDeliveryNote.setCarrierName(vendor.getVendorName());
            }
        }
        // 修改
        LambdaUpdateWrapper<DelDeliveryNote> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DelDeliveryNote::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid())
                .set(DelDeliveryNote::getCarrier, delDeliveryNote.getCarrier())
                .set(DelDeliveryNote::getCarrierNoteCode, delDeliveryNote.getCarrierNoteCode());
        row = delDeliveryNoteMapper.update(new DelDeliveryNote(), updateWrapper);
        return row;
    }

    /**
     * 修改交货单
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelDeliveryNote(DelDeliveryNote delDeliveryNote) {
        String bussiness=delDeliveryNoteMapper.selectById(delDeliveryNote.getDeliveryNoteSid()).getHandleStatus().equals(ConstantsEms.CHECK_STATUS)?"变更":"编辑";
        setConfirmInfo(delDeliveryNote);
        //交货单-明细对象
        List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNote.getDelDeliveryNoteItemList();
        // 校验
        if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
            // map的时候key不能为空，所以在这里处理一下
            List<DelDeliveryNoteItem> nullDocument = delDeliveryNoteItemList.stream().filter(o->o.getReferDocumentType() == null).collect(toList());
            if (nullDocument != null && nullDocument.size() != 0 && nullDocument.size() != delDeliveryNoteItemList.size()) {
                throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
            }
            List<DelDeliveryNoteItem> nullMode = delDeliveryNoteItemList.stream().filter(o->o.getInventoryControlMode() == null).collect(toList());
            if (nullMode != null && nullMode.size() != 0 && nullMode.size() != delDeliveryNoteItemList.size()) {
                throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
            }
            if (CollectionUtil.isEmpty(nullDocument) && CollectionUtil.isEmpty(nullMode)) {
                Map<String, List<DelDeliveryNoteItem>> map1 = delDeliveryNoteItemList.stream().collect(Collectors.groupingBy(e -> e.getReferDocumentType()));
                Map<String, List<DelDeliveryNoteItem>> map2 = delDeliveryNoteItemList.stream().collect(Collectors.groupingBy(e -> e.getInventoryControlMode()));
                if ((map1 != null && map1.size() > 1) || map2 != null && map2.size() > 1) {
                    throw new BaseException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
                }
            }
            String mode = delDeliveryNoteItemList.get(0).getSaleMode() == null ? delDeliveryNoteItemList.get(0).getPurchaseMode() : delDeliveryNoteItemList.get(0).getSaleMode();
            delDeliveryNote.setIsReturnGoods(delDeliveryNoteItemList.get(0).getIsReturnGoods())
                    .setIsConsignmentSettle(delDeliveryNoteItemList.get(0).getIsConsignmentSettle())
                    .setInventoryControlMode(delDeliveryNoteItemList.get(0).getInventoryControlMode())
                    .setSalePurchaseMode(mode).setIsFinanceBookYfzg(delDeliveryNoteItemList.get(0).getIsFinanceBookYfzg())
                    .setIsFinanceBookYszg(delDeliveryNoteItemList.get(0).getIsFinanceBookYszg());
        }
        else {
            delDeliveryNote.setIsReturnGoods(null)
                    .setIsConsignmentSettle(null)
                    .setInventoryControlMode(null)
                    .setSalePurchaseMode(null).setIsFinanceBookYfzg(null)
                    .setIsFinanceBookYszg(null);
        }
        delDeliveryNoteMapper.updateAllById(delDeliveryNote);
        if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
            List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                    .eq(DelDeliveryNoteItem::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid())
            );
            List<Long> longs = delDeliveryNoteItems.stream().map(li -> li.getDeliveryNoteItemSid()).collect(Collectors.toList());
            List<Long> longsNow = delDeliveryNoteItemList.stream().map(li -> li.getDeliveryNoteItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<DelDeliveryNoteItem> reduceList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                        .in(DelDeliveryNoteItem::getDeliveryNoteItemSid, reduce)
                );
                delDeliveryNoteItemMapper.deleteBatchIds(reduce);
            }
            //修改明细
            List<DelDeliveryNoteItem> exitItem = delDeliveryNoteItemList.stream().filter(li -> li.getDeliveryNoteItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    delDeliveryNoteItemMapper.updateById(li);
                });
            }
            //新增明细
            List<DelDeliveryNoteItem> nullItem = delDeliveryNoteItemList.stream().filter(li -> li.getDeliveryNoteItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                int max = delDeliveryNoteItems.stream().mapToInt(li -> li.getItemNum().intValue()).max().getAsInt();
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem=max+i+1;
                    nullItem.get(i).setItemNum(Long.valueOf(maxItem));
                    nullItem.get(i).setDeliveryNoteSid(delDeliveryNote.getDeliveryNoteSid());
                    if (ConstantsOrder.DELIVERY_NOTE_CG.equals(delDeliveryNote.getDeliveryCategory())) {
                        nullItem.get(i).setPrice(nullItem.get(i).getPurchasePrice());
                        nullItem.get(i).setPriceTax(nullItem.get(i).getPurchasePriceTax());
                    }
                    else if (ConstantsOrder.DELIVERY_NOTE_XS.equals(delDeliveryNote.getDeliveryCategory())) {
                        nullItem.get(i).setPrice(nullItem.get(i).getSalePrice());
                        nullItem.get(i).setPriceTax(nullItem.get(i).getSalePriceTax());
                    }
                    delDeliveryNoteItemMapper.insert(nullItem.get(i));
                }
            }
        }

        // 提交操作校验
        List<Long> sid = new ArrayList<>();
        sid.add(delDeliveryNote.getDeliveryNoteSid());
        if (ConstantsEms.CHECK_STATUS.equals(delDeliveryNote.getHandleStatus())
                || ConstantsEms.SUBMIT_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            submitInvCheck(delDeliveryNote.getInventoryControlMode(), delDeliveryNote.getIsReturnGoods(),
                    delDeliveryNote.getDocumentType(), delDeliveryNote.getBusinessType(), delDeliveryNote);
        }

        //交货单-附件对象
        List<DelDeliveryNoteAttachment> delDeliveryNoteAttachmentList = delDeliveryNote.getAttachmentList();
        if (CollectionUtils.isNotEmpty(delDeliveryNoteAttachmentList)) {
            delDeliveryNoteAttachmentList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addDelDeliveryNoteAttachment(delDeliveryNote, delDeliveryNoteAttachmentList);
        }
        //交货单-合作伙伴对象
        List<DelDeliveryNotePartner> delDeliveryNotePartnerList = delDeliveryNote.getDelDeliveryNotePartnerList();
        if (CollectionUtils.isNotEmpty(delDeliveryNotePartnerList)) {
            delDeliveryNotePartnerList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addDelDeliveryNotePartner(delDeliveryNote, delDeliveryNotePartnerList);
        }
        if (ConstantsEms.CHECK_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, delDeliveryNote.getDeliveryNoteSid()));
        }
        MongodbUtil.insertUserLog(delDeliveryNote.getDeliveryNoteSid(), bussiness,TITLE);
        // 提交即确认
        if (ConstantsEms.CHECK_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            DelDeliveryNote request = new DelDeliveryNote();
            request.setDeliveryNoteSids(sid.toArray(new Long[sid.size()]))
                    .setHandleStatus(delDeliveryNote.getHandleStatus());
            try {
                confirm(request);
            } catch (Exception e) {
                MongodbUtil.find(delDeliveryNote);
                MongodbUtil.remove(new Query().addCriteria(Criteria.where("_id").is(delDeliveryNote.getOperLogList().get(0).getId())));
                throw e;
            }
        }
        else if (ConstantsEms.SUBMIT_STATUS.equals(delDeliveryNote.getHandleStatus())) {
            try {
                Submit submit = new Submit();
                List<FormParameter> formParameters = new ArrayList<>();
                FormParameter formParameter = new FormParameter();
                formParameter.setFormCode(String.valueOf(delDeliveryNote.getDeliveryNoteCode()));
                formParameter.setFormId(String.valueOf(delDeliveryNote.getDeliveryNoteSid()));
                formParameter.setParentId(String.valueOf(delDeliveryNote.getDeliveryNoteSid()));
                formParameters.add(formParameter);
                submit.setFormParameters(formParameters);
                submit.setFormType("DeliveryNote");
                submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getSysUser().getUserId()));
                workFlowService.submitByItem(submit);
            } catch (Exception e) {
                MongodbUtil.find(delDeliveryNote);
                MongodbUtil.remove(new Query().addCriteria(Criteria.where("_id").is(delDeliveryNote.getOperLogList().get(0).getId())));
                throw e;
            }
        }
        return 1;
    }

    /**
     * 校验明细订单的单据类型和库存管理方式有没有一致
     * @param itemList
     */
    private void itemCheck(List<DelDeliveryNoteItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return;
        }
        // map的时候key不能为空，所以在这里处理一下
        List<DelDeliveryNoteItem> nullDocument = itemList.stream().filter(o->o.getReferDocumentType() == null).collect(toList());
        if (nullDocument != null && nullDocument.size() != 0 && nullDocument.size() != itemList.size()) {
            throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
        }
        List<DelDeliveryNoteItem> nullMode = itemList.stream().filter(o->o.getInventoryControlMode() == null).collect(toList());
        if (nullMode != null && nullMode.size() != 0 && nullMode.size() != itemList.size()) {
            throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
        }
        if (CollectionUtil.isEmpty(nullDocument) && CollectionUtil.isEmpty(nullMode)) {
            Map<String, List<DelDeliveryNoteItem>> map1 = itemList.stream().collect(Collectors.groupingBy(e -> e.getReferDocumentType()));
            Map<String, List<DelDeliveryNoteItem>> map2 = itemList.stream().collect(Collectors.groupingBy(e -> e.getInventoryControlMode()));
            if ((map1 != null && map1.size() > 1) || map2 != null && map2.size() > 1) {
                throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
            }
        }
    }

    /**
     * 校验明细可用库存不足
     * @param inventoryControlMode 订单的
     * @param isReturnGoods 订单的
     * @param documentType 订单的
     * @param businessType 发货单交货单本身的业务类型
     * @param itemList
     */
    private void submitInvCheck(String inventoryControlMode, String isReturnGoods,
                                String documentType, String businessType, DelDeliveryNote delDeliveryNote) {
        List<DelDeliveryNoteItem> itemList = delDeliveryNote.getDelDeliveryNoteItemList();
        if (CollectionUtil.isNotEmpty(itemList)) {
            ConBuTypeDeliveryNote conBuTypeDeliveryNote = conBuTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                    .eq(ConBuTypeDeliveryNote::getCode, businessType)
            );
            if(conBuTypeDeliveryNote!=null){
                String chukuCategory = conBuTypeDeliveryNote.getChukuCategory();
                if(!ConstantsEms.CK_CATEGORY.equals(chukuCategory)){
                    Boolean isCheck=false;
                    String returnType=null;
                    if(DocCategory.SALE_RU.getCode().equals(documentType)){
                        isCheck=true;
                        returnType=ConstantsEms.YES;

                    }else if(DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(documentType)){
                        isCheck=true;
                        returnType=ConstantsEms.NO;
                    }
                    if(isCheck){
                        if(!ConstantsEms.INV_CTROL_BGX.equals(inventoryControlMode)&&!returnType.equals(isReturnGoods)){
                            itemList.forEach(o->{
                                o.setDeliveryQuantity(o.getDeliveryQuantity().multiply(o.getUnitConversionRate()));
                            });
                            Map<Long, List<DelDeliveryNoteItem>> listMap = itemList.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
                            listMap.keySet().stream().forEach(l->{
                                List<DelDeliveryNoteItem> items = listMap.get(l);
                                if(items.size()==1){
                                    //商品条码不重复情况下
                                    itemList.forEach(m->{
                                        InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                        invInventoryLocation.setBarcodeSid(m.getBarcodeSid())
                                                .setStorehouseSid(delDeliveryNote.getStorehouseSid())
                                                .setStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid());
                                        InvInventoryLocation location = null;
                                        String saleAndPurchaseDocument = m.getSaleAndPurchaseDocument();
                                        if(DocCategory.PURCHAASE_JI_SHOU.getCode().equals(saleAndPurchaseDocument)){
                                            invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                                                    .setVendorSid(m.getVendorSid());
                                            location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                                        }else{
                                            location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                                        }
                                        if(location==null){
                                            location=new InvInventoryLocation();
                                            location.setAbleQuantity(BigDecimal.ZERO);
                                        }
                                        if(m.getDeliveryQuantity().compareTo(location.getAbleQuantity())==1){
                                            throw  new  CustomException("行号为"+m.getItemNum()+"的明细可用库存不足，无法提交");
                                        }
                                    });
                                }else{
                                    BigDecimal sum = items.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    DelDeliveryNoteItem note = items.get(0);
                                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                    invInventoryLocation.setBarcodeSid(note.getBarcodeSid())
                                            .setStorehouseSid(delDeliveryNote.getStorehouseSid())
                                            .setStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid());
                                    InvInventoryLocation location = null;
                                    String saleAndPurchaseDocument = note.getSaleAndPurchaseDocument();
                                    if(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(saleAndPurchaseDocument)){
                                        invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                                                .setVendorSid(note.getVendorSid());
                                        location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                                    }else{
                                        location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                                    }
                                    if(location==null){
                                        location=new InvInventoryLocation();
                                        location.setAbleQuantity(BigDecimal.ZERO);
                                    }
                                    if(sum.compareTo(location.getAbleQuantity())==1){
                                        BigDecimal comsum=BigDecimal.ZERO;
                                        for (int i = 0; i < items.size(); i++) {
                                            comsum=items.get(i).getDeliveryQuantity().add(comsum);
                                            if(comsum.compareTo(location.getAbleQuantity())==1){
                                                throw  new  CustomException("行号为"+items.get(i).getItemNum()+"的明细可用库存不足，无法提交");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        else {
            throw new CustomException("明细不能为空");
        }
    }


    @Override
    public int processCheck(List<Long> sids){
        sids.forEach(li->{
            DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectById(li);
            String inventoryControlMode = delDeliveryNote.getInventoryControlMode();
            String isReturnGoods = delDeliveryNote.getIsReturnGoods();
            String documentType = delDeliveryNote.getDocumentType();
            String businessType = delDeliveryNote.getBusinessType();

            //交货单-明细对象
            List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(new DelDeliveryNoteItem().setDeliveryNoteSid(li));
            // 校验
            if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
                // map的时候key不能为空，所以在这里处理一下
                List<DelDeliveryNoteItem> nullDocument = delDeliveryNoteItemList.stream().filter(o->o.getReferDocumentType() == null).collect(toList());
                if (nullDocument != null && nullDocument.size() != 0 && nullDocument.size() != delDeliveryNoteItemList.size()) {
                    throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
                }
                List<DelDeliveryNoteItem> nullMode = delDeliveryNoteItemList.stream().filter(o->o.getInventoryControlMode() == null).collect(toList());
                if (nullMode != null && nullMode.size() != 0 && nullMode.size() != delDeliveryNoteItemList.size()) {
                    throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
                }
                if (CollectionUtil.isEmpty(nullDocument) && CollectionUtil.isEmpty(nullMode)) {
                    Map<String, List<DelDeliveryNoteItem>> map1 = delDeliveryNoteItemList.stream().collect(Collectors.groupingBy(e -> e.getReferDocumentType()));
                    Map<String, List<DelDeliveryNoteItem>> map2 = delDeliveryNoteItemList.stream().collect(Collectors.groupingBy(e -> e.getInventoryControlMode()));
                    if ((map1 != null && map1.size() > 1) || map2 != null && map2.size() > 1) {
                        throw new CustomException("“明细信息”页签中，存在明细行的“单据类型”、“库存管理方式”的值不一致，请检查！");
                    }
                }
            }

            ConBuTypeDeliveryNote conBuTypeDeliveryNote = conBuTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                    .eq(ConBuTypeDeliveryNote::getCode, businessType)
            );
            if(conBuTypeDeliveryNote!=null){
                String chukuCategory = conBuTypeDeliveryNote.getChukuCategory();
                if(!ConstantsEms.CK_CATEGORY.equals(chukuCategory)){
                    Boolean isCheck=false;
                    String returnType=null;
                    if(DocCategory.SALE_RU.getCode().equals(documentType)){
                        isCheck=true;
                        // 是否预留库存的校验
                        ConDocTypeDeliveryNote docTypeDeliveryNote = conDocTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConDocTypeDeliveryNote>()
                                .lambda().eq(ConDocTypeDeliveryNote::getCode, documentType));
                        if (docTypeDeliveryNote != null && !ConstantsEms.YES.equals(docTypeDeliveryNote.getIsReserveStock())) {
                            isCheck=false;
                        }
                        returnType=ConstantsEms.YES;

                    }else if(DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(documentType)){
                        isCheck=true;
                        // 是否预留库存的校验
                        ConDocTypeDeliveryNote docTypeDeliveryNote = conDocTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConDocTypeDeliveryNote>()
                                .lambda().eq(ConDocTypeDeliveryNote::getCode, documentType));
                        if (docTypeDeliveryNote != null && !ConstantsEms.YES.equals(docTypeDeliveryNote.getIsReserveStock())) {
                            isCheck=false;
                        }
                        returnType=ConstantsEms.NO;
                    }
                    if(isCheck){
                        if(!ConstantsEms.INV_CTROL_BGX.equals(inventoryControlMode)&&!returnType.equals(isReturnGoods)){
                            DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
                            delDeliveryNoteItem.setDeliveryNoteSid(li);
                            List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
                            delDeliveryNoteItems.forEach(o->{
                                o.setDeliveryQuantity(o.getDeliveryQuantity().multiply(o.getUnitConversionRate()));
                            });
                            Map<Long, List<DelDeliveryNoteItem>> listMap = delDeliveryNoteItems.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
                            listMap.keySet().stream().forEach(l->{
                                List<DelDeliveryNoteItem> items = listMap.get(l);
                                if(items.size()==1){
                                    //商品条码不重复情况下
                                    delDeliveryNoteItems.forEach(m->{
                                        InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                        invInventoryLocation.setBarcodeSid(m.getBarcodeSid())
                                                .setStorehouseSid(delDeliveryNote.getStorehouseSid())
                                                .setStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid());
                                        InvInventoryLocation location = null;
                                        String saleAndPurchaseDocument = m.getSaleAndPurchaseDocument();
                                        if(DocCategory.PURCHAASE_JI_SHOU.getCode().equals(saleAndPurchaseDocument)){
                                            invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                                                    .setVendorSid(m.getVendorSid());
                                            location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                                        }else{
                                            location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                                        }
                                        if(location==null){
                                            location=new InvInventoryLocation();
                                            location.setAbleQuantity(BigDecimal.ZERO);
                                        }
                                        if(m.getDeliveryQuantity().compareTo(location.getAbleQuantity())==1){
                                            throw  new  CustomException("行号为"+m.getItemNum()+"的明细可用库存不足，无法提交");
                                        }
                                    });
                                }else{
                                    BigDecimal sum = items.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                    DelDeliveryNoteItem note = items.get(0);
                                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                    invInventoryLocation.setBarcodeSid(note.getBarcodeSid())
                                            .setStorehouseSid(delDeliveryNote.getStorehouseSid())
                                            .setStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid());
                                    InvInventoryLocation location = null;
                                    String saleAndPurchaseDocument = note.getSaleAndPurchaseDocument();
                                    if(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(saleAndPurchaseDocument)){
                                        invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                                                .setVendorSid(note.getVendorSid());
                                        location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                                    }else{
                                        location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                                    }
                                    if(location==null){
                                        location=new InvInventoryLocation();
                                        location.setAbleQuantity(BigDecimal.ZERO);
                                    }
                                    if(sum.compareTo(location.getAbleQuantity())==1){
                                        BigDecimal comsum=BigDecimal.ZERO;
                                        for (int i = 0; i < items.size(); i++) {
                                            comsum=items.get(i).getDeliveryQuantity().add(comsum);
                                            if(comsum.compareTo(location.getAbleQuantity())==1){
                                                throw  new  CustomException("行号为"+items.get(i).getItemNum()+"的明细可用库存不足，无法提交");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        return 1;
    }

    //出库
    @Override
    public AjaxResult invCK(Long sid){
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectById(sid);
        if (!ConstantsInventory.BU_TYPE_DEL_NOTE_SWC.equals(delDeliveryNote.getBusinessType())) {
            throw new BaseException("勾选的销售发货单不符合出库条件，请核实");
        }
        String inventoryControlMode = delDeliveryNote.getInventoryControlMode();
        if(!ConstantsEms.INV_RESH.equals(inventoryControlMode)){
            CommonErrMsgResponse commonErrMsgResponse = new CommonErrMsgResponse();
            commonErrMsgResponse.setMsg("库存管理方式必须为更新库存，出库失败");
            msgList.add(commonErrMsgResponse);
        }
        Long storehouseSid = delDeliveryNote.getStorehouseLocationSid();
        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectById(storehouseSid);
        String isVirtual = basStorehouseLocation.getIsVirtual();
        if(!ConstantsEms.YES.equals(isVirtual)){
            CommonErrMsgResponse commonErrMsgResponse = new CommonErrMsgResponse();
            commonErrMsgResponse.setMsg("库位必须为虚拟库位，出库失败");
            msgList.add(commonErrMsgResponse);
            return AjaxResult.success("500",msgList);
        }
        DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
        delDeliveryNoteItem.setDeliveryNoteSid(sid);
        List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);

        String isFinanceBookYfzg = delDeliveryNote.getIsFinanceBookYfzg();
        String isFinanceBookYszg = delDeliveryNote.getIsFinanceBookYszg();
        if (ConstantsEms.YES.equals(isFinanceBookYfzg) || ConstantsEms.YES.equals(isFinanceBookYszg)) {
            delDeliveryNoteItems.forEach(li -> {
                if (li.getOrderPriceTax() == null && !ConstantsEms.YES.equals(li.getFreeFlag())) {
                    throw new CustomException("对应销售订单存在销售价为空的明细行，无法出库！");
                }
            });
        }
        // 若所选发货单的“是否生成财务应收暂估流水”为“是”，要校验明细的税率是否为空，若明细的税率为空，提示：
        if (ConstantsEms.YES.equals(delDeliveryNote.getIsFinanceBookYszg())) {
            boolean flag2 = delDeliveryNoteItems.stream().anyMatch(o->o.getOrderTaxRate() == null && !ConstantsEms.YES.equals(o.getOrderFreeFlag()));
            if (flag2) {
                throw new CustomException("对应销售订单存在税率为空的明细行，无法出库！");
            }
        }
        //可用库存校验
        judge(delDeliveryNote,delDeliveryNoteItems,msgList);
        if(CollectionUtil.isNotEmpty(msgList)){
            msgList=msgList.stream().sorted(Comparator.comparing(item -> item.getItemNum())).collect(Collectors.toList());
            return AjaxResult.success("500",msgList);
        }
        //转换成库存凭证
        delDeliveryNote.setDelDeliveryNoteItemList(delDeliveryNoteItems);
        InvInventoryDocument invDocumnet = getInvDocumnet(delDeliveryNote);
        invDocumnet.setType(ConstantsEms.CHU_KU);
        invDocumnet.setDocumentType(null);
        invDocumnet.setReferDocCategory(delDeliveryNote.getDocumentType());
        invDocumnet.setCustomerSid(delDeliveryNote.getCustomerSid());
        invDocumnet.setMovementType(ConstantsEms.SALE_ORDER_RU);//销售发货单
        invInventoryDocumentService.insertInvInventoryDocument(invDocumnet);
        return  AjaxResult.success("200",200);
    }

    //出库 按销售发货单（直发）
    @Override
    public int invCkDirect(Long sid){
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectById(sid);
        // 点击生产直发出库按钮，若所选发货单的业务类型不是“生产直发出库”，提示
        if (!ConstantsInventory.BU_TYPE_DEL_NOTE_SCZF.equals(delDeliveryNote.getBusinessType())) {
            throw new CustomException("勾选的销售发货单不符合生产直发出库条件，请核实");
        }
        Long storehouseSid = delDeliveryNote.getStorehouseLocationSid();
        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectById(storehouseSid);
        String isVirtual = basStorehouseLocation.getIsVirtual();
        String inventoryControlMode = delDeliveryNote.getInventoryControlMode();
        if(!ConstantsEms.INV_RESH.equals(inventoryControlMode)){
            throw new CustomException("库存管理方式必须为更新库存，出库失败");
        }
        if(!ConstantsEms.YES.equals(isVirtual)){
            throw new CustomException("库位必须为虚拟库位，出库失败");
        }
        DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
        delDeliveryNoteItem.setDeliveryNoteSid(sid);
        List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
        // 若所选发货单的“是否生成财务应收暂估流水”为“是”，要校验明细的价格是否为空，若明细的价格为空，提示
        // 若所选发货单的“是否生成财务应收暂估流水”为“是”，要校验明细的税率是否为空，若明细的税率为空，提示：
        if (ConstantsEms.YES.equals(delDeliveryNote.getIsFinanceBookYszg())) {
            boolean flag1 = delDeliveryNoteItems.stream().anyMatch(o->o.getOrderPriceTax() == null && !ConstantsEms.YES.equals(o.getOrderFreeFlag()));
            if (flag1) {
                throw new CustomException("对应销售订单存在销售价为空的明细行，无法出库！");
            }
            boolean flag2 = delDeliveryNoteItems.stream().anyMatch(o->o.getOrderTaxRate() == null && !ConstantsEms.YES.equals(o.getOrderFreeFlag()));
            if (flag2) {
                throw new CustomException("对应销售订单存在税率为空的明细行，无法出库！");
            }
        }
        //转换成库存凭证
        delDeliveryNote.setDelDeliveryNoteItemList(delDeliveryNoteItems);
        InvInventoryDocument invDocumnet = getInvDocumnet(delDeliveryNote);
        invDocumnet.setType(ConstantsEms.CHU_KU);
        invDocumnet.setDocumentType(null);
        invDocumnet.setReferDocCategory(delDeliveryNote.getDocumentType());
        invDocumnet.setCustomerSid(delDeliveryNote.getCustomerSid());
        invDocumnet.setMovementType(ConstantsEms.SALE_ORDER_RU_DIRECT);//销售发货单
        if(DocCategory.SALE_RETURN.getCode().equals(invDocumnet.getSaleAndPurchaseDocument())){
            invInventoryDocumentService.insertInvInventoryDocument(invDocumnet);
        }else{
            delDeliveryNote.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS);
            delDeliveryNoteMapper.updateById(delDeliveryNote);
            delDeliveryNoteItems.forEach(it->{
                it.setInOutStockQuantity(it.getDeliveryQuantity())
                        .setReserveStatus(ConstantsEms.RE_STATUS_WY);
                delDeliveryNoteItemMapper.updateById(it);
            });
            invDocumnet.setAccountDate(new Date());
            invDocumnet.setStorehouseOperator(ApiThreadLocalUtil.get().getUsername());
            invDocumnet.setHandleStatus("B");//过账
            invDocumnet.setDocumentType(ConstantsEms.DOCUMNET_TYPE_ZG);
            int row = invInventoryDocumentMapper.insert(invDocumnet);
            if (row > 0) {
                List<InvInventoryDocumentItem> invInventoryDocumentItemList = invDocumnet.getInvInventoryDocumentItemList();
                if (CollectionUtil.isNotEmpty(invInventoryDocumentItemList)) {
                    setItemNumINV(invInventoryDocumentItemList);
                    invInventoryDocumentItemList.forEach(o -> {
                        o.setReferDocumentCode(invDocumnet.getReferDocumentCode());
                        o.setQuantity(o.getQuantity());
                        o.setReferDocumentSid(invDocumnet.getReferDocumentSid());
                        o.setInventoryDocumentSid(invDocumnet.getInventoryDocumentSid());
                        o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    });
                    invInventoryDocumentItemMapper.inserts(invInventoryDocumentItemList);
                    // 修改仓库物料数据库表
                    UpdateWrapper<InvStorehouseMaterial> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().set(InvStorehouseMaterial::getLatestStockEntryDate, new Date());
                    updateWrapper.lambda().set(InvStorehouseMaterial::getLatestStockOutDate, new Date());
                    updateWrapper.lambda().set(InvStorehouseMaterial::getLatestManufactEntryDate, new Date());
                    updateWrapper.lambda().set(InvStorehouseMaterial::getLatestSaleOutDate, new Date());
                    updateWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : invInventoryDocumentItemList) {
                            warpper.or(o -> {
                                o.eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid()).eq(InvStorehouseMaterial::getStorehouseSid, item.getStorehouseSid());
                            });
                        }
                    });
                    invStorehouseMaterialMapper.update(null, updateWrapper);
                }
                String isBusinessFinance = ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance();
                if(ConstantsEms.YES.equals(isBusinessFinance)){
                    // 生成应付暂估流水
                    invInventoryDocumentService.createpayment(invDocumnet, invInventoryDocumentItemList);
                    // 生成应收暂估流水
                    invInventoryDocumentService.createReceipt(invDocumnet, invInventoryDocumentItemList);
                }
            }
            invInventoryDocumentService.changeStaus(invDocumnet);
            invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                    .eq(InvReserveInventory::getBusinessOrderSid,delDeliveryNote.getDeliveryNoteSid())
            );
        }
        return  1;
    }

    /**
     * 行号赋值
     */
    public void  setItemNumINV(List<InvInventoryDocumentItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                if(list.get(i-1).getItemNum()==null){
                    list.get(i-1).setItemNum(i);
                }
            }
        }
    }

    public InvInventoryDocument getInvDocumnet(DelDeliveryNote delDeliveryNoteNew){
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        BeanCopyUtils.copyProperties(delDeliveryNoteNew, invInventoryDocument);
        if(CollectionUtil.isNotEmpty(delDeliveryNoteNew.getDelDeliveryNoteItemList())
                && delDeliveryNoteNew.getDelDeliveryNoteItemList().get(0).getSalesOrderSid() != null){
            SalSalesOrder salSalesOrder = salSalesOrderMapper.selectById(delDeliveryNoteNew.getDelDeliveryNoteItemList().get(0).getSalesOrderSid());
            if(salSalesOrder!=null){
                invInventoryDocument.setSaleAndPurchaseDocument(salSalesOrder.getDocumentType());
            }
        }
        if(DocCategory.PURCHAASE_JI_SHOU.getCode().equals(invInventoryDocument.getSaleAndPurchaseDocument())
                ||DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(invInventoryDocument.getSaleAndPurchaseDocument())){
            invInventoryDocument.setSpecialStock(ConstantsEms.VEN_CU);
        }
        LocalDate nowDate = LocalDate.now();
        invInventoryDocument.setReferDocumentCode(String.valueOf(delDeliveryNoteNew.getDeliveryNoteCode()));
        invInventoryDocument.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                .setUpdaterAccount(null).setUpdateDate(null)
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date())
                .setDocumentCategory("CK")
                .setAccountDate(new Date()).setDocumentDate(new Date())
                .setYear((long)nowDate.getYear())
                .setMonth((long)nowDate.getMonthValue())
                .setReferDocCategory(DocCategory.SALE_RU.getCode())
                .setReferDocumentSid(delDeliveryNoteNew.getDeliveryNoteSid())
                .setDeliveryNoteSid(delDeliveryNoteNew.getDeliveryNoteSid());
        List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNoteNew.getDelDeliveryNoteItemList();
        if(CollectionUtils.isNotEmpty(delDeliveryNoteItemList)){
            ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
            delDeliveryNoteItemList.forEach(item -> {
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                invInventoryDocumentItem.setStorehouseSid(delDeliveryNoteNew.getStorehouseSid());
                invInventoryDocumentItem.setStorehouseLocationSid(delDeliveryNoteNew.getStorehouseLocationSid());
                invInventoryDocumentItem.setReferDocumentItemNum(Integer.valueOf(item.getItemNum().toString()));
                invInventoryDocumentItem.setPriceQuantity(item.getDeliveryQuantity());
                invInventoryDocumentItem.setBarcode(item.getBarcode());
                invInventoryDocumentItem.setInvPrice(item.getSalePrice());
                invInventoryDocumentItem.setPrice(item.getSalePriceTax());
                invInventoryDocumentItem.setInvPriceTax(item.getSalePriceTax());
                invInventoryDocumentItem.setTaxRate(item.getOrderTaxRate());
                invInventoryDocumentItem.setBusinessQuantity(item.getDeliveryQuantity());
                invInventoryDocumentItem.setReferDocumentSid(item.getDeliveryNoteSid());
                invInventoryDocumentItem.setReferDocumentItemSid(item.getDeliveryNoteItemSid());
                invInventoryDocumentItem.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                        .setUpdaterAccount(null).setUpdateDate(null);
                if (invInventoryDocumentItem.getPriceQuantity() != null) {
                    invInventoryDocumentItem.setQuantity(invInventoryDocumentItem.getPriceQuantity());
                    if (invInventoryDocumentItem.getUnitConversionRate() != null) {
                        invInventoryDocumentItem.setQuantity(invInventoryDocumentItem.getPriceQuantity().multiply(invInventoryDocumentItem.getUnitConversionRate()));
                    }
                }
                invInventoryDocuments.add(invInventoryDocumentItem);
            });
            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
        }
        return invInventoryDocument;
    }

    //校验可用库存
    public void judge(DelDeliveryNote delDeliveryNote,List<DelDeliveryNoteItem> delDeliveryNoteItems,List<CommonErrMsgResponse> msgList){
        delDeliveryNoteItems.forEach(o->{
            o.setQuantity(o.getDeliveryQuantity());
            o.setDeliveryQuantity(o.getDeliveryQuantity().multiply(o.getUnitConversionRate()));
        });
        Map<Long, List<DelDeliveryNoteItem>> listMap = delDeliveryNoteItems.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
        listMap.keySet().stream().forEach(l->{
            List<DelDeliveryNoteItem> items = listMap.get(l);
            if(items.size()==1){
                //商品条码不重复情况下
                items.forEach(m->{
                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                    invInventoryLocation.setBarcodeSid(m.getBarcodeSid())
                            .setStorehouseSid(delDeliveryNote.getStorehouseSid())
                            .setBusinessOrderSid(m.getDeliveryNoteSid())
                            .setStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid());
                    InvInventoryLocation location = null;
                    String saleAndPurchaseDocument = m.getSaleAndPurchaseDocument();
                    if(DocCategory.PURCHAASE_JI_SHOU.getCode().equals(saleAndPurchaseDocument)){
                        invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                                .setVendorSid(m.getVendorSid());
                        location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                    }else{
                        location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                    }
                    if(location==null){
                        location=new InvInventoryLocation();
                        location.setAbleQuantity(BigDecimal.ZERO);
                    }
                    if(m.getDeliveryQuantity().compareTo(location.getAbleQuantity())==1){
                        CommonErrMsgResponse noteItem = new CommonErrMsgResponse();
                        noteItem.setItemNum(m.getItemNum().intValue())
                                .setMsg("第"+m.getItemNum().intValue()+"行，明细可用库存不足");
                        msgList.add(noteItem);
                    }
                });
            }else{
                BigDecimal sum = items.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                DelDeliveryNoteItem note = items.get(0);
                InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                invInventoryLocation.setBarcodeSid(note.getBarcodeSid())
                        .setStorehouseSid(delDeliveryNote.getStorehouseSid())
                        .setBusinessOrderSid(note.getDeliveryNoteSid())
                        .setStorehouseLocationSid(delDeliveryNote.getStorehouseLocationSid());
                InvInventoryLocation location = null;
                String saleAndPurchaseDocument = note.getSaleAndPurchaseDocument();
                if(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(saleAndPurchaseDocument)){
                    invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                            .setVendorSid(note.getVendorSid());
                    location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                }else{
                    location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                }
                if(location==null){
                    location=new InvInventoryLocation();
                    location.setAbleQuantity(BigDecimal.ZERO);
                }
                if(sum.compareTo(location.getAbleQuantity())==1){
                    BigDecimal comsum=BigDecimal.ZERO;
                    for (int i = 0; i < items.size(); i++) {
                        comsum=items.get(i).getDeliveryQuantity().add(comsum);
                        if(comsum.compareTo(location.getAbleQuantity())==1){
                            CommonErrMsgResponse noteItem = new CommonErrMsgResponse();
                            noteItem.setItemNum(items.get(i).getItemNum().intValue())
                                    .setMsg("第"+items.get(i).getItemNum().intValue()+"行，明细可用库存不足");
                            msgList.add(noteItem);
                        }
                    }
                }
            }
        });
        delDeliveryNoteItems.forEach(o->{
            o.setDeliveryQuantity(o.getQuantity());
        });
    }

    /**
     * 批量删除交货单
     *
     * @param deliveryNoteSids 需要删除的交货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelDeliveryNoteByIds(Long[] deliveryNoteSids) {
        //删除交货单
        delDeliveryNoteMapper.deleteDelDeliveryNoteByIds(deliveryNoteSids);
        //删除交货单明细
        delDeliveryNoteItemMapper.deleteDelDeliveryNoteItemByIds(deliveryNoteSids);
        //删除交货单附件
        delDeliveryNoteAttachmentMapper.deleteDelDeliveryNoteAttachmentByIds(deliveryNoteSids);
        //删除交货单合作伙伴
        delDeliveryNotePartnerMapper.deleteDelDeliveryNotePartnerByIds(deliveryNoteSids);
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, deliveryNoteSids));
        return deliveryNoteSids.length;
    }

    @Override
    public AjaxResult checkProcess(DelDeliveryNote delDeliveryNote){
        List<DelDeliveryNoteItem> itemList = delDeliveryNote.getDelDeliveryNoteItemList();
        itemCheck(itemList);
        List<CommonErrMsgResponse> responseList = new ArrayList<>();
        for (DelDeliveryNoteItem item : itemList) {
            List<DelDeliveryNoteItem> delDeliveryNoteItems = new ArrayList<>();
            if (item.getSalesOrderItemSid() != null) {
                List<DelDeliveryNoteItem> list = delDeliveryNoteMapper.getConnectSaleSid(item.getSalesOrderItemSid(), HandleStatus.INVALID.getCode());
                delDeliveryNoteItems.addAll(list);
            } else {
                List<DelDeliveryNoteItem> list = delDeliveryNoteMapper.getConnectPurSid(item.getPurchaseOrderItemSid(), HandleStatus.INVALID.getCode());
                delDeliveryNoteItems.addAll(list);
            }
            //过滤掉自身
            if(item.getDeliveryNoteItemSid()==null){
                if(delDeliveryNote.getDeliveryNoteSid()!=null){
                    if(CollectionUtil.isNotEmpty(delDeliveryNoteItems)){
                        delDeliveryNoteItems=delDeliveryNoteItems.stream().filter(li->!li.getDeliveryNoteSid().toString().equals(delDeliveryNote.getDeliveryNoteSid().toString())).collect(Collectors.toList());
                    }
                }
                delDeliveryNoteItems.add(item);
            }else{
                delDeliveryNoteItems= delDeliveryNoteItems.stream().filter(li -> !li.getDeliveryNoteItemSid().toString().equals(item.getDeliveryNoteItemSid().toString())).collect(Collectors.toList());
                delDeliveryNoteItems.add(item);
            }
            if (CollectionUtil.isNotEmpty(delDeliveryNoteItems)) {
                BigDecimal tatal = delDeliveryNoteItems.stream().map(li -> {
                    if (li.getInOutStockQuantity() != null && li.getInOutStockQuantity().compareTo(BigDecimal.ZERO) != 0) {
                        return li.getInOutStockQuantity();
                    } else {
                        return li.getDeliveryQuantity();
                    }
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (item.getQuantity().compareTo(tatal) == -1) {
                    CommonErrMsgResponse resultEntity = new CommonErrMsgResponse();
                    resultEntity.setMsg("商品条码" + item.getBarcode() + "，总发货量已超过订单量;");
                    responseList.add(resultEntity);
                }
            }
        }
        if (CollectionUtil.isEmpty(responseList)) {
            return AjaxResult.success(EmsResultEntity.success());
        }
        else {
            return AjaxResult.success(EmsResultEntity.warning(responseList));
        }
    }

    /**
     * 校验发货单对应的销售订单明细表中【s_sal_sales_order_item】，
     * 所有明细行的”销售价(含税)”是否有值，若存在”销售价(含税)”的值为空的明细行信息，
     * 则从销售价明细表【s_sal_sale_price_item，物料销售价明细表】中，
     * 获取对应物料的”销售价(含税)”的值；否则，不获取销售价明细表中对应物料的销售价；
     * <p>
     * 若发货单对应的销售订单明细表中【s_sal_sales_order_item】存在任意一明细行的”销售价(含税)”的值为空，
     * 不允许对此销售发货单进行确认操作，提示：物料编码XXX，销售价未维护，请至销售价页面进行维护
     */
    private void check( String deliveryCategory,List<DelDeliveryNoteItem> itemList) {
        if (deliveryCategory.equals(ConstantsEms.delivery_Category_CG)) {
            //校验采购订单
            itemList.forEach(item -> {
                Long sid = item.getPurchaseOrderItemSid();
                PurPurchaseOrderItem purPurchaseOrderItem = purPurchaseOrderItemMapper.selectOne(new QueryWrapper<PurPurchaseOrderItem>().lambda().eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, sid));
                if (purPurchaseOrderItem == null) {
                    throw new BaseException("采购订单明细不存在");
                }
                BigDecimal purchasePriceTax = purPurchaseOrderItem.getPurchasePriceTax();
                if (purchasePriceTax == null) {
                    //查询当前采购价
                    PurPurchaseOrder order = purPurchaseOrderMapper.selectOne(new QueryWrapper<PurPurchaseOrder>().lambda().eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrderItem.getPurchaseOrderSid()));
                    //供应商、公司、采购模式、甲供料方式、商品/物料sid、SKU1sid查
                    PurPurchasePrice purchasePrice = new PurPurchasePrice();
                    purchasePrice.setVendorSid(order.getVendorSid());
                    purchasePrice.setCompanySid(order.getCompanySid());
                    purchasePrice.setPurchaseMode(order.getPurchaseMode());
                    purchasePrice.setMaterialSid(item.getMaterialSid());
                    purchasePrice.setRawMaterialMode(order.getRawMaterialMode());
                    purchasePrice.setSku1Sid(item.getSku1Sid());
                    PurPurchasePriceItem priceInfo = purPurchasePriceService.getPurchasePrice(purchasePrice);
                    if (priceInfo == null) {
                        String documentType = order.getDocumentType();
                        if(!exit(documentType)){
                            Long materialSid = item.getMaterialSid();
                            BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
                            String msg = "物料编码[" + basMaterial.getMaterialCode() + "]，采购价未维护，请至采购价页面进行维护";
                            throw new BaseException(msg);
                        }
                    } else {
                        //更新采购订单明细 采购价
                        purPurchaseOrderItem.setTaxRate(priceInfo.getTaxRate());
                        purPurchaseOrderItem.setPurchasePriceTax(priceInfo.getPurchasePriceTax());
                        if (priceInfo.getPurchasePriceTax() != null && priceInfo.getTaxRate() != null) {
                            purPurchaseOrderItem.setPurchasePrice(priceInfo.getPurchasePriceTax().multiply(priceInfo.getTaxRate()));
                        }
                        purPurchaseOrderItemMapper.updateById(purPurchaseOrderItem);
                    }
                }
            });
        }
        if (deliveryCategory.equals(ConstantsEms.delivery_Category_XS)) {
            //校验销售订单
            itemList.forEach(item -> {
                Long sid = item.getSalesOrderItemSid();
                SalSalesOrderItem salSalesOrderItem = salSalesOrderItemMapper.selectOne(new QueryWrapper<SalSalesOrderItem>().lambda().eq(SalSalesOrderItem::getSalesOrderItemSid, sid));
                if (salSalesOrderItem == null) {
                    throw new BaseException("销售订单明细不存在");
                }
                BigDecimal salePriceTax = salSalesOrderItem.getSalePriceTax();
                if (salePriceTax == null) {
                    //查询当前销售价
                    SalSalesOrder order = salSalesOrderMapper.selectOne(new QueryWrapper<SalSalesOrder>().lambda().eq(SalSalesOrder::getSalesOrderSid, salSalesOrderItem.getSalesOrderSid()));
                    //按：客户、公司、销售模式、客供料方式、商品/物料sid、SKU1sid查
                    SalSalePrice salSalePrice = new SalSalePrice();
                    salSalePrice.setCustomerSid(order.getCustomerSid());
                    salSalePrice.setCompanySid(order.getCompanySid());
                    salSalePrice.setSaleMode(order.getSaleMode());
                    salSalePrice.setMaterialSid(item.getMaterialSid());
                    salSalePrice.setRawMaterialMode(order.getRawMaterialMode());
                    salSalePrice.setSku1Sid(item.getSku1Sid());
                    SalSalePriceItem priceInfo = salSalePriceService.getSalePrice(salSalePrice);
                    if (priceInfo == null) {
                        String documentType = order.getDocumentType();
                        if(!exitSale(documentType)){
                            Long materialSid = item.getMaterialSid();
                            BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
                            String msg = "物料编码[" + basMaterial.getMaterialCode() + "]，销售价未维护，请至销售价页面进行维护";
                            throw new BaseException(msg);
                        }
                    } else {
                        //更新销售价订单明细 销售价
                        salSalesOrderItem.setTaxRate(priceInfo.getTaxRate());
                        salSalesOrderItem.setSalePriceTax(priceInfo.getSalePriceTax());
                        if (priceInfo.getSalePriceTax() != null && priceInfo.getTaxRate() != null) {
                            salSalesOrderItem.setSalePrice(priceInfo.getSalePriceTax().multiply(priceInfo.getTaxRate()));
                        }
                        salSalesOrderItemMapper.updateById(salSalesOrderItem);
                    }
                }
            });
        }
    }

    /**
     * 交货单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(DelDeliveryNote delDeliveryNote) {
        //交货单sids
        Long[] deliveryNoteSids = delDeliveryNote.getDeliveryNoteSids();
        for (int i = 0; i < deliveryNoteSids.length; i++) {
            Long deliveryNoteSid = deliveryNoteSids[i];
            DelDeliveryNote note = delDeliveryNoteMapper.selectById(deliveryNoteSid);
            if (note == null) {
                throw new BaseException("数据异常,请联系管理员");
            }
            String deliveryCategory = note.getDeliveryCategory();
            if (StrUtil.isEmpty(deliveryCategory)) {
                throw new BaseException("交货类型为空");
            }
            DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
            delDeliveryNoteItem.setDeliveryNoteSid(deliveryNoteSid);
            List<DelDeliveryNoteItem> itemList = delDeliveryNoteItemMapper.getShipmentsItemList(delDeliveryNoteItem);
            String documentType = note.getDocumentType();
            Boolean isCheck=false;
            String returnType=null;
            if(DocCategory.SALE_RU.getCode().equals(documentType)){
                isCheck=true;
                returnType=ConstantsEms.YES;

            }else if(DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(documentType)){
                isCheck=true;
                returnType=ConstantsEms.NO;
            }
            String inventoryControlMode = note.getInventoryControlMode();
            String isReturnGoods = note.getIsReturnGoods();
            if(isCheck){
                // 是否预留库存的校验
                if (documentType != null) {
                    ConDocTypeDeliveryNote docTypeDeliveryNote = conDocTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConDocTypeDeliveryNote>()
                            .lambda().eq(ConDocTypeDeliveryNote::getCode, documentType));
                    if(docTypeDeliveryNote != null && ConstantsEms.YES.equals(docTypeDeliveryNote.getIsReserveStock())
                            && !ConstantsEms.INV_CTROL_BGX.equals(inventoryControlMode)&&!returnType.equals(isReturnGoods)){
                        //生成预留库存
                        createInv(itemList);
                    }
                }
            }
            if (CollUtil.isEmpty(itemList)) {
                throw new BaseException("明细列表不存在");
            }
            //校验是否存在待办
            checkTodoExist(note);
            //插入日志
            MongodbUtil.insertUserLog(deliveryNoteSid, com.platform.common.log.enums.BusinessType.CONFIRM.getValue(),TITLE);
        }
        delDeliveryNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        delDeliveryNote.setConfirmDate(new Date());
        int row = delDeliveryNoteMapper.confirm(delDeliveryNote);
        return row;
    }

    //明细报表生成/更新预留库存
    @Override
    public int reportCreateInv(List<Long> sids){
        invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                .in(InvReserveInventory::getBusinessOrderItemSid, sids)
        );
        DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
        delDeliveryNoteItem.setDeliveryNoteItemSidList(sids);
        List<DelDeliveryNoteItem> itemList = delDeliveryNoteItemMapper.getShipmentsItemList(delDeliveryNoteItem);
        List<DelDeliveryNoteItem> delDeliveryNoteItems = new ArrayList<>();
        //生成库存预留
        List<DelDeliveryNoteItem> itemListXSFH= itemList.stream().filter(li -> DocCategory.SALE_RU.getCode().equals(li.getDocumentType())&&!ConstantsEms.INV_CTROL_BGX.equals(li.getInventoryControlMode()) && !ConstantsEms.YES.equals(li.getIsReturnGoods())).collect(Collectors.toList());
        List<DelDeliveryNoteItem> itemListCGJH= itemList.stream().filter(li -> DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(li.getDocumentType())&&!ConstantsEms.INV_CTROL_BGX.equals(li.getInventoryControlMode()) && !ConstantsEms.NO.equals(li.getIsReturnGoods())).collect(Collectors.toList());
        delDeliveryNoteItems.addAll(itemListXSFH);
        delDeliveryNoteItems.addAll(itemListCGJH);
        if(CollectionUtil.isNotEmpty(delDeliveryNoteItems)){
            Map<Long, List<DelDeliveryNoteItem>> listMap = delDeliveryNoteItems.stream().collect(Collectors.groupingBy(v -> v.getDeliveryNoteSid()));
            listMap.keySet().forEach(l->{
                List<DelDeliveryNoteItem> items = listMap.get(l);
                createInv(items);
            });
        }
        return 1;
    }

    //明细报表释放预留库存
    @Override
    public int reportFreeInv(List<Long> sids){
        int row = invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                .in(InvReserveInventory::getBusinessOrderItemSid, sids)
        );
        delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                .in(DelDeliveryNoteItem::getDeliveryNoteItemSid,sids)
                .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
        );
        return row;
    }

    //冲销生成库存预留
    @Override
    public  void xcCreateInv(Long sid){
        DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectById(sid);
        String documentType = delDeliveryNote.getDocumentType();
        Boolean isCheck=false;
        String returnType=null;
        if(DocCategory.SALE_RU.getCode().equals(documentType)){
            isCheck=true;
            returnType=ConstantsEms.YES;

        }else if(DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(documentType)){
            isCheck=true;
            returnType=ConstantsEms.NO;
        }
        String inventoryControlMode = delDeliveryNote.getInventoryControlMode();
        String isReturnGoods = delDeliveryNote.getIsReturnGoods();
        if(isCheck){
            if(!ConstantsEms.INV_CTROL_BGX.equals(inventoryControlMode)&&!returnType.equals(isReturnGoods)){
                //生成预留库存
                DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
                delDeliveryNoteItem.setDeliveryNoteSid(sid);
                List<DelDeliveryNoteItem> itemList = delDeliveryNoteItemMapper.getShipmentsItemList(delDeliveryNoteItem);
                if(CollectionUtil.isNotEmpty(itemList)){
                    //生成库存预留
                    createInv(itemList);
                }
            }
        }
    }

    //生成预留库存
    public  void createInv(List<DelDeliveryNoteItem> itemList){
        itemList.forEach(li->{
            li.setDeliveryQuantity(li.getDeliveryQuantity().multiply(li.getUnitConversionRate()));
        });
        //改变预留状态
        Map<Long, List<DelDeliveryNoteItem>> listMap = itemList.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
        listMap.keySet().stream().forEach(l->{
            List<DelDeliveryNoteItem> items = listMap.get(l);
            if(items.size()==1){
                //商品条码不重复情况下
                items.forEach(m->{
                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                    invInventoryLocation.setBarcodeSid(m.getBarcodeSid())
                            .setStorehouseSid(m.getStorehouseSid())
                            .setStorehouseLocationSid(m.getStorehouseLocationSid());
                    String saleAndPurchaseDocument = m.getSaleAndPurchaseDocument();
                    InvInventoryLocation location=null;
                    if(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(saleAndPurchaseDocument)){
                        invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                                .setVendorSid(m.getVendorSid());
                        location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                    }else{
                        location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                    }
                    if(location==null){
                        location=new InvInventoryLocation();
                        location.setAbleQuantity(BigDecimal.ZERO);
                    }
                    if(m.getDeliveryQuantity().compareTo(location.getAbleQuantity())!=1){
                        delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,m.getDeliveryNoteItemSid())
                                .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                        );
                    }else if(m.getDeliveryQuantity().compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1){
                        delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,m.getDeliveryNoteItemSid())
                                .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                        );
                        m.setDeliveryQuantity(location.getAbleQuantity());
                    }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1){
                        delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,m.getDeliveryNoteItemSid())
                                .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                        );
                        m.setDeliveryQuantity(BigDecimal.ZERO);
                    }
                });
            }else{
                BigDecimal sum = items.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                DelDeliveryNoteItem noteSignle = items.get(0);
                InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                invInventoryLocation.setBarcodeSid(noteSignle.getBarcodeSid())
                        .setStorehouseSid(noteSignle.getStorehouseSid())
                        .setStorehouseLocationSid(noteSignle.getStorehouseLocationSid());
                String saleAndPurchaseDocument = noteSignle.getSaleAndPurchaseDocument();
                InvInventoryLocation location=null;
                if(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(saleAndPurchaseDocument)){
                    invInventoryLocation.setSpecialStock(ConstantsEms.VEN_CU)
                            .setVendorSid(noteSignle.getVendorSid());
                    location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                }else{
                    location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                }
                if(location==null){
                    location=new InvInventoryLocation();
                    location.setAbleQuantity(BigDecimal.ZERO);
                }
                if(sum.compareTo(location.getAbleQuantity())==1){
                    BigDecimal comsum=BigDecimal.ZERO;
                    for (int j = 0; j < items.size(); j++) {
                        comsum=items.get(j).getDeliveryQuantity().add(comsum);
                        if(comsum.compareTo(location.getAbleQuantity())!=1){
                            delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                    .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,items.get(j).getDeliveryNoteItemSid())
                                    .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                            );
                        }else if(comsum.compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1&&location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getDeliveryQuantity()))).compareTo(BigDecimal.ZERO)==1){
                            delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                    .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,items.get(j).getDeliveryNoteItemSid())
                                    .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                            );
                            items.get(j).setDeliveryQuantity(location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getDeliveryQuantity()))));

                        }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1||location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getDeliveryQuantity()))).compareTo(BigDecimal.ZERO)!=1){
                            delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                    .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,items.get(j).getDeliveryNoteItemSid())
                                    .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                            );
                            items.get(j).setDeliveryQuantity(BigDecimal.ZERO);
                        }
                    }
                }else{
                    //全部预留
                    items.forEach(h->{
                        delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(),new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                                .eq(DelDeliveryNoteItem::getDeliveryNoteItemSid,h.getDeliveryNoteItemSid())
                                .set(DelDeliveryNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                        );
                    });
                }
            }
        });
        List<InvReserveInventory> invReserveInventories = new ArrayList<>();
        itemList.forEach(li->{
            InvReserveInventory invReserveInventory = new InvReserveInventory();
            invReserveInventory.setBarcodeSid(li.getBarcodeSid())
                    .setDeliveryNoteCode(li.getDeliveryNoteCode()==null?null:Long.valueOf(li.getDeliveryNoteCode()))
                    .setDeliveryNoteItemNum(li.getItemNum())
                    .setDeliveryNoteItemSid(li.getDeliveryNoteItemSid())
                    .setDeliveryNoteSid(li.getDeliveryNoteSid())
                    .setBusinessOrderCode(li.getDeliveryNoteCode()==null?null:Long.valueOf(li.getDeliveryNoteCode()))
                    .setBusinessOrderItemNum(li.getItemNum())
                    .setBusinessOrderSid(li.getDeliveryNoteSid())
                    .setBusinessOrderItemSid(li.getDeliveryNoteItemSid())
                    .setMaterialSid(li.getMaterialSid())
                    .setReserveType(DocCategory.SALE_RU.getCode().equals(li.getDocumentType())?"XSFHD":"CGTHD")
                    .setSku1Sid(li.getSku1Sid())
                    .setSpecialStock(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(li.getSaleAndPurchaseDocument())?ConstantsEms.VEN_CU:null)
                    .setVendorSid(DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(li.getSaleAndPurchaseDocument())?li.getVendorSid():null)
                    .setSku2Sid(li.getSku2Sid())
                    .setStorehouseSid(li.getStorehouseSid())
                    .setStorehouseLocationSid(li.getStorehouseLocationSid())
                    .setQuantity(li.getDeliveryQuantity());
            invReserveInventories.add(invReserveInventory);
        });
        //生成库存预留
        invReserveInventoryMapper.inserts(invReserveInventories);
    }

    /**
     * 交货单变更
     */
    @Override
    public int change(DelDeliveryNote delDeliveryNote) {
        Long deliveryNoteSid = delDeliveryNote.getDeliveryNoteSid();
        DelDeliveryNote deliveryNote = delDeliveryNoteMapper.selectDelDeliveryNoteById(deliveryNoteSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(deliveryNote.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }
        setConfirmInfo(delDeliveryNote);
        delDeliveryNoteMapper.updateAllById(delDeliveryNote);
        //交货单-明细对象
        List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNote.getDelDeliveryNoteItemList();
        if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
            delDeliveryNoteItemList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            setItemNum(delDeliveryNote, delDeliveryNoteItemList);
            addDelDeliveryNoteItem(delDeliveryNote, delDeliveryNoteItemList);
        }
        //交货单-附件对象
        List<DelDeliveryNoteAttachment> delDeliveryNoteAttachmentList = delDeliveryNote.getAttachmentList();
        if (CollectionUtils.isNotEmpty(delDeliveryNoteAttachmentList)) {
            delDeliveryNoteAttachmentList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addDelDeliveryNoteAttachment(delDeliveryNote, delDeliveryNoteAttachmentList);
        }
        //交货单-合作伙伴对象
        List<DelDeliveryNotePartner> delDeliveryNotePartnerList = delDeliveryNote.getDelDeliveryNotePartnerList();
        if (CollectionUtils.isNotEmpty(delDeliveryNotePartnerList)) {
            delDeliveryNotePartnerList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addDelDeliveryNotePartner(delDeliveryNote, delDeliveryNotePartnerList);
        }
        return 1;
    }

    public Boolean exit(String code){
        List<String> codes = Arrays.asList("CPO", "RCPO", "BLTZD");//备料通知单、寄售订单、寄售退货订单
        boolean isMatch = codes.stream().anyMatch(li -> li.equals(code));
        return isMatch;
    }

    public Boolean exitSale(String code){
        List<String> codes = Arrays.asList("CSO", "RCSO", "BLTZD");//备料通知单、寄售订单、寄售退货订单
        boolean isMatch = codes.stream().anyMatch(li -> li.equals(code));
        return isMatch;
    }
}
