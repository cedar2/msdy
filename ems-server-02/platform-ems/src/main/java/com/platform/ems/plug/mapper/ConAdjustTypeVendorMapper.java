package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConAdjustTypeCustomer;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConAdjustTypeVendor;

/**
 * 调账类型_供应商Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConAdjustTypeVendorMapper  extends BaseMapper<ConAdjustTypeVendor> {


    ConAdjustTypeVendor selectConAdjustTypeVendorById(Long sid);

    List<ConAdjustTypeVendor> selectConAdjustTypeVendorList(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 添加多个
     * @param list List ConAdjustTypeVendor
     * @return int
     */
    int inserts(@Param("list") List<ConAdjustTypeVendor> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConAdjustTypeVendor
    * @return int
    */
    int updateAllById(ConAdjustTypeVendor entity);

    /**
     * 更新多个
     * @param list List ConAdjustTypeVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<ConAdjustTypeVendor> list);

    /**
     * 款项类别下拉框列表
     */
    List<ConAdjustTypeVendor> getConAdjustTypeVendorList();

}
