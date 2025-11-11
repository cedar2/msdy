package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManMonthManufacturePlanRequest;
import com.platform.ems.domain.dto.response.ManProduceConcernTaskResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IManMonthManufacturePlanService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 生产月计划Service业务层处理
 *
 * @author linhongwei
 * @date 2021-07-16
 */
@Service
@SuppressWarnings("all")
public class ManMonthManufacturePlanServiceImpl extends ServiceImpl<ManMonthManufacturePlanMapper, ManMonthManufacturePlan> implements IManMonthManufacturePlanService {
    @Autowired
    private ManMonthManufacturePlanMapper manMonthManufacturePlanMapper;
    @Autowired
    private ManMonthManufacturePlanItemMapper manMonthManufacturePlanItemMapper;
    @Autowired
    private ManMonthManufacturePlanAttachMapper manMonthManufacturePlanAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private ManProduceConcernTaskGroupMapper manProduceConcernTaskGroupMapper;
    @Autowired
    private ManMonthManufacturePlanProcessMapper  manMonthManufacturePlanProcessMapper;
    @Autowired
    private ManMonthManufacturePlanBanzuRemarkMapper manMonthManufacturePlanBanzuRemarkMapper;
    @Autowired
    private ManProcessRouteMapper  manProcessRouteMapper;
    @Autowired
    private  ManManufactureOrderConcernTaskMapper  manManufactureOrderConcernTaskMapper;
    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;



    private static final String TITLE = "生产月计划";

    /**
     * 查询生产月计划
     *
     * @param monthManufacturePlanSid 生产月计划ID
     * @return 生产月计划
     */
    @Override
    public ManMonthManufacturePlan selectManMonthManufacturePlanById(Long monthManufacturePlanSid) {
        ManMonthManufacturePlan manMonthManufacturePlan = manMonthManufacturePlanMapper.selectManMonthManufacturePlanById(monthManufacturePlanSid);
        if (manMonthManufacturePlan == null) {
            return null;
        }
        List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList =
                manMonthManufacturePlanItemMapper.selectManMonthManufacturePlanItemList(
                        new ManMonthManufacturePlanItem().setMonthManufacturePlanSid(monthManufacturePlanSid));
        if(CollectionUtil.isNotEmpty(manMonthManufacturePlanItemList)){
            manMonthManufacturePlanItemList.forEach(li->{
                if(manMonthManufacturePlan.getConcernTaskGroupSid()!=null){
                    List<ManProduceConcernTaskResponse> manProduceConcernTaskList = manProduceConcernTaskGroupMapper.addItem(manMonthManufacturePlan.getConcernTaskGroupSid(),li.getManufactureOrderCode());
                    li.setManProduceConcernList(manProduceConcernTaskList);
                }
                List<ManMonthManufacturePlanProcess> manMonthManufacturePlanProcesses = manMonthManufacturePlanProcessMapper.selectList(new QueryWrapper<ManMonthManufacturePlanProcess>().lambda()
                        .eq(ManMonthManufacturePlanProcess::getMonthManufacturePlanItemSid, li.getMonthManufacturePlanItemSid())
                );
                li.setManufacturePlanProcessList(manMonthManufacturePlanProcesses);
            });
            Set<Long> codeSet = manMonthManufacturePlanItemList.stream().map(li -> li.getManufactureOrderCode()).collect(Collectors.toSet());
            ManManufactureOrderConcernTask manManufactureOrderConcernTask = new ManManufactureOrderConcernTask();
            manManufactureOrderConcernTask.setManufactureOrderCodeSet(codeSet);
            List<ManManufactureOrderConcernTask> manManufactureOrderConcernTasks = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(manManufactureOrderConcernTask);
            manMonthManufacturePlan.setManufactureOrderConcernTaskList(manManufactureOrderConcernTasks);
        }
        List<ManMonthManufacturePlanBanzuRemark> manMonthManufacturePlanBanzuRemarks = manMonthManufacturePlanBanzuRemarkMapper.selectManMonthManufacturePlanBanzuRemarkById(monthManufacturePlanSid);
        List<ManMonthManufacturePlanAttach> manMonthManufacturePlanAttachList =
                manMonthManufacturePlanAttachMapper.selectManMonthManufacturePlanAttachList(
                        new ManMonthManufacturePlanAttach().setMonthManufacturePlanSid(monthManufacturePlanSid));
        manMonthManufacturePlan.setPlanBanzuRemarkList(manMonthManufacturePlanBanzuRemarks);
        manMonthManufacturePlan.setManMonthManufacturePlanItemList(manMonthManufacturePlanItemList);
        manMonthManufacturePlan.setManMonthManufacturePlanAttachList(manMonthManufacturePlanAttachList);
        MongodbUtil.find(manMonthManufacturePlan);
        return manMonthManufacturePlan;
    }

