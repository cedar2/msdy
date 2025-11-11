package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FunFundAccount;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资金账户信息Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IFunFundAccountService extends IService<FunFundAccount> {
    /**
     * 查询资金账户信息
     */
    FunFundAccount selectFunFundAccountById(Long fundAccountSid);

    /**
     * 查询资金账户信息列表
     */
    List<FunFundAccount> selectFunFundAccountList(FunFundAccount funFundAccount);

    /**
     * 查询资金统计信息列表
     */
    List<FunFundAccount> selectStatisticalFunFundAccountList(FunFundAccount funFundAccount);

    /**
     * 查询资金统计信息明细
     */
    List<FunFundAccount> selectStatisticalFunFundAccountDetail(FunFundAccount funFundAccount);

    /**
     * 新增资金账户信息
     */
    int insertFunFundAccount(FunFundAccount funFundAccount);

    /**
     * 修改资金账户信息
     */
    int updateFunFundAccount(FunFundAccount funFundAccount);

    /**
     * 更新余额
     */
    int updateAmount(FunFundAccount funFundAccount);

    /**
     * 变更资金账户信息
     */
    int changeFunFundAccount(FunFundAccount funFundAccount);

    /**
     * 批量删除资金账户信息
     */
    int deleteFunFundAccountByIds(List<Long> fundAccountSids);

    /**
     * 更改确认状态
     */
    int check(FunFundAccount funFundAccount);

    /**
     * 更改作废状态前的校验
     */
    void checkInvalid(FunFundAccount funFundAccount);

    /**
     * 更改作废状态
     */
    int invalid(FunFundAccount funFundAccount);

    /**
     * 更新账户金额
     */
    int updateCurrencyAmount(FunFundAccount funFundAccount);

    /**
     * 下拉框接口
     */
    List<FunFundAccount> getList(FunFundAccount funFundAccount);

    Object importData(MultipartFile file);
}
