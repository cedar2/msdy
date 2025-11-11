package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.SalSaleOrderProcessTracking;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.*;
import com.platform.system.service.ISysDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.platform.ems.constant.ConstantsEms.ADVANCE_SETTLE_MODE_DD;
import static java.util.stream.Collectors.toList;

/**
 * 销售订单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class SalSalesOrderServiceImpl extends ServiceImpl<SalSalesOrderMapper, SalSalesOrder> implements ISalSalesOrderService {
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialSkuMapper materialSkuMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasSkuMapper skuMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private SalSalesOrderAttachmentMapper salSalesOrderAttachmentMapper;
    @Autowired
    private ISalSalesOrderItemService salSalesOrderItemService;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private FinRecordAdvanceReceiptMapper finRecordAdvanceReceiptMapper;
    @Autowired
    private FinRecordAdvanceReceiptItemMapper finRecordAdvanceReceiptItemMapper;
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;
    @Autowired
    private InvInventoryDocumentServiceImpl InvInventoryDocumentimpl;

    private static final String TITLE = "销售订单";

    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private TecProductSizeZipperLengthMapper zipperLengthMapper;
    @Autowired
    private ConDocTypeSalesOrderMapper conDocTypeSalesOrderMapper;
    @Autowired
    private ConBuTypeSalesOrderMapper conBuTypeSalesOrderMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private ConSaleGroupMapper conSaleGroupMapper;
    @Autowired
    private ConSaleOrgMapper conSaleOrgMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private  BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ConSaleChannelMapper conSaleChannelMapper;
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private SalSalePriceServiceImpl salSalePriceServiceImpl;
    @Autowired
    private IPurPurchasePriceService iPurPurchasePriceService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;
    @Autowired
    private  FinBookReceiptEstimationMapper finBookReceiptEstimationMapper;
    @Autowired
    private  FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private ConDocBuTypeGroupSoMapper conDocBuTypeGroupSoMapper;
    @Autowired
    private IBasSkuGroupService  basSkuGroupService;
    @Autowired
    private SalSalesOrderDeliveryPlanMapper salSalesOrderDeliveryPlanMapper;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private ConInOutStockDocCategoryMapper  conInOutStockDocCategoryMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private IWorkFlowService workflowService;

    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;

    private static final String TYPE_MFL = "MFL";
    private static final String YCX_CUSTOMER = "YCX";

    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;

    /**
     * 查询销售订单
     *
     * @param salesOrderSid 销售订单ID
     * @return 销售订单
     */
    @Override
    public SalSalesOrder selectSalSalesOrderById(Long salesOrderSid) {
        SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(salesOrderSid);
        if (salSalesOrder == null) {
            return null;
        }
        //销售订单-明细
        SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
        salSalesOrderItem.setSalesOrderSid(salesOrderSid);
        List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrderItemMapper.selectSalSalesOrderItemList(salSalesOrderItem);
        if (CollectionUtil.isNotEmpty(salSalesOrderItemList)) {
            salSalesOrderItemList = salSalesOrderItemService.handleIndex(salSalesOrderItemList);
        }
        List<SalSalesOrderItem> items = salSalesOrderItemList;
        // 用来做获取当前价格
        String documentType = salSalesOrder.getDocumentType();
        boolean isGetPrice = false;
        if(CollectionUtil.isNotEmpty(items)){
            if (DocCategory.RETURN_BACK_SALE.getCode().equals(documentType) || DocCategory.SALE_JI_RETURN.getCode().equals(documentType) || DocCategory.JI_SHOU.getCode().equals(documentType)) {
                isGetPrice = true;
            }
            // 用来获取可用库存量
            List<Long> barcodeSidList = items.stream().map(SalSalesOrderItem::getBarcodeSid).collect(toList());
            List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>()
                    .lambda().in(InvInventoryLocation::getBarcodeSid, barcodeSidList));
            Map<Long, List<InvInventoryLocation>> inventoryMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(invInventoryLocations)) {
                inventoryMap = invInventoryLocations.stream().collect(Collectors.groupingBy(InvInventoryLocation::getBarcodeSid));
            }
            // 遍历明细
            for (SalSalesOrderItem item : items) {
                List<SalSalesOrderDeliveryPlan> salSalesOrderDeliveryPlans = salSalesOrderDeliveryPlanMapper.selectSalSalesOrderDeliveryPlanById(item.getSalesOrderItemSid());
                item.setDeliveryPlanList(salSalesOrderDeliveryPlans);

                if(ConstantsEms.YES.equals(item.getFreeFlag())){
                    item.setSalePriceTax(BigDecimal.ZERO);
                    item.setSalePrice(BigDecimal.ZERO);
                }

                // 获取可用库存量
                List<InvInventoryLocation> inventoryLocations = inventoryMap.containsKey(item.getBarcodeSid()) ?
                        inventoryMap.get(item.getBarcodeSid()) : new ArrayList<>();
                item.setInvQuantity(BigDecimal.ZERO);
                if( CollectionUtil.isNotEmpty(inventoryLocations)){
                    BigDecimal sum=BigDecimal.ZERO;
                    if(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())){
                        sum = inventoryLocations.stream().map(m -> m.getCustomerConsignQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }else{
                        sum = inventoryLocations.stream().map(m -> m.getUnlimitedQuantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    item.setInvQuantity(sum);
                }

                // 获取当前价格
                if (isGetPrice) {
                    SalSalePrice salSalePrice = new SalSalePrice();
                    BeanCopyUtils.copyProperties(salSalesOrder, salSalePrice);
                    BeanCopyUtils.copyProperties(item, salSalePrice);
                    Long materialSid = salSalePrice.getMaterialSid();
                    salSalePrice.setCustomerSid(salSalesOrder.getCustomerSid());
                    SalSalePriceItem salSalePriceItem = salSalePriceServiceImpl.getNewSalePrice(salSalePrice);
                    item.setReturnPtin(salSalePriceItem.getSalePriceTax());
                }

            }
        }
        //销售订单-附件
        SalSalesOrderAttachment salSalesOrderAttachment = new SalSalesOrderAttachment();
        salSalesOrderAttachment.setSalesOrderSid(salesOrderSid);
        List<SalSalesOrderAttachment> salSalesOrderAttachmentList =
                salSalesOrderAttachmentMapper.selectSalSalesOrderAttachmentList(salSalesOrderAttachment);
        salSalesOrder.setAttachmentList(salSalesOrderAttachmentList);
        /*
        getInvQuantily(items,salSalesOrder);
         */
        salSalesOrder.setSalSalesOrderItemList(items);
        /*
        items.forEach(li->{
            if(ConstantsEms.YES.equals(li.getFreeFlag())){
                li.setSalePriceTax(BigDecimal.ZERO);
                li.setSalePrice(BigDecimal.ZERO);
            }
        });
        String documentType = salSalesOrder.getDocumentType();
        if (DocCategory.RETURN_BACK_SALE.getCode().equals(documentType) || DocCategory.SALE_JI_RETURN.getCode().equals(documentType) || DocCategory.JI_SHOU.getCode().equals(documentType)) {
            items.forEach(item -> {
                SalSalePrice salSalePrice = new SalSalePrice();
                BeanCopyUtils.copyProperties(salSalesOrder, salSalePrice);
                BeanCopyUtils.copyProperties(item, salSalePrice);
                Long materialSid = salSalePrice.getMaterialSid();
                salSalePrice.setCustomerSid(salSalesOrder.getCustomerSid());
                BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
                String zipperFlag = basMaterial.getZipperFlag();
                SalSalePriceItem salSalePriceItem = salSalePriceServiceImpl.getNewSalePrice(salSalePrice);
                item.setReturnPtin(salSalePriceItem.getSalePriceTax());
            });
        }
        */

        SalSalesOrder count = getCount(items);
        salSalesOrder.setSumMoneyAmount(count.getSumMoneyAmount())
                .setSumQuantity(count.getSumQuantity())
                .setSumQuantityCode(count.getSumQuantityCode());

        //查询日志信息
        MongodbUtil.find(salSalesOrder);
        return salSalesOrder;
    }

    @Override
    public SalSalesOrder getItemTotalList(SalSalesOrder salSalesOrder, List<SalSalesOrderItem> items) {

        //明细页签汇总
        List<SalSalesOrderItem> isNUllGroup = items.stream().filter(li -> li.getSku2GroupSid() == null).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(isNUllGroup)){
            //计算总共金额 总数量
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
            /*
            items.forEach(li->{
                if(ConstantsEms.YES.equals(li.getFreeFlag())){
                    li.setSalePriceTax(BigDecimal.ZERO);
                    li.setSalePrice(BigDecimal.ZERO);
                }
            });
            */

            //分组按商品编码+颜色+销售价(含税)+合同交期
            Map<String, List<SalSalesOrderItem>> listMap = items.stream().collect(Collectors.groupingBy(v -> v.getMaterialCode() + "_" + v.getSalePriceTax() + "_" + v.getSku1Sid() + "_" + v.getContractDate()));
            listMap.keySet().stream().forEach(h->{
                List<SalSalesOrderItem> salSalesOrderItems = listMap.get(h);
                Long sku2GroupSid = salSalesOrderItems.get(0).getSku2GroupSid();
                SalSalesOrderItem orderItem = salSalesOrderItems.get(0);
                //尺码组
                ArrayList<SalSalesOrderTotalSku2Response> sku2QuantityList = new ArrayList<>();
                //获取对应的sku2组的名称-且统一取最长的尺码长度
                List<String> sku2NameList = Sku2GroupHashMap.get(sku2GroupSid);
                // 物料商品code
                String materialCode = salSalesOrderItems.get(0).getMaterialCode();
                for (int i = 0; i <maxSize ; i++) {
                    int size = sku2NameList.size();
                    SalSalesOrderTotalSku2Response sku2Quantity = new SalSalesOrderTotalSku2Response();
                    if(i+1<=size){
                        sku2Quantity.setMaterialCode(materialCode);
                        sku2Quantity.setSku2Name(sku2NameList.get(i));
                    }
                    sku2QuantityList.add(sku2Quantity);
                }
                //再次分组按尺码
                List<SalSalesOrderItem> newnewList = salSalesOrderItems.stream().filter(o->o.getSku2Name() != null).collect(toList());
                if (CollectionUtil.isNotEmpty(newnewList)) {
                    Map<String, List<SalSalesOrderItem>> itemTempsku2List = newnewList.stream().collect(Collectors.groupingBy(v -> v.getMaterialCode() + "_" + v.getSku2Name()));
                    itemTempsku2List.keySet().stream().forEach(li->{
                        List<SalSalesOrderItem> tems = itemTempsku2List.get(li);
                        BigDecimal quantity = tems.stream().map(m -> m.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        sku2QuantityList.forEach(n->{
                            //匹配尺码对应的 数量
                            if(n.getSku2Name()!=null){
                                if((n.getMaterialCode() + "_" + n.getSku2Name()).equals(li)){
                                    n.setSku2Quantity(quantity);
                                }
                            }
                        });
                    });
                }
                //计算数量小计 总金额
                BigDecimal sum = salSalesOrderItems.stream().map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                BigDecimal sumCu = salSalesOrderItems.stream().map(li -> {
                    BigDecimal price = BigDecimal.ZERO;
                    BigDecimal quantity = BigDecimal.ZERO;
                    if (li.getQuantity() != null) {
                        quantity = li.getQuantity();
                    }
                    if (li.getSalePriceTax() != null) {
                        price = li.getSalePriceTax();
                    }
                    return price.multiply(quantity);
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                SalSalesOrderTotalResponse totalItem = new SalSalesOrderTotalResponse();
                BeanCopyUtils.copyProperties(orderItem,totalItem);
                totalItem.setSumMoneyAmount(sumCu!=null?sumCu.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP):null)
                        .setSumQuantity(sum)
                        .setSku2TotalList(sku2QuantityList);
                itemTotalList.add(totalItem);
            });
            List<SalSalesOrderTotalResponse> AllList = new ArrayList<>();
            List<SalSalesOrderTotalResponse> NotNullList= itemTotalList.stream().filter(li -> li.getContractDate() != null)
                    .sorted(Comparator.comparing(SalSalesOrderTotalResponse::getContractDate, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());
            List<SalSalesOrderTotalResponse> NullList = itemTotalList.stream().filter(li -> li.getContractDate() == null).collect(Collectors.toList());
            AllList.addAll(NotNullList);
            AllList.addAll(NullList);

            List<SalSalesOrderTotalResponse> sortList = AllList.stream().sorted(
                    Comparator.comparing(SalSalesOrderTotalResponse::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(SalSalesOrderTotalResponse::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(SalSalesOrderTotalResponse::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());

            salSalesOrder.setItemTotalList(sortList);
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
            salSalesOrder.setSku2GroupList(Sku2GroupList);
        }
        return salSalesOrder;
    }

    /**
     * 撤回保存
     *
     * @param salesOrder 销售订单
     * @return 结果
     */
    @Override
    public int backSaveVerify(SalSalesOrder salesOrder) {
        int row = 1;
        if (salesOrder.getSalesOrderSid() == null) {
            throw new BaseException("请选择销售订单");
        }
        SalSalesOrder order = salSalesOrderMapper.selectById(salesOrder.getSalesOrderSid());
        if (order != null && ConstantsEms.CHECK_STATUS.equals(order.getHandleStatus())) {
            // 校验
            if (ConstantsEms.DELIEVER_type_FHD.equals(order.getDeliveryType())) {
                List<DelDeliveryNoteItem> documentItemList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>()
                        .lambda().eq(DelDeliveryNoteItem::getSalesOrderSid, salesOrder.getSalesOrderSid()));
                if (CollectionUtil.isNotEmpty(documentItemList)) {
                    Long[] sids = documentItemList.stream().map(DelDeliveryNoteItem::getDeliveryNoteSid).toArray(Long[]::new);
                    List<DelDeliveryNote> documentList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>()
                            .lambda().in(DelDeliveryNote::getDeliveryNoteSid, sids)
                            .ne(DelDeliveryNote::getHandleStatus, HandleStatus.INVALID.getCode()));
                    if (CollectionUtil.isNotEmpty(documentList)) {
                        throw new BaseException("该销售订单已存在出货数据，无法撤回！");
                    }
                }
            }
            else if (ConstantsEms.DELIEVER_type_DD.equals(order.getDeliveryType())) {
                List<InvInventoryDocumentItem> documentItemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>()
                        .lambda().eq(InvInventoryDocumentItem::getReferDocumentSid, salesOrder.getSalesOrderSid()));
                if (CollectionUtil.isNotEmpty(documentItemList)) {
                    Long[] sids = documentItemList.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).toArray(Long[]::new);
                    List<InvInventoryDocument> documentList = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>()
                            .lambda().in(InvInventoryDocument::getInventoryDocumentSid, sids).eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                            .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG));
                    if (CollectionUtil.isNotEmpty(documentList)) {
                        throw new BaseException("该销售订单已存在出货数据，无法撤回！");
                    }
                }
            }
        }
        else {
            throw new BaseException("已确认的销售订单才可进行此操作！");
        }
        return row;
    }

    /**
     * 撤回保存
     *
     * @param salesOrder 销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int backSave(SalSalesOrder salesOrder) {
        int row = 0;
        if (salesOrder.getSalesOrderSid() == null) {
            throw new BaseException("请选择销售订单");
        }
        if (StrUtil.isBlank(salesOrder.getComment())) {
            throw new BaseException("撤回说明不能为空");
        }
        this.backSaveVerify(salesOrder);
        row = salSalesOrderMapper.update(null, new UpdateWrapper<SalSalesOrder>().lambda()
                .set(SalSalesOrder::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .eq(SalSalesOrder::getSalesOrderSid, salesOrder.getSalesOrderSid()));
        // 操作日志
        MongodbUtil.insertUserLog(salesOrder.getSalesOrderSid(), com.platform.common.log.enums.BusinessType.QITA.getValue(), null, TITLE, "撤回说明：" + salesOrder.getComment());
        return row;
    }

    /**
     * 维护物流信息
     *
     * @param salesOrder 订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setCarrier(SalSalesOrder salesOrder) {
        int row = 0;
        if (salesOrder.getSalesOrderSid() == null) {
            throw new BaseException("请选择行！");
        }
        if (salesOrder.getCarrier() != null) {
            BasVendor vendor = basVendorMapper.selectById(salesOrder.getCarrier());
            if (vendor != null) {
                salesOrder.setCarrierName(vendor.getVendorName());
            }
        }
        // 修改
        LambdaUpdateWrapper<SalSalesOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SalSalesOrder::getSalesOrderSid, salesOrder.getSalesOrderSid())
                .set(SalSalesOrder::getCarrier, salesOrder.getCarrier())
                .set(SalSalesOrder::getCarrierName, salesOrder.getCarrierName())
                .set(SalSalesOrder::getCarrierNoteCode, salesOrder.getCarrierNoteCode());
        row = salSalesOrderMapper.update(new SalSalesOrder(), updateWrapper);
        return row;
    }

    /**
     * 维护纸质合同号
     *
     * @param salesOrder 订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity setPaperContract(SalSalesOrder salesOrder) {
        int row = 0;
        // 修改
        LambdaUpdateWrapper<SalSalesOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SalSalesOrder::getSalesOrderSid, salesOrder.getSalesOrderSid())
                .set(SalSalesOrder::getPaperSaleContractCode, salesOrder.getPaperSaleContractCode());
        row = salSalesOrderMapper.update(new SalSalesOrder(), updateWrapper);
        MongodbUtil.insertUserLog(salesOrder.getSalesOrderSid(), BusinessType.QITA.getValue(), null, TITLE, "维护纸质合同号");
        if (StrUtil.isNotBlank(salesOrder.getPaperSaleContractCode())) {
            List<SalSalesOrderAttachment> attachments = salSalesOrderAttachmentMapper.selectList(new QueryWrapper<SalSalesOrderAttachment>()
                    .lambda().eq(SalSalesOrderAttachment::getFileType, ConstantsOrder.PAPER_CONTRACT_XSDDHT)
                    .eq(SalSalesOrderAttachment::getSalesOrderSid, salesOrder.getSalesOrderSid()));
            if (CollectionUtil.isEmpty(attachments)) {
                // 弹出提示框 是否上传“销售订单合同(盖章版)”附件
                return EmsResultEntity.warning(row, null, "是否上传“销售订单合同(盖章版)”附件");
            }
        }
        return EmsResultEntity.success(row, "操作成功");
    }

    @Override
    public void setValueNull(Long salesOrderSid){
        List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                .eq(SalSalesOrderItem::getSalesOrderSid, salesOrderSid)
        );
        if(CollectionUtil.isNotEmpty(salSalesOrderItems)){
            salSalesOrderItems.forEach(li->{
                li.setNewSalePrice(null)
                        .setNewTaxRate(null)
                        .setNewQuantity(null)
                        .setNewSalePriceTax(null)
                        .setNewContractDate(null);
                salSalesOrderItemMapper.updateAllById(li);
            });
        }
    }
    /**
     * 获取可用库存量
     *
     */
    public void getInvQuantily(List<SalSalesOrderItem> items,SalSalesOrder order){
        if(CollectionUtil.isNotEmpty(items)){
            items.forEach(li->{
                List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>()
                        .lambda()
                        .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                );
                li.setInvQuantity(BigDecimal.ZERO);
                if(CollectionUtil.isNotEmpty(invInventoryLocations)){
                    BigDecimal sum=BigDecimal.ZERO;
                    if(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(order.getSpecialBusCategory())){
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
     *  按照“商品/物料编码+SKU1序号+SKU1名称+SKU2序号+SKU2名称”升序排列
     * （SKU1序号、SKU2序号，取对应商品/物料档案的“SKU1”、“SKU2”页签中的“序号”清单列的值）
     */
    @Override
    public List<SalSalesOrderItem> newSort(List<SalSalesOrderItem> itemList){
        if (CollectionUtil.isEmpty(itemList)) {
            return itemList;
        }
        List<SalSalesOrderItem> itemMat = itemList.stream().sorted(
                Comparator.comparing(SalSalesOrderItem::getSalesOrderCode, Comparator.nullsLast(String::compareTo).thenComparing(Comparator.comparingLong(Long::parseLong)).reversed())
                        .thenComparing(SalSalesOrderItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(SalSalesOrderItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(SalSalesOrderItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(SalSalesOrderItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(SalSalesOrderItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
        ).collect(toList());
        return itemMat;
    }

    /**
     * 查询销售订单列表
     *
     * @param salSalesOrder 销售订单
     * @return 销售订单
     */
    @Override
    public List<SalSalesOrder> selectSalSalesOrderList(SalSalesOrder salSalesOrder) {
        List<SalSalesOrder> list = salSalesOrderMapper.selectSalSalesOrderList(salSalesOrder);
        list.forEach(item->{
            List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                    .eq(SalSalesOrderItem::getSalesOrderSid, item.getSalesOrderSid())
            );
            if(CollectionUtil.isNotEmpty(salSalesOrderItems)){
                BigDecimal sumQu = salSalesOrderItems.stream().map( li->{if( li.getQuantity()==null){
                    return BigDecimal.ZERO;
                }else{
                    return li.getQuantity();
                }}).reduce(BigDecimal.ZERO, BigDecimal::add);
                item.setSumQuantity(sumQu);
                BigDecimal sumCu = salSalesOrderItems.stream().map(li ->{
                    if(ConstantsEms.YES.equals(li.getFreeFlag())){
                        return BigDecimal.ZERO;
                    }else{
                        BigDecimal price=li.getSalePriceTax()!=null?li.getSalePriceTax():BigDecimal.ZERO;
                        BigDecimal qutatil=li.getQuantity()!=null?li.getQuantity():BigDecimal.ZERO;
                        return price.multiply(qutatil);
                    }
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                item.setSumMoneyAmount(sumCu.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
                HashSet<Long> longs = new HashSet<>();
                salSalesOrderItems.forEach(li->{
                    longs.add(li.getMaterialSid());
                });
                item.setSumQuantityCode(longs.size());
            }
        });
        return list;
    }
    /**
     * 计算金额
     *
     */
    @Override
    public  SalSalesOrder getCount(List<SalSalesOrderItem> salSalesOrderItems){
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        if(CollectionUtil.isNotEmpty(salSalesOrderItems)){
            BigDecimal sumQu = salSalesOrderItems.stream().map( li->{if( li.getQuantity()==null){
                return BigDecimal.ZERO;
            }else{
                return li.getQuantity();
            }}).reduce(BigDecimal.ZERO, BigDecimal::add);
            salSalesOrder.setSumQuantity(sumQu);
            BigDecimal sumCu = salSalesOrderItems.stream().map(li ->{
                if(ConstantsEms.YES.equals(li.getFreeFlag())){
                    return BigDecimal.ZERO;
                }else{
                    BigDecimal price=li.getSalePriceTax()!=null?li.getSalePriceTax():BigDecimal.ZERO;
                    BigDecimal qutatil=li.getQuantity()!=null?li.getQuantity():BigDecimal.ZERO;
                    return price.multiply(qutatil);
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            salSalesOrder.setSumMoneyAmount(sumCu.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
            HashSet<Long> longs = new HashSet<>();
            salSalesOrderItems.forEach(li->{
                longs.add(li.getMaterialSid());
            });
            salSalesOrder.setSumQuantityCode(longs.size());
        }
        return salSalesOrder;
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<SalSalesOrderItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
    }

    /**
     * 新增销售订单
     * 需要注意编码重复校验
     *
     * @param salSalesOrder 销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesOrder(SalSalesOrder o) {
        Long customerSid = o.getCustomerSid();
        String customerGroup = basCustomerMapper.selectById(customerSid).getCustomerGroup();
        if (YCX_CUSTOMER.equals(customerGroup)) {
            if (o.getCustomerNameRemark() == null) {
                throw new CustomException("客户名称备注,不能为空");
            }
        }
        changePrice(o);
        //确认时判断
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            judgeNull(o);
        }
        // 校验明细数量
        if (CollectionUtil.isNotEmpty(o.getSalSalesOrderItemList())) {
            o.getSalSalesOrderItemList().forEach(item->{
                if (item.getQuantity() == null || BigDecimal.ZERO.compareTo(item.getQuantity()) >= 0) {
                    throw new CustomException("订单明细的订单量必须大于0");
                }
            });
        }
        //获取销售价
        getSalSale(o);
        setConfirmInfo(o);
        o.setDeliveryStatus("WFH");
        String documentType = o.getDocumentType();
        ConDocTypeSalesOrder conDocTypeSalesOrder = conDocTypeSalesOrderMapper.selectOne(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                .eq(ConDocTypeSalesOrder::getCode, documentType)
        );
        if (ConstantsEms.YES.equals(conDocTypeSalesOrder.getIsReturnGoods())) {
            o.setInOutStockStatus("WRK");
        } else {
            o.setInOutStockStatus("WCK");
        }
        //销售订单-明细对象
        List<SalSalesOrderItem> salSalesOrderItemList = o.getSalSalesOrderItemList();
        //初始值赋值
//            setInitial(salSalesOrderItemList);
        if (CollectionUtils.isNotEmpty(salSalesOrderItemList)) {
            updatePurchasePrice(salSalesOrderItemList);
            HashSet<Long> longsSet = new HashSet<>();
            salSalesOrderItemList.forEach(li -> {
                if (li.getSku2GroupSid() != null) {
                    longsSet.add(li.getSku2GroupSid());
                }
            });
            if (longsSet.size() > 3) {
                throw new CustomException("下单商品所属尺码组不允许超过3个，请核查！");
            }
        }
        int row = salSalesOrderMapper.insert(o);
        if (CollectionUtils.isNotEmpty(salSalesOrderItemList)) {
            setItemNum(salSalesOrderItemList);
            addSalSalesOrderItem(o, salSalesOrderItemList);
        }
        //销售订单-附件对象
        List<SalSalesOrderAttachment> salSalesOrderAttachmentList = o.getAttachmentList();
        if (CollectionUtils.isNotEmpty(salSalesOrderAttachmentList)) {
            addSalSalesOrderAttachment(o, salSalesOrderAttachmentList);
        }
        //客户寄售结算单
        inventoryDocument(o);
        //销售订单
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) && StringUtils.isEmpty(o.getSpecialBusCategory())) {
            advancesReceived(o);
        }
        //待办通知
        SalSalesOrder order = salSalesOrderMapper.selectById(o.getSalesOrderSid());
        o.setSalesOrderCode(order.getSalesOrderCode());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(order.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_SALE_ORDER)
                    .setDocumentSid(order.getSalesOrderSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("销售订单" + order.getSalesOrderCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(order.getSalesOrderCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(o);
        }
        List<OperMsg> msgList = new ArrayList<>();
        String type = StrUtil.isBlank(o.getImportType()) ? BusinessType.INSERT.getValue() : o.getImportType();
        MongodbUtil.insertUserLog(o.getSalesOrderSid(),type, msgList, TITLE);
        return row;
    }

    //更新不含税
    public void updatePurchasePrice(List<SalSalesOrderItem> salSalesOrderItemList ){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            salSalesOrderItemList.forEach(o->{
                if(o.getSalePriceTax()!=null&&o.getTaxRate()!=null){
                    o.setSalePrice(o.getSalePriceTax().divide(BigDecimal.ONE.add(o.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
                }else{
                    o.setSalePrice(null);
                }
            });
        }
    }
    //配置档案赋值
    public void setDoc(SalSalesOrder o){
        ConBuTypeSalesOrder conBuTypeSalesOrder = conBuTypeSalesOrderMapper.selectOne(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                .eq(ConBuTypeSalesOrder::getCode, o.getBusinessType())
        );
        if(conBuTypeSalesOrder!=null){
            //配置档案赋值
            o.setDeliveryType(conBuTypeSalesOrder.getDeliveryType())
                    .setInventoryControlMode(conBuTypeSalesOrder.getInventoryControlMode());
            ConDocTypeSalesOrder conDocTypeSalesOrder = conDocTypeSalesOrderMapper.selectOne(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                    .eq(ConDocTypeSalesOrder::getCode, o.getDocumentType())
            );
            if(conDocTypeSalesOrder!=null){
                o.setIsFinanceBookYszg(conDocTypeSalesOrder.getIsFinanceBookYszg())
                        .setIsFinanceBookDsys(conDocTypeSalesOrder.getIsFinanceBookDsys())
                        .setIsManufacture(conDocTypeSalesOrder.getIsManufacture())
                        .setIsFinanceBookYszg(conDocTypeSalesOrder.getIsFinanceBookYszg())
                        .setIsConsignmentSettle(conDocTypeSalesOrder.getIsConsignmentSettle())
                        .setIsReturnGoods(conDocTypeSalesOrder.getIsReturnGoods());
            }
        }
        salSalesOrderMapper.updateById(o);
    }
    public void changeYF(SalSalesOrder o){
        List<SalSalesOrderItem> salSalesOrderItemList = o.getSalSalesOrderItemList();
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            salSalesOrderItemList.forEach(li->{
                if(li.getNewSalePriceTax()!=null){
                    List<FinBookReceiptEstimationItem> finBookReceiptEstimationItems = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>().lambda()
                            .eq(FinBookReceiptEstimationItem::getSalesOrderSid, o.getSalesOrderSid())
                            .eq(FinBookReceiptEstimationItem::getItemNum, li.getItemNum())
                    );
                    finBookReceiptEstimationItems.forEach(m->{
                        m.setCurrencyAmountTax(m.getQuantity().multiply(li.getNewSalePriceTax()));
                        finBookReceiptEstimationItemMapper.updateById(m);
                    });
                    finBookReceiptEstimationItemMapper.update(new FinBookReceiptEstimationItem(),new UpdateWrapper<FinBookReceiptEstimationItem>().lambda()
                            .eq(FinBookReceiptEstimationItem::getSalesOrderSid, o.getSalesOrderSid())
                            .eq(FinBookReceiptEstimationItem::getItemNum,li.getItemNum())
                            .set(FinBookReceiptEstimationItem::getPrice,li.getNewSalePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
                            .set(FinBookReceiptEstimationItem::getPriceTax,li.getNewSalePriceTax())
                    );
                }
            });
        }
    }
    /**
     * 初始化值赋值
     */
    public void setInitial(List<SalSalesOrderItem> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            salSalesOrderItemList.forEach(item->{
                if(item.getInitialQuantity()==null){
                    item.setInitialQuantity(item.getQuantity());
                }
                if(item.getInitialContractDate()==null){
                    item.setInitialContractDate(item.getContractDate());
                }
                if(item.getInitialSalePrice()==null){
                    item.setInitialSalePrice(item.getSalePrice());
                }
                if(item.getInitialSalePriceTax()==null){
                    item.setInitialSalePriceTax(item.getSalePriceTax());
                }
                if(item.getInitialTaxRate()==null){
                    item.setInitialTaxRate(item.getTaxRate());
                }
            });
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(SalSalesOrder o) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, o.getSalesOrderSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SALE_ORDER)
                    .eq(SysTodoTask::getDocumentSid, o.getSalesOrderSid()));
        }
    }
    //获取最新不含税销售价
    public void changePrice(SalSalesOrder o){
        List<SalSalesOrderItem> salSalesOrderItemList = o.getSalSalesOrderItemList();
        if(CollectionUtils.isNotEmpty(salSalesOrderItemList)){
            salSalesOrderItemList.forEach(item->{
                BigDecimal salePriceTax = item.getSalePriceTax();
                BigDecimal taxRate = item.getTaxRate();
                if(salePriceTax!=null&&taxRate!=null){
                    BigDecimal bigDecimal = new BigDecimal(1);
                    BigDecimal trateAll = item.getTaxRate().add(bigDecimal);
                    item.setSalePrice(salePriceTax.divide(trateAll,6,BigDecimal.ROUND_HALF_UP));
                }
            });
        }
    }
    //判断核销状态
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int judgeReceipt(SalSalesOrder o){
        List<FinBookReceiptEstimationItem> finBookReceiptEstimationItems = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>()
                .lambda().eq(FinBookReceiptEstimationItem::getSalesOrderSid, o.getSalesOrderSid())
                .in(FinBookReceiptEstimationItem::getItemNum,o.getItemNumList())
        );
        if(CollectionUtil.isNotEmpty(finBookReceiptEstimationItems)){
            finBookReceiptEstimationItems.forEach(li->{
                if(!li.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)){
                    throw new CustomException("第"+li.getItemNum()+"行，对应的应收暂估流水非“未核销状态”，不允许更新销售价");
                }
            });
        }
        return 1;
    }
    /**
     * 销售订单确认时获取 采购价
     */
    public void getSalSale(SalSalesOrder salSalesOrder){
        String advanceSettleMode=null;
        String handleStatus = salSalesOrder.getHandleStatus();
        String documentType = salSalesOrder.getDocumentType();
        Long saleContractSid = salSalesOrder.getSaleContractSid();
        if(saleContractSid!=null){
            SalSaleContract salSaleContract = salSaleContractMapper.selectById(saleContractSid);
            advanceSettleMode = salSaleContract.getAdvanceSettleMode();
        }
        String mode=advanceSettleMode;
        List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
        if(!HandleStatus.SUBMIT.getCode().equals(salSalesOrder.getHandleStatus())){
            //获取新的价格
            getNewPrice(salSalesOrder,list,advanceSettleMode);
        }
        //不进行一下校验
        if(ConstantsEms.YES.equals(salSalesOrder.getIsSkipJudge())){
            return;
        }
//        if(ADVANCE_SETTLE_MODE_DD.equals(advanceSettleMode)){
//            if(CollectionUtils.isNotEmpty(list)){
//                if(ConstantsEms.CHECK_STATUS.equals(salSalesOrder.getHandleStatus())||HandleStatus.SUBMIT.getCode().equals(salSalesOrder.getHandleStatus())){
//                    if(!exitSale(salSalesOrder.getDocumentType())){
//                        list.forEach(li->{
//                            if(li.getSalePriceTax()==null&&!ConstantsEms.YES.equals(li.getFreeFlag())){
//                                throw new CustomException("物料/商品编码"+li.getMaterialCode()+"，销售价未维护，请到销售价页面进行维护");
//                            }
//                        });
//                    }
//                }
//            }
//        }
        //税率 一致性校验
        List<SalSalesOrderItem> items = list.stream().filter(li -> li.getTaxRate() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(items) && items.size() > 1) {
            for (int i = 0; i < items.size() - 1; i++) {
                if (!items.get(i).getTaxRate().toString().equals(items.get(i + 1).getTaxRate().toString())) {
                    throw new CustomException("存在税率不一致的明细行，请检查！");
                }

            }
        }

    }
    public void getNewPrice(SalSalesOrder salSalesOrder,List<SalSalesOrderItem> list,String advanceSettleMode){
        String handleStatus = salSalesOrder.getHandleStatus();
        String documentType = salSalesOrder.getDocumentType();
        Long saleContractSid = salSalesOrder.getSaleContractSid();
        SalSalePrice salSalePrice = new SalSalePrice();
        BeanCopyUtils.copyProperties(salSalesOrder,salSalePrice);
        ConBuTypeSalesOrder conBuTypeSalesOrder = conBuTypeSalesOrderMapper.selectOne(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                .eq(ConBuTypeSalesOrder::getCode, salSalesOrder.getBusinessType())
        );
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        if (taxRate == null) {
            taxRate = new ConTaxRate();
        }
        List<ConMeasureUnit> unitList = conMeasureUnitMapper.selectList(new QueryWrapper<>());
        Map<String, String> unitNameMap = unitList.stream().collect(Collectors.toMap(ConMeasureUnit::getCode, ConMeasureUnit::getName,
                (value1, value2) -> { return value2; }));
        if(CollectionUtils.isNotEmpty(list)){
            ConTaxRate finalTaxRate = taxRate;
            list.forEach(item->{
                item.setSystemTaxRate(finalTaxRate.getTaxRateValue());
                if(item.getSalePriceTax()==null||(ConstantsEms.NO.equals(conBuTypeSalesOrder.getIsEditPrice())
                        &&ConstantsEms.NO.equals(salSalesOrder.getIsConsignmentSettle())
                        &&ConstantsEms.NO.equals(salSalesOrder.getIsReturnGoods()))
                ){
                    BeanCopyUtils.copyProperties(item,salSalePrice);
                    Long materialSid = salSalePrice.getMaterialSid();
                    BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
                    salSalePrice.setCustomerSid(salSalesOrder.getCustomerSid());
                    String zipperFlag = basMaterial.getZipperFlag();
                    SalSalePriceItem salSalePriceItem = salSalePriceServiceImpl.getNewSalePrice(salSalePrice);
                    if(salSalePriceItem.getSalePriceTax()!=null){
                        if(ConstantsEms.YES.equals(salSalesOrder.getIsConsignmentSettle())||ConstantsEms.YES.equals(salSalesOrder.getIsReturnGoods())){
                            item.setReturnPtin(salSalePriceItem.getSalePriceTax());
                        }
                        item.setSalePriceTax(salSalePriceItem.getSalePriceTax());
                        item.setSalePrice(salSalePriceItem.getSalePriceTax().divide(BigDecimal.ONE.add(salSalePriceItem.getTaxRate()),6, BigDecimal.ROUND_HALF_UP));
                        item.setUnitBase(salSalePriceItem.getUnitBase())
                                .setUnitBaseName(unitNameMap.get(salSalePriceItem.getUnitBase()))
                                .setUnitConversionRate(salSalePriceItem.getUnitConversionRate())
                                .setUnitPriceName(unitNameMap.get(salSalePriceItem.getUnitPrice()))
                                .setUnitPrice(salSalePriceItem.getUnitPrice());
                        if (item.getTaxRate() == null || ConstantsEms.NO.equals(conBuTypeSalesOrder.getIsOnceSale())) {
                            item.setTaxRate(salSalePriceItem.getTaxRate());
                        }
                    }else{
                        /**
                         * 以下5种情况，获取销售价时，若未获取到销售价，则”税率“取值当前系统的默认税率，
                         * “销售价单位、基本计量单位“默认等于对应商品档案的”基本计量单位”，“单位换算比例(销售价单位/基本单位)”默认等于1
                         * 1》勾选“免费”的明细行
                         * 2》订单的单据类型为“备料通知单”（键值：BLTZD）
                         * 3》订单的“业务类型”的”是否允许编辑价格“的值为“是”
                         * 4》订单的“业务类型”的“是否一次性销售”的值为“是”
                         * 5》订单引用的合同的”预收款结算方式“为”按合同“
                         */
                    //    if(exitSale(documentType)||ConstantsEms.YES.equals(conBuTypeSalesOrder.getIsEditPrice())||ConstantsEms.YES.equals(item.getFreeFlag())||ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(advanceSettleMode)||ConstantsEms.YES.equals(conBuTypeSalesOrder.getIsOnceSale())){//备料通知单
                            item.setUnitBase(basMaterial.getUnitBase());
                            item.setUnitBaseName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitPrice(basMaterial.getUnitBase());
                            item.setUnitPriceName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitConversionRate(BigDecimal.ONE);
                            if (item.getTaxRate() == null) {
                                item.setTaxRate(finalTaxRate.getTaxRateValue());
                            }
                    //    }
                    }
                }else{
                    if(item.getUnitBase()==null){
                        BasMaterial basMaterial = basMaterialMapper.selectById(item.getMaterialSid());
                    //    if(exitSale(documentType)||ConstantsEms.YES.equals(conBuTypeSalesOrder.getIsEditPrice())||ConstantsEms.YES.equals(item.getFreeFlag())){//备料通知单
                            item.setUnitBase(basMaterial.getUnitBase());
                            item.setUnitBaseName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitPrice(basMaterial.getUnitBase());
                            item.setUnitPriceName(unitNameMap.get(basMaterial.getUnitBase()));
                            item.setUnitConversionRate(BigDecimal.ONE);
                            if (item.getTaxRate() == null) {
                                item.setTaxRate(finalTaxRate.getTaxRateValue());
                            }
                    //    }
                    }
                }
            });
        }
    }
    public Boolean exitSale(String code){
        List<String> codes = Arrays.asList("BLTZD");//备料通知单
        boolean isMatch = codes.stream().anyMatch(li -> li.equals(code));
        return isMatch;
    }
    //销售订单作废校验
    @Override
    public int disuseJudge(List<Long> sids){
        sids.forEach(id->{
            SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(id);
            String shipmentCategory = salSalesOrder.getDeliveryType();
            if(ConstantsEms.SALE_SHIP.equals(shipmentCategory)){
                List<DelDeliveryNoteItem> documentItemList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>()
                        .lambda().eq(DelDeliveryNoteItem::getSalesOrderSid, id));
                if (CollectionUtil.isNotEmpty(documentItemList)) {
                    Long[] documentSids = documentItemList.stream().map(DelDeliveryNoteItem::getDeliveryNoteSid).toArray(Long[]::new);
                    List<DelDeliveryNote> documentList = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>()
                            .lambda().in(DelDeliveryNote::getDeliveryNoteSid, documentSids)
                            .ne(DelDeliveryNote::getHandleStatus, HandleStatus.INVALID.getCode()));
                    if (CollectionUtil.isNotEmpty(documentList)) {
                        throw new BaseException("该销售订单已存在出货数据，无法作废！");
                    }
                }
            }else{
                List<InvInventoryDocumentItem> invInventoryDocumentItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, id));
                if (CollectionUtil.isNotEmpty(invInventoryDocumentItems)) {
                    List<Long> documentSids = invInventoryDocumentItems.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).collect(toList());
                    List<InvInventoryDocument> invInventoryDocuments = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                            .in(InvInventoryDocument::getInventoryDocumentSid, documentSids)
                            .eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                            .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG));
                    if(CollectionUtil.isNotEmpty(invInventoryDocuments)){
                        throw new CustomException("此销售订单已存在出库数据，无法作废！");
                    }
                }
                String advanceSettleMode = salSalesOrder.getAdvanceSettleMode();
                if (salSalesOrder.getSaleContractSid() != null) {
                    SalSaleContract salSaleContract = salSaleContractMapper.selectSalSaleContractById(salSalesOrder.getSaleContractSid());
                    if (salSaleContract != null && salSaleContract.getAccountsMethodGroup() != null) {
                        //预付款方式组合
                        ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(salSaleContract.getAccountsMethodGroup());
                        if (accountMethodGroup != null && accountMethodGroup.getAdvanceRate() != null
                                && ConstantsEms.ADVANCE_SETTLE_MODE_DD.equals(advanceSettleMode) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                            List<FinRecordAdvanceReceipt> finRecordAdvanceReceipts = finRecordAdvanceReceiptMapper.selectList(new QueryWrapper<FinRecordAdvanceReceipt>().lambda()
                                    .eq(FinRecordAdvanceReceipt::getSaleOrderSid, id)
                            );
                            if (CollectionUtil.isNotEmpty(finRecordAdvanceReceipts)) {
                                List<Long> longs = finRecordAdvanceReceipts.stream().map(li -> li.getRecordAdvanceReceiptSid()).collect(Collectors.toList());
                                List<FinRecordAdvanceReceiptItem> finRecordAdvanceReceiptItems = finRecordAdvanceReceiptItemMapper.selectList(new QueryWrapper<FinRecordAdvanceReceiptItem>().lambda()
                                        .in(FinRecordAdvanceReceiptItem::getRecordAdvanceReceiptSid, longs)
                                );
                                if (CollectionUtil.isNotEmpty(finRecordAdvanceReceiptItems)) {
                                    finRecordAdvanceReceiptItems.forEach(li -> {
                                        if (!ConstantsEms.CLEAR_STATUS_WHX.equals(li.getClearStatus())) {
                                            throw new CustomException("销售订单" + salSalesOrder.getSalesOrderCode() + "，对应的客户待收预收款流水非“未核销”状态，不允许作废！");
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        });
        return 1;
    }

    //销售订单作废
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int disuse( OrderInvalidRequest request){
        List<Long> sids = request.getSids();
        String explain = request.getExplain();
        sids.forEach(id->{
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            salSalesOrder.setSalesOrderSid(id)
                    .setCancelRemark(explain)
                    .setHandleStatus(HandleStatus.INVALID.getCode());
            salSalesOrderMapper.updateById(salSalesOrder);
            finRecordAdvanceReceiptMapper.update(new FinRecordAdvanceReceipt(),new UpdateWrapper<FinRecordAdvanceReceipt>().lambda()
                    .eq(FinRecordAdvanceReceipt::getSaleOrderSid, id)
                    .set(FinRecordAdvanceReceipt::getHandleStatus,HandleStatus.INVALID.getCode())
            );
            // 明细状态
            salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                    .eq(SalSalesOrderItem::getSalesOrderSid, id)
                    .set(SalSalesOrderItem::getItemStatus, ConstantsEms.STATUS_INVALID_STATUS));
            // 操作日志
            MongodbUtil.insertApprovalLog(id, BusinessType.CANCEL.getValue(),explain);
        });
        return 1;
    }

    // 明细作废
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int itemDisuse(OrderInvalidRequest request){
        int row = 0;
        List<Long> sids = request.getSids();
        if (sids == null || sids.size() == 0) {
            throw new BaseException("请选择行！");
        }
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.selectOrderItemListBy(new SalSalesOrderItem()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSalesOrderItemSidList(sids.stream().toArray(Long[]::new)));
        if (CollectionUtil.isNotEmpty(itemList) && itemList.size() == sids.size()) {
            // 明细状态
            row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                    .in(SalSalesOrderItem::getSalesOrderItemSid, sids)
                    .set(SalSalesOrderItem::getItemStatus, ConstantsEms.STATUS_INVALID_STATUS));
        }
        else {
            throw new BaseException("仅已确认状态的订单明细才可以作此操作！");
        }
        return row;
    }

    /**
     * 拷贝销售订单
     *
     */
    @Override
    public SalSalesOrder copy(Long salesOrderSid){
        SalSalesOrder salSalesOrder = selectSalSalesOrderById(salesOrderSid);
        salSalesOrder.setCreateDate(new Date())
                .setClientId(null)
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setHandleStatus(ConstantsEms.SAVA_STATUS)
                .setSalesOrderSid(null)
                .setInOutStockStatus(null)
                .setIsReturnGoods(null)
                .setIsConsignmentSettle(null)
                .setIsFinanceBookYszg(null)
                .setIsFinanceBookDsys(null)
                .setIsManufacture(null)
                .setTrustorAccount(null)
                .setDeliveryType(null)
                .setInventoryControlMode(null)
                .setTrustorAccountName(null)
                .setSalesOrderCode(null)
                .setCarrier(null)
                .setCarrierName(null)
                .setCarrierNoteCode(null)
                .setIsPushOtherSystem1(null)
                .setIsPushOtherSystem2(null)
                .setOtherSystemInOutStockOrder(null)
                .setOtherSystemStorehouseCode(null)
                .setOtherSystemSaleOrder(null)
                .setPushResultOtherSystem1(null)
                .setPushResultOtherSystem2(null)
                .setPushReturnMsgOtherSystem1(null)
                .setPushReturnMsgOtherSystem2(null)
                .setPushTimeOtherSystem1(null)
                .setPushTimeOtherSystem2(null)
                .setUpdaterAccount(null)
                .setCreatorAccount(null)
                .setCreatorAccountName(null)
                .setConfirmerAccount(null)
                .setConfirmerAccountName(null)
                .setConfirmDate(null)
                .setUpdateDate(null)
                .setDocumentDate(new Date());
        List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
        list.forEach(li->{
            li.setCreateDate(new Date())
                    .setSalesOrderSid(null)
                    .setSalePriceTax(null)
                    .setSalePrice(null)
                    .setUnitBase(null)
                    .setUnitBaseName(null)
                    .setUnitConversionRate(null)
                    .setTaxRate(null)
                    .setTaxRateName(null)
                    .setClientId(null)
                    .setUnitPrice(null)
                    .setUnitPriceName(null)
                    .setInOutStockStatus(null)
                    .setUnitConversionRate(null)
                    .setSalesOrderItemSid(null)
                    .setCreateDate(null)
                    .setUpdateDate(null)
                    .setItemNum(null)
                    .setNewContractDate(null)
                    .setNewSalePriceTax(null)
                    .setNewSalePrice(null)
                    .setNewQuantity(null)
                    .setInitialSalePriceTax(null)
                    .setInitialSalePrice(null)
                    .setInitialContractDate(null)
                    .setToexpireDays(null)
                    .setInitialQuantity(null)
                    .setCreatorAccountName(null)
                    .setUpdaterAccount(null)
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            li.setDeliveryPlanList(new ArrayList<>());
        });
        salSalesOrder.setSalSalesOrderItemList(list);
        salSalesOrder.setIsSkipJudge(ConstantsEms.YES);
        getSalSale(salSalesOrder);
        salSalesOrder.setAttachmentList(new ArrayList<>());
        return salSalesOrder;
    }
    //面辅料状态、甲供料状态
    @Override
    public int changeStatus(OrderItemStatusRequest order){
        int row=0;
        if(TYPE_MFL.equals(order.getType())){
            row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>()
                    .lambda()
                    .in(SalSalesOrderItem::getSalesOrderItemSid, order.getOrderItemSidList())
                    .set(SalSalesOrderItem::getYclBeiliaoStatus, order.getYclBeiliaoStatus())
                    .set(SalSalesOrderItem::getYclCaigouxiadanStatus, order.getYclCaigouxiadanStatus())
                    .set(SalSalesOrderItem::getYclQitaoRemark, order.getYclQitaoRemark())
                    .set(SalSalesOrderItem::getYclQitaoStatus, order.getYclQitaoStatus())
                    .set(SalSalesOrderItem::getYclXugouStatus, order.getYclXugouStatus())
            );
        }else{
            row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>()
                    .lambda()
                    .in(SalSalesOrderItem::getSalesOrderItemSid, order.getOrderItemSidList())
                    .set(SalSalesOrderItem::getKglDaoliaoStatus, order.getKglDaoliaoStatus())
                    .set(SalSalesOrderItem::getKglShenqingStatus, order.getKglShenqingStatus())
            );
        }
        return row;
    }
    /**
     * 审批/确认 执行成功后操作
     */
    public void changeValue(SalSalesOrder salSalesOrder){
        List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrder.getSalSalesOrderItemList();
        salSalesOrderItemList.forEach(li->{
            if(li.getNewContractDate()!=null){
                li.setContractDate(li.getNewContractDate());
                li.setNewContractDate(null);
            }
            if(li.getNewSalePriceTax()!=null){
                li.setSalePriceTax(li.getNewSalePriceTax());
                li.setNewSalePriceTax(null);
            }
            if(li.getNewSalePrice()!=null){
                li.setSalePrice(li.getNewSalePrice());
                li.setNewSalePrice(null);
            }
            if(li.getNewQuantity()!=null){
                li.setQuantity(li.getNewQuantity());
                li.setNewQuantity(null);
            }
            if(li.getNewTaxRate()!=null){
                li.setTaxRate(li.getNewTaxRate());
                li.setNewTaxRate(null);
            }
        });
        setInitial(salSalesOrderItemList);
    }


    private void inventoryDocument(SalSalesOrder o) {
        Boolean isDevlievry=false;
        List<SalSalesOrderItem> salSalesOrdeList= o.getSalSalesOrderItemList();
        List<SalSalesOrderItem> items = salSalesOrdeList.stream().filter(li -> li.getReferDocSid() != null&&"DeliveryNote".equals(li.getReferDocCategory())).collect(Collectors.toList());
        //判断是否来自交货单
        if(CollectionUtil.isNotEmpty(items)){
            isDevlievry=true;
        }
        if ((HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) &&
                ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(o.getSpecialBusCategory()))||(isDevlievry &&HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()))) {
            SalSalesOrder salSalesOrder = selectSalSalesOrderById(o.getSalesOrderSid());
            if(salSalesOrder.getStorehouseSid()==null){
                throw new CustomException("确认时，仓库不允许为空！");
            }
            if(salSalesOrder.getStorehouseLocationSid()==null){
                throw new CustomException("确认时，库位不允许为空！");
            }
            List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrder.getSalSalesOrderItemList();
            if(!isDevlievry){
                //生成预收款台账
                advancesReceived(o);
            }
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            BeanCopyUtils.copyProperties(o, invInventoryDocument);
            invInventoryDocument.setReferDocCategory("SO");//关联单据类别
            invInventoryDocument.setCustomerSid(o.getCustomerSid());
            invInventoryDocument.setDocumentCategory("CK"); //库存凭证类别
            invInventoryDocument.setProductSeasonSid(o.getProductSeasonSid());
            invInventoryDocument.setAccountDate(new Date());//入库日期
            invInventoryDocument.setDocumentDate(new Date());//单据日期
            invInventoryDocument.setType("1");//出库
            invInventoryDocument.setDocumentType("CG");//常规
            invInventoryDocument.setIsFinanceBookYszg("Y");
            invInventoryDocument.setMovementType(isDevlievry?"SC011":"SC24");//作业类型
            invInventoryDocument.setSpecialStock(isDevlievry?null:ConstantsEms.CUS_VE);//特殊库存：客户寄售
            invInventoryDocument.setCreateDate(new Date());
            invInventoryDocument.setReferDocumentSid(o.getSalesOrderSid());
            String salesOrderCode = salSalesOrderMapper.selectById(o.getSalesOrderSid()).getSalesOrderCode();
            invInventoryDocument.setReferDocumentCode(salesOrderCode);
            invInventoryDocument.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            List<InvInventoryDocumentItem> inventoryDocumentItemList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(salSalesOrderItemList)) {
                for (SalSalesOrderItem salSalesOrderItem : salSalesOrderItemList) {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(salSalesOrderItem, invInventoryDocumentItem);
                    invInventoryDocumentItem.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
                    //关联业务单sid
                    invInventoryDocumentItem.setReferDocumentSid(o.getSalesOrderSid());
                    //关联业务单code
                    invInventoryDocumentItem.setReferDocumentCode(String.valueOf(o.getSalesOrderCode()));
                    //关联业务单行sid
                    invInventoryDocumentItem.setReferDocumentItemSid(salSalesOrderItem.getSalesOrderItemSid());
                    //关联业务单行code
                    invInventoryDocumentItem.setReferDocumentItemNum(salSalesOrderItem.getItemNum());
                    invInventoryDocumentItem.setInvPrice(salSalesOrderItem.getSalePrice());
                    invInventoryDocumentItem.setPrice(salSalesOrderItem.getSalePriceTax());
                    invInventoryDocumentItem.setInvPriceTax(salSalesOrderItem.getSalePriceTax());
                    //数量
                    invInventoryDocumentItem.setQuantity(salSalesOrderItem.getUnitConversionRate()!=null?salSalesOrderItem.getUnitConversionRate().multiply(salSalesOrderItem.getQuantity()):salSalesOrderItem.getQuantity());
                    invInventoryDocumentItem.setPriceQuantity(salSalesOrderItem.getQuantity());
                    invInventoryDocumentItem.setCreateDate(new Date());
                    // 仓库库位
                    invInventoryDocumentItem.setStorehouseSid(o.getStorehouseSid())
                            .setStorehouseLocationSid(o.getStorehouseLocationSid());
                    inventoryDocumentItemList.add(invInventoryDocumentItem);
                }
            }

            invInventoryDocument.setInvInventoryDocumentItemList(inventoryDocumentItemList);
            InvInventoryDocumentimpl.insertInvInventoryDocument(invInventoryDocument);
        }
    }
    //确认时判断
    public void judgeNull(SalSalesOrder o){
        if(!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredSaleOrderContract())
                && o.getSaleContractCode()==null){
            throw new CustomException("销售合同号不允许为空！");
        }
        List<SalSalesOrderItem> list = o.getSalSalesOrderItemList();
        if(CollectionUtil.isEmpty(list)){
            throw new CustomException("明细行不允许为空！");
        }
        if(!ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(o.getSpecialBusCategory())){
            list.forEach(li->{
                if(li.getContractDate()==null){
                    throw new CustomException("明细行的合同交期不能为空");
                }
            });
        }
        //确认后 对新的值进行操作
        List<SalSalesOrderItem> items = list.stream().filter(m -> m.getNewQuantity() != null).collect(Collectors.toList());
        //存在 新销售量(变更中)
        if(CollectionUtil.isNotEmpty(items)){
            List<DelDeliveryNote> delDeliveryNotes = delDeliveryNoteMapper.selectList(new QueryWrapper<DelDeliveryNote>().lambda()
                    .eq(DelDeliveryNote::getSalesOrderSid, o.getSalesOrderSid())
            );
            list.forEach(li->{
                List<DelDeliveryNoteItem> deliveryList = new ArrayList<>();
                if(li.getNewQuantity()!=null){
                    BigDecimal total=BigDecimal.ZERO;
                    //已出库量
                    BigDecimal sumYCK=BigDecimal.ZERO;
                    String shipmentCategory = o.getDeliveryType();
                    String documentType = o.getDocumentType();
                    if(ConstantsEms.SALE_SHIP.equals(shipmentCategory)){
                        //获取该订单的销售发货单的所有明细信息
                        delDeliveryNotes.forEach(de->{
                            List<DelDeliveryNoteItem> delDeliveryNoteItems = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                                    .eq(DelDeliveryNoteItem::getDeliveryNoteSid, de.getDeliveryNoteSid())
                            );
                            delDeliveryNoteItems.forEach(l->{
                                if(de.getInOutStockStatus()!=null){
                                    l.setInOutStockStatus(de.getInOutStockStatus());
                                }
                            });
                            deliveryList.addAll(delDeliveryNoteItems);
                        });
                        if(CollectionUtil.isNotEmpty(deliveryList)){
                            List<DelDeliveryNoteItem> itemListW=null;
                            List<DelDeliveryNoteItem> itemListY=null;
                            if(ConstantsEms.YES.equals(o.getIsReturnGoods())){
                                //已出库量
                                itemListW = deliveryList.stream()
                                        .filter(m -> m.getSalesOrderItemSid().toString().equals(li.getSalesOrderItemSid().toString())&&ConstantsEms.IN_STORE_STATUS_NOT.equals(m.getInOutStockStatus())
                                        ).collect(Collectors.toList());
                                //已出库量
                                itemListY = deliveryList.stream()
                                        .filter(m -> m.getSalesOrderItemSid().toString().equals(li.getSalesOrderItemSid().toString())&&!ConstantsEms.IN_STORE_STATUS_NOT.equals(m.getInOutStockStatus())
                                        ).collect(Collectors.toList());
                            }else{
                                //未出库量
                                itemListW = deliveryList.stream()
                                        .filter(m -> m.getSalesOrderItemSid().toString().equals(li.getSalesOrderItemSid().toString())&&ConstantsEms.OUT_STORE_STATUS_NOT.equals(m.getInOutStockStatus())
                                        ).collect(Collectors.toList());
                                //已出库量
                                itemListY = deliveryList.stream()
                                        .filter(m -> m.getSalesOrderItemSid().toString().equals(li.getSalesOrderItemSid().toString())&&!ConstantsEms.OUT_STORE_STATUS_NOT.equals(m.getInOutStockStatus())
                                        ).collect(Collectors.toList());
                            }
                            if(CollectionUtil.isNotEmpty(itemListW)){
                                itemListW=itemListW.stream().filter(h->h.getDeliveryQuantity()!=null).collect(Collectors.toList());
                                BigDecimal sum = itemListW.stream().map(h -> h.getDeliveryQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                total=total.add(sum);
                            }
                            if(CollectionUtil.isNotEmpty(itemListY)){
                                itemListY=itemListY.stream().filter(h->h.getInOutStockQuantity()!=null).collect(Collectors.toList());
                                BigDecimal sum = itemListY.stream().map(h -> h.getInOutStockQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                sumYCK=sum;
                                total=total.add(sum);
                            }
                        }
                    }else{
                        List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                .eq(InvInventoryDocumentItem::getReferDocumentItemSid, li.getSalesOrderItemSid())
                        );
                        if(CollectionUtil.isNotEmpty(itemList)){
                            BigDecimal sum = itemList.stream().map(m ->
                            {
                                if(m.getPriceQuantity()!=null){
                                    return  m.getPriceQuantity();
                                }else{
                                    return m.getQuantity();
                                }
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);
                            total=sum;
                            sumYCK=sum;
                        }
                    }
                    if(li.getNewQuantity().compareTo(total)==-1){
                        throw new CustomException("第"+li.getItemNum()+"行，新订单量小于订单已发货量");
                    }
                    if(ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())){
                        if (li.getNewQuantity().compareTo(sumYCK) == 1) {
                            if (ConstantsEms.YES.equals(o.getIsReturnGoods())) {
                                if (sumYCK.compareTo(BigDecimal.ZERO) == 0) {
                                    li.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_NOT);
                                } else {
                                    li.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_LI);
                                }
                            } else {
                                if (sumYCK.compareTo(BigDecimal.ZERO) == 0) {
                                    li.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_NOT);
                                } else{
                                    li.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_LI);
                                }
                            }
                        } else {
                            if (ConstantsEms.YES.equals(o.getIsReturnGoods())) {
                                li.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS);
                            } else {
                                li.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS);
                            }

                        }
                    }
                }
            });
        }
        if(ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())){
            changeValue(o);
        }

    }

    //生成预收款台账
    private void advancesReceived(SalSalesOrder o) {
        Boolean isDevlievry=false;
        List<SalSalesOrderItem> salSalesOrdeList= o.getSalSalesOrderItemList();
        List<SalSalesOrderItem> itemOrders = salSalesOrdeList.stream().filter(li -> li.getReferDocSid() != null&&ConstantsEms.Delivery_Note.equals(li.getReferDocCategory())).collect(Collectors.toList());
        //判断是否来自交货单
        if(CollectionUtil.isNotEmpty(itemOrders)){
            isDevlievry=true;
        }
        String isBusinessFinance = ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance();
        if(!isDevlievry){
            if(ConstantsEms.YES.equals(isBusinessFinance)){
                //1.预付款结算方式是否选择：按合同   2.预付款比例是否大于0   3.合同类型是否选择：标准合同，如是则合同金额需大于0
                if (ConstantsEms.YES.equals(o.getIsFinanceBookDsys()) && o.getSaleContractSid() != null) {
                    //销售合同信息
                    SalSaleContract salSaleContract = salSaleContractMapper.selectSalSaleContractById(o.getSaleContractSid());
                    if (salSaleContract != null) {
                        //预付款方式组合
                        ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(salSaleContract.getAccountsMethodGroup());
                        //预付款结算方式
                        if (ConstantsEms.ADVANCE_SETTLE_MODE_DD.equals(salSaleContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                            //凭证日期
                            FinRecordAdvanceReceipt finRecordAdvanceReceipt = new FinRecordAdvanceReceipt();
                            BeanCopyUtils.copyProperties(salSaleContract, finRecordAdvanceReceipt);
                            finRecordAdvanceReceipt.setDocumentDate(new Date());
                            finRecordAdvanceReceipt.setBookType(ConstantsFinance.BOOK_TYPE_YUS);
                            finRecordAdvanceReceipt.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_SO);
                            finRecordAdvanceReceipt.setSettleMode(ADVANCE_SETTLE_MODE_DD);
                            finRecordAdvanceReceipt.setSaleContractSid(salSaleContract.getSaleContractSid());
                            finRecordAdvanceReceipt.setSaleContractCode(salSaleContract.getSaleContractCode());
                            finRecordAdvanceReceipt.setSaleOrderSid(o.getSalesOrderSid());
                            finRecordAdvanceReceipt.setProductSeasonSid(o.getProductSeasonSid());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(o.getDocumentDate());
                            finRecordAdvanceReceipt.setSalePerson(o.getSalePerson());
                            finRecordAdvanceReceipt.setMaterialType(o.getMaterialType());
                            finRecordAdvanceReceipt.setPaymentYear(calendar.get(Calendar.YEAR));
                            finRecordAdvanceReceipt.setPaymentMonth(calendar.get(Calendar.MONTH));
                            finRecordAdvanceReceipt.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                            finRecordAdvanceReceipt.setCurrencyAmountTaxContract(salSaleContract.getCurrencyAmountTax());//合同金额
                            finRecordAdvanceReceipt.setAdvanceRate(new BigDecimal(accountMethodGroup.getAdvanceRate()));//预收款比例
                            finRecordAdvanceReceipt.setCreateDate(new Date());
                            List<SalSalesOrderItem> salSalesOrderItemList = new ArrayList<>();
                            salSalesOrderItemList = o.getSalSalesOrderItemList();
                            if (CollectionUtils.isEmpty(salSalesOrderItemList)) {
                                salSalesOrderItemList = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>()
                                        .lambda().eq(SalSalesOrderItem::getSalesOrderSid, o.getSalesOrderSid()));
                            }
                            BigDecimal init = null;
                            BigDecimal amount = BigDecimal.ZERO;
                            FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem = new FinRecordAdvanceReceiptItem();
                            if (CollectionUtils.isNotEmpty(salSalesOrderItemList)) {
                                for (int i = 0; i < salSalesOrderItemList.size(); i++) {
                                    SalSalesOrderItem salSalesOrderItem = salSalesOrderItemList.get(i);
                                    if (salSalesOrderItem.getSalePriceTax() != null&&!ConstantsEms.YES.equals(salSalesOrderItem.getFreeFlag())) {
                                        //金额：销售量*销售价（含税）
                                        init = salSalesOrderItem.getQuantity().multiply(salSalesOrderItem.getSalePriceTax());
                                        amount = init.add(amount);
                                    }
                                    finRecordAdvanceReceiptItem.setTaxRate(salSalesOrderItem.getTaxRate());
                                }
                            }
                            //订单金额
                            finRecordAdvanceReceipt.setCurrencyAmountTaxSo(amount);
                            //预收款比例
                            BigDecimal advanceRate = new BigDecimal(accountMethodGroup.getAdvanceRate());
                            //预收款金额：总金额*预收款比例
                            amount = amount.multiply(advanceRate);
                            List<SalSalesOrderItem> items = salSalesOrderItemList.stream().filter(li -> li.getNewQuantity() != null || li.getNewSalePriceTax() != null).collect(Collectors.toList());
                            //得到原来的流水
                            List<FinRecordAdvanceReceipt> reportFormList = finRecordAdvanceReceiptMapper.getReportForm(new FinRecordAdvanceReceipt().setSaleOrderSid(o.getSalesOrderSid()));
                            if(CollectionUtil.isNotEmpty(reportFormList)){
                                Long recordAdvanceReceiptSid = reportFormList.get(0).getRecordAdvanceReceiptSid();
                                finRecordAdvanceReceipt.setRecordAdvanceReceiptSid(recordAdvanceReceiptSid);
                                finRecordAdvanceReceiptMapper.updateById(finRecordAdvanceReceipt);
                                //原来的流水的待核销金额+已核销金额
                                BigDecimal origin = reportFormList.get(0).getCurrencyAmountTaxYhx().add(reportFormList.get(0).getCurrencyAmountTaxHxz());
                                if (amount.subtract(origin).compareTo(BigDecimal.ZERO) == -1) {
                                    throw new CustomException("订单金额小于后续业务金额，不允许确认！");
                                }
                                finRecordAdvanceReceiptItemMapper.update(new FinRecordAdvanceReceiptItem(),new UpdateWrapper<FinRecordAdvanceReceiptItem>().lambda()
                                        .eq(FinRecordAdvanceReceiptItem::getRecordAdvanceReceiptSid,reportFormList.get(0).getRecordAdvanceReceiptSid())
                                        .set(FinRecordAdvanceReceiptItem::getCurrencyAmountTaxYings,amount)
                                );
                            }
                            if(CollectionUtil.isEmpty(reportFormList)){
                                finRecordAdvanceReceiptMapper.insert(finRecordAdvanceReceipt);
                                //应收金额
                                finRecordAdvanceReceiptItem.setCurrencyAmountTaxYings(amount);
                                finRecordAdvanceReceiptItem.setRecordAdvanceReceiptSid(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
                                finRecordAdvanceReceiptItem.setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO)
                                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                                if (salSaleContract.getYsAccountValidDays() != null){
                                    Date dateValid = new Date();
                                    Calendar calendarValid = new GregorianCalendar();
                                    calendarValid.setTime(dateValid);
                                    calendarValid.add(calendarValid.DATE,salSaleContract.getYsAccountValidDays()); //把日期往后增加i天,整数  往后推,负数往前移动
                                    dateValid = calendarValid.getTime(); //这个时间就是日期往后推i天的结果
                                    finRecordAdvanceReceiptItem.setAccountValidDate(dateValid);
                                }
                                finRecordAdvanceReceiptItemMapper.insert(finRecordAdvanceReceiptItem);
                            }
                        }
                    }
                }
            }
        }
    }
    public void setNull(List<SalSalesOrderItem> salSalesOrderItemList){
        salSalesOrderItemList.forEach(li->{
            li.setNewQuantity(null);
            li.setNewContractDate(null);
        });
    }
    /**
     * 录入合同号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setConstract(SalSalesOrder order){
        List<SalSalesOrder> salSalesOrderList = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid, order.getSalesOrderSids())
        );
        SalSaleContract contract = new SalSaleContract();
        String mode = ApiThreadLocalUtil.get().getSysUser().getClient().getSaleOrderContractEnterMode();
        if (!ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(mode)) {
            // 查找出 现在选择的 合同
            if (order.getSaleContractSid() != null) {
                contract = salSaleContractMapper.selectById(order.getSaleContractSid());
                if (contract == null) {
                    throw  new BaseException("该合同已被删除，请重新查询");
                }
            }
        }
        // 如果现在的 合同 不是 临时合同，则才考虑要去 清待办
        if (!ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(contract.getContractPurpose())) {
            // 筛选出旧数据 中有 合同 的 订单
            List<SalSalesOrder> salSalesOrders = salSalesOrderList.stream().filter(o -> o.getSaleContractSid() != null).collect(toList());
            if (CollectionUtil.isNotEmpty(salSalesOrders)) {
                // 拿出有合同的订单的 所有合同号
                List<Long> oldContractSidList = salSalesOrders.stream().map(SalSalesOrder::getSaleContractSid)
                        .distinct().collect(toList());
                // 得到 原订单的原合同 中 是 临时合同 的 合同
                List<SalSaleContract> saleContractList = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>().lambda()
                        .in(SalSaleContract::getSaleContractSid, oldContractSidList)
                        .eq(SalSaleContract::getContractPurpose, ConstantsOrder.CONTRACT_PURPOSE_LSGD));
                if (CollectionUtil.isNotEmpty(saleContractList)) {
                    Map<Long, SalSaleContract> map = saleContractList.stream().collect(Collectors.toMap(SalSaleContract::getSaleContractSid, Function.identity()));
                    // 存放需要删除待办的订单sid
                    List<Long> orderSidList = new ArrayList<>();
                    salSalesOrders.forEach(item -> {
                        // 如果该订单的合同 存在于 临时合同
                        if (map.get(item.getSaleContractSid()) != null) {
                            orderSidList.add(item.getSalesOrderSid());
                        }
                    });
                    if (CollectionUtil.isNotEmpty(orderSidList)) {
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .in(SysTodoTask::getDocumentSid, orderSidList).eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SALE_ORDER + "-" + ConstantsTable.TABLE_SALE_CONTRACT)
                                .like(SysTodoTask::getTitle, "使用的是过渡合同"));
                    }
                }
            }
        }
        int row = salSalesOrderMapper.update(new SalSalesOrder(), new UpdateWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid, order.getSalesOrderSids())
                .set(SalSalesOrder::getSaleContractSid, order.getSaleContractSid())
                .set(SalSalesOrder::getSaleContractCode, order.getSaleContractCode())
        );
        salSalesOrderList.forEach(item->{
            if ((item.getSaleContractCode() != null && !item.getSaleContractCode().equals(order.getSaleContractCode()))
                    || (item.getSaleContractCode() == null && order.getSaleContractCode() != null)) {
                String contractCode = item.getSaleContractCode() == null ? "" : item.getSaleContractCode();
                String newCode = order.getSaleContractCode() == null ? "" : order.getSaleContractCode();
                String remark = "销售合同号更新，更新前：" + contractCode + "，更新后："+ newCode;
                MongodbUtil.insertUserLog(item.getSalesOrderSid(), BusinessType.QITA.getValue(), null, TITLE, remark);
            }
        });
        return row;
    }

    /**
     * 变更合同号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setConstractCode(SalSalesOrder order) {
        int row = 1;
        SalSalesOrder salesOrder = salSalesOrderMapper.selectById(order.getSalesOrderSid());
        if (salesOrder == null) {
            throw new BaseException("该销售订单不存在");
        }
        if (salesOrder.getHandleStatus().equals(ConstantsEms.SAVA_STATUS)) {
            throw new BaseException("保存状态不允许变更合同号操作");
        }
        if (salesOrder.getSaleContractCode() != null && salesOrder.getSaleContractCode().equals(order.getSaleContractCode())) {
            return row;
        }
        LambdaUpdateWrapper<SalSalesOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SalSalesOrder::getSaleContractCode, order.getSaleContractCode());
        updateWrapper.set(SalSalesOrder::getSaleContractSid, order.getSaleContractSid());
        updateWrapper.eq(SalSalesOrder::getSalesOrderSid, order.getSalesOrderSid());
        row = salSalesOrderMapper.update(new SalSalesOrder(), updateWrapper);
        String contractCode = salesOrder.getSaleContractCode() == null ? "" : salesOrder.getSaleContractCode();
        String newCode = order.getSaleContractCode() == null ? "" : order.getSaleContractCode();
        String remark = "销售合同号变更，更新前：" + contractCode + "，更新后："+ newCode;
        MongodbUtil.insertUserLog(order.getSalesOrderSid(), BusinessType.QITA.getValue(), null, TITLE, remark);
        return row;
    }

    /**
     * 提交时校验
     */
    @Override
    public int processCheck(Long salesOrderSid){
        SalSalesOrder salSalesOrder = selectSalSalesOrderById(salesOrderSid);
        salSalesOrder.setHandleStatus(HandleStatus.SUBMIT.getCode());
        List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
        if(!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredSaleOrderContract())
                && salSalesOrder.getSaleContractCode()==null){
            throw new CustomException("提交时，销售合同号不允许为空！");
        }
        if(CollectionUtil.isEmpty(list)){
            throw new CustomException("提交时，明细行不允许为空！");
        }
        if(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())){
            if(salSalesOrder.getStorehouseSid()==null){
                throw new CustomException("提交时，仓库不允许为空！");
            }
            if(salSalesOrder.getStorehouseLocationSid()==null){
                throw new CustomException("提交时，库位不允许为空！");
            }
        }
        if(!ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())){
            list.forEach(li->{
                if(li.getContractDate()==null){
                    throw new CustomException("提交时，明细行的合同交期不能为空");
                }

            });
        }
        list.forEach(li->{
            if(li.getQuantity()==null){
                throw new CustomException("提交时，明细行的销售量不能为空");
            }
        });
        List<Long> longs = list.stream().map(li -> li.getBarcodeSid()).collect(Collectors.toList());
        List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                .in(BasMaterialBarcode::getBarcodeSid, longs)
        );
        basMaterialBarcodes.forEach(m->{
            if(!m.getStatus().equals(ConstantsEms.SAVA_STATUS)){
                throw new CustomException(salSalesOrder.getSalesOrderCode()+"，存在停用的商品条码"+m.getBarcode()+"，请检查！");
            }
        });
        getSalSale(salSalesOrder);
        if(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())){
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            BeanCopyUtils.copyProperties(salSalesOrder, invInventoryDocument);
            invInventoryDocument.setReferDocCategory("SO");//关联单据类别
            invInventoryDocument.setDocumentCategory("CK"); //库存凭证类别
            invInventoryDocument.setAccountDate(new Date());//入库日期
            invInventoryDocument.setDocumentDate(new Date());//单据日期
            invInventoryDocument.setType("1");//出库
            invInventoryDocument.setDocumentType("CG");//常规
            invInventoryDocument.setMovementType("SC24");//作业类型
            invInventoryDocument.setSpecialStock(ConstantsEms.CUS_VE);//特殊库存：客户寄售
            invInventoryDocument.setCreateDate(new Date());
            invInventoryDocument.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            List<InvInventoryDocumentItem> inventoryDocumentItemList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(list)) {
                for (SalSalesOrderItem salSalesOrderItem : list) {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(salSalesOrderItem, invInventoryDocumentItem);
                    invInventoryDocumentItem.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
                    invInventoryDocumentItem.setInvPrice(salSalesOrderItem.getSalePrice());
                    invInventoryDocumentItem.setInvPriceTax(salSalesOrderItem.getSalePriceTax());
                    invInventoryDocumentItem.setQuantity(salSalesOrderItem.getQuantity());
                    invInventoryDocumentItem.setItemNum(salSalesOrderItem.getItemNum());
                    invInventoryDocumentItem.setCreateDate(new Date());
                    BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(salSalesOrderItem.getMaterialSid());
                    inventoryDocumentItemList.add(invInventoryDocumentItem);
                }
            }
            Map<Long, Object> oldLocation=new HashMap<>();
            oldLocation.put(1L,"order");
            InvInventoryDocumentimpl.vatatil(oldLocation, invInventoryDocument, inventoryDocumentItemList);
        }
        return 1;
    }

    //多笔提交
    @Override
    public EmsResultEntity checkList(OrderErrRequest request) {
        List<CommonErrMsgResponse> errList = new ArrayList<>();
        List<CommonErrMsgResponse> warnList = new ArrayList<>();
        List<Long> sidList = request.getSidList();
        for (Long sid : sidList) {
            SalSalesOrder salSalesOrder = selectSalSalesOrderById(sid);
            salSalesOrder.setHandleStatus(HandleStatus.SUBMIT.getCode());
            List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
            if (!ConstantsEms.NO.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getIsRequiredSaleOrderContract())
                    && salSalesOrder.getSaleContractCode() == null) {
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                errMsgResponse.setMsg("销售合同号不允许为空！");
                errList.add(errMsgResponse);
            }
            if (CollectionUtil.isEmpty(list)) {
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                errMsgResponse.setMsg("明细行不允许为空！");
                errList.add(errMsgResponse);
            }
            if (ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())) {
                if (salSalesOrder.getStorehouseSid() == null) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                    errMsgResponse.setMsg("仓库不允许为空！");
                    errList.add(errMsgResponse);
                }
                if (salSalesOrder.getStorehouseLocationSid() == null) {
                    //  throw new CustomException("提交时，库位不允许为空！");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                    errMsgResponse.setMsg("库位不允许为空！");
                    errList.add(errMsgResponse);
                }
            }
            if (!ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())) {
                List<SalSalesOrderItem> items = list.stream().filter(li -> li.getContractDate() == null).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(items)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                    errMsgResponse.setMsg("明细行的合同交期不能为空");
                    errList.add(errMsgResponse);
                }
            }
            List<SalSalesOrderItem> items = list.stream().filter(li -> li.getQuantity() == null).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(items)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                errMsgResponse.setMsg("明细行的销售量不能为空");
                errList.add(errMsgResponse);
            }
            if(CollectionUtils.isNotEmpty(list)){
                List<SalSalesOrderItem> itemOrders = list.stream().filter(li -> li.getUnitBase() == null || li.getUnitPrice() == null
                        || li.getUnitConversionRate() == null || li.getTaxRate() == null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(itemOrders)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                    errMsgResponse.setMsg("存在明细行的基本单位、价格单位、单位换算比例或税率为空，请检查！");
                    errList.add(errMsgResponse);
                }
                List<Long> longs = list.stream().map(li -> li.getBarcodeSid()).collect(Collectors.toList());
                List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.getBasMaterialSkuName(longs);
                basMaterialBarcodes.forEach(m -> {
                    if (!m.getStatus().equals(ConstantsEms.SAVA_STATUS)) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                        String msg=null;
                        if(m.getSku2Name()!=null){
                            msg="商品/物料编码"+m.getMaterialCode()+"，SKU1名称"+m.getSku1Name()+"，SKU2名称"+m.getSku2Name()+"，已被停用，请检查！";
                        }else{
                            msg="商品/物料编码"+m.getMaterialCode()+"，SKU1名称"+m.getSku1Name()+"，已被停用，请检查！";
                        }
                        errMsgResponse.setMsg(msg);
                        errList.add(errMsgResponse);
                    }
                });
                SalSaleContract salSaleContract = null;
                if (salSalesOrder.getSaleContractSid() != null && !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(ApiThreadLocalUtil.get().getSysUser().getClient().getSaleOrderContractEnterMode())) {
                    salSaleContract = salSaleContractMapper.selectOne(new QueryWrapper<SalSaleContract>().lambda()
                            .eq(SalSaleContract::getSaleContractSid, salSalesOrder.getSaleContractSid()));
                    if (salSaleContract == null) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                        errMsgResponse.setMsg("合同号不存在");
                        errList.add(errMsgResponse);
                    }
                    else {
                        if (!salSaleContract.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                            errMsgResponse.setMsg("合同号不是已确认状态");
                            warnList.add(errMsgResponse);
                        }
                        // 判断合同的客户 跟公司 跟订单有没有一致
                        if ((salSaleContract.getCompanySid() != null && !salSaleContract.getCompanySid().equals(salSalesOrder.getCompanySid())
                                || (salSaleContract.getCompanySid() == null && salSalesOrder.getCompanySid() != null))) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                            errMsgResponse.setMsg("所引用合同号的“客户、公司”与订单中的“客户、公司”不一致！");
                            errList.add(errMsgResponse);
                        }
                        else if ((salSaleContract.getCustomerSid() != null && !salSaleContract.getCustomerSid().equals(salSalesOrder.getCustomerSid())
                                || (salSaleContract.getCustomerSid() == null && salSalesOrder.getCustomerSid() != null))) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                            errMsgResponse.setMsg("所引用合同号的“客户、公司”与订单中的“客户、公司”不一致！");
                            errList.add(errMsgResponse);
                        }