    /**
     * 查询生产月计划列表
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 生产月计划
     */
    @Override
    public List<ManMonthManufacturePlan> selectManMonthManufacturePlanList(ManMonthManufacturePlan manMonthManufacturePlan) {
        return manMonthManufacturePlanMapper.selectManMonthManufacturePlanList(manMonthManufacturePlan);
    }
    /**
     * 查询生产月计划添加明细-行转列
     *
     */
    @Override
    public List<ManMonthManufacturePlanItem> addItem(ManMonthManufacturePlanRequest request){
        List<ManMonthManufacturePlanItem> itemList = request.getItemList();
        itemList.forEach(item->{
            ManMonthManufacturePlanItem manMonthManufacturePlanItem = new ManMonthManufacturePlanItem();
            manMonthManufacturePlanItem.setManufactureOrderSid(item.getManufactureOrderSid())
                    .setWorkCenterSid(request.getWorkCenterSid())
                    .setSku1Sid(item.getSku1Sid());
            ManMonthManufacturePlanItem man = manMonthManufacturePlanItemMapper.getQuantityFenpei(manMonthManufacturePlanItem);
            if(man!=null){
                item.setQuantityFenpei(man.getQuantityFenpei());
            }
            List<ManProduceConcernTaskResponse> manProduceConcerns = manProduceConcernTaskGroupMapper.addItem(request.getConcernTaskGroupSid(),item.getManufactureOrderCode());
            List<ManMonthManufacturePlanProcess> manMonthManufacturePlanProcesses = manProcessRouteMapper.addItem(request.getProcessRouteSid(),item.getManufactureOrderSid(),request.getWorkCenterSid());
            item.setManProduceConcernList(manProduceConcerns);
            item.setManufacturePlanProcessList(manMonthManufacturePlanProcesses);
        });
        return itemList;
    }

