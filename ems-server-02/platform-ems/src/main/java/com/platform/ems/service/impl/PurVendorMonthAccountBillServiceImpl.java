package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPurVendorMonthAccountBillService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.CommonUtil;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 供应商对账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class PurVendorMonthAccountBillServiceImpl extends ServiceImpl<PurVendorMonthAccountBillMapper, PurVendorMonthAccountBill> implements IPurVendorMonthAccountBillService {
    @Autowired
    private PurVendorMonthAccountBillMapper purVendorMonthAccountBillMapper;
    @Autowired
    private PurVendorMonthAccountBillAttachMapper purVendorMonthAccountBillAttachMapper;
    @Autowired
    private PurVendorMonthAccountBillZanguMapper purVendorMonthAccountBillZanguMapper;
    //应付暂估
    @Autowired
    private FinBookPaymentEstimationMapper finBookPaymentEstimationMapper;
    //预付款
    @Autowired
    private FinRecordAdvancePaymentMapper finRecordAdvancePaymentMapper;
    //发票
    @Autowired
    private FinPurchaseInvoiceMapper finPurchaseInvoiceMapper;
    //应付
    @Autowired
    private FinBookAccountPayableMapper finBookAccountPayableMapper;
    //付款
    @Autowired
    private FinBookPaymentMapper finBookPaymentMapper;
    //付款明细
    @Autowired
    private FinBookPaymentItemMapper finBookPaymentItemMapper;
    //扣款
    @Autowired
    private FinBookVendorDeductionMapper finBookVendorDeductionMapper;
    //调账
    @Autowired
    private FinBookVendorAccountAdjustMapper finBookVendorAccountAdjustMapper;
    //押金
    @Autowired
    private FinVendorCashPledgeBillItemMapper finVendorCashPledgeBillItemMapper;
    //暂押款
    @Autowired
    private FinVendorFundsFreezeBillItemMapper finVendorFundsFreezeBillItemMapper;
    //付款单
    @Autowired
    private FinPayBillMapper finPayBillMapper;
    //收款单
    @Autowired
    private FinReceivableBillMapper finReceivableBillMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IWorkFlowService workflowService;

    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;


    private static final String TITLE = "供应商对账单";

    /**
     * 查询供应商对账单
     *
     * @param vendorMonthAccountBillSid 供应商对账单ID
     * @return 供应商对账单
     */
    @Override
    public PurVendorMonthAccountBill selectPurVendorMonthAccountBillById(Long vendorMonthAccountBillSid) {
        PurVendorMonthAccountBill purVendorMonthAccountBill = purVendorMonthAccountBillMapper.selectPurVendorMonthAccountBillById(vendorMonthAccountBillSid);
        //附件
        List<PurVendorMonthAccountBillAttach> purVendorMonthAccountBillAttachList = purVendorMonthAccountBillAttachMapper.selectList(new QueryWrapper<PurVendorMonthAccountBillAttach>()
                .lambda().eq(PurVendorMonthAccountBillAttach::getVendorMonthAccountBillSid, vendorMonthAccountBillSid));
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBillAttachList)) {
            purVendorMonthAccountBill.setAttachmentList(purVendorMonthAccountBillAttachList);
        }else{
            purVendorMonthAccountBill.setAttachmentList(new ArrayList<>());
        }
        //暂估明细
        List<PurVendorMonthAccountBillZangu> purBookPaymentEstimationSidList = purVendorMonthAccountBillZanguMapper.getReportForm(new PurVendorMonthAccountBillZangu()
                        .setVendorMonthAccountBillSid(purVendorMonthAccountBill.getVendorMonthAccountBillSid()));
        if (CollectionUtils.isNotEmpty(purBookPaymentEstimationSidList)) {
            purVendorMonthAccountBill.setPurVendorMonthAccountBillZanguList(purBookPaymentEstimationSidList);
        }else{
            purVendorMonthAccountBill.setPurVendorMonthAccountBillZanguList(new ArrayList<>());
        }
        MongodbUtil.find(purVendorMonthAccountBill);
        return purVendorMonthAccountBill;
    }

    /**
     * 查询供应商对账单列表
     *
     * @param purVendorMonthAccountBill 供应商对账单
     * @return 供应商对账单
     */
    @Override
    public List<PurVendorMonthAccountBill> selectPurVendorMonthAccountBillList(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        return purVendorMonthAccountBillMapper.selectPurVendorMonthAccountBillList(purVendorMonthAccountBill);
    }

    /**
     * 新增供应商对账单
     * 需要注意编码重复校验
     *
     * @param purVendorMonthAccountBill 供应商对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurVendorMonthAccountBill(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        List<PurVendorMonthAccountBill> one = purVendorMonthAccountBillMapper.selectList(new QueryWrapper<PurVendorMonthAccountBill>()
                .lambda().eq(PurVendorMonthAccountBill::getVendorSid, purVendorMonthAccountBill.getVendorSid())
                .eq(PurVendorMonthAccountBill::getCompanySid, purVendorMonthAccountBill.getCompanySid())
                .eq(PurVendorMonthAccountBill::getYearMonths, purVendorMonthAccountBill.getYearMonths())
                .eq(PurVendorMonthAccountBill::getMaterialType, purVendorMonthAccountBill.getMaterialType()));
        if (CollectionUtil.isNotEmpty(one)) {
            throw new BaseException("对账单已存在！");
        }
        //设置确认信息，校验
        setConfirmedInfo(purVendorMonthAccountBill);
        int row = purVendorMonthAccountBillMapper.insert(purVendorMonthAccountBill);
        if (row > 0) {
            //插入子表，暂估明细表
            insertChildZangu(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList(), purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入子表，附件表
            insertChild(purVendorMonthAccountBill.getAttachmentList(), purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            //待办通知
            if (ConstantsEms.SAVA_STATUS.equals(purVendorMonthAccountBill.getHandleStatus())) {
                PurVendorMonthAccountBill mx = purVendorMonthAccountBillMapper.selectOne(new QueryWrapper<PurVendorMonthAccountBill>().lambda()
                        .eq(PurVendorMonthAccountBill::getVendorMonthAccountBillSid,purVendorMonthAccountBill.getVendorMonthAccountBillSid()));
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_pur_vendor_month_account_bill")
                        .setDocumentSid(mx.getVendorMonthAccountBillSid());
                sysTodoTask.setTitle("供应对账单: " + mx.getVendorMonthAccountBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(mx.getVendorMonthAccountBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            if(ConstantsEms.CHECK_STATUS.equals(purVendorMonthAccountBill.getHandleStatus())){
                if(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList() !=null &&
                        purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().size() > 0){
                    // 明细改为 是
                    List<Long> sidList = purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().stream()
                            .map(PurVendorMonthAccountBillZangu::getBookPaymentEstimationItemSid) // 提取 sid
                            .collect(Collectors.toList()); // 收集为 List
                    finBookPaymentEstimationItemMapper.update(null, new UpdateWrapper<FinBookPaymentEstimationItem>().lambda()
                            .in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, sidList)
                            .set(FinBookPaymentEstimationItem::getIsBusinessVerify, ConstantsEms.YES));
                }
                MongodbUtil.insertUserLog(purVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 修改供应商对账单
     *
     * @param purVendorMonthAccountBill 供应商对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurVendorMonthAccountBill(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(purVendorMonthAccountBill);
        PurVendorMonthAccountBill response = purVendorMonthAccountBillMapper.selectPurVendorMonthAccountBillById(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
        int row = purVendorMonthAccountBillMapper.updateAllById(purVendorMonthAccountBill);
        if (row > 0) {
            //删除子表，暂估明细表
            deleteZangu(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入子表，暂估明细表
            insertChildZangu(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList(), purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //删除子表，附件表
            deleteItem(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入子表，附件表
            insertChild(purVendorMonthAccountBill.getAttachmentList(), purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(purVendorMonthAccountBill.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getDocumentSid, purVendorMonthAccountBill.getVendorMonthAccountBillSid()));
                if(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList() !=null &&
                        purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().size() > 0){
                    // 明细改为 是
                    List<Long> sidList = purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().stream()
                            .map(PurVendorMonthAccountBillZangu::getBookPaymentEstimationItemSid) // 提取 sid
                            .collect(Collectors.toList()); // 收集为 List
                    finBookPaymentEstimationItemMapper.update(null, new UpdateWrapper<FinBookPaymentEstimationItem>().lambda()
                            .in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, sidList)
                            .set(FinBookPaymentEstimationItem::getIsBusinessVerify, ConstantsEms.YES));
                }
                //插入日志
                MongodbUtil.insertUserLog(purVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.CHECK.getValue(), response, purVendorMonthAccountBill, TITLE);
            }else{
                //插入日志
                MongodbUtil.insertUserLog(purVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.UPDATE.getValue(), response, purVendorMonthAccountBill, TITLE);
            }
        }
        return row;
    }

    /**
     * 变更供应商对账单
     *
     * @param purVendorMonthAccountBill 供应商对账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurVendorMonthAccountBill(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        //设置确认信息，校验
        setConfirmedInfo(purVendorMonthAccountBill);
        PurVendorMonthAccountBill response = purVendorMonthAccountBillMapper.selectPurVendorMonthAccountBillById(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
        int row = purVendorMonthAccountBillMapper.updateAllById(purVendorMonthAccountBill);
        if (row > 0) {
            if(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList() == null
                    || purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().size() == 0){
                throw new BaseException("出入库明细不能为空！");
            }
            //删除子表，暂估明细表
            deleteZangu(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            if(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList() !=null &&
                    purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().size() > 0){
                // 明细改为 是
                List<Long> sidList = purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().stream()
                        .map(PurVendorMonthAccountBillZangu::getBookPaymentEstimationItemSid) // 提取 sid
                        .collect(Collectors.toList()); // 收集为 List
                finBookPaymentEstimationItemMapper.update(null,new UpdateWrapper<FinBookPaymentEstimationItem>().lambda()
                        .in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid,sidList)
                        .set(FinBookPaymentEstimationItem::getIsBusinessVerify,ConstantsEms.YES));
            }
            //插入子表，暂估明细表
            insertChildZangu(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList(), purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //删除子表，附件表
            deleteItem(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入子表，附件表
            insertChild(purVendorMonthAccountBill.getAttachmentList(), purVendorMonthAccountBill.getVendorMonthAccountBillSid());
            //插入日志
            MongodbUtil.insertUserLog(purVendorMonthAccountBill.getVendorMonthAccountBillSid(), BusinessType.CHANGE.getValue(), response, purVendorMonthAccountBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商对账单
     *
     * @param vendorMonthAccountBillSids 需要删除的供应商对账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurVendorMonthAccountBillByIds(List<Long> vendorMonthAccountBillSids) {
        int i = purVendorMonthAccountBillMapper.deleteBatchIds(vendorMonthAccountBillSids);
        if (i > 0) {
            vendorMonthAccountBillSids.forEach(sid -> {
                //删除子表，暂估明细表
                QueryWrapper<PurVendorMonthAccountBillZangu> atmWrapper = new QueryWrapper<>();
                atmWrapper.eq("vendor_month_account_bill_sid", sid);
                purVendorMonthAccountBillZanguMapper.delete(atmWrapper);
                //删除子表，附件表
                deleteItem(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .in(SysTodoTask::getDocumentSid, vendorMonthAccountBillSids));
        }
        return i;
    }

    /**
     * 更改确认状态供应商对账单
     *
     * @param purVendorMonthAccountBill
     * @return
     */
    @Override
    public int check(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        int row = 0;
        Long[] sids = purVendorMonthAccountBill.getVendorMonthAccountBillSidList();
        if (sids != null && sids.length > 0) {
            row = purVendorMonthAccountBillMapper.update(null, new UpdateWrapper<PurVendorMonthAccountBill>().lambda().set(PurVendorMonthAccountBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(PurVendorMonthAccountBill::getVendorMonthAccountBillSid, sids));
            for (Long id : sids) {
                PurVendorMonthAccountBill bill = selectPurVendorMonthAccountBillById(id);
                if (bill != null) {
                    selectInvoiceAndPayBillList(bill, new PurVendorMonthAccountBill().setVendorSid(bill.getVendorSid()).setCompanySid(bill.getCompanySid())
                            .setYearMonths(bill.getYearMonths()));
                    updateIsFinanceVerify(bill);
                    if(bill.getPurVendorMonthAccountBillZanguList() !=null &&
                            bill.getPurVendorMonthAccountBillZanguList().size() > 0){
                        // 明细改为 是
                        List<Long> sidList = bill.getPurVendorMonthAccountBillZanguList().stream()
                                .map(PurVendorMonthAccountBillZangu::getBookPaymentEstimationItemSid) // 提取 sid
                                .collect(Collectors.toList()); // 收集为 List
                        finBookPaymentEstimationItemMapper.update(null, new UpdateWrapper<FinBookPaymentEstimationItem>().lambda()
                                .in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, sidList)
                                .set(FinBookPaymentEstimationItem::getIsBusinessVerify, ConstantsEms.YES));
                    }
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(purVendorMonthAccountBill.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB).in(SysTodoTask::getDocumentSid, sids));
            }
        }
        return row;
    }


    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public PurVendorMonthAccountBill setConfirmedInfo(PurVendorMonthAccountBill entity) {
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
            updateIsFinanceVerify(entity);
        }
        return entity;
    }

    /**
     * 新增/编辑直接提交供应商对账单
     *
     * @param purPurchasePrice 供应商对账单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(PurVendorMonthAccountBill purVendorMonthAccountBill, String jump) {
        int row = 0;
        AjaxResult result = AjaxResult.success();
        if (purVendorMonthAccountBill.getVendorMonthAccountBillSid() == null) {
            // 新建
            row = this.insertPurVendorMonthAccountBill(purVendorMonthAccountBill);
        }
        else {
            row = this.updatePurVendorMonthAccountBill(purVendorMonthAccountBill);
        }
        if (row == 1) {
            List<Long> sidList = new ArrayList<Long>(){{add(purVendorMonthAccountBill.getVendorMonthAccountBillSid());}};
            if (CollectionUtil.isNotEmpty(purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList())) {
                // 提交
                Submit submit = new Submit();
                submit.setFormType(FormType.SalePrice.getCode());
                List<FormParameter> formParameters = new ArrayList<>();
                purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList().forEach(item->{
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(String.valueOf(purVendorMonthAccountBill.getVendorMonthAccountBillSid()));
                    formParameter.setFormId(String.valueOf(item.getVendorMonthAccountBillZanguSid()));
                    formParameter.setFormCode(String.valueOf(purVendorMonthAccountBill.getVendorMonthAccountBillCode()));
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
    public int processCheck(PurVendorMonthAccountBill purVendorMonthAccountBill){
        String status = purVendorMonthAccountBill.getTjzt();
        int row = 1;
        if(status.equals("XJ") || status.equals("BJ")){
            List<PurVendorMonthAccountBillZangu> purVendorMonthAccountBillZanguList =
                    purVendorMonthAccountBill.getPurVendorMonthAccountBillZanguList();
            if(purVendorMonthAccountBillZanguList == null || purVendorMonthAccountBillZanguList.size() == 0){
                throw new BaseException("出入库明细不能为空！");
            }
            for (PurVendorMonthAccountBillZangu item:purVendorMonthAccountBillZanguList) {
                if (!item.getIsBusinessVerify().equals(ConstantsEms.YES_OR_NO_N)){
                    row = 2;
                    throw new BaseException("存在暂估流水已对账！");
                }
            }
        }else if(status.equals("TJ")){
            Long[] sidList = purVendorMonthAccountBill.getVendorMonthAccountBillSidList();
            for (Long sid:sidList) {
                List<PurVendorMonthAccountBillZangu> purVendorMonthAccountBillZanguList =
                        purVendorMonthAccountBillZanguMapper.getReportForm(new PurVendorMonthAccountBillZangu()
                                .setVendorMonthAccountBillSid(sid));
                if(purVendorMonthAccountBillZanguList == null || purVendorMonthAccountBillZanguList.size() == 0){
                    throw new BaseException("出入库明细不能为空！");
                }
                for (PurVendorMonthAccountBillZangu item:purVendorMonthAccountBillZanguList) {
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
    private void checkTodoExist(PurVendorMonthAccountBill order) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, order.getVendorMonthAccountBillSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, order.getVendorMonthAccountBillSid())
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_VENDOR_MONTH_ACCOUNT_BILL));
        }
    }

    /**
     * 采购订单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        //采购订单sids vendorMonthAccountBill
        Long[] vendorMonthAccountBillSids = purVendorMonthAccountBill.getVendorMonthAccountBillSids();
        for (Long vendorMonthAccountBillSid : vendorMonthAccountBillSids) {
            PurVendorMonthAccountBill vendorMonthAccountBill = purVendorMonthAccountBillMapper.selectById(vendorMonthAccountBillSid);
            purVendorMonthAccountBillMapper.update(new PurVendorMonthAccountBill(), new UpdateWrapper<PurVendorMonthAccountBill>().lambda()
                    .eq(PurVendorMonthAccountBill::getVendorMonthAccountBillSid, vendorMonthAccountBillSid)
                    .set(PurVendorMonthAccountBill::getConfirmDate, new Date())
                    .set(PurVendorMonthAccountBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PurVendorMonthAccountBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
            );
            PurVendorMonthAccountBill o = selectPurVendorMonthAccountBillById(vendorMonthAccountBillSid);
            //校验是否存在待办
            checkTodoExist(o);
            PurVendorMonthAccountBillZangu purVendorMonthAccountBillItem = new PurVendorMonthAccountBillZangu();
            purVendorMonthAccountBillItem.setVendorMonthAccountBillSid(vendorMonthAccountBillSid);
            List<PurVendorMonthAccountBillZangu> purVendorMonthAccountBillItemList = purVendorMonthAccountBillZanguMapper.selectPurVendorMonthAccountBillZanguList(purVendorMonthAccountBillItem);
            o.setPurVendorMonthAccountBillZanguList(purVendorMonthAccountBillItemList);
            if (ConstantsEms.SAVA_STATUS.equals(vendorMonthAccountBill.getHandleStatus())) {
                MongodbUtil.insertUserLog(o.getVendorMonthAccountBillSid(), BusinessType.CONFIRM.getValue(), TITLE);
            }
        }
        return 1;
    }

    private void updateIsFinanceVerify(PurVendorMonthAccountBill entity) {
        if (CollectionUtils.isNotEmpty(entity.getPayBillList())) {
            List<Long> sidList = entity.getPayBillList().stream().map(FinPayBill::getPayBillSid).collect(Collectors.toList());
            finPayBillMapper.update(null, new UpdateWrapper<FinPayBill>().lambda()
                    .set(FinPayBill::getIsFinanceVerify, ConstantsEms.YES).in(FinPayBill::getPayBillSid, sidList));
        }
        if (CollectionUtils.isNotEmpty(entity.getInvoiceList())) {
            List<Long> sidList = entity.getInvoiceList().stream().map(FinPurchaseInvoice::getPurchaseInvoiceSid).collect(Collectors.toList());
            finPurchaseInvoiceMapper.update(null, new UpdateWrapper<FinPurchaseInvoice>().lambda()
                    .set(FinPurchaseInvoice::getIsFinanceVerify, ConstantsEms.YES).in(FinPurchaseInvoice::getPurchaseInvoiceSid, sidList));
        }
    }

    /**
     * 删除子表
     */
    public void deleteItem(Long sid) {
        //附件表
        QueryWrapper<PurVendorMonthAccountBillAttach> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("vendor_month_account_bill_sid", sid);
        purVendorMonthAccountBillAttachMapper.delete(atmWrapper);
    }

    /**
     * 添加子表
     *
     * @param atmList
     * @param sid
     */
    public void insertChild(List<PurVendorMonthAccountBillAttach> atmList, Long sid) {
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setVendorMonthAccountBillSid(sid);
            });
            purVendorMonthAccountBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 删除子表
     */
    public void deleteZangu(Long sid) {
        //附件表
        QueryWrapper<PurVendorMonthAccountBillZangu> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("vendor_month_account_bill_sid", sid);
        PurVendorMonthAccountBill item = selectPurVendorMonthAccountBillById(sid);
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())) {
            if (item.getPurVendorMonthAccountBillZanguList() == null || item.getPurVendorMonthAccountBillZanguList().size() == 0){
                throw new BaseException("出入库明细不能为空！");
            }
            if (item.getPurVendorMonthAccountBillZanguList() != null &&
                    item.getPurVendorMonthAccountBillZanguList().size() > 0) {
                // 明细改为 否
                List<Long> sidList = item.getPurVendorMonthAccountBillZanguList().stream()
                        .map(PurVendorMonthAccountBillZangu::getBookPaymentEstimationItemSid) // 提取 sid
                        .collect(Collectors.toList()); // 收集为 List
                finBookPaymentEstimationItemMapper.update(null, new UpdateWrapper<FinBookPaymentEstimationItem>().lambda()
                        .in(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid, sidList)
                        .set(FinBookPaymentEstimationItem::getIsBusinessVerify, ConstantsEms.NO));
            }
        }
        purVendorMonthAccountBillZanguMapper.delete(atmWrapper);
    }

    /**
     * 添加子表
     *
     * @param atmList
     * @param sid
     */
    public void insertChildZangu(List<PurVendorMonthAccountBillZangu> atmList, Long sid) {
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setVendorMonthAccountBillSid(sid);
            });
            purVendorMonthAccountBillZanguMapper.inserts(atmList);
        }
    }

    /**
     * 设置明细
     *
     * @param atmList
     * @param sid
     */
    public List<PurVendorMonthAccountBillZangu> setZanguList(List<FinBookPaymentEstimation> atmList, List<PurVendorMonthAccountBillZangu> mxList) {
        for (PurVendorMonthAccountBillZangu mx : mxList) {
            for (FinBookPaymentEstimation item : atmList) {
                if (mx.getBookPaymentEstimationCode() == item.getBookPaymentEstimationCode()) {
                    mx.setBookSourceCategoryName(item.getBookSourceCategoryName())
                            .setAccountDate(item.getAccountDate())
                            .setMaterialCode(item.getMaterialCode())
                            .setMaterialName(item.getMaterialName())
                            .setSku1Name(item.getSku1Name())
                            .setSku2Name(item.getSku2Name())
                            .setUnitPriceName(item.getUnitPriceName())
                            .setQuantity(item.getQuantity())
                            .setPriceTax(item.getPriceTax())
                            .setPrice(item.getPrice())
                            .setCurrencyAmountTax(item.getCurrencyAmountTax())
                            .setTaxRateName(item.getTaxRateName())
                            .setPurchaseContractCode(item.getPurchaseContractCode())
                            .setContractDate(item.getContractDate())
                            .setProductSeasonName(item.getProductSeasonName())
                            .setPurchaseMode(item.getPurchaseMode())
                            .setVendorName(item.getVendorName())
                            .setPurchaseOrderCode(item.getPurchaseOrderCode())
                            .setBookTypeName(item.getBookTypeName())
                            .setDeliveryNoteCode(item.getDeliveryNoteCode())
                            .setMaterialTypeName(item.getMaterialTypeName())
                            .setInventoryDocumentCode(item.getInventoryDocumentCode())
                            .setBusinessVerifyPeriod(item.getBusinessVerifyPeriod())
                            .setStorehouseOperatorName(item.getStorehouseOperatorName());
                }
            }
        }
        return mxList;
    }

    /**
     * 供应商对账单新建入口
     *
     * @param purVendorMonthAccountBill
     * @return 供应商对账单
     */
    @Override
    public PurVendorMonthAccountBill entrance(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        //查询供应商对账单明细信息
        purVendorMonthAccountBill = selectItemList(purVendorMonthAccountBill);
        //计算账单总览金额
        purVendorMonthAccountBill = calculationAmount(purVendorMonthAccountBill);
        return purVendorMonthAccountBill;
    }

    /**
     * 查询供应商对账单明细信息 : 本期到票(发票)  +  本期付款
     *
     * @param purVendorMonthAccountBill
     * @return 供应商对账单
     */
    private void selectInvoiceAndPayBillList(PurVendorMonthAccountBill request, PurVendorMonthAccountBill purVendorMonthAccountBill) {
        //本期到票(发票):显示此供应商的应付财务流水的“月账单所属期间”等于月账单的”所属年月”的应付财务流水的明细
        FinPurchaseInvoice invoice = new FinPurchaseInvoice().setVendorSid(purVendorMonthAccountBill.getVendorSid()).setCompanySid(purVendorMonthAccountBill.getCompanySid())
                .setMonthAccountPeriod(purVendorMonthAccountBill.getYearMonths()).setIsFinanceVerify(ConstantsEms.NO);
        invoice.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()});
        List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectFinPurchaseInvoiceList(invoice);
        request.setInvoiceList(invoiceList);
        if (invoiceList == null) {
            request.setInvoiceList(new ArrayList<>());
        }
        // 本期付款
        FinPayBill payBill = new FinPayBill().setCompanySid(purVendorMonthAccountBill.getCompanySid()).setVendorSid(purVendorMonthAccountBill.getVendorSid())
                .setPaymentStatus(ConstantsFinance.PAYMENT_STATUS_YZF).setIsFinanceVerify(ConstantsEms.NO)
                .setMonthAccountPeriod(purVendorMonthAccountBill.getYearMonths());
        List<FinPayBill> billList = finPayBillMapper.selectFinPayBillList(payBill);
        request.setPayBillList(billList);
        if (billList == null) {
            request.setPayBillList(new ArrayList<>());
        }
    }

    /**
     * 查询供应商对账单明细信息
     *
     * @param purVendorMonthAccountBill
     * @return 供应商对账单
     */
    @Override
    public PurVendorMonthAccountBill selectItemList(PurVendorMonthAccountBill request) {
        PurVendorMonthAccountBill purVendorMonthAccountBill = new PurVendorMonthAccountBill();
        purVendorMonthAccountBill.setVendorSid(request.getVendorSid()).setCompanySid(request.getCompanySid())
                .setYearMonths(request.getYearMonths());

        //应付暂估:显示此供应商的核销状态不是”全部核销“的应付暂估流水明细
        String[] clearStatus = new String[]{ConstantsFinance.CLEAR_STATUS_BFHX, ConstantsFinance.CLEAR_STATUS_WHX};
        FinBookPaymentEstimation estimation = new FinBookPaymentEstimation().setVendorSid(purVendorMonthAccountBill.getVendorSid()).setCompanySid(purVendorMonthAccountBill.getCompanySid())
                .setClearStatusMoneyList(clearStatus);
        List<FinBookPaymentEstimation> finBookPaymentEstimationList = finBookPaymentEstimationMapper.getReportForm(estimation);
        request.setBookPaymentEstimationList(finBookPaymentEstimationList);
        if (finBookPaymentEstimationList == null) {
            request.setBookPaymentEstimationList(new ArrayList<>());
        }
        //本期到票(发票)  +  付款
        selectInvoiceAndPayBillList(request, purVendorMonthAccountBill);

        //本期扣款:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“扣款”的扣款明细
        List<PurVendorMonthAccountBillKkInfo> finBookVendorDeductionList = new ArrayList<>();
        purVendorMonthAccountBill.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()});  // 已确认和已红冲
        purVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        List<PurVendorMonthAccountBillKkInfo> finBookVendorDeductionListFp = purVendorMonthAccountBillMapper.selectDeductionItemListFp(purVendorMonthAccountBill);
        purVendorMonthAccountBill.setHandleStatusList(null);
        purVendorMonthAccountBill.setPaymentStatus(ConstantsFinance.PAYMENT_STATUS_YZF); // 已支付
        List<PurVendorMonthAccountBillKkInfo> finBookVendorDeductionListFk = purVendorMonthAccountBillMapper.selectDeductionItemListFk(purVendorMonthAccountBill);
        purVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        purVendorMonthAccountBill.setPaymentStatus(null);
        purVendorMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);  //已确认
        List<PurVendorMonthAccountBillKkInfo> finBookVendorDeductionListHd = purVendorMonthAccountBillMapper.selectDeductionItemListHd(purVendorMonthAccountBill);
        purVendorMonthAccountBill.setHandleStatus(null);
        if (finBookVendorDeductionListFp != null) {
            finBookVendorDeductionList.addAll(finBookVendorDeductionListFp);
        }
        if (finBookVendorDeductionListFk != null) {
            finBookVendorDeductionList.addAll(finBookVendorDeductionListFk);
        }
        if (finBookVendorDeductionListHd != null) {
            finBookVendorDeductionList.addAll(finBookVendorDeductionListHd);
        }
        request.setDeductionList(finBookVendorDeductionList);

        //本期调账:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“调账”的调账明细
        List<PurVendorMonthAccountBillTzInfo> finBookVendorAdjustList = new ArrayList<>();
        purVendorMonthAccountBill.setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(), HandleStatus.REDDASHED.getCode()});  // 已确认和已红冲
        purVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        List<PurVendorMonthAccountBillTzInfo> finBookVendorAdjustListFp = purVendorMonthAccountBillMapper.selectAdjustItemListFp(purVendorMonthAccountBill);
        purVendorMonthAccountBill.setHandleStatusList(null);
        purVendorMonthAccountBill.setPaymentStatus(ConstantsFinance.PAYMENT_STATUS_YZF); // 已支付
        List<PurVendorMonthAccountBillTzInfo> finBookVendorAdjustListFk = purVendorMonthAccountBillMapper.selectAdjustItemListFk(purVendorMonthAccountBill);
        purVendorMonthAccountBill.setIsFinanceVerify(ConstantsEms.NO);
        purVendorMonthAccountBill.setPaymentStatus(null);
        purVendorMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);  //已确认
        List<PurVendorMonthAccountBillTzInfo> finBookVendorAdjustListHd = purVendorMonthAccountBillMapper.selectAdjustItemListHd(purVendorMonthAccountBill);
        purVendorMonthAccountBill.setHandleStatus(null);
        if (finBookVendorAdjustListFp != null) {
            finBookVendorAdjustList.addAll(finBookVendorAdjustListFp);
        }
        if (finBookVendorAdjustListFk != null) {
            finBookVendorAdjustList.addAll(finBookVendorAdjustListFk);
        }
        if (finBookVendorAdjustListHd != null) {
            finBookVendorAdjustList.addAll(finBookVendorAdjustListHd);
        }
        request.setAdjustList(finBookVendorAdjustList);

        String[] returnStatusList = new String[]{ConstantsFinance.RETURN_STATUS_BFTH, ConstantsFinance.RETURN_STATUS_WTH};
        //押金:显示供应商的退回状态不是“全部退回”的押金单的明细（支付）
        List<FinVendorCashPledgeBillItem> finVendorCashPledgeBillItemListZf = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                new FinVendorCashPledgeBillItem().setReturnStatusList(returnStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(purVendorMonthAccountBill.getVendorSid())
                        .setCompanySid(purVendorMonthAccountBill.getCompanySid()));
        request.setCashPledgeListZf(finVendorCashPledgeBillItemListZf);
        if (finVendorCashPledgeBillItemListZf == null) {
            request.setCashPledgeListZf(new ArrayList<>());
        }

        //押金:显示供应商的退回状态不是“全部退回”的押金单的明细（收取）
        List<FinVendorCashPledgeBillItem> finVendorCashPledgeBillItemListSq = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                new FinVendorCashPledgeBillItem().setReturnStatusList(returnStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(purVendorMonthAccountBill.getVendorSid())
                        .setCompanySid(purVendorMonthAccountBill.getCompanySid()));
        request.setCashPledgeListSq(finVendorCashPledgeBillItemListSq);
        if (finVendorCashPledgeBillItemListSq == null) {
            request.setCashPledgeListSq(new ArrayList<>());
        }
        request.getCashPledgeListSq().addAll(request.getCashPledgeListZf());

        //暂押款:显示供应商的释放状态不是“全部释放”的暂押款单的明细
        String[] unfreezeStatusList = new String[]{ConstantsFinance.UNFREEZE_STATUS_BFJD, ConstantsFinance.UNFREEZE_STATUS_WJD};
        List<FinVendorFundsFreezeBillItem> finVendorFundsFreezeBillItemList = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(
                new FinVendorFundsFreezeBillItem().setUnfreezeStatusList(unfreezeStatusList)
                        .setDocumentType(ConstantsFinance.DOC_TYPE_FREEZE_ZYK)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(purVendorMonthAccountBill.getVendorSid())
                        .setCompanySid(purVendorMonthAccountBill.getCompanySid()));
        request.setFundsFreezeList(finVendorFundsFreezeBillItemList);
        if (finVendorFundsFreezeBillItemList == null) {
            request.setFundsFreezeList(new ArrayList<>());
        }
        return request;
    }

    /**
     * 计算账单总览金额
     *
     * @param purVendorMonthAccountBill
     * @return 供应商对账单
     */
    @Override
    public PurVendorMonthAccountBill calculationAmount(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        //本期到票:显示此供应商的应付财务流水的“月账单所属期间”等于月账单的”所属年月”的应付财务流水的“应付金额”之和
        BigDecimal daopiao = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getInvoiceList())) {
            daopiao = purVendorMonthAccountBill.getInvoiceList().parallelStream().map(FinPurchaseInvoice::getTotalCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        //本期付款:显示付款财务流水中的“月账单所属期间”等于月账单的”所属年月”的付款流水的“付款金额”之和
        BigDecimal fukuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getPayBillList())) {
            fukuan = purVendorMonthAccountBill.getPayBillList().parallelStream().map(FinPayBill::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        //本期扣款:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“扣款”的“金额”之和
        BigDecimal koukuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getDeductionList())) {
            koukuan = purVendorMonthAccountBill.getDeductionList().parallelStream().map(PurVendorMonthAccountBillKkInfo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        //本期调账:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“调账”的“金额”之和；
        BigDecimal tiaozhang = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getAdjustList())) {
            tiaozhang = purVendorMonthAccountBill.getAdjustList().parallelStream().map(PurVendorMonthAccountBillTzInfo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        //上期余额
        BigDecimal yueQichu = BigDecimal.ZERO;
        String lastYearMonth = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date orgin = sdf.parse(purVendorMonthAccountBill.getYearMonths());  //得到当前年月的Date型
            Calendar cal = Calendar.getInstance();
            cal.setTime(orgin);
            cal.add(Calendar.MONTH, -1);  //得到当前年月的上一个月
            Date last = cal.getTime();
            lastYearMonth = sdf.format(last); //得到当前年月上一个月的String型
        } catch (Exception e) {
            throw new BaseException("系统未知错误，请联系管理员");
        }
        PurVendorMonthAccountBill lastBill = null;
        try {
            lastBill = purVendorMonthAccountBillMapper.selectOne(new QueryWrapper<PurVendorMonthAccountBill>()
                    .lambda().eq(PurVendorMonthAccountBill::getVendorSid, purVendorMonthAccountBill.getVendorSid())
                    .eq(PurVendorMonthAccountBill::getCompanySid, purVendorMonthAccountBill.getCompanySid())
                    .eq(PurVendorMonthAccountBill::getYearMonths, lastYearMonth));
        } catch (Exception e) {
            throw new BaseException("汇总上期余额时出现问题，请联系管理员");
        }
        if (lastBill != null && ConstantsEms.CHECK_STATUS.equals(lastBill.getHandleStatus())) {
            yueQichu = lastBill.getEndingBalance();
        }

        //本期余额:等于：上期余额 + 本期到票 - 本期付款
        BigDecimal yueQimo = BigDecimal.ZERO;
        yueQimo = yueQichu.add(daopiao).subtract(fukuan);

        //押金:显示供应商的退回状态不是“全部退回”的押金单的“押金金额-已退回金额”之和
        BigDecimal yajinSq = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getCashPledgeListSq())) {
            yajinSq = purVendorMonthAccountBill.getCashPledgeListSq().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            yajinSq = yajinSq.subtract(purVendorMonthAccountBill.getCashPledgeListSq().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmountYth)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
        }
        BigDecimal yajinZf = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getCashPledgeListZf())) {
            yajinZf = purVendorMonthAccountBill.getCashPledgeListZf().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            yajinZf = yajinZf.subtract(purVendorMonthAccountBill.getCashPledgeListZf().parallelStream().map(FinVendorCashPledgeBillItem::getCurrencyAmountYth)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
        }
        yajinSq = yajinSq.subtract(yajinZf.multiply(new BigDecimal("2")));

        //暂押款:显示供应商的释放状态不是“全部释放”的暂押款单的“暂压金额-已释放金额”之和
        BigDecimal zanyakuan = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getFundsFreezeList())) {
            zanyakuan = purVendorMonthAccountBill.getFundsFreezeList().parallelStream().map(FinVendorFundsFreezeBillItem::getCurrencyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            zanyakuan = zanyakuan.subtract(purVendorMonthAccountBill.getFundsFreezeList().parallelStream().map(FinVendorFundsFreezeBillItem::getCurrencyAmountYsf)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
        }

        //实际结欠余额:等于：本期余额 + 押金 + 暂押款
        BigDecimal yueShijijieqian = BigDecimal.ZERO;
        yueShijijieqian = yueQimo.add(yajinSq).add(zanyakuan);

        //应收暂估:显示此供应商档案中的客户编码的核销状态不是”全部核销“的应收暂估流水明细“金额- 已核销金额”之和
        BigDecimal yingfuzangu = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(purVendorMonthAccountBill.getBookPaymentEstimationList())) {
            yingfuzangu = purVendorMonthAccountBill.getBookPaymentEstimationList().parallelStream().map(FinBookPaymentEstimation::getCurrencyAmountTax).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            yingfuzangu = yingfuzangu.subtract(purVendorMonthAccountBill.getBookPaymentEstimationList().parallelStream().map(FinBookPaymentEstimation::getCurrencyAmountTaxYhx).reduce(BigDecimal.ZERO, BigDecimalSum::sum));
        }

        purVendorMonthAccountBill.setInitialBalance(yueQichu)  //期初余额/上期余额金额
                .setEndingBalance(yueQimo)                    //本期余额/期末余额
                .setDaopiao(daopiao)               //本期到票
                .setFukuan(fukuan)                 //本期付款
                .setShoukuan(BigDecimal.ZERO)      //本期收款
                .setXiaoshoudikou(BigDecimal.ZERO) //本期销售抵扣
                .setKoukuan(koukuan)               //本期扣款
                .setTiaozhang(tiaozhang)           //本期调账
                .setYajin(yajinSq)                      //押金
                .setZanyakuan(zanyakuan)                //暂押款
                .setYueShijijieqian(yueShijijieqian)    //实际结欠余额金额
                .setYingfuzangu(yingfuzangu)            //应付暂估
                .setYingshouzangu(BigDecimal.ZERO);     //应收暂估

        //将账单总览信息也存到一张列表里，方便前端读取数据
        PurVendorMonthAccountBillInfo info = new PurVendorMonthAccountBillInfo();
        BeanCopyUtils.copyProperties(purVendorMonthAccountBill, info);
        List<PurVendorMonthAccountBillInfo> list = new ArrayList<>();
        list.add(info);
        purVendorMonthAccountBill.setInfo(list);
        return purVendorMonthAccountBill;
    }


    /**
     * 变更所属账期
     *
     * @param purVendorMonthAccountBill
     * @return 供应商台账
     */
    @Override
    public int changeYearMonth(PurVendorMonthAccountBill list) {
        /** 验证目的账期是否存在且已确认 */
        PurVendorMonthAccountBill to = purVendorMonthAccountBillMapper.selectOne(new QueryWrapper<PurVendorMonthAccountBill>()
                .lambda().eq(PurVendorMonthAccountBill::getVendorSid, list.getVendorSid())
                .eq(PurVendorMonthAccountBill::getCompanySid, list.getCompanySid())
                .eq(PurVendorMonthAccountBill::getYearMonths, list.getYearMonths()));
        if (to != null) {
            if (ConstantsEms.CHECK_STATUS.equals(to.getHandleStatus()) || ConstantsEms.SUBMIT_STATUS.equals(to.getHandleStatus())) {
                throw new BaseException("选择月份的月账单的处理状态为已确认或审批中，不允许变更账期");
            }
        }
        int row = 0;
        /** 本期到票 */
        if (FormType.PurchaseInvoice.getCode().equals(list.getFormType())) {
            List<FinPurchaseInvoice> invoiceList = finPurchaseInvoiceMapper.selectList(new QueryWrapper<FinPurchaseInvoice>()
                    .lambda().in(FinPurchaseInvoice::getPurchaseInvoiceSid, list.getSidList()));
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            invoiceList.forEach(item -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date orgin = sdf.parse(item.getMonthAccountPeriod());
                    Date toDate = sdf.parse(list.getYearMonths());
                    start.setTime(orgin);
                    end.setTime(toDate);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    if (Math.abs(result + month) > 1) {
                        throw new BaseException("变更账期仅能选择当前月或当前月的前一个月或者下个月");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            LambdaUpdateWrapper<FinPurchaseInvoice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPurchaseInvoice::getPurchaseInvoiceSid, list.getSidList()).set(FinPurchaseInvoice::getMonthAccountPeriod, list.getYearMonths());
            row = finPurchaseInvoiceMapper.update(null, updateWrapper);
        }
        /** 本期付款 */
        if (FormType.PayBill.getCode().equals(list.getFormType())) {
            List<FinPayBill> payBillList = finPayBillMapper.selectList(new QueryWrapper<FinPayBill>()
                    .lambda().in(FinPayBill::getPayBillSid, list.getSidList()));
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            payBillList.forEach(item -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date orgin = sdf.parse(item.getMonthAccountPeriod());
                    Date toDate = sdf.parse(list.getYearMonths());
                    start.setTime(orgin);
                    end.setTime(toDate);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    if (Math.abs(result + month) > 1) {
                        throw new BaseException("变更账期仅能选择当前月或当前月的前一个月或者下个月");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPayBill::getPayBillSid, list.getSidList()).set(FinPayBill::getMonthAccountPeriod, list.getYearMonths());
            row = finPayBillMapper.update(null, updateWrapper);
        }
        /** 本期收款 */
        if (FormType.ReceivableBill.getCode().equals(list.getFormType())) {
            List<FinReceivableBill> receivableBillList = finReceivableBillMapper.selectList(new QueryWrapper<FinReceivableBill>()
                    .lambda().in(FinReceivableBill::getReceivableBillSid, list.getSidList()));
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            receivableBillList.forEach(item -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date orgin = sdf.parse(item.getMonthAccountPeriod());
                    Date toDate = sdf.parse(list.getYearMonths());
                    start.setTime(orgin);
                    end.setTime(toDate);
                    int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                    int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                    if (Math.abs(result + month) > 1) {
                        throw new BaseException("变更账期仅能选择当前月或当前月的前一个月或者下个月");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinReceivableBill::getReceivableBillSid, list.getSidList()).set(FinReceivableBill::getMonthAccountPeriod, list.getYearMonths());
            row = finReceivableBillMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 查询供应商台账
     *
     * @param purVendorMonthAccountBill
     * @return 供应商台账
     */
    @Override
    public TableDataInfo selectReportList(PurVendorMonthAccountBill purVendorMonthAccountBill) {
        purVendorMonthAccountBill.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
        purVendorMonthAccountBill.setClearStatusMoney(ConstantsFinance.CLEAR_STATUS_QHX);
        purVendorMonthAccountBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        //待核销待付预付款
        List<PurVendorMonthAccountBillInfo> yufukuanList = new ArrayList<>();
        purVendorMonthAccountBill.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YFK);
        yufukuanList = purVendorMonthAccountBillMapper.getBookYFk(purVendorMonthAccountBill);
        //待核销应付暂估
        List<PurVendorMonthAccountBillInfo> yingfuzanguList = new ArrayList<>();
        yingfuzanguList = purVendorMonthAccountBillMapper.getPaymentEstimation(purVendorMonthAccountBill);
        //待核销应付款
        List<PurVendorMonthAccountBillInfo> yingfukuanList = new ArrayList<>();
        yingfukuanList = purVendorMonthAccountBillMapper.getAccountPayable(purVendorMonthAccountBill);
        //待核销扣款
        List<PurVendorMonthAccountBillInfo> koukuanList = new ArrayList<>();
        koukuanList = purVendorMonthAccountBillMapper.getBookDeduction(purVendorMonthAccountBill);
        //待核销调账
        List<PurVendorMonthAccountBillInfo> tiaozhangList = new ArrayList<>();
        tiaozhangList = purVendorMonthAccountBillMapper.getBookAdjust(purVendorMonthAccountBill);
        //待核销特殊付款
        List<PurVendorMonthAccountBillInfo> teshufukuanList = new ArrayList<>();
        purVendorMonthAccountBill.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_TSFK);
        teshufukuanList = purVendorMonthAccountBillMapper.getBookTsFk(purVendorMonthAccountBill);
        //押金（分组组合：公司+供应商+单据类型(收取，支付)）
        List<PurVendorMonthAccountBillInfo> yajinList = new ArrayList<>();
        purVendorMonthAccountBill.setDocumentTypeList(new String[]{ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ, ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF})
                .setReturnStatusList(new String[]{ConstantsFinance.RETURN_STATUS_WTH, ConstantsFinance.RETURN_STATUS_BFTH})
                .setHandleStatusList(new String[]{ConstantsEms.CHECK_STATUS});
        yajinList = purVendorMonthAccountBillMapper.getCashPledge(purVendorMonthAccountBill);
        //押金=押金收取待退回明细+押金收取退回中明细-押金支付待退回明细-押金支付退回中明细
        yajinList = yajinList.stream().collect(Collectors.toMap(PurVendorMonthAccountBillInfo::getOneKey, a -> a, (o1, o2) -> {
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(o1.getDocumentType())) {
                o1.setYajin(o1.getYajin().subtract(o2.getYajin()));
            } else if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(o2.getDocumentType())) {
                o1.setYajin(o2.getYajin().subtract(o1.getYajin()));
            }
            return o1;
        })).values().stream().collect(Collectors.toList());
        //暂押款（分组组合：公司+供应商+单据类型(暂押款，被暂押款)）
        List<PurVendorMonthAccountBillInfo> zanyakuanList = new ArrayList<>();
        purVendorMonthAccountBill.setDocumentTypeList(new String[]{ConstantsFinance.DOC_TYPE_FREEZE_ZYK, ConstantsFinance.DOC_TYPE_FREEZE_BZYK})
                .setUnfreezeStatusList(new String[]{ConstantsFinance.UNFREEZE_STATUS_WJD, ConstantsFinance.UNFREEZE_STATUS_BFJD})
                .setHandleStatusList(new String[]{ConstantsEms.CHECK_STATUS});
        zanyakuanList = purVendorMonthAccountBillMapper.getFundsFreeze(purVendorMonthAccountBill);
        //暂押款=暂押款待释放明细+暂押款释放中明细-被暂押款待释放明细-被暂押款释放中明细
        zanyakuanList = zanyakuanList.stream().collect(Collectors.toMap(PurVendorMonthAccountBillInfo::getOneKey, a -> a, (o1, o2) -> {
            if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(o1.getDocumentType())) {
                o1.setYajin(o1.getZanyakuan().subtract(o2.getZanyakuan()));
            } else if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(o2.getDocumentType())) {
                o1.setYajin(o2.getZanyakuan().subtract(o1.getZanyakuan()));
            }
            return o1;
        })).values().stream().collect(Collectors.toList());
        Map<String, PurVendorMonthAccountBillInfo> map = new HashMap<String, PurVendorMonthAccountBillInfo>();
        if (CollectionUtils.isNotEmpty(yufukuanList)) {
            yufukuanList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYufukuan(item.getYufukuan());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yingfuzanguList)) {
            yingfuzanguList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYingfuzangu(item.getYingfuzangu());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yingfukuanList)) {
            yingfukuanList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYingfukuan(item.getYingfukuan());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(koukuanList)) {
            koukuanList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setKoukuan(item.getKoukuan());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(tiaozhangList)) {
            tiaozhangList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setTiaozhang(item.getTiaozhang());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(teshufukuanList)) {
            teshufukuanList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setTeshufukuan(item.getTeshufukuan());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(yajinList)) {
            yajinList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setYajin(item.getYajin());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(zanyakuanList)) {
            zanyakuanList.forEach(item -> {
                if (map.get(item.getOneKey()) == null) {
                    map.put(item.getOneKey(), item);
                } else {
                    PurVendorMonthAccountBillInfo e = map.get(item.getOneKey());
                    e.setZanyakuan(item.getZanyakuan());
                    map.put(item.getOneKey(), e);
                }
            });
        }
        List<PurVendorMonthAccountBillInfo> response = map.values().stream().collect(Collectors.toList());
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(response.size());
        if (purVendorMonthAccountBill.getPageNum() != null && purVendorMonthAccountBill.getPageSize() != null) {
            response = CommonUtil.startPage(response, purVendorMonthAccountBill.getPageNum(), purVendorMonthAccountBill.getPageSize());
        }
        tableDataInfo.setRows(response);
        if (response == null) {
            tableDataInfo.setRows(Collections.EMPTY_LIST);
        }
        return tableDataInfo;
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
        List<PurVendorMonthAccountBill> responseList = new ArrayList<>();
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
                 * 供应商编码
                 */
                String vendorShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString(); //供应商编码  (必填)
                Long vendorCode = null;
                String vendorName = null;
                Long vendorSid = null; //表：供应商Sid
                if (StrUtil.isBlank(vendorShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>().lambda().eq(BasVendor::getShortName, vendorShortName));
                        if (basVendor == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("供应商简称为" + vendorShortName + "没有对应的供应商，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(vendorShortName + "对应的供应商必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                vendorSid = basVendor.getVendorSid();
                                vendorName = basVendor.getVendorName();
                                vendorCode = basVendor.getVendorCode();
                            }
                        }
                    } catch (Exception e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName + "供应商档案存在重复，请先检查该供应商，导入失败！");
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
                if (companySid != null && vendorSid != null && yearMonth != null) {
                    if (map.get(companySid + vendorSid + yearMonth) == null) {
                        map.put(companySid + vendorSid + yearMonth, String.valueOf(num));
                        List<PurVendorMonthAccountBill> list = purVendorMonthAccountBillMapper.selectList(new QueryWrapper<PurVendorMonthAccountBill>()
                                .lambda().eq(PurVendorMonthAccountBill::getCompanySid, companySid)
                                .eq(PurVendorMonthAccountBill::getVendorSid, vendorSid)
                                .eq(PurVendorMonthAccountBill::getYearMonths, yearMonth));
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
                    PurVendorMonthAccountBill purVendorMonthAccountBill = new PurVendorMonthAccountBill();
                    purVendorMonthAccountBill.setInitialBalance(yueQichu)  //期初余额/上期余额金额
                            .setEndingBalance(yueQimo)            //本期余额/期末余额
                            .setDaopiao(daopiao)               //本期到票
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
                    purVendorMonthAccountBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setYearMonths(yearMonth);
                    purVendorMonthAccountBill.setCompanySid(companySid).setVendorSid(vendorSid).setVendorCode(vendorCode).setCompanyCode(companyCode)
                            .setVendorShortName(vendorShortName).setCompanyShortName(companyShortName).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    purVendorMonthAccountBill.setCompanyName(companyName).setVendorName(vendorName).setRemark(remark);
                    responseList.add(purVendorMonthAccountBill);
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
    public int addForm(List<PurVendorMonthAccountBill> list) {
        list.forEach(item -> {
            purVendorMonthAccountBillMapper.insert(item);
        });
        return list.size();
    }


    @Override
    public void exportPur(HttpServletResponse response, PurVendorMonthAccountBill purVendorMonthAccountBill) {
        PurVendorMonthAccountBill info = selectPurVendorMonthAccountBillById(purVendorMonthAccountBill.getVendorMonthAccountBillSid());
        List<PurVendorMonthAccountBillZangu> list = info.getPurVendorMonthAccountBillZanguList();
        int size = list.size();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 绘制excel表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("供应商对账单");
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
            PurVendorMonthAccountBill docment = info;
            // 每段标题
            String[] titleTipsOne = {info.getCompanyName() + "结算书", "", "", "", "", "", "", ""};
            // 获取当前月份的第一天和最后一天
            LocalDate firstDay = LocalDate.parse(info.getYearMonths() + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
            String[] titleTipsTwo = {"供应商：", docment.getVendorName(), "", "", info.getYearMonths() + "-01", "至", lastDay.toString(), ""};
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
                    String.valueOf((info.getDaohuo() != null) ? info.getDaohuo().toString().toString() : "0.0"),
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
                    "本期已付：", "", "", "",
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


