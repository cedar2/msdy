package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConAccountCategory;
import com.platform.ems.plug.domain.ConPaymentMethod;
import com.platform.ems.plug.mapper.ConAccountCategoryMapper;
import com.platform.ems.plug.mapper.ConPaymentMethodMapper;
import com.platform.ems.service.IFunFundAccountService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.service.ISysDictDataService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.IFunFundRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资金流水Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class FunFundRecordServiceImpl extends ServiceImpl<FunFundRecordMapper, FunFundRecord> implements IFunFundRecordService {
    @Autowired
    private FunFundRecordMapper funFundRecordMapper;
    @Autowired
    private FunFundRecordAttachMapper funFundRecordAttachMapper;
    @Autowired
    private FunFundAccountMapper funFundAccountMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private IFunFundAccountService funFundAccountService;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ConPaymentMethodMapper conPaymentMethodMapper;

    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConAccountCategoryMapper conAccountCategoryMapper;

    private static final String TITLE = "资金流水";

    /**
     * 查询资金流水
     *
     * @param fundRecordSid 资金流水ID
     * @return 资金流水
     */
    @Override
    public FunFundRecord selectFunFundRecordById(Long fundRecordSid) {
        FunFundRecord funFundRecord = funFundRecordMapper.selectFunFundRecordById(fundRecordSid);
        // 特殊字段处理
        getData(funFundRecord);

        List<FunFundRecordAttach> attachList = funFundRecordAttachMapper.selectFunFundRecordAttachList(new FunFundRecordAttach().setFundRecordSid(fundRecordSid));
        funFundRecord.setAttachmentList(attachList);
        MongodbUtil.find(funFundRecord);
        return funFundRecord;
    }

    /**
     * 查询资金流水列表
     *
     * @param funFundRecord 资金流水
     * @return 资金流水
     */
    @Override
    public List<FunFundRecord> selectFunFundRecordList(FunFundRecord funFundRecord) {
        List<FunFundRecord> list = funFundRecordMapper.selectFunFundRecordList(funFundRecord);
        // 图片视频字段查询页面要
        if (CollectionUtil.isNotEmpty(list)) {
            for (FunFundRecord record : list) {
                getData(record);
            }
        }
        return list;
    }

    /**
     * 新增资金流水
     * 需要注意编码重复校验
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFunFundRecord(FunFundRecord funFundRecord) {
        // 写默认值
        setData(funFundRecord);
        funFundRecord.setCurrencyUnit(ConstantsEms.YUAN);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(funFundRecord.getHandleStatus())) {
            funFundRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = funFundRecordMapper.insert(funFundRecord);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(funFundRecord.getAttachmentList())) {
                funFundRecord.getAttachmentList().forEach(item -> {
                    item.setFundRecordSid(funFundRecord.getFundRecordSid());
                });
                funFundRecordAttachMapper.inserts(funFundRecord.getAttachmentList());
            }
            if (ConstantsEms.SAVA_STATUS.equals(funFundRecord.getHandleStatus())) {
                FunFundRecord one = funFundRecordMapper.selectById(funFundRecord.getFundRecordSid());
                insertTodo(one);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FunFundRecord(), funFundRecord);
            MongodbDeal.insert(funFundRecord.getFundRecordSid(), funFundRecord.getHandleStatus(), msgList, TITLE, null, funFundRecord.getImportStatus());
        }
        return row;
    }

    /**
     * 修改资金流水
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFunFundRecord(FunFundRecord funFundRecord) {
        // 写默认值
        setData(funFundRecord);
        FunFundRecord response = funFundRecordMapper.selectFunFundRecordById(funFundRecord.getFundRecordSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(funFundRecord.getHandleStatus())) {
            funFundRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, funFundRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            funFundRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = funFundRecordMapper.updateAllById(funFundRecord);
        if (row > 0) {
            addAttach(funFundRecord);
            //非保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(funFundRecord.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid, funFundRecord.getFundRecordSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
            }
            //插入日志
            MongodbDeal.update(funFundRecord.getFundRecordSid(), response.getHandleStatus(),
                    funFundRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 更改资金账户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setAccountNameById(FunFundRecord funFundRecord) {
        FunFundRecord response = funFundRecordMapper.selectFunFundRecordById(funFundRecord.getFundRecordSid());
        response.setFundAccountSid(funFundRecord.getFundAccountSid());
        int row = funFundRecordMapper.updateAllById(response);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundRecord.getFundRecordSid(), BusinessType.QITA.getValue(),
                    null, TITLE, "更改资金账户");
        }
        return row;
    }

    /**
     * 更改所用汇票
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setHuipiaoCodeById(FunFundRecord funFundRecord) {
        FunFundRecord response = funFundRecordMapper.selectFunFundRecordById(funFundRecord.getFundRecordSid());
        response.setHuipiaoCode(funFundRecord.getHuipiaoCode());
        int row = funFundRecordMapper.updateAllById(response);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundRecord.getFundRecordSid(), BusinessType.QITA.getValue(),
                    null, TITLE, "更改所用汇票");
        }
        return row;
    }

    /**
     * 设置其他信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDateStatus(FunFundRecord funFundRecord) {
        FunFundRecord response = funFundRecordMapper.selectFunFundRecordById(funFundRecord.getFundRecordSid());
        // 写值
        setData(funFundRecord);
        //编写日志详细
        //原值：XXX，新值：XXX；
        StringBuilder changeInfo = new StringBuilder();
        changeInfo.append("设置其他信息。\n");

        //数据字典Map
        List<DictData> flowCategoryTypeDict = sysDictDataService.selectDictData("s_fund_record_category"); //用途
        flowCategoryTypeDict = flowCategoryTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String, String> flowCategoryTypeMaps = flowCategoryTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));

        //数据字典Map
        List<DictData> fundTypeTypeDict = sysDictDataService.selectDictData("s_fund_type"); //资金类型
        fundTypeTypeDict = fundTypeTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String, String> fundTypeTypeMaps = fundTypeTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));



        if (funFundRecord.getPlanAccountCategory() != null && funFundRecord.getPlanAccountCategory().equals("Y")) {
            ConAccountCategory conAccountCategory1 = conAccountCategoryMapper.selectOne(new QueryWrapper<ConAccountCategory>().lambda()
                    .eq(ConAccountCategory::getCode,response.getAccountCategory()));
            ConAccountCategory conAccountCategory2 = conAccountCategoryMapper.selectOne(new QueryWrapper<ConAccountCategory>().lambda()
                    .eq(ConAccountCategory::getCode,funFundRecord.getAccountCategory()));
            changeInfo.append("款项类别，原值：").append(conAccountCategory1 != null? conAccountCategory1.getName(): null)
                    .append("，新值：").append(conAccountCategory2 != null? conAccountCategory2.getName(): null).append("；\n");
            response.setAccountCategory(funFundRecord.getAccountCategory());
        }

        if (funFundRecord.getPlanPaymentMethod() != null && funFundRecord.getPlanPaymentMethod().equals("Y")) {
            ConPaymentMethod conPaymentMethod1 = conPaymentMethodMapper.selectOne(new QueryWrapper<ConPaymentMethod>().lambda()
                    .eq(ConPaymentMethod::getCode,response.getPaymentMethod()));
            ConPaymentMethod conPaymentMethod2 = conPaymentMethodMapper.selectOne(new QueryWrapper<ConPaymentMethod>().lambda()
                    .eq(ConPaymentMethod::getCode,funFundRecord.getPaymentMethod()));
            changeInfo.append("收付款方式，原值：").append(conPaymentMethod1 != null? conPaymentMethod1.getName(): null)
                    .append("，新值：").append(conPaymentMethod2 != null? conPaymentMethod2.getName(): null).append("；\n");
            response.setPaymentMethod(funFundRecord.getPaymentMethod());
        }

        if (funFundRecord.getPlanFlowCategory() != null && funFundRecord.getPlanFlowCategory().equals("Y")) {
            changeInfo.append("用途，原值：").append(flowCategoryTypeMaps.get(response.getFlowCategory()))
                    .append("，新值：").append(flowCategoryTypeMaps.get(funFundRecord.getFlowCategory())).append("；\n");
            response.setFlowCategory(funFundRecord.getFlowCategory());
        }

        if (funFundRecord.getPlanFundType() != null && funFundRecord.getPlanFundType().equals("Y")) {
            changeInfo.append("更改资金类型，原值：").append(fundTypeTypeMaps.get(response.getFundType()))
                    .append("，新值：").append(fundTypeTypeMaps.get(funFundRecord.getFundType())).append("；");
            response.setFundType(funFundRecord.getFundType());
        }

        int row = funFundRecordMapper.updateAllById(response);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(funFundRecord.getFundRecordSid(), BusinessType.QITA.getValue(),
                    null, TITLE, changeInfo.toString());
        }
        return row;
    }

    /**
     * 变更资金流水
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFunFundRecord(FunFundRecord funFundRecord) {
        // 写默认值
        setData(funFundRecord);
        FunFundRecord response = funFundRecordMapper.selectFunFundRecordById(funFundRecord.getFundRecordSid());
        int row = funFundRecordMapper.updateAllById(funFundRecord);
        if (row > 0) {
            addAttach(funFundRecord);
            if (HandleStatus.SUBMIT.getCode().equals(funFundRecord.getHandleStatus())) {
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.FundRecord_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(funFundRecord.getFundRecordSid().toString());
                formParameter.setFormId(funFundRecord.getFundRecordSid().toString());
                formParameter.setFormCode(funFundRecord.getFundRecordCode().toString());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }
            //插入日志
            MongodbUtil.insertUserLog(funFundRecord.getFundRecordSid(), BusinessType.CHANGE.getValue(), response, funFundRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除资金流水
     *
     * @param fundRecordSids 需要删除的资金流水ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFunFundRecordByIds(List<Long> fundRecordSids) {
        fundRecordSids.forEach(sid -> {
            FunFundRecord funFundRecord = funFundRecordMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(funFundRecord, new FunFundRecord());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        funFundRecordAttachMapper.delete(new QueryWrapper<FunFundRecordAttach>().lambda()
                .in(FunFundRecordAttach::getFundRecordSid, fundRecordSids));
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid, fundRecordSids));
        return funFundRecordMapper.deleteBatchIds(fundRecordSids);
    }

    /**
     * 更改确认状态
     *
     * @param funFundRecord
     * @return
     */
    @Override
    public int check(FunFundRecord funFundRecord) {
        int row = 0;
        Long[] sids = funFundRecord.getFundRecordSidList();
        if (sids != null && sids.length > 0) {
            row = funFundRecordMapper.update(null, new UpdateWrapper<FunFundRecord>().lambda()
                    .set(FunFundRecord::getHandleStatus, funFundRecord.getHandleStatus())
                    .set(FunFundRecord::getConfirmDate, new Date())
                    .set(FunFundRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(FunFundRecord::getUpdateDate, new Date())
                    .set(FunFundRecord::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(FunFundRecord::getFundRecordSid, sids));
            //删除代办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
            //插入日志
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, funFundRecord.getHandleStatus(), null, TITLE, null);
            }
            //如果是审批通过
            /**
             if (ConstantsEms.CHECK_STATUS.equals(funFundRecord.getHandleStatus())){
             List<FunFundRecord> recordList = funFundRecordMapper.selectList(new QueryWrapper<FunFundRecord>().lambda()
             .in(FunFundRecord::getFundRecordSid,sids));
             if (CollectionUtil.isNotEmpty(recordList)){
             recordList.forEach(record->{
             if (record.getFundAccountSid() != null){
             //找到资金账号
             FunFundAccount account = funFundAccountMapper.selectById(record.getFundAccountSid());
             //如果是付款
             if (ConstantsFinance.BOOK_TYPE_FK.equals(record.getPaymentType())){
             BigDecimal currencyAmount = account.getCurrencyAmount();
             //若为付款，更新后账号金额=更新前账号金额-付款金额
             currencyAmount = currencyAmount.subtract(record.getCurrencyAmount());
             account.setCurrencyAmountBgq(account.getCurrencyAmount())
             .setCurrencyAmount(currencyAmount);
             funFundAccountService.updateCurrencyAmount(account);
             }
             //如果是收款
             if (ConstantsFinance.BOOK_TYPE_SK.equals(record.getPaymentType())){
             BigDecimal currencyAmount = account.getCurrencyAmount();
             //若为付款，更新后账号金额=更新前账号金额-付款金额
             currencyAmount = currencyAmount.add(record.getCurrencyAmount());
             account.setCurrencyAmountBgq(account.getCurrencyAmount())
             .setCurrencyAmount(currencyAmount);
             funFundAccountService.updateCurrencyAmount(account);
             }
             }
             });
             }
             }
             */
        }
        return row;
    }

    /**
     * 处理附件
     *
     * @param funFundRecord
     * @return
     */
    public void addAttach(FunFundRecord funFundRecord) {
        funFundRecordAttachMapper.delete(new QueryWrapper<FunFundRecordAttach>().lambda()
                .eq(FunFundRecordAttach::getFundRecordSid, funFundRecord.getFundRecordSid()));
        if (CollectionUtil.isNotEmpty(funFundRecord.getAttachmentList())) {
            funFundRecord.getAttachmentList().forEach(item -> {
                item.setFundRecordSid(funFundRecord.getFundRecordSid());
            });
            funFundRecordAttachMapper.inserts(funFundRecord.getAttachmentList());
        }
    }

    /**
     * 新增待办
     *
     * @param funFundRecord
     * @return
     */
    private void insertTodo(FunFundRecord funFundRecord) {
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        sysTodoTask.setTableName("s_fun_fund_record")
                .setDocumentSid(funFundRecord.getFundRecordSid());
        sysTodoTask.setDocumentCode(String.valueOf(funFundRecord.getFundRecordSid()))
                .setNoticeDate(new Date())
                .setUserId(ApiThreadLocalUtil.get().getUserid());
        if (ConstantsEms.SAVA_STATUS.equals(funFundRecord.getHandleStatus())) {
            sysTodoTask.setTitle("资金流水 " + funFundRecord.getFundRecordCode() + " 当前是保存状态，请及时处理！")
                    .setTaskCategory(ConstantsEms.TODO_TASK_DB);
            sysTodoTaskMapper.insert(sysTodoTask);
        }
    }


    /**
     * 导入
     *
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importData(MultipartFile file) {
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
            List<DictData> paymentTypeDict = sysDictDataService.selectDictData("s_shoufukuan_type"); //收付款类型
            paymentTypeDict = paymentTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> paymentTypeMaps = paymentTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> flowCategoryDict = sysDictDataService.selectDictData("s_fund_record_category"); //资金流水类别
            flowCategoryDict = flowCategoryDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> flowCategoryMaps = flowCategoryDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> businessTypeDict = sysDictDataService.selectDictData("s_fund_business_type"); //资金流水业务归属
            businessTypeDict = businessTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> businessTypeMaps = businessTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> paymentBankNameDict = sysDictDataService.selectDictData("s_bank"); //银行名称
            paymentBankNameDict = paymentBankNameDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> paymentBankNameMaps = paymentBankNameDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> fundTypeDict = sysDictDataService.selectDictData("s_fund_type"); //资金类型
            fundTypeDict = fundTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> fundTypeMaps = fundTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            //每行对象
            List<FunFundRecord> recordList = new ArrayList<>();
            CommonErrMsgResponse errMsg = null;
            //错误信息
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            //读excel行和列
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
//                if (objects == null) {
//                    //空行
//                    num = i + 1;
//                    continue;
//                }
                //填充总列数
                copy(objects, readAll);
                num = i + 1;
                /**
                 * 流水交易日期 必填
                 */
                String transactionDate_s = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Date transactionDate = null;
                if (StrUtil.isBlank(transactionDate_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("流水交易日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(transactionDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("流水交易日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        transactionDate = DateUtil.parse(transactionDate_s);
                    }
                }
                /**
                 * 公司简称 必填 （配置档案）
                 */
                Long companySid = null; // 公司
                String companyName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isBlank(companyName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    //获取档案信息校验 公司XXXX不存在 、公司XXXX必须为确认且启用的数据
                    BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                            .eq(BasCompany::getShortName, companyName));
                    if (basCompany == null || basCompany.getCompanySid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司" + companyName + "不存在，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (!basCompany.getStatus().equals("1") || !basCompany.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司" + companyName + "必须为确认且启用的数据，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        companySid = basCompany.getCompanySid();
                    }
                }
                /**
                 * 金额 必填
                 */
                String currencyAmount_s = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                BigDecimal currencyAmount = null;
                if (StrUtil.isBlank(currencyAmount_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("金额不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(currencyAmount_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("金额数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        currencyAmount = new BigDecimal(currencyAmount_s);
                        currencyAmount = currencyAmount.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (currencyAmount != null && BigDecimal.ZERO.compareTo(currencyAmount) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("金额必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 收付款类型 必填 （数据字典）
                 */
                String paymentType = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (StrUtil.isBlank(paymentType)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("收付款类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    paymentType = paymentTypeMaps.get(paymentType); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(paymentType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 收付款方式 必填（配置档案）
                 */
                String paymentMethod = null;
                String paymentMethodName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                if (StrUtil.isBlank(paymentMethodName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("收付款方式不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    ConPaymentMethod conPaymentMethod = conPaymentMethodMapper.selectOne(new QueryWrapper<ConPaymentMethod>().lambda()
                            .eq(ConPaymentMethod::getStatus, "1").eq(ConPaymentMethod::getHandleStatus, "5")
                            .eq(ConPaymentMethod::getName, paymentMethodName));
                    if (conPaymentMethod == null || conPaymentMethod.getSid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款方式填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        paymentMethod = conPaymentMethod.getCode();
                    }
                }

                /**
                 * 资金账户
                 */
                Long fundAccountSid = null;
                String accountName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                if (StrUtil.isBlank(accountName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("资金账户名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                String accountNumber = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (StrUtil.isBlank(accountNumber)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("资金账号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (StrUtil.isNotBlank(accountName) && StrUtil.isNotBlank(accountNumber)) {
                    try {
                        FunFundAccount account = funFundAccountMapper.selectOne(new QueryWrapper<FunFundAccount>().lambda()
                                .eq(FunFundAccount::getCompanySid, companySid)
                                .eq(FunFundAccount::getAccountName, accountName)
                                .eq(FunFundAccount::getAccountNumber, accountNumber));
                        if (account == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("该公司下，不存在此“资金账户名称+资金账号”组合！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(account.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("“资金账户名称+资金账号”组合必须为已确认状态，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                fundAccountSid = account.getFundAccountSid();
                            }
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("“资金账户名称+资金账号”组合系统中存在多个，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 流水经办人 非必填
                 */
                String flowOperator = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                if (StrUtil.isNotBlank(flowOperator)) {
                    if (flowOperator.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("流水经办人不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 用途 必填 （数据字典）
                 */
                String flowCategory = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                if (StrUtil.isBlank(flowCategory)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("用途不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    flowCategory = flowCategoryMaps.get(flowCategory); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(flowCategory)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("用途填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 业务归属 非必填 （数据字典）
                 */
                String businessType = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                if (StrUtil.isNotBlank(businessType)) {
                    businessType = businessTypeMaps.get(businessType); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(businessType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("业务归属填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }


                /**
                 * 票据号码（汇票） 非必填
                 */
                String huipiaoCode = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (StrUtil.isNotBlank(huipiaoCode)) {
                    if (huipiaoCode.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("票据号码（汇票）不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 款项类别 非必填
                 */
                String accountCategory_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                String accountCategory = null;
                if (StrUtil.isNotBlank(accountCategory_s)) {
                    ConAccountCategory conAccountCategory = conAccountCategoryMapper.selectOne(new QueryWrapper<ConAccountCategory>().lambda()
                            .eq(ConAccountCategory::getName,accountCategory_s));
                    if(conAccountCategory == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("款项类别"+accountCategory_s+"不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }else if(!conAccountCategory.getHandleStatus().equals("5") || !conAccountCategory.getStatus().equals("1")){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("款项类别"+accountCategory_s+"必须为确认且启用的数据，导入失败！");
                        errMsgList.add(errMsg);
                    }else{
                        accountCategory = conAccountCategory.getCode();
                    }
                }

                /**
                 * 收付款单号 非必填
                 */
                String shoufukuanCode = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                if (StrUtil.isNotBlank(shoufukuanCode)) {
                    if (shoufukuanCode.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款单号不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 交易流水号 选填
                 */
                String transactionNumber = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                if (StrUtil.isNotBlank(transactionNumber)) {
                    if (transactionNumber.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("交易流水号不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 流水记账日期 选填
                 */
                String transactionAccountDate_s = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                Date transactionAccountDate = null;
                if (StrUtil.isNotBlank(transactionAccountDate_s)) {
                    if (!JudgeFormat.isValidDate(transactionAccountDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("流水记账日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        transactionAccountDate = DateUtil.parse(transactionAccountDate_s);
                    }
                }

                /**
                 * 收付款账号 必填
                 */
                String paymentAccountNumber = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                if (StrUtil.isNotBlank(paymentAccountNumber)) {
                    if (paymentAccountNumber.length() > 25) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款账号不能超过25个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 收付款方名称 选填
                 */
                String paymentName = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                if (StrUtil.isNotBlank(paymentName)) {
                    if (paymentName.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款方名称长度不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 收付款方银行名称 选填 (数据字典）
                 */
                String paymentBankName = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                if (StrUtil.isNotBlank(paymentBankName)) {
                    if (paymentBankName.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款方银行名称不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        paymentBankName = paymentBankNameMaps.get(paymentBankName); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(paymentBankName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("收付款方银行名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 收付款方银行支行名称 选填
                 */
                String paymentBankBranchName = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
                if (StrUtil.isNotBlank(paymentBankBranchName)) {
                    if (paymentBankBranchName.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收付款方银行支行名称不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 流水纸质单号 选填
                 */
                String transactionBillNumber = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                if (StrUtil.isNotBlank(transactionBillNumber)) {
                    if (transactionBillNumber.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("流水纸质单号不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 参考业务单号 选填
                 */
                String referBusinessNote = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                if (StrUtil.isNotBlank(referBusinessNote)) {
                    if (referBusinessNote.length() > 60) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("参考业务单号长度不能超过60个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 收单机构 选填
                 */
                String acquirer = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                if (StrUtil.isNotBlank(acquirer)) {
                    if (acquirer.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收单机构长度不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 清算机构 选填
                 */
                String liquidationInstitution = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                if (StrUtil.isNotBlank(liquidationInstitution)) {
                    if (liquidationInstitution.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("清算机构长度不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 资金类型 选填 (数据字典）
                 */
                String fundType = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                if (StrUtil.isNotBlank(fundType)) {
                    if (fundType.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资金类型长度不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        fundType = fundTypeMaps.get(fundType); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(fundType)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("资金类型填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 流水说明 选填
                 */
                String fundRecordRemark = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                if (StrUtil.isNotBlank(fundRecordRemark)) {
                    if (fundRecordRemark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("流水说明长度不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                FunFundRecord record = new FunFundRecord();
                record.setTransactionDate(transactionDate).setCompanySid(companySid).setCurrencyAmount(currencyAmount).setPaymentType(paymentType)
                        .setPaymentMethod(paymentMethod).setPaymentAccountNumber(paymentAccountNumber)
                        .setFundAccountSid(fundAccountSid).setHuipiaoCode(huipiaoCode).setAccountCategory(accountCategory)
                        .setShoufukuanCode(shoufukuanCode)
                        .setFlowOperator(flowOperator).setFlowCategory(flowCategory).setBusinessType(businessType)
                        .setTransactionNumber(transactionNumber).setTransactionAccountDate(transactionAccountDate)
                        .setPaymentName(paymentName).setPaymentBankName(paymentBankName).setPaymentBankBranchName(paymentBankBranchName)
                        .setTransactionBillNumber(transactionBillNumber).setReferBusinessNote(referBusinessNote).setAcquirer(acquirer)
                        .setLiquidationInstitution(liquidationInstitution).setFundType(fundType).setFundRecordRemark(fundRecordRemark)
                        .setRemark(remark).setImportStatus(BusinessType.IMPORT.getValue());
                record.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setCurrency(ConstantsFinance.CURRENCY_CNY)
                        .setHandleStatus(ConstantsEms.SAVA_STATUS);
                recordList.add(record);
            }
            //检查有没有报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return errMsgList;
            }
            //调用新增方法写入
            if (CollectionUtil.isNotEmpty(recordList)) {
                recordList.forEach(item -> {
                    insertFunFundRecord(item);
                });
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return num - 2;
    }

    /**
     * 写入数据字段处理
     */
    private void setData(FunFundRecord record) {
        // 图片
        String picture = null;
        if (ArrayUtil.isNotEmpty(record.getPicturePathList())) {
            picture = "";
            for (int i = 0; i < record.getPicturePathList().length; i++) {
                picture = picture + record.getPicturePathList()[i] + ";";
            }
        }
        record.setPicturePath(picture);
    }

    /**
     * 读取数据字段处理
     */
    private void getData(FunFundRecord record) {
        if (record == null) {
            return;
        }
        // 图片
        if (StrUtil.isNotBlank(record.getPicturePath())) {
            record.setPicturePathList(record.getPicturePath().split(";"));
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

}
