package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeSaleDeduction;

/**
 * 业务类型_销售扣款单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeSaleDeductionService extends IService<ConBuTypeSaleDeduction>{
    /**
     * 查询业务类型_销售扣款单
     * 
     * @param sid 业务类型_销售扣款单ID
     * @return 业务类型_销售扣款单
     */
    public ConBuTypeSaleDeduction selectConBuTypeSaleDeductionById(Long sid);

    /**
     * 查询业务类型_销售扣款单列表
     * 
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 业务类型_销售扣款单集合
     */
    public List<ConBuTypeSaleDeduction> selectConBuTypeSaleDeductionList(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

    /**
     * 新增业务类型_销售扣款单
     * 
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 结果
     */
    public int insertConBuTypeSaleDeduction(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

    /**
     * 修改业务类型_销售扣款单
     * 
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 结果
     */
    public int updateConBuTypeSaleDeduction(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

    /**
     * 变更业务类型_销售扣款单
     *
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 结果
     */
    public int changeConBuTypeSaleDeduction(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

    /**
     * 批量删除业务类型_销售扣款单
     * 
     * @param sids 需要删除的业务类型_销售扣款单ID
     * @return 结果
     */
    public int deleteConBuTypeSaleDeductionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeSaleDeduction
    * @return
    */
    int changeStatus(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

    /**
     * 更改确认状态
     * @param conBuTypeSaleDeduction
     * @return
     */
    int check(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

}
