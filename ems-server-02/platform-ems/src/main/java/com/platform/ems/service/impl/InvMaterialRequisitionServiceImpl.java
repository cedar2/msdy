package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvMaterialRequisitionReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvMaterialRequisitionReportResponse;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.IInvMaterialRequisitionService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.*;
import com.platform.api.service.RemoteFlowableService;
import com.platform.api.service.RemoteUserService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 领退料单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class InvMaterialRequisitionServiceImpl extends ServiceImpl<InvMaterialRequisitionMapper,InvMaterialRequisition>  implements IInvMaterialRequisitionService {
    @Autowired
    private InvMaterialRequisitionMapper invMaterialRequisitionMapper;
    @Autowired
    private InvMaterialRequisitionItemMapper invMaterialRequisitionItemMapper;
    @Autowired
    private InvMaterialRequisitionAttachmentMapper invMaterialRequisitionAttachmentMapper;
    @Autowired
    private ConDocTypeMaterialRequisitionMapper conDocTypeMaterialRequisitionMapper;
    @Autowired
    private ConBuTypeMaterialRequisitionMapper conBuTypeMaterialRequisitionMapper;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConSpecialStockMapper conSpecialStockMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private RemoteFlowableService flowableService;
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
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    private static final String TITLE = "领退料";

    /**
     * 查询领退料单
     *
     * @param materialRequisitionSid 领退料单ID
     * @return 领退料单
     */
    @Override
    public InvMaterialRequisition selectInvMaterialRequisitionById(Long materialRequisitionSid) {
        InvMaterialRequisition invMaterialRequisition = invMaterialRequisitionMapper.selectInvMaterialRequisitionById(materialRequisitionSid);
        if (invMaterialRequisition == null){
            return null;
        }
        //领退料单-明细
        List<InvMaterialRequisitionItem> invMaterialRequisitionItemList =
                invMaterialRequisitionItemMapper.selectInvMaterialRequisitionItemById(materialRequisitionSid);
        //领退料单-附件
        InvMaterialRequisitionAttachment invMaterialRequisitionAttachment = new InvMaterialRequisitionAttachment();
        invMaterialRequisitionAttachment.setMaterialRequisitionSid(materialRequisitionSid);
        List<InvMaterialRequisitionAttachment> invMaterialRequisitionAttachmentList =
                invMaterialRequisitionAttachmentMapper.selectInvMaterialRequisitionAttachmentList(invMaterialRequisitionAttachment);
        //领退料单-合作伙伴
        //TODO
        List<InvMaterialRequisitionItem> items = sort(invMaterialRequisitionItemList,null);
        invMaterialRequisition.setInvMaterialRequisitionItemList(items);
        invMaterialRequisition.setInvMaterialRequisitionAttachmentList(invMaterialRequisitionAttachmentList);
        MongodbUtil.find(invMaterialRequisition);
        return invMaterialRequisition;
    }

    @Override
    public List<InvMaterialRequisitionItem> sort(List<InvMaterialRequisitionItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvMaterialRequisitionItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                    List<InvMaterialRequisitionItem> allList = new ArrayList<>();
                    List<InvMaterialRequisitionItem> allThirdList = new ArrayList<>();
                    List<InvMaterialRequisitionItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvMaterialRequisitionItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvMaterialRequisitionItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvMaterialRequisitionItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvMaterialRequisitionItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvMaterialRequisitionItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvMaterialRequisitionItem::getMaterialCode)
                        .thenComparing(InvMaterialRequisitionItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvMaterialRequisitionItem::getMaterialCode)
                        .thenComparing(InvMaterialRequisitionItem::getSku1Name)
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
    public InvMaterialRequisition getCopy(Long sid){
        InvMaterialRequisition invMaterialRequisition = selectInvMaterialRequisitionById(sid);
        invMaterialRequisition.setMaterialRequisitionSid(null)
                .setMaterialRequisitionCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvMaterialRequisitionItem> list = invMaterialRequisition.getInvMaterialRequisitionItemList();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setMaterialRequisitionSid(null)
                        .setMaterialRequisitionItemSid(null)
                        .setCreateDate(null)
                        .setCreatorAccount(null);
            });
        }
        return invMaterialRequisition;
    }
    /**
     * 物料需求测算-创建领料单
     */
    @Override
    public InvMaterialRequisition getMaterialRequisition(List<TecBomItemReport> order) {
        Long manufactureOrderCode = order.get(0).getManufactureOrderCode();
        String orderCodeRemark = order.get(0).getManufactureOrderCodeRemark();
        List<String> codes = new ArrayList<>();
        if(manufactureOrderCode==null){
            List<String> manufactureOrderCodeList = order.stream().map(li -> li.getManufactureOrderCodeRemark()).collect(Collectors.toList());
            manufactureOrderCodeList.forEach(li->{
                String[] codeArr = li.split(";");
                for (String s : codeArr) {
                    codes.add(s);
                }
            });
            Set<String> codeSet = codes.stream().collect(Collectors.toSet());
            if(codeSet.size()>1){
                throw new CustomException("所选择数据的生产订单号不一致，请检查！");
            }
            List<ManManufactureOrder> manManufactureOrders = manManufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>().lambda()
                    .in(ManManufactureOrder::getManufactureOrderCode, codes)
            );
            manufactureOrderCode=Long.valueOf(manManufactureOrders.get(0).getManufactureOrderCode());
            if(CollectionUtils.isNotEmpty(manManufactureOrders)){
                manManufactureOrders.forEach(li->{
                    if(!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())){
                        throw new CustomException("非已确认状态的生产订单测算的物料需求，无法进行此操作！");
                    }
                });
            }
        }else{
            List<Long> longs = order.stream().map(li -> li.getManufactureOrderCode()).collect(Collectors.toList());
            Set<Long> codeSet = longs.stream().collect(Collectors.toSet());
            if(codeSet.size()>1){
                throw new CustomException("所选择数据的生产订单号不一致，请检查！");
            }
            List<ManManufactureOrder> manManufactureOrders = manManufactureOrderMapper.selectList(new QueryWrapper<ManManufactureOrder>().lambda()
                    .in(ManManufactureOrder::getManufactureOrderCode, longs)
            );
            if(CollectionUtils.isNotEmpty(manManufactureOrders)){
                manManufactureOrders.forEach(li->{
                    if(!ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())){
                        throw new CustomException("非已确认状态的生产订单测算的物料需求，无法进行此操作！");
                    }
                });
            }
        }
        InvMaterialRequisition materialRequisition = new InvMaterialRequisition();
        materialRequisition
                .setDocumentType(DocCategory.REQUESTION_CHK.getCode())
                .setCreateDate(new Date())
                .setManufactureOrderSid(order.get(0).getCommonSid())
                .setManufactureOrderCode(manufactureOrderCode)
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setInOutStockStatus("WCK")
                .setDocumentDate(new Date())
                .setHandleStatus(ConstantsEms.SAVA_STATUS);
        List<InvMaterialRequisitionItem> orderItems = new ArrayList<>();
        order.forEach(li->{
            BasMaterialBarcode basMaterialBarcode=null;
            Long barcodeSid=null;
            String barcode=null;
            InvMaterialRequisitionItem invMaterialRequisitionItem = new InvMaterialRequisitionItem();
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
                throw new CustomException(li.getMaterialCode()+","+li.getMaterialName()+"没有对应的商品条码，无法创建领料单");
            }
            invMaterialRequisitionItem.setMaterialSid(li.getBomMaterialSid())
                    .setMaterialName(li.getMaterialName())
                    .setMaterialCode(li.getMaterialCode())
                    .setSku1Name(li.getSku1Name())
                    .setSku1Sid(li.getBomMaterialSku1Sid())
                    .setSku2Sid(li.getBomMaterialSku2Sid())
                    .setBarcodeSid(barcodeSid)
                    .setProductQuantity(li.getProductQuantity())
                    .setInOutStockStatus("WCK")
                    .setBarcode(barcode)
                    .setSku2Name(li.getSku2Name())
                    .setQuantity(BigDecimal.valueOf(Double.valueOf(li.getLossRequireQuantity())))
                    .setUnitBase(li.getUnitBase())
                    .setUnitBaseName(li.getUnitBaseName())
                    .setProductCode(li.getSaleMaterialCode())
                    .setProductSid(li.getSaleMaterialSid())
                    .setProductName(li.getSaleMaterialName())
                    .setProductSku1Sid(li.getSaleSku1Sid())
                    .setProductSku1Name(li.getSaleSku1Name())
                    .setProductSku2Name(li.getSaleSku2Name())
                    .setProductSku2Sid(li.getSaleSku2Sid())
                    .setProductCodes(li.getMaterialCodeRemark())
                    .setProductSku1Names(li.getMaterialSkuRemark())
                    .setProductSku2Names(li.getMaterialSku2Remark());
            orderItems.add(invMaterialRequisitionItem);
        });
        materialRequisition.setInvMaterialRequisitionItemList(orderItems);
        return materialRequisition;
    }
    /**
     * 查询领退料单列表
     *
     * @param invMaterialRequisition 领退料单
     * @return 领退料单
     */
    @Override
    public List<InvMaterialRequisition> selectInvMaterialRequisitionList(InvMaterialRequisition invMaterialRequisition) {
        List<InvMaterialRequisition> list = invMaterialRequisitionMapper.selectInvMaterialRequisitionList(invMaterialRequisition);
        return list;
    }
    /**
     * 查询领退料单明细报表
     *
     * @param invMaterialRequisition 领退料单
     * @return 领退料单
     */
    @Override
    public List<InvMaterialRequisitionReportResponse> reportInvMaterialRequisition(InvMaterialRequisitionReportRequest request) {
        return invMaterialRequisitionItemMapper.reportInvMaterialRequisition(request);
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
     * 新增领退料单
     * 需要注意编码重复校验
     * @param invMaterialRequisition 领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvMaterialRequisition(InvMaterialRequisition invMaterialRequisition) {
        if(ConstantsEms.CHECK_STATUS.equals(invMaterialRequisition.getHandleStatus())){
            judgeNull(invMaterialRequisition);
        }
        String documentType = invMaterialRequisition.getDocumentType();
        if(DocCategory.REQUESTION_RU.getCode().equals(documentType)){
            invMaterialRequisition.setInOutStockStatus("WRK");
        }else{
            invMaterialRequisition.setInOutStockStatus("WCK");
        }
        int row;
        List<InvMaterialRequisitionItem> invMaterialRequisitionItemList = invMaterialRequisition.getInvMaterialRequisitionItemList();
        if(CollectionUtils.isEmpty(invMaterialRequisitionItemList)&&invMaterialRequisition.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
            throw new BaseException("确认时，明细行不允许为空");
        }
        setConfirmInfo(invMaterialRequisition);
        row=invMaterialRequisitionMapper.insert(invMaterialRequisition);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invMaterialRequisition.getMaterialRequisitionSid(), BusinessType.INSERT.getValue(),TITLE);
        }
        //领退料单-明细对象
        if (CollectionUtils.isNotEmpty(invMaterialRequisitionItemList)) {
            setItemNum(invMaterialRequisitionItemList);
            addInvMaterialRequisitionItem(invMaterialRequisition, invMaterialRequisitionItemList);
        }
        //领退料单-附件对象
        List<InvMaterialRequisitionAttachment> invMaterialRequisitionAttachmentList =
                invMaterialRequisition.getInvMaterialRequisitionAttachmentList();
        if (CollectionUtils.isNotEmpty(invMaterialRequisitionAttachmentList)) {
            addInvMaterialRequisitionAttachment(invMaterialRequisition, invMaterialRequisitionAttachmentList);
        }
        //待办通知
        InvMaterialRequisition requisition = invMaterialRequisitionMapper.selectById(invMaterialRequisition.getMaterialRequisitionSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(invMaterialRequisition.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(invMaterialRequisition.getMaterialRequisitionSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("领退料单" + requisition.getMaterialRequisitionCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode( requisition.getMaterialRequisitionCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invMaterialRequisition);
        }
        return row;
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvMaterialRequisitionItem> list){
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
    private void checkTodoExist(InvMaterialRequisition invMaterialRequisition) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invMaterialRequisition.getMaterialRequisitionSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invMaterialRequisition.getMaterialRequisitionSid()));
        }
    }
    public void judgeNull(InvMaterialRequisition note){
        List<InvMaterialRequisitionItem> list = note.getInvMaterialRequisitionItemList();
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
     * 设置确认信息
     */
    private void setConfirmInfo(InvMaterialRequisition o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 采购订单-明细对象
     */
    private void addInvMaterialRequisitionItem(InvMaterialRequisition invMaterialRequisition, List<InvMaterialRequisitionItem> invMaterialRequisitionItemList) {
        invMaterialRequisitionItemMapper.delete(
                new UpdateWrapper<InvMaterialRequisitionItem>()
                        .lambda()
                        .eq(InvMaterialRequisitionItem::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid())
        );
        if(CollectionUtils.isNotEmpty(invMaterialRequisitionItemList)){
            invMaterialRequisitionItemList.forEach(o -> {
                // 设置仓库库位CODE
                o.setStorehouseCode(setStorehouseCode(o.getStorehouseSid()));
                o.setStorehouseLocationCode(setStorehouseLocationCode(o.getStorehouseLocationSid()));
                o.setMaterialRequisitionSid(invMaterialRequisition.getMaterialRequisitionSid());
                String documentType = invMaterialRequisition.getDocumentType();
                if(DocCategory.REQUESTION_RU.getCode().equals(documentType)){
                    o.setInOutStockStatus("WRK");
                }else{
                    o.setInOutStockStatus("WCK");
                }
                invMaterialRequisitionItemMapper.insert(o);
            });
        }
    }

    /**
     * 采购订单-附件对象
     */
    private void addInvMaterialRequisitionAttachment(InvMaterialRequisition invMaterialRequisition, List<InvMaterialRequisitionAttachment> invMaterialRequisitionAttachmentList) {
        invMaterialRequisitionAttachmentMapper.delete(
                new UpdateWrapper<InvMaterialRequisitionAttachment>()
                        .lambda()
                        .eq(InvMaterialRequisitionAttachment::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid())
        );
        if(CollectionUtils.isNotEmpty(invMaterialRequisitionAttachmentList)){
            invMaterialRequisitionAttachmentList.forEach(o -> {
                o.setMaterialRequisitionSid(invMaterialRequisition.getMaterialRequisitionSid());
                invMaterialRequisitionAttachmentMapper.insert(o);
            });
        }
    }

    /**
     * 修改领退料单
     *
     * @param invMaterialRequisition 领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvMaterialRequisition(InvMaterialRequisition invMaterialRequisition) {
        if(ConstantsEms.CHECK_STATUS.equals(invMaterialRequisition.getHandleStatus())){
            judgeNull(invMaterialRequisition);
        }
        setConfirmInfo(invMaterialRequisition);
        int row=invMaterialRequisitionMapper.updateAllById(invMaterialRequisition);
        if (row > 0) {
            //插入日志
            String bussiness = invMaterialRequisitionMapper.selectById(invMaterialRequisition.getMaterialRequisitionSid()).getHandleStatus().equals(ConstantsEms.SAVA_STATUS) ? "编辑" : "变更";
            MongodbUtil.insertUserLog(invMaterialRequisition.getMaterialRequisitionSid(), bussiness, TITLE);
        }
        //领退料单-明细对象
        List<InvMaterialRequisitionItem> invMaterialRequisitionItemList = invMaterialRequisition.getInvMaterialRequisitionItemList();
        if (CollectionUtils.isNotEmpty(invMaterialRequisitionItemList)) {
            List<InvMaterialRequisitionItem> purPurchasePriceItems = invMaterialRequisitionItemMapper.selectList(new QueryWrapper<InvMaterialRequisitionItem>().lambda()
                    .eq(InvMaterialRequisitionItem::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid())
            );
            if(CollectionUtil.isEmpty(purPurchasePriceItems)){
                purPurchasePriceItems=new ArrayList<InvMaterialRequisitionItem>();
            }
            List<Long> longs = purPurchasePriceItems.stream().map(li -> li.getMaterialRequisitionItemSid()).collect(Collectors.toList());
            List<Long> longsNow = invMaterialRequisitionItemList.stream().map(li -> li.getMaterialRequisitionItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<InvMaterialRequisitionItem> reduceList = invMaterialRequisitionItemMapper.selectList(new QueryWrapper<InvMaterialRequisitionItem>().lambda()
                        .in(InvMaterialRequisitionItem::getMaterialRequisitionItemSid, reduce)
                );
                invMaterialRequisitionItemMapper.deleteBatchIds(reduce);
            }
            //修改明细
            List<InvMaterialRequisitionItem> exitItem = invMaterialRequisitionItemList.stream().filter(li -> li.getMaterialRequisitionItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    // 设置仓库库位CODE
                    li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                    li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                    invMaterialRequisitionItemMapper.updateById(li);
                });
            }
            //新增明细
            List<InvMaterialRequisitionItem> nullItem = invMaterialRequisitionItemList.stream().filter(li -> li.getMaterialRequisitionItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                int max =0;
                if(CollectionUtil.isNotEmpty(purPurchasePriceItems)){
                    max = purPurchasePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                }
                for (int i = 0; i < nullItem.size(); i++) {
                    // 设置仓库库位CODE
                    nullItem.get(i).setStorehouseCode(setStorehouseCode(nullItem.get(i).getStorehouseSid()));
                    nullItem.get(i).setStorehouseLocationCode(setStorehouseLocationCode(nullItem.get(i).getStorehouseLocationSid()));
                    String documentType = invMaterialRequisition.getDocumentType();
                    if(DocCategory.REQUESTION_RU.getCode().equals(documentType)){
                        nullItem.get(i).setInOutStockStatus("WRK");
                    }else{
                        nullItem.get(i).setInOutStockStatus("WCK");
                    }
                    int maxItem=max+i+1;
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setMaterialRequisitionSid(invMaterialRequisition.getMaterialRequisitionSid());
                    invMaterialRequisitionItemMapper.insert(nullItem.get(i));
                }
            }
        }else{
            invMaterialRequisitionItemMapper.delete(new QueryWrapper<InvMaterialRequisitionItem>().lambda()
            .eq(InvMaterialRequisitionItem::getMaterialRequisitionSid,invMaterialRequisition.getMaterialRequisitionSid())
            );
        }
        //领退料单-附件对象
        List<InvMaterialRequisitionAttachment> invMaterialRequisitionAttachmentList =
                invMaterialRequisition.getInvMaterialRequisitionAttachmentList();
        addInvMaterialRequisitionAttachment(invMaterialRequisition, invMaterialRequisitionAttachmentList);
        if (!ConstantsEms.SAVA_STATUS.equals(invMaterialRequisition.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(invMaterialRequisition);
        }
        return 1;
    }

    /**
     * 批量删除领退料单
     *
     * @param materialRequisitionSids 需要删除的领退料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvMaterialRequisitionByIds(Long[] materialRequisitionSids) {
        //删除领退料单
        invMaterialRequisitionMapper.deleteInvMaterialRequisitionByIds(materialRequisitionSids);
        //删除领退料单明细
        invMaterialRequisitionItemMapper.deleteInvMaterialRequisitionItemByIds(materialRequisitionSids);
        //删除领退料单附件
        invMaterialRequisitionAttachmentMapper.deleteInvMaterialRequisitionAttachmentByIds(materialRequisitionSids);
        for (Long materialRequisitionSid : materialRequisitionSids) {
            InvMaterialRequisition invMaterialRequisition = new InvMaterialRequisition();
            invMaterialRequisition.setMaterialRequisitionSid(materialRequisitionSid);
            //校验是否存在待办
            checkTodoExist(invMaterialRequisition);
            //插入日志
            MongodbUtil.insertUserLog(materialRequisitionSid, BusinessType.DELETE.getValue(), TITLE);
        }
        return materialRequisitionSids.length;
    }
    /**
     * 关闭领退料单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int close(Long[] materialRequisitionSids){
        int row = invMaterialRequisitionMapper.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                .in(InvMaterialRequisition::getMaterialRequisitionSid, materialRequisitionSids)
                .set(InvMaterialRequisition::getHandleStatus, HandleStatus.CLOSED.getCode())
        );
        return row;
    }
    /**
     * 领退料单确认
     */
    @Override
    public int confirm(InvMaterialRequisition invMaterialRequisition) {
        //领退料单sids
        Long[] materialRequisitionSids = invMaterialRequisition.getMaterialRequisitionSids();
        for (int i=0;i<materialRequisitionSids.length;i++){
            InvMaterialRequisition invMaterial= selectInvMaterialRequisitionById(materialRequisitionSids[i]);
            if(DocCategory.REQUESTION_CHK.getCode().equals(invMaterial.getDocumentType())){
                InvMaterialRequisitionReportRequest request = new InvMaterialRequisitionReportRequest();
                request.setMaterialRequisitionCode(invMaterial.getMaterialRequisitionCode());
                request.setDocumentTypeList(new String[]{DocCategory.REQUESTION_CHK.getCode()});
                List<InvMaterialRequisitionReportResponse> list = invMaterialRequisitionItemMapper.reportInvMaterialRequisition(request);
                List<InvMaterialRequisitionItem> invMaterialRequisitionItems = BeanCopyUtils.copyListProperties(list, InvMaterialRequisitionItem::new);
                createInv(invMaterialRequisitionItems);
            }
            judgeNull(invMaterial);
        }
        for (Long materialRequisitionSid : materialRequisitionSids) {
            InvMaterialRequisition invMaterial = new InvMaterialRequisition();
            invMaterial.setMaterialRequisitionSid(materialRequisitionSid);
            //校验是否存在待办
            checkTodoExist(invMaterial);
            //插入日志
//            MongodbUtil.insertUserLog(materialRequisitionSid, BusinessType.CHECK.getValue(),TITLE);
        }
        int row = invMaterialRequisitionMapper.update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
                .in(InvMaterialRequisition::getMaterialRequisitionSid, materialRequisitionSids)
                .set(InvMaterialRequisition::getHandleStatus, HandleStatus.CONFIRMED.getCode())
                .set(InvMaterialRequisition::getConfirmDate, new Date())
                .set(InvMaterialRequisition::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
        );
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
            InvMaterialRequisition invMaterial= selectInvMaterialRequisitionById(id);
            List<InvMaterialRequisitionItem> list = invMaterial.getInvMaterialRequisitionItemList();
            if(CollectionUtils.isEmpty(list)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不允许为空");
                msgList.add(errMsgResponse);
            }else{
                List<InvMaterialRequisitionItem> noteItems = list.stream().filter(item -> item.getQuantity() == null).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(noteItems)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setMsg("明细行的数量不允许为空");
                    msgList.add(errMsgResponse);
                }else{
                    if(DocCategory.REQUESTION_CHK.getCode().equals(invMaterial.getDocumentType())){
                        Map<Long, List<InvMaterialRequisitionItem>> listMap = list.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
                        listMap.keySet().stream().forEach(l->{
                            List<InvMaterialRequisitionItem> items = listMap.get(l);
                            if(items.size()==1){
                                //商品条码不重复情况下
                                list.forEach(m->{
                                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                    invInventoryLocation.setBarcodeSid(m.getBarcodeSid())
                                            .setStorehouseSid(invMaterial.getStorehouseSid())
                                            .setSpecialStock(invMaterial.getSpecialStock())
                                            .setCustomerSid(invMaterial.getCustomerSid())
                                            .setVendorSid(invMaterial.getVendorSid())
                                            .setStorehouseLocationSid(invMaterial.getStorehouseLocationSid());
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
                                InvMaterialRequisitionItem note = items.get(0);
                                InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                                invInventoryLocation.setBarcodeSid(note.getBarcodeSid())
                                        .setStorehouseSid(invMaterial.getStorehouseSid())
                                        .setSpecialStock(invMaterial.getSpecialStock())
                                        .setCustomerSid(invMaterial.getCustomerSid())
                                        .setVendorSid(invMaterial.getVendorSid())
                                        .setStorehouseLocationSid(invMaterial.getStorehouseLocationSid());
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
    //明细报表生成库存预留
    @Override
    public int create(List<Long> sids){
        InvMaterialRequisitionReportRequest request = new InvMaterialRequisitionReportRequest();
        request.setMaterialRequisitionItemSidList(sids);
        request.setDocumentTypeList(new String[]{DocCategory.REQUESTION_CHK.getCode()});
        invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
        .in(InvReserveInventory::getBusinessOrderItemSid,sids)
        );
        List<InvMaterialRequisitionReportResponse> list = invMaterialRequisitionItemMapper.reportInvMaterialRequisition(request);
        if(CollectionUtil.isNotEmpty(list)){
            List<InvMaterialRequisitionItem> invMaterialRequisitionItems = BeanCopyUtils.copyListProperties(list, InvMaterialRequisitionItem::new);
            Map<Long, List<InvMaterialRequisitionItem>> listMap = invMaterialRequisitionItems.stream().collect(Collectors.groupingBy(v -> v.getMaterialRequisitionSid()));
            listMap.keySet().forEach(l->{
                List<InvMaterialRequisitionItem> items = listMap.get(l);
                items.forEach(li->{
                    List<InvInventoryDocumentItem> bardcodeItems = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>().lambda()
                            .eq(InvInventoryDocumentItem::getReferDocumentItemSid,li.getMaterialRequisitionItemSid()));
                    BigDecimal sumCode=BigDecimal.ZERO;
                    if(CollectionUtil.isNotEmpty(bardcodeItems)){
                        sumCode = bardcodeItems.stream().map(o -> o.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    li.setQuantity(li.getQuantity().subtract(sumCode));
                });
                createInv(items);
            });
        }
        return 1;
    }
    //冲销
    @Override
    public int invCx(InvInventoryDocument invInventoryDocument,List<InvInventoryDocumentItem> list){
        List<InvMaterialRequisitionItem> invMaterialRequisitionItems = new ArrayList<>();
        list.forEach(li->{
            InvMaterialRequisitionItem invMaterialRequisitionItem = new InvMaterialRequisitionItem();
            InvReserveInventory invReserveInventory = invReserveInventoryMapper.selectOne(new QueryWrapper<InvReserveInventory>().lambda()
                    .eq(InvReserveInventory::getBusinessOrderItemSid, li.getReferDocumentItemSid())
            );
            BigDecimal quantity = invMaterialRequisitionItemMapper.getQuantity(li.getInventoryTransferItemSid());
            invMaterialRequisitionItem.setBarcodeSid(li.getBarcodeSid())
                    .setSpecialStock(invInventoryDocument.getSpecialStock())
                    .setVendorSid(invInventoryDocument.getVendorSid())
                    .setCustomerSid(invInventoryDocument.getCustomerSid())
                    .setStorehouseSid(invInventoryDocument.getStorehouseSid())
                    .setQuantity(quantity)
                    .setStorehouseLocationSid(invInventoryDocument.getDestStorehouseLocationSid());
            invMaterialRequisitionItems.add(invMaterialRequisitionItem);
            invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
            .eq(InvReserveInventory::getBusinessOrderItemSid,li.getReferDocumentItemSid())
            );
        });
        createInv(invMaterialRequisitionItems);
        return 1;
    }

    //明细报表释放预留库存
    @Override
    public int reportFreeInv(List<Long> sids){
        int row = invReserveInventoryMapper.delete(new QueryWrapper<InvReserveInventory>().lambda()
                .in(InvReserveInventory::getBusinessOrderItemSid, sids)
        );
        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                .in(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,sids)
                .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
        );
        return row;
    }

    //生成预留库存
    public  void createInv(List<InvMaterialRequisitionItem> itemList){
        //改变预留状态
        Map<Long, List<InvMaterialRequisitionItem>> listMap = itemList.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
        listMap.keySet().stream().forEach(l->{
            List<InvMaterialRequisitionItem> items = listMap.get(l);
            if(items.size()==1){
                //商品条码不重复情况下
                items.forEach(m->{
                    InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                     BeanCopyUtils.copyProperties(m,invInventoryLocation);
                    String specialStock = invInventoryLocation.getSpecialStock();
//                    invInventoryLocation.setBusinessOrderSid(m.getMaterialRequisitionSid());
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
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,m.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                        );
                    }else if(m.getQuantity().compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1){
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,m.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                        );
                        m.setQuantity(location.getAbleQuantity());
                    }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1){
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,m.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                        );
                        m.setQuantity(BigDecimal.ZERO);
                    }
                });
            }else{
                BigDecimal sum = items.stream().map(h -> h.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                InvMaterialRequisitionItem noteSignle = items.get(0);
                InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                BeanCopyUtils.copyProperties(noteSignle,invInventoryLocation);
                String specialStock = invInventoryLocation.getSpecialStock();
//                invInventoryLocation.setBusinessOrderSid(noteSignle.getMaterialRequisitionSid());
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
                            invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                    .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,items.get(j).getMaterialRequisitionItemSid())
                                    .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                            );
                        }else if(comsum.compareTo(location.getAbleQuantity())==1&&location.getAbleQuantity().compareTo(BigDecimal.ZERO)==1&&location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getQuantity()))).compareTo(BigDecimal.ZERO)==1){
                            invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                    .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,items.get(j).getMaterialRequisitionItemSid())
                                    .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_BF)
                            );
                            items.get(j).setQuantity(location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getQuantity()))));

                        }else if(location.getAbleQuantity().compareTo(BigDecimal.ZERO)!=1||location.getAbleQuantity().subtract((comsum.subtract(items.get(j).getQuantity()))).compareTo(BigDecimal.ZERO)!=1){
                            invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                    .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,items.get(j).getMaterialRequisitionItemSid())
                                    .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_WY)
                            );
                            items.get(j).setQuantity(BigDecimal.ZERO);
                        }
                    }
                }else{
                    //全部预留
                    items.forEach(h->{
                        invMaterialRequisitionItemMapper.update(new InvMaterialRequisitionItem(),new UpdateWrapper<InvMaterialRequisitionItem>().lambda()
                                .eq(InvMaterialRequisitionItem::getMaterialRequisitionItemSid,h.getMaterialRequisitionItemSid())
                                .set(InvMaterialRequisitionItem::getReserveStatus,ConstantsEms.RE_STATUS_QB)
                        );
                    });
                }
            }
        });
        List<InvReserveInventory> invReserveInventories = new ArrayList<>();
        itemList.forEach(li->{
            InvReserveInventory invReserveInventory = new InvReserveInventory();
            invReserveInventory.setBarcodeSid(li.getBarcodeSid())
                    .setMaterialRequisitionCode(li.getMaterialRequisitionCode())
                    .setMaterialRequisitionItemNum(Long.valueOf(li.getItemNum()))
                    .setMaterialRequisitionItemSid(li.getMaterialRequisitionItemSid())
                    .setMaterialRequisitionSid(li.getMaterialRequisitionSid())
                    .setBusinessOrderCode(li.getMaterialRequisitionCode())
                    .setBusinessOrderItemNum(Long.valueOf(li.getItemNum()))
                    .setBusinessOrderSid(li.getMaterialRequisitionSid())
                    .setBusinessOrderItemSid(li.getMaterialRequisitionItemSid())
                    .setMaterialSid(li.getMaterialSid())
                    .setReserveType("LTD")
                    .setSku1Sid(li.getSku1Sid())
                    .setSku2Sid(li.getSku2Sid())
                    .setStorehouseSid(li.getStorehouseSid())
                    .setSpecialStock(li.getSpecialStock())
                    .setCustomerSid(li.getCustomerSid())
                    .setVendorSid(li.getVendorSid())
                    .setStorehouseLocationSid(li.getStorehouseLocationSid())
                    .setQuantity(li.getQuantity());
            invReserveInventories.add(invReserveInventory);
        });
        //生成库存预留
        invReserveInventoryMapper.inserts(invReserveInventories);
    }
    /**
     * 领退料单变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(InvMaterialRequisition invMaterialRequisition) {
        Long materialRequisitionSid = invMaterialRequisition.getMaterialRequisitionSid();
        InvMaterialRequisition materialRequisition = invMaterialRequisitionMapper.selectInvMaterialRequisitionById(materialRequisitionSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(materialRequisition.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        List<InvMaterialRequisitionItem> invMaterialRequisitionItemList = invMaterialRequisition.getInvMaterialRequisitionItemList();
        if(CollectionUtils.isEmpty(invMaterialRequisitionItemList)){
            throw new BaseException("确认时，明细行不允许为空");
        }
        setConfirmInfo(invMaterialRequisition);
        invMaterialRequisitionMapper.updateAllById(invMaterialRequisition);
        MongodbUtil.insertUserLog(materialRequisitionSid, BusinessType.CHANGE.getValue(),TITLE);
        //领退料单-明细对象
        if (CollectionUtils.isNotEmpty(invMaterialRequisitionItemList)) {
            invMaterialRequisitionItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvMaterialRequisitionItem(invMaterialRequisition, invMaterialRequisitionItemList);
        }
        //领退料单-附件对象
        List<InvMaterialRequisitionAttachment> invMaterialRequisitionAttachmentList =
                invMaterialRequisition.getInvMaterialRequisitionAttachmentList();
        if (CollectionUtils.isNotEmpty(invMaterialRequisitionAttachmentList)) {
            invMaterialRequisitionAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvMaterialRequisitionAttachment(invMaterialRequisition, invMaterialRequisitionAttachmentList);
        }
        //领退料单-合作伙伴对象
        //TODO
        return 1;
    }


    /**
     * 导入领退料单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        int row = 0;
        Long basStorehouseSid=null;
        String basStorehouseCode=null;
        Long StorehouseLocationSid=null;
        String storehouseLocationCode=null;
        Long vendorSid=null;
        Long customerSid=null;
        Long manufactureOrderSid=null;
        Long manufactureOrderCode=null;
        Long workCenterSid=null;
        String movementType=null;
        Long companySid=null;
        Date accountDate=null;
        Date demandDate=null;
        String unitBase=null;
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
            InvMaterialRequisition invMaterialRequisition = new InvMaterialRequisition();
            List<InvMaterialRequisitionItem> invMaterialRequisitionItemList = new ArrayList<>();
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
                Long departmentSid=null;
                String inOutStockStatus=null;
                String materialRequisitionDocCode=null;
                String materialRequisitionBuCode=null;
                BasMaterialBarcode basMaterialBarcode=null;
                BigDecimal quantity=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                      //  throw new BaseException("单据类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("单据类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConDocTypeMaterialRequisition conDocTypeMaterialRequisition = conDocTypeMaterialRequisitionMapper.selectOne(new QueryWrapper<ConDocTypeMaterialRequisition>()
                                .lambda()
                                .eq(ConDocTypeMaterialRequisition::getName, objects.get(0).toString())
                        );
                        if(conDocTypeMaterialRequisition==null){
                           // throw new BaseException("单据类型，配置错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("单据类型，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.ENABLE_STATUS.equals(conDocTypeMaterialRequisition.getStatus())||!ConstantsEms.CHECK_STATUS.equals(conDocTypeMaterialRequisition.getHandleStatus())){
                                //throw new BaseException("单据类型，必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("单据类型，必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                materialRequisitionDocCode=conDocTypeMaterialRequisition.getCode();
                            }
                            if("MRR".equals(conDocTypeMaterialRequisition.getCode())){
                                inOutStockStatus="WRK";
                            }else{
                                inOutStockStatus="WCK";
                            }
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                       // throw new BaseException("业务类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("业务类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConBuTypeMaterialRequisition conBuTypeMaterialRequisition = conBuTypeMaterialRequisitionMapper.selectOne(new QueryWrapper<ConBuTypeMaterialRequisition>().lambda()
                                .eq(ConBuTypeMaterialRequisition::getName, objects.get(1).toString())
                        );
                        if(conBuTypeMaterialRequisition==null){
                            //throw new BaseException("业务类型，配置错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("业务类型，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.ENABLE_STATUS.equals(conBuTypeMaterialRequisition.getStatus())||!ConstantsEms.CHECK_STATUS.equals(conBuTypeMaterialRequisition.getHandleStatus())){
                               // throw new BaseException("业务类型，必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("业务类型，必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                materialRequisitionBuCode=conBuTypeMaterialRequisition.getCode();
                            }
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                       // throw new BaseException("作业类型名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                                .eq(ConMovementType::getName, objects.get(2).toString())
                        );
                        if(conMovementType==null){
                           // throw new BaseException("作业类型，配置错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.ENABLE_STATUS.equals(conMovementType.getStatus())||!ConstantsEms.CHECK_STATUS.equals(conMovementType.getHandleStatus())){
                             //   throw new BaseException("作业类型，必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型，必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            movementType=conMovementType.getCode();
                            String[] codes = {"MR", "MRR"};
                            List<ConInOutStockDocCategory> moveTypeList = conInOutStockDocCategoryMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                                    .in(ConInOutStockDocCategory::getInvDocCategoryCode, codes)
                                    .eq(ConInOutStockDocCategory::getMovementTypeCode, conMovementType.getCode())
                            );
                            if(CollectionUtil.isEmpty(moveTypeList)){
                               // throw new BaseException("该作业类型，不属于领退料单，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("该作业类型，不属于领退料单，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(materialRequisitionDocCode!=null){
                                ConInOutStockDocCategory conInOutStockDocCategory = conInOutStockDocCategoryMapper.selectOne(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                                        .eq(ConInOutStockDocCategory::getMovementTypeCode, conMovementType.getCode())
                                        .eq(ConInOutStockDocCategory::getInvDocCategoryCode,materialRequisitionDocCode)
                                );
                                if(conInOutStockDocCategory==null){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("作业类型和单据类型不匹配，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    matchOne=true;
                                }
                            }
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                      //  throw new BaseException("单据日期，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("开单日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(3).toString());
                        if(!validDate){
                            // throw new BaseException("单据日期，格式错误，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("开单日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String account = objects.get(3).toString();
                             accountDate = DateUtil.parse(account);
                        }
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                      //  throw new BaseException("仓库编码，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(4).toString())
                        );
                        if (basStorehouse == null) {
                            //throw new BaseException("没有编码为" + objects.get(4).toString() + "的仓库，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(4).toString() + "的仓库，导入失败");
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
                    if (objects.get(5) == null || objects.get(5) == "") {
                        if(DocCategory.REQUESTION_CHK.getCode().equals(materialRequisitionDocCode)){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("库位，不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(5) != null && objects.get(5) != "") {
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(5).toString())
                        );
                        if (basStorehouseLocation == null) {
                          //  throw new BaseException("编码为" + objects.get(4).toString() + "的仓库下没有"+objects.get(5).toString()+"的库位，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(4).toString() + "的仓库下没有"+objects.get(5).toString()+"的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                              //  throw new BaseException("库位必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            StorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                            storehouseLocationCode = basStorehouseLocation.getLocationCode();
                        }
                    }
                    if(objects.get(6) == null || objects.get(6) == ""){
                       // throw new BaseException("领料人账号，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("领料人账号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        R<LoginUser> userInfo = remoteUserService.getUserInfo(objects.get(6).toString());
                        if(userInfo.getData()==null){
                           // throw new BaseException("没有账号为"+objects.get(6).toString()+"的领料人,导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有账号为"+objects.get(6).toString()+"的领料人，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String status = userInfo.getData().getSysUser().getStatus();
                            if(!"0".equals(status)){
                               // throw new BaseException("领料人账号必须是启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("领料人账号必须是启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if(objects.get(7) == null || objects.get(7) == ""){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("公司简称，不能为空,导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                                .eq(BasCompany::getShortName, objects.get(7).toString())
                        );
                        if(company==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("公司简称为" + objects.get(7).toString() + "没有对应的公司，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(company.getStatus())){
                                //  throw new BaseException("库位必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("公司简称必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                companySid=company.getCompanySid();
                            }
                        }
                    }

                    if(objects.get(8) == null || objects.get(8) == ""){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("需求日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(8).toString());
                        if(!validDate){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("需求日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            demandDate=DateUtil.parse(objects.get(8).toString());
                        }
                    }


                    if("FSC".equals(materialRequisitionBuCode)){
                        if(objects.get(9) == null || objects.get(9) == ""){
                           // throw new BaseException("业务类型为非生产性时，需求部门不能为空，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("业务类型为非生产性时，需求部门不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(objects.get(13) != null && objects.get(1) != ""){
                           // throw new BaseException("业务类型为非生产性时，生产订单号不允许填值，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("业务类型为非生产性时，生产订单号必须为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }else if("SC".equals(materialRequisitionBuCode)){
                        if(objects.get(13) == null || objects.get(13) == ""){
                           // throw new BaseException("业务类型为生产性时，生产订单号不能为空，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("业务类型为生产性时，生产订单号不能为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(objects.get(9) != null && objects.get(9) != ""){
                           // throw new BaseException("业务类型为生产性时，需求部门不允许填值，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("业务类型为生产性时，需求部门必须为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(9) != null && objects.get(9) != ""){
                        if(companySid!=null){
                            BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                    .eq(BasDepartment::getDepartmentName, objects.get(9).toString())
                                    .eq(BasDepartment::getCompanySid, companySid)
                            );
                            if (basDepartment == null) {
                                // throw new BaseException("没有名称为" + objects.get(9).toString() + "的需求部门，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("简称为"+objects.get(7).toString()+"的公司下，没有名称为" + objects.get(9).toString() + "的需求部门，导入失败");
                                msgList.add(errMsgResponse);
                            } else {
                                if(!ConstantsEms.CHECK_STATUS.equals(basDepartment.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basDepartment.getStatus())){
                                    // throw new BaseException("需求部门必须是确认且已启用状态，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("需求部门必须是确认且已启用状态，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                departmentSid =basDepartment.getDepartmentSid();
                            }
                        }

                    }
                    if(objects.get(10) != null && objects.get(10) != ""){
                        String sep = objects.get(10).toString();
                        if(objects.get(10) != null && objects.get(10) != ""){
                            String special = specialMaps.get(objects.get(10).toString());
                            if(special==null){
                               // throw new BaseException("特殊库存数据格式错误，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("特殊库存，配置错误，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                specialStock=special;
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
                            }
                        }
                    }
                    if(matchOne){
                        //-常规物料(免费)
                        if("SR07".equals(movementType)||"SC07".equals(movementType)){
                            if(specialStock!=null){
                                // throw new BaseException("作业类型为"+objects.get(2).toString()+"时，特殊库存必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(2).toString()+"时，特殊库存必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR071".equals(movementType)||"SC071".equals(movementType)){
                            if(!ConstantsEms.CUS_RA.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(2).toString()+"时，特殊库存必须为客供料，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(2).toString()+"时，特殊库存必须为客供料，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }else if("SR072".equals(movementType)||"SC072".equals(movementType)){
                            if(!ConstantsEms.VEN_CU.equals(specialStock)){
                                // throw new BaseException("作业类型为"+objects.get(2).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型为"+objects.get(2).toString()+"时，特殊库存必须为供应商寄售，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                matchtwo=true;
                            }
                        }
                    }
                    if (objects.get(11) != "" &&objects.get(11) != null) {
                        String vendorCode = objects.get(11).toString();
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
                    if (objects.get(12) != ""&&objects.get(12) != null) {
                        String customerCode = objects.get(12).toString();
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
                               // throw new BaseException("简称为" + customerCode + "没有对应的客户，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }
                    if(matchtwo){
                        if(objects.get(10)!=null&&objects.get(10)!=""){
                            if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(10).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(10).toString()))){
                                if(objects.get(11)==null||objects.get(11)==""){
                                    //   throw new BaseException("供应商简称不能为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                if(objects.get(12)!=null&&objects.get(12)!=""){
                                    //throw new BaseException("客户简称必须为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(10).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(10).toString()))){
                                if(objects.get(12)==null||objects.get(12)==""){
                                    // throw new BaseException("客户简称不能为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                                if(objects.get(11)!=null&&objects.get(11)!=""){
                                    //throw new BaseException("供应商简称必须为空，导入失败");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                        if(objects.get(10)==null ||objects.get(10)==""){
                            if(objects.get(11)!=null&&objects.get(11)!=""){
                                //throw new BaseException("供应商简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(12)!=null&&objects.get(12)!=""){
                                // throw new BaseException("客户简称必须为空，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if(objects.get(13) != ""&&objects.get(13) != null){
                        ManManufactureOrder manManufactureOrder = manManufactureOrderMapper.selectOne(new QueryWrapper<ManManufactureOrder>().lambda()
                                .eq(ManManufactureOrder::getManufactureOrderCode, objects.get(13).toString())
                        );
                        if (manManufactureOrder == null) {
                          //  throw new BaseException("编码为" + objects.get(13).toString() + "没有对应的生产订单，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("编码为" + objects.get(13).toString() + "没有对应的生产订单，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(manManufactureOrder.getHandleStatus())){
                                //throw new BaseException("生产订单号必须是确认状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("生产订单号必须是确认状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            manufactureOrderSid = manManufactureOrder.getManufactureOrderSid();
                            manufactureOrderCode=Long.valueOf(manManufactureOrder.getManufactureOrderCode());
                        }
                    }
                    if(objects.get(14) != ""&&objects.get(14) != null){
                        ManWorkCenter manWorkCenter = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                                .eq(ManWorkCenter::getWorkCenterName, objects.get(14).toString())
                        );
                        if (manWorkCenter == null) {
                            //throw new BaseException("名称为" + objects.get(14).toString() + "没有对应的工作中心，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(14).toString() + "没有对应的班组，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(manWorkCenter.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(manWorkCenter.getStatus())){
                                //throw new BaseException("工作中心号必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("班组必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            workCenterSid = manWorkCenter.getWorkCenterSid();
                        }
                    }
                    invMaterialRequisition
                            .setMovementType(movementType)
                            .setManufactureOrderSid(manufactureOrderSid)
                            .setDocumentType(materialRequisitionDocCode)
                            .setBusinessType(materialRequisitionBuCode)
                            .setMaterialReceiver((objects.get(6)==""||objects.get(6)==null)?null:objects.get(6).toString())
                            .setRequireDepartment(departmentSid)
                            .setDemandDate(demandDate)
                            .setWorkCenterSid(workCenterSid)
                            .setStorehouseSid(basStorehouseSid)
                            .setCompanySid(companySid)
                            .setManufactureOrderCode(manufactureOrderCode)
                            .setManufactureOrderSid(manufactureOrderSid)
                            .setInOutStockStatus(inOutStockStatus)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setDocumentDate(accountDate)
                            .setCustomerSid(customerSid)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSpecialStock((objects.get(10)==""||objects.get(10)==null)?null:specialMaps.get(objects.get(10).toString()))
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(15)==""||objects.get(15)==null)?null:objects.get(15).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                if (objects.get(0) == null || objects.get(0) == "") {
                   // throw new BaseException("第"+num+"行，物料/商品编码不可为空，导入失败");
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
                            //throw new BaseException("第"+num+"行，物料/商品必须是确认且已启用状态，导入失败");
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
                       // throw new BaseException("第"+num+"行，没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
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
                    //throw new BaseException("第"+num+"行,数量 不可为空，导入失败");
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
                InvMaterialRequisitionItem invMaterialRequisitionItem = new InvMaterialRequisitionItem();
                invMaterialRequisitionItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code((objects.get(1)==""||objects.get(1)==null)?null:objects.get(1).toString())
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setBarcode(barcode)
                        .setMaterialSid(materialSid)
                        .setMaterialCode((objects.get(0)==""||objects.get(0)==null)?null:objects.get(0).toString())
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setStorehouseCode(basStorehouseCode)
                        .setStorehouseLocationCode(storehouseLocationCode)
                        .setQuantity(quantity)
                        .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                invMaterialRequisitionItemList.add(invMaterialRequisitionItem);
            }
            HashSet<Long> longs = new HashSet<>(bardCodeList);
            if(longs.size()!=bardCodeList.size()){
                for (int i=0;i<bardCodeList.size();i++){
                    for (int j=i+1;j<bardCodeList.size();j++){
                        if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                            int nu=j+1+5;
                          //  throw new BaseException("第"+nu+"行，商品条码重复，请核实");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(nu);
                            errMsgResponse.setMsg("商品条码重复，请核实");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
               return  AjaxResult.error("报错信息",msgList);
            }
            invMaterialRequisition.setInvMaterialRequisitionItemList(invMaterialRequisitionItemList);
            insertInvMaterialRequisition(invMaterialRequisition);

        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        return  AjaxResult.success(1);
    }

    /**
     * 领退料单-明细对象
     */
    private void addInvGoodReceiptNoteItem(InvMaterialRequisition invMaterialRequisition, List<InvMaterialRequisitionItem> invMaterialRequisitionItemList) {
        invMaterialRequisitionItemMapper.delete(new UpdateWrapper<InvMaterialRequisitionItem>()
                .lambda()
                .eq(InvMaterialRequisitionItem::getMaterialRequisitionSid, invMaterialRequisition.getMaterialRequisitionSid())
        );
        invMaterialRequisitionItemList.forEach(o -> {
            o.setMaterialRequisitionSid(invMaterialRequisition.getMaterialRequisitionSid());
            invMaterialRequisitionItemMapper.insert(o);
        });
    }

    /**
     * 生成PDF
     * @param invMaterialRequisition
     */
    @Override
	public AjaxResult generatePDF(InvMaterialRequisition invMaterialRequisition){
        try{
            InvMaterialRequisition materialRequisition = selectInvMaterialRequisitionById(invMaterialRequisition.getMaterialRequisitionSid());
            materialRequisition.setOutputPath(invMaterialRequisition.getOutputPath());
            Document document = new Document(PageSize.A4);
            File pdfFile = new File(CommonUtil.getDeskTopPath() +"\\"+TITLE+materialRequisition.getMaterialRequisitionCode()+".pdf");
            pdfFile.createNewFile();
            System.out.println("文件生成路径：" + pdfFile.getPath());
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            lingtuiliao(document,materialRequisition);
            document.close();
            return AjaxResult.success("文件已生成到桌面，请查看！");
        }catch (Exception e){

        }
        return AjaxResult.error("生成PDF文件失败！");
    }

    /**
     * 填充文档内容
     * @param document
     */
    public void lingtuiliao(Document document,InvMaterialRequisition materialRequisition) {
        List<InvMaterialRequisitionItem> itemList = materialRequisition.getInvMaterialRequisitionItemList();
        Long code = materialRequisition.getMaterialRequisitionCode();
        try{
            /**                   title结构                 */
            PdfPTable titleTablie = createTable(3);
            //生成二维码
            QCUtil.generateQRCodeImage(code.toString(),50,50,materialRequisition.outputPath+"\\qc_"+code+".png");
            Image qcImage = Image.getInstance(materialRequisition.outputPath+"\\qc_"+code+".png");
            qcImage.setAlignment(Image.ALIGN_LEFT);
            PdfPCell qcCell = new PdfPCell();
            qcCell.setBorderWidth(0);
            qcCell.setVerticalAlignment(Element.ALIGN_CENTER);
            qcCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qcCell.setImage(qcImage);
            titleTablie.addCell(qcCell);
            //标题
            PdfPCell titleCell = PdfUtil.createCellSetBorderWidth("领料/退料单", PdfUtil.titlefont,0);
            titleCell.setPaddingTop(70f);
            titleTablie.addCell(titleCell);
            // 生成条形码
//            BarcodeUtil.generateFile(code.toString(),materialRequisition.outputPath+"\\barcode_"+code+".png");
            QCUtil.generateCode128Image(code.toString(),230,55,materialRequisition.outputPath+"\\barcode_"+code+".png");
            Image barcodeImg = Image.getInstance(materialRequisition.outputPath+"\\barcode_"+code+".png");
            barcodeImg.setAlignment(Image.ALIGN_LEFT);
            PdfPCell barcodeCell = new PdfPCell();
            barcodeCell.setBorderWidth(0);
            barcodeCell.setVerticalAlignment(Element.ALIGN_CENTER);
            barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            barcodeCell.setImage(barcodeImg);
            barcodeCell.setPaddingTop(65);
            titleTablie.addCell(barcodeCell);

            document.add(titleTablie);
            /**                    title结构END                  */

            /**                    黑线                  */
            Paragraph blackLine = new Paragraph();
            blackLine.add(new Chunk(new LineSeparator()));

            document.add(blackLine);
            /**                    黑线                  */

            /**                    字段展示start                  */
            Paragraph pl1 = createParagraph();
            PdfPTable fieldLine1 = createTable(3);
            PdfPCell line1code = PdfUtil.createCell("领料/退料单号："+code,PdfUtil.keyfont);
            fieldLine1.addCell(line1code);
            String specialVal = materialRequisition.getSpecialStock()==null?"":materialRequisition.getSpecialStock();
            PdfPCell line1special = PdfUtil.createCell("特殊库存："+specialVal,PdfUtil.keyfont);
            fieldLine1.addCell(line1special);
            String nameVal = materialRequisition.getCustomerName()==null?materialRequisition.getVendorName():materialRequisition.getCustomerName();
            if(nameVal==null){
                nameVal = "";
            }
            PdfPCell line1name = PdfUtil.createCell("客户/供应商："+nameVal,PdfUtil.keyfont);
            fieldLine1.addCell(line1name);
            pl1.add(fieldLine1);

            Paragraph pl2 = createParagraph();
            PdfPTable fieldLine2 = createTable(3);
            PdfPCell line2date = PdfUtil.createCell("领料/退料日期："+ materialRequisition.getDocumentDate(),PdfUtil.keyfont);
            fieldLine2.addCell(line2date);
            PdfPCell line2warehouse = PdfUtil.createCell("仓库："+materialRequisition.getStorehouseName(),PdfUtil.keyfont);
            fieldLine2.addCell(line2warehouse);
            PdfPCell line2location = PdfUtil.createCell("库位："+materialRequisition.getLocationName(),PdfUtil.keyfont);
            fieldLine2.addCell(line2location);
            pl2.add(fieldLine2);

            Paragraph pl3 = createParagraph();
            PdfPTable fieldLine3 = createTable(1);
            String remake3 = materialRequisition.getRemark()==null?"":materialRequisition.getRemark();
            PdfPCell line3remake = PdfUtil.createCell("备注："+remake3,PdfUtil.keyfont);
            fieldLine3.addCell(line3remake);
            pl3.add(fieldLine3);

            document.add(pl1);
            document.add(pl2);
            document.add(pl3);
            /**                    字段展示end                  */

            /**                    表格展示start                  */
            //表头
            Paragraph pl4 = createParagraph();
            pl4.setSpacingBefore(3f);
            PdfPTable fieldLine4 = createTable(8);
            PdfPCell line4materialCode = PdfUtil.createCellSetBorderWidth("物料编码",PdfUtil.textfont,1);
            fieldLine4.addCell(line4materialCode);
            PdfPCell line4materialName = PdfUtil.createCellSetBorderWidth("物料名称",PdfUtil.textfont,1);
            fieldLine4.addCell(line4materialName);
            PdfPCell line4sku1Name = PdfUtil.createCellSetBorderWidth("SKU1名称",PdfUtil.textfont,1);
            fieldLine4.addCell(line4sku1Name);
            PdfPCell line4sku2Name = PdfUtil.createCellSetBorderWidth("SKU2名称",PdfUtil.textfont,1);
            fieldLine4.addCell(line4sku2Name);
            PdfPCell line4num = PdfUtil.createCellSetBorderWidth("数量",PdfUtil.textfont,1);
            fieldLine4.addCell(line4num);
            PdfPCell line4standard = PdfUtil.createCellSetBorderWidth("计量单位",PdfUtil.textfont,1);
            fieldLine4.addCell(line4standard);
            PdfPCell line4lineNum = PdfUtil.createCellSetBorderWidth("行号",PdfUtil.textfont,1);
            fieldLine4.addCell(line4lineNum);
            PdfPCell line4remake = PdfUtil.createCellSetBorderWidth("备注",PdfUtil.textfont,1);
            fieldLine4.addCell(line4remake);
            //值
            if(itemList!=null&&itemList.size()>0){
                for(int i = 0;i<itemList.size();i++){
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getMaterialCode(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getMaterialName(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getSku1Name(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getSku2Name(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getQuantity().toString(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getUnitBase(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getItemNum().toString(),PdfUtil.textfont,1));
                    fieldLine4.addCell(PdfUtil.createCellSetBorderWidth(itemList.get(i).getRemark(),PdfUtil.textfont,1));
                }
            }
            pl4.add(fieldLine4);

            document.add(pl4);
            /**                    表格展示end                  */

            /**                    底部签名start                  */
            Paragraph pl5 = createParagraph();
            PdfPTable fieldline5 = createTable(2);
            PdfPCell line5bill = PdfUtil.createCell("开单员(签字)：",PdfUtil.keyfont);
            fieldline5.addCell(line5bill);
            PdfPCell line5operator = PdfUtil.createCell("仓库出库人(签字)：",PdfUtil.keyfont);
            fieldline5.addCell(line5operator);
            pl5.setSpacingBefore(15f);
            pl5.add(fieldline5);

            document.add(pl5);
            /**                    底部签名end                  */
        } catch (Exception e){

        } finally {
            File QC = new File(materialRequisition.outputPath+"\\qc_"+code+".png");
            if(QC.exists()) {
                QC.delete();
            }
            File barcode = new File(materialRequisition.outputPath+"\\barcode_"+code+".png");
            if(barcode.exists()){
                barcode.delete();
            }
        }
    }

    public Paragraph createParagraph(){
        Paragraph paragraph = new Paragraph();
        paragraph.setLeading(20f);
        paragraph.setSpacingBefore(3f);
        return paragraph;
    }

    public PdfPTable createTable(int column){
        PdfPTable table = new PdfPTable(column);
        table.setTotalWidth(PdfUtil.maxWidth);
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100f);
        return table;
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
