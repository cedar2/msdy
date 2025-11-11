package com.platform.ems.service;

import java.util.List;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasVendorAddr;
import com.platform.ems.domain.dto.request.BasVendorAddrAddRequest;
import com.platform.ems.domain.dto.request.BasVendorAddrDeleteRequest;
import com.platform.ems.domain.dto.request.BasVendorAddrUpdateRequest;

/**
 * 供应商-联系方式信息Service接口
 *
 * @author linhongwei
 * @date 2021-01-31
 */
public interface IBasVendorAddrService {
    /**
     * 查询供应商-联系方式信息
     *
     * @param clientId 供应商-联系方式信息ID
     * @return 供应商-联系方式信息
     */
    public BasVendorAddr selectBasVendorAddrById(String clientId);

    /**
     * 查询供应商-联系方式信息列表
     *
     * @param basVendorAddr 供应商-联系方式信息
     * @return 供应商-联系方式信息集合
     */
    public List<BasVendorAddr> selectBasVendorAddrList(BasVendorAddr basVendorAddr);

    /**
     * 新增供应商-联系方式信息
     *
     * @param basVendorAddr 供应商-联系方式信息
     * @return 结果
     */
    public AjaxResult insertBasVendorAddr(BasVendorAddrAddRequest basVendorAddr);

    /**
     * 修改供应商-联系方式信息
     *
     * @param basVendorAddr 供应商-联系方式信息
     * @return 结果
     */
    public int updateBasVendorAddr(BasVendorAddrUpdateRequest basVendorAddr);

    /**
     * 批量删除供应商-联系方式信息
     *
     * @param clientIds 需要删除的供应商-联系方式信息ID
     * @return 结果
     */
    public int deleteBasVendorAddrByIds(BasVendorAddrDeleteRequest clientIds);

    /**
     * 删除供应商-联系方式信息信息
     *
     * @param clientId 供应商-联系方式信息ID
     * @return 结果
     */
    public int deleteBasVendorAddrById(String clientId);
}
