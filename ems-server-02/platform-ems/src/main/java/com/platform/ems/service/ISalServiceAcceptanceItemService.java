package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalServiceAcceptanceItem;

/**
 * 服务销售验收单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface ISalServiceAcceptanceItemService extends IService<SalServiceAcceptanceItem>{
    /**
     * 查询服务销售验收单-明细
     * 
     * @param serviceAcceptanceItemSid 服务销售验收单-明细ID
     * @return 服务销售验收单-明细
     */
    public SalServiceAcceptanceItem selectSalServiceAcceptanceItemById(Long serviceAcceptanceItemSid);

    /**
     * 查询服务销售验收单-明细列表
     * 
     * @param salServiceAcceptanceItem 服务销售验收单-明细
     * @return 服务销售验收单-明细集合
     */
    public List<SalServiceAcceptanceItem> selectSalServiceAcceptanceItemList(SalServiceAcceptanceItem salServiceAcceptanceItem);

    /**
     * 新增服务销售验收单-明细
     * 
     * @param salServiceAcceptanceItem 服务销售验收单-明细
     * @return 结果
     */
    public int insertSalServiceAcceptanceItem(SalServiceAcceptanceItem salServiceAcceptanceItem);

    /**
     * 修改服务销售验收单-明细
     * 
     * @param salServiceAcceptanceItem 服务销售验收单-明细
     * @return 结果
     */
    public int updateSalServiceAcceptanceItem(SalServiceAcceptanceItem salServiceAcceptanceItem);

    /**
     * 批量删除服务销售验收单-明细
     * 
     * @param serviceAcceptanceItemSids 需要删除的服务销售验收单-明细ID
     * @return 结果
     */
    public int deleteSalServiceAcceptanceItemByIds(List<Long> serviceAcceptanceItemSids);

}
