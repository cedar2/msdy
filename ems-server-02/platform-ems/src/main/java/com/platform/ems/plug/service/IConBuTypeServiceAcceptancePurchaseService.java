package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptancePurchase;

/**
 * 业务类型_服务采购验收单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeServiceAcceptancePurchaseService extends IService<ConBuTypeServiceAcceptancePurchase>{
    /**
     * 查询业务类型_服务采购验收单
     * 
     * @param sid 业务类型_服务采购验收单ID
     * @return 业务类型_服务采购验收单
     */
    public ConBuTypeServiceAcceptancePurchase selectConBuTypeServiceAcceptancePurchaseById(Long sid);

    /**
     * 查询业务类型_服务采购验收单列表
     * 
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 业务类型_服务采购验收单集合
     */
    public List<ConBuTypeServiceAcceptancePurchase> selectConBuTypeServiceAcceptancePurchaseList(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

    /**
     * 新增业务类型_服务采购验收单
     * 
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 结果
     */
    public int insertConBuTypeServiceAcceptancePurchase(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

    /**
     * 修改业务类型_服务采购验收单
     * 
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 结果
     */
    public int updateConBuTypeServiceAcceptancePurchase(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

    /**
     * 变更业务类型_服务采购验收单
     *
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 结果
     */
    public int changeConBuTypeServiceAcceptancePurchase(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

    /**
     * 批量删除业务类型_服务采购验收单
     * 
     * @param sids 需要删除的业务类型_服务采购验收单ID
     * @return 结果
     */
    public int deleteConBuTypeServiceAcceptancePurchaseByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeServiceAcceptancePurchase
    * @return
    */
    int changeStatus(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

    /**
     * 更改确认状态
     * @param conBuTypeServiceAcceptancePurchase
     * @return
     */
    int check(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase);

}
