package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasCompanyBankAccount;
import com.platform.ems.domain.BasCompanyBankAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公司银行账户信息Mapper接口
 *
 * @author qhq
 * @date 2021-03-12
 */
public interface BasCompanyBankAccountMapper extends BaseMapper<BasCompanyBankAccount> {


    BasCompanyBankAccount selectBasCompanyBankAccountById(String clientId);

    List<BasCompanyBankAccount> selectBasCompanyBankAccountList(BasCompanyBankAccount basCompanyBankAccount);

    /**
     * 添加多个
     * @param list List BasCompanyBankAccount
     * @return int
     */
    int inserts(@Param("list") List<BasCompanyBankAccount> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasCompanyBankAccount
     * @return int
     */
    int updateAllById(BasCompanyBankAccount entity);

    /**
     * 更新多个
     * @param list List BasCompanyBankAccount
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCompanyBankAccount> list);

    int deleteBankAccountByCompanySid(Long CompanySid);


}
