package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.platform.ems.domain.dto.request.ManWeekManufacturePlanRequest;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.domain.ConProduceStage;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.plug.mapper.ConProduceStageMapper;
import com.platform.ems.service.IManProduceWeekProgressTotalService;
import com.platform.ems.service.IManWeekManufacturePlanService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生产周计划Service业务层处理
 *
 * @author hjj
 * @date 2021-07-16
 */
@Service
@SuppressWarnings("all")
public class ManWeekManufacturePlanServiceImpl extends ServiceImpl<ManWeekManufacturePlanMapper, ManWeekManufacturePlan> implements IManWeekManufacturePlanService {
    @Autowired
    private ManWeekManufacturePlanMapper manWeekManufacturePlanMapper;
    @Autowired
    private ManWeekManufacturePlanItemMapper manWeekManufacturePlanItemMapper;
    @Autowired
    private ManWeekManufacturePlanAttachMapper manWeekManufacturePlanAttachMapper;
    @Autowired
    private ManDayManufacturePlanItemMapper  manDayManufacturePlanItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private ConProduceStageMapper conProduceStageMapper;
    @Autowired
    private ManWeekManufacturePlanBanzuRemarkMapper manWeekManufacturePlanBanzuRemarkMapper;
    @Autowired
    private  ManManufactureOrderConcernTaskMapper  manManufactureOrderConcernTaskMapper;
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;
    @Autowired
    private ManMonthManufacturePlanItemMapper manMonthManufacturePlanItemMapper;
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;
    @Autowired
    private IManProduceWeekProgressTotalService manProduceWeekProgressTotalService;
    private static final String TITLE = "生产周计划";

    /**
     * 查询生产周计划
     *
     * @param weekManufacturePlanSid 生产周计划ID
     * @return 生产周计划
     */
    @Override
    public ManWeekManufacturePlan selectManWeekManufacturePlanById(Long weekManufacturePlanSid) {
        ManWeekManufacturePlan manWeekManufacturePlan = manWeekManufacturePlanMapper.selectManWeekManufacturePlanById(weekManufacturePlanSid);
        if (manWeekManufacturePlan == null) {
            return null;
        }
        List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList =
                manWeekManufacturePlanItemMapper.selectManWeekManufacturePlanItemList(
                        new ManWeekManufacturePlanItem().setWeekManufacturePlanSid(weekManufacturePlanSid));
        if(CollectionUtil.isNotEmpty(manWeekManufacturePlanItemList)){
            manWeekManufacturePlanItemList.stream().forEach(li->{
                List<ManDayManufacturePlanItem> manDayManufacturePlanItems = manDayManufacturePlanItemMapper.selectList(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                        .eq(ManDayManufacturePlanItem::getManufacturePlanItemSid, li.getWeekManufacturePlanItemSid())
                );
                if(CollectionUtil.isNotEmpty(manDayManufacturePlanItems)){
                    BigDecimal sum = manDayManufacturePlanItems.stream().map(m ->{if(m.getPlanQuantity()!=null){
                        return m.getPlanQuantity();
                    }else{
                        return BigDecimal.ZERO;
                    }
                    }).reduce(BigDecimal.ZERO, BigDecimal::add);
                    li.setSumQuantity(sum);
                }
                li.setManDayManufacturePlanItemList(manDayManufacturePlanItems);
            });
        }
        List<ManWeekManufacturePlanAttach> manWeekManufacturePlanAttachList =
                manWeekManufacturePlanAttachMapper.selectManWeekManufacturePlanAttachList(
                        new ManWeekManufacturePlanAttach().setWeekManufacturePlanSid(weekManufacturePlanSid));
        List<ManWeekManufacturePlanBanzuRemark> manWeekManufacturePlanBanzuRemarks = manWeekManufacturePlanBanzuRemarkMapper.selectManWeekManufacturePlanBanzuRemarkById(weekManufacturePlanSid);
        if(CollectionUtil.isNotEmpty(manWeekManufacturePlanItemList)){
            Set<Long> codeSet = manWeekManufacturePlanItemList.stream().map(li -> li.getManufactureOrderCode()).collect(Collectors.toSet());
            ManManufactureOrderConcernTask manManufactureOrderConcernTask = new ManManufactureOrderConcernTask();
            manManufactureOrderConcernTask.setManufactureOrderCodeSet(codeSet);
            List<ManManufactureOrderConcernTask> manManufactureOrderConcernTasks = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(manManufactureOrderConcernTask);
            manWeekManufacturePlan.setManufactureOrderConcernTaskList(manManufactureOrderConcernTasks);
        }
        ManProduceWeekProgressTotal weekProgressTotal = new ManProduceWeekProgressTotal();
        weekProgressTotal.setDateStart(manWeekManufacturePlan.getDateStart())
                .setPlantSid(manWeekManufacturePlan.getPlantSid())
                .setRecentWeeks(4)
                .setDepartmentCode(manWeekManufacturePlan.getDepartment());
        List<ManProduceWeekProgressTotal> manProduceWeekProgressTotals = manProduceWeekProgressTotalService.selectManProduceWeekProgressTotalList(weekProgressTotal);
        manWeekManufacturePlan.setManProduceWeekProgressTotalList(manProduceWeekProgressTotals);
        manWeekManufacturePlan.setPlanBanzuRemarkList(manWeekManufacturePlanBanzuRemarks);
        manWeekManufacturePlan.setManWeekManufacturePlanItemList(manWeekManufacturePlanItemList);
        manWeekManufacturePlan.setManWeekManufacturePlanAttachList(manWeekManufacturePlanAttachList);
        MongodbUtil.find(manWeekManufacturePlan);
        return manWeekManufacturePlan;
    }

