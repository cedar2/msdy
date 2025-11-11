package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.domain.ConPurchaseOrg;

/**
 * 采购组织Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConPurchaseOrgService extends IService<ConPurchaseOrg>{
    /**
     * 查询采购组织
     *
     * @param sid 采购组织ID
     * @return 采购组织
     */
    public ConPurchaseOrg selectConPurchaseOrgById(Long sid);

    /**
     * 查询采购组织列表
     *
     * @param conPurchaseOrg 采购组织
     * @return 采购组织集合
     */
    public List<ConPurchaseOrg> selectConPurchaseOrgList(ConPurchaseOrg conPurchaseOrg);

    /**
     * 新增采购组织
     *
     * @param conPurchaseOrg 采购组织
     * @return 结果
     */
    public int insertConPurchaseOrg(ConPurchaseOrg conPurchaseOrg);

    /**
     * 修改采购组织
     *
     * @param conPurchaseOrg 采购组织
     * @return 结果
     */
    public int updateConPurchaseOrg(ConPurchaseOrg conPurchaseOrg);

    /**
     * 变更采购组织
     *
     * @param conPurchaseOrg 采购组织
     * @return 结果
     */
    public int changeConPurchaseOrg(ConPurchaseOrg conPurchaseOrg);

    /**
     * 批量删除采购组织
     *
     * @param sids 需要删除的采购组织ID
     * @return 结果
     */
    public int deleteConPurchaseOrgByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conPurchaseOrg
    * @return
    */
    int changeStatus(ConPurchaseOrg conPurchaseOrg);

    /**
     * 更改确认状态
     * @param conPurchaseOrg
     * @return
     */
    int check(ConPurchaseOrg conPurchaseOrg);

    /**  获取下拉列表 */
    List<ConPurchaseOrg> getConPurchaseOrgList();

}
