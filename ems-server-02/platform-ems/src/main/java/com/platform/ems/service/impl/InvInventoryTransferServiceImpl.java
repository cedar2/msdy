package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvInventoryTransferRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvInventoryTransferResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConInOutStockDocCategory;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.mapper.ConInOutStockDocCategoryMapper;
import com.platform.ems.plug.mapper.ConMovementTypeMapper;
import com.platform.ems.plug.mapper.ConSpecialStockMapper;
import com.platform.ems.service.IInvInventoryTransferService;
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
 * 调拨单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-04
 */
@Service
@SuppressWarnings("all")
public class InvInventoryTransferServiceImpl extends ServiceImpl<InvInventoryTransferMapper,InvInventoryTransfer>  implements IInvInventoryTransferService {
    @Autowired
    private InvInventoryTransferMapper invInventoryTransferMapper;
    @Autowired
    private InvInventoryTransferItemMapper invInventoryTransferItemMapper;
    @Autowired
    private  InvInventoryTransferAttachmentMapper attachmentMapper;
    @Autowired
    private InvIntransitInventoryServiceImpl invIntransitInventoryServiceImpl;
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
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private InvCusSpecialInventoryMapper invCusSpecialInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private InvReserveInventoryMapper invReserveInventoryMapper;
    @Autowired
    private  InvInventoryTransferMaterialProductMapper invInventoryTransferMaterialProductMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    private static final String TITLE = "调拨单";
    /**
     * 查询调拨单
     *
     * @param inventoryTransferSid 调拨单ID
     * @return 调拨单
     */
    @Override
    public InvInventoryTransfer selectInvInventoryTransferById(Long inventoryTransferSid) {
        InvInventoryTransfer invInventoryTransfer = invInventoryTransferMapper.selectInvInventoryTransferById(inventoryTransferSid);
        List<InvInventoryTransferItem> invInventoryTransferItems = invInventoryTransferItemMapper.selectInvInventoryTransferItemById(inventoryTransferSid);
        if(CollectionUtil.isNotEmpty(invInventoryTransferItems)){
            invInventoryTransferItems.forEach(o->{
                List<InvInventoryTransferMaterialProduct> materialProductList = invInventoryTransferMaterialProductMapper.selectInvInventoryTransferMaterialProductById(o.getInventoryTransferItemSid());
                o.setMaterialProductList(materialProductList);
            });
        }
        List<InvInventoryTransferAttachment> invInventoryTransferAttachments = attachmentMapper.selectInvInventoryTransferAttachmentById(inventoryTransferSid);
        List<InvInventoryTransferItem> items = sort(invInventoryTransferItems, null);
        invInventoryTransfer.setListInvInventoryTransfer(items);
        invInventoryTransfer.setAttachList(invInventoryTransferAttachments);
        MongodbUtil.find(invInventoryTransfer);
        return  invInventoryTransfer;
    }

