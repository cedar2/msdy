package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.ManManufactureOrderComponent;
import com.platform.ems.mapper.ManManufactureOrderComponentMapper;
import com.platform.ems.service.IManManufactureOrderComponentService;

/**
 * 生产订单-组件Service业务层处理
 * 
 * @author qhq
 * @date 2021-04-13
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderComponentServiceImpl extends ServiceImpl<ManManufactureOrderComponentMapper,ManManufactureOrderComponent>  implements IManManufactureOrderComponentService {
    @Autowired
    private ManManufactureOrderComponentMapper manManufactureOrderComponentMapper;

    /**
     * 查询生产订单-组件
     * 
     * @param manufactureOrderComponentSid 生产订单-组件ID
     * @return 生产订单-组件
     */
    @Override
    public ManManufactureOrderComponent selectManManufactureOrderComponentById(String manufactureOrderComponentSid) {
        return manManufactureOrderComponentMapper.selectManManufactureOrderComponentById(manufactureOrderComponentSid);
    }

    /**
     * 查询生产订单-组件列表
     * 
     * @param manManufactureOrderComponent 生产订单-组件
     * @return 生产订单-组件
     */
    @Override
    public List<ManManufactureOrderComponent> selectManManufactureOrderComponentList(ManManufactureOrderComponent manManufactureOrderComponent) {
        return manManufactureOrderComponentMapper.selectManManufactureOrderComponentList(manManufactureOrderComponent);
    }

    /**
     * 新增生产订单-组件
     * 需要注意编码重复校验
     * @param manManufactureOrderComponent 生产订单-组件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOrderComponent(ManManufactureOrderComponent manManufactureOrderComponent) {
        return manManufactureOrderComponentMapper.insert(manManufactureOrderComponent);
    }

    /**
     * 修改生产订单-组件
     * 
     * @param manManufactureOrderComponent 生产订单-组件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrderComponent(ManManufactureOrderComponent manManufactureOrderComponent) {
        return manManufactureOrderComponentMapper.updateById(manManufactureOrderComponent);
    }

    /**
     * 批量删除生产订单-组件
     * 
     * @param manufactureOrderComponentSids 需要删除的生产订单-组件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOrderComponentByIds(List<String> manufactureOrderComponentSids) {
        return manManufactureOrderComponentMapper.deleteBatchIds(manufactureOrderComponentSids);
    }


}
