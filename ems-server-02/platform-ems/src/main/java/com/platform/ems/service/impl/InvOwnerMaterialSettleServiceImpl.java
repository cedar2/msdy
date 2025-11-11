package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteFlowableService;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvOwnerMaterialSettleRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvOwnerMaterialSettleReportResponse;
import com.platform.ems.enums.DocumentCategory;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.mapper.ConSpecialStockMapper;
import com.platform.ems.service.IInvInventoryDocumentService;
import com.platform.ems.service.IInvOwnerMaterialSettleService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
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
 * 甲供料结算单Service业务层处理
 *
 * @author c
 * @date 2021-09-13
 */
@Service
@SuppressWarnings("all")
public class InvOwnerMaterialSettleServiceImpl extends ServiceImpl<InvOwnerMaterialSettleMapper,InvOwnerMaterialSettle>  implements IInvOwnerMaterialSettleService {
    @Autowired
    private InvOwnerMaterialSettleMapper invOwnerMaterialSettleMapper;
    @Autowired
    private InvOwnerMaterialSettleItemMapper itemMapper;
    @Autowired
    private InvOwnerMaterialSettleAttachMapper attachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IInvInventoryDocumentService documentService;
    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private  IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private ISysFormProcessService formProcessService;
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
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;

    private static final String table = "s_inv_owner_material_settle";

    private static final String TITLE = "甲供料结算单";
    /**
     * 查询甲供料结算单
     *
     * @param settleSid 甲供料结算单ID
     * @return 甲供料结算单
     */
    @Override
    public InvOwnerMaterialSettle selectInvOwnerMaterialSettleById(Long settleSid) {
        InvOwnerMaterialSettle invOwnerMaterialSettle = invOwnerMaterialSettleMapper.selectInvOwnerMaterialSettleById(settleSid);
        List<InvOwnerMaterialSettleItem> invOwnerMaterialSettleItems = itemMapper.selectInvOwnerMaterialSettleItemById(settleSid);
        List<InvOwnerMaterialSettleAttach> invOwnerMaterialSettleAttaches = attachMapper.selectInvOwnerMaterialSettleAttachById(settleSid);
        List<InvOwnerMaterialSettleItem> items = sort(invOwnerMaterialSettleItems,null);
        invOwnerMaterialSettle.setInvOwnerMaterialSettleItemList(items);
        invOwnerMaterialSettle.setInvOwnerMaterialSettleAttachList(invOwnerMaterialSettleAttaches);
        MongodbUtil.find(invOwnerMaterialSettle);
        return  invOwnerMaterialSettle;
    }

