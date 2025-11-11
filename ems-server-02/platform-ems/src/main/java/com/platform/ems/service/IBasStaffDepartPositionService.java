package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasStaffDepartPosition;

/**
 * 员工所属部门岗位信息Service接口
 * 
 * @author qhq
 * @date 2021-03-18
 */
public interface IBasStaffDepartPositionService extends IService<BasStaffDepartPosition>{
    /**
     * 查询员工所属部门岗位信息
     * 
     * @param clientId 员工所属部门岗位信息ID
     * @return 员工所属部门岗位信息
     */
    public BasStaffDepartPosition selectBasStaffDepartPositionById(String clientId);

    /**
     * 查询员工所属部门岗位信息列表
     * 
     * @param basStaffDepartPosition 员工所属部门岗位信息
     * @return 员工所属部门岗位信息集合
     */
    public List<BasStaffDepartPosition> selectBasStaffDepartPositionList(BasStaffDepartPosition basStaffDepartPosition);

    /**
     * 新增员工所属部门岗位信息
     * 
     * @param basStaffDepartPosition 员工所属部门岗位信息
     * @return 结果
     */
    public int insertBasStaffDepartPosition(BasStaffDepartPosition basStaffDepartPosition);

    /**
     * 修改员工所属部门岗位信息
     * 
     * @param basStaffDepartPosition 员工所属部门岗位信息
     * @return 结果
     */
    public int updateBasStaffDepartPosition(BasStaffDepartPosition basStaffDepartPosition);

    /**
     * 批量删除员工所属部门岗位信息
     * 
     * @param clientIds 需要删除的员工所属部门岗位信息ID
     * @return 结果
     */
    public int deleteBasStaffDepartPositionByIds(List<String>  clientIds);

}