    /**
     * 新增生产月计划
     * 需要注意编码重复校验
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManMonthManufacturePlan(ManMonthManufacturePlan manMonthManufacturePlan) {
        setConfirmInfo(manMonthManufacturePlan);
        judge(manMonthManufacturePlan);
        int row = manMonthManufacturePlanMapper.insert(manMonthManufacturePlan);
        if (row > 0) {
            //生产月计划-明细
            List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList = manMonthManufacturePlan.getManMonthManufacturePlanItemList();
            if (CollectionUtils.isNotEmpty(manMonthManufacturePlanItemList)) {
                changeDate(manMonthManufacturePlanItemList);
                addManMonthManufacturePlanItem(manMonthManufacturePlan, manMonthManufacturePlanItemList);
            }
            //生产月计划-附件对象
            List<ManMonthManufacturePlanAttach> manMonthManufacturePlanAttachList = manMonthManufacturePlan.getManMonthManufacturePlanAttachList();
            if (CollectionUtils.isNotEmpty(manMonthManufacturePlanAttachList)) {
                addManMonthManufacturePlanAttach(manMonthManufacturePlan, manMonthManufacturePlanAttachList);
            }
            addRemarkItems( manMonthManufacturePlan,manMonthManufacturePlan.getPlanBanzuRemarkList());
            ManMonthManufacturePlan manufacturePlan = manMonthManufacturePlanMapper.selectManMonthManufacturePlanById(manMonthManufacturePlan.getMonthManufacturePlanSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(manMonthManufacturePlan.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_MONTH_MANUFACTURE_PLAN)
                        .setDocumentSid(manMonthManufacturePlan.getMonthManufacturePlanSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("生产月计划" + manufacturePlan.getMonthManufacturePlanCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(manufacturePlan.getMonthManufacturePlanCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(manMonthManufacturePlan);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(manMonthManufacturePlan.getMonthManufacturePlanSid(), manMonthManufacturePlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    public void changeDate(List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList){
        List<ManManufactureOrder> manManufactureOrderList = new ArrayList<>();
        List<ManManufactureOrderProcess> manManufactureOrderProcesses = new ArrayList<>();
        manMonthManufacturePlanItemList.forEach(li->{
            List<ManMonthManufacturePlanProcess> manufacturePlanProcessList = li.getManufacturePlanProcessList();
            manufacturePlanProcessList.stream().filter(man->man.getPlanEndDate()!=null).forEach(
                    process->{
                        ManManufactureOrderProcess manManufactureOrderProcess = new ManManufactureOrderProcess();
                        manManufactureOrderProcess.setPlanEndDate(process.getPlanEndDate())
                                .setManufactureOrderSid(li.getManufactureOrderSid())
                                .setProcessSid(process.getProcessSid())
                                .setWorkCenterSid(li.getWorkCenterSid());
                        manManufactureOrderProcesses.add(manManufactureOrderProcess);
                    }
            );
        });
        if(CollectionUtil.isNotEmpty(manManufactureOrderProcesses)){
            manManufactureOrderProcessMapper.updatesAllById(manManufactureOrderProcesses);
        }
        manMonthManufacturePlanItemList.stream().filter(li->li.getPlanEndDate()!=null).forEach(li->{
            Date planEndDate = li.getPlanEndDate();
            ManManufactureOrder manManufactureOrder = new ManManufactureOrder();
            manManufactureOrder.setPlanEndDate(planEndDate)
                    .setManufactureOrderSid(li.getManufactureOrderSid());
            manManufactureOrderList.add(manManufactureOrder);
        });
        if(CollectionUtil.isNotEmpty(manManufactureOrderList)){
            manManufactureOrderMapper.updatesPlanById(manManufactureOrderList);
        }
    }
    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManMonthManufacturePlan manMonthManufacturePlan) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manMonthManufacturePlan.getMonthManufacturePlanSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manMonthManufacturePlan.getMonthManufacturePlanSid()));
        }
    }

    public void judge(ManMonthManufacturePlan manMonthManufacturePlan){
        List<ManMonthManufacturePlan> manMonthManufacturePlans = manMonthManufacturePlanMapper.selectList(new QueryWrapper<ManMonthManufacturePlan>().lambda()
                .eq(ManMonthManufacturePlan::getPlantSid, manMonthManufacturePlan.getPlantSid())
                .eq(ManMonthManufacturePlan::getDepartment, manMonthManufacturePlan.getDepartment())
                .eq(ManMonthManufacturePlan::getYearmonth, manMonthManufacturePlan.getYearmonth())
                .ne(manMonthManufacturePlan.getMonthManufacturePlanSid() != null, ManMonthManufacturePlan::getMonthManufacturePlanSid, manMonthManufacturePlan.getMonthManufacturePlanSid())
        );
        if(CollectionUtil.isNotEmpty(manMonthManufacturePlans)){
            throw new CustomException("“工厂+操作部门+计划年月“的值的组合已存在，请检查！");
        }
        if (!ConstantsEms.YES.equals(manMonthManufacturePlan.getContinueIsUnique())) {
            List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList = manMonthManufacturePlan.getManMonthManufacturePlanItemList();
            if(CollectionUtil.isNotEmpty(manMonthManufacturePlanItemList)){
                HashSet<String> hashSet = new HashSet<>();
                manMonthManufacturePlanItemList.forEach(li->{
                    Long sku1Sid = li.getSku1Sid()!=null?li.getSku1Sid():1L;
                    if(!hashSet.add(li.getWorkCenterSid()+";"+li.getManufactureOrderCode()+sku1Sid)){
                        throw new CustomException("明细清单页签，“班组+商品编码(款号)+排产批次号+颜色“的值的组合存在重复！", 1);
                    }
                });
            }
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManMonthManufacturePlan o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList = o.getManMonthManufacturePlanItemList();
            if (CollectionUtil.isEmpty(manMonthManufacturePlanItemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 生产周计划-明细对象
     */
    private void addManMonthManufacturePlanItem(ManMonthManufacturePlan manMonthManufacturePlan, List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList) {
//        deleteItem(manMonthManufacturePlan);
        long i = 1;
        Long maxItemNum = manMonthManufacturePlan.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (ManMonthManufacturePlanItem planItem : manMonthManufacturePlanItemList) {
            planItem.setMonthManufacturePlanSid(manMonthManufacturePlan.getMonthManufacturePlanSid());
            planItem.setItemNum(i);
            i++;
        }
        manMonthManufacturePlanItemMapper.inserts(manMonthManufacturePlanItemList);
        manMonthManufacturePlanItemList.stream().forEach(li->{
            List<ManMonthManufacturePlanProcess> processList = li.getManufacturePlanProcessList();
            processList.forEach(process->{
                process.setMonthManufacturePlanItemSid(li.getMonthManufacturePlanItemSid());
            });
            manMonthManufacturePlanProcessMapper.inserts(processList);
        });
    }

