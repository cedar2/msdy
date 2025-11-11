package com.platform.ems.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.TecModelPosSizeMapper;
import com.platform.ems.domain.TecModelPosSize;
import com.platform.ems.service.ITecModelPosSizeService;

/**
 * 版型-部位-尺码-尺寸Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-02-08
 */
@Service
public class TecModelPosSizeServiceImpl implements ITecModelPosSizeService {
    @Autowired
    private TecModelPosSizeMapper tecModelPosSizeMapper;

    /**
     * 查询版型-部位-尺码-尺寸
     * 
     * @param clientId 版型-部位-尺码-尺寸ID
     * @return 版型-部位-尺码-尺寸
     */
    @Override
    public TecModelPosSize selectTecModelPosSizeById(String clientId) {
        return tecModelPosSizeMapper.selectTecModelPosSizeById(clientId);
    }

    /**
     * 查询版型-部位-尺码-尺寸列表
     * 
     * @param tecModelPosSize 版型-部位-尺码-尺寸
     * @return 版型-部位-尺码-尺寸
     */
    @Override
    public List<TecModelPosSize> selectTecModelPosSizeList(TecModelPosSize tecModelPosSize) {
        return tecModelPosSizeMapper.selectTecModelPosSizeList(tecModelPosSize);
    }

    /**
     * 新增版型-部位-尺码-尺寸
     * 
     * @param tecModelPosSize 版型-部位-尺码-尺寸
     * @return 结果
     */
    @Override
    public int insertTecModelPosSize(TecModelPosSize tecModelPosSize) {
        return tecModelPosSizeMapper.insert(tecModelPosSize);
    }

    /**
     * 修改版型-部位-尺码-尺寸
     * 
     * @param tecModelPosSize 版型-部位-尺码-尺寸
     * @return 结果
     */
    @Override
    public int updateTecModelPosSize(TecModelPosSize tecModelPosSize) {
        return tecModelPosSizeMapper.updateTecModelPosSize(tecModelPosSize);
    }

    /**
     * 批量删除版型-部位-尺码-尺寸
     * 
     * @param clientIds 需要删除的版型-部位-尺码-尺寸ID
     * @return 结果
     */
    @Override
    public int deleteTecModelPosSizeByIds(String[] clientIds) {
        return tecModelPosSizeMapper.deleteTecModelPosSizeByIds(clientIds);
    }

    /**
     * 删除版型-部位-尺码-尺寸信息
     * 
     * @param clientId 版型-部位-尺码-尺寸ID
     * @return 结果
     */
    @Override
    public int deleteTecModelPosSizeById(String clientId) {
        return tecModelPosSizeMapper.deleteTecModelPosSizeById(clientId);
    }
}
