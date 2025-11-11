package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.ManProcess;
import com.platform.ems.domain.PayProcessStep;
import com.platform.ems.domain.PayProductProcessStepItem;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.service.IPayProcessStepService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.mapper.SysTodoTaskMapper;
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
 * 通用道序Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-07
 */
@Service
@SuppressWarnings("all")
public class PayProcessStepServiceImpl extends ServiceImpl<PayProcessStepMapper, PayProcessStep> implements IPayProcessStepService {
    @Autowired
    private PayProcessStepMapper payProcessStepMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private PayProductProcessStepMapper payProductProcessStepMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "通用道序";

    private static final String STEP_CATEGORY = "s_process_step_category";

    /**
     * 查询通用道序
     *
     * @param processStepSid 通用道序ID
     * @return 通用道序
     */
    @Override
    public PayProcessStep selectPayProcessStepById(Long processStepSid) {
        PayProcessStep payProcessStep = payProcessStepMapper.selectPayProcessStepById(processStepSid);
        MongodbUtil.find(payProcessStep);
        return payProcessStep;
    }

    /**
     * 查询通用道序列表
     *
     * @param payProcessStep 通用道序
     * @return 通用道序
     */
    @Override
    public List<PayProcessStep> selectPayProcessStepList(PayProcessStep payProcessStep) {
        return payProcessStepMapper.selectPayProcessStepList(payProcessStep);
    }