    @Override
    public List<ManManufactureOrderConcernTask> getConcernTask(List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList){
        if(CollectionUtil.isNotEmpty(manWeekManufacturePlanItemList)){
            Set<Long> codeSet = manWeekManufacturePlanItemList.stream().map(li -> li.getManufactureOrderCode()).collect(Collectors.toSet());
            ManManufactureOrderConcernTask manManufactureOrderConcernTask = new ManManufactureOrderConcernTask();
            manManufactureOrderConcernTask.setManufactureOrderCodeSet(codeSet);
            List<ManManufactureOrderConcernTask> manManufactureOrderConcernTasks = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(manManufactureOrderConcernTask);
           return manManufactureOrderConcernTasks;
        }
        return new ArrayList<>();
    }

    /**
     * 查询生产周计划列表
     *
     * @param manWeekManufacturePlan 生产周计划
     * @return 生产周计划
     */
    @Override
    public List<ManWeekManufacturePlan> selectManWeekManufacturePlanList(ManWeekManufacturePlan manWeekManufacturePlan) {
        return manWeekManufacturePlanMapper.selectManWeekManufacturePlanList(manWeekManufacturePlan);
    }
    /**
     * 获取分配量
     *
     */
    @Override
    public List<ManWeekManufacturePlanItem> getQuantityFenpei(List<ManWeekManufacturePlanItem> items){
        items.stream().forEach(li->{
            BigDecimal competele = manDayManufactureProgressItemMapper.getTotalCompetele(li);
            li.setTotalCompleteQuantity(competele);
            ManWeekManufacturePlanItem man = manWeekManufacturePlanItemMapper.getQuantityFenPei(li);
            if(man!=null){
                li.setQuantityFenpei(man.getQuantityFenpei());
            }else{
                ManMonthManufacturePlanItem manMonthManufacturePlanItem = new ManMonthManufacturePlanItem();
                manMonthManufacturePlanItem.setManufactureOrderSid(li.getManufactureOrderSid())
                        .setWorkCenterSid(li.getWorkCenterSid())
                        .setSku1Sid(li.getSku1Sid());
                ManMonthManufacturePlanItem manMonth = manMonthManufacturePlanItemMapper.getQuantityFenpei(manMonthManufacturePlanItem);
                if(manMonth!=null){
                    li.setQuantityFenpei(manMonth.getQuantityFenpei());
                }
            }
            if(li.getTotalCompleteQuantity()==null){
               li.setTotalCompleteQuantity(BigDecimal.ZERO);
            }
        });
        return items;
    }
    /**
     * 新增生产周计划
     * 需要注意编码重复校验
     *
     * @param manWeekManufacturePlan 生产周计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManWeekManufacturePlan(ManWeekManufacturePlan manWeekManufacturePlan) {
        ManWeekManufacturePlan Plan = manWeekManufacturePlanMapper.selectOne(new QueryWrapper<ManWeekManufacturePlan>().lambda()
                .eq(ManWeekManufacturePlan::getDateStart, manWeekManufacturePlan.getDateStart())
                .eq(ManWeekManufacturePlan::getPlantSid,manWeekManufacturePlan.getPlantSid())
                .eq(ManWeekManufacturePlan::getDepartment,manWeekManufacturePlan.getDepartment())
        );
        if(Plan!=null){
            throw  new CustomException("工厂(工序)+操作部门+周计划日期的值的组合已存在，请检查");
        }
        setConfirmInfo(manWeekManufacturePlan);
        int row = manWeekManufacturePlanMapper.insert(manWeekManufacturePlan);
        if (row > 0) {
            //生产周计划-明细
            List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList = manWeekManufacturePlan.getManWeekManufacturePlanItemList();
            if (CollectionUtils.isNotEmpty(manWeekManufacturePlanItemList)) {
                judge( manWeekManufacturePlan,manWeekManufacturePlanItemList);
                addManWeekManufacturePlanItem(manWeekManufacturePlan, manWeekManufacturePlanItemList);
            }
            //生产周计划-附件
            List<ManWeekManufacturePlanAttach> manWeekManufacturePlanAttachList = manWeekManufacturePlan.getManWeekManufacturePlanAttachList();
            if (CollectionUtils.isNotEmpty(manWeekManufacturePlanAttachList)) {
                addManWeekManufacturePlanAttach(manWeekManufacturePlan, manWeekManufacturePlanAttachList);
            }
            //生产周计划-班组总结
            addRemarkItems( manWeekManufacturePlan,manWeekManufacturePlan.getPlanBanzuRemarkList());
            ManWeekManufacturePlan manufacturePlan = manWeekManufacturePlanMapper.selectManWeekManufacturePlanById(manWeekManufacturePlan.getWeekManufacturePlanSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(manWeekManufacturePlan.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_WEEK_MANUFACTURE_PLAN)
                        .setDocumentSid(manWeekManufacturePlan.getWeekManufacturePlanSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("生产周计划" + manufacturePlan.getWeekManufacturePlanCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(manufacturePlan.getWeekManufacturePlanCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(manWeekManufacturePlan);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(manWeekManufacturePlan.getWeekManufacturePlanSid(), manWeekManufacturePlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    public  void judge(ManWeekManufacturePlan manWeekManufacturePlan,List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList){
        if(CollectionUtil.isNotEmpty(manWeekManufacturePlanItemList)){
            String departmentCode = manWeekManufacturePlan.getDepartment();
            ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>().lambda()
                    .eq(ConManufactureDepartment::getCode, departmentCode)
            );
            String name = conManufactureDepartment.getName();
            List<ManWeekManufacturePlanItem> list = manWeekManufacturePlanItemList.stream().filter(li -> !name.equals(li.getDepartmentName())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(list)){
                throw new CustomException("”明细清单“页签，存在明细行的操作部门与表头的操作部门不一致，请检查！");
            }
            HashSet<String> hashSet = new HashSet<>();
            manWeekManufacturePlanItemList.forEach(li->{
                String materilCode=li.getMaterialCode()!=null?li.getMaterialCode():"1";
                Long paichanBatch = li.getPaichanBatch()!=null?li.getPaichanBatch():1L;
                String workCenterName = li.getWorkCenterSid()!=null?li.getWorkCenterSid().toString():"1";
                String processName = li.getProcessName()!=null?li.getProcessName():"1";
                Long sku1Sid = li.getSku1Sid()!=null?li.getSku1Sid():1L;
                if(!hashSet.add(materilCode+paichanBatch+workCenterName+processName+sku1Sid)){
                    throw new CustomException("明细清单页签，“班组+商品编码+排产批次+工序+颜色“的值的组合存在重复！");
                }
            });

        }
    }
    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManWeekManufacturePlan manWeekManufacturePlan) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manWeekManufacturePlan.getWeekManufacturePlanSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manWeekManufacturePlan.getWeekManufacturePlanSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManWeekManufacturePlan o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList = o.getManWeekManufacturePlanItemList();
            if (CollectionUtil.isEmpty(manWeekManufacturePlanItemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            if (!ConstantsEms.YES.equals(o.getContinueIsNull())) {
                manWeekManufacturePlanItemList.forEach(li->{
                    List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = li.getManDayManufacturePlanItemList();
                    if(CollectionUtil.isNotEmpty(manDayManufacturePlanItemList)){
                        List<ManDayManufacturePlanItem> items = manDayManufacturePlanItemList.stream().filter(m -> m.getPlanQuantity() == null).collect(Collectors.toList());
                        if(CollectionUtil.isNotEmpty(items) && items.size() == manDayManufacturePlanItemList.size()){
                            throw new BaseException(EmsResultEntity.WARN_TAG ,"”明细清单”页签中，存在明细行的数量值都为空！");
                        }
                    }
                });
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 生产周计划-明细对象
     */
    private void addManWeekManufacturePlanItem(ManWeekManufacturePlan manWeekManufacturePlan, List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList) {
//        deleteItem(manWeekManufacturePlan);
        long i = 1;
        Long maxItemNum = manWeekManufacturePlan.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (ManWeekManufacturePlanItem planItem : manWeekManufacturePlanItemList) {
            planItem.setWeekManufacturePlanSid(manWeekManufacturePlan.getWeekManufacturePlanSid());
            planItem.setItemNum(i);
            i++;
        }
        manWeekManufacturePlanItemMapper.inserts(manWeekManufacturePlanItemList);
        manWeekManufacturePlanItemList.forEach(planItem->{
            List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = planItem.getManDayManufacturePlanItemList();
            manDayManufacturePlanItemList.forEach(li->{
                 li.setManufacturePlanItemSid(planItem.getWeekManufacturePlanItemSid());
            });
            manDayManufacturePlanItemMapper.inserts(manDayManufacturePlanItemList);
        });
    }

