package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import com.platform.ems.plug.domain.ConDeductionTypeVendor;

/**
 * 扣款类型_采购Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDeductionTypeVendorService extends IService<ConDeductionTypeVendor> {
    /**
     * 查询扣款类型_采购
     *
     * @param sid 扣款类型_采购ID
     * @return 扣款类型_采购
     */
    public ConDeductionTypeVendor selectConDeductionTypeVendorById(Long sid);

    /**
     * 查询扣款类型_采购列表
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 扣款类型_采购集合
     */
    public List<ConDeductionTypeVendor> selectConDeductionTypeVendorList(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 新增扣款类型_采购
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 结果
     */
    public int insertConDeductionTypeVendor(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 修改扣款类型_采购
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 结果
     */
    public int updateConDeductionTypeVendor(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 变更扣款类型_采购
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 结果
     */
    public int changeConDeductionTypeVendor(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 批量删除扣款类型_采购
     *
     * @param sids 需要删除的扣款类型_采购ID
     * @return 结果
     */
    public int deleteConDeductionTypeVendorByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDeductionTypeVendor
     * @return
     */
    int changeStatus(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 更改确认状态
     *
     * @param conDeductionTypeVendor
     * @return
     */
    int check(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 获取下拉列表
     */
    List<ConDeductionTypeVendor> getConDeductionTypeVendorList();
}
