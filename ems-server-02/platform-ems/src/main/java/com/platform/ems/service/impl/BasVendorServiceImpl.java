package com.platform.ems.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.IBasVendorService;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商档案Service业务层处理
 *
 * @author qhq
 * @date 2021-03-12
 */
@Service
@SuppressWarnings("all")
public class BasVendorServiceImpl extends ServiceImpl<BasVendorMapper, BasVendor> implements IBasVendorService {
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasVendorAddrMapper basVendorAddrMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasVendorBankAccountMapper basVendorBankAccountMapper;
    @Autowired
    private BasVendorCustomerMapper basVendorCustomerMapper;
    @Autowired
    private BasVendorSupplierMapper basVendorSupplierMapper;
    @Autowired
    private BasVendorProductivityMapper basVendorProductivityMapper;
    @Autowired
    private BasVendorTeamMapper basVendorTeamMapper;
    @Autowired
    private BasVendorMachineMapper basVendorMachineMapper;
    @Autowired
    private BasVendorAttachmentMapper basVendorAttachmentMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "供应商档案";


    /**
     * 查询供应商档案
     *
     * @param clientId 供应商档案ID
     * @return 供应商档案
     */
    @Override
    public BasVendor selectBasVendorBySid(Long vendorSid) {
        BasVendor basVendor = basVendorMapper.selectBasVendorById(vendorSid);
        BasVendorBankAccount bank = new BasVendorBankAccount();
        bank.setVendorSid(vendorSid);
        List<BasVendorBankAccount> list = basVendorBankAccountMapper.selectBasVendorBankAccountList(bank);
        List<BasVendorBankAccount> base = list.stream().filter(o->ConstantsEms.BANK_ACCOUNT_TYPE_JBH.equals(o.getAccountType())).collect(Collectors.toList());
        List<BasVendorBankAccount> common = list.stream().filter(o->ConstantsEms.BANK_ACCOUNT_TYPE_YBH.equals(o.getAccountType())).collect(Collectors.toList());
        basVendor.setBasVendorBankAccountList(common);
        basVendor.setBaseBankAccountList(base);
        BasVendorAddr addr = new BasVendorAddr().setVendorSid(vendorSid);
        List<BasVendorAddr> addrList = basVendorAddrMapper.selectBasVendorAddrList(addr);
        //客户与供应商-主要客户
        List<BasVendorCustomer> basMainCustomerList = basVendorCustomerMapper.selectBasVendorCustomerList(
                new BasVendorCustomer().setVendorSid(vendorSid)
        );
        //客户与供应商-主要供应商
        List<BasVendorSupplier> basMainVendorList = basVendorSupplierMapper.selectBasVendorSupplierList(
                new BasVendorSupplier().setVendorSid(vendorSid)
        );
        //供应商的产能信息表
        List<BasVendorProductivity> basVendorProductivity = basVendorProductivityMapper.selectBasVendorProductivityList(
                new BasVendorProductivity().setVendorSid(vendorSid)
        );
        //供应商的人员信息表
        List<BasVendorTeam> basVendorTeamList = basVendorTeamMapper.selectBasVendorTeamList(
                new BasVendorTeam().setVendorSid(vendorSid)
        );
        if (CollectionUtil.isNotEmpty(basVendorTeamList)){
            basVendor.setBasVendorTeam(basVendorTeamList.get(basVendorTeamList.size() - 1));
        }
        //供应商的设备信息表
        List<BasVendorMachine> basVendorMachine = basVendorMachineMapper.selectBasVendorMachineList(
                new BasVendorMachine().setVendorSid(vendorSid)
        );
        //供应商-附件对象
        List<BasVendorAttachment> basVendorAttachList = basVendorAttachmentMapper.selectBasVendorAttachmentList(
                new BasVendorAttachment().setVendorSid(vendorSid));
        basVendor.setAttachmentList(basVendorAttachList);
        basVendor.setAddrList(addrList);
        basVendor.setBasMainCustomerList(basMainCustomerList);
        basVendor.setBasMainVendorList(basMainVendorList);
        basVendor.setBasVendorProductivityList(basVendorProductivity);
        basVendor.setBasVendorMachineList(basVendorMachine);
        //查询日志信息
        MongodbUtil.find(basVendor);
        return basVendor;
    }

