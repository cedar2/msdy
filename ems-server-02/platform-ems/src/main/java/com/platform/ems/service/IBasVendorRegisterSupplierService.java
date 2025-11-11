package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterSupplier;

/**
 * 供应商注册-主要供应商信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterSupplierService extends IService<BasVendorRegisterSupplier> {
    /**
     * 查询供应商注册-主要供应商信息
     *
     * @param vendorRegisterSupplierSid 供应商注册-主要供应商信息ID
     * @return 供应商注册-主要供应商信息
     */
    public BasVendorRegisterSupplier selectBasVendorRegisterSupplierById(Long vendorRegisterSupplierSid);

    /**
     * 查询供应商注册-主要供应商信息列表
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 供应商注册-主要供应商信息集合
     */
    public List<BasVendorRegisterSupplier> selectBasVendorRegisterSupplierList(BasVendorRegisterSupplier basVendorRegisterSupplier);

    /**
     * 新增供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    public int insertBasVendorRegisterSupplier(BasVendorRegisterSupplier basVendorRegisterSupplier);

    /**
     * 修改供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    public int updateBasVendorRegisterSupplier(BasVendorRegisterSupplier basVendorRegisterSupplier);

    /**
     * 变更供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    public int changeBasVendorRegisterSupplier(BasVendorRegisterSupplier basVendorRegisterSupplier);

    /**
     * 批量删除供应商注册-主要供应商信息
     *
     * @param vendorRegisterSupplierSids 需要删除的供应商注册-主要供应商信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterSupplierByIds(List<Long> vendorRegisterSupplierSids);

    /**
     * 由主表查询供应商注册-主要供应商信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-主要供应商信息集合
     */
    public List<BasVendorRegisterSupplier> selectBasVendorRegisterSupplierListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplierList List 供应商注册-主要供应商信息
     * @return 结果
     */
    public int insertBasVendorRegisterSupplier(List<BasVendorRegisterSupplier> basVendorRegisterSupplierList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplierList List 供应商注册-主要供应商信息
     * @return 结果
     */
    public int updateBasVendorRegisterSupplier(List<BasVendorRegisterSupplier> basVendorRegisterSupplierList);

    /**
     * 由主表批量修改供应商注册-主要供应商信息
     *
     * @param response List 供应商注册-主要供应商信息 (原来的)
     * @param request  List 供应商注册-主要供应商信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterSupplier(List<BasVendorRegisterSupplier> response, List<BasVendorRegisterSupplier> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-主要供应商信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterSupplierListByIds(List<Long> vendorRegisterSids);
}
