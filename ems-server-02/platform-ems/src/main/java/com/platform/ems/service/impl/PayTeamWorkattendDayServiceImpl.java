package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.BasPlantMapper;
import com.platform.ems.mapper.ManWorkCenterMapper;
import com.platform.ems.mapper.PayTeamWorkattendDayMapper;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.IPayTeamWorkattendDayService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 班组日出勤信息Service业务层处理
 *
 * @author linhongwei
 * @date 2022-07-27
 */
@Service
@SuppressWarnings("all" )
public class PayTeamWorkattendDayServiceImpl extends ServiceImpl<PayTeamWorkattendDayMapper,PayTeamWorkattendDay> implements IPayTeamWorkattendDayService {
    @Autowired
    private PayTeamWorkattendDayMapper payTeamWorkattendDayMapper;

    private static final String TITLE = "班组日出勤信息" ;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    /**
     * 查询班组日出勤信息
     *
     * @param teamWorkattendDaySid 班组日出勤信息ID
     * @return 班组日出勤信息
     */
    @Override
    public PayTeamWorkattendDay selectPayTeamWorkattendDayById(Long teamWorkattendDaySid) {
        PayTeamWorkattendDay payTeamWorkattendDay =payTeamWorkattendDayMapper.selectPayTeamWorkattendDayById(teamWorkattendDaySid);
        MongodbUtil.find(payTeamWorkattendDay);
        return payTeamWorkattendDay;
    }
    /**
     * 获取当前账号的信息
     *
     */
    @Override
    public PayTeamWorkattendDay getPayTeamWorkattend(){
        String username = ApiThreadLocalUtil.get().getUsername();
        PayTeamWorkattendDay payTeamWorkattendByUserName = payTeamWorkattendDayMapper.getPayTeamWorkattendByUserName(username);
        return payTeamWorkattendByUserName;
    }
    /**
     * 查询班组日出勤信息列表
     *
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 班组日出勤信息
     */
    @Override
    public List<PayTeamWorkattendDay> selectPayTeamWorkattendDayList(PayTeamWorkattendDay payTeamWorkattendDay) {
        return payTeamWorkattendDayMapper.selectPayTeamWorkattendDayList(payTeamWorkattendDay);
    }

