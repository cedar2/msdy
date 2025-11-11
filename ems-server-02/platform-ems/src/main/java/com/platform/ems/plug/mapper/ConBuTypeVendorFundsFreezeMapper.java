package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuTypeVendorFundsFreeze;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务类型_供应商暂押款Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-27
 */
public interface ConBuTypeVendorFundsFreezeMapper extends BaseMapper<ConBuTypeVendorFundsFreeze> {


    ConBuTypeVendorFundsFreeze selectConBuTypeVendorFundsFreezeById(Long sid);

    List<ConBuTypeVendorFundsFreeze> selectConBuTypeVendorFundsFreezeList(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeVendorFundsFreeze
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeVendorFundsFreeze> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeVendorFundsFreeze
     * @return int
     */
    int updateAllById(ConBuTypeVendorFundsFreeze entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeVendorFundsFreeze
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeVendorFundsFreeze> list);

    /**
     * 业务类型_供应商暂押款下拉框列表
     */
    List<ConBuTypeVendorFundsFreeze> getList(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);
}
