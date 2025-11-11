package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRecommendAddr;

/**
 * 供应商推荐-联系方式信息Service接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRecommendAddrService extends IService<BasVendorRecommendAddr>{
    /**
     * 查询供应商推荐-联系方式信息
     * 
     * @param vendorRecommendContactSid 供应商推荐-联系方式信息ID
     * @return 供应商推荐-联系方式信息
     */
    public BasVendorRecommendAddr selectBasVendorRecommendAddrById(Long vendorRecommendContactSid);

    /**
     * 查询供应商推荐-联系方式信息列表
     * 
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 供应商推荐-联系方式信息集合
     */
    public List<BasVendorRecommendAddr> selectBasVendorRecommendAddrList(BasVendorRecommendAddr basVendorRecommendAddr);

    /**
     * 新增供应商推荐-联系方式信息
     * 
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    public int insertBasVendorRecommendAddr(BasVendorRecommendAddr basVendorRecommendAddr);

    /**
     * 修改供应商推荐-联系方式信息
     * 
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    public int updateBasVendorRecommendAddr(BasVendorRecommendAddr basVendorRecommendAddr);

    /**
     * 变更供应商推荐-联系方式信息
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    public int changeBasVendorRecommendAddr(BasVendorRecommendAddr basVendorRecommendAddr);

    /**
     * 批量删除供应商推荐-联系方式信息
     * 
     * @param vendorRecommendContactSids 需要删除的供应商推荐-联系方式信息ID
     * @return 结果
     */
    public int deleteBasVendorRecommendAddrByIds(List<Long>  vendorRecommendContactSids);

    /**
     * 查询主表下的联系方式信息
     *
     * @param vendorRecommendSid 供应商推荐ID
     * @return 供应商推荐-联系方式信息
     */
    public List<BasVendorRecommendAddr> selectBasVendorRecommendAddrListById(Long vendorRecommendSid);

    /**
     * 由主表批量新增供应商推荐-联系方式信息
     *
     * @param basVendorRecommendAddrList List 供应商推荐-联系方式信息
     * @return 结果
     */
    public int insertBasVendorRecommendAddr(List<BasVendorRecommendAddr> basVendorRecommendAddrList, Long vendorRecommendSid);

    /**
     * 批量更新供应商推荐-联系方式信息
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    public int updateBasVendorRecommendAddr(List<BasVendorRecommendAddr> basVendorRecommendAddr);

    /**
     * 由主表批量更新供应商推荐-联系方式信息
     *
     * @param list 供应商推荐-联系方式信息  （原先的）
     * @param request 供应商推荐-联系方式信息   （更新后的）
     * @return 结果
     */
    public int updateBasVendorRecommendAddr(List<BasVendorRecommendAddr> list, List<BasVendorRecommendAddr> request,Long vendorRecommendSid);

}
