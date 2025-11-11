package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.InvInventoryProductUserMaterial;
import com.platform.ems.domain.excel.SaleOrderExcel;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.DocumentCategory;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.IInvInventoryDocumentService;
import com.platform.ems.service.IPurPurchasePriceService;
import com.platform.ems.service.ISalSalePriceService;
import com.platform.ems.util.*;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.*;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.stream.Collectors.toList;

/**
 * 库存凭证Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-16
 */
@Slf4j
@Service
@Transactional
@SuppressWarnings("all")
public class InvInventoryDocumentServiceImpl extends ServiceImpl<InvInventoryDocumentMapper, InvInventoryDocument> implements IInvInventoryDocumentService {
    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private InvInventoryDocumentAttachMapper attachMapper;
    @Autowired
    private InvCusSpecialInventoryMapper invCusSpecialInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private SalSalesOrderServiceImpl salSalesOrderimpl;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private PurPurchaseOrderServiceImpl purPurchaseOrderServiceImpl;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private ConDocTypePurchaseOrderMapper docTypePurchaseOrderMapper;
    @Autowired
    private ConDocTypeSalesOrderMapper docTypeSalesOrderMapper;
    @Autowired
    private ManManufactureOrderServiceImpl manOrderServiceImpl;
    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private InvIntransitInventoryMapper invIntransitInventoryMapper;
    @Autowired
    private InvMaterialRequisitionServiceImpl invMaterialServiceImpl;
    @Autowired
    private InvMaterialRequisitionMapper invMaterialRequisitionMapper;
    @Autowired
    private InvInventoryTransferServiceImpl invTransferServiceImpl;
    @Autowired
    private InvInventoryTransferMapper invInventoryTransferMapper;
    @Autowired
    private InvInventoryTransferItemMapper invInventoryTransferItemMapper;
    @Autowired
    private InvGoodReceiptNoteServiceImpl invGooReceiptNoteServiceImpl;
    @Autowired
    private InvGoodReceiptNoteMapper invGoodReceiptNoteMapper;
    @Autowired
    private InvGoodReceiptNoteItemMapper invGoodReceiptNoteItemMapper;
    @Autowired
    private InvGoodIssueNoteServiceImpl invGoodIssueNoteServiceImpl;
    @Autowired
    private InvGoodIssueNoteMapper invGoodIssueNoteMapper;
    @Autowired
    private InvGoodIssueNoteItemMapper invGoodIssueNoteItemMapper;
    @Autowired
    private DelDeliveryNoteServiceImpl delDeliveryNoteServiceImpl;
    @Autowired
    private DelDeliveryNoteMapper delDeliveryNoteMapper;
    @Autowired
    private DelDeliveryNoteItemMapper delDeliveryNoteItemMapper;
    @Autowired
    private FinBookPaymentEstimationServiceImpl finBookPaymentEstimationServiceImpl;
    @Autowired
    private FinBookReceiptEstimationServiceImpl finBookReceiptEstimationServiceImpl;
    @Autowired
    private PurRecordVendorConsignServiceImpl purRecordVendorConsignServiceImpl;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockMapper;
    @Autowired
    private InvMaterialRequisitionItemMapper invMaterialRequisitionItemMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private SysDefaultSettingClientMapper sysDefaultSettingClientMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationeMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private FinBookReceiptEstimationMapper finBookReceiptEstimationMapper;
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private FinBookPaymentEstimationMapper finBookPaymentEstimationMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;
    @Autowired
    private InvStorehouseMaterialMapper invStorehouseMaterialMapper;
    @Autowired
    private InvReserveInventoryMapper invReserveInventoryMapper;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private ManManufactureOrderProductMapper manManufactureOrderProductMapper;
    @Autowired
    private ConBuTypeDeliveryNoteMapper conBuTypeDeliveryNoteMapper;
    @Autowired
    private ConDocTypeDeliveryNoteMapper conDocTypeDeliveryNoteMapper;
    @Autowired
    private ConInoutDocumentMovementTypeRelationMapper movementTypeRelationMapper;
    @Autowired
    private SysClientMapper sysClientMapper;
    @Autowired
    private BasMaterialBarcodeMapper materialBarcodeMapper;
    @Autowired
    private BasSkuMapper skuMapper;

    @Autowired
    private SalSaleContractMapper saleContractMapper;

    @Autowired
    private PurPurchaseContractMapper purchaseContractMapper;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "stock_lock";

    private static final String TITLE = "库存凭证";

    /**
     *通过业务单号查询对应数据(多订单)
     *
     * @param
     * @return 库存凭证
     */
    @Override
    public InvInventoryDocument getInvInventoryDocumentList(InvInventoryDocumentOrders request) {
        InvInventoryDocument response = new InvInventoryDocument();
        List<InvInventoryDocument> list = new ArrayList<>();
        List<InvInventoryDocumentItem> itemList = new ArrayList<>();
        String documentTypeName = null;
        // 采购
        List<ConDocTypePurchaseOrder> pdocList = docTypePurchaseOrderMapper.selectList(new QueryWrapper<>());
        Map<String, String> pdocMap = pdocList.stream().collect(Collectors.toMap(ConDocTypePurchaseOrder::getCode, ConDocTypePurchaseOrder::getName));
        // 销售
        List<ConDocTypeSalesOrder> sdocList = docTypeSalesOrderMapper.selectList(new QueryWrapper<>());
        Map<String, String> sdocMap = sdocList.stream().collect(Collectors.toMap(ConDocTypeSalesOrder::getCode, ConDocTypeSalesOrder::getName));
        if (ArrayUtil.isNotEmpty(request.getCodeList())) {
            if (ConstantsInventory.REFER_DOC_CAT_PO.equals(request.getReferDocCategory())) {
                List<PurPurchaseOrder> orderList = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda()
                        .in(PurPurchaseOrder::getPurchaseOrderCode, request.getCodeList()));
                if (CollectionUtil.isNotEmpty(orderList)) {
                    documentTypeName = pdocMap.get(orderList.get(0).getDocumentType());
                    Map<String, List<PurPurchaseOrder>> orderListMap = orderList.stream()
                            .collect(Collectors.groupingBy(e -> String.valueOf(e.getDocumentType())));
                    if (orderListMap.size() > 1) {
                        throw new BaseException("明细的订单的单据类型需一致！");
                    }
                }
            }
            else if (ConstantsInventory.REFER_DOC_CAT_PO.equals(request.getReferDocCategory())) {
                List<SalSalesOrder> orderList = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                        .in(SalSalesOrder::getSalesOrderCode, request.getCodeList()));
                if (CollectionUtil.isNotEmpty(orderList)) {
                    documentTypeName = sdocMap.get(orderList.get(0).getDocumentType());
                    Map<String, List<SalSalesOrder>> orderListMap = orderList.stream()
                            .collect(Collectors.groupingBy(e -> String.valueOf(e.getDocumentType())));
                    if (orderListMap.size() > 1) {
                        throw new BaseException("明细的订单的单据类型需一致！");
                    }
                }
            }

