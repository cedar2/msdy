package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterBankAccount;

/**
 * 供应商注册-银行账户信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterBankAccountService extends IService<BasVendorRegisterBankAccount> {
    /**
     * 查询供应商注册-银行账户信息
     *
     * @param vendorRegisterBankAccountSid 供应商注册-银行账户信息ID
     * @return 供应商注册-银行账户信息
     */
    public BasVendorRegisterBankAccount selectBasVendorRegisterBankAccountById(Long vendorRegisterBankAccountSid);

    /**
     * 查询供应商注册-银行账户信息列表
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 供应商注册-银行账户信息集合
     */
    public List<BasVendorRegisterBankAccount> selectBasVendorRegisterBankAccountList(BasVendorRegisterBankAccount basVendorRegisterBankAccount);

    /**
     * 新增供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    public int insertBasVendorRegisterBankAccount(BasVendorRegisterBankAccount basVendorRegisterBankAccount);

    /**
     * 修改供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    public int updateBasVendorRegisterBankAccount(BasVendorRegisterBankAccount basVendorRegisterBankAccount);

    /**
     * 变更供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccount 供应商注册-银行账户信息
     * @return 结果
     */
    public int changeBasVendorRegisterBankAccount(BasVendorRegisterBankAccount basVendorRegisterBankAccount);

    /**
     * 批量删除供应商注册-银行账户信息
     *
     * @param vendorRegisterBankAccountSids 需要删除的供应商注册-银行账户信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterBankAccountByIds(List<Long> vendorRegisterBankAccountSids);


    /**
     * 由主表查询供应商注册-银行账户信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-银行账户信息集合
     */
    public List<BasVendorRegisterBankAccount> selectBasVendorRegisterBankAccountListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccountList List 供应商注册-银行账户信息
     * @return 结果
     */
    public int insertBasVendorRegisterBankAccount(List<BasVendorRegisterBankAccount> basVendorRegisterBankAccountList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-银行账户信息
     *
     * @param basVendorRegisterBankAccountList List 供应商注册-银行账户信息
     * @return 结果
     */
    public int updateBasVendorRegisterBankAccount(List<BasVendorRegisterBankAccount> basVendorRegisterBankAccountList);

    /**
     * 由主表批量修改供应商注册-银行账户信息
     *
     * @param response List 供应商注册-银行账户信息 (原来的)
     * @param request  List 供应商注册-银行账户信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterBankAccount(List<BasVendorRegisterBankAccount> response, List<BasVendorRegisterBankAccount> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-银行账户信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterBankAccountListByIds(List<Long> vendorRegisterSids);
}
