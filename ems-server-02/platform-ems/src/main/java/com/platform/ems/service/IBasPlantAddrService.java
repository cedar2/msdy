package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasPlantAddr;

/**
 * 工厂-联系方式信息Service接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface IBasPlantAddrService extends IService<BasPlantAddr>{
    /**
     * 查询工厂-联系方式信息
     * 
     * @param plantContactSid 工厂-联系方式信息ID
     * @return 工厂-联系方式信息
     */
    public BasPlantAddr selectBasPlantAddrById(Long plantContactSid);

    /**
     * 查询工厂-联系方式信息列表
     * 
     * @param basPlantAddr 工厂-联系方式信息
     * @return 工厂-联系方式信息集合
     */
    public List<BasPlantAddr> selectBasPlantAddrList(BasPlantAddr basPlantAddr);

    /**
     * 新增工厂-联系方式信息
     * 
     * @param basPlantAddr 工厂-联系方式信息
     * @return 结果
     */
    public int insertBasPlantAddr(BasPlantAddr basPlantAddr);

    /**
     * 修改工厂-联系方式信息
     * 
     * @param basPlantAddr 工厂-联系方式信息
     * @return 结果
     */
    public int updateBasPlantAddr(BasPlantAddr basPlantAddr);

    /**
     * 批量删除工厂-联系方式信息
     * 
     * @param plantContactSids 需要删除的工厂-联系方式信息ID
     * @return 结果
     */
    public int deleteBasPlantAddrByIds(List<Long> plantContactSids);

}
