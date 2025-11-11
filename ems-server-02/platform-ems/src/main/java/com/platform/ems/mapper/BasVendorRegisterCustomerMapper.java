package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterCustomer;

/**
 * 供应商注册-主要客户信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterCustomerMapper  extends BaseMapper<BasVendorRegisterCustomer> {


    BasVendorRegisterCustomer selectBasVendorRegisterCustomerById(Long vendorRegisterCustomerSid);

    List<BasVendorRegisterCustomer> selectBasVendorRegisterCustomerList(BasVendorRegisterCustomer basVendorRegisterCustomer);

    /**
     * 添加多个
     * @param list List BasVendorRegisterCustomer
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterCustomer> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterCustomer
    * @return int
    */
    int updateAllById(BasVendorRegisterCustomer entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterCustomer> list);


}
