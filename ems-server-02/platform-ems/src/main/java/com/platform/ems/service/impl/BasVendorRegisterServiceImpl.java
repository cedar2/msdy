package com.platform.ems.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

/**
 * 供应商注册-基础Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class BasVendorRegisterServiceImpl extends ServiceImpl<BasVendorRegisterMapper, BasVendorRegister> implements IBasVendorRegisterService {
    @Autowired
    private BasVendorRegisterMapper basVendorRegisterMapper;
    @Autowired
    private BasVendorRecommendMapper basVendorRecommendMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private IBasVendorService basVendorService;
    @Autowired
    private IBasVendorRegisterAddrService basVendorRegisterAddrService;
    @Autowired
    private IBasVendorRegisterBankAccountService basVendorRegisterBankAccountService;
    @Autowired
    private IBasVendorRegisterCustomerService basVendorRegisterCustomerService;
    @Autowired
    private IBasVendorRegisterSupplierService basVendorRegisterSupplierService;
    @Autowired
    private IBasVendorRegisterProductivityService basVendorRegisterProductivityService;
    @Autowired
    private IBasVendorRegisterMachineService basVendorRegisterMachineService;
    @Autowired
    private IBasVendorRegisterTeamService basVendorRegisterTeamService;
    @Autowired
    private IBasVendorRegisterAttachService basVendorRegisterAttachService;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "供应商注册-基础";

    /**
     * 查询供应商注册-基础(流水号，注册码)
     *
     * @param vendorRegister 供应商注册-基础
     * @return 供应商注册-基础
     */
    @Override
    public BasVendorRegister selectBasVendorRegisterByCode(BasVendorRegister vendorRegister) {
        BasVendorRegister response = new BasVendorRegister();
        try {
            response = basVendorRegisterMapper.selectOne(new QueryWrapper<BasVendorRegister>().lambda()
                    .eq(BasVendorRegister::getVendorRegisterNum,vendorRegister.getVendorRegisterNum())
                    .eq(BasVendorRegister::getVendorRegisterCode,vendorRegister.getVendorRegisterCode()));
        } catch (Exception e){
            throw new BaseException("该供应商注册信息数据丢失，请联系管理员恢复或重新注册！");
        }
        if (response == null || response.getVendorRegisterSid() == null){
            throw new CustomException("查询不到对应供应商注册档案，请检查！");
        }
        return this.selectBasVendorRegisterById(response.getVendorRegisterSid());
    }

    /**
     * 查询供应商注册-基础
     *
     * @param vendorRegisterSid 供应商注册-基础ID
     * @return 供应商注册-基础
     */
    @Override
    public BasVendorRegister selectBasVendorRegisterById(Long vendorRegisterSid) {
        BasVendorRegister basVendorRegister = basVendorRegisterMapper.selectBasVendorRegisterById(vendorRegisterSid);
        //联系信息
        List<BasVendorRegisterAddr> addrList = basVendorRegisterAddrService.selectBasVendorRegisterAddrListById(vendorRegisterSid);
        basVendorRegister.setAddrList(addrList);
        //账户信息（一般户，基本户）
        List<BasVendorRegisterBankAccount> bankAccountList = basVendorRegisterBankAccountService.selectBasVendorRegisterBankAccountListById(vendorRegisterSid);
        List<BasVendorRegisterBankAccount> commonList = bankAccountList.stream()
                .filter(o -> ConstantsEms.BANK_ACCOUNT_TYPE_YBH.equals(o.getAccountType())).collect(Collectors.toList());
        basVendorRegister.setBasVendorBankAccountList(commonList);
        List<BasVendorRegisterBankAccount> baseList = bankAccountList.stream()
                .filter(o -> ConstantsEms.BANK_ACCOUNT_TYPE_JBH.equals(o.getAccountType())).collect(Collectors.toList());
        basVendorRegister.setBaseBankAccountList(baseList);
        //主要客户
        List<BasVendorRegisterCustomer> customerList = basVendorRegisterCustomerService.selectBasVendorRegisterCustomerListById(vendorRegisterSid);
        basVendorRegister.setBasMainCustomerList(customerList);
        //主要供应商
        List<BasVendorRegisterSupplier> supplierList = basVendorRegisterSupplierService.selectBasVendorRegisterSupplierListById(vendorRegisterSid);
        basVendorRegister.setBasMainVendorList(supplierList);
        //产能信息
        List<BasVendorRegisterProductivity> productivityList = basVendorRegisterProductivityService.selectBasVendorRegisterProductivityListById(vendorRegisterSid);
        basVendorRegister.setBasVendorProductivityList(productivityList);
        //设备信息
        List<BasVendorRegisterMachine> machineList = basVendorRegisterMachineService.selectBasVendorRegisterMachineListById(vendorRegisterSid);
        basVendorRegister.setBasVendorMachineList(machineList);
        //人员信息
        List<BasVendorRegisterTeam> teamList = basVendorRegisterTeamService.selectBasVendorRegisterTeamListById(vendorRegisterSid);
        if (CollectionUtil.isNotEmpty(teamList)) {
            basVendorRegister.setBasVendorTeam(teamList.get(teamList.size() - 1));
        }
        //附件信息
        List<BasVendorRegisterAttach> attachList = basVendorRegisterAttachService.selectBasVendorRegisterAttachListById(vendorRegisterSid);
        basVendorRegister.setAttachmentList(attachList);
        MongodbUtil.find(basVendorRegister);
        return basVendorRegister;
    }

    /**
     * 查询供应商注册-基础列表
     *
     * @param basVendorRegister 供应商注册-基础
     * @return 供应商注册-基础
     */
    @Override
    public List<BasVendorRegister> selectBasVendorRegisterList(BasVendorRegister basVendorRegister) {
        return basVendorRegisterMapper.selectBasVendorRegisterList(basVendorRegister);
    }

    /**
     * 新增供应商注册-基础
     * 需要注意编码重复校验
     *
     * @param basVendorRegister 供应商注册-基础
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BasVendorRegister insertBasVendorRegister(BasVendorRegister basVendorRegister) {
        checkName(basVendorRegister);
        checkShortName(basVendorRegister);
        setConfirm(basVendorRegister);
        basVendorRegister.setClientId(ConstantsEms.CLIENT_ID_10001);
        basVendorRegister.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
        basVendorRegister.setVendorRegisterSid(IdWorker.getId());
        basVendorRegister.setVendorRegisterCode(CommonUtil.random(5));
        int row = basVendorRegisterMapper.insert(basVendorRegister);
        if (row > 0) {
            //联系信息
            int addr = basVendorRegisterAddrService.insertBasVendorRegisterAddr
                    (basVendorRegister.getAddrList(), basVendorRegister.getVendorRegisterSid());
            //合并基本户和一般户
            List<BasVendorRegisterBankAccount> bankList = basVendorRegister.getBasVendorBankAccountList();
            if (CollectionUtil.isNotEmpty(basVendorRegister.getBaseBankAccountList())) {
                bankList.addAll(basVendorRegister.getBaseBankAccountList());
            }
            basVendorRegister.setBasVendorBankAccountList(bankList);
            int bank = basVendorRegisterBankAccountService.insertBasVendorRegisterBankAccount
                    (basVendorRegister.getBasVendorBankAccountList(), basVendorRegister.getVendorRegisterSid());
            //主要客户
            int customer = basVendorRegisterCustomerService.insertBasVendorRegisterCustomer
                    (basVendorRegister.getBasMainCustomerList(), basVendorRegister.getVendorRegisterSid());
            //主要供应商
            int supplier = basVendorRegisterSupplierService.insertBasVendorRegisterSupplier
                    (basVendorRegister.getBasMainVendorList(), basVendorRegister.getVendorRegisterSid());
            //产能信息
            int productivity = basVendorRegisterProductivityService.insertBasVendorRegisterProductivity
                    (basVendorRegister.getBasVendorProductivityList(), basVendorRegister.getVendorRegisterSid());
            //设备信息
            int machine = basVendorRegisterMachineService.insertBasVendorRegisterMachine
                    (basVendorRegister.getBasVendorMachineList(), basVendorRegister.getVendorRegisterSid());
            //合并人员信息
            if (basVendorRegister.getBasVendorTeam() != null) {
                List<BasVendorRegisterTeam> teamList = new ArrayList<>();
                teamList.add(basVendorRegister.getBasVendorTeam());
                int team = basVendorRegisterTeamService.insertBasVendorRegisterTeam(teamList, basVendorRegister.getVendorRegisterSid());
            }
            //附件
            int attach = basVendorRegisterAttachService.insertBasVendorRegisterAttach
                    (basVendorRegister.getAttachmentList(), basVendorRegister.getVendorRegisterSid());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegister(), basVendorRegister);
            MongodbUtil.insertUserLog(basVendorRegister.getVendorRegisterSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        basVendorRegister = basVendorRegisterMapper.selectById(basVendorRegister.getVendorRegisterSid());
        //邮件通知
        sentMail(basVendorRegister);
        return basVendorRegister;
    }

    /**
     * 修改供应商注册-基础
     *
     * @param basVendorRegister 供应商注册-基础
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegister(BasVendorRegister basVendorRegister) {
        BasVendorRegister response = this.selectBasVendorRegisterById(basVendorRegister.getVendorRegisterSid());
        if (!response.getVendorName().equals(basVendorRegister.getVendorName())) {
            checkName(basVendorRegister);
        }
        if (!response.getShortName().equals(basVendorRegister.getShortName())) {
            checkShortName(basVendorRegister);
        }
        setConfirm(basVendorRegister);
        int row = basVendorRegisterMapper.updateById(basVendorRegister);
        if (row > 0) {
            //联系信息
            int addr = basVendorRegisterAddrService.updateBasVendorRegisterAddr
                    (response.getAddrList(), basVendorRegister.getAddrList(), basVendorRegister.getVendorRegisterSid());
            //合并基本户和一般户
            List<BasVendorRegisterBankAccount> bankAccountList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(basVendorRegister.getBaseBankAccountList())) {
                bankAccountList.addAll(basVendorRegister.getBaseBankAccountList());
            }
            if (CollectionUtil.isNotEmpty(basVendorRegister.getBasVendorBankAccountList())) {
                bankAccountList.addAll(basVendorRegister.getBasVendorBankAccountList());
            }
            List<BasVendorRegisterBankAccount> responseBankAccountList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(response.getBaseBankAccountList())) {
                responseBankAccountList.addAll(response.getBaseBankAccountList());
            }
            if (CollectionUtil.isNotEmpty(response.getBasVendorBankAccountList())) {
                responseBankAccountList.addAll(response.getBasVendorBankAccountList());
            }
            int bank = basVendorRegisterBankAccountService.updateBasVendorRegisterBankAccount
                    (responseBankAccountList, bankAccountList, basVendorRegister.getVendorRegisterSid());
            //主要客户
            int customer = basVendorRegisterCustomerService.updateBasVendorRegisterCustomer
                    (response.getBasMainCustomerList(), basVendorRegister.getBasMainCustomerList(), basVendorRegister.getVendorRegisterSid());
            //主要供应商
            int supplier = basVendorRegisterSupplierService.updateBasVendorRegisterSupplier
                    (response.getBasMainVendorList(), basVendorRegister.getBasMainVendorList(), basVendorRegister.getVendorRegisterSid());
            //产能信息
            int productivity = basVendorRegisterProductivityService.updateBasVendorRegisterProductivity
                    (response.getBasVendorProductivityList(), basVendorRegister.getBasVendorProductivityList(), basVendorRegister.getVendorRegisterSid());
            //设备信息
            int machine = basVendorRegisterMachineService.updateBasVendorRegisterMachine
                    (response.getBasVendorMachineList(), basVendorRegister.getBasVendorMachineList(), basVendorRegister.getVendorRegisterSid());
            //合并人员信息
            //新的 请求的
            List<BasVendorRegisterTeam> teamList_request = new ArrayList<>();
            if (basVendorRegister.getBasVendorTeam() != null) {
                teamList_request.add(basVendorRegister.getBasVendorTeam());
            }
            //旧数据的
            List<BasVendorRegisterTeam> teamList_response = new ArrayList<>();
            if (response.getBasVendorTeam() != null) {
                teamList_response.add(response.getBasVendorTeam());
            }
            int team = basVendorRegisterTeamService.updateBasVendorRegisterTeam(teamList_response, teamList_request, basVendorRegister.getVendorRegisterSid());
            //附件
            int attach = basVendorRegisterAttachService.updateBasVendorRegisterAttach
                    (response.getAttachmentList(), basVendorRegister.getAttachmentList(), basVendorRegister.getVendorRegisterSid());
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegister.getVendorRegisterSid(), BusinessType.UPDATE.getValue(), response, basVendorRegister, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-基础
     *
     * @param basVendorRegister 供应商注册-基础
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegister(BasVendorRegister basVendorRegister) {
        BasVendorRegister response = basVendorRegisterMapper.selectBasVendorRegisterById(basVendorRegister.getVendorRegisterSid());
        if (!response.getVendorName().equals(basVendorRegister.getVendorName())) {
            checkName(basVendorRegister);
        }
        if (!response.getShortName().equals(basVendorRegister.getShortName())) {
            checkShortName(basVendorRegister);
        }
        setConfirm(basVendorRegister);
        int row = basVendorRegisterMapper.updateById(basVendorRegister);
        if (row > 0) {
            //联系信息
            int addr = basVendorRegisterAddrService.updateBasVendorRegisterAddr
                    (response.getAddrList(), basVendorRegister.getAddrList(), basVendorRegister.getVendorRegisterSid());
            //合并基本户和一般户
            List<BasVendorRegisterBankAccount> bankList = basVendorRegister.getBasVendorBankAccountList();
            if (CollectionUtil.isNotEmpty(basVendorRegister.getBaseBankAccountList())) {
                bankList.addAll(basVendorRegister.getBaseBankAccountList());
            }
            basVendorRegister.setBasVendorBankAccountList(bankList);
            int bank = basVendorRegisterBankAccountService.updateBasVendorRegisterBankAccount
                    (response.getBasVendorBankAccountList(), basVendorRegister.getBasVendorBankAccountList(), basVendorRegister.getVendorRegisterSid());
            //主要客户
            int customer = basVendorRegisterCustomerService.updateBasVendorRegisterCustomer
                    (response.getBasMainCustomerList(), basVendorRegister.getBasMainCustomerList(), basVendorRegister.getVendorRegisterSid());
            //主要供应商
            int supplier = basVendorRegisterSupplierService.updateBasVendorRegisterSupplier
                    (response.getBasMainVendorList(), basVendorRegister.getBasMainVendorList(), basVendorRegister.getVendorRegisterSid());
            //产能信息
            int productivity = basVendorRegisterProductivityService.updateBasVendorRegisterProductivity
                    (response.getBasVendorProductivityList(), basVendorRegister.getBasVendorProductivityList(), basVendorRegister.getVendorRegisterSid());
            //设备信息
            int machine = basVendorRegisterMachineService.updateBasVendorRegisterMachine
                    (response.getBasVendorMachineList(), basVendorRegister.getBasVendorMachineList(), basVendorRegister.getVendorRegisterSid());
            //合并人员信息
            List<BasVendorRegisterTeam> teamList_request = new ArrayList<>();
            if (basVendorRegister.getBasVendorTeam() != null) {
                teamList_request.add(basVendorRegister.getBasVendorTeam());
            }
            List<BasVendorRegisterTeam> teamList_response = new ArrayList<>();
            if (basVendorRegister.getBasVendorTeam() != null) {
                teamList_response.add(basVendorRegister.getBasVendorTeam());
            }
            int team = basVendorRegisterTeamService.updateBasVendorRegisterTeam(teamList_response, teamList_request, basVendorRegister.getVendorRegisterSid());
            //附件
            int attach = basVendorRegisterAttachService.updateBasVendorRegisterAttach
                    (response.getAttachmentList(), basVendorRegister.getAttachmentList(), basVendorRegister.getVendorRegisterSid());
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegister.getVendorRegisterSid(), BusinessType.CHANGE.getValue(), response, basVendorRegister, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商注册-基础
     *
     * @param vendorRegisterSids 需要删除的供应商注册-基础ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterByIds(List<Long> vendorRegisterSids) {
        int row = 0;
        for (Long sid : vendorRegisterSids) {
            BasVendorRegister basVendorRegister = basVendorRegisterMapper.selectById(sid);
            row += basVendorRegisterMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(basVendorRegister, new BasVendorRegister());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        int addr = basVendorRegisterAddrService.deleteBasVendorRegisterAddrListByIds(vendorRegisterSids);
        int bank = basVendorRegisterBankAccountService.deleteBasVendorRegisterBankAccountListByIds(vendorRegisterSids);
        int customer = basVendorRegisterCustomerService.deleteBasVendorRegisterCustomerListByIds(vendorRegisterSids);
        int supplier = basVendorRegisterSupplierService.deleteBasVendorRegisterSupplierListByIds(vendorRegisterSids);
        int productivity = basVendorRegisterProductivityService.deleteBasVendorRegisterProductivityListByIds(vendorRegisterSids);
        int machine = basVendorRegisterMachineService.deleteBasVendorRegisterMachineListByIds(vendorRegisterSids);
        int team = basVendorRegisterTeamService.deleteBasVendorRegisterTeamListByIds(vendorRegisterSids);
        int attach = basVendorRegisterAttachService.deleteBasVendorRegisterAttachListByIds(vendorRegisterSids);
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param basVendorRegister
     * @return
     */
    @Override
    public int check(BasVendorRegister basVendorRegister) {
        int row = 0;
        Long[] sids = basVendorRegister.getVendorRegisterSidList();
        if (basVendorRegister.getVendorRegisterSid() != null) {
            sids = Arrays.copyOf(sids, sids.length + 1);
            sids[sids.length - 1] = basVendorRegister.getVendorRegisterSid();
        }
        if (sids != null && sids.length > 0) {
            if (ConstantsEms.CHECK_STATUS.equals(basVendorRegister.getHandleStatus())) {
                row = basVendorRegisterMapper.update(null, new UpdateWrapper<BasVendorRegister>().lambda()
                        .set(BasVendorRegister::getHandleStatus, basVendorRegister.getHandleStatus())
                        .set(BasVendorRegister::getConfirmDate, new Date())
                        .in(BasVendorRegister::getVendorRegisterSid, sids));
                for (Long sid : sids) {
                    BasVendorRegister request = this.selectBasVendorRegisterById(sid);
                    BasVendor vendor = new BasVendor();
                    BeanCopyUtils.copyProperties(request, vendor);
                    vendor.setStatus(ConstantsEms.DISENABLE_STATUS);
                    //联系信息
                    if (CollectionUtil.isNotEmpty(request.getAddrList())){
                        List<BasVendorAddr> addrList = BeanCopyUtils.copyListProperties(request.getAddrList(), BasVendorAddr::new);
                        vendor.setAddrList(addrList);
                    }
                    //账号信息
                    List<BasVendorRegisterBankAccount> bank = new ArrayList<>();
                    List<BasVendorRegisterBankAccount> bankList = request.getBasVendorBankAccountList();
                    List<BasVendorRegisterBankAccount> basBankList = request.getBaseBankAccountList();
                    if (CollectionUtil.isNotEmpty(basBankList)) {
                        bank.addAll(basBankList);
                    }
                    if (CollectionUtil.isNotEmpty(bankList)){
                        bank.addAll(bankList);
                    }
                    if (CollectionUtil.isNotEmpty(bank)){
                        List<BasVendorBankAccount> bankAccountList = BeanCopyUtils.copyListProperties(bankList, BasVendorBankAccount::new);
                        bankAccountList.forEach(item->{
                            item.setStatus(ConstantsEms.ENABLE_STATUS);
                        });
                        vendor.setBasVendorBankAccountList(bankAccountList);
                    }
                    //主要客户
                    if (CollectionUtil.isNotEmpty(request.getBasMainCustomerList())){
                        List<BasVendorCustomer> customerList = BeanCopyUtils.copyListProperties(request.getBasMainCustomerList(), BasVendorCustomer::new);
                        vendor.setBasMainCustomerList(customerList);
                    }
                    //主要供应商
                    if (CollectionUtil.isNotEmpty(request.getBasMainVendorList())){
                        List<BasVendorSupplier> vendorList = BeanCopyUtils.copyListProperties(request.getBasMainVendorList(), BasVendorSupplier::new);
                        vendor.setBasMainVendorList(vendorList);
                    }
                    //产能信息
                    if (CollectionUtil.isNotEmpty(request.getBasVendorProductivityList())){
                        List<BasVendorProductivity> productivityList = BeanCopyUtils.copyListProperties(request.getBasVendorProductivityList(), BasVendorProductivity::new);
                        vendor.setBasVendorProductivityList(productivityList);
                    }
                    //人员信息
                    if (request.getBasVendorTeam() != null){
                        BasVendorTeam team = new BasVendorTeam();
                        BeanCopyUtils.copyProperties(request.getBasVendorTeam(), team);
                        vendor.setBasVendorTeam(team);
                    }
                    //设备信息
                    if (CollectionUtil.isNotEmpty(request.getBasVendorMachineList())){
                        List<BasVendorMachine> machineList = BeanCopyUtils.copyListProperties(request.getBasVendorMachineList(),BasVendorMachine::new);
                        vendor.setBasVendorMachineList(machineList);
                    }
                    //附件清单
                    if (CollectionUtil.isNotEmpty(request.getAttachmentList())){
                        List<BasVendorAttachment> attachList = BeanCopyUtils.copyListProperties(request.getAttachmentList(), BasVendorAttachment::new);
                        vendor.setAttachmentList(attachList);
                    }
                    basVendorService.insertBasVendor(vendor);
                }
            } else {
                row = basVendorRegisterMapper.update(null, new UpdateWrapper<BasVendorRegister>().lambda().set(BasVendorRegister::getHandleStatus, basVendorRegister.getHandleStatus())
                        .in(BasVendorRegister::getVendorRegisterSid, sids));
            }
        }
        return row;
    }

    /**
     * 验证名称是否重复
     *
     * @param basVendorRegister
     * @return
     */
    public void checkName(BasVendorRegister basVendorRegister) {
        List<BasVendorRegister> nameList_1 = basVendorRegisterMapper.selectList(new QueryWrapper<BasVendorRegister>().lambda()
                .eq(BasVendorRegister::getVendorName, basVendorRegister.getVendorName()));
        if (CollectionUtil.isNotEmpty(nameList_1)) {
            throw new BaseException("注册列表中已存在相同名称的供应商");
        }
        List<BasVendor> nameList_3 = basVendorMapper.selectList(new QueryWrapper<BasVendor>().lambda()
                .eq(BasVendor::getVendorName, basVendorRegister.getVendorName()));
        if (CollectionUtil.isNotEmpty(nameList_3)) {
            throw new BaseException("该供应商名称已存在系统档案中");
        }
    }

    /**
     * 验证简称是否重复
     *
     * @param basVendorRegister
     * @return
     */
    public void checkShortName(BasVendorRegister basVendorRegister) {
        List<BasVendorRegister> shortNameList_1 = basVendorRegisterMapper.selectList(new QueryWrapper<BasVendorRegister>().lambda()
                .eq(BasVendorRegister::getShortName, basVendorRegister.getShortName()));
        if (CollectionUtil.isNotEmpty(shortNameList_1)) {
            throw new BaseException("注册列表中已存在相同简称的供应商");
        }
        List<BasVendor> shortNameList_3 = basVendorMapper.selectList(new QueryWrapper<BasVendor>().lambda()
                .eq(BasVendor::getShortName, basVendorRegister.getShortName()));
        if (CollectionUtil.isNotEmpty(shortNameList_3)) {
            throw new BaseException("该供应商简称已存在系统档案中");
        }
    }

    /**
     * 确认操作额外处理
     *
     * @param basVendorRegister
     * @return
     */
    public void setConfirm(BasVendorRegister basVendorRegister) {
        if (ConstantsEms.CHECK_STATUS.equals(basVendorRegister.getHandleStatus())) {
            basVendorRegister.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }

    /**
     * 往邮箱发送注册码等信息
     *
     * @param basVendorRegister
     * @return
     */
    public void sentMail(BasVendorRegister basVendorRegister) {
        if (StrUtil.isBlank(basVendorRegister.getRegisterEmail())) {
            return;
        } else {
            String mailtext =
                    "<font color=\"gray\">回执单号：</font><font color=\"black\">" + basVendorRegister.getVendorRegisterNum() + "</font>  <br />" +
                            "<font color=\"gray\">回执码：</font><font color=\"black\">" + basVendorRegister.getVendorRegisterCode() + "</font>  <br />" +
                            "<font color=\"black\">供应商注册成功，您可在系统首页“回执入口”中查看对应供应商信息！</font>";
            try {
                MailUtil.send(basVendorRegister.getRegisterEmail(), null, basVendorRegister.getRegisterEmail(),
                        "【DCSCM供应商注册结果通知】 " + " 供应商：" + basVendorRegister.getVendorName() + " 信息注册成功，请耐心等待审核", mailtext, true);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("邮件发送失败");
            }
        }
    }
}
