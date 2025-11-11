package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasStorehouse;
import com.platform.ems.domain.BasStorehouseAddr;
import com.platform.ems.domain.BasStorehouseLocation;

/**
 * 仓库档案Service接口
 *
 * @author linhongwei
 * @date 2021-03-17
 */
public interface IBasStorehouseService extends IService<BasStorehouse>{
    /**
     * 查询仓库档案
     *
     * @param storehouseSid 仓库档案ID
     * @return 仓库档案
     */
    public BasStorehouse selectBasStorehouseById(Long storehouseSid);

    /**
     * 查询仓库档案列表
     *
     * @param basStorehouse 仓库档案
     * @return 仓库档案集合
     */
    public List<BasStorehouse> selectBasStorehouseList(BasStorehouse basStorehouse);

    /**
     * 新增仓库档案
     *
     * @param basStorehouse 仓库档案
     * @return 结果
     */
    public int insertBasStorehouse(BasStorehouse basStorehouse);

    /**
     * 修改仓库档案
     *
     * @param basStorehouse 仓库档案
     * @return 结果
     */
    public int updateBasStorehouse(BasStorehouse basStorehouse);

    /**
     * 批量删除仓库档案
     *
     * @param storehouseSids 需要删除的仓库档案ID
     * @return 结果
     */
    public int deleteBasStorehouseByIds(String[] storehouseSids);

    /**
     * 批量确认仓库档案
     *
     * @param basStorehouse 仓库档案IDS、确认状态
     * @return 结果
     */
    int confirm(BasStorehouse basStorehouse);

    /**
     * 变更仓库档案
     *
     * @param basStorehouse 仓库档案
     * @return 结果
     */
    int change(BasStorehouse basStorehouse);

    /**
     * 批量启用/停用仓库档案
     *
     * @param basStorehouse 仓库档案IDS、启用/停用状态
     * @return 结果
     */
    int status(BasStorehouse basStorehouse);

    /**
     * 仓库档案下拉框列表
     * @return 结果
     */
    List<BasStorehouse> getStorehouseList();

    /**
     * 仓库档案下拉框列表
     * @return 结果
     */
    List<BasStorehouse> getList(BasStorehouse basStorehouse);

    /**
     * 获取仓库下库位列表
     *
     * @param storehouseSid 仓库档案ID
     * @return 结果
     */
    List<BasStorehouseLocation> getStorehouseLocationListById(Long storehouseSid);

    /**
     * 获取仓库下库位列表
     *
     * @param basStorehouse 仓库档案ID
     * @return 结果
     */
    List<BasStorehouseLocation> getLocationList(BasStorehouse basStorehouse);

    /**
     * 查询仓库档案联系人档案列表
     *
     * @param addr 仓库档案
     * @return 仓库档案集合
     */
    public List<BasStorehouseAddr> selectBasStorehouseAddrList(BasStorehouseAddr addr);
}
