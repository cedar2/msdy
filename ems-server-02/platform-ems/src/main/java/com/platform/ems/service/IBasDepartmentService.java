package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasDepartment;

/**
 * 部门档案Service接口
 *
 * @author qhq
 * @date 2021-04-09
 */
public interface IBasDepartmentService extends IService<BasDepartment>{
    /**
     * 查询部门档案
     *
     * @param departmentSid 部门档案ID
     * @return 部门档案
     */
    public BasDepartment selectBasDepartmentById(Long departmentSid);

    /**
     * 查询部门档案列表
     *
     * @param basDepartment 部门档案
     * @return 部门档案集合
     */
    public List<BasDepartment> selectBasDepartmentList(BasDepartment basDepartment);

    /**
     * 新增部门档案
     *
     * @param basDepartment 部门档案
     * @return 结果
     */
    public int insertBasDepartment(BasDepartment basDepartment);

    /**
     * 修改部门档案
     *
     * @param basDepartment 部门档案
     * @return 结果
     */
    public int updateBasDepartment(BasDepartment basDepartment);

    /**
     * 批量删除部门档案
     *
     * @param departmentSids 需要删除的部门档案ID
     * @return 结果
     */
    public int deleteBasDepartmentByIds(List<Long>  departmentSids);

    public int status(BasDepartment basDepartment);

    public int handleStatus(BasDepartment basDepartment);

    public List<BasDepartment> getCompanyDept(Long companySid);

    public List<BasDepartment> getDeptList(BasDepartment basDepartment);

}
