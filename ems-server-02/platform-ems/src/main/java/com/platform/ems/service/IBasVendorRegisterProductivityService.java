package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterProductivity;

/**
 * 供应商注册-产能信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterProductivityService extends IService<BasVendorRegisterProductivity> {
    /**
     * 查询供应商注册-产能信息
     *
     * @param vendorRegisterProductivitySid 供应商注册-产能信息ID
     * @return 供应商注册-产能信息
     */
    public BasVendorRegisterProductivity selectBasVendorRegisterProductivityById(Long vendorRegisterProductivitySid);

    /**
     * 查询供应商注册-产能信息列表
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 供应商注册-产能信息集合
     */
    public List<BasVendorRegisterProductivity> selectBasVendorRegisterProductivityList(BasVendorRegisterProductivity basVendorRegisterProductivity);

    /**
     * 新增供应商注册-产能信息
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    public int insertBasVendorRegisterProductivity(BasVendorRegisterProductivity basVendorRegisterProductivity);

    /**
     * 修改供应商注册-产能信息
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    public int updateBasVendorRegisterProductivity(BasVendorRegisterProductivity basVendorRegisterProductivity);

    /**
     * 变更供应商注册-产能信息
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    public int changeBasVendorRegisterProductivity(BasVendorRegisterProductivity basVendorRegisterProductivity);

    /**
     * 批量删除供应商注册-产能信息
     *
     * @param vendorRegisterProductivitySids 需要删除的供应商注册-产能信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterProductivityByIds(List<Long> vendorRegisterProductivitySids);

    /**
     * 由主表查询供应商注册-产能信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-产能信息集合
     */
    public List<BasVendorRegisterProductivity> selectBasVendorRegisterProductivityListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-产能信息
     *
     * @param basVendorRegisterProductivityList List 供应商注册-产能信息
     * @return 结果
     */
    public int insertBasVendorRegisterProductivity(List<BasVendorRegisterProductivity> basVendorRegisterProductivityList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-产能信息
     *
     * @param basVendorRegisterProductivityList List 供应商注册-产能信息
     * @return 结果
     */
    public int updateBasVendorRegisterProductivity(List<BasVendorRegisterProductivity> basVendorRegisterProductivityList);

    /**
     * 由主表批量修改供应商注册-产能信息
     *
     * @param response List 供应商注册-产能信息 (原来的)
     * @param request  List 供应商注册-产能信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterProductivity(List<BasVendorRegisterProductivity> response, List<BasVendorRegisterProductivity> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-产能信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterProductivityListByIds(List<Long> vendorRegisterSids);

}