    /**
     * 新增通用道序
     * 需要注意编码重复校验
     *
     * @param payProcessStep 通用道序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayProcessStep(PayProcessStep payProcessStep) {
        /*List<PayProcessStep> codeList = payProcessStepMapper.selectList(new QueryWrapper<PayProcessStep>().lambda()
                .eq(PayProcessStep::getProcessStepCode, payProcessStep.getProcessStepCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException("通用道序编码已存在！");
        }*/
        List<PayProcessStep> nameList = payProcessStepMapper.selectList(new QueryWrapper<PayProcessStep>().lambda()
                .eq(PayProcessStep::getProcessStepName, payProcessStep.getProcessStepName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException("道序名称已存在！");
        }
        setConfirmInfo(payProcessStep);
        payProcessStep.setCreateDate(new Date());
        int row = payProcessStepMapper.insert(payProcessStep);

        PayProcessStep processStep = payProcessStepMapper.selectPayProcessStepById(payProcessStep.getProcessStepSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(payProcessStep.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_PAY_PROCESS_STEP)
                    .setDocumentSid(payProcessStep.getProcessStepSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("道序" + processStep.getProcessStepCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(processStep.getProcessStepCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(payProcessStep);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(payProcessStep.getProcessStepSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PayProcessStep payProcessStep) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, payProcessStep.getProcessStepSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, payProcessStep.getProcessStepSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PayProcessStep o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改通用道序
     *
     * @param payProcessStep 通用道序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProcessStep(PayProcessStep payProcessStep) {
        checkNameUnique(payProcessStep);
        setConfirmInfo(payProcessStep);
        PayProcessStep response = payProcessStepMapper.selectPayProcessStepById(payProcessStep.getProcessStepSid());
        int row = payProcessStepMapper.updateById(payProcessStep);
        if (!ConstantsEms.SAVA_STATUS.equals(payProcessStep.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(payProcessStep);
        }
        //插入日志
        MongodbUtil.insertUserLog(payProcessStep.getProcessStepSid(), BusinessType.UPDATE.getValue(), response, payProcessStep, TITLE);
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(PayProcessStep payProcessStep) {
        List<PayProcessStep> nameList = payProcessStepMapper.selectList(new QueryWrapper<PayProcessStep>().lambda()
                .eq(PayProcessStep::getProcessStepName, payProcessStep.getProcessStepName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!payProcessStep.getProcessStepSid().equals(o.getProcessStepSid())) {
                    throw new BaseException("道序名称已存在！");
                }
            });
        }
    }

    /**
     * 变更通用道序
     *
     * @param payProcessStep 通用道序
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProcessStep(PayProcessStep payProcessStep) {
        checkNameUnique(payProcessStep);
        payProcessStep.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        setConfirmInfo(payProcessStep);
        PayProcessStep response = payProcessStepMapper.selectPayProcessStepById(payProcessStep.getProcessStepSid());
        int row = payProcessStepMapper.updateAllById(payProcessStep);
        //插入日志
        MongodbUtil.insertUserLog(payProcessStep.getProcessStepSid(), BusinessType.CHANGE.getValue(), response, payProcessStep, TITLE);
        return row;
    }

    /**
     * 批量删除通用道序
     *
     * @param processStepSids 需要删除的通用道序ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProcessStepByIds(List<Long> processStepSids) {
        Integer count = payProcessStepMapper.selectCount(new QueryWrapper<PayProcessStep>().lambda()
                .eq(PayProcessStep::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(PayProcessStep::getProcessStepSid, processStepSids));
        if (processStepSids.size() != count) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        List<PayProductProcessStepItem> productProcessStepItems =
                payProductProcessStepItemMapper.selectPayProductProcessStepItemList(new PayProductProcessStepItem().setProcessStepSids(processStepSids));
        if (CollUtil.isNotEmpty(productProcessStepItems)) {
            //道序编码
            List<String> processStepCodeList = productProcessStepItems.stream().map(PayProductProcessStepItem::getProcessStepCode).distinct().collect(Collectors.toList());
            //商品编码
//            List<String> materialCodeList = productProcessStepItems.stream().map(PayProductProcessStepItem::getMaterialCode).distinct().collect(Collectors.toList());
            throw new BaseException("道序" + processStepCodeList.toString() + "，已被商品道序引用，删除失败！");
        }

        PayProcessStep payProcessStep = new PayProcessStep();
        processStepSids.forEach(processStepSid -> {
            payProcessStep.setProcessStepSid(processStepSid);
            //校验是否存在待办
            checkTodoExist(payProcessStep);
        });
        return payProcessStepMapper.deleteBatchIds(processStepSids);
    }

    /**
     * 启用/停用
     *
     * @param payProcessStep
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(PayProcessStep payProcessStep) {
        int row = 0;
        Long[] sids = payProcessStep.getProcessStepSidList();
        if (sids != null && sids.length > 0) {
            row = payProcessStepMapper.update(null, new UpdateWrapper<PayProcessStep>().lambda()
                    .set(PayProcessStep::getStatus, payProcessStep.getStatus())
                    .in(PayProcessStep::getProcessStepSid, sids));
            for (Long id : sids) {
                payProcessStep.setProcessStepSid(id);
                row = payProcessStepMapper.updateById(payProcessStep);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
//                String remark = payProcessStep.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                String remark = StrUtil.isEmpty(payProcessStep.getDisableRemark()) ? null : payProcessStep.getDisableRemark();
                String businessType = payProcessStep.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? BusinessType.ENABLE.getValue() : BusinessType.DISENABLE.getValue();
                MongodbUtil.insertUserLog(payProcessStep.getProcessStepSid(), businessType, msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param payProcessStep
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayProcessStep payProcessStep) {
        int row = 0;
        Long[] sids = payProcessStep.getProcessStepSidList();
        Integer count = payProcessStepMapper.selectCount(new QueryWrapper<PayProcessStep>().lambda()
                .eq(PayProcessStep::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(PayProcessStep::getProcessStepSid, sids));
        if (sids.length != count) {
            throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
        }
        row = payProcessStepMapper.update(null, new UpdateWrapper<PayProcessStep>().lambda()
                .set(PayProcessStep::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(PayProcessStep::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .set(PayProcessStep::getConfirmDate, new Date())
                .in(PayProcessStep::getProcessStepSid, sids));
        for (Long id : sids) {
            payProcessStep.setProcessStepSid(id);
            //校验是否存在待办
            checkTodoExist(payProcessStep);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
        }

        return row;
    }

    /**
     * 通用道序停用校验
     */
    @Override
    public PayProcessStep disableVerify(PayProcessStep payProcessStep) {
        List<Long> processStepSids = payProcessStep.getProcessStepSids();
        List<PayProcessStep> resultList = new ArrayList<>();
        PayProcessStep processStep = new PayProcessStep();
        List<PayProductProcessStepItem> list =
                payProductProcessStepItemMapper.selectPayProductProcessStepItemList(new PayProductProcessStepItem().setProcessStepSids(processStepSids));
        if (CollUtil.isNotEmpty(list)) {
            //若被非"已确认"状态的商品道序引用
            List<String> processStepCodeList1 = list.stream().filter(o -> !ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus()))
                    .map(PayProductProcessStepItem::getProcessStepCode).distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(processStepCodeList1)) {
                throw new BaseException("道序" + processStepCodeList1.toString() + "，已被商品道序引用，不能停用！");
            }
            //若被"已确认"状态的商品道序引用
            List<String> processStepCodeList2 = list.stream().filter(o -> ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus()))
                    .map(PayProductProcessStepItem::getProcessStepCode).distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(processStepCodeList2)) {
                processStep.setMsg("道序" + processStepCodeList2.toString() + "，已被商品道序引用，是否继续停用！");
            }
        } else {
            processStep.setMsg(ConstantsEms.YES_OR_NO_N);
        }
        return processStep;
    }

    /**
     * 导入通用道序
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
        List<PayProcessStep> payProcessStepList = new ArrayList<>();
        //批量报错提示
        CommonErrMsgResponse errMsgResponse = null;
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        //批量提醒提示
        CommonErrMsgResponse warnMsgResponse = null;
        List<CommonErrMsgResponse> warnList = new ArrayList<>();
        String msg = "";
        int warn = 0;
        String handleStatus = ConstantsEms.CHECK_STATUS;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //道序类别
            List<DictData> stepCategoryList = sysDictDataService.selectDictData("s_process_step_category");
            Map<String, String> stepCategoryMaps = stepCategoryList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 表格名称重复校验
            HashMap<String, String> nameMap = new HashMap<>();
            boolean flag = true;
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                flag = true;
                int sum = i+1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                PayProcessStep payProcessStep = new PayProcessStep();
                String processStepName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(processStepName)) {
                    errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(sum);
                    errMsgResponse.setMsg("道序名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    // 判断是否与表格内的编码重复
                    if (nameMap.get(processStepName) == null) {
                        nameMap.put(processStepName, String.valueOf(sum));
                        // 如果表格内没重复则判断与数据库之间是否存在重复
                        List<PayProcessStep> list = payProcessStepMapper.selectList(new QueryWrapper<PayProcessStep>().lambda()
                                .eq(PayProcessStep::getProcessStepName, processStepName));
                        if (CollUtil.isNotEmpty(list)) {
                            warnMsgResponse = new CommonErrMsgResponse();
                            warnMsgResponse.setItemNum(sum);
                            warnMsgResponse.setMsg("系统中，此道序名称已存在");
                            warnList.add(warnMsgResponse);
                            warn+=1;
                            flag = false;
                        }
                    }else {
                        errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sum);
                        errMsgResponse.setMsg("表格中，道序名称重复，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                String taskUnitName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isEmpty(taskUnitName)) {
                    errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(sum);
                    errMsgResponse.setMsg("作业计量单位名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    List<ConMeasureUnit> list = conMeasureUnitMapper.selectList(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, taskUnitName));
                    if (CollUtil.isEmpty(list)) {
                        errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sum);
                        errMsgResponse.setMsg("作业计量单位，配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        ConMeasureUnit conMeasureUnit = list.get(0);
                        if (!ConstantsEms.CHECK_STATUS.equals(conMeasureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conMeasureUnit.getStatus())) {
                            errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(sum);
                            errMsgResponse.setMsg("作业计量单位，必须是启用且已确认状态，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            payProcessStep.setTaskUnit(conMeasureUnit.getCode());
                        }
                    }
                }
                String processName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isEmpty(processName)) {
                    errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(sum);
                    errMsgResponse.setMsg("所属生产工序名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    List<ManProcess> list = manProcessMapper.selectList(new QueryWrapper<ManProcess>().lambda()
                            .eq(ManProcess::getProcessName, processName));
                    if (CollUtil.isEmpty(list)) {
                        errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sum);
                        errMsgResponse.setMsg(processName + "，没有对应的所属生产工序名称，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        ManProcess manProcess = list.get(0);
                        if (!ConstantsEms.CHECK_STATUS.equals(manProcess.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(manProcess.getStatus())) {
                            errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(sum);
                            errMsgResponse.setMsg("所属生产工序，必须是启用且已确认状态，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if (!ConstantsEms.YES.equals(manProcess.getIsProcessStepUsed())){
                                errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(sum);
                                errMsgResponse.setMsg(processName + "，该所属生产工序名称，不能被道序引用，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            payProcessStep.setProcessSid(manProcess.getProcessSid());
                        }
                    }
                }
                String stepCategory = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (StrUtil.isEmpty(stepCategory)) {
                    errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(sum);
                    errMsgResponse.setMsg("道序类别，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                } else {
                    List<DictData> dictDataList = sysDictDataService.selectDictData(STEP_CATEGORY);
                    List<DictData> list =
                            dictDataList.stream().filter(o -> o.getDictLabel().equals(stepCategory)).collect(Collectors.toList());
                    if (CollUtil.isEmpty(list)) {
                        errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sum);
                        errMsgResponse.setMsg("道序类别，配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if (!ConstantsEms.CHECK_STATUS.equals(list.get(0).getHandleStatus()) || !ConstantsEms.SYS_COMMON_STATUS_Y.equals(list.get(0).getStatus())) {
                            errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(sum);
                            errMsgResponse.setMsg("道序类别，配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            payProcessStep.setStepCategory(list.get(0).getDictValue());
                        }
                    }
                }
                String standardPrice_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal standardPrice = null;
                if (standardPrice_s != null) {
                    try {
                        standardPrice = new BigDecimal(standardPrice_s);
                        if (standardPrice.compareTo(BigDecimal.ZERO) != 1) {
                            errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(sum);
                            errMsgResponse.setMsg("标准工价(元)，不能小于等于0，导入失败");
                            msgList.add(errMsgResponse);
                        }else {
                            standardPrice=standardPrice.divide(BigDecimal.ONE,3,BigDecimal.ROUND_HALF_UP);
                        }
                    }catch (Exception e) {
                        errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sum);
                        errMsgResponse.setMsg("标准工价(元)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                String remark = "";
                if (objects.size() > 5) {
                    remark = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                }
                if (CollectionUtil.isEmpty(msgList)){
                    payProcessStep.setProcessStepName(processStepName);
                    payProcessStep.setStandardPrice(standardPrice);
                    payProcessStep.setCurrency(ConstantsFinance.CURRENCY_CNY);
                    payProcessStep.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                    payProcessStep.setHandleStatus(handleStatus);
                    payProcessStep.setStatus(ConstantsEms.ENABLE_STATUS);
                    payProcessStep.setRemark(remark);
                    if (flag){
                        payProcessStepList.add(payProcessStep);
                    }
                    if (ConstantsEms.CHECK_STATUS.equals(handleStatus)){
                        payProcessStep.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                }
            }
            if (CollUtil.isNotEmpty(msgList)) {
                return EmsResultEntity.error(msgList);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        if (CollUtil.isNotEmpty(payProcessStepList)) {
            payProcessStepMapper.inserts(payProcessStepList);
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus)){
                payProcessStepList.forEach(o -> {
                    MongodbUtil.insertUserLog(o.getProcessStepSid(),BusinessType.IMPORT.getValue(), TITLE);
                    MongodbUtil.insertUserLog(o.getProcessStepSid(),BusinessType.CONFIRM.getValue(), TITLE);
                });
            }
            if (ConstantsEms.SAVA_STATUS.equals(handleStatus)){
                //待办通知
                List<SysTodoTask> todoTaskList = new ArrayList<>();
                payProcessStepList.forEach(o -> {
                    PayProcessStep payProcessStep = payProcessStepMapper.selectPayProcessStepById(o.getProcessStepSid());
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsEms.TABLE_PAY_PROCESS_STEP)
                            .setDocumentSid(o.getProcessStepSid());
                    List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                    if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                        sysTodoTask.setTitle("道序" + payProcessStep.getProcessStepCode() + "当前是保存状态，请及时处理！")
                                .setDocumentCode(String.valueOf(payProcessStep.getProcessStepCode()))
                                .setNoticeDate(new Date())
                                .setUserId(ApiThreadLocalUtil.get().getUserid());
                        todoTaskList.add(sysTodoTask);
                    }
                    MongodbUtil.insertUserLog(o.getProcessStepSid(),BusinessType.IMPORT.getValue(), TITLE);
                });
                sysTodoTaskMapper.inserts(todoTaskList);
            }
            if (CollectionUtil.isNotEmpty(warnList)){
                msg = "导入成功" + payProcessStepList.size() + "条，与系统存在重复" + warn + "条（已跳过）";
                return EmsResultEntity.success(payProcessStepList.size(), warnList, msg);
            }
        } else {
            if (CollectionUtil.isNotEmpty(warnList)){
                msg = "导入成功" + payProcessStepList.size() + "条，与系统存在重复" + warn + "条（已跳过）";
                return EmsResultEntity.success(payProcessStepList.size(), warnList, msg);
            }
            throw new BaseException("请填写数据后再进行导入");
        }
        return EmsResultEntity.success(payProcessStepList.size());
    }

    private void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        for (int i = lineSize; i < size; i++) {
            Object o = null;
            objects.add(o);
        }
    }
}
