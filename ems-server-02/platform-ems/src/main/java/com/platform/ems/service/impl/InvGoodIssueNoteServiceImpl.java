package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvIssueNoteReportRequest;
import com.platform.ems.domain.dto.request.InvMaterialRequisitionReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvIssueNoteReportResponse;
import com.platform.ems.domain.dto.response.InvMaterialRequisitionReportResponse;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConInOutStockDocCategory;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.mapper.ConInOutStockDocCategoryMapper;
import com.platform.ems.plug.mapper.ConMovementTypeMapper;
import com.platform.ems.plug.mapper.ConSpecialStockMapper;
import com.platform.ems.service.IInvGoodIssueNoteService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteFlowableService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 发货单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@Service
@SuppressWarnings("all")
public class InvGoodIssueNoteServiceImpl extends ServiceImpl<InvGoodIssueNoteMapper, InvGoodIssueNote> implements IInvGoodIssueNoteService {
    @Autowired
    private InvGoodIssueNoteMapper invGoodIssueNoteMapper;
    @Autowired
    private InvGoodIssueNoteItemMapper invGoodIssueNoteItemMapper;
    @Autowired
    private  InvGoodIssueNoteAttachmentMapper attachmentMapper;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private ConSpecialStockMapper conSpecialStockMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private InvCusSpecialInventoryMapper invCusSpecialInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private InvReserveInventoryMapper invReserveInventoryMapper;
    private static final String TITLE = "发货单";

