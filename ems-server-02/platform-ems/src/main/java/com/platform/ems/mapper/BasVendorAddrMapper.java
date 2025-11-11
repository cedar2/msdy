package com.platform.ems.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasVendorAddr;
import org.apache.ibatis.annotations.Param;

/**
 * 供应商-联系方式信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-01-31
 */
public interface BasVendorAddrMapper extends BaseMapper<BasVendorAddr> {
    /**
     * 查询供应商-联系方式信息
     * 
     * @param clientId 供应商-联系方式信息ID
     * @return 供应商-联系方式信息
     */
    public BasVendorAddr selectBasVendorAddrById(String clientId);

    /**
     * 查询供应商-联系方式信息列表
     * 
     * @param basVendorAddr 供应商-联系方式信息
     * @return 供应商-联系方式信息集合
     */
    public List<BasVendorAddr> selectBasVendorAddrList(BasVendorAddr basVendorAddr);

    /**
     * 新增供应商-联系方式信息
     * 
     * @param basVendorAddr 供应商-联系方式信息
     * @return 结果
     */
    public int insertBasVendorAddr(BasVendorAddr basVendorAddr);

    /**
     * 修改供应商-联系方式信息
     * 
     * @param basVendorAddr 供应商-联系方式信息
     * @return 结果
     */
    public int updateBasVendorAddr(BasVendorAddr basVendorAddr);

    /**
     * 删除供应商-联系方式信息
     * 
     * @param clientId 供应商-联系方式信息ID
     * @return 结果
     */
    public int deleteBasVendorAddrById(String clientId);

    /**
     * 批量删除供应商-联系方式信息
     * 
     * @param clientIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBasVendorAddrByIds(String[] clientIds);

    /**
     * 添加多个
     * @param list List BasVendorAddr
     * @return int
     */
    int inserts(@Param("list") List<BasVendorAddr> list);
}