    /**
     * 查询供应商档案列表
     *
     * @param basVendor 供应商档案
     * @return 供应商档案
     */
    @Override
    public List<BasVendor> selectBasVendorList(BasVendor basVendor) {
        return basVendorMapper.selectBasVendorList(basVendor);
    }

    /**
     * 新增供应商档案
     *
     * @param basVendor 供应商档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendor(BasVendor basVendor) {
        int row;
        QueryWrapper<BasVendor> wrapper = new QueryWrapper<BasVendor>();
        wrapper = new QueryWrapper<BasVendor>();
        wrapper.eq("vendor_code", basVendor.getVendorCode());
        List<BasVendor> query1 = basVendorMapper.selectList(wrapper);
        if (query1.size() > 0) {
            throw new CheckedException("供应商编码重复，请查看");
        }
        wrapper = new QueryWrapper<BasVendor>();
        wrapper.eq("vendor_name", basVendor.getVendorName());
        List<BasVendor> query2 = basVendorMapper.selectList(wrapper);
        if (query2.size() > 0) {
            throw new CheckedException("供应商名称重复，请查看");
        }
        wrapper = new QueryWrapper<BasVendor>();
        wrapper.eq("short_name", basVendor.getShortName());
        List<BasVendor> query3 = basVendorMapper.selectList(wrapper);
        if (query3.size() > 0) {
            throw new CheckedException("供应商简称重复，请查看");
        }
        basVendor.setClientId(SecurityUtils.getClientId());
        basVendor.setVendorSid(IdWorker.getId());
        String remark = BusinessType.SAVE.getName();
        if (ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())) {
            basVendor.setConfirmDate(new Date());
            basVendor.setConfirmerAccount(SecurityUtils.getUsername());
            remark = BusinessType.CHECK.getName();
        }
        row = basVendorMapper.insert(basVendor);
        if (row <= 0) {
            throw new CheckedException("插入数据库异常");
        }
        List<BasVendorBankAccount> list = basVendor.getBasVendorBankAccountList();
        list.addAll(basVendor.getBaseBankAccountList());
        if (CollectionUtil.isNotEmpty(list)) {
            for (BasVendorBankAccount bankAccount : list) {
                if (StringUtils.isEmpty(bankAccount.getBankAccount())) {
                    continue;
                }
                bankAccount.setVendorSid(basVendor.getVendorSid());
                bankAccount.setClientId(SecurityUtils.getClientId());
                basVendorBankAccountMapper.insert(bankAccount);
            }
        }
        if (CollectionUtils.isNotEmpty(basVendor.getAddrList())){
            basVendor.getAddrList().forEach(item->{
                item.setVendorSid(basVendor.getVendorSid());
            });
            basVendorAddrMapper.inserts(basVendor.getAddrList());
        }
        //供应商-附件对象
        List<BasVendorAttachment> basVendorAttachmentList = basVendor.getAttachmentList();
        addBasVendorAttachment(basVendor);
        //主要客户
        addBasMainCustomerList(basVendor.getVendorSid(),basVendor.getBasMainCustomerList());
        //主要供应商
        addBasMainVendorList(basVendor.getVendorSid(),basVendor.getBasMainVendorList());
        //供应商的产能信息表
        addBasVendorProductivityList(basVendor.getVendorSid(),basVendor.getBasVendorProductivityList());
        //供应商的人员信息表
        addBasVendorTeamList(basVendor.getVendorSid(),basVendor.getBasVendorTeam());
        //供应商的设备信息表
        addBasVendorMachineList(basVendor.getVendorSid(),basVendor.getBasVendorMachineList());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        BasVendor one = basVendorMapper.selectBasVendorById(basVendor.getVendorSid());
        if (ConstantsEms.SAVA_STATUS.equals(basVendor.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_vendor")
                    .setDocumentSid(basVendor.getVendorSid());
            sysTodoTask.setTitle("供应商档案: " + one.getVendorCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(one.getVendorCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbDeal.insert(basVendor.getVendorSid(), basVendor.getHandleStatus(), null, TITLE,null);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importData(MultipartFile file) {
        List<BasVendor> vendorList = new ArrayList<>();
        List<String> vendorNameList = new ArrayList<>(); //用于全称查重
        List<String> shortNameList = new ArrayList<>();  //用于简称查重
        List<String> creditCodeList = new ArrayList<>(); //用于统一信用代码证号/身份证号查重
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
            List<DictData> groupList = sysDictDataService.selectDictData("s_vendor_group"); //供应商组
            Map<String, String> groupMaps = groupList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> categoryList = sysDictDataService.selectDictData("s_vendor_category"); //供应商类别
            Map<String, String> categoryMaps = categoryList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> yesnoList = sysDictDataService.selectDictData("sys_yes_no"); //是否
            Map<String, String> yesnoMaps = yesnoList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                String vendorGroup = objects.get(2) == null ? null : objects.get(2).toString();
                if (StrUtil.isBlank(vendorGroup)) {
                    throw new BaseException("供应商组不可为空");
                }
                String value = groupMaps.get(vendorGroup);
                if (StrUtil.isEmpty(value)) {
                    throw new BaseException("供应商组配置错误,请联系管理员");
                }
                String valueCategory = null;
                String vendorCategory = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                if (StrUtil.isNotBlank(vendorCategory)){
                    valueCategory = categoryMaps.get(vendorCategory);
                    if (StrUtil.isEmpty(valueCategory)) {
                        throw new BaseException("供应商类别配置错误,请联系管理员");
                    }
                }
                String vendorName = objects.get(0) == null ? null : objects.get(0).toString();
                if (StrUtil.isBlank(vendorName)) {
                    throw new BaseException("供应商名称不可为空");
                }
                String shortName = objects.get(1) == null ? null : objects.get(1).toString();
                if (StrUtil.isBlank(shortName)) {
                    throw new BaseException("供应商简称不可为空");
                }
                String creditCode = objects.get(3) == null ? null : objects.get(3).toString();
                String remark = "";
                if (objects.size() > 11) {
                    remark = objects.get(11) == null ? null : objects.get(11).toString();
                }
                BasVendor vendor = new BasVendor();
                vendor
                    .setCreditCode(creditCode)
                    .setVendorGroup(value)
                    .setVendorCategory(valueCategory)
                    .setStatus(ConstantsEms.ENABLE_STATUS)
                    .setHandleStatus(ConstantsEms.SAVA_STATUS)
                    .setCoreProduct(objects.get(5) == null ? null : objects.get(5).toString())
                    .setBusinessAddr(objects.get(6) == null ? null : objects.get(6).toString())
                    .setRegisterAddr(objects.get(7) == null ? null : objects.get(7).toString())
                    .setBusinessScope(objects.get(8) == null ? null : objects.get(8).toString());
                vendor.setVendorName(vendorName);
                vendor.setShortName(shortName);
                vendor.setRemark(remark);
                String companyCode = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                String customerCode = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (StrUtil.isNotBlank(companyCode)){
                    BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getCompanyCode, companyCode));
                    if (company == null){
                        throw new BaseException(objects.get(9).toString() + "关联公司编码不存在");
                    }
                    else {
                        if ((!ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(company.getStatus()))) {
                            throw new BaseException(objects.get(9).toString() + "关联公司必须是确认且已启用的状态，导入失败！");
                        }
                        else {
                            vendor.setRelateCompanySid(company.getCompanySid());
                        }
                    }
                }
                if (StrUtil.isNotBlank(customerCode)){
                    BasCustomer customer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getCustomerCode, customerCode));
                    if (customer == null){
                        throw new BaseException(objects.get(10).toString() + "关联客户编码不存在");
                    }
                    else {
                        if ((!ConstantsEms.CHECK_STATUS.equals(customer.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(customer.getStatus()))) {
                            throw new BaseException(objects.get(10).toString() + "关联客户必须是确认且已启用的状态，导入失败！");
                        }
                        else {
                            vendor.setCustomerSid(customer.getCustomerSid());
                        }
                    }
                }
                vendorList.add(vendor);
                vendorNameList.add(vendorName);
                shortNameList.add(shortName);
                if (StrUtil.isNotBlank(creditCode)) {
                    creditCodeList.add(creditCode);
                }
            }
            BasVendor params = new BasVendor();
            List<BasVendor> queryList = basVendorMapper.selectBasVendorList(params);
            //名称重复校验
            if (CollectionUtil.isNotEmpty(vendorNameList)) {
                params = new BasVendor();
                params.setVendorNameList(vendorNameList);
                queryList = basVendorMapper.selectBasVendorList(params);
                if (queryList != null && queryList.size() > 0) {
                    vendorNameList = new ArrayList<>();
                    for (int i = 0; i < queryList.size(); i++) {
                        vendorNameList.add(queryList.get(i).getVendorName());
                    }
                    throw new BaseException(vendorNameList.toString() + "名称重复,请检查后再试");
                }
            }
            //简称重复校验
            if (CollectionUtil.isNotEmpty(shortNameList)) {
                params = new BasVendor();
                params.setShortNameList(shortNameList);
                queryList = basVendorMapper.selectBasVendorList(params);
                if (queryList != null && queryList.size() > 0) {
                    shortNameList = new ArrayList<>();
                    for (int i = 0; i < queryList.size(); i++) {
                        shortNameList.add(queryList.get(i).getShortName());
                    }
                    throw new BaseException(shortNameList.toString() + "简称重复,请检查后再试");
                }
            }
            //信用代码重复校验
            if (CollectionUtil.isNotEmpty(creditCodeList)) {
                params = new BasVendor();
                params.setCreditCodeList(creditCodeList);
                queryList = basVendorMapper.selectBasVendorList(params);
                if (queryList != null && queryList.size() > 0) {
                    creditCodeList = new ArrayList<>();
                    for (int i = 0; i < queryList.size(); i++) {
                        creditCodeList.add(queryList.get(i).getCreditCode());
                    }
                    throw new BaseException(creditCodeList.toString() + "信用代码重复,请检查后再试");
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        int row = basVendorMapper.inserts(vendorList);
        if (row > 0) {
            Long[] sids = vendorList.stream().map(BasVendor::getVendorSid).toArray(Long[]::new);
            List<BasVendor> basVendorList = basVendorMapper.selectList(new QueryWrapper<BasVendor>().lambda()
                    .in(BasVendor::getVendorSid, sids));
            if (CollectionUtil.isNotEmpty(basVendorList)) {
                basVendorList.forEach(item->{
                    //待办通知
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    if (ConstantsEms.SAVA_STATUS.equals(item.getHandleStatus())) {
                        sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                .setTableName(ConstantsTable.TABLE_BAS_VENDOR)
                                .setDocumentSid(item.getVendorSid());
                        sysTodoTask.setTitle("供应商档案: " + item.getVendorCode() + " 当前是保存状态，请及时处理！")
                                .setDocumentCode(String.valueOf(item.getVendorCode()))
                                .setNoticeDate(new Date())
                                .setUserId(ApiThreadLocalUtil.get().getUserid());
                        sysTodoTaskMapper.insert(sysTodoTask);
                    }
                    //插入日志
                    List<OperMsg> msgList=new ArrayList<>();
                    msgList = BeanUtils.eq(new BasVendor(), item);
                    MongodbUtil.insertUserLog(item.getVendorSid(), BusinessType.IMPORT.getValue(), msgList, TITLE,null);
                });
            }
        }

        return row;
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
     * 修改供应商档案
     *
     * @param basVendor 供应商档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendor(BasVendor basVendor) {
        BasVendor old = basVendorMapper.selectById(basVendor.getVendorSid());
        QueryWrapper<BasVendor> wrapper = new QueryWrapper<BasVendor>();
        if (!old.getVendorName().equals(basVendor.getVendorName())) {
            wrapper = new QueryWrapper<BasVendor>();
            wrapper.eq("vendor_name", basVendor.getVendorName());
            List<BasVendor> query2 = basVendorMapper.selectList(wrapper);
            if (query2.size() > 0) {
                throw new CheckedException("供应商名称重复，请查看");
            }
        }
        if (old.getShortName() != null){
            if (!old.getShortName().equals(basVendor.getShortName())) {
                wrapper = new QueryWrapper<BasVendor>();
                wrapper.eq("short_name", basVendor.getShortName());
                List<BasVendor> query3 = basVendorMapper.selectList(wrapper);
                if (query3.size() > 0) {
                    throw new CheckedException("供应商简称重复，请查看");
                }
            }
        }
        basVendorBankAccountMapper.deleteBankAccountByVendorSid(basVendor.getVendorSid());
        List<BasVendorBankAccount> bankList = basVendor.getBasVendorBankAccountList();
        bankList.addAll(basVendor.getBaseBankAccountList());
        if (CollectionUtil.isNotEmpty(bankList)) {
            for (BasVendorBankAccount bank : bankList) {
                if (StringUtils.isEmpty(bank.getBankAccount())) {
                    continue;
                }
                bank.setVendorSid(basVendor.getVendorSid());
                bank.setClientId(SecurityUtils.getClientId());
                basVendorBankAccountMapper.insert(bank);
            }
        }
        //联系方式
        basVendorAddrMapper.delete(
                new UpdateWrapper<BasVendorAddr>()
                        .lambda()
                        .eq(BasVendorAddr::getVendorSid, basVendor.getVendorSid())
        );
        if (CollectionUtils.isNotEmpty(basVendor.getAddrList())){
            basVendor.getAddrList().forEach(item->{
                item.setVendorSid(basVendor.getVendorSid());
                if (item.getVendorContactSid() != null){
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    item.setUpdateDate(new Date());
                }
            });
            basVendorAddrMapper.inserts(basVendor.getAddrList());
        }
        //供应商-附件对象
        addBasVendorAttachment(basVendor);
        if (basVendor.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            basVendor.setConfirmDate(new Date());
            basVendor.setConfirmerAccount(SecurityUtils.getUsername());
        }
        //主要客户
        addBasMainCustomerList(basVendor.getVendorSid(),basVendor.getBasMainCustomerList());
        //主要供应商
        addBasMainVendorList(basVendor.getVendorSid(),basVendor.getBasMainVendorList());
        //供应商的产能信息表
        addBasVendorProductivityList(basVendor.getVendorSid(),basVendor.getBasVendorProductivityList());
        //供应商的人员信息表
        addBasVendorTeamList(basVendor.getVendorSid(),basVendor.getBasVendorTeam());
        //供应商的设备信息表
        addBasVendorMachineList(basVendor.getVendorSid(),basVendor.getBasVendorMachineList());
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basVendor.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basVendor.getVendorSid()));
        }
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(old, basVendor);
        String remark = null;
        MongodbDeal.update(basVendor.getVendorSid(), old.getHandleStatus(), basVendor.getHandleStatus(), msgList, TITLE, remark);
        return basVendorMapper.updateAllById(basVendor);
    }

    /**
     * 批量删除供应商档案
     *
     * @param clientIds 需要删除的供应商档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorByIds(List<Long> vendorSids) {
        basVendorCustomerMapper.delete(
                new UpdateWrapper<BasVendorCustomer>()
                        .lambda()
                        .in(BasVendorCustomer::getVendorSid, vendorSids)
        );
        basVendorSupplierMapper.delete(
                new UpdateWrapper<BasVendorSupplier>()
                        .lambda()
                        .in(BasVendorSupplier::getVendorSid, vendorSids)
        );
        basVendorProductivityMapper.delete(
                new UpdateWrapper<BasVendorProductivity>()
                        .lambda()
                        .in(BasVendorProductivity::getVendorSid, vendorSids)
        );
        basVendorTeamMapper.delete(
                new UpdateWrapper<BasVendorTeam>()
                        .lambda()
                        .in(BasVendorTeam::getVendorSid, vendorSids)
        );
        basVendorMachineMapper.delete(
                new UpdateWrapper<BasVendorMachine>()
                        .lambda()
                        .in(BasVendorMachine::getVendorSid, vendorSids)
        );
        for (Long sid : vendorSids) {
            BasVendor basVendor = basVendorMapper.selectById(sid);
            if (!basVendor.getHandleStatus().equals(ConstantsEms.SAVA_STATUS)) {
                throw new CheckedException("选择数据存在不可删除状态，请查看！");
            }
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
        }
        basVendorBankAccountMapper.delete(new UpdateWrapper<BasVendorBankAccount>().lambda().in(BasVendorBankAccount::getVendorSid,vendorSids));
        basVendorAttachmentMapper.delete(new UpdateWrapper<BasVendorAttachment>().lambda().in(BasVendorAttachment::getVendorSid,vendorSids));
        basVendorAddrMapper.delete(new UpdateWrapper<BasVendorAddr>().lambda().in(BasVendorAddr::getVendorSid,vendorSids));
        basVendorMapper.delete(new UpdateWrapper<BasVendor>().lambda().in(BasVendor::getVendorSid,vendorSids));
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, vendorSids));
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorById(Long vendorSid) {
        basVendorBankAccountMapper.deleteBankAccountByVendorSid(vendorSid);
        basVendorMapper.deleteById(vendorSid);
        return 1;
    }

    @Override
    public List<BasVendor> getVendorList(BasVendor basVendor) {
        return basVendorMapper.getVendorList(basVendor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editStatus(BasVendor basVendor) {
        BasVendor bas = new BasVendor();
        List<Long> list = basVendor.getVendorSids();
        if (list != null && list.size() > 0) {
            for (Long id : list) {
                bas = new BasVendor();
                bas.setVendorSid(id);
                bas.setStatus(basVendor.getStatus());
                bas.setUpdateDate(new Date());
                bas.setUpdaterAccount(SecurityUtils.getUsername());
                basVendorMapper.editStatus(bas);
                //插入日志
                String remark = StrUtil.isEmpty(basVendor.getDisableRemark()) ? null : basVendor.getDisableRemark();
                MongodbDeal.status(Long.valueOf(id), basVendor.getStatus(), null, TITLE, remark);
            }
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editHandleStatus(BasVendor basVendor) {
        BasVendor bas = new BasVendor();
        List<Long> list = basVendor.getVendorSids();
        if (list != null && list.size() > 0) {
            for (Long id : list) {
                bas = new BasVendor();
                bas.setVendorSid(id);
                bas.setHandleStatus(basVendor.getHandleStatus());
                bas.setConfirmDate(new Date());
                bas.setConfirmerAccount(SecurityUtils.getUsername());
                basVendorMapper.editHandleStatus(bas);
                //插入日志
                MongodbDeal.check(id, basVendor.getHandleStatus(), null, TITLE, null);
            }
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basVendor.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, list));
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperator(BasVendor basVendor) {
        if (CollectionUtils.isEmpty(basVendor.getVendorSids())){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasVendor> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //我方跟单员
        if (StrUtil.isBlank(basVendor.getBuOperator())){
            basVendor.setBuOperator(null);
        }
        updateWrapper.in(BasVendor::getVendorSid,basVendor.getVendorSids()).set(BasVendor::getBuOperator, basVendor.getBuOperator());
        row = basVendorMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperatorVendor(BasVendor basVendor) {
        if (CollectionUtils.isEmpty(basVendor.getVendorSids())){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasVendor> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //供方业务员
        if (StrUtil.isBlank(basVendor.getBuOperatorVendor())){
            basVendor.setBuOperatorVendor(null);
        }
        updateWrapper.in(BasVendor::getVendorSid,basVendor.getVendorSids()).set(BasVendor::getBuOperatorVendor, basVendor.getBuOperatorVendor());
        row = basVendorMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置合作状态
     * @param basCustomer
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setCooperate(BasVendor basVendor) {
        if (CollectionUtils.isEmpty(basVendor.getVendorSids())){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasVendor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BasVendor::getVendorSid,basVendor.getVendorSids())
                .set(BasVendor::getCooperateStatus, basVendor.getCooperateStatus());
        return basVendorMapper.update(null, updateWrapper);
    }

    @Override
    public String getCompanyNameBySid(Long sid) {
        return basVendorMapper.getCompanyNameBySid(sid);
    }

    @Override
    public String getCustomerNameBySid(Long sid) {
        return basVendorMapper.getCustomerNameBySid(sid);
    }

    @Override
    public String getVendorNameBySid(Long sid) {
        return basVendorMapper.getVendorNameBySid(sid);
    }

    /**
     * 供应商-附件对象
     */
    private void addBasVendorAttachment(BasVendor basVendor) {
        basVendorAttachmentMapper.delete(
                new UpdateWrapper<BasVendorAttachment>()
                        .lambda()
                        .eq(BasVendorAttachment::getVendorSid, basVendor.getVendorSid())
        );
        if (CollectionUtils.isNotEmpty(basVendor.getAttachmentList())) {
            basVendor.getAttachmentList().forEach(o -> {
                o.setVendorSid(basVendor.getVendorSid());
            });
            basVendorAttachmentMapper.inserts(basVendor.getAttachmentList());
        }
    }

