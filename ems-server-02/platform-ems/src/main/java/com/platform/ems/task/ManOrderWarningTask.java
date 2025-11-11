package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.SysOverdueBusinessMapper;
import com.platform.system.mapper.SysToexpireBusinessMapper;
import com.platform.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生产订单： 即将到期/已逾期 数据库表更新
 *
 * @author chenkw
 */
@Service
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class ManOrderWarningTask {

    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private ManManufactureOrderProductMapper manManufactureOrderProductMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private ManManufactureOrderConcernTaskMapper manManufactureOrderConcernTaskMapper;
    @Autowired
    private RepBusinessRemindMoMapper repBusinessRemindMoMapper;
    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Autowired
    private ManProduceWeekProgressTotalMapper manProduceWeekProgressTotalMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Scheduled(cron = "00 00 01 * * *")
    public void earlyWarningOrder() {
        log.info("=====>开始查询即将逾期生产订单");
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER));
        List<ManManufactureOrder> toexpireProcessList = manManufactureOrderMapper.selectToexpireList(new ManManufactureOrder().setHandleStatus(ConstantsEms.CHECK_STATUS));
        if (CollectionUtil.isNotEmpty(toexpireProcessList)) {
            // 通知给跟进人，跟进人不为空
            toexpireProcessList = toexpireProcessList.stream().filter(o->o.getGenjinrenSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(toexpireProcessList)) {
                List<SysToexpireBusiness> businesseToList = new ArrayList<>();
                toexpireProcessList.forEach(order -> {
                    // 找到跟进人的用户id
                    List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(order.getGenjinrenSid()));
                    if (CollectionUtil.isNotEmpty(userList)) {
                        for (SysUser user : userList) {
                            SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                            String planEndDate = DateUtil.format(order.getPlanEndDate(), "yyyy-MM-dd");
                            sysToexpireBusiness.setClientId(order.getClientId())
                                    .setTitle("款号"+ order.getMaterialCode() + "的生产订单" + order.getManufactureOrderCode() + "即将到期，计划完工日期" + planEndDate)
                                    .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER)
                                    .setDocumentSid(order.getManufactureOrderSid())
                                    .setDocumentCode(order.getManufactureOrderCode())
                                    .setExpiredDate(order.getPlanEndDate())
                                    .setNoticeDate(new Date())
                                    .setUserId(user.getUserId());
                            businesseToList.add(sysToexpireBusiness);
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(businesseToList)) {
                    sysToexpireBusinessMapper.insertAll(businesseToList);
                }
            }
        }
        log.info("=====>开始查询已逾期生产订单");
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER));
        List<ManManufactureOrder> overdueProcessList = manManufactureOrderMapper.selectOverdueList(new ManManufactureOrder().setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setCompleteStatus(ConstantsEms.COMPLETE_STATUS_YWG));
        if (CollectionUtil.isNotEmpty(overdueProcessList)) {
            // 通知给跟进人，跟进人不为空
            overdueProcessList = overdueProcessList.stream().filter(o -> o.getGenjinrenSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(overdueProcessList)) {
                List<SysOverdueBusiness> businesseOverList = new ArrayList<>();
                overdueProcessList.forEach(order -> {
                    // 找到跟进人的用户id
                    List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(order.getGenjinrenSid()));
                    if (CollectionUtil.isNotEmpty(userList)) {
                        for (SysUser user : userList) {
                            SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                            String planEndDate = DateUtil.format(order.getPlanEndDate(), "yyyy-MM-dd");
                            sysOverdueBusiness.setClientId(order.getClientId())
                                    .setTitle("款号"+ order.getMaterialCode() + "的生产订单" + order.getManufactureOrderCode()+ "已逾期，计划完工日期" + planEndDate)
                                    .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER)
                                    .setDocumentSid(order.getManufactureOrderSid())
                                    .setDocumentCode(order.getManufactureOrderCode())
                                    .setExpiredDate(order.getPlanEndDate())
                                    .setNoticeDate(new Date())
                                    .setUserId(user.getUserId());
                            businesseOverList.add(sysOverdueBusiness);
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(businesseOverList)) {
                    sysOverdueBusinessMapper.insertAll(businesseOverList);
                }
            }
        }
        log.info("==>处理结束");
    }

    @Scheduled(cron = "00 05 01 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void earlyWarningProduct() {
        repBusinessRemindMoMapper.delete(new RepBusinessRemindMo());
        RepBusinessRemindMo repBusinessRemindMo = new RepBusinessRemindMo();
        repBusinessRemindMo.setHandleStatus(ConstantsEms.CHECK_STATUS);
        log.info("=====>开始处理已逾期-生产订单商品对象 s_rep_business_remind_mo");
        repBusinessRemindMo.setRemindType(ConstantsEms.YYQ);
        List<RepBusinessRemindMo> yyqList = repBusinessRemindMoMapper.getFormData(repBusinessRemindMo);
        log.info("=====>开始处理即将到期-生产订单商品对象 s_rep_business_remind_mo");
        repBusinessRemindMo.setRemindType(ConstantsEms.JJDQ);
        List<RepBusinessRemindMo> jjdqList = repBusinessRemindMoMapper.getFormData(repBusinessRemindMo);
        // 汇总
        List<RepBusinessRemindMo> zong = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(yyqList)){
            zong.addAll(yyqList);
        }
        if (CollectionUtil.isNotEmpty(jjdqList)){
            zong.addAll(jjdqList);
        }
        if (CollectionUtil.isNotEmpty(zong)){
            repBusinessRemindMoMapper.insertAll(zong);
        }
    }

    @Scheduled(cron = "00 10 01 * * *")
    public void earlyWarningProcess() {
        log.info("=====>开始查询即将逾期生产订单工序");
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_PROCESS));
        List<ManManufactureOrderProcess> toexpireProcessList = manManufactureOrderProcessMapper.selectToexpireList(new ManManufactureOrderProcess()
                .setHandleStatus(ConstantsEms.CHECK_STATUS));
        if (CollectionUtil.isNotEmpty(toexpireProcessList)) {
            // 通知给负责人，负责人不为空
            toexpireProcessList = toexpireProcessList.stream().filter(o -> StrUtil.isNotBlank(o.getDirectorSid())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(toexpireProcessList)) {

                List<SysToexpireBusiness> businesseToList = new ArrayList<>();
                toexpireProcessList.forEach(orderProcess -> {

                    // 负责人 可能有多个
                    String[] directorSids = orderProcess.getDirectorSid().split(";");
                    for (String directorSid : directorSids) {
                        // 找到负责人的用户id
                        List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(Long.parseLong(directorSid)));
                        if (CollectionUtil.isNotEmpty(userList)) {
                            for (SysUser user : userList) {

                                SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                                String planEndDate = DateUtil.format(orderProcess.getPlanEndDate(), "yyyy-MM-dd");
                                sysToexpireBusiness.setClientId(orderProcess.getClientId())
                                        .setTitle("生产订单" + orderProcess.getManufactureOrderCode() + "的工序" + orderProcess.getProcessName() + "即将到期，计划完成日期" + planEndDate)
                                        .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_PROCESS)
                                        .setDocumentSid(orderProcess.getManufactureOrderProcessSid())
                                        .setDocumentCode(orderProcess.getManufactureOrderCode())
                                        .setExpiredDate(orderProcess.getPlanEndDate())
                                        .setNoticeDate(new Date())
                                        .setUserId(user.getUserId());
                                businesseToList.add(sysToexpireBusiness);
                            }
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(businesseToList)) {
                    sysToexpireBusinessMapper.insertAll(businesseToList);
                }
            }
        }
        log.info("=====>开始查询已逾期生产订单工序");
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_PROCESS));
        List<ManManufactureOrderProcess> overdueProcessList = manManufactureOrderProcessMapper.selectOverdueList(new ManManufactureOrderProcess()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setEndStatus(ConstantsEms.END_STATUS_YWC));
        if (CollectionUtil.isNotEmpty(overdueProcessList)) {
            // 通知给负责人，负责人不为空
            overdueProcessList = overdueProcessList.stream().filter(o -> StrUtil.isNotBlank(o.getDirectorSid())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(overdueProcessList)) {

                List<SysOverdueBusiness> businesseOverList = new ArrayList<>();
                overdueProcessList.forEach(orderProcess -> {

                    // 负责人 可能有多个
                    String[] directorSids = orderProcess.getDirectorSid().split(";");
                    for (String directorSid : directorSids) {
                        // 找到负责人的用户id
                        List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(Long.parseLong(directorSid)));
                        if (CollectionUtil.isNotEmpty(userList)) {
                            for (SysUser user : userList) {

                                SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                                String planEndDate = DateUtil.format(orderProcess.getPlanEndDate(), "yyyy-MM-dd");
                                sysOverdueBusiness.setClientId(orderProcess.getClientId())
                                        .setTitle("生产订单" + orderProcess.getManufactureOrderCode() + "的工序" + orderProcess.getProcessName() + "已逾期，计划完成日期" + planEndDate)
                                        .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_PROCESS)
                                        .setDocumentSid(orderProcess.getManufactureOrderSid())
                                        .setDocumentCode(orderProcess.getManufactureOrderCode())
                                        .setExpiredDate(orderProcess.getPlanEndDate())
                                        .setNoticeDate(new Date())
                                        .setUserId(user.getUserId());
                                businesseOverList.add(sysOverdueBusiness);
                            }
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(businesseOverList)) {
                    sysOverdueBusinessMapper.insertAll(businesseOverList);
                }
            }
        }
        log.info("==>处理结束");
    }

    @Scheduled(cron = "00 15 01 * * *")
    public void earlyWarningConcernTask() {
        log.info("=====>开始查询即将逾期生产订单事项");
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_CONCERN_TASK));
        List<ManManufactureOrderConcernTask> toexpireList = manManufactureOrderConcernTaskMapper.selectToexpireList(new ManManufactureOrderConcernTask()
                .setHandleStatus(ConstantsEms.CHECK_STATUS));
        if (CollectionUtil.isNotEmpty(toexpireList)) {
            // 通知给负责人，负责人不为空
            toexpireList = toexpireList.stream().filter(o -> o.getHandlerSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(toexpireList)) {

                List<SysToexpireBusiness> businesseToList = new ArrayList<>();
                toexpireList.forEach(item -> {

                    // 找到负责的用户id
                    List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(item.getHandlerSid()));
                    if (CollectionUtil.isNotEmpty(userList)) {
                        for (SysUser user : userList) {

                            SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                            String planEndDate = DateUtil.format(item.getPlanEndDate(), "yyyy-MM-dd");
                            sysToexpireBusiness.setClientId(item.getClientId())
                                    .setTitle("生产订单" + item.getManufactureOrderCode() + "的事项" + item.getConcernTaskName() + "即将到期，计划完成日期" + planEndDate)
                                    .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_CONCERN_TASK)
                                    .setDocumentSid(item.getManufactureOrderConcernTaskSid())
                                    .setDocumentCode(item.getManufactureOrderCode())
                                    .setExpiredDate(item.getPlanEndDate())
                                    .setNoticeDate(new Date())
                                    .setUserId(user.getUserId());
                            businesseToList.add(sysToexpireBusiness);
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(businesseToList)) {
                    sysToexpireBusinessMapper.insertAll(businesseToList);
                }
            }
        }
        log.info("=====>开始查询已逾期生产订单事项");
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_CONCERN_TASK));
        List<ManManufactureOrderConcernTask> overdueList = manManufactureOrderConcernTaskMapper.selectOverdueList(new ManManufactureOrderConcernTask()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setEndStatus(ConstantsEms.END_STATUS_YWC));
        if (CollectionUtil.isNotEmpty(overdueList)) {
            // 通知给负责人，负责人不为空
            overdueList = overdueList.stream().filter(o -> o.getHandlerSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(overdueList)) {

                List<SysOverdueBusiness> businesseOverList = new ArrayList<>();
                overdueList.forEach(item -> {

                    // 找到负责人的用户id
                    List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(item.getHandlerSid()));
                    if (CollectionUtil.isNotEmpty(userList)) {
                        for (SysUser user : userList) {

                            SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                            String planEndDate = DateUtil.format(item.getPlanEndDate(), "yyyy-MM-dd");
                            sysOverdueBusiness.setClientId(item.getClientId())
                                    .setTitle("生产订单" + item.getManufactureOrderCode() + "的事项" + item.getConcernTaskName() + "已逾期，计划完成日期" + planEndDate)
                                    .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER_CONCERN_TASK)
                                    .setDocumentSid(item.getManufactureOrderSid())
                                    .setDocumentCode(item.getManufactureOrderCode())
                                    .setExpiredDate(item.getPlanEndDate())
                                    .setNoticeDate(new Date())
                                    .setUserId(user.getUserId());
                            businesseOverList.add(sysOverdueBusiness);
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(overdueList)) {
                    sysOverdueBusinessMapper.insertAll(businesseOverList);
                }
            }
        }
        log.info("==>处理结束");
    }

    @Scheduled(cron = "00 20 01 * * *")
    public void earlyWarningContractTask() {
        log.info("=====>生产订单的计划完工日期晚于销售合同交期预警");
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableName("s_man_manufacture_order_contract"));
        List<ManManufactureOrder> overdueList = manManufactureOrderProductMapper.selectOverdueContractList();
        if (CollectionUtil.isNotEmpty(overdueList)) {
            // 通知给跟进人，跟进人不为空
            overdueList = overdueList.stream().filter(o -> o.getGenjinrenSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(overdueList)) {
                List<SysOverdueBusiness> businesseOverList = new ArrayList<>();
                overdueList.forEach(item -> {
                    List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser().setStaffSid(item.getGenjinrenSid()));
                    if (CollectionUtil.isNotEmpty(userList)) {
                        for (SysUser user : userList) {
                            SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                            String planEndDate = DateUtil.format(item.getPlanEndDate(), "yyyy-MM-dd");
                            sysOverdueBusiness.setClientId(item.getClientId())
                                    .setTitle("款号" + item.getMaterialCode() + "的生产订单" + item.getManufactureOrderCode() +
                                            "，计划完工日期" + planEndDate + "晚于合同交期" + item.getContractDate())
                                    .setTableName("s_man_manufacture_order_contract")
                                    .setDocumentSid(item.getManufactureOrderSid())
                                    .setDocumentCode(item.getManufactureOrderCode())
                                    .setExpiredDate(item.getPlanEndDate())
                                    .setNoticeDate(new Date())
                                    .setUserId(user.getUserId());
                            businesseOverList.add(sysOverdueBusiness);
                        }
                    }
                });
                if (CollectionUtil.isNotEmpty(businesseOverList)) {
                    sysOverdueBusinessMapper.insertAll(businesseOverList);
                }
            }
        }
        log.info("==>处理结束");
    }

    @Scheduled(cron = "00 25 01 * * 1")
    public void produceWeekProgressTotal() {
        log.info("=====>开始对上一周的生产进度进行汇总");
        // 求当前日期上一周的周一、周日
        LocalDate now = LocalDate.now();
        LocalDate todayOfLastWeek = now.minusDays(7);
        LocalDate monday = todayOfLastWeek.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1);
        LocalDate sunday = todayOfLastWeek.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
        ZoneId zone = ZoneId.systemDefault();
        Date begin = Date.from(monday.atStartOfDay().atZone(zone).toInstant());
        Date end = Date.from(sunday.atStartOfDay().atZone(zone).toInstant());
        List<ManProduceWeekProgressTotal> sumList = manProduceWeekProgressTotalMapper.selectManWeekManufacturePlanList(
                new ManProduceWeekProgressTotal().setDateStart(begin).setDateEnd(end));
        if (CollectionUtil.isNotEmpty(sumList)) {
            manProduceWeekProgressTotalMapper.delete(new ManProduceWeekProgressTotal().setDateStart(begin)
                    .setDateEnd(end));
            manProduceWeekProgressTotalMapper.inserts(sumList);
        }
        log.info("==>处理结束");
    }

}
