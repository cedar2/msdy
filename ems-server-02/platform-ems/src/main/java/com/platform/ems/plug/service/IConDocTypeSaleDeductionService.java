package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeSaleDeduction;

/**
 * 单据类型_销售扣款单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeSaleDeductionService extends IService<ConDocTypeSaleDeduction>{
    /**
     * 查询单据类型_销售扣款单
     * 
     * @param sid 单据类型_销售扣款单ID
     * @return 单据类型_销售扣款单
     */
    public ConDocTypeSaleDeduction selectConDocTypeSaleDeductionById(Long sid);

    /**
     * 查询单据类型_销售扣款单列表
     * 
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 单据类型_销售扣款单集合
     */
    public List<ConDocTypeSaleDeduction> selectConDocTypeSaleDeductionList(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

    /**
     * 新增单据类型_销售扣款单
     * 
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 结果
     */
    public int insertConDocTypeSaleDeduction(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

    /**
     * 修改单据类型_销售扣款单
     * 
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 结果
     */
    public int updateConDocTypeSaleDeduction(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

    /**
     * 变更单据类型_销售扣款单
     *
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 结果
     */
    public int changeConDocTypeSaleDeduction(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

    /**
     * 批量删除单据类型_销售扣款单
     * 
     * @param sids 需要删除的单据类型_销售扣款单ID
     * @return 结果
     */
    public int deleteConDocTypeSaleDeductionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeSaleDeduction
    * @return
    */
    int changeStatus(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

    /**
     * 更改确认状态
     * @param conDocTypeSaleDeduction
     * @return
     */
    int check(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

}
