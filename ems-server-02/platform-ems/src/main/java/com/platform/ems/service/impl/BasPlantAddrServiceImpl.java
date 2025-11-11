package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasPlantAddrMapper;
import com.platform.ems.domain.BasPlantAddr;
import com.platform.ems.service.IBasPlantAddrService;

/**
 * 工厂-联系方式信息Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
@Service
@SuppressWarnings("all")
public class BasPlantAddrServiceImpl extends ServiceImpl<BasPlantAddrMapper,BasPlantAddr>  implements IBasPlantAddrService {
    @Autowired
    private BasPlantAddrMapper basPlantAddrMapper;

    /**
     * 查询工厂-联系方式信息
     * 
     * @param plantContactSid 工厂-联系方式信息ID
     * @return 工厂-联系方式信息
     */
    @Override
    public BasPlantAddr selectBasPlantAddrById(Long plantContactSid) {
        return basPlantAddrMapper.selectBasPlantAddrById(plantContactSid);
    }

    /**
     * 查询工厂-联系方式信息列表
     * 
     * @param basPlantAddr 工厂-联系方式信息
     * @return 工厂-联系方式信息
     */
    @Override
    public List<BasPlantAddr> selectBasPlantAddrList(BasPlantAddr basPlantAddr) {
        return basPlantAddrMapper.selectBasPlantAddrList(basPlantAddr);
    }

    /**
     * 新增工厂-联系方式信息
     * 需要注意编码重复校验
     * @param basPlantAddr 工厂-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPlantAddr(BasPlantAddr basPlantAddr) {
        return basPlantAddrMapper.insert(basPlantAddr);
    }

    /**
     * 修改工厂-联系方式信息
     * 
     * @param basPlantAddr 工厂-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPlantAddr(BasPlantAddr basPlantAddr) {
        return basPlantAddrMapper.updateById(basPlantAddr);
    }

    /**
     * 批量删除工厂-联系方式信息
     * 
     * @param plantContactSids 需要删除的工厂-联系方式信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPlantAddrByIds(List<Long> plantContactSids) {
        return basPlantAddrMapper.deleteBatchIds(plantContactSids);
    }


}
