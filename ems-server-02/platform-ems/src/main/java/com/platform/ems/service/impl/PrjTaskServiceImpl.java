package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IPrjTaskService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 任务节点Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-07
 */
@Service
@SuppressWarnings("all" )
public class PrjTaskServiceImpl extends ServiceImpl<PrjTaskMapper,PrjTask> implements IPrjTaskService {
    @Autowired
    private PrjTaskMapper prjTaskMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasPositionMapper basPositionMapper;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "任务节点" ;

    /**
     * 查询任务节点
     *
     * @param taskSid 任务节点ID
     * @return 任务节点
     */
    @Override
    public PrjTask selectPrjTaskById(Long taskSid) {
        PrjTask prjTask =prjTaskMapper.selectPrjTaskById(taskSid);
        if (prjTask != null) {
            // 发起岗位 处理
            if (StrUtil.isNotBlank(prjTask.getStartPositionCode())) {
                String[] start = prjTask.getStartPositionCode().split(";");
                prjTask.setStartPositionCodeList(start);
            }
            // 负责岗位 处理
            if (StrUtil.isNotBlank(prjTask.getChargePositionCode())) {
                String[] charge = prjTask.getChargePositionCode().split(";");
                prjTask.setChargePositionCodeList(charge);
            }
            // 告知岗位 处理
            if (StrUtil.isNotBlank(prjTask.getNoticePositionCode())) {
                String[] notice = prjTask.getNoticePositionCode().split(";");
                prjTask.setNoticePositionCodeList(notice);
            }
        }
        MongodbUtil.find(prjTask);
        return prjTask;
    }

    /**
     * 复制任务节点
     *
     * @param taskSid 任务节点ID
     * @return 任务节点
     */
    @Override
    public PrjTask copyPrjTaskById(Long taskSid) {
        PrjTask prjTask =prjTaskMapper.selectPrjTaskById(taskSid);
        // 相关字段置空
        prjTask.setCreateDate(null).setCreatorAccount(null).setCreatorAccountName(null)
                .setUpdateDate(null).setUpdaterAccount(null).setUpdaterAccountName(null)
                .setConfirmDate(null).setConfirmerAccount(null).setConfirmerAccountName(null);
        prjTask.setHandleStatus(ConstantsEms.SAVA_STATUS).setStatus(ConstantsEms.ENABLE_STATUS)
                .setTaskSid(null).setTaskCode(null);
        return prjTask;
    }

