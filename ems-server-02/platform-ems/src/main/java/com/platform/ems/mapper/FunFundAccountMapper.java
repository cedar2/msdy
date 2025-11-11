package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FunFundAccount;

/**
 * 资金账户信息Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface FunFundAccountMapper extends BaseMapper<FunFundAccount> {

    FunFundAccount selectFunFundAccountById(Long fundAccountSid);

    List<FunFundAccount> selectFunFundAccountList(FunFundAccount funFundAccount);

    List<FunFundAccount> selectStatisticalFunFundAccountList(FunFundAccount funFundAccount);

    List<FunFundAccount> selectStatisticalFunFundAccountDetail(FunFundAccount funFundAccount);

    /**
     * 添加多个
     *
     * @param list List FunFundAccount
     * @return int
     */
    int inserts(@Param("list") List<FunFundAccount> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FunFundAccount
     * @return int
     */
    int updateAllById(FunFundAccount entity);

    /**
     * 更新多个
     *
     * @param list List FunFundAccount
     * @return int
     */
    int updatesAllById(@Param("list") List<FunFundAccount> list);

    /**
     * 下拉框接口
     */
    List<FunFundAccount> getList(FunFundAccount funFundAccount);
}
