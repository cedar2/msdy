package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManProcess;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManProcessActionRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.ems.mapper.ManProcessMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.IManProcessService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import jodd.cli.Cli;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工序Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Service
@SuppressWarnings("all")
public class ManProcessServiceImpl extends ServiceImpl<ManProcessMapper, ManProcess> implements IManProcessService {
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;

    private static final String TITLE = "工序";


    /**
     * 查询工序
     *
     * @param processSid 工序ID
     * @return 工序
     */
    @Override
    public ManProcess selectManProcessById(Long processSid) {
        ManProcess manProcess = manProcessMapper.selectManProcessById(processSid);
        MongodbUtil.find(manProcess);
        return manProcess;
    }

    /**
     * 查询工序列表
     *
     * @param manProcess 工序
     * @return 工序
     */
    @Override
    public List<ManProcess> selectManProcessList(ManProcess manProcess) {
        List<ManProcess> manProcesses = manProcessMapper.selectManProcessList(manProcess);
        return manProcesses;
    }

    /**
     * 新增工序
     * 需要注意编码重复校验
     *
     * @param manProcess 工序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProcess(ManProcess manProcess) {
        List<ManProcess> list1 = manProcessMapper.selectList(new QueryWrapper<ManProcess>().lambda()
                .eq(ManProcess::getProcessCode, manProcess.getProcessCode()));
        if (list1.size() > 0) {
            throw new BaseException("工序编码已存在！");
        }
        List<ManProcess> list2 = manProcessMapper.selectList(new QueryWrapper<ManProcess>().lambda()
                .eq(ManProcess::getProcessName, manProcess.getProcessName()));
        if (list2.size() > 0) {
            throw new BaseException("工序名称已存在！");
        }
        setDefaultField(manProcess);
        setConfirmInfo(manProcess);
        int row = manProcessMapper.insert(manProcess);
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbDeal.insert(manProcess.getProcessSid(), manProcess.getHandleStatus(), msgList, TITLE, null);

        ManProcess process = manProcessMapper.selectManProcessById(manProcess.getProcessSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(manProcess.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_PROCESS)
                    .setDocumentSid(manProcess.getProcessSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("工序" + process.getProcessCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(process.getProcessCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(manProcess);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManProcess o) {
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
    private void checkNameUnique(ManProcess manProcess) {
        List<ManProcess> list = manProcessMapper.selectList(new QueryWrapper<ManProcess>().lambda()
                .eq(ManProcess::getProcessName, manProcess.getProcessName()));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(o -> {
                if (!manProcess.getProcessSid().equals(o.getProcessSid())) {
                    throw new BaseException("工序名称已存在！");
                }
            });
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManProcess manProcess) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manProcess.getProcessSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manProcess.getProcessSid()));
        }
    }

    /**
     * 修改工序
     *
     * @param manProcess 工序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProcess(ManProcess manProcess) {
        checkNameUnique(manProcess);
        setDefaultField(manProcess);
        setConfirmInfo(manProcess);
        ManProcess response = manProcessMapper.selectManProcessById(manProcess.getProcessSid());
        int row = manProcessMapper.updateAllById(manProcess);
        if (!ConstantsEms.SAVA_STATUS.equals(manProcess.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(manProcess);
        }
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manProcess);
        MongodbDeal.update(manProcess.getProcessSid(), response.getHandleStatus(), manProcess.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 批量删除工序
     *
     * @param processSids 需要删除的工序ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProcessByIds(List<Long> processSids) {
        Integer count = manProcessMapper.selectCount(new QueryWrapper<ManProcess>().lambda()
                .eq(ManProcess::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManProcess::getProcessSid, processSids));
        if (count != processSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        ManProcess manProcess = new ManProcess();
        processSids.forEach(processSid -> {
            manProcess.setProcessSid(processSid);
            //校验是否存在待办
            checkTodoExist(manProcess);
        });
        return manProcessMapper.deleteBatchIds(processSids);
    }

    /**
     * 修改工序信息状态(确认)
     *
     * @param manProcessActionRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(ManProcessActionRequest manProcessActionRequest) {
        List<Long> processSids = manProcessActionRequest.getProcessSids();
        Integer count = manProcessMapper.selectCount(new QueryWrapper<ManProcess>().lambda()
                .eq(ManProcess::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManProcess::getProcessSid, processSids));
        if (count != processSids.size()) {
            throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
        }
        manProcessMapper.update(null, new UpdateWrapper<ManProcess>().lambda()
                .set(ManProcess::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(ManProcess::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .set(ManProcess::getConfirmDate, new Date())
                .in(ManProcess::getProcessSid, processSids));
        ManProcess manProcess = new ManProcess();
        for (Long id : processSids) {
            manProcess.setProcessSid(id);
            //校验是否存在待办
            checkTodoExist(manProcess);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(id, manProcessActionRequest.getHandleStatus(), msgList, TITLE, null);
        }
        return processSids.size();
    }

    /**
     * 变更工序
     *
     * @param manProcess 工序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(ManProcess manProcess) {
        checkNameUnique(manProcess);
        setDefaultField(manProcess);
        setConfirmInfo(manProcess);
        manProcess.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manProcess.setUpdateDate(new Date());
        ManProcess response = manProcessMapper.selectManProcessById(manProcess.getProcessSid());
        int row = manProcessMapper.updateAllById(manProcess);
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manProcess);
        MongodbDeal.update(manProcess.getProcessSid(), response.getHandleStatus(), manProcess.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 设置默认值
     */
    private void setDefaultField(ManProcess manProcess) {
        if (manProcess.getIsProduceComplete() == null) {
            manProcess.setIsProduceComplete(ConstantsEms.NO);
        }
        if (manProcess.getIsDetailQuantityEntry() == null) {
            manProcess.setIsDetailQuantityEntry(ConstantsEms.NO);
        }
        if (manProcess.getIsFirstProcess() == null) {
            manProcess.setIsFirstProcess(ConstantsEms.NO);
        }
        if (manProcess.getIsProduceComplete().equals(ConstantsEms.YES) && !manProcess.getIsDetailQuantityEntry().equals(ConstantsEms.YES)) {
            throw new BaseException("成品完工的工序，“是否录入明细完工量”要选择“是”");
        }
    }

