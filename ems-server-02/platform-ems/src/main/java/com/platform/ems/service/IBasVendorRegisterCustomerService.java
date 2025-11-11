package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterCustomer;

/**
 * 供应商注册-主要客户信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterCustomerService extends IService<BasVendorRegisterCustomer> {
    /**
     * 查询供应商注册-主要客户信息
     *
     * @param vendorRegisterCustomerSid 供应商注册-主要客户信息ID
     * @return 供应商注册-主要客户信息
     */
    public BasVendorRegisterCustomer selectBasVendorRegisterCustomerById(Long vendorRegisterCustomerSid);

    /**
     * 查询供应商注册-主要客户信息列表
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 供应商注册-主要客户信息集合
     */
    public List<BasVendorRegisterCustomer> selectBasVendorRegisterCustomerList(BasVendorRegisterCustomer basVendorRegisterCustomer);

    /**
     * 新增供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    public int insertBasVendorRegisterCustomer(BasVendorRegisterCustomer basVendorRegisterCustomer);

    /**
     * 修改供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    public int updateBasVendorRegisterCustomer(BasVendorRegisterCustomer basVendorRegisterCustomer);

    /**
     * 变更供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    public int changeBasVendorRegisterCustomer(BasVendorRegisterCustomer basVendorRegisterCustomer);

    /**
     * 批量删除供应商注册-主要客户信息
     *
     * @param vendorRegisterCustomerSids 需要删除的供应商注册-主要客户信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterCustomerByIds(List<Long> vendorRegisterCustomerSids);


    /**
     * 由主表查询供应商注册-主要客户信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-主要客户信息集合
     */
    public List<BasVendorRegisterCustomer> selectBasVendorRegisterCustomerListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomerList List 供应商注册-主要客户信息
     * @return 结果
     */
    public int insertBasVendorRegisterCustomer(List<BasVendorRegisterCustomer> basVendorRegisterCustomerList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomerList List 供应商注册-主要客户信息
     * @return 结果
     */
    public int updateBasVendorRegisterCustomer(List<BasVendorRegisterCustomer> basVendorRegisterCustomerList);

    /**
     * 由主表批量修改供应商注册-主要客户信息
     *
     * @param response List 供应商注册-主要客户信息 (原来的)
     * @param request  List 供应商注册-主要客户信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterCustomer(List<BasVendorRegisterCustomer> response, List<BasVendorRegisterCustomer> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-主要客户信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterCustomerListByIds(List<Long> vendorRegisterSids);
}
