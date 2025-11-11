package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterMachine;

/**
 * 供应商注册-设备信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterMachineService extends IService<BasVendorRegisterMachine> {
    /**
     * 查询供应商注册-设备信息
     *
     * @param vendorRegisterMachineSid 供应商注册-设备信息ID
     * @return 供应商注册-设备信息
     */
    public BasVendorRegisterMachine selectBasVendorRegisterMachineById(Long vendorRegisterMachineSid);

    /**
     * 查询供应商注册-设备信息列表
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 供应商注册-设备信息集合
     */
    public List<BasVendorRegisterMachine> selectBasVendorRegisterMachineList(BasVendorRegisterMachine basVendorRegisterMachine);

    /**
     * 新增供应商注册-设备信息
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    public int insertBasVendorRegisterMachine(BasVendorRegisterMachine basVendorRegisterMachine);

    /**
     * 修改供应商注册-设备信息
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    public int updateBasVendorRegisterMachine(BasVendorRegisterMachine basVendorRegisterMachine);

    /**
     * 变更供应商注册-设备信息
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    public int changeBasVendorRegisterMachine(BasVendorRegisterMachine basVendorRegisterMachine);

    /**
     * 批量删除供应商注册-设备信息
     *
     * @param vendorRegisterMachineSids 需要删除的供应商注册-设备信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterMachineByIds(List<Long> vendorRegisterMachineSids);

    /**
     * 由主表查询供应商注册-设备信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-设备信息集合
     */
    public List<BasVendorRegisterMachine> selectBasVendorRegisterMachineListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-设备信息
     *
     * @param basVendorRegisterMachineList List 供应商注册-设备信息
     * @return 结果
     */
    public int insertBasVendorRegisterMachine(List<BasVendorRegisterMachine> basVendorRegisterMachineList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-设备信息
     *
     * @param basVendorRegisterMachineList List 供应商注册-设备信息
     * @return 结果
     */
    public int updateBasVendorRegisterMachine(List<BasVendorRegisterMachine> basVendorRegisterMachineList);

    /**
     * 由主表批量修改供应商注册-设备信息
     *
     * @param response List 供应商注册-设备信息 (原来的)
     * @param request  List 供应商注册-设备信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterMachine(List<BasVendorRegisterMachine> response, List<BasVendorRegisterMachine> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-设备信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterMachineListByIds(List<Long> vendorRegisterSids);

}
