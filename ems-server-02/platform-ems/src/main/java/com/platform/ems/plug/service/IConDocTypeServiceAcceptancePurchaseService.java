package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptancePurchase;

/**
 * 单据类型_服务采购验收单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeServiceAcceptancePurchaseService extends IService<ConDocTypeServiceAcceptancePurchase>{
    /**
     * 查询单据类型_服务采购验收单
     * 
     * @param sid 单据类型_服务采购验收单ID
     * @return 单据类型_服务采购验收单
     */
    public ConDocTypeServiceAcceptancePurchase selectConDocTypeServiceAcceptancePurchaseById(Long sid);

    /**
     * 查询单据类型_服务采购验收单列表
     * 
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 单据类型_服务采购验收单集合
     */
    public List<ConDocTypeServiceAcceptancePurchase> selectConDocTypeServiceAcceptancePurchaseList(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

    /**
     * 新增单据类型_服务采购验收单
     * 
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 结果
     */
    public int insertConDocTypeServiceAcceptancePurchase(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

    /**
     * 修改单据类型_服务采购验收单
     * 
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 结果
     */
    public int updateConDocTypeServiceAcceptancePurchase(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

    /**
     * 变更单据类型_服务采购验收单
     *
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 结果
     */
    public int changeConDocTypeServiceAcceptancePurchase(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

    /**
     * 批量删除单据类型_服务采购验收单
     * 
     * @param sids 需要删除的单据类型_服务采购验收单ID
     * @return 结果
     */
    public int deleteConDocTypeServiceAcceptancePurchaseByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeServiceAcceptancePurchase
    * @return
    */
    int changeStatus(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

    /**
     * 更改确认状态
     * @param conDocTypeServiceAcceptancePurchase
     * @return
     */
    int check(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase);

}
