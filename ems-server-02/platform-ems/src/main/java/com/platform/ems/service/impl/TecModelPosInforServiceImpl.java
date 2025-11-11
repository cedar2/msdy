package com.platform.ems.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.TecModelPosInforMapper;
import com.platform.ems.domain.TecModelPosInfor;
import com.platform.ems.service.ITecModelPosInforService;

/**
 * 版型-部位信息Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-02-08
 */
@Service
public class TecModelPosInforServiceImpl implements ITecModelPosInforService {
    @Autowired
    private TecModelPosInforMapper tecModelPosInforMapper;

    /**
     * 查询版型-部位信息
     * 
     * @param clientId 版型-部位信息ID
     * @return 版型-部位信息
     */
    @Override
    public TecModelPosInfor selectTecModelPosInforById(String clientId) {
        return tecModelPosInforMapper.selectTecModelPosInforById(clientId);
    }

    /**
     * 查询版型-部位信息列表
     * 
     * @param tecModelPosInfor 版型-部位信息
     * @return 版型-部位信息
     */
    @Override
    public List<TecModelPosInfor> selectTecModelPosInforList(TecModelPosInfor tecModelPosInfor) {
        return tecModelPosInforMapper.selectTecModelPosInforList(tecModelPosInfor);
    }

    /**
     * 新增版型-部位信息
     * 
     * @param tecModelPosInfor 版型-部位信息
     * @return 结果
     */
    @Override
    public int insertTecModelPosInfor(TecModelPosInfor tecModelPosInfor) {
        return tecModelPosInforMapper.insert(tecModelPosInfor);
    }

    /**
     * 修改版型-部位信息
     * 
     * @param tecModelPosInfor 版型-部位信息
     * @return 结果
     */
    @Override
    public int updateTecModelPosInfor(TecModelPosInfor tecModelPosInfor) {
        return tecModelPosInforMapper.updateTecModelPosInfor(tecModelPosInfor);
    }

    /**
     * 批量删除版型-部位信息
     * 
     * @param clientIds 需要删除的版型-部位信息ID
     * @return 结果
     */
    @Override
    public int deleteTecModelPosInforByIds(String[] clientIds) {
        return tecModelPosInforMapper.deleteTecModelPosInforByIds(clientIds);
    }

    /**
     * 删除版型-部位信息信息
     * 
     * @param clientId 版型-部位信息ID
     * @return 结果
     */
    @Override
    public int deleteTecModelPosInforById(String clientId) {
        return tecModelPosInforMapper.deleteTecModelPosInforById(clientId);
    }
}
