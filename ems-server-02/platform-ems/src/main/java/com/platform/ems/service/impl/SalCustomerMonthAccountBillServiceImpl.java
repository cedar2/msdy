package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISalCustomerMonthAccountBillService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户对账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class SalCustomerMonthAccountBillServiceImpl extends ServiceImpl<SalCustomerMonthAccountBillMapper, SalCustomerMonthAccountBill> implements ISalCustomerMonthAccountBillService {
    @Autowired
    private SalCustomerMonthAccountBillMapper salCustomerMonthAccountBillMapper;
    @Autowired
    private SalCustomerMonthAccountBillAttachMapper salCustomerMonthAccountBillAttachMapper;
    @Autowired
    private SalCustomerMonthAccountBillZanguMapper salCustomerMonthAccountBillZanguMapper;
    //应付暂估
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    //预付款
    @Autowired
    private FinRecordAdvanceReceiptMapper finRecordAdvanceReceiptMapper;
    //发票
    @Autowired
    private FinPurchaseInvoiceMapper finPurchaseInvoiceMapper;
    //应付
    @Autowired
    private FinBookAccountPayableMapper finBookAccountPayableMapper;
    //收款单
    @Autowired
    private FinReceivableBillMapper finReceivableBillMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IWorkFlowService workflowService;

    private static final String TITLE = "客户对账单";

    /**
     * 查询客户对账单
     *
     * @param customerMonthAccountBillSid 客户对账单ID
     * @return 客户对账单
     */
    @Override
    public SalCustomerMonthAccountBill selectSalCustomerMonthAccountBillById(Long customerMonthAccountBillSid) {
        SalCustomerMonthAccountBill salCustomerMonthAccountBill = salCustomerMonthAccountBillMapper.selectSalCustomerMonthAccountBillById(customerMonthAccountBillSid);
        //附件
        List<SalCustomerMonthAccountBillAttach> salCustomerMonthAccountBillAttachList = salCustomerMonthAccountBillAttachMapper.selectList(new QueryWrapper<SalCustomerMonthAccountBillAttach>()
                .lambda().eq(SalCustomerMonthAccountBillAttach::getCustomerMonthAccountBillSid, customerMonthAccountBillSid));
        if (CollectionUtils.isNotEmpty(salCustomerMonthAccountBillAttachList)) {
            salCustomerMonthAccountBill.setAttachmentList(salCustomerMonthAccountBillAttachList);
        }else{
            salCustomerMonthAccountBill.setAttachmentList(new ArrayList<>());
        }
        //暂估明细
        List<SalCustomerMonthAccountBillZangu> bookReceiptEstimationItemSidList = salCustomerMonthAccountBillZanguMapper.getReportForm(new SalCustomerMonthAccountBillZangu()
                        .setCustomerMonthAccountBillSid(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid()));
        if (CollectionUtils.isNotEmpty(bookReceiptEstimationItemSidList)) {

            salCustomerMonthAccountBill.setSalCustomerMonthAccountBillZanguList(bookReceiptEstimationItemSidList);
        }else{
            salCustomerMonthAccountBill.setSalCustomerMonthAccountBillZanguList(new ArrayList<>());
        }
        MongodbUtil.find(salCustomerMonthAccountBill);
        return salCustomerMonthAccountBill;
    }

    /**
     * 查询客户对账单列表
     *
     * @param salCustomerMonthAccountBill 客户对账单
     * @return 客户对账单
     */
    @Override
    public List<SalCustomerMonthAccountBill> selectSalCustomerMonthAccountBillList(SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        return salCustomerMonthAccountBillMapper.selectSalCustomerMonthAccountBillList(salCustomerMonthAccountBill);
    }

    /**
     * 新增客户对账单
     * 需要注意编码重复校验
     *
     * @param salCustomerMonthAccountBill 客户对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalCustomerMonthAccountBill(SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        List<SalCustomerMonthAccountBill> one = salCustomerMonthAccountBillMapper.selectList(new QueryWrapper<SalCustomerMonthAccountBill>()
                .lambda().eq(SalCustomerMonthAccountBill::getCustomerSid, salCustomerMonthAccountBill.getCustomerSid())
                .eq(SalCustomerMonthAccountBill::getCompanySid, salCustomerMonthAccountBill.getCompanySid())
                .eq(SalCustomerMonthAccountBill::getYearMonths, salCustomerMonthAccountBill.getYearMonths())
                .eq(SalCustomerMonthAccountBill::getMaterialType, salCustomerMonthAccountBill.getMaterialType()));
        if (CollectionUtil.isNotEmpty(one)) {
            throw new BaseException("对账单已存在！");
        }
        //设置确认信息，校验
        setConfirmedInfo(salCustomerMonthAccountBill);
        int row = salCustomerMonthAccountBillMapper.insert(salCustomerMonthAccountBill);
        if (row > 0) {
            //插入子表，暂估明细表
            insertChildZangu(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList(), salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入子表，附件表
            insertChild(salCustomerMonthAccountBill.getAttachmentList(), salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            //待办通知
            if (ConstantsEms.SAVA_STATUS.equals(salCustomerMonthAccountBill.getHandleStatus())) {
                SalCustomerMonthAccountBill mx = salCustomerMonthAccountBillMapper.selectOne(new QueryWrapper<SalCustomerMonthAccountBill>().lambda()
                        .eq(SalCustomerMonthAccountBill::getCustomerMonthAccountBillSid,salCustomerMonthAccountBill.getCustomerMonthAccountBillSid()));
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_sal_customer_month_account_bill")
                        .setDocumentSid(mx.getCustomerMonthAccountBillSid());
                sysTodoTask.setTitle("客户对账单: " + mx.getCustomerMonthAccountBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(mx.getCustomerMonthAccountBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            if(ConstantsEms.CHECK_STATUS.equals(salCustomerMonthAccountBill.getHandleStatus())){
                if(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList() != null &&
                        salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().size() > 0) {
                    // 明细改为 是
                    List<Long> sidList = salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().stream()
                            .map(SalCustomerMonthAccountBillZangu::getBookReceiptEstimationItemSid) // 提取 sid
                            .collect(Collectors.toList()); // 收集为 List
                    finBookReceiptEstimationItemMapper.update(null, new UpdateWrapper<FinBookReceiptEstimationItem>().lambda()
                            .in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, sidList)
                            .set(FinBookReceiptEstimationItem::getIsBusinessVerify, ConstantsEms.YES));
                }
                MongodbUtil.insertUserLog(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 修改客户对账单
     *
     * @param salCustomerMonthAccountBill 客户对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalCustomerMonthAccountBill(SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(salCustomerMonthAccountBill);
        SalCustomerMonthAccountBill response = salCustomerMonthAccountBillMapper.selectSalCustomerMonthAccountBillById(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
        int row = salCustomerMonthAccountBillMapper.updateAllById(salCustomerMonthAccountBill);
        if (row > 0) {
            //删除子表，暂估明细表
            deleteZangu(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入子表，暂估明细表
            insertChildZangu(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList(), salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //删除子表，附件表
            deleteItem(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入子表，附件表
            insertChild(salCustomerMonthAccountBill.getAttachmentList(), salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(salCustomerMonthAccountBill.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getDocumentSid, salCustomerMonthAccountBill.getCustomerMonthAccountBillSid()));
                if(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList() != null &&
                        salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().size() > 0) {
                    // 明细改为 是
                    List<Long> sidList = salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().stream()
                            .map(SalCustomerMonthAccountBillZangu::getBookReceiptEstimationItemSid) // 提取 sid
                            .collect(Collectors.toList()); // 收集为 List
                    finBookReceiptEstimationItemMapper.update(null, new UpdateWrapper<FinBookReceiptEstimationItem>().lambda()
                            .in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, sidList)
                            .set(FinBookReceiptEstimationItem::getIsBusinessVerify, ConstantsEms.YES));
                }
                //插入日志
                 MongodbUtil.insertUserLog(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.CHECK.getValue(), response, salCustomerMonthAccountBill, TITLE);
            }else{
                //插入日志
                MongodbUtil.insertUserLog(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.UPDATE.getValue(), response, salCustomerMonthAccountBill, TITLE);
            }
        }
        return row;
    }

    /**
     * 变更客户对账单
     *
     * @param salCustomerMonthAccountBill 客户对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalCustomerMonthAccountBill(SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(salCustomerMonthAccountBill);
        SalCustomerMonthAccountBill response = salCustomerMonthAccountBillMapper.selectSalCustomerMonthAccountBillById(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
        int row = salCustomerMonthAccountBillMapper.updateAllById(salCustomerMonthAccountBill);
        if (row > 0) {
            if(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList() == null
                    || salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().size() == 0){
                throw new BaseException("出入库明细不能为空！");
            }
            //删除子表，暂估明细表
            deleteZangu(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            if(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList() != null &&
                    salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().size() > 0) {
                // 明细改为 是
                List<Long> sidList = salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().stream()
                        .map(SalCustomerMonthAccountBillZangu::getBookReceiptEstimationItemSid) // 提取 sid
                        .collect(Collectors.toList()); // 收集为 List
                finBookReceiptEstimationItemMapper.update(null,new UpdateWrapper<FinBookReceiptEstimationItem>().lambda()
                        .in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid,sidList)
                        .set(FinBookReceiptEstimationItem::getIsBusinessVerify,ConstantsEms.YES));
            }
            //插入子表，暂估明细表
            insertChildZangu(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList(), salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //删除子表，附件表
            deleteItem(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入子表，附件表
            insertChild(salCustomerMonthAccountBill.getAttachmentList(), salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
            //插入日志
            MongodbUtil.insertUserLog(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid(), BusinessType.CHANGE.getValue(), response, salCustomerMonthAccountBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户对账单
     *
     * @param customerMonthAccountBillSids 需要删除的客户对账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalCustomerMonthAccountBillByIds(List<Long> customerMonthAccountBillSids) {
        int i = salCustomerMonthAccountBillMapper.deleteBatchIds(customerMonthAccountBillSids);
        if (i > 0) {
            customerMonthAccountBillSids.forEach(sid -> {
                //删除子表，暂估明细表
                QueryWrapper<SalCustomerMonthAccountBillZangu> atmWrapper = new QueryWrapper<>();
                atmWrapper.eq("customer_month_account_bill_sid", sid);
                salCustomerMonthAccountBillZanguMapper.delete(atmWrapper);
                //删除子表，附件表
                deleteItem(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .in(SysTodoTask::getDocumentSid, customerMonthAccountBillSids));
        }
        return i;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public SalCustomerMonthAccountBill setConfirmedInfo(SalCustomerMonthAccountBill entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
        }
        return entity;
    }

    /**
     * 新增/编辑直接提交客户对账单
     *
     * @param purPurchasePrice 客户对账单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(SalCustomerMonthAccountBill salCustomerMonthAccountBill, String jump) {
        int row = 0;
        AjaxResult result = AjaxResult.success();
        if (salCustomerMonthAccountBill.getCustomerMonthAccountBillSid() == null) {
            // 新建
            row = this.insertSalCustomerMonthAccountBill(salCustomerMonthAccountBill);
        }
        else {
            row = this.updateSalCustomerMonthAccountBill(salCustomerMonthAccountBill);
        }
        if (row == 1) {
            List<Long> sidList = new ArrayList<Long>(){{add(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());}};
            if (CollectionUtil.isNotEmpty(salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList())) {
                // 提交
                Submit submit = new Submit();
                submit.setFormType(FormType.SalePrice.getCode());
                List<FormParameter> formParameters = new ArrayList<>();
                salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList().forEach(item->{
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(String.valueOf(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid()));
                    formParameter.setFormId(String.valueOf(item.getCustomerMonthAccountBillZanguSid()));
                    formParameter.setFormCode(String.valueOf(salCustomerMonthAccountBill.getCustomerMonthAccountBillCode()));
                    formParameters.add(formParameter);
                });
                submit.setFormParameters(formParameters);
                submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
                workflowService.submitByItem(submit);
            }
        }
        return AjaxResult.success(result.get(AjaxResult.DATA_TAG));
    }

    /**
     * 提交校验
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processCheck(SalCustomerMonthAccountBill salCustomerMonthAccountBill){
        String status = salCustomerMonthAccountBill.getTjzt();
        int row = 1;
        if(status.equals("XJ") || status.equals("BJ")){
            List<SalCustomerMonthAccountBillZangu> salCustomerMonthAccountBillZanguList =
                    salCustomerMonthAccountBill.getSalCustomerMonthAccountBillZanguList();
            if(salCustomerMonthAccountBillZanguList == null || salCustomerMonthAccountBillZanguList.size() == 0){
                throw new BaseException("出入库明细不能为空！");
            }
            for (SalCustomerMonthAccountBillZangu item:salCustomerMonthAccountBillZanguList) {
                if (!item.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_N)){
                    row = 2;
                    throw new BaseException("存在暂估流水已对账！");
                }
            }
        }else if(status.equals("TJ")){
            Long[] sidList = salCustomerMonthAccountBill.getCustomerMonthAccountBillSidList();
            for (Long sid:sidList) {
                List<SalCustomerMonthAccountBillZangu> salCustomerMonthAccountBillZanguList =
                        salCustomerMonthAccountBillZanguMapper.getReportForm(new SalCustomerMonthAccountBillZangu()
                                .setCustomerMonthAccountBillSid(sid));
                if(salCustomerMonthAccountBillZanguList == null || salCustomerMonthAccountBillZanguList.size() == 0){
                    throw new BaseException("出入库明细不能为空！");
                }
                for (SalCustomerMonthAccountBillZangu item:salCustomerMonthAccountBillZanguList) {
                    if (!item.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_N)){
                        row = 3;
                        throw new BaseException("存在暂估流水已对账！");
                    }
                }
            }
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(SalCustomerMonthAccountBill order) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, order.getCustomerMonthAccountBillSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, order.getCustomerMonthAccountBillSid())
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_CUSTOMER_MONTH_ACCOUNT_BILL));
        }
    }

    /**
     * 采购订单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        //采购订单sids customerMonthAccountBill
        Long[] customerMonthAccountBillSids = salCustomerMonthAccountBill.getCustomerMonthAccountBillSids();
        for (Long customerMonthAccountBillSid : customerMonthAccountBillSids) {
            SalCustomerMonthAccountBill customerMonthAccountBill = salCustomerMonthAccountBillMapper.selectById(customerMonthAccountBillSid);
            salCustomerMonthAccountBillMapper.update(new SalCustomerMonthAccountBill(), new UpdateWrapper<SalCustomerMonthAccountBill>().lambda()
                    .eq(SalCustomerMonthAccountBill::getCustomerMonthAccountBillSid, customerMonthAccountBillSid)
                    .set(SalCustomerMonthAccountBill::getConfirmDate, new Date())
                    .set(SalCustomerMonthAccountBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(SalCustomerMonthAccountBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
            );
            SalCustomerMonthAccountBill o = salCustomerMonthAccountBillMapper.selectSalCustomerMonthAccountBillById(customerMonthAccountBillSid);
            //校验是否存在待办
            checkTodoExist(o);
            SalCustomerMonthAccountBillZangu salCustomerMonthAccountBillItem = new SalCustomerMonthAccountBillZangu();
            salCustomerMonthAccountBillItem.setCustomerMonthAccountBillSid(customerMonthAccountBillSid);
            List<SalCustomerMonthAccountBillZangu> salCustomerMonthAccountBillItemList = salCustomerMonthAccountBillZanguMapper.selectSalCustomerMonthAccountBillZanguList(salCustomerMonthAccountBillItem);
            o.setSalCustomerMonthAccountBillZanguList(salCustomerMonthAccountBillItemList);
            if (ConstantsEms.SAVA_STATUS.equals(customerMonthAccountBill.getHandleStatus())) {
                MongodbUtil.insertUserLog(o.getCustomerMonthAccountBillSid(), BusinessType.CONFIRM.getValue(), TITLE);
            }
        }
        return 1;
    }

    /**
     * 删除子表
     */
    public void deleteItem(Long sid) {
        //附件表
        QueryWrapper<SalCustomerMonthAccountBillAttach> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("customer_month_account_bill_sid", sid);
        salCustomerMonthAccountBillAttachMapper.delete(atmWrapper);
    }

    /**
     * 添加子表
     *
     * @param atmList
     * @param sid
     */
    public void insertChild(List<SalCustomerMonthAccountBillAttach> atmList, Long sid) {
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setCustomerMonthAccountBillSid(sid);
            });
            salCustomerMonthAccountBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 删除子表
     */
    public void deleteZangu(Long sid) {
        //附件表
        QueryWrapper<SalCustomerMonthAccountBillZangu> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("customer_month_account_bill_sid", sid);
        SalCustomerMonthAccountBill item =  selectSalCustomerMonthAccountBillById(sid);
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())) {
            if (item.getSalCustomerMonthAccountBillZanguList() != null &&
                    item.getSalCustomerMonthAccountBillZanguList().size() > 0) {
                // 明细改为 否
                List<Long> sidList = item.getSalCustomerMonthAccountBillZanguList().stream()
                        .map(SalCustomerMonthAccountBillZangu::getBookReceiptEstimationItemSid) // 提取 sid
                        .collect(Collectors.toList()); // 收集为 List
                finBookReceiptEstimationItemMapper.update(null, new UpdateWrapper<FinBookReceiptEstimationItem>().lambda()
                        .in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, sidList)
                        .set(FinBookReceiptEstimationItem::getIsBusinessVerify, ConstantsEms.NO));
            }
        }
        salCustomerMonthAccountBillZanguMapper.delete(atmWrapper);
    }

    /**
     * 添加子表
     *
     * @param atmList
     * @param sid
     */
    public void insertChildZangu(List<SalCustomerMonthAccountBillZangu> atmList, Long sid) {
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setCustomerMonthAccountBillSid(sid);
            });
            salCustomerMonthAccountBillZanguMapper.inserts(atmList);
        }
    }

    /**
     * 更改确认状态供应商对账单
     *
     * @param salCustomerMonthAccountBill
     * @return
     */
    @Override
    public int check(SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        int row = 0;
        Long[] sids = salCustomerMonthAccountBill.getCustomerMonthAccountBillSidList();
        if (sids != null && sids.length > 0) {
            row = salCustomerMonthAccountBillMapper.update(null, new UpdateWrapper<SalCustomerMonthAccountBill>().lambda().set(SalCustomerMonthAccountBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(SalCustomerMonthAccountBill::getCustomerMonthAccountBillSid, sids));
            for (Long id : sids) {
                SalCustomerMonthAccountBill bill = selectSalCustomerMonthAccountBillById(id);
                if(bill.getSalCustomerMonthAccountBillZanguList() != null &&
                        bill.getSalCustomerMonthAccountBillZanguList().size() > 0) {
                    // 明细改为 是
                    List<Long> sidList = bill.getSalCustomerMonthAccountBillZanguList().stream()
                            .map(SalCustomerMonthAccountBillZangu::getBookReceiptEstimationItemSid) // 提取 sid
                            .collect(Collectors.toList()); // 收集为 List
                    finBookReceiptEstimationItemMapper.update(null, new UpdateWrapper<FinBookReceiptEstimationItem>().lambda()
                            .in(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid, sidList)
                            .set(FinBookReceiptEstimationItem::getIsBusinessVerify, ConstantsEms.YES));
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(salCustomerMonthAccountBill.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB).in(SysTodoTask::getDocumentSid, sids));
            }
        }
        return row;
    }


    /**
     * 导入
     *
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importData(MultipartFile file) {
        List<SalCustomerMonthAccountBill> responseList = new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
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
            List<DictData> yearDict = sysDictDataService.selectDictData("s_year"); //年份
            Map<String, String> yearMaps = yearDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> currencyDict = sysDictDataService.selectDictData("s_currency"); //币种
            Map<String, String> currencyMaps = currencyDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> currencyUnitDict = sysDictDataService.selectDictData("s_currency_unit"); //货币单位
            Map<String, String> currencyUnitMaps = currencyUnitDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                int num = i + 1;
                /**
                 * 客户编码
                 */
                String customerShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString(); //客户编码  (必填)
                String customerCode = null;
                String customerName = null;
                Long customerSid = null; //表：客户Sid
                if (StrUtil.isBlank(customerShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客户简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getShortName, customerShortName));
                        if (basCustomer == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户简称为" + customerShortName + "没有对应的客户，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(customerShortName + "对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                customerSid = basCustomer.getCustomerSid();
                                customerName = basCustomer.getCustomerName();
                                customerCode = basCustomer.getCustomerCode();
                            }
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName + "客户档案存在重复，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 公司编码
                 */
                String companyShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString(); //公司编码  (必填)
                Long companySid = null; //表：公司Sid
                String companyName = null;
                String companyCode = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName, companyShortName));
                        if (basCompany == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为" + companyShortName + "没有对应的公司，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "对应的公司必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                companySid = basCompany.getCompanySid();
                                companyName = basCompany.getCompanyName();
                                companyCode = basCompany.getCompanyCode();
                            }
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司档案存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 所属年月
                 */
                String yearMonth_s = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString(); //所属年月  (必填)
                String yearMonth = null;
                if (StrUtil.isBlank(yearMonth_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("所属年月不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isYearMonth(yearMonth_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("所属年月格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        String month_s = yearMonth_s.substring(5, 7);
                        Calendar cal = Calendar.getInstance();
                        int currentMonth = cal.get(Calendar.MONTH) + 1;
                        int month = Integer.parseInt(month_s);
                        if (!(currentMonth == month || currentMonth == month + 1)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("所属年月必须为当月或上月，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            yearMonth = yearMonth_s.replace("/", "-");
                        }
                    }
                }
                if (companySid != null && customerSid != null && yearMonth != null) {
                    if (map.get(companySid + customerSid + yearMonth) == null) {
                        map.put(companySid + customerSid + yearMonth, String.valueOf(num));
                        List<SalCustomerMonthAccountBill> list = salCustomerMonthAccountBillMapper.selectList(new QueryWrapper<SalCustomerMonthAccountBill>()
                                .lambda().eq(SalCustomerMonthAccountBill::getCompanySid, companySid)
                                .eq(SalCustomerMonthAccountBill::getCustomerSid, customerSid)
                                .eq(SalCustomerMonthAccountBill::getYearMonths, yearMonth));
                        if (CollectionUtil.isNotEmpty(list)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，已存在该月账单，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，已存在该月账单，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 *  上期余额
                 */
                String yueQichu_s = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString(); //金额  (必填)
                BigDecimal yueQichu = null;
                if (StrUtil.isBlank(yueQichu_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期期初余额金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(yueQichu_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期期初余额金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yueQichu = new BigDecimal(yueQichu_s);
                    }
                }
                /**
                 *  本期到票
                 */
                String daopiao_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal daopiao = null;
                if (StrUtil.isBlank(daopiao_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期到票金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(daopiao_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期到票金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        daopiao = new BigDecimal(daopiao_s); //金额  (必填)
                    }
                }

                /**
                 *  本期付款
                 */
                String fukuan_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal fukuan = null;
                if (StrUtil.isBlank(fukuan_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期付款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(fukuan_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期付款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        fukuan = new BigDecimal(fukuan_s);//金额  (必填)
                        //不能小于0
                        if (BigDecimal.ZERO.compareTo(fukuan) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("本期付款金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  本期销售抵扣
                 */
                String xiaoshoudikou_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal xiaoshoudikou = null;
                if (StrUtil.isBlank(xiaoshoudikou_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期销售抵扣金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(xiaoshoudikou_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期销售抵扣金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        xiaoshoudikou = new BigDecimal(xiaoshoudikou_s); //金额  (必填)
                    }
                }
                /**
                 *  本期扣款
                 */
                String koukuan_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal koukuan = null;
                if (StrUtil.isBlank(koukuan_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期扣款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(objects.get(7).toString(), 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期扣款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        koukuan = new BigDecimal(koukuan_s); //金额  (必填)
                        if (koukuan.compareTo(BigDecimal.ZERO) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("本期扣款金额不能大于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  本期调账
                 */
                String tiaozhang_s = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                BigDecimal tiaozhang = null;
                if (StrUtil.isBlank(tiaozhang_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期调账金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(tiaozhang_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期调账金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        tiaozhang = new BigDecimal(tiaozhang_s); //金额  (必填)
                    }
                }
                /**
                 *  本期余额
                 */
                String yueQimo_s = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString(); //金额  (必填)
                BigDecimal yueQimo = null;
                if (StrUtil.isBlank(yueQimo_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("本期期末余额金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(yueQimo_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期期末余额金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yueQimo = new BigDecimal(yueQimo_s);
                    }
                }
                /**
                 *  押金
                 */
                String yajin_s = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal yajin = null;
                if (StrUtil.isBlank(yajin_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("押金金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(yajin_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("押金金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yajin = new BigDecimal(yajin_s); //金额  (必填)
                    }

                }
                /**
                 *  暂押款
                 */
                String zanyakuan_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString(); //金额  (必填)
                BigDecimal zanyakuan = null;
                if (StrUtil.isBlank(zanyakuan_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("暂押款金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(zanyakuan_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("暂押款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        zanyakuan = new BigDecimal(zanyakuan_s);
                        //不能小于0
                        if (BigDecimal.ZERO.compareTo(zanyakuan) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("暂押款金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  应付暂估
                 */
                String yingfuzangu_s = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                BigDecimal yingfuzangu = null;
                if (StrUtil.isBlank(yingfuzangu_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("应付暂估金额不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(yingfuzangu_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应付暂估金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yingfuzangu = new BigDecimal(yingfuzangu_s); //金额  (必填)
                    }
                }
                /**
                 *  本期收款
                 */
                String shoukuan_s = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString(); //金额  (必填)
                BigDecimal shoukuan = null;
                if (StrUtil.isNotBlank(shoukuan_s)) {
                    if (!JudgeFormat.isValidDouble(shoukuan_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("本期收款金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        shoukuan = new BigDecimal(shoukuan_s);
                        //不能小于0
                        if (BigDecimal.ZERO.compareTo(shoukuan) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("本期收款金额不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 *  应收暂估
                 */
                String yingshouzangu_s = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString(); //金额  (必填)
                BigDecimal yingshouzangu = null;
                if (StrUtil.isNotBlank(yingshouzangu_s)) {
                    if (!JudgeFormat.isValidDouble(yingshouzangu_s, 11, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("应收暂估金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        yingshouzangu = new BigDecimal(yingshouzangu_s);
                    }
                }
                /**
                 * 备注
                 */
                String remark = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString(); //备注
                if (remark != null && remark.length() > 600) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtils.isEmpty(errMsgList)) {
                    SalCustomerMonthAccountBill salCustomerMonthAccountBill = new SalCustomerMonthAccountBill();
                    salCustomerMonthAccountBill.setInitialBalance(yueQichu)  //期初余额/上期余额金额
                            .setEndingBalance(yueQimo)            //本期余额/期末余额
                            .setKaipiao(daopiao)               //本期到票
                            .setFukuan(fukuan)                 //本期付款
                            .setShoukuan(shoukuan)      //本期收款
                            .setXiaoshoudikou(xiaoshoudikou) //本期销售抵扣
                            .setKoukuan(koukuan)               //本期扣款
                            .setTiaozhang(tiaozhang)           //本期调账
                            .setYajin(yajin)                      //押金
                            .setZanyakuan(zanyakuan)                //暂押款
                            .setYueShijijieqian(BigDecimal.ZERO)    //实际结欠余额金额
                            .setYingfuzangu(yingfuzangu)            //应付暂估
                            .setYingshouzangu(yingshouzangu);     //应收暂估
                    salCustomerMonthAccountBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setYearMonths(yearMonth);
                    salCustomerMonthAccountBill.setCompanySid(companySid).setCustomerSid(customerSid).setCustomerCode(customerCode).setCompanyCode(companyCode)
                            .setCustomerShortName(customerShortName).setCompanyShortName(companyShortName).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    salCustomerMonthAccountBill.setCompanyName(companyName).setCustomerName(customerName).setRemark(remark);
                    responseList.add(salCustomerMonthAccountBill);
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        if (CollectionUtils.isNotEmpty(errMsgList)) {
            return AjaxResult.error("导入失败", errMsgList);
        } else {
            return AjaxResult.success(responseList);
        }
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }

    @Override
    public int addForm(List<SalCustomerMonthAccountBill> list) {
        list.forEach(item -> {
            salCustomerMonthAccountBillMapper.insert(item);
        });
        return list.size();
    }


    @Override
    public void exportPur(HttpServletResponse response, SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        SalCustomerMonthAccountBill info = selectSalCustomerMonthAccountBillById(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid());
        List<SalCustomerMonthAccountBillZangu> list = info.getSalCustomerMonthAccountBillZanguList();
        int size = list.size();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 绘制excel表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("客户对账单");
            sheet.setDefaultColumnWidth(16);
            // 单元格格式
            CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
            CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
            defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
            defaultCellStyleNo.setBorderBottom(BorderStyle.NONE);
            defaultCellStyleNo.setBorderLeft(BorderStyle.NONE);
            defaultCellStyleNo.setBorderRight(BorderStyle.NONE);
            defaultCellStyleNo.setBorderTop(BorderStyle.NONE);
            // 样式 - 灰色
            XSSFColor color = new XSSFColor(new java.awt.Color(238, 236, 225));
            XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook, color);
            XSSFCellStyle cellStyleGrayLeft = ExcelStyleUtil.getXSSFCellStyle(workbook, color);
            cellStyleGrayLeft.setAlignment(HorizontalAlignment.LEFT);
            SalCustomerMonthAccountBill docment = info;
            // 每段标题
            String[] titleTipsOne = {info.getCompanyName() + "结算书", "", "", "", "", "", "", ""};
            // 获取当前月份的第一天和最后一天
            LocalDate firstDay = LocalDate.parse(info.getYearMonths() + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
            String[] titleTipsTwo = {"客户：", docment.getCustomerName(), "", "", info.getYearMonths() + "-01", "至", lastDay.toString(), ""};
            String[] titleTipsThird = {
                    "出入库日期：",
                    "物料编码",
                    "物料名称",
                    "颜色/尺码/规格型号",
                    "单位",
                    "数量",
                    "单价",
                    "金额(元)",};
            BigDecimal tatalQuantity = list.stream().filter(li -> li.getQuantity() != null).map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tatalprice = list.stream().filter(li -> li.getCurrencyAmountTax() != null && li.getPrice() != null).map(li -> li.getCurrencyAmountTax().multiply(li.getPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
            String[] titleTipsTotal = {
                    "总计：",
                    "",
                    "",
                    "",
                    "",
                    tatalQuantity.toString(),
                    "",
                    tatalprice.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_DOWN).toString()};
            Row one = sheet.createRow(0);
            one.setHeight((short) 600);
            for (int i = 0; i < titleTipsOne.length; i++) {
                Cell cell0 = one.createCell(i);
                cell0.setCellValue(titleTipsOne[i]);
                cell0.setCellStyle(defaultCellStyle);
            }
            Row two = sheet.createRow(1);
            for (int i = 0; i < titleTipsTwo.length; i++) {
                Cell twoCell0 = two.createCell(i);
                twoCell0.setCellValue(titleTipsTwo[i]);
                twoCell0.setCellStyle(defaultCellStyle);
            }
            Row third = sheet.createRow(2);
            for (int i = 0; i < titleTipsThird.length; i++) {
                Cell thirdCell = third.createCell(i);
                thirdCell.setCellValue(titleTipsThird[i]);
                thirdCell.setCellStyle(defaultCellStyle);
            }
            //数据部分
            for (int i = 0; i < list.size(); i++) {
                Row data = sheet.createRow(i + 3);
                //出入库日期
                Cell Cell1 = data.createCell(0);
                Cell1.setCellValue(sdf.format(list.get(i).getAccountDate()));
                Cell1.setCellStyle(defaultCellStyle);
                //物料编码
                Cell Cell2 = data.createCell(1);
                Cell2.setCellValue(list.get(i).getMaterialCode());
                Cell2.setCellStyle(defaultCellStyle);
                //物料名称
                Cell Cell3 = data.createCell(2);
                Cell3.setCellValue(list.get(i).getMaterialName());
                Cell3.setCellStyle(defaultCellStyle);
                //颜色/尺码/规格型号
                String type = list.get(i).getSku2Name() != null ? list.get(i).getSku1Name() + "/" + list.get(i).getSku2Name() : list.get(i).getSku1Name();
                //规格型号
                String specificationSize = list.get(i).getSpecificationSize() != null ? list.get(i).getSpecificationSize() : "";
                String modelSize = list.get(i).getModelSize() != null ? list.get(i).getModelSize() : "";
                if (!specificationSize.equals("") || !modelSize.equals("")) {
                    type = type + "/" + specificationSize + modelSize;
                }
                Cell Cell4 = data.createCell(3);
                Cell4.setCellValue(type);
                Cell4.setCellStyle(defaultCellStyle);
                //单位
                Cell Cell5 = data.createCell(4);
                Cell5.setCellValue(list.get(i).getUnitPriceName());
                Cell5.setCellStyle(defaultCellStyle);
                //数量
                Cell Cell6 = data.createCell(5);
                Cell6.setCellValue(list.get(i).getQuantity() != null ? list.get(i).getQuantity().toString() : null);
                Cell6.setCellStyle(defaultCellStyle);
                //单价
                Cell Cell7 = data.createCell(6);
                Cell7.setCellValue(list.get(i).getPrice() != null ? list.get(i).getPrice().toString() : null);
                Cell7.setCellStyle(defaultCellStyle);
                //金额
                Cell Cell8 = data.createCell(7);
                Cell8.setCellValue(list.get(i).getPrice() != null && list.get(i).getCurrencyAmountTax() != null ? list.get(i).getPrice().multiply(list.get(i).getCurrencyAmountTax()).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP).toString() : null);
                Cell8.setCellStyle(defaultCellStyle);
            }
            Row tatalData = sheet.createRow(list.size() + 3);
            //总计
            for (int i = 0; i < titleTipsTotal.length; i++) {
                Cell Cell1 = tatalData.createCell(i);
                Cell1.setCellValue(titleTipsTotal[i]);
                if (i == 0) {
                    CellStyle style = workbook.createCellStyle();
                    style.setAlignment(HorizontalAlignment.RIGHT);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setBorderBottom(BorderStyle.THIN); //下边框
                    style.setBorderLeft(BorderStyle.THIN);//左边框
                    style.setBorderTop(BorderStyle.THIN);//上边框
                    style.setBorderRight(BorderStyle.THIN);//右边框
                    Cell1.setCellStyle(style);
                } else {
                    Cell1.setCellStyle(defaultCellStyle);
                }
            }
            String[] endOne = {
                    "上期结欠：", "", "",
                    String.valueOf((info.getInitialBalance() != null) ? info.getInitialBalance().toString() : "0.0"),
                    "","", "",""};
            // 创建行并填充数据
            Row endOneRow = sheet.createRow(list.size() + 4); // 根据需要设置行号，+4 是示例
            for (int i = 0; i < endOne.length; i++) {
                Cell cell = endOneRow.createCell(i);
                cell.setCellValue(endOne[i]);
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT); // 左对齐
                style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
                style.setBorderBottom(BorderStyle.THIN); // 下边框
                style.setBorderLeft(BorderStyle.THIN); // 左边框
                style.setBorderTop(BorderStyle.THIN); // 上边框
                style.setBorderRight(BorderStyle.THIN); // 右边框
                cell.setCellStyle(style); // 应用样式
            }
            String[] endTwo = {
                    "本期发生：", "", "",
                    String.valueOf((info.getFahuo() != null) ? info.getFahuo().toString() : "0.0"),
                    "","", "",""};
            // 创建行并填充数据
            Row endTwoRow = sheet.createRow(list.size() + 5);
            for (int i = 0; i < endTwo.length; i++) {
                Cell cell = endTwoRow.createCell(i);
                cell.setCellValue(endTwo[i]);
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT); // 左对齐
                style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
                style.setBorderBottom(BorderStyle.THIN); // 下边框
                style.setBorderLeft(BorderStyle.THIN); // 左边框
                style.setBorderTop(BorderStyle.THIN); // 上边框
                style.setBorderRight(BorderStyle.THIN); // 右边框
                cell.setCellStyle(style); // 应用样式
            }
            String[] endThree = {
                    "本期已收：", "", "", "",
                    "","", "",""};
            // 创建行并填充数据
            Row endThreeRow = sheet.createRow(list.size() + 6);
            for (int i = 0; i < endThree.length; i++) {
                Cell cell = endThreeRow.createCell(i);
                cell.setCellValue(endThree[i]);
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT); // 左对齐
                style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
                style.setBorderBottom(BorderStyle.THIN); // 下边框
                style.setBorderLeft(BorderStyle.THIN); // 左边框
                style.setBorderTop(BorderStyle.THIN); // 上边框
                style.setBorderRight(BorderStyle.THIN); // 右边框
                cell.setCellStyle(style); // 应用样式
            }
            String[] endFour = {
                    "对账调整：", "", "",
                    String.valueOf((info.getTiaozhang() != null) ? info.getTiaozhang().toString() : "0.0"),
                    "","", "",""};
            Row endFourRow = sheet.createRow(list.size() + 7);
            for (int i = 0; i < endFour.length; i++) {
                Cell cell = endFourRow.createCell(i);
                cell.setCellValue(endFour[i]);
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT); // 左对齐
                style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
                style.setBorderBottom(BorderStyle.THIN); // 下边框
                style.setBorderLeft(BorderStyle.THIN); // 左边框
                style.setBorderTop(BorderStyle.THIN); // 上边框
                style.setBorderRight(BorderStyle.THIN); // 右边框
                cell.setCellStyle(style); // 应用样式
            }
            String[] endFire = {
                    "本期结欠：", "", "",
                    String.valueOf((info.getEndingBalance() != null) ? info.getEndingBalance().toString() : "0.0"),
                    "","", "",""};
            Row endFireRow = sheet.createRow(list.size() + 8);
            for (int i = 0; i < endFire.length; i++) {
                Cell cell = endFireRow.createCell(i);
                cell.setCellValue(endFire[i]);
                CellStyle style = workbook.createCellStyle();
                style.setAlignment(HorizontalAlignment.LEFT); // 左对齐
                style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
                style.setBorderBottom(BorderStyle.THIN); // 下边框
                style.setBorderLeft(BorderStyle.THIN); // 左边框
                style.setBorderTop(BorderStyle.THIN); // 上边框
                style.setBorderRight(BorderStyle.THIN); // 右边框
                cell.setCellStyle(style); // 应用样式
            }
            // 设置合并后的单元格的样式（可选）
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
            style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
            style.setBorderBottom(BorderStyle.THIN); // 下边框
            style.setBorderLeft(BorderStyle.THIN); // 左边框
            style.setBorderTop(BorderStyle.THIN); // 上边框
            style.setBorderRight(BorderStyle.THIN); // 右边框
            CellRangeAddress region2 = new CellRangeAddress(0, 0, 0, 7);
            sheet.addMergedRegion(region2);
            CellRangeAddress region3 = new CellRangeAddress(list.size() + 3, list.size() + 3, 0, 2);
            sheet.addMergedRegion(region3);
            //居中
            Row row3 = sheet.getRow(list.size() + 3);
            if (row3 == null) row3 = sheet.createRow(list.size() + 3);
            Cell cell3 = row3.getCell(0); // 获取合并后的第一个单元格
            cell3.setCellStyle(style); // 应用样式以居中

            int startRow = list.size() + 4; // 合并开始的行
            int numRowsToMerge = 5; // 需要合并的行数
            for (int i = 0; i < numRowsToMerge; i++) {
                // 当前行的索引
                int currentRow = startRow + i;
                // 合并前3列
                CellRangeAddress region4 = new CellRangeAddress(currentRow, currentRow, 0, 2);
                sheet.addMergedRegion(region4);
                //居中
                Row row4 = sheet.getRow(currentRow);
                if (row4 == null) row4 = sheet.createRow(currentRow);
                Cell cell4 = row4.getCell(0); // 获取合并后的第一个单元格
                cell4.setCellStyle(style); // 应用样式以居中

                // 合并后5列
                CellRangeAddress region5 = new CellRangeAddress(currentRow, currentRow, 3, 7);
                sheet.addMergedRegion(region5);
                Cell cell5 = row4.getCell(3); // 获取合并后的第一个单元格
                cell5.setCellStyle(style); // 应用样式以居中
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

}


