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
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvReceiptNoteReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvReceiptNoteReportResponse;
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
import com.platform.ems.service.IInvGoodReceiptNoteService;
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
 * 收货单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@Service
@SuppressWarnings("all")
public class InvGoodReceiptNoteServiceImpl extends ServiceImpl<InvGoodReceiptNoteMapper,InvGoodReceiptNote>  implements IInvGoodReceiptNoteService {
    @Autowired
    private InvGoodReceiptNoteMapper invGoodReceiptNoteMapper;
    @Autowired
    private InvGoodReceiptNoteItemMapper invGoodReceiptNoteItemMapper;
    @Autowired
    private InvGoodReceiptNoteAttachmentMapper invGoodReceiptNoteAttachmentMapper;
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
    private RemoteFlowableService flowableService;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;

    private static final String TITLE = "收货单";
    /**
     * 查询收货单
     *
     * @param goodReceiptNoteSid 收货单ID
     * @return 收货单
     */
    @Override
    public InvGoodReceiptNote selectInvGoodReceiptNoteById(Long goodReceiptNoteSid) {
        InvGoodReceiptNote invGoodReceiptNote = invGoodReceiptNoteMapper.selectInvGoodReceiptNoteById(goodReceiptNoteSid);
        List<InvGoodReceiptNoteItem> invGoodReceiptNoteItems = invGoodReceiptNoteItemMapper.selectInvGoodReceiptNoteItemById(goodReceiptNoteSid);
        List<InvGoodReceiptNoteItem> items = sort(invGoodReceiptNoteItems,null);
        invGoodReceiptNote.setListInvGoodReceiptNoteItem(items);
        List<InvGoodReceiptNoteAttachment> invGoodReceiptNoteAttachments = invGoodReceiptNoteAttachmentMapper.selectInvGoodReceiptNoteAttachmentById(goodReceiptNoteSid);
        invGoodReceiptNote.setListInvGoodReceiptNoteAttachment(invGoodReceiptNoteAttachments);
        MongodbUtil.find(invGoodReceiptNote);
        return  invGoodReceiptNote;
    }

