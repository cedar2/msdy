package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPaySalaryBillService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysRoleMenu;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工资单-主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Service
@SuppressWarnings("all")
public class PaySalaryBillServiceImpl extends ServiceImpl<PaySalaryBillMapper, PaySalaryBill> implements IPaySalaryBillService {
    @Autowired
    private PaySalaryBillMapper paySalaryBillMapper;
    @Autowired
    private PaySalaryBillItemMapper paySalaryBillItemMapper;
    @Autowired
    private PaySalaryBillAttachMapper paySalaryBillAttachMapper;
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
    private PayProcessStepCompleteItemMapper payProcessStepCompleteItemMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工资单-主";

    /**
     * 查询工资单-主
     *
     * @param salaryBillSid 工资单-主ID
     * @return 工资单-主
     */
    @Override
    public PaySalaryBill selectPaySalaryBillById(Long salaryBillSid) {
        PaySalaryBill paySalaryBill = paySalaryBillMapper.selectPaySalaryBillById(salaryBillSid);
        if (paySalaryBill == null) {
            return null;
        }
        List<PaySalaryBillItem> paySalaryBillItemList =
                paySalaryBillItemMapper.selectPaySalaryBillItemList(new PaySalaryBillItem().setSalaryBillSid(salaryBillSid));
        if(CollectionUtil.isNotEmpty(paySalaryBillItemList)){
             BigDecimal yingfuCount = paySalaryBillItemList.stream().filter(li -> li.getYingfPayroll() != null).map(li -> li.getYingfPayroll()).reduce(BigDecimal.ZERO, BigDecimal::add);
             BigDecimal netPayrollCount = paySalaryBillItemList.stream().filter(li -> li.getNetPayroll() != null).map(li -> li.getNetPayroll()).reduce(BigDecimal.ZERO, BigDecimal::add);
             paySalaryBill.setNetPayrollCount(netPayrollCount)
                    .setYingfPayrollCount(yingfuCount);
        }
        List<PaySalaryBillAttach> paySalaryBillAttachList =
                paySalaryBillAttachMapper.selectPaySalaryBillAttachList(new PaySalaryBillAttach().setSalaryBillSid(salaryBillSid));

        MongodbUtil.find(paySalaryBill);
        paySalaryBill.setPaySalaryBillItemList(paySalaryBillItemList);
        paySalaryBill.setPaySalaryBillAttachList(paySalaryBillAttachList);
        return paySalaryBill;
    }

    /**
     * 查询工资单-主列表
     *
     * @param paySalaryBill 工资单-主
     * @return 工资单-主
     */
    @Override
    public List<PaySalaryBill> selectPaySalaryBillList(PaySalaryBill paySalaryBill) {
        return paySalaryBillMapper.selectPaySalaryBillList(paySalaryBill);
    }