    /**
     * 修改工序信息状态(启用/停用)
     *
     * @param manProcessActionRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int status(ManProcessActionRequest manProcessActionRequest) {
        String status = manProcessActionRequest.getStatus();
        List<Long> processSids = manProcessActionRequest.getProcessSids();
        manProcessMapper.update(null, new UpdateWrapper<ManProcess>().lambda()
                .set(ManProcess::getStatus, status).in(ManProcess::getProcessSid, processSids));
        for (Long id : processSids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.status(id, status, msgList, TITLE, null);
        }
        return processSids.size();
    }

    /**
     * 工序 导入
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
            //生产方式
            sysDictDataService.deleteDictData("s_production_mode");
            List<DictData> productionModeList = sysDictDataService.selectDictData("s_production_mode");
            Map<String, String> productionModes = productionModeList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //是否
            List<DictData> sysList = sysDictDataService.selectDictData("sys_yes_no");
            Map<String, String> sysMaps = sysList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //参考工序所引用数量类型
            sysDictDataService.deleteDictData("s_quantity_type_refer_process");
            List<DictData> quantityTypeReferProcessList = sysDictDataService.selectDictData("s_quantity_type_refer_process");
            Map<String, String> quantityTypeReferProcessMaps = quantityTypeReferProcessList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //参考工序所引用数量类型
            sysDictDataService.deleteDictData("s_special_flag");
            List<DictData> specialFlagList = sysDictDataService.selectDictData("s_special_flag");
            Map<String, String> specialFlagMaps = specialFlagList.stream().filter(li-> "0".equals(li.getStatus())&&ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 工序类别
            List<DictData> categoryList = sysDictDataService.selectDictData("s_process_category");
            Map<String,DictData> categoryMaps = categoryList.stream().collect(Collectors.toMap(DictData::getDictLabel, Function.identity()));
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            ArrayList<ManProcess> manProcessList = new ArrayList<>();
            HashSet<String> set = new HashSet<>();
            HashSet<String> setName = new HashSet<>();
            String productionMode=null;
            String department=null;
            Long quantityReferProcessSid=null;
            String quantityReferProcessCode=null;
            CommonErrMsgResponse warnMsgResponse = null;
            List<CommonErrMsgResponse> warnList = new ArrayList<>();
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
                    errMsgResponse.setMsg("工序编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(JudgeFormat.isCodeType(objects.get(0).toString())){
                        ManProcess manProcess = manProcessMapper.selectOne(new QueryWrapper<ManProcess>().lambda()
                                .eq(ManProcess::getProcessCode, objects.get(0).toString())
                        );
                        if(!set.add(objects.get(0).toString())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("表格中，工序编码"+objects.get(0).toString()+"重复，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        if(manProcess!=null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("系统中，工序编码"+objects.get(0).toString()+"已存在，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }else{
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("工序编码格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("工序名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    ManProcess manProcess = manProcessMapper.selectOne(new QueryWrapper<ManProcess>().lambda()
                            .eq(ManProcess::getProcessName, objects.get(1).toString())
                    );
                    if(manProcess!=null){
                        isSkip=true;
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("系统中，工序名称"+objects.get(1).toString()+"已存在");
                        warnList.add(errMsgResponse);
                    }

                    if(!setName.add(objects.get(1).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("表格中，工序名称"+objects.get(1).toString()+"重复，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("生产方式，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                     productionMode = productionModes.get(objects.get(2).toString());
                     if(productionMode==null){
                         CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                         errMsgResponse.setItemNum(num);
                         errMsgResponse.setMsg("生产方式填写错误，导入失败！");
                         msgList.add(errMsgResponse);
                     }
                }

                if (objects.get(3) == null || objects.get(3) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("是否录入明细完工量，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(sysMaps.get(objects.get(3).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否录入明细完工量填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }

                if (objects.get(4) == null || objects.get(4) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("是否被道序引用，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(sysMaps.get(objects.get(4).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否被道序引用填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("是否标志部门完成的工序，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(sysMaps.get(objects.get(5).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否标志部门完成的工序填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(6) == null || objects.get(6) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("是否标志成品完工的工序，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(sysMaps.get(objects.get(6).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否标志成品完工的工序填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(7) == null || objects.get(7) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("是否第一个工序，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(sysMaps.get(objects.get(7).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否第一个工序填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(8) == null || objects.get(8) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("操作部门，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>().lambda()
                            .eq(ConManufactureDepartment::getName, objects.get(8).toString())
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
                            errMsgResponse.setMsg("操作部门必须为启用状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            department=conManufactureDepartment.getCode();
                        }
                    }

                }

                if (objects.get(9) != null && objects.get(9) != "") {
                    ManProcess manProcess = manProcessMapper.selectOne(new QueryWrapper<ManProcess>().lambda()
                            .eq(ManProcess::getProcessName, objects.get(9).toString())
                    );
                    if(manProcess==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("完成量校验参考工序"+objects.get(9).toString()+"不存在，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(manProcess.getHandleStatus())
                           ||!ConstantsEms.ENABLE_STATUS.equals(manProcess.getStatus())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("完成量校验参考工序必须为确认且启用状态，导入失败！");
                            msgList.add(errMsgResponse);
                        }else{
                            quantityReferProcessSid=manProcess.getProcessSid();
                            quantityReferProcessCode=manProcess.getProcessCode();
                        }
                    }
                }
                if (objects.get(10) != null && objects.get(10) != "") {
                    if(quantityTypeReferProcessMaps.get(objects.get(10).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("数量类型填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(11) != null && objects.get(11) != "") {
                    if(specialFlagMaps.get(objects.get(11).toString())==null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("特殊工序标识填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                }
                /*
                 * 工序类别
                 */
                String processCategoryName = objects.get(12)==null||objects.get(12)==""?null:objects.get(12).toString();
                String processCategory = null;
                if(StrUtil.isNotBlank(processCategoryName)){
                    if(!categoryMaps.containsKey(processCategoryName)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("工序类别" + processCategoryName + "不存在，导入失败！");
                        msgList.add(errMsgResponse);
                    }else {
                        if (!HandleStatus.CONFIRMED.getCode().equals(categoryMaps.get(processCategoryName).getHandleStatus()) ||
                                !ConstantsEms.SYS_COMMON_STATUS_Y.equals(categoryMaps.get(processCategoryName).getStatus())) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("工序类别" + processCategoryName + "填写错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        else {
                            processCategory = categoryMaps.get(processCategoryName).getDictValue();
                        }
                    }
                }
                ManProcess manProcess = new ManProcess();
                manProcess.setProcessName((objects.get(1)==""||objects.get(1)==null)?null:objects.get(1).toString())
                        .setProcessCode((objects.get(0)==""||objects.get(0)==null)?null:objects.get(0).toString())
                        .setProductionMode(productionMode)
                        .setDepartment(department)
                        .setIsDetailQuantityEntry((objects.get(3)==""||objects.get(3)==null)?null:sysMaps.get(objects.get(3).toString()))
                        .setIsProcessStepUsed((objects.get(4)==""||objects.get(4)==null)?null:sysMaps.get(objects.get(4).toString()))
                        .setIsStageComplete((objects.get(5)==""||objects.get(5)==null)?null:sysMaps.get(objects.get(5).toString()))
                        .setIsProduceComplete((objects.get(6)==""||objects.get(6)==null)?null:sysMaps.get(objects.get(6).toString()))
                        .setIsFirstProcess((objects.get(7)==""||objects.get(7)==null)?null:sysMaps.get(objects.get(7).toString()))
                        .setQuantityReferProcessSid(quantityReferProcessSid)
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setCreateDate(new Date())
                        .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                        .setConfirmDate(new Date())
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setStatus(ConstantsEms.ENABLE_STATUS)
                        .setQuantityReferProcessCode(quantityReferProcessCode)
                        .setQuantityTypeReferProcess((objects.get(10)==""||objects.get(10)==null)?null:quantityTypeReferProcessMaps.get(objects.get(10).toString()))
                        .setSpecialFlag((objects.get(11)==""||objects.get(11)==null)?null:specialFlagMaps.get(objects.get(11).toString()))
                        .setRemark((objects.get(13)==""||objects.get(13)==null)?null:objects.get(13).toString())
                        .setProcessCategory(processCategory);
                if(!isSkip){
                    manProcessList.add(manProcess);
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return EmsResultEntity.error(msgList);
            }
            if(CollectionUtil.isNotEmpty(manProcessList)){
                manProcessMapper.inserts(manProcessList);
            }
            if(warnList.size()>0){
                String  msg = "导入成功" + manProcessList.size() + "条，与系统中工序名称存在重复"+warnList.size()+"条（已跳过）";
                return EmsResultEntity.success(manProcessList.size(), warnList, msg);
            }
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
        }
        return EmsResultEntity.success("");
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
    /**
     * 工序档案列表
     *
     * @param manProcessActionRequest
     * @return 结果
     */
    @Override
    public List<ManProcess> getList(ManProcess manProcess) {
        return manProcessMapper.getList(manProcess);
    }
}
