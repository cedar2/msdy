package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptanceSale;

/**
 * 单据类型_服务销售验收单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeServiceAcceptanceSaleService extends IService<ConDocTypeServiceAcceptanceSale>{
    /**
     * 查询单据类型_服务销售验收单
     * 
     * @param sid 单据类型_服务销售验收单ID
     * @return 单据类型_服务销售验收单
     */
    public ConDocTypeServiceAcceptanceSale selectConDocTypeServiceAcceptanceSaleById(Long sid);

    /**
     * 查询单据类型_服务销售验收单列表
     * 
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 单据类型_服务销售验收单集合
     */
    public List<ConDocTypeServiceAcceptanceSale> selectConDocTypeServiceAcceptanceSaleList(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

    /**
     * 新增单据类型_服务销售验收单
     * 
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 结果
     */
    public int insertConDocTypeServiceAcceptanceSale(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

    /**
     * 修改单据类型_服务销售验收单
     * 
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 结果
     */
    public int updateConDocTypeServiceAcceptanceSale(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

    /**
     * 变更单据类型_服务销售验收单
     *
     * @param conDocTypeServiceAcceptanceSale 单据类型_服务销售验收单
     * @return 结果
     */
    public int changeConDocTypeServiceAcceptanceSale(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

    /**
     * 批量删除单据类型_服务销售验收单
     * 
     * @param sids 需要删除的单据类型_服务销售验收单ID
     * @return 结果
     */
    public int deleteConDocTypeServiceAcceptanceSaleByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeServiceAcceptanceSale
    * @return
    */
    int changeStatus(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

    /**
     * 更改确认状态
     * @param conDocTypeServiceAcceptanceSale
     * @return
     */
    int check(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

}
