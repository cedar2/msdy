package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeVendorDeduction;

/**
 * 业务类型_供应商扣款单Service接口
 *
 * @author chenkw
 * @date 2021-08-03
 */
public interface IConBuTypeVendorDeductionService extends IService<ConBuTypeVendorDeduction> {
    /**
     * 查询业务类型_供应商扣款单
     *
     * @param sid 业务类型_供应商扣款单ID
     * @return 业务类型_供应商扣款单
     */
    public ConBuTypeVendorDeduction selectConBuTypeVendorDeductionById(Long sid);

    /**
     * 查询业务类型_供应商扣款单列表
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 业务类型_供应商扣款单集合
     */
    public List<ConBuTypeVendorDeduction> selectConBuTypeVendorDeductionList(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 新增业务类型_供应商扣款单
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 结果
     */
    public int insertConBuTypeVendorDeduction(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 修改业务类型_供应商扣款单
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 结果
     */
    public int updateConBuTypeVendorDeduction(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 变更业务类型_供应商扣款单
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 结果
     */
    public int changeConBuTypeVendorDeduction(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 批量删除业务类型_供应商扣款单
     *
     * @param sids 需要删除的业务类型_供应商扣款单ID
     * @return 结果
     */
    public int deleteConBuTypeVendorDeductionByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorDeduction
     * @return
     */
    int changeStatus(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorDeduction
     * @return
     */
    int check(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 下拉框列表
     */
    List<ConBuTypeVendorDeduction> getConBuTypeVendorDeductionList();
}