    private void deleteItem(ManMonthManufacturePlan manMonthManufacturePlan) {
        manMonthManufacturePlanItemMapper.delete(
                new UpdateWrapper<ManMonthManufacturePlanItem>()
                        .lambda()
                        .eq(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, manMonthManufacturePlan.getMonthManufacturePlanSid())
        );
        List<ManMonthManufacturePlanItem> manMonthManufacturePlanItems = manMonthManufacturePlanItemMapper.selectList(new QueryWrapper<ManMonthManufacturePlanItem>().lambda()
                .eq(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, manMonthManufacturePlan.getMonthManufacturePlanSid())
        );
        if(CollectionUtil.isNotEmpty(manMonthManufacturePlanItems)){
            List<Long> sids = manMonthManufacturePlanItems.stream().map(li -> li.getMonthManufacturePlanItemSid()).collect(Collectors.toList());
            //删除月计划工序
            manMonthManufacturePlanProcessMapper.delete(new QueryWrapper<ManMonthManufacturePlanProcess>().lambda()
                    .in(ManMonthManufacturePlanProcess::getMonthManufacturePlanItemSid,sids)
            );
        }
    }

    public void addRemarkItems(ManMonthManufacturePlan manMonthManufacturePlan,List<ManMonthManufacturePlanBanzuRemark> items){
        manMonthManufacturePlanBanzuRemarkMapper.delete(new QueryWrapper<ManMonthManufacturePlanBanzuRemark>().lambda()
                .eq(ManMonthManufacturePlanBanzuRemark::getMonthManufacturePlanSid,manMonthManufacturePlan.getMonthManufacturePlanSid())
        );
        if(CollectionUtil.isNotEmpty(items)){
            items.forEach(li->{
                li.setMonthManufacturePlanSid(manMonthManufacturePlan.getMonthManufacturePlanSid());
            });
            manMonthManufacturePlanBanzuRemarkMapper.inserts(items);
        }
    }
    /**
     * 生产月计划-附件对象
     */
    private void addManMonthManufacturePlanAttach(ManMonthManufacturePlan manMonthManufacturePlan, List<ManMonthManufacturePlanAttach> manMonthManufacturePlanAttachList) {
//        deleteAttach(manMonthManufacturePlan);
        manMonthManufacturePlanAttachList.forEach(o -> {
            o.setMonthManufacturePlanSid(manMonthManufacturePlan.getMonthManufacturePlanSid());
        });
        manMonthManufacturePlanAttachMapper.inserts(manMonthManufacturePlanAttachList);
    }

    private void deleteAttach(ManMonthManufacturePlan manMonthManufacturePlan) {
        manMonthManufacturePlanAttachMapper.delete(
                new UpdateWrapper<ManMonthManufacturePlanAttach>()
                        .lambda()
                        .eq(ManMonthManufacturePlanAttach::getMonthManufacturePlanSid, manMonthManufacturePlan.getMonthManufacturePlanSid())
        );
    }

