package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDeliveryCategory;

/**
 * 交货类别Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDeliveryCategoryService extends IService<ConDeliveryCategory>{
    /**
     * 查询交货类别
     * 
     * @param sid 交货类别ID
     * @return 交货类别
     */
    public ConDeliveryCategory selectConDeliveryCategoryById(Long sid);

    /**
     * 查询交货类别列表
     * 
     * @param conDeliveryCategory 交货类别
     * @return 交货类别集合
     */
    public List<ConDeliveryCategory> selectConDeliveryCategoryList(ConDeliveryCategory conDeliveryCategory);

    /**
     * 新增交货类别
     * 
     * @param conDeliveryCategory 交货类别
     * @return 结果
     */
    public int insertConDeliveryCategory(ConDeliveryCategory conDeliveryCategory);

    /**
     * 修改交货类别
     * 
     * @param conDeliveryCategory 交货类别
     * @return 结果
     */
    public int updateConDeliveryCategory(ConDeliveryCategory conDeliveryCategory);

    /**
     * 变更交货类别
     *
     * @param conDeliveryCategory 交货类别
     * @return 结果
     */
    public int changeConDeliveryCategory(ConDeliveryCategory conDeliveryCategory);

    /**
     * 批量删除交货类别
     * 
     * @param sids 需要删除的交货类别ID
     * @return 结果
     */
    public int deleteConDeliveryCategoryByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDeliveryCategory
    * @return
    */
    int changeStatus(ConDeliveryCategory conDeliveryCategory);

    /**
     * 更改确认状态
     * @param conDeliveryCategory
     * @return
     */
    int check(ConDeliveryCategory conDeliveryCategory);

}
