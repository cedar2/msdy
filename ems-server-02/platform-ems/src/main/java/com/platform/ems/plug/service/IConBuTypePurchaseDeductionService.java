package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePurchaseDeduction;

/**
 * 业务类型_采购扣款单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypePurchaseDeductionService extends IService<ConBuTypePurchaseDeduction>{
    /**
     * 查询业务类型_采购扣款单
     * 
     * @param sid 业务类型_采购扣款单ID
     * @return 业务类型_采购扣款单
     */
    public ConBuTypePurchaseDeduction selectConBuTypePurchaseDeductionById(Long sid);

    /**
     * 查询业务类型_采购扣款单列表
     * 
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 业务类型_采购扣款单集合
     */
    public List<ConBuTypePurchaseDeduction> selectConBuTypePurchaseDeductionList(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

    /**
     * 新增业务类型_采购扣款单
     * 
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 结果
     */
    public int insertConBuTypePurchaseDeduction(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

    /**
     * 修改业务类型_采购扣款单
     * 
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 结果
     */
    public int updateConBuTypePurchaseDeduction(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

    /**
     * 变更业务类型_采购扣款单
     *
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 结果
     */
    public int changeConBuTypePurchaseDeduction(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

    /**
     * 批量删除业务类型_采购扣款单
     * 
     * @param sids 需要删除的业务类型_采购扣款单ID
     * @return 结果
     */
    public int deleteConBuTypePurchaseDeductionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypePurchaseDeduction
    * @return
    */
    int changeStatus(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

    /**
     * 更改确认状态
     * @param conBuTypePurchaseDeduction
     * @return
     */
    int check(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

}