            for (String code : request.getCodeList()) {
                InvInventoryDocument one = getInvInventoryDocument(code, request.getReferDocCategory(),
                        request.getType(), request.getMovementType(), request.getDocumentCategory(), ConstantsEms.YES);
                if (one != null && CollectionUtil.isNotEmpty(one.getInvInventoryDocumentItemList())) {
                    // 单据类型
                    for (InvInventoryDocumentItem documentItem : one.getInvInventoryDocumentItemList()) {
                        documentItem.setDocumentTypeName(documentTypeName);
                    }
                    list.add(one);
                }
            }
            if (CollectionUtil.isNotEmpty(list)) {
                for (InvInventoryDocument doc : list) {
                    itemList.addAll(doc.getInvInventoryDocumentItemList());
                }
                List<Long> sidItemList = Arrays.asList(request.getItemSidList());
                itemList = itemList.stream().filter(o->sidItemList.contains(o.getReferDocumentItemSid())).collect(toList());
                BeanCopyUtils.copyProperties(list.get(0), response);
                response.setReferDocumentSid(null).setReferDocumentCode(null);
                response.setInvInventoryDocumentItemList(itemList);
            }
        }
        return response;
    }

    /**
     * 通过业务单号查询
     *
     * @param inventoryDocumentSid 库存凭证ID
     * @return 库存凭证
     */
    @Override
    public InvInventoryDocument getInvInventoryDocument(String code, String referDocCategory, String type, String movementType, String documentCategory, String isMultiOrder) {
        //测试 暂时 ITI-调拨单,GRN-收货单；GIN-发货单；SO-销售订单；MO-生产订单；MRR-退料单；MR-领料单；PO-采购订单;SDN 销售发货单;PDN采购交货;销售退货发货单
        if (referDocCategory.equals(DocCategory.SALE_CHK.getCode()) || referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            DelDeliveryNote delDeliveryNote = new DelDeliveryNote();
            delDeliveryNote.setDeliveryNoteCode(Long.valueOf(code));
            if (referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
                //1是交货   2是发货
                if (referDocCategory.equals(DocCategory.SALE_RU.getCode())) {
                    delDeliveryNote.setDocumentType(DocCategory.SALE_RU.getCode()); //销售发货单
                } else {
                    delDeliveryNote.setDocumentType(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode());//销售退货收货单
                }
                delDeliveryNote.setDeliveryCategory("2");
            } else {
                if (referDocCategory.equals(DocCategory.SALE_CHK.getCode())) {
                    delDeliveryNote.setDocumentType(DocCategory.SALE_CHK.getCode());  //采购交货
                } else {
                    delDeliveryNote.setDocumentType(DocCategory.RETURN_BACK_PURCHASE_R.getCode());//采购退货发货单
                }
                delDeliveryNote.setDeliveryCategory("1");
            }
            DelDeliveryNote note = delDeliveryNoteMapper.selectOne(new QueryWrapper<DelDeliveryNote>()
                    .lambda().eq(DelDeliveryNote::getDeliveryNoteCode, code));
            if (note != null) {
                Long sid = note.getDeliveryNoteSid();
                DelDeliveryNote delDeliveryNoteNew = delDeliveryNoteServiceImpl.selectDelDeliveryNoteById(sid, delDeliveryNote.getDeliveryCategory());
                if (referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
                    ConBuTypeDeliveryNote conBuTypeDeliveryNote = conBuTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                            .eq(ConBuTypeDeliveryNote::getCode, note.getBusinessType())
                    );
                    if (conBuTypeDeliveryNote != null) {
                        String chukuCategory = conBuTypeDeliveryNote.getChukuCategory();
                        if (!ConstantsEms.CHUKU_CATEGORY_WSC.equals(chukuCategory)) {
                            throw new CustomException("业务单号校验不通过，请核查");
                        }
                    }
                    if (referDocCategory.equals(DocCategory.SALE_RU.getCode())) {
                        //销售发货单
                        if (ConstantsEms.YES.equals(delDeliveryNoteNew.getIsReturnGoods()) || ConstantsEms.YES.equals(delDeliveryNoteNew.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(delDeliveryNoteNew.getInventoryControlMode())) {
                            throw new CustomException("业务单号校验不通过，请核查");
                        }
                    } else {
                        //销售退货收货单
                        if (ConstantsEms.NO.equals(delDeliveryNoteNew.getIsReturnGoods()) || ConstantsEms.YES.equals(delDeliveryNoteNew.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(delDeliveryNoteNew.getInventoryControlMode())) {
                            throw new CustomException("业务单号校验不通过，请核查");
                        }
                    }
                } else {
                    if (referDocCategory.equals(DocCategory.SALE_CHK.getCode())) {
                        if (ConstantsEms.YES.equals(delDeliveryNoteNew.getIsReturnGoods()) || ConstantsEms.YES.equals(delDeliveryNoteNew.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(delDeliveryNoteNew.getInventoryControlMode())) {
                            throw new CustomException("业务单号校验不通过，请核查");
                        }
                    } else {//采购退货发货单
                        if (ConstantsEms.NO.equals(delDeliveryNoteNew.getIsReturnGoods()) || ConstantsEms.YES.equals(delDeliveryNoteNew.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(delDeliveryNoteNew.getInventoryControlMode())) {
                            throw new CustomException("业务单号校验不通过，请核查");
                        }
                    }
                }
                BeanCopyUtils.copyProperties(delDeliveryNoteNew, invInventoryDocument);
                Long purchaseOrderSid = delDeliveryNoteNew.getDelDeliveryNoteItemList().get(0).getPurchaseOrderSid();
                Long salesOrderSid = delDeliveryNoteNew.getDelDeliveryNoteItemList().get(0).getSalesOrderSid();
                if (purchaseOrderSid != null) {
                    PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectById(purchaseOrderSid);
                    if (purPurchaseOrder != null) {
                        invInventoryDocument.setSaleAndPurchaseDocument(purPurchaseOrder.getDocumentType());
                    }
                }
                if (salesOrderSid != null) {
                    SalSalesOrder salSalesOrder = salSalesOrderMapper.selectById(salesOrderSid);
                    if (salSalesOrder != null) {
                        invInventoryDocument.setSaleAndPurchaseDocument(salSalesOrder.getDocumentType());
                    }
                }
                if (DocCategory.PURCHAASE_JI_SHOU.getCode().equals(invInventoryDocument.getSaleAndPurchaseDocument()) || DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(invInventoryDocument.getSaleAndPurchaseDocument())) {
                    invInventoryDocument.setSpecialStock(ConstantsEms.VEN_CU);
                }
                invInventoryDocument.setReferDocumentCode(String.valueOf(delDeliveryNoteNew.getDeliveryNoteCode()));
                invInventoryDocument.setReferDocumentSid(delDeliveryNoteNew.getDeliveryNoteSid());
                invInventoryDocument.setDeliveryNoteSid(delDeliveryNoteNew.getDeliveryNoteSid());
                List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNoteNew.getDelDeliveryNoteItemList();
                if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    delDeliveryNoteItemList.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        invInventoryDocumentItem.setReferDocumentItemNum(Integer.valueOf(item.getItemNum().toString()));
                        invInventoryDocumentItem.setQuantity(item.getDeliveryQuantity());
                        invInventoryDocumentItem.setBarcode(item.getBarcode());
                        if (referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
                            invInventoryDocumentItem.setInvPrice(item.getSalePrice());
                            invInventoryDocumentItem.setPrice(item.getSalePriceTax());
                            invInventoryDocumentItem.setInvPriceTax(item.getSalePriceTax());
                            invInventoryDocumentItem.setBusinessQuantity(item.getDeliveryQuantity());
                        } else {
                            PurPurchaseOrderItem purPurchaseOrderItem = purPurchaseOrderItemMapper.selectOne(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                                    .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid()));
                            if (purPurchaseOrderItem != null) {
                                if (purPurchaseOrderItem.getProductCodes() != null) {
                                    invInventoryDocumentItem.setProductCodes(purPurchaseOrderItem.getProductCodes());
                                } else {
                                    invInventoryDocumentItem.setProductCodes(purPurchaseOrderItem.getProductCode() == null ? null : purPurchaseOrderItem.getProductCode().toString());
                                }
                            }
                            invInventoryDocumentItem.setPrice(item.getPurchasePriceTax());
                            invInventoryDocumentItem.setInvPrice(item.getPurchasePrice());
                            invInventoryDocumentItem.setInvPriceTax(item.getPurchasePriceTax());
                            invInventoryDocumentItem.setBusinessQuantity(item.getDeliveryQuantity());
                        }
                        invInventoryDocumentItem.setTaxRate(item.getOrderTaxRate());
                        invInventoryDocumentItem.setReferDocumentSid(item.getDeliveryNoteSid());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getDeliveryNoteItemSid());
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                judgeCode(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> list = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(list);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的销售发货单或采购交货单");
        }
        //库存凭证类别
        if (referDocCategory.equals(DocCategory.INV_DOCUMENT.getCode())) {
            InvInventoryDocument invInventoryDocument = invInventoryDocumentMapper.selectOne(new QueryWrapper<InvInventoryDocument>().lambda()
                    .eq(InvInventoryDocument::getInventoryDocumentCode, code)
            );
            if (invInventoryDocument == null) {
                throw new CustomException("无此业务单号的库存凭证");
            }
            if (!HandleStatus.POSTING.getCode().equals(invInventoryDocument.getHandleStatus())) {
                throw new CustomException("业务单号校验不通过，请核实");
            }
            ConInoutDocumentMovementTypeRelation typeRelation = movementTypeRelationMapper.selectOne(new QueryWrapper<ConInoutDocumentMovementTypeRelation>().lambda()
                    .eq(ConInoutDocumentMovementTypeRelation::getInOutMovementTypeCode, movementType)
                    .eq(ConInoutDocumentMovementTypeRelation::getDocumentMovementTypeCode, invInventoryDocument.getMovementType())
            );
            if (typeRelation == null) {
                throw new CustomException("出入库作业类型与单据作业类型不匹配");
            }
            if (!ConstantsEms.IN_STORE_STATUS_NOT.equals(invInventoryDocument.getInOutStockStatus())) {
                throw new CustomException("该单据已入库，无法重复进行入库操作");
            }
            List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectInvInventoryDocumentItemById(invInventoryDocument.getInventoryDocumentSid());
            invInventoryDocument.setCreateDate(null)
                    .setReferDocumentCode(invInventoryDocument.getInventoryDocumentCode().toString())
                    .setReferDocumentSid(invInventoryDocument.getInventoryDocumentSid())
                    .setCreatorAccountName(null)
                    .setInventoryDocumentSid(null)
                    .setInventoryDocumentCode(null)
                    .setStorehouseSid(null)
                    .setStorehouseLocationSid(null)
                    .setStorehouseName(null)
                    .setLocationName(null)
                    .setAccountDate(null)
                    .setInOutStockStatus(null)
                    .setHandleStatus(null)
                    .setUpdateDate(null)
                    .setUpdaterAccount(null);
            itemList.forEach(li -> {
                li.setReferDocumentItemNum(li.getItemNum())
                        .setReferDocumentCode(code)
                        .setReferDocumentItemSid(li.getInventoryDocumentItemSid())
                        .setReferDocumentSid(li.getInventoryDocumentSid())
                        .setInventoryDocumentSid(null)
                        .setInventoryDocumentItemSid(null)
                        .setCreateDate(null)
                        .setUpdateDate(null)
                        .setUpdaterAccount(null)
                        .setCreatorAccountName(null)
                        .setCreatorAccount(null);
            });
            invInventoryDocument.setInvInventoryDocumentItemList(itemList);
            // 获取库存量
            List<InvInventoryDocumentItem> list = getItemUnlimitedQuantity(invInventoryDocument);
            invInventoryDocument.setInvInventoryDocumentItemList(list);
            return invInventoryDocument;
        }
        if (referDocCategory.equals(DocCategory.ALLOCAT.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            InvInventoryTransfer transfer = invInventoryTransferMapper.selectOne(new QueryWrapper<InvInventoryTransfer>().lambda()
                    .eq(InvInventoryTransfer::getInventoryTransferCode, code)
            );
            if (transfer != null) {
                //判断作业类型
                judgeMove(movementType, transfer.getMovementType());
                ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation = movementTypeRelationMapper.selectOne(new QueryWrapper<ConInoutDocumentMovementTypeRelation>().lambda()
                        .eq(ConInoutDocumentMovementTypeRelation::getInOutMovementTypeCode, movementType)
                        .eq(ConInoutDocumentMovementTypeRelation::getDocumentMovementTypeCode, transfer.getMovementType())
                );
                if (DocumentCategory.RU.getCode().equals(conInoutDocumentMovementTypeRelation.getInvDocCategoryCode())) {
                    type = ConstantsEms.RU_KU;
                } else {
                    type = ConstantsEms.CHU_KU;
                }
                if (conInoutDocumentMovementTypeRelation == null) {
                    throw new CustomException("出入库作业类型与单据作业类型不匹配");
                }
                ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda().eq(ConMovementType::getCode, movementType));
                ConMovementType transferConMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda().eq(ConMovementType::getCode, transfer.getMovementType()));
                if (ConstantsEms.YES.equals(conMovementType.getIsTransferOnestep())) {
                    if (ConstantsEms.RU_KU.equals(type)) {
                        throw new CustomException("该调拨单已入库，无法重复进行入库操作");
                    }
                    if (!ConstantsEms.YES.equals(transferConMovementType.getIsTransferOnestep())) {
                        throw new CustomException("该调拨单校验不通过，请核实！");
                    }
                }
                Long sid = transfer.getInventoryTransferSid();
                InvInventoryTransfer invInventoryTransfer = invTransferServiceImpl.selectInvInventoryTransferById(sid);
                BeanCopyUtils.copyProperties(invInventoryTransfer, invInventoryDocument);
                invInventoryDocument.setReferDocumentCode(String.valueOf(invInventoryTransfer.getInventoryTransferCode()));
                invInventoryDocument.setReferDocumentSid(invInventoryTransfer.getInventoryTransferSid());
                List<InvInventoryTransferItem> listInvInventoryTransfer = invInventoryTransfer.getListInvInventoryTransfer();
                if (CollectionUtils.isNotEmpty(listInvInventoryTransfer)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    String finalType = type;
                    listInvInventoryTransfer.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        invInventoryDocumentItem.setReferDocumentSid(invInventoryTransfer.getInventoryTransferSid());
                        invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getInventoryTransferItemSid());
                        invInventoryDocumentItem.setInventoryTransferItemSid(item.getInventoryTransferItemSid());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                exchange(invInventoryDocument, type);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> list = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(list);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的调拨单");
        }
        if (referDocCategory.equals(DocCategory.RECIPIT.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            InvGoodReceiptNote note = invGoodReceiptNoteMapper.selectOne(new QueryWrapper<InvGoodReceiptNote>().lambda()
                    .eq(InvGoodReceiptNote::getNoteCode, code)
            );
            if (note != null) {
                //判断作业类型
                judgeMove(movementType, note.getMovementType());
                Long sid = note.getNoteSid();
                InvGoodReceiptNote invGoodReceiptNote = invGooReceiptNoteServiceImpl.selectInvGoodReceiptNoteById(sid);
                BeanCopyUtils.copyProperties(invGoodReceiptNote, invInventoryDocument);
                invInventoryDocument.setReferDocumentCode(String.valueOf(invGoodReceiptNote.getNoteCode()));
                invInventoryDocument.setReferDocumentSid(invGoodReceiptNote.getNoteSid());
                List<InvGoodReceiptNoteItem> listInvGoodReceiptNoteItem = invGoodReceiptNote.getListInvGoodReceiptNoteItem();
                if (CollectionUtils.isNotEmpty(listInvGoodReceiptNoteItem)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    listInvGoodReceiptNoteItem.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        invInventoryDocumentItem.setReferDocumentSid(invGoodReceiptNote.getNoteSid());
                        invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getNoteItemSid());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                getQuantity(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> list = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(list);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的收货单");
        }
        if (referDocCategory.equals(DocCategory.SHIP.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            InvGoodIssueNote note = invGoodIssueNoteMapper.selectOne(new QueryWrapper<InvGoodIssueNote>().lambda()
                    .eq(InvGoodIssueNote::getNoteCode, code)
            );
            List<InvGoodIssueNote> invGoodIssueNotes = invGoodIssueNoteServiceImpl.selectInvGoodIssueNoteList(new InvGoodIssueNote().setNoteCode(code));
            if (note != null) {
                //判断作业类型
                judgeMove(movementType, note.getMovementType());
                Long sid = note.getNoteSid();
                InvGoodIssueNote invGoodIssueNote = invGoodIssueNoteServiceImpl.selectInvGoodIssueNoteById(sid);
                BeanCopyUtils.copyProperties(invGoodIssueNote, invInventoryDocument);
                invInventoryDocument.setReferDocumentCode(String.valueOf(invGoodIssueNote.getNoteCode()));
                invInventoryDocument.setReferDocumentSid(invGoodIssueNote.getNoteSid());
                List<InvGoodIssueNoteItem> list = invGoodIssueNote.getListInvGoodIssueNoteItem();
                if (CollectionUtils.isNotEmpty(list)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    list.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        invInventoryDocumentItem.setReferDocumentSid(invGoodIssueNote.getNoteSid());
                        invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getNoteItemSid());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                getQuantity(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> documentItemList = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(documentItemList);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的发货单");
        }
        if (referDocCategory.equals(DocCategory.SALE_ORDER.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            SalSalesOrder order = salSalesOrderMapper.selectOne(new QueryWrapper<SalSalesOrder>().lambda()
                    .eq(SalSalesOrder::getSalesOrderCode, code)
            );
            if (order != null) {
                Long sid = order.getSalesOrderSid();
                SalSalesOrder salSalesOrder = salSalesOrderimpl.selectSalSalesOrderById(sid);
                String shipmentCategory = salSalesOrder.getDeliveryType();
                String documentType = salSalesOrder.getDocumentType();
                if (DocCategory.SALE_ORDER.getCode().equals(referDocCategory)) {
                    if (ConstantsEms.YES.equals(salSalesOrder.getIsReturnGoods()) || ConstantsEms.YES.equals(salSalesOrder.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(salSalesOrder.getInventoryControlMode())) {
                        throw new CustomException("业务单号校验不通过，请核查");
                    }
                    if (ConstantsEms.SALE_SHIP.equals(shipmentCategory)) {
                        throw new CustomException("业务单号" + code + "，不支持按销售订单出库，请按销售发货单出库！");
                    }
                } else {
                    if (ConstantsEms.NO.equals(salSalesOrder.getIsReturnGoods()) || ConstantsEms.YES.equals(salSalesOrder.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(salSalesOrder.getInventoryControlMode())) {
                        throw new CustomException("业务单号校验不通过，请核查");
                    }
                    if (ConstantsEms.SALE_SHIP.equals(shipmentCategory)) {
                        throw new CustomException("业务单号" + code + "，不支持按销售退货订单入库，请按销售退货收货单入库！");
                    }
                }
                BeanCopyUtils.copyProperties(salSalesOrder, invInventoryDocument);
                invInventoryDocument.setReferDocumentCode(String.valueOf(salSalesOrder.getSalesOrderCode()));
                invInventoryDocument.setReferDocumentSid(salSalesOrder.getSalesOrderSid());
                invInventoryDocument.setSalesOrderCode(String.valueOf(salSalesOrder.getSalesOrderCode()));
                invInventoryDocument.setSaleAndPurchaseDocument(salSalesOrder.getDocumentType());
                // 多订单的情况
                if (ConstantsEms.YES.equals(isMultiOrder)) {
                    invInventoryDocument.setReferDocumentCode(null);
                    invInventoryDocument.setReferDocumentSid(null);
                }
                List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
                if (CollectionUtils.isNotEmpty(list)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    list.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        // 合同
                        invInventoryDocumentItem.setSaleContractSid(salSalesOrder.getSaleContractSid())
                                .setSaleContractCode(salSalesOrder.getSaleContractCode());
                        invInventoryDocumentItem.setInvPrice(item.getSalePrice());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getSalesOrderItemSid());
                        invInventoryDocumentItem.setPrice(item.getSalePriceTax());
                        invInventoryDocumentItem.setReferDocumentSid(salSalesOrder.getSalesOrderSid());
                        invInventoryDocumentItem.setReferDocumentCode(salSalesOrder.getSalesOrderCode());
                        invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                        invInventoryDocumentItem.setInvPriceTax(item.getSalePriceTax());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        invInventoryDocumentItem.setOrderTaxRate(item.getTaxRate());
                        invInventoryDocumentItem.setOrderFreeFlag(item.getFreeFlag());
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                getQuantity(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> documentItemList = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(documentItemList);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的销售订单或者销售退货订单");
        }
        if (referDocCategory.equals(DocCategory.PURCHASE_ORDER.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_PURCHASE.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();

            PurPurchaseOrder order = purPurchaseOrderMapper.selectOne(new QueryWrapper<PurPurchaseOrder>().lambda()
                    .eq(PurPurchaseOrder::getPurchaseOrderCode, code)
            );
            if (order != null) {
                Long sid = order.getPurchaseOrderSid();
                PurPurchaseOrder purPurchaseOrder = purPurchaseOrderServiceImpl.selectPurPurchaseOrderById(sid);
                String shipmentCategory = purPurchaseOrder.getDeliveryType();
                String documentType = purPurchaseOrder.getDocumentType();
                if (DocCategory.PURCHASE_ORDER.getCode().equals(referDocCategory)) {
                    if (ConstantsEms.YES.equals(purPurchaseOrder.getIsReturnGoods()) || ConstantsEms.YES.equals(purPurchaseOrder.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(purPurchaseOrder.getInventoryControlMode())) {
                        throw new CustomException("业务单号校验不通过，请核查");
                    }
                    if (ConstantsEms.PURCHASE_SHIP.equals(shipmentCategory)) {
                        throw new CustomException("业务单号" + code + "，不支持按采购订单入库，请按采购交货单入库！");
                    }
                } else {
                    if (ConstantsEms.NO.equals(purPurchaseOrder.getIsReturnGoods()) || ConstantsEms.YES.equals(purPurchaseOrder.getIsConsignmentSettle()) || !ConstantsEms.INV_RESH.equals(purPurchaseOrder.getInventoryControlMode())) {
                        throw new CustomException("业务单号校验不通过，请核查");
                    }
                    if (ConstantsEms.PURCHASE_SHIP.equals(shipmentCategory)) {
                        throw new CustomException("业务单号" + code + "，不支持按采购退货订单出库，请按采购退货发货单出库！");
                    }
                }
                if (DocCategory.PURCHAASE_JI_SHOU.getCode().equals(documentType) || DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(documentType)) {
                    invInventoryDocument.setSpecialStock(ConstantsEms.VEN_CU);
                }
                BeanCopyUtils.copyProperties(purPurchaseOrder, invInventoryDocument);
                invInventoryDocument.setReferDocCategory(referDocCategory);
                invInventoryDocument.setReferDocumentCode(String.valueOf(purPurchaseOrder.getPurchaseOrderCode()));
                invInventoryDocument.setReferDocumentSid(purPurchaseOrder.getPurchaseOrderSid());
                invInventoryDocument.setPurchaseOrderCode(String.valueOf(purPurchaseOrder.getPurchaseOrderCode()));
                invInventoryDocument.setPurchaseContractSid(purPurchaseOrder.getPurchaseContractSid());
                invInventoryDocument.setSaleAndPurchaseDocument(purPurchaseOrder.getDocumentType());
                // 多订单的情况
                if (ConstantsEms.YES.equals(isMultiOrder)) {
                    invInventoryDocument.setReferDocumentCode(null);
                    invInventoryDocument.setReferDocumentSid(null);
                }
                List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
                if (CollectionUtils.isNotEmpty(list)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    list.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        if (item.getPurchasePriceTax() == null) {
//                            invInventoryDocumentItem.setInvPrice(new BigDecimal(0));
//                            invInventoryDocumentItem.setPrice(new BigDecimal(0));
                            invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        } else {
                            invInventoryDocumentItem.setInvPrice(item.getPurchasePrice());
                            invInventoryDocumentItem.setPrice(item.getPurchasePriceTax());
                            invInventoryDocumentItem.setInvPriceTax(item.getPurchasePriceTax());
                            invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        }
                        // 合同
                        invInventoryDocumentItem.setPurchaseContractSid(purPurchaseOrder.getPurchaseContractSid())
                                .setPurchaseContractCode(purPurchaseOrder.getPurchaseContractCode());
                        invInventoryDocumentItem.setOrderTaxRate(item.getTaxRate());
                        invInventoryDocumentItem.setOrderFreeFlag(item.getFreeFlag());
                        invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        invInventoryDocumentItem.setReferDocumentCode(String.valueOf(purPurchaseOrder.getPurchaseOrderCode()))
                                .setReferDocumentSid(purPurchaseOrder.getPurchaseOrderSid());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getPurchaseOrderItemSid())
                                .setReferDocumentItemNum(item.getItemNum());
                        if (item.getProductCodes() != null) {
                            invInventoryDocumentItem.setProductCodes(item.getProductCodes());
                        } else {
                            invInventoryDocumentItem.setProductCodes(item.getProductCode() == null ? null : item.getProductCode().toString());
                        }
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                getQuantity(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> documentItemList = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(documentItemList);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的采购订单或采购退货单");
        }
        if (referDocCategory.equals(DocCategory.PRODUCTION_ORDER.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            ManManufactureOrder order = manManufactureOrderMapper.selectOne(new QueryWrapper<ManManufactureOrder>().lambda()
                    .eq(ManManufactureOrder::getManufactureOrderCode, code)
            );
            if (order != null) {
                Long sid = order.getManufactureOrderSid();
                ManManufactureOrder manManufactureOrder = manOrderServiceImpl.selectManManufactureOrderById(sid);
                BeanCopyUtils.copyProperties(manManufactureOrder, invInventoryDocument);
                invInventoryDocument.setReferDocumentCode(String.valueOf(manManufactureOrder.getManufactureOrderCode()));
                invInventoryDocument.setReferDocumentSid(Long.valueOf(manManufactureOrder.getManufactureOrderSid()));
                List<ManManufactureOrderProduct> list = manManufactureOrder.getManManufactureOrderProductList();
                if (CollectionUtils.isNotEmpty(list)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    list.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        invInventoryDocumentItem.setReferDocumentItemNum(Integer.valueOf(item.getItemNum().toString()));
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getManufactureOrderProductSid());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        if (item.getRetailPrice() != null) {
                            invInventoryDocumentItem.setPrice(item.getRetailPrice());
                        }
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                }
                judgeStatus(invInventoryDocument);
                getQuantity(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> documentItemList = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(documentItemList);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的生产订单");
        }
        if (referDocCategory.equals(DocCategory.REQUESTION_CHK.getCode()) || referDocCategory.equals(DocCategory.REQUESTION_RU.getCode())) {
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory(referDocCategory);
            InvMaterialRequisition invMaterial = new InvMaterialRequisition();
            invMaterial.setMaterialRequisitionCode(Long.valueOf(code));
            invMaterial.setDocumentType(referDocCategory);
            InvMaterialRequisition invMateria = invMaterialRequisitionMapper.selectOne(new QueryWrapper<InvMaterialRequisition>().lambda()
                    .eq(InvMaterialRequisition::getMaterialRequisitionCode, code)
            );
            if (invMateria != null) {
                //判断作业类型
                judgeMove(movementType, invMateria.getMovementType());
                Long sid = invMateria.getMaterialRequisitionSid();
                InvMaterialRequisition invMaterialRequisition = invMaterialServiceImpl.selectInvMaterialRequisitionById(sid);
                BeanCopyUtils.copyProperties(invMaterialRequisition, invInventoryDocument);
                invInventoryDocument.setReferDocumentCode(String.valueOf(invMaterialRequisition.getMaterialRequisitionCode()));
                invInventoryDocument.setReferDocumentSid(Long.valueOf(invMaterialRequisition.getMaterialRequisitionSid()));
                List<InvMaterialRequisitionItem> list = invMaterialRequisition.getInvMaterialRequisitionItemList();
                if (CollectionUtils.isNotEmpty(list)) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                        list.forEach(item -> {
                            InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                            BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                            invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                            invInventoryDocumentItem.setReferDocumentSid(invMaterialRequisition.getMaterialRequisitionSid());
                            invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                            invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                            invInventoryDocumentItem.setReferDocumentItemSid(item.getMaterialRequisitionItemSid());
                            invInventoryDocuments.add(invInventoryDocumentItem);
                        });
                        invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocuments);
                    }
                }
                judgeStatus(invInventoryDocument);
                getQuantity(invInventoryDocument);
                getPriceInv(invInventoryDocument);
                setItemNum(invInventoryDocument);
                invInventoryDocument.setDocumentCategory(documentCategory);
                getIsLocation(referDocCategory, movementType, invInventoryDocument);
                // 获取库存量
                List<InvInventoryDocumentItem> documentItemList = getItemUnlimitedQuantity(invInventoryDocument);
                invInventoryDocument.setInvInventoryDocumentItemList(documentItemList);
                return invInventoryDocument;
            }
            throw new CustomException("无此业务单号的领退料单");
        }
        return null;
    }

    //获取各个单据明细
    @Override
    public ArrayList<InvInventoryDocumentItem> getItemAdd(DocumentAddItemRequest request) {
        String referDocCategory = request.getReferDocCategory();
        String code = request.getCode();
        if (referDocCategory.equals(DocCategory.SALE_CHK.getCode()) || referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode())) {
            DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
            BeanCopyUtils.copyProperties(request, delDeliveryNoteItem);
            delDeliveryNoteItem.setDeliveryNoteCode(code);
            List<DelDeliveryNoteItem> delDeliveryNoteItemList = delDeliveryNoteItemMapper.getDeliveryItemList(delDeliveryNoteItem);
            if (CollectionUtils.isNotEmpty(delDeliveryNoteItemList)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                delDeliveryNoteItemList.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    invInventoryDocumentItem.setReferDocumentItemNum(Integer.valueOf(item.getItemNum().toString()));
                    invInventoryDocumentItem.setQuantity(item.getDeliveryQuantity());
                    invInventoryDocumentItem.setBarcode(item.getBarcode());
                    if (referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
                        invInventoryDocumentItem.setInvPrice(item.getSalePrice());
                        invInventoryDocumentItem.setPrice(item.getSalePriceTax());
                        invInventoryDocumentItem.setInvPriceTax(item.getSalePriceTax());
                        invInventoryDocumentItem.setBusinessQuantity(item.getDeliveryQuantity());
                    } else {
                        PurPurchaseOrderItem purPurchaseOrderItem = purPurchaseOrderItemMapper.selectOne(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                                .eq(PurPurchaseOrderItem::getItemNum, item.getItemNum())
                                .eq(PurPurchaseOrderItem::getPurchaseOrderSid, item.getPurchaseOrderSid())
                        );
                        if (purPurchaseOrderItem.getProductCodes() != null) {
                            invInventoryDocumentItem.setProductCodes(purPurchaseOrderItem.getProductCodes());
                        } else {
                            invInventoryDocumentItem.setProductCodes(purPurchaseOrderItem.getProductCode() == null ? null : purPurchaseOrderItem.getProductCode().toString());
                        }
                        invInventoryDocumentItem.setPrice(item.getPurchasePriceTax());
                        invInventoryDocumentItem.setInvPrice(item.getPurchasePrice());
                        invInventoryDocumentItem.setInvPriceTax(item.getPurchasePriceTax());
                        invInventoryDocumentItem.setBusinessQuantity(item.getDeliveryQuantity());
                    }
                    invInventoryDocumentItem.setReferDocumentSid(item.getDeliveryNoteSid());
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getDeliveryNoteItemSid());
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return invInventoryDocuments;
            }
            return null;
        }
        if (referDocCategory.equals(DocCategory.ALLOCAT.getCode())) {
            InvInventoryTransferRequest invInventoryTransferRequest = new InvInventoryTransferRequest();
            BeanCopyUtils.copyProperties(request, invInventoryTransferRequest);
            invInventoryTransferRequest.setInventoryTransferCode(code);
            List<InvInventoryTransferResponse> listInvInventoryTransfer = invInventoryTransferItemMapper.reportInvInventoryTransfer(invInventoryTransferRequest);
            if (CollectionUtils.isNotEmpty(listInvInventoryTransfer)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                listInvInventoryTransfer.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());

                    invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getInventoryTransferItemSid());
                    invInventoryDocumentItem.setInventoryTransferItemSid(item.getInventoryTransferItemSid());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return invInventoryDocuments;
            }
            return null;
        }
        if (referDocCategory.equals(DocCategory.RECIPIT.getCode())) {
            InvReceiptNoteReportRequest invReceiptNoteReportRequest = new InvReceiptNoteReportRequest();
            BeanCopyUtils.copyProperties(request, invReceiptNoteReportRequest);
            List<InvReceiptNoteReportResponse> listInvGoodReceiptNoteItem = invGoodReceiptNoteItemMapper.reportInvGoodReceiptNote(invReceiptNoteReportRequest);
            if (CollectionUtils.isNotEmpty(listInvGoodReceiptNoteItem)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                listInvGoodReceiptNoteItem.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    invInventoryDocumentItem.setReferDocumentSid(item.getNoteSid());
                    invInventoryDocumentItem.setReferDocumentItemNum(Integer.valueOf(item.getItemNum()));
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getNoteItemSid());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return invInventoryDocuments;
            }
            return null;
        }
        if (referDocCategory.equals(DocCategory.SHIP.getCode())) {
            InvIssueNoteReportRequest invIssueNoteReportRequest = new InvIssueNoteReportRequest();
            BeanCopyUtils.copyProperties(request, invIssueNoteReportRequest);
            invIssueNoteReportRequest.setNoteCode(code);
            List<InvIssueNoteReportResponse> list = invGoodIssueNoteItemMapper.reportInvGoodIssueNote(invIssueNoteReportRequest);
            if (CollectionUtils.isNotEmpty(list)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                list.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    invInventoryDocumentItem.setReferDocumentSid(item.getNoteSid());
                    invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getNoteItemSid());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return invInventoryDocuments;
            }
            return null;
        }
        if (referDocCategory.equals(DocCategory.SALE_ORDER.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE.getCode())) {
            SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
            BeanCopyUtils.copyProperties(request, salSalesOrderItem);
            salSalesOrderItem.setSalesOrderCode(code);
            List<SalSalesOrderItem> list = salSalesOrderItemMapper.getItemList(salSalesOrderItem);
            if (CollectionUtils.isNotEmpty(list)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                list.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    invInventoryDocumentItem.setInvPrice(item.getSalePrice());
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getSalesOrderItemSid());
                    invInventoryDocumentItem.setPrice(item.getSalePriceTax());
                    invInventoryDocumentItem.setReferDocumentSid(item.getSalesOrderSid());
                    invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                    invInventoryDocumentItem.setInvPriceTax(item.getSalePriceTax());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return invInventoryDocuments;
            }
            return null;
        }
        if (referDocCategory.equals(DocCategory.PURCHASE_ORDER.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_PURCHASE.getCode())) {
            PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
            BeanCopyUtils.copyProperties(request, purPurchaseOrderItem);
            purPurchaseOrderItem.setPurchaseOrderCode(code);
            List<PurPurchaseOrderItem> list = purPurchaseOrderItemMapper.getItemList(purPurchaseOrderItem);
            if (CollectionUtils.isNotEmpty(list)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                list.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    if (item.getPurchasePriceTax() == null) {
//                            invInventoryDocumentItem.setInvPrice(new BigDecimal(0));
//                            invInventoryDocumentItem.setPrice(new BigDecimal(0));
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    } else {
                        invInventoryDocumentItem.setInvPrice(item.getPurchasePrice());
                        invInventoryDocumentItem.setPrice(item.getPurchasePriceTax());
                        invInventoryDocumentItem.setInvPriceTax(item.getPurchasePriceTax());
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    }
                    invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getPurchaseOrderItemSid());
                    if (item.getProductCodes() != null) {
                        invInventoryDocumentItem.setProductCodes(item.getProductCodes());
                    } else {
                        invInventoryDocumentItem.setProductCodes(item.getProductCode() == null ? null : item.getProductCode().toString());
                    }
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return null;
            }
        }
        if (referDocCategory.equals(DocCategory.PRODUCTION_ORDER.getCode())) {
            ManManufactureOrderProduct manManufactureOrderProduct = new ManManufactureOrderProduct();
            manManufactureOrderProduct.setManufactureOrderCode(code);
            BeanCopyUtils.copyProperties(request, manManufactureOrderProduct);
            List<ManManufactureOrderProduct> list = manManufactureOrderProductMapper.selectManManufactureOrderProductList(manManufactureOrderProduct);
            if (CollectionUtils.isNotEmpty(list)) {
                ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                list.forEach(item -> {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                    invInventoryDocumentItem.setReferDocumentItemNum(Integer.valueOf(item.getItemNum().toString()));
                    invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                    invInventoryDocumentItem.setReferDocumentItemSid(item.getManufactureOrderProductSid());
                    invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                    if (item.getRetailPrice() != null) {
                        invInventoryDocumentItem.setPrice(item.getRetailPrice());
                    }
                    invInventoryDocuments.add(invInventoryDocumentItem);
                });
                return invInventoryDocuments;
            }
            return null;
        }
        if (referDocCategory.equals(DocCategory.REQUESTION_CHK.getCode()) || referDocCategory.equals(DocCategory.REQUESTION_RU.getCode())) {
            InvMaterialRequisitionReportRequest invMaterialRequisitionReportRequest = new InvMaterialRequisitionReportRequest();
            invMaterialRequisitionReportRequest.setMaterialRequisitionCode(Long.valueOf(code));
            BeanCopyUtils.copyProperties(request, invMaterialRequisitionReportRequest);
            List<InvMaterialRequisitionReportResponse> list = invMaterialRequisitionItemMapper.reportInvMaterialRequisition(invMaterialRequisitionReportRequest);
            if (CollectionUtils.isNotEmpty(list)) {
                if (CollectionUtils.isNotEmpty(list)) {
                    ArrayList<InvInventoryDocumentItem> invInventoryDocuments = new ArrayList<>();
                    list.forEach(item -> {
                        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                        BeanCopyUtils.copyProperties(item, invInventoryDocumentItem);
                        invInventoryDocumentItem.setBusinessQuantity(item.getQuantity());
                        invInventoryDocumentItem.setReferDocumentSid(item.getMaterialRequisitionSid());
                        invInventoryDocumentItem.setReferDocumentItemNum(item.getItemNum());
                        invInventoryDocumentItem.setBarcode(item.getBarcode() != null ? item.getBarcode().toString() : null);
                        invInventoryDocumentItem.setReferDocumentItemSid(item.getMaterialRequisitionItemSid());
                        invInventoryDocuments.add(invInventoryDocumentItem);
                    });
                    return invInventoryDocuments;
                }
            }
            return null;
        }
        return null;
    }

    public void getIsLocation(String referDocCategory, String movementType, InvInventoryDocument invInventoryDocument) {
        ConInOutStockDocCategory conInOutStockDocCategory = conInOutStockDocCategoryMapper.selectOne(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                .eq(ConInOutStockDocCategory::getMovementTypeCode, movementType)
                .eq(ConInOutStockDocCategory::getDocCategoryCode, referDocCategory)
                .eq(ConInOutStockDocCategory::getInvDocCategoryCode, invInventoryDocument.getDocumentCategory())
        );
        if (conInOutStockDocCategory != null) {
            invInventoryDocument.setIsStorehouseEdit(conInOutStockDocCategory.getIsStorehouseEdit())
                    .setIsStorehouseLocationEdit(conInOutStockDocCategory.getIsStorehouseLocationEdit());
        }
    }

    public void judgeMove(String inOutMove, String documentMove) {
        ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation = movementTypeRelationMapper.selectOne(new QueryWrapper<ConInoutDocumentMovementTypeRelation>().lambda()
                .eq(ConInoutDocumentMovementTypeRelation::getInOutMovementTypeCode, inOutMove)
                .eq(ConInoutDocumentMovementTypeRelation::getDocumentMovementTypeCode, documentMove)
        );
        if (conInoutDocumentMovementTypeRelation == null) {
            throw new CustomException("出入库作业类型与单据作业类型不匹配");
        }
    }

    public void setItemNum(InvInventoryDocument invInventoryDocument) {
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtil.isNotEmpty(invInventoryDocumentItemList)) {
            setItemNum(invInventoryDocumentItemList);
        }
    }

    /**
     * 查看库存价
     */
    public void getPriceInv(InvInventoryDocument invInventoryDocument) {
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
            invInventoryDocumentItemList.forEach(item -> {
                if (item.getPrice() == null) {
                    InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                            .eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
                            .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid()));
                    if (location != null) {
                        item.setLocationPrice(location.getPrice());
                    }
                }
            });
        }
    }

    /**
     * 复制
     */
    @Override
    public InvInventoryDocument getCopy(Long sid) {
        InvInventoryDocument invInventoryDocument = selectInvInventoryDocumentById(sid);
        invInventoryDocument.setInventoryDocumentSid(null)
                .setInventoryDocumentCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setStorehouseOperator(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvInventoryDocumentItem> list = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                item.setCreateDate(null)
                        .setCreatorAccount(null)
                        .setInventoryDocumentSid(null)
                        .setInventoryDocumentItemSid(null)
                        .setRemark(null);
            });
        }
        return invInventoryDocument;
    }

    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private IPurPurchasePriceService purchasePriceService;

    /**
     * 出入库添加明细行时获取价格回传前端
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    public List<InvInventoryDocumentItem> setInvInventoryDocumentItemPrice(InvInventoryDocument invInventoryDocument) {
        List<InvInventoryDocumentItem> response = invInventoryDocument.getInvInventoryDocumentItemList();
        boolean doit = false;
        InvInventoryDocumentItem documentItem = new InvInventoryDocumentItem();
        // 作业类型为“其它入库-常规/自采”（SR30） 或者 “其它出库-常规/自采”（SC30）时
        if (ConstantsInventory.MOVEMENT_TYPE_CODE_SR30.equals(invInventoryDocument.getMovementType()) ||
                ConstantsInventory.MOVEMENT_TYPE_CODE_SC30.equals(invInventoryDocument.getMovementType())) {
            //出库基本信息的业务标识是“客户“ 或者 入库基本信息的业务标识是“采购”
            if ((ConstantsInventory.BUSINESS_FLAG_XS.equals(invInventoryDocument.getBusinessFlag()) && invInventoryDocument.getCustomerSid() != null)
                    || (ConstantsInventory.BUSINESS_FLAG_CG.equals(invInventoryDocument.getBusinessFlag())) && invInventoryDocument.getVendorSid() != null) {
                if (CollectionUtil.isNotEmpty(response)) {
                    // 出库
                    if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())) {
                        documentItem.setCustomerSid(invInventoryDocument.getCustomerSid());
                        doit = true;
                    }
                    // 入库
                    else if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())) {
                        documentItem.setVendorSid(invInventoryDocument.getVendorSid());
                        doit = true;
                    }
                }
            }
        }
        if (doit) {
            getRecentlyNewPrice(invInventoryDocument, response);
            // 2024年5月23号前的逻辑
            // setLastInvPrice(invInventoryDocument, documentItem, response);
        }
        return response;
    }

    public void getRecentlyNewPrice(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> response) {
        // 出库
        if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())) {
            // 调整后获取价格逻辑：（新写1个价格获取接口，不要改动原价格获取接口）
            // 根据“客户、商品/物料编码、客供料方式=无/供方全包料、销售模式=常规/买断”，获取当前生效的“销售价(含税)”的值，带到“价格(含税)”清单列中（如查不到数据，则“价格(含税)”默认为空）
            // PS：新的价格获取接口，可以参照当前销售订单中获取销售价的接口，只是“客供料方式、销售模式”字段是固定值
            SalSalePrice salSalePrice = new SalSalePrice();
            salSalePrice.setSaleMode(ConstantsEms.DOCUMNET_TYPE_ZG)
                    .setRawMaterialMode(ConstantsEms.RAW_MATERIAL_MODE_WU)
                    .setCustomerSid(invInventoryDocument.getCustomerSid());
            for (InvInventoryDocumentItem item : response) {
                salSalePrice.setSku1Sid(item.getSku1Sid())
                        .setSku2Sid(item.getSku2Sid())
                        .setMaterialSid(item.getMaterialSid());
                SalSalePriceItem price = salSalePriceService.getNewSalePrice(salSalePrice);
                if (price != null && price.getSalePriceTax() != null) {
                    item.setPrice(price.getSalePriceTax());
                }
            }
        }
        // 入库
        else if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())) {
            // 调整后获取价格逻辑：（新写1个价格获取接口，不要改动原价格获取接口）
            // 根据“供应商、商品/物料编码、甲供料方式=无/供方全包料、采购模式=常规/买断”，获取当前生效的“采购价(含税)”的值，带到“价格(含税)”清单列中（如查不到数据，则“价格(含税)”默认为空）
            // 新的价格获取接口，可以参照当前采购订单中获取采购价的接口，只是“甲供料方式、采购模式”字段是固定值
            PurPurchasePrice purchasePrice = new PurPurchasePrice();
            purchasePrice.setPurchaseMode(ConstantsEms.DOCUMNET_TYPE_ZG)
                    .setRawMaterialMode(ConstantsEms.RAW_MATERIAL_MODE_WU)
                    .setVendorSid(invInventoryDocument.getVendorSid());
            for (InvInventoryDocumentItem item : response) {
                purchasePrice.setSku1Sid(item.getSku1Sid())
                        .setSku2Sid(item.getSku2Sid())
                        .setMaterialSid(item.getMaterialSid());
                PurPurchasePriceItem price = purchasePriceService.getNewPurchase(purchasePrice);
                if (price != null && price.getPurchasePriceTax() != null) {
                    item.setPrice(price.getPurchasePriceTax());
                }
            }
        }
    }

    /**
     * 2024年5月23号前的逻辑 获取价格
     */
    public void setLastInvPrice(InvInventoryDocument invInventoryDocument, InvInventoryDocumentItem documentItem,
                                List<InvInventoryDocumentItem> response) {
        // 根据“业务标识、供应商、物料/商品条码”从库存凭证明细表（s_inv_inventory_document_item）中获取最近一笔的入库的价格显示到“明细页签”的“价格(含税)”列中
        documentItem.setDocumentCategory(invInventoryDocument.getDocumentCategory())
                .setBusinessFlag(invInventoryDocument.getBusinessFlag());
        documentItem.setPageNum(1).setPageSize(1);
        documentItem.setPageBegin(1);
        for (InvInventoryDocumentItem item : response) {
            documentItem.setBarcodeSid(item.getBarcodeSid());
            List<InvInventoryDocumentItem> list = invInventoryDocumentItemMapper.getInvInventoryDocumentItemNewPrice(documentItem);
            if (CollectionUtil.isNotEmpty(list)) {
                item.setPrice(list.get(0).getPrice());
            }
        }
    }

    /**
     * 出入库明细获取库存量回传前端
     *
     * @param invInventoryDocument 库存凭证明细
     * @return 结果
     */
    @Override
    public List<InvInventoryDocumentItem> getItemUnlimitedQuantity(InvInventoryDocument invInventoryDocument) {
        List<InvInventoryDocumentItem> response = invInventoryDocument.getInvInventoryDocumentItemList();
        // 循环设置 明细行的库位查询条件
        List<InvInventoryDocumentItem> storeLoc = response.stream().filter(item -> item.getStorehouseLocationSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(response)) {
            // 若订单的单据类型是采购退货寄售，则要走供应商寄售的获取库存量
            if (ConstantsOrder.ORDER_DOC_TYPE_RCPO.equals(invInventoryDocument.getSaleAndPurchaseDocument())) {
                invInventoryDocument.setSpecialStock(ConstantsInventory.SPECIAL_STOCK_GJS);
            }
            // 若特殊库存为空，根据“仓库、库位、商品条码”从表（s_inv_inventory_location）中获取“数量”（unlimited_quantity），显示到“库存量”列
            if (invInventoryDocument.getSpecialStock() == null) {
                QueryWrapper<InvInventoryLocation> queryWrapper = new QueryWrapper<InvInventoryLocation>();
                if (invInventoryDocument.getStorehouseSid() == null) {
                    queryWrapper.lambda().isNull(InvInventoryLocation::getStorehouseSid);
                }
                else {
                    queryWrapper.lambda().eq(InvInventoryLocation::getStorehouseSid, invInventoryDocument.getStorehouseSid());
                }
                // 循环设置 明细行的库位查询条件
                if (CollectionUtil.isNotEmpty(storeLoc)) {
                    queryWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : storeLoc) {
                            warpper.or().eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid());
                        }
                    });
                }
                // 循环设置 商品条码查询条件
                queryWrapper.lambda().and(warpper ->{
                    for (InvInventoryDocumentItem item : response) {
                        warpper.or().eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid());
                    }
                });
                List<InvInventoryLocation> locationList = invInventoryLocationMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(locationList)) {
                    // 设置为map, 用商品条码匹配出 对应的 库存量
                    Map<String, InvInventoryLocation> map = locationList.stream().filter(o ->o .getBarcodeSid() != null)
                            .collect(Collectors.toMap(d -> String.valueOf(d.getBarcodeSid()) + "-" + String.valueOf(d.getStorehouseLocationSid()),
                                    Function.identity(), (d1,d2) -> d1));
                    if (map != null && map.size() != 0) {
                        for (InvInventoryDocumentItem item : response) {
                            item.setUnlimitedQuantity(null);
                            if (map.containsKey(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))) {
                                item.setUnlimitedQuantity(map.get(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))
                                        .getUnlimitedQuantity());
                            }
                        }
                    }
                }
                else {
                    for (InvInventoryDocumentItem item : response) {
                        item.setUnlimitedQuantity(null);
                    }
                }
            }
            // 若特殊库存为“甲供料/供应商寄售”，根据“仓库、库位、特殊库存、供应商、商品条码”从表（s_inv_ven_special_inventory）中获取“数量”（unlimited_quantity），显示到“库存量”列
            else if (ConstantsInventory.SPECIAL_STOCK_JGL.equals(invInventoryDocument.getSpecialStock()) ||
                    ConstantsInventory.SPECIAL_STOCK_GJS.equals(invInventoryDocument.getSpecialStock())) {
                QueryWrapper<InvVenSpecialInventory> queryWrapper = new QueryWrapper<InvVenSpecialInventory>();
                if (invInventoryDocument.getStorehouseSid() == null) {
                    queryWrapper.lambda().isNull(InvVenSpecialInventory::getStorehouseSid);
                }
                else {
                    queryWrapper.lambda().eq(InvVenSpecialInventory::getStorehouseSid, invInventoryDocument.getStorehouseSid());
                }
                // 循环设置 明细行的库位查询条件
                if (CollectionUtil.isNotEmpty(storeLoc)) {
                    queryWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : storeLoc) {
                            warpper.or().eq(InvVenSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid());
                        }
                    });
                }
                if (invInventoryDocument.getVendorSid() == null) {
                    queryWrapper.lambda().isNull(InvVenSpecialInventory::getVendorSid);
                }
                else {
                    queryWrapper.lambda().eq(InvVenSpecialInventory::getVendorSid, invInventoryDocument.getVendorSid());
                }
                queryWrapper.lambda().eq(InvVenSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock());
                // 循环设置 商品条码查询条件
                queryWrapper.lambda().and(warpper ->{
                    for (InvInventoryDocumentItem item : response) {
                        warpper.or().eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid());
                    }
                });
                List<InvVenSpecialInventory> locationList = invVenSpecialInventoryMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(locationList)) {
                    // 设置为map, 用商品条码匹配出 对应的 库存量
                    Map<String, InvVenSpecialInventory> map = locationList.stream().filter(o ->o .getBarcodeSid() != null)
                            .collect(Collectors.toMap(d -> String.valueOf(d.getBarcodeSid()) + "-" + String.valueOf(d.getStorehouseLocationSid()),
                                    Function.identity(), (d1,d2) -> d1));
                    if (map != null && map.size() != 0) {
                        for (InvInventoryDocumentItem item : response) {
                            item.setUnlimitedQuantity(null);
                            if (map.containsKey(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))) {
                                item.setUnlimitedQuantity(map.get(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))
                                        .getUnlimitedQuantity());
                            }
                        }
                    }
                }
                else {
                    for (InvInventoryDocumentItem item : response) {
                        item.setUnlimitedQuantity(null);
                    }
                }
            }
            // 若特殊库存为“客供料/客户寄售”，根据“仓库、库位、特殊库存、客户、商品条码”从表（s_inv_cus_special_inventory）中获取“数量”（unlimited_quantity），显示到“库存量”列
            else if (ConstantsInventory.SPECIAL_STOCK_KGL.equals(invInventoryDocument.getSpecialStock()) ||
                    ConstantsInventory.SPECIAL_STOCK_KJS.equals(invInventoryDocument.getSpecialStock())) {
                QueryWrapper<InvCusSpecialInventory> queryWrapper = new QueryWrapper<InvCusSpecialInventory>();
                if (invInventoryDocument.getStorehouseSid() == null) {
                    queryWrapper.lambda().isNull(InvCusSpecialInventory::getStorehouseSid);
                }
                else {
                    queryWrapper.lambda().eq(InvCusSpecialInventory::getStorehouseSid, invInventoryDocument.getStorehouseSid());
                }
                // 循环设置 明细行的库位查询条件
                if (CollectionUtil.isNotEmpty(storeLoc)) {
                    queryWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : storeLoc) {
                            warpper.or().eq(InvCusSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid());
                        }
                    });
                }
                if (invInventoryDocument.getCustomerSid() == null) {
                    queryWrapper.lambda().isNull(InvCusSpecialInventory::getCustomerSid);
                }
                else {
                    queryWrapper.lambda().eq(InvCusSpecialInventory::getCustomerSid, invInventoryDocument.getCustomerSid());
                }
                queryWrapper.lambda().eq(InvCusSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock());
                // 循环设置 商品条码查询条件
                queryWrapper.lambda().and(warpper ->{
                    for (InvInventoryDocumentItem item : response) {
                        warpper.or().eq(InvCusSpecialInventory::getBarcodeSid, item.getBarcodeSid());
                    }
                });
                List<InvCusSpecialInventory> locationList = invCusSpecialInventoryMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(locationList)) {
                    // 设置为map, 用商品条码匹配出 对应的 库存量
                    Map<String, InvCusSpecialInventory> map = locationList.stream().filter(o ->o .getBarcodeSid() != null)
                            .collect(Collectors.toMap(d -> String.valueOf(d.getBarcodeSid()) + "-" + String.valueOf(d.getStorehouseLocationSid()),
                                    Function.identity(), (d1,d2) -> d1));
                    if (map != null && map.size() != 0) {
                        for (InvInventoryDocumentItem item : response) {
                            item.setUnlimitedQuantity(null);
                            if (map.containsKey(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))) {
                                item.setUnlimitedQuantity(map.get(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))
                                        .getUnlimitedQuantity());
                            }
                        }
                    }
                }
                else {
                    for (InvInventoryDocumentItem item : response) {
                        item.setUnlimitedQuantity(null);
                    }
                }
            }
            return response;
        }
        else {
            return new ArrayList<>();
        }
    }

    /**
     * 多作业类型出库  按明细获取库存量
     *
     * @param item 库存凭证明细
     * @return 结果
     */
    @Override
    public List<InvInventoryDocumentItem> getItemUnlimitedQuantityBymovementType(List<InvInventoryDocumentItem> response) {
        // 循环设置 明细行的库位查询条件
        List<InvInventoryDocumentItem> storeLoc = response.stream().filter(item -> item.getStorehouseLocationSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(response)) {
            // 若特殊库存为空，根据“仓库、库位、商品条码”从表（s_inv_inventory_location）中获取“数量”（unlimited_quantity），显示到“库存量”列
            if (response.get(0).getSpecialStock() == null) {
                QueryWrapper<InvInventoryLocation> queryWrapper = new QueryWrapper<InvInventoryLocation>();
                if (response.get(0).getStorehouseSid() == null) {
                    queryWrapper.lambda().isNull(InvInventoryLocation::getStorehouseSid);
                }
                else {
                    queryWrapper.lambda().eq(InvInventoryLocation::getStorehouseSid, response.get(0).getStorehouseSid());
                }
                // 循环设置 明细行的库位查询条件
                if (CollectionUtil.isNotEmpty(storeLoc)) {
                    queryWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : storeLoc) {
                            warpper.or().eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid());
                        }
                    });
                }
                // 循环设置 商品条码查询条件
                queryWrapper.lambda().and(warpper ->{
                    for (InvInventoryDocumentItem item : response) {
                        warpper.or().eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid());
                    }
                });
                List<InvInventoryLocation> locationList = invInventoryLocationMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(locationList)) {
                    // 设置为map, 用商品条码匹配出 对应的 库存量
                    Map<String, InvInventoryLocation> map = locationList.stream().filter(o ->o .getBarcodeSid() != null)
                            .collect(Collectors.toMap(d -> String.valueOf(d.getBarcodeSid()) + "-" + String.valueOf(d.getStorehouseLocationSid()),
                                    Function.identity(), (d1,d2) -> d1));
                    if (map != null && map.size() != 0) {
                        for (InvInventoryDocumentItem item : response) {
                            item.setUnlimitedQuantity(null);
                            if (map.containsKey(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))) {
                                item.setUnlimitedQuantity(map.get(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))
                                        .getUnlimitedQuantity());
                            }
                        }
                    }
                }
                else {
                    for (InvInventoryDocumentItem item : response) {
                        item.setUnlimitedQuantity(null);
                    }
                }
            }
            // 若特殊库存为“甲供料/供应商寄售”，根据“仓库、库位、特殊库存、供应商、商品条码”从表（s_inv_ven_special_inventory）中获取“数量”（unlimited_quantity），显示到“库存量”列
            else if (ConstantsInventory.SPECIAL_STOCK_JGL.equals(response.get(0).getSpecialStock()) ||
                    ConstantsInventory.SPECIAL_STOCK_GJS.equals(response.get(0).getSpecialStock())) {
                QueryWrapper<InvVenSpecialInventory> queryWrapper = new QueryWrapper<InvVenSpecialInventory>();
                if (response.get(0).getStorehouseSid() == null) {
                    queryWrapper.lambda().isNull(InvVenSpecialInventory::getStorehouseSid);
                }
                else {
                    queryWrapper.lambda().eq(InvVenSpecialInventory::getStorehouseSid, response.get(0).getStorehouseSid());
                }
                // 循环设置 明细行的库位查询条件
                if (CollectionUtil.isNotEmpty(storeLoc)) {
                    queryWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : storeLoc) {
                            warpper.or().eq(InvVenSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid());
                        }
                    });
                }
                if (response.get(0).getVendorSid() == null) {
                    queryWrapper.lambda().isNull(InvVenSpecialInventory::getVendorSid);
                }
                else {
                    queryWrapper.lambda().eq(InvVenSpecialInventory::getVendorSid, response.get(0).getVendorSid());
                }
                queryWrapper.lambda().eq(InvVenSpecialInventory::getSpecialStock, response.get(0).getSpecialStock());
                // 循环设置 商品条码查询条件
                queryWrapper.lambda().and(warpper ->{
                    for (InvInventoryDocumentItem item : response) {
                        warpper.or().eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid());
                    }
                });
                List<InvVenSpecialInventory> locationList = invVenSpecialInventoryMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(locationList)) {
                    // 设置为map, 用商品条码匹配出 对应的 库存量
                    Map<String, InvVenSpecialInventory> map = locationList.stream().filter(o ->o .getBarcodeSid() != null)
                            .collect(Collectors.toMap(d -> String.valueOf(d.getBarcodeSid()) + "-" + String.valueOf(d.getStorehouseLocationSid()),
                                    Function.identity(), (d1,d2) -> d1));
                    if (map != null && map.size() != 0) {
                        for (InvInventoryDocumentItem item : response) {
                            item.setUnlimitedQuantity(null);
                            if (map.containsKey(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))) {
                                item.setUnlimitedQuantity(map.get(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))
                                        .getUnlimitedQuantity());
                            }
                        }
                    }
                }
                else {
                    for (InvInventoryDocumentItem item : response) {
                        item.setUnlimitedQuantity(null);
                    }
                }
            }
            // 若特殊库存为“客供料/客户寄售”，根据“仓库、库位、特殊库存、客户、商品条码”从表（s_inv_cus_special_inventory）中获取“数量”（unlimited_quantity），显示到“库存量”列
            else if (ConstantsInventory.SPECIAL_STOCK_KGL.equals(response.get(0).getSpecialStock()) ||
                    ConstantsInventory.SPECIAL_STOCK_KJS.equals(response.get(0).getSpecialStock())) {
                QueryWrapper<InvCusSpecialInventory> queryWrapper = new QueryWrapper<InvCusSpecialInventory>();
                if (response.get(0).getStorehouseSid() == null) {
                    queryWrapper.lambda().isNull(InvCusSpecialInventory::getStorehouseSid);
                }
                else {
                    queryWrapper.lambda().eq(InvCusSpecialInventory::getStorehouseSid, response.get(0).getStorehouseSid());
                }
                // 循环设置 明细行的库位查询条件
                if (CollectionUtil.isNotEmpty(storeLoc)) {
                    queryWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : storeLoc) {
                            warpper.or().eq(InvCusSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid());
                        }
                    });
                }
                if (response.get(0).getCustomerSid() == null) {
                    queryWrapper.lambda().isNull(InvCusSpecialInventory::getCustomerSid);
                }
                else {
                    queryWrapper.lambda().eq(InvCusSpecialInventory::getCustomerSid, response.get(0).getCustomerSid());
                }
                queryWrapper.lambda().eq(InvCusSpecialInventory::getSpecialStock, response.get(0).getSpecialStock());
                // 循环设置 商品条码查询条件
                queryWrapper.lambda().and(warpper ->{
                    for (InvInventoryDocumentItem item : response) {
                        warpper.or().eq(InvCusSpecialInventory::getBarcodeSid, item.getBarcodeSid());
                    }
                });
                List<InvCusSpecialInventory> locationList = invCusSpecialInventoryMapper.selectList(queryWrapper);
                if (CollectionUtil.isNotEmpty(locationList)) {
                    // 设置为map, 用商品条码匹配出 对应的 库存量
                    Map<String, InvCusSpecialInventory> map = locationList.stream().filter(o ->o .getBarcodeSid() != null)
                            .collect(Collectors.toMap(d -> String.valueOf(d.getBarcodeSid()) + "-" + String.valueOf(d.getStorehouseLocationSid()),
                                    Function.identity(), (d1,d2) -> d1));
                    if (map != null && map.size() != 0) {
                        for (InvInventoryDocumentItem item : response) {
                            item.setUnlimitedQuantity(null);
                            if (map.containsKey(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))) {
                                item.setUnlimitedQuantity(map.get(String.valueOf(item.getBarcodeSid()) + "-" + String.valueOf(item.getStorehouseLocationSid()))
                                        .getUnlimitedQuantity());
                            }
                        }
                    }
                }
                else {
                    for (InvInventoryDocumentItem item : response) {
                        item.setUnlimitedQuantity(null);
                    }
                }
            }
            return response;
        }
        else {
            return null;
        }
    }

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    public EmsResultEntity insertVerifyInvInventoryDocument(InvInventoryDocument invInventoryDocument) {
        // 若所选业务单号的“是否生成财务应收/应付暂估流水”为“是”，要校验明细的税率是否为空，若明细的税率为空，提示：存在明细的税率为空，无法出库！
        if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())
                && CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
            boolean flag = invInventoryDocument.getInvInventoryDocumentItemList().stream().anyMatch(o->o.getOrderTaxRate() == null
                    && !ConstantsEms.YES.equals(o.getOrderFreeFlag())
                    && (ConstantsEms.YES.equals(o.getIsFinanceBookYfzg()) || ConstantsEms.YES.equals(o.getIsFinanceBookYszg())));
            if (flag) {
                throw new BaseException("存在明细的税率为空，无法出库！");
            }
        }
        // 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑
        // insertInvInventoryDocumentVerify(invInventoryDocument);
        // 出库 且 业务单据类别为 无 时校验本单的明细 是否重复
        EmsResultEntity uni = insertInvInventoryDocumentVerifyItem(invInventoryDocument);
        if (!EmsResultEntity.SUCCESS_TAG.equals(uni.getTag())) {
            return uni;
        }
        return EmsResultEntity.success();
    }

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑 ( 多作业类型出库 )
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    public EmsResultEntity insertVerifyInvInventoryDocumentByMovementType(InvInventoryDocument invInventoryDocument) {
        if ((ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory()) ||
                ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory()))
                && ConstantsInventory.WU.equals(invInventoryDocument.getReferDocCategory())) {
            if (CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
                Map<String, List<InvInventoryDocumentItem>> map = invInventoryDocument.getInvInventoryDocumentItemList().stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getBarcodeSid())+"-"+String.valueOf(o.getMovementType())+"-"+String.valueOf(o.getSpecialStock())
                                +"-"+String.valueOf(o.getVendorSid())+"-"+String.valueOf(o.getCustomerSid())+"-"+String.valueOf(o.getStorehouseLocationSid())));
                if (map.size() != invInventoryDocument.getInvInventoryDocumentItemList().size()) {
                    if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())) {
                        return EmsResultEntity.warning(null, "明细中存在重复的“作业类型+特殊库存+供应商+客户+库位+编码+SKU1+SKU2”的物料，是否继续入库？");
                    }
                    else {
                        return EmsResultEntity.warning(null, "明细中存在重复的“作业类型+特殊库存+供应商+客户+库位+编码+SKU1+SKU2”的物料，是否继续出库？");
                    }
                }
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public EmsResultEntity insertInvInventoryDocumentVerify(InvInventoryDocument invInventoryDocument) {
        SysDefaultSettingClient client = sysDefaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
        if (client != null) {
            // 出库  且 作业类型为“其它出库-常规/自采”（SC30）时
            if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory()) &&
                    ConstantsInventory.MOVEMENT_TYPE_CODE_SC30.equals(invInventoryDocument.getMovementType())) {
                // 业务标识为“采购退货”（CGTH）
                if (ConstantsInventory.BUSINESS_FLAG_CGTH.equals(invInventoryDocument.getBusinessFlag())) {
                    if (invInventoryDocument.getVendorSid() == null) {
                        // 必填
                        if (ConstantsEms.DATA_ENTER_REQUEST_BT.equals(client.getVendorEnterRequestOutStockOther())) {
                            return EmsResultEntity.error(null, "供应商不能为空！");
                        }
                        //提示
                        else if (ConstantsEms.DATA_ENTER_REQUEST_TS.equals(client.getVendorEnterRequestOutStockOther())) {
                            return EmsResultEntity.warning(null, "供应商为空，是否继续入库？");
                        } else {
                        }
                    }
                }
                // 业务标识为“销售”（XS）
                else if (ConstantsInventory.BUSINESS_FLAG_XS.equals(invInventoryDocument.getBusinessFlag())) {
                    if (invInventoryDocument.getCustomerSid() == null) {
                        // 必填
                        if (ConstantsEms.DATA_ENTER_REQUEST_BT.equals(client.getCustomerEnterRequestOutStockOther())) {
                            return EmsResultEntity.error(null, "客户不能为空！");
                        }
                        //提示
                        else if (ConstantsEms.DATA_ENTER_REQUEST_TS.equals(client.getCustomerEnterRequestOutStockOther())) {
                            return EmsResultEntity.warning(null, "客户为空，是否继续出库？");
                        } else {
                        }
                    }
                }
            }
            // 入库  且 作业类型为“其它入库-常规/自采”（SR30）时
            else if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory()) &&
                    ConstantsInventory.MOVEMENT_TYPE_CODE_SR30.equals(invInventoryDocument.getMovementType())) {
                // 业务标识为“采购”（CG）
                if (ConstantsInventory.BUSINESS_FLAG_CG.equals(invInventoryDocument.getBusinessFlag())) {
                    if (invInventoryDocument.getVendorSid() == null) {
                        // 必填
                        if (ConstantsEms.DATA_ENTER_REQUEST_BT.equals(client.getVendorEnterRequestInStockOther())) {
                            return EmsResultEntity.error(null, "供应商不能为空！");
                        }
                        //提示
                        else if (ConstantsEms.DATA_ENTER_REQUEST_TS.equals(client.getVendorEnterRequestInStockOther())) {
                            return EmsResultEntity.warning(null, "供应商为空，是否继续入库？");
                        } else {
                        }
                    }
                }
                // 业务标识为“销售退货”（XSTH）
                else if (ConstantsInventory.BUSINESS_FLAG_XSTH.equals(invInventoryDocument.getBusinessFlag())) {
                    if (invInventoryDocument.getVendorSid() == null) {
                        // 必填
                        if (ConstantsEms.DATA_ENTER_REQUEST_BT.equals(client.getCustomerEnterRequestInStockOther())) {
                            return EmsResultEntity.error(null, "客户不能为空！");
                        }
                        //提示
                        else if (ConstantsEms.DATA_ENTER_REQUEST_TS.equals(client.getCustomerEnterRequestInStockOther())) {
                            return EmsResultEntity.warning(null, "客户为空，是否继续入库？");
                        } else {
                        }
                    }
                }
            } else {
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 出库 入库 且 业务单据类别为 无 时校验本单的明细 是否重复
     * 点击出库时，进行如下校验：
     * 通过”商品条码 +款颜色+款尺码“校重，若该组合已存在，提示：明细中存在重复的“编码+款备注+SKU1+SKU2”的物料，是否继续出库？
     * 点击是则继续执行出库，点击否则关闭提示框
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public EmsResultEntity insertInvInventoryDocumentVerifyItem(InvInventoryDocument invInventoryDocument) {
        if ((ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory()) ||
                ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory()))
                && ConstantsInventory.WU.equals(invInventoryDocument.getReferDocCategory())) {
            if (CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
                if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())) {
                    Map<Long, List<InvInventoryDocumentItem>> map = invInventoryDocument.getInvInventoryDocumentItemList().stream()
                            .collect(Collectors.groupingBy(InvInventoryDocumentItem::getBarcodeSid));
                    if (map.size() != invInventoryDocument.getInvInventoryDocumentItemList().size()) {
                        return EmsResultEntity.warning(null, "明细中存在重复的“编码+SKU1+SKU2”的物料，是否继续入库？");
                    }
                } else {
                    Map<String, List<InvInventoryDocumentItem>> map = invInventoryDocument.getInvInventoryDocumentItemList().stream()
                            .collect(Collectors.groupingBy(o -> String.valueOf(o.getBarcodeSid()) + "-" + String.valueOf(o.getStorehouseLocationSid())));
                    if (map.size() != invInventoryDocument.getInvInventoryDocumentItemList().size()) {
                        return EmsResultEntity.warning(null, "明细中存在重复的“库位+编码+SKU1+SKU2”的物料，是否继续出库？");
                    }
                }

            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 校验单号是否重复
     */
    public void judgeCode(InvInventoryDocument invInventoryDocument) {
        Long referDocumentSid = invInventoryDocument.getReferDocumentSid();
        List<InvInventoryDocument> invInventoryDocuments = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .eq(InvInventoryDocument::getReferDocumentSid, referDocumentSid)
                .eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG)
        );
        if (CollectionUtils.isNotEmpty(invInventoryDocuments)) {
            throw new CustomException("库存凭证已存在该业务单号，不允许重复进行出入库");
        }
    }

    /**
     * 校验单号的处理状态是否是确认状态
     */
    public void judgeStatus(InvInventoryDocument invInventoryDocument) {
        String handleStatus = invInventoryDocument.getHandleStatus();
        if (!handleStatus.equals(ConstantsEms.CHECK_STATUS)) {
            throw new CustomException("仅确认状态下，才允许进行出入库操作");
        }
    }

    public void setBussiness(InvInventoryDocument invInventoryDocument, SysBusinessBcst sysBusinessBcst) {
        sysBusinessBcst.setDocumentSid(invInventoryDocument.getReferDocumentSid())
                .setDocumentCode(invInventoryDocument.getReferDocumentCode())
                .setNoticeDate(new Date());
    }

    private void businessBcst(SysBusinessBcst sysBusinessBcst, String name) {
        if (StrUtil.isNotBlank(name)) {
            SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                    .eq(SysUser::getUserName, name)
                    .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (sysUser != null) {
                sysBusinessBcst.setUserId(sysUser.getUserId());
                sysBusinessBcstMapper.insert(sysBusinessBcst);
            }
        }
    }

    /**
     * 调拨单入库量等于出库量
     */
    public void exchange(InvInventoryDocument invInventoryDocument, String type) {
        String code = invInventoryDocument.getMovementType();
        Long referDocumentSid = invInventoryDocument.getReferDocumentSid();
        //入库操作
        if (Adjust(code) && type.equals(ConstantsEms.RU_KU)) {
            InvInventoryDocument document = invInventoryDocumentMapper.selectOne(new QueryWrapper<InvInventoryDocument>().lambda()
                    .eq(InvInventoryDocument::getReferDocumentSid, referDocumentSid)
                    .eq(InvInventoryDocument::getDocumentCategory, DocumentCategory.CHK.getCode())
            );
            if (null == document) {
                throw new CustomException("该调拨单还未出库，无法进行入库操作");
            }
            InvInventoryDocument invDocument = invInventoryDocumentMapper.selectOne(new QueryWrapper<InvInventoryDocument>().lambda()
                    .eq(InvInventoryDocument::getReferDocumentSid, referDocumentSid)
                    .eq(InvInventoryDocument::getDocumentCategory, DocumentCategory.RU.getCode())
            );
            if (null != invDocument) {
                throw new CustomException("该调拨单已入库，无法重复进行入库操作");
            }
            //出库量赋值到入库量
            InvInventoryDocument invInventory = selectInvInventoryDocumentById(document.getInventoryDocumentSid());
            List<InvInventoryDocumentItem> itemListPurpose = invInventory.getInvInventoryDocumentItemList();
            invInventoryDocument.setInvInventoryDocumentItemList(itemListPurpose);
            List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
            invInventoryDocumentItemList.forEach(li -> {
                li.setInventoryDocumentItemSid(null);
                li.setInventoryDocumentSid(null);
            });

        }
        //出库操作
        if (Adjust(code) && type.equals(ConstantsEms.CHU_KU)) {
            InvInventoryDocument document = invInventoryDocumentMapper.selectOne(new QueryWrapper<InvInventoryDocument>().lambda()
                    .eq(InvInventoryDocument::getReferDocumentSid, referDocumentSid)
                    .eq(InvInventoryDocument::getDocumentCategory, DocumentCategory.CHK.getCode())
            );
            if (document != null) {
                throw new CustomException("该调拨单已出库，无法重复进行出库操作");
            }
        }
    }

    /**
     * 获取销售发货单
     */
    public DelDeliveryNote getSaleDelivery(String code, String referDocCategory, String type) {
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        DelDeliveryNote delDeliveryNote = new DelDeliveryNote();
        delDeliveryNote.setDeliveryNoteCode(Long.valueOf(code));
        if (referDocCategory.equals(DocCategory.SALE_RU.getCode()) || referDocCategory.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
            //1是交货   2是发货
            delDeliveryNote.setDeliveryCategory(ConstantsEms.delivery_Category_XS);
        } else {
            delDeliveryNote.setDeliveryCategory(ConstantsEms.delivery_Category_CG);
        }
        List<DelDeliveryNote> delDeliveryNotes = delDeliveryNoteServiceImpl.selectDelDeliveryNoteList(delDeliveryNote);
        if (CollectionUtils.isNotEmpty(delDeliveryNotes)) {
            Long sid = delDeliveryNotes.get(0).getDeliveryNoteSid();
            DelDeliveryNote delDeliveryNoteNew = delDeliveryNoteServiceImpl.selectDelDeliveryNoteById(sid, delDeliveryNote.getDeliveryCategory());
            return delDeliveryNoteNew;
        }
        return null;
    }

    /**
     * 获取可多次出入库默认量
     */
    @Override
    public void getQuantity(InvInventoryDocument invInventoryDocument) {
        List<InvInventoryDocumentItem> list = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getReferDocumentItemSid())
                        .eq(InvInventoryDocumentItem::getBarcodeSid, item.getBarcodeSid()));
                if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                    Long[] docSids = bardcodeItems.stream().map(InvInventoryDocumentItem::getInventoryDocumentSid).toArray(Long[]::new);
                    List<InvInventoryDocument> documentList = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                            .eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                            .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG)
                            .in(InvInventoryDocument::getInventoryDocumentSid, docSids));
                    List<Long> newSids = documentList.stream().map(InvInventoryDocument::getInventoryDocumentSid).collect(toList());
                    bardcodeItems = bardcodeItems.stream().filter(o->newSids.contains(o.getInventoryDocumentSid())).collect(toList());
                }
                BigDecimal sumCode = BigDecimal.ZERO;
                if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                    sumCode = bardcodeItems.stream().map(o -> {
                        if (o.getPriceQuantity() != null) {
                            return o.getPriceQuantity();
                        } else {
                            return o.getQuantity();
                        }
                    }).reduce(BigDecimal.ZERO, BigDecimal::add);
                }

                BigDecimal i = item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity();
                item.setQuantity(i.subtract(sumCode));
            });
            BigDecimal sum = list.stream().map(a -> a.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            String referDocCategory = invInventoryDocument.getReferDocCategory();
            //当默认值为空显示负数
            list.forEach(
                    li -> {
                        if (li.getQuantity().compareTo(BigDecimal.ZERO) == -1) {
                            li.setQuantity(null);
                        }
                    }
            );
            invInventoryDocument.setInvInventoryDocumentItemList(list);
        }
    }

    /**
     * 查询库存凭证
     *
     * @param inventoryDocumentSid 库存凭证ID
     * @return 库存凭证
     */
    @Override
    public InvInventoryDocument selectInvInventoryDocumentById(Long inventoryDocumentSid) {
        //库存凭证详情
        InvInventoryDocument invInventoryDocument = invInventoryDocumentMapper.selectInvInventoryDocumentById(inventoryDocumentSid);
        if (DocCategory.REQUESTION_CHK.getCode().equals(invInventoryDocument.getReferDocCategory()) || DocCategory.REQUESTION_RU.getCode().equals(invInventoryDocument.getReferDocCategory())) {
            invInventoryDocument.setCompanyShortName(null);
        }
        Optional<InvInventoryDocument> documentOptional = Optional.ofNullable(invInventoryDocument);
        documentOptional.ifPresent(document -> {
            //库存凭证-明细list
            List<InvInventoryDocumentItem> invInventoryDocumentItemList =
                    invInventoryDocumentItemMapper.selectInvInventoryDocumentItemById(inventoryDocumentSid);
            // 获取库存量
            if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())) {
                invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItemList);
                invInventoryDocumentItemList = getItemUnlimitedQuantity(invInventoryDocument);
            }
            List<InvInventoryDocumentItem> items = sort(invInventoryDocumentItemList, null);
            invInventoryDocument.setInvInventoryDocumentItemList(items);
            // 入库明细汇总页签
            if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())
                    && CollectionUtil.isNotEmpty(items)) {
                Map<String, List<InvInventoryDocumentItem>> map = new HashMap<>();
                map = invInventoryDocument.getInvInventoryDocumentItemList()
                        .stream().collect(Collectors.groupingBy(
                                o -> String.valueOf(o.getMaterialSid()) + "-" + String.valueOf(o.getSku1Sid())));
                List<InvInventoryDocumentItem> collect = new ArrayList<>();
                for (String key : map.keySet()) {
                    List<InvInventoryDocumentItem> iiList = map.get(key);
                    InvInventoryDocumentItem item = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(iiList.get(0), item);
                    BigDecimal priceQuantity = iiList.stream().map(InvInventoryDocumentItem::getPriceQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                    BigDecimal quantity = iiList.stream().map(InvInventoryDocumentItem::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                    item.setQuantity(quantity);
                    item.setPriceQuantity(priceQuantity);
                    collect.add(item);
                }
                invInventoryDocument.setItemCollect(collect);
            }
            // 出库明细汇总页签
            if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())
                    && CollectionUtil.isNotEmpty(items)) {
                Map<String, List<InvInventoryDocumentItem>> map = new HashMap<>();
                map = invInventoryDocument.getInvInventoryDocumentItemList()
                        .stream().collect(Collectors.groupingBy(
                                o -> String.valueOf(o.getMaterialSid()) + "-" + String.valueOf(o.getSku1Sid())));
                List<InvInventoryDocumentItem> collect = new ArrayList<>();
                for (String key : map.keySet()) {
                    List<InvInventoryDocumentItem> iiList = map.get(key);
                    InvInventoryDocumentItem item = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(iiList.get(0), item);
                    BigDecimal priceQuantity = iiList.stream().map(InvInventoryDocumentItem::getPriceQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                    BigDecimal quantity = iiList.stream().map(InvInventoryDocumentItem::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                    item.setQuantity(quantity);
                    item.setPriceQuantity(priceQuantity);
                    collect.add(item);
                }
                invInventoryDocument.setItemCollect(collect);
            }
            //库存凭证-附件list
            List<InvInventoryDocumentAttach> invInventoryDocumentAttaches = attachMapper.selectInvInventoryDocumentAttachList(new InvInventoryDocumentAttach().setInventoryDocumentSid(inventoryDocumentSid));
            invInventoryDocument.setInvInventoryDocumentAttacList(invInventoryDocumentAttaches);

        });
        String codeBase = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BarcodeUtils.generateBarCode128(String.valueOf(invInventoryDocument.getInventoryDocumentCode()), 10.0, 0.3, true, false, outputStream);
        // 将流转成数组
        // 将流转换成数组
        byte[] bytes1 = outputStream.toByteArray();
        // 进行Base64编码
        String base64Encoded = Base64.getEncoder().encodeToString(bytes1);
        // 构建data URL
        codeBase = "data:image/png;base64," + base64Encoded;
        // 二维码
        // codeBase = QrCodeUtil.generateAsBase64(invInventoryDocument.getInventoryDocumentCode().toString(), QrConfig.create().setWidth(80).setHeight(80).setMargin(0), "png");
        invInventoryDocument.setQrCode(codeBase);
        MongodbUtil.find(invInventoryDocument);
        return invInventoryDocument;
    }

    /**
     * 查询页面更新信息按钮获取信息
     *
     * @param inventoryDocumentSid 库存凭证ID
     * @return 库存凭证
     */
    @Override
    public InvInventoryDocument selectInvInventoryDocumentDetailById(Long inventoryDocumentSid) {
        InvInventoryDocument invInventoryDocument = invInventoryDocumentMapper.selectInvInventoryDocumentDetailById(inventoryDocumentSid);
        if (invInventoryDocument == null) {
            return new InvInventoryDocument();
        }
        return invInventoryDocument;
    }

    /**
     * 查询页面更新信息按钮修改信息
     *
     * @param invInventoryDocument 库存凭证
     * @return 库存凭证
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryDocumentDetailById(InvInventoryDocument invInventoryDocument) {
        int row = 0;
        // 得到原来的数据 做操作日志
        InvInventoryDocument response = selectInvInventoryDocumentDetailById(invInventoryDocument.getInventoryDocumentSid());
        // 设置下单季编码
        if (invInventoryDocument.getProductSeasonSid() != null && !invInventoryDocument.getProductSeasonSid().equals(response.getProductSeasonSid())) {
            setProductSeasonCode(invInventoryDocument);
        }
        // 设置过账年份月份
        if (invInventoryDocument.getAccountDate() != null && !invInventoryDocument.getAccountDate().equals(response.getAccountDate())) {
            setYearMonth(invInventoryDocument);
        }
        row = invInventoryDocumentMapper.updateInvInventoryDocumentDetailById(invInventoryDocument);
        if (row > 0) {
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, invInventoryDocument);
            MongodbDeal.update(invInventoryDocument.getInventoryDocumentSid(), invInventoryDocument.getHandleStatus(),
                    invInventoryDocument.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 移库方式为两步法且入库状态为未入库的移库进行入库
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void yikuInStock(InvInventoryDocument invInventoryDocument) {
        //库存凭证详情
        invInventoryDocument = invInventoryDocumentMapper.selectInvInventoryDocumentById(invInventoryDocument.getInventoryDocumentSid());
        if (invInventoryDocument == null) {
            return;
        }
        if (!ConstantsInventory.STOCK_TRANSFER_MODE_LB.equals(invInventoryDocument.getStockTransferMode())
                || !ConstantsInventory.IN_OUT_STORE_STATUS_WRK.equals(invInventoryDocument.getInOutStockStatus())) {
            throw new BaseException("仅移库方式为”两步法“且入库状态为”未入库“才能进行该操作！");
        }
        List<InvInventoryDocumentItem> invInventoryDocumentItemList =
                invInventoryDocumentItemMapper.selectInvInventoryDocumentItemById(invInventoryDocument.getInventoryDocumentSid());
        //变更前仓库信息
        Map<Long, Object> oldLocation = new HashMap<>();
        //出入库类型改成入库
        invInventoryDocument.setStorehouseSid(invInventoryDocument.getDestStorehouseSid());
        invInventoryDocument.setType("2");
        invInventoryDocumentItemList.forEach(item->{
            item.setStorehouseSid(item.getDestStorehouseSid())
                    .setStorehouseCode(item.getDestStorehouseCode())
                    .setStorehouseLocationSid(item.getDestStorehouseLocationSid())
                    .setLocationCode(item.getDestLocationCode());
        });
        LocationRU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        // 对应的库存凭证号的在途库存清零
        invIntransitInventoryMapper.update(null, new UpdateWrapper<InvIntransitInventory>().lambda()
                .eq(InvIntransitInventory::getInventoryDocumentSid, invInventoryDocument.getInventoryDocumentSid())
                .set(InvIntransitInventory::getUnlimitedQuantity, BigDecimal.ZERO));
        // 更新为全部入库
        invInventoryDocumentMapper.update(null, new UpdateWrapper<InvInventoryDocument>().lambda()
                .eq(InvInventoryDocument::getInventoryDocumentSid, invInventoryDocument.getInventoryDocumentSid())
                .set(InvInventoryDocument::getInOutStockStatus, ConstantsInventory.IN_OUT_STORE_STATUS_QBRK));
        // 删除相关待办
        String title = "移库单" +
                invInventoryDocument.getInventoryDocumentCode() + "的物料/商品已从" +
                invInventoryDocument.getStorehouseName() + "仓库出库，请及时入库";
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                .eq(SysTodoTask::getDocumentSid, invInventoryDocument.getInventoryDocumentSid())
                .eq(SysTodoTask::getTitle, title));
        MongodbUtil.insertUserLog(invInventoryDocument.getInventoryDocumentSid(), ConstantsInventory.DOCUMENT_CATEGORY_IN, null, "移库");
    }

    /**
     * 仓库物料信息表设置使用频率
     *
     * @param invStorehouseMaterialList 仓库物料信息表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int storehouseMaterialSetUsage(List<InvStorehouseMaterial> invStorehouseMaterialList, String usageFrequencyFlag) {
        int row = 0;
        if (CollectionUtil.isEmpty(invStorehouseMaterialList)) {
            return row;
        }
        Iterator<InvStorehouseMaterial> iterator = invStorehouseMaterialList.iterator();
        while (iterator.hasNext()) {
            InvStorehouseMaterial cur = iterator.next();
            // 如果没有 仓库sid 和 商品条码 sid 则跳过这笔
            if (cur.getStorehouseSid() == null || cur.getBarcodeSid() == null) {
                iterator.remove();
            } else {
                cur.setUsageFrequencyFlag(usageFrequencyFlag)
                        .setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            }
        }
        if (CollectionUtil.isNotEmpty(invStorehouseMaterialList)) {
            row = invStorehouseMaterialMapper.updatesUsageFrequencyFlag(invStorehouseMaterialList);
        }
        return row;
    }

    /**
     * 查询库存凭证列表
     *
     * @param invInventoryDocument 库存凭证
     * @return 库存凭证
     */
    @Override
    public List<InvInventoryDocument> selectInvInventoryDocumentList(InvInventoryDocument invInventoryDocument) {
        ArrayList<InvInventoryDocumentCodeRequest> list = new ArrayList<>();
        exchangeCode(invInventoryDocument, list);
        return invInventoryDocumentMapper.selectInvInventoryDocumentList(invInventoryDocument);
    }

    /**
     * 各种订单号转化
     */
    public void exchangeCode(InvInventoryDocument invInventoryDocument, ArrayList<InvInventoryDocumentCodeRequest> list) {
        //采购订单号
        String purchaseOrderCode = invInventoryDocument.getPurchaseOrderCode();
        if (purchaseOrderCode != null) {
            String[] purchaseOrderList = purchaseOrderCode.split(";");
            List<String> items = Arrays.asList(purchaseOrderList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.PURCHASE_ORDER.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //退货销售订单号
        String comebackOrderCode = invInventoryDocument.getComebackSaleCode();
        if (comebackOrderCode != null) {
            String[] comebackOrderList = comebackOrderCode.split(";");
            List<String> items = Arrays.asList(comebackOrderList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.RETURN_BACK_SALE.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //采购交货单号
        String deliveryPurchaseOrderCode = invInventoryDocument.getDeliveryPurchaseCode();
        if (deliveryPurchaseOrderCode != null) {
            String[] deliveryPurchaseOrderList = deliveryPurchaseOrderCode.split(";");
            List<String> items = Arrays.asList(deliveryPurchaseOrderList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.SALE_CHK.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //生产订单号
        String manufactureOrderCode = invInventoryDocument.getManufactureOrderCode();
        if (manufactureOrderCode != null) {
            String[] manufactureOrderList = manufactureOrderCode.split(";");
            List<String> items = Arrays.asList(manufactureOrderList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.PRODUCTION_ORDER.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //退料单号
        String materialsReturnedCode = invInventoryDocument.getMaterialsReturnedCode();
        if (materialsReturnedCode != null) {
            String[] materialsReturnedCodeList = materialsReturnedCode.split(";");
            List<String> items = Arrays.asList(materialsReturnedCodeList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.REQUESTION_RU.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //领料单号
        String materialRequisitionCode = invInventoryDocument.getMaterialRequisitionCode();
        if (materialRequisitionCode != null) {
            String[] materialRequisitionList = materialRequisitionCode.split(";");
            List<String> items = Arrays.asList(materialRequisitionList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.REQUESTION_CHK.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //销售订单号
        String salesOrderCode = invInventoryDocument.getSalesOrderCode();
        if (salesOrderCode != null) {
            String[] salesOrderCodeList = salesOrderCode.split(";");
            List<String> items = Arrays.asList(salesOrderCodeList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.SALE_ORDER.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //采购退货订单号
        String comebackPurchaseCode = invInventoryDocument.getComebackPurchaseCode();
        if (comebackPurchaseCode != null) {
            String[] comebackPurchaseCodeList = comebackPurchaseCode.split(";");
            List<String> items = Arrays.asList(comebackPurchaseCodeList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.RETURN_BACK_PURCHASE.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        //销售发货单号
        String deliverySaleCode = invInventoryDocument.getDeliverySaleCode();
        if (deliverySaleCode != null) {
            String[] deliverySaleCodeList = deliverySaleCode.split(";");
            List<String> items = Arrays.asList(deliverySaleCodeList);
            items.forEach(item -> {
                InvInventoryDocumentCodeRequest invInventoryDocumentCodeRequest = new InvInventoryDocumentCodeRequest();
                invInventoryDocumentCodeRequest.setReferDocCategory(DocCategory.SALE_RU.getCode());
                invInventoryDocumentCodeRequest.setReferDocumentCode(item);
                list.add(invInventoryDocumentCodeRequest);
            });
        }
        if (CollectionUtils.isNotEmpty(list)) {
            invInventoryDocument.setListCode(list);
        }
    }

    /**
     * 查询出入库明细报表
     *
     * @param invInventoryDocumentItem 库存凭证
     * @return 库存凭证明细报表
     */
    @Override
    public Map<String, Object> detailReport(InvInventoryDocumentItem invInventoryDocumentItem) {
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        BeanCopyUtils.copyProperties(invInventoryDocumentItem, invInventoryDocument);
        //查询库存凭证ids
        List<Long> ids = invInventoryDocumentMapper.selectInvInventoryDocumentSids(invInventoryDocument);
        invInventoryDocumentItem.setIds(ids);
        Integer start = (invInventoryDocumentItem.getPageNum() - 1) * invInventoryDocumentItem.getPageSize();
        invInventoryDocumentItem.setPageNum(start);
        List<InvInventoryDocumentItem> list = invInventoryDocumentItemMapper.selectReport(invInventoryDocumentItem);
        Map<String, Object> map = Maps.newHashMap();
        // 当前页所有数据
        map.put("list", list);
        //记录总条数
        map.put("count", ids.size());
        return map;
    }

    @Override
    public List<InvInventoryDocumentItem> sort(List<InvInventoryDocumentItem> items, String type) {
        if (CollectionUtil.isNotEmpty(items)) {
            items = items.stream().sorted(
                    Comparator.comparing(InvInventoryDocumentItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(InvInventoryDocumentItem::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(InvInventoryDocumentItem::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(InvInventoryDocumentItem::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(InvInventoryDocumentItem::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            return items;
        }
        return new ArrayList<>();
    }

    public List<InvInventoryDocumentReportResponse> sortResponse(List<InvInventoryDocumentReportResponse> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            List<InvInventoryDocumentReportResponse> itemMat = items.stream().sorted(
                    Comparator.comparing(InvInventoryDocumentReportResponse::getMaterialCode,
                                    Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(InvInventoryDocumentReportResponse::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(InvInventoryDocumentReportResponse::getSku1Name, Comparator.nullsLast(String::compareTo)
                                    .thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(InvInventoryDocumentReportResponse::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(InvInventoryDocumentReportResponse::getSku2Name, Comparator.nullsLast(String::compareTo)
                                    .thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            return items;
        }
        return new ArrayList<>();
    }

    @Override
    public List<InvInventoryDocumentItem> filter(InvInventoryDocument invInventoryDocument, String productCodes, String status) {
        List<InvInventoryDocumentItem> itemList = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtil.isNotEmpty(itemList)) {
            if (productCodes != null && productCodes != "") {
                itemList = itemList.stream().filter(li -> li.getProductCodes() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(itemList)) {
                    itemList = itemList.stream().filter(li -> li.getProductCodes().contains(productCodes)).collect(Collectors.toList());
                }
            }
            if (CollectionUtil.isNotEmpty(itemList) && StrUtil.isNotBlank(status)) {
                itemList = itemList.stream().filter(li -> status.equals(li.getStatus())).collect(Collectors.toList());
            }
        }
        else {
            itemList = new ArrayList<>();
        }
        return itemList;
    }


    /**
     * 查询库存凭证/甲供料结算单明细报表
     */
    @Override
    public List<InvInventoryDocumentReportResponse> selectDocumentReport(InvInventoryDocumentReportRequest invInventoryDocumentReportRequest) {
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentItemMapper.selectDocumentReport(invInventoryDocumentReportRequest);
        list.forEach(li -> {
            if (li.getPrice() != null && li.getTaxRate() != null) {
                li.setExcludingPriceTax(li.getPrice().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
            }
            if (li.getPrice() != null) {
                if (li.getPriceQuantity() != null) {
                    li.setCurrencyAmount(li.getPrice().multiply(li.getPriceQuantity()));
                }
                else if (li.getQuantity() != null) {
                    li.setCurrencyAmount(li.getPrice().multiply(li.getQuantity()));
                }
            }
        });
        return list;
    }

    /**
     * 新增库存凭证(出库入库)
     * 需要注意编码重复校验
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryDocument(InvInventoryDocument invInventoryDocument) {
        int row = 0;
        RLock lock = redissonClient.getLock(LOCK_KEY);
        String initializeStatus = invInventoryDocument.getInitializeStatus();
        if (ConstantsEms.YES.equals(initializeStatus)) {
            lock.lock(200L, TimeUnit.SECONDS);//库存初始化 延长释放时间
        } else {
            lock.lock(80L, TimeUnit.SECONDS);
        }
        try {
            String documentType = invInventoryDocument.getDocumentType();
            if (documentType == null) {
                invInventoryDocument.setDocumentType(ConstantsEms.DOCUMNET_TYPE_ZG);
            }
            String documentTypeInv = invInventoryDocument.getDocumentTypeInv();//出入库冲销 过滤出库校验
            String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();//寄售流程：采购、销售订单单据类型
            //变更前仓库信息
            Map<Long, Object> oldLocation = new HashMap<>();
            //变更后仓库信息
            List<InvInventoryLocation> updateLocation = new ArrayList<>();
            //库存凭证-明细list
            List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
            List<InvInventoryDocumentAttach> invInventoryDocumentAttacList = invInventoryDocument.getInvInventoryDocumentAttacList();
            //作业类型为“其它出库-客供料”（SC31）时，特殊库存默认“客供料”(KGL)，客户可编辑且必填/寄售
            invDocSpecalHandle(invInventoryDocument);
            judgeNull(invInventoryDocument, invInventoryDocumentItemList);
            judgePrice(invInventoryDocument, invInventoryDocumentItemList);
            setItemNum(invInventoryDocumentItemList);
            changeQutatitly(invInventoryDocumentItemList);
            repeat(invInventoryDocumentItemList);
            if (invInventoryDocument.getType().equals(ConstantsEms.CHU_KU)) {
                String movementType = invInventoryDocument.getMovementType();
                //发货单-甲供料  发货单-客户寄售
                if (ConstantsEms.ISSUE_V.equals(movementType) || ConstantsEms.ISSUE_C.equals(movementType)
                        || DocCategory.SALE_RETURN.getCode().equals(saleAndPurchaseDocument)) {
                    //出库操作
                    LocationCHU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
                } else {
                    //出库前进行校验
                    vatatil(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
                    //出库操作
                    LocationCHU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
                }
            } else {
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    //入库 冲销校验
                    vatatil(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
                }
                //入库操作
                LocationRU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
            }
            invInventoryDocument.setStorehouseOperator(ApiThreadLocalUtil.get().getUsername());
            invInventoryDocument.setHandleStatus("B");//过账
            invInventoryDocument.setInOutStockStatus(null);
            setProductSeasonCode(invInventoryDocument); // 获取产品季的编码
            setWorkCenterCode(invInventoryDocument); // 获取班组编码
            setYearMonth(invInventoryDocument); // 获取过账日期的年月份
            //插入库存凭证明细表 附件
            row = invInventoryDocumentMapper.insert(invInventoryDocument);
            if (row > 0) {
                if (CollectionUtil.isNotEmpty(invInventoryDocumentItemList)) {
                    addInvInventoryDocumentItem(invInventoryDocument, invInventoryDocumentItemList);
                }
                // 移库方式为”两步法（出库再入库）“(LB)
                // 过账成功时，仅进行出库操作，并生成在途库存（同调拨出库）【在途库存表（s_inv_intransit_inventory）】，
                // 同时默认“入库状态”为“未入库”，并生成待办信息给目的仓库的仓库主管员：移库单XXXX的物料/商品已从XXX仓库出库，请及时入库
                //移库 出库时，目的仓库入库
                String code = invInventoryDocument.getMovementType();
                //普通移库
                if (exitTransfer(code)) {
                    if (ConstantsInventory.STOCK_TRANSFER_MODE_LB.equals(invInventoryDocument.getStockTransferMode())) {
                        InvInventoryDocument response = invInventoryDocumentMapper.selectById(invInventoryDocument.getInventoryDocumentSid());
                        invInventoryDocument.setInventoryDocumentCode(response.getInventoryDocumentCode());
                        BasStorehouse basStorehouse = basStorehouseMapper.selectById(invInventoryDocument.getStorehouseSid());
                        if (basStorehouse != null) {
                            invInventoryDocument.setStorehouseName(basStorehouse.getStorehouseName());
                        }
                        try {
                            if (CollectionUtil.isNotEmpty(invInventoryDocumentItemList)) {
                                // 生成在途库存
                                List<InvIntransitInventory> intransitInventoryList = new ArrayList<>();
                                invInventoryDocumentItemList.forEach(item -> {
                                    InvIntransitInventory intransitInventory = new InvIntransitInventory();
                                    intransitInventory.setStorehouseSid(item.getStorehouseSid())
                                            .setStorehouseLocationSid(item.getStorehouseLocationSid())
                                            .setOutStockDate(invInventoryDocument.getAccountDate())
                                            .setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid())
                                            .setInventoryDocumentCode(invInventoryDocument.getInventoryDocumentCode())
                                            .setInventoryDocumentItemSid(item.getInventoryDocumentItemSid())
                                            .setUnlimitedQuantity(item.getQuantity())
                                            .setMaterialSid(item.getMaterialSid()).setSku1Sid(item.getSku1Sid()).setSku2Sid(item.getSku2Sid())
                                            .setBarcode(Long.parseLong(item.getBarcode())).setBarcodeSid(item.getBarcodeSid()).setUnitBase(item.getUnitBase())
                                            .setHandleStatus(ConstantsEms.CHECK_STATUS);
                                    intransitInventoryList.add(intransitInventory);
                                });
                                invIntransitInventoryMapper.inserts(intransitInventoryList);
                                // 更新为未入库
                                invInventoryDocumentMapper.update(null, new UpdateWrapper<InvInventoryDocument>().lambda()
                                        .eq(InvInventoryDocument::getInventoryDocumentSid, invInventoryDocument.getInventoryDocumentSid())
                                        .set(InvInventoryDocument::getInOutStockStatus, ConstantsInventory.IN_OUT_STORE_STATUS_WRK));
                                // 生成待办信息
                                if (invInventoryDocument.getDestStorehouseSid() != null) {
                                    BasStorehouse storehouse = null;
                                    try {
                                        storehouse = basStorehouseMapper.selectBasStorehouseById(invInventoryDocument.getDestStorehouseSid());
                                    } catch (Exception e) {
                                        log.error("移库两步法查询目的仓库时报错，目的仓库sid" + invInventoryDocument.getDestStorehouseSid());
                                    }
                                    if (storehouse != null && storehouse.getSupervisorId() != null) {
                                        List<SysTodoTask> todoTaskList = new ArrayList<>();
                                        SysTodoTask task = new SysTodoTask();
                                        task.setTaskCategory(ConstantsEms.TODO_TASK_DB).setTitle("移库单" +
                                                        invInventoryDocument.getInventoryDocumentCode() + "的物料/商品已从" +
                                                        invInventoryDocument.getStorehouseName() + "仓库出库，请及时入库")
                                                .setTableName(ConstantsTable.TABLE_INV_INVENTORY_DOCUMENT)
                                                .setDocumentSid(invInventoryDocument.getInventoryDocumentSid())
                                                .setDocumentCode(String.valueOf(invInventoryDocument.getInventoryDocumentCode()))
                                                .setMenuId(new Long("2143")).setNoticeDate(new Date()).setUserId(storehouse.getSupervisorId());
                                        todoTaskList.add(task);
                                        sysTodoTaskMapper.inserts(todoTaskList);
                                    }
                                }
                            }
                        } catch (CustomException e) {
                            throw new CustomException("移库方式为两步法时出现未知问题，请联系管理员");
                        }
                    }
                }
                //出库-判断是否是调拨单/移库两步法
                createTransfer(invInventoryDocument, invInventoryDocumentItemList);
                if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    if (CollectionUtil.isNotEmpty(invInventoryDocumentAttacList)) {
                        invInventoryDocumentAttacList.forEach(li -> {
                            li.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
                        });
                        attachMapper.inserts(invInventoryDocumentAttacList);
                    }
                }
            }
            //收发货、领退料、调拨 出入库状态值改变
            changeStaus(invInventoryDocument);
            // 业务通知
            String isBusinessFinance = ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance();
            if (ConstantsEms.YES.equals(isBusinessFinance)) {
                // 生成应付暂估流水
                createpayment(invInventoryDocument, invInventoryDocumentItemList);
                // 生成应收暂估流水
                createReceipt(invInventoryDocument, invInventoryDocumentItemList);
            }
            //生成供应商寄售待结算台账
            createPurRecordVendorConsign(invInventoryDocument, invInventoryDocumentItemList);
            // 修改仓库物料数据库表
            if (CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
                UpdateWrapper<InvStorehouseMaterial> updateWrapper = new UpdateWrapper<>();
                if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())) {
                    // 最近一次入库日期
                    updateWrapper.lambda().set(InvStorehouseMaterial::getLatestStockEntryDate, new Date());
                    // 最近一次采购入库日期
                    List<String> latestPurchaseEntry = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SR01);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR02);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR21);
                    }};
                    // 最近一次生产入库日期
                    List<String> latestManufactEntry = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SR05);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR23);
                    }};
                    // 最近一次调拨入库日期
                    List<String> latestTransferEntry = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SR08);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR081);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR082);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR083);
                        add(ConstantsInventory.MOVEMENT_TYPE_SR084);
                    }};
                    if (latestPurchaseEntry.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestPurchaseEntryDate, new Date());
                    }
                    else if (latestManufactEntry.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestManufactEntryDate, new Date());
                    }
                    else if (latestTransferEntry.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestTransferEntryDate, new Date());
                    }
                }
                if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())) {
                    // 最近一次出库日期
                    updateWrapper.lambda().set(InvStorehouseMaterial::getLatestStockOutDate, new Date());
                    // 最近一次销售出库日期
                    List<String> latestSaleOut = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SC01);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC02);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC21);
                    }};
                    // 最近一次领料出库日期
                    List<String> latestRequisitionOut = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SC07);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC071);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC072);
                    }};
                    // 最近一次调拨出库日期
                    List<String> latestTransferOut = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SC08);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC081);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC082);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC083);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC084);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC085);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC086);
                    }};
                    // 最近一次调拨入库日期
                    List<String> latestTransferEntry = new ArrayList<String>(){{
                        add(ConstantsInventory.MOVEMENT_TYPE_SC085);
                        add(ConstantsInventory.MOVEMENT_TYPE_SC086);
                    }};
                    if (latestSaleOut.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestSaleOutDate, new Date());
                    }
                    else if (latestRequisitionOut.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestRequisitionOutDate, new Date());
                    }
                    else if (latestTransferOut.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestTransferOutDate, new Date());
                    }
                    if (latestTransferEntry.contains(invInventoryDocument.getMovementType())) {
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestTransferEntryDate, new Date());
                        updateWrapper.lambda().set(InvStorehouseMaterial::getLatestStockEntryDate, new Date());
                    }
                }
                if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())
                        || ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())) {
                    updateWrapper.lambda().and(warpper ->{
                        for (InvInventoryDocumentItem item : invInventoryDocument.getInvInventoryDocumentItemList()) {
                            warpper.or(o -> {
                                o.eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid()).eq(InvStorehouseMaterial::getStorehouseSid, item.getStorehouseSid());
                            });
                        }
                    });
                    invStorehouseMaterialMapper.update(null, updateWrapper);
                }
            }
            MongodbUtil.insertUserLog(invInventoryDocument.getInventoryDocumentSid(), BusinessType.POSTING.getValue(), TITLE);
        } catch (CustomException e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("系统未知错误请联系管理员");
        } finally {
            lock.unlock();
        }
        return row;

    }

    /**
     * 多作业类型出库
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryDocumentByMovementType(InvInventoryDocument invInventoryDocument) {
        int row = 0;
        //库存凭证-明细list
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtil.isNotEmpty(invInventoryDocumentItemList)) {
            Map<String, List<InvInventoryDocumentItem>> map = invInventoryDocumentItemList.stream()
                    .collect(Collectors.groupingBy(item -> String.valueOf(item.getMovementType())+"-"+String.valueOf(item.getReferDocCategory())+
                            "-"+String.valueOf(item.getSpecialStock()) + "-"+String.valueOf(item.getVendorSid()) + "-"+String.valueOf(item.getCustomerSid())));
            for (String key : map.keySet()) {
                // 从明细获取主表的数据到主表
                InvInventoryDocument base = new InvInventoryDocument();
                BeanCopyUtils.copyProperties(invInventoryDocument, base);
                List<InvInventoryDocumentItem> baseItemList = map.get(key);
                base.setMovementType(baseItemList.get(0).getMovementType());
                base.setMovementTypeName(baseItemList.get(0).getMovementTypeName());
                base.setSpecialStock(baseItemList.get(0).getSpecialStock());
                base.setSpecialStockName(baseItemList.get(0).getSpecialStockName());
                base.setVendorSid(baseItemList.get(0).getVendorSid());
                base.setVendorName(baseItemList.get(0).getVendorName());
                base.setVendorShortName(baseItemList.get(0).getVendorShortName());
                base.setCustomerSid(baseItemList.get(0).getCustomerSid());
                base.setCustomerName(baseItemList.get(0).getCustomerName());
                base.setCustomerShortName(baseItemList.get(0).getCustomerShortName());
                base.setInvInventoryDocumentItemList(baseItemList);
                // 调用原先生成库存凭证的接口
                row = row + this.insertInvInventoryDocument(base);
            }
        }
        return row;
    }

    /**
     * 获取产品季编码
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public void setProductSeasonCode(InvInventoryDocument invInventoryDocument) {
        if (invInventoryDocument.getProductSeasonSid() != null) {
            BasProductSeason season = basProductSeasonMapper.selectById(invInventoryDocument.getProductSeasonSid());
            if (season != null) {
                invInventoryDocument.setProductSeasonCode(season.getProductSeasonCode());
            }
        }
    }

    /**
     * 获取班组编码
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public void setWorkCenterCode(InvInventoryDocument invInventoryDocument) {
        if (invInventoryDocument.getWorkCenterSid() != null) {
            ManWorkCenter center = manWorkCenterMapper.selectById(invInventoryDocument.getWorkCenterSid());
            if (center != null) {
                invInventoryDocument.setWorkCenterCode(center.getWorkCenterCode());
            }
        }
    }

    /**
     * 获取过账日期的年月份
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    public void setYearMonth(InvInventoryDocument invInventoryDocument) {
        if (invInventoryDocument.getAccountDate() != null) {
            try {
                // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
                LocalDate localDate = invInventoryDocument.getAccountDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                invInventoryDocument.setYear(new Long(localDate.getYear()));
                invInventoryDocument.setMonth(new Long(localDate.getMonth().getValue()));
            } catch (Exception e) {
                log.error("获取过账日期的年月份失败");
            }
        }
    }

    /**
     * 生成二维码
     */
    @Override
    public List<InvInventoryDocumentItem> getQr(List<InvInventoryDocumentItem> list) {
        // 租户图片
        String logoPicture = "";
        SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>()
                .lambda().eq(SysClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
        if (StrUtil.isNotBlank(sysClient.getLogoPicturePath())){
            GetObjectResponse object = null;
            String path = sysClient.getLogoPicturePath();
            String str1 = path.substring(0, path.indexOf("/" + minioConfig.getBucketName()));
            String str2 = path.substring(str1.length()+9);
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(str2).build();
            try {
                object= client.getObject(args);
                FastByteArrayOutputStream fos = new FastByteArrayOutputStream();
                BufferedImage image = ImageIO.read(object);
                BufferedImage images = new BufferedImage(55, 55, TYPE_INT_RGB);
                Graphics graphics = images.createGraphics();
                graphics.drawImage(image,0,0,55,55,null);
                ImageIO.write(images, "png", fos);
                //将Logo转成要在前端显示需要转成Base64
                logoPicture = Base64.getEncoder().encodeToString(fos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (InvInventoryDocumentItem li : list) {
            String codeBase = QrCodeUtil.generateAsBase64(li.getBarcode(), QrConfig.create().setWidth(80).setHeight(80).setMargin(0), "png");
            li.setQrCode(codeBase);
            li.setLogoPicturePath(logoPicture);
        }
        return list;
    }

    /**
     * 打印出库单
     */
    @Override
    public InvInventoryDocument getPrintck(Long[] sids) {
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        invInventoryDocument.setInventoryDocumentSids(sids);
        HashSet<String> sets = new HashSet<>();
        List<InvInventoryDocument> invInventoryDocuments = invInventoryDocumentMapper.selectInvInventoryDocumentList(invInventoryDocument);
        List<InvInventoryDocument> documentList = invInventoryDocuments.stream().filter(li -> li.getCarrierName() != null).collect(Collectors.toList());
        InvInventoryDocument document = null;
        if (CollectionUtil.isNotEmpty(documentList)) {
            document = documentList.get(0);
        } else {
            document = invInventoryDocuments.get(0);
        }
        invInventoryDocuments.forEach(li -> {
            Long customerSid = li.getCustomerSid() != null ? li.getCustomerSid() : 1L;
            Long vendorSid = li.getVendorSid() != null ? li.getVendorSid() : 1L;
            String specialStock = li.getSpecialStock() != null ? li.getSpecialStock() : "1";
            sets.add(li.getMovementType() + ""
                    + li.getStorehouseLocationSid() + ""
                    + li.getStorehouseSid() + ""
                    + customerSid + "" + vendorSid + "" + specialStock);
            if (sets.size() > 1) {
                throw new CustomException("所选择行的“作业类型、客户/供应商、仓库、库位、特殊库存”字段值要相同");
            }

        });
        InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
        invInventoryDocumentItem.setInventoryDocumentSids(sids);
        List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectInvInventoryDocumentItemList(invInventoryDocumentItem);
        List<InvInventoryDocumentItem> items = sort(itemList, null);
        List<InvInventoryDocumentItem> referList = items.stream().filter(li -> li.getReferDocumentCode() != null && li.getReferDocumentItemNum() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(referList) && referList.size() == items.size()) {
            items = referList.stream().sorted(Comparator.comparing(InvInventoryDocumentItem::getReferDocumentCode)
                    .thenComparing(InvInventoryDocumentItem::getReferDocumentItemNum)
            ).collect(Collectors.toList());
        }
        setItemNum(items);
        document.setInvInventoryDocumentItemList(items);
        return document;
    }

    /**
     * 根据特殊的作业类型进行特殊处理
     */
    public void invDocSpecalHandle(InvInventoryDocument invInventoryDocument) {
        String movementType = invInventoryDocument.getMovementType();
        String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();
        if (ConstantsEms.ORHER_MOVE_TYPE_CHK.equals(movementType) || ConstantsEms.ORHER_MOVE_TYPE_RU.equals(movementType)) {
            invInventoryDocument.setSpecialStock(ConstantsEms.CUS_RA);
        }
        if (DocCategory.PURCHAASE_JI_SHOU.getCode().equals(saleAndPurchaseDocument) || DocCategory.PURCHAASE_JI_SHOU_RETURN.getCode().equals(saleAndPurchaseDocument)) {
            invInventoryDocument.setSpecialStock(ConstantsEms.VEN_CU);
        }
    }

    public static File asFile(InputStream inputStream) throws IOException {
        File tmp = File.createTempFile("lzq", ".tmp", new File("C:\\"));
        OutputStream os = new FileOutputStream(tmp);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return tmp;
    }

    public void changeQutatitly(List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemList.forEach(li -> {
            if (li.getPriceQuantity() != null) {
                if (li.getUnitConversionRate() == null) {
                    throw new CustomException("明细的单位换算比例不允许为空");
                } else {
                    li.setQuantity(li.getPriceQuantity().multiply(li.getUnitConversionRate()));
                }
            }
            if (ConstantsEms.YES.equals(li.getFreeFlag())) {
                li.setPrice(BigDecimal.ZERO);
            }
        });
    }

    /**
     * 库存凭证冲销
     */
    @Override
    public int invDocumentCX(List<Long> sidList) {
        for (Long li : sidList) {
            InvInventoryDocument invInventoryDocument = selectInvInventoryDocumentById(li);
            Long inventoryDocumentSid = invInventoryDocument.getInventoryDocumentSid();
            String specialStock = invInventoryDocument.getSpecialStock();
            String referDocCategory = invInventoryDocument.getReferDocCategory();
            String movementType = invInventoryDocument.getMovementType();
            ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                    .eq(ConMovementType::getCode, movementType)
            );
            if (!ConstantsEms.YES.equals(conMovementType.getIsAllowReverse())) {
                throw new CustomException("此单据不允许冲销，请核实");
            }
            //来源类别销售订单
            if (DocCategory.SALE_ORDER.getCode().equals(referDocCategory) || DocCategory.RETURN_BACK_SALE.getCode().equals(referDocCategory)) {
                String orderCode = null;
                // 多订单的情况
                if (invInventoryDocument.getReferDocumentCode() == null) {
                    orderCode = invInventoryDocument.getInvInventoryDocumentItemList().get(0).getReferDocumentCode();
                }
                else {
                    orderCode = invInventoryDocument.getReferDocumentCode();
                }
                SalSalesOrder salSalesOrder = salSalesOrderMapper.selectOne(new QueryWrapper<SalSalesOrder>()
                        .lambda().eq(SalSalesOrder::getSalesOrderCode, orderCode));
                invInventoryDocument.setSaleAndPurchaseDocument(salSalesOrder.getDocumentType());
                invInventoryDocument.setIsReturnGoods(salSalesOrder.getIsReturnGoods());
            }
            //来源销售发货单
            // 是否预留库存
            String isReserveStock = null;
            if (DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(referDocCategory) || DocCategory.SALE_RU.getCode().equals(referDocCategory)) {
                DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectOne(new QueryWrapper<DelDeliveryNote>().lambda()
                        .eq(DelDeliveryNote::getDeliveryNoteCode, invInventoryDocument.getReferDocumentCode()));
                // 来到这边了一定会有明细的
                List<DelDeliveryNoteItem> noteItems = delDeliveryNoteItemMapper.selectDelDeliveryNoteItemList
                        (new DelDeliveryNoteItem().setDeliveryNoteCode(invInventoryDocument.getReferDocumentCode()));
                Long salesOrderSid = noteItems.get(0).getSalesOrderSid();
                invInventoryDocument.setIsReturnGoods(delDeliveryNote.getIsReturnGoods());
                if (salesOrderSid != null) {
                    ConBuTypeDeliveryNote conBuTypeDeliveryNote = conBuTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                            .eq(ConBuTypeDeliveryNote::getCode, delDeliveryNote.getBusinessType())
                    );
                    invInventoryDocument.setChukuCategory(conBuTypeDeliveryNote.getChukuCategory());
                    invInventoryDocument.setSaleAndPurchaseDocument(noteItems.get(0).getReferDocumentType());
                }
                // 预留库存
                if (delDeliveryNote.getDocumentType() != null) {
                    ConDocTypeDeliveryNote docTypeDeliveryNote = conDocTypeDeliveryNoteMapper.selectOne(new QueryWrapper<ConDocTypeDeliveryNote>().lambda()
                            .eq(ConDocTypeDeliveryNote::getCode, delDeliveryNote.getDocumentType()));
                    if (docTypeDeliveryNote != null) {
                        isReserveStock = docTypeDeliveryNote.getIsReserveStock();
                    }
                }
            }
            //来源类别 采购退货订单
            if (DocCategory.RETURN_BACK_PURCHASE.getCode().equals(referDocCategory)) {
                invInventoryDocument.setIsReturnGoods(ConstantsEms.YES);
            }
            //来源类别 采购退货交货单
            if (DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(referDocCategory)) {
                invInventoryDocument.setIsReturnGoods(ConstantsEms.YES);
            }
            String documentCategory = invInventoryDocument.getDocumentCategory();
            if (DocumentCategory.RU.getCode().equals(documentCategory)) {
                invInventoryDocument.setType(ConstantsEms.RU_KU);
            } else {
                invInventoryDocument.setType(ConstantsEms.CHU_KU);
            }
            invInventoryDocument.setPreInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
            if (DocCategory.SALE_ORDER.getCode().equals(referDocCategory) || DocCategory.RETURN_BACK_SALE.getCode().equals(referDocCategory) || DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(referDocCategory) || DocCategory.SALE_RU.getCode().equals(referDocCategory)) {
                List<FinBookReceiptEstimationItem> finBookReceiptEstimationItems = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>().lambda()
                        .eq(FinBookReceiptEstimationItem::getReferDocSid, invInventoryDocument.getInventoryDocumentSid())
                );
                if (CollectionUtil.isNotEmpty(finBookReceiptEstimationItems)) {
                    finBookReceiptEstimationItems.forEach(m -> {
                        if (!ConstantsEms.CLEAR_STATUS_WHX.equals(m.getClearStatus())) {
                            throw new CustomException("对应的财务流水已处于核销中，不允许冲销");
                        }
                    });
                }
            }
            else if (DocCategory.PURCHASE_ORDER.getCode().equals(referDocCategory) || DocCategory.RETURN_BACK_PURCHASE.getCode().equals(referDocCategory) || DocCategory.SALE_CHK.getCode().equals(referDocCategory) || DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(referDocCategory)) {
                List<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = finBookPaymentEstimationItemMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>().lambda()
                        .eq(FinBookPaymentEstimationItem::getReferDocSid, invInventoryDocument.getInventoryDocumentSid())
                );
                if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItems)) {
                    finBookPaymentEstimationItems.forEach(m -> {
                        if (!ConstantsEms.CLEAR_STATUS_WHX.equals(m.getClearStatus())) {
                            throw new CustomException("对应的财务流水已处于核销中，不允许冲销");
                        }
                    });
                }
            }
            //初始化
            invInventoryDocument.setInventoryDocumentSid(null);
            invInventoryDocument.setInventoryDocumentCode(null);
            invInventoryDocument.setCreatorAccount(null);
            invInventoryDocument.setCreateDate(null);
            invInventoryDocument.setUpdateDate(null);
            invInventoryDocument.setUpdaterAccount(null);
            List<InvInventoryDocumentItem> itemList = invInventoryDocument.getInvInventoryDocumentItemList();
            if (PurRecordVendorAdd(movementType) || (ConstantsEms.VEN_CU.equals(specialStock) && DocCategory.SALE_ORDER.getCode().equals(referDocCategory)) || (ConstantsEms.VEN_CU.equals(specialStock) && DocCategory.SALE_RU.getCode().equals(referDocCategory))) {
                itemList.forEach(item -> {
                    PurRecordVendorConsign purRecordVendorConsign = new PurRecordVendorConsign();
                    purRecordVendorConsign.setBarcodeSid(item.getBarcodeSid());
                    purRecordVendorConsign.setVendorSid(invInventoryDocument.getVendorSid());
                    PurRecordVendorConsign consign = purRecordVendorConsignServiceImpl.exitVendorConsign(purRecordVendorConsign);
                    if (consign != null) {
                        if (item.getQuantity().compareTo(consign.getQuantity()) == 1) {
                            if (purRecordVendorConsign.getSku2Name() != null) {
                                throw new CustomException("物料为" + item.getMaterialName() + ",sku1为" + item.getSku1Name() + ",sku2为" + item.getSku2Name() + ",供应商为" + invInventoryDocument.getVendorName() + "，扣减量大于待结算量，不允许冲销");
                            } else {
                                throw new CustomException("物料为" + item.getMaterialName() + ",sku1为" + item.getSku1Name() + ",供应商为" + invInventoryDocument.getVendorName() + "，扣减量大于待结算量，不允许冲销");
                            }
                        }
                    }
                });
            }
            itemList.forEach(item -> {
                item.setPreInventoryDocumentItemSid(item.getInventoryDocumentItemSid());
                item.setInventoryDocumentSid(null);
                item.setInventoryDocumentItemSid(null);
                item.setCreateDate(null);
                item.setCreatorAccount(null);
                item.setUpdateDate(null);
                item.setUpdaterAccount(null);
            });
            //作废
            invInventoryDocumentMapper.updateById(new InvInventoryDocument()
                    .setInventoryDocumentSid(li)
                    .setDocumentType("CG")//常规
                    .setHandleStatus("C"));//已冲销
            invInventoryDocument.setHandleStatus("B");//已过帐
            invInventoryDocument.setDocumentType(ConstantsEms.DOCUMNET_TYPE_CX);//冲销
            invInventoryDocument.setDocumentTypeInv(ConstantsEms.DOCUMNET_TYPE_CX);
            invInventoryDocument.setInvInventoryDocumentItemList(itemList);
            insertInvInventoryDocument(invInventoryDocument);
            //库存预留
            if (ConstantsEms.YES.equals(isReserveStock)){
                //销售发货单
                delDeliveryNoteServiceImpl.xcCreateInv(invInventoryDocument.getReferDocumentSid());
            }
            MongodbUtil.insertUserLog(inventoryDocumentSid, BusinessType.CHONGXIAO.getValue(), TITLE);
        }
        return 1;
    }

    /**
     * 销售发货单-发送推送消息和邮件
     */
    public void email(InvInventoryDocument invInventoryDocument, String temporaryCode) {
        if (invInventoryDocument.getType().equals(ConstantsEms.CHU_KU)) {
            if (ConstantsEms.SALE_ORDER_RU.equals(invInventoryDocument.getMovementType())) {
                DelDeliveryNote saleOrderDocument = getSaleDelivery(temporaryCode, "SDN", null);
                String markdowntext = "<font color=\"warning\">销售发货通知</font>` \n" +
                        ">销售订单号：<font color=\"info\">" + saleOrderDocument.getSalesOrderCode() + "</font> \n" +
                        ">销售发货单号：<font color=\"info\">" + saleOrderDocument.getDeliveryNoteCode() + "</font> \n" +
                        ">出库日期：<font color=\"info\">" + DateUtil.format(invInventoryDocument.getAccountDate(), "yyyy-MM-dd HH:mm:ss") + "</font> \n" +
                        ">销售员：<font color=\"info\">" + saleOrderDocument.getNickName() + "</font>  \n" +
                        ">仓管员：<font color=\"info\">" + invInventoryDocument.getStorehouseOperator() + "</font>  \n" +
                        ">备注：<font color=\"info\">" + invInventoryDocument.getRemark() + "</font>";
                try {
                    //         QiYePushUtil.SendQyMsgMarkdown(WxConstants.ALL_TOUSER, WxConstants.SCM_AGENT_ID, markdowntext);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("企业微信推送失败");
                }
                String mailtext = "<font color=\"warning\">销售发货通知</font>   <br />" +
                        "销售订单号：<font color=\"info\">" + saleOrderDocument.getSalesOrderCode() + "</font>  <br />" +
                        "销售发货单号：<font color=\"info\">" + saleOrderDocument.getDeliveryNoteCode() + "</font>  <br />" +
                        "出库日期：<font color=\"info\">" + DateUtil.format(invInventoryDocument.getAccountDate(), "yyyy-MM-dd HH:mm:ss") + "</font>  <br />" +
                        "销售员：<font color=\"info\">" + saleOrderDocument.getNickName() + "</font>   <br />" +
                        "仓管员：<font color=\"info\">" + invInventoryDocument.getStorehouseOperator() + "</font>   <br />" +
                        "备注：<font color=\"info\">" + invInventoryDocument.getRemark() + "</font>";
                List<SaleOrderExcel> rows = new ArrayList<>();
                SaleOrderExcel order = new SaleOrderExcel();
                order.setDeliveryNoteCode(String.valueOf(saleOrderDocument.getDeliveryNoteCode()));
                order.setDocumentDate(DateUtil.format(invInventoryDocument.getDocumentDate(), "yyyy-MM-dd HH:mm:ss"));
                order.setRemark(invInventoryDocument.getRemark());
                order.setSalePerson(saleOrderDocument.getNickName());
                order.setStorehouseAdmin(invInventoryDocument.getStorehouseOperator());
                order.setSaleOrderCode(String.valueOf(saleOrderDocument.getSalesOrderCode()));
                rows.add(order);
                CreateExcelUtil<SaleOrderExcel> createExcelUtil = new CreateExcelUtil<>();
                File excelfile = createExcelUtil.createExcel(rows, "销售发货通知");
                try {
                    MailUtil.send("414254651@qq.com", "794387921@qq.com", null, "【业务动态通知】销售发货单" + order.getSaleOrderCode() + "已出库", mailtext, true, excelfile);
                    excelfile.deleteOnExit();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("邮件发送失败");
                }
            }
        }
    }

    /**
     * 删除原有库存凭证
     */
    public void deleteInvDocument(InvInventoryDocument invInventoryDocument) {
        Long inventoryDocumentSid = invInventoryDocument.getInventoryDocumentSid();
        if (inventoryDocumentSid != null) {
            invInventoryDocumentMapper.delete(new QueryWrapper<InvInventoryDocument>().lambda()
                    .eq(InvInventoryDocument::getInventoryDocumentSid, inventoryDocumentSid));
            invInventoryDocumentItemMapper.delete(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                    .eq(InvInventoryDocumentItem::getInventoryDocumentSid, inventoryDocumentSid)
            );
        }
    }

    /**
     * 插入库存凭证
     */
    public void insertInvDocumnet(InvInventoryDocument invInventoryDocument) {
        invInventoryDocumentMapper.insert(invInventoryDocument);
        List<InvInventoryDocumentItem> list = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                item.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
            });
            invInventoryDocumentItemMapper.inserts(list);
        }

    }

    /**
     * 行号赋值
     */
    public void setItemNum(List<InvInventoryDocumentItem> list) {
        int size = list.size();
        if (size > 0) {
            for (int i = 1; i <= size; i++) {
                list.get(i - 1).setItemNum(i);
            }
        }
    }

    public void judgeNull(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        if (CollectionUtils.isEmpty(invInventoryDocumentItemList)) {
            throw new CustomException("过账时，明细不允许为空");
        }
        else {
            List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemList.stream().filter(o -> o.getStorehouseSid() == null
                    || o.getStorehouseLocationSid() == null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(itemList)) {
                throw new CustomException("明细信息，仓库、库位不能为空！");
            }
            if (ConstantsInventory.DOCUMENT_CATEGORY_YK.equals(invInventoryDocument.getDocumentCategory()) ||
                    ConstantsInventory.DOCUMENT_CATEGORY_TCZY.equals(invInventoryDocument.getDocumentCategory())) {
                itemList = invInventoryDocumentItemList.stream().filter(o -> o.getDestStorehouseSid() == null
                        || o.getDestStorehouseLocationSid() == null).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(itemList)) {
                    throw new CustomException("明细信息，目的仓库、目的库位不能为空！");
                }
            }
            for (InvInventoryDocumentItem item : invInventoryDocumentItemList) {
                List<BasStorehouseLocation> location = basStorehouseLocationeMapper.selectList(new QueryWrapper<BasStorehouseLocation>()
                        .lambda().eq(BasStorehouseLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
                        .eq(BasStorehouseLocation::getStorehouseSid, item.getStorehouseSid())
                        .eq(BasStorehouseLocation::getHandleStatus, ConstantsEms.CHECK_STATUS));
                if (CollectionUtils.isEmpty(location)) {
                    throw new CustomException("明细信息，存在明细行的仓库和库位不匹配！");
                }
            }
        }
    }

    /**
     * 改变出入库状态以及设置对应单据出入库时间
     */
    @Override
    public void changeStaus(InvInventoryDocument invInventoryDocument) {
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
        String code = invInventoryDocument.getReferDocCategory();
        Long sid = invInventoryDocument.getReferDocumentSid();
        String MaterialCode = invInventoryDocument.getReferDocumentCode();
        String referDocumentCode = invInventoryDocument.getReferDocumentCode();
        String documentType = invInventoryDocument.getDocumentType();
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        String movementType = invInventoryDocument.getMovementType();
        //收货单 全部入库和部分入库
        if (DocCategory.RECIPIT.getCode().equals(code)) {
            InvGoodReceiptNote invGoodReceiptNote = invGooReceiptNoteServiceImpl.selectInvGoodReceiptNoteById(invInventoryDocument.getReferDocumentSid());
            List<InvGoodReceiptNoteItem> list = invGoodReceiptNote.getListInvGoodReceiptNoteItem();
            list.forEach(item -> {
                Long barcodeSid = item.getBarcodeSid();
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                        .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                BigDecimal sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumitem = item.getQuantity();
                //判断是否出库完全
                if (sumCode.compareTo(sumitem) == -1) {
                    invInventoryDocument.setInOutStatus(false);
                    if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                        invInventoryDocument.setInOutStatusNo(false);
                    }
                    //改变出入库状态
                    if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                        invGoodReceiptNoteItemMapper.update(new InvGoodReceiptNoteItem(), new UpdateWrapper<InvGoodReceiptNoteItem>().lambda()
                                .eq(InvGoodReceiptNoteItem::getNoteItemSid, item.getNoteItemSid())
                                .set(InvGoodReceiptNoteItem::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_NOT)
                        );
                    } else {
                        invGoodReceiptNoteItemMapper.update(new InvGoodReceiptNoteItem(), new UpdateWrapper<InvGoodReceiptNoteItem>().lambda()
                                .eq(InvGoodReceiptNoteItem::getNoteItemSid, item.getNoteItemSid())
                                .set(InvGoodReceiptNoteItem::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_LI)
                        );
                    }
                } else {
                    //改变出入库状态
                    invGoodReceiptNoteItemMapper.update(new InvGoodReceiptNoteItem(), new UpdateWrapper<InvGoodReceiptNoteItem>().lambda()
                            .eq(InvGoodReceiptNoteItem::getNoteItemSid, item.getNoteItemSid())
                            .set(InvGoodReceiptNoteItem::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS)
                    );
                }
            });
            //主表出入库状态
            if (!invInventoryDocument.getInOutStatus()) {
                if (invInventoryDocument.getInOutStatusNo()) {
                    invGooReceiptNoteServiceImpl.update(new InvGoodReceiptNote(), new UpdateWrapper<InvGoodReceiptNote>().lambda()
                            .set(InvGoodReceiptNote::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_NOT)
                            .set(InvGoodReceiptNote::getAccountDate, new Date())
                            .eq(InvGoodReceiptNote::getNoteSid, invGoodReceiptNote.getNoteSid()));
                } else {
                    invGooReceiptNoteServiceImpl.update(new InvGoodReceiptNote(), new UpdateWrapper<InvGoodReceiptNote>().lambda()
                            .set(InvGoodReceiptNote::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_LI)
                            .set(InvGoodReceiptNote::getAccountDate, new Date())
                            .eq(InvGoodReceiptNote::getNoteSid, invGoodReceiptNote.getNoteSid()));
                }
            } else {
                invGooReceiptNoteServiceImpl.update(new InvGoodReceiptNote(), new UpdateWrapper<InvGoodReceiptNote>().lambda()
                        .set(InvGoodReceiptNote::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS)
                        .set(InvGoodReceiptNote::getAccountDate, new Date())
                        .eq(InvGoodReceiptNote::getNoteSid, invGoodReceiptNote.getNoteSid()));
            }
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                //动态通知
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                setBussiness(invInventoryDocument, sysBusinessBcst);
                sysBusinessBcst.setTitle("收货单" + referDocumentCode + "中已入库" + sum);
                businessBcst(sysBusinessBcst, invGoodReceiptNote.getCreatorAccount());
            }
        }
        //退料单 全部入库和部分入库
        if (DocCategory.REQUESTION_RU.getCode().equals(code)) {
            InvMaterialRequisition invMaterialRequisition = invMaterialServiceImpl.selectInvMaterialRequisitionById(invInventoryDocument.getReferDocumentSid());
            List<InvMaterialRequisitionItem> list = invMaterialRequisition.getInvMaterialRequisitionItemList();
            list.forEach(item -> {
                Boolean inOutStatus = invInventoryDocument.getInOutStatus();
                Boolean inOutStatusAll = invInventoryDocument.getInOutStatusAll();
                inOutStatus = true;
                inOutStatusAll = false;
                Long barcodeSid = item.getBarcodeSid();
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                        .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                BigDecimal sumCode = BigDecimal.ZERO;
                if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                    sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                BigDecimal sumitem = item.getQuantity();
                //判断是否出库完全
                if (sumCode.compareTo(sumitem) == -1) {
                    invInventoryDocument.setInOutStatus(false);
                    if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                        invInventoryDocument.setInOutStatusNo(false);
                    }
                    //改变出入库状态
                    if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_NOT)
                        );
                    } else {
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_LI)
                        );
                    }
                } else {
                    invInventoryDocument.setInOutStatusNo(false);
                    //改变出入库状态
                    invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                            .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                            .set(InvMaterialRequisitionItem::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS)
                    );
                }
            });
            //主表出入库状态
            if (!invInventoryDocument.getInOutStatus()) {
                if (invInventoryDocument.getInOutStatusNo()) {
                    invMaterialServiceImpl.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                            .set(InvMaterialRequisition::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_NOT)
                            .eq(InvMaterialRequisition::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid()));
                } else {
                    invMaterialServiceImpl.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                            .set(InvMaterialRequisition::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS_LI)
                            .eq(InvMaterialRequisition::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid()));
                }
            } else {
                invMaterialServiceImpl.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                        .set(InvMaterialRequisition::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS)
                        .eq(InvMaterialRequisition::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid()));
            }
            invMaterialServiceImpl.update(new UpdateWrapper<InvMaterialRequisition>().lambda()
                    .set(InvMaterialRequisition::getAccountDate, new Date())
                    .eq(InvMaterialRequisition::getMaterialRequisitionSid, sid));
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                //动态通知
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                setBussiness(invInventoryDocument, sysBusinessBcst);
                sysBusinessBcst.setTitle("退料单" + referDocumentCode + "中已入库" + sum);
                businessBcst(sysBusinessBcst, invMaterialRequisition.getCreatorAccount());
            }
        }
        //调拨单 全部入库和部分入库
        if (DocCategory.ALLOCAT.getCode().equals(code)) {
            String type = invInventoryDocument.getType();
            InvInventoryTransfer invInventoryTransfer = invTransferServiceImpl.selectInvInventoryTransferById(invInventoryDocument.getReferDocumentSid());
            //释放库存预留
            invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda().eq(InvReserveInventory::getBusinessOrderSid, invInventoryTransfer.getInventoryTransferSid()));
            invInventoryTransferItemMapper.update(new InvInventoryTransferItem(), new UpdateWrapper<InvInventoryTransferItem>().lambda()
                    .eq(InvInventoryTransferItem::getInventoryTransferSid, invInventoryTransfer.getInventoryTransferSid())
                    .set(InvInventoryTransferItem::getReserveStatus, ConstantsEms.RE_STATUS_WY)
            );
            List<InvInventoryTransferItem> list = invInventoryTransfer.getListInvInventoryTransfer();
            list.forEach(item -> {
                Long barcodeSid = item.getBarcodeSid();
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                        .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                BigDecimal sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumitem = item.getQuantity();
                //判断是否出库完全
                if (type.equals(ConstantsEms.CHU_KU)) {
                    if (sumCode.compareTo(sumitem) == -1) {
                        invInventoryDocument.setInOutStatus(false);
                        if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                            invInventoryDocument.setInOutStatusNo(false);
                        }
                    } else if (sumCode.compareTo(sumitem) == 1) {

                    }
                }
            });
            if (type.equals(ConstantsEms.CHU_KU) && !invInventoryDocument.getInOutStatus()) {
                invTransferServiceImpl.update(new UpdateWrapper<InvInventoryTransfer>().lambda()
                        .set(InvInventoryTransfer::getOutStockStatus, ConstantsEms.OUT_STORE_STATUS_LI)
                        .set(InvInventoryTransfer::getAccountDate, new Date())
                        .eq(InvInventoryTransfer::getInventoryTransferSid, sid));
            } else if (type.equals(ConstantsEms.CHU_KU) && invInventoryDocument.getInOutStatus()) {
                invTransferServiceImpl.update(new UpdateWrapper<InvInventoryTransfer>().lambda()
                        .set(InvInventoryTransfer::getOutStockStatus, ConstantsEms.OUT_STORE_STATUS)
                        .set(InvInventoryTransfer::getAccountDate, new Date())
                        .eq(InvInventoryTransfer::getInventoryTransferSid, sid));
            }

            if (type.equals(ConstantsEms.RU_KU) || exitOneTransfer(movementType)) {
                InvInventoryTransfer transfer = invTransferServiceImpl.selectInvInventoryTransferById(invInventoryDocument.getReferDocumentSid());
                String inOutStockStatus = transfer.getOutStockStatus();
                if (inOutStockStatus.equals(ConstantsEms.OUT_STORE_STATUS_LI)) {
                    invTransferServiceImpl.update(new UpdateWrapper<InvInventoryTransfer>().lambda()
                            .set(InvInventoryTransfer::getInStockStatus, ConstantsEms.IN_STORE_STATUS_LI)
                            .set(InvInventoryTransfer::getAccountDate, new Date())
                            .eq(InvInventoryTransfer::getInventoryTransferSid, sid));
                } else {
                    invTransferServiceImpl.update(new UpdateWrapper<InvInventoryTransfer>().lambda()
                            .set(InvInventoryTransfer::getInStockStatus, ConstantsEms.IN_STORE_STATUS)
                            .set(InvInventoryTransfer::getAccountDate, new Date())
                            .eq(InvInventoryTransfer::getInventoryTransferSid, sid));
                }
            }
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                //动态通知
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                setBussiness(invInventoryDocument, sysBusinessBcst);
                sysBusinessBcst.setTitle("调拨单" + referDocumentCode + "中已入库" + sum);
                businessBcst(sysBusinessBcst, invInventoryTransfer.getCreatorAccount());
            }
        }
        //发货单全部入库和部分入库
        if (DocCategory.SHIP.getCode().equals(code)) {
            InvGoodIssueNote invGoodIssueNote = invGoodIssueNoteServiceImpl.selectInvGoodIssueNoteById(invInventoryDocument.getReferDocumentSid());
            List<InvGoodIssueNoteItem> list = invGoodIssueNote.getListInvGoodIssueNoteItem();
            List<InvGoodIssueNoteItem> invGoodIssueNoteItems = new ArrayList<>();
            list.forEach(item -> {
                Long barcodeSid = item.getBarcodeSid();
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                        .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getNoteItemSid())
                        .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                BigDecimal sumCode = BigDecimal.ZERO;
                if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                    sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                BigDecimal subQuantity = item.getQuantity().subtract(sumCode);
                if (BigDecimal.ZERO.compareTo(subQuantity) == 0) {
                    invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                            .eq(InvReserveInventory::getBusinessOrderItemSid, item.getNoteItemSid())
                    );
                    invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(), new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                            .eq(InvGoodIssueNoteItem::getNoteItemSid, item.getNoteItemSid())
                            .set(InvGoodIssueNoteItem::getReserveStatus, ConstantsEms.RE_STATUS_WY)
                    );
                } else {
                    //更新库存预留
                    InvGoodIssueNoteItem invGoodIssueNoteItem = new InvGoodIssueNoteItem();
                    BeanCopyUtils.copyProperties(invGoodIssueNote, invGoodIssueNoteItem);
                    BeanCopyUtils.copyProperties(item, invGoodIssueNoteItem);
                    invGoodIssueNoteItem.setQuantity(item.getQuantity().subtract(sumCode));
                    invGoodIssueNoteItems.add(invGoodIssueNoteItem);
                    invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                            .eq(InvReserveInventory::getBusinessOrderItemSid, item.getNoteItemSid())
                    );
                }
                BigDecimal sumitem = item.getQuantity();
                //判断是否出库完全
                if (sumCode.compareTo(sumitem) == -1) {
                    invInventoryDocument.setInOutStatus(false);
                    if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                        invInventoryDocument.setInOutStatusNo(false);
                    }
                    //改变出入库态
                    if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(), new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                .eq(InvGoodIssueNoteItem::getNoteItemSid, item.getNoteItemSid())
                                .set(InvGoodIssueNoteItem::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_NOT)
                        );
                    } else {
                        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(), new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                .eq(InvGoodIssueNoteItem::getNoteItemSid, item.getNoteItemSid())
                                .set(InvGoodIssueNoteItem::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_LI)
                        );
                    }
                } else {
                    invInventoryDocument.setInOutStatusNo(false);
                    //改变出入库态
                    invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(), new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                            .eq(InvGoodIssueNoteItem::getNoteItemSid, item.getNoteItemSid())
                            .set(InvGoodIssueNoteItem::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS)
                    );
                }
            });
            if (CollectionUtil.isNotEmpty(invGoodIssueNoteItems)) {
                invGoodIssueNoteServiceImpl.createInv(invGoodIssueNoteItems);
            }
            //主表出入库状态
            if (!invInventoryDocument.getInOutStatus()) {
                if (invInventoryDocument.getInOutStatusNo()) {
                    invGoodIssueNoteServiceImpl.update(new UpdateWrapper<InvGoodIssueNote>().lambda()
                            .set(InvGoodIssueNote::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_NOT)
                            .set(InvGoodIssueNote::getAccountDate, new Date())
                            .eq(InvGoodIssueNote::getNoteSid, sid));
                } else {
                    invGoodIssueNoteServiceImpl.update(new UpdateWrapper<InvGoodIssueNote>().lambda()
                            .set(InvGoodIssueNote::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_LI)
                            .set(InvGoodIssueNote::getAccountDate, new Date())
                            .eq(InvGoodIssueNote::getNoteSid, sid));
                }
            } else {
                invGoodIssueNoteServiceImpl.update(new UpdateWrapper<InvGoodIssueNote>().lambda()
                        .set(InvGoodIssueNote::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS)
                        .set(InvGoodIssueNote::getAccountDate, new Date())
                        .eq(InvGoodIssueNote::getNoteSid, sid));
            }
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                //动态通知
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                setBussiness(invInventoryDocument, sysBusinessBcst);
                sysBusinessBcst.setTitle("发货单" + referDocumentCode + "中已出库" + sum);
                businessBcst(sysBusinessBcst, invGoodIssueNote.getCreatorAccount());
            }
        }
        //领料单 全部出库和部分出库
        if (DocCategory.REQUESTION_CHK.getCode().equals(code)) {
            InvMaterialRequisition invMaterialRequisition = invMaterialServiceImpl.selectInvMaterialRequisitionById(invInventoryDocument.getReferDocumentSid());
            List<InvMaterialRequisitionItem> list = invMaterialRequisition.getInvMaterialRequisitionItemList();
            List<InvMaterialRequisitionItem> invMaterialRequisitionItems = new ArrayList<>();
            list.forEach(item -> {
                Boolean inOutStatus = invInventoryDocument.getInOutStatus();
                Boolean inOutStatusAll = invInventoryDocument.getInOutStatusAll();
                inOutStatus = true;
                inOutStatusAll = false;
                Long barcodeSid = item.getBarcodeSid();
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                        .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getMaterialRequisitionItemSid())
                        .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                BigDecimal sumCode = BigDecimal.ZERO;
                if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                    sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                BigDecimal subQuantity = item.getQuantity().subtract(sumCode);
                if (BigDecimal.ZERO.compareTo(subQuantity) == 0) {
                    invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                            .eq(InvReserveInventory::getBusinessOrderItemSid, item.getMaterialRequisitionItemSid())
                    );
                    invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                            .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                            .set(InvMaterialRequisitionItem::getReserveStatus, ConstantsEms.RE_STATUS_WY)
                    );
                } else {
                    //更新库存预留
                    InvMaterialRequisitionItem invMaterialRequisitionItem = new InvMaterialRequisitionItem();
                    BeanCopyUtils.copyProperties(invMaterialRequisition, invMaterialRequisitionItem);
                    BeanCopyUtils.copyProperties(item, invMaterialRequisitionItem);
                    invMaterialRequisitionItem.setQuantity(item.getQuantity().subtract(sumCode));
                    invMaterialRequisitionItems.add(invMaterialRequisitionItem);
                    invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                            .eq(InvReserveInventory::getBusinessOrderItemSid, item.getMaterialRequisitionItemSid())
                    );
                }
                BigDecimal sumitem = item.getQuantity();
                //判断是否出库完全
                if (sumCode.compareTo(sumitem) == -1) {
                    invInventoryDocument.setInOutStatus(false);
                    if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                        invInventoryDocument.setInOutStatusNo(false);
                    }
                    //改变采购订单的处理状态
                    if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_NOT)
                        );
                    } else {
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_LI)
                        );
                    }
                } else {
                    invInventoryDocument.setInOutStatusNo(false);
                    //改变采购订单的处理状态
                    invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(), new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                            .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, item.getMaterialRequisitionItemSid())
                            .set(InvMaterialRequisitionItem::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS)
                    );
                }
            });
            if (CollectionUtil.isNotEmpty(invMaterialRequisitionItems)) {
                //更新可用库存
                invMaterialServiceImpl.createInv(invMaterialRequisitionItems);
            }
            //主表出入库状态
            if (!invInventoryDocument.getInOutStatus()) {
                if (invInventoryDocument.getInOutStatusNo()) {
                    invMaterialServiceImpl.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                            .set(InvMaterialRequisition::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_NOT)
                            .eq(InvMaterialRequisition::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid()));
                } else {
                    invMaterialServiceImpl.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                            .set(InvMaterialRequisition::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS_LI)
                            .eq(InvMaterialRequisition::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid()));
                }
            } else {
                invMaterialServiceImpl.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                        .set(InvMaterialRequisition::getInOutStockStatus, ConstantsEms.OUT_STORE_STATUS)
                        .eq(InvMaterialRequisition::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid()));
            }
            invMaterialServiceImpl.update(new UpdateWrapper<InvMaterialRequisition>().lambda()
                    .set(InvMaterialRequisition::getAccountDate, new Date())
                    .eq(InvMaterialRequisition::getMaterialRequisitionSid, sid));
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                //动态通知
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                setBussiness(invInventoryDocument, sysBusinessBcst);
                sysBusinessBcst.setTitle("领料单" + referDocumentCode + "中已出库" + sum);
                businessBcst(sysBusinessBcst, invMaterialRequisition.getCreatorAccount());
            }
        }
        //采购订单、采购退货订单
        if (DocCategory.PURCHASE_ORDER.getCode().equals(code) || DocCategory.RETURN_BACK_PURCHASE.getCode().equals(code)) {
            // 多订单
            if (ConstantsEms.YES.equals(invInventoryDocument.getIsMultiOrder()) || invInventoryDocument.getReferDocumentSid() == null) {
                Map<Long, List<InvInventoryDocumentItem>> itemMap = invInventoryDocumentItemList.stream()
                        .collect(Collectors.groupingBy(item -> item.getReferDocumentSid()));
                for (Long key : itemMap.keySet()) {
                    List<InvInventoryDocumentItem> orderDocumentItemList = itemMap.get(key);
                    PurPurchaseOrder purPurchaseOrder = purPurchaseOrderServiceImpl.selectPurPurchaseOrderById(key);
                    List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
                    list.forEach(item -> {
                        Long barcodeSid = item.getBarcodeSid();
                        List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                .eq(InvInventoryDocumentItem::getReferDocumentSid, key)
                                .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getPurchaseOrderItemSid())
                                .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                        BigDecimal sumCode = BigDecimal.ZERO;
                        if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                            sumCode = bardcodeItems.stream().map(o -> {
                                        if (o.getPriceQuantity() != null) {
                                            return o.getPriceQuantity();
                                        } else {
                                            return o.getQuantity();
                                        }
                                    }
                            ).reduce(BigDecimal.ZERO, BigDecimal::add);
                        }
                        BigDecimal sumitem = item.getQuantity();
                        //判断是否出入库完全
                        if (sumCode.compareTo(sumitem) == -1) {
                            invInventoryDocument.setInOutStatus(false);
                            if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                                invInventoryDocument.setInOutStatusNo(false);
                            }
                            //改变采购订单的处理状态
                            if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                                purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                        .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                        .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                                );
                            } else {
                                purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                        .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                        .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                );
                            }
                        } else {
                            invInventoryDocument.setInOutStatusNo(false);
                            //改变采购订单的处理状态
                            purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                    .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                    .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                            );
                        }
                    });
                    //销售订单主表出入库状态
                    if (!invInventoryDocument.getInOutStatus()) {
                        if (invInventoryDocument.getInOutStatusNo()) {
                            purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                    .set(PurPurchaseOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                                    .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
                        } else {
                            purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                    .set(PurPurchaseOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                    .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
                        }
                    } else {
                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                .set(PurPurchaseOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
                    }
                    if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                        //动态通知
                        SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                        BigDecimal sum = orderDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        sysBusinessBcst.setDocumentSid(purPurchaseOrder.getPurchaseOrderSid())
                                .setDocumentCode(String.valueOf(purPurchaseOrder.getPurchaseOrderCode()))
                                .setNoticeDate(new Date());
                        if (code.equals(DocCategory.PURCHASE_ORDER.getCode())) {
                            sysBusinessBcst.setTitle("供应商：" + purPurchaseOrder.getVendorShortName() + "的采购订单" + referDocumentCode + "中已入库" + sum);
                        } else {
                            sysBusinessBcst.setTitle("供应商：" + purPurchaseOrder.getVendorShortName() + "的采购退货订单" + referDocumentCode + "中已出库" + sum);
                        }
                        businessBcst(sysBusinessBcst, purPurchaseOrder.getBuyer());
                    }
                }
            }
            else {
                PurPurchaseOrder purPurchaseOrder = purPurchaseOrderServiceImpl.selectPurPurchaseOrderById(invInventoryDocument.getReferDocumentSid());
                List<PurPurchaseOrderItem> list = purPurchaseOrder.getPurPurchaseOrderItemList();
                list.forEach(item -> {
                    Long barcodeSid = item.getBarcodeSid();
                    List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                            .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                            .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getPurchaseOrderItemSid())
                            .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                    BigDecimal sumCode = BigDecimal.ZERO;
                    if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                        sumCode = bardcodeItems.stream().map(o -> {
                                    if (o.getPriceQuantity() != null) {
                                        return o.getPriceQuantity();
                                    } else {
                                        return o.getQuantity();
                                    }
                                }
                        ).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    BigDecimal sumitem = item.getQuantity();
                    //判断是否出入库完全
                    if (sumCode.compareTo(sumitem) == -1) {
                        invInventoryDocument.setInOutStatus(false);
                        if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                            invInventoryDocument.setInOutStatusNo(false);
                        }
                        //改变采购订单的处理状态
                        if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                            purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                    .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                    .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                            );
                        } else {
                            purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                    .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                    .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                            );
                        }
                    } else {
                        invInventoryDocument.setInOutStatusNo(false);
                        //改变采购订单的处理状态
                        purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                        );
                    }
                });
                //销售订单主表出入库状态
                if (!invInventoryDocument.getInOutStatus()) {
                    if (invInventoryDocument.getInOutStatusNo()) {
                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                .set(PurPurchaseOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
                    } else {
                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                .set(PurPurchaseOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
                    }
                } else {
                    purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                            .set(PurPurchaseOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE.getCode()) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                            .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrder.getPurchaseOrderSid()));
                }
                if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    //动态通知
                    SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                    BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    sysBusinessBcst.setDocumentSid(purPurchaseOrder.getPurchaseOrderSid())
                            .setDocumentCode(String.valueOf(purPurchaseOrder.getPurchaseOrderCode()))
                            .setNoticeDate(new Date());
                    if (code.equals(DocCategory.PURCHASE_ORDER.getCode())) {
                        sysBusinessBcst.setTitle("供应商：" + purPurchaseOrder.getVendorShortName() + "的采购订单" + referDocumentCode + "中已入库" + sum);
                    } else {
                        sysBusinessBcst.setTitle("供应商：" + purPurchaseOrder.getVendorShortName() + "的采购退货订单" + referDocumentCode + "中已出库" + sum);
                    }
                    businessBcst(sysBusinessBcst, purPurchaseOrder.getBuyer());
                }
            }
        }
        //销售订单、销售退货订单
        if (DocCategory.SALE_ORDER.getCode().equals(code) || DocCategory.RETURN_BACK_SALE.getCode().equals(code)) {
            // 多订单
            if (ConstantsEms.YES.equals(invInventoryDocument.getIsMultiOrder()) || invInventoryDocument.getReferDocumentSid() == null) {
                Map<Long, List<InvInventoryDocumentItem>> itemMap = invInventoryDocumentItemList.stream()
                        .collect(Collectors.groupingBy(item -> item.getReferDocumentSid()));
                for (Long key : itemMap.keySet()) {
                    List<InvInventoryDocumentItem> orderDocumentItemList = itemMap.get(key);
                    SalSalesOrder salSalesOrder = salSalesOrderimpl.selectSalSalesOrderById(key);
                    List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
                    list.forEach(item -> {
                        Long barcodeSid = item.getBarcodeSid();
                        List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                .eq(InvInventoryDocumentItem::getReferDocumentSid, key)
                                .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getSalesOrderItemSid())
                                .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                        BigDecimal sumCode = BigDecimal.ZERO;
                        if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                            sumCode = bardcodeItems.stream().map(o -> {
                                        if (o.getPriceQuantity() != null) {
                                            return o.getPriceQuantity();
                                        } else {
                                            return o.getQuantity();
                                        }
                                    }
                            ).reduce(BigDecimal.ZERO, BigDecimal::add);
                        }
                        BigDecimal sumitem = item.getQuantity();
                        //判断是否出入库完全
                        if (sumCode.compareTo(sumitem) == -1) {
                            // 不完全出入库
                            invInventoryDocument.setInOutStatus(false);
                            if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                                // 部分出入库
                                invInventoryDocument.setInOutStatusNo(false);
                            }
                            if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                                // 完全不出入库
                                salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                        .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                        .set(SalSalesOrderItem::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                                );
                            } else {
                                // 部分出入库
                                salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                        .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                        .set(SalSalesOrderItem::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                );
                            }
                        } else if (sumCode.compareTo(sumitem) != -1) {
                            // 完全出入库
                            invInventoryDocument.setInOutStatusNo(false);
                            //改变交货单中销售订单的处理状态
                            salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                    .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                    .set(SalSalesOrderItem::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                            );
                        }
                    });
                    //销售订单主表出入库状态
                    // 注意这里怎么知道此次的该订单下是否更新到了所有的订单明细
                    if (!invInventoryDocument.getInOutStatus()) {
                        // 不完全出入库
                        if (invInventoryDocument.getInOutStatusNo()) {
                            // 完全不出入库
                            salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                    .set(SalSalesOrder::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                                    .eq(SalSalesOrder::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
                        } else {
                            // 部分出入库
                            salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                    .set(SalSalesOrder::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                    .eq(SalSalesOrder::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
                        }
                    } else {
                        // 完全出入库
                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                .set(SalSalesOrder::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                                .eq(SalSalesOrder::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
                    }
                    if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                        //动态通知
                        SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                        BigDecimal sum = orderDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        sysBusinessBcst.setDocumentSid(key)
                                .setDocumentCode(salSalesOrder.getSalesOrderCode())
                                .setNoticeDate(new Date());
                        if (code.equals(DocCategory.SALE_ORDER.getCode())) {
                            sysBusinessBcst.setTitle("客户：" + salSalesOrder.getCustomerShortName() + "的销售订单" + referDocumentCode + "中已出库" + sum);
                        } else {
                            sysBusinessBcst.setTitle("客户：" + salSalesOrder.getCustomerShortName() + "的销售退货订单" + referDocumentCode + "中已入库" + sum);
                        }
                        businessBcst(sysBusinessBcst, salSalesOrder.getSalePerson());
                    }
                }
            }
            else {
                SalSalesOrder salSalesOrder = salSalesOrderimpl.selectSalSalesOrderById(invInventoryDocument.getReferDocumentSid());
                List<SalSalesOrderItem> list = salSalesOrder.getSalSalesOrderItemList();
                list.forEach(item -> {
                    Long barcodeSid = item.getBarcodeSid();
                    List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                            .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                            .eq(InvInventoryDocumentItem::getReferDocumentItemSid, item.getSalesOrderItemSid())
                            .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                    BigDecimal sumCode = BigDecimal.ZERO;
                    if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                        sumCode = bardcodeItems.stream().map(o -> {
                                    if (o.getPriceQuantity() != null) {
                                        return o.getPriceQuantity();
                                    } else {
                                        return o.getQuantity();
                                    }
                                }
                        ).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    BigDecimal sumitem = item.getQuantity();
                    //判断是否出入库完全
                    if (sumCode.compareTo(sumitem) == -1) {
                        // 不完全出入库
                        invInventoryDocument.setInOutStatus(false);
                        if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                            // 部分出入库
                            invInventoryDocument.setInOutStatusNo(false);
                        }
                        if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                            // 完全没出入库
                            salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                    .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                    .set(SalSalesOrderItem::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                            );
                        } else {
                            // 部分出入库 这个 else 应该可以合并上去的
                            salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                    .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                    .set(SalSalesOrderItem::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                            );
                        }
                    } else if (sumCode.compareTo(sumitem) != -1) {
                        // 订单明细的量比较小了：完全出入库
                        invInventoryDocument.setInOutStatusNo(false);
                        //改变交货单中销售订单的处理状态
                        salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                .set(SalSalesOrderItem::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                        );
                    }
                });
                //销售订单主表出入库状态
                // 注意这里怎么知道此次的该订单下是否更新到了所有的订单明细
                if (!invInventoryDocument.getInOutStatus()) {
                    // 不完全出入库的时候
                    if (invInventoryDocument.getInOutStatusNo()) {
                        // 完全没出入库的时候
                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                .set(SalSalesOrder::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                                .eq(SalSalesOrder::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
                    } else {
                        // 部分出入库的时候
                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                .set(SalSalesOrder::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                .eq(SalSalesOrder::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
                    }
                } else {
                    // 完全出入库的时候
                    salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                            .set(SalSalesOrder::getInOutStockStatus, DocCategory.RETURN_BACK_SALE.getCode().equals(code) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                            .eq(SalSalesOrder::getSalesOrderSid, salSalesOrder.getSalesOrderSid()));
                }
                if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    //动态通知
                    SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                    BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    setBussiness(invInventoryDocument, sysBusinessBcst);
                    if (code.equals(DocCategory.SALE_ORDER.getCode())) {
                        sysBusinessBcst.setTitle("客户：" + salSalesOrder.getCustomerShortName() + "的销售订单" + referDocumentCode + "中已出库" + sum);
                    } else {
                        sysBusinessBcst.setTitle("客户：" + salSalesOrder.getCustomerShortName() + "的销售退货订单" + referDocumentCode + "中已入库" + sum);
                    }
                    businessBcst(sysBusinessBcst, salSalesOrder.getSalePerson());
                }
            }
        }
        //生产订单
        if (DocCategory.PRODUCTION_ORDER.getCode().equals(code)) {
            ManManufactureOrder manManufactureOrder = manOrderServiceImpl.selectManManufactureOrderById(invInventoryDocument.getReferDocumentSid());
            List<ManManufactureOrderProduct> list = manManufactureOrder.getManManufactureOrderProductList();
            list.forEach(item -> {
                Long barcodeSid = item.getBarcodeSid();
                List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                        .eq(InvInventoryDocumentItem::getReferDocumentSid, sid)
                        .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                BigDecimal sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumitem = item.getQuantity();
                //判断是否出入库完全
                if (sumCode.compareTo(sumitem) == -1) {
                    invInventoryDocument.setInOutStatus(false);
                    if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                        invInventoryDocument.setInOutStatusNo(false);
                    }
                } else if (sumCode.compareTo(sumitem) == 1) {

                }
            });
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                //动态通知
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                setBussiness(invInventoryDocument, sysBusinessBcst);
                sysBusinessBcst.setTitle("生产订单" + referDocumentCode + "中已入库" + sum);
                businessBcst(sysBusinessBcst, manManufactureOrder.getCreatorAccount());
            }
        }
        //销售发货单或采购交货单
        if (DocCategory.SALE_CHK.getCode().equals(code) || DocCategory.SALE_RU.getCode().equals(code) || DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(code) || DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(code)) {
            String delivery;
            if (code.equals(DocCategory.SALE_RU.getCode()) || code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
                //1是交货   2是发货
                delivery = ConstantsEms.delivery_Category_XS;
            } else {
                delivery = ConstantsEms.delivery_Category_CG;
            }
            DelDeliveryNote delDeliveryNote = delDeliveryNoteServiceImpl.selectDelDeliveryNoteById(invInventoryDocument.getReferDocumentSid(), delivery);
            //删除预留库存
            invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                    .eq(InvReserveInventory::getDeliveryNoteCode, delDeliveryNote.getDeliveryNoteCode()));
            delDeliveryNoteItemMapper.update(new DelDeliveryNoteItem(), new UpdateWrapper<DelDeliveryNoteItem>().lambda()
                    .eq(DelDeliveryNoteItem::getDeliveryNoteSid, delDeliveryNote.getDeliveryNoteSid())
                    .set(DelDeliveryNoteItem::getReserveStatus, ConstantsEms.RE_STATUS_WY)
            );
            List<DelDeliveryNoteItem> list = delDeliveryNote.getDelDeliveryNoteItemList();
            for (DelDeliveryNoteItem i : list) {
                Long sidoo = i.getSalesOrderSid() != null ? i.getSalesOrderSid() : i.getPurchaseOrderSid();
                i.setOrderSid(sidoo);
            }
            Map<Long, List<DelDeliveryNoteItem>> listMap = list.stream().collect(Collectors.groupingBy(e -> e.getOrderSid()));
            //动态通知
            String buyer = null;
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                BigDecimal sum = invInventoryDocumentItemList.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                setBussiness(invInventoryDocument, sysBusinessBcst);
                // 获取供应商或者客户的简称与名称
                String customerName = "";
                String vendorName = "";
                if (delDeliveryNote.getCustomerSid() != null) {
                    BasCustomer basCustomer = basCustomerMapper.selectById(delDeliveryNote.getCustomerSid());
                    customerName = basCustomer != null ? "" : StrUtil.isNotBlank(basCustomer.getShortName()) ? basCustomer.getShortName() : basCustomer.getCustomerName();
                }
                if (delDeliveryNote.getVendorSid() != null) {
                    BasVendor basVendor = basVendorMapper.selectById(delDeliveryNote.getVendorSid());
                    vendorName = basVendor != null ? "" : StrUtil.isNotBlank(basVendor.getShortName()) ? basVendor.getShortName() : basVendor.getVendorName();
                }
                // 通知标题
                if (code.equals(DocCategory.SALE_RU.getCode())) {
                    sysBusinessBcst.setTitle("客户：" + customerName + "的销售发货单" + referDocumentCode + "中已出库" + sum);
                } else if ((code.equals(DocCategory.SALE_CHK.getCode()))) {
                    sysBusinessBcst.setTitle("供应商：" + vendorName + "的采购交货单" + referDocumentCode + "中已入库" + sum);
                } else if ((code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()))) {
                    sysBusinessBcst.setTitle("客户：" + customerName + "的销售退货发货单" + referDocumentCode + "中已入库" + sum);
                } else if ((code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()))) {
                    sysBusinessBcst.setTitle("供应商：" + vendorName + "的采购退货交货单" + referDocumentCode + "中已出库" + sum);
                }
            }
            // 这边写个map控制动态通知不重复订单sid
            Map<Long, Long> orderSidMap = new HashMap<>();
            // 多订单优化 采购/销售sid去明细取
            if (CollectionUtil.isNotEmpty(list)) {
                Long orderSid = null;
                for (DelDeliveryNoteItem item : list) {
                    Long salesOrderSid = item.getSalesOrderSid();
                    Long purchaseOrderSid = item.getPurchaseOrderSid();
                    List<Long> referDocumentSid = null;
                    List<DelDeliveryNoteItem> deliveryNoteList = null;
                    List<DelDeliveryNoteItem> deliveryNoteListItem = null;
                    if (salesOrderSid != null) {
                        deliveryNoteList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>()
                                .lambda().eq(DelDeliveryNoteItem::getSalesOrderSid, salesOrderSid)
                        );
                        // 控制动态通知
                        if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && !orderSidMap.containsKey(salesOrderSid)) {
                            if (code.equals(DocCategory.SALE_RU.getCode())) {
                                buyer = salSalesOrderMapper.selectById(salesOrderSid).getSalePerson();
                            } else if ((code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()))) {
                                buyer = salSalesOrderMapper.selectById(salesOrderSid).getSalePerson();
                            }
                            sysBusinessBcst.setBusinessBcstSid(null);
                            businessBcst(sysBusinessBcst, buyer);
                            orderSidMap.put(salesOrderSid, 1L);
                        }

                    }
                    if (purchaseOrderSid != null) {
                        deliveryNoteList = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>()
                                .lambda().eq(DelDeliveryNoteItem::getPurchaseOrderSid, purchaseOrderSid)
                        );
                        // 控制动态通知
                        if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && !orderSidMap.containsKey(purchaseOrderSid)) {
                            if ((code.equals(DocCategory.SALE_CHK.getCode()))) {
                                buyer = purPurchaseOrderMapper.selectById(purchaseOrderSid).getBuyer();
                            } else if ((code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()))) {
                                buyer = purPurchaseOrderMapper.selectById(purchaseOrderSid).getBuyer();
                            }
                            sysBusinessBcst.setBusinessBcstSid(null);
                            businessBcst(sysBusinessBcst, buyer);
                            orderSidMap.put(purchaseOrderSid, 1L);
                        }
                    }
                    if (CollectionUtil.isNotEmpty(deliveryNoteList)) {
                        referDocumentSid = deliveryNoteList.stream().map(o -> o.getDeliveryNoteSid()).collect(Collectors.toList());
                        deliveryNoteListItem = delDeliveryNoteItemMapper.selectList(new QueryWrapper<DelDeliveryNoteItem>().lambda()
                                .in(DelDeliveryNoteItem::getDeliveryNoteSid, referDocumentSid)
                        );
                        if (CollectionUtil.isNotEmpty(deliveryNoteListItem)) {
                            Long barcodeSid = item.getBarcodeSid();
                            //关联订单的所有的明细行
                            List<Long> longs = deliveryNoteListItem.stream().map(li -> {
                                if (item.getPurchaseOrderItemSid() != null) {
                                    if (li.getPurchaseOrderItemSid().toString().equals(item.getPurchaseOrderItemSid().toString())) {
                                        return li.getDeliveryNoteItemSid();
                                    } else {
                                        return null;
                                    }
                                } else {
                                    if (li.getSalesOrderItemSid().toString().equals(item.getSalesOrderItemSid().toString())) {
                                        return li.getDeliveryNoteItemSid();
                                    } else {
                                        return null;
                                    }
                                }
                            }).collect(Collectors.toList());
                            longs = longs.stream().filter(li -> li != null).collect(Collectors.toList());
                            List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                                    .in(InvInventoryDocumentItem::getReferDocumentSid, referDocumentSid)
                                    .in(InvInventoryDocumentItem::getReferDocumentItemSid, longs)
                                    .eq(InvInventoryDocumentItem::getBarcodeSid, barcodeSid));
                            BigDecimal sumCode = BigDecimal.ZERO;
                            if (CollectionUtil.isNotEmpty(bardcodeItems)) {
                                sumCode = bardcodeItems.stream().map(o -> {
                                            if (o.getPriceQuantity() != null) {
                                                return o.getPriceQuantity();
                                            } else {
                                                return o.getQuantity();
                                            }
                                        }
                                ).reduce(BigDecimal.ZERO, BigDecimal::add);
                            }
                            BigDecimal sumitem = item.getQuantity();
                            BigDecimal sumitemDelivery = item.getDeliveryQuantity();
                            //判断是否出入库完全
                            if (orderSid != null && !orderSid.equals(salesOrderSid) && !orderSid.equals(purchaseOrderSid)) {
                                invInventoryDocument.setInOutStatus(true);
                                invInventoryDocument.setInOutStatusNo(true);
                            }
                            if (sumCode.compareTo(sumitem) == -1) {
                                invInventoryDocument.setInOutStatus(false);
                                if (sumCode.compareTo(BigDecimal.ZERO) != 0) {
                                    invInventoryDocument.setInOutStatusNo(false);
                                }
                                //改变交货单中销售订单的处理状态
                                if (salesOrderSid != null) {
                                    if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                                        salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                                .eq(SalSalesOrderItem::getSalesOrderSid, salesOrderSid)
                                                .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                                .set(SalSalesOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                                        );
                                    } else {
                                        salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                                .eq(SalSalesOrderItem::getSalesOrderSid, salesOrderSid)
                                                .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                                .set(SalSalesOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                        );
                                    }
                                }
                                if (purchaseOrderSid != null) {
                                    if (sumCode.compareTo(BigDecimal.ZERO) == 0) {
                                        purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                                .eq(PurPurchaseOrderItem::getPurchaseOrderSid, purchaseOrderSid)
                                                .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                                .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                                        );
                                    } else {
                                        purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                                .eq(PurPurchaseOrderItem::getPurchaseOrderSid, purchaseOrderSid)
                                                .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                                .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                        );
                                    }
                                }
                            }
                            else if (sumCode.compareTo(sumitem) != -1) {
                                invInventoryDocument.setInOutStatusNo(false);
                                //改变交货单中销售订单的处理状态
                                if (salesOrderSid != null) {
                                    salSalesOrderItemMapper.update(new SalSalesOrderItem(), new UpdateWrapper<SalSalesOrderItem>().lambda()
                                            .eq(SalSalesOrderItem::getSalesOrderSid, salesOrderSid)
                                            .eq(SalSalesOrderItem::getSalesOrderItemSid, item.getSalesOrderItemSid())
                                            .set(SalSalesOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                                    );
                                }
                                if (purchaseOrderSid != null) {
                                    purPurchaseOrderItemMapper.update(new PurPurchaseOrderItem(), new UpdateWrapper<PurPurchaseOrderItem>().lambda()
                                            .eq(PurPurchaseOrderItem::getPurchaseOrderSid, purchaseOrderSid)
                                            .eq(PurPurchaseOrderItem::getPurchaseOrderItemSid, item.getPurchaseOrderItemSid())
                                            .set(PurPurchaseOrderItem::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                                    );
                                }
                            }
                            // 存当前一个sid给下一个用
                            orderSid = salesOrderSid != null ? salesOrderSid : purchaseOrderSid;
                            InvInventoryDocument document = null;
                            List<InvInventoryDocumentItem> nowList = null;
                            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(invInventoryDocument.getDocumentTypeInv())) {
                                document = invInventoryDocumentMapper.selectOne(new QueryWrapper<InvInventoryDocument>().lambda()
                                        .eq(InvInventoryDocument::getHandleStatus, HandleStatus.POSTING.getCode())
                                        .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_ZG)
                                        .eq(InvInventoryDocument::getReferDocumentSid, invInventoryDocument.getReferDocumentSid())
                                );
                                //获取当前的出入量量--关联sid
                                Long documentSid = document.getInventoryDocumentSid();
                                nowList =
                                        bardcodeItems.stream()
                                                .filter(li -> li.getReferDocumentSid().toString().equals(invInventoryDocument.getReferDocumentSid().toString()) && documentSid.toString().equals(li.getInventoryDocumentSid().toString()))
                                                .collect(Collectors.toList());
                            }
                            BigDecimal quantity = BigDecimal.ZERO;
                            BigDecimal priceQuantity = BigDecimal.ZERO;
                            if (CollectionUtil.isNotEmpty(nowList)) {
                                quantity = nowList.get(0).getQuantity();
                                priceQuantity = nowList.get(0).getPriceQuantity();
                            }
                            //冲销
                            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                                priceQuantity = BigDecimal.ZERO;
                            }
                            if (priceQuantity.compareTo(BigDecimal.ZERO) != 0) {
                                invInventoryDocument.setInOutStatusDelDeliveryNo(true);
                            }
                            //销售发货单出入库状态
                            if (priceQuantity.compareTo(sumitemDelivery) == -1) {
                                invInventoryDocument.setInOutStatusDelDelivery(false);
                            }
                            //改变交货单的出入库量
                            DelDeliveryNoteItem delDeliveryNoteItem = new DelDeliveryNoteItem();
                            delDeliveryNoteItem.setDeliveryNoteItemSid(item.getDeliveryNoteItemSid())
                                    .setInOutStockQuantity(priceQuantity);
                            delDeliveryNoteItemMapper.updateById(delDeliveryNoteItem);
                            // 改变订单主表的出入库状态
                            if (salesOrderSid != null) {
                                //改变销售订单主表 出入库状态
                                if (!invInventoryDocument.getInOutStatus()) {
                                    if (invInventoryDocument.getInOutStatusNo()) {
                                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                                .set(SalSalesOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                                                .eq(SalSalesOrder::getSalesOrderSid, salesOrderSid));
                                    } else {
                                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                                .set(SalSalesOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                                .eq(SalSalesOrder::getSalesOrderSid, salesOrderSid));
                                    }
                                } else {
                                    List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemMapper.selectList(new QueryWrapper<SalSalesOrderItem>().lambda()
                                            .eq(SalSalesOrderItem::getSalesOrderSid, salesOrderSid)
                                    );
                                    //查看是否存在还没被引用的订单
                                    if (salSalesOrderItems.size() > listMap.get(salesOrderSid).size()) {
                                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                                .set(SalSalesOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                                .eq(SalSalesOrder::getSalesOrderSid, salesOrderSid));
                                    } else {
                                        salSalesOrderimpl.update(new UpdateWrapper<SalSalesOrder>().lambda()
                                                .set(SalSalesOrder::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                                                .eq(SalSalesOrder::getSalesOrderSid, salesOrderSid));
                                    }
                                }
                            }
                            else if (purchaseOrderSid != null) {
                                //改变采购订单主表 出入库状态
                                if (!invInventoryDocument.getInOutStatus()) {
                                    if (invInventoryDocument.getInOutStatusNo()) {
                                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                                .set(PurPurchaseOrder::getInOutStockStatus, DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(code) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSid));
                                    } else {
                                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                                .set(PurPurchaseOrder::getInOutStockStatus, DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(code) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSid));
                                    }
                                } else {
                                    List<PurPurchaseOrderItem> purPurchaseOrderItems = purPurchaseOrderItemMapper.selectList(new QueryWrapper<PurPurchaseOrderItem>().lambda()
                                            .eq(PurPurchaseOrderItem::getPurchaseOrderSid, purchaseOrderSid)
                                    );
                                    if (purPurchaseOrderItems.size() > listMap.get(purchaseOrderSid).size()) {
                                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                                .set(PurPurchaseOrder::getInOutStockStatus, DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(code) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSid));
                                    } else {
                                        purPurchaseOrderMapper.update(new PurPurchaseOrder(), new UpdateWrapper<PurPurchaseOrder>().lambda()
                                                .set(PurPurchaseOrder::getInOutStockStatus, DocCategory.RETURN_BACK_PURCHASE_R.getCode().equals(code) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                                                .eq(PurPurchaseOrder::getPurchaseOrderSid, purchaseOrderSid));
                                    }
                                }
                            }
                        }
                    }
                }
                //
                if (!invInventoryDocument.getInOutStatusDelDelivery()) {
                    if (DocCategory.SALE_RU.getCode().equals(code) || DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(code)) {
                        if (!invInventoryDocument.getInOutStatusDelDeliveryNo()) {
                            delDeliveryNoteServiceImpl.update(new UpdateWrapper<DelDeliveryNote>().lambda()
                                    .set(DelDeliveryNote::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_NOT : ConstantsEms.OUT_STORE_STATUS_NOT)
                                    .eq(DelDeliveryNote::getDeliveryNoteSid, sid));
                        } else {
                            delDeliveryNoteServiceImpl.update(new UpdateWrapper<DelDeliveryNote>().lambda()
                                    .set(DelDeliveryNote::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS_LI : ConstantsEms.OUT_STORE_STATUS_LI)
                                    .eq(DelDeliveryNote::getDeliveryNoteSid, sid));
                        }
                    } else {
                        if (!invInventoryDocument.getInOutStatusDelDeliveryNo()) {
                            delDeliveryNoteServiceImpl.update(new UpdateWrapper<DelDeliveryNote>().lambda()
                                    .set(DelDeliveryNote::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()) ? ConstantsEms.OUT_STORE_STATUS_NOT : ConstantsEms.IN_STORE_STATUS_NOT)
                                    .eq(DelDeliveryNote::getDeliveryNoteSid, sid));
                        } else {
                            delDeliveryNoteServiceImpl.update(new UpdateWrapper<DelDeliveryNote>().lambda()
                                    .set(DelDeliveryNote::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()) ? ConstantsEms.OUT_STORE_STATUS_LI : ConstantsEms.IN_STORE_STATUS_LI)
                                    .eq(DelDeliveryNote::getDeliveryNoteSid, sid));
                        }
                    }
                } else {
                    if (DocCategory.SALE_RU.getCode().equals(code) || DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(code)) {
                        delDeliveryNoteServiceImpl.update(new UpdateWrapper<DelDeliveryNote>().lambda()
                                .set(DelDeliveryNote::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode()) ? ConstantsEms.IN_STORE_STATUS : ConstantsEms.OUT_STORE_STATUS)
                                .eq(DelDeliveryNote::getDeliveryNoteSid, sid));
                    } else {
                        delDeliveryNoteServiceImpl.update(new UpdateWrapper<DelDeliveryNote>().lambda()
                                .set(DelDeliveryNote::getInOutStockStatus, code.equals(DocCategory.RETURN_BACK_PURCHASE_R.getCode()) ? ConstantsEms.OUT_STORE_STATUS : ConstantsEms.IN_STORE_STATUS)
                                .eq(DelDeliveryNote::getDeliveryNoteSid, sid));
                    }
                }
            }
        }
    }

    /**
     * 校验明细的价格必须有数据
     */
    public void judgePrice(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String isFinanceBookYfzg = invInventoryDocument.getIsFinanceBookYfzg();
        String isFinanceBookYszg = invInventoryDocument.getIsFinanceBookYszg();
        if (DocumentCategory.RU.getCode().equals(invInventoryDocument.getDocumentCategory()) || DocumentCategory.CHK.getCode().equals(invInventoryDocument.getDocumentCategory())) {
            if (ConstantsEms.YES.equals(isFinanceBookYfzg) || ConstantsEms.YES.equals(isFinanceBookYszg)) {
                invInventoryDocumentItemList.forEach(li -> {
                    if (li.getPrice() == null && !ConstantsEms.YES.equals(li.getFreeFlag())) {
                        throw new CustomException("存在明细的价格为空，无法出入库！");
                    }
                });
            }
        }
        // 若所选业务单号的“是否生成财务应收/应付暂估流水”为“是”，要校验明细的税率是否为空，若明细的税率为空，提示：存在明细的税率为空，无法出库！
        if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(invInventoryDocument.getDocumentCategory())
                && CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
            boolean flag = invInventoryDocument.getInvInventoryDocumentItemList().stream().anyMatch(o->o.getOrderTaxRate() == null
                    && !ConstantsEms.YES.equals(o.getOrderFreeFlag())
                    && (ConstantsEms.YES.equals(o.getIsFinanceBookYfzg()) || ConstantsEms.YES.equals(o.getIsFinanceBookYszg())));
            if (flag) {
                throw new CustomException("存在明细的税率为空，无法出库！");
            }
        }
    }

    /**
     * 生成应付暂估财务流水
     */
    @Override
    public void createpayment(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String code = invInventoryDocument.getMovementType();
        String documentCategory = invInventoryDocument.getDocumentCategory();
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        String isFinanceBookYfzg = invInventoryDocument.getIsFinanceBookYfzg();
        String isReturnGoods = invInventoryDocument.getIsReturnGoods();
        if (ConstantsEms.YES.equals(isFinanceBookYfzg)) {
            FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
            BeanCopyUtils.copyProperties(invInventoryDocument, finBookPaymentEstimation);
            finBookPaymentEstimation.setHandleStatus(ConstantsEms.CHECK_STATUS);
            finBookPaymentEstimation.setBookSourceCategory(ConstantsEms.PURCHASE_CATEGORY);
            if (code.equals(ConstantsEms.PURCHASE_ORDER_RU_L) || code.equals(ConstantsEms.PURCHASE_ORDER_RU_R)) {
                finBookPaymentEstimation.setBookSourceCategory(ConstantsEms.PURCHASE_CATEGORY_BACK);
            }

            // 若作业类型为“按采购订单”、“按采购交货单”，若生成暂估流水，默认生成暂估流水的“是否退货”为“否”
            // 默认“已核销金额（含税）付款”、“核销中金额（含税）付款”、“已核销金额（含税）开票”、“核销中金额（含税）开票”为0
            // 若作业类型为“按销售退货订单”、“按销售退货收货单”，若生成暂估流水，默认生成暂估流水的“是否退货”为“是”
            if (ConstantsInventory.MOVEMENT_TYPE_SR01.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SR02.equals(invInventoryDocument.getMovementType())) {
                finBookPaymentEstimation.setIsTuihuo(ConstantsEms.NO);
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SC03.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SC04.equals(invInventoryDocument.getMovementType())) {
                finBookPaymentEstimation.setIsTuihuo(ConstantsEms.YES);
            }
            // 对账账期格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            finBookPaymentEstimation.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
            if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
                ArrayList<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = new ArrayList<>();
                invInventoryDocumentItemList.forEach(item -> {
                    if (!ConstantsEms.YES.equals(item.getFreeFlag()) && item.getInvPrice() != null && BigDecimal.ZERO.compareTo(item.getInvPrice()) != 0) {
                        FinBookPaymentEstimationItem finBookPaymentEstimationItem = new FinBookPaymentEstimationItem();
                        BeanCopyUtils.copyProperties(item, finBookPaymentEstimationItem);
                        // 流水明细，根据“采购合同SID”保存对应的“采购合同号”，根据“采购订单SID”保存对应的“采购订单号”，根据对应的“采购交货单SID”保存对应的“采购交货单号”
                        PurPurchaseContract purchaseContract = purchaseContractMapper.selectOne(new QueryWrapper<PurPurchaseContract>()
                                .lambda().eq(PurPurchaseContract::getPurchaseContractSid,item.getPurchaseContractSid()));
                        PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectOne(new QueryWrapper<PurPurchaseOrder>()
                                .lambda().eq(PurPurchaseOrder::getPurchaseOrderSid,item.getPurchaseOrderSid()));
                        DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectOne(new QueryWrapper<DelDeliveryNote>()
                                .lambda().eq(DelDeliveryNote::getDeliveryNoteSid,invInventoryDocument.getDeliveryNoteSid()));
                        if(purchaseContract != null){
                            finBookPaymentEstimationItem.setPurchaseContractCode(purchaseContract.getPurchaseContractCode());
                        }else{
                            finBookPaymentEstimationItem.setPurchaseContractCode(item.getPurchaseContractCode());
                        }
                        if(purPurchaseOrder != null){
                            finBookPaymentEstimationItem.setPurchaseOrderCode(purPurchaseOrder.getPurchaseOrderCode());
                        }else{
                            finBookPaymentEstimationItem.setPurchaseOrderCode(Long.valueOf(item.getPurchaseOrderCode()));
                        }
                        if(delDeliveryNote != null){
                            finBookPaymentEstimationItem.setDeliveryNoteCode(delDeliveryNote.getDeliveryNoteCode());
                        }else{
                            finBookPaymentEstimationItem.setDeliveryNoteCode(invInventoryDocument.getDeliveryNoteCode());
                        }
                        int itemNum = item.getItemNum();
                        finBookPaymentEstimationItem.setCreateDate(new Date())
                                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                .setPriceTax(item.getInvPriceTax())
                                .setItemNum(Long.valueOf(itemNum))
                                .setUnitPrice(item.getUnitPrice())
                                .setQuantity(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity())
                                .setPrice(item.getInvPrice())
                                .setReferDocSid(invInventoryDocument.getInventoryDocumentSid())
                                .setReferDocItemSid(item.getInventoryDocumentItemSid())
                                .setPurchaseContractSid(item.getPurchaseContractSid())
                                .setPurchaseOrderSid(item.getPurchaseOrderSid())
                                .setDeliveryNoteSid(invInventoryDocument.getDeliveryNoteSid())
                                .setClearStatus("WHX")
                                .setIsBusinessVerify(ConstantsEms.NO)
                                .setCurrencyAmountTaxFkYhx(BigDecimal.valueOf(0))
                                .setCurrencyAmountTaxFkHxz(BigDecimal.valueOf(0))
                                .setCurrencyAmountTaxKpYhx(BigDecimal.valueOf(0))
                                .setCurrencyAmountTaxKpHxz(BigDecimal.valueOf(0))
                                .setReferDocCategory(documentCategory)
                                .setClearStatusMoney("WHX")
                                .setClearStatusQuantity("WHX");
                        // 对账账期
                        if (invInventoryDocument.getAccountDate() != null) {
                            finBookPaymentEstimationItem.setBusinessVerifyPeriod(sdf.format(invInventoryDocument.getAccountDate()));
                        }
                        if (ConstantsEms.YES.equals(isReturnGoods)) {
                            if (item.getQuantity() != null && item.getInvPriceTax() != null) {
                                finBookPaymentEstimationItem.setQuantity((item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity().abs()).multiply(new BigDecimal(-1)));
                                finBookPaymentEstimationItem.setCurrencyAmountTax(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity().multiply(new BigDecimal(-1).multiply(item.getInvPriceTax())));
                            }
                        }
                        if (item.getInvPriceTax() != null) {
                            finBookPaymentEstimationItem.setCurrencyAmountTax(finBookPaymentEstimationItem.getQuantity().multiply(item.getInvPriceTax()));
                        }
                        finBookPaymentEstimationItems.add(finBookPaymentEstimationItem);
                    }
                });
                finBookPaymentEstimation.setItemList(finBookPaymentEstimationItems);
            }
            List<FinBookPaymentEstimationItem> itemList = finBookPaymentEstimation.getItemList();
            if (CollectionUtil.isNotEmpty(itemList)) {
                finBookPaymentEstimationServiceImpl.insertFinBookPaymentEstimation(finBookPaymentEstimation);
            }
        }
        //冲销
        if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
            List<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = finBookPaymentEstimationItemMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>().lambda()
                    .eq(FinBookPaymentEstimationItem::getReferDocSid, invInventoryDocument.getPreInventoryDocumentSid())
            );
            if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItems)) {
                FinBookPaymentEstimation finBookPaymentEstimation = finBookPaymentEstimationMapper.selectById(finBookPaymentEstimationItems.get(0).getBookPaymentEstimationSid());
                //修改原流水
                UpdateWrapper<FinBookPaymentEstimationItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.in("book_payment_estimation_sid", finBookPaymentEstimation.getBookPaymentEstimationSid())
                        .set("clear_status", ConstantsFinance.CLEAR_STATUS_QHX)
                        .set("clear_status_quantity", ConstantsFinance.CLEAR_STATUS_QHX)
                        .set("clear_status_money", ConstantsFinance.CLEAR_STATUS_QHX)
                        .set("currency_amount_tax_hxz", BigDecimal.ZERO)
                        .set("quantity_hxz", BigDecimal.ZERO);
                updateWrapper.setSql("currency_amount_tax_yhx = currency_amount_tax , quantity_yhx = quantity");
                int row = finBookPaymentEstimationItemMapper.update(null, updateWrapper);
                finBookPaymentEstimation.setBookPaymentEstimationSid(null)
                        .setBookFeature(ConstantsFinance.BOOK_FEATURE_CX)
                        .setAccountDate(new Date())
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setBookPaymentEstimationCode(null);

                // 冲销生成的流水的关联sid应该取自冲销凭证的sid 根据商品条码找到对应的冲销明细sid
                Map<Long, Long> methodMaps = invInventoryDocumentItemList.stream().collect(Collectors.toMap
                        (InvInventoryDocumentItem::getPreInventoryDocumentItemSid, InvInventoryDocumentItem::getInventoryDocumentItemSid, (key1, key2) -> key2));

                finBookPaymentEstimationItems.forEach(li -> {
                    if (li.getQuantity() != null) {
                        li.setQuantity(li.getQuantity().multiply(new BigDecimal(-1)));
                    }
                    if (li.getCurrencyAmountTax() != null) {
                        li.setCurrencyAmountTax(li.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
                    }
                    li.setReferDocSid(invInventoryDocument.getInventoryDocumentSid());
                    li.setBookPaymentEstimationItemSid(null);
                    // 冲销生成的流水的关联sid应该取自冲销凭证的sid
                    Long itemSid = methodMaps.get(li.getReferDocItemSid());
                    li.setReferDocCode(invInventoryDocument.getInventoryDocumentCode())
                            .setReferDocItemSid(itemSid);
                });
                finBookPaymentEstimation.setItemList(finBookPaymentEstimationItems);
                finBookPaymentEstimationServiceImpl.insertFinBookPaymentEstimation(finBookPaymentEstimation);
            }
        }
    }

    /**
     * 生成应收暂估流水
     */
    @Override
    public void createReceipt(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String code = invInventoryDocument.getMovementType();
        String documentCategory = invInventoryDocument.getDocumentCategory();
        String specialStock = invInventoryDocument.getSpecialStock();
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        String isFinanceBookYszg = invInventoryDocument.getIsFinanceBookYszg();
        String isReturnGoods = invInventoryDocument.getIsReturnGoods();
        //应收暂估流水：销售退货订单、销售退货收货单
        if (ConstantsEms.YES.equals(isFinanceBookYszg)) {
            FinBookReceiptEstimation finBookReceiptEstimation = new FinBookReceiptEstimation();
            BeanCopyUtils.copyProperties(invInventoryDocument, finBookReceiptEstimation);
            finBookReceiptEstimation.setBookSourceCategory(ConstantsEms.SALE_CATEGORY);
            finBookReceiptEstimation.setHandleStatus(ConstantsEms.CHECK_STATUS);
            finBookReceiptEstimation.setProductSeasonSid(invInventoryDocument.getProductSeasonSid());

            // 若作业类型为“按销售订单”、“按销售发货单”，若生成暂估流水，默认生成暂估流水的“是否退货”为“否”
            // 默认“已核销金额（含税）收款”、“核销中金额（含税）收款”、“已核销金额（含税）开票”、“核销中金额（含税）开票”为0
            // 若作业类型为“按采购退货订单”、“按采购退货发货单” ，若生成暂估流水，默认生成暂估流水的“是否退货”为“是”
            if (ConstantsInventory.MOVEMENT_TYPE_SC01.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SC02.equals(invInventoryDocument.getMovementType())) {
                finBookReceiptEstimation.setIsTuihuo(ConstantsEms.NO);
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SR03.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SR04.equals(invInventoryDocument.getMovementType())) {
                finBookReceiptEstimation.setIsTuihuo(ConstantsEms.YES);
            }
            if (code.equals(ConstantsEms.SALE_ORDER_RU_R) || code.equals(ConstantsEms.SALE_ORDER_RU_L)) {
                finBookReceiptEstimation.setBookSourceCategory(ConstantsEms.SALE_CATEGORY_BACK);
            }
            // 对账账期格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            finBookReceiptEstimation.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
            if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
                ArrayList<FinBookReceiptEstimationItem> finBookPaymentEstimationItems = new ArrayList<>();
                invInventoryDocumentItemList.forEach(item -> {
                    if (!ConstantsEms.YES.equals(item.getFreeFlag()) && item.getInvPrice() != null && BigDecimal.ZERO.compareTo(item.getInvPrice()) != 0) {
                        FinBookReceiptEstimationItem finBookReceipEstimationItem = new FinBookReceiptEstimationItem();
                        BeanCopyUtils.copyProperties(item, finBookReceipEstimationItem);
                        // 根据“销售合同SID”保存对应的“销售合同号”，根据“销售订单SID”保存对应的“销售订单号”，根据对应的“销售发货单SID”保存对应的“销售发货单号”
                        SalSaleContract salSaleContract = saleContractMapper.selectOne(new QueryWrapper<SalSaleContract>().lambda()
                                .eq(SalSaleContract::getSaleContractSid,item.getSaleContractSid()));
                        SalSalesOrder salSalesOrder = salSalesOrderMapper.selectOne(new QueryWrapper<SalSalesOrder>().lambda()
                                .eq(SalSalesOrder::getSalesOrderSid,item.getSalesOrderSid()));
                        DelDeliveryNote delDeliveryNote = delDeliveryNoteMapper.selectOne(new QueryWrapper<DelDeliveryNote>().lambda()
                                .eq(DelDeliveryNote::getDeliveryNoteSid,invInventoryDocument.getDeliveryNoteSid()));
                        if(salSaleContract != null){
                            finBookReceipEstimationItem.setSaleContractCode(salSaleContract.getSaleContractCode());
                        }else{
                            finBookReceipEstimationItem.setSaleContractCode(item.getSaleContractCode());
                        }
                        if(salSalesOrder != null){
                            finBookReceipEstimationItem.setSalesOrderCode(salSalesOrder.getSalesOrderCode());
                        }else{
                            finBookReceipEstimationItem.setSalesOrderCode(item.getSalesOrderCode());
                        }
                        if(delDeliveryNote != null){
                            finBookReceipEstimationItem.setDeliveryNoteCode(delDeliveryNote.getDeliveryNoteCode());
                        }else{
                            finBookReceipEstimationItem.setDeliveryNoteCode(invInventoryDocument.getDeliveryNoteCode());
                        }
                        int itemNum = item.getItemNum();
                        finBookReceipEstimationItem.setCreateDate(new Date())
                                .setItemNum(Long.valueOf(itemNum))
                                .setPriceTax(item.getInvPriceTax())
                                .setQuantity(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity())
                                .setPrice(item.getInvPrice())
                                .setUnitPrice(item.getUnitPrice())
                                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                .setReferDocSid(invInventoryDocument.getInventoryDocumentSid())
                                .setReferDocItemSid(item.getInventoryDocumentItemSid())
                                .setSaleContractSid(item.getSaleContractSid())
                                .setSalesOrderSid(item.getSalesOrderSid())

                                .setDeliveryNoteSid(invInventoryDocument.getDeliveryNoteSid())
                                .setClearStatus("WHX")
                                .setIsBusinessVerify(ConstantsEms.NO)
                                .setCurrencyAmountTaxSkYhx(BigDecimal.valueOf(0))
                                .setCurrencyAmountTaxSkHxz(BigDecimal.valueOf(0))
                                .setCurrencyAmountTaxDpYhx(BigDecimal.valueOf(0))
                                .setCurrencyAmountTaxDpHxz(BigDecimal.valueOf(0))
                                .setReferDocCategory(documentCategory)
                                .setClearStatusMoney("WHX")
                                .setClearStatusQuantity("WHX");
                        // 对账账期
                        if (invInventoryDocument.getAccountDate() != null) {
                            finBookReceipEstimationItem.setBusinessVerifyPeriod(sdf.format(invInventoryDocument.getAccountDate()));
                        }
                        String salesOrderCode = invInventoryDocument.getSalesOrderCode();
                        if (salesOrderCode != null) {
                            finBookReceipEstimationItem.setSalesOrderCode(String.valueOf(salesOrderCode));
                        }
                        if (ConstantsEms.YES.equals(isReturnGoods)) {
                            if (item.getQuantity() != null && item.getInvPriceTax() != null) {
                                finBookReceipEstimationItem.setQuantity((item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity().abs()).multiply(new BigDecimal(-1)));
                                finBookReceipEstimationItem.setCurrencyAmountTax(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity().multiply(new BigDecimal(-1).multiply(item.getInvPriceTax())));
                            }
                        }
                        if (item.getInvPriceTax() != null) {
                            finBookReceipEstimationItem.setCurrencyAmountTax(finBookReceipEstimationItem.getQuantity().multiply(item.getInvPriceTax()));
                        }
                        finBookPaymentEstimationItems.add(finBookReceipEstimationItem);
                    }
                });
                finBookReceiptEstimation.setItemList(finBookPaymentEstimationItems);
                if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItems)) {
                    finBookReceiptEstimationServiceImpl.insertFinBookReceiptEstimation(finBookReceiptEstimation);
                }
            }
        }
        if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
            List<FinBookReceiptEstimationItem> finBookReceiptEstimationItems = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>().lambda()
                    .eq(FinBookReceiptEstimationItem::getReferDocSid, invInventoryDocument.getPreInventoryDocumentSid())
            );
            if (CollectionUtil.isNotEmpty(finBookReceiptEstimationItems)) {
                FinBookReceiptEstimation finBookReceiptEstimation = finBookReceiptEstimationMapper.selectById(finBookReceiptEstimationItems.get(0).getBookReceiptEstimationSid());
                //修改原流水
                UpdateWrapper<FinBookReceiptEstimationItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("clear_status", ConstantsFinance.CLEAR_STATUS_QHX);
                updateWrapper.set("clear_status_quantity", ConstantsFinance.CLEAR_STATUS_QHX);
                updateWrapper.set("clear_status_money", ConstantsFinance.CLEAR_STATUS_QHX);
                updateWrapper.set("currency_amount_tax_hxz", BigDecimal.ZERO);
                updateWrapper.set("quantity_hxz", BigDecimal.ZERO);
                updateWrapper.setSql("currency_amount_tax_yhx = currency_amount_tax , quantity_yhx = quantity");
                updateWrapper.in("book_receipt_estimation_sid", finBookReceiptEstimation.getBookReceiptEstimationSid());
                int row = finBookReceiptEstimationItemMapper.update(null, updateWrapper);
                finBookReceiptEstimation.setBookReceiptEstimationSid(null)
                        .setBookFeature(ConstantsFinance.BOOK_FEATURE_CX)
                        .setAccountDate(new Date())
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setBookReceiptEstimationCode(null);

                // 冲销生成的流水的关联sid应该取自冲销凭证的sid 根据商品条码找到对应的冲销明细sid
                Map<Long, Long> methodMaps = invInventoryDocumentItemList.stream().collect(Collectors.toMap
                        (InvInventoryDocumentItem::getPreInventoryDocumentItemSid, InvInventoryDocumentItem::getInventoryDocumentItemSid, (key1, key2) -> key2));

                finBookReceiptEstimationItems.forEach(li -> {
                    if (li.getQuantity() != null) {
                        li.setQuantity(li.getQuantity().multiply(new BigDecimal(-1)));
                    }
                    if (li.getCurrencyAmountTax() != null) {
                        li.setCurrencyAmountTax(li.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
                    }
                    li.setReferDocSid(invInventoryDocument.getInventoryDocumentSid());
                    li.setBookReceiptEstimationItemSid(null);
                    // 冲销生成的流水的关联sid应该取自冲销凭证的sid
                    Long itemSid = methodMaps.get(li.getReferDocItemSid());
                    li.setReferDocCode(invInventoryDocument.getInventoryDocumentCode())
                            .setReferDocItemSid(itemSid);
                });
                finBookReceiptEstimation.setItemList(finBookReceiptEstimationItems);
                finBookReceiptEstimationServiceImpl.insertFinBookReceiptEstimation(finBookReceiptEstimation);
            }
        }

    }

    /**
     * 生成供应商寄售待结算台账
     */
    public void createPurRecordVendorConsign(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String code = invInventoryDocument.getMovementType();
        String specialStock = invInventoryDocument.getSpecialStock();
        String referDocumentCode = invInventoryDocument.getReferDocCategory();
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();
        //供应商寄售待结算台账 扣减
        if (PurRecordVendorReduce(code) || (ConstantsEms.VEN_CU.equals(specialStock) && DocCategory.RETURN_BACK_SALE.getCode().equals(referDocumentCode)) || (ConstantsEms.VEN_CU.equals(specialStock) && DocCategory.RETURN_BACK_SALE_RECEPIT.getCode().equals(referDocumentCode))) {
            invInventoryDocumentItemList.forEach(o -> {
                PurRecordVendorConsign purRecordVendorConsign = new PurRecordVendorConsign();
                BeanCopyUtils.copyProperties(invInventoryDocument, purRecordVendorConsign);
                BeanCopyUtils.copyProperties(o, purRecordVendorConsign);
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    purRecordVendorConsign.setQuantity(purRecordVendorConsign.getQuantity().abs().multiply(new BigDecimal(-1)));
                }
                purRecordVendorConsign.setType(ConstantsEms.CHU_KU);
                purRecordVendorConsignServiceImpl.insertPurRecordVendorConsign(purRecordVendorConsign);
            });
        } else if (PurRecordVendorAdd(code) || (ConstantsEms.VEN_CU.equals(specialStock) && DocCategory.SALE_ORDER.getCode().equals(referDocumentCode)) || (ConstantsEms.VEN_CU.equals(specialStock) && DocCategory.SALE_RU.getCode().equals(referDocumentCode))) {
            //供应商寄售待结算台账 增加
            invInventoryDocumentItemList.forEach(o -> {
                PurRecordVendorConsign purRecordVendorConsign = new PurRecordVendorConsign();
                BeanCopyUtils.copyProperties(invInventoryDocument, purRecordVendorConsign);
                BeanCopyUtils.copyProperties(o, purRecordVendorConsign);
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    purRecordVendorConsign.setQuantity(purRecordVendorConsign.getQuantity().abs().multiply(new BigDecimal(-1)));
                }
                purRecordVendorConsign.setType(ConstantsEms.RU_KU);
                purRecordVendorConsignServiceImpl.insertPurRecordVendorConsign(purRecordVendorConsign);
            });
        }
    }

    //创建库存库位信息
    private InvInventoryLocation createLocation(InvInventoryDocument invInventoryDocument, InvInventoryDocumentItem documentItem) {
        InvInventoryLocation location = new InvInventoryLocation();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setCreateDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUpdateDate(null);
        location.setPrice(new BigDecimal(0));
        location.setVendorSubcontractQuantity(new BigDecimal(0));
        location.setVendorConsignQuantity(new BigDecimal(0));
        location.setCustomerConsignQuantity(new BigDecimal(0));
        location.setCustomerSubcontractQuantity(new BigDecimal(0));
        location.setUnlimitedQuantity(new BigDecimal(0));
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invInventoryLocationMapper.insert(location);
        // MongodbUtil.insertUserLog(location.getLocationStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }

    //创建特殊库存供应商信息
    private InvVenSpecialInventory createInvVenSpecialLocation(InvInventoryDocument invInventoryDocument, InvInventoryDocumentItem documentItem) {
        InvVenSpecialInventory location = new InvVenSpecialInventory();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setCreateDate(new Date());
        location.setFirstUpdateStockDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUnlimitedQuantity(new BigDecimal(0));
        location.setUpdateDate(null);
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invVenSpecialInventoryMapper.insert(location);
        // MongodbUtil.insertUserLog(location.getVendorSpecialStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }

    //创建特殊库存客户信息
    private InvCusSpecialInventory createInvCusSpecialLocation(InvInventoryDocument invInventoryDocument, InvInventoryDocumentItem documentItem) {
        InvCusSpecialInventory location = new InvCusSpecialInventory();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setCreateDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUnlimitedQuantity(new BigDecimal(0));
        location.setUpdateDate(null);
        location.setFirstUpdateStockDate(new Date());
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invCusSpecialInventoryMapper.insert(location);
        //  MongodbUtil.insertUserLog(location.getCustomerSpecialStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }

    /**
     * 校验出库时仓库信息
     */
    @Override
    public void vatatil(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        //判断是否是自有特殊库存互转(移库)
        judgeSpecialToCommon(invInventoryDocument);
        String stockType = invInventoryDocument.getSpecialStock();
        //生产直发出库不走一下逻辑
        if ("SC021".equals(invInventoryDocument.getMovementType()) && ConstantsEms.CK_CATEGORY.equals(invInventoryDocument.getChukuCategory())) {
            return;
        }
        if (stockType == null) {
            InvInventoryLocation(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        } else if (stockType.equals(ConstantsEms.VEN_CU) || stockType.equals(ConstantsEms.VEN_RA)) {
            InvVenSpecialInventory(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        } else {
            InvCusSpecialInventory(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        }

    }

    /**
     * 仓库出库操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void LocationCHU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvInventoryLocation> updateLocation = new ArrayList<>();
        String documentType = invInventoryDocument.getDocumentType();
        //发货单和领料单、甲供料结算单 价格初始化
        setPiceMarteril(invInventoryDocument, invInventoryDocumentItemList);
        //按发货单-甲供料 按发货单-客户寄售 寄售订单
        Issue(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        //按销售发货单（直发）不走下面的逻辑
        if (ConstantsEms.SALE_ORDER_RU_DIRECT.equals(invInventoryDocument.getMovementType())) {
            return;
        }
        String stockType = invInventoryDocument.getSpecialStock();
        //判断作业类型是否是移库或者串色串码（入库操作）
        Judgetransfer(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        //常规出库
        if (stockType == null) {
            commonCHU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        }
        //供应商特殊库存出库
        if (ConstantsEms.VEN_CU.equals(stockType) || ConstantsEms.VEN_RA.equals(stockType)) {
            InvVenSpecialCHU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        }
        //客户特殊库存出库
        if (ConstantsEms.CUS_RA.equals(stockType) || ConstantsEms.CUS_VE.equals(stockType)) {
            InvCusSpecialCHU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        }
        //按发货单-甲供料 按发货单-客户寄售 特殊库存类型
        getSpecial(invInventoryDocument);
    }

    /**
     * 收发货和领退料价格初始化、甲供料结算单
     */
    public void setPiceMarteril(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String code = invInventoryDocument.getMovementType();
        if (materialAndGoods(code)) {
            setPrice(invInventoryDocument, invInventoryDocumentItemList);
        }
    }

    /**
     * 按发货单-甲供料 按发货单-客户寄售 特殊库存类型
     */
    public void getSpecial(InvInventoryDocument invInventoryDocument) {
        String movementType = invInventoryDocument.getMovementType();
        if (ConstantsEms.ISSUE_V.equals(movementType)) {
            invInventoryDocument.setSpecialStock(ConstantsEms.VEN_RA);
        }
        if (ConstantsEms.ISSUE_C.equals(movementType)) {
            invInventoryDocument.setSpecialStock(ConstantsEms.CUS_VE);
        }
        if (ConstantsEms.RECIPT_V.equals(movementType)) {
            invInventoryDocument.setSpecialStock(ConstantsEms.VEN_RA);
        }
        if (ConstantsEms.RECIPT_C.equals(movementType)) {
            invInventoryDocument.setSpecialStock(ConstantsEms.CUS_VE);
        }
    }

    /**
     * 按发货单-甲供料 按发货单-客户寄售  -销售寄售订单
     */
    public void Issue(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String movementType = invInventoryDocument.getMovementType();
        String documentType = invInventoryDocument.getDocumentType();
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();
        if (movementType.equals(ConstantsEms.ISSUE_V) || movementType.equals(ConstantsEms.ISSUE_C) || DocCategory.SALE_RETURN.getCode().equals(saleAndPurchaseDocument)) {
            InvInventoryDocument purposeInvInventory = new InvInventoryDocument();
            BeanCopyUtils.copyProperties(invInventoryDocument, purposeInvInventory);
            //出库 常规库存减少 / 冲销 常规库存增加
            invInventoryDocument.setSpecialStock(null);
            //入库  特殊库存 增加/ 冲销 常规库存减少
            purposeInvInventory.setType(ConstantsEms.RU_KU);
            if (movementType.equals(ConstantsEms.ISSUE_V)) {
                purposeInvInventory.setSpecialStock(ConstantsEms.VEN_RA);
            }
            if (movementType.equals(ConstantsEms.ISSUE_C) || DocCategory.SALE_RETURN.getCode().equals(documentType) || DocCategory.SALE_RETURN.getCode().equals(saleAndPurchaseDocument)) {
                purposeInvInventory.setSpecialStock(ConstantsEms.CUS_VE);
            }
            List<InvInventoryDocumentItem> purposeitems = purposeInvInventory.getInvInventoryDocumentItemList();
            //冲销校验
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                judgeSpecCX(purposeInvInventory, purposeitems);
            }
            LocationRU(oldLocation, purposeInvInventory, purposeitems);
            //按销售发货单（直发）不走下面的逻辑
            if (ConstantsEms.SALE_ORDER_RU_DIRECT.equals(invInventoryDocument.getMovementType())) {
                return;
            }
            vatatil(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        }
    }

    /**
     * 特殊冲销校验 入库 （按发货单-甲供料 按发货单-客户寄售-销售寄售订单冲\按收货单-甲供料退料 按收货单-客户寄售退货 销售寄售退货订单）
     */
    public void judgeSpecCX(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String specialStock = invInventoryDocument.getSpecialStock();
        if (specialStock == null) {
            invInventoryDocumentItemList.forEach(item -> {
                InvInventoryLocation inventoryLocation = new InvInventoryLocation();
                inventoryLocation.setBarcodeSid(item.getBarcodeSid())
                        .setStorehouseLocationSid(item.getStorehouseLocationSid())
                        .setBusinessOrderSid(item.getReferDocumentSid())
                        .setStorehouseSid(item.getStorehouseSid());
                //获取可用库存量
                InvInventoryLocation location = invInventoryLocationMapper.getLocationAble(inventoryLocation);
                String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
                if (location != null) {
                    if (item.getLocationQuantity() != null) {
                        if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                            item.setLocationQuantity(item.getLocationQuantity().multiply(new BigDecimal("-1")));
                        }
                        location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getLocationQuantity()));
                        location.setAbleQuantity(location.getAbleQuantity().subtract(item.getLocationQuantity()));
                    }
                }
                if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && !ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) || ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType())) {//冲销入库须交验
                    if (location == null) {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，不允许冲销");
                    }
                    BigDecimal quantity = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity().abs().setScale(4, BigDecimal.ROUND_HALF_UP);
                    if ((location.getAbleQuantity().subtract(quantity).compareTo(new BigDecimal(0))) == -1) {
                        throw new CustomException("行号为" + item.getItemNum() + "的可用库存量不足，不允许冲销");
                    }
                }
                invInventoryLocationMapper.update(new InvInventoryLocation(), new UpdateWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                        .eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
                        .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid())
                        .set(InvInventoryLocation::getClientId, ApiThreadLocalUtil.get().getClientId())
                );
            });
        } else if (specialStock.equals(ConstantsEms.VEN_CU) || specialStock.equals(ConstantsEms.VEN_RA)) {
            invInventoryDocumentItemList.forEach(item -> {
                InvInventoryLocation inventory = new InvInventoryLocation();
                inventory.setBarcodeSid(item.getBarcodeSid())
                        .setStorehouseLocationSid(item.getStorehouseLocationSid())
                        .setStorehouseSid(item.getStorehouseSid())
                        .setBusinessOrderSid(item.getReferDocumentSid())
                        .setVendorSid(invInventoryDocument.getVendorSid())
                        .setSpecialStock(invInventoryDocument.getSpecialStock());
                //获取可用库存
                InvInventoryLocation location = invVenSpecialInventoryMapper.getLocationAble(inventory);
                if (location != null) {
                    location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getLocationQuantity()));
                    location.setAbleQuantity(location.getAbleQuantity().subtract(item.getLocationQuantity()));
                }
                if (location == null) {
                    throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法冲销");
                }
                if ((location.getUnlimitedQuantity().subtract(item.getQuantity().abs()).compareTo(new BigDecimal(0))) == -1) {
                    throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存小于出库量，无法冲销");
                }
                invVenSpecialInventoryMapper.update(new InvVenSpecialInventory(), new UpdateWrapper<InvVenSpecialInventory>().lambda()
                        .eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                        .eq(InvVenSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
                        .eq(InvVenSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                        .eq(InvVenSpecialInventory::getVendorSid, invInventoryDocument.getVendorSid())
                        .eq(InvVenSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock())
                        .set(InvVenSpecialInventory::getClientId, ApiThreadLocalUtil.get().getClientId())
                );
            });
        } else {
            invInventoryDocumentItemList.forEach(item -> {
                InvInventoryLocation inventory = new InvInventoryLocation();
                inventory.setBarcodeSid(item.getBarcodeSid())
                        .setStorehouseLocationSid(item.getStorehouseLocationSid())
                        .setStorehouseSid(invInventoryDocument.getStorehouseSid())
                        .setBusinessOrderSid(item.getReferDocumentSid())
                        .setCustomerSid(invInventoryDocument.getCustomerSid())
                        .setSpecialStock(invInventoryDocument.getSpecialStock());
                //获取可用库存
                InvInventoryLocation location = invCusSpecialInventoryMapper.getLocationAble(inventory);
                if (location != null) {
                    location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getLocationQuantity()));
                }
                if (location == null) {
                    throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法冲销");
                }
                if ((location.getUnlimitedQuantity().subtract(item.getQuantity().abs()).compareTo(new BigDecimal(0))) == -1) {
                    throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存小于出库量，无法冲销");
                }
                invCusSpecialInventoryMapper.update(new InvCusSpecialInventory(), new UpdateWrapper<InvCusSpecialInventory>().lambda()
                        .eq(InvCusSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                        .eq(InvCusSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
                        .eq(InvCusSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                        .eq(InvCusSpecialInventory::getCustomerSid, invInventoryDocument.getCustomerSid())
                        .eq(InvCusSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock())
                        .set(InvCusSpecialInventory::getClientId, ApiThreadLocalUtil.get().getClientId())
                );
            });
        }
    }

    /**
     * 移库
     */
    @Transactional(rollbackFor = Exception.class)
    public void Judgetransfer(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        //移库 出库时，目的仓库入库
        String code = invInventoryDocument.getMovementType();
        //普通移库
        if (exitTransfer(code)) {
            InvInventoryDocument invInventoryDocumentpurpose = new InvInventoryDocument();
            ArrayList<InvInventoryDocumentItem> invInventoryDocumentItemPurpose = new ArrayList<>();
            invInventoryDocumentItemPurpose.addAll(invInventoryDocumentItemList);
            BeanCopyUtils.copyProperties(invInventoryDocument, invInventoryDocumentpurpose);
            invInventoryDocumentpurpose.setStorehouseSid(invInventoryDocument.getDestStorehouseSid());
            //初始化价格
            setPrice(invInventoryDocument, invInventoryDocumentItemPurpose);
            //若移库方式为”一步法（出库即入库）“（YB） 过账成功时，按照当前逻辑进行移库  // 或者在调拨时读取作业类型的配置
            List<ConMovementType> conMovementTypes = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
                    .eq(ConMovementType::getIsTransferOnestep, ConstantsEms.YES)
                    .eq(ConMovementType::getCode, code));
            if (ConstantsInventory.STOCK_TRANSFER_MODE_YB.equals(invInventoryDocument.getStockTransferMode()) ||
                    (CollectionUtil.isNotEmpty(conMovementTypes) && ConstantsInventory.REFER_DOC_CAT_DBD.equals(invInventoryDocument.getReferDocCategory()))) {
                //出入库类型改成入库
                invInventoryDocumentpurpose.setType("2");
                invInventoryDocumentItemPurpose.forEach(item->{
                    // 目的 转 一下
                    Long storehouseSid = item.getStorehouseSid();
                    String storehouseCode = item.getStorehouseCode();
                    Long storehouseLocationSid = item.getStorehouseLocationSid();
                    String storehouseLocationCode = item.getLocationCode();
                    item.setStorehouseSid(item.getDestStorehouseSid())
                            .setStorehouseCode(item.getDestStorehouseCode())
                            .setStorehouseLocationSid(item.getDestStorehouseLocationSid())
                            .setLocationCode(item.getDestLocationCode());
                    item.setDestStorehouseSid(storehouseSid)
                            .setDestStorehouseCode(storehouseCode)
                            .setDestStorehouseLocationSid(storehouseLocationSid)
                            .setDestLocationCode(storehouseLocationCode);
                });
                // 入库
                LocationRU(oldLocation, invInventoryDocumentpurpose, invInventoryDocumentItemPurpose);
                invInventoryDocumentItemPurpose.forEach(item->{
                    // 入库完再 目的 转 一下
                    Long storehouseSid = item.getDestStorehouseSid();
                    String storehouseCode = item.getDestStorehouseCode();
                    Long storehouseLocationSid = item.getDestStorehouseLocationSid();
                    String storehouseLocationCode = item.getDestLocationCode();
                    item.setDestStorehouseSid(item.getStorehouseSid())
                            .setDestStorehouseCode(item.getStorehouseCode())
                            .setDestStorehouseLocationSid(item.getStorehouseLocationSid())
                            .setDestLocationCode(item.getLocationCode());
                    item.setStorehouseSid(storehouseSid)
                            .setStorehouseCode(storehouseCode)
                            .setStorehouseLocationSid(storehouseLocationSid)
                            .setLocationCode(storehouseLocationCode);
                });
            }
            return;
        }
        //特殊移库(特殊仓库和自有库存互转)
        int sepcialOrCommon = SpecialToTransfer(code);
        int commonToTransfer = CommonToTransfer(code);
        if (sepcialOrCommon != 0 || commonToTransfer != 0) {
            InvInventoryDocument invInventoryDocumentpurpose = new InvInventoryDocument();
            ArrayList<InvInventoryDocumentItem> invInventoryDocumentItemPurpose = new ArrayList<>();
            invInventoryDocumentItemPurpose.addAll(invInventoryDocumentItemList);
            BeanCopyUtils.copyProperties(invInventoryDocument, invInventoryDocumentpurpose);
            //初始化价格
            setPrice(invInventoryDocument, invInventoryDocumentItemPurpose);
            //出入库类型改成入库
            invInventoryDocumentpurpose.setType(ConstantsEms.RU_KU);
            //1为特殊仓库转自有仓库
            if (sepcialOrCommon == 1) {
                invInventoryDocumentpurpose.setSpecialStock(null);
            } else {
                invInventoryDocumentpurpose.setSpecialStock(invInventoryDocument.getSpecialToCommon());
            }
            LocationRU(oldLocation, invInventoryDocumentpurpose, invInventoryDocumentItemPurpose);
            return;
        }
        //库存调整-串色串码
        Boolean exit = exitAdjust(code);
        if (exit) {
            InvInventoryDocument invInventoryDocumentpurpose = new InvInventoryDocument();
            ArrayList<InvInventoryDocumentItem> invInventoryDocumentItemPurpose = new ArrayList<>();
            BeanCopyUtils.copyProperties(invInventoryDocument, invInventoryDocumentpurpose);
            invInventoryDocumentpurpose.setStorehouseSid(invInventoryDocument.getDestStorehouseSid());
            //被串商品条码
            invInventoryDocumentItemList.forEach(o -> {
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                BeanCopyUtils.copyProperties(o, invInventoryDocumentItem);
                invInventoryDocumentItem.setBarcodeSid(invInventoryDocumentItem.getDestBarcodeSid());
                invInventoryDocumentItem.setSku1Sid(invInventoryDocumentItem.getDestSku1Sid());
                invInventoryDocumentItem.setSku2Sid(invInventoryDocumentItem.getDestSku2Sid());
                invInventoryDocumentItemPurpose.add(invInventoryDocumentItem);
            });

            //初始化价格
            setPrice(invInventoryDocument, invInventoryDocumentItemPurpose);
            //出入库类型改成入库
            invInventoryDocumentpurpose.setType("2");
            LocationRU(oldLocation, invInventoryDocumentpurpose, invInventoryDocumentItemPurpose);
            return;
        }

    }

    /**
     * 获取原先仓库的价格
     */
    public void setPrice(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemList.forEach(item -> {
            InvStorehouseMaterial location = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                    .eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvStorehouseMaterial::getStorehouseSid, item.getStorehouseSid())
            );
            if (location != null) {
                item.setPrice(location.getPrice());
            } else {
                item.setPrice(new BigDecimal(0));
            }

        });
    }

    /**
     * 判断出库是否是特殊库存和自由仓库互转
     */
    public void judgeSpecialToCommon(InvInventoryDocument invInventoryDocument) {
        String code = invInventoryDocument.getMovementType();
        int sepcialOrCommon = CommonToTransfer(code);
        //自有库存转特殊库存 2
        if (sepcialOrCommon == 2) {
            //临时存储特殊库存类型
            invInventoryDocument.setSpecialToCommon(invInventoryDocument.getSpecialStock());
            invInventoryDocument.setSpecialStock(null);
        }
    }

    /**
     * 仓库入库操作
     */
    public void LocationRU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvInventoryLocation> updateLocation = new ArrayList<>();
        //按收货单-甲供料退料 按收货单-客户寄售退货 销售寄售退货订单 入库
        recipit(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
        String stockType = invInventoryDocument.getSpecialStock();
        //常规入库
        if (stockType == null) {
            commonRU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
            return;
        }
        //供应商特殊库存入库
        if (ConstantsEms.VEN_CU.equals(stockType) || ConstantsEms.VEN_RA.equals(stockType)) {
            InvVenSpecialRU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
            return;
        }
        //客户特殊库存入库
        if (ConstantsEms.CUS_RA.equals(stockType) || ConstantsEms.CUS_VE.equals(stockType)) {
            InvCusSpecialRU(oldLocation, invInventoryDocument, invInventoryDocumentItemList);
            return;
        }
        //按收货单-甲供料退料 按收货单-客户寄售退货 特殊库存
        getSpecial(invInventoryDocument);
    }

    /**
     * 按收货单-甲供料退料 按收货单-客户寄售退货 销售寄售退货订单 入库
     */
    public void recipit(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String movementType = invInventoryDocument.getMovementType();
        String documentType = invInventoryDocument.getDocumentType();
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();
        if (movementType.equals(ConstantsEms.RECIPT_V) || movementType.equals(ConstantsEms.RECIPT_C) || DocCategory.SALE_JI_RETURN.getCode().equals(saleAndPurchaseDocument)) {
            InvInventoryDocument purposeInvInventory = new InvInventoryDocument();
            BeanCopyUtils.copyProperties(invInventoryDocument, purposeInvInventory);
            //入库  常规库存增加 //冲销  常规库存减少
            invInventoryDocument.setSpecialStock(null);
            //冲销校验
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                judgeSpecCX(invInventoryDocument, invInventoryDocumentItemList);
            }
            if (movementType.equals(ConstantsEms.RECIPT_V)) {
                purposeInvInventory.setSpecialStock(ConstantsEms.VEN_RA);
            }
            if (movementType.equals(ConstantsEms.RECIPT_C) || DocCategory.SALE_JI_RETURN.getCode().equals(documentType) || DocCategory.SALE_JI_RETURN.getCode().equals(saleAndPurchaseDocument)) {
                purposeInvInventory.setSpecialStock(ConstantsEms.CUS_VE);
            }
            //出库  甲供料 减少 //冲销 甲供料 增加
            purposeInvInventory.setType(ConstantsEms.CHU_KU);
            List<InvInventoryDocumentItem> purposeitems = purposeInvInventory.getInvInventoryDocumentItemList();
            if (!ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {//常规
                purposeInvInventory.setSpecialVtil(ConstantsEms.YES);//校验
            }
            vatatil(oldLocation, purposeInvInventory, purposeitems);
            LocationCHU(oldLocation, purposeInvInventory, purposeitems);
        }

    }

    /**
     * 调拨单生成在途库存
     */
    public void createTransfer(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        //判断是否是调拨单\移库两步法
        String code = invInventoryDocument.getMovementType();
        if (exitAllocate(code)) {
            ArrayList<InvIntransitInventory> invIntransitInventories = new ArrayList<>();
            Long invCode = null;
            InvInventoryDocument document = invInventoryDocumentMapper.selectById(invInventoryDocument.getInventoryDocumentSid());
            invCode = document.getInventoryDocumentCode();
            invInventoryDocumentItemList.forEach(o -> {
                //在途库存
                InvIntransitInventory invIntransitInventory = new InvIntransitInventory();
                BeanCopyUtils.copyProperties(o, invIntransitInventory);
                BeanCopyUtils.copyProperties(invInventoryDocument, invIntransitInventory);
                invIntransitInventory.setStorehouseLocationSid(o.getStorehouseLocationSid());
                invIntransitInventory.setUnlimitedQuantity(o.getQuantity());
                //调拨单赋值
                invIntransitInventory.setInventoryTransferSid(invInventoryDocument.getInventoryTransferSid());
                invIntransitInventory.setInventoryTransferItemSid(o.getInventoryTransferItemSid());
                invIntransitInventory.setInventoryTransferCode(invInventoryDocument.getInventoryTransferCode());
                invIntransitInventory.setOutStockDate(new Date());
                invIntransitInventory.setHandleStatus(ConstantsEms.CHECK_STATUS);
                if (invInventoryDocument.getInventoryTransferSid() == null) {
                    invIntransitInventory.setInventoryDocumentCode(document.getInventoryDocumentCode())
                            .setInventoryDocumentSid(document.getInventoryDocumentSid())
                            .setInventoryDocumentItemSid(o.getInventoryDocumentItemSid());
                }
                invIntransitInventories.add(invIntransitInventory);
            });
            invIntransitInventoryMapper.inserts(invIntransitInventories);
            invInventoryDocument.setInOutStockStatus(ConstantsEms.IN_STORE_STATUS_NOT);
            invInventoryDocumentMapper.updateById(invInventoryDocument);
        }
    }

    /**
     * 常规出库
     */
    public void commonCHU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvInventoryLocation> updateLocation = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
            invInventoryDocumentItemList.forEach(item -> {
                //冲销
                String documentType = invInventoryDocument.getDocumentTypeInv();
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource())) {
                    item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
                }
                InvInventoryLocation location = item.getLocation();
                if (location.getPrice() == null) {
                    location.setPrice(BigDecimal.ZERO);
                }
                BigDecimal quantityAfter = location.getUnlimitedQuantity().subtract(item.getQuantity());
                //库存凭证出库前
                item.setTotalQuantityBefore(location.getUnlimitedQuantity());
                item.setCurrencyAmountBefore(location.getUnlimitedQuantity().multiply(location.getPrice()));
                //库存凭证出库后
                item.setTotalQuantityAfter(quantityAfter);
                // 如果作业类型是 其它出库-常规/自采物料 则 明细的 价格 由用户 前端输入控制
                if (!ConstantsInventory.MOVEMENT_TYPE_CODE_SC30.equals(invInventoryDocument.getMovementType())) {
                    item.setPrice(location.getPrice());
                }
                item.setCurrencyAmountAfter(quantityAfter.multiply(location.getPrice()));
                invInventoryLocationMapper.update(new InvInventoryLocation(), new UpdateWrapper<InvInventoryLocation>().lambda()
                        .set(InvInventoryLocation::getUnlimitedQuantity, quantityAfter)
                        .set(InvInventoryLocation::getLatestUpdateStockDate, new Date())
                        .eq(InvInventoryLocation::getLocationStockSid, location.getLocationStockSid())
                );
                updateLocation.add(location);
            });
        }
        //库存日志