    private void deleteItem(ManWeekManufacturePlan manWeekManufacturePlan) {
        List<ManWeekManufacturePlanItem> manWeekManufacturePlanItems = manWeekManufacturePlanItemMapper.selectList(new QueryWrapper<ManWeekManufacturePlanItem>().lambda()
                .eq(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, manWeekManufacturePlan.getWeekManufacturePlanSid())
        );
        if(CollectionUtil.isNotEmpty(manWeekManufacturePlanItems)){
            List<Long> sids = manWeekManufacturePlanItems.stream().map(li -> li.getWeekManufacturePlanItemSid()).collect(Collectors.toList());
            manDayManufacturePlanItemMapper.delete(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                    .in(ManDayManufacturePlanItem::getManufacturePlanItemSid,sids)
            );
        }
        manWeekManufacturePlanItemMapper.delete(
                new UpdateWrapper<ManWeekManufacturePlanItem>()
                        .lambda()
                        .eq(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, manWeekManufacturePlan.getWeekManufacturePlanSid())
        );
    }

    /**
     * 生产周计划-附件对象
     */
    private void addManWeekManufacturePlanAttach(ManWeekManufacturePlan manWeekManufacturePlan, List<ManWeekManufacturePlanAttach> manWeekManufacturePlanAttachList) {
//        deleteAttach(manWeekManufacturePlan);
        manWeekManufacturePlanAttachList.forEach(o -> {
            o.setWeekManufacturePlanSid(manWeekManufacturePlan.getWeekManufacturePlanSid());
        });
        manWeekManufacturePlanAttachMapper.inserts(manWeekManufacturePlanAttachList);
    }

