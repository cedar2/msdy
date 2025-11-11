package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterSupplier;

/**
 * 供应商注册-主要供应商信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterSupplierMapper  extends BaseMapper<BasVendorRegisterSupplier> {


    BasVendorRegisterSupplier selectBasVendorRegisterSupplierById(Long vendorRegisterSupplierSid);

    List<BasVendorRegisterSupplier> selectBasVendorRegisterSupplierList(BasVendorRegisterSupplier basVendorRegisterSupplier);

    /**
     * 添加多个
     * @param list List BasVendorRegisterSupplier
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterSupplier> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterSupplier
    * @return int
    */
    int updateAllById(BasVendorRegisterSupplier entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterSupplier
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterSupplier> list);


}