    /**
     * 修改生产月计划
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManMonthManufacturePlan(ManMonthManufacturePlan manMonthManufacturePlan) {
        judge(manMonthManufacturePlan);
        ManMonthManufacturePlan response = manMonthManufacturePlanMapper.selectManMonthManufacturePlanById(manMonthManufacturePlan.getMonthManufacturePlanSid());
        setConfirmInfo(manMonthManufacturePlan);
        manMonthManufacturePlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manMonthManufacturePlanMapper.updateAllById(manMonthManufacturePlan);
        if (row > 0) {
            //生产月计划-明细
            List<ManMonthManufacturePlanItem> planItemList = manMonthManufacturePlan.getManMonthManufacturePlanItemList();
            operateItem(manMonthManufacturePlan, planItemList);
            //生产月计划-附件对象
            List<ManMonthManufacturePlanAttach> planAttachList = manMonthManufacturePlan.getManMonthManufacturePlanAttachList();
            operateAttachment(manMonthManufacturePlan, planAttachList);
            addRemarkItems( manMonthManufacturePlan,manMonthManufacturePlan.getPlanBanzuRemarkList());
            if (!ConstantsEms.SAVA_STATUS.equals(manMonthManufacturePlan.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(manMonthManufacturePlan);
            }
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manMonthManufacturePlan);
            MongodbDeal.update(manMonthManufacturePlan.getMonthManufacturePlanSid(), response.getHandleStatus(), manMonthManufacturePlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 生产月计划-明细
     */
    private void operateItem(ManMonthManufacturePlan manMonthManufacturePlan, List<ManMonthManufacturePlanItem> planItemList) {
        if (CollectionUtil.isNotEmpty(planItemList)) {
            changeDate(planItemList);
            //最大行号
            List<Long> itemNums = planItemList.stream().filter(o -> o.getItemNum() != null).map(ManMonthManufacturePlanItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manMonthManufacturePlan.setMaxItemNum(maxItemNum);
            }
            //新增
            List<ManMonthManufacturePlanItem> addList = planItemList.stream().filter(o -> o.getMonthManufacturePlanItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManMonthManufacturePlanItem(manMonthManufacturePlan, addList);
            }
            //编辑
            List<ManMonthManufacturePlanItem> editList = planItemList.stream().filter(o -> o.getMonthManufacturePlanItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manMonthManufacturePlanItemMapper.updateAllById(o);
                    List<ManMonthManufacturePlanProcess> manufacturePlanProcessList = o.getManufacturePlanProcessList();
                    manMonthManufacturePlanProcessMapper.updatesAllById(manufacturePlanProcessList);
                });
            }
            //原有数据
            List<ManMonthManufacturePlanItem> itemList = manMonthManufacturePlanItemMapper.selectList(new QueryWrapper<ManMonthManufacturePlanItem>().lambda()
                    .eq(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, manMonthManufacturePlan.getMonthManufacturePlanSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManMonthManufacturePlanItem::getMonthManufacturePlanItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = planItemList.stream().map(ManMonthManufacturePlanItem::getMonthManufacturePlanItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manMonthManufacturePlanItemMapper.deleteBatchIds(result);
                manMonthManufacturePlanProcessMapper.delete(new QueryWrapper<ManMonthManufacturePlanProcess>().lambda()
                .in(ManMonthManufacturePlanProcess::getMonthManufacturePlanItemSid,result)
                );
            }
        } else {
            deleteItem(manMonthManufacturePlan);
        }
    }