    /**
     * 查询班组日出勤信息
     *
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 班组日出勤信息集合
     */
    @Override
    public PayTeamWorkattendDay selectPayTeamWorkattendDayListBy(PayTeamWorkattendDay payTeamWorkattendDay) {
        List<PayTeamWorkattendDay> list = payTeamWorkattendDayMapper.selectPayTeamWorkattendDayList(payTeamWorkattendDay);
        if (CollectionUtil.isNotEmpty(list) && list.size() > 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 新增班组日出勤信息
     * 需要注意编码重复校验
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayTeamWorkattendDay(PayTeamWorkattendDay payTeamWorkattendDay) {
        judgeRepate(payTeamWorkattendDay);
        setConfirm(payTeamWorkattendDay);
        int row = payTeamWorkattendDayMapper.insert(payTeamWorkattendDay);
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(payTeamWorkattendDay.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_PAY_TEAM_WORKATTEND_DAY)
                    .setDocumentSid(payTeamWorkattendDay.getTeamWorkattendDaySid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                PayTeamWorkattendDay day = payTeamWorkattendDayMapper.selectById(payTeamWorkattendDay.getTeamWorkattendDaySid());
                sysTodoTask.setTitle("班组日出勤" + day.getTeamWorkattendDayCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(day.getTeamWorkattendDayCode()!=null?day.getTeamWorkattendDayCode().toString():null)
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        }
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PayTeamWorkattendDay(), payTeamWorkattendDay);
            MongodbDeal.insert(payTeamWorkattendDay.getTeamWorkattendDaySid(), payTeamWorkattendDay.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    public void judgeRepate(PayTeamWorkattendDay payTeamWorkattendDay){
        PayTeamWorkattendDay item = payTeamWorkattendDayMapper.selectOne(new QueryWrapper<PayTeamWorkattendDay>().lambda()
                .eq(PayTeamWorkattendDay::getWorkCenterSid, payTeamWorkattendDay.getWorkCenterSid())
                .eq(PayTeamWorkattendDay::getWorkShift, payTeamWorkattendDay.getWorkShift())
                .eq(PayTeamWorkattendDay::getPlantSid, payTeamWorkattendDay.getPlantSid())
                .eq(PayTeamWorkattendDay::getDepartment, payTeamWorkattendDay.getDepartment())
                .eq(PayTeamWorkattendDay::getWorkattendDate, payTeamWorkattendDay.getWorkattendDate())
        );
        if((item!=null
                &&payTeamWorkattendDay.getTeamWorkattendDaySid()!=null
                &&!payTeamWorkattendDay.getTeamWorkattendDaySid().toString().equals(item.getTeamWorkattendDaySid().toString()))
        ||(payTeamWorkattendDay.getTeamWorkattendDaySid()==null&&item!=null)
        ){
            throw new CustomException("日期+工厂+操作部门+班组+工作班次”的值的组合已存在！");
        }
    }

    public void setConfirm(PayTeamWorkattendDay payTeamWorkattendDay){
        if(ConstantsEms.CHECK_STATUS.equals(payTeamWorkattendDay.getHandleStatus())){
            payTeamWorkattendDay.setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
            .eq(SysTodoTask::getDocumentSid,payTeamWorkattendDay.getTeamWorkattendDaySid())
            );
        }
    }
    /**
     * 修改班组日出勤信息
     *
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayTeamWorkattendDay(PayTeamWorkattendDay payTeamWorkattendDay) {
        judgeRepate(payTeamWorkattendDay);
        setConfirm(payTeamWorkattendDay);
        PayTeamWorkattendDay original = payTeamWorkattendDayMapper.selectPayTeamWorkattendDayById(payTeamWorkattendDay.getTeamWorkattendDaySid());
        int row = payTeamWorkattendDayMapper.updateAllById(payTeamWorkattendDay);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, payTeamWorkattendDay);
            MongodbDeal.update(payTeamWorkattendDay.getTeamWorkattendDaySid(), original.getHandleStatus(), payTeamWorkattendDay.getHandleStatus(), msgList, TITLE, null);
        }

        return row;
    }



    /**
     * 变更班组日出勤信息
     *
     * @param payTeamWorkattendDay 班组日出勤信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayTeamWorkattendDay(PayTeamWorkattendDay payTeamWorkattendDay) {
        setConfirm(payTeamWorkattendDay);
        PayTeamWorkattendDay response = payTeamWorkattendDayMapper.selectPayTeamWorkattendDayById(payTeamWorkattendDay.getTeamWorkattendDaySid());
        int row = payTeamWorkattendDayMapper.updateAllById(payTeamWorkattendDay);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payTeamWorkattendDay.getTeamWorkattendDaySid(), BusinessType.CHANGE.getValue(), response, payTeamWorkattendDay, TITLE);
        }
        return row;
    }

    /**
     * 批量删除班组日出勤信息
     *
     * @param teamWorkattendDaySids 需要删除的班组日出勤信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayTeamWorkattendDayByIds(List<Long> teamWorkattendDaySids) {
                List<PayTeamWorkattendDay> list = payTeamWorkattendDayMapper.selectList(new QueryWrapper<PayTeamWorkattendDay>()
                .lambda().in(PayTeamWorkattendDay::getTeamWorkattendDaySid, teamWorkattendDaySids));
        int row = payTeamWorkattendDayMapper.deleteBatchIds(teamWorkattendDaySids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PayTeamWorkattendDay());
                MongodbUtil.insertUserLog(o.getTeamWorkattendDaySid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid,teamWorkattendDaySids)
        );
        return row;
    }

    /**
     * 启用/停用
     * @param payTeamWorkattendDay
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(PayTeamWorkattendDay payTeamWorkattendDay) {
                int row = 0;
        return row;
    }

    /**
     *更改确认状态
     * @param payTeamWorkattendDay
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayTeamWorkattendDay payTeamWorkattendDay) {
                int row = 0;
        Long[] sids =payTeamWorkattendDay.getTeamWorkattendDaySidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<PayTeamWorkattendDay> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PayTeamWorkattendDay::getTeamWorkattendDaySid, sids);
            updateWrapper.set(PayTeamWorkattendDay::getHandleStatus, payTeamWorkattendDay.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(payTeamWorkattendDay.getHandleStatus())) {
                updateWrapper.set(PayTeamWorkattendDay::getConfirmDate, new Date());
                updateWrapper.set(PayTeamWorkattendDay::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = payTeamWorkattendDayMapper.update(null, updateWrapper);
            if (row > 0){
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, payTeamWorkattendDay.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid,sids)
        );
        return row;
    }

    /**
     * 班组日出勤 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataPur(MultipartFile file) {
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
            //工作班次
            List<DictData> workCenterTypeList = sysDictDataService.selectDictData("s_work_shift");
            Map<String, String> workCenterTypeMaps = workCenterTypeList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            ArrayList<PayTeamWorkattendDay> payTeamWorkattendDayList = new ArrayList<>();
            HashSet<String> set = new HashSet<>();
            HashSet<String> setName = new HashSet<>();
            String department=null;
            Long plantSid=null;
            String departmentCode=null;
            Long departmentSid=null;
            Long vendorSid=null;
            String workshif=null;
            Long workCenterSid=null;
            Long yingcq=null;
            String vendorCode=null;
            Long shicq=null;
            Long qingj=null;
            Long dail=null;
            Long kuangg=null;
            Long quek=null;
            Long chid=null;
            Long zaot=null;
            Date workattendDate=null;
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
                    errMsgResponse.setMsg("日期，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(!JudgeFormat.isValidDate(objects.get(0).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("日期数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        workattendDate= DateUtils.parseDate(objects.get(0).toString());
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("工厂简称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    BasPlant basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>().lambda()
                            .eq(BasPlant::getShortName, objects.get(1).toString())
                    );
                    if(basPlant==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("不存在简称为"+objects.get(1).toString()+"的工厂，导入失败!");
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

                if (objects.get(2) == null || objects.get(2) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("班组，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    ManWorkCenter manWorkCenter = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                            .eq(ManWorkCenter::getWorkCenterName, objects.get(2).toString())
                    );
                    if(manWorkCenter==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("不存在名称为"+objects.get(2).toString()+"的班组，导入失败!");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(manWorkCenter.getHandleStatus())
                                ||!ConstantsEms.ENABLE_STATUS.equals(manWorkCenter.getStatus())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("班组必须为确认且启用状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            workCenterSid=manWorkCenter.getWorkCenterSid();
                            ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>().lambda()
                                    .eq(ConManufactureDepartment::getSid, manWorkCenter.getDepartmentSid())
                            );
                            if(conManufactureDepartment!=null){
                                departmentCode=conManufactureDepartment.getCode();
                            }
                            departmentSid=manWorkCenter.getDepartmentSid();
                        }
                    }
                }
                if(plantSid!=null&&workCenterSid!=null){
                    ManWorkCenter manWorkCenter = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                            .eq(ManWorkCenter::getWorkCenterSid, workCenterSid)
                            .eq(ManWorkCenter::getPlantSid,plantSid)
                    );
                    if(manWorkCenter==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg(objects.get(1).toString()+"下不存在名称为"+objects.get(2).toString()+"的班组，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("工作班次，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(workCenterTypeMaps.get(objects.get(3).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("工作班次填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        workshif=workCenterTypeMaps.get(objects.get(3).toString());
                    }
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("应出勤(人数)，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean valid = JudgeFormat.isValidInt(objects.get(4).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("应出勤(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(4).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("应出勤(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            yingcq=Long.valueOf(objects.get(4).toString());
                        }
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("实出勤(人数)，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean valid = JudgeFormat.isValidInt(objects.get(5).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("实出勤(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(5).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("实出勤(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            shicq=Long.valueOf(objects.get(5).toString());
                        }
                    }
                }
                if (objects.get(6) != null && objects.get(6) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(6).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("请假(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(6).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("请假(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            qingj=Long.valueOf(objects.get(6).toString());
                        }
                    }
                }
                if (objects.get(7) != null && objects.get(7) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(7).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("待料(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(7).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("待料(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            dail=Long.valueOf(objects.get(7).toString());
                        }
                    }
                }
                if (objects.get(8) != null && objects.get(8) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(8).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("旷工(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(8).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("旷工(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            kuangg=Long.valueOf(objects.get(8).toString());
                        }
                    }
                }

                if (objects.get(9) != null && objects.get(9) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(9).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("缺卡(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(9).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("缺卡(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            quek=Long.valueOf(objects.get(9).toString());
                        }
                    }
                }
                if (objects.get(10) != null && objects.get(10) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(10).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("迟到(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(10).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("迟到(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            chid=Long.valueOf(objects.get(10).toString());
                        }
                    }
                }
                if (objects.get(11) != null && objects.get(11) != "") {
                    boolean valid = JudgeFormat.isValidInt(objects.get(11).toString());
                    if(!valid){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("早退(人数)数据格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        if(Double.valueOf(objects.get(11).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("早退(人数)数据格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            zaot=Long.valueOf(objects.get(11).toString());
                        }
                    }
                }
                PayTeamWorkattendDay payTeamWorkattendDay = new PayTeamWorkattendDay();
                payTeamWorkattendDay.setDepartment(departmentCode)
                        .setWorkattendDate(workattendDate)
                        .setPlantSid(plantSid)
                        .setWorkCenterSid(workCenterSid)
                        .setWorkShift(workshif)
                        .setKuangg(kuangg)
                        .setQuek(quek)
                        .setHandleStatus(ConstantsEms.ENABLE_STATUS)
                        .setCreateDate(new Date())
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setQingj(qingj)
                        .setShicq(shicq)
                        .setDail(dail)
                        .setChid(chid)
                        .setZaot(zaot)
                        .setYingcq(yingcq)
                        .setRemark(objects.get(12)==""||objects.get(12)==null?null:objects.get(12).toString());
                    payTeamWorkattendDayList.add(payTeamWorkattendDay);
                    if(departmentCode!=null&&workattendDate!=null&&workCenterSid!=null&&workshif!=null&&plantSid!=null){
                        if(!setName.add(departmentCode+workattendDate+workCenterSid+workshif+plantSid)){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("“日期+工厂+操作部门+班组+工作班次”的值的组合已存在！");
                            msgList.add(errMsgResponse);
                        }
                    }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("导入失败",msgList);
            }
            for (int i = 0; i < payTeamWorkattendDayList.size(); i++) {
                PayTeamWorkattendDay item = payTeamWorkattendDayMapper.selectOne(new QueryWrapper<PayTeamWorkattendDay>().lambda()
                        .eq(PayTeamWorkattendDay::getWorkCenterSid, payTeamWorkattendDayList.get(i).getWorkCenterSid())
                        .eq(PayTeamWorkattendDay::getWorkShift, payTeamWorkattendDayList.get(i).getWorkShift())
                        .eq(PayTeamWorkattendDay::getPlantSid, payTeamWorkattendDayList.get(i).getPlantSid())
                        .eq(PayTeamWorkattendDay::getDepartment, payTeamWorkattendDayList.get(i).getDepartment())
                        .eq(PayTeamWorkattendDay::getWorkattendDate, payTeamWorkattendDayList.get(i).getWorkattendDate())
                );
                if(item!=null){
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(i+3);
                    errMsgResponse.setMsg("“日期+工厂+操作部门+班组+工作班次”的值的组合已存在！");
                    msgList.add(errMsgResponse);
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("导入失败",msgList);
            }
            if(CollectionUtil.isNotEmpty(payTeamWorkattendDayList)){
                payTeamWorkattendDayMapper.inserts(payTeamWorkattendDayList);
            }
            return AjaxResult.success("导入成功");
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
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



}
