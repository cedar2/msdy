package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConShippingPoint;

/**
 * 装运点Service接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConShippingPointService extends IService<ConShippingPoint>{
    /**
     * 查询装运点
     * 
     * @param sid 装运点ID
     * @return 装运点
     */
    public ConShippingPoint selectConShippingPointById(Long sid);

    /**
     * 查询装运点列表
     * 
     * @param conShippingPoint 装运点
     * @return 装运点集合
     */
    public List<ConShippingPoint> selectConShippingPointList(ConShippingPoint conShippingPoint);

    /**
     * 新增装运点
     * 
     * @param conShippingPoint 装运点
     * @return 结果
     */
    public int insertConShippingPoint(ConShippingPoint conShippingPoint);

    /**
     * 修改装运点
     * 
     * @param conShippingPoint 装运点
     * @return 结果
     */
    public int updateConShippingPoint(ConShippingPoint conShippingPoint);

    /**
     * 变更装运点
     *
     * @param conShippingPoint 装运点
     * @return 结果
     */
    public int changeConShippingPoint(ConShippingPoint conShippingPoint);

    /**
     * 批量删除装运点
     * 
     * @param sids 需要删除的装运点ID
     * @return 结果
     */
    public int deleteConShippingPointByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conShippingPoint
    * @return
    */
    int changeStatus(ConShippingPoint conShippingPoint);

    /**
     * 更改确认状态
     * @param conShippingPoint
     * @return
     */
    int check(ConShippingPoint conShippingPoint);

}