    /**
     * 生产月计划-附件
     */
    private void operateAttachment(ManMonthManufacturePlan manMonthManufacturePlan, List<ManMonthManufacturePlanAttach> planAttachList) {
        if (CollectionUtil.isNotEmpty(planAttachList)) {
            //新增
            List<ManMonthManufacturePlanAttach> addList = planAttachList.stream().filter(o -> o.getManufacturePlanAttachSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManMonthManufacturePlanAttach(manMonthManufacturePlan, addList);
            }
            //编辑
            List<ManMonthManufacturePlanAttach> editList = planAttachList.stream().filter(o -> o.getManufacturePlanAttachSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manMonthManufacturePlanAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManMonthManufacturePlanAttach> itemList =
                    manMonthManufacturePlanAttachMapper.selectList(new QueryWrapper<ManMonthManufacturePlanAttach>().lambda()
                            .eq(ManMonthManufacturePlanAttach::getMonthManufacturePlanSid, manMonthManufacturePlan.getMonthManufacturePlanSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManMonthManufacturePlanAttach::getManufacturePlanAttachSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = planAttachList.stream().map(ManMonthManufacturePlanAttach::getManufacturePlanAttachSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manMonthManufacturePlanAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manMonthManufacturePlan);
        }
    }

    /**
     * 变更生产月计划
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManMonthManufacturePlan(ManMonthManufacturePlan manMonthManufacturePlan) {
        ManMonthManufacturePlan response = manMonthManufacturePlanMapper.selectManMonthManufacturePlanById(manMonthManufacturePlan.getMonthManufacturePlanSid());
        setConfirmInfo(manMonthManufacturePlan);
        manMonthManufacturePlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manMonthManufacturePlan.setUpdateDate(new Date());
        int row = manMonthManufacturePlanMapper.updateAllById(manMonthManufacturePlan);
        if (row > 0) {
            //生产月计划-明细
            List<ManMonthManufacturePlanItem> planItemList = manMonthManufacturePlan.getManMonthManufacturePlanItemList();
            operateItem(manMonthManufacturePlan, planItemList);
            //生产月计划-附件对象
            List<ManMonthManufacturePlanAttach> planAttachList = manMonthManufacturePlan.getManMonthManufacturePlanAttachList();
            operateAttachment(manMonthManufacturePlan, planAttachList);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manMonthManufacturePlan);
            MongodbDeal.update(manMonthManufacturePlan.getMonthManufacturePlanSid(), response.getHandleStatus(), manMonthManufacturePlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除生产月计划
     *
     * @param monthManufacturePlanSids 需要删除的生产月计划ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManMonthManufacturePlanByIds(List<Long> monthManufacturePlanSids) {
        int count = manMonthManufacturePlanMapper.selectCount(new QueryWrapper<ManMonthManufacturePlan>().lambda().in(ManMonthManufacturePlan::getMonthManufacturePlanSid, monthManufacturePlanSids)
                .eq(ManMonthManufacturePlan::getHandleStatus, HandleStatus.SAVE.getCode()));
        if (count != monthManufacturePlanSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        manMonthManufacturePlanMapper.delete(new QueryWrapper<ManMonthManufacturePlan>()
                .lambda().in(ManMonthManufacturePlan::getMonthManufacturePlanSid, monthManufacturePlanSids));
        List<ManMonthManufacturePlanItem> manMonthManufacturePlanItems = manMonthManufacturePlanItemMapper.selectList(new QueryWrapper<ManMonthManufacturePlanItem>().lambda()
                .in(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, monthManufacturePlanSids)
        );
        if(CollectionUtil.isNotEmpty(manMonthManufacturePlanItems)){
            List<Long> sids = manMonthManufacturePlanItems.stream().map(li -> li.getMonthManufacturePlanItemSid()).collect(Collectors.toList());
            //删除月计划工序
            manMonthManufacturePlanProcessMapper.delete(new QueryWrapper<ManMonthManufacturePlanProcess>().lambda()
                    .in(ManMonthManufacturePlanProcess::getMonthManufacturePlanItemSid,sids)
            );
        }
        //删除生产月计划-明细
        manMonthManufacturePlanItemMapper.delete(new QueryWrapper<ManMonthManufacturePlanItem>()
                .lambda().in(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, monthManufacturePlanSids));
        //删除生产月计划-附件
        manMonthManufacturePlanAttachMapper.delete(new QueryWrapper<ManMonthManufacturePlanAttach>()
                .lambda().in(ManMonthManufacturePlanAttach::getMonthManufacturePlanSid, monthManufacturePlanSids));
        manMonthManufacturePlanBanzuRemarkMapper.delete(new QueryWrapper<ManMonthManufacturePlanBanzuRemark>().lambda()
        .in(ManMonthManufacturePlanBanzuRemark::getMonthManufacturePlanSid,monthManufacturePlanSids)
        );
        ManMonthManufacturePlan manMonthManufacturePlan = new ManMonthManufacturePlan();
        monthManufacturePlanSids.forEach(monthManufacturePlanSid -> {
            manMonthManufacturePlan.setMonthManufacturePlanSid(monthManufacturePlanSid);
            //校验是否存在待办
            checkTodoExist(manMonthManufacturePlan);
        });
        return monthManufacturePlanSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param manMonthManufacturePlan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManMonthManufacturePlan manMonthManufacturePlan) {
        int row = 0;
        Long[] sids = manMonthManufacturePlan.getMonthManufacturePlanSidList();
        if (sids != null && sids.length > 0) {
            int count = manMonthManufacturePlanMapper.selectCount(new QueryWrapper<ManMonthManufacturePlan>().lambda().in(ManMonthManufacturePlan::getMonthManufacturePlanSid, sids)
                    .eq(ManMonthManufacturePlan::getHandleStatus, HandleStatus.SAVE.getCode()));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            List<String> msg = new ArrayList<>();
            for (Long id : sids) {
                List<ManMonthManufacturePlanItem> items =
                        manMonthManufacturePlanItemMapper.selectList(new QueryWrapper<ManMonthManufacturePlanItem>().lambda()
                                .eq(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, id));
                if (CollectionUtil.isEmpty(items)) {
                    throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }
                if (!ConstantsEms.YES.equals(manMonthManufacturePlan.getContinueIsUnique())) {
                    if(CollectionUtil.isNotEmpty(items)){
                        HashSet<String> hashSet = new HashSet<>();
                        List<String> finalMsg = msg;
                        try {
                            items.forEach(li->{
                                Long sku1Sid = li.getSku1Sid()!=null?li.getSku1Sid():1L;
                                if(!hashSet.add(li.getWorkCenterSid()+";"+li.getManufactureOrderCode()+sku1Sid)){
                                    ManMonthManufacturePlan temp = manMonthManufacturePlanMapper.selectById(id);
                                    finalMsg.add(temp.getMonthManufacturePlanCode().toString());
                                    throw new RuntimeException("");
                                }
                            });
                        } catch (Exception e) {}
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(msg)) {
                msg = msg.stream().sorted(Comparator.comparingDouble(Long::parseLong)).collect(Collectors.toList());
                List<String> msgs = new ArrayList<>();
                for (String s : msg) {
                    msgs.add(s.toString()+"明细清单页签，“班组+商品编码(款号)+排产批次号+颜色“的值的组合存在重复！");
                }
                throw new BaseException(EmsResultEntity.WARN_TAG, null, (Object[]) msgs.toArray());
            }
            for (Long id : sids) {
                manMonthManufacturePlan.setMonthManufacturePlanSid(id);
                //校验是否存在待办
                checkTodoExist(manMonthManufacturePlan);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, manMonthManufacturePlan.getHandleStatus(), msgList, TITLE, null);
            }
            row = manMonthManufacturePlanMapper.update(null, new UpdateWrapper<ManMonthManufacturePlan>().lambda()
                    .set(ManMonthManufacturePlan::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(ManMonthManufacturePlan::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(ManMonthManufacturePlan::getConfirmDate, new Date())
                    .in(ManMonthManufacturePlan::getMonthManufacturePlanSid, sids));
        }
        return row;
    }

    /**
     * 生产月计划明细报表
     */
    @Override
    public List<ManMonthManufacturePlanItem> getItemList(ManMonthManufacturePlanItem manMonthManufacturePlanItem) {
        return manMonthManufacturePlanItemMapper.getItemList(manMonthManufacturePlanItem);
    }

    /**
     * 作废-生产月计划
     */
    @Override
    public int cancellationMonthManufacturePlanById(Long monthManufacturePlanSid) {
        ManMonthManufacturePlan manMonthManufacturePlan = manMonthManufacturePlanMapper.selectManMonthManufacturePlanById(monthManufacturePlanSid);
        if (!ConstantsEms.CHECK_STATUS.equals(manMonthManufacturePlan.getHandleStatus())) {
            throw new BaseException(ConstantsEms.CONFIRM_CANCELLATION);
        }
        //插入日志
        MongodbUtil.insertUserLog(monthManufacturePlanSid, BusinessType.CANCEL.getValue(), manMonthManufacturePlan, manMonthManufacturePlan, TITLE);
        manMonthManufacturePlan.setHandleStatus(ConstantsEms.HANDLE_IM);
        manMonthManufacturePlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manMonthManufacturePlan.setUpdateDate(new Date());
        return manMonthManufacturePlanMapper.updateById(manMonthManufacturePlan);
    }

    /**
     * 提交前校验-生产月计划
     */
    @Override
    public int verify(Long monthManufacturePlanSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<ManMonthManufacturePlanItem> items =
                    manMonthManufacturePlanItemMapper.selectList(new QueryWrapper<ManMonthManufacturePlanItem>().lambda()
                            .eq(ManMonthManufacturePlanItem::getMonthManufacturePlanSid, monthManufacturePlanSid));
            if (CollectionUtil.isEmpty(items)) {
                throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
            }
            //校验是否存在待办
            checkTodoExist(new ManMonthManufacturePlan().setMonthManufacturePlanSid(monthManufacturePlanSid));
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(monthManufacturePlanSid, BusinessType.SUBMIT.getValue(), msgList, TITLE);
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return 1;
    }
}
