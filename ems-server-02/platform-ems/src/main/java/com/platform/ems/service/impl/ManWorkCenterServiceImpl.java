package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManWorkCenterActionRequest;
import com.platform.ems.domain.dto.request.ManWorkCenterReportRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.IManWorkCenterService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作中心/班组Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Service
@SuppressWarnings("all")
public class ManWorkCenterServiceImpl extends ServiceImpl<ManWorkCenterMapper, ManWorkCenter> implements IManWorkCenterService {
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private ManWorkCenterProcessMapper manWorkCenterProcessMapper;
    @Autowired
    private ManWorkCenterMemberMapper manWorkCenterMemberMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;

    private static final String TITLE = "工作中心/班组";

    /**
     * 查询工作中心/班组
     *
     * @param workCenterSid 工作中心/班组ID
     * @return 工作中心/班组
     */
    @Override
    public ManWorkCenter selectManWorkCenterById(Long workCenterSid) {
        ManWorkCenter manWorkCenter = manWorkCenterMapper.selectManWorkCenterById(workCenterSid);
        if (manWorkCenter == null) {
            return null;
        }
        // 得到多工序List
        getProcessList(manWorkCenter);
        ManWorkCenterProcess manWorkCenterProcess = new ManWorkCenterProcess();
        manWorkCenterProcess.setWorkCenterSid(workCenterSid);
        List<ManWorkCenterProcess> manWorkCenterProcessList = manWorkCenterProcessMapper.selectManWorkCenterProcessList(manWorkCenterProcess);
        manWorkCenter.setListManWorkCenterProcess(manWorkCenterProcessList);

        ManWorkCenterMember manWorkCenterMember = new ManWorkCenterMember();
        manWorkCenterMember.setWorkCenterSid(workCenterSid);
        List<ManWorkCenterMember> memberList = manWorkCenterMemberMapper.selectManWorkCenterMemberList(manWorkCenterMember);
        manWorkCenter.setWorkCenterMemberList(memberList);
        BasStaff basStaff = new BasStaff();
        basStaff.setIsOnJob(ConstantsEms.IS_ON_JOB_ZZ);
        basStaff.setPlantSid(manWorkCenter.getPlantSid());
        basStaff.setWorkCenterSid(manWorkCenter.getWorkCenterSid());
        List<BasStaff> basStaffs = basStaffMapper.selectBasStaffList(basStaff);
        manWorkCenter.setStaffList(basStaffs);
        MongodbUtil.find(manWorkCenter);
        return manWorkCenter;
    }

    /**
     * 查询工作中心/班组的编码和名称
     *
     * @param workCenterSid 工作中心/班组ID
     * @return 工作中心/班组
     */
    @Override
    public ManWorkCenter selectCodeNameById(Long workCenterSid) {
        return manWorkCenterMapper.selectById(workCenterSid);
    }

    /**
     * 查询工作中心/班组列表
     *
     * @param manWorkCenter 工作中心/班组
     * @return 工作中心/班组
     */
    @Override
    public List<ManWorkCenter> selectManWorkCenterList(ManWorkCenter manWorkCenter) {
        if (manWorkCenter.getProcessSidList() != null && manWorkCenter.getProcessSidList().length > 0){
            manWorkCenter.setProcessSids(setProcessSids(manWorkCenter.getProcessSidList()));
        }
        List<ManWorkCenter> manWorkCenters = manWorkCenterMapper.selectManWorkCenterList(manWorkCenter);
        if (CollectionUtil.isNotEmpty(manWorkCenters)){
            Long[] workCenterSidList = manWorkCenters.stream().map(ManWorkCenter::getWorkCenterSid).toArray(Long[]::new);
            Map<Long, ManWorkCenter> processNameMap = manWorkCenterMapper.selectManWorkCenterProcessNameList(new ManWorkCenter().setWorkCenterSidList(workCenterSidList));
            manWorkCenters.forEach(item->{
                item.setProcessNames(processNameMap.get(item.getWorkCenterSid()).getProcessNames());
            });
        }
        return manWorkCenters;
    }


