package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvCrossColorReportRequest;
import com.platform.ems.domain.dto.request.InvInventoryAdjustReportRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvCrossColorReportResponse;
import com.platform.ems.domain.dto.response.InvInventoryAdjustReportResponse;
import com.platform.ems.enums.CrossColorCode;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.mapper.ConInOutStockDocCategoryMapper;
import com.platform.ems.plug.mapper.ConMovementTypeMapper;
import com.platform.ems.service.IInvInventoryAdjustService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存调整单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-19
 */
@Service
@SuppressWarnings("all")
public class InvInventoryAdjustServiceImpl extends ServiceImpl<InvInventoryAdjustMapper,InvInventoryAdjust>  implements IInvInventoryAdjustService {
    @Autowired
    private InvInventoryAdjustMapper invInventoryAdjustMapper;
    @Autowired
    private InvInventoryAdjustItemMapper invInventoryAdjustItemMapper;
    @Autowired
    private InvInventoryAdjustAttachmentMapper invInventoryAdjustAttachmentMapper;
    @Autowired
    private  InvInventoryDocumentServiceImpl invInventoryDocumentServiceImpl;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private InvStorehouseMaterialMapper  invStorehouseMaterialMapper;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    private static final String TITLE = "库存调整单";

    /**
     * 查询库存调整单
     *
     * @param inventoryAdjustSid 库存调整单ID
     * @return 库存调整单
     */
    @Override
    public InvInventoryAdjust selectInvInventoryAdjustById(Long inventoryAdjustSid) {
        InvInventoryAdjust invInventoryAdjust = invInventoryAdjustMapper.selectInvInventoryAdjustById(inventoryAdjustSid);
        if (invInventoryAdjust == null){
            return null;
        }
        //库存调整单-明细对象
        List<InvInventoryAdjustItem> invInventoryAdjustItemList =
                invInventoryAdjustItemMapper.selectInvInventoryAdjustItemById(inventoryAdjustSid);
        //库存调整单-附件对象
        InvInventoryAdjustAttachment invInventoryAdjustAttachment = new InvInventoryAdjustAttachment();
        invInventoryAdjustAttachment.setInventoryAdjustSid(inventoryAdjustSid);
        List<InvInventoryAdjustAttachment> invInventoryAdjustAttachmentList =
                invInventoryAdjustAttachmentMapper.selectInvInventoryAdjustAttachmentList(invInventoryAdjustAttachment);
        if(CollectionUtil.isNotEmpty(invInventoryAdjustItemList)){
            invInventoryAdjustItemList.forEach(li->{
                if(li.getCurrencyAmount()!=null){
                    if(li.getCurrencyAmount().compareTo(BigDecimal.ZERO)==-1){
                        li.setFlag(ConstantsEms.MINUS_FLAG);
                    }else if(li.getCurrencyAmount().compareTo(BigDecimal.ZERO)==1){
                        li.setFlag(ConstantsEms.PLUS_FLAG);
                    }
                    li.setCurrencyAmount(li.getCurrencyAmount().abs());
                }
            });
        }
        List<InvInventoryAdjustItem> items = sort(invInventoryAdjustItemList, null);
        invInventoryAdjust.setInvInventoryAdjustItemList(items);
        invInventoryAdjust.setInvInventoryAdjustAttachmentList(invInventoryAdjustAttachmentList);
        //审批
        MongodbUtil.find(invInventoryAdjust);
        return invInventoryAdjust;
    }

