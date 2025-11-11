package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPurchaseOrg;
import com.platform.ems.plug.domain.ConSaleOrg;

/**
 * 销售组织Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConSaleOrgService extends IService<ConSaleOrg>{
    /**
     * 查询销售组织
     *
     * @param sid 销售组织ID
     * @return 销售组织
     */
    public ConSaleOrg selectConSaleOrgById(Long sid);

    /**
     * 查询销售组织列表
     *
     * @param conSaleOrg 销售组织
     * @return 销售组织集合
     */
    public List<ConSaleOrg> selectConSaleOrgList(ConSaleOrg conSaleOrg);

    /**
     * 新增销售组织
     *
     * @param conSaleOrg 销售组织
     * @return 结果
     */
    public int insertConSaleOrg(ConSaleOrg conSaleOrg);

    /**
     * 修改销售组织
     *
     * @param conSaleOrg 销售组织
     * @return 结果
     */
    public int updateConSaleOrg(ConSaleOrg conSaleOrg);

    /**
     * 变更销售组织
     *
     * @param conSaleOrg 销售组织
     * @return 结果
     */
    public int changeConSaleOrg(ConSaleOrg conSaleOrg);

    /**
     * 批量删除销售组织
     *
     * @param sids 需要删除的销售组织ID
     * @return 结果
     */
    public int deleteConSaleOrgByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conSaleOrg
    * @return
    */
    int changeStatus(ConSaleOrg conSaleOrg);

    /**
     * 更改确认状态
     * @param conSaleOrg
     * @return
     */
    int check(ConSaleOrg conSaleOrg);

    /**  获取下拉列表 */
    List<ConSaleOrg> getConSaleOrgList();
}
