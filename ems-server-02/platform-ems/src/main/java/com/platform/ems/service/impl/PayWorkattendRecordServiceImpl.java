package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.constant.Constants;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.PayWorkattendRecordItemResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.service.IPayWorkattendRecordService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考勤信息-主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class PayWorkattendRecordServiceImpl extends ServiceImpl<PayWorkattendRecordMapper, PayWorkattendRecord> implements IPayWorkattendRecordService {
    @Autowired
    private PayWorkattendRecordMapper payWorkattendRecordMapper;
    @Autowired
    private PayWorkattendRecordItemMapper payWorkattendRecordItemMapper;
    @Autowired
    private PayWorkattendRecordAttachMapper payWorkattendRecordAttachMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "考勤信息-主";

    /**
     * 查询考勤信息-主
     *
     * @param workattendRecordSid 考勤信息-主ID
     * @return 考勤信息-主
     */
    @Override
    public PayWorkattendRecord selectPayWorkattendRecordById(Long workattendRecordSid) {
        PayWorkattendRecord payWorkattendRecord = payWorkattendRecordMapper.selectPayWorkattendRecordById(workattendRecordSid);
        if (payWorkattendRecord == null) {
            return null;
        }
        List<PayWorkattendRecordItem> payWorkattendRecordItemList =
                payWorkattendRecordItemMapper.selectPayWorkattendRecordItemList(new PayWorkattendRecordItem().setWorkattendRecordSid(workattendRecordSid));

        List<PayWorkattendRecordAttach> payWorkattendRecordAttachList =
                payWorkattendRecordAttachMapper.selectPayWorkattendRecordAttachList(new PayWorkattendRecordAttach().setWorkattendRecordSid(workattendRecordSid));

        MongodbUtil.find(payWorkattendRecord);
        payWorkattendRecord.setPayWorkattendRecordItemList(payWorkattendRecordItemList);
        payWorkattendRecord.setPayWorkattendRecordAttachList(payWorkattendRecordAttachList);
        return payWorkattendRecord;
    }

    /**
     * 查询考勤信息-主列表
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 考勤信息-主
     */
    @Override
    public List<PayWorkattendRecord> selectPayWorkattendRecordList(PayWorkattendRecord payWorkattendRecord) {
        return payWorkattendRecordMapper.selectPayWorkattendRecordList(payWorkattendRecord);
    }

    /**
     * 查询考勤信息-明细报表
     */
    @Override
    public List<PayWorkattendRecordItemResponse> report(PayWorkattendRecord payWorkattendRecord) {
        return payWorkattendRecordItemMapper.getreport(payWorkattendRecord);
    }
    /**
     * 新增考勤信息-主
     * 需要注意编码重复校验
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayWorkattendRecord(PayWorkattendRecord payWorkattendRecord) {
        scene0(payWorkattendRecord);
        judge0(payWorkattendRecord);
        setConfirmInfo(payWorkattendRecord);
        setCompanyCode(payWorkattendRecord);
        setDepartmentCode(payWorkattendRecord);
        setPlantCode(payWorkattendRecord);
        int row = payWorkattendRecordMapper.insert(payWorkattendRecord);
        if (row > 0) {
            //考勤信息-明细对象
            List<PayWorkattendRecordItem> payWorkattendRecordItemList = payWorkattendRecord.getPayWorkattendRecordItemList();
            if (CollectionUtil.isNotEmpty(payWorkattendRecordItemList)) {
                //根据所属年月+公司+工号校验每位员工每月考勤明细的唯一性
                payWorkattendRecordItemList.forEach(item -> {
                    PayWorkattendRecordItem recordItem = new PayWorkattendRecordItem();
                    recordItem.setYearmonth(payWorkattendRecord.getYearmonth())
                            .setCompanySid(payWorkattendRecord.getCompanySid())
                            .setStaffSid(item.getStaffSid());
                    List<PayWorkattendRecordItem> itemList = payWorkattendRecordItemMapper.selectPayWorkattendRecordItemList(recordItem);
                    if (CollectionUtil.isNotEmpty(itemList)) {
                        throw new BaseException("当前所属年月，考勤单号" + itemList.get(0).getWorkattendRecordCode() + "中已存在工号" + itemList.get(0).getStaffCode() + "的考勤信息，请核实");
                    }
                });
                addPayWorkattendRecordItem(payWorkattendRecord, payWorkattendRecordItemList);
            }
            //考勤信息-附件对象
            List<PayWorkattendRecordAttach> payWorkattendRecordAttachList = payWorkattendRecord.getPayWorkattendRecordAttachList();
            if (CollectionUtil.isNotEmpty(payWorkattendRecordAttachList)) {
                addPayWorkattendRecordAttach(payWorkattendRecord, payWorkattendRecordAttachList);
            }
            PayWorkattendRecord workattendRecord = payWorkattendRecordMapper.selectPayWorkattendRecordById(payWorkattendRecord.getWorkattendRecordSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(payWorkattendRecord.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_WORKATTEND_RECORD)
                        .setDocumentSid(payWorkattendRecord.getWorkattendRecordSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("考勤信息" + workattendRecord.getWorkattendRecordCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(workattendRecord.getWorkattendRecordCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(payWorkattendRecord);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            String businessType = StrUtil.isBlank(payWorkattendRecord.getImportType()) ? BusinessType.INSERT.getValue() : payWorkattendRecord.getImportType();
            MongodbUtil.insertUserLog(payWorkattendRecord.getWorkattendRecordSid(), businessType, msgList, TITLE);
        }
        return row;
    }

    public void judge0(PayWorkattendRecord payWorkattendRecord){
        List<PayWorkattendRecordItem> payWorkattendRecordItemList = payWorkattendRecord.getPayWorkattendRecordItemList();
        HashMap<String, String> map = new HashMap<>();
        Long plantSid=payWorkattendRecord.getPlantSid()!=null?payWorkattendRecord.getPlantSid():1L;
        map.put(payWorkattendRecord.getCompanySid()+";"+plantSid,"1");
        if(CollectionUtil.isNotEmpty(payWorkattendRecordItemList)){
            payWorkattendRecordItemList.forEach(li->{
                Long plantSidLi=li.getDefaultPlantSid()!=null?li.getDefaultPlantSid():1L;
                if(map.get(li.getDefaultCompanySid()+";"+plantSidLi)==null){
                    throw new CustomException("该“公司+工厂”组合，不存在员工"+li.getStaffName()+"，请检查！");
                }
            });
            //根据所属年月+公司+工号校验每位员工每月考勤明细的唯一性
            payWorkattendRecordItemList.forEach(item -> {
                PayWorkattendRecordItem recordItem = new PayWorkattendRecordItem();
                recordItem.setYearmonth(payWorkattendRecord.getYearmonth())
                        .setCompanySid(payWorkattendRecord.getCompanySid())
                        .setStaffSid(item.getStaffSid());
                List<PayWorkattendRecordItem> itemList = payWorkattendRecordItemMapper.selectPayWorkattendRecordItemList(recordItem);
                if (CollectionUtil.isNotEmpty(itemList)) {
                    throw new BaseException("当前所属年月，考勤单号" + itemList.get(0).getWorkattendRecordCode() + "中已存在工号" + itemList.get(0).getStaffCode() + "的考勤信息，请核实");
                }
            });
        }
    }

    private void scene0(PayWorkattendRecord payWorkattendRecord) {
        QueryWrapper<PayWorkattendRecord> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(PayWorkattendRecord::getCompanySid,payWorkattendRecord.getCompanySid())
                .eq(PayWorkattendRecord::getYearmonth, payWorkattendRecord.getYearmonth());
        // 编辑排除自身
        if (payWorkattendRecord.getWorkattendRecordSid() != null){
            queryWrapper.lambda().ne(PayWorkattendRecord::getWorkattendRecordSid,payWorkattendRecord.getWorkattendRecordSid());
        }
        // 选了工厂
        if (payWorkattendRecord.getPlantSid() != null){
            queryWrapper.lambda().eq(PayWorkattendRecord::getPlantSid,payWorkattendRecord.getPlantSid());
                if (CollectionUtil.isNotEmpty(payWorkattendRecordMapper.selectList(queryWrapper))){
                    throw new BaseException("该公司下此工厂在当前所属年月已建立考勤信息，请核实");
                }
        }
        // 没选工厂
        else {
            queryWrapper.lambda().isNull(PayWorkattendRecord::getPlantSid);
            if (CollectionUtil.isNotEmpty(payWorkattendRecordMapper.selectList(queryWrapper))){
                throw new BaseException("该公司下在当前所属年月已建立考勤信息，请核实");
            }
        }
    }

    /**
     * 根据所属年月+公司+工号校验每位员工每月考勤明细的唯一性
     */
    private void verifyItem(PayWorkattendRecord payWorkattendRecord, List<PayWorkattendRecordItem> payWorkattendRecordItemList) {
        payWorkattendRecordItemList.forEach(item -> {
            PayWorkattendRecordItem recordItem = new PayWorkattendRecordItem();
            recordItem.setYearmonth(payWorkattendRecord.getYearmonth())
                    .setCompanySid(payWorkattendRecord.getCompanySid())
                    .setStaffSid(item.getStaffSid());
            List<PayWorkattendRecordItem> itemList = payWorkattendRecordItemMapper.selectPayWorkattendRecordItemList(recordItem);
            if (CollectionUtil.isNotEmpty(itemList) && !payWorkattendRecord.getWorkattendRecordSid().equals(itemList.get(0).getWorkattendRecordSid())) {
                throw new BaseException("当前所属年月，考勤单号" + itemList.get(0).getWorkattendRecordCode() + "中已存在工号" + itemList.get(0).getStaffCode() + "的考勤信息，请核实");
            }
        });
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PayWorkattendRecord payWorkattendRecord) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, payWorkattendRecord.getWorkattendRecordSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, payWorkattendRecord.getWorkattendRecordSid()));
        }
    }

    /**
     * 设置公司编码
     */
    private void setCompanyCode(PayWorkattendRecord paySalaryBill) {
        if (paySalaryBill.getCompanySid() != null) {
            BasCompany basCompany = basCompanyMapper.selectBasCompanyById(paySalaryBill.getCompanySid());
            paySalaryBill.setCompanyCode(basCompany.getCompanyCode());
        }
    }

    private void setDepartmentCode(PayWorkattendRecord paySalaryBill) {
        if (paySalaryBill.getDepartmentSid() != null) {
            BasDepartment basDepartment = basDepartmentMapper.selectBasDepartmentById(paySalaryBill.getDepartmentSid());
            paySalaryBill.setDepartmentCode(basDepartment.getDepartmentCode());
        }
    }

    private void setPlantCode(PayWorkattendRecord paySalaryBill) {
        if (paySalaryBill.getPlantSid() != null){
            BasPlant basPlant = basPlantMapper.selectBasPlantById(paySalaryBill.getPlantSid());
            paySalaryBill.setPlantCode(basPlant.getPlantCode());
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PayWorkattendRecord o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 考勤信息-明细对象
     */
    private void addPayWorkattendRecordItem(PayWorkattendRecord payWorkattendRecord, List<PayWorkattendRecordItem> payWorkattendRecordItemList) {
//        deleteItem(payWorkattendRecord);
        payWorkattendRecordItemList.forEach(o -> {
            o.setWorkattendRecordSid(payWorkattendRecord.getWorkattendRecordSid());
        });
        payWorkattendRecordItemMapper.inserts(payWorkattendRecordItemList);
    }

    private void deleteItem(PayWorkattendRecord payWorkattendRecord) {
        payWorkattendRecordItemMapper.delete(
                new UpdateWrapper<PayWorkattendRecordItem>()
                        .lambda()
                        .eq(PayWorkattendRecordItem::getWorkattendRecordSid, payWorkattendRecord.getWorkattendRecordSid())
        );
    }

    /**
     * 考勤信息-附件对象
     */
    private void addPayWorkattendRecordAttach(PayWorkattendRecord payWorkattendRecord, List<PayWorkattendRecordAttach> payWorkattendRecordAttachList) {
//        deleteAttach(payWorkattendRecord);
        payWorkattendRecordAttachList.forEach(o -> {
            o.setWorkattendRecordSid(payWorkattendRecord.getWorkattendRecordSid());
        });
        payWorkattendRecordAttachMapper.inserts(payWorkattendRecordAttachList);
    }

    private void deleteAttach(PayWorkattendRecord payWorkattendRecord) {
        payWorkattendRecordAttachMapper.delete(
                new UpdateWrapper<PayWorkattendRecordAttach>()
                        .lambda()
                        .eq(PayWorkattendRecordAttach::getWorkattendRecordSid, payWorkattendRecord.getWorkattendRecordSid())
        );
    }

    /**
     * 修改考勤信息-主
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayWorkattendRecord(PayWorkattendRecord payWorkattendRecord) {
        scene0(payWorkattendRecord);
        setConfirmInfo(payWorkattendRecord);
        PayWorkattendRecord response = payWorkattendRecordMapper.selectPayWorkattendRecordById(payWorkattendRecord.getWorkattendRecordSid());
        if (response.getCompanySid() == null ||
                response.getCompanySid().equals(payWorkattendRecord.getCompanySid())){
            setCompanyCode(payWorkattendRecord);
        }
        if (response.getDepartmentSid() == null ||
                response.getDepartmentSid().equals(payWorkattendRecord.getDepartmentSid())){
            setDepartmentCode(payWorkattendRecord);
        }
        if (response.getPlantSid() == null ||
                response.getPlantSid().equals(payWorkattendRecord.getPlantSid())){
            setPlantCode(payWorkattendRecord);
        }
        payWorkattendRecord.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = payWorkattendRecordMapper.updateAllById(payWorkattendRecord);
        if (row > 0) {
            //考勤信息-明细对象
            List<PayWorkattendRecordItem> recordItemList = payWorkattendRecord.getPayWorkattendRecordItemList();
            operateItem(payWorkattendRecord, recordItemList);
            //考勤信息-附件对象
            List<PayWorkattendRecordAttach> recordAttachList = payWorkattendRecord.getPayWorkattendRecordAttachList();
            operateAttachment(payWorkattendRecord, recordAttachList);
            if (!ConstantsEms.SAVA_STATUS.equals(payWorkattendRecord.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(payWorkattendRecord);
            }
            //插入日志
            MongodbUtil.insertUserLog(payWorkattendRecord.getWorkattendRecordSid(), BusinessType.UPDATE.getValue(), response, payWorkattendRecord, TITLE);
        }
        return row;
    }

    /**
     * 考勤信息-明细
     */
    private void operateItem(PayWorkattendRecord payWorkattendRecord, List<PayWorkattendRecordItem> recordItemList) {
        if (CollectionUtil.isNotEmpty(recordItemList)) {
            //根据所属年月+公司+工号校验每位员工每月考勤明细的唯一性
            verifyItem(payWorkattendRecord, recordItemList);
            //新增
            List<PayWorkattendRecordItem> addList = recordItemList.stream().filter(o -> o.getRecordItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPayWorkattendRecordItem(payWorkattendRecord, addList);
            }
            //编辑
            List<PayWorkattendRecordItem> editList = recordItemList.stream().filter(o -> o.getRecordItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    payWorkattendRecordItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PayWorkattendRecordItem> itemList = payWorkattendRecordItemMapper.selectList(new QueryWrapper<PayWorkattendRecordItem>().lambda()
                    .eq(PayWorkattendRecordItem::getWorkattendRecordSid, payWorkattendRecord.getWorkattendRecordSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PayWorkattendRecordItem::getRecordItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = recordItemList.stream().map(PayWorkattendRecordItem::getRecordItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                payWorkattendRecordItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(payWorkattendRecord);
        }
    }

    /**
     * 考勤信息-附件
     */
    private void operateAttachment(PayWorkattendRecord payWorkattendRecord, List<PayWorkattendRecordAttach> recordAttachList) {
        if (CollectionUtil.isNotEmpty(recordAttachList)) {
            //新增
            List<PayWorkattendRecordAttach> addList = recordAttachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPayWorkattendRecordAttach(payWorkattendRecord, addList);
            }
            //编辑
            List<PayWorkattendRecordAttach> editList = recordAttachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    payWorkattendRecordAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PayWorkattendRecordAttach> itemList =
                    payWorkattendRecordAttachMapper.selectList(new QueryWrapper<PayWorkattendRecordAttach>().lambda()
                            .eq(PayWorkattendRecordAttach::getWorkattendRecordSid, payWorkattendRecord.getWorkattendRecordSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PayWorkattendRecordAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = recordAttachList.stream().map(PayWorkattendRecordAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                payWorkattendRecordAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(payWorkattendRecord);
        }
    }


    /**
     * 变更考勤信息-主
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayWorkattendRecord(PayWorkattendRecord payWorkattendRecord) {
        scene0(payWorkattendRecord);
        setConfirmInfo(payWorkattendRecord);
        PayWorkattendRecord response = payWorkattendRecordMapper.selectPayWorkattendRecordById(payWorkattendRecord.getWorkattendRecordSid());
        if (response.getCompanySid() == null ||
                response.getCompanySid().equals(payWorkattendRecord.getCompanySid())){
            setCompanyCode(payWorkattendRecord);
        }
        if (response.getDepartmentSid() == null ||
                response.getDepartmentSid().equals(payWorkattendRecord.getDepartmentSid())){
            setDepartmentCode(payWorkattendRecord);
        }
        if (response.getPlantSid() == null ||
                response.getPlantSid().equals(payWorkattendRecord.getPlantSid())){
            setPlantCode(payWorkattendRecord);
        }
        payWorkattendRecord.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = payWorkattendRecordMapper.updateAllById(payWorkattendRecord);
        if (row > 0) {
            //考勤信息-明细对象
            List<PayWorkattendRecordItem> recordItemList = payWorkattendRecord.getPayWorkattendRecordItemList();
            operateItem(payWorkattendRecord, recordItemList);
            //考勤信息-附件对象
            List<PayWorkattendRecordAttach> recordAttachList = payWorkattendRecord.getPayWorkattendRecordAttachList();
            operateAttachment(payWorkattendRecord, recordAttachList);
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("考勤信息" + payWorkattendRecord.getWorkattendRecordCode() + "已更新")
                    .setDocumentSid(payWorkattendRecord.getWorkattendRecordSid())
                    .setDocumentCode(payWorkattendRecord.getWorkattendRecordCode())
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
            //插入日志
            MongodbUtil.insertUserLog(payWorkattendRecord.getWorkattendRecordSid(), BusinessType.CHANGE.getValue(), response, payWorkattendRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除考勤信息-主
     *
     * @param workattendRecordSids 需要删除的考勤信息-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayWorkattendRecordByIds(List<Long> workattendRecordSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = payWorkattendRecordMapper.selectCount(new UpdateWrapper<PayWorkattendRecord>().lambda()
                .in(PayWorkattendRecord::getHandleStatus, handleStatusList)
                .in(PayWorkattendRecord::getWorkattendRecordSid, workattendRecordSids));
        if (count != workattendRecordSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        //删除考勤信息-明细对象
        payWorkattendRecordItemMapper.delete(new UpdateWrapper<PayWorkattendRecordItem>().lambda()
                .in(PayWorkattendRecordItem::getWorkattendRecordSid, workattendRecordSids));
        ///删除考勤信息-附件对象
        payWorkattendRecordAttachMapper.delete(new UpdateWrapper<PayWorkattendRecordAttach>().lambda()
                .in(PayWorkattendRecordAttach::getWorkattendRecordSid, workattendRecordSids));
        PayWorkattendRecord payWorkattendRecord = new PayWorkattendRecord();
        workattendRecordSids.forEach(workattendRecordSid -> {
            payWorkattendRecord.setWorkattendRecordSid(workattendRecordSid);
            //校验是否存在待办
            checkTodoExist(payWorkattendRecord);
        });
        return payWorkattendRecordMapper.deleteBatchIds(workattendRecordSids);
    }

    /**
     * 更改确认状态
     *
     * @param payWorkattendRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayWorkattendRecord payWorkattendRecord) {
        int row = 0;
        Long[] sids = payWorkattendRecord.getWorkattendRecordSidList();
        for (Long sid : sids) {
            List<PayWorkattendRecordItem> payWorkattendRecordItems = payWorkattendRecordItemMapper.selectList(new QueryWrapper<PayWorkattendRecordItem>().lambda()
                    .eq(PayWorkattendRecordItem::getWorkattendRecordSid, sid)
            );
            if(CollectionUtil.isEmpty(payWorkattendRecordItems)){
                throw new CustomException("考勤单明细不能为空，请检查！");
            }
        }
        if (ArrayUtil.isNotEmpty(sids)) {
            row = payWorkattendRecordMapper.update(null, new UpdateWrapper<PayWorkattendRecord>().lambda()
                    .set(PayWorkattendRecord::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PayWorkattendRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(PayWorkattendRecord::getConfirmDate, new Date())
                    .in(PayWorkattendRecord::getWorkattendRecordSid, sids));

        }
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
        .in(SysTodoTask::getDocumentSid,sids)
        );
        return row;
    }

    /**
     * 单据提交校验
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verify(PayWorkattendRecord payWorkattendRecord) {
        if (null == payWorkattendRecord.getWorkattendRecordSid() || StrUtil.isBlank(payWorkattendRecord.getHandleStatus())) {
            throw new CustomException("参数错误");
        }
        PayWorkattendRecord workattendRecord = selectPayWorkattendRecordById(payWorkattendRecord.getWorkattendRecordSid());
        if (CollectionUtil.isEmpty(workattendRecord.getPayWorkattendRecordItemList())) {
            throw new CustomException("考勤单号" + workattendRecord.getWorkattendRecordCode() + ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
        }
        //校验是否存在待办
        checkTodoExist(payWorkattendRecord);
        return payWorkattendRecordMapper.updateById(payWorkattendRecord);
    }

    @Override
    public void exportItemByRecord(HttpServletResponse response, PayWorkattendRecord payWorkattendRecord) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("考勤明细");
            sheet.setDefaultColumnWidth(20);
            XSSFColor color;
            //样式 - 灰色
            color = new XSSFColor(new java.awt.Color(238, 236, 225));
            XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
            //样式 - 红色
            color = new XSSFColor(new java.awt.Color(255, 0, 0));
            XSSFCellStyle cellStyleRed = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
            //样式 - 绿色
            color = new XSSFColor(new java.awt.Color(146, 208, 80));
            XSSFCellStyle cellStyleGreen = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
            String[] titles = {"考勤单号*","所属年月*","公司*","部门","工厂","班组","考勤模板类型","备注"};
            //第一行数据
            Row rowHead = sheet.createRow(0);
            //第一行样式
            //第一行数据
            for (int i = 0; i < titles.length; i++) {
                Cell cell = rowHead.createCell(i);
                cell.setCellValue(titles[i]);
                if (i < 3){
                    cell.setCellStyle(cellStyleRed);
                }else {
                    cell.setCellStyle(cellStyleGreen);
                }
            }
            CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
            //第二行数据
            Row rowSecond = sheet.createRow(1);
            String[] titleTips={"必填","必填","必填","选填","选填","选填","选填","选填"};
            for (int i=0;i<titleTips.length;i++) {
                Cell cell = rowSecond.createCell(i);
                cell.setCellValue(titleTips[i]);
                cell.setCellStyle(cellStyleGray);
            }
            //第三行数据
            Row rowThird = sheet.createRow(2);
            // 获取主表数据
            PayWorkattendRecord request = this.selectPayWorkattendRecordById(payWorkattendRecord.getWorkattendRecordSid());
            if (request == null){
                throw new BaseException("");
            }
            // 考勤单号
            Cell cell0 = rowThird.createCell(0);
            cell0.setCellValue(request.getWorkattendRecordCode());
            cell0.setCellStyle(defaultCellStyle);
            // 所属年月
            Cell cell1 = rowThird.createCell(1);
            cell1.setCellValue(request.getYearmonth());
            cell1.setCellStyle(defaultCellStyle);
            // 公司
            Cell cell2 = rowThird.createCell(2);
            cell2.setCellValue(request.getCompanyName());
            cell2.setCellStyle(defaultCellStyle);
            // 部门
            Cell cell3 = rowThird.createCell(3);
            cell3.setCellValue(request.getDepartmentName());
            cell3.setCellStyle(defaultCellStyle);
            // 工厂
            Cell cell4 = rowThird.createCell(4);
            cell4.setCellValue(request.getPlantName());
            cell4.setCellStyle(defaultCellStyle);
            // 班组
            Cell cell5 = rowThird.createCell(5);
            cell5.setCellValue(request.getWorkCenterName());
            cell5.setCellStyle(defaultCellStyle);
            // 考勤模板类型
            List<DictData> typeDict = sysDictDataService.selectDictData("s_workattend_record_type");
            Map<String, String> typeMaps = typeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
            Cell cell6 = rowThird.createCell(6);
            cell6.setCellValue(typeMaps.get(request.getWorkattendRecordType()));
            cell6.setCellStyle(defaultCellStyle);
            // 备注
            Cell cell7 = rowThird.createCell(7);
            cell7.setCellValue(request.getRemark());
            cell7.setCellStyle(defaultCellStyle);
            //第四行数据
            Row rowFour = sheet.createRow(3);
            String[] titleItem={"工号*","姓名*","岗位","应出勤天数*","实计薪天数*","实出勤天数*","日常白天加班时数","日常夜晚加班时数","节假期白天加班时数","节假日夜晚加班时数","请调休假天数","请年假天数"
                    ,"请无薪病假天数","请带薪病假天数","请带薪待料假天数","请无薪事假天数","请带薪事假天数","迟到次数","迟到分数","旷工天数","请婚假天数","请产假天数","请陪产假天数","请带薪差旅路途假天数","请丧假天数","备注"};
            for (int i=0;i<titleItem.length;i++) {
                Cell cell = rowFour.createCell(i);
                cell.setCellValue(titleItem[i]);
                if (i < 6){
                    cell.setCellStyle(cellStyleRed);
                    if (i == 2){
                        cell.setCellStyle(cellStyleGreen);
                    }
                }else {
                    cell.setCellStyle(cellStyleGreen);
                }
            }
            //第四行数据
            Row rowFive = sheet.createRow(4);
            String[] titleItemTips={"必填","必填","选填","必填","必填","必填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填"};
            for (int i=0;i<titleItemTips.length;i++) {
                Cell cell = rowFive.createCell(i);
                cell.setCellValue(titleItemTips[i]);
                cell.setCellStyle(cellStyleGray);
            }
            // 明细数据
            List<PayWorkattendRecordItem> itemList = request.getPayWorkattendRecordItemList();
            for (int i=0;i<itemList.size();i++) {
                Row row = sheet.createRow(i+5);
                //工号
                Cell cell01 = row.createCell(0);
                cell01.setCellValue(itemList.get(i).getStaffCode());
                cell01.setCellStyle(defaultCellStyle);
                //姓名
                Cell cell02 = row.createCell(1);
                cell02.setCellValue(itemList.get(i).getStaffName());
                cell02.setCellStyle(defaultCellStyle);
                //岗位
                Cell cell03 = row.createCell(2);
                cell03.setCellValue(itemList.get(i).getPositionName());
                cell03.setCellStyle(defaultCellStyle);
                //应出勤天数
                Cell cell05 = row.createCell(3);
                cell05.setCellValue(itemList.get(i).getYingcq()==null?null:itemList.get(i).getYingcq().toString());
                cell05.setCellStyle(defaultCellStyle);
                //实计薪天数
                Cell cell06 = row.createCell(4);
                cell06.setCellValue(itemList.get(i).getDaix()==null?null:itemList.get(i).getDaix().toString());
                cell06.setCellStyle(defaultCellStyle);
                //实出勤天数
                Cell cell07 = row.createCell(5);
                cell07.setCellValue(itemList.get(i).getShicq()==null?null:itemList.get(i).getShicq().toString());
                cell07.setCellStyle(defaultCellStyle);
                //日常白天加班时数
                Cell cell08 = row.createCell(6);
                cell08.setCellValue(itemList.get(i).getRcbtjb()==null?null:itemList.get(i).getRcbtjb().toString());
                cell08.setCellStyle(defaultCellStyle);
                //日常夜晚加班时数
                Cell cell09 = row.createCell(7);
                cell09.setCellValue(itemList.get(i).getRcywjb()==null?null:itemList.get(i).getRcywjb().toString());
                cell09.setCellStyle(defaultCellStyle);
                //节假期白天加班时数
                Cell cell10 = row.createCell(8);
                cell10.setCellValue(itemList.get(i).getJrbtjb()==null?null:itemList.get(i).getJrbtjb().toString());
                cell10.setCellStyle(defaultCellStyle);
                //节假日夜晚加班时数
                Cell cell11 = row.createCell(9);
                cell11.setCellValue(itemList.get(i).getJrywjb()==null?null:itemList.get(i).getJrywjb().toString());
                cell11.setCellStyle(defaultCellStyle);
                //请调休假天数
                Cell cell12 = row.createCell(10);
                cell12.setCellValue(itemList.get(i).getTiaoxj()==null?null:itemList.get(i).getTiaoxj().toString());
                cell12.setCellStyle(defaultCellStyle);
                //请年假天数
                Cell cell13 = row.createCell(11);
                cell13.setCellValue(itemList.get(i).getNianj()==null?null:itemList.get(i).getNianj().toString());
                cell13.setCellStyle(defaultCellStyle);
                //请无薪病假天数
                Cell cell14 = row.createCell(12);
                cell14.setCellValue(itemList.get(i).getBingjWx()==null?null:itemList.get(i).getBingjWx().toString());
                cell14.setCellStyle(defaultCellStyle);
                //请带薪病假天数
                Cell cell15 = row.createCell(13);
                cell15.setCellValue(itemList.get(i).getBingjDx()==null?null:itemList.get(i).getBingjDx().toString());
                cell15.setCellStyle(defaultCellStyle);
                //请带薪待料假天数
                Cell cell16 = row.createCell(14);
                cell16.setCellValue(itemList.get(i).getDailjDx()==null?null:itemList.get(i).getDailjDx().toString());
                cell16.setCellStyle(defaultCellStyle);
                //请无薪事假天数
                Cell cell17 = row.createCell(15);
                cell17.setCellValue(itemList.get(i).getShijWx()==null?null:itemList.get(i).getShijWx().toString());
                cell17.setCellStyle(defaultCellStyle);
                //请带薪事假天数
                Cell cell18 = row.createCell(16);
                cell18.setCellValue(itemList.get(i).getShijDx()==null?null:itemList.get(i).getShijDx().toString());
                cell18.setCellStyle(defaultCellStyle);
                //迟到次数
                Cell cell19 = row.createCell(17);
                cell19.setCellValue(itemList.get(i).getChidCs()==null?null:itemList.get(i).getChidCs().toString());
                cell19.setCellStyle(defaultCellStyle);
                //迟到分数
                Cell cell20 = row.createCell(18);
                cell20.setCellValue(itemList.get(i).getChidFs()==null?null:itemList.get(i).getChidFs().toString());
                cell20.setCellStyle(defaultCellStyle);
                //矿工天数
                Cell cell21 = row.createCell(19);
                cell21.setCellValue(itemList.get(i).getKuangg()==null?null:itemList.get(i).getKuangg().toString());
                cell21.setCellStyle(defaultCellStyle);
                //请婚假天数
                Cell cell22 = row.createCell(20);
                cell22.setCellValue(itemList.get(i).getHunj()==null?null:itemList.get(i).getHunj().toString());
                cell22.setCellStyle(defaultCellStyle);
                //请产假天数
                Cell cell23 = row.createCell(21);
                cell23.setCellValue(itemList.get(i).getChanj()==null?null:itemList.get(i).getChanj().toString());
                cell23.setCellStyle(defaultCellStyle);
                //请陪产假天数
                Cell cell24 = row.createCell(22);
                cell24.setCellValue(itemList.get(i).getPeicj()==null?null:itemList.get(i).getPeicj().toString());
                cell24.setCellStyle(defaultCellStyle);
                //请带薪差旅路途假天数
                Cell cell25 = row.createCell(23);
                cell25.setCellValue(itemList.get(i).getChailjDx()==null?null:itemList.get(i).getChailjDx().toString());
                cell25.setCellStyle(defaultCellStyle);
                //请丧假天数
                Cell cell26 = row.createCell(24);
                cell26.setCellValue(itemList.get(i).getShangj()==null?null:itemList.get(i).getShangj().toString());
                cell26.setCellStyle(defaultCellStyle);
                //备注
                Cell cell27 = row.createCell(25);
                cell27.setCellValue(itemList.get(i).getRemark()==null?null:itemList.get(i).getRemark().toString());
                cell27.setCellStyle(defaultCellStyle);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importItemData(MultipartFile file, String workattendRecordCode) {
        List<PayWorkattendRecordItem> workattendRecordItemAddList = new ArrayList<>(); // 新增员工记录
        List<PayWorkattendRecordItem> workattendRecordItemUpdateList = new ArrayList<>(); // 变更员工记录
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //
        PayWorkattendRecord workattendRecord = null;
        try {
            workattendRecord = payWorkattendRecordMapper.selectOne(new QueryWrapper<PayWorkattendRecord>()
                    .lambda().eq(PayWorkattendRecord::getWorkattendRecordCode, workattendRecordCode));
        } catch (Exception e) {
            throw new BaseException("考勤记录 " + workattendRecordCode + " 数据出现重复，请联系管理员！");
        }
        if (workattendRecord == null) {
            throw new BaseException("考勤记录 " + workattendRecordCode + " 不存在");
        }
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
            //excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前2行跳过，主要获取明细行
                    continue;
                }
                int num = i + 1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (i < 5) {
                    if (i == 2) {
                        String code = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                        if (!code.equals(workattendRecordCode)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("表格中的考勤单号" + code + "，与系统中的考勤单号不一致，导入失败！");
                            errMsgList.add(errMsg);
                            break;
                        }
                    }
                    continue;
                }
                PayWorkattendRecordItem item = new PayWorkattendRecordItem();
                /*
                 * 工号 （必填）
                 */
                String staffCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long staffSid = null;
                if (staffCode == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasStaff staff = basStaffMapper.selectOne(new QueryWrapper<BasStaff>().lambda().eq(BasStaff::getStaffCode, staffCode));
                        if (staff != null) {
                            staffSid = staff.getStaffSid();
                            try {
                                item = payWorkattendRecordItemMapper.selectOne(new QueryWrapper<PayWorkattendRecordItem>()
                                        .lambda().eq(PayWorkattendRecordItem::getWorkattendRecordSid, workattendRecord.getWorkattendRecordSid())
                                        .eq(PayWorkattendRecordItem::getStaffSid, staffSid));
                                if (item == null || item.getRecordItemSid() == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("工号" + staffCode + "在考勤单"+ workattendRecordCode +"中不存在，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("工号 " + staffCode + " 在该考勤单号里存在多笔记录，请先在系统中处理完成后再重新导入！");
                                errMsgList.add(errMsg);
                            }
                        } else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工号" + staffCode + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("系统中" + staffCode + "员工档案存在重复工号，请先检查该员工，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 姓名 （必填）
                 */
                String staffName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (staffName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("姓名不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                /*
                 * 岗位
                 */
                String positionName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                /*
                 * 应出勤天数 （必填）
                 */
                String yingcq_s = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal yingcq = null;
                if (yingcq_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应出勤天数不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(yingcq_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应出勤天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yingcq = new BigDecimal(yingcq_s);
                        if (yingcq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("应出勤天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 实计薪天数 （必填）
                 */
                String daix_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal daix = null;
                if (daix_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("实计薪天数不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(daix_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("实计薪天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        daix = new BigDecimal(daix_s);
                        if (daix.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("实计薪天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 实出勤天数 （必填）
                 */
                String shicq_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal shicq = null;
                if (shicq_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("实出勤天数不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(shicq_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("实出勤天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        shicq = new BigDecimal(shicq_s);
                        if (shicq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("实出勤天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 日常白天加班时数
                 */
                String rcbtjb_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal rcbtjb = null;
                if (rcbtjb_s != null) {
                    if (!JudgeFormat.isValidDouble(rcbtjb_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("日常白天加班时数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        rcbtjb = new BigDecimal(rcbtjb_s);
                        if (rcbtjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("日常白天加班时数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 日常夜晚加班时数
                 */
                String rcywjb_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal rcywjb = null;
                if (rcywjb_s != null) {
                    if (!JudgeFormat.isValidDouble(rcywjb_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("日常夜晚加班时数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        rcywjb = new BigDecimal(rcywjb_s);
                        if (rcywjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("日常夜晚加班时数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 节假日白天加班时数
                 */
                String jrbtjb_s = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                BigDecimal jrbtjb = null;
                if (jrbtjb_s != null) {
                    if (!JudgeFormat.isValidDouble(jrbtjb_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("节假日白天加班时数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        jrbtjb = new BigDecimal(jrbtjb_s);
                        if (jrbtjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("节假日白天加班时数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 节假日夜晚加班时数
                 */
                String jrywjb_s = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                BigDecimal jrywjb = null;
                if (jrywjb_s != null) {
                    if (!JudgeFormat.isValidDouble(jrywjb_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("节假日夜晚加班时数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        jrywjb = new BigDecimal(jrywjb_s);
                        if (jrywjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("节假日夜晚加班时数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请调休假天数
                 */
                String tiaoxj_s = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal tiaoxj = null;
                if (tiaoxj_s != null) {
                    if (!JudgeFormat.isValidDouble(tiaoxj_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请调休假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        tiaoxj = new BigDecimal(tiaoxj_s);
                        if (tiaoxj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请调休假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请年假天数
                 */
                String nianj_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                BigDecimal nianj = null;
                if (nianj_s != null) {
                    if (!JudgeFormat.isValidDouble(nianj_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请年假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        nianj = new BigDecimal(nianj_s);
                        if (nianj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请年假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请无薪病假天数
                 */
                String bingjWx_s = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                BigDecimal bingjWx = null;
                if (bingjWx_s != null) {
                    if (!JudgeFormat.isValidDouble(bingjWx_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请无薪病假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bingjWx = new BigDecimal(bingjWx_s);
                        if (bingjWx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请无薪病假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请带薪病假天数
                 */
                String bingjDx_s = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                BigDecimal bingjDx = null;
                if (bingjDx_s != null) {
                    if (!JudgeFormat.isValidDouble(bingjDx_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请带薪病假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bingjDx = new BigDecimal(bingjDx_s);
                        if (bingjDx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请带薪病假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请带薪待料假天数
                 */
                String dailjDx_s = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                BigDecimal dailjDx = null;
                if (dailjDx_s != null) {
                    if (!JudgeFormat.isValidDouble(dailjDx_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请带薪待料假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        dailjDx = new BigDecimal(dailjDx_s);
                        if (dailjDx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请带薪待料假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请无薪事假天数
                 */
                String shijWx_s = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                BigDecimal shijWx = null;
                if (shijWx_s != null) {
                    if (!JudgeFormat.isValidDouble(shijWx_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请无薪事假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        shijWx = new BigDecimal(shijWx_s);
                        if (shijWx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请无薪事假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请带薪事假天数
                 */
                String shijDx_s = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                BigDecimal shijDx = null;
                if (shijDx_s != null) {
                    if (!JudgeFormat.isValidDouble(shijDx_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请带薪事假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        shijDx = new BigDecimal(shijDx_s);
                        if (shijDx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请带薪事假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 迟到次数
                 */
                String chidCs_s = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                Integer chidCs = null;
                if (chidCs_s != null) {
                    try {
                        chidCs = Integer.valueOf(chidCs_s);
                        if (chidCs <= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("迟到次数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("迟到次数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 迟到分数
                 */
                String chidFs_s = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
                Integer chidFs = null;
                if (chidFs_s != null) {
                    try {
                        chidFs = Integer.valueOf(chidFs_s);
                        if (chidFs <= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("迟到分数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("迟到分数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 旷工天数
                 */
                String kuangg_s = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                BigDecimal kuangg = null;
                if (kuangg_s != null) {
                    if (!JudgeFormat.isValidDouble(kuangg_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("旷工天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        kuangg = new BigDecimal(kuangg_s);
                        if (kuangg.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("旷工天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请婚嫁天数
                 */
                String hunj_s = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal hunj = null;
                if (hunj_s != null) {
                    if (!JudgeFormat.isValidDouble(hunj_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请婚假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        hunj = new BigDecimal(hunj_s);
                        if (hunj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请婚假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请产假天数
                 */
                String chanj_s = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                BigDecimal chanj = null;
                if (chanj_s != null) {
                    if (!JudgeFormat.isValidDouble(chanj_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请产假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        chanj = new BigDecimal(chanj_s);
                        if (chanj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请产假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请陪产假天数
                 */
                String peicj_s = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                BigDecimal peicj = null;
                if (peicj_s != null) {
                    if (!JudgeFormat.isValidDouble(peicj_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请陪产假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        peicj = new BigDecimal(peicj_s);
                        if (peicj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请陪产假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请带薪差旅路途假天数
                 */
                String chailjDx_s = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                BigDecimal chailjDx = null;
                if (chailjDx_s != null) {
                    if (!JudgeFormat.isValidDouble(chailjDx_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请带薪差旅路途假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        chailjDx = new BigDecimal(chailjDx_s);
                        if (chailjDx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请带薪差旅路途假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 请丧假天数
                 */
                String shangj_s = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                BigDecimal shangj = null;
                if (shangj_s != null) {
                    if (!JudgeFormat.isValidDouble(shangj_s, 2, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请丧假天数，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        shangj = new BigDecimal(shangj_s);
                        if (shangj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("请丧假天数，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                if (remark != null && remark.length() > 600) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList) && staffSid != null) {
                    item.setYingcq(yingcq).setDaix(daix).setShicq(shicq).setRcbtjb(rcbtjb).setRcywjb(rcywjb).setJrbtjb(jrbtjb).setJrywjb(jrywjb)
                            .setTiaoxj(tiaoxj).setNianj(nianj).setBingjDx(bingjDx).setBingjWx(bingjWx).setDailjDx(dailjDx).setShijDx(shijDx).setShijWx(shijWx)
                            .setChidCs(chidCs==null?null:chidCs.toString()).setChidFs(chidFs==null?null:chidFs.toString()).setKuangg(kuangg).setHunj(hunj).setChanj(chanj).setPeicj(peicj)
                            .setChailjDx(chailjDx).setShangj(shangj).setRemark(remark);
                    if (item.getRecordItemSid() != null) {
                        workattendRecordItemUpdateList.add(item);
                    } else {
                        // 新加员工
//                            item.setWorkattendRecordSid(workattendRecord.getWorkattendRecordSid());
//                            item.setStaffSid(staffSid);
//                            workattendRecordItemAddList.add(item);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(errMsgList)) {
                return errMsgList;
            } else {
                int row = 0;
                if (CollectionUtil.isNotEmpty(workattendRecordItemUpdateList)) {
                    workattendRecordItemUpdateList.forEach(item -> {
                        payWorkattendRecordItemMapper.updateAllById(item);
                    });
                    row = row + workattendRecordItemUpdateList.size();
                }
                if (CollectionUtil.isNotEmpty(workattendRecordItemAddList)) {
                    row = row + payWorkattendRecordItemMapper.inserts(workattendRecordItemAddList);
                }

                return row;
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
    }

    /**
     *考勤 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importDataM(MultipartFile file){
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            Long companySid=null;
            Long plantSid=null;
            String errMsg="";
            String yearmonth=null;
            int warmSize=0;
            HashMap<String, String> map = new HashMap<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            CommonErrMsgResponse warnMsgResponse = null;
            List<CommonErrMsgResponse> warnList = new ArrayList<>();
            HashSet<String> nameSet = new HashSet<>();
            int size = readAll.size();
            if(readAll.size()<7){
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg("明细行不能为空，导入失败");
                msgList.add(errMsgResponse);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return EmsResultEntity.error(msgList);
            }
            PayWorkattendRecord payWorkattendRecord = new PayWorkattendRecord();
            List<PayWorkattendRecordItem> items = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                 Long staffSid=null;
                BigDecimal yingcq=null;
                BigDecimal shicq=null;
                BigDecimal kuangg=null;
                BigDecimal qitj=null;
                Long quek=null;
                String chid=null;
                Long zaot=null;
                BigDecimal pings=null;
                BigDecimal zhoum=null;
                BigDecimal jiej=null;
                BigDecimal shenye=null;
                BigDecimal qit=null;
                BigDecimal shij=null;
                BigDecimal tiaoxiu=null;
                BigDecimal nianj=null;
                BigDecimal bingj=null;
                BigDecimal hunj=null;
                BigDecimal chanj=null;
                BigDecimal sangj=null;
                BigDecimal dail=null;
                Long defaultPlantSid=null;
                Long defaultCompanySid=null;
                Long butie=null;
                int num=i+1;
                if (i < 2 || i==3||i==4||i==5) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copyHead(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("所属年月，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!JudgeFormat.isYearMonth(objects.get(0).toString())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("所属年月，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String time = objects.get(0).toString();
                            String[]  timeArr= time.split("-|/");
                            yearmonth=timeArr[0]+"-"+timeArr[1];
                        }
                    }

                    if (objects.get(1) == null || objects.get(1) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("公司，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                                .eq(BasCompany::getShortName, objects.get(1).toString())
                        );
                        if(company==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("不存在简称为"+objects.get(1).toString()+"的公司，导入失败!");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus())
                                    ||!ConstantsEms.ENABLE_STATUS.equals(company.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("公司必须为确认且启用状态，导入失败！");
                                msgList.add(errMsgResponse);
                            }else{
                                companySid=company.getCompanySid();
                            }
                        }
                    }

                    if(objects.get(2) != null && objects.get(2) != ""){
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
                    payWorkattendRecord.setCompanySid(companySid)
                            .setPlantSid(plantSid)
                            .setCreateDate(new Date())
                            .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setYearmonth(yearmonth)
                            .setRemark(objects.get(3)==""||objects.get(3)==null?null:objects.get(3).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS);
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("姓名，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    List<BasStaff> basStaffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                            .eq(BasStaff::getStaffName, objects.get(0))
                    );
                    if(!nameSet.add(objects.get(0).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("表格中，姓名"+objects.get(0).toString()+"存在重复");
                        msgList.add(errMsgResponse);
                    }
                    if(CollectionUtil.isEmpty(basStaffList)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("不存在姓名为"+objects.get(0).toString()+"的员工，导入失败!");
                        msgList.add(errMsgResponse);
                    }else{
                        List<BasStaff> BasStaffItems = basStaffList.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())
                                && ConstantsEms.ENABLE_STATUS.equals(li.getStatus())&&ConstantsEms.IS_ON_JOB_ZZ.equals(li.getIsOnJob())
                        ).collect(Collectors.toList());
                        if(CollectionUtil.isNotEmpty(BasStaffItems)){
                            BasStaff basStaff =null;
                            if(companySid!=null&&plantSid==null){
                               Long companySidTemp=companySid;
                                List<BasStaff> staffs = BasStaffItems.stream().filter(li -> companySidTemp.toString().equals(li.getDefaultCompanySid().toString())).collect(Collectors.toList());
                                if(CollectionUtil.isNotEmpty(staffs)){
                                    basStaff=staffs.get(0);
                                }else{
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("该“公司+工厂”组合，不存在员工"+objects.get(0).toString()+"，请检查！");
                                    msgList.add(errMsgResponse);
                                }
                            }else if(companySid!=null&&plantSid!=null){
                                Long companySidTemp=companySid;
                                Long plantSidTemp=plantSid;
                                List<BasStaff> staffs = BasStaffItems.stream().filter(li->li.getDefaultPlantSid()!=null).filter(li -> companySidTemp.toString().equals(li.getDefaultCompanySid().toString())
                                        &&plantSidTemp.toString().equals(li.getDefaultPlantSid().toString())
                                ).collect(Collectors.toList());
                                if(CollectionUtil.isNotEmpty(staffs)){
                                    basStaff=staffs.get(0);
                                }else{
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(num);
                                    errMsgResponse.setMsg("该“公司+工厂”组合，不存在员工"+objects.get(0).toString()+"，请检查！");
                                    msgList.add(errMsgResponse);
                                }
                            }
                            if(basStaff==null){
                                basStaff = BasStaffItems.get(0);
                            }
                            staffSid=basStaff.getStaffSid();
                            defaultPlantSid=basStaff.getDefaultPlantSid();
                            defaultCompanySid=basStaff.getDefaultCompanySid();
                            if(plantSid==null){
                                defaultPlantSid=null;
                            }
                            if(basStaffList.size()>1){
                                warmSize=warmSize+1;
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("系统存在多个姓名("+objects.get(0).toString()+")的员工档案，本次导入的是编号"+basStaff.getStaffCode()+"的员工");
                                warnList.add(errMsgResponse);
                            }

                        }else{
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("姓名对应的员工必须为确认且启用状态的在职员工，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("应出勤(天)，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                   if(!JudgeFormat.isValidDouble(objects.get(1).toString(),2,1)){
                       CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                       errMsgResponse.setItemNum(num);
                       errMsgResponse.setMsg("应出勤(天)，数据格式错误，导入失败");
                       msgList.add(errMsgResponse);
                   }else{
                       yingcq=new BigDecimal(objects.get(1).toString());
                       if(yingcq.compareTo(BigDecimal.ZERO)==-1){
                           CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                           errMsgResponse.setItemNum(num);
                           errMsgResponse.setMsg("应出勤(天)，数据格式错误，导入失败");
                           msgList.add(errMsgResponse);
                       }
                   }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("实出勤(天)，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(2).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("实出勤(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        shicq=new BigDecimal(objects.get(2).toString());
                        if(shicq.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("实出勤(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(3) == null || objects.get(3) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(3).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("旷工(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        kuangg=new BigDecimal(objects.get(3).toString());
                        if(kuangg.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("旷工(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(4) == null || objects.get(4) == "") {

                }else{
                    if(!JudgeFormat.isValidInt(objects.get(4).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("缺卡(次)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        quek=Long.valueOf(objects.get(4).toString());
                        if(quek<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("缺卡(次)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {

                }else{
                    if(!JudgeFormat.isValidInt(objects.get(5).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("迟到(次)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        chid=objects.get(5).toString();
                        if(Long.valueOf(objects.get(5).toString())<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("迟到(次)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(6) == null || objects.get(6) == "") {

                }else{
                    if(!JudgeFormat.isValidInt(objects.get(6).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("早退(次)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        zaot=Long.valueOf(objects.get(6).toString());
                        if(zaot<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("早退(次)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(7) == null || objects.get(7) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(7).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("平时(时)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        pings=new BigDecimal(objects.get(7).toString());
                        if(pings.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("平时(时)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(8) == null || objects.get(8) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(8).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("周末(时)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        zhoum=new BigDecimal(objects.get(8).toString());
                        if(zhoum.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("周末(时)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }


                if (objects.get(9) == null || objects.get(9) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(9).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("节假日(时)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        jiej=new BigDecimal(objects.get(9).toString());
                        if(jiej.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("节假日(时)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(10) == null || objects.get(10) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(10).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("深夜(时)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        shenye=new BigDecimal(objects.get(10).toString());
                        if(shenye.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("深夜(时)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(11) == null || objects.get(11) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(11).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("其它(时)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        qit=new BigDecimal(objects.get(11).toString());
                        if(qit.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("其它(时)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(12) == null || objects.get(12) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(12).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("事假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        shij=new BigDecimal(objects.get(12).toString());
                        if(shij.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("事假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(13) == null || objects.get(13) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(13).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("调休(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        tiaoxiu=new BigDecimal(objects.get(13).toString());
                        if(tiaoxiu.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("调休(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(14) == null || objects.get(14) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(14).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("年假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        nianj=new BigDecimal(objects.get(14).toString());
                        if(nianj.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("年假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(15) == null || objects.get(15) == "") {
                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(15).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("病假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        bingj=new BigDecimal(objects.get(15).toString());
                        if(bingj.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("病假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(16) == null || objects.get(16) == "") {
                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(16).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("婚假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        hunj=new BigDecimal(objects.get(16).toString());
                        if(hunj.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("婚假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(17) == null || objects.get(17) == "") {

                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(17).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("产假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        chanj=new BigDecimal(objects.get(17).toString());
                        if(chanj.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("产假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(18) == null || objects.get(18) == "") {
                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(18).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("丧假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        sangj=new BigDecimal(objects.get(18).toString());
                        if(sangj.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("丧假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(19) == null || objects.get(19) == "") {
                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(19).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("待料天数(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        dail=new BigDecimal(objects.get(19).toString());
                        if(dail.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("待料天数(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(20) == null || objects.get(20) == "") {
                }else{
                    if(!JudgeFormat.isValidDouble(objects.get(20).toString(),2,1)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("其它假(天)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        qitj=new BigDecimal(objects.get(20).toString());
                        if(qitj.compareTo(BigDecimal.ZERO)==-1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("其它假(天)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }

                if (objects.get(21) == null || objects.get(21) == "") {
                }else{
                    if(!JudgeFormat.isValidInt(objects.get(21).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("晚餐补贴(次)，数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        butie=Long.valueOf(objects.get(21).toString());
                        if(butie<0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("晚餐补贴(次)，数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                PayWorkattendRecordItem payWorkattendRecordItem = new PayWorkattendRecordItem();
                payWorkattendRecordItem
                        .setStaffSid(staffSid)
                        .setKuangg(kuangg)
                        .setQuekCs(quek)
                        .setDefaultPlantSid(defaultPlantSid)
                        .setDefaultCompanySid(defaultCompanySid)
                        .setChidCs(chid)
                        .setZaotCs(zaot)
                        .setPingsjb(pings)
                        .setZhoumjb(zhoum)
                        .setJiejrjb(jiej)
                        .setShenyjb(shenye)
                        .setQitjb(qit)
                        .setShijWx(shij)
                        .setYingcq(yingcq)
                        .setShicq(shicq)
                        .setTiaoxj(tiaoxiu)
                        .setNianj(nianj)
                        .setShangj(sangj)
                        .setBingjDx(bingj)
                        .setHunj(hunj)
                        .setChanj(chanj)
                        .setShicq(shicq)
                        .setDailjDx(dail)
                        .setQitj(qitj)
                        .setWancbtCs(butie)
                        .setRemark(objects.get(22)==""||objects.get(22)==null?null:objects.get(22).toString());
                items.add(payWorkattendRecordItem);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return EmsResultEntity.error(msgList);
            }
            try{
                payWorkattendRecord.setPayWorkattendRecordItemList(items);
                payWorkattendRecord.setImportType(BusinessType.IMPORT.getValue());
                insertPayWorkattendRecord(payWorkattendRecord);
            }catch (BaseException e)
            {
                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                errMsgResponse.setMsg(e.getDefaultMessage());
                msgList.add(errMsgResponse);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return EmsResultEntity.error(msgList);
            }
            if (CollectionUtil.isNotEmpty(warnList)){
                String  msg = "导入成功" + items.size() + "条，系统中员工姓名存在重复" + warmSize + "条";
                return EmsResultEntity.success(items.size(), warnList, msg);
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(1);
    }

    //填充
    public void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第四行的列数
        int size = readAll.get(3).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }


    //填充-主表
    public void copyHead(List<Object> objects,List<List<Object>> readAll){
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
    //填充-明细表
    public void copyItem(List<Object> objects,List<List<Object>> readAll){
        //获取第6行的列数
        int size = readAll.get(5).size();
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
