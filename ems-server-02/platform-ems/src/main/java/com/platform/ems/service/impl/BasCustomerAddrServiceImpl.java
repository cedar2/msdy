package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.BasCustomerAddrMapper;
import com.platform.ems.domain.BasCustomerAddr;
import com.platform.ems.service.IBasCustomerAddrService;

/**
 * 客户-联系方式信息Service业务层处理
 * 
 * @author ChenPinzhen
 * @date 2021-01-27
 */
@Service
public class BasCustomerAddrServiceImpl implements IBasCustomerAddrService {
    @Autowired
    private BasCustomerAddrMapper basCustomerAddrMapper;

    /**
     * 查询客户-联系方式信息
     * 
     * @param clientId 客户-联系方式信息ID
     * @return 客户-联系方式信息
     */
    @Override
    public BasCustomerAddr selectBasCustomerAddrById(String clientId) {
        return basCustomerAddrMapper.selectBasCustomerAddrById(clientId);
    }

    /**
     * 查询客户-联系方式信息列表
     * 
     * @param basCustomerAddr 客户-联系方式信息
     * @return 客户-联系方式信息
     */
    @Override
    public List<BasCustomerAddr> selectBasCustomerAddrList(BasCustomerAddr basCustomerAddr) {
        return basCustomerAddrMapper.selectBasCustomerAddrList(basCustomerAddr);
    }

    /**
     * 新增客户-联系方式信息
     * 
     * @param basCustomerAddr 客户-联系方式信息
     * @return 结果
     */
    @Override
    public int insertBasCustomerAddr(BasCustomerAddr basCustomerAddr) {
        return basCustomerAddrMapper.insertBasCustomerAddr(basCustomerAddr);
    }

    /**
     * 修改客户-联系方式信息
     * 
     * @param basCustomerAddr 客户-联系方式信息
     * @return 结果
     */
    @Override
    public int updateBasCustomerAddr(BasCustomerAddr basCustomerAddr) {
        return basCustomerAddrMapper.updateBasCustomerAddr(basCustomerAddr);
    }

    /**
     * 批量删除客户-联系方式信息
     * 
     * @param customerContactSids 需要删除的客户-联系方式信息ID
     * @return 结果
     */
    @Override
    public int deleteBasCustomerAddrByIds(String[] customerContactSids) {
        return basCustomerAddrMapper.deleteBasCustomerAddrByIds(customerContactSids);
    }

    /**
     * 删除客户-联系方式信息信息
     * 
     * @param customerContactSid 客户-联系方式信息ID
     * @return 结果
     */
    @Override
    public int deleteBasCustomerAddrById(String customerContactSid) {
        return basCustomerAddrMapper.deleteBasCustomerAddrById(customerContactSid);
    }
}
