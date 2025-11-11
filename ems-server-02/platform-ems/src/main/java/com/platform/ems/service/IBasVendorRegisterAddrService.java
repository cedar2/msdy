package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterAddr;

/**
 * 供应商注册-联系方式信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterAddrService extends IService<BasVendorRegisterAddr> {
    /**
     * 查询供应商注册-联系方式信息
     *
     * @param vendorRegisterContactSid 供应商注册-联系方式信息ID
     * @return 供应商注册-联系方式信息
     */
    public BasVendorRegisterAddr selectBasVendorRegisterAddrById(Long vendorRegisterContactSid);

    /**
     * 查询供应商注册-联系方式信息列表
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 供应商注册-联系方式信息集合
     */
    public List<BasVendorRegisterAddr> selectBasVendorRegisterAddrList(BasVendorRegisterAddr basVendorRegisterAddr);

    /**
     * 新增供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    public int insertBasVendorRegisterAddr(BasVendorRegisterAddr basVendorRegisterAddr);

    /**
     * 修改供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    public int updateBasVendorRegisterAddr(BasVendorRegisterAddr basVendorRegisterAddr);

    /**
     * 变更供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    public int changeBasVendorRegisterAddr(BasVendorRegisterAddr basVendorRegisterAddr);

    /**
     * 批量删除供应商注册-联系方式信息
     *
     * @param vendorRegisterContactSids 需要删除的供应商注册-联系方式信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterAddrByIds(List<Long> vendorRegisterContactSids);

    /**
     * 由主表查询供应商注册-联系方式信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-联系方式信息集合
     */
    public List<BasVendorRegisterAddr> selectBasVendorRegisterAddrListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddrList List 供应商注册-联系方式信息
     * @return 结果
     */
    public int insertBasVendorRegisterAddr(List<BasVendorRegisterAddr> basVendorRegisterAddrList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddrList List 供应商注册-联系方式信息
     * @return 结果
     */
    public int updateBasVendorRegisterAddr(List<BasVendorRegisterAddr> basVendorRegisterAddrList);

    /**
     * 由主表批量修改供应商注册-联系方式信息
     *
     * @param response List 供应商注册-联系方式信息 (原来的)
     * @param request  List 供应商注册-联系方式信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterAddr(List<BasVendorRegisterAddr> response, List<BasVendorRegisterAddr> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-联系方式信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterAddrListByIds(List<Long> vendorRegisterSids);
}