//                        String advanceSettleMode = salSaleContract.getAdvanceSettleMode();
//                        String isConsignmentSettle = salSalesOrder.getIsConsignmentSettle();
//                        if(ADVANCE_SETTLE_MODE_DD.equals(advanceSettleMode)|| ConstantsEms.YES.equals(isConsignmentSettle)){
//                            if(CollectionUtils.isNotEmpty(list)){
//                                if(ConstantsEms.CHECK_STATUS.equals(salSalesOrder.getHandleStatus())||HandleStatus.SUBMIT.getCode().equals(salSalesOrder.getImportHandle())||HandleStatus.SUBMIT.getCode().equals(salSalesOrder.getHandleStatus())){
//                                    if(!exitSale(salSalesOrder.getDocumentType())){
//                                        list.forEach(li->{
//                                            if(li.getSalePriceTax()==null&&!ConstantsEms.YES.equals(li.getFreeFlag())){
//                                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
//                                                errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
//                                                errMsgResponse.setMsg("物料/商品编码"+li.getMaterialCode()+"，销售价未维护，请到销售价页面进行维护");
//                                                errList.add(errMsgResponse);
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
                    }
                }
                if (ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(salSalesOrder.getSpecialBusCategory())) {
                    InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
                    BeanCopyUtils.copyProperties(salSalesOrder, invInventoryDocument);
                    invInventoryDocument.setReferDocCategory("SO");//关联单据类别
                    invInventoryDocument.setDocumentCategory("CK"); //库存凭证类别
                    invInventoryDocument.setAccountDate(new Date());//入库日期
                    invInventoryDocument.setDocumentDate(new Date());//单据日期
                    invInventoryDocument.setType("1");//出库
                    invInventoryDocument.setDocumentType("CG");//常规
                    invInventoryDocument.setMovementType("SC24");//作业类型
                    invInventoryDocument.setSpecialStock(ConstantsEms.CUS_VE);//特殊库存：客户寄售
                    invInventoryDocument.setCreateDate(new Date());
                    invInventoryDocument.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    List<InvInventoryDocumentItem> inventoryDocumentItemList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (SalSalesOrderItem salSalesOrderItem : list) {
                            InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                            BeanCopyUtils.copyProperties(salSalesOrderItem, invInventoryDocumentItem);
                            invInventoryDocumentItem.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
                            invInventoryDocumentItem.setInvPrice(salSalesOrderItem.getSalePrice());
                            invInventoryDocumentItem.setInvPriceTax(salSalesOrderItem.getSalePriceTax());
                            invInventoryDocumentItem.setQuantity(salSalesOrderItem.getQuantity().multiply(salSalesOrderItem.getUnitConversionRate()));
                            invInventoryDocumentItem.setItemNum(salSalesOrderItem.getItemNum());
                            invInventoryDocumentItem.setCreateDate(new Date());
                            invInventoryDocumentItem.setStorehouseSid(salSalesOrder.getStorehouseSid())
                                    .setStorehouseLocationSid(salSalesOrder.getStorehouseLocationSid());
                            inventoryDocumentItemList.add(invInventoryDocumentItem);
                        }
                    }
                    Map<Long, Object> oldLocation = new HashMap<>();
                    oldLocation.put(1L, "order");
                    try {
                        InvInventoryDocumentimpl.vatatil(oldLocation, invInventoryDocument, inventoryDocumentItemList);
                    } catch (Exception e) {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                        errMsgResponse.setMsg(e.getMessage());
                        errList.add(errMsgResponse);
                    }
                }
                if (CollectionUtil.isEmpty(errList)) {
                    List<SalSalesOrderItem> listFree = list.stream().filter(li -> ConstantsEms.YES.equals(li.getFreeFlag())).collect(Collectors.toList());
                    if(!ConstantsEms.YES.equals(request.getIsSkipFree())){
                        if(CollectionUtils.isNotEmpty(listFree)){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                            errMsgResponse.setMsg("包含免费的明细行");
                            warnList.add(errMsgResponse);
                        }
                    }
                    if (salSaleContract != null) {
                        String rawMaterialMode = salSalesOrder.getRawMaterialMode();
                        if (salSaleContract.getRawMaterialMode() != null) {
                            if (!ConstantsEms.YES.equals(request.getIsSkipRaw())) {
                                if (!rawMaterialMode.equals(salSaleContract.getRawMaterialMode())) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                                    errMsgResponse.setMsg("订单的“客供料方式”与其引用合同" + salSalesOrder.getSaleContractCode() + "的“客供料方式”不一致");
                                    warnList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                    if(salSaleContract != null){
                        if (!ConstantsEms.YES.equals(request.getIsSkipConstract())) {
                            int i = judgeConstract(salSalesOrder);//合同金额校验
                            if (i == -1) {
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setCode(Long.valueOf(salSalesOrder.getSalesOrderCode()));
                                errMsgResponse.setMsg("合同号" + salSaleContract.getSaleContractCode() + "的订单金额已超过合同金额");
                                warnList.add(errMsgResponse);
                            }
                        }
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(errList)) {
            return EmsResultEntity.error(errList);
        }
        if (CollectionUtil.isNotEmpty(warnList)) {
            return EmsResultEntity.warning(warnList);
        }
        return EmsResultEntity.success();
    }

    //多笔提交
    @Override
    public AjaxResult checkListFree(List<Long> sids){
        for (Long sid : sids) {
            SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
            List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemList(salSalesOrderItem.setSalesOrderSid(sid));
            for (SalSalesOrderItem salesOrderItem : itemList) {
                if(ConstantsEms.YES.equals(salesOrderItem.getFreeFlag())){
                    return  AjaxResult.success("500",salesOrderItem.getSalesOrderCode());
                }
            }
        }
        return  AjaxResult.success("200","1");
    }

    //校验是否超过合同金额
    @Override
    public int judgeConstract(SalSalesOrder order){
        if (order.getSaleContractSid() == null) {
            return 1;
        }
        List<SalSalesOrderItem> salSalesOrderItems = new ArrayList<>();
        String[] handleList={HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode(),HandleStatus.SUBMIT.getCode()};
        List<SalSalesOrderItem> salSalesOrderItemList = order.getSalSalesOrderItemList();
        List<SalSalesOrder> salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                .eq(SalSalesOrder::getSaleContractSid, order.getSaleContractSid())
                .in(SalSalesOrder::getHandleStatus,handleList)
        );

        if(CollectionUtil.isNotEmpty(salSalesOrders)){
            List<SalSalesOrder> orders = salSalesOrders.stream().filter(li -> !li.getSaleContractSid().toString().equals(order.getSaleContractSid())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(orders)){
                orders.forEach(li->{
                    //关闭的明细
                    if(HandleStatus.CLOSED.getCode().equals(li.getHandleStatus())){
                        List<SalSalesOrderItem> orderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                                .eq(SalSalesOrderItem::getSalesOrderSid, li.getSalesOrderSid())
                        );
                        if(CollectionUtil.isNotEmpty(orderItems)){
                            //出入库量
                            orderItems.forEach(m->{
                                List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                        .eq(InvInventoryDocumentItem::getReferDocumentItemSid, m.getSalesOrderItemSid())
                                );
                                if(CollectionUtil.isNotEmpty(itemList)){
                                    BigDecimal sum = itemList.stream().map(i ->{
                                        if(i.getPriceQuantity()!=null){
                                            return i.getQuantity().multiply(i.getUnitConversionRate());
                                        }else{
                                            return i.getQuantity();
                                        }
                                    }).reduce(BigDecimal.ZERO,BigDecimal::add);
                                    if(sum.compareTo(BigDecimal.ZERO)==0){
                                        m.setQuantity(BigDecimal.ZERO);
                                    }else{
                                        m.setQuantity(sum);
                                    }
                                }
                            });
                            salSalesOrderItems.addAll(orderItems);
                        }
                    }else{
                        List<SalSalesOrderItem> orderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                                .eq(SalSalesOrderItem::getSalesOrderSid, li.getSalesOrderSid())
                        );
                        salSalesOrderItems.addAll(orderItems);
                    }
                });
            }
        }
        Long saleContractSid = order.getSaleContractSid();
        //所有符合的明细
        salSalesOrderItems.addAll(salSalesOrderItemList);
        if(CollectionUtil.isNotEmpty(salSalesOrderItems)){
            BigDecimal sum=BigDecimal.ZERO;
            for (SalSalesOrderItem item : salSalesOrderItems) {
                if(!ConstantsEms.YES.equals(item.getFreeFlag())){
                    if(item.getNewSalePriceTax()!=null){
                        if(item.getNewQuantity()!=null){
                            sum=item.getNewSalePriceTax().multiply(item.getNewQuantity()).add(sum);
                        }else{
                            sum=item.getNewSalePriceTax().multiply(item.getQuantity()).add(sum);
                        }
                    }else{
                        if(item.getSalePriceTax()!=null){
                            if(item.getNewQuantity()!=null){
                                sum=item.getSalePriceTax().multiply(item.getNewQuantity()).add(sum);
                            }else{
                                sum=item.getSalePriceTax().multiply(item.getQuantity()).add(sum);
                            }
                        }
                    }
                }
            }

            SalSaleContract contract = salSaleContractMapper.selectById(saleContractSid);
            BigDecimal currencyAmountTax = contract.getCurrencyAmountTax();
            if(sum.compareTo(currencyAmountTax!=null?currencyAmountTax:BigDecimal.ZERO)==1){
                return -1;
            }
        }
        return 1;
    }
    /**
     * 录入合同号
     */
    @Override
    public int addConstract(List<Long> salesOrderSids){
        List<SalSalesOrder> salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid, salesOrderSids)
        );
        for (int i = 0; i < salSalesOrders.size() - 1; i++) {
            if (salSalesOrders.get(i).getCustomerName().equals(salSalesOrders.get(i + 1).getCustomerName()) && salSalesOrders.get(i).getCompanyName().equals(salSalesOrders.get(i + 1).getCompanyName())) {
                throw new CustomException("存在客户/公司不一致的销售订单，不允许录入同一合同号，请检查！");
            }
        }
        return 1;
    }

    /**
     * 创建合同
     */
    @Override
    public EmsResultEntity constractAdd(List<SalSalesOrder> list, String jump) {
        if (CollectionUtil.isNotEmpty(list)) {
            // 供料方式校验是否一致 以第一笔为准 保留单号最大的订单写入合同
            SalSalesOrder order = list.get(0);
            // 合计金额
            BigDecimal sum = list.get(0).getSumMoneyAmount();
            // 订单的sid
            List<String> orderSidList = new ArrayList<>();
            orderSidList.add(list.get(0).getSalesOrderSid().toString());
            if (sum == null) {
                sum = BigDecimal.ZERO;
            }
            for (int i = 1; i < list.size(); i++) {
                if (ConstantsEms.NO.equals(jump)) {
                    // 当第一笔是空的，则存在一笔非空就报错
                    if (list.get(0).getRawMaterialMode() == null && list.get(i).getRawMaterialMode() != null) {
                        return EmsResultEntity.warning(null, "存在客供料方式不一致的销售订单，是否确认创建合同？");
                    }
                    // 当第一笔不是空的，则每一笔都跟第一步判断是否一样
                    if (list.get(0).getRawMaterialMode() != null &&
                            !list.get(0).getRawMaterialMode().equals(list.get(i).getRawMaterialMode())) {
                        return EmsResultEntity.warning(null, "存在客供料方式不一致的销售订单，是否确认创建合同？");
                    }
                }
                // 保留单号最大的订单写入合同
                if (list.get(i).getSalesOrderCode().compareTo(order.getSalesOrderCode()) > 0) {
                    order = list.get(i);
                }
                sum = sum.add(list.get(i).getSumMoneyAmount()==null ? BigDecimal.ZERO : list.get(i).getSumMoneyAmount());
                orderSidList.add(list.get(i).getSalesOrderSid().toString());
            }
            SalSaleContract contract = new SalSaleContract();
            BeanCopyUtils.copyProperties(order, contract);
            contract.setCurrencyAmountTax(sum);
            contract.setSaleOrderSidList(orderSidList);
            return EmsResultEntity.success(contract, null, null);
        }
        else {
            throw new CustomException("请选择行！");
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(SalSalesOrder o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 销售订单-明细对象
     */
    private void addSalSalesOrderItem(SalSalesOrder salSalesOrder, List<SalSalesOrderItem> salSalesOrderItemList) {
        salSalesOrderItemMapper.delete(
                new UpdateWrapper<SalSalesOrderItem>()
                        .lambda()
                        .eq(SalSalesOrderItem::getSalesOrderSid, salSalesOrder.getSalesOrderSid())
        );
        salSalesOrderItemList.forEach(o -> {
            if (o.getBarcodeSid() == null) {
                throw new BaseException("编码为" + o.getMaterialCode() + "的商品未生成商品条码，请重新添加！");
            }
            if(o.getInOutStockStatus()==null){
                o.setInOutStockStatus(salSalesOrder.getInOutStockStatus());
            }
            if(o.getSalePriceTax()!=null&&o.getTaxRate()!=null){
                o.setSalePrice(o.getSalePriceTax().divide(BigDecimal.ONE.add(o.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
            }
            if(o.getReferDocSid()!=null){
                if(ConstantsEms.Delivery_Note.equals(o.getReferDocCategory())){
                    delDeliveryNoteMapper.update(new DelDeliveryNote(),new UpdateWrapper<DelDeliveryNote>().lambda()
                            .eq(DelDeliveryNote::getDeliveryNoteSid,o.getReferDocSid())
                            .set(DelDeliveryNote::getIsDirectTransportFollowup,ConstantsEms.YES)
                            .set(DelDeliveryNote::getFollowupBusinessType,"XS")
                    );
                }
            }
            if (ConstantsEms.YES.equals(o.getFreeFlag())) {
                o.setSalePrice(BigDecimal.ZERO).setSalePriceTax(BigDecimal.ZERO);
            }
            o.setSalesOrderSid(salSalesOrder.getSalesOrderSid());
            salSalesOrderItemMapper.insert(o);
            List<SalSalesOrderDeliveryPlan> deliveryPlanList = o.getDeliveryPlanList();
            if(CollectionUtil.isNotEmpty(deliveryPlanList)){
                BigDecimal sum = deliveryPlanList.stream().map(li -> li.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(sum.compareTo(o.getQuantity())==1){
                    throw new BaseException("第"+o.getItemNum()+"行，订单量小于计划发货量总和，请核实！");
                }
                deliveryPlanList.forEach(li->{
                    li.setSalesOrderItemSid(o.getSalesOrderItemSid())
                            .setSalesOrderSid(o.getSalesOrderSid());
                });
                salSalesOrderDeliveryPlanMapper.inserts(deliveryPlanList);
            }
        });
    }

    /**
     * 销售订单-附件对象
     */
    private void addSalSalesOrderAttachment(SalSalesOrder salSalesOrder, List<SalSalesOrderAttachment> salSalesOrderAttachmentList) {
        salSalesOrderAttachmentList.forEach(o -> {
            o.setSalesOrderSid(salSalesOrder.getSalesOrderSid());
            salSalesOrderAttachmentMapper.insert(o);
        });
    }

    /**
     * 修改销售订单
     *
     * @param salSalesOrder 销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesOrder(SalSalesOrder o) {
        Long customerSid = o.getCustomerSid();
        String customerGroup = basCustomerMapper.selectById(customerSid).getCustomerGroup();
        if(YCX_CUSTOMER.equals(customerGroup)){
            if(o.getCustomerNameRemark()==null){
                throw new CustomException("客户名称备注,不能为空");
            }
        }
        SalSalesOrder old = salSalesOrderMapper.selectById(o);
        String userName = ApiThreadLocalUtil.get().getSysUser().getUserName();
        if(!userName.equals(old.getCreatorAccount())&&!userName.equals(old.getTrustorAccount())){
            throw new CustomException("不是创建人或委托人，不允许此操作！");
        }
        changePrice(o);
        //确认时判断
        if(ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())){
            judgeNull(o);
        }
        //获取销售价
        getSalSale(o);
        setConfirmInfo(o);
        int row = salSalesOrderMapper.updateAllById(o);
        if (row > 0) {
            //客户寄售结算单
            inventoryDocument(o);
            //销售订单
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) && StringUtils.isEmpty(o.getSpecialBusCategory())) {
                advancesReceived(o);
            }
            //销售订单-明细对象
            List<SalSalesOrderItem> salSalesOrderItemList = o.getSalSalesOrderItemList();
            //初始值赋值
            if(ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())){
                setInitial(salSalesOrderItemList);
            }
            if (CollectionUtils.isNotEmpty(salSalesOrderItemList)) {
                updatePurchasePrice(salSalesOrderItemList);
                Set<Long> setGroubp = salSalesOrderItemList.stream().map(li -> li.getSku2GroupSid()).collect(Collectors.toSet());
                if(setGroubp.size()>3){
                    throw new CustomException("下单商品所属尺码组不允许超过3个，请核查！");
                }
                setItemValue(o, salSalesOrderItemList);
                List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                        .eq(SalSalesOrderItem::getSalesOrderSid, o.getSalesOrderSid())
                );
                List<Long> longs = salSalesOrderItems.stream().map(li -> li.getSalesOrderItemSid()).collect(Collectors.toList());
                List<Long> longsNow = salSalesOrderItemList.stream().map(li -> li.getSalesOrderItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if(CollectionUtil.isNotEmpty(reduce)){
                    salSalesOrderItemMapper.deleteBatchIds(reduce);
                    salSalesOrderDeliveryPlanMapper.delete(new QueryWrapper<SalSalesOrderDeliveryPlan>().lambda()
                            .in(SalSalesOrderDeliveryPlan::getSalesOrderItemSid,reduce));
                }
                //修改明细
                List<SalSalesOrderItem> exitItem = salSalesOrderItemList.stream().filter(li -> li.getSalesOrderItemSid() != null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(exitItem)){
                    exitItem.forEach(li->{
                        if (ConstantsEms.YES.equals(li.getFreeFlag())) {
                            li.setSalePrice(BigDecimal.ZERO).setSalePriceTax(BigDecimal.ZERO);
                        }
                        salSalesOrderItemMapper.updateAllById(li);
                        salSalesOrderDeliveryPlanMapper.delete(new QueryWrapper<SalSalesOrderDeliveryPlan>().lambda()
                                .eq(SalSalesOrderDeliveryPlan::getSalesOrderItemSid,li.getSalesOrderItemSid()));
                        List<SalSalesOrderDeliveryPlan> deliveryPlanList = li.getDeliveryPlanList();
                        //销售发货计划
                        if(CollectionUtil.isNotEmpty(deliveryPlanList)){
                            BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if(sum.compareTo(li.getQuantity())==1){
                                throw new CustomException("第"+li.getItemNum()+"行，订单量小于计划发货量总和，请核实！");
                            }
                            deliveryPlanList.forEach(i->{
                                i.setSalesOrderItemSid(li.getSalesOrderItemSid())
                                        .setSalesOrderSid(li.getSalesOrderSid());
                            });
                            salSalesOrderDeliveryPlanMapper.inserts(deliveryPlanList);
                        }
                    });
                }
                //新增明细
                List<SalSalesOrderItem> nullItem = salSalesOrderItemList.stream().filter(li -> li.getSalesOrderItemSid() == null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(nullItem)) {
                    int max =0;
                    if(CollectionUtils.isNotEmpty(salSalesOrderItems)){
                        max = salSalesOrderItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                    }
                    for (int i = 0; i < nullItem.size(); i++) {
                        int maxItem = max + i + 1;
                        nullItem.get(i).setItemNum(maxItem);
                        nullItem.get(i).setSalesOrderSid(o.getSalesOrderSid());
                        if (ConstantsEms.YES.equals(nullItem.get(i).getFreeFlag())) {
                            nullItem.get(i).setSalePrice(BigDecimal.ZERO).setSalePriceTax(BigDecimal.ZERO);
                        }
                        salSalesOrderItemMapper.insert(nullItem.get(i));
                        List<SalSalesOrderDeliveryPlan> deliveryPlanList = nullItem.get(i).getDeliveryPlanList();
                        //销售发货计划
                        if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                            BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if(sum.compareTo(nullItem.get(i).getQuantity())==1){
                                throw new CustomException("第"+nullItem.get(i).getItemNum()+"行，订单量小于计划发货量总和，请核实！");
                            }
                            for (SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan : deliveryPlanList) {
                                salSalesOrderDeliveryPlan.setSalesOrderItemSid(nullItem.get(i).getSalesOrderItemSid())
                                        .setSalesOrderSid(nullItem.get(i).getSalesOrderSid());
                            }
                            salSalesOrderDeliveryPlanMapper.inserts(deliveryPlanList);
                        }
                    }
                }
            }else{
                salSalesOrderItemMapper.delete(new QueryWrapper<SalSalesOrderItem>().lambda()
                        .eq(SalSalesOrderItem::getSalesOrderSid,o.getSalesOrderSid())
                );
            }
            //销售订单-附件对象
            List<SalSalesOrderAttachment> salSalesOrderAttachmentList = o.getAttachmentList();
            salSalesOrderAttachmentMapper.delete(new UpdateWrapper<SalSalesOrderAttachment>()
                    .lambda().eq(SalSalesOrderAttachment::getSalesOrderSid, o.getSalesOrderSid()));
            if (CollectionUtils.isNotEmpty(salSalesOrderAttachmentList)) {
                salSalesOrderAttachmentList.stream().forEach(a -> {
                    a.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    a.setUpdateDate(new Date());
                });
                addSalSalesOrderAttachment(o, salSalesOrderAttachmentList);
            }
        }
        if (!ConstantsEms.SAVA_STATUS.equals(o.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(o);
        }
        // 判断 新旧合同 的 特殊用途是否 是 临时过渡, 删除该待办
        if (old.getSaleContractSid() != null && !old.getSaleContractSid().equals(o.getSaleContractSid())) {
            List<Long> contractSidList = new ArrayList<Long>(){{
                add(old.getSaleContractSid());
            }};
            if (o.getSaleContractSid() != null) {
                contractSidList.add(o.getSaleContractSid());
            }
            List<SalSaleContract> saleContractList = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>().lambda()
                    .in(SalSaleContract::getSaleContractSid, contractSidList));
            if (CollectionUtil.isNotEmpty(saleContractList)) {
                Map<Long, SalSaleContract> map = saleContractList.stream().collect(Collectors.toMap(SalSaleContract::getSaleContractSid, Function.identity()));
                if (map.containsKey(old.getSaleContractSid()) && ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(old.getSaleContractSid()).getContractPurpose())) {
                    if (!map.containsKey(o.getSaleContractSid()) || !ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(o.getSaleContractSid()).getContractPurpose())) {
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .eq(SysTodoTask::getDocumentSid, o.getSalesOrderSid()).eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SALE_ORDER + "-" + ConstantsTable.TABLE_SALE_CONTRACT)
                                .like(SysTodoTask::getTitle, "使用的是过渡合同"));
                    }
                }
            }
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            String shortName = basCustomerMapper.selectById(o.getCustomerSid()).getShortName();
            sysBusinessBcst.setTitle(shortName+"，销售订单"+o.getSalesOrderCode()+"的信息发生变更，请知悉！")
                    .setDocumentSid(o.getSalesOrderSid())
                    .setDocumentCode(o.getSalesOrderCode())
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        //审批
        if (ConstantsEms.SUBMIT_STATUS.equals(o.getHandleStatus())){
            Submit submit = new Submit();
            submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
            submit.setFormType(FormType.XSDD_BG.getCode());
            List<FormParameter> list = new ArrayList();
            FormParameter formParameter = new FormParameter();
            formParameter.setParentId(o.getSalesOrderSid().toString());
            formParameter.setFormId(o.getSalesOrderSid().toString());
            formParameter.setFormCode(o.getSalesOrderCode());
            list.add(formParameter);
            submit.setFormParameters(list);
            workflowService.change(submit);
        }
        MongodbUtil.insertUserLog(o.getSalesOrderSid(), BusinessType.UPDATE.getValue(),old, o, TITLE);
        return row;
    }
    //改变主表处理状态
    public void changeInvoutStatus(SalSalesOrder o){
        List<SalSalesOrderItem> salSalesOrderItemList = o.getSalSalesOrderItemList();
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            String isReturnGoods = o.getIsReturnGoods();
            if(ConstantsEms.NO.equals(isReturnGoods)){
                //部分出库
                List<SalSalesOrderItem> littleQuatial = salSalesOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.OUT_STORE_STATUS_LI)).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(littleQuatial)){
                    o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_LI);
                }else{
                    List<SalSalesOrderItem> allQuatial = salSalesOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.OUT_STORE_STATUS)).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(allQuatial)){
                        if(allQuatial.size()==salSalesOrderItemList.size()){
                            o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS);
                        }else{
                            o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_LI);
                        }
                    }else{
                        o.setInOutStockStatus(ConstantsEms.OUT_STORE_STATUS_NOT);
                    }
                }
            }else{
                //部分入库
                List<SalSalesOrderItem> littleQuatial = salSalesOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.IN_STORE_STATUS_LI)).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(littleQuatial)){
                    o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_LI);
                }else{
                    List<SalSalesOrderItem> allQuatial = salSalesOrderItemList.stream().filter(m -> m.getInOutStockStatus().equals(ConstantsEms.IN_STORE_STATUS)).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(allQuatial)){
                        if(allQuatial.size()==salSalesOrderItemList.size()){
                            o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS);
                        }else{
                            o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_LI);
                        }
                    }else{
                        o.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_NOT);
                    }
                }
            }
        }
    }
    /**
     * 批量删除销售订单
     *
     * @param clientIds 需要删除的销售订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesOrderByIds(Long[] salesOrderSids) {
        List<SalSalesOrder> salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid,salesOrderSids)
        );
        List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                .eq(SalSalesOrderItem::getReferDocCategory, ConstantsEms.Delivery_Note)
                .in(SalSalesOrderItem::getSalesOrderSid, salesOrderSids)
        );
        if(CollectionUtil.isNotEmpty(salSalesOrderItems)){
            salSalesOrderItems.forEach(li->{
                DelDeliveryNote delDeliveryNote = new DelDeliveryNote();
                delDeliveryNote.setIsDirectTransportFollowup(ConstantsEms.NO);
                delDeliveryNote.setDeliveryNoteSid(li.getReferDocSid());
                delDeliveryNoteMapper.updateAllZf(delDeliveryNote);
            });
        }
        String userName = ApiThreadLocalUtil.get().getSysUser().getUserName();
        salSalesOrders.forEach(li->{
            if(!userName.equals(li.getCreatorAccount())&&!userName.equals(li.getTrustorAccount())){
                throw new CustomException("当前登入账号不是订单号"+li.getSalesOrderCode()+"的创建人或委托人，不允许此操作！");
            }
        });
        //删除销售订单
        int row = salSalesOrderMapper.deleteSalSalesOrderByIds(salesOrderSids);
        for (Long salesOrderSid : salesOrderSids) {
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            salSalesOrder.setSalesOrderSid(salesOrderSid);
            checkTodoExist(salSalesOrder);
            //插入日志
            MongodbUtil.insertUserLog(salesOrderSid,BusinessType.DELETE.getValue(), null, TITLE);
        }
        if (row > 0) {
            //删除销售订单明细
            salSalesOrderItemMapper.deleteSalSalesOrderItemByIds(salesOrderSids);
            //删除销售订单附件
            salSalesOrderAttachmentMapper.deleteSalSalesOrderAttachmentByIds(salesOrderSids);
            salSalesOrderDeliveryPlanMapper.delete(new QueryWrapper<SalSalesOrderDeliveryPlan>().lambda()
                    .in(SalSalesOrderDeliveryPlan::getSalesOrderSid,salesOrderSids)
            );
        }
        return row;
    }
    public void setItemValue(SalSalesOrder order, List<SalSalesOrderItem> list){
        list.forEach(o->{
            if(o.getSalePriceTax()!=null&&o.getTaxRate()!=null){
                o.setSalePrice(o.getSalePriceTax().divide(BigDecimal.ONE.add(o.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
            }
            if(o.getInOutStockStatus()==null){
                if(ConstantsEms.YES.equals(order.getIsReturnGoods())){
                    o.setInOutStockStatus("WRK");
                }else{
                    o.setInOutStockStatus("WCK");
                }
            }
        });
    }
    //物料需求测算报表-跳转库存出库
    @Override
    public InvInventoryDocument hadnleItem(TecBomItemReportRequest request){
        List<TecBomItemReport> list = request.getOrderList();
        String movementType = request.getMovementType();
        ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                .eq(ConMovementType::getCode, movementType)
        );
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        invInventoryDocument.setReferDocCategory("WU")
                .setMovementType(movementType)
                .setSpecialStock(conMovementType.getSpecialStock());
        if(!"ST52".equals(movementType)){
            ConInOutStockDocCategory docCategory = conInOutStockDocCategoryMapper.selectOne(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                    .eq(ConInOutStockDocCategory::getDocCategoryCode, "WU")
                    .eq(ConInOutStockDocCategory::getInvDocCategoryCode,"CK")
                    .eq(ConInOutStockDocCategory::getMovementTypeCode, movementType)
            );
            invInventoryDocument.setIsStorehouseLocationEdit(docCategory.getIsStorehouseLocationEdit())
                    .setIsStorehouseEdit(docCategory.getIsStorehouseEdit());
        }
        List<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
        invInventoryDocument.setSpecialStock(conMovementType.getSpecialStock())
                .setReferUnitType(conMovementType.getReferUnitType());
        list.forEach(li->{
            BasMaterialBarcode basMaterialBarcode=null;
            Long barcodeSid=null;
            String barcode=null;
            if(li.getBomMaterialSku2Sid()==null){
                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                        .eq(BasMaterialBarcode::getSku1Sid,li.getBomMaterialSku1Sid())
                        .eq(BasMaterialBarcode::getMaterialSid,li.getBomMaterialSid())
                        .isNull(BasMaterialBarcode::getSku2Sid)
                );
            }else{
                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                        .eq(BasMaterialBarcode::getSku1Sid, li.getBomMaterialSku1Sid())
                        .eq(BasMaterialBarcode::getMaterialSid,li.getBomMaterialSid())
                        .eq(BasMaterialBarcode::getSku2Sid,li.getBomMaterialSku2Sid())
                );
            }
            if(basMaterialBarcode!=null){
                barcodeSid=basMaterialBarcode.getBarcodeSid();
                barcode=basMaterialBarcode.getBarcode();
            }else{
                throw new CustomException(li.getMaterialCode()+","+li.getMaterialName()+"没有对应的商品条码");
            }
            InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
            invInventoryDocumentItem.setMaterialSid(li.getMaterialSid())
                    .setMaterialCode(li.getMaterialCode())
                    .setMaterialName(li.getMaterialName())
                    .setSku1Name(li.getSku1Name())
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Name(li.getSku2Name())
                    .setBarcode(barcode)
                    .setBarcodeSid(barcodeSid)
                    .setUnitConversionRate(BigDecimal.ONE)
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setQuantity(li.getLossRequireQuantity()!=null?new BigDecimal(li.getLossRequireQuantity()):null)
                    .setUnitBase(li.getUnitBase())
                    .setUnitBaseName(li.getUnitBaseName())
                    .setProductCodes(li.getSaleMaterialCode()!=null?li.getSaleMaterialCode():li.getMaterialCodeRemark())
                    .setProductSku1Names(li.getSaleSku1Name()!=null?li.getSaleSku1Name():li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getSaleSku2Name()!=null?li.getSaleSku2Name():li.getMaterialSku2Remark())
                    .setProductQuantityRemark(li.getProductQuantity()!=null?li.getProductQuantity().toString():null);
            invInventoryDocumentItems.add(invInventoryDocumentItem);
        });
        invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
        return invInventoryDocument;
    }
    @Override
    public SalSalesOrder getOrder(List<TecBomItemReport> order) {
        Long purchaseOrderCode = order.get(0).getPurchaseOrderCode();
        String codeRemark = order.get(0).getPurchaseOrderCodeRemark();
        List<Long> orderSidList = order.stream().map(li -> li.getCommonSid()).collect(Collectors.toList());
        Long vendorSid=null;
        List<PurPurchaseOrder> orders=null;
        if(purchaseOrderCode!=null){
            orders = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>()
                    .lambda()
                    .in(PurPurchaseOrder::getPurchaseOrderSid, orderSidList)
            );
        }else{
            if(codeRemark==null){
                throw new CustomException("非已确认状态的采购订单测算的物料需求，无法进行此操作！");
            }
            List<String> codes = new ArrayList<>();
            order.stream().forEach(li->{
                String purchaseOrderCodeRemark = li.getPurchaseOrderCodeRemark();
                String[] purchaseOrderCodes = purchaseOrderCodeRemark.split(";");
                for (String code : purchaseOrderCodes) {
                    codes.add(code);
                }
            });
            orders = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>()
                    .lambda()
                    .in(PurPurchaseOrder::getPurchaseOrderCode, codes)
            );
        }
        if(CollectionUtils.isNotEmpty(orders)){
            Set<Long> customerSidSet = orders.stream().map(li -> li.getVendorSid()).collect(Collectors.toSet());
            if(customerSidSet.size()>1){
                throw new CustomException("所选择数据的采购订单的供应商不一致，请检查！");
            }
            vendorSid=orders.get(0).getVendorSid();
            orders.forEach(li->{
                if(!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())){
                    throw  new  CustomException("非已确认状态的采购订单测算的物料需求，无法点击此按钮！");
                }
            });
        }else {
            throw new CustomException("非已确认状态的采购订单测算的物料需求，无法进行此操作！");
        }
        BasVendor basVendor = basVendorMapper.selectById(vendorSid);
        if(basVendor.getCustomerSid()==null){
            throw new CustomException("未找到客户信息，请检查！");
        }
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        salSalesOrder.setCustomerSid(basVendor.getCustomerSid())
                .setDocumentType(DocCategory.SALE_ORDER.getCode())
                .setCreateDate(new Date())
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setDeliveryStatus("WFH")
                .setIsConsignmentSettle("N")
                .setInOutStockStatus("WCK")
                .setSalePerson(ApiThreadLocalUtil.get().getUsername())
                .setDocumentDate(new Date())
                .setCurrency("CNY")
                .setMaterialCategory("WL")
                .setCurrencyUnit("YUAN")
                .setSaleMode("CG")
                .setRawMaterialMode(ConstantsEms.RAW_w)
                .setHandleStatus(ConstantsEms.SAVA_STATUS);
        SalSalePrice salSalePrice = new SalSalePrice();
        BeanCopyUtils.copyProperties(salSalesOrder,salSalePrice);
        List<SalSalesOrderItem> orderItems = new ArrayList<>();
        order.forEach(li->{
            BasMaterialBarcode basMaterialBarcode=null;
            Long barcodeSid=null;
            String barcode=null;
            SalSalePriceItem newPurchase=null;
            BigDecimal tax=null;
            BigDecimal PriceTax=null;
            SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
            Long produceSku1Sid=li.getSaleSku1Sid()==null?null:Long.valueOf(li.getSaleSku1Sid());
            Long produceSku2Sid=li.getSaleSku2Sid()==null?null:Long.valueOf(li.getSaleSku2Sid());
            Long produceSid=li.getSaleMaterialSid()==null?null:Long.valueOf(li.getSaleMaterialSid());
            if(li.getBomMaterialSku2Sid()==null){
                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                        .eq(BasMaterialBarcode::getSku1Sid,li.getBomMaterialSku1Sid())
                        .eq(BasMaterialBarcode::getMaterialSid,li.getBomMaterialSid())
                        .isNull(BasMaterialBarcode::getSku2Sid)
                );
            }else{
                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                        .eq(BasMaterialBarcode::getSku1Sid, li.getBomMaterialSku1Sid())
                        .eq(BasMaterialBarcode::getMaterialSid,li.getBomMaterialSid())
                        .eq(BasMaterialBarcode::getSku2Sid,li.getBomMaterialSku2Sid())
                );
            }
            if(basMaterialBarcode!=null){
                barcodeSid=basMaterialBarcode.getBarcodeSid();
                barcode=basMaterialBarcode.getBarcode();
            }else{
                throw new CustomException(li.getMaterialCode()+","+li.getMaterialName()+"没有对应的商品条码，无法创建采购订单");
            }
            salSalePrice.setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setMaterialSid(li.getBomMaterialSid());
            newPurchase = salSalePriceService.getNewSalePrice(salSalePrice);
            if(newPurchase.getSalePriceTax()!=null){
                tax=newPurchase.getTaxRate();
                PriceTax=newPurchase.getSalePriceTax();
            }
            salSalesOrderItem.setMaterialSid(li.getBomMaterialSid())
                    .setMaterialName(li.getMaterialName())
                    .setMaterialCode(li.getMaterialCode())
                    .setSku1Name(li.getSku1Name())
                    .setTaxRate(tax)
                    .setSalePriceTax(PriceTax)
                    .setSalePrice(PriceTax==null?null:PriceTax.divide(BigDecimal.ONE.add(tax),6,BigDecimal.ROUND_HALF_UP))
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setBarcodeSid(barcodeSid)
                    .setInOutStockStatus("WCK")
                    .setBarcode(barcode)
                    .setSku2Name(li.getSku2Name())
                    .setProductQuantity(li.getProductQuantity())
                    .setQuantity(BigDecimal.valueOf(Double.valueOf(li.getLossRequireQuantity())))
                    .setUnitBase(newPurchase!=null?newPurchase.getUnitBase():null)
                    .setUnitBaseName(newPurchase!=null?newPurchase.getUnitBaseName():null)
                    .setUnitPrice(newPurchase!=null?newPurchase.getUnitPrice():null)
                    .setUnitPriceName(newPurchase!=null?newPurchase.getUnitPriceName():null)
                    .setUnitConversionRate(newPurchase!=null?newPurchase.getUnitConversionRate():null)
                    .setProductCode(li.getSaleMaterialCode())
                    .setProductSid(li.getSaleMaterialSid())
                    .setProductName(li.getSaleMaterialName())
                    .setProductSku1Sid(li.getSaleSku1Sid())
                    .setProductSku1Name(li.getSaleSku1Name())
                    .setCreateDate(new Date())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                    .setProductSku2Name(li.getSaleSku2Name())
                    .setProductSku2Sid(li.getSaleSku2Sid())
                    .setProductCodes(li.getMaterialCodeRemark())
                    .setProductPoCodes(li.getPurchaseOrderCodeRemark())
                    .setProductSku1Names(li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getMaterialSku2Remark())
                    .setReferPurchaseOrderCode(li.getPurchaseOrderCode())
                    .setReferPurchaseOrderSid(li.getPurchaseOrderCode()!=null?li.getCommonSid():null)
                    .setReferPurchaseOrderItemSid(li.getPurchaseOrderCode()!=null?li.getCommonItemSid():null)
                    .setReferPurchaseOrderItemNum(li.getPurchaseOrderCode()!=null?li.getCommonItemNum():null);
            orderItems.add(salSalesOrderItem);
        });
        salSalesOrder.setSalSalesOrderItemList(orderItems);
        return salSalesOrder;
    }
    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int close(SalSalesOrder salSalesOrder){
        Long[] salesOrderSids = salSalesOrder.getSalesOrderSids();
        List<SalSalesOrder> salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid, salesOrderSids)
                .eq(SalSalesOrder::getSpecialBusCategory,ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY)
        );
        if(CollectionUtil.isNotEmpty(salSalesOrders)){
            List<FinBookReceiptEstimationItem> finBookReceiptEstimations = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>()
                    .lambda()
                    .in(FinBookReceiptEstimationItem::getSalesOrderSid, salesOrderSids)
                    .eq(FinBookReceiptEstimationItem::getClearStatus, ConstantsFinance.CLEAR_STATUS_WHX)
            );
            String isBusinessFinance = ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance();
            if(CollectionUtil.isEmpty(finBookReceiptEstimations)){
                if(ConstantsEms.YES.equals(isBusinessFinance)){
                    throw new CustomException("客户寄售结算单，对应的应收暂估流水非“未核销”状态，不允许作废");
                }
            }else{
                List<Long> sids = finBookReceiptEstimations.stream().map(li -> li.getBookReceiptEstimationSid()).collect(Collectors.toList());
                finBookReceiptEstimationMapper.update(new FinBookReceiptEstimation(),new UpdateWrapper<FinBookReceiptEstimation>()
                        .lambda()
                        .set(FinBookReceiptEstimation::getHandleStatus,HandleStatus.INVALID.getCode())
                        .in(FinBookReceiptEstimation::getBookReceiptEstimationSid, sids)
                );
            }
            List<InvInventoryDocumentItem> invInventoryDocumentItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                    .in(InvInventoryDocumentItem::getReferDocumentSid, salesOrderSids)
            );
            List<Long> sids = invInventoryDocumentItems.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).collect(toList());
            List<InvInventoryDocument> invInventoryDocuments = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                    .in(InvInventoryDocument::getInventoryDocumentSid, sids)
            );
            if(CollectionUtil.isNotEmpty(invInventoryDocuments)){
                List<Long> sidList = invInventoryDocuments.stream().map(li -> li.getInventoryDocumentSid()).distinct().collect(Collectors.toList());
                //冲销
                invDocumentCX(sidList);
            }
        }
        int row=0;
        if(CollectionUtil.isNotEmpty(salSalesOrders)){
            row = salSalesOrderMapper.update(new SalSalesOrder(), new UpdateWrapper<SalSalesOrder>().lambda()
                    .in(SalSalesOrder::getSalesOrderSid, salesOrderSids)
                    .set(SalSalesOrder::getHandleStatus, HandleStatus.INVALID.getCode())
            );
            // 明细状态
            salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                    .in(SalSalesOrderItem::getSalesOrderSid, salesOrderSids)
                    .set(SalSalesOrderItem::getItemStatus, ConstantsEms.STATUS_INVALID_STATUS));
            // 操作日志
            for (Long orderSid : salesOrderSids) {
                MongodbUtil.insertUserLog(orderSid, BusinessType.CANCEL.getValue(), null, TITLE);
            }
        }else{
            row = salSalesOrderMapper.update(new SalSalesOrder(), new UpdateWrapper<SalSalesOrder>().lambda()
                    .in(SalSalesOrder::getSalesOrderSid, salesOrderSids)
                    .set(SalSalesOrder::getHandleStatus, HandleStatus.CLOSED.getCode())
            );
            // 明细状态
            salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                    .in(SalSalesOrderItem::getSalesOrderSid, salesOrderSids)
                    .set(SalSalesOrderItem::getItemStatus, ConstantsEms.STATUS_CLOSE_STATUS));
            // 操作日志
            for (Long orderSid : salesOrderSids) {
                MongodbUtil.insertUserLog(orderSid, BusinessType.CLOSE.getValue(), null, TITLE);
            }
        }
        return row;
    }

    // 订单明细关闭
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int itemClose(SalSalesOrderItem request){
        int row = 0;
        Long[] sids = request.getSalesOrderItemSidList();
        if (sids == null || sids.length == 0) {
            throw new BaseException("请选择行！");
        }
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.selectOrderItemListBy(new SalSalesOrderItem()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSalesOrderItemSidList(sids));
        if (CollectionUtil.isNotEmpty(itemList) && itemList.size() == sids.length) {
            // 明细状态
            row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                    .in(SalSalesOrderItem::getSalesOrderItemSid, sids)
                    .set(SalSalesOrderItem::getItemStatus, ConstantsEms.STATUS_CLOSE_STATUS));
        }
        else {
            throw new BaseException("仅已确认状态的订单明细才可以作此操作！");
        }
        return row;
    }

    /*
     *客户寄售冲销
     */
    public void invDocumentCX(List<Long> sidList){
        sidList.forEach(sid->{
            InvInventoryDocument invInventoryDocument = InvInventoryDocumentimpl.selectInvInventoryDocumentById(sid);
            invInventoryDocument.setType(ConstantsEms.RU_KU);//入库
            invInventoryDocument.setPreInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
            //初始化
            invInventoryDocument.setInventoryDocumentSid(null);
            invInventoryDocument.setDocumentCategory("RK");
            invInventoryDocument.setCreatorAccount(null);
            invInventoryDocument.setCreateDate(null);
            invInventoryDocument.setInventoryDocumentCode(null);
            List<InvInventoryDocumentItem> itemList = invInventoryDocument.getInvInventoryDocumentItemList();
            itemList.forEach(item->{
                item.setInventoryDocumentSid(null);
                item.setInventoryDocumentItemSid(null);
            });
            //作废
            InvInventoryDocumentimpl.updateById(new InvInventoryDocument()
                    .setInventoryDocumentSid(sid)
                    .setHandleStatus("C")
                    .setDocumentType(ConstantsEms.DOCUMNET_TYPE_ZG));
            invInventoryDocument.setMovementType("SR24");//客户寄售冲销
            invInventoryDocument.setHandleStatus(HandleStatus.POSTING.getCode());//
            invInventoryDocument.setDocumentType(ConstantsEms.DOCUMNET_TYPE_CX);//冲销
            invInventoryDocument.setInvInventoryDocumentItemList(itemList);
            invInventoryDocument.setSource(ConstantsEms.INV_SOURCE);
            InvInventoryDocumentimpl.insertInvInventoryDocument(invInventoryDocument);
        });
    }

    /**
     * 销售出库进度跟踪报表
     *
     * @param salSaleOrder 销售订单
     * @return 销售订单集合
     */
    @Override
    public List<SalSaleOrderProcessTracking> selectSalSaleProcessTrackingList(SalSaleOrderProcessTracking salSaleOrder) {
        return salSalesOrderItemMapper .selectSalSaleProcessTrackingList(salSaleOrder);
    }

    /**
     * 新建直接点提交/编辑点提交
     *
     * @param salSalesOrder 销售订单
     * @param jump 是否忽略并继续 提示校验  N 第一次校验 ， Y 忽略，
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(SalSalesOrder order, String jump) {
        int row = 0;
        if (order.getSalesOrderSid() == null) {
            // 新建
            row = this.insertSalSalesOrder(order);
        }
        else {
            row = this.updateSalSalesOrder(order);
        }
        if (row == 1) {
            List<ConDocBuTypeGroupSo> conf = conDocBuTypeGroupSoMapper.selectList(new QueryWrapper<ConDocBuTypeGroupSo>().lambda()
                    .eq(ConDocBuTypeGroupSo::getDocTypeCode, order.getDocumentType())
                    .eq(ConDocBuTypeGroupSo::getBuTypeCode, order.getBusinessType())
                    .eq(ConDocBuTypeGroupSo::getClientId, ApiThreadLocalUtil.get().getClientId())
                    .orderByDesc(ConDocBuTypeGroupSo::getCreateDate));
            EmsResultEntity verifyResult = null;
            // 提交校验  这里可能有 连续多次提交的校验 ，每次把 校验的结果 返回给前端，前端再更新 到参数中的 orderErrRequest
            List<Long> sidList = new ArrayList<Long>(){{add(order.getSalesOrderSid());}};
            OrderErrRequest request = new OrderErrRequest();
            request.setSidList(sidList);
            if (ConstantsEms.YES.equals(jump)) {
                verifyResult = this.checkList(request);
                if (verifyResult != null && EmsResultEntity.ERROR_TAG.equals(verifyResult.getTag())) {
                    return AjaxResult.success(verifyResult);
                }
                else {
                    verifyResult = null;
                }
            }
            else {
                verifyResult = this.checkList(request);
            }
            // 是否 无需审批 配置
            if (CollectionUtil.isEmpty(conf) || ConstantsEms.NO.equals(conf.get(0).getIsNonApproval()) ||
                    conf.get(0).getIsNonApproval() == null) {
                // 校验通过
                if (verifyResult == null || CollectionUtil.isEmpty(verifyResult.getMsgList())) {
                    Submit submit = new Submit();
                    submit.setFormType(FormType.SalesOrder.getCode());
                    List<FormParameter> formParameters = new ArrayList<>();
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(String.valueOf(order.getSalesOrderSid()));
                    formParameter.setFormId(String.valueOf(order.getSalesOrderSid()));
                    formParameter.setFormCode(String.valueOf(order.getSalesOrderCode()));
                    formParameters.add(formParameter);
                    submit.setFormParameters(formParameters);
                    submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
                    workflowService.submitByItem(submit);
                    return AjaxResult.success("操作成功", new SalSalesOrder().setSalesOrderSid(order.getSalesOrderSid()));
                }
                else {
                    // 手动回滚 新建 或者 暂存 的事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.success(verifyResult);
                }
            }
            else {
                // 校验通过
                if (verifyResult == null || CollectionUtil.isEmpty(verifyResult.getMsgList())) {
                    // 直接确认
                    Long[] sids = new Long[]{order.getSalesOrderSid()};
                    SalSalesOrder confirmOrder = new SalSalesOrder();
                    confirmOrder.setSalesOrderSids(sids);
                    confirmOrder.setHandleStatus(ConstantsEms.CHECK_STATUS);
                    this.confirm(confirmOrder);
                    return AjaxResult.success("操作成功", new SalSalesOrder().setSalesOrderSid(order.getSalesOrderSid()));
                }
                else {
                    // 手动回滚 新建 或者 暂存 的事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.success(verifyResult);
                }
            }
        }
        return AjaxResult.success("操作成功", new SalSalesOrder().setSalesOrderSid(order.getSalesOrderSid()));
    }


    /**
     * 销售订单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(SalSalesOrder salSalesOrder) {
        //销售订单sids
        Long[] salesOrderSids = salSalesOrder.getSalesOrderSids();
        if (ArrayUtil.isEmpty(salesOrderSids)) {
            throw new BaseException("参数缺失");
        }
        for (Long salesOrderSid : salesOrderSids) {
            SalSalesOrder order = salSalesOrderMapper.selectById(salesOrderSid);
            salSalesOrderMapper.update(new SalSalesOrder(),new UpdateWrapper<SalSalesOrder>().lambda()
                    .eq(SalSalesOrder::getSalesOrderSid,salesOrderSid)
                    .set(SalSalesOrder::getConfirmDate,new Date())
                    .set(SalSalesOrder::getHandleStatus,ConstantsEms.CHECK_STATUS)
                    .set(SalSalesOrder::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
            );
            SalSalesOrder o = selectSalSalesOrderById(salesOrderSid);
            //校验是否存在待办
            checkTodoExist(o);
            judgeNull(o);
            List<SalSalesOrderItem> salSalesOrderItemList = o.getSalSalesOrderItemList();
            ConBuTypeSalesOrder conBuTypeSalesOrder = conBuTypeSalesOrderMapper.selectOne(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                    .eq(ConBuTypeSalesOrder::getCode, o.getBusinessType())
            );
            salSalesOrderItemList.forEach(item->{
                item.setToexpireDays(conBuTypeSalesOrder.getToexpireDays());
                salSalesOrderItemMapper.updateAllById(item);
            });
            //客户寄售结算单
            inventoryDocument(o);
            //配置档案赋值
            setDoc(o);
            //销售订单
            if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus()) && StringUtils.isEmpty(o.getSpecialBusCategory())) {
                advancesReceived(o);
            }
            if(ConstantsEms.SAVA_STATUS.equals(order.getHandleStatus())){
                MongodbUtil.insertUserLog(salesOrderSid, BusinessType.CONFIRM.getValue(),TITLE);
            }
        }
        return 1;
    }

    /**
     * 设置负责生产工厂
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrderProducePlant(OrderProducePlantRequest orderProducePlantRequest){
        if (orderProducePlantRequest.getSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        Long producePlantSid = orderProducePlantRequest.getProducePlantSid();
        String plantCode = null;
        if (producePlantSid != null) {
            BasPlant basPlant = basPlantMapper.selectById(producePlantSid);
            if (basPlant != null) {
                plantCode = basPlant.getPlantCode();
            }
        }
        LambdaUpdateWrapper<SalSalesOrderItem> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //负责生产工厂
        updateWrapper.in(SalSalesOrderItem::getSalesOrderItemSid, orderProducePlantRequest.getSidList())
                .set(SalSalesOrderItem::getProducePlantSid, producePlantSid)
                .set(SalSalesOrderItem::getProducePlantCode, plantCode);
        row = salSalesOrderItemMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 明细报表更新销售价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updatePrice(List<SalSalesOrderItem> salSalesOrderItemList){
        List<SalSalesOrderItem> orderItemList = salSalesOrderItemList.stream().filter(li -> li.getSalePriceTax() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(orderItemList)) {
            throw new CustomException("所选行，销售价不允许有值");
        }
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        ArrayList<SalSalesOrderItem> salOrderItemList = new ArrayList<>();
        Map<String , List<SalSalesOrderItem>> map = salSalesOrderItemList.stream().collect(Collectors.groupingBy(v -> v.getSalesOrderSid()+";"+v.getSalesOrderCode()));
        map.keySet().stream().forEach(key->{
            String[] arr = key.split(";");
            List<SalSalesOrderItem> items = map.get(key);
            List<Long> notSidList = items.stream().map(li -> li.getSalesOrderItemSid()).collect(Collectors.toList());
            List<SalSalesOrderItem> orderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                    .notIn(SalSalesOrderItem::getSalesOrderItemSid, notSidList)
                    .eq(SalSalesOrderItem::getSalesOrderSid, arr[0])
            );
            HashSet<BigDecimal> set = new HashSet<>();
            if(CollectionUtil.isNotEmpty(orderItems)){
                Set<BigDecimal> taxSet = orderItems.stream().filter(li->li.getTaxRate()!=null).map(li -> li.getTaxRate()).collect(Collectors.toSet());
                set.addAll(taxSet);
            }
            items.stream().forEach(li->{
                SalSalePrice salSalePrice = new SalSalePrice();
                BeanCopyUtils.copyProperties(li, salSalePrice);
                //获取销售
                SalSalePriceItem item = salSalePriceServiceImpl.getNewSalePrice(salSalePrice);
                if (item.getSalePriceTax() != null) {
                    BigDecimal price = item.getSalePriceTax().divide(BigDecimal.ONE.add(item.getTaxRate()),6, BigDecimal.ROUND_HALF_UP);
                    li.setSalePriceTax(item.getSalePriceTax())
                            .setUnitPrice(item.getUnitPrice())
                            .setUnitBase(item.getUnitBase())
                            .setTaxRate(item.getTaxRate())
                            .setUnitConversionRate(item.getUnitConversionRate())
                            .setSalePrice(price);
                    set.add(item.getTaxRate());
                }
            });
            salOrderItemList.addAll(items);
            if(set.size()>1){
                CommonErrMsgResponse commonErrMsgResponse = new CommonErrMsgResponse();
                commonErrMsgResponse.setCode(Long.valueOf(arr[1]))
                        .setMsg("明细行的税率不一致");
                msgList.add(commonErrMsgResponse);
            }
        });
        if(CollectionUtil.isNotEmpty(msgList)){
            return  AjaxResult.success("500",msgList);
        }
        if(CollectionUtil.isNotEmpty(salOrderItemList)){
            salOrderItemList.forEach(li->{
                salSalesOrderItemMapper.updateById(li);
            });
        }
        return AjaxResult.success(1);
    }

    /**
     * 设置委托人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTrustor(OrderTrustorAccountRequest order){
        List<Long> orderSidList = order.getOrderSidList();
        String trustorAccount = order.getTrustorAccount();
        int row = salSalesOrderMapper.update(new SalSalesOrder(), new UpdateWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid, orderSidList)
                .set(SalSalesOrder::getTrustorAccount, trustorAccount)
        );
        return row;
    }

    /**
     * 销售订单变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(SalSalesOrder salSalesOrder) {
        SalSalesOrder old = salSalesOrderMapper.selectById(salSalesOrder);
        boolean buTypeChange = false;
        if (salSalesOrder.getBusinessType() != null && !salSalesOrder.getBusinessType().equals(old.getBusinessType())) {
            ConBuTypeSalesOrder buType = conBuTypeSalesOrderMapper.selectOne(new QueryWrapper<ConBuTypeSalesOrder>()
                    .lambda().eq(ConBuTypeSalesOrder::getCode, salSalesOrder.getBusinessType()));
            if (buType == null) {
                throw new BaseException("业务类型不存在");
            }
            salSalesOrder.setDeliveryType(buType.getDeliveryType()).setInventoryControlMode(buType.getInventoryControlMode());
            // 单据类型与业务类型
            ConDocBuTypeGroupSo so = conDocBuTypeGroupSoMapper.selectOne(new QueryWrapper<ConDocBuTypeGroupSo>().lambda()
                    .eq(ConDocBuTypeGroupSo::getDocTypeCode, salSalesOrder.getDocumentType())
                    .eq(ConDocBuTypeGroupSo::getBuTypeCode, salSalesOrder.getBusinessType()));
            if (so != null) {
                salSalesOrder.setIsNonApproval(so.getIsNonApproval());
            }
            buTypeChange = true;
        }
        else if (salSalesOrder.getBusinessType() == null) {
            throw new BaseException("业务类型不能为空");
        }
        String isNonApproval = salSalesOrder.getIsNonApproval();
        if(ConstantsEms.YES.equals(isNonApproval)){
            salSalesOrder.setHandleStatus(ConstantsEms.CHECK_STATUS);
        }else{
            //获取销售价
            getSalSale(salSalesOrder);
        }
        Long customerSid = salSalesOrder.getCustomerSid();
        String customerGroup = basCustomerMapper.selectById(customerSid).getCustomerGroup();
        if(YCX_CUSTOMER.equals(customerGroup)){
            if(salSalesOrder.getCustomerNameRemark()==null){
                throw new CustomException("客户名称备注,不能为空");
            }
        }
        String userName = ApiThreadLocalUtil.get().getSysUser().getUserName();
        if(!userName.equals(salSalesOrder.getCreatorAccount())&&!userName.equals(salSalesOrder.getTrustorAccount())){
            throw new CustomException("不是创建人或委托人，不允许此操作！");
        }
        judgeNull(salSalesOrder);
        //确认时判断
        if(ConstantsEms.CHECK_STATUS.equals(salSalesOrder.getHandleStatus())){
            //报表变更
//            changeYF(salSalesOrder);
            setConfirmInfo(salSalesOrder);
        }
        Long salesOrderSid = salSalesOrder.getSalesOrderSid();
        //改变主表处理状态
        changeInvoutStatus(salSalesOrder);
        int row = salSalesOrderMapper.updateAllById(salSalesOrder);
        if (row > 0) {
            //销售订单-明细对象
            List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrder.getSalSalesOrderItemList();
            setItemNum(salSalesOrderItemList);
            //初始值赋值
            setInitial(salSalesOrderItemList);
            advancesReceived(salSalesOrder);
            if (CollectionUtils.isNotEmpty(salSalesOrderItemList)) {
                updatePurchasePrice(salSalesOrderItemList);
                // setNull(salSalesOrderItemList);
                changePrice(salSalesOrder);
                setItemValue(salSalesOrder, salSalesOrderItemList);
                List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                        .eq(SalSalesOrderItem::getSalesOrderSid, salSalesOrder.getSalesOrderSid())
                );
                List<Long> longs = salSalesOrderItems.stream().map(li -> li.getSalesOrderItemSid()).collect(Collectors.toList());
                List<Long> longsNow = salSalesOrderItemList.stream().map(li -> li.getSalesOrderItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if(CollectionUtil.isNotEmpty(reduce)){
                    List<SalSalesOrderItem> reduceList = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                            .in(SalSalesOrderItem::getSalesOrderItemSid, reduce)
                    );
                    salSalesOrderItemMapper.deleteBatchIds(reduce);
                    salSalesOrderDeliveryPlanMapper.delete(new QueryWrapper<SalSalesOrderDeliveryPlan>().lambda()
                            .eq(SalSalesOrderDeliveryPlan::getSalesOrderItemSid,reduce));
                }
                //修改明细
                List<SalSalesOrderItem> exitItem = salSalesOrderItemList.stream().filter(li -> li.getSalesOrderItemSid() != null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(exitItem)){
                    exitItem.forEach(li->{
                        salSalesOrderItemMapper.updateAllById(li);
                        salSalesOrderDeliveryPlanMapper.delete(new QueryWrapper<SalSalesOrderDeliveryPlan>().lambda()
                                .eq(SalSalesOrderDeliveryPlan::getSalesOrderItemSid,li.getSalesOrderItemSid()));
                        List<SalSalesOrderDeliveryPlan> deliveryPlanList = li.getDeliveryPlanList();
                        //销售发货计划
                        if(CollectionUtil.isNotEmpty(deliveryPlanList)){
                            BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if(sum.compareTo(li.getQuantity())==1){
                                throw new CustomException("第"+li.getItemNum()+"行，订单量小于计划发货量总和，请核实！");
                            }
                            deliveryPlanList.forEach(i->{
                                i.setSalesOrderItemSid(li.getSalesOrderItemSid())
                                        .setSalesOrderSid(li.getSalesOrderSid());
                            });
                            salSalesOrderDeliveryPlanMapper.inserts(deliveryPlanList);
                        }
                    });
                }
                //新增明细
                List<SalSalesOrderItem> nullItem = salSalesOrderItemList.stream().filter(li -> li.getSalesOrderItemSid() == null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(nullItem)) {
                    int max = salSalesOrderItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                    for (int i = 0; i < nullItem.size(); i++) {
                        int maxItem = max + i + 1;
                        nullItem.get(i).setItemNum(maxItem);
                        nullItem.get(i).setSalesOrderSid(salSalesOrder.getSalesOrderSid());
                        salSalesOrderItemMapper.insert(nullItem.get(i));
                        List<SalSalesOrderDeliveryPlan> deliveryPlanList = nullItem.get(i).getDeliveryPlanList();
                        //销售发货计划
                        if (CollectionUtil.isNotEmpty(deliveryPlanList)) {
                            BigDecimal sum = deliveryPlanList.stream().map(h -> h.getPlanQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            if(sum.compareTo(nullItem.get(i).getQuantity())==1){
                                throw new CustomException("第"+nullItem.get(i).getItemNum()+"行，订单量小于计划发货量总和，请核实！");
                            }
                            for (SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan : deliveryPlanList) {
                                salSalesOrderDeliveryPlan.setSalesOrderItemSid(nullItem.get(i).getSalesOrderItemSid())
                                        .setSalesOrderSid(nullItem.get(i).getSalesOrderSid());
                            }
                            salSalesOrderDeliveryPlanMapper.inserts(deliveryPlanList);
                        }
                    }
                }
            }
            //销售订单-附件对象
            List<SalSalesOrderAttachment> salSalesOrderAttachmentList = salSalesOrder.getAttachmentList();
            salSalesOrderAttachmentMapper.delete(new UpdateWrapper<SalSalesOrderAttachment>()
                    .lambda().eq(SalSalesOrderAttachment::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
            if (CollectionUtils.isNotEmpty(salSalesOrderAttachmentList)) {
                addSalSalesOrderAttachment(salSalesOrder, salSalesOrderAttachmentList);
            }
            // 操作日志详情记录
            String remark = "";
            if (buTypeChange) {
                List<ConBuTypeSalesOrder> listType = conBuTypeSalesOrderMapper.selectList(new QueryWrapper<>());
                Map<String, String> map = listType.stream().collect(Collectors.toMap(ConBuTypeSalesOrder::getCode, ConBuTypeSalesOrder::getName, (key1, key2) -> key2));
                String oldOne = map.containsKey(old.getBusinessType()) ? map.get(old.getBusinessType()) : "";
                String newOne = map.containsKey(salSalesOrder.getBusinessType()) ? map.get(salSalesOrder.getBusinessType()) : "";
                remark = remark + "业务类型字段变更，更新前：" + oldOne + "，更新后：" + newOne;
            }
            MongodbUtil.insertUserLog(salSalesOrder.getSalesOrderSid(), BusinessType.CHANGE.getValue(), null, TITLE, remark);
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(salSalesOrder.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            String shortName = basCustomerMapper.selectById(salSalesOrder.getCustomerSid()).getShortName();
            sysBusinessBcst.setTitle("客户:"+shortName+"，销售订单编号"+salSalesOrder.getSalesOrderCode()+"的信息发生变更，请知悉！")
                    .setDocumentSid(salSalesOrder.getSalesOrderSid())
                    .setDocumentCode(salSalesOrder.getSalesOrderCode())
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        if(!ConstantsEms.YES.equals(isNonApproval)){
            //审批
            if (ConstantsEms.SUBMIT_STATUS.equals(salSalesOrder.getHandleStatus())){
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.XSDD_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(salSalesOrder.getSalesOrderSid().toString());
                formParameter.setFormId(salSalesOrder.getSalesOrderSid().toString());
                formParameter.setFormCode(salSalesOrder.getSalesOrderCode());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }
        }
        // 判断 新旧合同 的 特殊用途是否 是 临时过渡, 删除该待办
        if (old.getSaleContractSid() != null && !old.getSaleContractSid().equals(salSalesOrder.getSaleContractSid())) {
            List<Long> contractSidList = new ArrayList<Long>(){{
                add(old.getSaleContractSid());
            }};
            if (salSalesOrder.getSaleContractSid() != null) {
                contractSidList.add(salSalesOrder.getSaleContractSid());
            }
            List<SalSaleContract> saleContractList = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>().lambda()
                    .in(SalSaleContract::getSaleContractSid, contractSidList));
            if (CollectionUtil.isNotEmpty(saleContractList)) {
                Map<Long, SalSaleContract> map = saleContractList.stream().collect(Collectors.toMap(SalSaleContract::getSaleContractSid, Function.identity()));
                if (map.containsKey(old.getSaleContractSid()) && ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(old.getSaleContractSid()).getContractPurpose())) {
                    if (!map.containsKey(salSalesOrder.getSaleContractSid()) || !ConstantsOrder.CONTRACT_PURPOSE_LSGD.equals(map.get(salSalesOrder.getSaleContractSid()).getContractPurpose())) {
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .eq(SysTodoTask::getDocumentSid, salSalesOrder.getSalesOrderSid()).eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SALE_ORDER + "-" + ConstantsTable.TABLE_SALE_CONTRACT)
                                .like(SysTodoTask::getTitle, "使用的是过渡合同"));
                    }
                }
            }
        }
        return row;
    }

    /**
     * 设置签收状态
     *
     */
    @Override
    public int setSignStatus(OrderItemStatusSignRequest request){
        Long[] sidList = request.getSidList();
        String signInStatus = request.getSignInStatus();
        int row = salSalesOrderMapper.update(new SalSalesOrder(), new UpdateWrapper<SalSalesOrder>().lambda()
                .in(SalSalesOrder::getSalesOrderSid, sidList)
                .set(SalSalesOrder::getSignInStatus, signInStatus)
        );
        return row;
    }

    /**
     * 设置首批
     *
     */
    @Override
    public int setShouPi(OrderItemShouPiRequest request){
        List<Long> sidList = request.getSids();
        String isMakeShoupi = request.getIsMakeShoupi();
        int row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                .in(SalSalesOrderItem::getSalesOrderItemSid, sidList)
                .set(SalSalesOrderItem::getIsMakeShoupi, isMakeShoupi)
        );
        return row;
    }
    //设置首缸
    @Override
    public int setShouGang(SalSalesOrderItemSetRequest quest){
        int row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                .in(SalSalesOrderItem::getSalesOrderItemSid, quest.getSalSalesOrderItemSidList())
                .set(SalSalesOrderItem::getIsMakeShougang, quest.getIsMakeShougang())
        );
        return row;
    }

    //设置下单状态
    @Override
    public int setMaterialOrder(MaterialOrderRequest quest){
        Long[] sidList = quest.getSidList();
        String flCaigouxiadanStatus = quest.getFlCaigouxiadanStatus();
        String mlCaigouxiadanStatus = quest.getMlCaigouxiadanStatus();
        if(ConstantsEms.MATERIAL_F.equals(quest.getType())){
            List<SalSalesOrderItem> salSalesOrderItems = new ArrayList<>();
            for (Long item : sidList) {
                SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
                salSalesOrderItem.setSalesOrderItemSid(item)
                        .setFlCaigouxiadanStatus(flCaigouxiadanStatus);
                salSalesOrderItems.add(salSalesOrderItem);
            }
            salSalesOrderItemMapper.updatesFl(salSalesOrderItems);
        }

        if(ConstantsEms.MATERIAL_M.equals(quest.getType())){
            List<SalSalesOrderItem> salSalesOrderItems = new ArrayList<>();
            for (Long item : sidList) {
                SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
                salSalesOrderItem.setSalesOrderItemSid(item)
                        .setMlCaigouxiadanStatus(mlCaigouxiadanStatus);
                salSalesOrderItems.add(salSalesOrderItem);
            }
            salSalesOrderItemMapper.updatesMl(salSalesOrderItems);
        }

        return 1;
    }

    //设置到期天数
    @Override
    public int setToexpireDays(OrderItemToexpireRequest quest){
        int row = salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                .in(SalSalesOrderItem::getSalesOrderItemSid, quest.getSidList())
                .set(SalSalesOrderItem::getToexpireDays, quest.getToexpireDays())
        );
        return row;
    }

    @Override
    public List<BasMaterial> getMaterialInfo(BasSaleOrderRequest basSaleOrderRequest) {
        List<BasMaterial> materialList = new ArrayList<>();
        Long[] materialBarcodeSids = basSaleOrderRequest.getMaterialBarcodeSidList();
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y"));
        if (taxRate == null) { taxRate = new ConTaxRate(); }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);
        for (Long materialBarcodeSid : materialBarcodeSids) {
            BasMaterial basMaterial = basMaterialBarcodeMapper.selectBasMaterialBarcodeById(materialBarcodeSid);
            Optional<BasMaterial> materialSkuOptional = Optional.ofNullable(basMaterial);
            ConTaxRate finalTaxRate = taxRate;
            materialSkuOptional.ifPresent(s -> {
                //查找销售价明细
                if ("2".equals(basSaleOrderRequest.getType())) {
                    SalSalePrice salSalePrice = new SalSalePrice();
                    salSalePrice.setSaleMode(basSaleOrderRequest.getMode())
                            .setCustomerSid(basSaleOrderRequest.getCustomerSid())
                            .setRawMaterialMode(basSaleOrderRequest.getRawMaterialMode())
                            .setSku2Sid(basMaterial.getSku2Sid())
                            .setSku1Sid(basMaterial.getSku1Sid())
                            .setMaterialSid(basMaterial.getMaterialSid());
                    //获取销售
                    SalSalePriceItem item = salSalePriceServiceImpl.getNewSalePrice(salSalePrice);
                    if (item != null) {
                        if (item.getSalePriceTax()!= null){
                            BigDecimal init = BigDecimal.ONE;
                            //销售价不含税：销售价含税/(1+税率)
                            BigDecimal price = BigDecimal.ZERO;
                            price = item.getSalePriceTax().divide(init.add(item.getTaxRate()), 6,BigDecimal.ROUND_HALF_UP);
                            item.setSalePrice(price);
                        }
                    }
                    if (item == null || item.getSalePriceTax() == null) {
                        item = new SalSalePriceItem();
                        item.setTaxRate(finalTaxRate.getTaxRateValue());
                        item.setSystemTaxRate(finalTaxRate.getTaxRateValue());
                        item.setUnitBase(basMaterial.getUnitBase());
                        item.setUnitBaseName(basMaterial.getUnitBaseName());
                        item.setUnitPrice(basMaterial.getUnitBase());
                        item.setUnitPriceName(basMaterial.getUnitBaseName());
                        item.setUnitConversionRate(BigDecimal.ONE);
                    }
                    basMaterial.setSalePriceDetail(item);
                }
                else {
                    //查找采购价
//                    basMaterial.setTaxRate(taxRate.getTaxRateValue().toString());
                    PurPurchasePrice purchasePrice = new PurPurchasePrice();
                    purchasePrice.setVendorSid(basSaleOrderRequest.getVendorSid())
                            .setPurchaseMode(basSaleOrderRequest.getMode())
                            .setSku2Sid(basMaterial.getSku2Sid())
                            .setRawMaterialMode(basSaleOrderRequest.getRawMaterialMode())
                            .setMaterialSid(basMaterial.getMaterialSid())
                            .setSku1Sid(basMaterial.getSku1Sid());
                    PurPurchasePriceItem item = iPurPurchasePriceService.getNewPurchase(purchasePrice);
                    if (item.getPurchasePriceTax() != null) {
                        BigDecimal init = BigDecimal.ONE;
                        //采购价不含税：采购价含税/(1+税率)
                        BigDecimal price = null;
                        price = item.getPurchasePriceTax().divide(init.add(item.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP);
                        item.setPurchasePrice(price);
                    }
                    if (item == null || item.getPurchasePriceTax() == null) {
                        item = new PurPurchasePriceItem();
                        item.setTaxRate(finalTaxRate.getTaxRateValue());
                        item.setSystemTaxRate(finalTaxRate.getTaxRateValue());
                        item.setUnitBase(basMaterial.getUnitBase());
                        item.setUnitBaseName(basMaterial.getUnitBaseName());
                        item.setUnitPrice(basMaterial.getUnitBase());
                        item.setUnitPriceName(basMaterial.getUnitBaseName());
                        item.setUnitConversionRate(BigDecimal.ONE);
                    }
                    basMaterial.setPurchasePriceDetail(item);
                }
                // 税率
                if (basMaterial.getTaxRate() == null) {
                    basMaterial.setTaxRate(String.valueOf(finalTaxRate.getTaxRateValue()));
                }
                materialList.add(basMaterial);
            });
        }
        return materialList;
    }
    //更新价格(采购价/销售价)
    @Override
    public AjaxResult updatePrice(BasSaleOrderRequest basSaleOrderRequest) {
        List<PurPurchaseOrderItem> purPurchaseOrderItemList = basSaleOrderRequest.getPurPurchaseOrderItemList();
        List<SalSalesOrderItem> salesOrderItemsList = basSaleOrderRequest.getSalesOrderItemsList();
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        OrderUpdatePriceResponse response = new OrderUpdatePriceResponse();
        HashSet<String> hashCodeSet = new HashSet<>();
        List<BigDecimal> taxList = new ArrayList<>();
        HashSet<String> hashBarcodeSet = new HashSet<>();
        //查找销售价明细
        if ("2".equals(basSaleOrderRequest.getType())) {
            salesOrderItemsList.forEach(li->{
                SalSalePrice salSalePrice = new SalSalePrice();
                salSalePrice.setSaleMode(basSaleOrderRequest.getMode())
                        .setCustomerSid(basSaleOrderRequest.getCustomerSid())
                        .setRawMaterialMode(basSaleOrderRequest.getRawMaterialMode())
                        .setSku2Sid(li.getSku2Sid())
                        .setSku1Sid(li.getSku1Sid())
                        .setMaterialSid(li.getMaterialSid());
                li.setSalePriceTax(null)
                        .setSalePrice(null)
                        .setUnitBaseName(null)
                        .setUnitPriceName(null)
                        .setUnitBase(null)
                        .setUnitPrice(null)
                        .setTaxRate(null);
                //获取销售
                SalSalePriceItem item = salSalePriceServiceImpl.getNewSalePrice(salSalePrice);
                if (item != null) {
                    if (item.getSalePriceTax()!= null){
                        BigDecimal init = BigDecimal.ONE;
                        //销售价不含税：销售价含税/(1+税率)
                        BigDecimal price = BigDecimal.ZERO;
                        price = item.getSalePriceTax().divide(init.add(item.getTaxRate()), 6,BigDecimal.ROUND_HALF_UP);
                        if(li.getUnitConversionRate()!=null){
                            if(item.getUnitConversionRate().compareTo(li.getUnitConversionRate())!=0){
                                int sizeBefore = hashCodeSet.size();
                                hashCodeSet.add(li.getBarcode());
                                int sizeAfter = hashCodeSet.size();
                                if(sizeBefore!=sizeAfter){
                                    CommonErrMsgResponse commonErrMsgRespons = new CommonErrMsgResponse();
                                    commonErrMsgRespons.setCode(Long.valueOf(li.getBarcode()));
                                    commonErrMsgRespons.setMsg("新的单位换算比例与原单位换算比例不一致");
                                    msgList.add(commonErrMsgRespons);
                                }
                            }
                        }
                        if(item.getTaxRate()!=null){
                            if(CollectionUtil.isEmpty(taxList)){
                                taxList.add(item.getTaxRate());
                            }else{
                                BigDecimal tax = taxList.get(0);
                                if(tax.compareTo(item.getTaxRate())!=0){
                                    if(hashBarcodeSet.add(li.getBarcode())){
                                        CommonErrMsgResponse commonErrMsgRespons = new CommonErrMsgResponse();
                                        commonErrMsgRespons.setCode(Long.valueOf(li.getBarcode()));
                                        commonErrMsgRespons.setMsg("新的税率与该订单其他明细行的税率不一致");
                                        msgList.add(commonErrMsgRespons);
                                        response.setIsIncludeTax(ConstantsEms.YES);
                                    }
                                }
                            }
                        }
                        li.setSalePriceTax(item.getSalePriceTax())
                                .setSalePrice(price)
                                .setUnitBase(item.getUnitBase())
                                .setUnitBaseName(item.getUnitBaseName())
                                .setUnitPriceName(item.getUnitPriceName())
                                .setUnitPrice(item.getUnitPrice())
                                .setUnitConversionRate(item.getUnitConversionRate())
                                .setTaxRate(item.getTaxRate());
                    }
                }
            });
            response.setMsgList(msgList).setSalesOrderItemsList(salesOrderItemsList);
            return AjaxResult.success(response);
        } else {
            //查找采购价
            purPurchaseOrderItemList.forEach(li->{
                PurPurchasePrice purchasePrice = new PurPurchasePrice();
                purchasePrice.setVendorSid(basSaleOrderRequest.getVendorSid())
                        .setPurchaseMode(basSaleOrderRequest.getMode())
                        .setSku2Sid(li.getSku2Sid())
                        .setRawMaterialMode(basSaleOrderRequest.getRawMaterialMode())
                        .setMaterialSid(li.getMaterialSid())
                        .setSku1Sid(li.getSku1Sid());
                li.setPurchasePriceTax(null)
                        .setPurchasePrice(null)
                        .setUnitBaseName(null)
                        .setUnitPriceName(null)
                        .setUnitBase(null)
                        .setUnitPrice(null)
                        .setTaxRate(null);
                PurPurchasePriceItem item = iPurPurchasePriceService.getNewPurchase(purchasePrice);
                if (item.getPurchasePriceTax() != null) {
                    BigDecimal init = BigDecimal.ONE;
                    //采购价不含税：采购价含税/(1+税率)
                    BigDecimal price = null;
                    price = item.getPurchasePriceTax().divide(init.add(item.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP);
                    if (li.getUnitConversionRate() != null) {
                        if (item.getUnitConversionRate().compareTo(li.getUnitConversionRate()) != 0) {
                            int sizeBefore = hashCodeSet.size();
                            hashCodeSet.add(li.getBarcode());
                            int sizeAfter = hashCodeSet.size();
                            if (sizeBefore != sizeAfter) {
                                CommonErrMsgResponse commonErrMsgRespons = new CommonErrMsgResponse();
                                commonErrMsgRespons.setCode(Long.valueOf(li.getBarcode()));
                                commonErrMsgRespons.setMsg("新的单位换算比例与原单位换算比例不一致");
                                msgList.add(commonErrMsgRespons);
                            }
                        }
                    }
                    if(item.getTaxRate()!=null){
                        if(CollectionUtil.isEmpty(taxList)){
                            taxList.add(item.getTaxRate());
                        }else{
                            BigDecimal tax = taxList.get(0);
                            if(tax.compareTo(item.getTaxRate())!=0){
                                if(hashBarcodeSet.add(li.getBarcode())){
                                    CommonErrMsgResponse commonErrMsgRespons = new CommonErrMsgResponse();
                                    commonErrMsgRespons.setCode(Long.valueOf(li.getBarcode()));
                                    commonErrMsgRespons.setMsg("新的税率与该订单其他明细行的税率不一致");
                                    msgList.add(commonErrMsgRespons);
                                    response.setIsIncludeTax(ConstantsEms.YES);
                                }
                            }
                        }
                    }
                    li.setPurchasePriceTax(item.getPurchasePriceTax())
                            .setPurchasePrice(price)
                            .setUnitBaseName(item.getUnitBaseName())
                            .setUnitPriceName(item.getUnitPriceName())
                            .setUnitBase(item.getUnitBase())
                            .setUnitPrice(item.getUnitPrice())
                            .setUnitConversionRate(item.getUnitConversionRate())
                            .setTaxRate(item.getTaxRate());
                }
            });
            response.setMsgList(msgList).setPurPurchaseOrderItemList(purPurchaseOrderItemList);
            return AjaxResult.success(response);
        }
    }
    private SalSalePrice checkPriceUnique(SalSalePrice salSalePrice, List<SalSalePrice> salSalePriceList) {
        if (salSalePriceList.size() > 0) {
            if (salSalePriceList.size() > 1) {
                throw new CustomException("存在2条或以上价格配置，请联系管理员");
            }
            if (salSalePriceList.size() > 0) {
                salSalePrice = salSalePriceList.get(0);
            }
        }
        return salSalePrice;
    }



    /**
     * 物料需求报表(商品销售订单)
     */
    @Override
    public SalSalesOrder getMaterialRequireListByCode(Long salesOrderCode) {
        SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderByCode(salesOrderCode);
        if (salSalesOrder == null) {
            throw new BaseException("销售订单号不存在，请重新输入");
        }
        SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
        salSalesOrderItem.setSalesOrderSid(salSalesOrder.getSalesOrderSid());
        List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrderItemMapper.selectSalSalesOrderItemList(salSalesOrderItem);
        if (CollectionUtils.isEmpty(salSalesOrderItemList)) {
            throw new BaseException("该销售订单没有创建明细信息");
        }
        List<TecBomItem> list = new ArrayList<>();
        salSalesOrderItemList.stream().forEach(salesOrderItem -> {
            if (salesOrderItem.getMaterialSid() != null && salesOrderItem.getSku1Sid() != null) {
                TecBomHead tecBomHead = new TecBomHead();
                tecBomHead.setMaterialSid(salesOrderItem.getMaterialSid());
                tecBomHead.setSku1Sid(salesOrderItem.getSku1Sid());
                List<TecBomHead> tecBomHeadList = tecBomHeadMapper.selectTecBomHeadList(tecBomHead);
                //Bom明细列表
                List<TecBomItem> tecBomItemList = tecBomItemMapper.getMaterialRequireList(tecBomHeadList.get(0));
                if (CollectionUtils.isEmpty(tecBomItemList)) {
                    throw new BaseException("该商品没有创建BOM信息");
                }
                tecBomItemList.stream().forEach(bomItem -> {
                    bomItem.setRequireQuantity(bomItem.getConfirmQuantity().multiply(salesOrderItem.getQuantity()));
                });
                list.addAll(tecBomItemList);
            }
        });
        salSalesOrder.setTecBomItemList(list);
        return salSalesOrder;
    }

    /**
     * 物料需求报表(商品销售订单) ---常规
     */
    @Override
    public List<TecBomItem> getMaterialRequireListByCode2(List<TecBomItem> requestList) {
        List<TecBomItem> list = new ArrayList<>();
        requestList.stream().forEach(item -> {
            List<TecBomHead> tecBomHeadList = tecBomHeadMapper.selectTecBomHeadList(new TecBomHead().setMaterialSid(item.getMaterialSid()).setSku1Sid(item.getSku1Sid()));
            if (CollectionUtils.isEmpty(tecBomHeadList)) {
                throw new BaseException("商品"+item.getSaleMaterialCode()+"没有维护BOM信息，请核实！");
            }
            TecBomHead tecBomHead = tecBomHeadList.get(0);
            tecBomHead.setPurchaseType(item.getPurchaseType())
                    .setTouseProduceStage(item.getTouseProduceStage())
                    .setMaterialCode(item.getMaterialCode())
                    .setMaterialType(item.getMaterialType());
            if(item.getVendorSid()==null){
                tecBomHead.setVendorSid(null);
            }else{
                tecBomHead.setVendorSid(String.valueOf(item.getVendorSid()));
            }
            List<TecBomItem> tecBomItemList = tecBomItemMapper.getMaterialRequireList2(tecBomHead);
            //过滤 sku1为 null
            tecBomItemList=tecBomItemList.stream().filter(it->it.getBomMaterialSku1Sid()!=null).collect(Collectors.toList());
            tecBomItemList.forEach(li->{
                if(li.getQuantity()==null){
                    throw new BaseException("存在BOM用量未填写的明细行，无法测算物料需求");
                }
            });
            //赋值销售订单带过来的信息
            tecBomItemList.forEach(tecBomItem -> {
                tecBomItem.setSaleMaterialName(item.getSaleMaterialName());//款名称
                tecBomItem.setSaleSku1Name(item.getSaleSku1Name());//款颜色
                tecBomItem.setSaleSku2Name(item.getSaleSku2Name());//款尺码
                tecBomItem.setSaleMaterialCode(item.getSaleMaterialCode());//款号
                tecBomItem.setSaleSku1Sid(item.getSku1Sid());
                tecBomItem.setSaleSku2Sid(item.getSku2Sid());
                tecBomItem.setSku2Sid(item.getSku2Sid());
                tecBomItem.setSaleMaterialSid(item.getMaterialSid().toString());
                tecBomItem.setQuantity(tecBomItem.getQuantity().divide(new BigDecimal(tecBomItem.getUnitConversionRate()),4));
                tecBomItem.setCommonSid(item.getCommonSid());
                tecBomItem.setCommonItemSid(item.getCommonItemSid());
                tecBomItem.setCommonItemNum(item.getCommonItemNum());
                tecBomItem.setCommonItemSidRemark(item.getCommonItemSid()!=null?item.getCommonItemSid().toString():null);//明细行行sid
                if(item.getSumDimension().equals("LS1")){
                    tecBomItem.setMaterialCodeRemark(item.getSaleMaterialCode());//款备注
                }
                if(item.getSumDimension().equals("KLS1")){
                    tecBomItem.setMaterialSkuRemark(item.getSaleSku1Name());//款颜色
                }
                if(item.getSumDimension().equals("KS1LS1")||item.getSumDimension().equals("DKS1LS1")){
                    tecBomItem.setMaterialSku2Remark(item.getSaleSku2Name());//款尺码
                }
                //生产订单
                if(item.getManufactureOrderCode()!=null){
                    tecBomItem.setManufactureOrderCodeRemark(item.getManufactureOrderCode().toString());
                    tecBomItem.setCommonCode(item.getManufactureOrderCode().toString());
                    tecBomItem.setManufactureOrderCode(item.getManufactureOrderCode());
                }
                //采购订单
                if(item.getPurchaseOrderCode()!=null){
                    tecBomItem.setPurchaseOrderCodeRemark(item.getPurchaseOrderCode().toString());
                    tecBomItem.setCommonCode(item.getPurchaseOrderCode().toString());
                    tecBomItem.setPurchaseOrderCode(item.getPurchaseOrderCode());
                }
                //销售订单
                if(item.getSalesOrderCode()!=null){
                    tecBomItem.setSalesOrderCodeRemark(item.getSalesOrderCode().toString());
                    tecBomItem.setCommonCode(item.getSalesOrderCode().toString());
                    tecBomItem.setSalesOrderCode(item.getSalesOrderCode());
                }
            });
            ArrayList<TecBomItem> temporList = new ArrayList<>();
            tecBomItemList.forEach(bomItem -> {
                //判断同种物料、同种sku1 合并 需求量值累加
                if (CollectionUtils.isNotEmpty(list)) {
                    Boolean exit = true;
                    for (TecBomItem li : list) {
                        //料号+料SKU1
                        if(item.getSumDimension().equals("LS1")){
                            if (li.getBomMaterialSid().equals(bomItem.getBomMaterialSid()) && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())) {
                                //款备注
                                String materialCodeRemark = bomItem.getMaterialCodeRemark();//当前符合条件的款备注
                                String remark = li.getMaterialCodeRemark();//当前所有的跨备注
                                if (StrUtil.isBlank(materialCodeRemark) && StrUtil.isNotBlank(remark)) {
                                    li.setMaterialSkuRemark(remark);
                                }
                                else if (StrUtil.isNotBlank(materialCodeRemark) && StrUtil.isBlank(remark)) {
                                    li.setMaterialCodeRemark(materialCodeRemark);
                                }
                                else if (StrUtil.isNotBlank(materialCodeRemark) && StrUtil.isNotBlank(remark)) {
                                    Boolean match = match(remark, materialCodeRemark);//重复校验
                                    if(!match){
                                        String code = materialCodeRemark + ";" + remark;
                                        li.setMaterialCodeRemark(code);
                                    }
                                }
                                //计算重复物料的需求量
                                bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                //单间用量含损耗率
                                li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                //单间不含
                                li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                //订单号赋值
                                setCode(li, bomItem);
                                exit = false;
                                break;
                            }
                        }
                        //款号+料号+料SKU1
                        if(item.getSumDimension().equals("KLS1")){
                            if (li.getBomMaterialSid().equals(bomItem.getBomMaterialSid()) && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())&&li.getSaleMaterialSid().equals(bomItem.getSaleMaterialSid())) {
                                //款颜色
                                String materialSkuRemark = bomItem.getMaterialSkuRemark();//当前符合条件的款备注
                                String remark = li.getMaterialSkuRemark();//当前所有的跨备注
                                if (StrUtil.isBlank(materialSkuRemark) && StrUtil.isNotBlank(remark)) {
                                    li.setMaterialSkuRemark(remark);
                                }
                                else if (StrUtil.isNotBlank(materialSkuRemark) && StrUtil.isBlank(remark)) {
                                    li.setMaterialSkuRemark(materialSkuRemark);
                                }
                                else if (StrUtil.isNotBlank(materialSkuRemark) && StrUtil.isNotBlank(remark)) {
                                    Boolean match = match(remark, materialSkuRemark);//重复校验
                                    if(!match){
                                        String code = materialSkuRemark + ";" + remark;
                                        li.setMaterialSkuRemark(code);
                                    }
                                }
                                //计算重复物料的需求量
                                bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                //单间用量含损耗率
                                li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                //单间不含
                                li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                //订单号赋值
                                setCode( li, bomItem);
                                exit = false;
                                break;
                            }
                        }

                        //款号+款颜色+料号+料SKU1
                        if(item.getSumDimension().equals("KS1LS1")){
                            if (li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                    && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                    &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                    &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())) {
                                //款尺码
                                String materialSku2Remark = bomItem.getMaterialSku2Remark();//当前符合条件的款备注
                                String remark = li.getMaterialSku2Remark();//当前所有的跨备注
                                if (StrUtil.isBlank(materialSku2Remark) && StrUtil.isNotBlank(remark)) {
                                    li.setMaterialSkuRemark(remark);
                                }
                                else if (StrUtil.isNotBlank(materialSku2Remark) && StrUtil.isBlank(remark)) {
                                    li.setMaterialSku2Remark(materialSku2Remark);
                                }
                                else if (StrUtil.isNotBlank(materialSku2Remark) && StrUtil.isNotBlank(remark)) {
                                    Boolean match = match(remark, materialSku2Remark);//重复校验
                                    if(!match){
                                        String code = materialSku2Remark + ";" + remark;
                                        li.setMaterialSku2Remark(code);
                                    }
                                }
                                //计算重复物料的需求量
                                bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                //单间用量含损耗率
                                li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                //单间不含
                                li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                //订单号赋值
                                setCode(li,bomItem);
                                exit = false;
                                break;
                            }
                        }

                        //商品订单号+款号+款颜色+款尺码+料号+料SKU1+料SKU2
                        if(item.getSumDimension().equals("DKS1S2LS1S2")){
                            if(li.getBomMaterialSku2Sid()!=null){
                                if (li.getCommonCode()!=null?(
                                        li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                                && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                                &&li.getBomMaterialSku2Sid().equals(bomItem.getBomMaterialSku2Sid())
                                                &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                                &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                                &&li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())
                                                &&li.getCommonCode().equals(bomItem.getCommonCode()))
                                        :(
                                        li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                                && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                                &&li.getBomMaterialSku2Sid().equals(bomItem.getBomMaterialSku2Sid())
                                                &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                                &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                                &&li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())
                                )

                                ) {
                                    //计算重复物料的需求量
                                    bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                    bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                    li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                    //单间用量含损耗率
                                    li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                    //单间不含
                                    li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                    //款数量
                                    li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                    //订单号赋值
//                                    setCode(li,bomItem);
                                    exit = false;
                                    break;
                                }
                            }else{
                                if (li.getCommonCode()!=null?
                                        (li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                                &&li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                                &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                                &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                                &&li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())
                                                &&li.getCommonCode().equals(bomItem.getCommonCode()))
                                        :(
                                        li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                                &&li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                                &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                                &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                                &&li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())
                                )
                                ) {
                                    //计算重复物料的需求量
                                    bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                    bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                    li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                    //单间用量含损耗率
                                    li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                    //单间不含
                                    li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                    //款数量
                                    li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                    //订单号赋值
                                    setCodeItem(li,bomItem);
                                    exit = false;
                                    break;
                                }
                            }
                        }

                        //商品订单号+款号+款颜色+料号+料SKU1
                        if(item.getSumDimension().equals("DKS1LS1")){
                            if (    li.getCommonCode()!=null?(
                                    li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                            &&li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                            &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                            &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                            &&li.getCommonCode().equals(bomItem.getCommonCode())
                            ):(
                                    li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                            &&li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                            &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                            &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                            )

                            ) {
                                //款尺码
                                String materialSku2Remark = bomItem.getMaterialSku2Remark();//当前符合条件的款备注
                                String remark = li.getMaterialSku2Remark();//当前所有的跨备注
                                if (StrUtil.isBlank(materialSku2Remark) && StrUtil.isNotBlank(remark)) {
                                    li.setMaterialSkuRemark(remark);
                                }
                                else if (StrUtil.isNotBlank(materialSku2Remark) && StrUtil.isBlank(remark)) {
                                    li.setMaterialSku2Remark(materialSku2Remark);
                                }
                                else if (StrUtil.isNotBlank(materialSku2Remark) && StrUtil.isNotBlank(remark)) {
                                    Boolean match = match(remark, materialSku2Remark);//重复校验
                                    if(!match){
                                        String code = materialSku2Remark + ";" + remark;
                                        li.setMaterialSku2Remark(code);
                                    }
                                }
                                //计算重复物料的需求量
                                bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                //单间用量含损耗率
                                li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                //单间不含
                                li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                //订单号赋值
                                setCodeItem(li,bomItem);
                                exit = false;
                                break;
                            }
                        }

                        //款号+款颜色+款尺码+料号+料SKU1+料SKU2
                        if(item.getSumDimension().equals("KS1S2LS1S2")){
                            if(li.getBomMaterialSku2Sid()!=null){
                                if (li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                        && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                        &&li.getBomMaterialSku2Sid().equals(bomItem.getBomMaterialSku2Sid())
                                        &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                        &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                        &&li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())

                                ) {
                                    //计算重复物料的需求量
                                    bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                    bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                    li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                    //单间用量含损耗率
                                    li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                    //单间不含
                                    li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                    //款数量
                                    li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                    //订单号赋值
                                    setCode(li,bomItem);
                                    exit = false;
                                    break;
                                }
                            }else{
                                if (li.getBomMaterialSid().equals(bomItem.getBomMaterialSid())
                                        && li.getBomMaterialSku1Sid().equals(bomItem.getBomMaterialSku1Sid())
                                        &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                        &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                        &&li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())

                                ) {
                                    //计算重复物料的需求量
                                    bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                                    bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                                    li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                    li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                                    //款数量
                                    li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                    //单间用量含损耗率
                                    li.setLossInnerQuantity(li.getLossInnerQuantity().add(bomItem.getLossInnerQuantity()));
                                    //单间不含
                                    li.setQuantity(li.getQuantity().add(bomItem.getQuantity()));
                                    //订单号赋值
                                    setCode(li,bomItem);
                                    exit = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (exit) {
                        temporList.clear();
                        bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                        bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                        bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                        //款数量
                        bomItem.setProductQuantity(item.getQuantity());
                        //添加明细行数量
                        if(bomItem.getCommonItemSidRemark()!=null){
                            HashMap<String, BigDecimal> quantityMap = bomItem.getQuantityMap();
                            quantityMap.put(bomItem.getCommonItemSidRemark(),bomItem.getLossRequireQuantity());
                        }
                        temporList.add(bomItem);
                    }

                } else {
                    bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                    bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                    bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                    //款数量
                    bomItem.setProductQuantity(item.getQuantity());
                    //添加明细行数量
                    if(bomItem.getCommonItemSidRemark()!=null){
                        HashMap<String, BigDecimal> quantityMap = bomItem.getQuantityMap();
                        quantityMap.put(bomItem.getCommonItemSidRemark(),bomItem.getLossRequireQuantity());
                    }
                    temporList.add(bomItem);
                }
                list.addAll(temporList);
                temporList.clear();
            });
        });
        //拉链标识
        list.forEach(o -> {
            if (ConstantsEms.ZIPPER_ZH.equals(o.getZipperFlag()) || ConstantsEms.ZIPPER_ZT.equals(o.getZipperFlag()) || ConstantsEms.ZIPPER_LP.equals(o.getZipperFlag())) {
                BasSku sku = zipperLengthMapper.getZipperSku2(o);
                if (sku!= null) {
                    o.setSku2Name(sku.getSkuName());
                    o.setBomMaterialSku2Sid(sku.getSkuSid());
                }
            }
        });
        //过滤
        String sumDimension = requestList.get(0).getSumDimension();//汇总维度
        if(sumDimension.equals("LS1")){
            list.forEach(li->{
                li.setSaleMaterialCode(null)
                        .setSaleMaterialName(null)
                        .setSaleMaterialSid(null)
                        .setSaleSku2Sid(null)
                        .setSaleSku2Name(null)
                        .setSaleSku1Sid(null)
                        .setQuantity(null)
                        .setLossRate(null)
                        .setSku2Name(null)
                        .setSaleSku1Name(null);
            });
        }else if(sumDimension.equals("KLS1")){
            list.forEach(li->{
                li.setSaleSku2Name(null)
                        .setSaleSku2Sid(null)
                        .setSku2Name(null)
                        .setSaleSku1Sid(null)
                        .setQuantity(null)
                        .setLossRate(null)
                        .setSaleSku1Name(null);
            });
        }else if(sumDimension.equals("KS1LS1")||sumDimension.equals("DKS1LS1")) {
            list.forEach(li->{
                li.setSaleSku2Sid(null)
                        .setSku2Name(null)
                        .setSaleSku2Name(null);
            });
        }
        if(sumDimension.equals("DKS1LS1")||sumDimension.equals("DKS1S2LS1S2")){
            list.forEach(li->{
                li.setSalesOrderCodeRemark(null)
                        .setPurchaseOrderCodeRemark(null)
                        .setManufactureOrderCodeRemark(null);

            });
        }
        list.forEach(li->{
            if(li.getPurchaseOrderCodeRemark()!=null){
                li.setPurchaseOrderCode(null);
            }
            if(li.getSalesOrderCodeRemark()!=null){
                li.setSalesOrderCode(null);
            }
            if(li.getManufactureOrderCodeRemark()!=null){
                li.setManufactureOrderCode(null);
            }
        });
        //计算可用库存量
        list.forEach(bomItem -> {
            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
            invInventoryLocation.setMaterialSid(bomItem.getBomMaterialSid())
                    .setSku1Sid(bomItem.getBomMaterialSku1Sid())
                    .setUsableType(ConstantsEms.USABLE_TYPE_KY)
                    .setSku2Sid(bomItem.getBomMaterialSku2Sid());
            List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectInvInventoryLocationList(invInventoryLocation);
            //计算该物料所有仓库库存量信息
            if (CollectionUtils.isNotEmpty(invInventoryLocations)) {
                BigDecimal sum = invInventoryLocations.stream().map(li -> li.getAbleQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                bomItem.setUnlimitedQuantity(sum);
            } else {
                bomItem.setUnlimitedQuantity(BigDecimal.ZERO);
            }
        });
        //需求量计算取整
        list.forEach(h->{
            if(ConstantsEms.YES.equals(h.getIsInteger())){
                h.setRequireQuantity(h.getRequireQuantity().setScale(0,BigDecimal.ROUND_HALF_UP))
                        .setLossRequireQuantity(h.getLossRequireQuantity().setScale(0,BigDecimal.ROUND_HALF_UP));
                h.setRequireQuantityView(h.getRequireQuantity().toString())
                        .setLossRequireQuantityView(h.getLossRequireQuantity().toString());
            }else{
//                h.setRequireQuantity(h.getRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP))
//                        .setLossRequireQuantity(h.getLossRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
                DecimalFormat decimalFormat = new DecimalFormat("0.0000#");
                String requireQuantity = decimalFormat.format(h.getRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
                String lossRequireQuantity = decimalFormat.format(h.getLossRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
                h.setRequireQuantityView(requireQuantity)
                        .setLossRequireQuantityView(lossRequireQuantity);
            }
        });
        //按物料编码降序
        List<TecBomItem> descList = list.stream()
                .sorted(Comparator.comparing(TecBomItem::getMaterialCode).reversed()
                )
                .collect(Collectors.toList());
        return descList;
    }

    /**
     * 匹配值 是否重复
     */
    public Boolean match(String remark,String match){
        String[] remarkList = remark.split(";");
        List remarkListNow = Arrays.asList(remarkList);
        boolean exit = remarkListNow.stream().anyMatch(m -> m.equals(match));
        return exit;
    }
    /**
     * 订单号赋值去重
     */
    public void setCode(TecBomItem li,TecBomItem tecBomItem){
        //生产订单
        if(tecBomItem.getManufactureOrderCodeRemark()!=null){
            String man = tecBomItem.getManufactureOrderCodeRemark();
            String remark = li.getManufactureOrderCodeRemark();
            if (StrUtil.isBlank(man) && StrUtil.isNotBlank(remark)) {
                li.setMaterialSkuRemark(remark);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isBlank(remark)) {
                li.setManufactureOrderCodeRemark(man);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isNotBlank(remark)) {
                Boolean match = match(remark, man);
                if(!match){
                    String code = man + ";" + remark;
                    li.setManufactureOrderCodeRemark(code);
                }
            }
        }
        //采购订单
        if(tecBomItem.getPurchaseOrderCodeRemark()!=null){
            String man = tecBomItem.getPurchaseOrderCodeRemark();
            String remark = li.getPurchaseOrderCodeRemark();
            if (StrUtil.isBlank(man) && StrUtil.isNotBlank(remark)) {
                li.setMaterialSkuRemark(remark);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isBlank(remark)) {
                li.setPurchaseOrderCodeRemark(man);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isNotBlank(remark)) {
                Boolean match = match(remark, man);
                if(!match){
                    String code = man + ";" + remark;
                    li.setPurchaseOrderCodeRemark(code);
                }
            }
        }
        //销售订单
        if(tecBomItem.getSalesOrderCodeRemark()!=null){
            String man = tecBomItem.getSalesOrderCodeRemark();
            String remark = li.getSalesOrderCodeRemark();
            if (StrUtil.isBlank(man) && StrUtil.isNotBlank(remark)) {
                li.setMaterialSkuRemark(remark);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isBlank(remark)) {
                li.setSalesOrderCodeRemark(man);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isNotBlank(remark)) {
                Boolean match = match(remark, man);
                if(!match){
                    String code = man + ";" + remark;
                    li.setSalesOrderCodeRemark(code);
                }
            }
        }
        //来源数据明细行sid
        if(tecBomItem.getCommonItemSidRemark()!=null){
            String man = tecBomItem.getCommonItemSidRemark();
            String remark = li.getCommonItemSidRemark();
            if (StrUtil.isBlank(man) && StrUtil.isNotBlank(remark)) {
                li.setMaterialSkuRemark(remark);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isBlank(remark)) {
                li.setCommonItemSidRemark(man);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isNotBlank(remark)) {
                Boolean match = match(remark, man);
                if(!match){
                    String code = man + ";" + remark;
                    li.setCommonItemSidRemark(code);
                }
            }
            HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
            BigDecimal quantity = quantityMap.get(man);
            if(quantity==null){
                quantityMap.put(man,tecBomItem.getLossRequireQuantity());
            }else{
                quantityMap.put(man,quantity.add(tecBomItem.getLossRequireQuantity()));
            }
        }
    }

    /**
     * 订单号行sid处理
     */
    public void setCodeItem(TecBomItem li,TecBomItem tecBomItem){
        //来源数据明细行sid
        if(tecBomItem.getCommonItemSidRemark()!=null){
            String man = tecBomItem.getCommonItemSidRemark();
            String remark = li.getCommonItemSidRemark();
            if (StrUtil.isBlank(man) && StrUtil.isNotBlank(remark)) {
                li.setMaterialSkuRemark(remark);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isBlank(remark)) {
                li.setCommonItemSidRemark(man);
            }
            else if (StrUtil.isNotBlank(man) && StrUtil.isNotBlank(remark)) {
                Boolean match = match(remark, man);
                if(!match){
                    String code = man + ";" + remark;
                    li.setCommonItemSidRemark(code);
                }
            }
            HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
            BigDecimal quantity = quantityMap.get(man);
            if(quantity==null){
                quantityMap.put(man,tecBomItem.getLossRequireQuantity());
            }else{
                quantityMap.put(man,quantity.add(tecBomItem.getLossRequireQuantity()));
            }
        }
    }
    /**
     * 物料需求报表(拉链)
     */
    @Override
    public List<TecBomItem> getMaterialZipper(List<TecBomItem> requestList) {
        List<TecBomItem> list = new ArrayList<>();
        requestList.stream().forEach(item -> {
            List<TecBomHead> tecBomHeadList = tecBomHeadMapper.selectTecBomHeadList(new TecBomHead().setMaterialSid(item.getMaterialSid()).setSku1Sid(item.getSku1Sid()));
            if (CollectionUtils.isEmpty(tecBomHeadList)) {
                throw new BaseException("该商品没有创建BOM信息");
            }
            TecBomHead tecBomHead = tecBomHeadList.get(0);
            tecBomHead.setPurchaseType(item.getPurchaseType())
                    .setTouseProduceStage(item.getTouseProduceStage())
                    .setMaterialType(item.getMaterialType());
            if(item.getVendorSid()==null){
                tecBomHead.setVendorSid(null);
            }else{
                tecBomHead.setVendorSid(String.valueOf(item.getVendorSid()));
            }
            List<TecBomItem> tecBomItemList = tecBomItemMapper.getMaterialRequireList2(tecBomHead);
            //过滤 sku1为 null
            tecBomItemList=tecBomItemList.stream().filter(it->it.getBomMaterialSku1Sid()!=null).collect(Collectors.toList());
            tecBomItemList.forEach(li->{
                if(li.getQuantity()==null){
                    throw new BaseException("存在BOM用量未填写的明细行，无法测算物料需求");
                }
            });
            //赋值销售订单带过来的信息
            tecBomItemList.forEach(tecBomItem -> {
                tecBomItem.setSaleMaterialName(item.getSaleMaterialName());
                tecBomItem.setSaleSku1Name(item.getSaleSku1Name());
                tecBomItem.setSaleSku2Name(item.getSaleSku2Name());
                tecBomItem.setSaleMaterialCode(item.getSaleMaterialCode());
                tecBomItem.setSaleSku1Sid(item.getSku1Sid());
                tecBomItem.setSaleSku2Sid(item.getSku2Sid());
                tecBomItem.setSaleMaterialSid(item.getMaterialSid().toString());
                tecBomItem.setSku2Sid(item.getSku2Sid());
                //生产订单
                if(item.getManufactureOrderCode()!=null){
                    tecBomItem.setManufactureOrderCodeRemark(item.getManufactureOrderCode().toString());
                }
                //采购订单
                if(item.getPurchaseOrderCode()!=null){
                    tecBomItem.setPurchaseOrderCodeRemark(item.getPurchaseOrderCode().toString());
                }
                //销售订单
                if(item.getSalesOrderCode()!=null){
                    tecBomItem.setSalesOrderCodeRemark(item.getSalesOrderCode().toString());
                }
            });
            ArrayList<TecBomItem> temporList = new ArrayList<>();
            tecBomItemList.forEach(bomItem -> {
                //判断同种物料、同种sku1 合并 需求量值累加
                if (CollectionUtils.isNotEmpty(list)) {
                    Boolean exit = true;
                    for (TecBomItem li : list) {
                        if (li.getBomItemSid().equals(bomItem.getBomItemSid())
                                && li.getSaleSku2Name().equals(bomItem.getSaleSku2Name())) {
                            //计算重复物料的需求量
                            bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                            bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                            bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                            li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                            li.setRequireQuantity(li.getRequireQuantity().add(bomItem.getRequireQuantity()));
                            //订单号赋值
                            setCode(li, bomItem);
                            exit = false;
                            break;
                        }
                    }
                    if (exit) {
                        temporList.clear();
                        bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                        bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                        bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                        temporList.add(bomItem);
                    }
                } else {
                    bomItem.setLossInnerQuantity(bomItem.getQuantity().multiply(bomItem.getLossRate().add(new BigDecimal("1"))).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                    bomItem.setLossRequireQuantity(bomItem.getLossInnerQuantity().multiply(item.getQuantity()));
                    bomItem.setRequireQuantity(bomItem.getQuantity().multiply(item.getQuantity()).divide(new BigDecimal(bomItem.getUnitConversionRate()),4));
                    temporList.add(bomItem);
                }
                list.addAll(temporList);
                temporList.clear();
            });
        });
        //拉链标识
        list.forEach(o -> {
            if (ConstantsEms.ZIPPER_ZH.equals(o.getZipperFlag()) || ConstantsEms.ZIPPER_ZT.equals(o.getZipperFlag()) || ConstantsEms.ZIPPER_LP.equals(o.getZipperFlag())) {
                BasSku sku = zipperLengthMapper.getZipperSku2(o);
                if (sku!= null) {
                    o.setSku2Name(sku.getSkuName());
                    o.setBomMaterialSku2Sid(sku.getSkuSid());
                }
            }
        });
        //计算可用库存量
        list.forEach(bomItem -> {
            List<InvInventoryLocation> invInventoryLocations = new ArrayList<>();
            if (bomItem.getBomMaterialSku2Sid()==null) {
                List<InvInventoryLocation> invSku1 = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getMaterialSid, bomItem.getBomMaterialSid())
                        .eq(InvInventoryLocation::getSku1Sid, bomItem.getBomMaterialSku1Sid())
                );
                invInventoryLocations.addAll(invSku1);
            } else {
                List<InvInventoryLocation> invSku1AndSku2 = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getMaterialSid, bomItem.getBomMaterialSid())
                        .eq(InvInventoryLocation::getSku1Sid, bomItem.getBomMaterialSku1Sid())
                        .eq(InvInventoryLocation::getSku2Sid, bomItem.getBomMaterialSku2Sid())
                );
                invInventoryLocations.addAll(invSku1AndSku2);
            }
            //计算该物料所有仓库库存量信息
//            if (CollectionUtils.isNotEmpty(invInventoryLocations)) {
//                int sum = invInventoryLocations.stream().mapToInt(o -> o.getUnlimitedQuantity().intValue()).sum();
//                bomItem.setUnlimitedQuantity(sum);
//            } else {
//                bomItem.setUnlimitedQuantity(0);
//            }
        });
        //需求量计算取整
        list.forEach(h->{
            if(ConstantsEms.YES.equals(h.getIsInteger())){
                h.setRequireQuantity(h.getRequireQuantity().setScale(0,BigDecimal.ROUND_HALF_UP))
                        .setLossRequireQuantity(h.getLossRequireQuantity().setScale(0,BigDecimal.ROUND_HALF_UP));
                h.setRequireQuantityView(h.getRequireQuantity().toString())
                        .setLossRequireQuantityView(h.getLossRequireQuantity().toString());
            }else{
//                h.setRequireQuantity(h.getRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP))
//                        .setLossRequireQuantity(h.getLossRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
                DecimalFormat decimalFormat = new DecimalFormat("0.0000#");
                String requireQuantity = decimalFormat.format(h.getRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
                String lossRequireQuantity = decimalFormat.format(h.getLossRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
                h.setRequireQuantityView(requireQuantity)
                        .setLossRequireQuantityView(lossRequireQuantity);
            }
        });
        //按物料编码降序
        List<TecBomItem> descList = list.stream()
                .sorted(Comparator.comparing(TecBomItem::getSaleMaterialCode).reversed()
                        .thenComparing(TecBomItem::getMaterialCode,Comparator.reverseOrder())
                )
                .collect(Collectors.toList());
        return descList;
    }

    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;

    /**
     * 获取租户默认设置
     */
    @Override
    public SysDefaultSettingClient getClientSetting() {
        return defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
    }

    /**
     * 商品销售订单 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataSale(MultipartFile file){
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            int size = readAll.size();
            if(size<6){
                throw new BaseException("明细行不能为空，导入失败");
            }
            //销售订单-单据类型
            Map<String, String> saleDocumentMaps = conDocTypeSalesOrderMapper.getConDocTypeSalesOrderList().stream().collect(Collectors.toMap(ConDocTypeSalesOrder::getName, ConDocTypeSalesOrder::getCode, (key1, key2) -> key2));
            //销售订单-业务类型
            Map<String, String> saleTypeMaps = conBuTypeSalesOrderMapper.getConBuTypeSalesOrderList().stream().collect(Collectors.toMap(ConBuTypeSalesOrder::getName, ConBuTypeSalesOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            boolean is = sysDictDataService.deleteDictData("s_raw_material_mode");
            List<DictData> rawMaterialMode=sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String,String> rawMaterialModeMaps=rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售模式
            sysDictDataService.deleteDictData("s_price_type");
            List<DictData> priceType=sysDictDataService.selectDictData("s_price_type");
            Map<String,String> priceTypeMaps=priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售渠道
            Map<String,String> conSaleChannelMaps=conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName,(key1, key2)->key2));
            //物料类型
            Map<String,String> materailTypeMaps=conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            //销售组织
            Map<String,String> saleOrgMaps=conSaleOrgMapper.getConSaleOrgList().stream().collect(Collectors.toMap(ConSaleOrg::getCode, ConSaleOrg::getName,(key1, key2)->key2));
            //销售组
            Map<String,String> saleGroupMaps=conSaleGroupMapper.getConSaleGroupList().stream().collect(Collectors.toMap(ConSaleGroup::getCode, ConSaleGroup::getName,(key1, key2)->key2));

            SysDefaultSettingClient client = getClientSetting();

            ArrayList<SalSalesOrderItem> SalSalesOrderItems = new ArrayList<>();
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long basStorehouseSid=null;
                Long storehouseLocationSid=null;
                Long companySid=null;
                Long customerSid=null;
                Long productSeasonSid=null;
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                Long saleContractSid=null;
                String materialName=null;
                String materialCode=null;
                BigDecimal salePrice=null;
                String unitBase=null;
                String isReturn=null;
                String inOutStatus=null;
                String businessChannel=null;
                String materialType=null;
                BasMaterialBarcode basMaterialBarcode=null;
                String isReturnGoods=null;
                String saleMode=null;
                Date documnetDate=null;
                String valueSaleType=null;
                String valueSaleDocument=null;
                String valueRawMaterial=null;
                BigDecimal qutatiy=null;
                String adMode=null;
                Date contractDate=null;
                Date latestDemandDate=null;
                Date demandDate=null;
                String valuePriceType=null;
                String customerGroup=null;
                String department=null;
                Long skuGoup=null;
                int num=i+1;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        // throw new BaseException("第"+num+"行,客户简称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("客户简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        String cstomcerCode = objects.get(0).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, cstomcerCode));
                        if (basCustomer == null) {
                            // throw new BaseException("第"+num+"行,客户简称为" + cstomcerCode + "没有对应的客户，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("客户简称为" + cstomcerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(basCustomer.getStatus())){
                                customerSid = basCustomer.getCustomerSid();
                                customerGroup=basCustomer.getCustomerGroup();
                            }else{
                                // throw new BaseException("第"+num+"行,客户简称必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("客户简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        // throw new BaseException("第"+num+"行,单据类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        valueSaleDocument = saleDocumentMaps.get(objects.get(1).toString());
                        if(valueSaleDocument==null){
                            //throw new BaseException("第"+num+"行,单据类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            ConDocTypeSalesOrder conDocTypeSalesOrder = conDocTypeSalesOrderMapper.selectOne(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                                    .eq(ConDocTypeSalesOrder::getCode, valueSaleDocument)
                            );
                            if(!ConstantsEms.CHECK_STATUS.equals(conDocTypeSalesOrder.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conDocTypeSalesOrder.getStatus())){
                                // throw new BaseException("第"+num+"行,单据类型必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("单据类型必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            isReturnGoods = conDocTypeSalesOrder.getIsReturnGoods();
                            saleMode=conDocTypeSalesOrder.getSaleMode();
                        }
                    }

                    if(ConstantsEms.YES.equals(isReturnGoods)){
                        inOutStatus="WRK";
                        isReturn="Y";
                    }else{
                        inOutStatus="WCK";
                        isReturn="N";
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        // throw new BaseException("第"+num+"行,业务类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("业务类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConBuTypeSalesOrder conBuTypeSalesOrder = conBuTypeSalesOrderMapper.selectOne(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                                .eq(ConBuTypeSalesOrder::getName, objects.get(2).toString())
                        );
                        if(conBuTypeSalesOrder==null){
                            // throw new BaseException("第"+num+"行,业务类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(conBuTypeSalesOrder.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conBuTypeSalesOrder.getStatus())){
                                //  throw new BaseException("第"+num+"行,业务类型必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("业务类型必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                valueSaleType=conBuTypeSalesOrder.getCode();
                            }
                        }
                    }
                    if(valueSaleDocument!=null&&valueSaleType!=null){
                        ConDocBuTypeGroupSo conDocBuTypeGroupSo = conDocBuTypeGroupSoMapper.selectOne(new QueryWrapper<ConDocBuTypeGroupSo>().lambda()
                                .eq(ConDocBuTypeGroupSo::getDocTypeCode, valueSaleDocument)
                                .eq(ConDocBuTypeGroupSo::getBuTypeCode, valueSaleType)
                                .eq(ConDocBuTypeGroupSo::getHandleStatus,ConstantsEms.CHECK_STATUS)
                                .eq(ConDocBuTypeGroupSo::getStatus,ConstantsEms.ENABLE_STATUS)
                        );
                        if(conDocBuTypeGroupSo==null){
                            //  throw new BaseException("第"+num+"行,业务类型与单据类型对应关系不匹配，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型与单据类型对应关系不匹配，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        //throw new BaseException("第"+num+"行,公司编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("公司简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        String compamyCode = objects.get(3).toString();
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                                .lambda().eq(BasCompany::getShortName, compamyCode));
                        if (company == null) {
                            // throw new BaseException("第"+num+"行,公司编码为" + compamyCode + "没有对应的公司，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("公司简称为" + compamyCode + "没有对应的公司，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(company.getStatus())){
                                companySid = company.getCompanySid();
                            }else{
                                // throw new BaseException("第"+num+"行,公司编码必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("公司简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    String productSeasonName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                    if (ConstantsEms.YES.equals(client.getIsXiefuIndustry()) && StrUtil.isBlank(productSeasonName)) {
                        //  throw new BaseException("第"+num+"行,产品季，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("下单季，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else if (StrUtil.isNotBlank(productSeasonName)){
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                        if (productSeason == null) {
                            // throw new BaseException("第"+num+"行,产品季名称为" + productSeasonName + "没有对应的产品季，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("下单季名称为" + productSeasonName + "没有对应的下单季，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(productSeason.getStatus())){
                                productSeasonSid = productSeason.getProductSeasonSid();
                            }else{
                                // throw new BaseException("第"+num+"行,产品季编码必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("下单季名称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        //  throw new BaseException("第"+num+"行,销售员账号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售员账号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        try {
                            SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                                    .eq(SysUser::getUserName, objects.get(5).toString())
                                    .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getClientId()));
                            if(sysUser==null){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("没有账号为"+objects.get(5).toString()+"的销售员,导入失败");
                                msgList.add(errMsgResponse);
                            } else{
                                if(!ConstantsEms.SYS_COMMON_STATUS_Y.equals(sysUser.getStatus())){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("销售员账号必须是启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        } catch (TooManyResultsException e) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("销售员账号存在重复，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        //  throw new BaseException("第"+num+"行,单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                        if(!validDate){
                            // throw new BaseException("第"+num+"行,单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String documnet = objects.get(6).toString();
                            documnetDate = DateUtil.parse(documnet);
                        }
                    }
                    if (objects.get(7) == null || objects.get(7) == "") {
                        // throw new BaseException("第"+num+"行,客供料方式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("客供料方式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                        if(valueRawMaterial==null){
                            //throw new BaseException("第"+num+"行,客供料方式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("客供料方式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String value=valueRawMaterial;
                            if(CollectionUtil.isNotEmpty(rawMaterialMode)){
                                List<DictData> list = rawMaterialMode.stream()
                                        .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                        .collect(Collectors.toList());
                                if(CollectionUtil.isEmpty(list)){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("客供料方式配置错误，请联系管理员，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        //throw new BaseException("第"+num+"行,销售模式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售模式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                        if(valuePriceType==null){
                            // throw new BaseException("第"+num+"行,销售模式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("销售模式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(valuePriceType!=null){
                                String value=valuePriceType;
                                if(CollectionUtil.isNotEmpty(priceType)){
                                    List<DictData> list = priceType.stream()
                                            .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                            .collect(Collectors.toList());
                                    if(CollectionUtil.isEmpty(list)){
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("销售模式配置错误，请联系管理员，导入失败");
                                        msgList.add(errMsgResponse);
                                    }else{
                                        if(saleMode!=null&&valuePriceType!=null){
                                            if(!saleMode.equals(valuePriceType)){
                                                // throw new BaseException("第"+num+"行,销售模式与单据类型对应关系不匹配，导入失败");
                                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                                errMsgResponse.setItemNum(num);
                                                errMsgResponse.setMsg("销售模式与单据类型对应关系不匹配，导入失败");
                                                msgList.add(errMsgResponse);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String saleContractCode = null;
                    if (objects.get(9) == null || objects.get(9) == "") {
                        //throw new BaseException("第"+num+"行,销售合同号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售合同号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    else{
                        saleContractCode = objects.get(9).toString();
                        if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getSaleOrderContractEnterMode())) {
                            List<SalSaleContract> salSaleContract = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>()
                                    .lambda().eq(SalSaleContract::getSaleContractCode, saleContractCode));
                            if (CollectionUtil.isEmpty(salSaleContract)) {
                                //throw new BaseException("第"+num+"行,合同号校验不通过，导入失败！");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("合同号不存在，导入失败！");
                                msgList.add(errMsgResponse);
                            } else {
                                Long finalCustomerSid = customerSid;
                                Long finalCompanySid = companySid;
                                salSaleContract = salSaleContract.stream().filter(o->o.getCustomerSid().equals(finalCustomerSid)&&o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(salSaleContract)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("合同的“客户+公司”与订单的“客户+公司”不一致，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                else {
                                    saleContractSid = salSaleContract.get(0).getSaleContractSid();
                                    adMode=salSaleContract.get(0).getAdvanceSettleMode();
                                }
                            }
                        }
                    }
                    if(objects.get(10)==null||objects.get(10)==""){
                        // throw new BaseException("第"+num+"行,销售渠道编码不允许为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售渠道不允许为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if(objects.get(10)!=null&&objects.get(10)!=""){
                        String code = objects.get(10).toString();
                        ConSaleChannel channel = conSaleChannelMapper.selectOne(new QueryWrapper<ConSaleChannel>().lambda()
                                .eq(ConSaleChannel::getName, code)
                        );
                        if(channel==null){
                            // throw new BaseException("第"+num+"行,销售渠道编码为" + code + "没有对应的销售渠道，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("销售渠道为" + code + "没有对应的销售渠道，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(channel.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(channel.getStatus())){
                                //throw new BaseException("第"+num+"行,销售渠道必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("销售渠道必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                businessChannel=channel.getCode();
                            }
                        }
                    }
                    if(objects.get(11)!=null&&objects.get(11)!=""){
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName, objects.get(11).toString())
                        );
                        if(conMaterialType==null){
                            //  throw new BaseException("第"+num+"行,物料类型为" + code + "没有对应的物料类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料类型为" + objects.get(11).toString() + "没有对应的物料类型，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(conMaterialType.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conMaterialType.getStatus())){
                                // throw new BaseException("第"+num+"行,仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("物料类型是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                materialType=conMaterialType.getCode();
                            }
                        }
                    }
                    if(objects.get(13)!=null&&objects.get(13)!=""){
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(13).toString())
                        );
                        if (basStorehouse == null) {
                            //throw new BaseException("第"+num+"行,没有编码为" + objects.get(13).toString() + "的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有名称为" + objects.get(13).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                                // throw new BaseException("第"+num+"行,仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(14)!=null&&objects.get(14)!="") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(14).toString())
                        );
                        if (basStorehouseLocation == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(14).toString() + "的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg(objects.get(14).toString()+"下，没有名称为" + objects.get(14).toString() + "的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                                //  throw new BaseException("第"+num+"行,库位必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if(companySid!=null){
                        if(objects.get(12)!=null&&objects.get(12)!=""){
                            BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                    .eq(BasDepartment::getDepartmentName, objects.get(12).toString())
                                    .eq(BasDepartment::getCompanySid,companySid)
                            );
                            if (basDepartment == null) {
                                // throw new BaseException("第"+num+"行,没有编码为" + objects.get(12).toString() + "的部门，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg(objects.get(3).toString()+"下没有名称为" + objects.get(12).toString() + "的销售部门，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                if(!ConstantsEms.CHECK_STATUS.equals(basDepartment.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basDepartment.getStatus())){
                                    //  throw new BaseException("第"+num+"行,库位必须是确认且已启用状态，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("销售部门必须是确认且已启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    department=basDepartment.getDepartmentSid().toString();
                                }
                            }
                        }
                    }
                    if(objects.get(16)!=null&&objects.get(16)!=""){
                        boolean phone = JudgeFormat.isPhone(objects.get(16).toString());
                        if (!phone) {
                            // throw new BaseException("第"+num+"行,手机格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("收货人联系电话，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(18)!=null&&objects.get(18)!=""){
                        String name = saleOrgMaps.get(objects.get(18).toString());
                        if (name == null) {
                            //  throw new BaseException("第"+num+"行,没有编码为" + objects.get(18).toString() + "的销售组织，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(18).toString() + "的销售组织，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(19)!=null&&objects.get(19)!=""){
                        String name = saleGroupMaps.get(objects.get(19).toString());
                        if (name == null) {
                            //throw new BaseException("第"+num+"行,没有编码为" + objects.get(19).toString() + "的销售组，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(19).toString() + "的销售组，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if("YCX".equals(customerGroup)){
                        if(objects.get(21) == "" || objects.get(21) == null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("客户名称备注，不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }else{
                        if(customerSid!=null){
                            if(objects.get(21) != "" && objects.get(21) != null){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("客户为非一次性客户组时，客户名称备注必须为空");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }

                    // 纸质下单合同号
                    String paperSaleContractCode = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString().trim();
                    if (StrUtil.isNotBlank(paperSaleContractCode)) {
                        if (paperSaleContractCode.length() > 120) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("纸质下单合同号最多不能超过120位，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }

                    salSalesOrder.setCustomerSid(customerSid)
                            .setDocumentType(valueSaleDocument)
                            .setBusinessType(valueSaleType)
                            .setAdvanceSettleMode(adMode)
                            .setCompanySid(companySid)
                            .setIsConsignmentSettle("N")
                            .setInOutStockStatus(inOutStatus)
                            .setIsReturnGoods(isReturnGoods)
                            .setProductSeasonSid(productSeasonSid)
                            .setSalePerson((objects.get(5)==""||objects.get(5)==null)?null:objects.get(5).toString())
                            .setSaleMode(valuePriceType)
                            .setCurrency("CNY")
                            .setCurrencyUnit("YUAN")
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setSaleContractSid(saleContractSid)
                            .setSaleContractCode(saleContractCode)
                            .setBusinessChannel(businessChannel)
                            .setMaterialType((objects.get(11)==""||objects.get(11)==null)?null:materialType)
                            .setSaleDepartment((objects.get(12)==""||objects.get(12)==null)?null:department)
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(15)==""||objects.get(15)==null)?null:objects.get(15).toString())
                            .setConsigneePhone((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setConsigneeAddr((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setSaleOrg((objects.get(18) == "" || objects.get(18) == null) ? null : objects.get(18).toString())
                            .setSaleGroup((objects.get(19) == "" || objects.get(19) == null) ? null : objects.get(19).toString())
                            .setCustomerBusinessman((objects.get(20) == "" || objects.get(20) == null) ? null : objects.get(20).toString())
                            .setCustomerNameRemark((objects.get(21) == "" || objects.get(21) == null) ? null : objects.get(21).toString())
                            .setPaperSaleContractCode(paperSaleContractCode)
                            .setRemark((objects.get(23) == "" || objects.get(23) == null) ? null : objects.get(23).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    //  throw new BaseException("第"+num+"行,商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("商品编码不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial==null) {
                        //  throw new BaseException("第"+num+"行,没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(basMaterial.getStatus())){
                            materialSid=basMaterial.getMaterialSid();
                            unitBase=basMaterial.getUnitBase();
                            materialCode=basMaterial.getMaterialCode();
                            materialName=basMaterial.getMaterialName();
                            if(basMaterial.getSku2GroupSid()!=null){
                                skuGoup=basMaterial.getSku2GroupSid();
                            }
                            if (objects.get(1) == null || objects.get(1) == "") {
                                //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("SKU1名称不可为空，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                        .eq(BasSku::getSkuName, objects.get(1).toString())
                                        .eq(BasSku::getSkuType,basMaterial.getSku1Type())
                                );
                                if (basSku==null) {
                                    //throw new BaseException("第"+num+"行,SKU1名称为"+objects.get(1).toString()+",没有对应的SKU1名称，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU1名称为"+objects.get(1).toString()+",没有对应类型的SKU1名称，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    sku1Sid=basSku.getSkuSid();
                                }
                            }
                            if(materialSid!=null&&sku1Sid!=null){
                                BasMaterialSku skuName = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                        .eq(BasMaterialSku::getMaterialSid, materialSid)
                                        .eq(BasMaterialSku::getSkuSid,sku1Sid)
                                );
                                if(skuName==null){
                                    //throw new BaseException("第"+num+"行,SKU1名称必须是所填商品当中已启用的颜色，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU1名称必须是所填商品当中的颜色，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    if(!ConstantsEms.ENABLE_STATUS.equals(skuName.getStatus())){
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU1名称必须是所填商品当中已启用的颜色，导入失败");
                                        msgList.add(errMsgResponse);
                                    }
                                }
                            }
                            if (objects.get(2) == null || objects.get(2) == "") {
                                //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("SKU2名称不可为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if (objects.get(2) != null && objects.get(2) != "") {
                                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                        .eq(BasSku::getSkuName, objects.get(2).toString())
                                        .eq(BasSku::getSkuType,basMaterial.getSku2Type())
                                );
                                if (basSku2==null) {
                                    // throw new BaseException("第"+num+"行,SKU2名称为"+objects.get(2).toString()+",没有对应的SKU2名称，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU2名称为"+objects.get(2).toString()+",没有对应类型的SKU2名称，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    sku2Sid=basSku2.getSkuSid();
                                    BasMaterialSku basMaterialSkusSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                            .eq(BasMaterialSku::getMaterialSid, materialSid)
                                            .eq(BasMaterialSku::getSkuSid,sku2Sid)
                                    );
                                    if(basMaterialSkusSku==null){
                                        // throw new BaseException("第"+num+"行,SKU2名称必须是所填商品当中已启用的长度或尺码，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU2名称必须是所填商品当中的长度或尺码，导入失败");
                                        msgList.add(errMsgResponse);
                                    }else{
                                        if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialSkusSku.getStatus())){
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("SKU2名称必须是所填商品当中已启用的长度或尺码，导入失败");
                                            msgList.add(errMsgResponse);
                                        }
                                    }
                                }
                            }
                            if(sku1Sid!=null&&sku2Sid!=null){
                                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                        .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                        .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                        .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                                );
                            }else if(sku1Sid!=null&&sku2Sid==null){
                                basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                        .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                        .isNull(BasMaterialBarcode::getSku2Sid)
                                        .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                );
                            }
                            if(materialCode!=null){
                                if (basMaterialBarcode == null) {
                                    //throw new BaseException("第" + num + "行,不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    if(objects.get(2)==null||objects.get(2)==""){
                                        errMsgResponse.setMsg("不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码，导入失败");
                                    }else{
                                        errMsgResponse.setMsg("不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                    }
                                    msgList.add(errMsgResponse);
                                } else {
                                    if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())) {
                                        // throw new BaseException("第" + num + "行,商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        if(objects.get(2)==null||objects.get(2)==""){
                                            errMsgResponse.setMsg("商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码不是启用状态，导入失败");
                                        }else{
                                            errMsgResponse.setMsg("商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                        }
                                        msgList.add(errMsgResponse);
                                    }
                                    barcodeSid = basMaterialBarcode.getBarcodeSid();
                                }
                            }
                        }else{
                            //  throw new BaseException("第"+num+"行,商品编码必须是启用且已确认状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("商品编码必须是启用且已确认状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    //throw new BaseException("第"+num+"行,订单量 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("订单量 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,订单量格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("订单量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        qutatiy=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                        if(qutatiy.compareTo(BigDecimal.ZERO)==-1||qutatiy.compareTo(BigDecimal.ZERO)==0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("订单量不能小于等于0，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(4) != null && objects.get(4) != "") {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(4).toString());
                    if(!validDate){
                        // throw new BaseException("第"+num+"行,需求日期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("需求日期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        demandDate=DateUtil.parse(objects.get(4).toString());
                    }
                }
                if (objects.get(5) != null && objects.get(5) != "") {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(5).toString());
                    if(!validDate){
                        //  throw new BaseException("第"+num+"行,最晚需求日期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("最晚需求日期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        latestDemandDate=DateUtil.parse(objects.get(5).toString());
                    }
                }
                if (objects.get(6) == null || objects.get(6) == "") {
                    // throw new BaseException("第"+num+"行,合同交期 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("合同交期 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                    if(!validDate){
                        //throw new BaseException("第"+num+"行,合同交期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("合同交期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        contractDate=DateUtil.parse(objects.get(6).toString());
                    }
                }
                if((objects.get(7) != "" && objects.get(7) != null)){
                    if(!"是".equals(objects.get(7).toString())&&!"否".equals(objects.get(7).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否免费只能填是或者否，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                //默认获取通用税率
                ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                        .eq(ConTaxRate::getIsDefault, "Y")
                );
                SalSalesOrderItem salesOrderItem = new SalSalesOrderItem();
                salesOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setInOutStockStatus(inOutStatus)
                        .setTaxRate(taxRate.getTaxRateValue())
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(qutatiy)
                        .setSku2GroupSid(skuGoup)
                        .setSalePriceTax(salePrice)
                        .setMaterialName(materialName)
                        .setDemandDate((objects.get(4) == "" || objects.get(4) == null) ? null : demandDate)
                        .setLatestDemandDate((objects.get(5) == "" || objects.get(5) == null) ? null :latestDemandDate)
                        .setContractDate(contractDate)
                        .setFreeFlag((objects.get(7) == "" || objects.get(7) == null) ? null :("是".equals(objects.get(7).toString())?"Y":null))
                        .setRemark((objects.get(8) == "" || objects.get(8) == null) ? null : objects.get(8).toString());

                SalSalesOrderItems.add(salesOrderItem);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            setItemNum(SalSalesOrderItems);
            salSalesOrder.setSalSalesOrderItemList(SalSalesOrderItems);
            try{
                salSalesOrder.setImportType(BusinessType.IMPORT.getValue());
                insertSalSalesOrder(salSalesOrder);
            }catch (CustomException e){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg(e.getMessage());
                msgList.add(errMsgResponse);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }

        return AjaxResult.success(1);
    }

    /**
     * 商品销售退货订单 导入
     */
    @Override
    public int importDataSaleBACK(MultipartFile file){
        Long basStorehouseSid=null;
        Long storehouseLocationSid=null;
        Long companySid=null;
        Long customerSid=null;
        Long productSeasonSid=null;
        Long sku1Sid=null;
        Long sku2Sid=null;
        Long materialSid=null;
        Long barcodeSid=null;
        Long saleContractSid=null;
        String materialName=null;
        BigDecimal salePrice=null;
        String unitBase=null;
        BasMaterialBarcode basMaterialBarcode=null;
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //销售订单-单据类型
            Map<String, String> saleDocumentMaps = conDocTypeSalesOrderMapper.getConDocTypeSalesOrderList().stream().collect(Collectors.toMap(ConDocTypeSalesOrder::getName, ConDocTypeSalesOrder::getCode, (key1, key2) -> key2));
            //销售订单-业务类型
            Map<String, String> saleTypeMaps = conBuTypeSalesOrderMapper.getConBuTypeSalesOrderList().stream().collect(Collectors.toMap(ConBuTypeSalesOrder::getName, ConBuTypeSalesOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            List<DictData> rawMaterialMode=sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String,String> rawMaterialModeMaps=rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售模式
            List<DictData> priceType=sysDictDataService.selectDictData("s_price_type");
            Map<String,String> priceTypeMaps=priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售渠道
            Map<String,String> conSaleChannelMaps=conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName,(key1, key2)->key2));
            //物料类型
            Map<String,String> materailTypeMaps=conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            //销售组织
            Map<String,String> saleOrgMaps=conSaleOrgMapper.getConSaleOrgList().stream().collect(Collectors.toMap(ConSaleOrg::getCode, ConSaleOrg::getName,(key1, key2)->key2));
            //销售组
            Map<String,String> saleGroupMaps=conSaleGroupMapper.getConSaleGroupList().stream().collect(Collectors.toMap(ConSaleGroup::getCode, ConSaleGroup::getName,(key1, key2)->key2));
            ArrayList<SalSalesOrderItem> SalSalesOrderItems = new ArrayList<>();
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("客户编码，不能为空");
                    }
                    String cstomcerCode = objects.get(0).toString();
                    BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                            .lambda().eq(BasCustomer::getCustomerCode, cstomcerCode));
                    if (basCustomer == null) {
                        throw new BaseException("客户编码为" + cstomcerCode + "没有对应的客户");
                    } else {
                        customerSid = basCustomer.getCustomerSid();
                    }

                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("单据类型名称，不能为空");
                    }
                    String valueSaleDocument = saleDocumentMaps.get(objects.get(1).toString());
                    if(valueSaleDocument==null){
                        throw new BaseException("单据类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("业务类型名称，不能为空");
                    }
                    String valueSaleType = saleTypeMaps.get(objects.get(2).toString());
                    if(valueSaleType==null){
                        throw new BaseException("业务类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("公司编码，不能为空");
                    }
                    String compamyCode = objects.get(3).toString();
                    BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                            .lambda().eq(BasCompany::getCompanyCode, compamyCode));
                    if (company == null) {
                        throw new BaseException("公司编码为" + compamyCode + "没有对应的公司");
                    } else {
                        companySid = company.getCompanySid();
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("产品季，不能为空");
                    }
                    String productSeasonName = objects.get(4).toString();
                    BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                            .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                    if (productSeason == null) {
                        throw new BaseException("产品季名称为" + productSeasonName + "没有对应的产品季");
                    } else {
                        productSeasonSid = productSeason.getProductSeasonSid();
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("销售员账号，不能为空");
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        throw new BaseException("单据日期，不能为空");
                    }
                    String documnet = objects.get(6).toString();
                    Date documnetDate = DateUtil.parse(documnet);
                    if (objects.get(7) == null || objects.get(7) == "") {
                        throw new BaseException("客供料方式，不能为空");
                    }
                    String valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                    if(valueRawMaterial==null){
                        throw new BaseException("客供料方式配置错误，请联系管理员");
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        throw new BaseException("销售模式，不能为空");
                    }
                    String valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                    if(valuePriceType==null){
                        throw new BaseException("销售模式配置错误，请联系管理员");
                    }
                    if (objects.get(9) == null || objects.get(9) == "") {
                        throw new BaseException("销售合同号，不能为空");
                    }
                    String saleContractCode = objects.get(9).toString();
                    if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getSaleOrderContractEnterMode())) {
                        List<SalSaleContract> salSaleContract = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>()
                                .lambda().eq(SalSaleContract::getSaleContractCode, saleContractCode));
                        if (CollectionUtil.isEmpty(salSaleContract)) {
                            throw new BaseException("销售合同号为" + saleContractCode + "没有对应的销售合同号");
                        } else {
                            Long finalCustomerSid = customerSid;
                            Long finalCompanySid = companySid;
                            salSaleContract = salSaleContract.stream().filter(o->o.getCustomerSid().equals(finalCustomerSid)&&o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                            if (CollectionUtil.isEmpty(salSaleContract)) {
                                throw new BaseException("合同的“客户+公司”与订单的“客户+公司”不一致，导入失败");
                            }
                            else {
                                saleContractSid = salSaleContract.get(0).getSaleContractSid();
                            }
                        }
                    }
                    if(objects.get(12)!=null&&objects.get(12)!=""){
                        String code = objects.get(12).toString();
                        String name = conSaleChannelMaps.get(code);
                        if(name==null){
                            throw new BaseException("销售渠道编码为" + code + "没有对应的销售渠道");
                        }
                    }
                    if(objects.get(13)!=null&&objects.get(13)!=""){
                        String code = objects.get(13).toString();
                        String name = materailTypeMaps.get(code);
                        if(name==null){
                            throw new BaseException("物料类型为" + code + "没有对应的物料类型");
                        }
                    }
                    if(objects.get(10)!=null&&objects.get(10)!=""){
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseCode, objects.get(10).toString())
                        );
                        if (basStorehouse == null) {
                            throw new BaseException("没有编码为" + objects.get(10).toString() + "的仓库");
                        } else {
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(11)!=null&&objects.get(11)!="") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationCode, objects.get(11).toString())
                        );
                        if (basStorehouseLocation == null) {
                            throw new BaseException("没有编码为" + objects.get(11).toString() + "的库位");
                        } else {
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if(objects.get(15)!=null&&objects.get(15)!=""){
                        BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                .eq(BasDepartment::getDepartmentCode, objects.get(15).toString())
                        );
                        if (basDepartment == null) {
                            throw new BaseException("没有编码为" + objects.get(15).toString() + "的部门");
                        }
                    }
                    if(objects.get(18)!=null&&objects.get(18)!=""){
                        String name = saleOrgMaps.get(objects.get(18).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(18).toString() + "的销售组织");
                        }
                    }
                    if(objects.get(19)!=null&&objects.get(19)!=""){
                        String name = saleGroupMaps.get(objects.get(19).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(19).toString() + "的销售组");
                        }
                    }
                    salSalesOrder.setCustomerSid(customerSid)
                            .setDocumentType(valueSaleDocument)
                            .setBusinessType(valueSaleType)
                            .setCompanySid(companySid)
                            .setProductSeasonSid(productSeasonSid)
                            .setSalePerson(objects.get(5).toString())
                            .setSaleMode(valuePriceType)
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setSaleContractSid(saleContractSid)
                            .setSaleContractCode(saleContractCode)
                            .setBusinessChannel((objects.get(12)==""||objects.get(12)==null)?null:objects.get(12).toString())
                            .setMaterialType((objects.get(13)==""||objects.get(13)==null)?null:objects.get(13).toString())
                            .setSaleDepartment((objects.get(14)==""||objects.get(14)==null)?null:objects.get(14).toString())
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(15)==""||objects.get(15)==null)?null:objects.get(15).toString())
                            .setConsigneePhone((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setConsigneeAddr((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setSaleOrg((objects.get(18) == "" || objects.get(18) == null) ? null : objects.get(18).toString())
                            .setSaleGroup((objects.get(19) == "" || objects.get(19) == null) ? null : objects.get(19).toString())
                            .setRemark((objects.get(20) == "" || objects.get(20) == null) ? null : objects.get(20).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    throw new BaseException("商品编码不可为空");
                }
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                );
                if (basMaterial==null) {
                    throw new BaseException("没有编码为"+objects.get(0).toString()+"的商品");
                }else{
                    materialSid=basMaterial.getMaterialSid();
                    unitBase=basMaterial.getUnitBase();
                    materialName=basMaterial.getMaterialName();
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    throw new BaseException("SKU1编码不可为空");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(1).toString())
                );
                if (basSku==null) {
                    throw new BaseException("没有编码为"+objects.get(1).toString()+"的sku1");
                }else{
                    sku1Sid=basSku.getSkuSid();
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    throw new BaseException("SKU2编码不可为空");
                }
                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(2).toString())
                );
                if (basSku2==null) {
                    throw new BaseException("没有编码为"+objects.get(2).toString()+"的sku2");
                }else{
                    sku2Sid=basSku2.getSkuSid();
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    throw new BaseException("订单量 不可为空");
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    throw new BaseException("退货价(含税) 不可为空");
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    throw new BaseException("合同交期 不可为空");
                }
                if(sku1Sid!=null&&sku2Sid!=null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                }else if(sku1Sid!=null&&sku2Sid==null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                    );
                }
                if(basMaterialBarcode==null){
                    throw new BaseException("不存在物料编码为"+objects.get(0)+"sku1编码为"+objects.get(1)+""+"sku2编码为"+objects.get(2)+"的商品条码");
                }else{
                    barcodeSid=basMaterialBarcode.getBarcodeSid();
                }
                SalSalesOrderItem salesOrderItem = new SalSalesOrderItem();
                salesOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialSid(materialSid)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(BigDecimal.valueOf(Long.valueOf(objects.get(3).toString())))
                        .setSalePriceTax(BigDecimal.valueOf(Long.valueOf(objects.get(4).toString())))
                        .setMaterialName(materialName)
                        .setContractDate(DateUtil.parse(objects.get(5).toString()))
                        .setUnitBase(unitBase)
                        .setRemark((objects.get(6) == "" || objects.get(6) == null) ? null : objects.get(6).toString());

                SalSalesOrderItems.add(salesOrderItem);
            }
            salSalesOrder.setSalSalesOrderItemList(SalSalesOrderItems);
            salSalesOrder.setImportType(BusinessType.IMPORT.getValue());
            insertSalSalesOrder(salSalesOrder);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }

        return 1;
    }
    /*
     * 客户寄售结算单 导入
     */
    @Override
    public int importDataSaleCus(MultipartFile file){
        Long basStorehouseSid=null;
        Long storehouseLocationSid=null;
        Long companySid=null;
        Long customerSid=null;
        Long productSeasonSid=null;
        Long sku1Sid=null;
        Long sku2Sid=null;
        Long materialSid=null;
        Long barcodeSid=null;
        Long saleContractSid=null;
        String materialName=null;
        BigDecimal salePrice=null;
        String unitBase=null;
        BasMaterialBarcode basMaterialBarcode=null;
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //销售订单-单据类型
            Map<String, String> saleDocumentMaps = conDocTypeSalesOrderMapper.getConDocTypeSalesOrderList().stream().collect(Collectors.toMap(ConDocTypeSalesOrder::getName, ConDocTypeSalesOrder::getCode, (key1, key2) -> key2));
            //销售订单-业务类型
            Map<String, String> saleTypeMaps = conBuTypeSalesOrderMapper.getConBuTypeSalesOrderList().stream().collect(Collectors.toMap(ConBuTypeSalesOrder::getName, ConBuTypeSalesOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            List<DictData> rawMaterialMode=sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String,String> rawMaterialModeMaps=rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售模式
            List<DictData> priceType=sysDictDataService.selectDictData("s_price_type");
            Map<String,String> priceTypeMaps=priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售渠道
            Map<String,String> conSaleChannelMaps=conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName,(key1, key2)->key2));
            //物料类型
            Map<String,String> materailTypeMaps=conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            //销售组织
            Map<String,String> saleOrgMaps=conSaleOrgMapper.getConSaleOrgList().stream().collect(Collectors.toMap(ConSaleOrg::getCode, ConSaleOrg::getName,(key1, key2)->key2));
            //销售组
            Map<String,String> saleGroupMaps=conSaleGroupMapper.getConSaleGroupList().stream().collect(Collectors.toMap(ConSaleGroup::getCode, ConSaleGroup::getName,(key1, key2)->key2));
            ArrayList<SalSalesOrderItem> SalSalesOrderItems = new ArrayList<>();
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("客户编码，不能为空");
                    }
                    String cstomcerCode = objects.get(0).toString();
                    BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                            .lambda().eq(BasCustomer::getCustomerCode, cstomcerCode));
                    if (basCustomer == null) {
                        throw new BaseException("客户编码为" + cstomcerCode + "没有对应的客户");
                    } else {
                        customerSid = basCustomer.getCustomerSid();
                    }

                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("单据类型名称，不能为空");
                    }
                    String valueSaleDocument = saleDocumentMaps.get(objects.get(1).toString());
                    if(valueSaleDocument==null){
                        throw new BaseException("单据类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("业务类型名称，不能为空");
                    }
                    String valueSaleType = saleTypeMaps.get(objects.get(2).toString());
                    if(valueSaleType==null){
                        throw new BaseException("业务类型名称配置错误，请联系管理员");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("公司编码，不能为空");
                    }
                    String compamyCode = objects.get(3).toString();
                    BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                            .lambda().eq(BasCompany::getCompanyCode, compamyCode));
                    if (company == null) {
                        throw new BaseException("公司编码为" + compamyCode + "没有对应的公司");
                    } else {
                        companySid = company.getCompanySid();
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("产品季，不能为空");
                    }
                    String productSeasonName = objects.get(4).toString();
                    BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                            .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                    if (productSeason == null) {
                        throw new BaseException("产品季名称为" + productSeasonName + "没有对应的产品季");
                    } else {
                        productSeasonSid = productSeason.getProductSeasonSid();
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("销售员账号，不能为空");
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        throw new BaseException("单据日期，不能为空");
                    }
                    String documnet = objects.get(6).toString();
                    Date documnetDate = DateUtil.parse(documnet);
                    if (objects.get(7) == null || objects.get(7) == "") {
                        throw new BaseException("客供料方式，不能为空");
                    }
                    String valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                    if(valueRawMaterial==null){
                        throw new BaseException("客供料方式配置错误，请联系管理员");
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        throw new BaseException("销售模式，不能为空");
                    }
                    String valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                    if(valuePriceType==null){
                        throw new BaseException("销售模式配置错误，请联系管理员");
                    }
                    if (objects.get(9) == null || objects.get(9) == "") {
                        throw new BaseException("销售合同号，不能为空");
                    }
                    String saleContractCode = objects.get(9).toString();
                    if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getSaleOrderContractEnterMode())) {
                        List<SalSaleContract> salSaleContract = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>()
                                .lambda().eq(SalSaleContract::getSaleContractCode, saleContractCode));
                        if (CollectionUtil.isEmpty(salSaleContract)) {
                            throw new BaseException("销售合同号为" + saleContractCode + "没有对应的销售合同号");
                        } else {
                            Long finalCustomerSid = customerSid;
                            Long finalCompanySid = companySid;
                            salSaleContract = salSaleContract.stream().filter(o->o.getCustomerSid().equals(finalCustomerSid)&&o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                            if (CollectionUtil.isEmpty(salSaleContract)) {
                                throw new BaseException("合同的“客户+公司”与订单的“客户+公司”不一致，导入失败");
                            }
                            else {
                                saleContractSid = salSaleContract.get(0).getSaleContractSid();
                            }
                        }
                    }
                    if(objects.get(12)!=null&&objects.get(12)!=""){
                        String code = objects.get(12).toString();
                        String name = conSaleChannelMaps.get(code);
                        if(name==null){
                            throw new BaseException("销售渠道编码为" + code + "没有对应的销售渠道");
                        }
                    }
                    if(objects.get(16)!=null&&objects.get(16)!=""){
                        String code = objects.get(16).toString();
                        String name = materailTypeMaps.get(code);
                        if(name==null){
                            throw new BaseException("物料类型为" + code + "没有对应的物料类型");
                        }
                    }
                    if (objects.get(10) == null || objects.get(10) == "") {
                        throw new BaseException("仓库，不能为空");
                    }
                    if(objects.get(10)!=null&&objects.get(10)!=""){
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseCode, objects.get(10).toString())
                        );
                        if (basStorehouse == null) {
                            throw new BaseException("没有编码为" + objects.get(10).toString() + "的仓库");
                        } else {
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(11) == null || objects.get(11) == "") {
                        throw new BaseException("库位，不能为空");
                    }
                    if (objects.get(11)!=null&&objects.get(11)!="") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationCode, objects.get(11).toString())
                        );
                        if (basStorehouseLocation == null) {
                            throw new BaseException("没有编码为" + objects.get(11).toString() + "的库位");
                        } else {
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if(objects.get(13)!=null&&objects.get(13)!=""){
                        BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                .eq(BasDepartment::getDepartmentCode, objects.get(13).toString())
                        );
                        if (basDepartment == null) {
                            throw new BaseException("没有编码为" + objects.get(13).toString() + "的部门");
                        }
                    }
                    if(objects.get(14)!=null&&objects.get(14)!=""){
                        String name = saleOrgMaps.get(objects.get(14).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(14).toString() + "的销售组织");
                        }
                    }
                    if(objects.get(15)!=null&&objects.get(15)!=""){
                        String name = saleGroupMaps.get(objects.get(15).toString());
                        if (name == null) {
                            throw new BaseException("没有编码为" + objects.get(15).toString() + "的销售组");
                        }
                    }
                    salSalesOrder.setCustomerSid(customerSid)
                            .setDocumentType(valueSaleDocument)
                            .setBusinessType(valueSaleType)
                            .setCompanySid(companySid)
                            .setProductSeasonSid(productSeasonSid)
                            .setSalePerson(objects.get(5).toString())
                            .setSaleMode(valuePriceType)
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setSaleContractSid(saleContractSid)
                            .setSaleContractCode(saleContractCode)
                            .setBusinessChannel((objects.get(12)==""||objects.get(12)==null)?null:objects.get(12).toString())
                            .setMaterialType((objects.get(16)==""||objects.get(16)==null)?null:objects.get(16).toString())
                            .setSaleDepartment((objects.get(13)==""||objects.get(13)==null)?null:objects.get(13).toString())
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setSaleOrg((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString())
                            .setSaleGroup((objects.get(15) == "" || objects.get(15) == null) ? null : objects.get(15).toString())
                            .setRemark((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSpecialBusCategory(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    throw new BaseException("商品编码不可为空");
                }
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                );
                if (basMaterial==null) {
                    throw new BaseException("没有编码为"+objects.get(0).toString()+"的商品");
                }else{
                    materialSid=basMaterial.getMaterialSid();
                    unitBase=basMaterial.getUnitBase();
                    materialName=basMaterial.getMaterialName();
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    throw new BaseException("SKU1编码不可为空");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(1).toString())
                );
                if (basSku==null) {
                    throw new BaseException("没有编码为"+objects.get(1).toString()+"的sku1");
                }else{
                    sku1Sid=basSku.getSkuSid();
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    throw new BaseException("SKU2编码不可为空");
                }
                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuCode, objects.get(2).toString())
                );
                if (basSku2==null) {
                    throw new BaseException("没有编码为"+objects.get(2).toString()+"的sku2");
                }else{
                    sku2Sid=basSku2.getSkuSid();
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    throw new BaseException("结算量  不可为空");
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    throw new BaseException("结算价(含税) 不可为空");
                }
                if(sku1Sid!=null&&sku2Sid!=null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                }else if(sku1Sid!=null&&sku2Sid==null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                    );
                }
                if(basMaterialBarcode==null){
                    throw new BaseException("不存在物料编码为"+objects.get(0)+"sku1编码为"+objects.get(1)+""+"sku2编码为"+objects.get(2)+"的商品条码");
                }else{
                    barcodeSid=basMaterialBarcode.getBarcodeSid();
                }
                SalSalesOrderItem salesOrderItem = new SalSalesOrderItem();
                salesOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setMaterialSid(materialSid)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(BigDecimal.valueOf(Long.valueOf(objects.get(3).toString())))
                        .setSalePriceTax(BigDecimal.valueOf(Long.valueOf(objects.get(4).toString())))
                        .setMaterialName(materialName)
                        .setUnitBase(unitBase)
                        .setRemark((objects.get(5) == "" || objects.get(5) == null) ? null : objects.get(5).toString());

                SalSalesOrderItems.add(salesOrderItem);
            }
            salSalesOrder.setSalSalesOrderItemList(SalSalesOrderItems);
            salSalesOrder.setImportType(BusinessType.IMPORT.getValue());
            insertSalSalesOrder(salSalesOrder);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }

        return 1;
    }

    /**
     * 物料销售订单 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataSaleWl(MultipartFile file){
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //销售订单-单据类型
            Map<String, String> saleDocumentMaps = conDocTypeSalesOrderMapper.getConDocTypeSalesOrderList().stream().collect(Collectors.toMap(ConDocTypeSalesOrder::getName, ConDocTypeSalesOrder::getCode, (key1, key2) -> key2));
            //销售订单-业务类型
            Map<String, String> saleTypeMaps = conBuTypeSalesOrderMapper.getConBuTypeSalesOrderList().stream().collect(Collectors.toMap(ConBuTypeSalesOrder::getName, ConBuTypeSalesOrder::getCode, (key1, key2) -> key2));
            // 租户配置
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            //客供料 方式
            sysDictDataService.deleteDictData("s_raw_material_mode");
            List<DictData> rawMaterialMode=sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String,String> rawMaterialModeMaps=rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售模式
            sysDictDataService.deleteDictData("s_price_type");
            List<DictData> priceType=sysDictDataService.selectDictData("s_price_type");
            Map<String,String> priceTypeMaps=priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //销售渠道
            Map<String,String> conSaleChannelMaps=conSaleChannelMapper.getConSaleChannelList().stream().collect(Collectors.toMap(ConSaleChannel::getCode, ConSaleChannel::getName,(key1, key2)->key2));
            //物料类型
            Map<String,String> materailTypeMaps=conMaterialTypeMapper.getConMaterialTypeList().stream().collect(Collectors.toMap(ConMaterialType::getName, ConMaterialType::getCode,(key1, key2)->key2));
            //销售组织
            Map<String,String> saleOrgMaps=conSaleOrgMapper.getConSaleOrgList().stream().collect(Collectors.toMap(ConSaleOrg::getCode, ConSaleOrg::getName,(key1, key2)->key2));
            //销售组
            Map<String,String> saleGroupMaps=conSaleGroupMapper.getConSaleGroupList().stream().collect(Collectors.toMap(ConSaleGroup::getCode, ConSaleGroup::getName,(key1, key2)->key2));

            SysDefaultSettingClient client = getClientSetting();

            ArrayList<SalSalesOrderItem> SalSalesOrderItems = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            int size = readAll.size();
            if(size<6){
                throw new BaseException("明细行不能为空，导入失败");
            }
            for (int i = 0; i < readAll.size(); i++) {
                Long basStorehouseSid=null;
                Long storehouseLocationSid=null;
                Long companySid=null;
                Long customerSid=null;
                Long productSeasonSid=null;
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                Long saleContractSid=null;
                String materialName=null;
                String materialCode=null;
                String isReturn=null;
                String inOutStatus=null;
                String customerGroup=null;
                BigDecimal salePrice=null;
                String unitBase=null;
                String materialType=null;
                BasMaterialBarcode basMaterialBarcode=null;
                String isReturnGoods=null;
                String saleMode=null;
                Date documnetDate=null;
                String valueSaleType=null;
                String valueSaleDocument=null;
                String valueRawMaterial=null;
                String saleChannel=null;
                BigDecimal qutatiy=null;
                String adMode=null;
                Date contractDate=null;
                String department=null;
                Date latestDemandDate=null;
                Date demandDate=null;
                String valuePriceType=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                int num=i+1;
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        // throw new BaseException("第"+num+"行,客户简称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("客户简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        String cstomcerCode = objects.get(0).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, cstomcerCode));
                        if (basCustomer == null) {
                            // throw new BaseException("第"+num+"行,客户简称为" + cstomcerCode + "没有对应的客户，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("客户简称为" + cstomcerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(basCustomer.getStatus())){
                                customerSid = basCustomer.getCustomerSid();
                                customerGroup=basCustomer.getCustomerGroup();
                            }else{
                                // throw new BaseException("第"+num+"行,客户简称必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("客户简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        // throw new BaseException("第"+num+"行,单据类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        valueSaleDocument = saleDocumentMaps.get(objects.get(1).toString());
                        if(valueSaleDocument==null){
                            //throw new BaseException("第"+num+"行,单据类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            ConDocTypeSalesOrder conDocTypeSalesOrder = conDocTypeSalesOrderMapper.selectOne(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                                    .eq(ConDocTypeSalesOrder::getCode, valueSaleDocument)
                            );
                            if(!ConstantsEms.CHECK_STATUS.equals(conDocTypeSalesOrder.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conDocTypeSalesOrder.getStatus())){
                                // throw new BaseException("第"+num+"行,单据类型必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("单据类型必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            isReturnGoods = conDocTypeSalesOrder.getIsReturnGoods();
                            saleMode=conDocTypeSalesOrder.getSaleMode();
                        }
                    }

                    if(ConstantsEms.YES.equals(isReturnGoods)){
                        inOutStatus="WRK";
                        isReturn="Y";
                    }else{
                        inOutStatus="WCK";
                        isReturn="N";
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        // throw new BaseException("第"+num+"行,业务类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("业务类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConBuTypeSalesOrder conBuTypeSalesOrder = conBuTypeSalesOrderMapper.selectOne(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                                .eq(ConBuTypeSalesOrder::getName, objects.get(2).toString())
                        );
                        if(conBuTypeSalesOrder==null){
                            // throw new BaseException("第"+num+"行,业务类型名称配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型名称配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(conBuTypeSalesOrder.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conBuTypeSalesOrder.getStatus())){
                                //  throw new BaseException("第"+num+"行,业务类型必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("业务类型必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                valueSaleType=conBuTypeSalesOrder.getCode();
                            }
                        }
                    }
                    if(valueSaleDocument!=null&&valueSaleType!=null){
                        ConDocBuTypeGroupSo conDocBuTypeGroupSo = conDocBuTypeGroupSoMapper.selectOne(new QueryWrapper<ConDocBuTypeGroupSo>().lambda()
                                .eq(ConDocBuTypeGroupSo::getDocTypeCode, valueSaleDocument)
                                .eq(ConDocBuTypeGroupSo::getBuTypeCode, valueSaleType)
                                .eq(ConDocBuTypeGroupSo::getHandleStatus,ConstantsEms.CHECK_STATUS)
                                .eq(ConDocBuTypeGroupSo::getStatus,ConstantsEms.ENABLE_STATUS)
                        );
                        if(conDocBuTypeGroupSo==null){
                            //  throw new BaseException("第"+num+"行,业务类型与单据类型对应关系不匹配，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("业务类型与单据类型对应关系不匹配，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        //throw new BaseException("第"+num+"行,公司编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("公司简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        String compamyCode = objects.get(3).toString();
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>()
                                .lambda().eq(BasCompany::getShortName, compamyCode));
                        if (company == null) {
                            // throw new BaseException("第"+num+"行,公司编码为" + compamyCode + "没有对应的公司，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("公司简称为" + compamyCode + "没有对应的公司，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(company.getStatus())){
                                companySid = company.getCompanySid();
                            }else{
                                // throw new BaseException("第"+num+"行,公司编码必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("公司简称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    String productSeasonName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                    if (ConstantsEms.YES.equals(client.getIsXiefuIndustry()) && StrUtil.isBlank(productSeasonName)) {
                        //  throw new BaseException("第"+num+"行,产品季，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("下单季，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else if (StrUtil.isNotBlank(productSeasonName)) {
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName, productSeasonName));
                        if (productSeason == null) {
                            // throw new BaseException("第"+num+"行,产品季名称为" + productSeasonName + "没有对应的产品季，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("下单季名称为" + productSeasonName + "没有对应的下单季，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(productSeason.getStatus())){
                                productSeasonSid = productSeason.getProductSeasonSid();
                            }else{
                                // throw new BaseException("第"+num+"行,产品季编码必须是启用且已确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("下单季名称必须是启用且已确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        //  throw new BaseException("第"+num+"行,销售员账号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售员账号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        try {
                            SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                                    .eq(SysUser::getUserName, objects.get(5).toString())
                                    .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getClientId()));
                            if(sysUser==null){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("没有账号为"+objects.get(5).toString()+"的销售员,导入失败");
                                msgList.add(errMsgResponse);
                            } else{
                                if(!ConstantsEms.SYS_COMMON_STATUS_Y.equals(sysUser.getStatus())){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("销售员账号必须是启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        } catch (TooManyResultsException e) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("销售员账号存在重复，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        //  throw new BaseException("第"+num+"行,单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单据日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                        if(!validDate){
                            // throw new BaseException("第"+num+"行,单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("单据日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String documnet = objects.get(6).toString();
                            documnetDate = DateUtil.parse(documnet);
                        }
                    }
                    if (objects.get(7) == null || objects.get(7) == "") {
                        // throw new BaseException("第"+num+"行,客供料方式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("客供料方式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        valueRawMaterial = rawMaterialModeMaps.get(objects.get(7).toString());
                        if(valueRawMaterial==null){
                            //throw new BaseException("第"+num+"行,客供料方式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("客供料方式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(CollectionUtil.isNotEmpty(rawMaterialMode)){
                                String value=valueRawMaterial;
                                List<DictData> list = rawMaterialMode.stream()
                                        .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                        .collect(Collectors.toList());
                                if(CollectionUtil.isEmpty(list)){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("客供料方式配置错误，请联系管理员，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                    if (objects.get(8) == null || objects.get(8) == "") {
                        //throw new BaseException("第"+num+"行,销售模式，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售模式，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        valuePriceType = priceTypeMaps.get(objects.get(8).toString());
                        if(valuePriceType==null){
                            // throw new BaseException("第"+num+"行,销售模式配置错误，请联系管理员，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("销售模式配置错误，请联系管理员，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(valuePriceType!=null){
                                String value=valuePriceType;
                                if(CollectionUtil.isNotEmpty(priceType)){
                                    List<DictData> list = priceType.stream()
                                            .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                            .collect(Collectors.toList());
                                    if(CollectionUtil.isEmpty(list)){
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("销售模式配置错误，请联系管理员，导入失败");
                                        msgList.add(errMsgResponse);
                                    }else{
                                        if(saleMode!=null&&valuePriceType!=null){
                                            if(!saleMode.equals(valuePriceType)){
                                                // throw new BaseException("第"+num+"行,销售模式与单据类型对应关系不匹配，导入失败");
                                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                                errMsgResponse.setItemNum(num);
                                                errMsgResponse.setMsg("销售模式与单据类型对应关系不匹配，导入失败");
                                                msgList.add(errMsgResponse);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String saleContractCode = null;
                    if (objects.get(9) == null || objects.get(9) == "") {
                        //throw new BaseException("第"+num+"行,销售合同号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售合同号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        saleContractCode = objects.get(9).toString();
                        if (settingClient == null || !ConstantsOrder.CONTRACT_ENTER_MODE_SG.equals(settingClient.getSaleOrderContractEnterMode())) {
                            List<SalSaleContract> salSaleContract = salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>()
                                    .lambda().eq(SalSaleContract::getSaleContractCode, saleContractCode));
                            if (CollectionUtil.isEmpty(salSaleContract)) {
                                //throw new BaseException("第"+num+"行,合同号校验不通过，导入失败！");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("合同号不存在，导入失败！");
                                msgList.add(errMsgResponse);
                            } else {
                                Long finalCustomerSid = customerSid;
                                Long finalCompanySid = companySid;
                                salSaleContract = salSaleContract.stream().filter(o->o.getCustomerSid().equals(finalCustomerSid)&&o.getCompanySid().equals(finalCompanySid)).collect(Collectors.toList());
                                if (CollectionUtil.isEmpty(salSaleContract)) {
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("合同的“客户+公司”与订单的“客户+公司”不一致，导入失败！");
                                    msgList.add(errMsgResponse);
                                }
                                else {
                                    saleContractSid = salSaleContract.get(0).getSaleContractSid();
                                    adMode=salSaleContract.get(0).getAdvanceSettleMode();
                                }
                            }
                        }
                    }

                    if(objects.get(10)==null||objects.get(10)==""){
                        // throw new BaseException("第"+num+"行,销售渠道编码不允许为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("销售渠道不允许为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if(objects.get(10)!=null&&objects.get(10)!=""){
                        String code = objects.get(10).toString();
                        ConSaleChannel channel = conSaleChannelMapper.selectOne(new QueryWrapper<ConSaleChannel>().lambda()
                                .eq(ConSaleChannel::getName, code)
                        );
                        if(channel==null){
                            // throw new BaseException("第"+num+"行,销售渠道编码为" + code + "没有对应的销售渠道，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("销售渠道为" + code + "没有对应的销售渠道，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(channel.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(channel.getStatus())){
                                //throw new BaseException("第"+num+"行,销售渠道必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("销售渠道必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                saleChannel=channel.getCode();
                            }
                        }
                    }
                    if(objects.get(11)!=null&&objects.get(11)!=""){
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>().lambda()
                                .eq(ConMaterialType::getName, objects.get(11).toString())
                        );
                        if(conMaterialType==null){
                            //  throw new BaseException("第"+num+"行,物料类型为" + code + "没有对应的物料类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料类型为" + objects.get(11).toString() + "没有对应的物料类型，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(conMaterialType.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conMaterialType.getStatus())){
                                // throw new BaseException("第"+num+"行,仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("物料类型是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                materialType=conMaterialType.getCode();
                            }
                        }
                    }
                    if(objects.get(13)!=null&&objects.get(13)!=""){
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(13).toString())
                        );
                        if (basStorehouse == null) {
                            //throw new BaseException("第"+num+"行,没有编码为" + objects.get(13).toString() + "的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有名称为" + objects.get(13).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                                // throw new BaseException("第"+num+"行,仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(14)!=null&&objects.get(14)!="") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(14).toString())
                        );
                        if (basStorehouseLocation == null) {
                            // throw new BaseException("第"+num+"行,没有编码为" + objects.get(14).toString() + "的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg(objects.get(13).toString()+"下，没有名称为" + objects.get(14).toString() + "的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                                //  throw new BaseException("第"+num+"行,库位必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            storehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if(companySid!=null){
                        if(objects.get(12)!=null&&objects.get(12)!=""){
                            BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                    .eq(BasDepartment::getDepartmentName, objects.get(12).toString())
                                    .eq(BasDepartment::getCompanySid,companySid)
                            );
                            if (basDepartment == null) {
                                // throw new BaseException("第"+num+"行,没有编码为" + objects.get(12).toString() + "的部门，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg(objects.get(3).toString()+"下没有名称为" + objects.get(12).toString() + "的销售部门，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                if(!ConstantsEms.CHECK_STATUS.equals(basDepartment.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basDepartment.getStatus())){
                                    //  throw new BaseException("第"+num+"行,库位必须是确认且已启用状态，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("销售部门必须是确认且已启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    department=basDepartment.getDepartmentSid().toString();
                                }
                            }
                        }
                    }
                    if(objects.get(16)!=null&&objects.get(16)!=""){
                        boolean phone = JudgeFormat.isPhone(objects.get(16).toString());
                        if (!phone) {
                            // throw new BaseException("第"+num+"行,手机格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("收货人联系电话，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(18)!=null&&objects.get(18)!=""){
                        String name = saleOrgMaps.get(objects.get(18).toString());
                        if (name == null) {
                            //  throw new BaseException("第"+num+"行,没有编码为" + objects.get(18).toString() + "的销售组织，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(18).toString() + "的销售组织，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(19)!=null&&objects.get(19)!=""){
                        String name = saleGroupMaps.get(objects.get(19).toString());
                        if (name == null) {
                            //throw new BaseException("第"+num+"行,没有编码为" + objects.get(19).toString() + "的销售组，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("没有编码为" + objects.get(19).toString() + "的销售组，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if("YCX".equals(customerGroup)){
                        if(objects.get(21) == "" || objects.get(21) == null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("客户名称备注，不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }else{
                        if(customerSid!=null){
                            if(objects.get(21) != "" && objects.get(21) != null){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("客户为非一次性客户组时，客户名称备注必须为空");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }

                    // 纸质下单合同号
                    String paperSaleContractCode = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString().trim();
                    if (StrUtil.isNotBlank(paperSaleContractCode)) {
                        if (paperSaleContractCode.length() > 120) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("纸质下单合同号最多不能超过120位，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }

                    salSalesOrder.setCustomerSid(customerSid)
                            .setDocumentType(valueSaleDocument)
                            .setInOutStockStatus(inOutStatus)
                            .setBusinessType(valueSaleType)
                            .setIsReturnGoods(isReturnGoods)
                            .setIsConsignmentSettle("N")
                            .setCompanySid(companySid)
                            .setProductSeasonSid(productSeasonSid)
                            .setSalePerson((objects.get(5)==""||objects.get(5)==null)?null:objects.get(5).toString())
                            .setSaleMode(valuePriceType)
                            .setCurrency("CNY")
                            .setCurrencyUnit("YUAN")
                            .setDocumentDate(documnetDate)
                            .setRawMaterialMode(valueRawMaterial)
                            .setSaleContractSid(saleContractSid)
                            .setSaleContractCode(saleContractCode)
                            .setBusinessChannel(saleChannel)
                            .setMaterialType((objects.get(11)==""||objects.get(11)==null)?null:materialType)
                            .setSaleDepartment((objects.get(12)==""||objects.get(12)==null)?null:department)
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setConsignee((objects.get(15)==""||objects.get(15)==null)?null:objects.get(15).toString())
                            .setConsigneePhone((objects.get(16) == "" || objects.get(16) == null) ? null : objects.get(16).toString())
                            .setConsigneeAddr((objects.get(17) == "" || objects.get(17) == null) ? null : objects.get(17).toString())
                            .setSaleOrg((objects.get(18) == "" || objects.get(18) == null) ? null : objects.get(18).toString())
                            .setSaleGroup((objects.get(19) == "" || objects.get(19) == null) ? null : objects.get(19).toString())
                            .setCustomerNameRemark((objects.get(21) == "" || objects.get(21) == null) ? null : objects.get(21).toString())
                            .setCustomerBusinessman((objects.get(20) == "" || objects.get(20) == null) ? null : objects.get(20).toString())
                            .setPaperSaleContractCode(paperSaleContractCode)
                            .setRemark((objects.get(23) == "" || objects.get(23) == null) ? null : objects.get(23).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_WL);

                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    //  throw new BaseException("第"+num+"行,商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料编码不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial==null) {
                        //  throw new BaseException("第"+num+"行,没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为"+objects.get(0).toString()+"的物料，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())&&ConstantsEms.SAVA_STATUS.equals(basMaterial.getStatus())){
                            if(!ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("所填明细必须是物料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                materialSid=basMaterial.getMaterialSid();
                                unitBase=basMaterial.getUnitBase();
                                materialCode=basMaterial.getMaterialCode();
                                materialName=basMaterial.getMaterialName();
                                if (objects.get(1) == null || objects.get(1) == "") {
                                    //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("SKU1名称不可为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                            .eq(BasSku::getSkuName, objects.get(1).toString())
                                            .eq(BasSku::getSkuType,basMaterial.getSku1Type())
                                    );
                                    if (basSku==null) {
                                        //throw new BaseException("第"+num+"行,SKU1名称为"+objects.get(1).toString()+",没有对应的SKU1名称，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU1名称为"+objects.get(1).toString()+",没有对应类型的SKU1名称，导入失败");
                                        msgList.add(errMsgResponse);
                                    }else{
                                        sku1Sid=basSku.getSkuSid();
                                    }
                                }
                                if(materialSid!=null&&sku1Sid!=null){
                                    BasMaterialSku basMaterialSkus = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                            .eq(BasMaterialSku::getMaterialSid, materialSid)
                                            .eq(BasMaterialSku::getSkuSid,sku1Sid)
                                    );
                                    if(basMaterialSkus==null){
                                        //throw new BaseException("第"+num+"行,SKU1名称必须是所填商品当中已启用的颜色，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU1名称必须是所填物料当中的颜色，导入失败");
                                        msgList.add(errMsgResponse);
                                    }else{
                                        if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialSkus.getStatus())){
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("SKU1名称必须是所填物料当中已启用的颜色，导入失败");
                                            msgList.add(errMsgResponse);
                                        }
                                    }
                                }
                                if (objects.get(2) != null && objects.get(2) != "") {
                                    BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                            .eq(BasSku::getSkuName, objects.get(2).toString())
                                            .eq(BasSku::getSkuType,basMaterial.getSku2Type())
                                    );
                                    if (basSku2==null) {
                                        // throw new BaseException("第"+num+"行,SKU2名称为"+objects.get(2).toString()+",没有对应的SKU2名称，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        errMsgResponse.setMsg("SKU2名称为"+objects.get(2).toString()+",没有对应类型的SKU2名称，导入失败");
                                        msgList.add(errMsgResponse);
                                    }else{
                                        sku2Sid=basSku2.getSkuSid();
                                        BasMaterialSku basMaterialSkusSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                                                .eq(BasMaterialSku::getMaterialSid, materialSid)
                                                .eq(BasMaterialSku::getSkuSid,sku2Sid)
                                        );
                                        if(basMaterialSkusSku==null){
                                            // throw new BaseException("第"+num+"行,SKU2名称必须是所填商品当中已启用的长度或尺码，导入失败");
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            errMsgResponse.setMsg("SKU2名称必须是所填物料当中的长度或尺码，导入失败");
                                            msgList.add(errMsgResponse);
                                        }else{
                                            if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialSkusSku.getStatus())){
                                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                                errMsgResponse.setItemNum(num);
                                                errMsgResponse.setMsg("SKU2名称必须是所填物料当中已启用的长度或尺码，导入失败");
                                                msgList.add(errMsgResponse);
                                            }
                                        }
                                    }
                                }
                                if(sku1Sid!=null&&sku2Sid!=null){
                                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                                    );
                                }else if(sku1Sid!=null&&sku2Sid==null){
                                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                                            .isNull(BasMaterialBarcode::getSku2Sid)
                                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                    );
                                }
                                if(materialCode!=null){
                                    if (basMaterialBarcode == null) {
                                        //throw new BaseException("第" + num + "行,不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(num);
                                        if(objects.get(2)==null||objects.get(2)==""){
                                            errMsgResponse.setMsg("不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码，导入失败");
                                        }else{
                                            errMsgResponse.setMsg("不存在商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码，导入失败");
                                        }
                                        msgList.add(errMsgResponse);
                                    } else {
                                        if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())) {
                                            // throw new BaseException("第" + num + "行,商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                            errMsgResponse.setItemNum(num);
                                            if(objects.get(2)==null||objects.get(2)==""){
                                                errMsgResponse.setMsg("商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "的商品条码不是启用状态，导入失败");
                                            }else{
                                                errMsgResponse.setMsg("商品编码为" + objects.get(0) + ",sku1名称为" + objects.get(1) + "" + ",sku2名称为" + objects.get(2) + "的商品条码不是启用状态，导入失败");
                                            }
                                            msgList.add(errMsgResponse);
                                        }
                                        barcodeSid = basMaterialBarcode.getBarcodeSid();
                                    }
                                }
                            }
                        }else{
                            //  throw new BaseException("第"+num+"行,商品编码必须是启用且已确认状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料编码必须是启用且已确认状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    //throw new BaseException("第"+num+"行,订单量 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("订单量 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,订单量格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("订单量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        qutatiy=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                        if(qutatiy.compareTo(BigDecimal.ZERO)==-1||qutatiy.compareTo(BigDecimal.ZERO)==0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("订单量不能小于等于0，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(4) != null && objects.get(4) != "") {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(4).toString());
                    if(!validDate){
                        // throw new BaseException("第"+num+"行,需求日期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("需求日期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        demandDate=DateUtil.parse(objects.get(4).toString());
                    }
                }
                if (objects.get(5) != null && objects.get(5) != "") {
                    boolean validDate = JudgeFormat.isValidDate(objects.get(5).toString());
                    if(!validDate){
                        //  throw new BaseException("第"+num+"行,最晚需求日期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("最晚需求日期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        latestDemandDate=DateUtil.parse(objects.get(5).toString());
                    }
                }
                if (objects.get(6) == null || objects.get(6) == "") {
                    // throw new BaseException("第"+num+"行,合同交期 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("合同交期 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validDate = JudgeFormat.isValidDate(objects.get(6).toString());
                    if(!validDate){
                        //throw new BaseException("第"+num+"行,合同交期，格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("合同交期，格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        contractDate=DateUtil.parse(objects.get(6).toString());
                    }
                }
                if((objects.get(7) != "" && objects.get(7) != null)){
                    if(!"是".equals(objects.get(7).toString())&&!"否".equals(objects.get(7).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否免费只能填是或者否，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                //默认获取通用税率
                ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                        .eq(ConTaxRate::getIsDefault, "Y")
                );
                SalSalesOrderItem salesOrderItem = new SalSalesOrderItem();
                salesOrderItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setInOutStockStatus(inOutStatus)
                        .setMaterialSid(materialSid)
                        .setTaxRate(taxRate.getTaxRateValue())
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(qutatiy)
                        .setSalePriceTax(salePrice)
                        .setMaterialCode(materialCode)
                        .setMaterialName(materialName)
                        .setDemandDate((objects.get(4) == "" || objects.get(4) == null) ? null : demandDate)
                        .setLatestDemandDate((objects.get(5) == "" || objects.get(5) == null) ? null : latestDemandDate)
                        .setContractDate(contractDate)
                        .setFreeFlag((objects.get(7) == "" || objects.get(7) == null) ? null : ("是".equals(objects.get(7).toString())?"Y":null))
                        .setRemark((objects.get(8) == "" || objects.get(8) == null) ? null : objects.get(8).toString());
//                        .setUnitBase(unitBase);

                SalSalesOrderItems.add(salesOrderItem);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            setItemNum(SalSalesOrderItems);
            salSalesOrder.setSalSalesOrderItemList(SalSalesOrderItems);
            try{
                salSalesOrder.setImportType(BusinessType.IMPORT.getValue());
                insertSalSalesOrder(salSalesOrder);
            }catch (CustomException e){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg(e.getMessage());
                msgList.add(errMsgResponse);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }

        return AjaxResult.success(1);
    }

    /*
     * 商品 导入-物料需求测算报表
     */
    @Override
    public List<BasMaterialImResponse> importDataMaterial(MultipartFile file){
        List<BasMaterialImResponse> basMaterialList=new ArrayList<>();
        Long sku1Sid=null;
        Long sku2Sid=null;
        String sku1Name=null;
        String sku2Name=null;
        Long barcodeSid=null;
        String barcode=null;
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            for (int i = 0; i < readAll.size(); i++) {
                if(i<2){
                    //前两行跳过
                    continue;
                }
                int num=i+1;
                List<Object> objects=readAll.get(i);
                copy( objects, readAll);
                if(objects.get(0)==null||objects.get(0)==""){
                    throw new BaseException("第"+num+"行,商品编码不可为空，导入失败");
                }
                BasMaterial basMater= new BasMaterial();
                List<BasMaterial> list = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString()));
                basMater.setMaterialCode(objects.get(0).toString());
                List<BasMaterial> basMaterials = basMaterialMapper.selectBasMaterialList(basMater);
                if(CollectionUtils.isEmpty(list)){
                    throw new BaseException("第"+num+"行,不存在商品编码为"+objects.get(0)+"的商品，导入失败");
                }
                BasMaterial material = basMaterials.get(0);
                if(!ConstantsEms.ENABLE_STATUS.equals(material.getStatus())||!ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus())){
                    throw new BaseException("第"+num+"行,商品必须是已确认且启用状态，导入失败");
                }
                if(objects.get(1)==null||objects.get(1)==""){
                    throw new BaseException("第"+num+"行,sku1名称不可为空，导入失败");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuName, objects.get(1).toString())
                );
                if (basSku==null) {
                    throw new BaseException("第"+num+"行,没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                }else{
                    if(ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus())){
                        sku1Name=basSku.getSkuName();
                        sku1Sid=basSku.getSkuSid();
                    }else{
                        throw new BaseException("第"+num+"行,sku1必须是启用状态，导入失败");
                    }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    throw new BaseException("第" + num + "行,sku2名称不可为空，导入失败");
                }
                BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuName, objects.get(2).toString())
                );
                if (basSku2 == null) {
                    throw new BaseException("第" + num + "行,没有名称为" + objects.get(2).toString() + "的sku2，导入失败");
                } else {
                    if (ConstantsEms.ENABLE_STATUS.equals(basSku2.getStatus())) {
                        sku2Name = basSku2.getSkuName();
                        sku2Sid = basSku2.getSkuSid();
                    } else {
                        throw new BaseException("第" + num + "行,sku2必须是启用状态，导入失败");
                    }
                }
                BasMaterialBarcode basMaterialBarcode = new BasMaterialBarcode();
                if(sku1Sid!=null&&sku2Sid!=null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, basMaterials.get(0).getMaterialSid())
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                }else if(sku1Sid!=null&&sku2Sid==null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, basMaterials.get(0).getMaterialSid())
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                    );
                }
                if(basMaterialBarcode==null){
                    throw new BaseException("第"+num+"行没有对应的商品条码存在，导入失败");
                }else{
                    if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())){
                        throw new BaseException("第"+num+"行对应的商品条码必须时已启用状态，导入失败");
                    }
                    barcodeSid=basMaterialBarcode.getBarcodeSid();
                    barcode=basMaterialBarcode.getBarcode();
                }
                if(objects.get(3)==null||objects.get(3)==""){
                    throw new BaseException("第"+num+"行,数量不可为空，导入失败");
                }
                boolean validDouble = JudgeFormat.isValidInt(objects.get(3).toString());
                if(!validDouble){
                    throw new BaseException("第"+num+"行,数量格式错误，导入失败");
                }
                BasMaterialImResponse basMaterialImResponse = new BasMaterialImResponse();
                BeanCopyUtils.copyProperties(material,basMaterialImResponse);
                basMaterialImResponse.setSku1Name(sku1Name)
                        .setSku1Sid(sku1Sid)
                        .setSku2Name(sku2Name)
                        .setSku2Sid(sku2Sid)
                        .setQuantity(BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())))
                        .setBarcode(barcode);
                basMaterialList.add(basMaterialImResponse);
            }
            return basMaterialList;
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
    }

    //填充-主表
    public void copy(List<Object> objects,List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
    //填充-明细表
    public void copyItem(List<Object> objects,List<List<Object>> readAll){
        //获取第三行的列数
        int size = readAll.get(3).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }

    //物料采购订单明细导出
    @Override
    public void exportWl(HttpServletResponse response, Long[] sids){
//        Long[] a ={1458705346865446913L};
//        sids=a;
        for (int m = 0; m < sids.length; m++) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("物料销售订单明细");
                SalSalesOrder salSalesOrder = selectSalSalesOrderById(sids[m]);
                List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrder.getSalSalesOrderItemList();
                String isViewPrice = ApiThreadLocalUtil.get().getSysUser().getIsViewPrice();
                salSalesOrderItemList.forEach(li->{
                    if(!ConstantsEms.YES.equals(isViewPrice)){
                        li.setSalePriceTax(null)
                                .setSalePrice(null)
                                .setPriceTax(null)
                                .setPrice(null);

                    }
                });
                sheet.setDefaultColumnWidth(18);
                //甲供料方式
                List<DictData> raw=sysDictDataService.selectDictData("s_raw_material_mode");
                Map<String,String> rawMaps=raw.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
                //采购模式
                List<DictData> mode=sysDictDataService.selectDictData("s_price_type");
                Map<String,String> modeMaps=mode.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
                String[] titles = {"销售订单号", "客户", "单据类型", "业务类型", "公司", "产品季", "销售员", "单据日期", "客供料方式", "销售模式", "销售合同号", "销售渠道", "物料类型", "销售部门",
                        "仓库", "库位", "收货人", "收货人联系电话", "收货地址", "销售组织", "销售组", "客方跟单员", "纸质下单合同号", "备注"};
                //第一行数据
                Row rowBOMHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleLime(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowBOMHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                //第二行数据
                Row rowBOM = sheet.createRow(1);
                //销售订单号
                Cell cell0 = rowBOM.createCell(0);
                cell0.setCellValue(salSalesOrder.getSalesOrderCode());
                cell0.setCellStyle(defaultCellStyle);
                //客户
                Cell cell1 = rowBOM.createCell(1);
                cell1.setCellValue(salSalesOrder.getCustomerName());
                cell1.setCellStyle(defaultCellStyle);
                //单据类型
                Cell cell2 = rowBOM.createCell(2);
                cell2.setCellValue(salSalesOrder.getDocumentTypeName());
                cell2.setCellStyle(defaultCellStyle);
                //业务类型
                Cell cell3 = rowBOM.createCell(3);
                cell3.setCellValue(salSalesOrder.getBusinessTypeName());
                cell3.setCellStyle(defaultCellStyle);
                //公司
                Cell cell4 = rowBOM.createCell(4);
                cell4.setCellValue(salSalesOrder.getCompanyName());
                cell4.setCellStyle(defaultCellStyle);
                //产品季
                Cell cell5 = rowBOM.createCell(5);
                cell5.setCellValue(salSalesOrder.getProductSeasonName());
                cell5.setCellStyle(defaultCellStyle);
                //销售员
                Cell cell6 = rowBOM.createCell(6);
                cell6.setCellValue(salSalesOrder.getNickName());
                cell6.setCellStyle(defaultCellStyle);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //单据日期
                Cell cell7 = rowBOM.createCell(7);
                cell7.setCellValue(sdf.format(salSalesOrder.getDocumentDate()));
                cell7.setCellStyle(defaultCellStyle);
                //客供料
                Cell cell8 = rowBOM.createCell(8);
                cell8.setCellValue(salSalesOrder.getRawMaterialMode()==null?null:rawMaps.get(salSalesOrder.getRawMaterialMode().toString()));
                cell8.setCellStyle(defaultCellStyle);
                //销售模式
                Cell cell9 = rowBOM.createCell(9);
                cell9.setCellValue(salSalesOrder.getSaleMode()==null?null:modeMaps.get(salSalesOrder.getSaleMode().toString()));
                cell9.setCellStyle(defaultCellStyle);
                //销售合同号
                Cell cell10A = rowBOM.createCell(10);
                cell10A.setCellValue(salSalesOrder.getSaleContractCode());
                cell10A.setCellStyle(defaultCellStyle);
                //销售渠道
                Cell cell11A = rowBOM.createCell(11);
                cell11A.setCellValue(salSalesOrder.getBusinessChannelName());
                cell11A.setCellStyle(defaultCellStyle);
                //物料类型
                Cell cell12A = rowBOM.createCell(12);
                cell12A.setCellValue(salSalesOrder.getMaterialTypeName());
                cell12A.setCellStyle(defaultCellStyle);
                //销售部门
                Cell cell13A = rowBOM.createCell(13);
                cell13A.setCellValue(salSalesOrder.getDepartmentName());
                cell13A.setCellStyle(defaultCellStyle);
                //"仓库"
                Cell cell14A = rowBOM.createCell(14);
                cell14A.setCellValue(salSalesOrder.getStorehouseName());
                cell14A.setCellStyle(defaultCellStyle);
                //"库位"
                Cell cell15A = rowBOM.createCell(15);
                cell15A.setCellValue(salSalesOrder.getLocationName());
                cell15A.setCellStyle(defaultCellStyle);
                //"收货人"
                Cell cell16A = rowBOM.createCell(16);
                cell16A.setCellValue(salSalesOrder.getConsignee());
                cell16A.setCellStyle(defaultCellStyle);
                //"收货人联系电话"
                Cell cell17A = rowBOM.createCell(17);
                cell17A.setCellValue(salSalesOrder.getConsigneePhone());
                cell17A.setCellStyle(defaultCellStyle);
                //,"收货地址"
                Cell cell18A = rowBOM.createCell(18);
                cell18A.setCellValue(salSalesOrder.getConsigneeAddr());
                cell18A.setCellStyle(defaultCellStyle);
                //"销售组织",
                Cell cell19A = rowBOM.createCell(19);
                cell19A.setCellValue(salSalesOrder.getSaleOrgName());
                cell19A.setCellStyle(defaultCellStyle);
                // "销售组"
                Cell cell20A = rowBOM.createCell(20);
                cell20A.setCellValue(salSalesOrder.getSaleGroupName());
                cell20A.setCellStyle(defaultCellStyle);
                // " 客方跟单员",
                Cell cell21A = rowBOM.createCell(21);
                cell21A.setCellValue(salSalesOrder.getCustomerBusinessman());
                cell21A.setCellStyle(defaultCellStyle);
                // 纸质下单合同号
                Cell cell22A = rowBOM.createCell(22);
                cell22A.setCellValue(salSalesOrder.getPaperSaleContractCode());
                cell22A.setCellStyle(defaultCellStyle);
                // "备注"
                Cell cell23A = rowBOM.createCell(23);
                cell23A.setCellValue(salSalesOrder.getRemark());
                cell23A.setCellStyle(defaultCellStyle);
                //第三行数据
                Row rowBomItm = sheet.createRow(2);
                String[] titleItem={"物料编码","物料名称","SKU1名称","SKU2名称","订单量","基本计量单位","需求日期","最晚需求日期","合同交期","销售价(含税)","销售价(不含税)","金额(含税)","金额(不含税)","税率","备注"};
                for (int i=0;i<titleItem.length;i++) {
                    Cell cell = rowBomItm.createCell(i);
                    cell.setCellValue(titleItem[i]);
                    cell.setCellStyle(cellStyle);
                }
                //   数据部分
                for (int i=0;i<salSalesOrderItemList.size();i++) {
                    Row row = sheet.createRow(i+3);
                    //物料编码
                    Cell cell01 = row.createCell(0);
                    cell01.setCellValue(salSalesOrderItemList.get(i).getMaterialCode());
                    cell01.setCellStyle(defaultCellStyle);
                    //物料名称
                    Cell cell02 = row.createCell(1);
                    cell02.setCellValue(salSalesOrderItemList.get(i).getMaterialName());
                    cell02.setCellStyle(defaultCellStyle);
                    //SKU1名称
                    Cell cell03 = row.createCell(2);
                    cell03.setCellValue(salSalesOrderItemList.get(i).getSku1Name());
                    cell03.setCellStyle(defaultCellStyle);
                    //SKU2名称
                    Cell cell05 = row.createCell(3);
                    cell05.setCellValue(salSalesOrderItemList.get(i).getSku2Name());
                    cell05.setCellStyle(defaultCellStyle);
                    //订单量
                    Cell cell06 = row.createCell(4);
                    cell06.setCellValue(salSalesOrderItemList.get(i).getQuantity()==null?null:removeZero(salSalesOrderItemList.get(i).getQuantity().toString()));
                    cell06.setCellStyle(defaultCellStyle);
                    //基本计量单位
                    Cell cell07 = row.createCell(5);
                    cell07.setCellValue(salSalesOrderItemList.get(i).getUnitBaseName());
                    cell07.setCellStyle(defaultCellStyle);
                    // 需求日期
                    Cell cell08 = row.createCell(6);
                    cell08.setCellValue(salSalesOrderItemList.get(i).getDemandDate()==null?null:sdf.format(salSalesOrderItemList.get(i).getDemandDate()));
                    cell08.setCellStyle(defaultCellStyle);
                    //最晚需求日期
                    Cell cell09 = row.createCell(7);
                    cell09.setCellValue(salSalesOrderItemList.get(i).getLatestDemandDate()==null?null:sdf.format(salSalesOrderItemList.get(i).getLatestDemandDate()));
                    cell09.setCellStyle(defaultCellStyle);
                    //合同交期
                    Cell cell10 = row.createCell(8);
                    cell10.setCellValue(salSalesOrderItemList.get(i).getContractDate()==null?null:sdf.format(salSalesOrderItemList.get(i).getContractDate()));
                    cell10.setCellStyle(defaultCellStyle);
                    //销售价(含税)
                    Cell cell11 = row.createCell(9);
                    cell11.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().toString()));
                    cell11.setCellStyle(defaultCellStyle);
                    //销售价(不含税)
                    Cell cell12 = row.createCell(10);
                    cell12.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().divide(BigDecimal.ONE.add(salSalesOrderItemList.get(i).getTaxRate()),BigDecimal.ROUND_HALF_UP,4).toString()));
                    cell12.setCellStyle(defaultCellStyle);
                    //金额(含税)
                    Cell cell13 = row.createCell(11);
                    cell13.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().multiply(salSalesOrderItemList.get(i).getQuantity()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    cell13.setCellStyle(defaultCellStyle);
                    //金额(不含税)
                    Cell cell14 = row.createCell(12);
                    cell14.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().divide(BigDecimal.ONE.add(salSalesOrderItemList.get(i).getTaxRate()),BigDecimal.ROUND_HALF_UP,6).multiply(salSalesOrderItemList.get(i).getQuantity()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    cell14.setCellStyle(defaultCellStyle);
                    //税率
                    Cell cell15 = row.createCell(13);
                    cell15.setCellValue(salSalesOrderItemList.get(i).getTaxRate()==null?null:removeZero(salSalesOrderItemList.get(i).getTaxRate().toString()));
                    cell15.setCellStyle(defaultCellStyle);
                    //备注
                    Cell cell16 = row.createCell(14);
                    cell16.setCellValue(salSalesOrderItemList.get(i).getRemark());
                    cell16.setCellStyle(defaultCellStyle);
                }
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                workbook.write(response.getOutputStream());
            }catch (Exception e){
                throw new CustomException("导出失败");
            }}
    }

    //商品采购订单明细导出
    @Override
    public void exportGood(HttpServletResponse response, Long[] sids){
//        Long[] a ={1458705346865446913L};
//        sids=a;
        for (int m = 0; m < sids.length; m++) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("商品销售订单明细");
                SalSalesOrder salSalesOrder = selectSalSalesOrderById(sids[m]);
                List<SalSalesOrderItem> salSalesOrderItemList = salSalesOrder.getSalSalesOrderItemList();
                String isViewPrice = ApiThreadLocalUtil.get().getSysUser().getIsViewPrice();
                salSalesOrderItemList.forEach(li->{
                    if(!ConstantsEms.YES.equals(isViewPrice)){
                        li.setSalePriceTax(null)
                                .setSalePrice(null)
                                .setPriceTax(null)
                                .setPrice(null);

                    }
                });
                sheet.setDefaultColumnWidth(18);
                //甲供料方式
                List<DictData> raw=sysDictDataService.selectDictData("s_raw_material_mode");
                Map<String,String> rawMaps=raw.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
                //采购模式
                List<DictData> mode=sysDictDataService.selectDictData("s_price_type");
                Map<String,String> modeMaps=mode.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
                String[] titles = {"销售订单号", "客户", "单据类型", "业务类型", "公司", "产品季", "销售员", "单据日期", "客供料方式", "销售模式", "销售合同号", "销售渠道", "物料类型",
                        "销售部门", "仓库", "库位", "收货人", "收货人联系电话", "收货地址", "销售组织", "销售组", "客方跟单员", "纸质下单合同号", "备注"};
                //第一行数据
                Row rowBOMHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleLime(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowBOMHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                //第二行数据
                Row rowBOM = sheet.createRow(1);
                //销售订单号
                Cell cell0 = rowBOM.createCell(0);
                cell0.setCellValue(salSalesOrder.getSalesOrderCode());
                cell0.setCellStyle(defaultCellStyle);
                //客户
                Cell cell1 = rowBOM.createCell(1);
                cell1.setCellValue(salSalesOrder.getCustomerName());
                cell1.setCellStyle(defaultCellStyle);
                //单据类型
                Cell cell2 = rowBOM.createCell(2);
                cell2.setCellValue(salSalesOrder.getDocumentTypeName());
                cell2.setCellStyle(defaultCellStyle);
                //业务类型
                Cell cell3 = rowBOM.createCell(3);
                cell3.setCellValue(salSalesOrder.getBusinessTypeName());
                cell3.setCellStyle(defaultCellStyle);
                //公司
                Cell cell4 = rowBOM.createCell(4);
                cell4.setCellValue(salSalesOrder.getCompanyName());
                cell4.setCellStyle(defaultCellStyle);
                //产品季
                Cell cell5 = rowBOM.createCell(5);
                cell5.setCellValue(salSalesOrder.getProductSeasonName());
                cell5.setCellStyle(defaultCellStyle);
                //销售员
                Cell cell6 = rowBOM.createCell(6);
                cell6.setCellValue(salSalesOrder.getNickName());
                cell6.setCellStyle(defaultCellStyle);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //单据日期
                Cell cell7 = rowBOM.createCell(7);
                cell7.setCellValue(sdf.format(salSalesOrder.getDocumentDate()));
                cell7.setCellStyle(defaultCellStyle);
                //客供料
                Cell cell8 = rowBOM.createCell(8);
                cell8.setCellValue(salSalesOrder.getRawMaterialMode()==null?null:rawMaps.get(salSalesOrder.getRawMaterialMode().toString()));
                cell8.setCellStyle(defaultCellStyle);
                //销售模式
                Cell cell9 = rowBOM.createCell(9);
                cell9.setCellValue(salSalesOrder.getSaleMode()==null?null:modeMaps.get(salSalesOrder.getSaleMode().toString()));
                cell9.setCellStyle(defaultCellStyle);
                //销售合同号
                Cell cell10A = rowBOM.createCell(10);
                cell10A.setCellValue(salSalesOrder.getSaleContractCode());
                cell10A.setCellStyle(defaultCellStyle);
                //销售渠道
                Cell cell11A = rowBOM.createCell(11);
                cell11A.setCellValue(salSalesOrder.getBusinessChannelName());
                cell11A.setCellStyle(defaultCellStyle);
                //物料类型
                Cell cell12A = rowBOM.createCell(12);
                cell12A.setCellValue(salSalesOrder.getMaterialTypeName());
                cell12A.setCellStyle(defaultCellStyle);
                //销售部门
                Cell cell13A = rowBOM.createCell(13);
                cell13A.setCellValue(salSalesOrder.getDepartmentName());
                cell13A.setCellStyle(defaultCellStyle);
                //"仓库"
                Cell cell14A = rowBOM.createCell(14);
                cell14A.setCellValue(salSalesOrder.getStorehouseName());
                cell14A.setCellStyle(defaultCellStyle);
                //"库位"
                Cell cell15A = rowBOM.createCell(15);
                cell15A.setCellValue(salSalesOrder.getLocationName());
                cell15A.setCellStyle(defaultCellStyle);
                //"收货人"
                Cell cell16A = rowBOM.createCell(16);
                cell16A.setCellValue(salSalesOrder.getConsignee());
                cell16A.setCellStyle(defaultCellStyle);
                //"收货人联系电话"
                Cell cell17A = rowBOM.createCell(17);
                cell17A.setCellValue(salSalesOrder.getConsigneePhone());
                cell17A.setCellStyle(defaultCellStyle);
                //,"收货地址"
                Cell cell18A = rowBOM.createCell(18);
                cell18A.setCellValue(salSalesOrder.getConsigneeAddr());
                cell18A.setCellStyle(defaultCellStyle);
                //"销售组织",
                Cell cell19A = rowBOM.createCell(19);
                cell19A.setCellValue(salSalesOrder.getSaleOrgName());
                cell19A.setCellStyle(defaultCellStyle);
                // "销售组"
                Cell cell20A = rowBOM.createCell(20);
                cell20A.setCellValue(salSalesOrder.getSaleGroupName());
                cell20A.setCellStyle(defaultCellStyle);
                // " 客方跟单员",
                Cell cell21A = rowBOM.createCell(21);
                cell21A.setCellValue(salSalesOrder.getCustomerBusinessman());
                cell21A.setCellStyle(defaultCellStyle);
                // 纸质下单合同号
                Cell cell22A = rowBOM.createCell(22);
                cell22A.setCellValue(salSalesOrder.getPaperSaleContractCode());
                cell22A.setCellStyle(defaultCellStyle);
                // "备注"
                Cell cell23A = rowBOM.createCell(23);
                cell23A.setCellValue(salSalesOrder.getRemark());
                cell23A.setCellStyle(defaultCellStyle);
                //第三行数据
                Row rowBomItm = sheet.createRow(2);
                String[] titleItem={"商品编码","商品名称","SKU1名称","SKU2名称","订单量","基本计量单位","需求日期","最晚需求日期","合同交期","销售价(含税)","销售价(不含税)","金额(含税)","金额(不含税)","税率","备注"};
                for (int i=0;i<titleItem.length;i++) {
                    Cell cell = rowBomItm.createCell(i);
                    cell.setCellValue(titleItem[i]);
                    cell.setCellStyle(cellStyle);
                }
                //   数据部分
                for (int i=0;i<salSalesOrderItemList.size();i++) {
                    Row row = sheet.createRow(i+3);
                    //物料编码
                    Cell cell01 = row.createCell(0);
                    cell01.setCellValue(salSalesOrderItemList.get(i).getMaterialCode());
                    cell01.setCellStyle(defaultCellStyle);
                    //物料名称
                    Cell cell02 = row.createCell(1);
                    cell02.setCellValue(salSalesOrderItemList.get(i).getMaterialName());
                    cell02.setCellStyle(defaultCellStyle);
                    //SKU1名称
                    Cell cell03 = row.createCell(2);
                    cell03.setCellValue(salSalesOrderItemList.get(i).getSku1Name());
                    cell03.setCellStyle(defaultCellStyle);
                    //SKU2名称
                    Cell cell05 = row.createCell(3);
                    cell05.setCellValue(salSalesOrderItemList.get(i).getSku2Name());
                    cell05.setCellStyle(defaultCellStyle);
                    //订单量
                    Cell cell06 = row.createCell(4);
                    cell06.setCellValue(salSalesOrderItemList.get(i).getQuantity()==null?null:removeZero(salSalesOrderItemList.get(i).getQuantity().toString()));
                    cell06.setCellStyle(defaultCellStyle);
                    //基本计量单位
                    Cell cell07 = row.createCell(5);
                    cell07.setCellValue(salSalesOrderItemList.get(i).getUnitBaseName());
                    cell07.setCellStyle(defaultCellStyle);
                    // 需求日期
                    Cell cell08 = row.createCell(6);
                    cell08.setCellValue(salSalesOrderItemList.get(i).getDemandDate()==null?null:sdf.format(salSalesOrderItemList.get(i).getDemandDate()));
                    cell08.setCellStyle(defaultCellStyle);
                    //最晚需求日期
                    Cell cell09 = row.createCell(7);
                    cell09.setCellValue(salSalesOrderItemList.get(i).getLatestDemandDate()==null?null:sdf.format(salSalesOrderItemList.get(i).getLatestDemandDate()));
                    cell09.setCellStyle(defaultCellStyle);
                    //合同交期
                    Cell cell10 = row.createCell(8);
                    cell10.setCellValue(salSalesOrderItemList.get(i).getContractDate()==null?null:sdf.format(salSalesOrderItemList.get(i).getContractDate()));
                    cell10.setCellStyle(defaultCellStyle);
                    //销售价(含税)
                    Cell cell11 = row.createCell(9);
                    cell11.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().toString()));
                    cell11.setCellStyle(defaultCellStyle);
                    //销售价(不含税)
                    Cell cell12 = row.createCell(10);
                    cell12.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().divide(BigDecimal.ONE.add(salSalesOrderItemList.get(i).getTaxRate()),BigDecimal.ROUND_HALF_UP,4).toString()));
                    cell12.setCellStyle(defaultCellStyle);
                    //金额(含税)
                    Cell cell13 = row.createCell(11);
                    cell13.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().multiply(salSalesOrderItemList.get(i).getQuantity()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    cell13.setCellStyle(defaultCellStyle);
                    //金额(不含税)
                    Cell cell14 = row.createCell(12);
                    cell14.setCellValue(salSalesOrderItemList.get(i).getSalePriceTax()==null?null:removeZero(salSalesOrderItemList.get(i).getSalePriceTax().divide(BigDecimal.ONE.add(salSalesOrderItemList.get(i).getTaxRate()),BigDecimal.ROUND_HALF_UP,6).multiply(salSalesOrderItemList.get(i).getQuantity()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    cell14.setCellStyle(defaultCellStyle);
                    //税率
                    Cell cell15 = row.createCell(13);
                    cell15.setCellValue(salSalesOrderItemList.get(i).getTaxRate()==null?null:removeZero(salSalesOrderItemList.get(i).getTaxRate().toString()));
                    cell15.setCellStyle(defaultCellStyle);
                    //备注
                    Cell cell16 = row.createCell(14);
                    cell16.setCellValue(salSalesOrderItemList.get(i).getRemark());
                    cell16.setCellStyle(defaultCellStyle);
                }
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                workbook.write(response.getOutputStream());
            }catch (Exception e){
                throw new CustomException("导出失败");
            }}
    }
    public String removeZero(String s){
        if(s.indexOf(".") > 0){
            //正则表达
            s = s.replaceAll("0+?$", "");//去掉后面无用的零
            s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return s;
    }
}
