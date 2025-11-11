package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptanceSale;

/**
 * 业务类型_服务销售验收单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeServiceAcceptanceSaleService extends IService<ConBuTypeServiceAcceptanceSale>{
    /**
     * 查询业务类型_服务销售验收单
     * 
     * @param sid 业务类型_服务销售验收单ID
     * @return 业务类型_服务销售验收单
     */
    public ConBuTypeServiceAcceptanceSale selectConBuTypeServiceAcceptanceSaleById(Long sid);

    /**
     * 查询业务类型_服务销售验收单列表
     * 
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 业务类型_服务销售验收单集合
     */
    public List<ConBuTypeServiceAcceptanceSale> selectConBuTypeServiceAcceptanceSaleList(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

    /**
     * 新增业务类型_服务销售验收单
     * 
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 结果
     */
    public int insertConBuTypeServiceAcceptanceSale(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

    /**
     * 修改业务类型_服务销售验收单
     * 
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 结果
     */
    public int updateConBuTypeServiceAcceptanceSale(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

    /**
     * 变更业务类型_服务销售验收单
     *
     * @param conBuTypeServiceAcceptanceSale 业务类型_服务销售验收单
     * @return 结果
     */
    public int changeConBuTypeServiceAcceptanceSale(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

    /**
     * 批量删除业务类型_服务销售验收单
     * 
     * @param sids 需要删除的业务类型_服务销售验收单ID
     * @return 结果
     */
    public int deleteConBuTypeServiceAcceptanceSaleByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeServiceAcceptanceSale
    * @return
    */
    int changeStatus(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

    /**
     * 更改确认状态
     * @param conBuTypeServiceAcceptanceSale
     * @return
     */
    int check(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

}
