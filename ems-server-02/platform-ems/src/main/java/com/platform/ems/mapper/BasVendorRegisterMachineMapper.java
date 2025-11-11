package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterMachine;

/**
 * 供应商注册-设备信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterMachineMapper  extends BaseMapper<BasVendorRegisterMachine> {


    BasVendorRegisterMachine selectBasVendorRegisterMachineById(Long vendorRegisterMachineSid);

    List<BasVendorRegisterMachine> selectBasVendorRegisterMachineList(BasVendorRegisterMachine basVendorRegisterMachine);

    /**
     * 添加多个
     * @param list List BasVendorRegisterMachine
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterMachine> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterMachine
    * @return int
    */
    int updateAllById(BasVendorRegisterMachine entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterMachine
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterMachine> list);


}
