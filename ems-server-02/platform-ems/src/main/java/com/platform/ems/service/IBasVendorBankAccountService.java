package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorBankAccount;

/**
 * 供应商银行账户信息Service接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface IBasVendorBankAccountService extends IService<BasVendorBankAccount>{
    /**
     * 查询供应商银行账户信息
     *
     * @param clientId 供应商银行账户信息ID
     * @return 供应商银行账户信息
     */
    public BasVendorBankAccount selectBasVendorBankAccountById(String clientId);

    /**
     * 查询供应商银行账户信息列表
     *
     * @param basVendorBankAccount 供应商银行账户信息
     * @return 供应商银行账户信息集合
     */
    public List<BasVendorBankAccount> selectBasVendorBankAccountList(BasVendorBankAccount basVendorBankAccount);

    /**
     * 新增供应商银行账户信息
     *
     * @param basVendorBankAccount 供应商银行账户信息
     * @return 结果
     */
    public int insertBasVendorBankAccount(BasVendorBankAccount basVendorBankAccount);

    /**
     * 修改供应商银行账户信息
     *
     * @param basVendorBankAccount 供应商银行账户信息
     * @return 结果
     */
    public int updateBasVendorBankAccount(BasVendorBankAccount basVendorBankAccount);

    /**
     * 批量删除供应商银行账户信息
     *
     * @param clientIds 需要删除的供应商银行账户信息ID
     * @return 结果
     */
    public int deleteBasVendorBankAccountByIds(List<String>  clientIds);

    /**
     * 删除供应商银行账户信息信息
     *
     * @param clientId 供应商银行账户信息ID
     * @return 结果
     */
    public int deleteBasVendorBankAccountById(String clientId);
}
