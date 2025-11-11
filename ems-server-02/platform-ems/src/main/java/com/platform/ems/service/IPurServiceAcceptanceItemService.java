package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurServiceAcceptanceItem;

/**
 * 服务采购验收单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
public interface IPurServiceAcceptanceItemService extends IService<PurServiceAcceptanceItem>{
    /**
     * 查询服务采购验收单-明细
     * 
     * @param clientId 服务采购验收单-明细ID
     * @return 服务采购验收单-明细
     */
    public PurServiceAcceptanceItem selectPurServiceAcceptanceItemById(String clientId);

    /**
     * 查询服务采购验收单-明细列表
     * 
     * @param purServiceAcceptanceItem 服务采购验收单-明细
     * @return 服务采购验收单-明细集合
     */
    public List<PurServiceAcceptanceItem> selectPurServiceAcceptanceItemList(PurServiceAcceptanceItem purServiceAcceptanceItem);

    /**
     * 新增服务采购验收单-明细
     * 
     * @param purServiceAcceptanceItem 服务采购验收单-明细
     * @return 结果
     */
    public int insertPurServiceAcceptanceItem(PurServiceAcceptanceItem purServiceAcceptanceItem);

    /**
     * 修改服务采购验收单-明细
     * 
     * @param purServiceAcceptanceItem 服务采购验收单-明细
     * @return 结果
     */
    public int updatePurServiceAcceptanceItem(PurServiceAcceptanceItem purServiceAcceptanceItem);

    /**
     * 批量删除服务采购验收单-明细
     * 
     * @param clientIds 需要删除的服务采购验收单-明细ID
     * @return 结果
     */
    public int deletePurServiceAcceptanceItemByIds(List<String> clientIds);

}
