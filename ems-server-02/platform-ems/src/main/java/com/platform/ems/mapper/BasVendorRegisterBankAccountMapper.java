package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterBankAccount;

/**
 * 供应商注册-银行账户信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterBankAccountMapper  extends BaseMapper<BasVendorRegisterBankAccount> {


    BasVendorRegisterBankAccount selectBasVendorRegisterBankAccountById(Long vendorRegisterBankAccountSid);

    List<BasVendorRegisterBankAccount> selectBasVendorRegisterBankAccountList(BasVendorRegisterBankAccount basVendorRegisterBankAccount);

    /**
     * 添加多个
     * @param list List BasVendorRegisterBankAccount
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterBankAccount> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterBankAccount
    * @return int
    */
    int updateAllById(BasVendorRegisterBankAccount entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterBankAccount
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterBankAccount> list);


}
