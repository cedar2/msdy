package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPurchaseGroup;

/**
 * 采购组Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConPurchaseGroupService extends IService<ConPurchaseGroup>{
    /**
     * 查询采购组
     * 
     * @param sid 采购组ID
     * @return 采购组
     */
    public ConPurchaseGroup selectConPurchaseGroupById(Long sid);

    /**
     * 查询采购组列表
     * 
     * @param conPurchaseGroup 采购组
     * @return 采购组集合
     */
    public List<ConPurchaseGroup> selectConPurchaseGroupList(ConPurchaseGroup conPurchaseGroup);

    /**
     * 新增采购组
     * 
     * @param conPurchaseGroup 采购组
     * @return 结果
     */
    public int insertConPurchaseGroup(ConPurchaseGroup conPurchaseGroup);

    /**
     * 修改采购组
     * 
     * @param conPurchaseGroup 采购组
     * @return 结果
     */
    public int updateConPurchaseGroup(ConPurchaseGroup conPurchaseGroup);

    /**
     * 变更采购组
     *
     * @param conPurchaseGroup 采购组
     * @return 结果
     */
    public int changeConPurchaseGroup(ConPurchaseGroup conPurchaseGroup);

    /**
     * 批量删除采购组
     * 
     * @param sids 需要删除的采购组ID
     * @return 结果
     */
    public int deleteConPurchaseGroupByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conPurchaseGroup
    * @return
    */
    int changeStatus(ConPurchaseGroup conPurchaseGroup);

    /**
     * 更改确认状态
     * @param conPurchaseGroup
     * @return
     */
    int check(ConPurchaseGroup conPurchaseGroup);

    /**
     * 采购组下拉框
     */
    List<ConPurchaseGroup> getList();

    /**
     * 采购组下拉框
     */
    List<ConPurchaseGroup> getPurchaseGroupList(ConPurchaseGroup conPurchaseGroup);
}