    /**
     * 新增工资单-主
     * 需要注意编码重复校验
     *
     * @param paySalaryBill 工资单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPaySalaryBill(PaySalaryBill paySalaryBill) {
        scene0(paySalaryBill);
        setConfirmInfo(paySalaryBill);
        setCompanyCodeAndDepartmentCode(paySalaryBill);
        int row = paySalaryBillMapper.insert(paySalaryBill);
        if (row > 0) {
            //工资单-明细对象
            List<PaySalaryBillItem> paySalaryBillItemList = paySalaryBill.getPaySalaryBillItemList();
            if (CollectionUtil.isNotEmpty(paySalaryBillItemList)) {
                addPaySalaryBillItem(paySalaryBill, paySalaryBillItemList);
            }
            //工资单-附件对象
            List<PaySalaryBillAttach> paySalaryBillAttachList = paySalaryBill.getPaySalaryBillAttachList();
            if (CollectionUtil.isNotEmpty(paySalaryBillAttachList)) {
                addPaySalaryBillAttach(paySalaryBill, paySalaryBillAttachList);
            }
            PaySalaryBill salaryBill = paySalaryBillMapper.selectPaySalaryBillById(paySalaryBill.getSalaryBillSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(paySalaryBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PAY_SALARY_BILL)
                        .setDocumentSid(paySalaryBill.getSalaryBillSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("工资单" + salaryBill.getSalaryBillCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(salaryBill.getSalaryBillCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(paySalaryBill.getSalaryBillSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    private void scene0(PaySalaryBill paySalaryBill) {
        QueryWrapper<PaySalaryBill> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(PaySalaryBill::getCompanySid,paySalaryBill.getCompanySid())
                .eq(PaySalaryBill::getYearmonth, paySalaryBill.getYearmonth());
        // 编辑排除自身
        if (paySalaryBill.getSalaryBillSid() != null){
            queryWrapper.lambda().ne(PaySalaryBill::getSalaryBillSid,paySalaryBill.getSalaryBillSid());
        }
        if (paySalaryBill.getPlantSid() == null) {
            queryWrapper.lambda().isNull(PaySalaryBill::getPlantSid);
            if (CollectionUtil.isNotEmpty(paySalaryBillMapper.selectList(queryWrapper))){
                throw new BaseException("该公司在当前所属年月已建立工资单，请核实");
            }
        }
        else {
            queryWrapper.lambda().eq(PaySalaryBill::getPlantSid,paySalaryBill.getPlantSid());
            if (CollectionUtil.isNotEmpty(paySalaryBillMapper.selectList(queryWrapper))){
                throw new BaseException("该公司下此工厂在当前所属年月已建立工资单，请核实");
            }
        }
    }

    /**
     * 校验是否存在待办
     */
    private void deleteTodo(PaySalaryBill paySalaryBill) {
        if (!ConstantsEms.SAVA_STATUS.equals(paySalaryBill.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getDocumentSid, paySalaryBill.getSalaryBillSid()));
        }
    }

    /**
     * 设置公司编码
     */
    private void setCompanyCodeAndDepartmentCode(PaySalaryBill paySalaryBill) {
        if (paySalaryBill.getCompanySid() != null) {
            BasCompany basCompany = basCompanyMapper.selectBasCompanyById(paySalaryBill.getCompanySid());
            paySalaryBill.setCompanyCode(basCompany.getCompanyCode());
        }
        if (paySalaryBill.getDepartmentSid() != null) {
            BasDepartment basDepartment = basDepartmentMapper.selectBasDepartmentById(paySalaryBill.getDepartmentSid());
            paySalaryBill.setDepartmentCode(basDepartment.getDepartmentCode());
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PaySalaryBill o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 工资单-明细对象
     */
    private void addPaySalaryBillItem(PaySalaryBill paySalaryBill, List<PaySalaryBillItem> paySalaryBillItemList) {
//        deleteItem(paySalaryBill);
        paySalaryBillItemList.forEach(o -> {
            o.setSalaryBillSid(paySalaryBill.getSalaryBillSid());
        });
        paySalaryBillItemMapper.inserts(paySalaryBillItemList);
    }

    private void deleteItem(PaySalaryBill paySalaryBill) {
        paySalaryBillItemMapper.delete(
                new UpdateWrapper<PaySalaryBillItem>()
                        .lambda()
                        .eq(PaySalaryBillItem::getSalaryBillSid, paySalaryBill.getSalaryBillSid())
        );
    }

    /**
     * 工资单-附件对象
     */
    private void addPaySalaryBillAttach(PaySalaryBill paySalaryBill, List<PaySalaryBillAttach> paySalaryBillAttachList) {
//        deleteAttach(paySalaryBill);
        paySalaryBillAttachList.forEach(o -> {
            o.setSalaryBillSid(paySalaryBill.getSalaryBillSid());
        });
        paySalaryBillAttachMapper.inserts(paySalaryBillAttachList);
    }

    private void deleteAttach(PaySalaryBill paySalaryBill) {
        paySalaryBillAttachMapper.delete(
                new UpdateWrapper<PaySalaryBillAttach>()
                        .lambda()
                        .eq(PaySalaryBillAttach::getSalaryBillSid, paySalaryBill.getSalaryBillSid())
        );
    }

    /**
     * 修改工资单-主
     *
     * @param paySalaryBill 工资单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePaySalaryBill(PaySalaryBill paySalaryBill) {
        scene0(paySalaryBill);
        setConfirmInfo(paySalaryBill);
        setCompanyCodeAndDepartmentCode(paySalaryBill);
        PaySalaryBill response = paySalaryBillMapper.selectPaySalaryBillById(paySalaryBill.getSalaryBillSid());
        paySalaryBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = paySalaryBillMapper.updateAllById(paySalaryBill);
        if (row > 0) {
            //工资单-明细对象
            List<PaySalaryBillItem> billItemList = paySalaryBill.getPaySalaryBillItemList();
            operateItem(paySalaryBill, billItemList);
            //工资单-附件对象
            List<PaySalaryBillAttach> billAttachList = paySalaryBill.getPaySalaryBillAttachList();
            operateAttachment(paySalaryBill, billAttachList);
            deleteTodo(paySalaryBill);
            //插入日志
            MongodbUtil.insertUserLog(paySalaryBill.getSalaryBillSid(), BusinessType.UPDATE.getValue(), response, paySalaryBill, TITLE);
        }
        return row;
    }

    /**
     * 工资单-明细
     */
    private void operateItem(PaySalaryBill paySalaryBill, List<PaySalaryBillItem> billItemList) {
        if (CollectionUtil.isNotEmpty(billItemList)) {
            //新增
            List<PaySalaryBillItem> addList = billItemList.stream().filter(o -> o.getBillItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPaySalaryBillItem(paySalaryBill, addList);
            }
            //编辑
            List<PaySalaryBillItem> editList = billItemList.stream().filter(o -> o.getBillItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    paySalaryBillItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PaySalaryBillItem> itemList = paySalaryBillItemMapper.selectList(new QueryWrapper<PaySalaryBillItem>().lambda()
                    .eq(PaySalaryBillItem::getSalaryBillSid, paySalaryBill.getSalaryBillSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PaySalaryBillItem::getBillItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = billItemList.stream().map(PaySalaryBillItem::getBillItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                paySalaryBillItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(paySalaryBill);
        }
    }

    /**
     * 工资单-附件
     */
    private void operateAttachment(PaySalaryBill paySalaryBill, List<PaySalaryBillAttach> billAttachList) {
        if (CollectionUtil.isNotEmpty(billAttachList)) {
            //新增
            List<PaySalaryBillAttach> addList = billAttachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPaySalaryBillAttach(paySalaryBill, addList);
            }
            //编辑
            List<PaySalaryBillAttach> editList = billAttachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    paySalaryBillAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PaySalaryBillAttach> itemList =
                    paySalaryBillAttachMapper.selectList(new QueryWrapper<PaySalaryBillAttach>().lambda()
                            .eq(PaySalaryBillAttach::getSalaryBillSid, paySalaryBill.getSalaryBillSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PaySalaryBillAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = billAttachList.stream().map(PaySalaryBillAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                paySalaryBillAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(paySalaryBill);
        }
    }

    /**
     * 变更工资单-主
     *
     * @param paySalaryBill 工资单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePaySalaryBill(PaySalaryBill paySalaryBill) {
        scene0(paySalaryBill);
        setConfirmInfo(paySalaryBill);
        setCompanyCodeAndDepartmentCode(paySalaryBill);
        PaySalaryBill response = paySalaryBillMapper.selectPaySalaryBillById(paySalaryBill.getSalaryBillSid());
        paySalaryBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = paySalaryBillMapper.updateAllById(paySalaryBill);
        if (row > 0) {
            //工资单-明细对象
            List<PaySalaryBillItem> billItemList = paySalaryBill.getPaySalaryBillItemList();
            operateItem(paySalaryBill, billItemList);
            //工资单-附件对象
            List<PaySalaryBillAttach> billAttachList = paySalaryBill.getPaySalaryBillAttachList();
            operateAttachment(paySalaryBill, billAttachList);
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("工资单" + paySalaryBill.getSalaryBillCode() + "已更新")
                    .setDocumentSid(paySalaryBill.getSalaryBillSid())
                    .setDocumentCode(paySalaryBill.getSalaryBillCode())
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
            //插入日志
            MongodbUtil.insertUserLog(paySalaryBill.getSalaryBillSid(), BusinessType.CHANGE.getValue(), response, paySalaryBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工资单-主
     *
     * @param salaryBillSids 需要删除的工资单-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePaySalaryBillByIds(List<Long> salaryBillSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = paySalaryBillMapper.selectCount(new UpdateWrapper<PaySalaryBill>().lambda()
                .in(PaySalaryBill::getHandleStatus, handleStatusList)
                .in(PaySalaryBill::getSalaryBillSid, salaryBillSids));
        if (count != salaryBillSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        //删除工资单-明细对象
        paySalaryBillItemMapper.delete(new UpdateWrapper<PaySalaryBillItem>().lambda().in(PaySalaryBillItem::getSalaryBillSid, salaryBillSids));
        //删除工资单-附件对象
        paySalaryBillAttachMapper.delete(new UpdateWrapper<PaySalaryBillAttach>().lambda().in(PaySalaryBillAttach::getSalaryBillSid, salaryBillSids));
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,salaryBillSids));
        return paySalaryBillMapper.deleteBatchIds(salaryBillSids);
    }

    /**
     * 更改确认状态
     *
     * @param paySalaryBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PaySalaryBill paySalaryBill) {
        int row = 0;
        Long[] sids = paySalaryBill.getSalaryBillSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            row = paySalaryBillMapper.update(null, new UpdateWrapper<PaySalaryBill>().lambda()
                    .set(PaySalaryBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PaySalaryBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(PaySalaryBill::getConfirmDate, new Date())
                    .in(PaySalaryBill::getSalaryBillSid, sids));
        }
        return row;
    }

    /**
     * 计件工资(自动)
     */
    @Override
    public PaySalaryBillItem getPieceworkSalary(PayProcessStepCompleteItem payProcessStepCompleteItem) {
        List<PayProcessStepCompleteItem> completeItemList =
                payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(payProcessStepCompleteItem);
        List<BigDecimal> priceList = new ArrayList<>();
        PaySalaryBillItem paySalaryBillItem = new PaySalaryBillItem();
        if (CollectionUtil.isNotEmpty(completeItemList)) {
            List<PayProcessStepCompleteItem> distinctList =
                    completeItemList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(o -> o.getProductSid() + ";" + o.getProcessStepItemSid()))), ArrayList::new));
            for (PayProcessStepCompleteItem item : distinctList) {
                List<PayProcessStepCompleteItem> resultList =
                        completeItemList.stream().filter(o -> o.getProductSid().equals(item.getProductSid()) &&
                                o.getProcessStepItemSid().equals(item.getProcessStepItemSid())).collect(Collectors.toList());
                //当月完工量
                BigDecimal completeQuantitySum = resultList.stream().map(PayProcessStepCompleteItem::getCompleteQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
                //获取'商品编码，道序'对应的工价*道序倍率
                List<PayProductProcessStepItem> stepItemList =
                        payProductProcessStepItemMapper.selectList(new QueryWrapper<PayProductProcessStepItem>().lambda()
//                                .eq(PayProductProcessStepItem::getProductSid, item.getProductSid())
                                .eq(PayProductProcessStepItem::getStepItemSid, item.getProcessStepItemSid()));
                if (CollectionUtil.isNotEmpty(stepItemList)) {
                    for (PayProductProcessStepItem stepItem : stepItemList) {
                        BigDecimal price = stepItem.getPrice().multiply(stepItem.getPriceRate());
                        //当月完工量*工价
                        BigDecimal priceSum = completeQuantitySum.multiply(price);
                        priceList.add(priceSum);
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(priceList)) {
            //计件工资(自动)
            BigDecimal wagePieceSys = priceList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            paySalaryBillItem.setWagePieceSys(wagePieceSys);
        }
        return paySalaryBillItem;
    }

    /**
     * 工资单明细校验
     */
    @Override
    public PaySalaryBill verifyItem(PaySalaryBill paySalaryBill) {
        if (null == paySalaryBill) {
            return null;
        }
        List<String> verifyList = new ArrayList<>();
        List<String> salaryBillCodeList = new ArrayList<>();
        List<String> staffCodeList = new ArrayList<>();
        List<PaySalaryBillItem> itemList = paySalaryBill.getPaySalaryBillItemList();
        if (CollectionUtil.isNotEmpty(itemList)) {
            PaySalaryBillItem paySalaryBillItem = new PaySalaryBillItem();
            //根据当前年月+公司+员工查询是否存在重复工资单
            paySalaryBillItem.setYearmonth(paySalaryBill.getYearmonth()).setCompanySid(paySalaryBill.getCompanySid());
            for (PaySalaryBillItem item : itemList) {
                paySalaryBillItem.setStaffSid(item.getStaffSid());
                List<PaySalaryBillItem> billItemList = paySalaryBillItemMapper.selectPaySalaryBillItemList(paySalaryBillItem);
                if (CollectionUtil.isNotEmpty(billItemList)) {
                    PaySalaryBillItem billItem = billItemList.get(0);
                    if (!billItem.getBillItemSid().equals(item.getBillItemSid())) {
                        verifyList.add(ConstantsEms.YES);
                        salaryBillCodeList.add(billItem.getSalaryBillCode());
                        staffCodeList.add(billItem.getStaffCode());
                    } else {
                        verifyList.add(ConstantsEms.NO);
                    }
                } else {
                    verifyList.add(ConstantsEms.NO);
                }
            }
        } else {
            verifyList.add(ConstantsEms.NO);
        }
        PaySalaryBill resust = new PaySalaryBill();
        List<String> verifyResult = verifyList.stream().filter(o -> ConstantsEms.YES.equals(o)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(verifyResult)) {
            resust.setVerify(ConstantsEms.YES);
        } else {
            resust.setVerify(ConstantsEms.NO);
        }
        if (CollectionUtil.isNotEmpty(salaryBillCodeList) && CollectionUtil.isNotEmpty(staffCodeList)) {
            resust.setVerifyHint("当前所属年月，工资单号" + salaryBillCodeList.toString() + "中已存在工号" + staffCodeList.toString() + "的工资清单，是否继续操作？");
        }
        return resust;
    }

    /**
     * 单据提交校验
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verify(PaySalaryBill paySalaryBill) {
        if (null == paySalaryBill.getSalaryBillSid() || StrUtil.isBlank(paySalaryBill.getHandleStatus())) {
            throw new CustomException("参数错误");
        }
        PaySalaryBill salaryBill = selectPaySalaryBillById(paySalaryBill.getSalaryBillSid());
        if (CollectionUtil.isEmpty(salaryBill.getPaySalaryBillItemList())) {
            throw new CustomException("工资单号" + salaryBill.getSalaryBillCode() + ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
        }
        return paySalaryBillMapper.updateById(paySalaryBill);
    }

    @Override
    public void exportItemBySalary(HttpServletResponse response, PaySalaryBill paySalaryBill) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("工资清单");
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
            String[] titles = {"工资单号*","所属年月*","公司*","部门","工厂","班组","工资模板类型","备注"};
            //第一行数据
            Row rowHead = sheet.createRow(0);
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
            PaySalaryBill request = this.selectPaySalaryBillById(paySalaryBill.getSalaryBillSid());
            if (request == null){
                throw new BaseException("");
            }
            // 考勤单号
            Cell cell0 = rowThird.createCell(0);
            cell0.setCellValue(request.getSalaryBillCode());
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
            // 工资模板类型
            List<DictData> typeDict = sysDictDataService.selectDictData("s_salary_bill_type");
            Map<String, String> typeMaps = typeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
            Cell cell6 = rowThird.createCell(6);
            cell6.setCellValue(typeMaps.get(request.getSalaryBillType()));
            cell6.setCellStyle(defaultCellStyle);
            // 备注
            Cell cell7 = rowThird.createCell(7);
            cell7.setCellValue(request.getRemark());
            cell7.setCellStyle(defaultCellStyle);
            //第四行数据
            Row rowFour = sheet.createRow(3);
            String[] titleItem={"工号*","姓名*","部门","岗位","入职时间","基本工资*","责任工资","计件工资","计时工资","返修工资","其它工资"
                    ,"加班时间(时)","加班工资","通宵工资","其它工资"
                    ,"月绩效奖","月度满勤奖","年度满勤奖","机台保养奖","服从奖","抽成","计件奖","人才介绍奖","其它奖金"
                    ,"住房补贴","餐费补贴","话费补贴","交通补贴","差旅补贴","岗位津贴","稳岗补贴","特殊工种补贴","餐具补贴","计件保底补贴","其它补贴"
                    ,"水电费扣款","迟到/缺卡扣款","餐具扣款","短少扣款","品质扣款","5S检查扣款","其它扣款"
                    ,"应发工资*"
                    ,"代缴(公积金)","代缴(医保)","代缴(社保)","代缴(个税)","其它代缴"
                    ,"实发工资*","备注","工资成本分摊"};
            for (int i=0;i<titleItem.length;i++) {
                Cell cell = rowFour.createCell(i);
                cell.setCellValue(titleItem[i]);
                if (i < 5){
                    cell.setCellStyle(cellStyleRed);
                    if (i==2){
                        cell.setCellStyle(cellStyleGreen);
                    }
                }else {
                    cell.setCellStyle(cellStyleGreen);
                }
            }
            //第四行数据
            Row rowFive = sheet.createRow(4);
            String[] titleItemTips={
                     "必填","必填","选填","选填","选填","必填","选填","选填","选填","选填","选填"
                    ,"选填","选填","选填","选填"
                    ,"选填","选填","选填","选填","选填","选填","选填","选填","选填"
                    ,"选填","选填","选填","选填","选填","选填","选填","选填","选填","选填","选填"
                    ,"选填","选填","选填","选填","选填","选填","选填"
                    ,"必填"
                    ,"选填","选填","选填","选填","选填"
                    ,"必填","选填","选填"};
            for (int i=0;i<titleItemTips.length;i++) {
                Cell cell = rowFive.createCell(i);
                cell.setCellValue(titleItemTips[i]);
                cell.setCellStyle(cellStyleGray);
            }
            // 明细数据
            List<PaySalaryBillItem> itemList = request.getPaySalaryBillItemList();
            for (int i=0;i<itemList.size();i++) {
                Row row = sheet.createRow(i+5);
                //工号
                Cell cell00 = row.createCell(0);
                cell00.setCellValue(itemList.get(i).getStaffCode());
                cell00.setCellStyle(defaultCellStyle);
                //姓名
                Cell cell01 = row.createCell(1);
                cell01.setCellValue(itemList.get(i).getStaffName());
                cell01.setCellStyle(defaultCellStyle);
                //部门
                Cell cell02 = row.createCell(2);
                cell02.setCellValue(itemList.get(i).getDepartmentName());
                cell02.setCellStyle(defaultCellStyle);
                //岗位
                Cell cell03 = row.createCell(3);
                cell03.setCellValue(itemList.get(i).getPositionName());
                cell03.setCellStyle(defaultCellStyle);
                //入职时间
                Cell cell04 = row.createCell(4);
                cell04.setCellValue("");
                cell04.setCellStyle(defaultCellStyle);
                //基本工资*
                Cell cell05 = row.createCell(5);
                cell05.setCellValue(itemList.get(i).getWageBase()==null?null:itemList.get(i).getWageBase().toString());
                cell05.setCellStyle(defaultCellStyle);
                //责任工资
                Cell cell06 = row.createCell(6);
                cell06.setCellValue(itemList.get(i).getWageDuty()==null?null:itemList.get(i).getWageDuty().toString());
                cell06.setCellStyle(defaultCellStyle);
                //计件工资
                Cell cell07 = row.createCell(7);
                cell07.setCellValue(itemList.get(i).getWagePiece()==null?null:itemList.get(i).getWagePiece().toString());
                cell07.setCellStyle(defaultCellStyle);
                //计时工资
                Cell cell08 = row.createCell(8);
                cell08.setCellValue("");
                cell08.setCellStyle(defaultCellStyle);
                //返修工资
                Cell cell09 = row.createCell(9);
                cell09.setCellValue(itemList.get(i).getWageFanxiu()==null?null:itemList.get(i).getWageFanxiu().toString());
                cell09.setCellStyle(defaultCellStyle);
                //其它工资
                Cell cell10 = row.createCell(10);
                cell10.setCellValue(itemList.get(i).getWageQit()==null?null:itemList.get(i).getWageQit().toString());
                cell10.setCellStyle(defaultCellStyle);
                //加班时间(时)
                Cell cell11 = row.createCell(11);
                cell11.setCellValue(itemList.get(i).getTimeRcjb()==null?null:itemList.get(i).getTimeRcjb().toString());
                cell11.setCellStyle(defaultCellStyle);
                //加班工资
                Cell cell12 = row.createCell(12);
                cell12.setCellValue(itemList.get(i).getWageRcjb()==null?null:itemList.get(i).getWageRcjb().toString());
                cell12.setCellStyle(defaultCellStyle);
                //通宵工资
                Cell cell13 = row.createCell(13);
                cell13.setCellValue(itemList.get(i).getWageTongxjb()==null?null:itemList.get(i).getWageTongxjb().toString());
                cell13.setCellStyle(defaultCellStyle);
                //其它加班工资
                Cell cell14 = row.createCell(14);
                cell14.setCellValue(itemList.get(i).getWageQitjb()==null?null:itemList.get(i).getWageQitjb().toString());
                cell14.setCellStyle(defaultCellStyle);
                //月绩效奖
                Cell cell15 = row.createCell(15);
                cell15.setCellValue(itemList.get(i).getBonusYuejx()==null?null:itemList.get(i).getBonusYuejx().toString());
                cell15.setCellStyle(defaultCellStyle);
                //月度满勤奖
                Cell cell16 = row.createCell(16);
                cell16.setCellValue(itemList.get(i).getBonusYuemq()==null?null:itemList.get(i).getBonusYuemq().toString());
                cell16.setCellStyle(defaultCellStyle);
                //季度满勤奖
                Cell cell17 = row.createCell(17);
                cell17.setCellValue(itemList.get(i).getBonusJidmq()==null?null:itemList.get(i).getBonusJidmq().toString());
                cell17.setCellStyle(defaultCellStyle);
                //年满勤奖
                Cell cell18 = row.createCell(18);
                cell18.setCellValue(itemList.get(i).getBonusNiandmq()==null?null:itemList.get(i).getBonusNiandmq().toString());
                cell18.setCellStyle(defaultCellStyle);
                //机台保养奖
                Cell cell19 = row.createCell(19);
                cell19.setCellValue(itemList.get(i).getBonusJitby()==null?null:itemList.get(i).getBonusJitby().toString());
                cell19.setCellStyle(defaultCellStyle);
                //服从奖
                Cell cell20 = row.createCell(20);
                cell20.setCellValue(itemList.get(i).getBonusFuc()==null?null:itemList.get(i).getBonusFuc().toString());
                cell20.setCellStyle(defaultCellStyle);
                //抽成
                Cell cell21 = row.createCell(21);
                cell21.setCellValue(itemList.get(i).getBonusChouc()==null?null:itemList.get(i).getBonusChouc().toString());
                cell21.setCellStyle(defaultCellStyle);
                //计件奖
                Cell cell22 = row.createCell(22);
                cell22.setCellValue(itemList.get(i).getBonusJij()==null?null:itemList.get(i).getBonusJij().toString());
                cell22.setCellStyle(defaultCellStyle);
                //人才介绍奖
                Cell cell23 = row.createCell(23);
                cell23.setCellValue(itemList.get(i).getBonusRencjs()==null?null:itemList.get(i).getBonusRencjs().toString());
                cell23.setCellStyle(defaultCellStyle);
                //其它奖金
                Cell cell24 = row.createCell(24);
                cell24.setCellValue(itemList.get(i).getBonusOther()==null?null:itemList.get(i).getBonusOther().toString());
                cell24.setCellStyle(defaultCellStyle);
                //住房补贴
                Cell cell25 = row.createCell(25);
                cell25.setCellValue(itemList.get(i).getAllowanceZhuf()==null?null:itemList.get(i).getAllowanceZhuf().toString());
                cell25.setCellStyle(defaultCellStyle);
                //餐费补贴
                Cell cell26 = row.createCell(26);
                cell26.setCellValue(itemList.get(i).getAllowanceCanf()==null?null:itemList.get(i).getAllowanceCanf().toString());
                cell26.setCellStyle(defaultCellStyle);
                //话费补贴
                Cell cell27 = row.createCell(27);
                cell27.setCellValue(itemList.get(i).getAllowanceHauf()==null?null:itemList.get(i).getAllowanceHauf().toString());
                cell27.setCellStyle(defaultCellStyle);
                //交通补贴
                Cell cell28 = row.createCell(28);
                cell28.setCellValue(itemList.get(i).getAllowanceJiaot()==null?null:itemList.get(i).getAllowanceJiaot().toString());
                cell28.setCellStyle(defaultCellStyle);
                //差旅补贴
                Cell cell29 = row.createCell(29);
                cell29.setCellValue(itemList.get(i).getAllowanceChail()==null?null:itemList.get(i).getAllowanceChail().toString());
                cell29.setCellStyle(defaultCellStyle);
                //岗位津贴
                Cell cell30 = row.createCell(30);
                cell30.setCellValue(itemList.get(i).getAllowanceGangw()==null?null:itemList.get(i).getAllowanceGangw().toString());
                cell30.setCellStyle(defaultCellStyle);
                //稳岗补贴
                Cell cell31 = row.createCell(31);
                cell31.setCellValue(itemList.get(i).getAllowanceWeng()==null?null:itemList.get(i).getAllowanceWeng().toString());
                cell31.setCellStyle(defaultCellStyle);
                //特殊工种补贴
                Cell cell32 = row.createCell(32);
                cell32.setCellValue(itemList.get(i).getAllowanceTesgz()==null?null:itemList.get(i).getAllowanceTesgz().toString());
                cell32.setCellStyle(defaultCellStyle);
                //餐具补贴
                Cell cell33 = row.createCell(33);
                cell33.setCellValue(itemList.get(i).getAllowanceCanj()==null?null:itemList.get(i).getAllowanceCanj().toString());
                cell33.setCellStyle(defaultCellStyle);
                //计件保底补贴
                Cell cell34 = row.createCell(34);
                cell34.setCellValue(itemList.get(i).getAllowanceBaod()==null?null:itemList.get(i).getAllowanceBaod().toString());
                cell34.setCellStyle(defaultCellStyle);
                //其它计件补贴
                Cell cell35 = row.createCell(35);
                cell35.setCellValue(itemList.get(i).getAllowanceQitjj()==null?null:itemList.get(i).getAllowanceQitjj().toString());
                cell35.setCellStyle(defaultCellStyle);
                //其它补贴
                Cell cell36 = row.createCell(36);
                cell36.setCellValue(itemList.get(i).getAllowanceOther()==null?null:itemList.get(i).getAllowanceOther().toString());
                cell36.setCellStyle(defaultCellStyle);
                //水电费扣款
                Cell cell37 = row.createCell(37);
                cell37.setCellValue(itemList.get(i).getDeductShuid()==null?null:itemList.get(i).getDeductShuid().toString());
                cell37.setCellStyle(defaultCellStyle);
                //迟到/缺卡扣款
                Cell cell38 = row.createCell(38);
                cell38.setCellValue(itemList.get(i).getDeductChid()==null?null:itemList.get(i).getDeductChid().toString());
                cell38.setCellStyle(defaultCellStyle);
                //餐具扣款
                Cell cell39 = row.createCell(39);
                cell39.setCellValue(itemList.get(i).getDeductCanj()==null?null:itemList.get(i).getDeductCanj().toString());
                cell39.setCellStyle(defaultCellStyle);
                //短少扣款
                Cell cell40 = row.createCell(40);
                cell40.setCellValue(itemList.get(i).getDeductDuans()==null?null:itemList.get(i).getDeductDuans().toString());
                cell40.setCellStyle(defaultCellStyle);
                //品质扣款
                Cell cell41 = row.createCell(41);
                cell41.setCellValue(itemList.get(i).getDeductPinz()==null?null:itemList.get(i).getDeductPinz().toString());
                cell41.setCellStyle(defaultCellStyle);
                //5S检查扣款
                Cell cell42 = row.createCell(42);
                cell42.setCellValue(itemList.get(i).getDeductFivecheck()==null?null:itemList.get(i).getDeductFivecheck().toString());
                cell42.setCellStyle(defaultCellStyle);
                //其它扣款
                Cell cell43 = row.createCell(43);
                cell43.setCellValue(itemList.get(i).getDeductOther()==null?null:itemList.get(i).getDeductOther().toString());
                cell43.setCellStyle(defaultCellStyle);
                //应发工资 *
                Cell cell44 = row.createCell(44);
                cell44.setCellValue(itemList.get(i).getYingfPayroll()==null?null:itemList.get(i).getYingfPayroll().toString());
                cell44.setCellStyle(defaultCellStyle);
                //代缴(公积金)
                Cell cell45 = row.createCell(45);
                cell45.setCellValue(itemList.get(i).getFeesGongjj()==null?null:itemList.get(i).getFeesGongjj().toString());
                cell45.setCellStyle(defaultCellStyle);
                //代缴(医保)
                Cell cell46 = row.createCell(46);
                cell46.setCellValue(itemList.get(i).getFeesYib()==null?null:itemList.get(i).getFeesYib().toString());
                cell46.setCellStyle(defaultCellStyle);
                //代缴(社保)
                Cell cell47 = row.createCell(47);
                cell47.setCellValue(itemList.get(i).getFeesSheb()==null?null:itemList.get(i).getFeesSheb().toString());
                cell47.setCellStyle(defaultCellStyle);
                //代缴(个税)
                Cell cell48 = row.createCell(48);
                cell48.setCellValue(itemList.get(i).getFeesIncometax()==null?null:itemList.get(i).getFeesIncometax().toString());
                cell48.setCellStyle(defaultCellStyle);
                //其它代缴
                Cell cell49 = row.createCell(49);
                cell49.setCellValue(itemList.get(i).getFeesOther()==null?null:itemList.get(i).getFeesOther().toString());
                cell49.setCellStyle(defaultCellStyle);
                //实发工资(税后) *
                Cell cell50 = row.createCell(50);
                cell50.setCellValue(itemList.get(i).getNetPayroll()==null?null:itemList.get(i).getNetPayroll().toString());
                cell50.setCellStyle(defaultCellStyle);
                //备注
                Cell cell51 = row.createCell(51);
                cell51.setCellValue(itemList.get(i).getRemark()==null?null:itemList.get(i).getRemark().toString());
                cell51.setCellStyle(defaultCellStyle);
                //工资成本分摊
                Cell cell52 = row.createCell(52);
                cell52.setCellValue(itemList.get(i).getSalaryCostAllocateType()==null?null:itemList.get(i).getSalaryCostAllocateType().toString());
                cell52.setCellStyle(defaultCellStyle);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    /**
     * 导入工资单
     *
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //提示信息
        List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
        CommonErrMsgResponse warnMsg = null;
        //主表
        PaySalaryBill paySalaryBill = new PaySalaryBill();
        //明细表
        List<PaySalaryBillItem> paySalaryBillItemList = new ArrayList<>();
        PaySalaryBillItem paySalaryBillItem = null;
        //员工 可能姓名存在重复
        List<BasStaff> staffList = new ArrayList<>();
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
            //excel表里面员工的缓存
            HashMap<String, Integer> staffMap = new HashMap<>();
            int num = 0;
            int staffNum = 0; // 用来记录几个重复员工
            // 工厂权限
            Long[] roleIds = null;
            List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
            if (CollectionUtil.isNotEmpty(roleList)){
                roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
            }
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleIds(roleIds);
            roleMenu.setPerms("ems:plant:all");
            Long plantSid = null;
            Long companySid = null;
            String companyShortName = null;
            String plantShortName = null;
            boolean isAll = true;
            if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
                isAll = remoteSystemService.isHavePerms(roleMenu).getData();
            }
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前2行跳过
                    continue;
                }
                num = i + 1;
                List<Object> objects = readAll.get(i);
                // 工资分摊类型
                String salaryCostAllocateType = null;
                if (i <= 5) {
                    if (i == 2) {
                        copy(objects, readAll, 0);
                        /**
                         * 所属年月 *
                         */
                        String yearMonth_s = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                        String yearMonth= null;
                        if (StrUtil.isBlank(yearMonth_s)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("所属年月不可为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!JudgeFormat.isYearMonth(yearMonth_s)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("所属年月格式错误，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                yearMonth = yearMonth_s.replace("/","-");
                            }
                        }
                        /**
                         * 公司编码 *
                         */
                        String companyName_s = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                        String companyCode = null;
                        String companyName = null;
                        BasCompany basCompany = null;
                        if (StrUtil.isBlank(companyName_s)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司不可为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            try {
                                basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getCompanyName, companyName_s));
                                if (basCompany == null){
                                    basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName, companyName_s));
                                }
                            } catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyName_s + "公司档案存在重复，请先检查该公司，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            if (basCompany == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyName_s +"没有对应的公司，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg(companyName_s + "对应的公司必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    companySid = basCompany.getCompanySid();
                                    companyCode = basCompany.getCompanyCode();
                                    companyName = basCompany.getCompanyName();
                                    companyShortName = basCompany.getShortName();
                                }
                            }
                        }
                        /**
                         * 工厂编码 *
                         */
                        String plantName_s = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                        String plantCode = null;
                        String plantName = null;
                        BasPlant basPlant = null;
                        if (StrUtil.isNotBlank(plantName_s)) {
                            try {
                                basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>().lambda().eq(BasPlant::getShortName, plantName_s));
                            } catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(plantName_s + "工厂档案存在重复，请先检查该工厂，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            if (basPlant == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(plantName_s +"没有对应的工厂，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                if (ConstantsEms.DISENABLE_STATUS.equals(basPlant.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basPlant.getHandleStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg(plantName_s + "对应的工厂必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    String companySidPlant = basPlant.getCompanySid();
                                    if(companySid!=null
                                            &&companySidPlant!=null
                                            &&!companySid.toString().equals(companySidPlant.toString())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("公司"+companyName_s+"下没有简称为"+plantName_s+"的工厂，导入失败！！");
                                        errMsgList.add(errMsg);
                                    }else{
                                        if (!isAll){
                                            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
                                            if (staffSid != null) {
                                                BasStaff staff = basStaffMapper.selectBasStaffById(staffSid);
                                                if (!basPlant.getPlantSid().equals(String.valueOf(staff.getDefaultPlantSid()))) {
                                                    errMsg = new CommonErrMsgResponse();
                                                    errMsg.setItemNum(num);
                                                    errMsg.setMsg("无权限导入" + plantShortName + "（工厂简称）的商品道序，导入失败！");
                                                    errMsgList.add(errMsg);
                                                }else {
                                                    plantSid = Long.parseLong(basPlant.getPlantSid());
                                                    plantCode = basPlant.getPlantCode();
                                                    plantName = basPlant.getPlantName();
                                                    plantShortName = basPlant.getShortName();
                                                }
                                            }
                                        }
                                        else {
                                            plantSid = Long.parseLong(basPlant.getPlantSid());
                                            plantCode = basPlant.getPlantCode();
                                            plantName = basPlant.getPlantName();
                                            plantShortName = basPlant.getShortName();
                                        }
                                    }
                                }
                            }
                        }
                        // 判断系统是否已存在
                        if (companySid != null && yearMonth != null) {
                            try {
                                scene0(new PaySalaryBill().setCompanySid(companySid).setPlantSid(plantSid).setYearmonth(yearMonth));
                            } catch (BaseException e) {
                                String msg = "";
                                if (plantSid == null) {
                                    msg = companyName_s + "公司下，在所属年月" + yearMonth_s + "已建立工资单，请核实";
                                }
                                else {
                                    msg = companyName_s + "公司下，" + plantName_s + "工厂在所属年月" + yearMonth_s + "已建立工资单，请核实";
                                }
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(msg);
                                errMsgList.add(errMsg);
                            }
                        }
                        if (CollectionUtil.isEmpty(errMsgList)) {
                            paySalaryBill.setCompanySid(companySid).setCompanyCode(companyCode).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                                    .setPlantSid(plantSid).setPlantCode(plantCode).setCurrency(ConstantsFinance.CURRENCY_CNY)
                                    .setYearmonth(yearMonth).setHandleStatus(ConstantsEms.SAVA_STATUS);
                        }
                    }
                    continue;
                }
                copy(objects, readAll, 4);
                /**
                 * 姓名 *
                 */
                String staffName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long staffSid = null;
                String staffCode = null;
                BasStaff basStaff = null;
                if (StrUtil.isBlank(staffName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("姓名不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    // 判断表格内是否存在重复员工行
                    if (staffMap.containsKey(staffName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格内在第" + String.valueOf(staffMap.get(staffName)) + "行已存在" + staffName +"员工，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        staffMap.put(staffName, num);
                        staffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda().eq(BasStaff::getStaffName, staffName));
                        if (CollectionUtil.isNotEmpty(staffList)) {
                            staffList = staffList.stream().filter(o->ConstantsEms.ENABLE_STATUS.equals(o.getStatus())
                                    && ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())
                                    &&ConstantsEms.IS_ON_JOB_ZZ.equals(o.getIsOnJob())
                            ).collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(staffList)) {
                                if(plantSid==null&&companySid!=null){
                                    Long companySidTemp=companySid;
                                    staffList=staffList.stream().filter(li->li.getDefaultCompanySid()!=null).filter(li->companySidTemp.toString().equals(li.getDefaultCompanySid().toString())).collect(Collectors.toList());
                                    if(CollectionUtil.isEmpty(staffList)){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("公司"+companyShortName+"下没有名称为"+staffName+"的员工，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                }
                                if(plantSid!=null&&companySid!=null){
                                    Long companySidTemp=companySid;
                                    Long plantSidTemp=plantSid;
                                    staffList=staffList.stream().filter(li->li.getDefaultCompanySid()!=null&&li.getDefaultPlantSid()!=null)
                                            .filter(li->companySidTemp.toString().equals(li.getDefaultCompanySid().toString())
                                            &&plantSidTemp.toString().equals(li.getDefaultPlantSid().toString())
                                            )
                                            .collect(Collectors.toList());
                                    if(CollectionUtil.isEmpty(staffList)){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("公司"+companyShortName+"，工厂"+plantShortName+"下没有名称为"+staffName+"的员工，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                }
                                if(CollectionUtil.isNotEmpty(staffList)){
                                    basStaff = staffList.get(0);
                                    if (staffList.size() > 1) {
                                        warnMsg = new CommonErrMsgResponse();
                                        warnMsg.setItemNum(num);
                                        warnMsg.setMsg("系统存在多个姓名" + staffName + "的员工档案，本次导入的是编号" + basStaff.getStaffCode() + "的员工");
                                        warnMsgList.add(warnMsg);
                                        staffNum += 1;
                                        staffSid = basStaff.getStaffSid();
                                        staffCode = basStaff.getStaffCode();
                                        if (basStaff.getDefaultDepartmentSid() != null) {
                                            BasDepartment department = basDepartmentMapper.selectById(basStaff.getDefaultDepartmentSid());
                                            salaryCostAllocateType = department.getSalaryCostAllocateType();
                                        }
                                    }
                                    else {
                                        staffSid = basStaff.getStaffSid();
                                        staffCode = basStaff.getStaffCode();
                                        if (basStaff.getDefaultDepartmentSid() != null) {
                                            BasDepartment department = basDepartmentMapper.selectById(basStaff.getDefaultDepartmentSid());
                                            salaryCostAllocateType = department.getSalaryCostAllocateType();
                                        }
                                    }
                                }
                            }
                            else {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(staffName + "对应的员工必须是确认且在职状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                        else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("姓名为" + staffName +"没有对应的员工，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 基本工资 （必填）
                 */
                String wage_base = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                BigDecimal wageBase = null;
                if (wage_base == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本工资不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(wage_base, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("基本工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageBase = new BigDecimal(wage_base);
                        if (wageBase.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 责任工资
                 */
                String wage_duty = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                BigDecimal wageDuty = null;
                if (wage_duty != null) {
                    if (!JudgeFormat.isValidDouble(wage_duty, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("责任工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageDuty = new BigDecimal(wage_duty);
                        if (wageDuty.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("责任工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计件工资
                 */
                String wage_piece = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal wagePiece = null;
                if (wage_piece != null) {
                    if (!JudgeFormat.isValidDouble(wage_piece, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计件工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wagePiece = new BigDecimal(wage_piece);
                        if (wagePiece.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计件工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计时工资
                 */
                String wage_time = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal wageTime = null;
                if (wage_time != null) {
                    if (!JudgeFormat.isValidDouble(wage_time, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计时工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageTime = new BigDecimal(wage_time);
                        if (wageTime.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计时工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 返修工资
                 */
                String wage_fanx= objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal wageFanx = null;
                if (wage_fanx != null) {
                    if (!JudgeFormat.isValidDouble(wage_fanx, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("返修工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageFanx = new BigDecimal(wage_fanx);
                        if (wageFanx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("返修工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它工资
                 */
                String wage_qit= objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal wageQit = null;
                if (wage_qit != null) {
                    if (!JudgeFormat.isValidDouble(wage_qit, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageQit = new BigDecimal(wage_qit);
                        if (wageQit.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 日常加班时间(时)
                 */
                String time_rcjb= objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal timeRcjb = null;
                if (time_rcjb != null) {
                    if (!JudgeFormat.isValidDouble(time_rcjb, 6, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("加班时间(时)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        timeRcjb = new BigDecimal(time_rcjb);
                        if (timeRcjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("加班时间(时)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 日常加班工资
                 */
                String wage_rcjb= objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                BigDecimal wageRcjb = null;
                if (wage_rcjb != null) {
                    if (!JudgeFormat.isValidDouble(wage_rcjb, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("加班工资，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageRcjb = new BigDecimal(wage_rcjb);
                        if (wageRcjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("加班工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 通宵加班工资
                 */
                String wage_tongxjb= objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                BigDecimal wageTongxjb = null;
                if (wage_tongxjb != null) {
                    if (!JudgeFormat.isValidDouble(wage_tongxjb, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("通宵工资，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageTongxjb = new BigDecimal(wage_tongxjb);
                        if (wageTongxjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("通宵工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它加班工资
                 */
                String wage_qitjb= objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal wageQitjb = null;
                if (wage_qitjb != null) {
                    if (!JudgeFormat.isValidDouble(wage_qitjb, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它加班工资，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageQitjb = new BigDecimal(wage_qitjb);
                        if (wageQitjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它加班工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 月绩效奖金
                 */
                String bonus_yuejx= objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                BigDecimal bonusYuejx = null;
                if (bonus_yuejx != null) {
                    if (!JudgeFormat.isValidDouble(bonus_yuejx, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月绩效奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusYuejx = new BigDecimal(bonus_yuejx);
                        if (bonusYuejx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月绩效奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 月满勤奖
                 */
                String bonus_yuemq= objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                BigDecimal bonusYuemq = null;
                if (bonus_yuemq != null) {
                    if (!JudgeFormat.isValidDouble(bonus_yuemq, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月度满勤奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusYuemq = new BigDecimal(bonus_yuemq);
                        if (bonusYuemq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月度满勤奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 季度满勤奖
                 */
                String bonus_jidmq= objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                BigDecimal bonusJidmq = null;
                if (bonus_jidmq != null) {
                    if (!JudgeFormat.isValidDouble(bonus_jidmq, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("季度满勤奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusJidmq = new BigDecimal(bonus_jidmq);
                        if (bonusJidmq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("季度满勤奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 年度满勤奖
                 */
                String bonus_niandmq= objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                BigDecimal bonusNiandmq = null;
                if (bonus_niandmq != null) {
                    if (!JudgeFormat.isValidDouble(bonus_niandmq, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度满勤奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusNiandmq = new BigDecimal(bonus_niandmq);
                        if (bonusNiandmq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("年度满勤奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 机台保养奖
                 */
                String bonus_jitby= objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                BigDecimal bonusJitby = null;
                if (bonus_jitby != null) {
                    if (!JudgeFormat.isValidDouble(bonus_jitby, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("机台保养奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusJitby = new BigDecimal(bonus_jitby);
                        if (bonusJitby.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("机台保养奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 服从奖
                 */
                String bonus_fuc= objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                BigDecimal bonusFuc = null;
                if (bonus_fuc != null) {
                    if (!JudgeFormat.isValidDouble(bonus_fuc, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("服从奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusFuc = new BigDecimal(bonus_fuc);
                        if (bonusFuc.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("服从奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 抽成
                 */
                String bonus_chouc= objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                BigDecimal bonusChouc = null;
                if (bonus_chouc != null) {
                    if (!JudgeFormat.isValidDouble(bonus_chouc, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("抽成，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusChouc = new BigDecimal(bonus_chouc);
                        if (bonusChouc.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("抽成，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计件奖
                 */
                String bonus_jij= objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
                BigDecimal bonusJij = null;
                if (bonus_jij != null) {
                    if (!JudgeFormat.isValidDouble(bonus_jij, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计件奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusJij = new BigDecimal(bonus_jij);
                        if (bonusJij.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计件奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 人才介绍奖
                 */
                String bonus_rencjs= objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                BigDecimal bonusRencjs = null;
                if (bonus_rencjs != null) {
                    if (!JudgeFormat.isValidDouble(bonus_rencjs, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("人才介绍奖，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusRencjs = new BigDecimal(bonus_rencjs);
                        if (bonusRencjs.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("人才介绍奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它奖金
                 */
                String bonus_other= objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal bonusOther = null;
                if (bonus_other != null) {
                    if (!JudgeFormat.isValidDouble(bonus_other, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它奖金，数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusOther = new BigDecimal(bonus_other);
                        if (bonusOther.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它奖金，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 住房补贴
                 */
                String allowance_zhuf = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                BigDecimal allowanceZhuf = null;
                if (allowance_zhuf != null) {
                    if (!JudgeFormat.isValidDouble(allowance_zhuf, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("住房补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceZhuf = new BigDecimal(allowance_zhuf);
                        if (allowanceZhuf.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("住房补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 餐费
                 */
                String allowance_canf = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                BigDecimal allowanceCanf = null;
                if (allowance_canf != null) {
                    if (!JudgeFormat.isValidDouble(allowance_canf, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("餐费补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceCanf = new BigDecimal(allowance_canf);
                        if (allowanceCanf.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("餐费补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 话费补贴
                 */
                String allowance_hauf = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                BigDecimal allowanceHauf = null;
                if (allowance_hauf != null) {
                    if (!JudgeFormat.isValidDouble(allowance_hauf, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("话费补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceHauf = new BigDecimal(allowance_hauf);
                        if (allowanceHauf.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("话费补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 交通补贴
                 */
                String allowance_jiaot = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                BigDecimal allowanceJiaot = null;
                if (allowance_jiaot != null) {
                    if (!JudgeFormat.isValidDouble(allowance_jiaot, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("交通补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceJiaot = new BigDecimal(allowance_jiaot);
                        if (allowanceJiaot.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("交通补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 差旅补贴
                 */
                String allowance_chail = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                BigDecimal allowanceChail = null;
                if (allowance_chail != null) {
                    if (!JudgeFormat.isValidDouble(allowance_chail, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("差旅补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceChail = new BigDecimal(allowance_chail);
                        if (allowanceChail.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("差旅补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 岗位津贴
                 */
                String allowance_gangw = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString();
                BigDecimal allowanceGangw = null;
                if (allowance_gangw != null) {
                    if (!JudgeFormat.isValidDouble(allowance_gangw, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("岗位津贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceGangw = new BigDecimal(allowance_gangw);
                        if (allowanceGangw.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("岗位津贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 稳岗补贴
                 */
                String allowance_weng = objects.get(27) == null || objects.get(27) == "" ? null : objects.get(27).toString();
                BigDecimal allowanceWeng = null;
                if (allowance_weng != null) {
                    if (!JudgeFormat.isValidDouble(allowance_weng, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("稳岗补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceWeng = new BigDecimal(allowance_weng);
                        if (allowanceWeng.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("稳岗补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 特殊工种补贴
                 */
                String allowance_tesgz = objects.get(28) == null || objects.get(28) == "" ? null : objects.get(28).toString();
                BigDecimal allowanceTesgz = null;
                if (allowance_tesgz != null) {
                    if (!JudgeFormat.isValidDouble(allowance_tesgz, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("特殊工种补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceTesgz = new BigDecimal(allowance_tesgz);
                        if (allowanceTesgz.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("特殊工种补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 餐具补贴
                 */
                String allowance_canj = objects.get(29) == null || objects.get(29) == "" ? null : objects.get(29).toString();
                BigDecimal allowanceCanj = null;
                if (allowance_canj != null) {
                    if (!JudgeFormat.isValidDouble(allowance_canj, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("餐具补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceCanj = new BigDecimal(allowance_canj);
                        if (allowanceCanj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("餐具补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计件保底补贴
                 */
                String allowance_baod = objects.get(30) == null || objects.get(30) == "" ? null : objects.get(30).toString();
                BigDecimal allowanceBaod = null;
                if (allowance_baod != null) {
                    if (!JudgeFormat.isValidDouble(allowance_baod, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计件保底补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceBaod = new BigDecimal(allowance_baod);
                        if (allowanceBaod.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计件保底补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它计件补贴
                 */
                String allowance_qitjj = objects.get(31) == null || objects.get(31) == "" ? null : objects.get(31).toString();
                BigDecimal allowanceQitjj = null;
                if (allowance_qitjj != null) {
                    if (!JudgeFormat.isValidDouble(allowance_qitjj, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它计件补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceQitjj = new BigDecimal(allowance_qitjj);
                        if (allowanceQitjj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它计件补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它补贴
                 */
                String allowance_qit = objects.get(32) == null || objects.get(32) == "" ? null : objects.get(32).toString();
                BigDecimal allowanceQit = null;
                if (allowance_qit != null) {
                    if (!JudgeFormat.isValidDouble(allowance_qit, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceQit = new BigDecimal(allowance_qit);
                        if (allowanceQit.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 水电费
                 */
                String deduct_shuid = objects.get(33) == null || objects.get(33) == "" ? null : objects.get(33).toString();
                BigDecimal deductShuid = null;
                if (deduct_shuid != null) {
                    if (!JudgeFormat.isValidDouble(deduct_shuid, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("水电费扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductShuid = new BigDecimal(deduct_shuid);
                        if (deductShuid.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("水电费扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 迟到/缺卡扣款
                 */
                String deduct_chid = objects.get(34) == null || objects.get(34) == "" ? null : objects.get(34).toString();
                BigDecimal deductChid = null;
                if (deduct_chid != null) {
                    if (!JudgeFormat.isValidDouble(deduct_chid, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("迟到/缺卡扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductChid = new BigDecimal(deduct_chid);
                        if (deductChid.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("迟到/缺卡扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 餐费扣款
                 */
                String deduct_canj = objects.get(35) == null || objects.get(35) == "" ? null : objects.get(35).toString();
                BigDecimal deductCanj = null;
                if (deduct_canj != null) {
                    if (!JudgeFormat.isValidDouble(deduct_canj, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("餐具扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductCanj = new BigDecimal(deduct_canj);
                        if (deductCanj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("餐具扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 短少扣款
                 */
                String deduct_duans = objects.get(36) == null || objects.get(36) == "" ? null : objects.get(36).toString();
                BigDecimal deductDuans = null;
                if (deduct_duans != null) {
                    if (!JudgeFormat.isValidDouble(deduct_duans, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("短少扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductDuans = new BigDecimal(deduct_duans);
                        if (deductDuans.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("短少扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 品质扣款
                 */
                String deduct_pinz = objects.get(37) == null || objects.get(37) == "" ? null : objects.get(37).toString();
                BigDecimal deductPinz = null;
                if (deduct_pinz != null) {
                    if (!JudgeFormat.isValidDouble(deduct_pinz, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("品质扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductPinz = new BigDecimal(deduct_pinz);
                        if (deductPinz.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("品质扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 5S检查
                 */
                String deduct_fivecheck = objects.get(38) == null || objects.get(38) == "" ? null : objects.get(38).toString();
                BigDecimal deductFivecheck = null;
                if (deduct_fivecheck != null) {
                    if (!JudgeFormat.isValidDouble(deduct_fivecheck, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("5S检查，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductFivecheck = new BigDecimal(deduct_fivecheck);
                        if (deductFivecheck.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("5S检查，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它扣款
                 */
                String deduct_other = objects.get(39) == null || objects.get(39) == "" ? null : objects.get(39).toString();
                BigDecimal deductOther = null;
                if (deduct_other != null) {
                    if (!JudgeFormat.isValidDouble(deduct_other, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductOther = new BigDecimal(deduct_other);
                        if (deductOther.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 应发工资(税后) （必填）
                 */
                String yingf_payroll = objects.get(40) == null || objects.get(40) == "" ? null : objects.get(40).toString();
                BigDecimal yingfPayroll = null;
                if (yingf_payroll == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应发工资不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(yingf_payroll, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应发工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yingfPayroll = new BigDecimal(yingf_payroll);
                        if (yingfPayroll.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("应发工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 公积金
                 */
                String fees_gongjj = objects.get(41) == null || objects.get(41) == "" ? null : objects.get(41).toString();
                BigDecimal feesGongjj = null;
                if (fees_gongjj != null) {
                    if (!JudgeFormat.isValidDouble(fees_gongjj, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公积金，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesGongjj = new BigDecimal(fees_gongjj);
                        if (feesGongjj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公积金，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 社保
                 */
                String fees_sheb = objects.get(42) == null || objects.get(42) == "" ? null : objects.get(42).toString();
                BigDecimal feesSheb = null;
                if (fees_sheb != null) {
                    if (!JudgeFormat.isValidDouble(fees_sheb, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("社保，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesSheb = new BigDecimal(fees_sheb);
                        if (feesSheb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("社保，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 医保
                 */
                String fees_yib = objects.get(43) == null || objects.get(43) == "" ? null : objects.get(43).toString();
                BigDecimal feesYib = null;
                if (fees_yib != null) {
                    if (!JudgeFormat.isValidDouble(fees_yib, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("医保，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesYib = new BigDecimal(fees_yib);
                        if (feesYib.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("医保，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 个人所得税
                 */
                String fees_incometax = objects.get(44) == null || objects.get(44) == "" ? null : objects.get(44).toString();
                BigDecimal feesIncometax = null;
                if (fees_incometax != null) {
                    if (!JudgeFormat.isValidDouble(fees_incometax, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("个人所得税，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesIncometax = new BigDecimal(fees_incometax);
                        if (feesIncometax.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("个人所得税，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它代扣
                 */
                String fees_other = objects.get(45) == null || objects.get(45) == "" ? null : objects.get(45).toString();
                BigDecimal feesOther = null;
                if (fees_other != null) {
                    if (!JudgeFormat.isValidDouble(fees_other, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它代扣，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesOther = new BigDecimal(fees_other);
                        if (feesOther.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它代扣，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 实发工资(税后) （必填）
                 */
                String net_payroll = objects.get(46) == null || objects.get(46) == "" ? null : objects.get(46).toString();
                BigDecimal netPayroll = null;
                if (net_payroll == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("实发工资不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(net_payroll, 6, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("实发工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        netPayroll = new BigDecimal(net_payroll);
                        if (netPayroll.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("实发工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(47) == null || objects.get(47) == "" ? null : objects.get(47).toString();
                if (remark != null && remark.length() > 600) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }

                if (CollectionUtil.isEmpty(errMsgList)) {
                    PaySalaryBillItem item = new PaySalaryBillItem();
                    item.setStaffSid(staffSid).setStaffCode(staffCode).setDepartmentSid(null).setPositionSid(null);
                    item.setWageBase(wageBase).setWageDuty(wageDuty).setWagePiece(wagePiece).setWageJishi(wageTime).setWageFanxiu(wageFanx).setWageQit(wageQit)
                            .setTimeRcjb(timeRcjb).setWageRcjb(wageRcjb).setWageTongxjb(wageTongxjb).setWageQitjb(wageQitjb).setNetPayroll(netPayroll)
                            .setBonusYuejx(bonusYuejx).setBonusYuemq(bonusYuemq).setBonusJidmq(bonusJidmq).setBonusNiandmq(bonusNiandmq)
                            .setBonusJitby(bonusJitby).setBonusFuc(bonusFuc).setBonusChouc(bonusChouc).setBonusJij(bonusJij).setBonusRencjs(bonusRencjs)
                            .setBonusOther(bonusOther).setAllowanceZhuf(allowanceZhuf).setAllowanceCanf(allowanceCanf).setAllowanceHauf(allowanceHauf)
                            .setAllowanceJiaot(allowanceJiaot).setAllowanceChail(allowanceChail).setAllowanceGangw(allowanceGangw).setAllowanceWeng(allowanceWeng)
                            .setAllowanceTesgz(allowanceTesgz).setAllowanceCanj(allowanceCanj).setAllowanceBaod(allowanceBaod).setAllowanceQitjj(allowanceQitjj)
                            .setAllowanceOther(allowanceQit).setDeductShuid(deductShuid).setDeductCanj(deductCanj)
                            .setDeductChid(deductChid).setAllowanceCanj(allowanceCanj).setAllowanceCanf(allowanceCanf)
                            .setDeductDuans(deductDuans).setDeductPinz(deductPinz).setDeductFivecheck(deductFivecheck).setDeductOther(deductOther)
                            .setYingfPayroll(yingfPayroll).setFeesGongjj(feesGongjj).setFeesYib(feesYib).setFeesSheb(feesSheb)
                            .setFeesIncometax(feesIncometax).setFeesOther(feesOther).setRemark(remark);
                    item.setSalaryCostAllocateType(salaryCostAllocateType);
                    paySalaryBillItemList.add(item);
                }
            }
            if (CollectionUtil.isEmpty(errMsgList)) {
                int row = paySalaryBillMapper.insert(paySalaryBill);
                if (row > 0) {
                    //工资单-明细对象
                    paySalaryBillItemList.forEach(o -> {
                        o.setSalaryBillSid(paySalaryBill.getSalaryBillSid());
                    });
                    paySalaryBillItemMapper.inserts(paySalaryBillItemList);
                    PaySalaryBill salaryBill = paySalaryBillMapper.selectPaySalaryBillById(paySalaryBill.getSalaryBillSid());
                    //待办通知
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    if (ConstantsEms.SAVA_STATUS.equals(paySalaryBill.getHandleStatus())) {
                        sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                .setTableName(ConstantsEms.TABLE_PAY_SALARY_BILL)
                                .setDocumentSid(paySalaryBill.getSalaryBillSid());
                        List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                        if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                            sysTodoTask.setTitle("工资单" + salaryBill.getSalaryBillCode() + "当前是保存状态，请及时处理！")
                                    .setDocumentCode(String.valueOf(salaryBill.getSalaryBillCode()))
                                    .setNoticeDate(new Date())
                                    .setUserId(ApiThreadLocalUtil.get().getUserid());
                            sysTodoTaskMapper.insert(sysTodoTask);
                        }
                    }
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(new PaySalaryBill(), paySalaryBill);
                    MongodbUtil.insertUserLog(paySalaryBill.getSalaryBillSid(), BusinessType.IMPORT.getValue(), msgList, TITLE);
                }
                if (CollectionUtil.isNotEmpty(warnMsgList)) {
                    String message = "导入成功" + String.valueOf(paySalaryBillItemList.size()) + "条明细，系统中员工姓名存在重复" + staffNum + "条";
                    return EmsResultEntity.success(row, warnMsgList, message);
                }
                else {
                    return EmsResultEntity.success(row);
                }
            }
            else {
                return EmsResultEntity.error(errMsgList);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importItemData(MultipartFile file, String salaryBillCode) {
        List<PaySalaryBillItem> paySalaryBillItemAddList = new ArrayList<>(); // 新增员工记录
        List<PaySalaryBillItem> paySalaryBillItemUpdateList = new ArrayList<>(); // 变更员工记录
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //
        PaySalaryBill paySalaryBill = null;
        try {
            paySalaryBill = paySalaryBillMapper.selectOne(new QueryWrapper<PaySalaryBill>()
                    .lambda().eq(PaySalaryBill::getSalaryBillCode, salaryBillCode));
        } catch (Exception e) {
            throw new BaseException("工资单 " + salaryBillCode + " 数据出现重复，请联系管理员！");
        }
        if (paySalaryBill == null) {
            throw new BaseException("工资单 " + salaryBillCode + " 不存在");
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
                        if (!code.equals(salaryBillCode)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("表格中的工资单号" + code + "，与系统中的工资单号不一致，导入失败！");
                            errMsgList.add(errMsg);
                            break;
                        }
                    }
                    continue;
                }
                PaySalaryBillItem item = new PaySalaryBillItem();
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
                }
                else {
                    try {
                        BasStaff staff = basStaffMapper.selectOne(new QueryWrapper<BasStaff>().lambda().eq(BasStaff::getStaffCode, staffCode));
                        if (staff != null) {
                            staffSid = staff.getStaffSid();
                            try {
                                item = paySalaryBillItemMapper.selectOne(new QueryWrapper<PaySalaryBillItem>()
                                        .lambda().eq(PaySalaryBillItem::getSalaryBillSid, paySalaryBill.getSalaryBillSid())
                                        .eq(PaySalaryBillItem::getStaffSid, staffSid));
                                if (item == null || item.getBillItemSid() == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("工号" + staffCode + "在工资单"+ salaryBillCode +"中不存在，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("工号 " + staffCode + " 在该工资单号里存在多笔记录，请先在系统中处理完成后再重新导入！");
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
                 * 部门
                 */
                String departName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                /*
                 * 岗位
                 */
                String positionName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                /*
                 * 入职时间
                 */
                String inWorkDate = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                /*
                 * 基本工资 （必填）
                 */
                String wage_base = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal wageBase = null;
                if (wage_base == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("基本工资不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(wage_base, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("基本工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageBase = new BigDecimal(wage_base);
                        if (wageBase.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("基本工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 责任工资
                 */
                String wage_duty = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal wageDuty = null;
                if (wage_duty != null) {
                    if (!JudgeFormat.isValidDouble(wage_duty, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("责任工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageDuty = new BigDecimal(wage_duty);
                        if (wageDuty.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("责任工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计件工资
                 */
                String wage_piece = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal wagePiece = null;
                if (wage_piece != null) {
                    if (!JudgeFormat.isValidDouble(wage_piece, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计件工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wagePiece = new BigDecimal(wage_piece);
                        if (wagePiece.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计件工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计时工资
                 */
                String wage_time = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                BigDecimal wageTime = null;
                if (wage_time != null) {
                    if (!JudgeFormat.isValidDouble(wage_time, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计时工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageTime = new BigDecimal(wage_time);
                        if (wageTime.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计时工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 返修工资
                 */
                String wage_fanx= objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                BigDecimal wageFanx = null;
                if (wage_fanx != null) {
                    if (!JudgeFormat.isValidDouble(wage_fanx, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("返修工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageFanx = new BigDecimal(wage_fanx);
                        if (wageFanx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("返修工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它工资
                 */
                String wage_qit= objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal wageQit = null;
                if (wage_qit != null) {
                    if (!JudgeFormat.isValidDouble(wage_qit, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageQit = new BigDecimal(wage_qit);
                        if (wageQit.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 日常加班时间(时)
                 */
                String time_rcjb= objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                BigDecimal timeRcjb = null;
                if (time_rcjb != null) {
                    if (!JudgeFormat.isValidDouble(time_rcjb, 6, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("加班时间(时)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        timeRcjb = new BigDecimal(time_rcjb);
                        if (timeRcjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("加班时间(时)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 日常加班工资
                 */
                String wage_rcjb= objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                BigDecimal wageRcjb = null;
                if (wage_rcjb != null) {
                    if (!JudgeFormat.isValidDouble(wage_rcjb, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("加班工资，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageRcjb = new BigDecimal(wage_rcjb);
                        if (wageRcjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("加班工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 通宵加班工资
                 */
                String wage_tongxjb= objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                BigDecimal wageTongxjb = null;
                if (wage_tongxjb != null) {
                    if (!JudgeFormat.isValidDouble(wage_tongxjb, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("通宵工资，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageTongxjb = new BigDecimal(wage_tongxjb);
                        if (wageTongxjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("通宵工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它加班工资
                 */
                String wage_qitjb= objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                BigDecimal wageQitjb = null;
                if (wage_qitjb != null) {
                    if (!JudgeFormat.isValidDouble(wage_qitjb, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它加班工资，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        wageQitjb = new BigDecimal(wage_qitjb);
                        if (wageQitjb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它加班工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 月绩效奖金
                 */
                String bonus_yuejx= objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                BigDecimal bonusYuejx = null;
                if (bonus_yuejx != null) {
                    if (!JudgeFormat.isValidDouble(bonus_yuejx, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月绩效奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusYuejx = new BigDecimal(bonus_yuejx);
                        if (bonusYuejx.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月绩效奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 月满勤奖
                 */
                String bonus_yuemq= objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                BigDecimal bonusYuemq = null;
                if (bonus_yuemq != null) {
                    if (!JudgeFormat.isValidDouble(bonus_yuemq, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月度满勤奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusYuemq = new BigDecimal(bonus_yuemq);
                        if (bonusYuemq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月度满勤奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 季度满勤奖
                 */
                String bonus_jidmq= objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                BigDecimal bonusJidmq = null;
                if (bonus_jidmq != null) {
                    if (!JudgeFormat.isValidDouble(bonus_jidmq, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("季度满勤奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusJidmq = new BigDecimal(bonus_jidmq);
                        if (bonusJidmq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("季度满勤奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 年度满勤奖
                 */
                String bonus_niandmq= objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
                BigDecimal bonusNiandmq = null;
                if (bonus_niandmq != null) {
                    if (!JudgeFormat.isValidDouble(bonus_niandmq, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度满勤奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusNiandmq = new BigDecimal(bonus_niandmq);
                        if (bonusNiandmq.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("年度满勤奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 机台保养奖
                 */
                String bonus_jitby= objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                BigDecimal bonusJitby = null;
                if (bonus_jitby != null) {
                    if (!JudgeFormat.isValidDouble(bonus_jitby, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("机台保养奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusJitby = new BigDecimal(bonus_jitby);
                        if (bonusJitby.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("机台保养奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 服从奖
                 */
                String bonus_fuc= objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal bonusFuc = null;
                if (bonus_fuc != null) {
                    if (!JudgeFormat.isValidDouble(bonus_fuc, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("服从奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusFuc = new BigDecimal(bonus_fuc);
                        if (bonusFuc.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("服从奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 抽成
                 */
                String bonus_chouc= objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                BigDecimal bonusChouc = null;
                if (bonus_chouc != null) {
                    if (!JudgeFormat.isValidDouble(bonus_chouc, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("抽成，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusChouc = new BigDecimal(bonus_chouc);
                        if (bonusChouc.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("抽成，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计件奖
                 */
                String bonus_jij= objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                BigDecimal bonusJij = null;
                if (bonus_jij != null) {
                    if (!JudgeFormat.isValidDouble(bonus_jij, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计件奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusJij = new BigDecimal(bonus_jij);
                        if (bonusJij.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计件奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 人才介绍奖
                 */
                String bonus_rencjs= objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                BigDecimal bonusRencjs = null;
                if (bonus_rencjs != null) {
                    if (!JudgeFormat.isValidDouble(bonus_rencjs, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("人才介绍奖，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusRencjs = new BigDecimal(bonus_rencjs);
                        if (bonusRencjs.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("人才介绍奖，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它奖金
                 */
                String bonus_other= objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                BigDecimal bonusOther = null;
                if (bonus_other != null) {
                    if (!JudgeFormat.isValidDouble(bonus_other, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它奖金，,数据格式错误,导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bonusOther = new BigDecimal(bonus_other);
                        if (bonusOther.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它奖金，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 住房补贴
                 */
                String allowance_zhuf = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                BigDecimal allowanceZhuf = null;
                if (allowance_zhuf != null) {
                    if (!JudgeFormat.isValidDouble(allowance_zhuf, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("住房补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceZhuf = new BigDecimal(allowance_zhuf);
                        if (allowanceZhuf.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("住房补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 餐费补贴
                 */
                String allowance_canf = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString();
                BigDecimal allowanceCanf = null;
                if (allowance_canf != null) {
                    if (!JudgeFormat.isValidDouble(allowance_canf, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("餐费补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceCanf = new BigDecimal(allowance_canf);
                        if (allowanceCanf.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("餐费补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 话费补贴
                 */
                String allowance_hauf = objects.get(27) == null || objects.get(27) == "" ? null : objects.get(27).toString();
                BigDecimal allowanceHauf = null;
                if (allowance_hauf != null) {
                    if (!JudgeFormat.isValidDouble(allowance_hauf, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("话费补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceHauf = new BigDecimal(allowance_hauf);
                        if (allowanceHauf.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("话费补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 交通补贴
                 */
                String allowance_jiaot = objects.get(28) == null || objects.get(28) == "" ? null : objects.get(28).toString();
                BigDecimal allowanceJiaot = null;
                if (allowance_jiaot != null) {
                    if (!JudgeFormat.isValidDouble(allowance_jiaot, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("交通补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceJiaot = new BigDecimal(allowance_jiaot);
                        if (allowanceJiaot.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("交通补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 差旅补贴
                 */
                String allowance_chail = objects.get(29) == null || objects.get(29) == "" ? null : objects.get(29).toString();
                BigDecimal allowanceChail = null;
                if (allowance_chail != null) {
                    if (!JudgeFormat.isValidDouble(allowance_chail, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("差旅补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceChail = new BigDecimal(allowance_chail);
                        if (allowanceChail.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("差旅补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 岗位津贴
                 */
                String allowance_gangw = objects.get(30) == null || objects.get(30) == "" ? null : objects.get(30).toString();
                BigDecimal allowanceGangw = null;
                if (allowance_gangw != null) {
                    if (!JudgeFormat.isValidDouble(allowance_gangw, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("岗位津贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceGangw = new BigDecimal(allowance_gangw);
                        if (allowanceGangw.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("岗位津贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 稳岗补贴
                 */
                String allowance_weng = objects.get(31) == null || objects.get(31) == "" ? null : objects.get(31).toString();
                BigDecimal allowanceWeng = null;
                if (allowance_weng != null) {
                    if (!JudgeFormat.isValidDouble(allowance_weng, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("稳岗补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceWeng = new BigDecimal(allowance_weng);
                        if (allowanceWeng.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("稳岗补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 特殊工种补贴
                 */
                String allowance_tesgz = objects.get(32) == null || objects.get(32) == "" ? null : objects.get(32).toString();
                BigDecimal allowanceTesgz = null;
                if (allowance_tesgz != null) {
                    if (!JudgeFormat.isValidDouble(allowance_tesgz, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("特殊工种补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceTesgz = new BigDecimal(allowance_tesgz);
                        if (allowanceTesgz.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("特殊工种补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 餐具补贴
                 */
                String allowance_canj = objects.get(33) == null || objects.get(33) == "" ? null : objects.get(33).toString();
                BigDecimal allowanceCanj = null;
                if (allowance_canj != null) {
                    if (!JudgeFormat.isValidDouble(allowance_canj, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("餐具补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceCanj = new BigDecimal(allowance_canj);
                        if (allowanceCanj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("餐具补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 计件保底补贴
                 */
                String allowance_baod = objects.get(34) == null || objects.get(34) == "" ? null : objects.get(34).toString();
                BigDecimal allowanceBaod = null;
                if (allowance_baod != null) {
                    if (!JudgeFormat.isValidDouble(allowance_baod, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计件保底补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceBaod = new BigDecimal(allowance_baod);
                        if (allowanceBaod.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计件保底补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它计件补贴
                 */
                String allowance_qitjj = objects.get(35) == null || objects.get(35) == "" ? null : objects.get(35).toString();
                BigDecimal allowanceQitjj = null;
                if (allowance_qitjj != null) {
                    if (!JudgeFormat.isValidDouble(allowance_qitjj, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它计件补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceQitjj = new BigDecimal(allowance_qitjj);
                        if (allowanceQitjj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它计件补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它补贴
                 */
                String allowance_qit = objects.get(36) == null || objects.get(36) == "" ? null : objects.get(36).toString();
                BigDecimal allowanceQit = null;
                if (allowance_qit != null) {
                    if (!JudgeFormat.isValidDouble(allowance_qit, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它补贴，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        allowanceQit = new BigDecimal(allowance_qit);
                        if (allowanceQit.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它补贴，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 水电费扣款
                 */
                String deduct_shuid = objects.get(37) == null || objects.get(37) == "" ? null : objects.get(37).toString();
                BigDecimal deductShuid = null;
                if (deduct_shuid != null) {
                    if (!JudgeFormat.isValidDouble(deduct_shuid, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("水电费扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductShuid = new BigDecimal(deduct_shuid);
                        if (deductShuid.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("水电费扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 迟到扣款
                 */
                String deduct_chid = objects.get(38) == null || objects.get(38) == "" ? null : objects.get(38).toString();
                BigDecimal deductChid = null;
                if (deduct_chid != null) {
                    if (!JudgeFormat.isValidDouble(deduct_chid, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("迟到扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductChid = new BigDecimal(deduct_chid);
                        if (deductChid.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("迟到扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 餐费扣款
                 */
                String deduct_canj = objects.get(39) == null || objects.get(39) == "" ? null : objects.get(39).toString();
                BigDecimal deductCanj = null;
                if (deduct_canj != null) {
                    if (!JudgeFormat.isValidDouble(deduct_canj, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("餐具扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductCanj = new BigDecimal(deduct_canj);
                        if (deductCanj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("餐具扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 短少扣款
                 */
                String deduct_duans = objects.get(40) == null || objects.get(40) == "" ? null : objects.get(40).toString();
                BigDecimal deductDuans = null;
                if (deduct_duans != null) {
                    if (!JudgeFormat.isValidDouble(deduct_duans, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("短少扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductDuans = new BigDecimal(deduct_duans);
                        if (deductDuans.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("短少扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 品质扣款
                 */
                String deduct_pinz = objects.get(41) == null || objects.get(41) == "" ? null : objects.get(41).toString();
                BigDecimal deductPinz = null;
                if (deduct_pinz != null) {
                    if (!JudgeFormat.isValidDouble(deduct_pinz, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("品质扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductPinz = new BigDecimal(deduct_pinz);
                        if (deductPinz.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("品质扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 5S扣款
                 */
                String deduct_fivecheck = objects.get(42) == null || objects.get(42) == "" ? null : objects.get(42).toString();
                BigDecimal deductFivecheck = null;
                if (deduct_fivecheck != null) {
                    if (!JudgeFormat.isValidDouble(deduct_fivecheck, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("5S扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductFivecheck = new BigDecimal(deduct_fivecheck);
                        if (deductFivecheck.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("5S扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 其它扣款
                 */
                String deduct_other = objects.get(43) == null || objects.get(43) == "" ? null : objects.get(43).toString();
                BigDecimal deductOther = null;
                if (deduct_other != null) {
                    if (!JudgeFormat.isValidDouble(deduct_other, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它扣款，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        deductOther = new BigDecimal(deduct_other);
                        if (deductOther.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它扣款，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 应发工资(税后) （必填）
                 */
                String yingf_payroll = objects.get(44) == null || objects.get(44) == "" ? null : objects.get(44).toString();
                BigDecimal yingfPayroll = null;
                if (yingf_payroll == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应发工资不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(yingf_payroll, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应发工资，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yingfPayroll = new BigDecimal(yingf_payroll);
                        if (yingfPayroll.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("应发工资，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 代缴(公积金)
                 */
                String fees_gongjj = objects.get(45) == null || objects.get(45) == "" ? null : objects.get(45).toString();
                BigDecimal feesGongjj = null;
                if (fees_gongjj != null) {
                    if (!JudgeFormat.isValidDouble(fees_gongjj, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("代缴(公积金)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesGongjj = new BigDecimal(fees_gongjj);
                        if (feesGongjj.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("代缴(公积金)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 代缴(医保)
                 */
                String fees_yib = objects.get(46) == null || objects.get(46) == "" ? null : objects.get(46).toString();
                BigDecimal feesYib = null;
                if (fees_yib != null) {
                    if (!JudgeFormat.isValidDouble(fees_yib, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("代缴(医保)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesYib = new BigDecimal(fees_yib);
                        if (feesYib.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("代缴(医保)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 代缴(社保)
                 */
                String fees_sheb = objects.get(47) == null || objects.get(47) == "" ? null : objects.get(47).toString();
                BigDecimal feesSheb = null;
                if (fees_sheb != null) {
                    if (!JudgeFormat.isValidDouble(fees_sheb, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("代缴(社保)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesSheb = new BigDecimal(fees_sheb);
                        if (feesSheb.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("代缴(社保)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 代缴(个税)
                 */
                String fees_incometax = objects.get(48) == null || objects.get(48) == "" ? null : objects.get(48).toString();
                BigDecimal feesIncometax = null;
                if (fees_incometax != null) {
                    if (!JudgeFormat.isValidDouble(fees_incometax, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("代缴(个税)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesIncometax = new BigDecimal(fees_incometax);
                        if (feesIncometax.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("代缴(个税)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 代缴(其它)
                 */
                String fees_other = objects.get(49) == null || objects.get(49) == "" ? null : objects.get(49).toString();
                BigDecimal feesOther = null;
                if (fees_other != null) {
                    if (!JudgeFormat.isValidDouble(fees_other, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("代缴(其它)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        feesOther = new BigDecimal(fees_other);
                        if (feesOther.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("代缴(其它)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 实发工资(税后) （必填）
                 */
                String net_payroll = objects.get(50) == null || objects.get(50) == "" ? null : objects.get(50).toString();
                BigDecimal netPayroll = null;
                if (net_payroll == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("实发工资(税后)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDouble(net_payroll, 6, 3)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("实发工资(税后)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        netPayroll = new BigDecimal(net_payroll);
                        if (netPayroll.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("实发工资(税后)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(51) == null || objects.get(51) == "" ? null : objects.get(51).toString();
                if (remark != null && remark.length() > 600) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList) && staffSid != null) {
                    item.setStaffSid(staffSid).setStaffCode(staffCode).setDepartmentSid(null).setPositionSid(null);
                    item.setWageBase(wageBase).setWageDuty(wageDuty).setWagePiece(wagePiece).setWageFanxiu(wageFanx).setWageQit(wageQit)
                                    .setTimeRcjb(timeRcjb).setWageRcjb(wageRcjb).setWageTongxjb(wageTongxjb).setWageQitjb(wageQitjb)
                                    .setWageJishi(wageTime).setNetPayroll(netPayroll).setDeductCanj(deductCanj)
                                    .setBonusYuejx(bonusYuejx).setBonusYuemq(bonusYuemq).setBonusJidmq(bonusJidmq).setBonusNiandmq(bonusNiandmq);
                    item.setBonusJitby(bonusJitby).setBonusFuc(bonusFuc).setBonusChouc(bonusChouc).setBonusJij(bonusJij).setBonusRencjs(bonusRencjs)
                                    .setBonusOther(bonusOther).setAllowanceZhuf(allowanceZhuf).setAllowanceCanf(allowanceCanf).setAllowanceHauf(allowanceHauf)
                                    .setAllowanceJiaot(allowanceJiaot).setAllowanceChail(allowanceChail).setAllowanceGangw(allowanceGangw).setAllowanceWeng(allowanceWeng)
                                    .setAllowanceTesgz(allowanceTesgz).setAllowanceCanj(allowanceCanj).setAllowanceBaod(allowanceBaod).setAllowanceQitjj(allowanceQitjj);
                    item.setAllowanceOther(allowanceQit).setDeductShuid(deductShuid).setDeductChid(deductChid).setAllowanceCanj(allowanceCanj).setAllowanceCanf(allowanceCanf)
                                    .setDeductDuans(deductDuans).setDeductPinz(deductPinz).setDeductFivecheck(deductFivecheck).setDeductOther(deductOther)
                                    .setYingfPayroll(yingfPayroll).setFeesGongjj(feesGongjj).setFeesYib(feesYib).setFeesSheb(feesSheb)
                                    .setFeesIncometax(feesIncometax).setFeesOther(feesOther).setRemark(remark);
                    if (item.getBillItemSid() != null) {
                        paySalaryBillItemUpdateList.add(item);
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
                if (CollectionUtil.isNotEmpty(paySalaryBillItemUpdateList)) {
                    paySalaryBillItemUpdateList.forEach(item -> {
                        paySalaryBillItemMapper.updateAllById(item);
                    });
                    row = row + paySalaryBillItemUpdateList.size();
                }
                if (CollectionUtil.isNotEmpty(paySalaryBillItemAddList)) {
                    row = row + paySalaryBillItemMapper.inserts(paySalaryBillItemAddList);
                }

                return row;
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
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

    //填充
    public void copy(List<Object> objects,List<List<Object>> readAll, int row){
        //获取第一行的列数
        int size = readAll.get(row).size();
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
