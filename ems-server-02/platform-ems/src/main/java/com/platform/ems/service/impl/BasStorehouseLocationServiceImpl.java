package com.platform.ems.service.impl;

import com.platform.ems.domain.BasStorehouseLocation;
import com.platform.ems.mapper.BasStorehouseLocationMapper;
import com.platform.ems.service.IBasStorehouseLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仓库-库位信息Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-17
 */
@Service
public class BasStorehouseLocationServiceImpl implements IBasStorehouseLocationService {
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;

    /**
     * 查询仓库-库位信息
     * 
     * @param clientId 仓库-库位信息ID
     * @return 仓库-库位信息
     */
    @Override
    public BasStorehouseLocation selectBasStorehouseLocationById(String clientId) {
        return basStorehouseLocationMapper.selectBasStorehouseLocationById(clientId);
    }

    /**
    /**
     * 查询仓库-库位信息列表
     * 
     * @param basStorehouseLocation 仓库-库位信息
     * @return 仓库-库位信息
     */
    @Override
    public List<BasStorehouseLocation> selectBasStorehouseLocationList(BasStorehouseLocation basStorehouseLocation) {
        return basStorehouseLocationMapper.selectBasStorehouseLocationList(basStorehouseLocation);
    }

    /**
     * 新增仓库-库位信息
     * 需要注意编码重复校验
     * @param basStorehouseLocation 仓库-库位信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStorehouseLocation(BasStorehouseLocation basStorehouseLocation) {
        return basStorehouseLocationMapper.insert(basStorehouseLocation);
    }

    /**
     * 修改仓库-库位信息
     * 
     * @param basStorehouseLocation 仓库-库位信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasStorehouseLocation(BasStorehouseLocation basStorehouseLocation) {
        return basStorehouseLocationMapper.updateById(basStorehouseLocation);
    }

    /**
     * 批量删除仓库-库位信息
     * 
     * @param clientIds 需要删除的仓库-库位信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasStorehouseLocationByIds(List<String> clientIds) {
        return basStorehouseLocationMapper.deleteBatchIds(clientIds);
    }


}
