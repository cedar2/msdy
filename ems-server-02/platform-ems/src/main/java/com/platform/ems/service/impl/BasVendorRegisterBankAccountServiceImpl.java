package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasVendorRegisterBankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterBankAccountMapper;
import com.platform.ems.service.IBasVendorRegisterBankAccountService;

/**
 * 供应商注册-银行账户信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterBankAccountServiceImpl extends ServiceImpl<BasVendorRegisterBankAccountMapper, BasVendorRegisterBankAccount> implements IBasVendorRegisterBankAccountService {
    @Autowired
    private BasVendorRegisterBankAccountMapper basVendorRegisterBankAccountMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-银行账户信息";

    /**
     * 查询供应商注册-银行账户信息
     *
     * @param vendorRegisterBankAccountSid 供应商注册-银行账户信息ID
     * @return 供应商注册-银行账户信息
     */
    @Override
    public BasVendorRegisterBankAccount selectBasVendorRegisterBankAccountById(Long vendorRegisterBankAccountSid) {
        BasVendorRegisterBankAccount basVendorRegisterBankAccount = basVendorRegisterBankAccountMapper.selectBasVendorRegisterBankAccountById
                (vendorRegisterBankAccountSid);
        MongodbUtil.find(basVendorRegisterBankAccount);
        return basVendorRegisterBankAccount;
    }

    /**
     * 查询供应商注册-银行账户信息列表
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 供应商注册-银行账户信息
     */
    @Override
    public List<BasVendorRegisterBankAccount> selectBasVendorRegisterBankAccountList(BasVendorRegisterBankAccount basVendorRegisterBankAccount) {
        return basVendorRegisterBankAccountMapper.selectBasVendorRegisterBankAccountList(basVendorRegisterBankAccount);
    }

    /**
     * 新增供应商注册-银行账户信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterBankAccount(BasVendorRegisterBankAccount basVendorRegisterBankAccount) {
        int row = basVendorRegisterBankAccountMapper.insert(basVendorRegisterBankAccount);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterBankAccount(), basVendorRegisterBankAccount);
            MongodbUtil.insertUserLog(basVendorRegisterBankAccount.getVendorRegisterBankAccountSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterBankAccount(BasVendorRegisterBankAccount basVendorRegisterBankAccount) {
        BasVendorRegisterBankAccount response = basVendorRegisterBankAccountMapper.selectBasVendorRegisterBankAccountById
                (basVendorRegisterBankAccount.getVendorRegisterBankAccountSid());
        int row = basVendorRegisterBankAccountMapper.updateById(basVendorRegisterBankAccount);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterBankAccount.getVendorRegisterBankAccountSid(),
                    BusinessType.UPDATE.getValue(), response, basVendorRegisterBankAccount, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterBankAccount(BasVendorRegisterBankAccount basVendorRegisterBankAccount) {
        BasVendorRegisterBankAccount response = basVendorRegisterBankAccountMapper.selectBasVendorRegisterBankAccountById
                (basVendorRegisterBankAccount.getVendorRegisterBankAccountSid());
        int row = basVendorRegisterBankAccountMapper.updateAllById(basVendorRegisterBankAccount);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterBankAccount.getVendorRegisterBankAccountSid(),
                    BusinessType.CHANGE.getValue(), response, basVendorRegisterBankAccount, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商注册-银行账户信息
     *
     * @param vendorRegisterBankAccountSids 需要删除的供应商注册-银行账户信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterBankAccountByIds(List<Long> vendorRegisterBankAccountSids) {
        int row = 0;
        for (Long sid : vendorRegisterBankAccountSids) {
            BasVendorRegisterBankAccount response = basVendorRegisterBankAccountMapper.selectById(sid);
            row += basVendorRegisterBankAccountMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterBankAccount());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }


    /**
     * 由主表查询供应商注册-银行账户信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-银行账户信息集合
     */
    @Override
    public List<BasVendorRegisterBankAccount> selectBasVendorRegisterBankAccountListById(Long vendorRegisterSid) {
        List<BasVendorRegisterBankAccount> response = basVendorRegisterBankAccountMapper.selectBasVendorRegisterBankAccountList
                (new BasVendorRegisterBankAccount().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterBankAccount -> {
            MongodbUtil.find(basVendorRegisterBankAccount);
        });
        return response;
    }


    /**
     * 新增供应商注册-银行账户信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterBankAccount(List<BasVendorRegisterBankAccount> basVendorRegisterBankAccountList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterBankAccountList)) {
            return 0;
        }
        basVendorRegisterBankAccountList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterBankAccountMapper.inserts(basVendorRegisterBankAccountList);
        if (row > 0) {
            //插入日志
            basVendorRegisterBankAccountList.forEach(basVendorRegisterBankAccount -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterBankAccount(), basVendorRegisterBankAccount);
                MongodbUtil.insertUserLog(basVendorRegisterBankAccount.getVendorRegisterBankAccountSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccountList 供应商注册-银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterBankAccount(List<BasVendorRegisterBankAccount> basVendorRegisterBankAccountList) {
        int row = 0;
        for (BasVendorRegisterBankAccount basVendorRegisterBankAccount : basVendorRegisterBankAccountList) {
            BasVendorRegisterBankAccount response = basVendorRegisterBankAccountMapper.selectBasVendorRegisterBankAccountById
                    (basVendorRegisterBankAccount.getVendorRegisterBankAccountSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterBankAccount);
            if (msgList.size() > 0) {
                row += basVendorRegisterBankAccountMapper.updateById(basVendorRegisterBankAccount);
                MongodbUtil.insertUserLog(basVendorRegisterBankAccount.getVendorRegisterBankAccountSid(),
                        BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccountList 供应商注册-银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterBankAccount(List<BasVendorRegisterBankAccount> response, List<BasVendorRegisterBankAccount> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterBankAccount::getVendorRegisterBankAccountSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterBankAccount> updateBankAccountList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateBankAccountList = request.stream()
                        .filter(item -> item.getVendorRegisterBankAccountSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateBankAccountList)) {
                this.deleteBasVendorRegisterBankAccountByIds(oldIds);
            } else {
                List<Long> updateIds = updateBankAccountList.stream()
                        .map(BasVendorRegisterBankAccount::getVendorRegisterBankAccountSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterBankAccountByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterBankAccount(updateBankAccountList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterBankAccount> newBankAccountList = request.stream()
                .filter(item -> item.getVendorRegisterBankAccountSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newBankAccountList)) {
            row += this.insertBasVendorRegisterBankAccount(newBankAccountList, vendorRegisterSid);
        }
        return row;
    }


    /**
     * 由主表批量删除供应商注册-财务信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterBankAccountListByIds(List<Long> vendorRegisterSids) {
        List<BasVendorRegisterBankAccount> addrList = basVendorRegisterBankAccountMapper.selectList(new QueryWrapper<BasVendorRegisterBankAccount>().lambda()
                .in(BasVendorRegisterBankAccount::getVendorRegisterSid, vendorRegisterSids));
        List<Long> bankSids = addrList.stream().map(BasVendorRegisterBankAccount::getVendorRegisterBankAccountSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterBankAccountByIds(bankSids);
    }
}
