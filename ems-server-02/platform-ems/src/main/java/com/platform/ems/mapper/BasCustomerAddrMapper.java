package com.platform.ems.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasCustomerAddr;
import org.apache.ibatis.annotations.Param;

/**
 * 客户-联系方式信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-01-27
 */
public interface BasCustomerAddrMapper extends BaseMapper<BasCustomerAddr> {
    /**
     * 查询客户-联系方式信息
     * 
     * @param clientId 客户-联系方式信息ID
     * @return 客户-联系方式信息
     */
    public BasCustomerAddr selectBasCustomerAddrById(String clientId);

    /**
     * 查询客户-联系方式信息列表
     * 
     * @param basCustomerAddr 客户-联系方式信息
     * @return 客户-联系方式信息集合
     */
    public List<BasCustomerAddr> selectBasCustomerAddrList(BasCustomerAddr basCustomerAddr);

    /**
     * 新增客户-联系方式信息
     * 
     * @param basCustomerAddr 客户-联系方式信息
     * @return 结果
     */
    public int insertBasCustomerAddr(BasCustomerAddr basCustomerAddr);

    /**
     * 修改客户-联系方式信息
     * 
     * @param basCustomerAddr 客户-联系方式信息
     * @return 结果
     */
    public int updateBasCustomerAddr(BasCustomerAddr basCustomerAddr);

    /**
     * 删除客户-联系方式信息
     * 
     * @param customerContactSid 客户-联系方式信息ID
     * @return 结果
     */
    public int deleteBasCustomerAddrById(String customerContactSid);

    /**
     * 批量删除客户-联系方式信息
     * 
     * @param customerContactSids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBasCustomerAddrByIds(String[] customerContactSids);

    /**
     * 添加多个
     * @param list List BasCustomerAddr
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerAddr> list);
}
