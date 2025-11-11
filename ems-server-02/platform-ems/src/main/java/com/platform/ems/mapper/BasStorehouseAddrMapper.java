package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasStorehouseAddr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仓库-联系方式信息Mapper接口
 *
 * @author chenkw
 * @date 2022-01-05
 */
public interface BasStorehouseAddrMapper extends BaseMapper<BasStorehouseAddr> {

    /**
     * 查询客户-联系方式信息
     *
     * @param clientId 客户-联系方式信息ID
     * @return 客户-联系方式信息
     */
    public BasStorehouseAddr selectBasStorehouseAddrById(String clientId);

    /**
     * 查询客户-联系方式信息列表
     *
     * @param basStorehouseAddr 客户-联系方式信息
     * @return 客户-联系方式信息集合
     */
    public List<BasStorehouseAddr> selectBasStorehouseAddrList(BasStorehouseAddr basStorehouseAddr);

    /**
     * 新增客户-联系方式信息
     *
     * @param basStorehouseAddr 客户-联系方式信息
     * @return 结果
     */
    public int insertBasStorehouseAddr(BasStorehouseAddr basStorehouseAddr);

    /**
     * 修改客户-联系方式信息
     *
     * @param basStorehouseAddr 客户-联系方式信息
     * @return 结果
     */
    public int updateBasStorehouseAddr(BasStorehouseAddr basStorehouseAddr);

    /**
     * 删除客户-联系方式信息
     *
     * @param storehouseContactSid 客户-联系方式信息ID
     * @return 结果
     */
    public int deleteBasStorehouseAddrById(String storehouseContactSid);

    /**
     * 批量删除客户-联系方式信息
     *
     * @param storehouseContactSids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBasStorehouseAddrByIds(String[] storehouseContactSids);

    /**
     * 添加多个
     * @param list List BasStorehouseAddr
     * @return int
     */
    int inserts(@Param("list") List<BasStorehouseAddr> list);
}
