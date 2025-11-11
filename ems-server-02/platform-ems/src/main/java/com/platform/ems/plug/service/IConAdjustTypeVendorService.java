package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAdjustTypeCustomer;
import com.platform.ems.plug.domain.ConAdjustTypeVendor;

/**
 * 调账类型_供应商Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConAdjustTypeVendorService extends IService<ConAdjustTypeVendor>{
    /**
     * 查询调账类型_供应商
     *
     * @param sid 调账类型_供应商ID
     * @return 调账类型_供应商
     */
    public ConAdjustTypeVendor selectConAdjustTypeVendorById(Long sid);

    /**
     * 查询调账类型_供应商列表
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 调账类型_供应商集合
     */
    public List<ConAdjustTypeVendor> selectConAdjustTypeVendorList(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 新增调账类型_供应商
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 结果
     */
    public int insertConAdjustTypeVendor(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 修改调账类型_供应商
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 结果
     */
    public int updateConAdjustTypeVendor(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 变更调账类型_供应商
     *
     * @param conAdjustTypeVendor 调账类型_供应商
     * @return 结果
     */
    public int changeConAdjustTypeVendor(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 批量删除调账类型_供应商
     *
     * @param sids 需要删除的调账类型_供应商ID
     * @return 结果
     */
    public int deleteConAdjustTypeVendorByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conAdjustTypeVendor
    * @return
    */
    int changeStatus(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 更改确认状态
     * @param conAdjustTypeVendor
     * @return
     */
    int check(ConAdjustTypeVendor conAdjustTypeVendor);

    /**
     * 款项类别下拉框列表
     */
    List<ConAdjustTypeVendor> getConAdjustTypeVendorList();
}
