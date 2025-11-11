package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypePurchaseDeduction;

/**
 * 单据类型_采购扣款单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypePurchaseDeductionService extends IService<ConDocTypePurchaseDeduction>{
    /**
     * 查询单据类型_采购扣款单
     * 
     * @param sid 单据类型_采购扣款单ID
     * @return 单据类型_采购扣款单
     */
    public ConDocTypePurchaseDeduction selectConDocTypePurchaseDeductionById(Long sid);

    /**
     * 查询单据类型_采购扣款单列表
     * 
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 单据类型_采购扣款单集合
     */
    public List<ConDocTypePurchaseDeduction> selectConDocTypePurchaseDeductionList(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

    /**
     * 新增单据类型_采购扣款单
     * 
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 结果
     */
    public int insertConDocTypePurchaseDeduction(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

    /**
     * 修改单据类型_采购扣款单
     * 
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 结果
     */
    public int updateConDocTypePurchaseDeduction(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

    /**
     * 变更单据类型_采购扣款单
     *
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 结果
     */
    public int changeConDocTypePurchaseDeduction(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

    /**
     * 批量删除单据类型_采购扣款单
     * 
     * @param sids 需要删除的单据类型_采购扣款单ID
     * @return 结果
     */
    public int deleteConDocTypePurchaseDeductionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypePurchaseDeduction
    * @return
    */
    int changeStatus(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

    /**
     * 更改确认状态
     * @param conDocTypePurchaseDeduction
     * @return
     */
    int check(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

}
