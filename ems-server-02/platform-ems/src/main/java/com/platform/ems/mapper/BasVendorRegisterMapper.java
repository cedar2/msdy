package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegister;

/**
 * 供应商注册-基础Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterMapper  extends BaseMapper<BasVendorRegister> {


    BasVendorRegister selectBasVendorRegisterById(Long vendorRegisterSid);

    List<BasVendorRegister> selectBasVendorRegisterList(BasVendorRegister basVendorRegister);

    /**
     * 添加多个
     * @param list List BasVendorRegister
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegister> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegister
    * @return int
    */
    int updateAllById(BasVendorRegister entity);

    /**
     * 更新多个
     * @param list List BasVendorRegister
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegister> list);


}
