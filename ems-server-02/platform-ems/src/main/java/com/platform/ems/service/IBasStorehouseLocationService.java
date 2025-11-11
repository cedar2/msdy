package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasStorehouseLocation;

/**
 * 仓库-库位信息Service接口
 * 
 * @author linhongwei
 * @date 2021-03-17
 */
public interface IBasStorehouseLocationService {
    /**
     * 查询仓库-库位信息
     * 
     * @param clientId 仓库-库位信息ID
     * @return 仓库-库位信息
     */
    public BasStorehouseLocation selectBasStorehouseLocationById(String clientId);

    /**
     * 查询仓库-库位信息列表
     * 
     * @param basStorehouseLocation 仓库-库位信息
     * @return 仓库-库位信息集合
     */
    public List<BasStorehouseLocation> selectBasStorehouseLocationList(BasStorehouseLocation basStorehouseLocation);

    /**
     * 新增仓库-库位信息
     * 
     * @param basStorehouseLocation 仓库-库位信息
     * @return 结果
     */
    public int insertBasStorehouseLocation(BasStorehouseLocation basStorehouseLocation);

    /**
     * 修改仓库-库位信息
     * 
     * @param basStorehouseLocation 仓库-库位信息
     * @return 结果
     */
    public int updateBasStorehouseLocation(BasStorehouseLocation basStorehouseLocation);

    /**
     * 批量删除仓库-库位信息
     * 
     * @param clientIds 需要删除的仓库-库位信息ID
     * @return 结果
     */
    public int deleteBasStorehouseLocationByIds(List<String> clientIds);

}
