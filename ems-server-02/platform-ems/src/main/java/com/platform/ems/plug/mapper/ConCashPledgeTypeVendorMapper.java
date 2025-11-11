package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConCashPledgeTypeVendor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 押金类型_供应商Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConCashPledgeTypeVendorMapper extends BaseMapper<ConCashPledgeTypeVendor> {


    ConCashPledgeTypeVendor selectConCashPledgeTypeVendorById(Long sid);

    List<ConCashPledgeTypeVendor> selectConCashPledgeTypeVendorList(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 添加多个
     *
     * @param list List ConCashPledgeTypeVendor
     * @return int
     */
    int inserts(@Param("list") List<ConCashPledgeTypeVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConCashPledgeTypeVendor
     * @return int
     */
    int updateAllById(ConCashPledgeTypeVendor entity);

    /**
     * 更新多个
     *
     * @param list List ConCashPledgeTypeVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<ConCashPledgeTypeVendor> list);

    /**
     * 押金类型_供应商下拉框列表
     */
    List<ConCashPledgeTypeVendor> getList(ConCashPledgeTypeVendor conCashPledgeTypeVendor);
}
