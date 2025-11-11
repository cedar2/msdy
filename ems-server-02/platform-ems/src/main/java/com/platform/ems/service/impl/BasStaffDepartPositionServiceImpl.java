package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.BasStaffDepartPosition;
import com.platform.ems.mapper.BasStaffDepartPositionMapper;
import com.platform.ems.service.IBasStaffDepartPositionService;

/**
 * 员工所属部门岗位信息Service业务层处理
 * 
 * @author qhq
 * @date 2021-03-18
 */
@Service
@SuppressWarnings("all")
public class BasStaffDepartPositionServiceImpl extends ServiceImpl<BasStaffDepartPositionMapper,BasStaffDepartPosition>  implements IBasStaffDepartPositionService {
    @Autowired
    private BasStaffDepartPositionMapper basStaffDepartPositionMapper;

    /**
     * 查询员工所属部门岗位信息
     * 
     * @param clientId 员工所属部门岗位信息ID
     * @return 员工所属部门岗位信息
     */
    @Override
    public BasStaffDepartPosition selectBasStaffDepartPositionById(String clientId) {
        return basStaffDepartPositionMapper.selectBasStaffDepartPositionById(clientId);
    }

    /**
     * 查询员工所属部门岗位信息列表
     * 
     * @param basStaffDepartPosition 员工所属部门岗位信息
     * @return 员工所属部门岗位信息
     */
    @Override
    public List<BasStaffDepartPosition> selectBasStaffDepartPositionList(BasStaffDepartPosition basStaffDepartPosition) {
        return basStaffDepartPositionMapper.selectBasStaffDepartPositionList(basStaffDepartPosition);
    }

    /**
     * 新增员工所属部门岗位信息
     * 需要注意编码重复校验
     * @param basStaffDepartPosition 员工所属部门岗位信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStaffDepartPosition(BasStaffDepartPosition basStaffDepartPosition) {
        return basStaffDepartPositionMapper.insert(basStaffDepartPosition);
    }

    /**
     * 修改员工所属部门岗位信息
     * 
     * @param basStaffDepartPosition 员工所属部门岗位信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasStaffDepartPosition(BasStaffDepartPosition basStaffDepartPosition) {
        return basStaffDepartPositionMapper.updateById(basStaffDepartPosition);
    }

    /**
     * 批量删除员工所属部门岗位信息
     * 
     * @param clientIds 需要删除的员工所属部门岗位信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasStaffDepartPositionByIds(List<String> clientIds) {
        return basStaffDepartPositionMapper.deleteBatchIds(clientIds);
    }


}