    public void addRemarkItems(ManWeekManufacturePlan manWeekManufacturePlan,List<ManWeekManufacturePlanBanzuRemark> items){
        manWeekManufacturePlanBanzuRemarkMapper.delete(new QueryWrapper<ManWeekManufacturePlanBanzuRemark>().lambda()
                .eq(ManWeekManufacturePlanBanzuRemark::getWeekManufacturePlanSid,manWeekManufacturePlan.getWeekManufacturePlanSid())
        );
        if(CollectionUtil.isNotEmpty(items)){
            items.forEach(li->{
                li.setWeekManufacturePlanSid(manWeekManufacturePlan.getWeekManufacturePlanSid());
            });
            manWeekManufacturePlanBanzuRemarkMapper.inserts(items);
        }
    }
    private void deleteAttach(ManWeekManufacturePlan manWeekManufacturePlan) {
        manWeekManufacturePlanAttachMapper.delete(
                new UpdateWrapper<ManWeekManufacturePlanAttach>()
                        .lambda()
                        .eq(ManWeekManufacturePlanAttach::getWeekManufacturePlanSid, manWeekManufacturePlan.getWeekManufacturePlanSid())
        );
    }

    /**
     * 修改生产周计划
     *
     * @param manWeekManufacturePlan 生产周计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManWeekManufacturePlan(ManWeekManufacturePlan manWeekManufacturePlan) {
        ManWeekManufacturePlan Plan = manWeekManufacturePlanMapper.selectOne(new QueryWrapper<ManWeekManufacturePlan>().lambda()
                .eq(ManWeekManufacturePlan::getDateStart, manWeekManufacturePlan.getDateStart())
                .eq(ManWeekManufacturePlan::getPlantSid,manWeekManufacturePlan.getPlantSid())
                .eq(ManWeekManufacturePlan::getDepartment,manWeekManufacturePlan.getDepartment())
        );
        if(Plan!=null&&!Plan.getWeekManufacturePlanSid().toString().equals(manWeekManufacturePlan.getWeekManufacturePlanSid().toString())){
            throw  new CustomException("工厂(工序)+操作部门+周计划日期的值的组合已存在，请检查");
        }
        ManWeekManufacturePlan response = manWeekManufacturePlanMapper.selectManWeekManufacturePlanById(manWeekManufacturePlan.getWeekManufacturePlanSid());
        setConfirmInfo(manWeekManufacturePlan);
        manWeekManufacturePlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manWeekManufacturePlanMapper.updateAllById(manWeekManufacturePlan);
        if (row > 0) {
            //生产周计划-明细
            List<ManWeekManufacturePlanItem> planItemList = manWeekManufacturePlan.getManWeekManufacturePlanItemList();
            judge(manWeekManufacturePlan,planItemList);
            operateItem(manWeekManufacturePlan, planItemList);
            //生产周计划-附件
            List<ManWeekManufacturePlanAttach> planAttachList = manWeekManufacturePlan.getManWeekManufacturePlanAttachList();
            operateAttachment(manWeekManufacturePlan, planAttachList);
            //生产周计划-班组总结
            addRemarkItems(manWeekManufacturePlan,manWeekManufacturePlan.getPlanBanzuRemarkList());
            if (!ConstantsEms.SAVA_STATUS.equals(manWeekManufacturePlan.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(manWeekManufacturePlan);
            }
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manWeekManufacturePlan);
            MongodbDeal.update(manWeekManufacturePlan.getWeekManufacturePlanSid(), response.getHandleStatus(), manWeekManufacturePlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 生产周计划-明细
     */
    private void operateItem(ManWeekManufacturePlan manWeekManufacturePlan, List<ManWeekManufacturePlanItem> planItemList) {
        if (CollectionUtil.isNotEmpty(planItemList)) {
            //最大行号
            List<Long> itemNums = planItemList.stream().filter(o -> o.getItemNum() != null).map(ManWeekManufacturePlanItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manWeekManufacturePlan.setMaxItemNum(maxItemNum);
            }
            //新增
            List<ManWeekManufacturePlanItem> addList = planItemList.stream().filter(o -> o.getWeekManufacturePlanItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManWeekManufacturePlanItem(manWeekManufacturePlan, addList);
            }
            //编辑
            List<ManWeekManufacturePlanItem> editList = planItemList.stream().filter(o -> o.getWeekManufacturePlanItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manWeekManufacturePlanItemMapper.updateAllById(o);
                    List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = o.getManDayManufacturePlanItemList();
                    manDayManufacturePlanItemList.forEach(day->{
                        manDayManufacturePlanItemMapper.updateAllById(day);
                    });
                });
            }
            //原有数据
            List<ManWeekManufacturePlanItem> itemList = manWeekManufacturePlanItemMapper.selectList(new QueryWrapper<ManWeekManufacturePlanItem>().lambda()
                    .eq(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, manWeekManufacturePlan.getWeekManufacturePlanSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManWeekManufacturePlanItem::getWeekManufacturePlanItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = planItemList.stream().map(ManWeekManufacturePlanItem::getWeekManufacturePlanItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manWeekManufacturePlanItemMapper.deleteBatchIds(result);
                manDayManufacturePlanItemMapper.delete(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                .in(ManDayManufacturePlanItem::getManufacturePlanItemSid,result)
                );
            }
        } else {
            deleteItem(manWeekManufacturePlan);
        }
    }

