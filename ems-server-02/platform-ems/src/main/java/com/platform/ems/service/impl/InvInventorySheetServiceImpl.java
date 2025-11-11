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
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvInventorySheetReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvInventorySheetReportResponse;
import com.platform.ems.enums.DocumentCategory;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.mapper.ConSpecialStockMapper;
import com.platform.ems.service.IInvInventorySheetService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteFlowableService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 盘点单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class InvInventorySheetServiceImpl extends ServiceImpl<InvInventorySheetMapper,InvInventorySheet>  implements IInvInventorySheetService {
    @Autowired
    private InvInventorySheetMapper invInventorySheetMapper;
    @Autowired
    private InvInventorySheetItemMapper invInventorySheetItemMapper;
    @Autowired
    private InvInventorySheetAttachmentMapper invInventorySheetAttachmentMapper;
    @Autowired
    private  InvInventoryDocumentMapper  invInventoryDocumentMapper;
    @Autowired
    private  InvInventoryDocumentItemMapper  invDocumentItemMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private InvStorehouseMaterialMapper invStorehouseMaterialMapper;
    @Autowired
    private InvCusSpecialInventoryMapper invCusSpecialInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private PurRecordVendorConsignServiceImpl  purRecordVendorConsignServiceImpl;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private ConSpecialStockMapper conSpecialStockMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "盘点单";

    /**
     * 查询盘点单
     *
     * @param inventorySheetSid 盘点单ID
     * @return 盘点单
     */
    @Override
    public InvInventorySheet selectInvInventorySheetById(Long inventorySheetSid) {
        InvInventorySheet invInventorySheet = invInventorySheetMapper.selectInvInventorySheetById(inventorySheetSid);
        if (invInventorySheet == null){
            return null;
        }
        //盘点单-明细对象
        List<InvInventorySheetItem> invInventorySheetItemList = invInventorySheetItemMapper.selectInvInventorySheetItemById(inventorySheetSid);
        //盘点单-附件对象
        InvInventorySheetAttachment invInventorySheetAttachment = new InvInventorySheetAttachment();
        invInventorySheetAttachment.setInventorySheetSid(inventorySheetSid);
        List<InvInventorySheetAttachment> invInventorySheetAttachmentList =
                invInventorySheetAttachmentMapper.selectInvInventorySheetAttachmentList(invInventorySheetAttachment);
        List<InvInventorySheetItem> items = sort(invInventorySheetItemList, null);
        invInventorySheet.setInvInventorySheetItemList(items);
        invInventorySheet.setInvInventorySheetAttachmentList(invInventorySheetAttachmentList);
        MongodbUtil.find(invInventorySheet);
        return invInventorySheet;
    }

    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvInventorySheetItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
    }

    /**
     * 查询盘点单列表
     *
     * @param invInventorySheet 盘点单
     * @return 盘点单
     */
    @Override
    public List<InvInventorySheet> selectInvInventorySheetList(InvInventorySheet invInventorySheet) {
        List<String> idList = new ArrayList<>();
        if(Objects.nonNull(invInventorySheet.getApprovalUserId())){
            FlowTaskVo task = new FlowTaskVo();
            task.setUserId(invInventorySheet.getApprovalUserId());
            task.setDefinitionId(FormType.InventorySheet.getCode());
            AjaxResult userTask = flowableService.getUserTaskList(task);
            if(!userTask.get("msg").equals("操作成功")){
                throw new CustomException(userTask.get("msg").toString());
            }
            idList = (List<String>) userTask.get("data");
            if(null==idList||0==idList.size()){
                return new ArrayList<InvInventorySheet>();
            }
            List<Long> sidList = new ArrayList<>();
            sidList = idList.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            invInventorySheet.setItemSidList(sidList);
        }
        List<InvInventorySheet> list = invInventorySheetMapper.selectInvInventorySheetList(invInventorySheet);
        return list;
    }
    /**
     * 复制
     */
    @Override
    public InvInventorySheet getCopy(Long sid){
        InvInventorySheet invInventorySheet = selectInvInventorySheetById(sid);
        invInventorySheet.setInventorySheetSid(null)
                .setInventorySheetCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvInventorySheetItem> list = invInventorySheet.getInvInventorySheetItemList();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setInventorySheetSid(null)
                        .setInventorySheetItemSid(null)
                        .setCreateDate(null)
                        .setCreatorAccount(null);
            });
        }
        return invInventorySheet;
    }

    /**
     * 盘点单明细报表
     * @param invInventorySheet 盘点单
     * @return 结果
     */
    @Override
    public List<InvInventorySheetReportResponse> reportInvInventorySheet(InvInventorySheetReportRequest invInventorySheetReportRequest) {
        List<InvInventorySheetReportResponse> list = invInventorySheetItemMapper.reportInvInventorySheet(invInventorySheetReportRequest);
        list.forEach(item->{
            if(item.getCountQuantity()!=null&&item.getStockQuantity()!=null){
                item.setDivQuantity((item.getCountQuantity().subtract(item.getStockQuantity())).abs());
            }
            if(item.getPrice()!=null&&item.getDivQuantity()!=null){
                item.setDivPrice(item.getPrice().multiply(item.getDivQuantity()));
                item.setDivPrice(new BigDecimal(item.getDivPrice().stripTrailingZeros().toPlainString()));
            }
        });
        return list;
    }

    /**
     * 新增盘点单
     * 需要注意编码重复校验
     * @param invInventorySheet 盘点单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventorySheet(InvInventorySheet invInventorySheet) {
        judge(invInventorySheet);
        if(ConstantsEms.CHECK_STATUS.equals(invInventorySheet.getHandleStatus())){
            judgeNull(invInventorySheet);
        }
        int row;
        //设置确认信息
        setConfirmInfo(invInventorySheet);
        String type = invInventorySheet.getType();
        //盘点单-明细list
        List<InvInventorySheetItem> invInventorySheetItemList = invInventorySheet.getInvInventorySheetItemList();
        //过账
        if(HandleStatus.POSTING.getCode().equals(invInventorySheet.getHandleStatus())){
            invInventorySheet.setCountResultEnterDate(new Date());
            invInventorySheet.setActualCountDate(new Date());
            invInventorySheet.setCountStatus(ConstantsEms.CONUNT_STATUS_R);
            invInventorySheet.setAccountDate(new Date());
            invInventorySheet.setAccountor(ApiThreadLocalUtil.get().getUsername());
            Calendar date = Calendar.getInstance();
            String year = String.valueOf(date.get(Calendar.YEAR));
            invInventorySheet.setYear(year);
            row=invInventorySheetMapper.insert(invInventorySheet);
            judegeDocument(invInventorySheet,invInventorySheetItemList);
        }else{
            invInventorySheet.setCountStatus(ConstantsEms.CONUNT_STATUS_B);
            row=invInventorySheetMapper.insert(invInventorySheet);
        }
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventorySheet.getInventorySheetSid(), BusinessType.INSERT.getValue(),TITLE);
        }
        if (CollectionUtils.isNotEmpty(invInventorySheetItemList)) {
            setItemNum(invInventorySheetItemList);
            addInvInventorySheetItem(invInventorySheet, invInventorySheetItemList);
        }
        //盘点单-附件list
        List<InvInventorySheetAttachment> invInventorySheetAttachmentList = invInventorySheet.getInvInventorySheetAttachmentList();
        if (CollectionUtils.isNotEmpty(invInventorySheetAttachmentList)) {
            addInvInventorySheetAttachment(invInventorySheet, invInventorySheetAttachmentList);
        }
        //待办通知
        InvInventorySheet sheet = invInventorySheetMapper.selectById(invInventorySheet.getInventorySheetSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(invInventorySheet.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(invInventorySheet.getInventorySheetSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("盘点单" + sheet.getInventorySheetCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(sheet.getInventorySheetCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invInventorySheet);
        }
        return row;
    }

    public void setStock(List<InvInventorySheetItem> invInventorySheetItemList){
        invInventorySheetItemList.forEach(item->{
            if(item.getCountQuantity().compareTo(item.getStockQuantity())==0){
                item.setStockCountResult(ConstantsEms.STOCK_P);
            }else if(item.getCountQuantity().compareTo(item.getStockQuantity())==-1){
                item.setStockCountResult(ConstantsEms.STOCK_K);
            }else{
                item.setStockCountResult(ConstantsEms.STOCK_Y);
            }
            invInventorySheetItemMapper.updateById(item);
        });
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(InvInventorySheet invInventorySheet) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invInventorySheet.getInventorySheetSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invInventorySheet.getInventorySheetSid()));
        }
    }
    /**
     * 盘亏 盘盈 盈亏持平
     */
    public void sheetCopy(InvInventorySheet invInventorySheet,List<InvInventorySheetItem> invInventorySheetItemList){
        List<InvInventorySheetItem> sheetLoss = new ArrayList<>();
        List<InvInventorySheetItem> sheetOver = new ArrayList<>();
        List<InvInventorySheetItem> sheetCommon = new ArrayList<>();
        invInventorySheetItemList.forEach(item->{
                    BigDecimal stockQuantity = item.getStockQuantity();
                    BigDecimal countQuantity = item.getCountQuantity();
                    if(stockQuantity.subtract(countQuantity).compareTo(new BigDecimal(0))==-1){
                        //盘盈
                        sheetOver.add(item);
                    }else if(stockQuantity.subtract(countQuantity).compareTo(new BigDecimal(0))==0){
                        // 盈亏持平
                        sheetCommon.add(item);
                    }else{
                        sheetLoss.add(item);
                    }

                }
        );
        if(CollectionUtils.isNotEmpty(sheetLoss)){
            invInventorySheet.setMovementType(ConstantsEms.OVERLINK);
            judegeDocument(invInventorySheet,sheetLoss);
        }
        if(CollectionUtils.isNotEmpty(sheetOver)){
            invInventorySheet.setMovementType(ConstantsEms.PROFIT);
            judegeDocument(invInventorySheet,sheetOver);
        }
        if(CollectionUtils.isNotEmpty(sheetCommon)){
            invInventorySheet.setMovementType(ConstantsEms.COMMON);
            judegeDocument(invInventorySheet,sheetCommon);
        }
    }

    /**
     * 获取过账日期的年月份
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
     * 检验是否需要生成库存凭证
     */
    public void  judegeDocument(InvInventorySheet invInventorySheet,List<InvInventorySheetItem> invInventorySheetItemList ){
        JudegeCus(invInventorySheet,invInventorySheetItemList);
        InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
        ArrayList<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
        BeanCopyUtils.copyProperties(invInventorySheet,invInventoryDocument);
        invInventoryDocument.setReferDocumentSid(invInventorySheet.getInventorySheetSid());
        invInventoryDocument.setReferDocumentCode(String.valueOf(invInventorySheet.getInventorySheetCode()));
        invInventoryDocument.setMovementType(invInventorySheet.getMovementType());
        invInventoryDocument.setDocumentType("CG");
        invInventoryDocument.setHandleStatus(HandleStatus.POSTING.getCode());
        invInventoryDocument.setStorehouseOperator(ApiThreadLocalUtil.get().getUsername());
        invInventoryDocument.setReferDocCategory(DocumentCategory.SHEET.getCode());
        invInventoryDocument.setDocumentCategory(DocumentCategory.SHEET.getCode());
        setYearMonth(invInventoryDocument);
        invInventoryDocumentMapper.insert(invInventoryDocument);
        if(CollectionUtils.isNotEmpty(invInventorySheetItemList)){
            ArrayList<InvInventoryDocumentItem> documentlist = new ArrayList<>();
            invInventorySheetItemList.forEach(o-> {
                ArrayList<InvInventoryDocumentItem> list = new ArrayList<>();
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                BeanCopyUtils.copyProperties(o, invInventoryDocumentItem);
                invInventoryDocumentItem.setReferDocumentSid(invInventorySheet.getInventorySheetSid());
                invInventoryDocumentItem.setReferDocumentCode(String.valueOf(invInventorySheet.getInventorySheetCode()));
                invInventoryDocumentItem.setQuantity(o.getCountQuantity());
                invInventoryDocumentItem.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
                invInventoryDocumentItem.setDestStorehouseSid(invInventoryDocument.getDestStorehouseSid());
                invInventoryDocumentItem.setDestStorehouseLocationSid(invInventoryDocument.getDestStorehouseLocationSid());
                documentlist.add(invInventoryDocumentItem);
                invDocumentItemMapper.insert(invInventoryDocumentItem);
            });
//            invDocumentItemMapper.inserts(documentlist);
        }
        changeInvDocumentLocation(invInventorySheet,invInventorySheetItemList);
    }
    /**
     * 覆盖对应的仓库信息
     */
    public void changeInvDocumentLocation(InvInventorySheet invInventorySheet,List<InvInventorySheetItem> list){
        ArrayList<InvInventorySheetItem> invInventorySheetItems = new ArrayList<>();
        String specialStock = invInventorySheet.getSpecialStock();
        //常规库存
        if (specialStock == null) {
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(o -> {
                    InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getStorehouseLocationSid, o.getStorehouseLocationSid())
                            .eq(InvInventoryLocation::getStorehouseSid, o.getStorehouseSid())
                            .eq(InvInventoryLocation::getBarcodeSid, o.getBarcodeSid()));
                    if (location != null) {
                        InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                        invInventoryLocationMapper.update(invInventoryLocation, new UpdateWrapper<InvInventoryLocation>().lambda()
                                .set(InvInventoryLocation::getUnlimitedQuantity, o.getCountQuantity())
                                .eq(InvInventoryLocation::getStorehouseLocationSid, o.getStorehouseLocationSid())
                                .eq(InvInventoryLocation::getStorehouseSid, o.getStorehouseSid())
                                .eq(InvInventoryLocation::getBarcodeSid, o.getBarcodeSid())
                                .set(InvInventoryLocation::getLatestCountDate, new Date())
                        );
                    }else {
                        createLocation(invInventorySheet,o);
                    }
                });
            }
        }
        //供应商特殊库存
        if (ConstantsEms.VEN_CU.equals(specialStock) || ConstantsEms.VEN_RA.equals(specialStock)) {
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(o->{
                    //判断仓库是否存在对应的库存
                    InvVenSpecialInventory invVenSpecial = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                            .eq(InvVenSpecialInventory::getStorehouseLocationSid, o.getStorehouseLocationSid())
                            .eq(InvVenSpecialInventory::getStorehouseSid, o.getStorehouseSid())
                            .eq(InvVenSpecialInventory::getBarcodeSid, o.getBarcodeSid())
                            .eq(InvVenSpecialInventory::getSpecialStock,specialStock)
                            .eq(InvVenSpecialInventory::getVendorSid, invInventorySheet.getVendorSid())
                    );
                    if(invVenSpecial!=null){
                        //修改特殊库存 供应商 数量
                        InvVenSpecialInventory invVenSpecialInventory = new InvVenSpecialInventory();
                        invVenSpecialInventoryMapper.update(invVenSpecialInventory,new UpdateWrapper<InvVenSpecialInventory>().lambda()
                                .set(InvVenSpecialInventory::getUnlimitedQuantity,o.getCountQuantity())
                                .eq(InvVenSpecialInventory::getStorehouseLocationSid,o.getStorehouseLocationSid())
                                .eq(InvVenSpecialInventory::getStorehouseSid,o.getStorehouseSid())
                                .eq(InvVenSpecialInventory::getBarcodeSid,o.getBarcodeSid())
                                .eq(InvVenSpecialInventory::getSpecialStock,invInventorySheet.getSpecialStock())
                                .eq(InvVenSpecialInventory::getVendorSid,invInventorySheet.getVendorSid())
                                .set(InvVenSpecialInventory::getLatestCountDate,new Date())
                        );
                    }else{
                        createInvVenSpecialLocation(invInventorySheet,o);
                    }
                    //获取当前 常规库存中 特殊库存 供应商 数量
                    InvInventoryLocation location= invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getStorehouseLocationSid, o.getStorehouseLocationSid())
                            .eq(InvInventoryLocation::getStorehouseSid, o.getStorehouseSid())
                            .eq(InvInventoryLocation::getBarcodeSid, o.getBarcodeSid()));
                    //修改常规库存中 特殊库存 供应商 数量
                    if(ConstantsEms.VEN_CU.equals(specialStock)){
                        if(location!=null){
                            BigDecimal quantity = location.getVendorConsignQuantity();
                            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                            invInventoryLocationMapper.update(invInventoryLocation,new UpdateWrapper<InvInventoryLocation>().lambda()
                                    .set(InvInventoryLocation::getVendorConsignQuantity,quantity.add(o.getCountQuantity().subtract(o.getStockQuantity())))
                                    .eq(InvInventoryLocation::getStorehouseLocationSid,o.getStorehouseLocationSid())
                                    .eq(InvInventoryLocation::getStorehouseSid,o.getStorehouseSid())
                                    .eq(InvInventoryLocation::getBarcodeSid,o.getBarcodeSid())
                                    .set(InvInventoryLocation::getLatestCountDate,new Date())
                            );
                        }else{
                            createLocationVEN(invInventorySheet,o,specialStock);
                        }
                    }else{
                        if(location!=null){
                            BigDecimal quantity = location.getVendorSubcontractQuantity();
                            BigDecimal subtract = o.getCountQuantity().subtract(o.getStockQuantity());
                            BigDecimal reduce = subtract.abs();
                            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                            invInventoryLocationMapper.update(invInventoryLocation,new UpdateWrapper<InvInventoryLocation>().lambda()
                                    .set(InvInventoryLocation::getVendorSubcontractQuantity,quantity.add(o.getCountQuantity().subtract(o.getStockQuantity())))
                                    .eq(InvInventoryLocation::getStorehouseLocationSid,o.getStorehouseLocationSid())
                                    .eq(InvInventoryLocation::getStorehouseSid,o.getStorehouseSid())
                                    .eq(InvInventoryLocation::getBarcodeSid,o.getBarcodeSid())
                                    .set(InvInventoryLocation::getLatestCountDate,new Date())
                            );
                        }else{
                            createLocationVEN(invInventorySheet,o,specialStock);
                        }
                    }

                });
            }

        }
        //客户特殊库存
        if (ConstantsEms.CUS_RA.equals(specialStock) || ConstantsEms.CUS_VE.equals(specialStock)) {
            list.forEach(o->{
                //判断仓库是否存在对应的库存
                InvCusSpecialInventory invCusSpecial = invCusSpecialInventoryMapper.selectOne(new QueryWrapper<InvCusSpecialInventory>().lambda()
                        .eq(InvCusSpecialInventory::getStorehouseLocationSid, o.getStorehouseLocationSid())
                        .eq(InvCusSpecialInventory::getStorehouseSid, o.getStorehouseSid())
                        .eq(InvCusSpecialInventory::getBarcodeSid, o.getBarcodeSid())
                        .eq(InvCusSpecialInventory::getSpecialStock,specialStock)
                        .eq(InvCusSpecialInventory::getCustomerSid, invInventorySheet.getCustomerSid())
                );
                if(invCusSpecial!=null){
                    //修改特殊库存 客户 数量
                    InvCusSpecialInventory invCusSpecialInventory = new InvCusSpecialInventory();
                    invCusSpecialInventoryMapper.update(invCusSpecialInventory,new UpdateWrapper<InvCusSpecialInventory>().lambda()
                            .set(InvCusSpecialInventory::getUnlimitedQuantity,o.getCountQuantity())
                            .eq(InvCusSpecialInventory::getStorehouseLocationSid,o.getStorehouseLocationSid())
                            .eq(InvCusSpecialInventory::getStorehouseSid,o.getStorehouseSid())
                            .eq(InvCusSpecialInventory::getBarcodeSid,o.getBarcodeSid())
                            .eq(InvCusSpecialInventory::getSpecialStock,invInventorySheet.getSpecialStock())
                            .eq(InvCusSpecialInventory::getCustomerSid,invInventorySheet.getCustomerSid())
                            .set(InvCusSpecialInventory::getLatestCountDate,new Date())
                    );
                }else{
                    createInvCusSpecialLocation(invInventorySheet,o);
                }
                //获取当前 常规库存中 特殊库存 客户 数量
                InvInventoryLocation location= invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getStorehouseLocationSid, o.getStorehouseLocationSid())
                        .eq(InvInventoryLocation::getStorehouseSid, o.getStorehouseSid())
                        .eq(InvInventoryLocation::getBarcodeSid, o.getBarcodeSid()));
                //修改常规库存中 特殊库存 客户 数量
                if(ConstantsEms.CUS_RA.equals(specialStock)){
                    if(location!=null){
                        BigDecimal quantity = location.getCustomerSubcontractQuantity();
                        InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                        invInventoryLocationMapper.update(invInventoryLocation,new UpdateWrapper<InvInventoryLocation>().lambda()
                                .set(InvInventoryLocation::getCustomerSubcontractQuantity,quantity.add(o.getCountQuantity().subtract(o.getStockQuantity())))
                                .eq(InvInventoryLocation::getStorehouseLocationSid,o.getStorehouseLocationSid())
                                .eq(InvInventoryLocation::getStorehouseSid,o.getStorehouseSid())
                                .eq(InvInventoryLocation::getBarcodeSid,o.getBarcodeSid())
                                .set(InvInventoryLocation::getLatestCountDate,new Date())
                        );
                    }else{
                        createLocationCUS(invInventorySheet,o,specialStock);
                    }
                }else{
                    if(location!=null){
                        BigDecimal quantity = location.getCustomerConsignQuantity();
                        InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
                        invInventoryLocationMapper.update(invInventoryLocation,new UpdateWrapper<InvInventoryLocation>().lambda()
                                .set(InvInventoryLocation::getCustomerConsignQuantity,quantity.add(o.getCountQuantity().subtract(o.getStockQuantity())))
                                .eq(InvInventoryLocation::getStorehouseLocationSid,o.getStorehouseLocationSid())
                                .eq(InvInventoryLocation::getStorehouseSid,o.getStorehouseSid())
                                .eq(InvInventoryLocation::getBarcodeSid,o.getBarcodeSid())
                                .set(InvInventoryLocation::getLatestCountDate,new Date())
                        );
                    }else{
                        createLocationCUS(invInventorySheet,o,specialStock);
                    }
                }

            });

        }
    }
    /**
     * 通过仓库库位获取对应的仓库信息
     */
    @Override
    public InvInventorySheet getInvInventorySheet(InvInventorySheet invInventorySheet){
        ArrayList<InvInventorySheetItem> invInventorySheetItems = new ArrayList<>();
        String specialStock = invInventorySheet.getSpecialStock();
        //常规库存
        if (specialStock == null) {
            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
            BeanCopyUtils.copyProperties(invInventorySheet,invInventoryLocation);
            List<InvInventoryLocation> list = invInventoryLocationMapper.getInvInventoryLocation(invInventoryLocation);
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(o->{
                    InvInventorySheetItem sheet = new InvInventorySheetItem();
                    BeanCopyUtils.copyProperties(o,sheet);
                    sheet.setStockQuantity(o.getUnlimitedQuantity());
                    invInventorySheetItems.add(sheet);
                });
            }
        }
        //供应商特殊库存
        if (ConstantsEms.VEN_CU.equals(specialStock) || ConstantsEms.VEN_RA.equals(specialStock)) {
            InvVenSpecialInventory invVenSpecialInventory = new InvVenSpecialInventory();
            BeanCopyUtils.copyProperties(invInventorySheet,invVenSpecialInventory);
            List<InvVenSpecialInventory> list = invVenSpecialInventoryMapper.getInvVenSpecialInventory(invVenSpecialInventory);
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(o->{
                    InvInventorySheetItem sheet = new InvInventorySheetItem();
                    BeanCopyUtils.copyProperties(o,sheet);
                    sheet.setStockQuantity(o.getUnlimitedQuantity());
                    sheet.setBarcode(Long.valueOf(o.getBarcode()));
                    invInventorySheetItems.add(sheet);
                });
            }
        }
        //客户特殊库存
        if (ConstantsEms.CUS_RA.equals(specialStock) || ConstantsEms.CUS_VE.equals(specialStock)) {
            InvCusSpecialInventory invCusSpecialInventory = new InvCusSpecialInventory();
            BeanCopyUtils.copyProperties(invInventorySheet,invCusSpecialInventory);
            List<InvCusSpecialInventory> list = invCusSpecialInventoryMapper.getInvCusSpecialInventory(invCusSpecialInventory);
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(o->{
                    InvInventorySheetItem sheet = new InvInventorySheetItem();
                    BeanCopyUtils.copyProperties(o,sheet);
                    sheet.setStockQuantity(o.getUnlimitedQuantity());
                    sheet.setBarcode(Long.valueOf(o.getBarcode()));
                    invInventorySheetItems.add(sheet);
                });
            }
        }
        if(CollectionUtils.isNotEmpty(invInventorySheetItems)){
            Boolean isZero = invInventorySheet.getIsZero();
            if(isZero==null){
                isZero=false;
            }
            List<InvInventorySheetItem> AllList = new ArrayList<>();
            if(!isZero){
                List<InvInventorySheetItem> items = invInventorySheetItems.stream().filter(li -> li.getStockQuantity() != null && li.getStockQuantity().compareTo(BigDecimal.ZERO)!=0).collect(Collectors.toList());
                AllList.addAll(items);
            }else{
                AllList.addAll(invInventorySheetItems);
            }
            setItemNum(AllList);
            invInventorySheet.setInvInventorySheetItemList(AllList);
            return invInventorySheet;
        }else{
            throw new CustomException("该仓库库位下没有库存信息");
        }
    }


    /**
     * 盘点单-明细对象
     */
    private void addInvInventorySheetItem(InvInventorySheet invInventorySheet, List<InvInventorySheetItem> invInventorySheetItemList) {
        invInventorySheetItemMapper.delete(
                new UpdateWrapper<InvInventorySheetItem>()
                        .lambda()
                        .eq(InvInventorySheetItem::getInventorySheetSid, invInventorySheet.getInventorySheetSid())
        );
        if(CollectionUtils.isNotEmpty(invInventorySheetItemList)){
            invInventorySheetItemList.forEach(o -> {
                o.setStockQuantity(o.getStockQuantity()!=null?o.getStockQuantity():BigDecimal.ZERO);
                o.setInventorySheetSid(invInventorySheet.getInventorySheetSid());
                invInventorySheetItemMapper.insert(o);
            });
        }
    }


    /**
     * 盘点单-附件对象
     */
    private void addInvInventorySheetAttachment(InvInventorySheet invInventorySheet, List<InvInventorySheetAttachment> invInventorySheetAttachmentList) {
        invInventorySheetAttachmentMapper.delete(
                new UpdateWrapper<InvInventorySheetAttachment>()
                        .lambda()
                        .eq(InvInventorySheetAttachment::getInventorySheetSid, invInventorySheet.getInventorySheetSid())
        );
        if(CollectionUtils.isNotEmpty(invInventorySheetAttachmentList)){
            invInventorySheetAttachmentList.forEach(o -> {
                o.setInventorySheetSid(invInventorySheet.getInventorySheetSid());
                invInventorySheetAttachmentMapper.insert(o);
            });
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(InvInventorySheet o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }
    public void judgeNull(InvInventorySheet note){
        List<InvInventorySheetItem> list = note.getInvInventorySheetItemList();
        if(CollectionUtils.isEmpty(list)){
            throw  new CustomException("确认时，明细行不允许为空");
        }
    }

    /**
     * 修改盘点单
     *
     * @param invInventorySheet 盘点单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventorySheet(InvInventorySheet invInventorySheet) {
        if(ConstantsEms.CHECK_STATUS.equals(invInventorySheet.getHandleStatus())){
            judgeNull(invInventorySheet);
        }
        //设置确认信息
        setConfirmInfo(invInventorySheet);
        judge(invInventorySheet);
        invInventorySheet.setCountStatus(ConstantsEms.CONUNT_STATUS_B);
        int row=invInventorySheetMapper.updateAllById(invInventorySheet);
        //盘点单-明细list
        List<InvInventorySheetItem> invInventorySheetItemList = invInventorySheet.getInvInventorySheetItemList();
        setItemNum(invInventorySheetItemList);
        addInvInventorySheetItem(invInventorySheet, invInventorySheetItemList);
        //盘点单-附件list
        List<InvInventorySheetAttachment> invInventorySheetAttachmentList = invInventorySheet.getInvInventorySheetAttachmentList();
        addInvInventorySheetAttachment(invInventorySheet, invInventorySheetAttachmentList);
        String type = invInventorySheet.getType();
        //过账
        if(HandleStatus.POSTING.getCode().equals(invInventorySheet.getHandleStatus())){
            invInventorySheet.setCountResultEnterDate(new Date());
            invInventorySheet.setActualCountDate(new Date());
            invInventorySheet.setCountStatus(ConstantsEms.CONUNT_STATUS_R);
            invInventorySheet.setAccountDate(new Date());
            invInventorySheet.setAccountor(ApiThreadLocalUtil.get().getUsername());
            Calendar date = Calendar.getInstance();
            String year = String.valueOf(date.get(Calendar.YEAR));
            invInventorySheet.setYear(year);
            invInventorySheetMapper.updateAllById(invInventorySheet);
            judegeDocument(invInventorySheet,invInventorySheetItemList);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(invInventorySheet.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(invInventorySheet);
        }
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventorySheet.getInventorySheetSid(), BusinessType.UPDATE.getValue(),TITLE);
        }
        return 1;
    }

    @Override
    public List<InvInventorySheetItem> sort(List<InvInventorySheetItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvInventorySheetItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                    List<InvInventorySheetItem> allList = new ArrayList<>();
                    List<InvInventorySheetItem> allThirdList = new ArrayList<>();
                    List<InvInventorySheetItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvInventorySheetItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvInventorySheetItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvInventorySheetItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvInventorySheetItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvInventorySheetItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvInventorySheetItem::getMaterialCode)
                        .thenComparing(InvInventorySheetItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                List<InvInventorySheetItem> codeNullList = itemArrayListAll.stream().filter(li -> li.getMaterialCode()==null).collect(Collectors.toList());
                List<InvInventorySheetItem> codeNotNullList = itemArrayListAll.stream().filter(li -> li.getMaterialCode()!=null).collect(Collectors.toList());
                codeNullList=codeNullList.stream().sorted(Comparator.comparing(InvInventorySheetItem::getSku1Name)
                ).collect(Collectors.toList());
                codeNotNullList=codeNotNullList.stream().sorted(Comparator.comparing(InvInventorySheetItem::getMaterialCode)
                        .thenComparing(InvInventorySheetItem::getSku1Name)
                ).collect(Collectors.toList());
                ArrayList<InvInventorySheetItem> all = new ArrayList<>();
                all.addAll(codeNotNullList);
                all.addAll(codeNullList);
                items=all;
            }
            return items;
        }
        return new ArrayList<>();
    }


    //客户寄售 生成 供应商台账
    public void JudegeCus(InvInventorySheet invInventorySheet,List<InvInventorySheetItem> invInventorySheetItemList){
        String specialStock = invInventorySheet.getSpecialStock();
        String handleStatus = invInventorySheet.getHandleStatus();
        if (ConstantsEms.VEN_CU.equals(specialStock)) {
            invInventorySheetItemList.forEach(li -> {
                PurRecordVendorConsign purRecordVendorConsign = new PurRecordVendorConsign();
                BeanCopyUtils.copyProperties(invInventorySheet, purRecordVendorConsign);
                BeanCopyUtils.copyProperties(li, purRecordVendorConsign);
                BigDecimal subtract = li.getCountQuantity().subtract(li.getStockQuantity());
                purRecordVendorConsign.setQuantity(subtract.abs());
                //盘亏 供应商寄售待结算台账 增加
                if (subtract.compareTo(new BigDecimal(0)) == -1) {
                    purRecordVendorConsign.setType(ConstantsEms.RU_KU);
                    //盘盈 供应商寄售待结算台账 扣减
                } else {
                    purRecordVendorConsign.setType(ConstantsEms.CHU_KU);
                }
                purRecordVendorConsignServiceImpl.insertPurRecordVendorConsign(purRecordVendorConsign);
            });
        }
    }

    /**
     * 批量删除盘点单
     *
     * @param inventorySheetSids 需要删除的盘点单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventorySheetByIds(Long[] inventorySheetSids) {
        //删除盘点单
        invInventorySheetMapper.deleteInvInventorySheetByIds(inventorySheetSids);
        //删除盘点单明细
        invInventorySheetItemMapper.deleteInvInventorySheetItemByIds(inventorySheetSids);
        //删除盘点单附件
        invInventorySheetAttachmentMapper.deleteInvInventorySheetAttachmentByIds(inventorySheetSids);
        for (Long sid : inventorySheetSids) {
            InvInventorySheet sheet = new InvInventorySheet();
            sheet.setInventorySheetSid(sid);
            //校验是否存在待办
            checkTodoExist(sheet);
            //插入日志
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(),TITLE);
        }
        return inventorySheetSids.length;
    }

    /**
     * 盘点单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(InvInventorySheet invInventorySheet) {
        //盘点单sids
        Long[] inventorySheetSids = invInventorySheet.getInventorySheetSids();
        String handleStatus = invInventorySheet.getHandleStatus();
        if(HandleStatus.POSTING.getCode().equals(handleStatus)){
            //过账
            post(invInventorySheet);
        }else{
            //生成库存凭证
            for (Long sid : inventorySheetSids) {
                InvInventorySheet sheet = selectInvInventorySheetById(sid);
                List<InvInventorySheetItem> invInventorySheetItemList = sheet.getInvInventorySheetItemList();
                if(CollectionUtils.isEmpty(invInventorySheetItemList)){
                    throw new CustomException("确认时，明细行不允许为空");
                }else{
                    invInventorySheetItemList.forEach(item->{
                        if(item.getCountQuantity()==null){
                            throw new CustomException("确认时,实盘量不允许为空");
                        }
                    });
                }
                //校验是否存在待办
                checkTodoExist(sheet);
                //插入日志
//                MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(),TITLE);
            }
        }
        return invInventorySheetMapper.confirm(invInventorySheet);
    }

    /**
     * 盘点单批量修改处理状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handle(InvInventorySheet invInventorySheet) {
        int row = 0;
        String handleStatus = invInventorySheet.getHandleStatus();
        if (invInventorySheet.getInventorySheetSid() != null) {
            Long[] sids = invInventorySheet.getInventorySheetSids();
            if (sids != null) {
                sids[sids.length] = invInventorySheet.getInventorySheetSid();
            }
            else {
                sids = new Long[]{invInventorySheet.getInventorySheetSid()};
            }
            invInventorySheet.setInventorySheetSids(sids);
        }
        if (ArrayUtil.isNotEmpty(invInventorySheet.getInventorySheetSids()) && StrUtil.isNotBlank(handleStatus)) {
            LambdaUpdateWrapper<InvInventorySheet> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(InvInventorySheet::getInventorySheetSid, invInventorySheet.getInventorySheetSids())
                    .set(InvInventorySheet::getHandleStatus, handleStatus);
            row = invInventorySheetMapper.update(null, updateWrapper);
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, invInventorySheet.getInventorySheetSids()));
            }
            for (Long sid : invInventorySheet.getInventorySheetSids()) {
                MongodbDeal.check(sid, handleStatus, null, TITLE, null);
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
            InvInventorySheet sheet = selectInvInventorySheetById(id);
            List<InvInventorySheetItem> list = sheet.getInvInventorySheetItemList();
            if(CollectionUtils.isEmpty(list)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不允许为空");
                msgList.add(errMsgResponse);
            }else{
                List<InvInventorySheetItem> noteItems = list.stream().filter(item -> item.getCountQuantity() == null).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(noteItems)){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setMsg("明细行的实盘量不允许为空");
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
     * 盘点新建导入
     */
    @Override
    public AjaxResult importDataInv(MultipartFile file){
        Long basStorehouseSid=null;
        String basStorehouseCode=null;
        Long StorehouseLocationSid=null;
        String storehouseLocationCode=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
        Date documentDate=null;
        Date planCountDate=null;
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
            InvInventorySheet sheet = new InvInventorySheet();
            List<InvInventorySheetItem> invInventorySheets = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Code=null;
                String sku2Code=null;
                String materialName=null;
                String unitBase=null;
                BigDecimal countQuantity=null;
                Boolean match=true;
                Boolean matchSpecial=false;
                BasMaterial basMaterial=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("开单日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(0).toString());
                        if(!validDate){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("开单日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            documentDate=DateUtil.parse(objects.get(0).toString());
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("计划盘点日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean valid = JudgeFormat.isValidDate(objects.get(1).toString());
                        if(!valid){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("计划盘点日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            planCountDate=DateUtil.parse(objects.get(1).toString());
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(2).toString())
                        );
                        if (basStorehouse == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(2).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                            basStorehouseCode = basStorehouse.getStorehouseCode();
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("库位，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(3).toString())
                        );
                        if (basStorehouseLocation == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(2).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
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
                        String special = specialMaps.get(objects.get(4).toString());
                        if(special==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("特殊库存，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                            match=false;
                        }else{
                            ConSpecialStock conSpecialStock = conSpecialStockMapper.selectOne(new QueryWrapper<ConSpecialStock>().lambda()
                                    .eq(ConSpecialStock::getCode, special)
                                    .eq(ConSpecialStock::getStatus, ConstantsEms.ENABLE_STATUS)
                                    .eq(ConSpecialStock::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            );
                            if(conSpecialStock==null){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("特殊库存，必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                                match=false;
                            }
                            matchSpecial=true;
                        }
                        specialStock=special;
                    }
                    if (objects.get(5) != "" &&objects.get(5) != null) {
                        String vendorCode = objects.get(5).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorCode)
                        );
                        if (basVendor == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                                match=false;
                            }else{
                                vendorSid = Long.valueOf(basVendor.getVendorSid());
                            }
                        }
                    }
                    if (objects.get(6) != ""&&objects.get(6) != null) {
                        String customerCode = objects.get(6).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerCode));
                        if (basCustomer == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + customerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                                match=false;
                            }else{
                                customerSid = basCustomer.getCustomerSid();
                            }
                        }
                    }
                    if(objects.get(4)==null ||objects.get(4)==""){
                        if(objects.get(5)!=null&&objects.get(5)!=""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("供应商简称必须为空，导入失败");
                            msgList.add(errMsgResponse);
                            match=false;
                        }
                        if(objects.get(6)!=null&&objects.get(6)!=""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("客户简称必须为空，导入失败");
                            msgList.add(errMsgResponse);
                            match=false;
                        }
                    }
                    if(matchSpecial){
                        if(objects.get(4)!=null&&objects.get(4)!=""){
                            if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(4).toString()))){
                                if(objects.get(5)==null||objects.get(5)==""){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                    match=false;
                                }
                                if(objects.get(6)!=null&&objects.get(6)!=""){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                    match=false;
                                }
                            }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(4).toString()))){
                                if(objects.get(6)==null||objects.get(6)==""){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("客户简称，不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                    match=false;
                                }
                                if(objects.get(5)!=null&&objects.get(5)!=""){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("供应商简称，必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                    match=false;
                                }
                            }
                        }
                    }
                    if(match){
                        InvInventorySheet invInventorySheet = new InvInventorySheet();
                        invInventorySheet .setMovementType(ConstantsEms.PROFIT)
                                .setStorehouseSid(basStorehouseSid)
                                .setSpecialStock(specialStock)
                                .setStorehouseLocationSid(StorehouseLocationSid)
                                .setCustomerSid(customerSid)
                                .setVendorSid(vendorSid);
                        try{
                            judge(invInventorySheet);
                        }catch (BaseException e) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("盘点单已存在，请核实！");
                            msgList.add(errMsgResponse);
                        }
                    }
                    sheet
                            .setMovementType(ConstantsEms.PROFIT)
                            .setDocumentDate(documentDate)
                            .setPlanCountDate(planCountDate)
                            .setStorehouseSid(basStorehouseSid)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setSpecialStock((objects.get(4)==""||objects.get(4)==null)?null:specialMaps.get(objects.get(4).toString()))
                            .setCustomerSid(customerSid)
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(7)==""||objects.get(7)==null)?null:objects.get(7).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                // 物料/商品编码
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (materialCode == null) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                // sku1名称
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                // sku2名称
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                // 实盘量
                if (objects.get(3) != null && objects.get(3) != "") {
                    boolean validInt = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validInt){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("实盘量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        countQuantity=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                        Double mount = Double.valueOf(objects.get(3).toString());
                        if(mount<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("实盘量小于0，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                try {
                    if (materialCode != null) {
                        BasMaterialBarcode basMaterialBarcode = basMaterialBarcodeMapper.selectBasMaterialBarcodeListByInvImport(new BasMaterialBarcode()
                                .setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                        if (basMaterialBarcode != null && basMaterialBarcode.getBarcodeSid() != null) {
                            // 物料商品信息
                            materialName = basMaterialBarcode.getMaterialName();
                            materialSid = basMaterialBarcode.getMaterialSid();
                            unitBase = basMaterialBarcode.getUnitBase();
                            // sku1信息
                            sku1Name = basMaterialBarcode.getSku1Name();
                            sku1Code = basMaterialBarcode.getSku1Code();
                            sku1Sid = basMaterialBarcode.getSku1Sid();
                            // sku2信息
                            sku2Code = basMaterialBarcode.getSku2Code();
                            sku2Name = basMaterialBarcode.getSku2Name();
                            sku2Sid = basMaterialBarcode.getSku2Sid();
                            // 商品条码信息
                            barcodeSid = basMaterialBarcode.getBarcodeSid();
                            barcode = basMaterialBarcode.getBarcode();
                            bardCodeList.add(basMaterialBarcode.getBarcodeSid());
                        }
                        else {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("此组合的商品条码不存在，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }
                } catch (TooManyResultsException e) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("此组合的商品条码系统中存在多笔，请联系管理员，导入失败！");
                    msgList.add(errMsgResponse);
                }
                InvInventorySheetItem sheetItem = new InvInventorySheetItem();
                sheetItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1Code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setBarcode(barcode==null?null:Long.valueOf(barcode))
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setCountQuantity(countQuantity)
                        .setStorehouseSid(sheet.getStorehouseSid())
                        .setStorehouseCode(basStorehouseCode)
                        .setStorehouseLocationSid(sheet.getStorehouseLocationSid())
                        .setStorehouseLocationCode(storehouseLocationCode)
                        .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                invInventorySheets.add(sheetItem);
            }
            HashSet<Long> longs = new HashSet<>(bardCodeList);
            if(longs.size()!=bardCodeList.size()){
                for (int i=0;i<bardCodeList.size();i++){
                    for (int j=i+1;j<bardCodeList.size();j++){
                        if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                            int nu=j+1+5;
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
            String stock = sheet.getSpecialStock();
            //常规库存
            if (stock == null) {
                invInventorySheets.forEach(li->{
                    InvInventoryLocation invInventoryLocation = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                            .eq(InvInventoryLocation::getStorehouseSid, li.getStorehouseSid())
                            .eq(InvInventoryLocation::getStorehouseLocationSid, li.getStorehouseLocationSid())
                    );
                    if(invInventoryLocation!=null){
                        li.setStockQuantity(invInventoryLocation.getUnlimitedQuantity());
                    }else{
                        li.setStockQuantity(BigDecimal.ZERO);
                    }
                });
            }
            //供应商特殊库存
            if (ConstantsEms.VEN_CU.equals(stock) || ConstantsEms.VEN_RA.equals(stock)) {
                invInventorySheets.forEach(li->{
                    InvVenSpecialInventory invVenSpecialInventory = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                            .eq(InvVenSpecialInventory::getBarcodeSid, li.getBarcodeSid())
                            .eq(InvVenSpecialInventory::getSpecialStock, stock)
                            .eq(InvVenSpecialInventory::getVendorSid,sheet.getVendorSid())
                            .eq(InvVenSpecialInventory::getStorehouseSid, li.getStorehouseSid())
                            .eq(InvVenSpecialInventory::getStorehouseLocationSid, li.getStorehouseLocationSid())
                    );
                    if(invVenSpecialInventory!=null){
                        li.setStockQuantity(invVenSpecialInventory.getUnlimitedQuantity());
                    }else{
                        li.setStockQuantity(BigDecimal.ZERO);
                    }
                });
            }
            //客户特殊库存
            if (ConstantsEms.CUS_RA.equals(stock) || ConstantsEms.CUS_VE.equals(stock)) {
                invInventorySheets.forEach(li->{
                    InvCusSpecialInventory invCusSpecialInventory = invCusSpecialInventoryMapper.selectOne(new QueryWrapper<InvCusSpecialInventory>().lambda()
                            .eq(InvCusSpecialInventory::getBarcodeSid, li.getBarcodeSid())
                            .eq(InvCusSpecialInventory::getSpecialStock, stock)
                            .eq(InvCusSpecialInventory::getCustomerSid,sheet.getCustomerSid())
                            .eq(InvCusSpecialInventory::getStorehouseSid, li.getStorehouseSid())
                            .eq(InvCusSpecialInventory::getStorehouseLocationSid, li.getStorehouseLocationSid())
                    );
                    if(invCusSpecialInventory!=null){
                        li.setStockQuantity(invCusSpecialInventory.getUnlimitedQuantity());
                    }else{
                        li.setStockQuantity(BigDecimal.ZERO);
                    }
                });
            }
            sheet.setInvInventorySheetItemList(invInventorySheets);
             insertInvInventorySheet(sheet);
            return AjaxResult.success(1);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
    }


    /**
     * 实盘量导入
     */
    @Override
    public AjaxResult importData(MultipartFile file){
        Long basStorehouseSid=null;
        String basStorehouseCode=null;
        Long StorehouseLocationSid=null;
        String storehouseLocationCode=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
        Date documentDate=null;
        Date planCountDate=null;
        InvInventorySheet invInventorySheet=new InvInventorySheet();
        Long sid=null;
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
            // sku类型
            List<DictData> skuTypeList=sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> skuTypeMaps=skuTypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
            Map<String,String> specialMaps = conSpecialStockMapper.getList().stream().collect(Collectors.toMap(ConSpecialStock::getName, ConSpecialStock::getCode, (key1, key2) -> key2));
            List<InvInventorySheetItem> invInventorySheets = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Code=null;
                String sku2Code=null;
                String materialName=null;
                String unitBase=null;
                BigDecimal countQuantity=null;
                Boolean match=true;
                Boolean matchSpecial=false;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("盘点单号，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                         invInventorySheet = invInventorySheetMapper.selectOne(new QueryWrapper<InvInventorySheet>().lambda()
                                .eq(InvInventorySheet::getInventorySheetCode, objects.get(0).toString())
                        );
                        if(invInventorySheet==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("单号为"+objects.get(0).toString()+"的盘点单不存在，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String handleStatus = invInventorySheet.getHandleStatus();
                            if(!HandleStatus.SAVE.getCode().equals(handleStatus)&&!HandleStatus.RETURNED.getCode().equals(handleStatus)){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("盘点单必须是保存或已退回状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                basStorehouseSid=invInventorySheet.getStorehouseSid();
                                if (basStorehouseSid != null) {
                                    BasStorehouse storehouse = basStorehouseMapper.selectById(basStorehouseSid);
                                    if (storehouse != null) {
                                        basStorehouseCode=storehouse.getStorehouseCode();
                                    }
                                }
                                StorehouseLocationSid=invInventorySheet.getStorehouseLocationSid();
                                if (StorehouseLocationSid != null) {
                                    BasStorehouseLocation storehouseLocation = basStorehouseLocationMapper.selectById(StorehouseLocationSid);
                                    if (storehouseLocation != null) {
                                        storehouseLocationCode=storehouseLocation.getLocationCode();
                                    }
                                }
                                sid=invInventorySheet.getInventorySheetSid();
                            }
                        }
                    }
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                // 物料/商品编码
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (materialCode == null) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);

                }
                // sku1名称
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                // sku2名称
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                // 实盘
                if (objects.get(4) == null || objects.get(4) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("实盘量，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validInt = JudgeFormat.isValidDouble(objects.get(4).toString());
                    if(!validInt){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("实盘量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        countQuantity=BigDecimal.valueOf(Double.valueOf(objects.get(4).toString()));
                        Double mount = Double.valueOf(objects.get(4).toString());
                        if(mount<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("实盘量小于0，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                try {
                    if (materialCode != null) {
                        BasMaterialBarcode basMaterialBarcode = basMaterialBarcodeMapper.selectBasMaterialBarcodeListByInvImport(new BasMaterialBarcode()
                                .setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                        if (basMaterialBarcode != null && basMaterialBarcode.getBarcodeSid() != null) {
                            // 物料商品信息
                            materialName = basMaterialBarcode.getMaterialName();
                            materialSid = basMaterialBarcode.getMaterialSid();
                            unitBase = basMaterialBarcode.getUnitBase();
                            // sku1信息
                            sku1Name = basMaterialBarcode.getSku1Name();
                            sku1Code = basMaterialBarcode.getSku1Code();
                            sku1Sid = basMaterialBarcode.getSku1Sid();
                            // sku2信息
                            sku2Code = basMaterialBarcode.getSku2Code();
                            sku2Name = basMaterialBarcode.getSku2Name();
                            sku2Sid = basMaterialBarcode.getSku2Sid();
                            // 商品条码信息
                            barcodeSid = basMaterialBarcode.getBarcodeSid();
                            barcode = basMaterialBarcode.getBarcode();
                            bardCodeList.add(basMaterialBarcode.getBarcodeSid());
                        }
                        else {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("此组合的商品条码不存在，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }
                } catch (TooManyResultsException e) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("此组合的商品条码系统中存在多笔，请联系管理员，导入失败！");
                    msgList.add(errMsgResponse);
                }
                InvInventorySheetItem sheetItem = new InvInventorySheetItem();
                sheetItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1Code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setBarcode(barcode==null?null:Long.valueOf(barcode))
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseCode(basStorehouseCode)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setStorehouseLocationCode(storehouseLocationCode)
                        .setStockQuantity((objects.get(3)==""||objects.get(3)==null)?null:BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())))
                        .setCountQuantity(countQuantity)
                        .setRemark((objects.get(5)==""||objects.get(5)==null)?null:objects.get(5).toString());
                invInventorySheets.add(sheetItem);
            }
            HashSet<Long> longs = new HashSet<>(bardCodeList);
            if(longs.size()!=bardCodeList.size()){
                for (int i=0;i<bardCodeList.size();i++){
                    for (int j=i+1;j<bardCodeList.size();j++){
                        if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                            int nu=j+1+5;
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
            String stock = invInventorySheet.getSpecialStock();
          Long  basStorehouseSidCopy=basStorehouseSid;
          Long  StorehouseLocationSidCopy=StorehouseLocationSid;
            Long customerSidCopy = invInventorySheet.getCustomerSid();
            Long vendorSidCopy = invInventorySheet.getVendorSid();

            //常规库存
            if (stock == null) {
                invInventorySheets.forEach(li->{
                    if(li.getStockQuantity()==null){
                        InvInventoryLocation invInventoryLocation = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                                .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                                .eq(InvInventoryLocation::getStorehouseSid, basStorehouseSidCopy)
                                .eq(InvInventoryLocation::getStorehouseLocationSid, StorehouseLocationSidCopy)
                        );
                        if(invInventoryLocation!=null){
                            li.setStockQuantity(invInventoryLocation.getUnlimitedQuantity());
                        }else{
                            li.setStockQuantity(BigDecimal.ZERO);
                        }
                    }
                });
            }
            //供应商特殊库存
            if (ConstantsEms.VEN_CU.equals(stock) || ConstantsEms.VEN_RA.equals(stock)) {
                invInventorySheets.forEach(li -> {
                    if (li.getStockQuantity() == null) {
                        InvVenSpecialInventory invVenSpecialInventory = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                                .eq(InvVenSpecialInventory::getBarcodeSid, li.getBarcodeSid())
                                .eq(InvVenSpecialInventory::getSpecialStock, stock)
                                .eq(InvVenSpecialInventory::getVendorSid,vendorSidCopy)
                                .eq(InvVenSpecialInventory::getStorehouseSid, basStorehouseSidCopy)
                                .eq(InvVenSpecialInventory::getStorehouseLocationSid, StorehouseLocationSidCopy)
                        );
                        if (invVenSpecialInventory != null) {
                            li.setStockQuantity(invVenSpecialInventory.getUnlimitedQuantity());
                        } else {
                            li.setStockQuantity(BigDecimal.ZERO);
                        }
                    }
                });
            }
            //客户特殊库存
            if (ConstantsEms.CUS_RA.equals(stock) || ConstantsEms.CUS_VE.equals(stock)) {
                invInventorySheets.forEach(li -> {
                    if (li.getStockQuantity() == null) {
                        InvCusSpecialInventory invCusSpecialInventory = invCusSpecialInventoryMapper.selectOne(new QueryWrapper<InvCusSpecialInventory>().lambda()
                                .eq(InvCusSpecialInventory::getBarcodeSid, li.getBarcodeSid())
                                .eq(InvCusSpecialInventory::getSpecialStock, stock)
                                .eq(InvCusSpecialInventory::getCustomerSid, customerSidCopy)
                                .eq(InvCusSpecialInventory::getStorehouseSid, basStorehouseSidCopy)
                                .eq(InvCusSpecialInventory::getStorehouseLocationSid, StorehouseLocationSidCopy)
                        );
                        if (invCusSpecialInventory != null) {
                            li.setStockQuantity(invCusSpecialInventory.getUnlimitedQuantity());
                        } else {
                            li.setStockQuantity(BigDecimal.ZERO);
                        }
                    }
                });
            }
            //系统内的明细行
            List<InvInventorySheetItem> invInventorySheetItems = invInventorySheetItemMapper.selectList(new QueryWrapper<InvInventorySheetItem>().lambda()
                    .eq(InvInventorySheetItem::getInventorySheetSid, sid)
            );
            int max = 0;
            if (CollectionUtil.isNotEmpty(invInventorySheetItems)) {
                max = invInventorySheetItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
            }
            List<Long> nows = invInventorySheetItems.stream().map(li -> li.getBarcodeSid()).collect(Collectors.toList());
            //新增的数据
            List<InvInventorySheetItem> itemsIn = invInventorySheets.stream().filter(li -> !nows.contains(li.getBarcodeSid())).collect(Collectors.toList());
            Long id=sid;
            if(CollectionUtil.isNotEmpty(itemsIn)){
                for (int i = 0; i < itemsIn.size(); i++) {
                    int itemNum=i+1+max;
                    itemsIn.get(i).setItemNum(itemNum);
                    itemsIn.get(i).setInventorySheetSid(id);
                    invInventorySheetItemMapper.insert(itemsIn.get(i));
                }
            }
            //修改的数据
            List<InvInventorySheetItem> itemsUp = invInventorySheets.stream().filter(li -> nows.contains(li.getBarcodeSid())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(itemsUp)){
                itemsUp.forEach(li->{
                    Long barcodeSid = li.getBarcodeSid();
                    invInventorySheetItemMapper.update(new InvInventorySheetItem(),new UpdateWrapper<InvInventorySheetItem>().lambda()
                    .eq(InvInventorySheetItem::getBarcodeSid,barcodeSid)
                            .set(InvInventorySheetItem::getRemark,li.getRemark())
                    .set(InvInventorySheetItem::getCountQuantity,li.getCountQuantity()));
                });
            }
            MongodbUtil.insertApprovalLog(invInventorySheet.getInventorySheetSid(), BusinessType.UPDATE.getValue(),"更新实盘量");
            return AjaxResult.success(1);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
    }

    public void judge(InvInventorySheet sheet){
        List<InvInventorySheet> sheets=null;
        String stock = sheet.getSpecialStock();
        if(stock==null){
            sheets = invInventorySheetMapper.selectList(
                    new QueryWrapper<InvInventorySheet>().lambda()
                            .eq(InvInventorySheet::getStorehouseSid, sheet.getStorehouseSid())
                            .isNull(InvInventorySheet::getSpecialStock)
                            .eq(InvInventorySheet::getMovementType,sheet.getMovementType())
                            .eq(InvInventorySheet::getStorehouseLocationSid, sheet.getStorehouseLocationSid())
            );
        }else if(ConstantsEms.VEN_CU.equals(stock) || ConstantsEms.VEN_RA.equals(stock)){
            sheets = invInventorySheetMapper.selectList(
                    new QueryWrapper<InvInventorySheet>().lambda()
                            .eq(InvInventorySheet::getStorehouseSid, sheet.getStorehouseSid())
                            .eq(InvInventorySheet::getVendorSid,sheet.getVendorSid())
                            .eq(InvInventorySheet::getSpecialStock,sheet.getSpecialStock())
                            .eq(InvInventorySheet::getMovementType,sheet.getMovementType())
                            .eq(InvInventorySheet::getStorehouseLocationSid, sheet.getStorehouseLocationSid())
            );
        }else{
            sheets = invInventorySheetMapper.selectList(
                    new QueryWrapper<InvInventorySheet>().lambda()
                            .eq(InvInventorySheet::getStorehouseSid, sheet.getStorehouseSid())
                            .eq(InvInventorySheet::getCustomerSid,sheet.getCustomerSid())
                            .eq(InvInventorySheet::getSpecialStock,sheet.getSpecialStock())
                            .eq(InvInventorySheet::getMovementType,sheet.getMovementType())
                            .eq(InvInventorySheet::getStorehouseLocationSid, sheet.getStorehouseLocationSid())
            );
        }
        List<InvInventorySheet> list = sheets.stream().filter(li -> !li.getHandleStatus().equals(HandleStatus.POSTING.getCode()
        )).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(list)){
            if(ConstantsEms.PROFIT.equals(sheet.getMovementType())){
                Long inventorySheetSid = list.get(0).getInventorySheetSid();
                Long sid = sheet.getInventorySheetSid();
                if(sid==null){
                    throw new BaseException("盘点单已存在，请核实！");
                }else{
                    if(!inventorySheetSid.toString().equals(sid.toString())){
                        throw new BaseException("盘点单已存在，请核实！");
                    }
                }
            }else{
                throw new BaseException("样品盘点单已重复，请核实！");
            }
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
    /**
     * 盘点单过账
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int post(InvInventorySheet invInventorySheet){
        //盘点单sids
        Long[] inventorySheetSids = invInventorySheet.getInventorySheetSids();
        invInventorySheet.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        invInventorySheet.setConfirmDate(new Date());
        Map<Long, InvInventorySheet> sheetMap = new HashMap<>();
        //生成库存凭证
        for (Long sid : inventorySheetSids) {
            InvInventorySheet sheet = selectInvInventorySheetById(sid);
            List<InvInventorySheetItem> invInventorySheetItemList = sheet.getInvInventorySheetItemList();
            if(CollectionUtils.isEmpty(invInventorySheetItemList)){
                throw new CustomException("过账时，明细行不允许为空");
            }else{
                invInventorySheetItemList.forEach(item->{
                    if(item.getCountQuantity()==null){
                        throw new CustomException("过账时,实盘量不允许为空");
                    }
                });
            }
            sheetMap.put(sid,sheet);
            sheet.setCountResultEnterDate(new Date());
            sheet.setActualCountDate(new Date());
            sheet.setCountStatus(ConstantsEms.CONUNT_STATUS_R);
            sheet.setAccountDate(new Date());
            sheet.setAccountor(ApiThreadLocalUtil.get().getUsername());
            Calendar date = Calendar.getInstance();
            String year = String.valueOf(date.get(Calendar.YEAR));
            sheet.setYear(year);
            invInventorySheetMapper.updateAllById(sheet);
            setStock(invInventorySheetItemList);
            judegeDocument(sheet, invInventorySheetItemList);
            //校验是否存在待办
            checkTodoExist(sheet);
            //插入日志
            MongodbUtil.insertUserLog(sid, BusinessType.POSTING.getValue(),TITLE);
        }
        int row = invInventorySheetMapper.update(new InvInventorySheet(), new UpdateWrapper<InvInventorySheet>().lambda()
                .in(InvInventorySheet::getInventorySheetSid, inventorySheetSids)
                .set(InvInventorySheet::getHandleStatus, HandleStatus.POSTING.getCode())
        );
        // 若盘点时，存在新增物料/商品为仓库中没有的数据，进行校验：
        // 在库存物料表（s_inv_storehouse_material）中是否存在数据（仓库+商品条码），若不存在，进行下述操作：
        // 过账成功时，在库存物料表（s_inv_storehouse_material）中生成一笔新数据，
        // 默认使用频率标识“usage_frequency_flag”为“常用”（CY），默认价格”price“为0
        List<InvInventorySheetItem> sheetItemList = invInventorySheetItemMapper.selectList(new QueryWrapper<InvInventorySheetItem>().lambda()
                .in(InvInventorySheetItem::getInventorySheetSid, inventorySheetSids));
        if (CollectionUtil.isNotEmpty(sheetItemList)) {
            List<InvStorehouseMaterial> storehouseMaterialList = new ArrayList<>();
            sheetItemList.forEach(item->{
                Long storehouseSid = sheetMap.get(item.getInventorySheetSid()).getStorehouseSid();
                if (CollectionUtil.isEmpty(invStorehouseMaterialMapper.selectList(new QueryWrapper<InvStorehouseMaterial>().lambda()
                        .eq(InvStorehouseMaterial::getStorehouseSid, storehouseSid)
                        .eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid())))) {
                    InvStorehouseMaterial material = new InvStorehouseMaterial();
                    material.setMaterialSid(item.getMaterialSid())
                            .setSku1Sid(item.getSku1Sid())
                            .setSku2Sid(item.getSku2Sid())
                            .setBarcodeSid(item.getBarcodeSid())
                            .setStorehouseSid(storehouseSid)
                            .setPrice(BigDecimal.ZERO)
                            .setUsageFrequencyFlag(ConstantsInventory.USAGE_FREQUENCY_FLAG_CY);
                    storehouseMaterialList.add(material);
                }
            });
            if (CollectionUtil.isNotEmpty(storehouseMaterialList)) {
                invStorehouseMaterialMapper.inserts(storehouseMaterialList);
            }
        }
        return row;
    }
    /**
     * 盘点单变更
     */
    @Override
    public int change(InvInventorySheet invInventorySheet) {
        Long inventorySheetSid = invInventorySheet.getInventorySheetSid();
        InvInventorySheet inventorySheet = invInventorySheetMapper.selectInvInventorySheetById(inventorySheetSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(inventorySheet.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }

        invInventorySheetMapper.updateAllById(invInventorySheet);
        //盘点单-明细list
        List<InvInventorySheetItem> invInventorySheetItemList = invInventorySheet.getInvInventorySheetItemList();
        if (CollectionUtils.isNotEmpty(invInventorySheetItemList)) {
            invInventorySheetItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvInventorySheetItem(invInventorySheet, invInventorySheetItemList);
        }
        //盘点单-附件list
        List<InvInventorySheetAttachment> invInventorySheetAttachmentList = invInventorySheet.getInvInventorySheetAttachmentList();
        if (CollectionUtils.isNotEmpty(invInventorySheetAttachmentList)) {
            invInventorySheetAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvInventorySheetAttachment(invInventorySheet, invInventorySheetAttachmentList);
        }
        return 1;
    }

    //创建库存库位信息
    private InvInventoryLocation createLocation(InvInventorySheet invInventoryDocument, InvInventorySheetItem documentItem) {
        InvInventoryLocation location = new InvInventoryLocation();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setUnlimitedQuantity(documentItem.getCountQuantity());
        location.setCreateDate(new Date());
        location.setLatestCountDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUpdateDate(null);
        location.setPrice(new BigDecimal(0));
        location.setVendorSubcontractQuantity(new BigDecimal(0));
        location.setVendorConsignQuantity(new BigDecimal(0));
        location.setCustomerConsignQuantity(new BigDecimal(0));
        location.setCustomerSubcontractQuantity(new BigDecimal(0));
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invInventoryLocationMapper.insert(location);
        MongodbUtil.insertUserLog(location.getLocationStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }

    //创建库存库位信息
    private InvInventoryLocation createLocationCUS(InvInventorySheet invInventoryDocument, InvInventorySheetItem documentItem,String specialStock) {
        InvInventoryLocation location = new InvInventoryLocation();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setUnlimitedQuantity(new BigDecimal(0));
        if(ConstantsEms.CUS_RA.equals(specialStock)){
            location.setCustomerSubcontractQuantity(documentItem.getCountQuantity());
            location.setCustomerConsignQuantity(new BigDecimal(0));
        }else{
            location.setCustomerConsignQuantity(documentItem.getCountQuantity());
            location.setCustomerSubcontractQuantity(new BigDecimal(0));
        }
        location.setCreateDate(new Date());
        location.setLatestCountDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUpdateDate(null);
        location.setPrice(new BigDecimal(0));
        location.setVendorSubcontractQuantity(new BigDecimal(0));
        location.setVendorConsignQuantity(new BigDecimal(0));
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invInventoryLocationMapper.insert(location);
        MongodbUtil.insertUserLog(location.getLocationStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }
    //创建库存库位信息
    private InvInventoryLocation createLocationVEN(InvInventorySheet invInventoryDocument, InvInventorySheetItem documentItem,String specialStock) {
        InvInventoryLocation location = new InvInventoryLocation();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setUnlimitedQuantity(new BigDecimal(0));
        if(ConstantsEms.VEN_CU.equals(specialStock)){
            location.setVendorConsignQuantity(documentItem.getCountQuantity());
            location.setVendorSubcontractQuantity(new BigDecimal(0));
        }else{
            location.setVendorSubcontractQuantity(documentItem.getCountQuantity());
            location.setVendorConsignQuantity(new BigDecimal(0));
        }
        location.setCreateDate(new Date());
        location.setLatestCountDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUpdateDate(null);
        location.setPrice(new BigDecimal(0));
        location.setCustomerConsignQuantity(new BigDecimal(0));
        location.setCustomerSubcontractQuantity(new BigDecimal(0));
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invInventoryLocationMapper.insert(location);
        MongodbUtil.insertUserLog(location.getLocationStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }
    //创建特殊库存供应商信息
    private InvVenSpecialInventory createInvVenSpecialLocation(InvInventorySheet invInventoryDocument, InvInventorySheetItem documentItem) {
        InvVenSpecialInventory location = new InvVenSpecialInventory();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setUnlimitedQuantity(documentItem.getCountQuantity());
        location.setCreateDate(new Date());
        location.setLatestCountDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUpdateDate(null);
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invVenSpecialInventoryMapper.insert(location);
        MongodbUtil.insertUserLog(location.getVendorSpecialStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }

    //创建特殊库存客户信息
    private InvCusSpecialInventory createInvCusSpecialLocation(InvInventorySheet invInventoryDocument, InvInventorySheetItem documentItem) {
        InvCusSpecialInventory location = new InvCusSpecialInventory();
        BeanCopyUtils.copyProperties(documentItem, location);
        BeanCopyUtils.copyProperties(invInventoryDocument, location);
        location.setStorehouseSid(documentItem.getStorehouseSid());
        location.setStorehouseLocationSid(documentItem.getStorehouseLocationSid());
        location.setUnlimitedQuantity(documentItem.getCountQuantity());
        location.setCreateDate(new Date());
        location.setLatestCountDate(new Date());
        location.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        location.setUpdateDate(null);
        location.setUpdaterAccount(null);
        location.setConfirmDate(null);
        location.setConfirmerAccount(null);
        invCusSpecialInventoryMapper.insert(location);
        MongodbUtil.insertUserLog(location.getCustomerSpecialStockSid(), BusinessType.INSERT.ordinal(), null, "库存信息");
        return location;
    }

    //商品采购订单明细导出
    @Override
    public void exportGood(HttpServletResponse response, Long sid){
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("盘点单明细");
                InvInventorySheet invInventorySheet = selectInvInventorySheetById(sid);
                List<InvInventorySheetItem> invInventorySheetItemList = invInventorySheet.getInvInventorySheetItemList();
                sheet.setDefaultColumnWidth(20);
                String[] titles = {"盘点单号","单据日期","计划盘点日期","仓库名称","库位名称","特殊库存","供应商名称","客户名称"	,"备注"};
                //第一行数据
                Row rowHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleGrey(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);

                //第二行数据
                Row rowSecond = sheet.createRow(1);
                String[] titleItem={"必填","选填","选填","选填","选填","选填","选填","选填","选填"};
                for (int i=0;i<titleItem.length;i++) {
                    Cell cell = rowSecond.createCell(i);
                    cell.setCellValue(titleItem[i]);
                    cell.setCellStyle(cellStyle);
                }
                //第三行数据
                Row rowThird = sheet.createRow(2);
                //盘点单号
                Cell cell0 = rowThird.createCell(0);
                cell0.setCellValue(invInventorySheet.getInventorySheetCode());
                cell0.setCellStyle(defaultCellStyle);
                //单据日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Cell cell1 = rowThird.createCell(1);
                cell1.setCellValue(sdf.format(invInventorySheet.getDocumentDate()));
                cell1.setCellStyle(defaultCellStyle);
                //计划盘点日期
                Cell cell2 = rowThird.createCell(2);
                cell2.setCellValue(sdf.format(invInventorySheet.getPlanCountDate()));
                cell2.setCellStyle(defaultCellStyle);
                //仓库编码
                Cell cell3 = rowThird.createCell(3);
                cell3.setCellValue(invInventorySheet.getStorehouseName());
                cell3.setCellStyle(defaultCellStyle);
                //库位编码
                Cell cell4 = rowThird.createCell(4);
                cell4.setCellValue(invInventorySheet.getLocationName());
                cell4.setCellStyle(defaultCellStyle);
                //特殊库存
                Cell cell5 = rowThird.createCell(5);
                cell5.setCellValue(invInventorySheet.getSpecialStockName());
                cell5.setCellStyle(defaultCellStyle);
                //供应商简称
                Cell cell6 = rowThird.createCell(6);
                cell6.setCellValue(invInventorySheet.getVendorName());
                cell6.setCellStyle(defaultCellStyle);
                //客户简称
                Cell cell7 = rowThird.createCell(7);
                cell7.setCellValue(invInventorySheet.getCustomerName());
                cell7.setCellStyle(defaultCellStyle);
                //备注
                Cell cell8 = rowThird.createCell(8);
                cell8.setCellValue(invInventorySheet.getRemark());
                cell8.setCellStyle(defaultCellStyle);
                //第四行数据
                Row rowFour = sheet.createRow(3);
                String[] titleF={"物料/商品编码","SKU1名称","SKU2名称","数量","实盘量","备注"};
                for (int i=0;i<titleF.length;i++) {
                    Cell cell = rowFour.createCell(i);
                    cell.setCellValue(titleF[i]);
                    cell.setCellStyle(cellStyle);
                }
                //第四行数据
                Row rowFive = sheet.createRow(4);
                String[] titleFive={"必填","选填","选填","选填","必填","选填"};
                for (int i=0;i<titleFive.length;i++) {
                    Cell cell = rowFive.createCell(i);
                    cell.setCellValue(titleFive[i]);
                    cell.setCellStyle(cellStyle);
                }
                //   数据部分
                for (int i=0;i<invInventorySheetItemList.size();i++) {
                    Row row = sheet.createRow(i+5);
                    //物料编码
                    Cell cell01 = row.createCell(0);
                    cell01.setCellValue(invInventorySheetItemList.get(i).getMaterialCode());
                    cell01.setCellStyle(defaultCellStyle);
                    //SKU1名称
                    Cell cell02 = row.createCell(1);
                    cell02.setCellValue(invInventorySheetItemList.get(i).getSku1Name());
                    cell02.setCellStyle(defaultCellStyle);
                    //SKU2名称
                    Cell cell03 = row.createCell(2);
                    cell03.setCellValue(invInventorySheetItemList.get(i).getSku2Name());
                    cell03.setCellStyle(defaultCellStyle);
                    //数量
                    Cell cell05 = row.createCell(3);
                    cell05.setCellValue(invInventorySheetItemList.get(i).getStockQuantity()==null?null:invInventorySheetItemList.get(i).getStockQuantity().toString());
                    cell05.setCellStyle(defaultCellStyle);
                    //实盘量
                    Cell cell06 = row.createCell(4);
                    cell06.setCellValue(invInventorySheetItemList.get(i).getCountQuantity()==null?null:invInventorySheetItemList.get(i).getCountQuantity().toString());
                    cell06.setCellStyle(defaultCellStyle);
                    //备注
                    Cell cell07 = row.createCell(5);
                    cell07.setCellValue(invInventorySheetItemList.get(i).getRemark());
                    cell07.setCellStyle(defaultCellStyle);
                }
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                workbook.write(response.getOutputStream());
            }catch (Exception e){
                throw new CustomException("导出失败");
            }
    }

}
