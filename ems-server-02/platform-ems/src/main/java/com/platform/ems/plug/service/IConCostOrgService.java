package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConCostOrg;

/**
 * 成本组织Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConCostOrgService extends IService<ConCostOrg>{
    /**
     * 查询成本组织
     * 
     * @param sid 成本组织ID
     * @return 成本组织
     */
    public ConCostOrg selectConCostOrgById(Long sid);

    /**
     * 查询成本组织列表
     * 
     * @param conCostOrg 成本组织
     * @return 成本组织集合
     */
    public List<ConCostOrg> selectConCostOrgList(ConCostOrg conCostOrg);

    /**
     * 查询成本组织列表-下拉框
     *
     * @param conCostOrg 成本组织
     * @return 成本组织集合
     */
    public List<ConCostOrg> getCostOrgList(ConCostOrg conCostOrg);

    /**
     * 新增成本组织
     * 
     * @param conCostOrg 成本组织
     * @return 结果
     */
    public int insertConCostOrg(ConCostOrg conCostOrg);

    /**
     * 修改成本组织
     * 
     * @param conCostOrg 成本组织
     * @return 结果
     */
    public int updateConCostOrg(ConCostOrg conCostOrg);

    /**
     * 变更成本组织
     *
     * @param conCostOrg 成本组织
     * @return 结果
     */
    public int changeConCostOrg(ConCostOrg conCostOrg);

    /**
     * 批量删除成本组织
     * 
     * @param sids 需要删除的成本组织ID
     * @return 结果
     */
    public int deleteConCostOrgByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conCostOrg
    * @return
    */
    int changeStatus(ConCostOrg conCostOrg);

    /**
     * 更改确认状态
     * @param conCostOrg
     * @return
     */
    int check(ConCostOrg conCostOrg);

}