    @Override
    public List<ManManufactureOrderProcess> selectManWorkCenterReportList(ManWorkCenterReportRequest reportRequest) {
        if (StrUtil.isEmpty(reportRequest.getBeginTime()) || StrUtil.isEmpty(reportRequest.getEndTime())) {
            throw new BaseException("请选择时间");
        }
        List<ManManufactureOrderProcess> reportList = manWorkCenterMapper.selectManWorkCenterReportList(reportRequest);
        List<String> dateList = new ArrayList<>();
        try {
            dateList = findDates(reportRequest.getBeginTime(), reportRequest.getEndTime());
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
        List<String> list = dateList;
        reportList.parallelStream().forEach(report -> {
            Map<String, Integer> map = new LinkedHashMap<>();
            List<ManManufactureOrderProcess> datas = report.getDatas();
            list.forEach(s -> {
                Date date = DateUtil.parse(s);
                Integer count = 0;
                for (int i = 0; i < datas.size(); i++) {
                    ManManufactureOrderProcess process = datas.get(i);
                    if (process.getPlanStartDate().getTime() <= date.getTime() && process.getPlanEndDate().getTime() >= date.getTime()) {
                        count++;
                    }
                }
                map.put(s, count);
            });
            report.setReportMap(map);
        });
        return reportList;
    }


    public static void main(String[] args) {
        try {
            List<String> list = findDates("2021-06-01", "2021-06-15");
            System.out.println(list.toString());
        } catch (Exception e) {

        }

    }

    /**
     * @param [stime, etime]
     * @return java.util.List<java.lang.String>
     * @title 根据开始时间，结束时间获取期间所有日期yyyy-MM-dd
     * 例：开始时间：2019-05-01 结束时间：2019-05-05
     */
    public static List<String> findDates(String stime, String etime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date dBegin = sdf.parse(stime);
        Date dEnd = sdf.parse(etime);

        List<String> allDate = new ArrayList();
        allDate.add(sdf1.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            allDate.add(sdf1.format(calBegin.getTime()));
        }
        return allDate;
    }

    /**
     * 新增工作中心/班组
     * 需要注意编码重复校验
     *
     * @param manWorkCenter 工作中心/班组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManWorkCenter(ManWorkCenter manWorkCenter) {
        List<ManWorkCenter> workCenterCodeList = manWorkCenterMapper.selectList(new QueryWrapper<ManWorkCenter>().lambda()
                .eq(ManWorkCenter::getWorkCenterCode, manWorkCenter.getWorkCenterCode()));
        if (workCenterCodeList.size() > 0) {
            throw new BaseException("工作中心/班组编码已存在！");
        }
        List<ManWorkCenter> workCenterNameList = manWorkCenterMapper.selectList(new QueryWrapper<ManWorkCenter>().lambda()
                .eq(ManWorkCenter::getWorkCenterName, manWorkCenter.getWorkCenterName())
                .eq(ManWorkCenter::getPlantSid, manWorkCenter.getPlantSid()));
        if (workCenterNameList.size() > 0) {
            throw new BaseException("该工厂下已存在相同的工作中心/班组！");
        }
        setProcessList(manWorkCenter,null);
        setConfirmInfo(manWorkCenter);
        setCode(manWorkCenter);
        int row = manWorkCenterMapper.insert(manWorkCenter);
        List<ManWorkCenterProcess> manWorkCenterProcessList = manWorkCenter.getListManWorkCenterProcess();
        if (CollectionUtils.isNotEmpty(manWorkCenterProcessList)) {
            addManWorkCenterProcess(manWorkCenter, manWorkCenterProcessList);
        }
        List<ManWorkCenterMember> memberList = manWorkCenter.getWorkCenterMemberList();
        if (CollectionUtils.isNotEmpty(memberList)) {
            addWorkCenterMember(manWorkCenter, memberList);
        }
        ManWorkCenter workCenter = manWorkCenterMapper.selectManWorkCenterById(manWorkCenter.getWorkCenterSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(manWorkCenter.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_WORK_CENTER)
                    .setDocumentSid(manWorkCenter.getWorkCenterSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("工作中心/班组" + workCenter.getWorkCenterCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(workCenter.getWorkCenterCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(manWorkCenter);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbDeal.insert(manWorkCenter.getWorkCenterSid(), manWorkCenter.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    public void setCode(ManWorkCenter manWorkCenter){
        Long departmentSid = manWorkCenter.getDepartmentSid();
        if(departmentSid!=null){
            ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>().lambda()
                    .eq(ConManufactureDepartment::getSid, departmentSid)
            );
            if(conManufactureDepartment!=null){
                manWorkCenter.setDepartmentCode(conManufactureDepartment.getCode());
            }
        }
    }
    /**
     * 设置多个负责工序
     * payWorkattendRecord: 请求
     * processSidList：原来的
     */
    public void setProcessList(ManWorkCenter manWorkCenter, String processSidList){
        if (manWorkCenter.getProcessSidList() != null && manWorkCenter.getProcessSidList().length > 0){
            // 拼接sid
            String processSids = setProcessSids(manWorkCenter.getProcessSidList());
            // 如果与原来相等就不用变了
            if (processSidList != null && processSids.equals(processSidList)){
                return;
            }
            manWorkCenter.setProcessSids(processSids);
            // 找出code并拼接
            List<ManProcess> processeList = manProcessMapper.selectList(new QueryWrapper<ManProcess>().lambda()
                    .in(ManProcess::getProcessSid,manWorkCenter.getProcessSidList()));
            String processCodes = "";
            for (int i = 0; i < processeList.size(); i++) {
                processCodes = processCodes + processeList.get(i).getProcessCode()+";";
            }
            if (StrUtil.isNotBlank(processCodes)){
                if (processCodes.endsWith(";")) {
                    processCodes = processCodes.substring(0,processCodes.length() - 1);
                }
            }
            manWorkCenter.setProcessCodes(processCodes);
        }else {
            manWorkCenter.setProcessSids(null);
            manWorkCenter.setProcessCodes(null);
        }
    }

    /**
     * 拼接多个负责工序sid
     */
    public String setProcessSids(String[] sids){
        if (sids != null && sids.length > 0){
            return StringUtils.join(sids, ";");
        }
        return null;
    }

    /**
     * 取出多个负责工序
     */
    public void getProcessList(ManWorkCenter manWorkCenter){
        if (StrUtil.isNotBlank(manWorkCenter.getProcessSids())){
            String[] processSidsList = manWorkCenter.getProcessSids().split(";");
            manWorkCenter.setProcessSidList(processSidsList);
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManWorkCenter o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(ManWorkCenter manWorkCenter) {
        List<ManWorkCenter> workCenterNameList = manWorkCenterMapper.selectList(new QueryWrapper<ManWorkCenter>().lambda()
                .eq(ManWorkCenter::getWorkCenterName, manWorkCenter.getWorkCenterName())
                .eq(ManWorkCenter::getPlantSid, manWorkCenter.getPlantSid()));
        if (CollectionUtils.isNotEmpty(workCenterNameList)) {
            workCenterNameList.forEach(o -> {
                if (!manWorkCenter.getWorkCenterSid().equals(o.getWorkCenterSid())) {
                    throw new BaseException("该工厂下已存在相同的工作中心/班组！");
                }
            });
        }
    }

    /**
     * 工作中心/班组-工序对象
     */
    private void addManWorkCenterProcess(ManWorkCenter manWorkCenter, List<ManWorkCenterProcess> processList) {
//        deleteManWorkCenterProcess(manWorkCenter);
        processList.forEach(o -> {
            o.setWorkCenterSid(manWorkCenter.getWorkCenterSid());
        });
        manWorkCenterProcessMapper.inserts(processList);
    }

    private void deleteManWorkCenterProcess(ManWorkCenter manWorkCenter) {
        manWorkCenterProcessMapper.delete(
                new UpdateWrapper<ManWorkCenterProcess>()
                        .lambda()
                        .eq(ManWorkCenterProcess::getWorkCenterSid, manWorkCenter.getWorkCenterSid())
        );
    }

    private void deleteWorkCenterMember(ManWorkCenter manWorkCenter) {
        manWorkCenterMemberMapper.delete(
                new UpdateWrapper<ManWorkCenterMember>()
                        .lambda()
                        .eq(ManWorkCenterMember::getWorkCenterSid, manWorkCenter.getWorkCenterSid())
        );
    }

    /**
     * 工作中心/班组-成员对象
     */
    private void addWorkCenterMember(ManWorkCenter manWorkCenter, List<ManWorkCenterMember> memberList) {
        memberList.forEach(o -> {
            o.setWorkCenterSid(manWorkCenter.getWorkCenterSid());
        });
        manWorkCenterMemberMapper.inserts(memberList);
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManWorkCenter manWorkCenter) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manWorkCenter.getWorkCenterSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manWorkCenter.getWorkCenterSid()));
        }
    }

    /**
     * 修改工作中心/班组
     *
     * @param manWorkCenter 工作中心/班组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManWorkCenter(ManWorkCenter manWorkCenter) {
        checkNameUnique(manWorkCenter);
        setConfirmInfo(manWorkCenter);
        setCode(manWorkCenter);
        manWorkCenter.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ManWorkCenter response = manWorkCenterMapper.selectManWorkCenterById(manWorkCenter.getWorkCenterSid());
        setProcessList(manWorkCenter,response.getProcessSids());
        int row = manWorkCenterMapper.updateAllById(manWorkCenter);
        //工作中心/班组-工序对象
        operateItem(manWorkCenter);
        //工作中心/班组-成员对象
        operateMember(manWorkCenter);
        if (!ConstantsEms.SAVA_STATUS.equals(manWorkCenter.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(manWorkCenter);
        }
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manWorkCenter);
        MongodbDeal.update(manWorkCenter.getWorkCenterSid(), response.getHandleStatus(), manWorkCenter.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 工作中心/班组-工序
     */
    private void operateItem(ManWorkCenter manWorkCenter) {
        List<ManWorkCenterProcess> processList = manWorkCenter.getListManWorkCenterProcess();
        if (CollectionUtils.isNotEmpty(processList)) {
            //新增
            List<ManWorkCenterProcess> addList = processList.stream().filter(o -> o.getWorkCenterProcessSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManWorkCenterProcess(manWorkCenter, addList);
            }
            //编辑
            List<ManWorkCenterProcess> editList = processList.stream().filter(o -> o.getWorkCenterProcessSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manWorkCenterProcessMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManWorkCenterProcess> itemList = manWorkCenterProcessMapper.selectList(new QueryWrapper<ManWorkCenterProcess>().lambda()
                    .eq(ManWorkCenterProcess::getWorkCenterSid, manWorkCenter.getWorkCenterSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManWorkCenterProcess::getWorkCenterProcessSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = processList.stream().map(ManWorkCenterProcess::getWorkCenterProcessSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manWorkCenterProcessMapper.deleteBatchIds(result);
            }
        } else {
            deleteManWorkCenterProcess(manWorkCenter);
        }
    }

    /**
     * 工作中心/班组-成员
     */
    private void operateMember(ManWorkCenter manWorkCenter) {
        List<ManWorkCenterMember> memberList = manWorkCenter.getWorkCenterMemberList();
        if (CollectionUtils.isNotEmpty(memberList)) {
            //新增
            List<ManWorkCenterMember> addList = memberList.stream().filter(o -> o.getWorkCenterMemberSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addWorkCenterMember(manWorkCenter, addList);
            }
            //编辑
            List<ManWorkCenterMember> editList = memberList.stream().filter(o -> o.getWorkCenterMemberSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manWorkCenterMemberMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManWorkCenterMember> itemList = manWorkCenterMemberMapper.selectList(new QueryWrapper<ManWorkCenterMember>().lambda()
                    .eq(ManWorkCenterMember::getWorkCenterSid, manWorkCenter.getWorkCenterSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManWorkCenterMember::getWorkCenterMemberSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = memberList.stream().map(ManWorkCenterMember::getWorkCenterMemberSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manWorkCenterMemberMapper.deleteBatchIds(result);
            }
        } else {
            deleteWorkCenterMember(manWorkCenter);
        }
    }

    /**
     * 变更工作中心/班组
     *
     * @param manWorkCenter 工作中心/班组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(ManWorkCenter manWorkCenter) {
        checkNameUnique(manWorkCenter);
        setConfirmInfo(manWorkCenter);
        setCode(manWorkCenter);
        manWorkCenter.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ManWorkCenter response = manWorkCenterMapper.selectManWorkCenterById(manWorkCenter.getWorkCenterSid());
        setProcessList(manWorkCenter,response.getProcessSids());
        int row = manWorkCenterMapper.updateAllById(manWorkCenter);
        //工作中心/班组-工序对象
        operateItem(manWorkCenter);
        //工作中心/班组-成员对象
        operateMember(manWorkCenter);
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manWorkCenter);
        MongodbDeal.update(manWorkCenter.getWorkCenterSid(), response.getHandleStatus(), manWorkCenter.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 批量删除工作中心/班组
     *
     * @param workCenterSids 需要删除的工作中心/班组ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManWorkCenterByIds(List<Long> workCenterSids) {
        Integer count = manWorkCenterMapper.selectCount(new QueryWrapper<ManWorkCenter>().lambda()
                .eq(ManWorkCenter::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManWorkCenter::getWorkCenterSid, workCenterSids));
        if (count != workCenterSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        ManWorkCenter manWorkCenter = new ManWorkCenter();
        workCenterSids.forEach(workCenterSid -> {
            manWorkCenter.setWorkCenterSid(workCenterSid);
            //校验是否存在待办
            checkTodoExist(manWorkCenter);
        });
        manWorkCenterProcessMapper.delete(new UpdateWrapper<ManWorkCenterProcess>().lambda()
                .in(ManWorkCenterProcess::getWorkCenterSid, workCenterSids));
        manWorkCenterMemberMapper.delete(new UpdateWrapper<ManWorkCenterMember>().lambda()
                .in(ManWorkCenterMember::getWorkCenterSid, workCenterSids));
        return manWorkCenterMapper.deleteBatchIds(workCenterSids);
    }

    /**
     * 批量确认工作中心/班组
     *
     * @param
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(ManWorkCenterActionRequest action) {
        List<Long> workCenterSids = action.getWorkCenterSids();
        Integer count = manWorkCenterMapper.selectCount(new QueryWrapper<ManWorkCenter>().lambda()
                .eq(ManWorkCenter::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManWorkCenter::getWorkCenterSid, workCenterSids));
        if (count != workCenterSids.size()) {
            throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
        }
        manWorkCenterMapper.update(null, new UpdateWrapper<ManWorkCenter>().lambda()
                .set(ManWorkCenter::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(ManWorkCenter::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .set(ManWorkCenter::getConfirmDate, new Date())
                .in(ManWorkCenter::getWorkCenterSid, workCenterSids));
        ManWorkCenter manWorkCenter = new ManWorkCenter();
        for (Long id : workCenterSids) {
            manWorkCenter.setWorkCenterSid(id);
            //校验是否存在待办
            checkTodoExist(manWorkCenter);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(id, action.getHandleStatus(), msgList, TITLE, null);
        }
        return workCenterSids.size();
    }

    /**
     * 批量启用/停用 工作中心/班组
     *
     * @param
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int status(ManWorkCenterActionRequest action) {
        List<Long> workCenterSids = action.getWorkCenterSids();
        String status = action.getStatus();
        manWorkCenterMapper.update(null, new UpdateWrapper<ManWorkCenter>().lambda()
                .set(ManWorkCenter::getStatus, status).in(ManWorkCenter::getWorkCenterSid, workCenterSids));
        for (Long id : workCenterSids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.status(id, status, msgList, TITLE, null);
        }
        return workCenterSids.size();
    }

    /**
     * 查询 工作中心/班组列表
     *
     * @param
     * @return 结果
     */
    @Override
    public List<ManWorkCenter> getList() {
        List<ManWorkCenter> list = manWorkCenterMapper.getList();
        return list;
    }

    @Override
    public List<ManWorkCenter> getWorkCenterList(ManWorkCenter manWorkCenter) {
        return manWorkCenterMapper.getWorkCenterList(manWorkCenter);
    }

    /**
     * 获取工厂+部门下启用&确认班组
     */
    @Override
    public List<ManWorkCenter> getCoDeptList(ManWorkCenter manWorkCenter) {
        if (manWorkCenter.getCompanySid() == null) {
            throw new BaseException("参数错误！");
        }
        //公司下工厂
        List<BasPlant> basPlantList = basPlantMapper.selectList(new QueryWrapper<BasPlant>().lambda()
                .eq(BasPlant::getCompanySid, manWorkCenter.getCompanySid()));
        List<ManWorkCenter> workCenterList = new ArrayList<>();
        if (CollUtil.isNotEmpty(basPlantList)) {
            List<Long> planSidList = basPlantList.stream().map(BasPlant::getPlantSid).map(o -> Long.parseLong(o)).collect(Collectors.toList());
            if (manWorkCenter.getDepartmentSid() == null) {
                workCenterList = manWorkCenterMapper.selectList(new QueryWrapper<ManWorkCenter>().lambda()
                        .eq(ManWorkCenter::getHandleStatus, ConstantsEms.CHECK_STATUS)
                        .eq(ManWorkCenter::getStatus, ConstantsEms.ENABLE_STATUS)
                        .in(ManWorkCenter::getPlantSid, planSidList));
            } else {
                workCenterList = manWorkCenterMapper.selectList(new QueryWrapper<ManWorkCenter>().lambda()
                        .eq(ManWorkCenter::getHandleStatus, ConstantsEms.CHECK_STATUS)
                        .eq(ManWorkCenter::getStatus, ConstantsEms.ENABLE_STATUS)
                        .eq(ManWorkCenter::getDepartmentSid, manWorkCenter.getDepartmentSid())
                        .in(ManWorkCenter::getPlantSid, planSidList));
            }
        }
        return workCenterList;
    }

    /**
     * 班组 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importDataPur(MultipartFile file) {
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //班组类型
            List<DictData> workCenterTypeList = sysDictDataService.selectDictData("s_work_center_type");
            Map<String, String> workCenterTypeMaps = workCenterTypeList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //是否
            List<DictData> sysList = sysDictDataService.selectDictData("sys_yes_no");
            Map<String, String> sysMaps = sysList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //参考工序所引用数量类型
            List<DictData> quantityTypeReferProcessList = sysDictDataService.selectDictData("s_quantity_type_refer_process");
            Map<String, String> quantityTypeReferProcessMaps = quantityTypeReferProcessList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            ArrayList<ManWorkCenter> manWorkCenterList = new ArrayList<>();
            HashSet<String> set = new HashSet<>();
            HashSet<String> setName = new HashSet<>();
            CommonErrMsgResponse warnMsgResponseStaff = null;
            List<CommonErrMsgResponse> warnListStaff = new ArrayList<>();
            CommonErrMsgResponse warnMsgResponseWorkName = null;
            List<CommonErrMsgResponse> warnListWorkName = new ArrayList<>();
            String productionMode=null;
            String department=null;
            String processSids=null;
            String director=null;
            Long plantSid=null;
            String departmentCode=null;
            Long departmentSid=null;
            Long vendorSid=null;
            String workCenterType=null;
            Long output=null;
            String vendorCode=null;
            int size = readAll.size();
            if(size<2){
                throw new BaseException("表格数据不能为空");
            }
            for (int i = 0; i < readAll.size(); i++) {
                boolean isSkip=false;
                String suit=null;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                int num = i + 1;
                if (objects.get(0) == null || objects.get(0) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("班组编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(JudgeFormat.isCodeType(objects.get(0).toString())){
                        ManWorkCenter manWorkCenter = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                                .eq(ManWorkCenter::getWorkCenterCode, objects.get(0).toString())
                        );
                        if(!set.add(objects.get(0).toString())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("表格中，班组编码"+objects.get(0).toString()+"重复，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        if(manWorkCenter!=null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("系统中，班组编码"+objects.get(0).toString()+"已存在，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }else{
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("班组编码格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("班组名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    ManWorkCenter manWorkCenter = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                            .eq(ManWorkCenter::getWorkCenterName, objects.get(1).toString())
                    );
                    if(manWorkCenter!=null){
                        isSkip=true;
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("系统中，班组名称"+objects.get(1).toString()+"已存在");
                        warnListWorkName.add(errMsgResponse);
                    }
                    if(!setName.add(objects.get(1).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("表格中，班组名称"+objects.get(1).toString()+"重复，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("所属工厂简称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasPlant basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>().lambda()
                            .eq(BasPlant::getShortName, objects.get(2).toString())
                    );
                    if(basPlant==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("不存在简称为"+objects.get(2).toString()+"的工厂，导入失败!");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basPlant.getHandleStatus())
                                ||!ConstantsEms.ENABLE_STATUS.equals(basPlant.getStatus())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("工厂必须为确认且启用状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            plantSid=Long.valueOf(basPlant.getPlantSid());
                        }
                    }
                }

                if (objects.get(3) == null || objects.get(3) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("负责人名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    List<BasStaff> basStaffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                            .eq(BasStaff::getStaffName, objects.get(3).toString())
                    );
                    if(CollectionUtil.isEmpty(basStaffList)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("不存在名称为"+objects.get(3).toString()+"的负责人，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        List<BasStaff> basStaffs=basStaffList.stream().filter(li->ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())&&ConstantsEms.IS_ON_JOB_ZZ.equals(li.getIsOnJob())).collect(Collectors.toList());
                        if(CollectionUtil.isEmpty(basStaffs)){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("负责人必须为确认且在职状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            if(basStaffs.size()>1){
                                BasStaff basStaff = basStaffs.get(0);
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("系统存在多个姓名("+objects.get(3).toString()+")的员工档案，本次导入的是编号"+basStaff.getStaffCode()+"的员工");
                                warnListStaff.add(errMsgResponse);
                                director=basStaff.getStaffCode();
                            }else{
                                BasStaff basStaff = basStaffs.get(0);
                                director=basStaff.getStaffCode();
                            }
                        }
                    }
                }

                if (objects.get(4) == null || objects.get(4) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("操作部门，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>().lambda()
                            .eq(ConManufactureDepartment::getName, objects.get(4).toString())
                    );
                    if(conManufactureDepartment==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("操作部门填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.ENABLE_STATUS.equals(conManufactureDepartment.getStatus())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("操作部门必须为确认且启用状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            departmentCode=conManufactureDepartment.getCode();
                            departmentSid=conManufactureDepartment.getSid();
                        }
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("班组类型，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(workCenterTypeMaps.get(objects.get(5).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("班组类型填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        workCenterType=workCenterTypeMaps.get(objects.get(5).toString());
                    }
                }
                if (objects.get(6) != null && objects.get(6) != ""){
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>().lambda()
                            .eq(BasVendor::getShortName, objects.get(6).toString())
                    );
                    if(basVendor==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("供应商"+objects.get(6).toString()+"不存在，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())
                                ||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("供应商必须为确认且启用状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            vendorSid=basVendor.getVendorSid();
                            vendorCode=basVendor.getVendorCode().toString();
                        }
                    }
                }


                if (objects.get(7) != null && objects.get(7) != "") {
                    String process = objects.get(7).toString();
                    String[] processList = process.split(";|；");
                    Set<String> processSet = new HashSet<>(Arrays.asList(processList));
                    for (String name : processSet) {
                        ManProcess manProcess = manProcessMapper.selectOne(new QueryWrapper<ManProcess>().lambda()
                                .eq(ManProcess::getProcessName, name)
                        );
                        if(manProcess==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("负责工序"+name+"不存在，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(manProcess.getHandleStatus())
                                    ||!ConstantsEms.ENABLE_STATUS.equals(manProcess.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("负责工序"+name+"必须为确认且启用状态，导入失败！");
                                msgList.add(errMsgResponse);
                            }else{
                                if(processSids==null){
                                    processSids=manProcess.getProcessSid().toString();
                                }else{
                                    processSids=processSids+";"+manProcess.getProcessSid().toString();
                                }

                            }
                        }
                    }
                }
                if (objects.get(8) != null && objects.get(8) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(8).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("产能/天数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(8).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("产能/天数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            output=Long.valueOf(objects.get(8).toString());
                        }
                    }
                }
                ManWorkCenter manWorkCenter = new ManWorkCenter();
                manWorkCenter.setWorkCenterCode(objects.get(0)==""||objects.get(0)==null?null:objects.get(0).toString())
                        .setWorkCenterName(objects.get(1)==""||objects.get(1)==null?null:objects.get(1).toString())
                        .setPlantSid(plantSid)
                        .setDirector(director)
                        .setDepartmentCode(departmentCode)
                        .setDepartmentSid(departmentSid)
                        .setWorkCenterType(workCenterType)
                        .setVendorSid(vendorSid)
                        .setVendorCode(vendorCode)
                        .setProcessSids(processSids)
                        .setOutput(output)
                        .setStatus(ConstantsEms.ENABLE_STATUS)
                        .setHandleStatus(ConstantsEms.ENABLE_STATUS)
                        .setCreateDate(new Date())
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setAddress(objects.get(9)==""||objects.get(9)==null?null:objects.get(9).toString())
                        .setRemark(objects.get(10)==""||objects.get(10)==null?null:objects.get(10).toString());

                if(!isSkip){
                    manWorkCenterList.add(manWorkCenter);
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return EmsResultEntity.error(msgList);
            }
            List<CommonErrMsgResponse> allList = new ArrayList<>();
            allList.addAll(warnListWorkName);
            allList.addAll(warnListStaff);
            if(CollectionUtil.isNotEmpty(manWorkCenterList)){
                manWorkCenterMapper.inserts(manWorkCenterList);
                task(manWorkCenterList);
            }
            String  msg = "导入成功" + manWorkCenterList.size() + "条";
            String msgStaff="，与系统中员工姓名存在重复" + warnListStaff .size()+ "条";
            String msgWorkName="，与系统中班组名称存在重复" + warnListWorkName .size()+ "条(已跳过)";
            if(warnListWorkName.size()>0&&warnListStaff.size()==0){
                msg=msg+msgWorkName;
            }else if(warnListWorkName.size()==0&&warnListStaff.size()>0){
                msg=msg+msgStaff;
            }else if(warnListWorkName.size()==0&&warnListStaff.size()==0){
            }else{
                msg=msg+msgWorkName+msgStaff;
            }
            return EmsResultEntity.success(manWorkCenterList.size(), allList, msg);
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
        }
    }
    public void task( ArrayList<ManWorkCenter> manWorkCenterList){
        manWorkCenterList.forEach(li->{
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_WORK_CENTER)
                    .setDocumentSid(li.getWorkCenterSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("工作中心/班组" + li.getWorkCenterCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(li.getWorkCenterCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
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
}
