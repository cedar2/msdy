package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterAddr;

/**
 * 供应商注册-联系方式信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterAddrMapper  extends BaseMapper<BasVendorRegisterAddr> {


    BasVendorRegisterAddr selectBasVendorRegisterAddrById(Long vendorRegisterContactSid);

    List<BasVendorRegisterAddr> selectBasVendorRegisterAddrList(BasVendorRegisterAddr basVendorRegisterAddr);

    /**
     * 添加多个
     * @param list List BasVendorRegisterAddr
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterAddr> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterAddr
    * @return int
    */
    int updateAllById(BasVendorRegisterAddr entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterAddr
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterAddr> list);


}