    /**
     * 客户与供应商-主要客户
     */
    private void addBasMainCustomerList(Long vendorSid, List<BasVendorCustomer> list) {
        basVendorCustomerMapper.delete(
                new UpdateWrapper<BasVendorCustomer>()
                        .lambda()
                        .eq(BasVendorCustomer::getVendorSid, vendorSid)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(o -> {
                o.setVendorSid(vendorSid);
                if (o.getVendorCustomerSid() != null){
                    o.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            basVendorCustomerMapper.inserts(list);
        }
    }

    /**
     * 客户与供应商-主要供应商
     */
    private void addBasMainVendorList(Long vendorSid, List<BasVendorSupplier> list) {
        basVendorSupplierMapper.delete(
                new UpdateWrapper<BasVendorSupplier>()
                        .lambda()
                        .eq(BasVendorSupplier::getVendorSid, vendorSid)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(o -> {
                o.setVendorSid(vendorSid);
                if (o.getVendorSupplierSid() != null){
                    o.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            basVendorSupplierMapper.inserts(list);
        }
    }

    /**
     * 供应商的产能信息表
     */
    private void addBasVendorProductivityList(Long vendorSid, List<BasVendorProductivity> list) {
        basVendorProductivityMapper.delete(
                new UpdateWrapper<BasVendorProductivity>()
                        .lambda()
                        .eq(BasVendorProductivity::getVendorSid, vendorSid)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(o -> {
                o.setVendorSid(vendorSid);
                if (o.getVendorProductivitySid() != null){
                    o.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            basVendorProductivityMapper.inserts(list);
        }
    }

    /**
     * 供应商的人员信息表
     */
    private void addBasVendorTeamList(Long vendorSid, BasVendorTeam basVendorTeam) {
        basVendorTeamMapper.delete(
                new UpdateWrapper<BasVendorTeam>()
                        .lambda()
                        .eq(BasVendorTeam::getVendorSid, vendorSid)
        );
        if (basVendorTeam != null){
            basVendorTeam.setVendorSid(vendorSid);
            if (basVendorTeam.getVendorTeamSid() != null){
                basVendorTeam.setUpdateDate(new Date());
                basVendorTeam.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            }
            basVendorTeamMapper.insert(basVendorTeam);
        }
    }

    /**
     * 供应商的设备信息表
     */
    private void addBasVendorMachineList(Long vendorSid, List<BasVendorMachine> list) {
        basVendorMachineMapper.delete(
                new UpdateWrapper<BasVendorMachine>()
                        .lambda()
                        .eq(BasVendorMachine::getVendorSid, vendorSid)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(o -> {
                o.setVendorSid(vendorSid);
                if (o.getVendorMachineSid() != null){
                    o.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            basVendorMachineMapper.inserts(list);
        }
    }

    /**
     * 查询供应商档案联系人列表
     *
     * @param basVendor 供应商档案
     * @return 供应商档案
     */
    @Override
    public List<BasVendorAddr> selectBasVendorAddrList(BasVendorAddr addr) {
        return basVendorAddrMapper.selectBasVendorAddrList(addr);
    }


}
