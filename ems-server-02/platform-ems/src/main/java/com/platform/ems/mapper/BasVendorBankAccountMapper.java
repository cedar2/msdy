package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasVendorBankAccount;

/**
 * 供应商银行账户信息Mapper接口
 *
 * @author qhq
 * @date 2021-03-12
 */
public interface BasVendorBankAccountMapper  extends BaseMapper<BasVendorBankAccount> {


    BasVendorBankAccount selectBasVendorBankAccountById(String clientId);

    List<BasVendorBankAccount> selectBasVendorBankAccountList(BasVendorBankAccount basVendorBankAccount);

    /**
     * 添加多个
     * @param list List BasVendorBankAccount
     * @return int
     */
    int inserts(@Param("list") List<BasVendorBankAccount> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasVendorBankAccount
     * @return int
     */
    int updateAllById(BasVendorBankAccount entity);

    /**
     * 更新多个
     * @param list List BasVendorBankAccount
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorBankAccount> list);

    int deleteBankAccountByVendorSid(Long vendorSid);


}
