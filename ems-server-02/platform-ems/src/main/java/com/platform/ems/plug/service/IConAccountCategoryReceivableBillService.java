package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAccountCategoryReceivableBill;

/**
 * 款项类别_收款单Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConAccountCategoryReceivableBillService extends IService<ConAccountCategoryReceivableBill>{
    /**
     * 查询款项类别_收款单
     * 
     * @param sid 款项类别_收款单ID
     * @return 款项类别_收款单
     */
    public ConAccountCategoryReceivableBill selectConAccountCategoryReceivableBillById(Long sid);

    /**
     * 查询款项类别_收款单列表
     * 
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 款项类别_收款单集合
     */
    public List<ConAccountCategoryReceivableBill> selectConAccountCategoryReceivableBillList(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

    /**
     * 新增款项类别_收款单
     * 
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 结果
     */
    public int insertConAccountCategoryReceivableBill(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

    /**
     * 修改款项类别_收款单
     * 
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 结果
     */
    public int updateConAccountCategoryReceivableBill(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

    /**
     * 变更款项类别_收款单
     *
     * @param conAccountCategoryReceivableBill 款项类别_收款单
     * @return 结果
     */
    public int changeConAccountCategoryReceivableBill(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

    /**
     * 批量删除款项类别_收款单
     * 
     * @param sids 需要删除的款项类别_收款单ID
     * @return 结果
     */
    public int deleteConAccountCategoryReceivableBillByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conAccountCategoryReceivableBill
    * @return
    */
    int changeStatus(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

    /**
     * 更改确认状态
     * @param conAccountCategoryReceivableBill
     * @return
     */
    int check(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

}