    /**
     * 查询发货单
     *
     * @param goodIssueNoteSid 发货单ID
     * @return 发货单
     */
    @Override
    public InvGoodIssueNote selectInvGoodIssueNoteById(Long goodIssueNoteSid) {
        InvGoodIssueNote invGoodIssueNote = invGoodIssueNoteMapper.selectInvGoodIssueNoteById(goodIssueNoteSid);
        List<InvGoodIssueNoteItem> invGoodIssueNoteItems = invGoodIssueNoteItemMapper.selectInvGoodIssueNoteItemById(goodIssueNoteSid);
        List<InvGoodIssueNoteAttachment> invGoodIssueNoteAttachments = attachmentMapper.selectInvGoodIssueNoteAttachmentList(new InvGoodIssueNoteAttachment().setNoteSid(goodIssueNoteSid));
        List<InvGoodIssueNoteItem> items = sort(invGoodIssueNoteItems, null);
        invGoodIssueNote.setListInvGoodIssueNoteItem(items);
        invGoodIssueNote.setAttachmentList(invGoodIssueNoteAttachments);
        MongodbUtil.find(invGoodIssueNote);
        return invGoodIssueNote;
    }
    @Override
    public List<InvGoodIssueNoteItem> sort(List<InvGoodIssueNoteItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvGoodIssueNoteItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                        if(!JudgeFormat.isValidDouble(li.getFirstSort())){
                            li.setFirstSort("10000");
                        }
                    });
                    List<InvGoodIssueNoteItem> allList = new ArrayList<>();
                    List<InvGoodIssueNoteItem> allThirdList = new ArrayList<>();
                    List<InvGoodIssueNoteItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvGoodIssueNoteItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvGoodIssueNoteItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvGoodIssueNoteItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvGoodIssueNoteItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvGoodIssueNoteItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvGoodIssueNoteItem::getMaterialCode)
                        .thenComparing(InvGoodIssueNoteItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvGoodIssueNoteItem::getMaterialCode)
                        .thenComparing(InvGoodIssueNoteItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return items;
        }
        return new ArrayList<>();
    }
    /**
     * 物料需求测算-创建收货单
     */
    @Override
    public InvGoodIssueNote getGoodIssueNote(List<TecBomItemReport> order) {
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
        InvGoodIssueNote invGoodIssueNote = new InvGoodIssueNote();
        invGoodIssueNote
                .setDocumentType(DocCategory.SHIP.getCode())
                .setCreateDate(new Date())
                .setVendorSid(vendorSid)
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setInOutStockStatus("WCK")
                .setSpecialStock(ConstantsEms.VEN_RA)
                .setDocumentDate(new Date())
                .setHandleStatus(ConstantsEms.SAVA_STATUS);
        List<InvGoodIssueNoteItem> orderItems = new ArrayList<>();
        order.forEach(li->{
            BasMaterialBarcode basMaterialBarcode=null;
            Long barcodeSid=null;
            String barcode=null;
            InvGoodIssueNoteItem invGoodIssueNoteItem = new InvGoodIssueNoteItem();
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
                throw new CustomException(li.getMaterialCode()+","+li.getMaterialName()+"没有对应的商品条码，无法创建发货单");
            }
            invGoodIssueNoteItem.setMaterialSid(li.getBomMaterialSid())
                    .setMaterialName(li.getMaterialName())
                    .setMaterialCode(li.getMaterialCode())
                    .setSku1Name(li.getSku1Name())
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setBarcodeSid(barcodeSid)
                    .setInOutStockStatus("WCK")
                    .setBarcode(Long.valueOf(barcode))
                    .setSku2Name(li.getSku2Name())
                    .setQuantity(BigDecimal.valueOf(Double.valueOf(li.getLossRequireQuantity())))
                    .setUnitBase(li.getUnitBase())
                    .setProductPoCodes(li.getPurchaseOrderCodeRemark())
                    .setUnitBaseName(li.getUnitBaseName())
                    .setProductCode(li.getSaleMaterialCode())
                    .setProductSid(li.getSaleMaterialSid())
                    .setProductName(li.getSaleMaterialName())
                    .setProductSku1Sid(li.getSaleSku1Sid())
                    .setProductQuantity(li.getProductQuantity())
                    .setProductSku1Name(li.getSaleSku1Name())
                    .setProductSku2Name(li.getSaleSku2Name())
                    .setProductSku2Sid(li.getSaleSku2Sid())
                    .setProductCodes(li.getMaterialCodeRemark())
                    .setProductSku1Names(li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getMaterialSku2Remark())
                    .setReferPurchaseOrderCode(li.getPurchaseOrderCode())
                    .setReferPurchaseOrderSid(li.getPurchaseOrderCode()!=null?li.getCommonSid():null)
                    .setReferPurchaseOrderItemSid(li.getPurchaseOrderCode()!=null?li.getCommonItemSid():null)
                    .setReferPurchaseOrderItemNum(li.getPurchaseOrderCode()!=null?li.getCommonItemNum():null);
            orderItems.add(invGoodIssueNoteItem);
        });
        invGoodIssueNote.setListInvGoodIssueNoteItem(orderItems);
        return invGoodIssueNote;
    }
    /**
     * 复制
     */
    @Override
    public InvGoodIssueNote getCopy(Long sid){
        InvGoodIssueNote invGoodIssueNote = selectInvGoodIssueNoteById(sid);
        invGoodIssueNote.setNoteSid(null)
                .setNoteCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setAccounter(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvGoodIssueNoteItem> list = invGoodIssueNote.getListInvGoodIssueNoteItem();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setNoteSid(null)
                        .setNoteItemSid(null)
                        .setCreateDate(null)
                        .setCreatorAccount(null);
            });
        }
        return invGoodIssueNote;
    }

    /**
     * 查询发货单列表
     *
     * @param invGoodIssueNote 发货单
     * @return 发货单
     */
    @Override
    public List<InvGoodIssueNote> selectInvGoodIssueNoteList(InvGoodIssueNote invGoodIssueNote) {
        List<InvGoodIssueNote> list = invGoodIssueNoteMapper.selectInvGoodIssueNoteList(invGoodIssueNote);
        return list;
    }

    /**
     * 查询发货单列表
     *
     * @param invGoodIssueNote 发货单
     * @return 发货单
     */
    @Override
    public List<InvIssueNoteReportResponse> report(InvIssueNoteReportRequest request) {
        List<InvIssueNoteReportResponse> list = invGoodIssueNoteItemMapper.reportInvGoodIssueNote(request);
        return list;
    }

    /**
     * 获取仓库编码
     *
     * @param storehouseSid 仓库sid
     * @return 编码
     */
    private String setStorehouseCode(Long storehouseSid){
        if (storehouseSid != null) {
            BasStorehouse storehouse = basStorehouseMapper.selectById(storehouseSid);
            if (storehouse != null) {
                return storehouse.getStorehouseCode();
            }
        }
        return null;
    }

    /**
     * 获取库位编码
     *
     * @param storehouseLocationSid 库位sid
     * @return 编码
     */
    private String setStorehouseLocationCode(Long storehouseLocationSid){
        if (storehouseLocationSid != null) {
            BasStorehouseLocation storehouseLocation = basStorehouseLocationMapper.selectById(storehouseLocationSid);
            if (storehouseLocation != null) {
                return storehouseLocation.getLocationCode();
            }
        }
        return null;
    }

    /**
     * 新增发货单
     * 需要注意编码重复校验
     *
     * @param invGoodIssueNote 发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvGoodIssueNote(InvGoodIssueNote invGoodIssueNote) {
        if(ConstantsEms.CHECK_STATUS.equals(invGoodIssueNote.getHandleStatus())){
            judgeNull(invGoodIssueNote);
        }
        int row = invGoodIssueNoteMapper.insert(invGoodIssueNote);
        if (row > 0) {
            //插入明细
            List<InvGoodIssueNoteItem> listInvGoodIssueNoteItem = invGoodIssueNote.getListInvGoodIssueNoteItem();
            List<InvGoodIssueNoteAttachment> attachmentList = invGoodIssueNote.getAttachmentList();
            //行号赋值
            setItemNum(listInvGoodIssueNoteItem);
            createItem(invGoodIssueNote, listInvGoodIssueNoteItem);
            createAttach(invGoodIssueNote,attachmentList);
        }
        //待办通知
        InvGoodIssueNote note = invGoodIssueNoteMapper.selectById(invGoodIssueNote.getNoteSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(invGoodIssueNote.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(invGoodIssueNote.getNoteSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("发货单" + note.getNoteCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(note.getNoteCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invGoodIssueNote);
        }
        //插入日志
        MongodbUtil.insertUserLog(invGoodIssueNote.getNoteSid(), BusinessType.INSERT.getValue(), TITLE);
        return row;
    }

    //明细报表释放预留库存
    @Override
    public int reportFreeInv(List<Long> sids){
        int row = invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                .in(InvReserveInventory::getBusinessOrderItemSid, sids)
        );
        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                .in(InvGoodIssueNoteItem::getNoteItemSid,sids)
                .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
        );
        return row;
    }
    //明细报表生成库存预留
    @Override
    public int create(List<Long> sids){
        InvIssueNoteReportRequest request = new InvIssueNoteReportRequest();
        request.setItemSidList(sids);
        invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                .in(InvReserveInventory::getBusinessOrderItemSid,sids)
        );
        List<InvIssueNoteReportResponse> list = invGoodIssueNoteItemMapper.reportInvGoodIssueNote(request);
        if(CollectionUtil.isNotEmpty(list)){
            List<InvGoodIssueNoteItem> invMaterialRequisitionItems = BeanCopyUtils.copyListProperties(list, InvGoodIssueNoteItem::new);
            Map<Long, List<InvGoodIssueNoteItem>> listMap = invMaterialRequisitionItems.stream().collect(Collectors.groupingBy(v -> v.getNoteSid()));
            listMap.keySet().forEach(l->{
                List<InvGoodIssueNoteItem> items = listMap.get(l);
                createInv(items);
            });
        }
        return 1;
    }
    //生成预留库存
    public  void createInv(List<InvGoodIssueNoteItem> itemList){
        //改变预留状态
        Map<Long, List<InvGoodIssueNoteItem>> listMap = itemList.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
        listMap.keySet().stream().forEach(l->{
            List<InvGoodIssueNoteItem> items = listMap.get(l);
            if(items.size()==1){
                //商品条码不重复情况下
                items.forEach(m->{
                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                    BeanCopyUtils.copyProperties(m,invInventoryLocation);
                    invInventoryLocation.setSpecialStock(ConstantsEms.VEN_RA.equals(invInventoryLocation.getSpecialStock())?null:invInventoryLocation.getSpecialStock());
                    String specialStock = invInventoryLocation.getSpecialStock();
                    InvInventoryLocation location=null;
                    if(specialStock==null){
                        location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                    }else if(ConstantsEms.VEN_CU.equals(specialStock) || ConstantsEms.VEN_RA.equals(specialStock)){
                        location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                    }else{
                        location = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                    }
                    if(location==null){
                        location=new InvInventoryLocation();
                        location.setAbleQuantity(BigDecimal.ZERO);
                    }
                    if(m.getQuantity().compareTo(location.getAbleQuantity())!=1){
                        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                .eq(InvGoodIssueNoteItem::getNoteItemSid,m.getNoteItemSid())
                                .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                        );
                    }else if(m.getQuantity().compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1){
                        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                .eq(InvGoodIssueNoteItem::getNoteItemSid,m.getNoteItemSid())
                                .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                        );
                        m.setQuantity(location.getAbleQuantity());
                    }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1){
                        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                .eq(InvGoodIssueNoteItem::getNoteItemSid,m.getNoteItemSid())
                                .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                        );
                        m.setQuantity(BigDecimal.ZERO);
                    }
                });
            }else{
                BigDecimal sum = items.stream().map(h -> h.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                InvGoodIssueNoteItem noteSignle = items.get(0);
                InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                BeanCopyUtils.copyProperties(noteSignle,invInventoryLocation);
                invInventoryLocation.setSpecialStock(ConstantsEms.VEN_RA.equals(invInventoryLocation.getSpecialStock())?null:invInventoryLocation.getSpecialStock());
                String specialStock = invInventoryLocation.getSpecialStock();
                InvInventoryLocation location=null;
                if(specialStock==null){
                    location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                }else if(ConstantsEms.VEN_CU.equals(specialStock) || ConstantsEms.VEN_RA.equals(specialStock)){
                    location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                }else{
                    location = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                }
                if(location==null){
                    location=new InvInventoryLocation();
                    location.setAbleQuantity(BigDecimal.ZERO);
                }
                if(sum.compareTo(location.getAbleQuantity())==1){
                    BigDecimal comsum=BigDecimal.ZERO;
                    for (int j = 0; j < items.size(); j++) {
                        comsum=items.get(j).getQuantity().add(comsum);
                        if(comsum.compareTo(location.getAbleQuantity())!=1){
                            invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                    .eq(InvGoodIssueNoteItem::getNoteItemSid,items.get(j).getNoteItemSid())
                                    .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                            );
                        }else if(comsum.compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1&&location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getQuantity()))).compareTo(BigDecimal.ZERO)==1){
                            invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                    .eq(InvGoodIssueNoteItem::getNoteItemSid,items.get(j).getNoteItemSid())
                                    .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                            );
                            items.get(j).setQuantity(location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getQuantity()))));

                        }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1||location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getQuantity()))).compareTo(BigDecimal.ZERO)!=1){
                            invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                    .eq(InvGoodIssueNoteItem::getNoteItemSid,items.get(j).getNoteItemSid())
                                    .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                            );
                            items.get(j).setQuantity(BigDecimal.ZERO);
                        }
                    }
                }else{
                    //全部预留
                    items.forEach(h->{
                        invGoodIssueNoteItemMapper.update(new InvGoodIssueNoteItem(),new UpdateWrapper<InvGoodIssueNoteItem>().lambda()
                                .eq(InvGoodIssueNoteItem::getNoteItemSid,h.getNoteItemSid())
                                .set(InvGoodIssueNoteItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                        );
                    });
                }
            }
        });
        List<InvReserveInventory> invReserveInventories = new ArrayList<>();
        itemList.forEach(li->{
            InvReserveInventory invReserveInventory = new InvReserveInventory();
            invReserveInventory.setBarcodeSid(li.getBarcodeSid())
                    .setGoodIssueNoteCode(Long.valueOf(li.getNoteCode()))
                    .setGoodIssueNoteItemNum(Long.valueOf(li.getItemNum()))
                    .setGoodIssueNoteItemSid(li.getNoteItemSid())
                    .setGoodIssueNoteSid(li.getNoteSid())
                    .setBusinessOrderCode(Long.valueOf(li.getNoteCode()))
                    .setBusinessOrderItemNum(Long.valueOf(li.getItemNum()))
                    .setBusinessOrderSid(li.getNoteSid())
                    .setBusinessOrderItemSid(li.getNoteItemSid())
                    .setMaterialSid(li.getMaterialSid())
                    .setReserveType("FHD")
                    .setSku1Sid(li.getSku1Sid())
                    .setSku2Sid(li.getSku2Sid())
                    .setStorehouseSid(li.getStorehouseSid())
                    .setSpecialStock(ConstantsEms.VEN_RA.equals(li.getSpecialStock())?null:li.getSpecialStock())
                    .setCustomerSid(ConstantsEms.VEN_RA.equals(li.getSpecialStock())?null:li.getCustomerSid())
                    .setVendorSid(ConstantsEms.VEN_RA.equals(li.getSpecialStock())?null:li.getVendorSid())
                    .setStorehouseLocationSid(li.getStorehouseLocationSid())
                    .setQuantity(li.getQuantity());
            invReserveInventories.add(invReserveInventory);
        });
        //生成库存预留
        invReserveInventoryMapper.inserts(invReserveInventories);
    }
    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(InvGoodIssueNote invGoodIssueNote) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invGoodIssueNote.getNoteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invGoodIssueNote.getNoteSid()));
        }
    }

    public void judgeNull(InvGoodIssueNote note){
        List<InvGoodIssueNoteItem> list = note.getListInvGoodIssueNoteItem();
        if(CollectionUtils.isEmpty(list)){
            throw  new CustomException("确认时，明细行不允许为空");
        }
        list.forEach(item->{
            if(item.getQuantity()==null){
                throw  new CustomException("确认时，明细行的数量不允许为空");
            }
        });
    }

    public void createItem(InvGoodIssueNote invGoodIssueNote, List<InvGoodIssueNoteItem> listInvGoodIssueNoteItem) {
        if (CollectionUtils.isNotEmpty(listInvGoodIssueNoteItem)) {
            listInvGoodIssueNoteItem.forEach(o -> {
                // 设置仓库库位CODE
                o.setStorehouseCode(setStorehouseCode(o.getStorehouseSid()));
                o.setStorehouseLocationCode(setStorehouseLocationCode(o.getStorehouseLocationSid()));
                o.setClientId(ApiThreadLocalUtil.get().getClientId());
                o.setCreateDate(new Date());
                o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                o.setInOutStockStatus("WCK");
                o.setNoteSid(invGoodIssueNote.getNoteSid());
                o.setNoteItemSid(IdWorker.getId());
            });
            invGoodIssueNoteItemMapper.inserts(listInvGoodIssueNoteItem);
        }
    }

    public void createAttach(InvGoodIssueNote invGoodIssueNote, List<InvGoodIssueNoteAttachment> listInvGoodIssueNoteAttachment) {
        if (CollectionUtils.isNotEmpty(listInvGoodIssueNoteAttachment)) {
            listInvGoodIssueNoteAttachment.forEach(o -> {
                o.setNoteSid(invGoodIssueNote.getNoteSid());
                attachmentMapper.insert(o);
            });
//            attachmentMapper.inserts(listInvGoodIssueNoteAttachment);
        }
    }

    /**
     * 行号赋值
     */
    public void setItemNum(List<InvGoodIssueNoteItem> list) {
        int size = list.size();
        if (size > 0) {
            for (int i = 1; i <= size; i++) {
                list.get(i - 1).setItemNum(i);
            }
        }
    }
    /**
     * 关闭
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int close(List<Long> sidList){
        int row = invGoodIssueNoteMapper.update(new InvGoodIssueNote(), new UpdateWrapper<InvGoodIssueNote>().lambda()
                .in(InvGoodIssueNote::getNoteSid, sidList)
                .set(InvGoodIssueNote::getHandleStatus, HandleStatus.CLOSED.getCode())
        );
        return row;
    }
    /**
     * 修改发货单
     *
     * @param invGoodIssueNote 发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvGoodIssueNote(InvGoodIssueNote invGoodIssueNote) {
        if(ConstantsEms.CHECK_STATUS.equals(invGoodIssueNote.getHandleStatus())){
            judgeNull(invGoodIssueNote);
        }
        InvGoodIssueNote response = invGoodIssueNoteMapper.selectInvGoodIssueNoteById(invGoodIssueNote.getNoteSid());
        int row = invGoodIssueNoteMapper.updateById(invGoodIssueNote);
        List<InvGoodIssueNoteItem> listInvGoodIssueNoteItem = invGoodIssueNote.getListInvGoodIssueNoteItem();
        List<InvGoodIssueNoteAttachment> attachmentList = invGoodIssueNote.getAttachmentList();
        //删除原有附件
        attachmentMapper.delete(new QueryWrapper<InvGoodIssueNoteAttachment>().lambda()
                .eq(InvGoodIssueNoteAttachment::getNoteSid, invGoodIssueNote.getNoteSid()));
        //插入现有附件
        createAttach(invGoodIssueNote, attachmentList);
        if (CollectionUtils.isNotEmpty(listInvGoodIssueNoteItem)) {
            setItemNum(listInvGoodIssueNoteItem);
            List<InvGoodIssueNoteItem> invGoodReceiptNoteItems = invGoodIssueNoteItemMapper.selectList(new QueryWrapper<InvGoodIssueNoteItem>().lambda()
                    .eq(InvGoodIssueNoteItem::getNoteSid, invGoodIssueNote.getNoteSid())
            );
            if(CollectionUtil.isEmpty(invGoodReceiptNoteItems)){
                invGoodReceiptNoteItems=new ArrayList<InvGoodIssueNoteItem>();
            }
            List<Long> longs = invGoodReceiptNoteItems.stream().map(li -> li.getNoteItemSid()).collect(Collectors.toList());
            List<Long> longsNow = listInvGoodIssueNoteItem.stream().map(li -> li.getNoteItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<InvGoodIssueNoteItem> reduceList = invGoodIssueNoteItemMapper.selectList(new QueryWrapper<InvGoodIssueNoteItem>().lambda()
                        .in(InvGoodIssueNoteItem::getNoteItemSid, reduce)
                );
                invGoodIssueNoteItemMapper.deleteBatchIds(reduce);
            }
            //修改明细
            List<InvGoodIssueNoteItem> exitItem = listInvGoodIssueNoteItem.stream().filter(li -> li.getNoteItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    // 设置仓库库位CODE
                    li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                    li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                    invGoodIssueNoteItemMapper.updateById(li);
                });
            }
            //新增明细
            List<InvGoodIssueNoteItem> nullItem = listInvGoodIssueNoteItem.stream().filter(li -> li.getNoteItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                nullItem.forEach(li->{
                    // 设置仓库库位CODE
                    li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                    li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                    li.setNoteSid(invGoodIssueNote.getNoteSid());
                    li.setInOutStockStatus("WCK");
                    invGoodIssueNoteItemMapper.insert(li);
                });
            }
        }else{
            invGoodIssueNoteItemMapper.delete(new QueryWrapper<InvGoodIssueNoteItem>().lambda()
            .eq(InvGoodIssueNoteItem::getNoteSid,invGoodIssueNote.getNoteSid())
            );
        }
        if (row > 0) {
            String businessType=invGoodIssueNoteMapper.selectById(invGoodIssueNote.getNoteSid()).getHandleStatus().equals(ConstantsEms.CHECK_STATUS)?"变更":"编辑";
            MongodbUtil.insertUserLog(invGoodIssueNote.getNoteSid(),businessType,TITLE);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(invGoodIssueNote.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(invGoodIssueNote);
        }
        return row;
    }

    /**
     * 变更发货单
     * @param invGoodIssueNote 发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvGoodIssueNote(InvGoodIssueNote invGoodIssueNote) {
        InvGoodIssueNote response = invGoodIssueNoteMapper.selectInvGoodIssueNoteById(invGoodIssueNote.getNoteSid());
        int row = invGoodIssueNoteMapper.updateAllById(invGoodIssueNote);
        if (row > 0) {
            //插入日志
            String businessType=invGoodIssueNoteMapper.selectById(invGoodIssueNote.getNoteSid()).getHandleStatus().equals(ConstantsEms.CHECK_STATUS)?"变更":"编辑";
            MongodbUtil.insertUserLog(invGoodIssueNote.getNoteSid(),businessType,TITLE);
        }
        return row;
    }

    /**
     * 批量删除发货单
     *
     * @param goodIssueNoteSids 需要删除的发货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvGoodIssueNoteByIds(List<Long> goodIssueNoteSids) {
        int row = invGoodIssueNoteMapper.deleteBatchIds(goodIssueNoteSids);
        if(row>0){
            invGoodIssueNoteItemMapper.delete(new QueryWrapper<InvGoodIssueNoteItem>().lambda()
                    .in(InvGoodIssueNoteItem::getNoteSid,goodIssueNoteSids));
            for (int i=0;i<goodIssueNoteSids.size();i++){
                InvGoodIssueNote issueNote = new InvGoodIssueNote();
                issueNote.setNoteSid(goodIssueNoteSids.get(i));
                //校验是否存在待办
                checkTodoExist(issueNote);
            }
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param invGoodIssueNote
     * @return
     */
    @Override
    public int changeStatus(InvGoodIssueNote invGoodIssueNote) {
        int row = 0;
        Long[] sids = invGoodIssueNote.getGoodIssueNoteSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                invGoodIssueNote.setNoteSid(id);
                InvGoodIssueNote note = selectInvGoodIssueNoteById(id);
                judgeNull(note);
                row = invGoodIssueNoteMapper.updateById(invGoodIssueNote);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                MongodbUtil.insertUserLog(invGoodIssueNote.getNoteSid(), BusinessType.CHECK.getValue(), TITLE);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param invGoodIssueNote
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(InvGoodIssueNote invGoodIssueNote) {
        int row = 0;
        Long[] sids = invGoodIssueNote.getGoodIssueNoteSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                invGoodIssueNote.setNoteSid(id);
                InvGoodIssueNote note = selectInvGoodIssueNoteById(id);
                judgeNull(note);
                List<InvGoodIssueNoteItem> listInvGoodIssueNoteItem = note.getListInvGoodIssueNoteItem();
                listInvGoodIssueNoteItem.forEach(li->{
                    li.setSpecialStock(note.getSpecialStock())
                            .setVendorSid(note.getVendorSid())
                            .setCustomerSid(note.getCustomerSid())
                            .setNoteCode(note.getNoteCode())
                            .setStorehouseLocationSid(note.getStorehouseLocationSid())
                            .setStorehouseSid(note.getStorehouseSid());
                });
                //预留库存
                createInv(listInvGoodIssueNoteItem);
                row = invGoodIssueNoteMapper.updateById(invGoodIssueNote);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
//                List<OperMsg> msgList = new ArrayList<>();
//                MongodbUtil.insertUserLog(invGoodIssueNote.getNoteSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        for (int i=0;i<sids.length;i++){
            InvGoodIssueNote issueNote = new InvGoodIssueNote();
            issueNote.setNoteSid(sids[i]);
            //校验是否存在待办
            checkTodoExist(issueNote);
        }
        return row;
    }

    /**
     * 提交时校验
     */
    @Override
    public OrderErrRequest processCheck(OrderErrRequest request){
        List<Long> sidList = request.getSidList();
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        sidList.stream().forEach(id->{
            InvGoodIssueNote note = selectInvGoodIssueNoteById(id);
            List<InvGoodIssueNoteItem> list = note.getListInvGoodIssueNoteItem();
            if(CollectionUtils.isEmpty(list)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不允许为空");
                msgList.add(errMsgResponse);
            }else{
                List<InvGoodIssueNoteItem> noteItems = list.stream().filter(item -> item.getQuantity() == null).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(noteItems)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setMsg("明细行的数量不允许为空");
                    msgList.add(errMsgResponse);
                }else{
                    Map<Long, List<InvGoodIssueNoteItem>> listMap = list.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
                    listMap.keySet().stream().forEach(l->{
                        List<InvGoodIssueNoteItem> items = listMap.get(l);
                        if(items.size()==1){
                            //商品条码不重复情况下
                            list.forEach(m->{
                                InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                invInventoryLocation.setBarcodeSid(m.getBarcodeSid())
                                        .setStorehouseSid(note.getStorehouseSid())
                                        .setSpecialStock(ConstantsEms.VEN_RA.equals(note.getSpecialStock())?null:note.getSpecialStock())
                                        .setCustomerSid(note.getCustomerSid())
                                        .setVendorSid(note.getVendorSid())
                                        .setStorehouseLocationSid(note.getStorehouseLocationSid());
                                String specialStock = invInventoryLocation.getSpecialStock();
                                InvInventoryLocation locationAble=null;
                                if(specialStock==null){
                                    locationAble = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                                }else if(ConstantsEms.VEN_CU.equals(specialStock) || ConstantsEms.VEN_RA.equals(specialStock)){
                                    locationAble = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                                }else{
                                    locationAble = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                                }
                                if(locationAble==null){
                                    locationAble=new InvInventoryLocation();
                                    locationAble.setAbleQuantity(BigDecimal.ZERO);
                                }
                                if(m.getQuantity().compareTo(locationAble.getAbleQuantity())==1){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setMsg("行号为"+m.getItemNum()+"的明细可用库存不足，无法提交");
                                    msgList.add(errMsgResponse);
                                }
                            });
                        }else{
                            BigDecimal sum = items.stream().map(h -> h.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                            InvGoodIssueNoteItem noteItem = items.get(0);
                            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                            invInventoryLocation.setBarcodeSid(noteItem.getBarcodeSid())
                                    .setStorehouseSid(note.getStorehouseSid())
                                    .setSpecialStock(ConstantsEms.VEN_RA.equals(note.getSpecialStock())?null:note.getSpecialStock())
                                    .setCustomerSid(note.getCustomerSid())
                                    .setVendorSid(note.getVendorSid())
                                    .setStorehouseLocationSid(note.getStorehouseLocationSid());
                            InvInventoryLocation location=null;
                            String specialStock = invInventoryLocation.getSpecialStock();
                            if(specialStock==null){
                                location = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                            }else if(ConstantsEms.VEN_CU.equals(specialStock) || ConstantsEms.VEN_RA.equals(specialStock)){
                                location = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                            }else{
                                location = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                            }
                            if(location==null){
                                location=new InvInventoryLocation();
                                location.setAbleQuantity(BigDecimal.ZERO);
                            }
                            if(sum.compareTo(location.getAbleQuantity())==1){
                                BigDecimal comsum=BigDecimal.ZERO;
                                for (int i = 0; i < items.size(); i++) {
                                    comsum=items.get(i).getQuantity().add(comsum);
                                    if(comsum.compareTo(location.getAbleQuantity())==1){
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setMsg("行号为"+items.get(i).getItemNum()+"的明细可用库存不足，无法提交");
                                        msgList.add(errMsgResponse);
                                    }
                                }
                            }
                        }
                    });
                }
                if (!ConstantsEms.YES.equals(request.getIsStatus())) {
                    HashMap<Long, Integer> BarcodeHashMap = new HashMap<>();
                    list.forEach(li -> {
                        BarcodeHashMap.put(li.getBarcodeSid(), li.getItemNum());
                    });
                    Set<Long> sids = BarcodeHashMap.keySet();
                    List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .in(BasMaterialBarcode::getBarcodeSid, sids)
                            .eq(BasMaterialBarcode::getStatus, ConstantsEms.DISENABLE_STATUS)
                    );
                    if (CollectionUtils.isNotEmpty(basMaterialBarcodes)) {
                        basMaterialBarcodes.forEach(li->{
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setMsg("行号为"+BarcodeHashMap.get(li.getBarcodeSid())+"的商品条码已停用");
                            msgList.add(errMsgResponse);
                        });
                        request.setIsStatus(ConstantsEms.YES);
                    }
                }else{
                    request.setIsStatus(null);
                }
            }
        });
        request.setMsgList(msgList);
        return request;
    }

    /**
     * 导入发货单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        Long basStorehouseSid=null;
        String basStorehouseCode=null;
        Long StorehouseLocationSid=null;
        String storehouseLocationCode=null;
        Long vendorSid=null;
        Long customerSid=null;
        Date accountDate=null;
        BigDecimal quantity=null;
        List<Long> bardCodeList = new ArrayList<>();
        Boolean matchOne=false;
        Boolean matchtwo=false;
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
            Map<String,String> specialMaps = conSpecialStockMapper.getList().stream().collect(Collectors.toMap(ConSpecialStock::getName, ConSpecialStock::getCode, (key1, key2) -> key2));
            InvGoodIssueNote note = new InvGoodIssueNote();
            List<InvGoodIssueNoteItem> invGoodIssueNoteItemList = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Name=null;
                String sku2Name=null;
                String sku2Code=null;
                String materialName=null;
                String specialStock=null;
                String movementType=null;
                String unitBase=null;
                BasMaterialBarcode basMaterialBarcode=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                      //  throw new BaseException("作业类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                                .eq(ConMovementType::getName, objects.get(0).toString())
                        );
                        if(conMovementType==null){
                            //throw new BaseException("作业类型，配置错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.ENABLE_STATUS.equals(conMovementType.getStatus())||!ConstantsEms.CHECK_STATUS.equals(conMovementType.getHandleStatus())){
                                //throw new BaseException("作业类型，必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型，必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                if(!iSSue(conMovementType.getCode())){
                                    // throw new BaseException("该作业类型，不属于收货单，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("该作业类型，不属于发货单，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    matchOne=true;
                                    movementType=conMovementType.getCode();
                                }
                            }
                        }

                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                       // throw new BaseException("单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("开单日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(1).toString());
                        if(!validDate){
                          //  throw new BaseException("单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("开单日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String account = objects.get(1).toString();
                             accountDate = DateUtil.parse(account);
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        //throw new BaseException("仓库编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(2).toString())
                        );
                        if (basStorehouse == null) {
                            //throw new BaseException("没有编码为" + objects.get(2).toString() + "的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(2).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                              //  throw new BaseException("仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                            basStorehouseCode = basStorehouse.getStorehouseCode();
                        }
                    }
                    if(objects.get(3) == null || objects.get(3) == ""){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("库位，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(3) != null && objects.get(3) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(3).toString())
                        );
                        if (basStorehouseLocation == null) {
                           // throw new BaseException("编码为" + objects.get(2).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(2).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                               // throw new BaseException("库位必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            StorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                            storehouseLocationCode = basStorehouseLocation.getLocationCode();
                        }
                    }
                    if(objects.get(4) != null && objects.get(4) != ""){
                        String sep = objects.get(4).toString();
                        if(objects.get(4) != null && objects.get(4) != ""){
                            String special = specialMaps.get(objects.get(4).toString());
                            if(special==null){
                              // throw new BaseException("特殊库存数据格式错误，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("特殊库存，配置错误，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                ConSpecialStock conSpecialStock = conSpecialStockMapper.selectOne(new QueryWrapper<ConSpecialStock>().lambda()
                                        .eq(ConSpecialStock::getName, sep)
                                        .eq(ConSpecialStock::getStatus, ConstantsEms.ENABLE_STATUS)
                                        .eq(ConSpecialStock::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                );
                                if(conSpecialStock==null){
                                    // throw new BaseException("特殊库存，必须是确认且已启用状态，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("特殊库存，必须是确认且已启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                specialStock=special;
                            }
                        }
                    }
                    if(matchOne){
                        //收货-常规物料(免费)
                        if("SC09".equals(movementType)){
                            if(specialStock!=null){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SC093".equals(movementType)){
                            if(!ConstantsEms.CUS_RA.equals(specialStock)){
                                //throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SC094".equals(movementType)){
                            if(!ConstantsEms.VEN_CU.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SC091".equals(movementType)){
                            if(!ConstantsEms.VEN_RA.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为甲供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为甲供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR092".equals(movementType)){
                            if(!ConstantsEms.CUS_VE.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客户寄售，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客户寄售，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }
                    }
                    if (objects.get(5) != "" &&objects.get(5) != null) {
                        String vendorCode = objects.get(5).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorCode)
                        );
                        if (basVendor == null) {
                          //  throw new BaseException("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                                //throw new BaseException("供应商必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            vendorSid = Long.valueOf(basVendor.getVendorSid());
                        }
                    }
                    if (objects.get(6) != ""&&objects.get(6) != null) {
                        String customerCode = objects.get(6).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerCode));
                        if (basCustomer == null) {
                          //  throw new BaseException("简称为" + customerCode + "没有对应的客户，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + customerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())){
                                //throw new BaseException("客户必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }
                    if(matchtwo){
                        if(objects.get(4)!=null&&objects.get(4)!=""){
                            if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(4).toString()))){
                                if(objects.get(5)==null||objects.get(5)==""){
                                    // throw new BaseException("供应商简称不能为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                if(objects.get(6)!=null&&objects.get(6)!=""){
                                    //throw new BaseException("客户简称必须为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(4).toString()))){
                                if(objects.get(6)==null||objects.get(6)==""){
                                    //throw new BaseException("客户简称不能为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                if(objects.get(5)!=null&&objects.get(5)!=""){
                                    // throw new BaseException("供应商简称必须为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                        if(objects.get(4)==null ||objects.get(4)==""){
                            if(objects.get(5)!=null&&objects.get(5)!=""){
                                // throw new BaseException("供应商简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(6)!=null&&objects.get(6)!=""){
                                //throw new BaseException("客户简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    note
                            .setMovementType(movementType)
                            .setStorehouseSid(basStorehouseSid)
                            .setInOutStockStatus("WCK")
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setDocumentDate(accountDate)
                            .setCustomerSid(customerSid)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSpecialStock((objects.get(4)==""||objects.get(4)==null)?null:specialMaps.get(objects.get(4).toString()))
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(7)==""||objects.get(7)==null)?null:objects.get(7).toString());
                    continue;
                }
                int num=i+1;
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                  //  throw new BaseException("物料/商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial==null) {
                        //throw new BaseException("没有编码为"+objects.get(0).toString()+"的物料/商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为"+objects.get(0).toString()+"的物料/商品，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())){
                            // throw new BaseException("物料/商品必须是确认且已启用状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料/商品必须是确认且已启用状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        materialName=basMaterial.getMaterialName();
                        materialSid=basMaterial.getMaterialSid();
                        unitBase = basMaterial.getUnitBase();
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                   // throw new BaseException("SKU1名称不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("SKU1名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(1).toString())
                    );
                    if (basSku==null) {
                      //  throw new BaseException("没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus())){
                            //throw new BaseException("SKU1名称必须是确认且已启用状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU1名称必须是确认且已启用状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        sku1Name=basSku.getSkuName();
                        sku1Sid=basSku.getSkuSid();
                    }
                }
                if (objects.get(2) != null && objects.get(2) != "") {
                    BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(2).toString())
                    );
                    if (basSku2==null) {
                        //throw new BaseException("没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(basSku2.getSkuType().equals(ConstantsEms.SKUTYP_YS)){
                           // throw new BaseException("SKU2名称不能是颜色类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU2名称不能是颜色类型，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku2.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku2.getStatus())){
                            //throw new BaseException("SKU2名称必须是确认且已启用状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU2名称必须是确认且已启用状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        sku2Code=basSku2.getSkuCode();
                        sku2Name=basSku2.getSkuName();
                        sku2Sid=basSku2.getSkuSid();
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                   // throw new BaseException("第"+num+"行,数量 不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("数量，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validInt = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validInt){
                        // throw new BaseException("第"+num+"行,数量格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("数量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        quantity=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                        Double mount = Double.valueOf(objects.get(3).toString());
                        if(mount<=0){
                            // throw new BaseException("第"+num+"行的数量小于0，不允许导入，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("数量小于等于0，不允许导入，导入失败");
                            msgList.add(errMsgResponse);
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
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .isNull(BasMaterialBarcode::getSku2Sid)
                    );
                }
                if(basMaterialBarcode==null){
                    //throw new BaseException("第"+num+"行没有对应的商品条码存在，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("没有对应的商品条码存在，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())){
                       // throw new BaseException("第"+num+"行对应的商品条码必须已启用的状态，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("对应的商品条码必须是已启用的状态，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    barcodeSid=basMaterialBarcode.getBarcodeSid();
                    barcode=basMaterialBarcode.getBarcode();
                    bardCodeList.add(barcodeSid);
                }
                InvGoodIssueNoteItem invGoodIssueNoteItem = new InvGoodIssueNoteItem();
                invGoodIssueNoteItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code((objects.get(1)==""||objects.get(1)==null)?null:objects.get(1).toString())
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setBarcode(barcode==null?null:Long.valueOf(barcode))
                        .setMaterialSid(materialSid)
                        .setMaterialCode((objects.get(0)==""||objects.get(0)==null)?null:objects.get(0).toString())
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseCode(basStorehouseCode)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setStorehouseLocationCode(storehouseLocationCode)
                        .setQuantity(quantity)
                        .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                invGoodIssueNoteItemList.add(invGoodIssueNoteItem);
            }
            HashSet<Long> longs = new HashSet<>(bardCodeList);
            if(longs.size()!=bardCodeList.size()){
                for (int i=0;i<bardCodeList.size();i++){
                    for (int j=i+1;j<bardCodeList.size();j++){
                        if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                            int nu=j+1+5;
                           // throw new BaseException("第"+nu+"行，商品条码重复，请核实");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(nu);
                            errMsgResponse.setMsg("商品条码重复，请核实");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            note.setListInvGoodIssueNoteItem(invGoodIssueNoteItemList);
            insertInvGoodIssueNote(note);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        return AjaxResult.success(1);
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
    private void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        for (int i = lineSize; i < size; i++) {
            Object o = null;
            objects.add(o);
        }
    }

    /**
     * 发货单-明细对象
     */
    private void addInvGoodIssueNoteItem(InvGoodIssueNote goodIssueNote, List<InvGoodIssueNoteItem> goodIssueNoteItemList) {
        invGoodIssueNoteItemMapper.delete(new UpdateWrapper<InvGoodIssueNoteItem>()
                .lambda()
                .eq(InvGoodIssueNoteItem::getNoteSid, goodIssueNote.getNoteSid())
        );
        goodIssueNoteItemList.forEach(o -> {
            o.setNoteSid(goodIssueNote.getNoteSid());
            invGoodIssueNoteItemMapper.insert(o);
        });
    }

    public Boolean iSSue(String code){
        List<String> transferList = Arrays.asList("SC09","SC092","SC091","SC093","SC094");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }
}
