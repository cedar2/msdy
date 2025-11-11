package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOrderComponent;

/**
 * 生产订单-组件Service接口
 * 
 * @author qhq
 * @date 2021-04-13
 */
public interface IManManufactureOrderComponentService extends IService<ManManufactureOrderComponent>{
    /**
     * 查询生产订单-组件
     * 
     * @param manufactureOrderComponentSid 生产订单-组件ID
     * @return 生产订单-组件
     */
    public ManManufactureOrderComponent selectManManufactureOrderComponentById(String manufactureOrderComponentSid);

    /**
     * 查询生产订单-组件列表
     * 
     * @param manManufactureOrderComponent 生产订单-组件
     * @return 生产订单-组件集合
     */
    public List<ManManufactureOrderComponent> selectManManufactureOrderComponentList(ManManufactureOrderComponent manManufactureOrderComponent);

    /**
     * 新增生产订单-组件
     * 
     * @param manManufactureOrderComponent 生产订单-组件
     * @return 结果
     */
    public int insertManManufactureOrderComponent(ManManufactureOrderComponent manManufactureOrderComponent);

    /**
     * 修改生产订单-组件
     * 
     * @param manManufactureOrderComponent 生产订单-组件
     * @return 结果
     */
    public int updateManManufactureOrderComponent(ManManufactureOrderComponent manManufactureOrderComponent);

    /**
     * 批量删除生产订单-组件
     * 
     * @param manufactureOrderComponentSids 需要删除的生产订单-组件ID
     * @return 结果
     */
    public int deleteManManufactureOrderComponentByIds(List<String>  manufactureOrderComponentSids);

}
