package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasVendor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商档案Mapper接口
 *
 * @author qhq
 * @date 2021-03-12
 */
public interface BasVendorMapper  extends BaseMapper<BasVendor> {


    BasVendor selectBasVendorById(Long vendorSid);

    List<BasVendor> selectBasVendorList(BasVendor basVendor);

    /**
     * 添加多个
     * @param list List BasVendor
     * @return int
     */
    int inserts(@Param("list") List<BasVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasVendor
     * @return int
     */
    int updateAllById(BasVendor entity);

    /**
     * 更新多个
     * @param list List BasVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendor> list);

    /**
     * 查询供应商档案sid、名称及简称，用于下拉框
     */
    public List<BasVendor> getVendorList(BasVendor basVendor);
    
    int editStatus(BasVendor basVendor);
    
    int editHandleStatus(BasVendor basVendor);
    
    String getCompanyNameBySid(Long sid);
    
    String getCustomerNameBySid(Long sid);
    
    String getVendorNameBySid(Long sid);

}