    /**
     * 生产周计划-附件
     */
    private void operateAttachment(ManWeekManufacturePlan manWeekManufacturePlan, List<ManWeekManufacturePlanAttach> planAttachList) {
        if (CollectionUtil.isNotEmpty(planAttachList)) {
            //新增
            List<ManWeekManufacturePlanAttach> addList = planAttachList.stream().filter(o -> o.getManufacturePlanAttachSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManWeekManufacturePlanAttach(manWeekManufacturePlan, addList);
            }
            //编辑
            List<ManWeekManufacturePlanAttach> editList = planAttachList.stream().filter(o -> o.getManufacturePlanAttachSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manWeekManufacturePlanAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManWeekManufacturePlanAttach> itemList =
                    manWeekManufacturePlanAttachMapper.selectList(new QueryWrapper<ManWeekManufacturePlanAttach>().lambda()
                            .eq(ManWeekManufacturePlanAttach::getWeekManufacturePlanSid, manWeekManufacturePlan.getWeekManufacturePlanSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManWeekManufacturePlanAttach::getManufacturePlanAttachSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = planAttachList.stream().map(ManWeekManufacturePlanAttach::getManufacturePlanAttachSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manWeekManufacturePlanAttachMapper.deleteBatchIds(result);
                manDayManufacturePlanItemMapper.delete(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                .in(ManDayManufacturePlanItem::getManufacturePlanItemSid,result)
                );
            }
        } else {
            deleteAttach(manWeekManufacturePlan);
        }
    }

    /**
     * 变更生产周计划
     *
     * @param manWeekManufacturePlan 生产周计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManWeekManufacturePlan(ManWeekManufacturePlan manWeekManufacturePlan) {
        ManWeekManufacturePlan response = manWeekManufacturePlanMapper.selectManWeekManufacturePlanById(manWeekManufacturePlan.getWeekManufacturePlanSid());
        setConfirmInfo(manWeekManufacturePlan);
        manWeekManufacturePlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manWeekManufacturePlanMapper.updateAllById(manWeekManufacturePlan);
        if (row > 0) {
            //生产周计划-明细
            List<ManWeekManufacturePlanItem> planItemList = manWeekManufacturePlan.getManWeekManufacturePlanItemList();
            operateItem(manWeekManufacturePlan, planItemList);
            //生产周计划-附件
            List<ManWeekManufacturePlanAttach> planAttachList = manWeekManufacturePlan.getManWeekManufacturePlanAttachList();
            operateAttachment(manWeekManufacturePlan, planAttachList);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manWeekManufacturePlan);
            MongodbDeal.update(manWeekManufacturePlan.getWeekManufacturePlanSid(), response.getHandleStatus(), manWeekManufacturePlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除生产周计划
     *
     * @param weekManufacturePlanSids 需要删除的生产周计划ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManWeekManufacturePlanByIds(List<Long> weekManufacturePlanSids) {
        Integer count = manWeekManufacturePlanMapper.selectCount(new QueryWrapper<ManWeekManufacturePlan>().lambda()
                .eq(ManWeekManufacturePlan::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManWeekManufacturePlan::getWeekManufacturePlanSid, weekManufacturePlanSids));
        if (count != weekManufacturePlanSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        manWeekManufacturePlanMapper.deleteBatchIds(weekManufacturePlanSids);
        List<ManWeekManufacturePlanItem> manWeekManufacturePlanItems = manWeekManufacturePlanItemMapper.selectList(new QueryWrapper<ManWeekManufacturePlanItem>().lambda()
                .in(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, weekManufacturePlanSids)
        );
        if(CollectionUtil.isNotEmpty(manWeekManufacturePlanItems)){
            List<Long> sids = manWeekManufacturePlanItems.stream().map(li -> li.getWeekManufacturePlanItemSid()).collect(Collectors.toList());
            manDayManufacturePlanItemMapper.delete(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                    .in(ManDayManufacturePlanItem::getManufacturePlanItemSid,sids)
            );
        }
        //删除生产周计划-明细
        manWeekManufacturePlanItemMapper.delete(new UpdateWrapper<ManWeekManufacturePlanItem>().lambda()
                .in(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, weekManufacturePlanSids));
        //删除生产周计划-附件
        manWeekManufacturePlanAttachMapper.delete(new UpdateWrapper<ManWeekManufacturePlanAttach>().lambda()
                .in(ManWeekManufacturePlanAttach::getWeekManufacturePlanSid, weekManufacturePlanSids));
        manWeekManufacturePlanBanzuRemarkMapper.delete(new QueryWrapper<ManWeekManufacturePlanBanzuRemark>().lambda()
        .in(ManWeekManufacturePlanBanzuRemark::getWeekManufacturePlanSid,weekManufacturePlanSids)
        );
        ManWeekManufacturePlan manWeekManufacturePlan = new ManWeekManufacturePlan();
        weekManufacturePlanSids.forEach(weekManufacturePlanSid -> {
            manWeekManufacturePlan.setWeekManufacturePlanSid(weekManufacturePlanSid);
            //校验是否存在待办
            checkTodoExist(manWeekManufacturePlan);
        });
        return weekManufacturePlanSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param manWeekManufacturePlan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManWeekManufacturePlan manWeekManufacturePlan) {
        int row = 0;
        Long[] sids = manWeekManufacturePlan.getWeekManufacturePlanSidList();
        if (sids != null && sids.length > 0) {
            Integer count = manWeekManufacturePlanMapper.selectCount(new QueryWrapper<ManWeekManufacturePlan>().lambda()
                    .eq(ManWeekManufacturePlan::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(ManWeekManufacturePlan::getWeekManufacturePlanSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            List<String> msg = new ArrayList<>();
            for (Long id : sids) {
                List<ManWeekManufacturePlanItem> items =
                        manWeekManufacturePlanItemMapper.selectList(new QueryWrapper<ManWeekManufacturePlanItem>().lambda()
                                .eq(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, id));
                if (CollectionUtil.isEmpty(items)) {
                    throw new BaseException("存在生产周计划的明细为空，无法确认！");
                }
                if (!ConstantsEms.YES.equals(manWeekManufacturePlan.getContinueIsNull())) {
                    List<String> finalMsg = msg;
                    try {
                        items.forEach(li->{
                            List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = manDayManufacturePlanItemMapper.selectList(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                                    .eq(ManDayManufacturePlanItem::getManufacturePlanItemSid, li.getWeekManufacturePlanItemSid())
                            );
                            if(CollectionUtil.isNotEmpty(manDayManufacturePlanItemList)){
                                List<ManDayManufacturePlanItem> it = manDayManufacturePlanItemList.stream().filter(m -> m.getPlanQuantity() == null).collect(Collectors.toList());
                                if(CollectionUtil.isNotEmpty(it) && it.size() == manDayManufacturePlanItemList.size()){
                                    ManWeekManufacturePlan plan = manWeekManufacturePlanMapper.selectById(id);
                                    finalMsg.add(plan.getWeekManufacturePlanCode().toString());
                                    throw new RuntimeException("");
                                }
                            }
                        });
                    } catch (Exception e) {}
                }
            }
            if (CollectionUtil.isNotEmpty(msg)) {
                List<String> msgs = new ArrayList<>();
                msg = msg.stream().sorted(Comparator.comparingDouble(Long::parseLong)).collect(Collectors.toList());
                msg.forEach(o->{
                    msgs.add(o.toString()+"，”明细清单”页签中，存在明细行的数量值都为空！");
                });
                throw new BaseException(EmsResultEntity.WARN_TAG, null, (Object[]) msgs.toArray());
            }
            for (Long id : sids) {
                manWeekManufacturePlan.setWeekManufacturePlanSid(id);
                //校验是否存在待办
                checkTodoExist(manWeekManufacturePlan);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, manWeekManufacturePlan.getHandleStatus(), msgList, TITLE, null);
            }
            row = manWeekManufacturePlanMapper.update(null, new UpdateWrapper<ManWeekManufacturePlan>().lambda()
                    .set(ManWeekManufacturePlan::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(ManWeekManufacturePlan::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(ManWeekManufacturePlan::getConfirmDate, new Date())
                    .in(ManWeekManufacturePlan::getWeekManufacturePlanSid, sids));
        }
        return row;
    }

    /**
     * 作废-生产周计划
     */
    @Override
    public int cancellationWeekManufacturePlanById(Long weekManufacturePlanSid) {
        ManWeekManufacturePlan manWeekManufacturePlan = manWeekManufacturePlanMapper.selectManWeekManufacturePlanById(weekManufacturePlanSid);
        if (!ConstantsEms.CHECK_STATUS.equals(manWeekManufacturePlan.getHandleStatus())) {
            throw new BaseException(ConstantsEms.CONFIRM_CANCELLATION);
        }
        //插入日志
        MongodbUtil.insertUserLog(weekManufacturePlanSid, BusinessType.CANCEL.getValue(), manWeekManufacturePlan, manWeekManufacturePlan, TITLE);
        manWeekManufacturePlan.setHandleStatus(ConstantsEms.HANDLE_IM);
        manWeekManufacturePlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manWeekManufacturePlan.setUpdateDate(new Date());
        return manWeekManufacturePlanMapper.updateById(manWeekManufacturePlan);
    }

    /**
     * 提交前校验-生产周计划
     */
    @Override
    public int verify(Long weekManufacturePlanSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<ManWeekManufacturePlanItem> items =
                    manWeekManufacturePlanItemMapper.selectList(new QueryWrapper<ManWeekManufacturePlanItem>().lambda()
                            .eq(ManWeekManufacturePlanItem::getWeekManufacturePlanSid, weekManufacturePlanSid));
            if (CollectionUtil.isEmpty(items)) {
                throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
            }
            //校验是否存在待办
            checkTodoExist(new ManWeekManufacturePlan().setWeekManufacturePlanSid(weekManufacturePlanSid));
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(weekManufacturePlanSid, BusinessType.SUBMIT.getValue(), msgList, TITLE);
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return 1;
    }
}
