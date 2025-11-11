package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeVendorFundsFreeze;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据类型_供应商暂押款Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConDocTypeVendorFundsFreezeMapper extends BaseMapper<ConDocTypeVendorFundsFreeze> {


    ConDocTypeVendorFundsFreeze selectConDocTypeVendorFundsFreezeById(Long sid);

    List<ConDocTypeVendorFundsFreeze> selectConDocTypeVendorFundsFreezeList(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 添加多个
     *
     * @param list List ConDocTypeVendorFundsFreeze
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeVendorFundsFreeze> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocTypeVendorFundsFreeze
     * @return int
     */
    int updateAllById(ConDocTypeVendorFundsFreeze entity);

    /**
     * 更新多个
     *
     * @param list List ConDocTypeVendorFundsFreeze
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeVendorFundsFreeze> list);

    /**
     * 单据类型_供应商暂押款下拉框列表
     */
    List<ConDocTypeVendorFundsFreeze> getList(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);
}