    @Override
    public List<InvOwnerMaterialSettleItem> sort(List<InvOwnerMaterialSettleItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvOwnerMaterialSettleItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                    List<InvOwnerMaterialSettleItem> allList = new ArrayList<>();
                    List<InvOwnerMaterialSettleItem> allThirdList = new ArrayList<>();
                    List<InvOwnerMaterialSettleItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvOwnerMaterialSettleItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvOwnerMaterialSettleItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvOwnerMaterialSettleItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvOwnerMaterialSettleItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvOwnerMaterialSettleItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvOwnerMaterialSettleItem::getMaterialCode)
                        .thenComparing(InvOwnerMaterialSettleItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvOwnerMaterialSettleItem::getMaterialCode)
                        .thenComparing(InvOwnerMaterialSettleItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return items;
        }
        return new ArrayList<>();
    }

    /**
     * 查询甲供料结算单列表
     *
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 甲供料结算单
     */
    @Override
    public List<InvOwnerMaterialSettle> selectInvOwnerMaterialSettleList(InvOwnerMaterialSettle invOwnerMaterialSettle) {
        List<String> idList = new ArrayList<>();
        if(Objects.nonNull(invOwnerMaterialSettle.getApprovalUserId())){
            FlowTaskVo task = new FlowTaskVo();
            task.setUserId(invOwnerMaterialSettle.getApprovalUserId());
            task.setDefinitionId(FormType.InvOwnerMaterialSettle.getCode());
            AjaxResult userTask = flowableService.getUserTaskList(task);
            if(!userTask.get("msg").equals("操作成功")){
                throw new CustomException(userTask.get("msg").toString());
            }
            idList = (List<String>) userTask.get("data");
            if(null==idList||0==idList.size()){
                return new ArrayList<InvOwnerMaterialSettle>();
            }
            List<Long> sidList = new ArrayList<>();
            sidList = idList.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            invOwnerMaterialSettle.setItemSidList(sidList);
        }
        List<InvOwnerMaterialSettle> list = invOwnerMaterialSettleMapper.selectInvOwnerMaterialSettleList(invOwnerMaterialSettle);
        //审批
        for(InvOwnerMaterialSettle p : list) {
            SysFormProcess formProcess = new SysFormProcess();
            formProcess.setFormId(p.getSettleSid());
            List<SysFormProcess> fpList = formProcessService.selectSysFormProcessList(formProcess);
            if(fpList!=null&&fpList.size()>0) {
                formProcess = new SysFormProcess();
                formProcess = fpList.get(0);
                p.setApprovalNode(formProcess.getApprovalNode());
                p.setApprovalUserName(formProcess.getApprovalUserName());
                p.setApprovalUserId(formProcess.getApprovalUserId());
                p.setSubmitDate(formProcess.getCreateDate());
                p.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        }
        return list;
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvOwnerMaterialSettleItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
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
     * 新增甲供料结算单
     * 需要注意编码重复校验
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvOwnerMaterialSettle(InvOwnerMaterialSettle invOwnerMaterialSettle) {
        //过账
        account(invOwnerMaterialSettle);
        int row= invOwnerMaterialSettleMapper.insert(invOwnerMaterialSettle);
        if(row>0){
            List<InvOwnerMaterialSettleItem> invOwnerMaterialSettleItemList = invOwnerMaterialSettle.getInvOwnerMaterialSettleItemList();
            if(CollectionUtil.isNotEmpty(invOwnerMaterialSettleItemList)){
                invOwnerMaterialSettleItemList.forEach(li->{
                    // 设置仓库库位CODE
                    li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                    li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                    li.setSettleSid(invOwnerMaterialSettle.getSettleSid());
                });
                setItemNum(invOwnerMaterialSettleItemList);
                itemMapper.inserts(invOwnerMaterialSettleItemList);
            }
            List<InvOwnerMaterialSettleAttach> invOwnerMaterialSettleAttachList = invOwnerMaterialSettle.getInvOwnerMaterialSettleAttachList();
            if(CollectionUtil.isNotEmpty(invOwnerMaterialSettleAttachList)){
                invOwnerMaterialSettleAttachList.forEach(li->{
                    li.setSettleSid(invOwnerMaterialSettle.getSettleSid());
                });
                attachMapper.inserts(invOwnerMaterialSettleAttachList);
            }

        }
        //待办通知
        InvOwnerMaterialSettle settle = invOwnerMaterialSettleMapper.selectById(invOwnerMaterialSettle.getSettleSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(settle.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(table)
                    .setDocumentSid(settle.getSettleSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("甲供料结算单" + settle.getSettleCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(settle.getSettleCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(invOwnerMaterialSettle);
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbUtil.insertUserLog(invOwnerMaterialSettle.getSettleSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(InvOwnerMaterialSettle invOwnerMaterialSettle) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, invOwnerMaterialSettle.getSettleSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, invOwnerMaterialSettle.getSettleSid()));
        }
    }

    /**
     * 修改甲供料结算单
     *
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvOwnerMaterialSettle(InvOwnerMaterialSettle invOwnerMaterialSettle) {
        InvOwnerMaterialSettle response = invOwnerMaterialSettleMapper.selectInvOwnerMaterialSettleById(invOwnerMaterialSettle.getSettleSid());
        //过账
        account(invOwnerMaterialSettle);
        int row=invOwnerMaterialSettleMapper.updateById(invOwnerMaterialSettle);
        if(row>0){
            //明细表
            List<InvOwnerMaterialSettleItem> itemList = invOwnerMaterialSettle.getInvOwnerMaterialSettleItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                List<InvOwnerMaterialSettleItem> purPurchasePriceItems = itemMapper.selectList(new QueryWrapper<InvOwnerMaterialSettleItem>().lambda()
                        .eq(InvOwnerMaterialSettleItem::getSettleSid, invOwnerMaterialSettle.getSettleSid())
                );
                List<Long> longs = purPurchasePriceItems.stream().map(li -> li.getSettleItemSid()).collect(Collectors.toList());
                List<Long> longsNow = itemList.stream().map(li -> li.getSettleItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if(CollectionUtil.isNotEmpty(reduce)){
                    List<InvOwnerMaterialSettleItem> reduceList = itemMapper.selectList(new QueryWrapper<InvOwnerMaterialSettleItem>().lambda()
                            .in(InvOwnerMaterialSettleItem::getSettleItemSid, reduce)
                    );
                    itemMapper.deleteBatchIds(reduce);
                }
                //修改明细
                List<InvOwnerMaterialSettleItem> exitItem = itemList.stream().filter(li -> li.getSettleItemSid() != null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(exitItem)){
                    exitItem.forEach(li->{
                        // 设置仓库库位CODE
                        li.setStorehouseCode(setStorehouseCode(li.getStorehouseSid()));
                        li.setStorehouseLocationCode(setStorehouseLocationCode(li.getStorehouseLocationSid()));
                        itemMapper.updateById(li);
                    });
                }
                //新增明细
                List<InvOwnerMaterialSettleItem> nullItem = itemList.stream().filter(li -> li.getSettleItemSid() == null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(nullItem)){
                    int max=0;
                    if(CollectionUtil.isNotEmpty(purPurchasePriceItems)){
                         max = purPurchasePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                    }
                    for (int i = 0; i < nullItem.size(); i++) {
                        // 设置仓库库位CODE
                        nullItem.get(i).setStorehouseCode(setStorehouseCode(nullItem.get(i).getStorehouseSid()));
                        nullItem.get(i).setStorehouseLocationCode(setStorehouseLocationCode(nullItem.get(i).getStorehouseLocationSid()));
                        int maxItem=max+i+1;
                        nullItem.get(i).setItemNum(maxItem);
                        nullItem.get(i).setSettleSid(invOwnerMaterialSettle.getSettleSid());
                        itemMapper.insert(nullItem.get(i));
                    }
                }
            }else{
                itemMapper.delete(new QueryWrapper<InvOwnerMaterialSettleItem>().lambda()
                        .eq(InvOwnerMaterialSettleItem::getSettleSid, invOwnerMaterialSettle.getSettleSid())
                );
            }
            //附件表
            attachMapper.delete(new QueryWrapper<InvOwnerMaterialSettleAttach>().lambda()
                    .eq(InvOwnerMaterialSettleAttach::getSettleSid,invOwnerMaterialSettle.getSettleSid()));
            List<InvOwnerMaterialSettleAttach> settleAttachList = invOwnerMaterialSettle.getInvOwnerMaterialSettleAttachList();
            if(CollectionUtil.isNotEmpty(settleAttachList)){
                settleAttachList.forEach(li->{
                    li.setSettleSid(invOwnerMaterialSettle.getSettleSid());
                });
                attachMapper.inserts(settleAttachList);
            }
            //插入日志
            MongodbUtil.insertUserLog(invOwnerMaterialSettle.getSettleSid(), BusinessType.UPDATE.getValue(), response,invOwnerMaterialSettle,TITLE);
        }
        return row;
    }
    //过账
    public void account(InvOwnerMaterialSettle invOwnerMaterialSettle){
        String handleStatus = invOwnerMaterialSettle.getHandleStatus();
        if(HandleStatus.POSTING.getCode().equals(handleStatus)){
            judgeNull(invOwnerMaterialSettle);
            invOwnerMaterialSettle.setAccountor(ApiThreadLocalUtil.get().getUsername());
            invOwnerMaterialSettle.setAccountDate(new Date());
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            BeanCopyUtils.copyProperties(invOwnerMaterialSettle,invInventoryDocument);
            List<InvOwnerMaterialSettleItem> itemList = invOwnerMaterialSettle.getInvOwnerMaterialSettleItemList();
            List<InvInventoryDocumentItem> invInventoryDocumentItems = BeanCopyUtils.copyListProperties(itemList, InvInventoryDocumentItem::new);
            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
            invInventoryDocument.setMovementType(ConstantsEms.ARMOR_FOR_MATERIALS);
            invInventoryDocument.setType(ConstantsEms.CHU_KU);
            invInventoryDocument.setReferDocCategory("RMMN");
            invInventoryDocument.setReferDocumentCode(invOwnerMaterialSettle.getSettleCode()==null?null:invOwnerMaterialSettle.getSettleCode().toString());
            invInventoryDocument.setDocumentCategory(DocumentCategory.CHK.getCode());
            invInventoryDocument.setReferDocumentSid(invOwnerMaterialSettle.getSettleSid());
            //入库
            documentService.insertInvInventoryDocument(invInventoryDocument);
        }
    }
    /**
     * 查询甲供料结算单明细报表
     *
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    @Override
    public List<InvOwnerMaterialSettleReportResponse> getReport(InvOwnerMaterialSettleRequest InvOwnerMaterialSettleRequest){
        List<InvOwnerMaterialSettleReportResponse> report = itemMapper.getReport(InvOwnerMaterialSettleRequest);
        return report;
    }

    /**
     * 变更甲供料结算单
     *
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvOwnerMaterialSettle(InvOwnerMaterialSettle invOwnerMaterialSettle) {
        InvOwnerMaterialSettle response = invOwnerMaterialSettleMapper.selectInvOwnerMaterialSettleById(invOwnerMaterialSettle.getSettleSid());
        int row=invOwnerMaterialSettleMapper.updateAllById(invOwnerMaterialSettle);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invOwnerMaterialSettle.getSettleSid(), BusinessType.CHANGE.ordinal(), response,invOwnerMaterialSettle,TITLE);
        }
        return row;
    }

    /**
     * 批量删除甲供料结算单
     *
     * @param settleSids 需要删除的甲供料结算单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvOwnerMaterialSettleByIds(List<Long> settleSids) {
        for (Long settleSid : settleSids) {
            InvOwnerMaterialSettle invOwnerMaterialSettle = new InvOwnerMaterialSettle();
            invOwnerMaterialSettle.setSettleSid(settleSid);
        }
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, settleSids));
        return invOwnerMaterialSettleMapper.deleteBatchIds(settleSids);
    }
    public void judgeNull(InvOwnerMaterialSettle invOwnerMaterialSettle){
        List<InvOwnerMaterialSettleItem> itemList = invOwnerMaterialSettle.getInvOwnerMaterialSettleItemList();
        Long storehouseLocationSid = invOwnerMaterialSettle.getStorehouseLocationSid();
        Long storehouseSid = invOwnerMaterialSettle.getStorehouseSid();
        if(storehouseSid==null){
            throw  new  CustomException("确认时，仓库不允许为空");
        }
        if(storehouseLocationSid==null){
            throw new CustomException("确认时，库位不允许为空");
        }
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(li->{
                BigDecimal quantity = li.getQuantity();
                if(quantity==null){
                    throw  new CustomException("确认时，数量不允许为空");
                }
            });
        }else{
            throw new CustomException("确认时，明细行不允许为空");
        }
    }
    /**
     * 作废
     *
     */
    @Override
    public int disuse(List<Long>  settleSids){
        int row =-1;
        List<InvInventoryDocument> invInventoryDocuments = invInventoryDocumentMapper.selectList(new UpdateWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getReferDocumentSid, settleSids)
        );
        if(CollectionUtil.isNotEmpty(invInventoryDocuments)){
            List<Long> longs = invInventoryDocuments.stream().map(li -> li.getInventoryDocumentSid()).collect(Collectors.toList());
             row = documentService.invDocumentCX(longs);
        }
        invOwnerMaterialSettleMapper.update(new InvOwnerMaterialSettle(), new UpdateWrapper<InvOwnerMaterialSettle>()
                .lambda().in(InvOwnerMaterialSettle::getSettleSid, settleSids)
                .set(InvOwnerMaterialSettle::getHandleStatus, HandleStatus.INVALID.getCode())
        );
        return row;
    }
    /**
     *更改确认状态
     * @param invOwnerMaterialSettle
     * @return
     */
    @Override
    public int check(InvOwnerMaterialSettle invOwnerMaterialSettle){
        int row=0;
        Long[] sids=invOwnerMaterialSettle.getSettleSidList();
        String handleStatus = invOwnerMaterialSettle.getHandleStatus();
        if(sids!=null&&sids.length>0){
            for(Long id:sids) {
                InvOwnerMaterialSettle materialsettle = selectInvOwnerMaterialSettleById(id);
                judgeNull(materialsettle);
                if (HandleStatus.POSTING.getCode().equals(handleStatus)) {
                    //过账
                    materialsettle.setHandleStatus(HandleStatus.POSTING.getCode());
                    account(materialsettle);
                    row =invOwnerMaterialSettleMapper.updateById(materialsettle);
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                } else {
                    InvOwnerMaterialSettle settle = new InvOwnerMaterialSettle();
                    row = invOwnerMaterialSettleMapper.update(settle, new UpdateWrapper<InvOwnerMaterialSettle>().lambda().set(InvOwnerMaterialSettle::getHandleStatus, handleStatus)
                            .in(InvOwnerMaterialSettle::getSettleSid, sids));
                }
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
            InvOwnerMaterialSettle materialsettle = selectInvOwnerMaterialSettleById(id);
            List<InvOwnerMaterialSettleItem> itemList = materialsettle.getInvOwnerMaterialSettleItemList();
            Long storehouseLocationSid = materialsettle.getStorehouseLocationSid();
            Long storehouseSid = materialsettle.getStorehouseSid();
            if(storehouseSid==null){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("仓库不允许为空");
                msgList.add(errMsgResponse);
            }
            if(storehouseLocationSid==null){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("库位不允许为空");
                msgList.add(errMsgResponse);
            }
            if(CollectionUtils.isEmpty(msgList)){
                if(CollectionUtil.isNotEmpty(itemList)){
                    List<InvOwnerMaterialSettleItem> noteItems = itemList.stream().filter(item -> item.getQuantity() == null).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(noteItems)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setMsg("明细行的数量不允许为空");
                        msgList.add(errMsgResponse);
                    }else{
                        try{
                            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
                            BeanCopyUtils.copyProperties(materialsettle,invInventoryDocument);
                            List<InvInventoryDocumentItem> invInventoryDocumentItems = BeanCopyUtils.copyListProperties(itemList, InvInventoryDocumentItem::new);
                            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
                            invInventoryDocument.setMovementType(ConstantsEms.ARMOR_FOR_MATERIALS);
                            invInventoryDocument.setType(ConstantsEms.CHU_KU);
                            invInventoryDocument.setDocumentType("CG");
                            invInventoryDocument.setDocumentCategory(DocumentCategory.CHK.getCode());
                            invInventoryDocument.setReferDocumentSid(materialsettle.getSettleSid());
                            Map<Long, Object> oldLocation = new HashMap<>();
                            invInventoryDocumentService.vatatil(oldLocation,invInventoryDocument,invInventoryDocumentItems);
                        }catch (CustomException e){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setMsg(e.getMessage());
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (!ConstantsEms.YES.equals(request.getIsStatus())) {
                        HashMap<Long, Integer> BarcodeHashMap = new HashMap<>();
                        itemList.forEach(li -> {
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
                }else{
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setMsg("明细行不允许为空");
                    msgList.add(errMsgResponse);
                }
            }
        });
        request.setMsgList(msgList);
        return request;
    }

    /**
     * 甲供料结算单
     */
    @Override
    public AjaxResult importDataInv(MultipartFile file){
        Long basStorehouseSid=null;
        Long StorehouseLocationSid=null;
        String  basStorehouseCode=null;
        String  basStorehouseName=null;
        String  locationStorehouseCode=null;
        String  locationStorehouseName=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
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
            InvOwnerMaterialSettle invOwnerMaterialSettle = new InvOwnerMaterialSettle();
            List<InvOwnerMaterialSettleItem> invOwnerMaterialSettleItems = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Code=null;
                String sku2Code=null;
                String unitBase=null;
                String materialName=null;
                Date accountDate=null;
                BigDecimal qutatil=null;
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
                        errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        String vendorCode = objects.get(0).toString();
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
                            }
                            vendorSid = Long.valueOf(basVendor.getVendorSid());
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!objects.get(1).toString().equals("甲供料结算")){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型名称，只能是甲供料结算，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("开单日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(2).toString());
                        if(!validDate){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("开单日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String account = objects.get(2).toString();
                             accountDate = DateUtil.parse(account);
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(3).toString())
                        );
                        if (basStorehouse == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(3).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                            basStorehouseCode=basStorehouse.getStorehouseCode();
                            basStorehouseName=basStorehouse.getStorehouseName();
                        }
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("库位，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(4).toString())
                        );
                        if (basStorehouseLocation == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(3).toString() + "的仓库下没有"+objects.get(4).toString()+"的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            StorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                            locationStorehouseCode=basStorehouseLocation.getLocationCode();
                            locationStorehouseName=basStorehouseLocation.getLocationName();
                        }
                    }
                    if(objects.get(5) == null || objects.get(5) == ""){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("特殊库存，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        String sep = objects.get(5).toString();
                        if(objects.get(5) != null && objects.get(5) != ""){
                            String special = specialMaps.get(objects.get(5).toString());
                            if(special==null){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("特殊库存，配置错误，导入失败");
                                msgList.add(errMsgResponse);
                            }else{
                                if(!ConstantsEms.VEN_RA.equals(special)){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("特殊库存必须是甲供料库存，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    specialStock=special;
                                }
                            }
                        }
                    }
                    invOwnerMaterialSettle
                            .setMovementType(ConstantsEms.ARMOR_FOR_MATERIALS)
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setDocumentDate(accountDate)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setDocCategory("RMM")
                            .setSpecialStock((objects.get(5)==""||objects.get(5)==null)?null:specialMaps.get(objects.get(5).toString()))
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(6)==""||objects.get(6)==null)?null:objects.get(6).toString());
                    continue;
                }
                int num=i+1;
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                // 物料/商品编码
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (materialCode == null) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                // sku1名称
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                // sku2名称
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                // 结算量
                if (objects.get(3) == null || objects.get(3) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("结算量，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validInt = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validInt){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("结算量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        qutatil=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())).divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP);
                        Double mount = Double.valueOf(objects.get(3).toString());
                        if(mount<=0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("结算量必须大于0，导入失败");
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
                InvOwnerMaterialSettleItem invOwnerMaterialSettleItem = new InvOwnerMaterialSettleItem();
                invOwnerMaterialSettleItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1Code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setBarcode(barcode)
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseCode(basStorehouseCode)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setStorehouseLocationCode(locationStorehouseCode)
                        .setQuantity(qutatil)
                        .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                invOwnerMaterialSettleItems.add(invOwnerMaterialSettleItem);
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
                return  AjaxResult.error("报错信息",msgList);
            }
            for (int i = 0; i < invOwnerMaterialSettleItems.size(); i++) {
                int nu=6+i;
                InvVenSpecialInventory invVenSpecialInventory = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                        .eq(InvVenSpecialInventory::getBarcodeSid, invOwnerMaterialSettleItems.get(i).getBarcodeSid())
                        .eq(InvVenSpecialInventory::getVendorSid,invOwnerMaterialSettle.getVendorSid())
                        .eq(InvVenSpecialInventory::getStorehouseLocationSid, invOwnerMaterialSettle.getStorehouseLocationSid())
                        .eq(InvVenSpecialInventory::getStorehouseSid, invOwnerMaterialSettle.getStorehouseSid())
                );
                if(invVenSpecialInventory==null){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(nu);
                    errMsgResponse.setMsg("仓库名"+basStorehouseName+"，库位名为"+locationStorehouseName+"下没有对应的库存，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BigDecimal unlimitedQuantity = invVenSpecialInventory.getUnlimitedQuantity();
                    if(unlimitedQuantity.compareTo(invOwnerMaterialSettleItems.get(i).getQuantity())==-1){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(nu);
                        errMsgResponse.setMsg("仓库名"+basStorehouseName+"，库位名为"+locationStorehouseName+"下库存不足，无法结算，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return  AjaxResult.error("报错信息",msgList);
            }
            invOwnerMaterialSettle.setInvOwnerMaterialSettleItemList(invOwnerMaterialSettleItems);
          insertInvOwnerMaterialSettle(invOwnerMaterialSettle);
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