    @Override
    public List<InvInventoryAdjustItem> sort(List<InvInventoryAdjustItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvInventoryAdjustItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                    List<InvInventoryAdjustItem> allList = new ArrayList<>();
                    List<InvInventoryAdjustItem> allThirdList = new ArrayList<>();
                    List<InvInventoryAdjustItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvInventoryAdjustItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvInventoryAdjustItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvInventoryAdjustItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvInventoryAdjustItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvInventoryAdjustItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvInventoryAdjustItem::getMaterialCode)
                        .thenComparing(InvInventoryAdjustItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvInventoryAdjustItem::getMaterialCode)
                        .thenComparing(InvInventoryAdjustItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return items;
        }
        return new ArrayList<>();
    }

    /**
     * 查询库存调整单列表
     *
     * @param invInventoryAdjust 库存调整单
     * @return 库存调整单
     */
    @Override
    public List<InvInventoryAdjust> selectInvInventoryAdjustList(InvInventoryAdjust invInventoryAdjust) {
        List<String> idList = new ArrayList<>();
        if(Objects.nonNull(invInventoryAdjust.getApprovalUserId())){
            FlowTaskVo task = new FlowTaskVo();
            task.setUserId(invInventoryAdjust.getApprovalUserId());
            task.setDefinitionId(FormType.InventoryAdjust.getCode());
            AjaxResult userTask = flowableService.getUserTaskList(task);
            if(!userTask.get("msg").equals("操作成功")){
                throw new CustomException(userTask.get("msg").toString());
            }
            idList = (List<String>) userTask.get("data");
            if(null==idList||0==idList.size()){
                return new ArrayList<InvInventoryAdjust>();
            }
            List<Long> sidList = new ArrayList<>();
            sidList = idList.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            invInventoryAdjust.setItemSidList(sidList);
        }
        List<InvInventoryAdjust> list = invInventoryAdjustMapper.selectInvInventoryAdjustList(invInventoryAdjust);
        //审批
        for(InvInventoryAdjust p : list) {
            SysFormProcess formProcess = new SysFormProcess();
            formProcess.setFormId(p.getInventoryAdjustSid());
            List<SysFormProcess> fpList = formProcessService.selectSysFormProcessList(formProcess);
            if(fpList!=null&&fpList.size()>0) {
                formProcess = new SysFormProcess();
                formProcess = fpList.get(0);
                p.setApprovalNode(formProcess.getApprovalNode());
                p.setApprovalUserId(formProcess.getApprovalUserId());
                p.setApprovalUserName(formProcess.getApprovalUserName());
                p.setSubmitDate(formProcess.getCreateDate());
                p.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        }
        return list;
    }
    /**
     * 复制
     */
    @Override
    public InvInventoryAdjust getCopy(Long sid){
        InvInventoryAdjust invInventoryAdjust = selectInvInventoryAdjustById(sid);
        invInventoryAdjust.setInventoryAdjustSid(null)
                .setInventoryAdjustCode(null)
                .setDocumentDate(null)
                .setAccountDate(null)
                .setHandleStatus(null)
                .setCreateDate(null)
                .setCreatorAccount(null)
                .setRemark(null);
        List<InvInventoryAdjustItem> list = invInventoryAdjust.getInvInventoryAdjustItemList();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setInventoryAdjustItemSid(null)
                        .setInventoryAdjustSid(null)
                        .setCreateDate(null)
                        .setCreatorAccount(null);
            });
        }
        return invInventoryAdjust;
    }
    /**
     * 查询库存调整单明细报表
     *
     * @param request 库存调整单请求实体
     * @return 库存调整单响应集合
     */
    @Override
    public List<InvInventoryAdjustReportResponse> reportInvInventoryAdjust(InvInventoryAdjustReportRequest request) {
        return invInventoryAdjustItemMapper.reportInvInventoryAdjust(request);
    }

    /**
     * 查询串色串码明细报表
     *
     * @param request 串色串码请求实体
     * @return 串色串码响应集合
     */
    @Override
    public List<InvCrossColorReportResponse> reportCrossColor(InvCrossColorReportRequest request) {
        List<String> movementTypeList = request.getMovementTypeList();
        if(CollectionUtils.isEmpty(movementTypeList)){
            //所有串色串码对应的code
            List<String> codeList = new ArrayList<>();
            for (CrossColorCode e : CrossColorCode.values()) {
                codeList.add(e.getCode());
            }
            request.setMovementTypeList(codeList);
        }
        return invInventoryAdjustItemMapper.reportInvCrossColor(request);
    }

    /**
     * 新增库存调整单
     * 需要注意编码重复校验
     * @param invInventoryAdjust 库存调整单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryAdjust(InvInventoryAdjust invInventoryAdjust) {
        int row=0;
        String handleStatus = invInventoryAdjust.getHandleStatus();
        //库存调整单-明细list
        List<InvInventoryAdjustItem> invInventoryAdjustItemList = invInventoryAdjust.getInvInventoryAdjustItemList();
        row=invInventoryAdjustMapper.insert(invInventoryAdjust);
        if (CollectionUtils.isNotEmpty(invInventoryAdjustItemList)) {
            setItemNum(invInventoryAdjustItemList);
            invInventoryAdjustItemList.forEach(li->{
                if(li.getCurrencyAmount()!=null&&ConstantsEms.MINUS_FLAG.equals(li.getFlag())){
                    li.setCurrencyAmount(li.getCurrencyAmount().abs().multiply(new BigDecimal("-1")));
                }
            });
            addInvInventoryAdjustItem(invInventoryAdjust, invInventoryAdjustItemList);
        }
        //库存调整单-附件list
        List<InvInventoryAdjustAttachment> invInventoryAdjustAttachmentList = invInventoryAdjust.getInvInventoryAdjustAttachmentList();
        if (CollectionUtils.isNotEmpty(invInventoryAdjustAttachmentList)) {
            addInvInventoryAdjustAttachment(invInventoryAdjust, invInventoryAdjustAttachmentList);
        }
        //待办通知
        InvInventoryAdjust adjust = invInventoryAdjustMapper.selectById(invInventoryAdjust.getInventoryAdjustSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(adjust.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentSid(invInventoryAdjust.getInventoryAdjustSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("库存调整单" + adjust.getInventoryAdjustCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(adjust.getInventoryAdjustCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invInventoryAdjust);
        }
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventoryAdjust.getInventoryAdjustSid(), BusinessType.INSERT.getValue(),TITLE);
        }
        return row;
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvInventoryAdjustItem> list){
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
    private void checkTodoExist(InvInventoryAdjust invInventoryAdjust) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invInventoryAdjust.getInventoryAdjustSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invInventoryAdjust.getInventoryAdjustSid()));
        }
    }
    /**
     * 确认时 校验
     */
    public  void JudgeNull(InvInventoryAdjust invInventoryAdjust){
        List<InvInventoryAdjustItem> list = invInventoryAdjust.getInvInventoryAdjustItemList();
        String code = invInventoryAdjust.getMovementType();
        if(CollectionUtils.isEmpty(list)){
            throw  new CustomException("确认时，明细行不允许为空");
        }else{
            if(!exitAjustPrice(code)){
                list.forEach(item->{
                    if(item.getQuantity()==null){
                        throw  new CustomException("确认时，明细数量不能为空");
                    }
                });
            }else{
                list.forEach(item->{
                    if(item.getPrice()==null){
                        throw  new CustomException("确认时，明细价格不能为空");
                    }
                });
            }
        }

    }

    /**
     * 库存调整单-明细对象
     */
    private void addInvInventoryAdjustItem(InvInventoryAdjust invInventoryAdjust, List<InvInventoryAdjustItem> invInventoryAdjustItemList) {
        invInventoryAdjustItemMapper.delete(
                new UpdateWrapper<InvInventoryAdjustItem>()
                        .lambda()
                        .eq(InvInventoryAdjustItem::getInventoryAdjustSid, invInventoryAdjust.getInventoryAdjustSid())
        );
        String movementType = invInventoryAdjust.getMovementType();
        if(exitAdjust(movementType)){
            invInventoryAdjustItemList.forEach(
                    o->{
                        BasMaterialBarcode basMaterialBarcode = new BasMaterialBarcode();
                        basMaterialBarcode.setSku1Sid(o.getDestSku1Sid());
                        basMaterialBarcode.setSku2Sid(o.getDestSku2Sid());
                        basMaterialBarcode.setMaterialSid(o.getMaterialSid());
                        List<BasMaterialBarcode> basMaterialBarcodes = basMaterialBarcodeMapper.selectBasMaterialBarcodeList(basMaterialBarcode);
                        o.setDestBarcodeSid(basMaterialBarcodes.get(0).getBarcodeSid());
                    }
            );
        }
        invInventoryAdjustItemList.forEach(o -> {
            o.setInventoryAdjustSid(invInventoryAdjust.getInventoryAdjustSid());
            invInventoryAdjustItemMapper.insert(o);
        });
    }

    /**
     * 库存调整单-附件对象
     */
    private void addInvInventoryAdjustAttachment(InvInventoryAdjust invInventoryAdjust, List<InvInventoryAdjustAttachment> invInventoryAdjustAttachmentList) {
        invInventoryAdjustAttachmentMapper.delete(
                new UpdateWrapper<InvInventoryAdjustAttachment>()
                        .lambda()
                        .eq(InvInventoryAdjustAttachment::getInventoryAdjustSid, invInventoryAdjust.getInventoryAdjustSid())
        );
        invInventoryAdjustAttachmentList.forEach(o -> {
            o.setInventoryAdjustSid(invInventoryAdjust.getInventoryAdjustSid());
            invInventoryAdjustAttachmentMapper.insert(o);
        });
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(InvInventoryAdjust o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改库存调整单
     *
     * @param invInventoryAdjust 库存调整单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryAdjust(InvInventoryAdjust invInventoryAdjust) {
        String handleStatus = invInventoryAdjust.getHandleStatus();
        if(handleStatus.equals(ConstantsEms.CHECK_STATUS)){
            JudgeNull(invInventoryAdjust);
        }
        //新增库存调整单
        int row=invInventoryAdjustMapper.updateAllById(invInventoryAdjust);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventoryAdjust.getInventoryAdjustSid(), BusinessType.UPDATE.getValue(),TITLE);
        }
        //库存调整单-明细list
        List<InvInventoryAdjustItem> invInventoryAdjustItemList = invInventoryAdjust.getInvInventoryAdjustItemList();
        if (CollectionUtils.isNotEmpty(invInventoryAdjustItemList)) {
            invInventoryAdjustItemList.forEach(li->{
                    if(li.getCurrencyAmount()!=null&&ConstantsEms.MINUS_FLAG.equals(li.getFlag())){
                        li.setCurrencyAmount(li.getCurrencyAmount().abs().multiply(new BigDecimal("-1")));
                    }
                });
            invInventoryAdjustItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            setItemNum(invInventoryAdjustItemList);
            addInvInventoryAdjustItem(invInventoryAdjust, invInventoryAdjustItemList);
        }else{
            invInventoryAdjustItemMapper.delete(
                    new UpdateWrapper<InvInventoryAdjustItem>()
                            .lambda()
                            .eq(InvInventoryAdjustItem::getInventoryAdjustSid, invInventoryAdjust.getInventoryAdjustSid())
            );
        }
        //库存调整单-附件list
        List<InvInventoryAdjustAttachment> invInventoryAdjustAttachmentList = invInventoryAdjust.getInvInventoryAdjustAttachmentList();
        if (CollectionUtils.isNotEmpty(invInventoryAdjustAttachmentList)) {
            invInventoryAdjustAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvInventoryAdjustAttachment(invInventoryAdjust, invInventoryAdjustAttachmentList);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(invInventoryAdjust.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(invInventoryAdjust);
        }
        return 1;
    }

    /**
     * 批量删除库存调整单
     *
     * @param inventoryAdjustSids 需要删除的库存调整单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventoryAdjustByIds(Long[] inventoryAdjustSids) {
        //删除库存调整单
        invInventoryAdjustMapper.deleteInvInventoryAdjustByIds(inventoryAdjustSids);
        //删除库存调整单明细
        invInventoryAdjustItemMapper.deleteInvInventoryAdjustItemByIds(inventoryAdjustSids);
        //删除库存调整单附件
        invInventoryAdjustAttachmentMapper.deleteInvInventoryAdjustAttachmentByIds(inventoryAdjustSids);
        for (Long inventoryAdjustSid : inventoryAdjustSids) {
            InvInventoryAdjust invInventoryAdjust = new InvInventoryAdjust();
            invInventoryAdjust.setInventoryAdjustSid(inventoryAdjustSid);
            //校验是否存在待办
            checkTodoExist(invInventoryAdjust);
            //插入日志
            MongodbUtil.insertUserLog(inventoryAdjustSid, BusinessType.DELETE.getValue(),TITLE);
        }
        return inventoryAdjustSids.length;
    }

    /**
     * 库存调整单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(InvInventoryAdjust invInventoryAdjust) {
        //库存调整单sids
        Long[] inventoryAdjustSids = invInventoryAdjust.getInventoryAdjustSids();
        int row=invInventoryAdjustMapper.update(new InvInventoryAdjust(),new UpdateWrapper<InvInventoryAdjust>().lambda()
        .in(InvInventoryAdjust::getInventoryAdjustSid,inventoryAdjustSids)
                .set(InvInventoryAdjust::getHandleStatus,invInventoryAdjust.getHandleStatus())
                .set(InvInventoryAdjust::getConfirmDate,new Date())
                .set(InvInventoryAdjust::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
        );
        //生成库存凭证
        for (Long sid : inventoryAdjustSids) {
            InvInventoryAdjust invInventoryAdjustNow = selectInvInventoryAdjustById(sid);
            //校验是否存在待办
            checkTodoExist(invInventoryAdjust);
            if(HandleStatus.POSTING.getCode().equals(invInventoryAdjustNow.getHandleStatus())){
                List<InvInventoryAdjustItem> invInventoryAdjustItemList = invInventoryAdjust.getInvInventoryAdjustItemList();
                if (CollectionUtil.isNotEmpty(invInventoryAdjustItemList)) {
                    invInventoryAdjustItemList.forEach(li->{
                        if(li.getCurrencyAmount()!=null&&ConstantsEms.MINUS_FLAG.equals(li.getFlag())){
                            li.setCurrencyAmount(li.getCurrencyAmount().abs().multiply(new BigDecimal("-1")));
                        }
                    });
                }
                //过账
                copyDocumnet(invInventoryAdjustNow);
                invInventoryAdjustNow.setAccountDate(new Date());
                invInventoryAdjustNow.setAccountor(ApiThreadLocalUtil.get().getUsername());
                invInventoryAdjustMapper.updateAllById(invInventoryAdjustNow);
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
            InvInventoryAdjust invInventoryAdjustNow = selectInvInventoryAdjustById(id);
            List<InvInventoryAdjustItem> list = invInventoryAdjustNow.getInvInventoryAdjustItemList();
            String code = invInventoryAdjustNow.getMovementType();
            if(CollectionUtils.isEmpty(list)){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不允许为空");
                msgList.add(errMsgResponse);
            }else{
                if(ConstantsEms.ADJUST_CUR.equals(invInventoryAdjustNow.getMovementType())){
                    List<InvInventoryAdjustItem> currencyAmountList = list.stream().filter(li -> li.getCurrencyAmount() == null).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(currencyAmountList)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setMsg("金额不能为空");
                        msgList.add(errMsgResponse);
                    }
                }else{
                    List<InvInventoryAdjustItem> priceList = list.stream().filter(li -> li.getPrice() == null).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(priceList)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setMsg("明细价格不能为空");
                        msgList.add(errMsgResponse);
                    }
                }
                list.forEach(item->{
                    List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new UpdateWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getStorehouseSid, invInventoryAdjustNow.getStorehouseSid())
                            .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    );
                    if(CollectionUtil.isEmpty(invInventoryLocations)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setMsg("仓库"+item.getStorehouseName()+"中没有行号"+item.getItemNum()+"的物料/商品"+item.getMaterialName()+"，请核实");
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
        });
        request.setMsgList(msgList);
        return request;
    }

    //库存调整单-过账
    public void copyDocumnet(InvInventoryAdjust invInventoryAdjust){
        List<InvInventoryAdjustItem> invInventoryAdjustItemList = invInventoryAdjust.getInvInventoryAdjustItemList();
        String movementType = invInventoryAdjust.getMovementType();
        //调金额
        if(ConstantsEms.ADJUST_CUR.equals(movementType)){
            invInventoryAdjustItemList.forEach(li->{
                List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new UpdateWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getStorehouseSid, li.getStorehouseSid())
                        .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                );
                InvStorehouseMaterial invStorehouseMaterial = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                        .eq(InvStorehouseMaterial::getStorehouseSid,li.getStorehouseSid())
                        .eq(InvStorehouseMaterial::getBarcodeSid, li.getBarcodeSid())
                );
                BigDecimal price=BigDecimal.ZERO;
                BigDecimal totalQutatily=invInventoryLocations.stream().map(m -> m.getUnlimitedQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(totalQutatily.compareTo(BigDecimal.ZERO)!=0){
                    BigDecimal total= invInventoryLocations.stream().map(m -> {
                                if(invStorehouseMaterial!=null){
                                    return invStorehouseMaterial.getPrice().multiply(m.getUnlimitedQuantity());
                                }else{
                                    return BigDecimal.ZERO;
                                }
                    }
                    ).reduce(BigDecimal.ZERO, BigDecimal::add);
                    total=total.add(li.getCurrencyAmount());
                    price=total.divide(totalQutatily,5,BigDecimal.ROUND_HALF_UP);
                    invInventoryLocationMapper.update(new InvInventoryLocation(),new UpdateWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getStorehouseSid,li.getStorehouseSid())
                            .eq(InvInventoryLocation::getBarcodeSid,li.getBarcodeSid())
                            .set(InvInventoryLocation::getPrice,price)
                    );
                    changeInvMaterialPrice(li,li.getStorehouseSid(),price);
                }
            });
        }else{
            //调价格
            invInventoryAdjustItemList.forEach(li->{
                invInventoryLocationMapper.update(new InvInventoryLocation(),new UpdateWrapper<InvInventoryLocation>().lambda()
                .eq(InvInventoryLocation::getStorehouseSid,invInventoryAdjust.getStorehouseSid())
                        .eq(InvInventoryLocation::getBarcodeSid,li.getBarcodeSid())
                        .set(InvInventoryLocation::getPrice,li.getPrice())
                );
                changeInvMaterialPrice(li,invInventoryAdjust.getStorehouseSid(),li.getPrice());
            });
        }
    }
    //改变仓库物料信息
    public void changeInvMaterialPrice(InvInventoryAdjustItem item,Long StorehouseSid,BigDecimal price){
        InvStorehouseMaterial invStorehouseMaterial = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                .eq(InvStorehouseMaterial::getStorehouseSid,StorehouseSid)
                .eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid())
        );
        if(invStorehouseMaterial==null){
            InvStorehouseMaterial storehouseMaterial = new InvStorehouseMaterial();
            storehouseMaterial.setStorehouseSid(StorehouseSid)
                    .setPrice(price)
                    .setMaterialSid(item.getMaterialSid())
                    .setSku1Sid(item.getSku1Sid())
                    .setSku2Sid(item.getSku2Sid())
                    .setBarcodeSid(item.getBarcodeSid());
            storehouseMaterial.setCreateDate(new Date());
            storehouseMaterial.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            storehouseMaterial.setUpdateDate(null);
            storehouseMaterial.setUpdaterAccount(null);
            storehouseMaterial.setConfirmDate(null);
            storehouseMaterial.setConfirmerAccount(null);
            invStorehouseMaterialMapper.insert(storehouseMaterial);
        }else{
            invStorehouseMaterial.setPrice(price);
            invStorehouseMaterial.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            invStorehouseMaterial.setUpdateDate(new Date());
            invStorehouseMaterialMapper.updateById(invStorehouseMaterial);
        }
    }
    //获取原先仓库的价格
    public  void  setPrice(InvInventoryDocument invInventoryDocument,List<InvInventoryDocumentItem> invInventoryDocumentItemList){
        invInventoryDocumentItemList.forEach(item-> {
            InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvInventoryLocation::getStorehouseLocationSid, invInventoryDocument.getStorehouseLocationSid())
                    .eq(InvInventoryLocation::getStorehouseSid, invInventoryDocument.getStorehouseSid())
            );
            if(location!=null){
                item.setPrice(location.getPrice());
            }
        });
    }
    //直接修改价格或金额
    public void directUpdatePrice(List<InvInventoryAdjustItem> invInventoryAdjustItemList,InvInventoryDocument invInventoryDocument){
        List<InvInventoryDocumentItem> itemListOld = invInventoryDocument.getInvInventoryDocumentItemList();
        ArrayList<InvInventoryDocumentItem> documentItems = new ArrayList<>();
        //修改金额
        String code = invInventoryDocument.getMovementType();
        invInventoryAdjustItemList.forEach(item->{
            InvInventoryLocation location = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvInventoryLocation::getStorehouseLocationSid, invInventoryDocument.getStorehouseLocationSid())
                    .eq(InvInventoryLocation::getStorehouseSid, invInventoryDocument.getStorehouseSid())
            );
            //调金额-减少
            if(code.equals(ConstantsEms.ADJUST_RED_MO)){
                item.setPrice(item.getPrice().multiply(new BigDecimal(-1)));
            }
            BigDecimal price = ((location.getPrice().multiply(location.getUnlimitedQuantity())).add(item.getPrice())).divide(location.getUnlimitedQuantity(),2);
            if(price.compareTo(new BigDecimal(0))==-1){
                throw new CustomException("金额的减少量大于自身金额，请调低金额数目后重试");
            }
            location.setPrice(price);
            invInventoryLocationMapper.updateById(location);
        });
    }

    /**
     * 库存调整单变更
     */
    @Override
    public int change(InvInventoryAdjust invInventoryAdjust) {
        Long inventoryAdjustSid = invInventoryAdjust.getInventoryAdjustSid();
        InvInventoryAdjust inventoryAdjust = invInventoryAdjustMapper.selectInvInventoryAdjustById(inventoryAdjustSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(inventoryAdjust.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        invInventoryAdjustMapper.updateAllById(invInventoryAdjust);
        //库存调整单-明细list
        List<InvInventoryAdjustItem> invInventoryAdjustItemList = invInventoryAdjust.getInvInventoryAdjustItemList();
        if (CollectionUtils.isNotEmpty(invInventoryAdjustItemList)) {
            invInventoryAdjustItemList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvInventoryAdjustItem(invInventoryAdjust, invInventoryAdjustItemList);
        }
        //库存调整单-附件list
        List<InvInventoryAdjustAttachment> invInventoryAdjustAttachmentList = invInventoryAdjust.getInvInventoryAdjustAttachmentList();
        if (CollectionUtils.isNotEmpty(invInventoryAdjustAttachmentList)) {
            invInventoryAdjustAttachmentList.stream().forEach(o ->{
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addInvInventoryAdjustAttachment(invInventoryAdjust, invInventoryAdjustAttachmentList);
        }
        return 1;
    }
    //是否是库存调整-串色串码
    public Boolean exitAdjust(String code){
        //串色串码
        List<String> transferList = Arrays.asList("ST041", "ST042", "ST043","ST044");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }
    //是否是库存调整-普通调整
    public Boolean exitAjustCommon(String code){
        //普通调整
        List<String> transferList = Arrays.asList("ST011", "ST012", "ST021","ST022","ST031","ST032");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }
    //是否是库存调整-调价格或调金额
    public Boolean exitAjustPrice(String code){
        //普通调整
        List<String> transferList = Arrays.asList("ST021", "ST022", "ST031","ST032");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }

    /**
     * 库存调整导入
     */
    @Override
    public AjaxResult importDataInv(MultipartFile file){
        Long basStorehouseSid=null;
        String basStorehouseCode=null;
        Long StorehouseLocationSid=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
        String moveType=null;
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
            InvInventoryAdjust invInventoryAdjust = new InvInventoryAdjust();
            List<InvInventoryAdjustItem> invInventoryDocumentItems = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String sku1Name=null;
                String sku1code=null;
                String sku2Name=null;
                String sku2Code=null;
                String materialName=null;
                BigDecimal price=null;
                Date accountDate=null;
                String unitBase=null;
                BigDecimal currencyAmount=null;
                BasMaterialBarcode basMaterialBarcode=null;
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
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                                .eq(ConMovementType::getName,objects.get(0).toString())
                        );
                        if(conMovementType==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(conMovementType.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conMovementType.getStatus())){
                                //throw new BaseException("第"+num+"行,物料/商品必须是确认且已启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("作业类型必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                if(!ConstantsEms.ADJUST_CUR.equals(conMovementType.getCode())&&!ConstantsEms.ADJUST_PRI.equals(conMovementType.getCode())){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("该作业类型，不属于库存调整，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    moveType=conMovementType.getCode();
                                }
                            }
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("开单日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(1).toString());
                        if(!validDate){
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
                    if(objects.get(3) != null && objects.get(3) != ""){
                        boolean validInt = JudgeFormat.isValidInt(objects.get(3).toString());
                        if(!validInt){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("参考作业单号格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    invInventoryAdjust
                            .setHandleStatus(HandleStatus.SAVE.getCode())
                            .setDocumentType("KCTZ")
                            .setMovementType(moveType)
                            .setStorehouseCode(basStorehouseCode)
                            .setStorehouseSid(basStorehouseSid)
                            .setReferDocument((objects.get(3)==""||objects.get(3)==null)?null:objects.get(3).toString())
                            .setDocumentDate(accountDate)
                            .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                if (objects.get(0) == null || objects.get(0) == "") {
                    // throw new BaseException("第"+num+"行,物料/商品编码不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial==null) {
                        // throw new BaseException("第"+num+"行,没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())){
                            //throw new BaseException("第"+num+"行,物料/商品必须是确认且已启用状态，导入失败");
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
                    //  throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("SKU1名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(1).toString())
                    );
                    if (basSku==null) {
                        // throw new BaseException("第"+num+"行,没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus())){
                            //  throw new BaseException("第"+num+"行,SKU1名称必须是确认且已启用状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU1名称必须是确认且已启用状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        sku1Name=basSku.getSkuName();
                        sku1code=basSku.getSkuCode();
                        sku1Sid=basSku.getSkuSid();
                    }
                }
                if (objects.get(2) != null && objects.get(2) != "") {
                    BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(2).toString())
                    );
                    if (basSku2==null) {
                        // throw new BaseException("第"+num+"行,没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(basSku2.getSkuType().equals(ConstantsEms.SKUTYP_YS)){
                            //throw new BaseException("第"+num+"行,SKU2名称不能是颜色类型，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("SKU2名称不能是颜色类型，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku2.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku2.getStatus())){
                            //  throw new BaseException("第"+num+"行,SKU2名称必须是确认且已启用状态，导入失败");
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
                if (moveType != null) {
                    if (ConstantsEms.ADJUST_CUR.equals(moveType)) {
                        if (objects.get(3) != null && objects.get(3) != "") {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("作业类型为调金额时，库存价必须为空");
                            msgList.add(errMsgResponse);
                        }
                        if (objects.get(4) == null || objects.get(4) == "") {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("作业类型为调金额时，金额不能为空");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(ConstantsEms.ADJUST_PRI.equals(moveType)){
                        if(objects.get(4) != null && objects.get(4) != ""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("作业类型为调价时，金额必须为空");
                            msgList.add(errMsgResponse);
                        }
                        if(objects.get(3) == null || objects.get(3) == ""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("作业类型为调价时，价格不能为空");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(3) != null && objects.get(3) != "") {
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,库存价格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("库存价格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(objects.get(3)!=""&&objects.get(3)!=null){
                            Double mountPrice = Double.valueOf(objects.get(3).toString());
                            if(mountPrice<=0){
                                // throw new BaseException("第"+num+"行的库存价小于0，不允许导入，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("库存价小于等于0，不允许导入，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                        price= BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())).divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (objects.get(4) != null && objects.get(4) != "") {
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(4).toString());
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,库存价格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("金额格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        currencyAmount= BigDecimal.valueOf(Double.valueOf(objects.get(4).toString())).divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
                        if(currencyAmount.compareTo(BigDecimal.ZERO)==0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("金额等于0，不允许导入，导入失败");
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
                        //throw new BaseException("第"+num+"行对应的商品条码必须已启用的状态，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("对应的商品条码必须已启用的状态，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    barcodeSid=basMaterialBarcode.getBarcodeSid();
                    bardCodeList.add(barcodeSid);
                }
                InvInventoryAdjustItem invInventoryDocumentItem = new InvInventoryAdjustItem();
                invInventoryDocumentItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setMaterialSid(materialSid)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseCode(basStorehouseCode)
                        .setMaterialCode(objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString())
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setPrice(objects.get(3)==null||objects.get(3)==""?null:price)
                        .setCurrencyAmount(objects.get(4)==null||objects.get(4)==""?null:currencyAmount)
                        .setRemark((objects.get(5)==""||objects.get(5)==null)?null:objects.get(5).toString());
                invInventoryDocumentItems.add(invInventoryDocumentItem);
            }
            invInventoryAdjust.setInvInventoryAdjustItemList(invInventoryDocumentItems);
            if(CollectionUtil.isNotEmpty(bardCodeList)){
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
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            List<InvInventoryAdjustItem> invInventoryAdjustItemList = invInventoryAdjust.getInvInventoryAdjustItemList();
            if(CollectionUtil.isNotEmpty(invInventoryAdjustItemList)){
                for (int i = 0; i < invInventoryAdjustItemList.size(); i++) {
                    int nu=i+1+5;
                    List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getStorehouseSid, invInventoryAdjust.getStorehouseSid())
                            .eq(InvInventoryLocation::getBarcodeSid, invInventoryAdjustItemList.get(i).getBarcodeSid()));
                    if(CollectionUtil.isEmpty(invInventoryLocations)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(nu);
                        errMsgResponse.setMsg("编码为"+invInventoryAdjust.getStorehouseCode()+"的仓库下没有对应的库存，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    String movementType = invInventoryAdjust.getMovementType();
                    if(ConstantsEms.ADJUST_CUR.equals(movementType)){
                     if(CollectionUtil.isNotEmpty(invInventoryLocations)){
                         BigDecimal sum = invInventoryLocations.stream().map(InvInventoryLocation::getUnlimitedQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                         if(BigDecimal.ZERO.compareTo(sum)==0){
                             CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                             errMsgResponse.setItemNum(nu);
                             errMsgResponse.setMsg("编码为" + invInventoryAdjust.getStorehouseCode() + "的仓库下库存为0，导入失败");
                             msgList.add(errMsgResponse);
                         }
                     }
                    }
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            insertInvInventoryAdjust(invInventoryAdjust);
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