//        updateLocation.forEach(location -> {
//            InvInventoryLocation old = (InvInventoryLocation) oldLocation.get(location.getLocationStockSid());
//            //MongodbUtil.insertUserLog(location.getLocationStockSid(), BusinessType.UPDATE.ordinal(), old, location, "库存信息");
//        });
    }

    /**
     * 供应商特殊库存出库
     */
    public void InvVenSpecialCHU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvVenSpecialInventory> updateLocation = new ArrayList<>();
        invInventoryDocumentItemList.forEach(item -> {
            //冲销
            String documentType = invInventoryDocument.getDocumentTypeInv();
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource())) {
                item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
            }
            InvVenSpecialInventory location = item.getInvVenSpecialInventory();
            location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getQuantity()));
            location.setLatestUpdateStockDate(new Date());
            invVenSpecialInventoryMapper.updateById(location);
            updateLocation.add(location);
        });
        //修改库存库位表中特殊仓库的数量
        InvVenSpecialMount(invInventoryDocument, invInventoryDocumentItemList);
    }

    /**
     * 客户特殊库存出库
     */
    public void InvCusSpecialCHU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvCusSpecialInventory> updateLocation = new ArrayList<>();
        invInventoryDocumentItemList.forEach(item -> {
            //冲销
            String documentType = invInventoryDocument.getDocumentTypeInv();
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource())) {
                item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
            }
            InvCusSpecialInventory location = item.getInvCusSpecialInventory();
            location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getQuantity()));
            location.setLatestUpdateStockDate(new Date());
            invCusSpecialInventoryMapper.updateById(location);
            updateLocation.add(location);
        });
        //修改库存库位表中特殊仓库的数量
        InvVenSpecialMount(invInventoryDocument, invInventoryDocumentItemList);
    }

    /**
     * 入库-调拨单-在途库存相应减少
     */
    public void invReduce(InvInventoryDocument invInventoryDocument, InvInventoryDocumentItem item) {
        invInventoryDocumentMapper.update(new InvInventoryDocument(), new UpdateWrapper<InvInventoryDocument>().lambda()
                .eq(InvInventoryDocument::getInventoryDocumentSid, invInventoryDocument.getReferDocumentSid())
                .set(InvInventoryDocument::getInOutStockStatus, ConstantsEms.IN_STORE_STATUS)
        );
        //调拨单-在途库存
        InvIntransitInventory invIntransitInventory = invIntransitInventoryMapper.selectOne(new QueryWrapper<InvIntransitInventory>()
                .lambda()
                .eq(invInventoryDocument.getInventoryTransferCode() != null, InvIntransitInventory::getInventoryTransferCode, invInventoryDocument.getInventoryTransferCode())
                .eq(invInventoryDocument.getReferDocumentSid() != null && ConstantsInventory.REFER_DOC_CAT_DBD.equals(invInventoryDocument.getReferDocCategory()),
                        InvIntransitInventory::getInventoryTransferSid, invInventoryDocument.getReferDocumentSid())
                .eq(item.getReferDocumentItemSid() != null && ConstantsInventory.REFER_DOC_CAT_DBD.equals(invInventoryDocument.getReferDocCategory()),
                        InvIntransitInventory::getInventoryTransferItemSid, item.getReferDocumentItemSid())
                .eq(invInventoryDocument.getReferDocumentSid() != null && !ConstantsInventory.REFER_DOC_CAT_DBD.equals(invInventoryDocument.getReferDocCategory()),
                        InvIntransitInventory::getInventoryDocumentSid, invInventoryDocument.getReferDocumentSid())
                .eq(item.getReferDocumentItemSid() != null && !ConstantsInventory.REFER_DOC_CAT_DBD.equals(invInventoryDocument.getReferDocCategory()),
                        InvIntransitInventory::getInventoryDocumentItemSid, item.getReferDocumentItemSid())
                .eq(InvIntransitInventory::getBarcodeSid, item.getBarcodeSid()));
        BigDecimal unlimitOrigin = invIntransitInventory.getUnlimitedQuantity();
        invIntransitInventory.setUnlimitedQuantity(unlimitOrigin.subtract(item.getQuantity()));
        invIntransitInventoryMapper.updateById(invIntransitInventory);

    }

    /**
     * 常规入库
     */
    public void commonRU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvInventoryLocation> updateLocation = new ArrayList<>();
        String isReturnGoods = invInventoryDocument.getIsReturnGoods();
        invInventoryDocumentItemList.forEach(item -> {
            BigDecimal sumQuantity = BigDecimal.ZERO;
            // 移库的入库，以及调拨单 要根据目的库位入库
            InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvInventoryLocation::getClientId, item.getClientId() != null ? item.getClientId() : ApiThreadLocalUtil.get().getClientId())
                    .eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid())
            );
            List<InvInventoryLocation> locationSum = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid())
            );
            if (CollectionUtil.isNotEmpty(locationSum)) {
                sumQuantity = locationSum.stream().map(li -> li.getUnlimitedQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            InvStorehouseMaterial invStorehouseMaterial = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                    .eq(InvStorehouseMaterial::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid())
            );
            String code = invInventoryDocument.getMovementType();
            item.setQuantity(item.getQuantity().abs());
            if (location == null) {
                location = createLocation(invInventoryDocument, item);
                location.setUnlimitedQuantity(item.getQuantity());
                if (!ConstantsEms.YES.equals(isReturnGoods)) {
                    if (item.getPrice() == null) {
                        item.setPrice(BigDecimal.ZERO);
                    }
                    if (invStorehouseMaterial != null) {
                        if (sumQuantity.compareTo(BigDecimal.ZERO) == 0) {
                            location.setPrice(item.getUnitConversionRate() != null ? invStorehouseMaterial.getPrice().divide(item.getUnitConversionRate(), 6, BigDecimal.ROUND_HALF_UP) : invStorehouseMaterial.getPrice());
                        } else {
                            BigDecimal price = BigDecimal.ZERO;
                            BigDecimal sumQu = BigDecimal.ZERO;
                            price = sumQuantity.multiply(invStorehouseMaterial.getPrice()).add(item.getQuantity().multiply(item.getUnitConversionRate() != null ? item.getPrice().divide(item.getUnitConversionRate(), 6, BigDecimal.ROUND_HALF_UP) : item.getPrice()));
                            sumQu = sumQuantity.add(item.getUnitConversionRate() != null ? item.getPriceQuantity().multiply(item.getUnitConversionRate()) : item.getQuantity());
                            location.setPrice(price.divide(sumQu, 5, BigDecimal.ROUND_HALF_UP));
                        }
                    } else {
                        location.setPrice(item.getUnitConversionRate() != null ? item.getPrice().divide(item.getUnitConversionRate(), 6, BigDecimal.ROUND_HALF_UP) : item.getPrice());
                    }
                }
            }
            else {
                BigDecimal tatalQuantity = BigDecimal.ZERO;
                if (invStorehouseMaterial != null) {
                    location.setPrice(invStorehouseMaterial.getPrice());
                } else {
                    location.setPrice(BigDecimal.ZERO);
                }
                //调拨单或盘点、领退料、入库价格初始化、销售退货发货单
                if (item.getPrice() == null) {
                    item.setPrice(location.getPrice());
                }
                //计算入库后的加权平均价
                //冲销
                String documentType = invInventoryDocument.getDocumentType();
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource())) {
                    item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal("-1")));
                    if (item.getPriceQuantity() != null) {
                        item.setPriceQuantity(item.getPriceQuantity().abs().multiply(new BigDecimal("-1")));
                    }
                }
                BigDecimal totalOld = BigDecimal.ZERO;
                BigDecimal add = BigDecimal.ZERO;
                if (!ConstantsEms.YES.equals(isReturnGoods)) {
                    totalOld = location.getPrice().multiply(sumQuantity);
                    add = item.getPriceQuantity() != null ? item.getPrice().multiply(item.getPriceQuantity()) : item.getPrice().multiply(item.getQuantity());
                }
                //采购数量、入库量
                tatalQuantity = sumQuantity.add(item.getPriceQuantity() != null ? item.getPriceQuantity().multiply(item.getUnitConversionRate()) : item.getQuantity());
                BigDecimal tatalQuantitySin = location.getUnlimitedQuantity().add(item.getPriceQuantity() != null ? item.getPriceQuantity().multiply(item.getUnitConversionRate()) : item.getQuantity());
                if (!ConstantsEms.YES.equals(isReturnGoods)) {
                    BigDecimal tatalPrice = totalOld.add(add);
                    BigDecimal priceAfter = BigDecimal.ZERO;
                    if (tatalQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        priceAfter = tatalPrice.divide(tatalQuantity, 5, BigDecimal.ROUND_HALF_UP);
                    }
                    item.setCurrencyAmountBefore(totalOld);
                    item.setCurrencyAmountAfter(tatalPrice);
                    //加权平均价前
                    if (location.getPrice() == null) {
                        item.setPriceBefore(new BigDecimal(0));
                    } else {
                        item.setPriceBefore(location.getPrice());
                    }
                    item.setPriceAfter(priceAfter);
//                item.setPrice(priceAfter);
                    location.setPrice(priceAfter);
                }
                //库存凭证入库前
                item.setTotalQuantityBefore(location.getUnlimitedQuantity());

                //库存凭证入库后
                item.setTotalQuantityAfter(tatalQuantitySin);
                //入库后的数量
                location.setUnlimitedQuantity(tatalQuantitySin);
            }
            location.setLatestUpdateStockDate(new Date());
            if (location.getFirstUpdateStockDate() == null) {
                location.setFirstUpdateStockDate(new Date());
            } else {
                location.setLatestUpdateStockDate(new Date());
            }
            invInventoryLocationMapper.updateById(location);
            updateLocation.add(location);
            //入库时调拨单-在途库存相应减少
            if (exitRuAllocate(code)) {
                invReduce(invInventoryDocument, item);
            }
            //修改仓库物料价格信息
            createLocationMaterial(location);
        });
    }

    /**
     * 创建物料信息
     */
    public void createLocationMaterial(InvInventoryLocation location) {
        InvStorehouseMaterial invStorehouseMaterial = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                .eq(InvStorehouseMaterial::getStorehouseSid, location.getStorehouseSid())
                .eq(InvStorehouseMaterial::getBarcodeSid, location.getBarcodeSid())
        );
        if (invStorehouseMaterial == null) {
            InvStorehouseMaterial storehouseMaterial = new InvStorehouseMaterial();
            BeanCopyUtils.copyProperties(location, storehouseMaterial);
            storehouseMaterial.setUsageFrequencyFlag(ConstantsInventory.USAGE_FREQUENCY_FLAG_CY);
            storehouseMaterial.setCreateDate(new Date());
            storehouseMaterial.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            storehouseMaterial.setUpdateDate(null);
            storehouseMaterial.setPrice(location.getPrice() != null ? location.getPrice() : BigDecimal.ZERO);
            storehouseMaterial.setUpdaterAccount(null);
            storehouseMaterial.setConfirmDate(null);
            storehouseMaterial.setConfirmerAccount(null);
            invStorehouseMaterialMapper.insert(storehouseMaterial);
        } else {
            invStorehouseMaterial.setPrice(location.getPrice());
            invStorehouseMaterial.setUpdateDate(new Date());
            invStorehouseMaterial.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            invStorehouseMaterialMapper.updateById(invStorehouseMaterial);
        }
    }

    /**
     * 供应商特殊库存入库
     */
    public void InvVenSpecialRU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvVenSpecialInventory> updateLocation = new ArrayList<>();
        invInventoryDocumentItemList.forEach(item -> {
            // 移库的入库，以及调拨单 要根据目的库位入库
            InvVenSpecialInventory location = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                    .eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvVenSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvVenSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvVenSpecialInventory::getVendorSid, invInventoryDocument.getVendorSid())
                    .eq(InvVenSpecialInventory::getClientId, item.getClientId() != null ? item.getClientId() : ApiThreadLocalUtil.get().getClientId())
                    .eq(InvVenSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock()));
            String code = invInventoryDocument.getMovementType();
            item.setQuantity(item.getQuantity().abs());
            if (location == null) {
                location = createInvVenSpecialLocation(invInventoryDocument, item);
                location.setUnlimitedQuantity(item.getQuantity());
                invVenSpecialInventoryMapper.updateById(location);
                oldLocation.put(location.getVendorSpecialStockSid(), location);
            } else {
                //冲销
                String documentType = invInventoryDocument.getDocumentType();
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource())) {
                    item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
                }
                oldLocation.put(location.getVendorSpecialStockSid(), location);
                if (location.getFirstUpdateStockDate() == null) {
                    location.setFirstUpdateStockDate(new Date());
                } else {
                    location.setLatestUpdateStockDate(new Date());
                }
                location.setUnlimitedQuantity(location.getUnlimitedQuantity().add(item.getQuantity()));
                invVenSpecialInventoryMapper.updateById(location);
                updateLocation.add(location);
            }
            //入库时调拨单-在途库存相应减少
            if (exitRuAllocate(code)) {
                invReduce(invInventoryDocument, item);
            }
        });
        //修改库存库位表中供应商的数量
        InvVenSpecialMount(invInventoryDocument, invInventoryDocumentItemList);
    }

    /**
     * 计算库存库位入库时特殊库存数量
     */
    public void InvVenSpecialMount(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemList.forEach(item -> {
            InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid())
            );
            //出库时取反
            if (invInventoryDocument.getType().equals(ConstantsEms.CHU_KU)) {
                item.setQuantity(item.getQuantity().divide(new BigDecimal(-1)));
            }
            //冲销
            String documentType = invInventoryDocument.getDocumentTypeInv();
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource()) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType())) {
                item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
            }
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource()) && ConstantsEms.CHU_KU.equals(invInventoryDocument.getType())) {
                item.setQuantity(item.getQuantity().abs());
            }
            if (location == null) {
                location = createLocation(invInventoryDocument, item);
            }
            if (invInventoryDocument.getSpecialStock().equals(ConstantsEms.VEN_RA)) {
                location.setVendorSubcontractQuantity(location.getVendorSubcontractQuantity().add(item.getQuantity()));
            } else if (invInventoryDocument.getSpecialStock().equals(ConstantsEms.VEN_CU)) {
                location.setVendorConsignQuantity(location.getVendorConsignQuantity().add(item.getQuantity()));
            } else if (invInventoryDocument.getSpecialStock().equals(ConstantsEms.CUS_RA)) {
                location.setCustomerSubcontractQuantity(location.getCustomerSubcontractQuantity().add(item.getQuantity()));
            } else {
                location.setCustomerConsignQuantity(location.getCustomerConsignQuantity().add(item.getQuantity()));
            }
            if (ConstantsEms.DOCUMNET_TYPE_ZG.equals(invInventoryDocument.getDocumentType())) {
                item.setQuantity(item.getQuantity().abs());
            }
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource()) && ConstantsEms.CHU_KU.equals(invInventoryDocument.getType())) {
                item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
            }
            if (location.getFirstUpdateStockDate() == null) {
                location.setFirstUpdateStockDate(new Date());
            } else {
                location.setLatestUpdateStockDate(new Date());
            }
            invInventoryLocationMapper.updateAllById(location);
        });

    }

    /**
     * 客户特殊库存入库
     */
    public void InvCusSpecialRU(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvCusSpecialInventory> updateLocation = new ArrayList<>();
        invInventoryDocumentItemList.forEach(item -> {
            // 移库的入库，以及调拨单 要根据目的库位入库
            InvCusSpecialInventory location = invCusSpecialInventoryMapper.selectOne(new QueryWrapper<InvCusSpecialInventory>().lambda()
                    .eq(InvCusSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvCusSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvCusSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvCusSpecialInventory::getClientId, item.getClientId() != null ? item.getClientId() : ApiThreadLocalUtil.get().getClientId())
                    .eq(InvCusSpecialInventory::getCustomerSid, invInventoryDocument.getCustomerSid())
                    .eq(InvCusSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock()));
            String code = invInventoryDocument.getMovementType();
            item.setQuantity(item.getQuantity().abs());
            if (location == null) {
                location = createInvCusSpecialLocation(invInventoryDocument, item);
                location.setUnlimitedQuantity(item.getQuantity());
                invCusSpecialInventoryMapper.updateById(location);
                oldLocation.put(location.getCustomerSpecialStockSid(), location);
            } else {
                //冲销
                String documentType = invInventoryDocument.getDocumentType();
                if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentType) && !ConstantsEms.INV_SOURCE.equals(invInventoryDocument.getSource())) {
                    item.setQuantity(item.getQuantity().abs().multiply(new BigDecimal(-1)));
                }
                oldLocation.put(location.getCustomerSpecialStockSid(), location);
                if (location.getFirstUpdateStockDate() == null) {
                    location.setFirstUpdateStockDate(new Date());
                } else {
                    location.setLatestUpdateStockDate(new Date());
                }
                location.setUnlimitedQuantity(location.getUnlimitedQuantity().add(item.getQuantity()));
                invCusSpecialInventoryMapper.updateById(location);
                updateLocation.add(location);
            }
            //入库时调拨单-在途库存相应减少
            if (exitRuAllocate(code)) {
                invReduce(invInventoryDocument, item);
            }
        });
        //修改库存库位表中特殊仓库的数量
        InvVenSpecialMount(invInventoryDocument, invInventoryDocumentItemList);
    }

    /**
     * 仓库库位库存信息
     */
    public void InvInventoryLocation(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemList.forEach(item -> {
            InvInventoryLocation inventoryLocation = new InvInventoryLocation();
            inventoryLocation.setBarcodeSid(item.getBarcodeSid())
                    .setStorehouseLocationSid(item.getStorehouseLocationSid())
                    .setBusinessOrderSid(item.getReferDocumentSid())
                    .setStorehouseSid(item.getStorehouseSid());
            //获取可用库存量
            InvInventoryLocation location = invInventoryLocationMapper.getLocationAble(inventoryLocation);
//            InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
//                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
//                    .eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
//                    .eq(InvInventoryLocation::getStorehouseSid, invInventoryDocument.getStorehouseSid()));
            String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
            if (location != null) {
                if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                    item.setLocationQuantity(item.getLocationQuantity().multiply(new BigDecimal("-1")));
                }
                location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getLocationQuantity()));
                location.setAbleQuantity(location.getAbleQuantity().subtract(item.getLocationQuantity()));
            }
            if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && !ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) || (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType()))) {//冲销入库须交验
                if (location == null) {
                    throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法出库");
                }
                BigDecimal quantity = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity().abs().setScale(4, BigDecimal.ROUND_HALF_UP);
                if ((location.getAbleQuantity().subtract(quantity).compareTo(new BigDecimal(0))) == -1) {
                    if ((ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType()))) {
                        throw new CustomException("行号为" + item.getItemNum() + "的可用库存量不足，不允许冲销");
                    } else {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存小于出库量，无法出库");
                    }
                }
            }
            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
            BeanCopyUtils.copyProperties(location, invInventoryLocation);
            item.setLocation(invInventoryLocation);
            invInventoryLocationMapper.update(new InvInventoryLocation(), new UpdateWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvInventoryLocation::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid())
                    .set(InvInventoryLocation::getClientId, ApiThreadLocalUtil.get().getClientId())
            );
            oldLocation.put(location.getLocationStockSid(), location);
        });

    }

    /**
     * 商品条码重复计算库存数量
     */
    public void repeat(List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        List<InvInventoryDocumentItem> actualItemList = new ArrayList<InvInventoryDocumentItem>();
        invInventoryDocumentItemList.forEach(item -> {
            actualItemList.add(item);
            BigDecimal total = actualItemList.stream().map(li -> {
                if (item.getBarcodeSid().toString().equals(li.getBarcodeSid().toString())
                        && item.getStorehouseSid().toString().equals(li.getStorehouseSid().toString())
                        && item.getStorehouseLocationSid().toString().equals(li.getStorehouseLocationSid().toString())) {
                    return li.getQuantity() == null ? BigDecimal.ZERO : li.getQuantity();
                } else {
                    return BigDecimal.ZERO;
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (item.getQuantity() == null) {
                item.setLocationQuantity(total);
            } else {
                item.setLocationQuantity(total.subtract(item.getQuantity()));
            }
        });
    }

    /**
     * 供应商特殊库存信息
     */
    public void InvVenSpecialInventory(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemList.forEach(item -> {
//            InvVenSpecialInventory location = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
//                    .eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid())
//                    .eq(InvVenSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
//                    .eq(InvVenSpecialInventory::getStorehouseSid, invInventoryDocument.getStorehouseSid())
//                    .eq(InvVenSpecialInventory::getVendorSid, invInventoryDocument.getVendorSid())
//                    .eq(InvVenSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock())
//            );
            InvInventoryLocation inventory = new InvInventoryLocation();
            inventory.setBarcodeSid(item.getBarcodeSid())
                    .setStorehouseLocationSid(item.getStorehouseLocationSid())
                    .setStorehouseSid(item.getStorehouseSid())
                    .setBusinessOrderSid(item.getReferDocumentSid())
                    .setVendorSid(invInventoryDocument.getVendorSid())
                    .setSpecialStock(invInventoryDocument.getSpecialStock());
            //获取可用库存
            InvInventoryLocation location = invVenSpecialInventoryMapper.getLocationAble(inventory);
            String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
            String movementType = invInventoryDocument.getMovementType();
            String documentType = invInventoryDocument.getDocumentType();
            String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();
            if (location != null) {
                if (item.getLocationQuantity() != null) {
                    if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                        item.setLocationQuantity(item.getLocationQuantity().multiply(new BigDecimal("-1")));
                    }
                    location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getLocationQuantity()));
                    location.setAbleQuantity(location.getAbleQuantity().subtract(item.getLocationQuantity()));
                }
            }
            if (!(movementType.equals(ConstantsEms.RECIPT_V) || movementType.equals(ConstantsEms.RECIPT_C) || DocCategory.SALE_JI_RETURN.getCode().equals(documentType) || DocCategory.SALE_JI_RETURN.getCode().equals(saleAndPurchaseDocument))) {
                if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && !ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) || ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType())) {
                    if (location == null) {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法出库");
                    }
                    if ((location.getAbleQuantity().subtract(item.getQuantity().abs()).compareTo(new BigDecimal(0))) == -1) {
                        if ((ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType()))) {
                            throw new CustomException("行号为" + item.getItemNum() + "的库存量不足，不允许冲销");
                        } else {
                            throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存量小于出库量，无法出库");
                        }
                    }
                }
            } else {
                //按收货单-甲供料退料 按收货单-客户寄售退货 销售寄售退货订单 常规
                if (ConstantsEms.YES.equals(invInventoryDocument.getSpecialVtil())) {
                    if (location == null) {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法出库");
                    }
                    if ((location.getAbleQuantity().subtract(item.getQuantity().abs()).compareTo(new BigDecimal(0))) == -1) {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存量小于出库量，无法出库");

                    }
                }
            }
            InvVenSpecialInventory invVenSpecialInventory = new InvVenSpecialInventory();
            BeanCopyUtils.copyProperties(location, invVenSpecialInventory);
            item.setInvVenSpecialInventory(invVenSpecialInventory);
            invVenSpecialInventoryMapper.update(new InvVenSpecialInventory(), new UpdateWrapper<InvVenSpecialInventory>().lambda()
                    .eq(InvVenSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvVenSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvVenSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvVenSpecialInventory::getVendorSid, invInventoryDocument.getVendorSid())
                    .eq(InvVenSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock())
                    .set(InvVenSpecialInventory::getClientId, ApiThreadLocalUtil.get().getClientId())
            );
            oldLocation.put(location.getVendorSpecialStockSid(), location);
        });
    }

    /**
     * 客户特殊库存信息
     */
    public void InvCusSpecialInventory(Map<Long, Object> oldLocation, InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemList.forEach(item -> {
//            InvCusSpecialInventory location = invCusSpecialInventoryMapper.selectOne(new QueryWrapper<InvCusSpecialInventory>().lambda()
//                    .eq(InvCusSpecialInventory::getBarcodeSid, item.getBarcodeSid())
//                    .eq(InvCusSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
//                    .eq(InvCusSpecialInventory::getStorehouseSid, invInventoryDocument.getStorehouseSid())
//                    .eq(InvCusSpecialInventory::getCustomerSid, invInventoryDocument.getCustomerSid())
//                    .eq(InvCusSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock())
//            );
            InvInventoryLocation inventory = new InvInventoryLocation();
            inventory.setBarcodeSid(item.getBarcodeSid())
                    .setStorehouseLocationSid(item.getStorehouseLocationSid())
                    .setStorehouseSid(item.getStorehouseSid())
                    .setBusinessOrderSid(item.getReferDocumentSid())
                    .setCustomerSid(invInventoryDocument.getCustomerSid())
                    .setSpecialStock(invInventoryDocument.getSpecialStock());
            //获取可用库存
            InvInventoryLocation location = invCusSpecialInventoryMapper.getLocationAble(inventory);
            //销售订单提交校验
            Object o = oldLocation.get(1L);
            String order = o == null ? null : o.toString();
            String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
            String movementType = invInventoryDocument.getMovementType();
            String documentType = invInventoryDocument.getDocumentType();
            String saleAndPurchaseDocument = invInventoryDocument.getSaleAndPurchaseDocument();
            if (location != null) {
                if (item.getLocationQuantity() != null) {
                    if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                        item.setLocationQuantity(item.getLocationQuantity().multiply(new BigDecimal("-1")));
                    }
                    location.setUnlimitedQuantity(location.getUnlimitedQuantity().subtract(item.getLocationQuantity()));
                    location.setAbleQuantity(location.getAbleQuantity().subtract(item.getLocationQuantity()));
                }
            }
            if (!(movementType.equals(ConstantsEms.RECIPT_V) || movementType.equals(ConstantsEms.RECIPT_C) || DocCategory.SALE_JI_RETURN.getCode().equals(documentType) || DocCategory.SALE_JI_RETURN.getCode().equals(saleAndPurchaseDocument))) {
                if (ConstantsEms.CHU_KU.equals(invInventoryDocument.getType()) && !ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) || ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType())) {
                    if (location == null) {
                        if ("order".equals(order)) {
                            throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法提交");
                        } else {

                            throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法出库");
                        }
                    }
                    if ((location.getAbleQuantity().subtract(item.getQuantity().abs()).compareTo(new BigDecimal(0))) == -1) {
                        if ("order".equals(order)) {
                            throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存量小于出库量，无法提交");
                        } else {
                            if ((ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv) && ConstantsEms.RU_KU.equals(invInventoryDocument.getType()))) {
                                throw new CustomException("行号为" + item.getItemNum() + "的库存量不足，不允许冲销");
                            } else {
                                throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存量小于出库量，无法出库");
                            }
                        }
                    }
                }
            } else {
                //按收货单-甲供料退料 按收货单-客户寄售退货 销售寄售退货订单   常规
                if (ConstantsEms.YES.equals(invInventoryDocument.getSpecialVtil())) {
                    if (location == null) {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下没有相关信息，无法出库");
                    }
                    if ((location.getAbleQuantity().subtract(item.getQuantity().abs()).compareTo(new BigDecimal(0))) == -1) {
                        throw new CustomException("行号为" + item.getItemNum() + "的仓库的库位下的可用库存量小于出库量，无法出库");

                    }
                }
            }
            InvCusSpecialInventory invCusSpecialInventory = new InvCusSpecialInventory();
            BeanCopyUtils.copyProperties(location, invCusSpecialInventory);
            item.setInvCusSpecialInventory(invCusSpecialInventory);
            invCusSpecialInventoryMapper.update(new InvCusSpecialInventory(), new UpdateWrapper<InvCusSpecialInventory>().lambda()
                    .eq(InvCusSpecialInventory::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvCusSpecialInventory::getStorehouseLocationSid, item.getStorehouseLocationSid())
                    .eq(InvCusSpecialInventory::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvCusSpecialInventory::getCustomerSid, invInventoryDocument.getCustomerSid())
                    .eq(InvCusSpecialInventory::getSpecialStock, invInventoryDocument.getSpecialStock())
                    .set(InvCusSpecialInventory::getClientId, ApiThreadLocalUtil.get().getClientId())
            );
            oldLocation.put(location.getCustomerSpecialStockSid(), location);
        });
    }

    /**
     * 库存凭证-明细对象
     */
    private void addInvInventoryDocumentItem(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        invInventoryDocumentItemMapper.delete(
                new UpdateWrapper<InvInventoryDocumentItem>()
                        .lambda()
                        .eq(InvInventoryDocumentItem::getInventoryDocumentSid, invInventoryDocument.getInventoryDocumentSid())
        );
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        List<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
        invInventoryDocumentItemList.forEach(o -> {
            // 若作业类型为“按销售订单”、“按销售发货单”、“按采购退货订单”、“按采购退货发货单”，出库生成库存凭证的价格取值为对应订单明细中的价格
            if (ConstantsInventory.MOVEMENT_TYPE_SC01.equals(invInventoryDocument.getMovementType())) {
                SalSalesOrderItem orderItem = salSalesOrderItemMapper.selectById(o.getReferDocumentItemSid());
                if (orderItem != null) {
                    o.setPrice(orderItem.getSalePriceTax());
                }
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SC02.equals(invInventoryDocument.getMovementType())) {
                DelDeliveryNoteItem delDeliveryNoteItem = delDeliveryNoteItemMapper.selectById(o.getReferDocumentItemSid());
                if (delDeliveryNoteItem != null && delDeliveryNoteItem.getSalesOrderItemSid() != null) {
                    SalSalesOrderItem orderItem = salSalesOrderItemMapper.selectById(delDeliveryNoteItem.getSalesOrderItemSid());
                    if (orderItem != null) {
                        o.setPrice(orderItem.getSalePriceTax());
                    }
                }
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SC03.equals(invInventoryDocument.getMovementType())) {
                PurPurchaseOrderItem orderItem = purPurchaseOrderItemMapper.selectById(o.getReferDocumentItemSid());
                if (orderItem != null) {
                    o.setPrice(orderItem.getPurchasePriceTax());
                }
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SC04.equals(invInventoryDocument.getMovementType())) {
                DelDeliveryNoteItem delDeliveryNoteItem = delDeliveryNoteItemMapper.selectById(o.getReferDocumentItemSid());
                if (delDeliveryNoteItem != null && delDeliveryNoteItem.getPurchaseOrderItemSid() != null) {
                    PurPurchaseOrderItem orderItem = purPurchaseOrderItemMapper.selectById(o.getReferDocumentItemSid());
                    if (orderItem != null) {
                        o.setPrice(orderItem.getPurchasePriceTax());
                    }
                }
            }
            o.setStorehouseSid(invInventoryDocument.getStorehouseSid());
            o.setDestStorehouseSid(invInventoryDocument.getDestStorehouseSid());
            if (!ConstantsEms.YES.equals(invInventoryDocument.getIsMultiOrder()) && invInventoryDocument.getReferDocumentCode() != null) {
                o.setReferDocumentCode(invInventoryDocument.getReferDocumentCode());
            }
            if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
                if (o.getPriceQuantity() != null) {
                    o.setPriceQuantity(o.getPriceQuantity().abs().multiply(new BigDecimal("-1")));
                }
                if (ConstantsEms.SALE_ORDER_RU_DIRECT.equals(invInventoryDocument.getMovementType())) {
                    o.setQuantity(o.getQuantity().abs().multiply(new BigDecimal("-1")));
                }
            }
            //发货单常特库存转移
            if ("ST52".equals(invInventoryDocument.getMovementType())) {
                if (o.getReferDocumentSid() != null) {
                    delDeliveryNoteMapper.update(new DelDeliveryNote(), new UpdateWrapper<DelDeliveryNote>().lambda()
                            .eq(DelDeliveryNote::getDeliveryNoteSid, o.getReferDocumentSid())
                            .set(DelDeliveryNote::getIsDirectTransportFollowup, ConstantsEms.YES)
                            .set(DelDeliveryNote::getFollowupBusinessType, "WWPF"));
                }
            }
            if (!ConstantsEms.YES.equals(invInventoryDocument.getIsMultiOrder()) && invInventoryDocument.getReferDocumentSid() != null) {
                o.setReferDocumentSid(invInventoryDocument.getReferDocumentSid());
            }
            o.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(invInventoryDocument.getDocumentCategory())
                    && ConstantsInventory.REFER_DOC_CAT_DBD.equals(invInventoryDocument.getReferDocCategory())) {
                o.setDestStorehouseSid(null).setDestStorehouseLocationSid(null);
            }
            invInventoryDocumentItems.add(o);
        });
        invInventoryDocumentItemMapper.inserts(invInventoryDocumentItems);
    }

    /**
     * 修改库存凭证
     *
     * @param invInventoryDocument 库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryDocument(InvInventoryDocument invInventoryDocument) {
        //设置确认信息
        invInventoryDocumentMapper.updateAllById(invInventoryDocument);
        //库存凭证-明细list
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtil.isNotEmpty(invInventoryDocumentItemList)) {
            addInvInventoryDocumentItem(invInventoryDocument, invInventoryDocumentItemList);
        }
        //库存凭证-附件list
        //TODO
        //库存凭证-合作伙伴list
        //TODO
        return 1;
    }

    @Override
    public void exportPur(HttpServletResponse response, List<InvInventoryDocumentReportResponse> list) {
        list = sortResponse(list);
        int size = list.size();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 绘制excel表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("采购结算单");
            sheet.setDefaultColumnWidth(16);
            // 单元格格式
            CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
            CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
            defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
            defaultCellStyleNo.setBorderBottom(BorderStyle.NONE);
            defaultCellStyleNo.setBorderLeft(BorderStyle.NONE);
            defaultCellStyleNo.setBorderRight(BorderStyle.NONE);
            defaultCellStyleNo.setBorderTop(BorderStyle.NONE);
            // 样式 - 灰色
            XSSFColor color = new XSSFColor(new java.awt.Color(238, 236, 225));
            XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook, color);
            XSSFCellStyle cellStyleGrayLeft = ExcelStyleUtil.getXSSFCellStyle(workbook, color);
            cellStyleGrayLeft.setAlignment(HorizontalAlignment.LEFT);
            InvInventoryDocumentReportResponse docment = list.get(0);
            // 每段标题
            String[] titleTipsOne = {list.get(0).getCompanyName() + "采购结算单", "", "", "", "", "", "", "", "", ""};

            // 对日期排序
            String startD = "", endD = "";
            List<InvInventoryDocumentReportResponse> dateSort = list.stream().filter(o->o.getAccountDate() != null).collect(toList());
            if (CollectionUtil.isNotEmpty(dateSort)) {
                dateSort = dateSort.stream().sorted(Comparator.comparing(InvInventoryDocumentReportResponse::getAccountDate)).collect(toList());
                startD = sdf.format(dateSort.get(0).getAccountDate());
                endD = sdf.format(dateSort.get(dateSort.size() - 1).getAccountDate());
            }
            String[] titleTipsTwo = {"供应商：", docment.getVendorName(), "", "", "", "", startD, "至", endD, ""};
            String[] titleTipsThird = {
                    "出入库日期：",
                    "物料编码",
                    "物料名称",
                    "颜色/尺码/规格型号",
                    "单位",
                    "数量",
                    "单价",
                    "金额(元)",
                    "采购订单号",
                    "库存凭证号"};
            BigDecimal tatalQuantity = list.stream().filter(li -> li.getPriceQuantity() != null).map(li -> li.getPriceQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tatalprice = list.stream().filter(li -> li.getPriceQuantity() != null && li.getPrice() != null).map(li -> li.getPriceQuantity().multiply(li.getPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
            String[] titleTipsTotal = {
                    "总计：",
                    "",
                    "",
                    "",
                    "",
                    tatalQuantity.toString(),
                    "",
                    tatalprice.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    "",
                    ""};
            Row one = sheet.createRow(0);
            one.setHeight((short) 600);
            for (int i = 0; i < titleTipsOne.length; i++) {
                Cell cell0 = one.createCell(i);
                cell0.setCellValue(titleTipsOne[i]);
                cell0.setCellStyle(defaultCellStyle);
            }
            Row two = sheet.createRow(1);
            for (int i = 0; i < titleTipsTwo.length; i++) {
                Cell twoCell0 = two.createCell(i);
                twoCell0.setCellValue(titleTipsTwo[i]);
                twoCell0.setCellStyle(defaultCellStyle);
            }
            Row third = sheet.createRow(2);
            for (int i = 0; i < titleTipsThird.length; i++) {
                Cell thirdCell = third.createCell(i);
                thirdCell.setCellValue(titleTipsThird[i]);
                thirdCell.setCellStyle(defaultCellStyle);
            }
            //数据部分
            for (int i = 0; i < list.size(); i++) {
                Row data = sheet.createRow(i + 3);
                //出入库日期
                Cell Cell1 = data.createCell(0);
                Cell1.setCellValue(sdf.format(list.get(i).getAccountDate()));
                Cell1.setCellStyle(defaultCellStyle);
                //物料编码
                Cell Cell2 = data.createCell(1);
                Cell2.setCellValue(list.get(i).getMaterialCode());
                Cell2.setCellStyle(defaultCellStyle);
                //物料名称
                Cell Cell3 = data.createCell(2);
                Cell3.setCellValue(list.get(i).getMaterialName());
                Cell3.setCellStyle(defaultCellStyle);
                //颜色/尺码/规格型号
                String type = list.get(i).getSku2Name() != null ? list.get(i).getSku1Name() + "/" + list.get(i).getSku2Name() : list.get(i).getSku1Name();
                //规格型号
                String specificationSize = list.get(i).getSpecificationSize() != null ? list.get(i).getSpecificationSize() : "";
                String modelSize = list.get(i).getModelSize() != null ? list.get(i).getModelSize() : "";
                if (!specificationSize.equals("") || !modelSize.equals("")) {
                    type = type + "/" + specificationSize + modelSize;
                }
                Cell Cell4 = data.createCell(3);
                Cell4.setCellValue(type);
                Cell4.setCellStyle(defaultCellStyle);
                //单位
                Cell Cell5 = data.createCell(4);
                Cell5.setCellValue(list.get(i).getUnitPriceName());
                Cell5.setCellStyle(defaultCellStyle);
                //数量
                Cell Cell6 = data.createCell(5);
                Cell6.setCellValue(list.get(i).getPriceQuantity() != null ? list.get(i).getPriceQuantity().toString() : null);
                Cell6.setCellStyle(defaultCellStyle);
                //单价
                Cell Cell7 = data.createCell(6);
                Cell7.setCellValue(list.get(i).getPrice() != null ? list.get(i).getPrice().toString() : null);
                Cell7.setCellStyle(defaultCellStyle);
                //金额
                Cell Cell8 = data.createCell(7);
                Cell8.setCellValue(list.get(i).getPrice() != null && list.get(i).getPriceQuantity() != null ? list.get(i).getPrice().multiply(list.get(i).getPriceQuantity()).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString() : null);
                Cell8.setCellStyle(defaultCellStyle);
                //采购订单号
                Cell Cell9 = data.createCell(8);
                Cell9.setCellValue(list.get(i).getPurchaseOrderCode());
                Cell9.setCellStyle(defaultCellStyle);
                //库存凭证
                Cell Cell10 = data.createCell(9);
                Cell10.setCellValue(list.get(i).getInventoryDocumentCode());
                Cell10.setCellStyle(defaultCellStyle);
            }
            Row tatalData = sheet.createRow(list.size() + 3);
            //总计
            for (int i = 0; i < titleTipsTotal.length; i++) {
                Cell Cell1 = tatalData.createCell(i);
                Cell1.setCellValue(titleTipsTotal[i]);
                if (i == 0) {
                    CellStyle style = workbook.createCellStyle();
                    style.setAlignment(HorizontalAlignment.RIGHT);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setBorderBottom(BorderStyle.THIN); //下边框
                    style.setBorderLeft(BorderStyle.THIN);//左边框
                    style.setBorderTop(BorderStyle.THIN);//上边框
                    style.setBorderRight(BorderStyle.THIN);//右边框
                    Cell1.setCellStyle(style);
                } else {
                    Cell1.setCellStyle(defaultCellStyle);
                }
            }
            CellRangeAddress region2 = new CellRangeAddress(0, 0, 0, 9);
            sheet.addMergedRegion(region2);
            CellRangeAddress region3 = new CellRangeAddress(list.size() + 3, list.size() + 3, 0, 4);
            sheet.addMergedRegion(region3);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    @Override
    public void exportSal(HttpServletResponse response, List<InvInventoryDocumentReportResponse> list) {
        list = sortResponse(list);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int size = list.size();
        // 绘制excel表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("销售结算单");
            sheet.setDefaultColumnWidth(16);
            // 单元格格式
            CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
            CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
            defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
            defaultCellStyleNo.setBorderBottom(BorderStyle.NONE);
            defaultCellStyleNo.setBorderLeft(BorderStyle.NONE);
            defaultCellStyleNo.setBorderRight(BorderStyle.NONE);
            defaultCellStyleNo.setBorderTop(BorderStyle.NONE);
            // 样式 - 灰色
            XSSFColor color = new XSSFColor(new java.awt.Color(238, 236, 225));
            XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook, color);
            XSSFCellStyle cellStyleGrayLeft = ExcelStyleUtil.getXSSFCellStyle(workbook, color);
            cellStyleGrayLeft.setAlignment(HorizontalAlignment.LEFT);
            InvInventoryDocumentReportResponse docment = list.get(0);
            // 每段标题
            String[] titleTipsOne = {list.get(0).getCompanyName() + "销售结算单", "", "", "", "", "", "", "", "", ""};
            // 对日期排序
            String startD = "", endD = "";
            List<InvInventoryDocumentReportResponse> dateSort = list.stream().filter(o->o.getAccountDate() != null).collect(toList());
            if (CollectionUtil.isNotEmpty(dateSort)) {
                dateSort = dateSort.stream().sorted(Comparator.comparing(InvInventoryDocumentReportResponse::getAccountDate)).collect(toList());
                startD = sdf.format(dateSort.get(0).getAccountDate());
                endD = sdf.format(dateSort.get(dateSort.size() - 1).getAccountDate());
            }
            String[] titleTipsTwo = {"客户：", docment.getCustomerName(), "", "", "", "", startD, "至", endD, ""};
            String[] titleTipsThird = {
                    "出入库日期：",
                    "商品编码",
                    "商品名称",
                    "颜色/尺码/规格型号",
                    "单位",
                    "数量",
                    "单价",
                    "金额(元)",
                    "销售订单号",
                    "库存凭证号"};
            BigDecimal tatalQuantity = list.stream().filter(li -> li.getPriceQuantity() != null).map(li -> li.getPriceQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tatalprice = list.stream().filter(li -> li.getPriceQuantity() != null && li.getPrice() != null).map(li -> li.getPriceQuantity().multiply(li.getPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
            String[] titleTipsTotal = {
                    "总计：",
                    "",
                    "",
                    "",
                    "",
                    tatalQuantity.toString(),
                    "",
                    tatalprice.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_DOWN).toString(),
                    "",
                    ""};
            Row one = sheet.createRow(0);
            one.setHeight((short) 600);
            for (int i = 0; i < titleTipsOne.length; i++) {
                Cell cell0 = one.createCell(i);
                cell0.setCellValue(titleTipsOne[i]);
                cell0.setCellStyle(defaultCellStyle);
            }
            Row two = sheet.createRow(1);
            for (int i = 0; i < titleTipsTwo.length; i++) {
                Cell twoCell0 = two.createCell(i);
                twoCell0.setCellValue(titleTipsTwo[i]);
                twoCell0.setCellStyle(defaultCellStyle);
            }
            Row third = sheet.createRow(2);
            for (int i = 0; i < titleTipsThird.length; i++) {
                Cell thirdCell = third.createCell(i);
                thirdCell.setCellValue(titleTipsThird[i]);
                thirdCell.setCellStyle(defaultCellStyle);
            }
            //数据部分
            for (int i = 0; i < list.size(); i++) {
                Row data = sheet.createRow(i + 3);
                //出入库日期
                Cell Cell1 = data.createCell(0);
                Cell1.setCellValue(sdf.format(list.get(i).getAccountDate()));
                Cell1.setCellStyle(defaultCellStyle);
                //物料编码
                Cell Cell2 = data.createCell(1);
                Cell2.setCellValue(list.get(i).getMaterialCode());
                Cell2.setCellStyle(defaultCellStyle);
                //物料名称
                Cell Cell3 = data.createCell(2);
                Cell3.setCellValue(list.get(i).getMaterialName());
                Cell3.setCellStyle(defaultCellStyle);
                //颜色/尺码/规格型号
                String type = list.get(i).getSku2Name() != null ? list.get(i).getSku1Name() + "/" + list.get(i).getSku2Name() : list.get(i).getSku1Name();
                //规格型号
                String specificationSize = list.get(i).getSpecificationSize() != null ? list.get(i).getSpecificationSize() : "";
                String modelSize = list.get(i).getModelSize() != null ? list.get(i).getModelSize() : "";
                if (!specificationSize.equals("") || !modelSize.equals("")) {
                    type = type + "/" + specificationSize + modelSize;
                }
                Cell Cell4 = data.createCell(3);
                Cell4.setCellValue(type);
                Cell4.setCellStyle(defaultCellStyle);
                //单位
                Cell Cell5 = data.createCell(4);
                Cell5.setCellValue(list.get(i).getUnitPriceName());
                Cell5.setCellStyle(defaultCellStyle);
                //数量
                Cell Cell6 = data.createCell(5);
                Cell6.setCellValue(list.get(i).getPriceQuantity() != null ? list.get(i).getPriceQuantity().toString() : null);
                Cell6.setCellStyle(defaultCellStyle);
                //单价
                Cell Cell7 = data.createCell(6);
                Cell7.setCellValue(list.get(i).getPrice() != null ? list.get(i).getPrice().toString() : null);
                Cell7.setCellStyle(defaultCellStyle);
                //金额
                Cell Cell8 = data.createCell(7);
                Cell8.setCellValue(list.get(i).getPrice() != null && list.get(i).getPriceQuantity() != null ? list.get(i).getPrice().multiply(list.get(i).getPriceQuantity()).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString() : null);
                Cell8.setCellStyle(defaultCellStyle);
                //采购订单号
                Cell Cell9 = data.createCell(8);
                Cell9.setCellValue(list.get(i).getSalesOrderCode());
                Cell9.setCellStyle(defaultCellStyle);
                //库存凭证
                Cell Cell10 = data.createCell(9);
                Cell10.setCellValue(list.get(i).getInventoryDocumentCode());
                Cell10.setCellStyle(defaultCellStyle);
            }
            Row tatalData = sheet.createRow(list.size() + 3);
            //总计
            for (int i = 0; i < titleTipsTotal.length; i++) {
                Cell Cell1 = tatalData.createCell(i);
                Cell1.setCellValue(titleTipsTotal[i]);
                if (i == 0) {
                    CellStyle style = workbook.createCellStyle();
                    style.setAlignment(HorizontalAlignment.RIGHT);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setBorderBottom(BorderStyle.THIN); //下边框
                    style.setBorderLeft(BorderStyle.THIN);//左边框
                    style.setBorderTop(BorderStyle.THIN);//上边框
                    style.setBorderRight(BorderStyle.THIN);//右边框
                    Cell1.setCellStyle(style);
                } else {
                    Cell1.setCellStyle(defaultCellStyle);
                }
            }
            CellRangeAddress region2 = new CellRangeAddress(0, 0, 0, 9);
            sheet.addMergedRegion(region2);
            CellRangeAddress region3 = new CellRangeAddress(list.size() + 3, list.size() + 3, 0, 4);
            sheet.addMergedRegion(region3);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    /**
     * 批量删除库存凭证
     *
     * @param inventoryDocumentSids 需要删除的库存凭证ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventoryDocumentByIds(Long[] inventoryDocumentSids) {
        //删除库存凭证
        invInventoryDocumentMapper.deleteInvInventoryDocumentByIds(inventoryDocumentSids);
        //删除库存凭证-明细
        invInventoryDocumentItemMapper.deleteInvInventoryDocumentItemByIds(inventoryDocumentSids);
        return inventoryDocumentSids.length;
    }

    /**
     * 库存凭证确认
     */
    @Override
    public int confirm(Long[] inventoryDocumentSidList) {
        for (int i = 0; i < inventoryDocumentSidList.length; i++) {
            InvInventoryDocument invInventoryDocument = selectInvInventoryDocumentById(inventoryDocumentSidList[i]);
            deleteInvDocument(invInventoryDocument);
            invInventoryDocument.setType(ConstantsEms.CHU_KU);
            insertInvInventoryDocument(invInventoryDocument);
        }

        return 0;
    }

    /**
     * 查询商品领用物料统计报表
     *
     * @param request
     * @return
     */
    @Override
    public List<InvInventoryProductUserMaterial> productUserMaterialStatistics(InvInventoryProductUserMaterial request) {
        return invInventoryDocumentMapper.productUserMaterialStatistics(request);
    }

    /**
     * 库存凭证变更
     */
    @Override
    public int change(InvInventoryDocument invInventoryDocument) {
        Long inventoryDocumentSid = invInventoryDocument.getInventoryDocumentSid();
        InvInventoryDocument inventoryDocument = invInventoryDocumentMapper.selectInvInventoryDocumentById(inventoryDocumentSid);

        invInventoryDocumentMapper.updateAllById(invInventoryDocument);
        //库存凭证明细对象
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = invInventoryDocument.getInvInventoryDocumentItemList();
        if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
            addInvInventoryDocumentItem(invInventoryDocument, invInventoryDocumentItemList);
        }
        //库存凭证-附件list
        //TODO
        //库存凭证-合作伙伴list
        //TODO
        return 1;
    }

    //是否是入库
    public Boolean RU(String code) {
        List<String> transferList = conInOutStockMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                .eq(ConInOutStockDocCategory::getInvDocCategoryCode, DocumentCategory.RU.getCode())).stream().map(o -> o.getMovementTypeCode()).collect(Collectors.toList());
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //是否是普通移库
    public Boolean exitTransfer(String code) {
        //普通移库-
        ArrayList<Object> list = new ArrayList<>();
        List<String> transferList = Arrays.asList("SY01", "SY011", "SY012", "SY013", "SY014", "SY02", "SY021", "SY022", "SY023", "SY024", "SY61", "SY62");
//           List<String> transferList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
//                   .eq(ConMovementType::getIsBnDisplay, "Y")).stream().map(o -> o.getCode()).collect(Collectors.toList());
        List<ConMovementType> conMovementTypes = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
                .eq(ConMovementType::getIsTransferOnestep, ConstantsEms.YES)

        );
        //调拨(一步)
        if (CollectionUtil.isNotEmpty(conMovementTypes)) {
            List<String> isTransferOnestepList = conMovementTypes.stream().map(li -> li.getCode()).collect(Collectors.toList());
            list.addAll(isTransferOnestepList);
        }
        list.addAll(transferList);
        boolean exit = list.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //是否是一步调拨
    public Boolean exitOneTransfer(String code) {
        //普通移库-
        ArrayList<Object> list = new ArrayList<>();
        List<ConMovementType> conMovementTypes = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
                .eq(ConMovementType::getIsTransferOnestep, ConstantsEms.YES)

        );
        //调拨(一步)
        if (CollectionUtil.isNotEmpty(conMovementTypes)) {
            List<String> isTransferOnestepList = conMovementTypes.stream().map(li -> li.getCode()).collect(Collectors.toList());
            list.addAll(isTransferOnestepList);
        }
        boolean exit = list.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //是否是特殊移库(特殊仓库转自有库存)
    public int SpecialToTransfer(String code) {
        //特殊移库
        List<String> specialTransferList = Arrays.asList("ST51", "ST53", "ST55", "ST57");
        boolean exit = specialTransferList.stream().anyMatch(item -> item.equals(code));
        return exit == true ? 1 : 0;
    }

    //是否是特殊移库(自有库存转特殊仓库)
    public int CommonToTransfer(String code) {
        //特殊移库
        List<String> specialTransferList = Arrays.asList("ST52", "ST54", "ST56", "ST58");
        boolean exit = specialTransferList.stream().anyMatch(item -> item.equals(code));
        return exit == true ? 2 : 0;
    }

    //是否是出库调拨单\移库两步法
    public Boolean exitAllocate(String code) {
        //出库调拨单
        List<String> allocateList = Arrays.asList("SC08", "SC081", "SC082", "SC083", "SC084", "SC11", "SC111", "SC112");
//            List<String> allocateList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
//                    .eq(ConMovementType::getIsGiDisplay, "Y")
//                    .eq(ConMovementType::getIsItnDisplay,"Y")
//            ).stream().map(o -> o.getCode()).collect(Collectors.toList());
        boolean exit = allocateList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //是否是入库调拨单\移库两步法
    public Boolean exitRuAllocate(String code) {
        //入库调拨单
        List<String> allocateList = Arrays.asList("SR08", "SR081", "SR082", "SR083", "SR084", "SR11", "SR111", "SR112");
//            List<String> allocateList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
//                    .eq(ConMovementType::getIsGrDisplay, "Y")
//                    .eq(ConMovementType::getIsItnDisplay,"Y")
//            ).stream().map(o -> o.getCode()).collect(Collectors.toList());
        boolean exit = allocateList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //是否是库存调整-串色串码
    public Boolean exitAdjust(String code) {
        //串色串码
        List<String> transferList = Arrays.asList("ST041", "ST042", "ST043", "ST044");
//            List<String> transferList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
//                    .eq(ConMovementType::getIsCstDisplay, "Y")
//            ).stream().map(o -> o.getCode()).collect(Collectors.toList());
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //收发货或者领退料单、甲供料结算单、结果初始化
    public Boolean materialAndGoods(String code) {
        List<String> transferList = Arrays.asList("SC07", "SC071", "SC072", "SC09", "SC091", "SC091", "SR07", "SR071", "SR072", "SR09", "SR091", "SR092", "SC099", "SR093", "SR094", "SC093", "SC094", "SC61", "SR61");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //供应商寄售待结算台账 扣减（退料单-供应商寄售、自有库存转供应商寄售）
    public Boolean PurRecordVendorReduce(String code) {
        List<String> transferList = Arrays.asList("SR072", "ST56");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //供应商寄售待结算台账 增加（领料单-供应商寄售、销售退货订单、供应商寄售转自有库存）
    public Boolean PurRecordVendorAdd(String code) {
        List<String> transferList = Arrays.asList("SC072", "ST55");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    //是否是调拨单
    public Boolean Adjust(String code) {
        List<String> transferList = conInOutStockMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                .eq(ConInOutStockDocCategory::getInvDocCategoryCode, DocumentCategory.ADJUST.getCode())).stream().map(o -> o.getMovementTypeCode()).collect(Collectors.toList());
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    /**
     * 明细页签导入明细列表
     * @param file 文件
     * @return 列表结果
     */
    @Override
    public EmsResultEntity importItemList(MultipartFile file, String documentCategory) {
        List<InvInventoryDocumentItem> documentItemList = new ArrayList<>();
        InvInventoryDocumentItem documentItem = null;
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        // text
        String text = "数";
        if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(documentCategory)) {
            text = "入库";
        } else if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(documentCategory)) {
            text = "出库";
        } else if (ConstantsInventory.DOCUMENT_CATEGORY_YK.equals(documentCategory)) {
            text = "移库";
        }

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
                int num = i + 1;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);

                documentItem = new InvInventoryDocumentItem();

                /*
                 * 物料/商品编码
                 */
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料/商品编码不能为空！");
                    errMsgList.add(errMsg);
                }
                // 提示信息
                String sku1 = "", sku2 = "";
                /*
                 * SKU1名称
                 */
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isNotBlank(sku1Name)) {
                    sku1 = "+SKU1名称(" + sku1Name + ")";
                }
                /*
                 * SKU2名称
                 */
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isNotBlank(sku2Name)) {
                    sku2 = "+SKU2名称(" + sku2Name + ")";
                }
                /*
                 * 校验商品条码
                 */
                if (StrUtil.isNotBlank(materialCode)) {
                    try {
                        BasMaterialBarcode materialBarcode = materialBarcodeMapper.selectBasMaterialBarcodeListByInvImport(
                                new BasMaterialBarcode().setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                        if (materialBarcode == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料/商品编码(" + materialCode + ")" + sku1 + sku2 + "组合的商品条码不存在！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            BeanCopyUtils.copyProperties(materialBarcode, documentItem);
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料/商品编码(" + materialCode + ")" + sku1 + sku2 + "的组合存在多笔商品条码，请先核实！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 入库量
                 */
                String quantity_s = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal quantity = null;
                if (quantity_s != null) {
                    if (!JudgeFormat.isValidDouble(quantity_s, 8)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(text + "量数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        quantity = new BigDecimal(quantity_s);
                        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(text + "量数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            quantity = quantity.divide(BigDecimal.ONE, 4, BigDecimal.ROUND_HALF_UP);
                            documentItem.setQuantity(quantity);
                        }
                    }
                }
                /*
                 * 款备注
                 */
                if (!documentCategory.equals(ConstantsInventory.DOCUMENT_CATEGORY_YK)) {
                    String productCodes = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                    if (StrUtil.isNotBlank(productCodes)) {
                        if (productCodes.length() > 300){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("款备注不能超过300个字符，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        documentItem.setProductCodes(productCodes);
                    }
                }
                // 写入列表
                if (CollUtil.isEmpty(errMsgList)) {
                    documentItemList.add(documentItem);
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        if (CollUtil.isNotEmpty(errMsgList)) {
            return EmsResultEntity.error(errMsgList);
        }
        return EmsResultEntity.success(documentItemList);
    }

    //填充
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

}