    @Override
    public List<InvInventoryTransferItem> sort(List<InvInventoryTransferItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvInventoryTransferItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                    List<InvInventoryTransferItem> allList = new ArrayList<>();
                    List<InvInventoryTransferItem> allThirdList = new ArrayList<>();
                    List<InvInventoryTransferItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvInventoryTransferItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvInventoryTransferItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvInventoryTransferItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvInventoryTransferItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvInventoryTransferItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvInventoryTransferItem::getMaterialCode)
                        .thenComparing(InvInventoryTransferItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvInventoryTransferItem::getMaterialCode)
                        .thenComparing(InvInventoryTransferItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return items;
        }
        return new ArrayList<>();
    }
    /**
     * 复制
     */
    @Override
    public InvInventoryTransfer getCopy(Long sid){
        InvInventoryTransfer invInventoryTransfer = selectInvInventoryTransferById(sid);
        invInventoryTransfer.setInventoryTransferSid(null)
                .setInventoryTransferCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvInventoryTransferItem> list = invInventoryTransfer.getListInvInventoryTransfer();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setInventoryTransferItemSid(null)
                        .setInventoryTransferSid(null)
                        .setCreateDate(null)
                        .setCreatorAccount(null);
            });
        }
        return invInventoryTransfer;
    }

    /**
     * 查询调拨单列表
     *
     * @param invInventoryTransfer 调拨单
     * @return 调拨单
     */
    @Override
    public List<InvInventoryTransfer> selectInvInventoryTransferList(InvInventoryTransfer invInventoryTransfer) {
        List<InvInventoryTransfer> list = invInventoryTransferMapper.selectInvInventoryTransferList(invInventoryTransfer);
        return list;
    }

    /**
     * 查询调拨单明细报表
     *
     * @param invInventoryTransfer 调拨单
     * @return 调拨单
     */
    @Override
    public List<InvInventoryTransferResponse> report(InvInventoryTransferRequest request) {
        return invInventoryTransferItemMapper.reportInvInventoryTransfer(request);
    }
    /**
     * 物料需求测算-创建调拨单
     */
    @Override
    public InvInventoryTransfer getGoodIssueNote(List<TecBomItemReport> list){
        InvInventoryTransfer invInventoryTransfer = new InvInventoryTransfer();
        List<InvInventoryTransferItem> invInventoryTransferItems = new ArrayList<>();
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
            InvInventoryTransferItem item = new InvInventoryTransferItem();
            item.setMaterialSid(li.getMaterialSid())
                    .setMaterialCode(li.getMaterialCode())
                    .setMaterialName(li.getMaterialName())
                    .setSku1Name(li.getSku1Name())
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Name(li.getSku2Name())
                    .setBarcode(barcode==null?null:Long.valueOf(barcode))
                    .setBarcodeSid(barcodeSid)
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setQuantity(li.getLossRequireQuantity()!=null?new BigDecimal(li.getLossRequireQuantity()):null)
                    .setUnitBase(li.getUnitBase())
                    .setUnitBaseName(li.getUnitBaseName())
                    .setProductSoCodes(li.getSalesOrderCode()!=null?li.getSalesOrderCode().toString(): li.getSalesOrderCodeRemark())
                    .setProductCodes(li.getSaleMaterialCode()!=null?li.getSaleMaterialCode():li.getMaterialCodeRemark())
                    .setProductSku1Names(li.getSaleSku1Name()!=null?li.getSaleSku1Name():li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getSaleSku2Name()!=null?li.getSaleSku2Name():li.getMaterialSku2Remark())
                    .setProductQuantityRemark(li.getProductQuantity()!=null?li.getProductQuantity().toString():null);
            String productRequestPartys=null;
            String productRequestBusType=null;
            if(li.getCommonItemSidRemark()!=null){
                String itemSid = li.getCommonItemSidRemark();
                String[] sids = itemSid.split(";");
                if(li.getSalesOrderCode()!=null||li.getSalesOrderCodeRemark()!=null){
                    SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
                    salSalesOrderItem.setItemSidList(sids);
                    List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemList(salSalesOrderItem);
                    List<InvInventoryTransferMaterialProduct> purPurchaseOrderMaterialProducts = new ArrayList<>();
                    HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
                    for (SalSalesOrderItem it : itemList) {
                        InvInventoryTransferMaterialProduct materialProduct = new InvInventoryTransferMaterialProduct();
                        materialProduct.setSalesOrderCode(Long.valueOf(it.getSalesOrderCode()))
                                .setSalesOrderItemNum(Long.valueOf(it.getItemNum()))
                                .setSalesOrderItemSid(it.getSalesOrderItemSid())
                                .setProductSid(it.getMaterialSid())
                                .setProductSku1Sid(it.getSku1Sid())
                                .setProductSku2Sid(it.getSku2Sid())
                                .setProductCode(it.getMaterialCode())
                                .setProductName(it.getMaterialName())
                                .setProductSku1Name(it.getSku1Name())
                                .setProductSku2Name(it.getSku2Name())
                                .setSalesOrderSid(it.getSalesOrderSid())
                                .setReferDocCategory("SalesOrder")
                                .setReferDocCategoryName("销售订单")
                                .setReferDocCode(Long.valueOf(it.getSalesOrderCode()))
                                .setReferDocItemNum(Long.valueOf(it.getItemNum()))
                                .setReferDocItemSid(it.getSalesOrderItemSid())
                                .setReferDocSid(it.getSalesOrderSid())
                                .setQuantityProduct(it.getQuantity()!=null?it.getQuantity().divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP):null)
                                .setQuantityMaterial(quantityMap.get(it.getSalesOrderItemSid().toString())!=null?quantityMap.get(it.getSalesOrderItemSid().toString()).divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP):null);
                        purPurchaseOrderMaterialProducts.add(materialProduct);
                        if(productRequestPartys!=null){
                            Boolean match = match(productRequestPartys, it.getCustomerShortName());//重复校验
                            if(!match){
                                productRequestPartys = productRequestPartys + ";" + it.getCustomerShortName();
                            }
                        }else{
                            productRequestPartys=it.getCustomerShortName();
                        }
                        if(productRequestBusType!=null){
                            Boolean match = match(productRequestBusType, it.getBusinessTypeName());//重复校验
                            if(!match){
                                productRequestBusType = productRequestBusType + ";" + it.getBusinessTypeName();
                            }
                        }else{
                            productRequestBusType=it.getBusinessTypeName();
                        }
                    }
                    item.setMaterialProductList(purPurchaseOrderMaterialProducts);
                }
            }
            item.setProductRequestPartys(productRequestPartys)
                    .setProductRequestBusType(productRequestBusType);
            invInventoryTransferItems.add(item);
        });
        invInventoryTransfer.setListInvInventoryTransfer(invInventoryTransferItems);
        invInventoryTransfer.setDocumentDate(new Date());
        return invInventoryTransfer;
    }
    /**
     * 新增调拨单
     * 需要注意编码重复校验
     * @param invInventoryTransfer 调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryTransfer(InvInventoryTransfer invInventoryTransfer) {
        String handleStatus = invInventoryTransfer.getHandleStatus();
        List<InvInventoryTransferItem> listInvInventoryTransfer = invInventoryTransfer.getListInvInventoryTransfer();
        if(handleStatus.equals(ConstantsEms.CHECK_STATUS)){
            if(CollectionUtils.isEmpty(listInvInventoryTransfer)){
                throw new CustomException("确认时，明细行不允许为空");
            }
        }
        invInventoryTransfer.setInStockStatus("WRK")
                .setOutStockStatus("WCK");
        int row= invInventoryTransferMapper.insert(invInventoryTransfer);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventoryTransfer.getInventoryTransferSid(), BusinessType.INSERT.getValue(),TITLE);
        }
        //行号赋值
        setItemNum(listInvInventoryTransfer);
        addInvInventoryTransferItem(invInventoryTransfer,listInvInventoryTransfer);
        addInvInventoryTransferAttach(invInventoryTransfer,invInventoryTransfer.getAttachList());
        //待办通知
        InvInventoryTransfer note = invInventoryTransferMapper.selectById(invInventoryTransfer.getInventoryTransferSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(invInventoryTransfer.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(invInventoryTransfer.getInventoryTransferSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("调拨单" + note.getInventoryTransferCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(note.getInventoryTransferCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invInventoryTransfer);
        }
        return row;
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvInventoryTransferItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(InvInventoryTransfer invInventoryTransfer) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invInventoryTransfer.getInventoryTransferSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invInventoryTransfer.getInventoryTransferSid()));
        }
    }

    public void judgeNull(InvInventoryTransfer note){
        List<InvInventoryTransferItem> list = note.getListInvInventoryTransfer();
        if(CollectionUtils.isEmpty(list)){
            throw  new CustomException("确认时，明细行不允许为空");
        }
        list.forEach(item->{
            if(item.getQuantity()==null){
                throw  new CustomException("确认时，明细行的数量不允许为空");
            }
        });
    }

    private void addInvInventoryTransferItem(InvInventoryTransfer invInventoryTransfer,List<InvInventoryTransferItem> listInvInventoryTransfer){
        invInventoryTransferItemMapper.delete(
                new QueryWrapper<InvInventoryTransferItem>().lambda()
                        .eq(InvInventoryTransferItem::getInventoryTransferSid,invInventoryTransfer.getInventoryTransferSid()));
        invInventoryTransferMaterialProductMapper.delete(new QueryWrapper<InvInventoryTransferMaterialProduct>().lambda()
        .eq(InvInventoryTransferMaterialProduct::getInventoryTransferSid,invInventoryTransfer.getInventoryTransferSid())
        );
        if(CollectionUtils.isNotEmpty(listInvInventoryTransfer)){
            listInvInventoryTransfer.forEach(o->{
                o.setInventoryTransferSid(invInventoryTransfer.getInventoryTransferSid());
                o.setClientId(ApiThreadLocalUtil.get().getClientId());
                o.setCreateDate(new Date());
                o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                List<InvInventoryTransferMaterialProduct> materialProductList = o.getMaterialProductList();
                if(CollectionUtil.isNotEmpty(materialProductList)){
                    BigDecimal total = materialProductList.stream().map(li -> li.getQuantityMaterial()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if(o.getQuantity()==null){
                        o.setQuantity(BigDecimal.ZERO);
                    }
                    if(o.getQuantity().compareTo(total)==-1){
                        throw new CustomException("面辅料数量总和不能大于调拨量，请核实！");
                    }
                }
            });
            int row=invInventoryTransferItemMapper.inserts(listInvInventoryTransfer);
            listInvInventoryTransfer.forEach(o->{
                List<InvInventoryTransferMaterialProduct> materialProductList = o.getMaterialProductList();
                if(CollectionUtil.isNotEmpty(materialProductList)){
                    materialProductList.forEach(li->{
                        li.setInventoryTransferItemSid(o.getInventoryTransferItemSid())
                                .setInventoryTransferSid(o.getInventoryTransferSid());
                    });
                    invInventoryTransferMaterialProductMapper.inserts(materialProductList);
                }
            });
        }
    }

    private void addInvInventoryTransferAttach(InvInventoryTransfer invInventoryTransfer,List<InvInventoryTransferAttachment> attachmentList){
        attachmentMapper.delete(
                new QueryWrapper<InvInventoryTransferAttachment>().lambda()
                        .eq(InvInventoryTransferAttachment::getInventoryTransferSid,invInventoryTransfer.getInventoryTransferSid()));
        if(CollectionUtils.isNotEmpty(attachmentList)){
            attachmentList.forEach(o->{
                o.setInventoryTransferSid(invInventoryTransfer.getInventoryTransferSid());
                o.setClientId(ApiThreadLocalUtil.get().getClientId());
                o.setCreateDate(new Date());
                o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            });
            int row=attachmentMapper.inserts(attachmentList);
        }
    }

    /**
     * 修改调拨单
     *
     * @param invInventoryTransfer 调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryTransfer(InvInventoryTransfer invInventoryTransfer) {
        String handleStatus = invInventoryTransfer.getHandleStatus();
        InvInventoryTransfer response = invInventoryTransferMapper.selectInvInventoryTransferById(invInventoryTransfer.getInventoryTransferSid());
        List<InvInventoryTransferItem> listInvInventoryTransfer = invInventoryTransfer.getListInvInventoryTransfer();
        if(handleStatus.equals(ConstantsEms.CHECK_STATUS)){
            if(CollectionUtils.isEmpty(listInvInventoryTransfer)){
                throw new CustomException("确认时，明细行不允许为空");
            }
        }
        int row=invInventoryTransferMapper.updateById(invInventoryTransfer);
        setItemNum(listInvInventoryTransfer);
        addInvInventoryTransferItem(invInventoryTransfer,listInvInventoryTransfer);
        addInvInventoryTransferAttach(invInventoryTransfer,invInventoryTransfer.getAttachList());
        if (row > 0) {
            //插入日志
            String bussiness = invInventoryTransferMapper.selectById(invInventoryTransfer.getInventoryTransferSid()).getHandleStatus().equals(ConstantsEms.SAVA_STATUS) ? "编辑" : "变更";
            MongodbUtil.insertUserLog(invInventoryTransfer.getInventoryTransferSid(), bussiness, TITLE);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(invInventoryTransfer.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(invInventoryTransfer);
        }
        return row;
    }

    /**
     * 变更调拨单
     *
     * @param invInventoryTransfer 调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvInventoryTransfer(InvInventoryTransfer invInventoryTransfer) {
        InvInventoryTransfer response = invInventoryTransferMapper.selectInvInventoryTransferById(invInventoryTransfer.getInventoryTransferSid());
        int row=invInventoryTransferMapper.updateAllById(invInventoryTransfer);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventoryTransfer.getInventoryTransferSid(), BusinessType.CHANGE.getValue(), response,invInventoryTransfer,TITLE);
        }
        return row;
    }

    /**
     * 批量删除调拨单
     *
     * @param inventoryTransferSids 需要删除的调拨单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventoryTransferByIds(List<Long> inventoryTransferSids) {
        int row = invInventoryTransferMapper.deleteBatchIds(inventoryTransferSids);
        if(row>0){
            invInventoryTransferItemMapper.delete(new QueryWrapper<InvInventoryTransferItem>()
                    .lambda()
                    .in(InvInventoryTransferItem::getInventoryTransferSid,inventoryTransferSids)
            );
            invInventoryTransferMaterialProductMapper.delete(new QueryWrapper<InvInventoryTransferMaterialProduct>().lambda()
            .in(InvInventoryTransferMaterialProduct::getInventoryTransferSid,inventoryTransferSids)
            );
            for (Long inventoryTransferSid : inventoryTransferSids) {
                InvInventoryTransfer invInventoryTransfer = new InvInventoryTransfer();
                invInventoryTransfer.setInventoryTransferSid(inventoryTransferSid);
                //校验是否存在待办
                checkTodoExist(invInventoryTransfer);
            }
        }
        return row;
    }

    /**
     * 关闭调拨单
     */
    public int close(List<Long> inventoryTransferSids) {
        int row = invInventoryTransferMapper.update(new InvInventoryTransfer(), new UpdateWrapper<InvInventoryTransfer>().lambda()
                .in(InvInventoryTransfer::getInventoryTransferSid, inventoryTransferSids)
                .set(InvInventoryTransfer::getHandleStatus, HandleStatus.CLOSED.getCode())
        );
        return row;
    }
    /**
     * 启用/停用
     * @param invInventoryTransfer
     * @return
     */
    @Override
    public int changeStatus(InvInventoryTransfer invInventoryTransfer){
        int row=0;
        Long[] sids=invInventoryTransfer.getInventoryTransferSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invInventoryTransfer.setInventoryTransferSid(id);
                row=invInventoryTransferMapper.updateById( invInventoryTransfer);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invInventoryTransfer.getInventoryTransferSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,null);
            }
        }
        return row;
    }
    //明细报表生成库存预留
    @Override
    public int create(Long[] inventoryTransferItemSidList){
        int row = invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                .in(InvReserveInventory::getBusinessOrderItemSid, inventoryTransferItemSidList)
        );
        InvInventoryTransferRequest transferRequest = new InvInventoryTransferRequest();
        transferRequest.setInventoryTransferItemSidList(inventoryTransferItemSidList);
        List<InvInventoryTransferResponse> list = invInventoryTransferItemMapper.reportInvInventoryTransfer(transferRequest);
        List<InvInventoryTransferItem> invInventoryTransferItems = BeanCopyUtils.copyListProperties(list, InvInventoryTransferItem::new);
        if(CollectionUtil.isNotEmpty(invInventoryTransferItems)){
            Map<Long, List<InvInventoryTransferItem>> listMap = invInventoryTransferItems.stream().collect(Collectors.groupingBy(v -> v.getInventoryTransferSid()));
            listMap.keySet().forEach(l->{
                List<InvInventoryTransferItem> items = listMap.get(l);
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
        invInventoryTransferItemMapper.update(new InvInventoryTransferItem(),new UpdateWrapper<InvInventoryTransferItem>().lambda()
                .in(InvInventoryTransferItem::getInventoryTransferItemSid,sids)
                .set(InvInventoryTransferItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
        );
        return row;
    }

    //生成预留库存
    public  void createInv(List<InvInventoryTransferItem> itemList){
        itemList.forEach(m->{
            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
            BeanCopyUtils.copyProperties(m,invInventoryLocation);
            invInventoryLocation.setBarcodeSid(m.getBarcodeSid());
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
                invInventoryTransferItemMapper.update(new InvInventoryTransferItem(),new UpdateWrapper<InvInventoryTransferItem>().lambda()
                        .eq(InvInventoryTransferItem::getInventoryTransferItemSid,m.getInventoryTransferItemSid())
                        .set(InvInventoryTransferItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                );
            }else if(m.getQuantity().compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1){
                invInventoryTransferItemMapper.update(new InvInventoryTransferItem(),new UpdateWrapper<InvInventoryTransferItem>().lambda()
                        .eq(InvInventoryTransferItem::getInventoryTransferItemSid,m.getInventoryTransferItemSid())
                        .set(InvInventoryTransferItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                );
                m.setQuantity(location.getAbleQuantity());
            }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1){
                invInventoryTransferItemMapper.update(new InvInventoryTransferItem(),new UpdateWrapper<InvInventoryTransferItem>().lambda()
                        .eq(InvInventoryTransferItem::getInventoryTransferItemSid,m.getInventoryTransferItemSid())
                        .set(InvInventoryTransferItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                );
                m.setQuantity(BigDecimal.ZERO);
            }
        });
        List<InvReserveInventory> invReserveInventories = new ArrayList<>();
        itemList.forEach(li->{
            InvReserveInventory invReserveInventory = new InvReserveInventory();
            invReserveInventory.setBarcodeSid(li.getBarcodeSid())
                    .setBusinessOrderCode(li.getInventoryTransferCode())
                    .setBusinessOrderItemNum(Long.valueOf(li.getItemNum()))
                    .setBusinessOrderSid(li.getInventoryTransferSid())
                    .setBusinessOrderItemSid(li.getInventoryTransferItemSid())
                    .setInventoryTransferCode(li.getInventoryTransferCode())
                    .setInventoryTransferItemNum(Long.valueOf(li.getItemNum()))
                    .setInventoryTransferItemSid(li.getInventoryTransferItemSid())
                    .setInventoryTransferSid(li.getInventoryTransferSid())
                    .setMaterialSid(li.getMaterialSid())
                    .setReserveType("DBD")
                    .setSku1Sid(li.getSku1Sid())
                    .setCustomerSid(li.getCustomerSid())
                    .setSpecialStock(li.getSpecialStock())
                    .setVendorSid(li.getVendorSid())
                    .setSku2Sid(li.getSku2Sid())
                    .setStorehouseSid(li.getStorehouseSid())
                    .setStorehouseLocationSid(li.getStorehouseLocationSid())
                    .setQuantity(li.getQuantity());
            invReserveInventories.add(invReserveInventory);
        });
        //生成库存预留
        invReserveInventoryMapper.inserts(invReserveInventories);
    }

    /**
     *更改确认状态
     * @param invInventoryTransfer
     * @return
     */
    @Override
    public int check(InvInventoryTransfer invInventoryTransfer){
        int row=0;
        Long[] sids=invInventoryTransfer.getInventoryTransferSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invInventoryTransfer.setInventoryTransferSid(id);
                InvInventoryTransfer note = selectInvInventoryTransferById(id);
                judgeNull(note);
                row=invInventoryTransferMapper.updateById(invInventoryTransfer);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                InvInventoryTransfer transfer = new InvInventoryTransfer();
                transfer.setInventoryTransferSid(id);
                //生成库存预留
                InvInventoryTransferRequest invInventoryTransferRequest = new InvInventoryTransferRequest();
                invInventoryTransferRequest.setInventoryTransferCode(note.getInventoryTransferCode().toString());
                List<InvInventoryTransferResponse> list = invInventoryTransferItemMapper.reportInvInventoryTransfer(invInventoryTransferRequest);
                List<InvInventoryTransferItem> invInventoryTransferItems = BeanCopyUtils.copyListProperties(list, InvInventoryTransferItem::new);
                createInv(invInventoryTransferItems);
                //校验是否存在待办
                checkTodoExist(transfer);
                //插入日志
               // MongodbUtil.insertUserLog(invInventoryTransfer.getInventoryTransferSid(), BusinessType.CHECK.getValue(),TITLE);
            }
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
            InvInventoryTransfer note = selectInvInventoryTransferById(id);
            List<InvInventoryTransferItem> list = note.getListInvInventoryTransfer();
            if(CollectionUtils.isEmpty(list)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不允许为空");
                msgList.add(errMsgResponse);
            }else{
                List<InvInventoryTransferItem> noteItems = list.stream().filter(item -> item.getQuantity() == null).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(noteItems)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setMsg("明细行的数量不允许为空");
                    msgList.add(errMsgResponse);
                }else{
                    String specialStock = note.getSpecialStock();
                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                    BeanCopyUtils.copyProperties(note,invInventoryLocation);
                    list.forEach(m->{
                        InvInventoryLocation locationAble=null;
                        invInventoryLocation.setBarcodeSid(m.getBarcodeSid());
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
                            errMsgResponse.setMsg("行号为"+m.getItemNum()+"的明细可用库存不足");
                            msgList.add(errMsgResponse);
                        }
                    });
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
            }
        });
        request.setMsgList(msgList);
        return request;
    }



    /**
     * 导入调拨单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        Long basStorehouseSid=null;
        String basStorehouseCode=null;
        Long StorehouseLocationSid=null;
        String storehouseLocationCode=null;
        Long destBasStorehouseSid=null;
        String destStorehouseCode=null;
        Long destStorehouseLocationSid=null;
        String destStorehouseLocationCode=null;
        Long vendorSid=null;
        Long customerSid=null;
        Boolean matchOne=false;
        Boolean matchtwo=false;
        List<Long> bardCodeList = new ArrayList<>();
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
            InvInventoryTransfer invInventoryTransfer = new InvInventoryTransfer();
            List<InvInventoryTransferItem> invInventoryTransferList = new ArrayList<>();
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
                BasMaterialBarcode basMaterialBarcode=null;
                String movementType=null;
                BigDecimal quantity=null;
                Date accountDate=null;
                String unitBase=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                       // throw new BaseException("作业类型名称，不能为空，导入失败");
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
                                movementType=conMovementType.getCode();
                                List<ConInOutStockDocCategory> moveTypeList = conInOutStockDocCategoryMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                                        .eq(ConInOutStockDocCategory::getInvDocCategoryCode, "ITN")
                                        .eq(ConInOutStockDocCategory::getMovementTypeCode, conMovementType.getCode())
                                );
                                if(CollectionUtil.isEmpty(moveTypeList)){
                                    //throw new BaseException("该作业类型，不属于调拨单，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("该作业类型，不属于调拨单，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    matchOne=true;
                                }
                            }
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                      //  throw new BaseException("单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("开单日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(1).toString());
                        if(!validDate){
                           // throw new BaseException("单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("开单日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            accountDate=DateUtil.parseDate(objects.get(1).toString());
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                       // throw new BaseException("仓库编码，不能为空，导入失败");
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
                               // throw new BaseException("仓库必须是确认且已启用状态，导入失败");
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
                       // throw new BaseException("库位编码，不能为空，导入失败");
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
                            //throw new BaseException("编码为" + objects.get(3).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(3).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
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
                    if (objects.get(4) == null || objects.get(4) == "") {
                       // throw new BaseException("仓库编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("目的仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse destBasStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(4).toString())
                        );
                        if (destBasStorehouse == null) {
                            //throw new BaseException("没有编码为" + objects.get(4).toString() + "的目的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(4).toString() + "的目的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(destBasStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(destBasStorehouse.getStatus())){
                                // throw new BaseException("目的仓库必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("目的仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            destBasStorehouseSid = Long.valueOf(destBasStorehouse.getStorehouseSid());
                            destStorehouseCode = destBasStorehouse.getStorehouseCode();
                        }
                    }
                    if(objects.get(5) == null || objects.get(5) == ""){
                       // throw new BaseException("目的库位编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("目的库位，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(5) != null && objects.get(5) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, destBasStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(5).toString())
                        );
                        if (basStorehouseLocation == null) {
                           // throw new BaseException("编码为" + objects.get(4).toString() + "的目的仓库下没有"+objects.get(5).toString()+"的目的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(4).toString() + "的目的仓库下没有"+objects.get(5).toString()+"的目的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                              //  throw new BaseException("库位必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("目的库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            destStorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                            destStorehouseLocationCode = basStorehouseLocation.getLocationCode();
                        }
                    }
                    Boolean isCommon = commonLo(movementType);
                    if(basStorehouseSid!=null&&destBasStorehouseSid!=null){
                        if(isCommon){
                            if(!basStorehouseSid.toString().equals(destBasStorehouseSid.toString())){
                                //  throw new BaseException("同仓，仓库和目的仓库必须相同，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为同仓不同库时，仓库和目的仓库必须相同，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(destStorehouseLocationSid!=null&&StorehouseLocationSid!=null){
                                if(destStorehouseLocationSid.toString().equals(StorehouseLocationSid.toString())){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("作业类型为同仓不同库时，库位与目的库位不能相同，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }else{
                            if(basStorehouseSid!=null&&destBasStorehouseSid!=null){
                                if(basStorehouseSid.toString().equals(destBasStorehouseSid.toString())){
                                    // throw new BaseException("不同仓，仓库和目的仓库不允许相同，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("作业类型为不同仓时，仓库和目的仓库不允许相同，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                    if(objects.get(6) != null && objects.get(6) != ""){
                        String sep = objects.get(6).toString();
                        if(objects.get(6) != null && objects.get(6) != ""){
                            String special = specialMaps.get(objects.get(6).toString());
                            if(special==null){
                                //throw new BaseException("特殊库存数据格式错误，导入失败");
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
                                    //  throw new BaseException("特殊库存，必须是确认且已启用状态，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("特殊库存，必须是确认且已启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    specialStock=special;
                                }
                            }
                        }
                    }
                    if(matchOne){
                        //-常规物料(免费)
                        if("SY01".equals(movementType)||"SY02".equals(movementType)){
                            if(specialStock!=null){
                                //throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SY012".equals(movementType)||"SY022".equals(movementType)){
                            if(!ConstantsEms.CUS_RA.equals(specialStock)){
                                //  throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SY013".equals(movementType)||"SY023".equals(movementType)){
                            if(!ConstantsEms.VEN_CU.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SY011".equals(movementType)||"SY021".equals(movementType)){
                            if(!ConstantsEms.VEN_RA.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为甲供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为甲供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SY014".equals(movementType)||"SY024".equals(movementType)){
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
                    if (objects.get(7) != "" &&objects.get(7) != null) {
                        String vendorCode = objects.get(7).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorCode)
                        );
                        if (basVendor == null) {
                           // throw new BaseException("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                               // throw new BaseException("供应商必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            vendorSid = Long.valueOf(basVendor.getVendorSid());
                        }
                    }
                    if (objects.get(8) != ""&&objects.get(8) != null) {
                        String customerCode = objects.get(8).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerCode));
                        if (basCustomer == null) {
                            //throw new BaseException("简称为" + customerCode + "没有对应的客户，导入失败");
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
                        if(objects.get(6)!=null&&objects.get(6)!=""){
                            if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(6).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(6).toString()))){
                                if(objects.get(7)==null||objects.get(7)==""){
                                    //throw new BaseException("供应商简称不能为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                if(objects.get(8)!=null&&objects.get(8)!=""){
                                    // throw new BaseException("客户简称必须为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(6).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(6).toString()))){
                                if(objects.get(8)==null||objects.get(8)==""){
                                    // throw new BaseException("客户简称不能为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                if(objects.get(7)!=null&&objects.get(7)!=""){
                                    //  throw new BaseException("供应商简称必须为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                        if(objects.get(6)==null ||objects.get(6)==""){
                            if(objects.get(7)!=null&&objects.get(7)!=""){
                                //throw new BaseException("供应商简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(8)!=null&&objects.get(8)!=""){
                                //throw new BaseException("客户简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    invInventoryTransfer
                            .setMovementType(movementType)
                            .setStorehouseSid(basStorehouseSid)
                            .setOutStockStatus("WCK")
                            .setInStockStatus("WRK")
                            .setDestStorehouseSid(destBasStorehouseSid)
                            .setDestStorehouseLocationSid(destStorehouseLocationSid)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setDocumentDate(accountDate)
                            .setCustomerSid(customerSid)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSpecialStock((objects.get(6)==""||objects.get(6)==null)?null:specialMaps.get(objects.get(6).toString()))
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(9)==""||objects.get(9)==null)?null:objects.get(9).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                if (objects.get(0) == null || objects.get(0) == "") {
                  //  throw new BaseException("第"+num+"行，物料/商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial==null) {
                       // throw new BaseException("第"+num+"行，没有编码为"+objects.get(0).toString()+"的物料/商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为"+objects.get(0).toString()+"的物料/商品，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())){
                           // throw new BaseException("第"+num+"行，物料/商品必须是确认且已启用状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("物料/商品必须是确认且已启用状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        materialName=basMaterial.getMaterialName();
                        materialSid=basMaterial.getMaterialSid();
                        unitBase=basMaterial.getUnitBase();
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                   // throw new BaseException("第"+num+"行，SKU1名称不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("SKU1名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(1).toString())
                    );
                    if (basSku==null) {
                       // throw new BaseException("第"+num+"行，没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus())){
                           // throw new BaseException("第"+num+"行，SKU1名称必须是确认且已启用状态，导入失败");
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
                        //throw new BaseException("第"+num+"行，没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(basSku2.equals(ConstantsEms.SKUTYP_YS)){
                            //throw new BaseException("第"+num+"行，SKU2名称不能是颜色类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU2名称不能是颜色类型，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku2.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku2.getStatus())){
                           // throw new BaseException("第"+num+"行，SKU2名称必须是确认且已启用状态，导入失败");
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
                            //throw new BaseException("第"+num+"行的数量小于0，不允许导入，导入失败");
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
                   // throw new BaseException("第"+num+"行没有对应的商品条码存在，导入失败");
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
                InvInventoryTransferItem invInventoryTransferItem = new InvInventoryTransferItem();
                invInventoryTransferItem
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseCode(basStorehouseCode)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setStorehouseLocationCode(storehouseLocationCode)
                        .setDestStorehouseSid(destBasStorehouseSid)
                        .setDestStorehouseCode(destStorehouseCode)
                        .setDestStorehouseLocationSid(destStorehouseLocationSid)
                        .setDestStorehouseLocationCode(destStorehouseLocationCode)
                        .setSku1Sid(sku1Sid)
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
                        .setQuantity(quantity)
                        .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                invInventoryTransferList.add(invInventoryTransferItem);
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
            invInventoryTransfer.setListInvInventoryTransfer(invInventoryTransferList);
            insertInvInventoryTransfer(invInventoryTransfer);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        return AjaxResult.success(1);
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
    /**
     * 匹配值 是否重复
     */
    public Boolean match(String remark,String match){
        String[] remarkList = remark.split(";");
        List remarkListNow = Arrays.asList(remarkList);
        boolean exit = remarkListNow.stream().anyMatch(m -> m.equals(match));
        return exit;
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
    /**
     * 调拨单-明细对象
     */
    private void addInventoryTransferItem(InvInventoryTransfer invInventoryTransfer, List<InvInventoryTransferItem> invInventoryTransferItemList) {
        invInventoryTransferItemMapper.delete(new UpdateWrapper<InvInventoryTransferItem>()
                .lambda()
                .eq(InvInventoryTransferItem::getInventoryTransferSid, invInventoryTransfer.getInventoryTransferSid())
        );
        invInventoryTransferItemList.forEach(o -> {
            o.setInventoryTransferSid(invInventoryTransfer.getInventoryTransferSid());
            invInventoryTransferItemMapper.insert(o);
        });
    }

    //同仓
    public Boolean commonLo(String code){
        List<String> transferList = Arrays.asList("SY023", "SY022", "SY024","SY02","SY021");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }
}
