package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsProcess;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.ComUtil;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 班组生产日报Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManDayManufactureProgressServiceImpl extends ServiceImpl<ManDayManufactureProgressMapper, ManDayManufactureProgress> implements IManDayManufactureProgressService {
    @Autowired
    private ManDayManufactureProgressMapper manDayManufactureProgressMapper;
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;
    @Autowired
    private ManDayManufactureProgressDetailMapper manDayManufactureProgressDetailMapper;
    @Autowired
    private ManDayManufactureKuanProgressMapper manDayManufactureKuanProgressMapper;
    @Autowired
    private ManDayManufactureProgressAttachMapper manDayManufactureProgressAttachMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private IPayTeamWorkattendDayService payTeamWorkattendDayService;
    @Autowired
    private IManDayManufactureKuanProgressService manDayManufactureKuanProgressService;
    @Autowired
    private PayTeamWorkattendDayMapper payTeamWorkattendDayMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private IWorkFlowService workflowService;


    private static final String TITLE = "班组生产日报";

    /**
     * 查询班组生产日报
     *
     * @param dayManufactureProgressSid 班组生产日报ID
     * @return 班组生产日报
     */
    @Override
    public ManDayManufactureProgress selectManDayManufactureProgressById(Long dayManufactureProgressSid) {
        ManDayManufactureProgress manDayManufactureProgress = manDayManufactureProgressMapper.selectManDayManufactureProgressById(dayManufactureProgressSid);
        if (manDayManufactureProgress == null) {
            return null;
        }
        //班组生产日报-明细
        ManDayManufactureProgressItem manDayManufactureProgressItem = new ManDayManufactureProgressItem();
        manDayManufactureProgressItem.setDayManufactureProgressSid(dayManufactureProgressSid);
        List<ManDayManufactureProgressItem> manDayManufactureProgressItemList =
                manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemList(manDayManufactureProgressItem);
        if (CollectionUtil.isNotEmpty(manDayManufactureProgressItemList)){
            for (ManDayManufactureProgressItem item : manDayManufactureProgressItemList) {
                // 图片视频
                item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                //
                List<ManDayManufactureProgressDetail> detailList = manDayManufactureProgressDetailMapper.selectManDayManufactureProgressDetailList(
                        new ManDayManufactureProgressDetail().setDayManufactureProgressItemSid(item.getDayManufactureProgressItemSid()));
                item.setProgressDetailList(detailList);
                // 新优化 分配量的取值
                /*
                 * 未获取到数据，根据如下逻辑获取：
                   2.1 通过“班组生产日报明细中的生产订单号、明细行中工序所属的“操作部门”，从“生产订单-工序表”中获取到该“生产订单、操作部门”下的所有工序明细行sid（manufacture_order_process_sid）
                   2.2 对2.1获取到的所有工序明细行sid，按“生产订单工序sid  + 班组生产日报明细sku1颜色sid  + 班组生产日报明细表的班组”维度，获取处理状态是“已确认”的周计划的“分配量”的值
                   2.3 若2.2 获取到多个“分配量”的值，则获取”周计划日期“最大的”分配量“的值
                   2.4 若2.3 获取到多个“分配量”的值，则随机取值其中1个
                 */
                if (item.getQuantityFenpei() == null) {
                    List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(
                            new ManManufactureOrderProcess().setManufactureOrderSid(item.getManufactureOrderSid())
                                    .setDepartmentSid(item.getDepartmentSid())
                    );
                    if (CollectionUtil.isNotEmpty(processList)) {
                        Long[] manOrderProSids = processList.stream().map(ManManufactureOrderProcess::getManufactureOrderProcessSid).toArray(Long[]::new);
                        List<ManDayManufactureProgressItem> newFenpei = manDayManufactureProgressItemMapper.getQuantityFenpei(new ManDayManufactureProgressItem()
                                .setManufactureOrderProcessSidList(manOrderProSids)
                                .setWorkCenterSid(item.getWorkCenterSid())
                                .setSku1Sid(item.getSku1Sid()).setSku1SidIsNull(ConstantsEms.YES));
                        if (CollectionUtil.isNotEmpty(newFenpei)) {
                            newFenpei = newFenpei.stream().sorted(Comparator.comparing(ManDayManufactureProgressItem::getDateStart).reversed()).collect(Collectors.toList());
                            item.setQuantityFenpei(newFenpei.get(0).getQuantityFenpei());
                        }
                    }
                }
                BigDecimal fen = item.getQuantityFenpei() == null ? BigDecimal.ZERO : item.getQuantityFenpei();
                BigDecimal wan = item.getTotalCompleteQuantity() == null ? BigDecimal.ZERO : new BigDecimal(item.getTotalCompleteQuantity());
                item.setShicaiUnfinishedQuantity(fen.subtract(wan));
            }
        }
        //班组生产日报-款生产进度对象
        List<ManDayManufactureKuanProgress> kuanProgressList =
                manDayManufactureKuanProgressMapper.selectManDayManufactureKuanProgressList(new ManDayManufactureKuanProgress().setDayManufactureProgressSid(dayManufactureProgressSid));
        manDayManufactureProgress.setKuanProcessList(kuanProgressList);
        //班组生产日报-附件
        ManDayManufactureProgressAttach manDayManufactureProgressAttach = new ManDayManufactureProgressAttach();
        manDayManufactureProgressAttach.setDayManufactureProgressSid(dayManufactureProgressSid);
        List<ManDayManufactureProgressAttach> manDayManufactureProgressAttachList =
                manDayManufactureProgressAttachMapper.selectManDayManufactureProgressAttachList(manDayManufactureProgressAttach);
        manDayManufactureProgress.setDayManufactureProgressItemList(manDayManufactureProgressItemList);
        manDayManufactureProgress.setAttachmentList(manDayManufactureProgressAttachList);
        //操作日志
        MongodbUtil.find(manDayManufactureProgress);
        return manDayManufactureProgress;
    }

    /**
     * 查询班组生产日报列表
     *
     * @param manDayManufactureProgress 班组生产日报
     * @return 班组生产日报
     */
    @Override
    public List<ManDayManufactureProgress> selectManDayManufactureProgressList(ManDayManufactureProgress manDayManufactureProgress) {
        return manDayManufactureProgressMapper.selectManDayManufactureProgressList(manDayManufactureProgress);
    }

    /**
     * 设置工厂编码
     * @param manDayManufactureProgress 班组生产日报
     */
    private void setPlantCode(ManDayManufactureProgress progress) {
        if (progress.getPlantSid() != null) {
            BasPlant plant = basPlantMapper.selectById(progress.getPlantSid());
            if (plant != null) {
                progress.setPlantCode(plant.getPlantCode());
            }
        }
        else {
            progress.setPlantCode(null);
        }
    }

    /**
     * 设置班组编码
     * @param manDayManufactureProgress 班组生产日报
     */
    private String getWorkCenterCode(Long workCenterSid) {
        if (workCenterSid != null) {
            ManWorkCenter workCenter = manWorkCenterMapper.selectManWorkCenterById(workCenterSid);
            if (workCenter != null) {
               return workCenter.getWorkCenterCode();
            }
        }
        return null;
    }

    /**
     * 设置操作部门编码
     * @param manDayManufactureProgress 班组生产日报
     */
    private String getDepartmentCode(Long departmentSid) {
        if (departmentSid != null) {
            ConManufactureDepartment department = conManufactureDepartmentMapper.selectById(departmentSid);
            if (department != null) {
                return department.getCode();
            }
        }
        return null;
    }

    /**
     * 对班组日出勤表进行处理
     * @param manDayManufactureProgress 班组生产日报
     */
    private void setPayTeamWorkattendDay(ManDayManufactureProgress progress) {
        PayTeamWorkattendDay payTeamWorkattendDay = new PayTeamWorkattendDay();
        payTeamWorkattendDay.setPlantSid(progress.getPlantSid()).setWorkCenterSid(progress.getWorkCenterSid())
                .setWorkattendDate(progress.getDocumentDate()).setWorkShift(progress.getWorkShift());
        List<PayTeamWorkattendDay> list = payTeamWorkattendDayService.selectPayTeamWorkattendDayList(payTeamWorkattendDay);
        if (CollectionUtil.isNotEmpty(list)) {
            PayTeamWorkattendDay day = list.get(0);
            day.setYingcq(progress.getYingcq()).setShicq(progress.getShicq()).setQingj(progress.getQingj())
                    .setDail(progress.getDail()).setKuangg(progress.getKuangg()).setRemark(progress.getWorkattendRemark());
            if (progress.getWorkCenterSid() != null) {
                day.setDepartment(progress.getDepartment());
            }
            payTeamWorkattendDayService.updatePayTeamWorkattendDay(day);
        }
        else {
            payTeamWorkattendDay.setYingcq(progress.getYingcq()).setShicq(progress.getShicq()).setQingj(progress.getQingj())
                    .setDail(progress.getDail()).setKuangg(progress.getKuangg()).setRemark(progress.getWorkattendRemark());
            if (progress.getWorkCenterSid() != null) {
                payTeamWorkattendDay.setDepartment(progress.getDepartment());
            }
            payTeamWorkattendDay.setPlantCode(progress.getPlantCode()).setWorkCenterCode(progress.getWorkCenterCode());
            payTeamWorkattendDay.setRemark(progress.getWorkattendRemark());
            payTeamWorkattendDay.setHandleStatus(ConstantsEms.SAVA_STATUS);
            payTeamWorkattendDayService.insertPayTeamWorkattendDay(payTeamWorkattendDay);
        }
    }

    /**
     * 新增班组生产日报
     * 需要注意编码重复校验
     *
     * @param manDayManufactureProgress 班组生产日报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity insertManDayManufactureProgress(ManDayManufactureProgress manDayManufactureProgress) {
        checkUnique(manDayManufactureProgress);
        setConfirmInfo(manDayManufactureProgress);
        setPlantCode(manDayManufactureProgress);
        manDayManufactureProgress.setWorkCenterCode(getWorkCenterCode(manDayManufactureProgress.getWorkCenterSid()));
        manDayManufactureProgress.setDepartmentCode(getDepartmentCode(manDayManufactureProgress.getDepartmentSid()));
        int row = manDayManufactureProgressMapper.insert(manDayManufactureProgress);
        if (row > 0) {
            //班组生产日报-明细对象
            List<ManDayManufactureProgressItem> dayManufactureProgressItemList = manDayManufactureProgress.getDayManufactureProgressItemList();
            if (CollectionUtils.isNotEmpty(dayManufactureProgressItemList)) {
                addManDayManufactureProgressItem(manDayManufactureProgress, dayManufactureProgressItemList);
            }
            manDayManufactureProgress.setDayManufactureProgressItemList(dayManufactureProgressItemList);
            //班组生产日报-附件对象
            List<ManDayManufactureProgressAttach> dayManufactureProgressAttachList = manDayManufactureProgress.getAttachmentList();
            if (CollectionUtils.isNotEmpty(dayManufactureProgressAttachList)) {
                addManDayManufactureProgressAttach(manDayManufactureProgress, dayManufactureProgressAttachList);
            }
            //班组生产日报-款生产进度对象
            if (CollectionUtil.isEmpty(dayManufactureProgressItemList)) {
                deleteKuanProgress(manDayManufactureProgress);
            }
            List<ManDayManufactureKuanProgress> kuanProgressList = manDayManufactureKuanProgressService.selectManDayManufactureProgressKuanList(manDayManufactureProgress);
            manDayManufactureProgress.setKuanProcessList(kuanProgressList);
            if (CollectionUtils.isNotEmpty(kuanProgressList)) {
                addManDayManufactureKuanProgress(manDayManufactureProgress, kuanProgressList);
            }
            ManDayManufactureProgress manufactureProgress =
                    manDayManufactureProgressMapper.selectManDayManufactureProgressById(manDayManufactureProgress.getDayManufactureProgressSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(manDayManufactureProgress.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_DAY_MANUFACTURE_PROGRESS)
                        .setDocumentSid(manDayManufactureProgress.getDayManufactureProgressSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("班组生产日报" + manufactureProgress.getDayManufactureProgressCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(manufactureProgress.getDayManufactureProgressCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(manDayManufactureProgress.getDayManufactureProgressSid(), manDayManufactureProgress.getHandleStatus(), msgList, TITLE, null);
        }
        else {
            throw new BaseException("操作失败");
        }
        return EmsResultEntity.success(String.valueOf(manDayManufactureProgress.getDayManufactureProgressSid()));
    }

    /**
     * 新增/编辑直接提交生产进度日报
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(ManDayManufactureProgress manDayManufactureProgress) {
        int row = 0;
        AjaxResult result = null;
        EmsResultEntity resultEntity = null;
        if (manDayManufactureProgress.getDayManufactureProgressSid() == null) {
            // 新建
            resultEntity = this.insertManDayManufactureProgress(manDayManufactureProgress);
            if (resultEntity.getData() != null) {
                row = 1;
            }
        }
        else {
            row = this.updateManDayManufactureProgress(manDayManufactureProgress);
        }
        if (row == 1) {
            Long[] sidList = new Long[]{manDayManufactureProgress.getDayManufactureProgressSid()};
            row = this.check(new ManDayManufactureProgress().setDayManufactureProgressSidList(sidList)
                    .setHandleStatus(ConstantsEms.CHECK_STATUS));
        }
        return AjaxResult.success(manDayManufactureProgress);
    }

    /**
     * 需再次校验页签每一个明细行的所有完工明细行的完工量的总和必须<=该明细行的当天完成量/发料量/收料量
     */
    private void checkDetail(List<ManDayManufactureProgressItem> itemList){
        if (CollectionUtil.isNotEmpty(itemList)){
            BigDecimal quantity = BigDecimal.ZERO;
            BigDecimal quantityDetail = BigDecimal.ZERO;
            for (ManDayManufactureProgressItem item : itemList) {
                quantity = item.getQuantity();
                if (CollectionUtil.isNotEmpty(item.getProgressDetailList())){
                    quantityDetail = item.getProgressDetailList().parallelStream().map(ManDayManufactureProgressDetail::getQuantity)
                            .reduce(BigDecimal.ZERO,BigDecimal::add);
                    if (quantityDetail.compareTo(quantity) != 0){
                        throw new BaseException("第" + item.getItemNum() + "行，当天完成量不等于完工明细总和，请核实！");
                    }
                }
            }
        }
    }

    /**
     * 唯一性校验
     */
    private void checkUnique(ManDayManufactureProgress o) {
        // 主表唯一性校验
        QueryWrapper<ManDayManufactureProgress> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ManDayManufactureProgress::getEnterMode, o.getEnterMode());
        if (o.getDayManufactureProgressSid() != null) {
            queryWrapper.lambda().ne(ManDayManufactureProgress::getDayManufactureProgressSid, o.getDayManufactureProgressSid());
        }
        if (ConstantsProcess.DAY_MAN_PRO_GC.equals(o.getEnterMode())) {
            queryWrapper.lambda().eq(ManDayManufactureProgress::getPlantSid, o.getPlantSid())
                    .eq(ManDayManufactureProgress::getDocumentDate, o.getDocumentDate());
            List<ManDayManufactureProgress> list = manDayManufactureProgressMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("“工厂(工序)+汇报日期“的值的组合已存在，请检查！");
            }
        }
        else if (ConstantsProcess.DAY_MAN_PRO_BZ.equals(o.getEnterMode())) {
            queryWrapper.lambda().eq(ManDayManufactureProgress::getPlantSid, o.getPlantSid())
                    .eq(ManDayManufactureProgress::getWorkCenterSid, o.getWorkCenterSid())
                    .eq(ManDayManufactureProgress::getDocumentDate, o.getDocumentDate());
            List<ManDayManufactureProgress> list = manDayManufactureProgressMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("“工厂(工序)+班组+汇报日期“的值的组合已存在，请检查！");
            }
            if (CollectionUtil.isNotEmpty(o.getDayManufactureProgressItemList())) {
                List<ManDayManufactureProgressItem> itemList = o.getDayManufactureProgressItemList().stream().filter(e ->
                        !e.getWorkCenterSid().equals(o.getWorkCenterSid())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(itemList)) {
                    throw new BaseException("“工序进度“页签，存在明细行的班组与表头的班组不一致，请检查！");
                }
            }
        }
        else if (ConstantsProcess.DAY_MAN_PRO_CZBM.equals(o.getEnterMode())) {
            queryWrapper.lambda().eq(ManDayManufactureProgress::getPlantSid, o.getPlantSid())
                    .eq(ManDayManufactureProgress::getDepartmentSid, o.getDepartmentSid())
                    .eq(ManDayManufactureProgress::getDocumentDate, o.getDocumentDate());
            List<ManDayManufactureProgress> list = manDayManufactureProgressMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                throw new BaseException("“工厂(工序)+操作部门+汇报日期“的值的组合已存在，请检查！");
            }
            if (CollectionUtil.isNotEmpty(o.getDayManufactureProgressItemList())) {
                List<ManDayManufactureProgressItem> itemList = o.getDayManufactureProgressItemList().stream().filter(e ->
                        !e.getDepartmentSid().equals(o.getDepartmentSid())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(itemList)) {
                    throw new BaseException("“工序进度“页签，存在明细行的操作部门与表头的操作部门不一致，请检查！");
                }
            }
        }
        // 明细唯一性校验
        if (CollectionUtil.isNotEmpty(o.getDayManufactureProgressItemList())) {
            Map<String, List<ManDayManufactureProgressItem>> map = o.getDayManufactureProgressItemList().stream()
                    .collect(Collectors.groupingBy(item -> String.valueOf(item.getWorkCenterSid())+"-"+String.valueOf(item.getManufactureOrderProcessSid())+
                            "-"+String.valueOf(item.getSku1Sid())));
            if (map.size() != o.getDayManufactureProgressItemList().size()) {
                throw new BaseException("“工序进度“页签，“班组+商品编码+颜色+排产批次号+工序”存在重复数据，请检查！");
            }
            List<ManDayManufactureProgressItem> itemList = o.getDayManufactureProgressItemList().stream().filter(e ->
                    ConstantsProcess.MAN_PROCESS_SPECIAL_FLAG_CHUH.equals(e.getSpecialFlag()) && (e.getXiangshu() == null || e.getChuhuoQuantity() == null)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemList)) {
                String[] processNames = itemList.stream().map(ManDayManufactureProgressItem::getProcessName).distinct().toArray(String[]::new);
                String name = "";
                for (int i = 0; i < processNames.length; i++) {
                    name = name + processNames[i] + ";";
                }
                if (name.endsWith(";")) {
                    name = name.substring(0,name.length() - 1);
                }
                throw new BaseException(name + "，有明细行的出货数、箱数为空，请检查！");
            }
        }
        // 明细校验
        if (CollectionUtil.isNotEmpty(o.getDayManufactureProgressItemList())) {

            List<ManDayManufactureProgressItem> itemList = o.getDayManufactureProgressItemList().stream().filter(e ->
                    ConstantsProcess.MAN_PROCESS_SPECIAL_FLAG_CAIC.equals(e.getSpecialFlag())
                            && e.getDanjianhaoliang() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemList)) {
                String[] processNames = itemList.stream().map(ManDayManufactureProgressItem::getProcessName).distinct().toArray(String[]::new);
                String name = "";
                for (int i = 0; i < processNames.length; i++) {
                    name = name + processNames[i] + ";";
                }
                if (name.endsWith(";")) {
                    name = name.substring(0,name.length() - 1);
                }
                throw new BaseException(name + "，有明细行的单件耗量为空，请检查！");
            }
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManDayManufactureProgress o) {
        if (o == null) {
            return;
        }
        if (o.getDocumentDate() != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  // 设置日期格式
            String yearMonth = simpleDateFormat.format(o.getDocumentDate()).substring(0,7);
            o.setYearmonth(yearMonth);
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            List<ManDayManufactureProgressItem> dayManufactureProgressItemList = o.getDayManufactureProgressItemList();
            if (CollectionUtil.isEmpty(dayManufactureProgressItemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 班组生产日报-明细对象
     */
    private void addManDayManufactureProgressItem(ManDayManufactureProgress manDayManufactureProgress, List<ManDayManufactureProgressItem> dayManufactureProgressItemList) {
        long i = 1;
        Long maxItemNum = manDayManufactureProgress.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (ManDayManufactureProgressItem o : dayManufactureProgressItemList) {
            judgeItemQuantity(o);
            o.setDayManufactureProgressSid(manDayManufactureProgress.getDayManufactureProgressSid());
            o.setCompleteType(ConstantsEms.JXCG);
            o.setWorkCenterCode(getWorkCenterCode(o.getWorkCenterSid()));
            o.setDepartmentCode(getDepartmentCode(o.getDepartmentSid()));
            o.setItemNum(i);
            i++;
        }
        manDayManufactureProgressItemMapper.inserts(dayManufactureProgressItemList);
        checkDetail(dayManufactureProgressItemList);
        // 完工明细对象
        if (CollectionUtil.isNotEmpty(dayManufactureProgressItemList)){
            List<ManDayManufactureProgressDetail> detailList = new ArrayList<>();
            for (ManDayManufactureProgressItem item : dayManufactureProgressItemList) {
                if (CollectionUtil.isNotEmpty(item.getProgressDetailList())){
                    item.getProgressDetailList().forEach(detail->{
                        detail.setDayManufactureProgressItemSid(item.getDayManufactureProgressItemSid());
                    });
                    detailList.addAll(item.getProgressDetailList());
                }
            }
            if (CollectionUtil.isNotEmpty(detailList)){
                manDayManufactureProgressDetailMapper.inserts(detailList);
            }
        }
    }

    private void deleteItem(ManDayManufactureProgress manDayManufactureProgress) {
        List<ManDayManufactureProgressItem> itemList = manDayManufactureProgressItemMapper.selectList(new QueryWrapper<ManDayManufactureProgressItem>()
                        .lambda()
                        .eq(ManDayManufactureProgressItem::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid()));
        if (CollectionUtil.isNotEmpty(itemList)){
            manDayManufactureProgressItemMapper.delete(
                    new QueryWrapper<ManDayManufactureProgressItem>()
                            .lambda()
                            .eq(ManDayManufactureProgressItem::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid())
            );
            List<Long> sidList = itemList.stream().map(ManDayManufactureProgressItem::getDayManufactureProgressItemSid).collect(Collectors.toList());
            manDayManufactureProgressDetailMapper.delete(new QueryWrapper<ManDayManufactureProgressDetail>().lambda()
                    .in(ManDayManufactureProgressDetail::getDayManufactureProgressItemSid,sidList));
        }
    }

    /**
     * 班组生产日报-款生产进度对象对象
     */
    private void addManDayManufactureKuanProgress(ManDayManufactureProgress manDayManufactureProgress, List<ManDayManufactureKuanProgress> kuanProgressList) {
        kuanProgressList.forEach(o -> {
            o.setDayManufactureProgressSid(manDayManufactureProgress.getDayManufactureProgressSid());
            manDayManufactureKuanProgressMapper.insert(o);
        });
    }

    private void deleteKuanProgress(ManDayManufactureProgress manDayManufactureProgress) {
        manDayManufactureKuanProgressMapper.delete(
                new UpdateWrapper<ManDayManufactureKuanProgress>()
                        .lambda()
                        .eq(ManDayManufactureKuanProgress::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid())
        );
    }

    /**
     * 班组生产日报-附件对象
     */
    private void addManDayManufactureProgressAttach(ManDayManufactureProgress manDayManufactureProgress, List<ManDayManufactureProgressAttach> dayManufactureProgressAttachList) {
        dayManufactureProgressAttachList.forEach(o -> {
            o.setDayManufactureProgressSid(manDayManufactureProgress.getDayManufactureProgressSid());
            manDayManufactureProgressAttachMapper.insert(o);
        });
    }

    private void deleteAttach(ManDayManufactureProgress manDayManufactureProgress) {
        manDayManufactureProgressAttachMapper.delete(
                new UpdateWrapper<ManDayManufactureProgressAttach>()
                        .lambda()
                        .eq(ManDayManufactureProgressAttach::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid())
        );
    }

    /**
     * 修改班组生产日报
     *
     * @param manDayManufactureProgress 班组生产日报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManDayManufactureProgress(ManDayManufactureProgress manDayManufactureProgress) {
        ManDayManufactureProgress response = manDayManufactureProgressMapper.selectManDayManufactureProgressById(manDayManufactureProgress.getDayManufactureProgressSid());
        checkUnique(manDayManufactureProgress);
        setConfirmInfo(manDayManufactureProgress);
        if (manDayManufactureProgress.getPlantSid() == null) {
            manDayManufactureProgress.setPlantCode(null);
        }
        else if (!manDayManufactureProgress.getPlantSid().equals(response.getPlantSid())) {
            setPlantCode(manDayManufactureProgress);
        }
        if (manDayManufactureProgress.getWorkCenterSid() == null) {
            manDayManufactureProgress.setWorkCenterCode(null);
        }
        else if (!manDayManufactureProgress.getWorkCenterSid().equals(response.getWorkCenterSid())) {
            manDayManufactureProgress.setWorkCenterCode(getWorkCenterCode(manDayManufactureProgress.getWorkCenterSid()));
        }
        if (manDayManufactureProgress.getDepartmentSid() == null) {
            manDayManufactureProgress.setDepartmentCode(null);
        }
        else if (!manDayManufactureProgress.getDepartmentSid().equals(response.getDepartmentSid())) {
            manDayManufactureProgress.setDepartmentCode(getDepartmentCode(manDayManufactureProgress.getDepartmentSid()));
        }
        manDayManufactureProgress.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manDayManufactureProgressMapper.updateAllById(manDayManufactureProgress);
        if (row > 0) {
            //班组生产日报-明细对象
            List<ManDayManufactureProgressItem> progressItemList = manDayManufactureProgress.getDayManufactureProgressItemList();
            operateItem(manDayManufactureProgress, progressItemList);
            //班组生产日报-款生产进度对象
            if (CollectionUtil.isEmpty(progressItemList)) {
                deleteKuanProgress(manDayManufactureProgress);
            }
            else {
                List<ManDayManufactureKuanProgress> kuanProgressList = manDayManufactureKuanProgressService.selectManDayManufactureProgressKuanList(manDayManufactureProgress);
                manDayManufactureProgress.setKuanProcessList(kuanProgressList);
                operateKuanProcess(manDayManufactureProgress, kuanProgressList, progressItemList);
            }
            //班组生产日报-附件对象
            List<ManDayManufactureProgressAttach> progressAttachList = manDayManufactureProgress.getAttachmentList();
            operateAttachment(manDayManufactureProgress, progressAttachList);
            if (!ConstantsEms.SAVA_STATUS.equals(manDayManufactureProgress.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsEms.TABLE_DAY_MANUFACTURE_PROGRESS)
                        .eq(SysTodoTask::getDocumentSid, manDayManufactureProgress.getDayManufactureProgressSid()));
            }
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manDayManufactureProgress);
            MongodbDeal.update(manDayManufactureProgress.getDayManufactureProgressSid(), response.getHandleStatus(), manDayManufactureProgress.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    private void judgeItemQuantity(ManDayManufactureProgressItem item){
//        if (BigDecimal.ZERO.compareTo(item.getJieshouQuantity()==null?BigDecimal.ZERO:item.getJieshouQuantity()) > 0){
//            throw new BaseException("“当天接收量(上一工序)”必须大于等于0");
//        }
    }

    /**
     * 班组生产日报-明细
     */
    private void operateItem(ManDayManufactureProgress manDayManufactureProgress, List<ManDayManufactureProgressItem> planItemList) {
        if (CollectionUtil.isNotEmpty(planItemList)) {
            //最大行号
            List<Long> itemNums = planItemList.stream().filter(o -> o.getItemNum() != null).map(ManDayManufactureProgressItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manDayManufactureProgress.setMaxItemNum(maxItemNum);
            }
            //新增
            List<ManDayManufactureProgressItem> addList = planItemList.stream().filter(o -> o.getDayManufactureProgressItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManDayManufactureProgressItem(manDayManufactureProgress, addList);
            }
            //编辑
            List<ManDayManufactureProgressItem> editList = planItemList.stream().filter(o -> o.getDayManufactureProgressItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    judgeItemQuantity(o);
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manDayManufactureProgressItemMapper.updateAllById(o);
                });
                checkDetail(editList);
                // 完工明细对象
                List<ManDayManufactureProgressDetail> detailList = new ArrayList<>();
                for (ManDayManufactureProgressItem item : editList) {
                    if (CollectionUtil.isNotEmpty(item.getProgressDetailList())){
                        //原有数据
                        List<ManDayManufactureProgressDetail> detailOldList = manDayManufactureProgressDetailMapper.selectList(new QueryWrapper<ManDayManufactureProgressDetail>().lambda()
                                .eq(ManDayManufactureProgressDetail::getDayManufactureProgressItemSid, item.getDayManufactureProgressItemSid()));
                        //原有数据ids
                        List<Long> detailOldSidList = detailOldList.stream().map(ManDayManufactureProgressDetail::getDayManufactureProgressDetailSid).collect(Collectors.toList());
                        //现有数据ids
                        List<Long> detailNowSidList = item.getProgressDetailList().stream().map(ManDayManufactureProgressDetail::getDayManufactureProgressDetailSid).collect(Collectors.toList());
                        //清空删除的数据
                        List<Long> detailDelSidList = detailOldSidList.stream().filter(id -> !detailNowSidList.contains(id)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(detailDelSidList)) {
                            manDayManufactureProgressDetailMapper.deleteBatchIds(detailDelSidList);
                        }
                        item.getProgressDetailList().forEach(detail->{
                            detail.setDayManufactureProgressItemSid(item.getDayManufactureProgressItemSid());
                            if (detail.getDayManufactureProgressDetailSid() == null){
                                detailList.add(detail);
                            }else {
                                detail.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                                manDayManufactureProgressDetailMapper.updateAllById(detail);
                            }
                        });
                    }
                    else {
                        manDayManufactureProgressDetailMapper.delete(new QueryWrapper<ManDayManufactureProgressDetail>().lambda()
                                .eq(ManDayManufactureProgressDetail::getDayManufactureProgressItemSid,item.getDayManufactureProgressItemSid()));
                    }
                }
                if (CollectionUtil.isNotEmpty(detailList)){
                    manDayManufactureProgressDetailMapper.inserts(detailList);
                }
            }
            //原有数据
            List<ManDayManufactureProgressItem> itemList = manDayManufactureProgressItemMapper.selectList(new QueryWrapper<ManDayManufactureProgressItem>().lambda()
                    .eq(ManDayManufactureProgressItem::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManDayManufactureProgressItem::getDayManufactureProgressItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = planItemList.stream().map(ManDayManufactureProgressItem::getDayManufactureProgressItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manDayManufactureProgressItemMapper.deleteBatchIds(result);
                manDayManufactureProgressDetailMapper.delete(new QueryWrapper<ManDayManufactureProgressDetail>().lambda()
                        .in(ManDayManufactureProgressDetail::getDayManufactureProgressItemSid,result));
            }
        } else {
            deleteItem(manDayManufactureProgress);
        }
    }

    /**
     * 班组生产日报-款生产进度对象对象
     */
    private void operateKuanProcess(ManDayManufactureProgress manDayManufactureProgress, List<ManDayManufactureKuanProgress> kuanProgressList,
                                    List<ManDayManufactureProgressItem> progressItemList) {
        if (CollectionUtil.isNotEmpty(kuanProgressList)) {
            //新增
            List<ManDayManufactureKuanProgress> addList = kuanProgressList.stream().filter(o -> o.getDayManufactureKuanProgressSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManDayManufactureKuanProgress(manDayManufactureProgress, addList);
            }
            //编辑
            List<ManDayManufactureKuanProgress> editList = kuanProgressList.stream().filter(o -> o.getDayManufactureKuanProgressSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manDayManufactureKuanProgressMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManDayManufactureKuanProgress> itemList =
                    manDayManufactureKuanProgressMapper.selectList(new QueryWrapper<ManDayManufactureKuanProgress>().lambda()
                            .eq(ManDayManufactureKuanProgress::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManDayManufactureKuanProgress::getDayManufactureKuanProgressSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = kuanProgressList.stream().map(ManDayManufactureKuanProgress::getDayManufactureKuanProgressSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manDayManufactureKuanProgressMapper.deleteBatchIds(result);
            }
        }
    }

    /**
     * 班组生产日报-附件
     */
    private void operateAttachment(ManDayManufactureProgress manDayManufactureProgress, List<ManDayManufactureProgressAttach> planAttachList) {
        if (CollectionUtil.isNotEmpty(planAttachList)) {
            //新增
            List<ManDayManufactureProgressAttach> addList = planAttachList.stream().filter(o -> o.getDayManufactureProgressAttachSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManDayManufactureProgressAttach(manDayManufactureProgress, addList);
            }
            //编辑
            List<ManDayManufactureProgressAttach> editList = planAttachList.stream().filter(o -> o.getDayManufactureProgressAttachSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manDayManufactureProgressAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManDayManufactureProgressAttach> itemList =
                    manDayManufactureProgressAttachMapper.selectList(new QueryWrapper<ManDayManufactureProgressAttach>().lambda()
                            .eq(ManDayManufactureProgressAttach::getDayManufactureProgressSid, manDayManufactureProgress.getDayManufactureProgressSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManDayManufactureProgressAttach::getDayManufactureProgressAttachSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = planAttachList.stream().map(ManDayManufactureProgressAttach::getDayManufactureProgressAttachSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manDayManufactureProgressAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manDayManufactureProgress);
        }
    }

    /**
     * 变更班组生产日报
     *
     * @param manDayManufactureProgress 班组生产日报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManDayManufactureProgress(ManDayManufactureProgress manDayManufactureProgress) {
        ManDayManufactureProgress response = manDayManufactureProgressMapper.selectManDayManufactureProgressById(manDayManufactureProgress.getDayManufactureProgressSid());
        checkUnique(manDayManufactureProgress);
        setConfirmInfo(manDayManufactureProgress);
        if (manDayManufactureProgress.getPlantSid() == null) {
            manDayManufactureProgress.setPlantCode(null);
        }
        else if (!manDayManufactureProgress.getPlantSid().equals(response.getPlantSid())) {
            setPlantCode(manDayManufactureProgress);
        }
        if (manDayManufactureProgress.getWorkCenterSid() == null) {
            manDayManufactureProgress.setWorkCenterCode(null);
        }
        else if (!manDayManufactureProgress.getWorkCenterSid().equals(response.getWorkCenterSid())) {
            manDayManufactureProgress.setWorkCenterCode(getWorkCenterCode(manDayManufactureProgress.getWorkCenterSid()));
        }
        if (manDayManufactureProgress.getDepartmentSid() == null) {
            manDayManufactureProgress.setDepartmentCode(null);
        }
        else if (!manDayManufactureProgress.getDepartmentSid().equals(response.getDepartmentSid())) {
            manDayManufactureProgress.setDepartmentCode(getDepartmentCode(manDayManufactureProgress.getDepartmentSid()));
        }
        manDayManufactureProgress.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manDayManufactureProgressMapper.updateAllById(manDayManufactureProgress);
        if (row > 0) {
            //班组生产日报-明细对象
            List<ManDayManufactureProgressItem> progressItemList = manDayManufactureProgress.getDayManufactureProgressItemList();
            operateItem(manDayManufactureProgress, progressItemList);
            //班组生产日报-款生产进度对象
            if (CollectionUtil.isEmpty(progressItemList)) {
                deleteKuanProgress(manDayManufactureProgress);
            }
            else {
                List<ManDayManufactureKuanProgress> kuanProgressList = manDayManufactureKuanProgressService.selectManDayManufactureProgressKuanList(manDayManufactureProgress);
                manDayManufactureProgress.setKuanProcessList(kuanProgressList);
                operateKuanProcess(manDayManufactureProgress, kuanProgressList, progressItemList);
            }
            //班组生产日报-附件对象
            List<ManDayManufactureProgressAttach> progressAttachList = manDayManufactureProgress.getAttachmentList();
            operateAttachment(manDayManufactureProgress, progressAttachList);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manDayManufactureProgress);
            MongodbDeal.update(manDayManufactureProgress.getDayManufactureProgressSid(), response.getHandleStatus(), manDayManufactureProgress.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除班组生产日报
     *
     * @param dayManufactureProgressSids 需要删除的班组生产日报ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManDayManufactureProgressByIds(List<Long> dayManufactureProgressSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = manDayManufactureProgressMapper.selectCount(new QueryWrapper<ManDayManufactureProgress>().lambda()
                .in(ManDayManufactureProgress::getHandleStatus, handleStatusList)
                .in(ManDayManufactureProgress::getDayManufactureProgressSid, dayManufactureProgressSids));
        if (count != dayManufactureProgressSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        //删除班组生产日报
        manDayManufactureProgressMapper.deleteBatchIds(dayManufactureProgressSids);
        List<ManDayManufactureProgressItem> itemList = manDayManufactureProgressItemMapper.selectList(new QueryWrapper<ManDayManufactureProgressItem>()
                .lambda()
                .in(ManDayManufactureProgressItem::getDayManufactureProgressSid, dayManufactureProgressSids));
        if (CollectionUtil.isNotEmpty(itemList)){
            List<Long> sidList = itemList.stream().map(ManDayManufactureProgressItem::getDayManufactureProgressItemSid).collect(Collectors.toList());
            manDayManufactureProgressDetailMapper.delete(new QueryWrapper<ManDayManufactureProgressDetail>().lambda()
                    .in(ManDayManufactureProgressDetail::getDayManufactureProgressItemSid,sidList));
            //删除班组生产日报明细
            manDayManufactureProgressItemMapper.delete(new QueryWrapper<ManDayManufactureProgressItem>().lambda()
                    .in(ManDayManufactureProgressItem::getDayManufactureProgressSid, dayManufactureProgressSids));
            manDayManufactureKuanProgressMapper.delete(new QueryWrapper<ManDayManufactureKuanProgress>().lambda()
                    .in(ManDayManufactureKuanProgress::getDayManufactureProgressSid, dayManufactureProgressSids));
        }
        //删除班组生产日报附件
        manDayManufactureProgressAttachMapper.delete(new QueryWrapper<ManDayManufactureProgressAttach>().lambda()
                .in(ManDayManufactureProgressAttach::getDayManufactureProgressSid, dayManufactureProgressSids));
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getTableName, ConstantsEms.TABLE_DAY_MANUFACTURE_PROGRESS)
                .in(SysTodoTask::getDocumentSid, dayManufactureProgressSids));
        return dayManufactureProgressSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param manDayManufactureProgress
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManDayManufactureProgress manDayManufactureProgress) {
        int row = 0;
        Long[] sids = manDayManufactureProgress.getDayManufactureProgressSidList();
        if (sids != null && sids.length > 0) {
            Integer count = manDayManufactureProgressMapper.selectCount(new QueryWrapper<ManDayManufactureProgress>().lambda()
                    .eq(ManDayManufactureProgress::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(ManDayManufactureProgress::getDayManufactureProgressSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            for (Long id : sids) {
                List<ManDayManufactureProgressItem> items =
                        manDayManufactureProgressItemMapper.selectList(new QueryWrapper<ManDayManufactureProgressItem>().lambda()
                                .eq(ManDayManufactureProgressItem::getDayManufactureProgressSid, id));
                if (CollectionUtil.isEmpty(items)) {
                    throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }
                if (ConstantsEms.CHECK_STATUS.equals(manDayManufactureProgress.getHandleStatus())) {
                    manDayManufactureProgress.setConfirmDate(new Date());
                    manDayManufactureProgress.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                    // 修改全天的日出勤表为确认
                    ManDayManufactureProgress base = manDayManufactureProgressMapper.selectById(id);
                    if (base != null) {
                        List<PayTeamWorkattendDay> teamWorkattendDayList = payTeamWorkattendDayMapper.selectList(new QueryWrapper<PayTeamWorkattendDay>()
                                .lambda().eq(PayTeamWorkattendDay::getPlantSid, base.getPlantSid())
                                .eq(PayTeamWorkattendDay::getWorkCenterSid, base.getWorkCenterSid())
                                .eq(PayTeamWorkattendDay::getWorkattendDate, base.getDocumentDate())
                                .eq(PayTeamWorkattendDay::getWorkShift, "QT"));
                        if (CollectionUtil.isNotEmpty(teamWorkattendDayList)) {
                            List<Long> sidList = teamWorkattendDayList.stream().map(PayTeamWorkattendDay::getTeamWorkattendDaySid).collect(Collectors.toList());
                            LambdaUpdateWrapper<PayTeamWorkattendDay> updateWrapper = new LambdaUpdateWrapper<>();
                            updateWrapper.in(PayTeamWorkattendDay::getTeamWorkattendDaySid, sidList);
                            updateWrapper.set(PayTeamWorkattendDay::getHandleStatus, ConstantsEms.CHECK_STATUS);
                            updateWrapper.set(PayTeamWorkattendDay::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                            updateWrapper.set(PayTeamWorkattendDay::getConfirmDate, new Date());
                            payTeamWorkattendDayMapper.update(null, updateWrapper);
                            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PAY_TEAM_WORKATTEND_DAY)
                                    .in(SysTodoTask::getDocumentSid, sidList));
                        }

                    }
                }
                manDayManufactureProgress.setDayManufactureProgressSid(id);
                row = manDayManufactureProgressMapper.updateById(manDayManufactureProgress);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
            }
            if (ConstantsEms.CHECK_STATUS.equals(manDayManufactureProgress.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsEms.TABLE_DAY_MANUFACTURE_PROGRESS)
                        .in(SysTodoTask::getDocumentSid, sids));
            }
            for (Long id : sids) {
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), null, TITLE);
            }
        }
        return sids.length;
    }

    /**
     * 提交
     *
     * @param manDayManufactureProgress
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verify(Long dayManufactureProgressSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<ManDayManufactureProgressItem> items =
                    manDayManufactureProgressItemMapper.selectList(new QueryWrapper<ManDayManufactureProgressItem>().lambda()
                            .eq(ManDayManufactureProgressItem::getDayManufactureProgressSid, dayManufactureProgressSid));
            if (CollectionUtil.isEmpty(items)) {
                throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
            }
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return 1;
    }


}
