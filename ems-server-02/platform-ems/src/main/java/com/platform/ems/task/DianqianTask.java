package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IHrLaborContractService;
import com.platform.ems.service.IManManufactureOrderService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xfzz
 * @date 2024/4/12
 */
@Service
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class DianqianTask {

    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private HrLaborContractMapper hrLaborContractMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;

    @Autowired
    private IHrLaborContractService hrLaborContractService;
    @Autowired
    private SysDefaultSettingClientMapper sysDefaultSettingClientMapper;
    @Autowired
    private SysDefaultSettingSystemMapper sysDefaultSettingSystemMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private ManManufactureOrderConcernTaskMapper manManufactureOrderConcernTaskMapper;

    @Autowired
    private SysClientMapper sysClientMapper;

    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;

    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private IManManufactureOrderService manManufactureOrderService;

    @Scheduled(cron = "00 00 09 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void dianqian() {
        List<SysClient> clientList = sysClientMapper.selectSysClientAll(new SysClient());

        for (SysClient item : clientList) {
            if (item.getUseDianqianNum() == null) {
                item.setUseDianqianNum(0);
            }
            if (item.getLicenseDianqianNum() == null) {
                item.setLicenseDianqianNum(0);
            }
            if (item.getUseDianqianNum() > item.getLicenseDianqianNum()) {
                //给admin发动态
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                sysBusinessBcst.setTitle("租户" + item.getClientId() + "当期使用电签数已超量，请注意！").setNoticeDate(new Date())
                        .setDocumentSid(Long.valueOf(item.getClientId())).setDocumentCode(item.getClientCode());
                //通知人
                sysBusinessBcst.setUserId(1L);
                sysBusinessBcstMapper.insert(sysBusinessBcst);
                //该租户对应的电签超量提醒人员发送业务动态
                SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                        .selectSysDefaultSettingClientById(item.getClientId());
                String[] dianqianExceedNoticeAccountList = new String[]{};
                //电签超量提醒人员
                if (StrUtil.isNotBlank(sysDefaultSettingClient.getDianqianExceedNoticeAccount())) {
                    dianqianExceedNoticeAccountList = sysDefaultSettingClient.getDianqianExceedNoticeAccount().split(";");
                    sysDefaultSettingClient.setDianqianExceedNoticeAccountList(dianqianExceedNoticeAccountList);
                }
                for (String dianqianUserName : dianqianExceedNoticeAccountList) {
                    List<SysUser> dianqianUserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(dianqianUserName));

                    for (SysUser user :dianqianUserList) {
                        //通知人
                        sysBusinessBcst.setUserId(user.getUserId());
                        sysBusinessBcst.setBusinessBcstSid(IdWorker.getId());
                        sysBusinessBcstMapper.insert(sysBusinessBcst);
                    }

                }


            }
        }
    }

    @Scheduled(cron = "00 30 06 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void autoUpdateProcessStatusAndConcernTaskStatus() {
        List<SysClient> clientList = sysClientMapper.selectList(new QueryWrapper<SysClient>());
        for (SysClient item : clientList) {
            List<SysDefaultSettingClient> sysDefaultSettingClientList = sysDefaultSettingClientMapper
                    .selectList(new QueryWrapper<SysDefaultSettingClient>().lambda()
                            .eq(SysDefaultSettingClient::getClientId, item.getClientId()));
            List<ManManufactureOrder> list = manManufactureOrderService.selectManManufactureOrderList(
                    new ManManufactureOrder().setCompleteStatus("YWG"));
            List<Long> ManufactureOrderSidList = new ArrayList<Long>();
            for (ManManufactureOrder manManufactureOrder : list) {
                ManufactureOrderSidList.add(manManufactureOrder.getManufactureOrderSid());
            }
            for (SysDefaultSettingClient sysDefaultSettingClient : sysDefaultSettingClientList) {
                Long previousManufactureOrderSid = 0L;
                if (sysDefaultSettingClient.getIsAutoUpdateProcessStatus().equals("Y")) {
                    //设置工序的完成状态
                    List<ManManufactureOrderProcess> manManufactureOrderProcessList = manManufactureOrderProcessMapper
                            .selectList(new QueryWrapper<ManManufactureOrderProcess>().lambda()
                                    .in(ManManufactureOrderProcess::getManufactureOrderSid, ManufactureOrderSidList));
                    for (ManManufactureOrderProcess manManufactureOrderProcess : manManufactureOrderProcessList) {
                        if (manManufactureOrderProcess.getEndStatus() != null && manManufactureOrderProcess.getEndStatus().equals("YWC")) {
                            continue;
                        } else {
                            manManufactureOrderProcessMapper.update(null, new UpdateWrapper<ManManufactureOrderProcess>().lambda()
                                    .eq(ManManufactureOrderProcess::getManufactureOrderProcessSid,
                                            manManufactureOrderProcess.getManufactureOrderProcessSid())
                                    .set(ManManufactureOrderProcess::getEndStatus, "YWC"));
                        }
                        if (!previousManufactureOrderSid.equals(manManufactureOrderProcess.getManufactureOrderSid())) {
                            MongodbUtil.insertUserLog(manManufactureOrderProcess.getManufactureOrderSid(), BusinessType.CHANGE.getValue(), null, "生产订单", "定时任务，自动更新工序的完成状态为已完成");
                        }
                        previousManufactureOrderSid = manManufactureOrderProcess.getManufactureOrderSid();
                    }
                }

                if (sysDefaultSettingClient.getIsAutoUpdateConcernTaskStatus().equals("Y")) {
                    //设置事项的完成状态
                    List<ManManufactureOrderConcernTask> manManufactureOrderConcernTaskList = manManufactureOrderConcernTaskMapper
                            .selectList(new QueryWrapper<ManManufactureOrderConcernTask>().lambda()
                                    .in(ManManufactureOrderConcernTask::getManufactureOrderSid, ManufactureOrderSidList));
                    for (ManManufactureOrderConcernTask manManufactureOrderConcernTask : manManufactureOrderConcernTaskList) {
                        if (manManufactureOrderConcernTask.getEndStatus() != null && manManufactureOrderConcernTask.getEndStatus().equals("YWC")) {
                            continue;
                        } else {
                            manManufactureOrderConcernTaskMapper.update(null, new UpdateWrapper<ManManufactureOrderConcernTask>()
                                    .lambda().eq(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid,
                                            manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid())
                                    .set(ManManufactureOrderConcernTask::getEndStatus, "YWC"));
                        }
                        if (!previousManufactureOrderSid.equals(manManufactureOrderConcernTask.getManufactureOrderSid())) {
                            MongodbUtil.insertUserLog(manManufactureOrderConcernTask.getManufactureOrderSid(), BusinessType.CHANGE.getValue(), null, "生产订单", "定时任务，自动更新事项的完成状态为已完成");
                        }
                        previousManufactureOrderSid = manManufactureOrderConcernTask.getManufactureOrderSid();
                    }
                }
            }
        }
    }

    /**
     * 劳动合同定时任务 2个
     * */
    @Scheduled(cron = "00 30 09 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void laodong() {
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_HR_LABOR_CONTRACT};
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableNameList(tableNames));
        LocalDate nowLocalDate = LocalDate.now();
        // 将 LocalDate 转换为 java.util.Date
        Date nowDate = Date.from(nowLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<HrLaborContract> hrLaborContractList = hrLaborContractMapper.selectAll(new HrLaborContract()
                .setLvyueStatus("LYZ").setHandleStatus("5"));
        for (HrLaborContract item : hrLaborContractList) {
            int toexpuredatsKdgt;
                SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                        .selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                                .eq(SysDefaultSettingClient::getClientId, item.getClientId()));
                if(sysDefaultSettingClient != null && sysDefaultSettingClient.getToexpireDaysLdht() == null){
                    SysDefaultSettingSystem sysDefaultSettingSystem = sysDefaultSettingSystemMapper
                            .selectOne(new QueryWrapper<SysDefaultSettingSystem>().lambda()
                                    .eq(SysDefaultSettingSystem::getClientId, "10000"));
                    if(sysDefaultSettingSystem.getToexpireDaysLdht() == null){
                        toexpuredatsKdgt = 30;
                    }else {
                        toexpuredatsKdgt = sysDefaultSettingSystem.getToexpireDaysLdht().intValue();
                    }
                }else {
                    toexpuredatsKdgt = sysDefaultSettingClient.getToexpireDaysLdht().intValue();
                }
            if (item.getEndDate().compareTo(nowDate) >= 0 &&
                    calculateRemainingDays(LocalDate.now(), item.getEndDate()) <= toexpuredatsKdgt){
                //通知人,该租户对应的人事专员发送业务动态
                SysDefaultSettingClient sysDefaultSettingTZR = sysDefaultSettingClientMapper
                        .selectSysDefaultSettingClientById(item.getClientId());
                String[] personnelMgtNoticeAccountList = new String[]{};
                //人事专员
                if (StrUtil.isNotBlank(sysDefaultSettingTZR.getPersonnelMgtNoticeAccount())) {
                    personnelMgtNoticeAccountList = sysDefaultSettingTZR.getPersonnelMgtNoticeAccount().split(";");
                    sysDefaultSettingTZR.setPersonnelMgtNoticeAccountList(personnelMgtNoticeAccountList);
                }
                for (String UserName : personnelMgtNoticeAccountList) {
                    List<SysUser> UserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(UserName));
                    for (SysUser user : UserList) {
                        //通知人
                        //给创建人发动态
                        SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                        sysToexpireBusiness.setTitle(item.getStaffName() + "的劳动合同" + item.getLaborContractNum() + "即将到期，合同有效期至"
                                        + formatToDateStr(item.getEndDate()))
                                .setNoticeDate(new Date()).setDocumentSid(Long.valueOf(item.getLaborContractSid()))
                                .setDocumentCode(item.getLaborContractNum().toString()).setTableName(ConstantsTable.TABLE_HR_LABOR_CONTRACT)
                                .setExpiredDate(item.getEndDate());
                        sysToexpireBusiness.setUserId(user.getUserId());
                        sysToexpireBusinessMapper.insert(sysToexpireBusiness);
                    }
                }
            }
        }
    }

    /**
     * 劳动合同定时任务 2个
     * */
    @Scheduled(cron = "00 00 10 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void laodongTwo() {
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_HR_LABOR_CONTRACT};
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableNameList(tableNames));
        List<HrLaborContract> hrLaborContractList = hrLaborContractMapper.selectAll(new HrLaborContract()
                .setLvyueStatus("LYZ").setHandleStatus("5"));
        LocalDate nowLocalDate = LocalDate.now();
        Date nowDate = Date.from(nowLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        for (HrLaborContract item : hrLaborContractList) {
            if (item.getEndDate().compareTo(nowDate) < 0){
                ///通知人,该租户对应的人事专员发送业务动态
                SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                        .selectSysDefaultSettingClientById(item.getClientId());
                String[] personnelMgtNoticeAccountList = new String[]{};
                //人事专员
                if (StrUtil.isNotBlank(sysDefaultSettingClient.getPersonnelMgtNoticeAccount())) {
                    personnelMgtNoticeAccountList = sysDefaultSettingClient.getPersonnelMgtNoticeAccount().split(";");
                    sysDefaultSettingClient.setPersonnelMgtNoticeAccountList(personnelMgtNoticeAccountList);
                }
                for (String UserName : personnelMgtNoticeAccountList) {
                    List<SysUser> UserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(UserName));
                    for (SysUser user : UserList) {
                        //给创建人发动态
                        SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                        sysOverdueBusiness.setTitle(item.getStaffName() + "的劳动合同" + item.getLaborContractNum() + "已逾期，合同有效期至" +
                                        formatToDateStr(item.getEndDate()))
                                .setNoticeDate(new Date()).setDocumentSid(Long.valueOf(item.getLaborContractSid()))
                                .setDocumentCode(item.getLaborContractNum().toString()).setTableName(ConstantsTable.TABLE_HR_LABOR_CONTRACT)
                                .setExpiredDate(item.getEndDate());
                        ;
                        //通知人
                        sysOverdueBusiness.setUserId(user.getUserId());
                        sysOverdueBusinessMapper.insert(sysOverdueBusiness);
                    }
                }
            }
        }
    }

    public static int calculateRemainingDays(LocalDate currentDate, Date endDate) {
        LocalDate endDateDate = endDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        // 计算两个 LocalDate 之间的天数差
        int row;
        row = (int) ChronoUnit.DAYS.between(currentDate, endDateDate);
        if(row<0){
            row = 0;
        }
        return row;
    }

    public static Date getDateUpX(int x) {
        // 获取当前日期
        LocalDate now = LocalDate.now();
        // 计算过期日期
        LocalDate expiredLocalDate = now.plus(Period.ofDays(x));
        return Date.from(expiredLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 Date 对象格式化为 "yyyy-MM-dd" 格式的字符串。
     *
     * @param date 要格式化的 Date 对象
     * @return 格式化后的日期字符串，如果输入为 null，则返回 null
     */
    public static String formatToDateStr(Date date) {
        if (date == null) {
            return null;
        }
        // 将 Date 转换为 LocalDate
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 定义日期格式器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期
        return localDate.format(formatter);
    }
    /**
     * 员工档案定时任务 2个
     * */
    @Scheduled(cron = "00 00 10 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void yuanGong() {
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_BAS_STAFF};
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableNameList(tableNames));
        LocalDate nowLocalDate = LocalDate.now();
        // 将 LocalDate 转换为 java.util.Date
        Date nowDate = Date.from(nowLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<BasStaff> BasStaffList = basStaffMapper.selectAll(new BasStaff()
                .setStaffStatus("SY").setHandleStatus("5").setIsOnJob("ZZ")
                .setStatus(ConstantsEms.ENABLE_STATUS));
        for (BasStaff item : BasStaffList) {
            if (item.getProbationPeriodToDate() != null){
            int toexpuredatsKdgt;
            SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                        .selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                                .eq(SysDefaultSettingClient::getClientId, item.getClientId()));
                if(sysDefaultSettingClient == null || sysDefaultSettingClient.getToexpireDaysSyq() == null){
                    SysDefaultSettingSystem sysDefaultSettingSystem = sysDefaultSettingSystemMapper
                            .selectOne(new QueryWrapper<SysDefaultSettingSystem>().lambda()
                                    .eq(SysDefaultSettingSystem::getClientId, "10000"));
                    if(sysDefaultSettingSystem.getToexpireDaysSyq() == null){
                        toexpuredatsKdgt = 7;
                    }else {
                        toexpuredatsKdgt = sysDefaultSettingSystem.getToexpireDaysSyq().intValue();
                    }
                }else {
                    toexpuredatsKdgt = sysDefaultSettingClient.getToexpireDaysSyq().intValue();
                }

            if (item.getProbationPeriodToDate().compareTo(nowDate) >= 0 &&
                    calculateRemainingDays(LocalDate.now(), item.getProbationPeriodToDate()) <= toexpuredatsKdgt){
                //通知人,该租户对应的人事专员发送业务动态
                SysDefaultSettingClient sysDefaultSettingTZR = sysDefaultSettingClientMapper
                        .selectSysDefaultSettingClientById(item.getClientId());
                String[] personnelMgtNoticeAccountList = new String[]{};
                //人事专员
                if (StrUtil.isNotBlank(sysDefaultSettingTZR.getPersonnelMgtNoticeAccount())) {
                    personnelMgtNoticeAccountList = sysDefaultSettingTZR.getPersonnelMgtNoticeAccount().split(";");
                    sysDefaultSettingTZR.setPersonnelMgtNoticeAccountList(personnelMgtNoticeAccountList);
                }
                for (String UserName : personnelMgtNoticeAccountList) {
                    List<SysUser> UserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(UserName));
                    for (SysUser user :UserList) {
                        //通知人
                        //给创建人发动态
                        SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                        sysToexpireBusiness.setTitle("员工"+item.getStaffName()+"的试用期即将到期，试用期至"+
                                formatToDateStr(item.getProbationPeriodToDate()))
                                .setNoticeDate(new Date()).setDocumentSid(Long.valueOf(item.getStaffSid()))
                                .setDocumentCode(item.getStaffCode()).setTableName(ConstantsTable.TABLE_BAS_STAFF)
                                .setExpiredDate(item.getProbationPeriodToDate());
                        sysToexpireBusiness.setUserId(user.getUserId());
                        sysToexpireBusinessMapper.insert(sysToexpireBusiness);
                    }
                }

            }
            }
        }

    }

    /**
     * 员工档案定时任务 2个
     * */
    @Scheduled(cron = "00 00 11 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void yuanGongTwo() {
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_HR_LABOR_CONTRACT};
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableNameList(tableNames));
        LocalDate nowLocalDate = LocalDate.now();
        Date nowDate = Date.from(nowLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<BasStaff> BasStaffList = basStaffMapper.selectAll(new BasStaff()
                .setStaffStatus("ZS").setHandleStatus("5").setIsOnJob("ZZ")
                .setStatus(ConstantsEms.ENABLE_STATUS));
        for (BasStaff item : BasStaffList) {
            if (item.getEmployerLiabilityInsuranceDate() != null){
                int toexpuredatsKdgt;
                    SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                            .selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                                    .eq(SysDefaultSettingClient::getClientId, item.getClientId()));
                    if (sysDefaultSettingClient == null || sysDefaultSettingClient.getToexpireDaysGzzrx() == null) {
                        SysDefaultSettingSystem sysDefaultSettingSystem = sysDefaultSettingSystemMapper
                                .selectOne(new QueryWrapper<SysDefaultSettingSystem>().lambda()
                                        .eq(SysDefaultSettingSystem::getClientId, "10000"));
                        if (sysDefaultSettingSystem.getToexpireDaysGzzrx() == null) {
                            toexpuredatsKdgt = 30;
                        } else {
                            toexpuredatsKdgt = sysDefaultSettingSystem.getToexpireDaysGzzrx().intValue();
                        }
                    } else {
                        toexpuredatsKdgt = sysDefaultSettingClient.getToexpireDaysGzzrx().intValue();
                    }
                if (item.getEmployerLiabilityInsuranceDate().compareTo(nowDate) >= 0 &&
                        calculateRemainingDays(LocalDate.now(), item.getEmployerLiabilityInsuranceDate()) <= toexpuredatsKdgt) {
                    //通知人,该租户对应的人事专员发送业务动态
                    SysDefaultSettingClient sysDefaultSettingTZR = sysDefaultSettingClientMapper
                            .selectSysDefaultSettingClientById(item.getClientId());
                    String[] personnelMgtNoticeAccountList = new String[]{};
                    //人事专员
                    if (StrUtil.isNotBlank(sysDefaultSettingTZR.getPersonnelMgtNoticeAccount())) {
                        personnelMgtNoticeAccountList = sysDefaultSettingTZR.getPersonnelMgtNoticeAccount().split(";");
                        sysDefaultSettingTZR.setPersonnelMgtNoticeAccountList(personnelMgtNoticeAccountList);
                    }
                    for (String UserName : personnelMgtNoticeAccountList) {
                        List<SysUser> UserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(UserName));
                        for (SysUser user :UserList) {
                            //给创建人发动态
                            SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                            sysToexpireBusiness.setTitle("员工"+item.getStaffName()+"的雇主责任险即将到期，到期日"+
                                    formatToDateStr(item.getEmployerLiabilityInsuranceDate()))
                                    .setNoticeDate(new Date()).setDocumentSid(Long.valueOf(item.getStaffSid()))
                                    .setDocumentCode(item.getStaffCode()).setTableName(ConstantsTable.TABLE_BAS_STAFF)
                                    .setExpiredDate(item.getEmployerLiabilityInsuranceDate());
                            sysToexpireBusiness.setUserId(user.getUserId());
                            sysToexpireBusinessMapper.insert(sysToexpireBusiness);
                        }
                    }
                }
            }
        }
    }

    /**
     * 员工档案定时任务：已逾期 2个
     * */
    @Scheduled(cron = "00 00 10 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void yuanGongYYQ() {
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_BAS_STAFF};
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableNameList(tableNames));
        LocalDate nowLocalDate = LocalDate.now();
        Date nowDate = Date.from(nowLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<SysOverdueBusiness> sysOverdueBusinessList = new ArrayList<SysOverdueBusiness>();
        List<BasStaff> BasStaffList = basStaffMapper.selectAll(new BasStaff()
                .setStaffStatus("SY").setHandleStatus("5").setIsOnJob("ZZ")
                .setStatus(ConstantsEms.ENABLE_STATUS));
        for (BasStaff item : BasStaffList) {
            if (item.getProbationPeriodToDate() != null && item.getProbationPeriodToDate().compareTo(nowDate) < 0){
                ///通知人,该租户对应的人事专员发送业务动态
                SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                        .selectSysDefaultSettingClientById(item.getClientId());
                String[] personnelMgtNoticeAccountList = new String[]{};
                //人事专员
                if (StrUtil.isNotBlank(sysDefaultSettingClient.getPersonnelMgtNoticeAccount())) {
                    personnelMgtNoticeAccountList = sysDefaultSettingClient.getPersonnelMgtNoticeAccount().split(";");
                    sysDefaultSettingClient.setPersonnelMgtNoticeAccountList(personnelMgtNoticeAccountList);
                }
                for (String UserName : personnelMgtNoticeAccountList) {
                    List<SysUser> UserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(UserName));
                    for (SysUser user : UserList) {
                        //给创建人发动态
                        SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                        sysOverdueBusiness.setTitle("员工"+item.getStaffName()+"的试用期已逾期，试用期至"+
                                        formatToDateStr(item.getProbationPeriodToDate()))
                                .setNoticeDate(new Date()).setDocumentSid(Long.valueOf(item.getStaffSid()))
                                .setDocumentCode(item.getStaffCode().toString()).setTableName(ConstantsTable.TABLE_BAS_STAFF)
                                .setExpiredDate(item.getProbationPeriodToDate());
                        //通知人
                        sysOverdueBusiness.setUserId(user.getUserId());
                        sysOverdueBusinessList.add(sysOverdueBusiness);
                    }
                }
            }
        }
        sysOverdueBusinessMapper.insertAll(sysOverdueBusinessList);
    }

    /**
     * 员工档案定时任务：已逾期 2个
     * */
    @Scheduled(cron = "00 00 11 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void yuanGongYYQTwo() {
        List<SysOverdueBusiness> sysOverdueBusinessList = new ArrayList<SysOverdueBusiness>();
        LocalDate nowLocalDate = LocalDate.now();
        Date nowDate = Date.from(nowLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<BasStaff> BasStaffList = basStaffMapper.selectAll(new BasStaff()
                .setStaffStatus("ZS").setHandleStatus("5").setIsOnJob("ZZ")
                .setStatus(ConstantsEms.ENABLE_STATUS));
        for (BasStaff item : BasStaffList) {
            if (item.getEmployerLiabilityInsuranceDate() != null && item.getEmployerLiabilityInsuranceDate().compareTo(nowDate) < 0){
                ///通知人,该租户对应的人事专员发送业务动态
                SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper
                        .selectSysDefaultSettingClientById(item.getClientId());
                String[] personnelMgtNoticeAccountList = new String[]{};
                //人事专员
                if (StrUtil.isNotBlank(sysDefaultSettingClient.getPersonnelMgtNoticeAccount())) {
                    personnelMgtNoticeAccountList = sysDefaultSettingClient.getPersonnelMgtNoticeAccount().split(";");
                    sysDefaultSettingClient.setPersonnelMgtNoticeAccountList(personnelMgtNoticeAccountList);
                }
                for (String UserName : personnelMgtNoticeAccountList) {
                    List<SysUser> UserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(UserName));
                    for (SysUser user : UserList) {
                        //给创建人发动态
                        SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                        sysOverdueBusiness.setTitle("员工"+item.getStaffName()+"的雇主责任险已逾期，到期日"+
                                        formatToDateStr(item.getEmployerLiabilityInsuranceDate()))
                                .setNoticeDate(new Date()).setDocumentSid(Long.valueOf(item.getStaffSid()))
                                .setDocumentCode(item.getStaffCode()).setTableName(ConstantsTable.TABLE_BAS_STAFF)
                                .setExpiredDate(item.getEmployerLiabilityInsuranceDate());
                        //通知人
                        sysOverdueBusiness.setUserId(user.getUserId());
                        sysOverdueBusinessList.add(sysOverdueBusiness);
                    }
                }
            }
        }
        sysOverdueBusinessMapper.insertAll(sysOverdueBusinessList);
    }

}