    @Override
    public List<InvGoodReceiptNoteItem> sort(List<InvGoodReceiptNoteItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvGoodReceiptNoteItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit.forEach(li -> {
                        String skuName = li.getSku2Name();
                        String[] nameSplit = skuName.split("/");
                        if (nameSplit.length == 1) {
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        }else{
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
                    List<InvGoodReceiptNoteItem> allList = new ArrayList<>();
                    List<InvGoodReceiptNoteItem> allThirdList = new ArrayList<>();
                    List<InvGoodReceiptNoteItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvGoodReceiptNoteItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvGoodReceiptNoteItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvGoodReceiptNoteItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvGoodReceiptNoteItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvGoodReceiptNoteItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvGoodReceiptNoteItem::getMaterialCode)
                        .thenComparing(InvGoodReceiptNoteItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvGoodReceiptNoteItem::getMaterialCode)
                        .thenComparing(InvGoodReceiptNoteItem::getSku1Name)
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
    public InvGoodReceiptNote getCopy(Long sid){
        InvGoodReceiptNote invGoodReceiptNote = selectInvGoodReceiptNoteById(sid);
        invGoodReceiptNote.setNoteSid(null)
                .setNoteCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setAccounter(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvGoodReceiptNoteItem> list = invGoodReceiptNote.getListInvGoodReceiptNoteItem();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setNoteSid(null)
                        .setNoteItemSid(null)
                        .setCreateDate(null)
                        .setCreatorAccount(null);
            });
        }
        return invGoodReceiptNote;
    }

    /**
     * 查询收货单列表
     *
     * @param invGoodReceiptNote 收货单
     * @return 收货单
     */
    @Override
    public List<InvGoodReceiptNote> selectInvGoodReceiptNoteList(InvGoodReceiptNote invGoodReceiptNote) {
        List<InvGoodReceiptNote> list = invGoodReceiptNoteMapper.selectInvGoodReceiptNoteList(invGoodReceiptNote);
        return list;
    }

    /**
     * 物料需求测算-创建收货单
     */
    @Override
    public InvGoodReceiptNote getGoodReceiptNote(List<TecBomItemReport> order) {
        Long salesOrderCode = order.get(0).getSalesOrderCode();
        String orderCodeRemark = order.get(0).getSalesOrderCodeRemark();
        List<Long> saleOrderSidList = order.stream().map(li -> li.getCommonSid()).collect(Collectors.toList());
        Long customerSid=null;
        List<SalSalesOrder> salSalesOrders=null;
        if(salesOrderCode!=null){
             salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>()
                    .lambda()
                    .in(SalSalesOrder::getSalesOrderSid, saleOrderSidList)
            );

        }else{
            if(orderCodeRemark==null){
                throw new CustomException("非已确认状态的销售订单测算的物料需求，无法进行此操作！");
            }
            List<String> codes = new ArrayList<>();
            order.stream().forEach(li->{
                String salesOrderCodeRemark = li.getSalesOrderCodeRemark();
                String[] salesOrderCodes = salesOrderCodeRemark.split(";");
                for (String code : salesOrderCodes) {
                    codes.add(code);
                }
            });
            salSalesOrders = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>()
                    .lambda()
                    .in(SalSalesOrder::getSalesOrderCode, codes)
            );
        }
        if(CollectionUtils.isNotEmpty(salSalesOrders)){
            Set<Long> customerSidSet = salSalesOrders.stream().map(li -> li.getCustomerSid()).collect(Collectors.toSet());
            if(customerSidSet.size()>1){
                throw new CustomException("所选择数据的销售订单的客户不一致，请检查！");
            }
            salSalesOrders.forEach(li->{
                if(!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())){
                    throw  new  CustomException("非已确认状态的销售订单测算的物料需求，无法点击此按钮！");
                }
            });
        }else{
            throw new CustomException("非已确认状态的销售订单测算的物料需求，无法进行此操作！");
        }
        customerSid=salSalesOrders.get(0).getCustomerSid();
        InvGoodReceiptNote invGoodReceiptNote = new InvGoodReceiptNote();
        invGoodReceiptNote
                .setDocumentType(DocCategory.RECIPIT.getCode())
                .setCreateDate(new Date())
                .setCustomerSid(customerSid)
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setInOutStockStatus("WRK")
                .setSpecialStock(ConstantsEms.CUS_RA)
                .setDocumentDate(new Date())
                .setHandleStatus(ConstantsEms.SAVA_STATUS);
        List<InvGoodReceiptNoteItem> orderItems = new ArrayList<>();
        order.forEach(li->{
            BasMaterialBarcode basMaterialBarcode=null;
            Long barcodeSid=null;
            String barcode=null;
            InvGoodReceiptNoteItem invGoodReceiptNoteItem = new InvGoodReceiptNoteItem();
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
                throw new CustomException(li.getMaterialCode()+","+li.getMaterialName()+"没有对应的商品条码，无法创建收货单");
            }
            invGoodReceiptNoteItem.setMaterialSid(li.getBomMaterialSid())
                    .setMaterialName(li.getMaterialName())
                    .setMaterialCode(li.getMaterialCode())
                    .setSku1Name(li.getSku1Name())
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setBarcodeSid(barcodeSid)
                    .setInOutStockStatus("WRK")
                    .setBarcode(Long.valueOf(barcode))
                    .setSku2Name(li.getSku2Name())
                    .setQuantity(BigDecimal.valueOf(Double.valueOf(li.getLossRequireQuantity())))
                    .setUnitBase(li.getUnitBase())
                    .setProductQuantity(li.getProductQuantity())
                    .setUnitBaseName(li.getUnitBaseName())
                    .setProductCode(li.getSaleMaterialCode())
                    .setProductSid(li.getSaleMaterialSid())
                    .setProductName(li.getSaleMaterialName())
                    .setProductSku1Sid(li.getSaleSku1Sid())
                    .setProductSoCodes(li.getSalesOrderCodeRemark())
                    .setProductSku1Name(li.getSaleSku1Name())
                    .setProductSku2Name(li.getSaleSku2Name())
                    .setProductSku2Sid(li.getSaleSku2Sid())
                    .setProductCodes(li.getMaterialCodeRemark())
                    .setProductSku1Names(li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getMaterialSku2Remark())
                    .setSalesOrderCode(li.getSalesOrderCode())
                    .setSalesOrderSid(li.getSalesOrderCode()!=null?li.getCommonSid():null)
                    .setSalesOrderItemNum(li.getSalesOrderCode()!=null?li.getCommonItemSid():null)
                    .setSalesOrderItemSid(li.getSalesOrderCode()!=null?li.getCommonItemNum():null);
            orderItems.add(invGoodReceiptNoteItem);
        });
        invGoodReceiptNote.setListInvGoodReceiptNoteItem(orderItems);
        return invGoodReceiptNote;
    }

    /**
     * 查询收货单报表
     *
     */
    @Override
    public List<InvReceiptNoteReportResponse> reportInvReceiptNote(InvReceiptNoteReportRequest request){
        return invGoodReceiptNoteItemMapper.reportInvGoodReceiptNote(request);
    }

    public void judgeNull(InvGoodReceiptNote note){
        List<InvGoodReceiptNoteItem> list = note.getListInvGoodReceiptNoteItem();
        if(CollectionUtils.isEmpty(list)){
            throw  new CustomException("确认时，明细行不允许为空");
        }
        list.forEach(item->{
            if(item.getQuantity()==null){
                throw  new CustomException("确认时，明细行的数量不允许为空");
            }
        });
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
     * 新增收货单
     * 需要注意编码重复校验
     * @param invGoodReceiptNote 收货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvGoodReceiptNote(InvGoodReceiptNote invGoodReceiptNote) {
        if(ConstantsEms.CHECK_STATUS.equals(invGoodReceiptNote.getHandleStatus())){
            judgeNull(invGoodReceiptNote);
        }
        int row= invGoodReceiptNoteMapper.insert(invGoodReceiptNote);
        List<InvGoodReceiptNoteItem> listInvGoodReceiptNoteItem = invGoodReceiptNote.getListInvGoodReceiptNoteItem();
        List<InvGoodReceiptNoteItem> list = invGoodReceiptNote.getListInvGoodReceiptNoteItem();
        List<InvGoodReceiptNoteAttachment> listInvGoodReceiptNoteAttachment = invGoodReceiptNote.getListInvGoodReceiptNoteAttachment();
        //行号赋值
        setItemNum(list);
        createItem(invGoodReceiptNote,list);
        createAttach(invGoodReceiptNote,listInvGoodReceiptNoteAttachment);
        //待办通知
        InvGoodReceiptNote note = invGoodReceiptNoteMapper.selectById(invGoodReceiptNote.getNoteSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(invGoodReceiptNote.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(invGoodReceiptNote.getNoteSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("收货单" + note.getNoteCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(note.getNoteCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invGoodReceiptNote);
        }
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invGoodReceiptNote.getNoteSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }
    public void createAttach(InvGoodReceiptNote invGoodReceiptNote,List<InvGoodReceiptNoteAttachment> listInvGoodReceiptNoteAttach){
        if(CollectionUtils.isNotEmpty(listInvGoodReceiptNoteAttach)){
            listInvGoodReceiptNoteAttach.forEach(o->{
                o.setNoteSid(invGoodReceiptNote.getNoteSid());
                invGoodReceiptNoteAttachmentMapper.insert(o);
            });
//            invGoodReceiptNoteAttachmentMapper.inserts(listInvGoodReceiptNoteAttach);
        }
    }

    public void createItem(InvGoodReceiptNote invGoodReceiptNote,List<InvGoodReceiptNoteItem> listInvGoodReceiptNoteItem){
        if(CollectionUtils.isNotEmpty(listInvGoodReceiptNoteItem)){
            listInvGoodReceiptNoteItem.forEach(o->{
                // 设置仓库库位CODE
                o.setStorehouseCode(setStorehouseCode(o.getStorehouseSid()));
                o.setStorehouseLocationCode(setStorehouseLocationCode(o.getStorehouseLocationSid()));
                o.setClientId(ApiThreadLocalUtil.get().getClientId());
                o.setCreateDate(new Date());
                o.setInOutStockStatus("WRK");
                o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                o.setNoteSid(invGoodReceiptNote.getNoteSid());
                o.setNoteItemSid(IdWorker.getId());
            });
            invGoodReceiptNoteItemMapper.inserts(listInvGoodReceiptNoteItem);
        }
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvGoodReceiptNoteItem> list){
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
    private void checkTodoExist(InvGoodReceiptNote invGoodReceiptNote) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invGoodReceiptNote.getNoteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invGoodReceiptNote.getNoteSid()));
        }
    }
    /**
     * 修改收货单
     *
     * @param invGoodReceiptNote 收货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvGoodReceiptNote(InvGoodReceiptNote invGoodReceiptNote) {
        if(ConstantsEms.CHECK_STATUS.equals(invGoodReceiptNote.getHandleStatus())){
            judgeNull(invGoodReceiptNote);
        }
        String handleStatus = invGoodReceiptNote.getHandleStatus();
        InvGoodReceiptNote response = invGoodReceiptNoteMapper.selectInvGoodReceiptNoteById(invGoodReceiptNote.getNoteSid());
        int row=invGoodReceiptNoteMapper.updateById(invGoodReceiptNote);
        List<InvGoodReceiptNoteItem> listInvGoodReceiptNoteItem = invGoodReceiptNote.getListInvGoodReceiptNoteItem();
        List<InvGoodReceiptNoteAttachment> listInvGoodReceiptNoteAttachment = invGoodReceiptNote.getListInvGoodReceiptNoteAttachment();
        //删除原有附件
        invGoodReceiptNoteAttachmentMapper.delete(new QueryWrapper<InvGoodReceiptNoteAttachment>().lambda()
                .eq(InvGoodReceiptNoteAttachment::getNoteSid,invGoodReceiptNote.getNoteSid()));
        //插入现有附件
        createAttach(invGoodReceiptNote, listInvGoodReceiptNoteAttachment);
        if (CollectionUtils.isNotEmpty(listInvGoodReceiptNoteItem)) {
            setItemNum(listInvGoodReceiptNoteItem);
            List<InvGoodReceiptNoteItem> invGoodReceiptNoteItems = invGoodReceiptNoteItemMapper.selectList(new QueryWrapper<InvGoodReceiptNoteItem>().lambda()
                    .eq(InvGoodReceiptNoteItem::getNoteSid, invGoodReceiptNote.getNoteSid())
            );
            if(CollectionUtil.isEmpty(invGoodReceiptNoteItems)){
                invGoodReceiptNoteItems=new ArrayList<InvGoodReceiptNoteItem>();
            }
            List<Long> longs = invGoodReceiptNoteItems.stream().map(li -> li.getNoteItemSid()).collect(Collectors.toList());
            List<Long> longsNow = listInvGoodReceiptNoteItem.stream().map(li -> li.getNoteItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<InvGoodReceiptNoteItem> reduceList = invGoodReceiptNoteItemMapper.selectList(new QueryWrapper<InvGoodReceiptNoteItem>().lambda()
                        .in(InvGoodReceiptNoteItem::getNoteItemSid, reduce)
                );
                invGoodReceiptNoteItemMapper.deleteBatchIds(reduce);
            }
            //修改明细
            List<InvGoodReceiptNoteItem> exitItem = listInvGoodReceiptNoteItem.stream().filter(li -> li.getNoteItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    // 设置仓库库位CODE
                    li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                    li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                    invGoodReceiptNoteItemMapper.updateById(li);
                });
            }
            //新增明细
            List<InvGoodReceiptNoteItem> nullItem = listInvGoodReceiptNoteItem.stream().filter(li -> li.getNoteItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                nullItem.forEach(li->{
                    // 设置仓库库位CODE
                    li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                    li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                    li.setNoteSid(invGoodReceiptNote.getNoteSid());
                    li.setInOutStockStatus("WRK");
                    invGoodReceiptNoteItemMapper.insert(li);
                });
            }
        }else{
            invGoodReceiptNoteItemMapper.delete(new QueryWrapper<InvGoodReceiptNoteItem>().lambda()
            .eq(InvGoodReceiptNoteItem::getNoteSid,invGoodReceiptNote.getNoteSid())
            );
        }
        if(row>0){
            String businessType=invGoodReceiptNoteMapper.selectById(invGoodReceiptNote.getNoteSid()).getHandleStatus().equals(ConstantsEms.CHECK_STATUS)?"变更":"编辑";
            MongodbUtil.insertUserLog(invGoodReceiptNote.getNoteSid(),businessType,TITLE);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(invGoodReceiptNote.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(invGoodReceiptNote);
        }
        return row;
    }
    /**
     * 关闭
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int close(List<Long> sidList){
        int row = invGoodReceiptNoteMapper.update(new InvGoodReceiptNote(), new UpdateWrapper<InvGoodReceiptNote>().lambda()
                .in(InvGoodReceiptNote::getNoteSid, sidList)
                .set(InvGoodReceiptNote::getHandleStatus, HandleStatus.CLOSED.getCode())
        );
        return row;
    }
    /**
     * 变更收货单
     *
     * @param invGoodReceiptNote 收货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvGoodReceiptNote(InvGoodReceiptNote invGoodReceiptNote) {
        judgeNull(invGoodReceiptNote);
        InvGoodReceiptNote response = invGoodReceiptNoteMapper.selectInvGoodReceiptNoteById(invGoodReceiptNote.getNoteSid());
        int row = invGoodReceiptNoteMapper.updateAllById(invGoodReceiptNote);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(invGoodReceiptNote.getNoteSid(), BusinessType.CHANGE.getValue(), response, invGoodReceiptNote, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收货单
     *
     * @param goodReceiptNoteSids 需要删除的收货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvGoodReceiptNoteByIds(List<Long> goodReceiptNoteSids) {
        int row = invGoodReceiptNoteMapper.deleteBatchIds(goodReceiptNoteSids);
        if(row>0){
            invGoodReceiptNoteItemMapper.delete(new QueryWrapper<InvGoodReceiptNoteItem>().lambda()
                    .in(InvGoodReceiptNoteItem::getNoteSid,goodReceiptNoteSids)
            );
            for (int i=0;i<goodReceiptNoteSids.size();i++){
                InvGoodReceiptNote issueNote = new InvGoodReceiptNote();
                issueNote.setNoteSid(goodReceiptNoteSids.get(i));
                //校验是否存在待办
                checkTodoExist(issueNote);
            }
        }
        return row;
    }

    /**
     * 启用/停用
     * @param invGoodReceiptNote
     * @return
     */
    @Override
    public int changeStatus(InvGoodReceiptNote invGoodReceiptNote){
        int row=0;
        Long[] sids=invGoodReceiptNote.getGoodReceiptNoteSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invGoodReceiptNote.setNoteSid(id);
                row=invGoodReceiptNoteMapper.updateById( invGoodReceiptNote);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invGoodReceiptNote.getNoteSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param invGoodReceiptNote
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(InvGoodReceiptNote invGoodReceiptNote){
        int row=0;
        Long[] sids=invGoodReceiptNote.getGoodReceiptNoteSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invGoodReceiptNote.setNoteSid(id);
                InvGoodReceiptNote note = selectInvGoodReceiptNoteById(id);
                judgeNull(note);
                row=invGoodReceiptNoteMapper.updateById(invGoodReceiptNote);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                MongodbUtil.insertUserLog(invGoodReceiptNote.getNoteSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        for (int i=0;i<sids.length;i++){
            InvGoodReceiptNote issueNote = new InvGoodReceiptNote();
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
            InvGoodReceiptNote note = selectInvGoodReceiptNoteById(id);
            List<InvGoodReceiptNoteItem> list = note.getListInvGoodReceiptNoteItem();
            if(CollectionUtils.isEmpty(list)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不允许为空");
                msgList.add(errMsgResponse);
            }else{
                List<InvGoodReceiptNoteItem> noteItems = list.stream().filter(item -> item.getQuantity() == null).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(noteItems)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setMsg("明细行的数量不允许为空");
                    msgList.add(errMsgResponse);
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
     * 导入收货单
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
            InvGoodReceiptNote invGoodReceiptNote = new InvGoodReceiptNote();
            List<InvGoodReceiptNoteItem> invGoodReceiptNoteList = new ArrayList<>();
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
                        //throw new BaseException("作业类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                                .eq(ConMovementType::getName, objects.get(0).toString())
                        );
                        if(conMovementType==null){
                         //   throw new BaseException("作业类型，配置错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.ENABLE_STATUS.equals(conMovementType.getStatus())||!ConstantsEms.CHECK_STATUS.equals(conMovementType.getHandleStatus())){
                               // throw new BaseException("作业类型，必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型，必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                List<ConInOutStockDocCategory> moveTypeList = conInOutStockDocCategoryMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                                        .eq(ConInOutStockDocCategory::getInvDocCategoryCode, "GRN")
                                        .eq(ConInOutStockDocCategory::getMovementTypeCode, conMovementType.getCode())
                                );
                                if(CollectionUtil.isEmpty(moveTypeList)){
                                    //throw new BaseException("该作业类型，不属于收货单，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("该作业类型，不属于收货单，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    movementType=conMovementType.getCode();
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
                           // throw new BaseException("没有编码为" + objects.get(2).toString() + "的仓库，导入失败");
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
                        if("SR09".equals(movementType)){
                            if(specialStock!=null){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR091".equals(movementType)){
                            if(!ConstantsEms.CUS_RA.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR092".equals(movementType)){
                            if(!ConstantsEms.VEN_CU.equals(specialStock)){
                                //throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR093".equals(movementType)){
                            if(!ConstantsEms.VEN_RA.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为甲供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为甲供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR094".equals(movementType)){
                            if(!ConstantsEms.CUS_VE.equals(specialStock)){
                                //throw new BaseException("作业类型为"+objects.get(0).toString()+"时，特殊库存必须为客户寄售，导入失败");
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
                           // throw new BaseException("简称为" + vendorCode + "没有对应的供应商，导入失败");
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
                            //throw new BaseException("简称为" + customerCode + "没有对应的客户，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + customerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())){
                               // throw new BaseException("客户必须是确认且已启用状态，导入失败");
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
                                    //  throw new BaseException("供应商简称必须为空，导入失败");
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
                                // throw new BaseException("客户简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    invGoodReceiptNote
                            .setMovementType(movementType)
                            .setStorehouseSid(basStorehouseSid)
                            .setInOutStockStatus("WRK")
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setDocumentDate(accountDate)
                            .setCustomerSid(customerSid)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSpecialStock((objects.get(4)==""||objects.get(4)==null)?null:specialMaps.get(objects.get(4).toString()))
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(7)==""||objects.get(7)==null)?null:objects.get(7).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                if (objects.get(0) == null || objects.get(0) == "") {
                    //throw new BaseException("第"+num+"行，物料/商品编码不可为空，导入失败");
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
                          //  throw new BaseException("第"+num+"行，物料/商品必须是确认且已启用状态，导入失败");
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
                    //throw new BaseException("第"+num+"行，SKU1名称不可为空，导入失败");
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
                            //throw new BaseException("第"+num+"行，SKU1名称必须是确认且已启用状态，导入失败");
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
                        if(basSku2.getSkuType().equals(ConstantsEms.SKUTYP_YS)){
                           // throw new BaseException("第"+num+"行，SKU2名称不能是颜色类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU2名称不能是颜色类型，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku2.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku2.getStatus())){
                            //throw new BaseException("第"+num+"行，SKU2名称必须是确认且已启用状态，导入失败");
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
                InvGoodReceiptNoteItem invGoodReceiptNoteItem = new InvGoodReceiptNoteItem();
                invGoodReceiptNoteItem.setSku1Sid(sku1Sid)
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
                invGoodReceiptNoteList.add(invGoodReceiptNoteItem);
            }
            HashSet<Long> longs = new HashSet<>(bardCodeList);
            if(longs.size()!=bardCodeList.size()){
                for (int i=0;i<bardCodeList.size();i++){
                    for (int j=i+1;j<bardCodeList.size();j++){
                        if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                            int nu=j+1+5;
                            //throw new BaseException("第"+nu+"行，商品条码重复，请核实");
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
            invGoodReceiptNote.setListInvGoodReceiptNoteItem(invGoodReceiptNoteList);
            insertInvGoodReceiptNote(invGoodReceiptNote);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        return AjaxResult.success(1);
    }

    /**
     * 收货单-明细对象
     */
    private void addInvGoodReceiptNoteItem(InvGoodReceiptNote goodReceiptNote, List<InvGoodReceiptNoteItem> goodReceiptNoteItemList) {
        invGoodReceiptNoteItemMapper.delete(new UpdateWrapper<InvGoodReceiptNoteItem>()
                .lambda()
                .eq(InvGoodReceiptNoteItem::getNoteSid, goodReceiptNote.getNoteSid())
        );
        goodReceiptNoteItemList.forEach(o -> {
            o.setNoteSid(goodReceiptNote.getNoteSid());
            invGoodReceiptNoteItemMapper.insert(o);
        });
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
}