    /**
     * 查询任务节点列表
     *
     * @param prjTask 任务节点
     * @return 任务节点
     */
    @Override
    public List<PrjTask> selectPrjTaskList(PrjTask prjTask) {
        List<PrjTask> list = prjTaskMapper.selectPrjTaskList(prjTask);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                // 发起岗位
                if (StrUtil.isNotBlank(item.getStartPositionCode())) {
                    String[] starts = item.getStartPositionCode().split(";");
                    List<BasPosition> startList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                            .lambda().in(BasPosition::getPositionCode, starts));
                    if (ArrayUtil.isNotEmpty(startList)) {
                        String startName = "";
                        for (int i = 0; i < startList.size(); i++) {
                            startName = startName + startList.get(i).getPositionName() + ";";
                        }
                        item.setStartPositionName(startName);
                    }
                }
                // 负责岗位
                if (StrUtil.isNotBlank(item.getChargePositionCode())) {
                    String[] charges = item.getChargePositionCode().split(";");
                    List<BasPosition> chargeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                            .lambda().in(BasPosition::getPositionCode, charges));
                    if (ArrayUtil.isNotEmpty(chargeList)) {
                        String chargeName = "";
                        for (int i = 0; i < chargeList.size(); i++) {
                            chargeName = chargeName + chargeList.get(i).getPositionName() + ";";
                        }
                        item.setChargePositionName(chargeName);
                    }
                }
                // 告知岗位
                if (StrUtil.isNotBlank(item.getNoticePositionCode())) {
                    String[] notices = item.getNoticePositionCode().split(";");
                    List<BasPosition> noticeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                            .lambda().in(BasPosition::getPositionCode, notices));
                    if (ArrayUtil.isNotEmpty(noticeList)) {
                        String noticeName = "";
                        for (int i = 0; i < noticeList.size(); i++) {
                            noticeName = noticeName + noticeList.get(i).getPositionName() + ";";
                        }
                        item.setNoticePositionName(noticeName);
                    }
                }
            });
        }
        return list;
    }

    /**
     * 数据字段处理
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void setData(PrjTask prjTask) {
        // 发起岗位处理
        String startCode = null;
        if (ArrayUtil.isNotEmpty(prjTask.getStartPositionCodeList())) {
            startCode = "";
            for (int i = 0; i < prjTask.getStartPositionCodeList().length; i++) {
                startCode = startCode + prjTask.getStartPositionCodeList()[i] + ";";
            }
        }
        prjTask.setStartPositionCode(startCode);
        // 负责岗位处理
        String chargeCode = null;
        if (ArrayUtil.isNotEmpty(prjTask.getChargePositionCodeList())) {
            chargeCode = "";
            for (int i = 0; i < prjTask.getChargePositionCodeList().length; i++) {
                chargeCode = chargeCode + prjTask.getChargePositionCodeList()[i] + ";";
            }
        }
        prjTask.setChargePositionCode(chargeCode);
        // 告知岗位处理
        String noticeCode = null;
        if (ArrayUtil.isNotEmpty(prjTask.getNoticePositionCodeList())) {
            noticeCode = "";
            for (int i = 0; i < prjTask.getNoticePositionCodeList().length; i++) {
                noticeCode = noticeCode + prjTask.getNoticePositionCodeList()[i] + ";";
            }
        }
        prjTask.setNoticePositionCode(noticeCode);
    }

    /**
     * 新增任务节点
     * 需要注意编码重复校验
     * @param prjTask 任务节点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjTask(PrjTask prjTask) {
        // 校验
        judge(prjTask);
        // 字段数据处理
        this.setData(prjTask);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(prjTask.getHandleStatus())) {
            prjTask.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = prjTaskMapper.insert(prjTask);
        if (row > 0) {
            // 主要获取编码
            PrjTask original = prjTaskMapper.selectPrjTaskById(prjTask.getTaskSid());
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(prjTask.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_PRJ_TASK)
                        .setDocumentSid(prjTask.getTaskSid());
                    sysTodoTask.setTitle("任务节点" + original.getTaskCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(original.getTaskCode())
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PrjTask(), prjTask);
            MongodbDeal.insert(prjTask.getTaskSid(), prjTask.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 写入部分字段值
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void setFieldData(PrjTask prjTask) {

    }

    /**
     * 校验
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void judge(PrjTask prjTask) {
        // 校验名称是否重复
        QueryWrapper<PrjTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PrjTask::getTaskName, prjTask.getTaskName());
        if (prjTask.getTaskSid() != null) {
            queryWrapper.lambda().ne(PrjTask::getTaskSid, prjTask.getTaskSid());
        }
        List<PrjTask> taskNameList = prjTaskMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(taskNameList)) {
            throw new BaseException("任务节点名称已存在！");
        }
    }

    /**
     * 修改任务节点
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjTask(PrjTask prjTask) {
        PrjTask original = prjTaskMapper.selectPrjTaskById(prjTask.getTaskSid());
        // 校验
        if (!prjTask.getTaskName().equals(original.getTaskName())) {
            judge(prjTask);
        }
        // 字段数据处理
        this.setData(prjTask);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(prjTask.getHandleStatus())) {
            prjTask.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, prjTask);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjTask.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = prjTaskMapper.updateAllById(prjTask);
        if (row > 0) {
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(prjTask.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, prjTask.getTaskSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_TASK));
            }
            //插入日志
            MongodbDeal.update(prjTask.getTaskSid(), original.getHandleStatus(), prjTask.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更任务节点
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePrjTask(PrjTask prjTask) {
        PrjTask response = prjTaskMapper.selectPrjTaskById(prjTask.getTaskSid());
        // 校验
        if (!prjTask.getTaskName().equals(response.getTaskName())) {
            judge(prjTask);
        }
        // 字段数据处理
        this.setData(prjTask);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, prjTask);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjTask.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = prjTaskMapper.updateAllById(prjTask);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(prjTask.getTaskSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除任务节点
     *
     * @param taskSids 需要删除的任务节点ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjTaskByIds(List<Long> taskSids) {
        if (CollectionUtil.isEmpty(taskSids)) {
            return 0;
        }
        List<PrjTask> list = prjTaskMapper.selectList(new QueryWrapper<PrjTask>().lambda().in(PrjTask::getTaskSid, taskSids));
        // 删除校验
        list = list.stream().filter(o-> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(o.getHandleStatus()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = prjTaskMapper.deleteBatchIds(taskSids);
        if (row > 0) {
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, taskSids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_TASK));
            // 插入日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PrjTask());
                MongodbUtil.insertUserLog(o.getTaskSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     * @param prjTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(PrjTask prjTask) {
        int row = 0;
        Long[] sids =prjTask.getTaskSidList();
        if (sids != null && sids.length > 0) {
            row = prjTaskMapper.update(null, new UpdateWrapper<PrjTask>().lambda().set(PrjTask::getStatus,prjTask.getStatus() )
                    .in(PrjTask::getTaskSid, sids));
            if (row == 0) {
                throw new BaseException("更改状态失败,请联系管理员" );
            }
            for (Long id : sids) {
                MongodbDeal.status(id, prjTask.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     *更改确认状态
     * @param prjTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PrjTask prjTask) {
        int row = 0;
        Long[] sids =prjTask.getTaskSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<PrjTask> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PrjTask::getTaskSid, sids);
            updateWrapper.set(PrjTask::getHandleStatus, prjTask.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(prjTask.getHandleStatus())) {
                updateWrapper.set(PrjTask::getConfirmDate, new Date());
                updateWrapper.set(PrjTask::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = prjTaskMapper.update(null, updateWrapper);
            if (row > 0){
                if (ConstantsEms.CHECK_STATUS.equals(prjTask.getHandleStatus())) {
                    // 删除待办
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sids)
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_TASK));
                }
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, prjTask.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }


    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
        int num = 0;
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
            //数据字典Map
            List<DictData> relateBusinessFormDict = sysDictDataService.selectDictData("s_relate_business_form"); // 关联业务单据
            relateBusinessFormDict = relateBusinessFormDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> relateBusinessFormMaps = relateBusinessFormDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> isMonitorDict = sysDictDataService.selectDictData("sys_yes_no"); // 是否监控
            isMonitorDict = isMonitorDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> isMonitorMaps = isMonitorDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> calendarTypeDict = sysDictDataService.selectDictData("s_day_type"); // 日历类型
            calendarTypeDict = calendarTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> calendarTypeMaps = calendarTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> taskPhaseDict = sysDictDataService.selectDictData("s_task_phase"); // 所属任务阶段
            taskPhaseDict = taskPhaseDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> taskPhaseMaps = taskPhaseDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();

            // 当前用户 的关联员工 的所属公司
            Long companySid = null;
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null) {
                BasStaff staff = basStaffMapper.selectById(staffSid);
                if (staff != null && staff.getDefaultCompanySid() != null) {
                    BasCompany company = basCompanyMapper.selectById(staff.getDefaultCompanySid());
                    companySid = company.getCompanySid();
                }
            }

            // 基本
            PrjTask prjTask = null;
            List<PrjTask> prjTaskList = new ArrayList<>();
            Map<String, String> prjTaskHasMap = new HashMap<>();
            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                /**
                 * 任务节点名称 必填
                 */
                String taskName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(taskName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("任务节点名称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (taskName.length() > 300){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("任务节点名称长度不能超过300个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        // 表格中
                        if (prjTaskHasMap.containsKey(taskName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("表格中，任务节点名称已存在！");
                            errMsgList.add(errMsg);
                        } else {
                            // 存入map
                            prjTaskHasMap.put(taskName, "1");
                            // 去空格
                            taskName = taskName.replaceAll(" ","");
                            // 系统中
                            List<PrjTask> tasks = prjTaskMapper.selectList(new QueryWrapper<PrjTask>().lambda()
                                    .eq(PrjTask::getTaskName, taskName));
                            if (CollectionUtil.isNotEmpty(tasks)) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("系统中，任务节点名称已存在！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 标准用时(天) 选填
                 */
                String standardTimeString = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long standardTime = null;
                if (StrUtil.isNotBlank(standardTimeString)) {
                    if (!JudgeFormat.isValidInt(standardTimeString)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("标准用时(天)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        int day = Integer.parseInt(standardTimeString);
                        if (day <= 0 || day >= 1000) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("标准用时(天)格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        standardTime = Long.valueOf(standardTimeString);
                    }
                }

                /**
                 * 关联业务单据 选填
                 */
                String relateBusinessFormName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                String relateBusinessFormCode = null;
                if (StrUtil.isNotBlank(relateBusinessFormName)) {
                    relateBusinessFormCode = relateBusinessFormMaps.get(relateBusinessFormName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(relateBusinessFormCode)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("关联业务单据填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 发起岗位 必填
                 */
                String startPositionNames = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                List<String> startCodeList = new ArrayList<>();
                if (StrUtil.isNotBlank(startPositionNames)) {
                    if (companySid == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("发起岗位不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        String[] startPositionNameList = startPositionNames.split(";|；");
                        //字符串拆分数组后利用set去重复
                        Set<String> startsSet = new HashSet<>(Arrays.asList(startPositionNameList));
                        for (String startPositionName : startsSet) {
                            try {
                                BasPosition position1 = basPositionMapper.selectOne(new QueryWrapper<BasPosition>().lambda()
                                        .eq(BasPosition::getPositionName, startPositionName)
                                        .eq(BasPosition::getCompanySid, companySid));
                                if (position1 == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("名称为"+ startPositionName +"没有对应的岗位，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    if (ConstantsEms.DISENABLE_STATUS.equals(position1.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(position1.getHandleStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("岗位" + position1.getPositionName() + "必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                    startCodeList.add(position1.getPositionCode());
                                }
                            }catch (TooManyResultsException e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(startPositionName + "岗位档案存在重复，请先检查该岗位，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 负责岗位 必填
                 */
                String chargePositionNames = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                List<String> chargeCodeList = new ArrayList<>();
                if (StrUtil.isNotBlank(chargePositionNames)) {
                    if (companySid == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("负责岗位不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        String[] chargePositionNameList = chargePositionNames.split(";|；");
                        //字符串拆分数组后利用set去重复
                        Set<String> chargesSet = new HashSet<>(Arrays.asList(chargePositionNameList));
                        for (String chargePositionName : chargesSet) {
                            try {
                                BasPosition position2 = basPositionMapper.selectOne(new QueryWrapper<BasPosition>().lambda()
                                        .eq(BasPosition::getPositionName,chargePositionName)
                                        .eq(BasPosition::getCompanySid, companySid));
                                if (position2 == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("名称为"+ chargePositionName +"没有对应的岗位，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    if (ConstantsEms.DISENABLE_STATUS.equals(position2.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(position2.getHandleStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("岗位" + position2.getPositionName() + "必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                    chargeCodeList.add(position2.getPositionCode());
                                }
                            }catch (TooManyResultsException e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(chargePositionName + "岗位档案存在重复，请先检查该岗位，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 告知岗位 选填
                 */
                String noticePositionNames = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                List<String> noticeCodeList = new ArrayList<>();
                if (StrUtil.isNotBlank(noticePositionNames)) {
                    if (companySid == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("告知岗位不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        String[] noticePositionNameList = noticePositionNames.split(";|；");
                        //字符串拆分数组后利用set去重复
                        Set<String> noticesSet = new HashSet<>(Arrays.asList(noticePositionNameList));
                        for (String noticePositionName : noticesSet) {
                            try {
                                BasPosition position3 = basPositionMapper.selectOne(new QueryWrapper<BasPosition>().lambda()
                                        .eq(BasPosition::getPositionName,noticePositionName)
                                        .eq(BasPosition::getCompanySid, companySid));
                                if (position3 == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("名称为"+ noticePositionName +"没有对应的岗位，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    if (ConstantsEms.DISENABLE_STATUS.equals(position3.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(position3.getHandleStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("岗位" + position3.getPositionName() + "必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                    noticeCodeList.add(position3.getPositionCode());
                                }
                            }catch (TooManyResultsException e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(noticePositionName + "岗位档案存在重复，请先检查该岗位，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 是否监控(数据字典) 必填
                 */
                String isMonitorName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                String isMonitor = null;
                if (StrUtil.isNotBlank(isMonitorName)) {
                    isMonitor = isMonitorMaps.get(isMonitorName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(isMonitor)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否监控填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 日历类型(数据字典) 必填
                 */
                String calendarTypeName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                String calendarType = null;
                if (StrUtil.isNotBlank(calendarTypeName)) {
                    calendarType = calendarTypeMaps.get(calendarTypeName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(calendarType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("日历类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 所属任务阶段(数据字典) 必填
                 */
                String taskPhaseName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                String taskPhase = null;
                if (StrUtil.isNotBlank(taskPhaseName)) {
                    taskPhase = taskPhaseMaps.get(taskPhaseName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(taskPhase)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("所属任务阶段填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                if (StrUtil.isNotBlank(remark) && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能超过600个字符，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)){
                    prjTask = new PrjTask();
                    prjTask.setTaskName(taskName).setStandardTime(standardTime).setRelateBusinessFormCode(relateBusinessFormCode)
                            .setIsMonitor(isMonitor).setCalendarType(calendarType).setTaskPhase(taskPhase).setRemark(remark);
                    prjTask.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
                    if (CollectionUtil.isNotEmpty(startCodeList)) {
                        prjTask.setStartPositionCodeList(startCodeList.toArray(new String[startCodeList.size()]));
                    }
                    if (CollectionUtil.isNotEmpty(chargeCodeList)) {
                        prjTask.setChargePositionCodeList(chargeCodeList.toArray(new String[chargeCodeList.size()]));
                    }
                    if (CollectionUtil.isNotEmpty(noticeCodeList)) {
                        prjTask.setNoticePositionCodeList(noticeCodeList.toArray(new String[noticeCodeList.size()]));
                    }
                    prjTaskList.add(prjTask);
                }
            }

            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }
            if (CollectionUtil.isNotEmpty(prjTaskList)){
                prjTaskList.forEach(item->{
                    this.insertPrjTask(item);
                });
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num-2);
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll){
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
