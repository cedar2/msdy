package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorMachine;

/**
 * 供应商的设备信息Mapper接口
 *
 * @author chenkw
 * @date 2022-01-06
 */
public interface BasVendorMachineMapper extends BaseMapper<BasVendorMachine> {


    BasVendorMachine selectBasVendorMachineById(Long vendorMachineSid);

    List<BasVendorMachine> selectBasVendorMachineList(BasVendorMachine basVendorMachine);

    /**
     * 添加多个
     *
     * @param list List BasVendorMachine
     * @return int
     */
    int inserts(@Param("list") List<BasVendorMachine> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorMachine
     * @return int
     */
    int updateAllById(BasVendorMachine entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorMachine
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorMachine> list);


}
