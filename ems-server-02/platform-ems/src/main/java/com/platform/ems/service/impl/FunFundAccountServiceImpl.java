package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.BasCompany;
import com.platform.ems.domain.FunFundAccount;
import com.platform.ems.domain.FunFundAccountAttach;
import com.platform.ems.domain.FunFundRecord;
import com.platform.ems.mapper.FunFundRecordMapper;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.ems.mapper.FunFundAccountAttachMapper;
import com.platform.ems.mapper.FunFundAccountMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IFunFundAccountService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资金账户信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class FunFundAccountServiceImpl extends ServiceImpl<FunFundAccountMapper, FunFundAccount> implements IFunFundAccountService {
    @Autowired
    private FunFundAccountMapper funFundAccountMapper;
    @Autowired
    private FunFundAccountAttachMapper funFundAccountAttachMapper;
    @Autowired
    private FunFundRecordMapper funFundRecordMapper;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    private static final String TITLE = "资金账户信息";

    /**
     * 查询资金账户信息
     *
     * @param fundAccountSid 资金账户信息ID
     * @return 资金账户信息
     */
    @Override
    public FunFundAccount selectFunFundAccountById(Long fundAccountSid) {
        FunFundAccount funFundAccount = funFundAccountMapper.selectFunFundAccountById(fundAccountSid);
        // 特殊字段处理
        getData(funFundAccount);
        List<FunFundAccountAttach> attachList = funFundAccountAttachMapper.selectFunFundAccountAttachList
                (new FunFundAccountAttach().setFundAccountSid(fundAccountSid));
        funFundAccount.setAttachmentList(attachList);
        MongodbUtil.find(funFundAccount);
        return funFundAccount;
    }

    /**
     * 查询资金账户信息列表
     *
     * @param funFundAccount 资金账户信息
     * @return 资金账户信息
     */
    @Override
    public List<FunFundAccount> selectFunFundAccountList(FunFundAccount funFundAccount) {
        List<FunFundAccount> list = funFundAccountMapper.selectFunFundAccountList(funFundAccount);
        // 图片字段查询页面要
        if (CollectionUtil.isNotEmpty(list)) {
            for (FunFundAccount record : list) {
                getData(record);
            }
        }
        return list;
    }

    /**
     * 查询资金统计信息
     *
     * @param fundAccountSid 资金账户信息ID
     * @return 资金统计信息
     */
    @Override
    public List<FunFundAccount> selectStatisticalFunFundAccountList(FunFundAccount funFundAccount) {
        return funFundAccountMapper.selectStatisticalFunFundAccountList(funFundAccount);
    }

    /**
     * 查询资金统计信息明细
     *
     * @param funFundAccount 资金账户信息
     * @return 资金统计信息集合
     */
    @Override
    public List<FunFundAccount> selectStatisticalFunFundAccountDetail(FunFundAccount funFundAccount) {
        return funFundAccountMapper.selectStatisticalFunFundAccountDetail(funFundAccount);
    }

    /**
     * 新增资金账户信息
     * 需要注意编码重复校验
     *
     * @param funFundAccount 资金账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFunFundAccount(FunFundAccount funFundAccount) {
        checkName(funFundAccount);
        // 写默认值
        setData(funFundAccount);
        funFundAccount.setCurrencyUnit(ConstantsEms.YUAN);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(funFundAccount.getHandleStatus())) {
            funFundAccount.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            funFundAccount.setLatestUpdateDate(new Date());
            funFundAccount.setHuipiaoLatestUpdateDate(new Date());
        }
        int row = funFundAccountMapper.insert(funFundAccount);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(funFundAccount.getAttachmentList())) {
                funFundAccount.getAttachmentList().forEach(item -> {
                    item.setFundAccountSid(funFundAccount.getFundAccountSid());
                });
                funFundAccountAttachMapper.inserts(funFundAccount.getAttachmentList());
            }
            if (ConstantsEms.SAVA_STATUS.equals(funFundAccount.getHandleStatus())) {
                FunFundAccount one = funFundAccountMapper.selectById(funFundAccount.getFundAccountSid());
                insertTodo(one);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FunFundAccount(), funFundAccount);
            MongodbDeal.insert(funFundAccount.getFundAccountSid(), funFundAccount.getHandleStatus(), msgList, TITLE, null, funFundAccount.getImportStatus());

        }
        return row;
    }

    /**
     * 修改资金账户信息
     *
     * @param funFundAccount 资金账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFunFundAccount(FunFundAccount funFundAccount) {
        checkName(funFundAccount);
        // 写默认值
        setData(funFundAccount);
        FunFundAccount response = funFundAccountMapper.selectFunFundAccountById(funFundAccount.getFundAccountSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(funFundAccount.getHandleStatus())) {
            funFundAccount.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            funFundAccount.setLatestUpdateDate(new Date());
            funFundAccount.setHuipiaoLatestUpdateDate(new Date());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, funFundAccount);
        if (CollectionUtil.isNotEmpty(msgList)) {
            funFundAccount.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = funFundAccountMapper.updateAllById(funFundAccount);
        if (row > 0) {
            addAttach(funFundAccount);
            //非保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(funFundAccount.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid, funFundAccount.getFundAccountSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
            }
            //插入日志
            MongodbDeal.update(funFundAccount.getFundAccountSid(), response.getHandleStatus(),
                    funFundAccount.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更资金账户信息
     *
     * @param funFundAccount 资金账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFunFundAccount(FunFundAccount funFundAccount) {
        checkName(funFundAccount);
        // 写默认值
        setData(funFundAccount);
        FunFundAccount response = funFundAccountMapper.selectFunFundAccountById(funFundAccount.getFundAccountSid());
        if (response.getAccountNumber() == null) {
            response.setAccountNumber("");
        }
        //编写日志详细
        //账户名称变更，原值：XXX，新值：XXX；账户金额变更，原值：XXX，新值：XXX；账号变更，原值：XXX，新值：XXX
        StringBuilder changeInfo = new StringBuilder();
        if (!response.getAccountName().equals(funFundAccount.getAccountName())) {
            changeInfo.append("账户名称变更，原值：").append(response.getAccountName()).append("，新值：").append(funFundAccount.getAccountName()).append("；");
        }

        if (!response.getAccountNumber().equals(funFundAccount.getAccountNumber())) {
            changeInfo.append("账号变更，原值：").append(response.getAccountNumber()).append("，新值：").append(funFundAccount.getAccountNumber()).append("；");
        }

        if (response.getCurrencyAmount().compareTo(funFundAccount.getCurrencyAmount()) != 0) {
            changeInfo.append("存款金额变更，原值：").append(response.getCurrencyAmount().stripTrailingZeros().toPlainString()).append("，新值：")
                    .append(funFundAccount.getCurrencyAmount().stripTrailingZeros().toPlainString()).append("；");
            funFundAccount.setLatestUpdateDate(new Date());
            funFundAccount.setCurrencyAmountBgq(response.getCurrencyAmount());
        }
        // 汇票金额 非必填
        String huipiaoCurrencyAmount = response.getHuipiaoCurrencyAmount() == null ? "" : String.valueOf(response.getHuipiaoCurrencyAmount());
        if ((response.getHuipiaoCurrencyAmount() != null && funFundAccount.getHuipiaoCurrencyAmount() != null
                && response.getHuipiaoCurrencyAmount().compareTo(funFundAccount.getHuipiaoCurrencyAmount()) != 0) ||
                (response.getHuipiaoCurrencyAmount() != null && funFundAccount.getHuipiaoCurrencyAmount() == null) ||
                (response.getHuipiaoCurrencyAmount() == null && funFundAccount.getHuipiaoCurrencyAmount() != null)) {
            if (response.getHuipiaoCurrencyAmount() != null && funFundAccount.getHuipiaoCurrencyAmount() == null) {
                changeInfo.append("汇票金额变更，原值：").append(response.getHuipiaoCurrencyAmount().stripTrailingZeros().toPlainString())
                        .append("，新值：").append("").append("；");
            }
            else if (response.getHuipiaoCurrencyAmount() == null && funFundAccount.getHuipiaoCurrencyAmount() != null) {
                changeInfo.append("汇票金额变更，原值：").append("")
                        .append("，新值：").append(funFundAccount.getHuipiaoCurrencyAmount().stripTrailingZeros().toPlainString()).append("；");
            }
            else {
                changeInfo.append("汇票金额变更，原值：").append(response.getHuipiaoCurrencyAmount().stripTrailingZeros().toPlainString())
                        .append("，新值：").append(funFundAccount.getHuipiaoCurrencyAmount().stripTrailingZeros().toPlainString()).append("；");
            }
        }
        //汇票金额发生变化时，点击确认按钮，更新“最近更新时间(汇票金额)”为操作当天
        if (response.getHuipiaoCurrencyAmount() != null && funFundAccount.getHuipiaoCurrencyAmount() != null) {
            if (response.getHuipiaoCurrencyAmount().compareTo(funFundAccount.getHuipiaoCurrencyAmount()) != 0) {
                funFundAccount.setHuipiaoLatestUpdateDate(new Date());
            }
        } else if (response.getHuipiaoCurrencyAmount() != null && funFundAccount.getHuipiaoCurrencyAmount() == null) {
            funFundAccount.setHuipiaoLatestUpdateDate(new Date());
        } else if (response.getHuipiaoCurrencyAmount() == null && funFundAccount.getHuipiaoCurrencyAmount() != null) {
            funFundAccount.setHuipiaoLatestUpdateDate(new Date());
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(funFundAccount.getHandleStatus())) {
            funFundAccount.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, funFundAccount);
        if (CollectionUtil.isNotEmpty(msgList)) {
            funFundAccount.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = funFundAccountMapper.updateAllById(funFundAccount);
        if (row > 0) {
            addAttach(funFundAccount);
            if (HandleStatus.SUBMIT.getCode().equals(funFundAccount.getHandleStatus())) {
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.FundAccount_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(funFundAccount.getFundAccountSid().toString());
                formParameter.setFormId(funFundAccount.getFundAccountSid().toString());
                formParameter.setFormCode(funFundAccount.getFundAccountCode().toString());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }
            //插入日志
            MongodbUtil.insertUserLog(funFundAccount.getFundAccountSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, changeInfo.toString());
        }
        return row;
    }

    /**
     * 批量删除资金账户信息
     *
     * @param fundAccountSids 需要删除的资金账户信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFunFundAccountByIds(List<Long> fundAccountSids) {
        fundAccountSids.forEach(sid -> {
            FunFundAccount funFundAccount = funFundAccountMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(funFundAccount, new FunFundAccount());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        funFundAccountAttachMapper.delete(new QueryWrapper<FunFundAccountAttach>().lambda()
                .in(FunFundAccountAttach::getFundAccountSid, fundAccountSids));
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid, fundAccountSids));
        return funFundAccountMapper.deleteBatchIds(fundAccountSids);
    }

    /**
     * 更改确认状态
     *
     * @param funFundAccount
     * @return
     */
    @Override
    public int check(FunFundAccount funFundAccount) {
        int row = 0;
        Long[] sids = funFundAccount.getFundAccountSidList();
        if (sids != null && sids.length > 0) {
            //确认状态
            row = funFundAccountMapper.update(null, new UpdateWrapper<FunFundAccount>().lambda()
                    .set(FunFundAccount::getLatestUpdateDate, new Date())
                    .set(FunFundAccount::getHuipiaoLatestUpdateDate, new Date())
                    .set(FunFundAccount::getHandleStatus, funFundAccount.getHandleStatus())
                    .set(FunFundAccount::getConfirmDate, new Date())
                    .set(FunFundAccount::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(FunFundAccount::getUpdateDate, new Date())
                    .set(FunFundAccount::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(FunFundAccount::getFundAccountSid, sids));
            //删除代办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
            //插入日志
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, funFundAccount.getHandleStatus(), null, TITLE, null);
            }
        }

        return row;
    }

    /**
     * 更改作废状态前的校验
     *
     * @param funFundAccount
     * @return
     */
    @Override
    public void checkInvalid(FunFundAccount funFundAccount) {
        if (!HandleStatus.INVALID.getCode().equals(funFundAccount.getHandleStatus())) {
            return;
        }
        Long[] sids = funFundAccount.getFundAccountSidList();
        if (sids != null && sids.length > 0) {
            for (Long sid : sids) {
                FunFundAccount response = funFundAccountMapper.selectById(sid);
                if (BigDecimal.ZERO.compareTo(response.getCurrencyAmount()) < 0) {
                    throw new CustomException("当前账户金额大于0，是否确认作废？");
                }
            }
        }
        return;
    }

    /**
     * 更改作废状态
     *
     * @param funFundAccount
     * @return
     */
    @Override
    public int invalid(FunFundAccount funFundAccount) {
        int row = 0;
        if (!HandleStatus.INVALID.getCode().equals(funFundAccount.getHandleStatus())) {
            return row;
        }
        Long[] sids = funFundAccount.getFundAccountSidList();
        if (sids != null && sids.length > 0) {
            for (Long sid : sids) {
                FunFundAccount response = funFundAccountMapper.selectById(sid);
                row = funFundAccountMapper.update(null, new UpdateWrapper<FunFundAccount>().lambda().set(FunFundAccount::getHandleStatus, funFundAccount.getHandleStatus())
                        .set(FunFundAccount::getCurrencyAmountZfq, response.getCurrencyAmount())
                        .eq(FunFundAccount::getFundAccountSid, sid));
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(sid, funFundAccount.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 写入数据字段处理
     */
    private void setData(FunFundAccount record) {
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
    private void getData(FunFundAccount record) {
        if (record == null) {
            return;
        }
        // 图片
        if (StrUtil.isNotBlank(record.getPicturePath())) {
            record.setPicturePathList(record.getPicturePath().split(";"));
        }
    }

    /**
     * 处理附件
     *
     * @param FunFundAccount
     * @return
     */
    public void addAttach(FunFundAccount funFundAccount) {
        funFundAccountAttachMapper.delete(new QueryWrapper<FunFundAccountAttach>().lambda()
                .eq(FunFundAccountAttach::getFundAccountSid, funFundAccount.getFundAccountSid()));
        if (CollectionUtil.isNotEmpty(funFundAccount.getAttachmentList())) {
            funFundAccount.getAttachmentList().forEach(item -> {
                item.setFundAccountSid(funFundAccount.getFundAccountSid());
            });
            funFundAccountAttachMapper.inserts(funFundAccount.getAttachmentList());
        }
    }

    /**
     * 账户名称+账户不能同时一致
     *
     * @param funFundAccount
     * @return
     */
    private void checkName(FunFundAccount funFundAccount) {
        if (StrUtil.isNotBlank(funFundAccount.getAccountName()) && StrUtil.isNotBlank(funFundAccount.getAccountNumber())) {
            QueryWrapper<FunFundAccount> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(FunFundAccount::getAccountName, funFundAccount.getAccountName())
                    .eq(FunFundAccount::getAccountNumber, funFundAccount.getAccountNumber())
                    .eq(FunFundAccount::getClientId, ApiThreadLocalUtil.get().getClientId());
            if (funFundAccount.getFundAccountSid() != null) {
                queryWrapper.lambda().ne(FunFundAccount::getFundAccountSid, funFundAccount.getFundAccountSid());
            }
            List<FunFundAccount> accountList = funFundAccountMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(accountList)) {
                throw new CustomException("“账户名称+账号”已存在，请核实！");
            }
        }
    }

    /**
     * 新增待办
     *
     * @param funFundAccount
     * @return
     */
    private void insertTodo(FunFundAccount funFundAccount) {
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        sysTodoTask.setTableName("s_fun_fund_account")
                .setDocumentSid(funFundAccount.getFundAccountSid());
        sysTodoTask.setDocumentCode(String.valueOf(funFundAccount.getFundAccountSid()))
                .setNoticeDate(new Date())
                .setUserId(ApiThreadLocalUtil.get().getUserid());
        if (ConstantsEms.SAVA_STATUS.equals(funFundAccount.getHandleStatus())) {
            sysTodoTask.setTitle("资金账户 " + funFundAccount.getFundAccountCode() + " 当前是保存状态，请及时处理！")
                    .setTaskCategory(ConstantsEms.TODO_TASK_DB);
            sysTodoTaskMapper.insert(sysTodoTask);
        }
    }

    /**
     * 更新余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAmount(FunFundAccount request) {
        int row = 1;
        if (request.getFundAccountSid() == null) {
            return row;
        }
        FunFundAccount funFundAccount = funFundAccountMapper.selectById(request.getFundAccountSid());
        if (funFundAccount == null || !ConstantsEms.CHECK_STATUS.equals(funFundAccount.getHandleStatus())) {
            throw new BaseException("仅处理状态为已确认的资金账户才可进行此操作！");
        }
        StringBuilder changeInfo = new StringBuilder();
        List<FunFundRecord> recordList = funFundRecordMapper.selectList(new QueryWrapper<FunFundRecord>().lambda()
                .eq(FunFundRecord::getFundAccountSid, request.getFundAccountSid())
                .eq(FunFundRecord::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .ne(FunFundRecord::getPaymentMethod, "HP")
                .gt(FunFundRecord::getCreateDate, funFundAccount.getLatestUpdateDate()));
        if (CollectionUtil.isNotEmpty(recordList)) {
            List<FunFundRecord> shouList = recordList.stream().filter(o -> o.getPaymentType().equals(ConstantsFinance.BOOK_TYPE_SK) && !o.getPaymentMethod().equals("HP")).collect(Collectors.toList());
            List<FunFundRecord> fuList = recordList.stream().filter(o -> o.getPaymentType().equals(ConstantsFinance.BOOK_TYPE_FK) && !o.getPaymentMethod().equals("HP")).collect(Collectors.toList());
            BigDecimal shou = BigDecimal.ZERO, fu = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(shouList)) {
                shou = shouList.stream().map(FunFundRecord::getCurrencyAmount).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
            if (CollectionUtil.isNotEmpty(fuList)) {
                fu = fuList.stream().map(FunFundRecord::getCurrencyAmount).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
            BigDecimal currencyAmount = funFundAccount.getCurrencyAmount().add(shou).subtract(fu);
            row = funFundAccountMapper.update(null, new UpdateWrapper<FunFundAccount>().lambda()
                    .eq(FunFundAccount::getFundAccountSid, funFundAccount.getFundAccountSid())
                    .set(FunFundAccount::getCurrencyAmountBgq, funFundAccount.getCurrencyAmount())
                    .set(FunFundAccount::getCurrencyAmount, currencyAmount)
                    .set(FunFundAccount::getLatestUpdateDate, LocalDateTime.now()));
            changeInfo.append("存款金额，更新前：").append(funFundAccount.getCurrencyAmount().stripTrailingZeros().toPlainString()).append("，更新后：")
                    .append(currencyAmount.stripTrailingZeros().toPlainString()).append("；\n");
        }
        List<FunFundRecord> hpList = funFundRecordMapper.selectList(new QueryWrapper<FunFundRecord>().lambda()
                .eq(FunFundRecord::getFundAccountSid, request.getFundAccountSid())
                .eq(FunFundRecord::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .eq(FunFundRecord::getPaymentMethod, "HP")
                .gt(FunFundRecord::getCreateDate, funFundAccount.getHuipiaoLatestUpdateDate()));
        if (CollectionUtil.isNotEmpty(hpList)) {
            List<FunFundRecord> shouList = hpList.stream().filter(o -> o.getPaymentType().equals(ConstantsFinance.BOOK_TYPE_SK) && o.getPaymentMethod().equals("HP")).collect(Collectors.toList());
            List<FunFundRecord> fuList = hpList.stream().filter(o -> o.getPaymentType().equals(ConstantsFinance.BOOK_TYPE_FK) && o.getPaymentMethod().equals("HP")).collect(Collectors.toList());
            BigDecimal shou = BigDecimal.ZERO, fu = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(shouList)) {
                shou = shouList.stream().map(FunFundRecord::getCurrencyAmount).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
            if (CollectionUtil.isNotEmpty(fuList)) {
                fu = fuList.stream().map(FunFundRecord::getCurrencyAmount).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
            if(funFundAccount.getHuipiaoCurrencyAmount() == null){
                funFundAccount.setHuipiaoCurrencyAmount(BigDecimal.valueOf(0));
            }
            BigDecimal huipiaoCurrencyAmount = funFundAccount.getHuipiaoCurrencyAmount().add(shou).subtract(fu);
            row = funFundAccountMapper.update(null, new UpdateWrapper<FunFundAccount>().lambda()
                    .eq(FunFundAccount::getFundAccountSid, funFundAccount.getFundAccountSid())
                    .set(FunFundAccount::getHuipiaoCurrencyAmount, huipiaoCurrencyAmount)
                    .set(FunFundAccount::getHuipiaoLatestUpdateDate, LocalDateTime.now()));
            changeInfo.append("汇票金额，更新前：").append(funFundAccount.getHuipiaoCurrencyAmount().stripTrailingZeros().toPlainString()).append("，更新后：")
                    .append(huipiaoCurrencyAmount.stripTrailingZeros().toPlainString()).append("；");
        }

        MongodbUtil.insertUserLog(funFundAccount.getFundAccountSid(), BusinessType.QITA.getValue(), null, TITLE, "更新余额。\n" + changeInfo);

        return row;
    }

    /**
     * 修改资金账户金额
     *
     * @param funFundAccount 资金账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCurrencyAmount(FunFundAccount funFundAccount) {
        int row = 0;
        if (funFundAccount.getFundAccountSid() == null || funFundAccount.getCurrencyAmount() == null) {
            return row;
        } else {
            row = funFundAccountMapper.update(null, new UpdateWrapper<FunFundAccount>().lambda()
                    .set(FunFundAccount::getCurrencyAmountBgq, funFundAccount.getCurrencyAmountBgq())
                    .set(FunFundAccount::getLatestUpdateDate, new Date())
                    .set(FunFundAccount::getCurrencyAmount, funFundAccount.getCurrencyAmount())
                    .eq(FunFundAccount::getFundAccountSid, funFundAccount.getFundAccountSid()));
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(funFundAccount.getFundAccountSid(), BusinessType.AMOUNT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 下拉框接口
     */
    @Override
    public List<FunFundAccount> getList(FunFundAccount funFundAccount) {
        return funFundAccountMapper.getList(funFundAccount);
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
            List<DictData> accountTypeDict = sysDictDataService.selectDictData("s_fund_account_type"); //类型
            accountTypeDict = accountTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> accountTypeMaps = accountTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> bankNameDict = sysDictDataService.selectDictData("s_bank"); //银行名称
            bankNameDict = bankNameDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> bankNameMaps = bankNameDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            //每行对象
            List<FunFundAccount> recordList = new ArrayList<>();
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
                //填充总列数
                copy(objects, readAll);
                num = i + 1;
                /**
                 * 类型 必填
                 */
                String accountType = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(accountType)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    accountType = accountTypeMaps.get(accountType); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(accountType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 账号名称 必填
                 */
                String accountName_S = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                String accountName = null;
                if (StrUtil.isBlank(accountName_S)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("账户名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (accountName_S.length() > 255) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("账户名称不能超过255个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        accountName = accountName_S;
                    }
                }

                /**
                 * 账号 必填
                 */
                String accountNumber_S = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                String accountNumber = null;
                if (StrUtil.isBlank(accountNumber_S)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("账号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isCodeType(accountNumber_S)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("账号格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (accountNumber_S.length() > 25) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("账号不能超过25个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        accountNumber = accountNumber_S;
                    }

                }

                //校验 账号名称+账号 是否在该租户下存在
                if (accountName != null && accountNumber != null) {
                    //(查数据库）
                    QueryWrapper<FunFundAccount> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(FunFundAccount::getAccountName, accountName)
                            .eq(FunFundAccount::getAccountNumber, accountNumber)
                            .eq(FunFundAccount::getClientId, ApiThreadLocalUtil.get().getClientId());
                    List<FunFundAccount> accountList = funFundAccountMapper.selectList(queryWrapper);
                    if (CollectionUtil.isNotEmpty(accountList)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("系统中，“账号名称+账号”组合已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    boolean isData = false;
                    String accountNameNumber = accountName + accountNumber;
                    for (FunFundAccount item : recordList) {
                        String nameNumber = item.getAccountName() + item.getAccountNumber();
                        if (nameNumber.equals(accountNameNumber)) {
                            isData = true;
                            break;
                        }
                    }
                    if (isData) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，“账号名称+账号”组合已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }


                /**
                 * 账户金额(元) 必填
                 */
                String currencyAmount_s = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal currencyAmount = null;
                if (StrUtil.isBlank(currencyAmount_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("存款金额(元)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(currencyAmount_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("存款金额(元)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        currencyAmount = new BigDecimal(currencyAmount_s);
                        currencyAmount = currencyAmount.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (currencyAmount != null && BigDecimal.ZERO.compareTo(currencyAmount) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("存款金额(元)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 公司简称 必填 （配置档案）
                 */
                Long companySid = null; // 公司
                String companyName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
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
                 * 汇票金额(元) 选填
                 */
                String huipiaoCurrencyAmount_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal huipiaoCurrencyAmount = null;
                if (StrUtil.isNotBlank(huipiaoCurrencyAmount_s)) {
                    if (!JudgeFormat.isValidDouble(huipiaoCurrencyAmount_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("汇票金额(元)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        huipiaoCurrencyAmount = new BigDecimal(huipiaoCurrencyAmount_s);
                        huipiaoCurrencyAmount = huipiaoCurrencyAmount.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (huipiaoCurrencyAmount != null && BigDecimal.ZERO.compareTo(currencyAmount) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("汇票金额(元)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 银行名称 选填 (数据字典）
                 */
                String bankName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (StrUtil.isNotBlank(bankName)) {
                    if (bankName.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("银行名称不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        bankName = bankNameMaps.get(bankName); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(bankName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("银行名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 银行支行名称 选填
                 */
                String bankBranchName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                if (StrUtil.isNotBlank(bankBranchName)) {
                    if (bankBranchName.length() > 255) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("银行支行名称不能超过255个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                FunFundAccount record = new FunFundAccount();
                record.setAccountType(accountType).setAccountName(accountName).setAccountNumber(accountNumber)
                        .setCurrencyAmount(currencyAmount).setHuipiaoCurrencyAmount(huipiaoCurrencyAmount)
                        .setCompanySid(companySid).setBankName(bankName).setBankBranchName(bankBranchName).setRemark(remark);
                record.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setCurrency(ConstantsFinance.CURRENCY_CNY)
                        .setHandleStatus(ConstantsEms.SAVA_STATUS).setImportStatus(BusinessType.IMPORT.getValue());
                recordList.add(record);
            }
            //检查有没有报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return errMsgList;
            }
            //调用新增方法写入
            if (CollectionUtil.isNotEmpty(recordList)) {
                recordList.forEach(item -> {
                    insertFunFundAccount(item);
                });
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return num - 2;
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
