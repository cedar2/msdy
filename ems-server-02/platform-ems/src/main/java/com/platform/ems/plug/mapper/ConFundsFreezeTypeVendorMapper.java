package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConFundsFreezeTypeVendor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 暂押款类型_供应商Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConFundsFreezeTypeVendorMapper extends BaseMapper<ConFundsFreezeTypeVendor> {


    ConFundsFreezeTypeVendor selectConFundsFreezeTypeVendorById(Long sid);

    List<ConFundsFreezeTypeVendor> selectConFundsFreezeTypeVendorList(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 添加多个
     *
     * @param list List ConFundsFreezeTypeVendor
     * @return int
     */
    int inserts(@Param("list") List<ConFundsFreezeTypeVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConFundsFreezeTypeVendor
     * @return int
     */
    int updateAllById(ConFundsFreezeTypeVendor entity);

    /**
     * 更新多个
     *
     * @param list List ConFundsFreezeTypeVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<ConFundsFreezeTypeVendor> list);

    /**
     * 暂押款类型_供应商下拉框列表
     */
    List<ConFundsFreezeTypeVendor> getList(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);
}
