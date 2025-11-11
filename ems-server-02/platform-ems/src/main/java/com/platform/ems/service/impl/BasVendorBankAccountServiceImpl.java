package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.BasVendorBankAccount;
import com.platform.ems.mapper.BasVendorBankAccountMapper;
import com.platform.ems.service.IBasVendorBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 供应商银行账户信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Service
@SuppressWarnings("all")
public class BasVendorBankAccountServiceImpl extends ServiceImpl<BasVendorBankAccountMapper,BasVendorBankAccount>  implements IBasVendorBankAccountService {
    @Autowired
    private BasVendorBankAccountMapper basVendorBankAccountMapper;

    /**
     * 查询供应商银行账户信息
     *
     * @param clientId 供应商银行账户信息ID
     * @return 供应商银行账户信息
     */
    @Override
    public BasVendorBankAccount selectBasVendorBankAccountById(String clientId) {
        return basVendorBankAccountMapper.selectBasVendorBankAccountById(clientId);
    }

    /**
     * 查询供应商银行账户信息列表
     *
     * @param basVendorBankAccount 供应商银行账户信息
     * @return 供应商银行账户信息
     */
    @Override
    public List<BasVendorBankAccount> selectBasVendorBankAccountList(BasVendorBankAccount basVendorBankAccount) {
        return basVendorBankAccountMapper.selectBasVendorBankAccountList(basVendorBankAccount);
    }

    /**
     * 新增供应商银行账户信息
     *
     * @param basVendorBankAccount 供应商银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorBankAccount(BasVendorBankAccount basVendorBankAccount) {
        return basVendorBankAccountMapper.insert(basVendorBankAccount);
    }

    /**
     * 修改供应商银行账户信息
     *
     * @param basVendorBankAccount 供应商银行账户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorBankAccount(BasVendorBankAccount basVendorBankAccount) {
        return basVendorBankAccountMapper.updateById(basVendorBankAccount);
    }

    /**
     * 批量删除供应商银行账户信息
     *
     * @param clientIds 需要删除的供应商银行账户信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorBankAccountByIds(List<String> clientIds) {
        return basVendorBankAccountMapper.deleteBatchIds(clientIds);
    }

    @Override
    public int deleteBasVendorBankAccountById(String clientId) {
        return 0;
    }


}
