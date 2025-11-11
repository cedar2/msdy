package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConManufactureDepartment;

/**
 * 生产操作部门Service接口
 * 
 * @author zhuangyz
 * @date 2022-07-25
 */
public interface IConManufactureDepartmentService extends IService<ConManufactureDepartment>{
    /**
     * 查询生产操作部门
     * 
     * @param sid 生产操作部门ID
     * @return 生产操作部门
     */
    public ConManufactureDepartment selectConManufactureDepartmentById(Long sid);

    /**
     * 查询生产操作部门列表
     * 
     * @param conManufactureDepartment 生产操作部门
     * @return 生产操作部门集合
     */
    public List<ConManufactureDepartment> selectConManufactureDepartmentList(ConManufactureDepartment conManufactureDepartment);

    /**
     * 新增生产操作部门
     * 
     * @param conManufactureDepartment 生产操作部门
     * @return 结果
     */
    public int insertConManufactureDepartment(ConManufactureDepartment conManufactureDepartment);

    /**
     * 修改生产操作部门
     * 
     * @param conManufactureDepartment 生产操作部门
     * @return 结果
     */
    public int updateConManufactureDepartment(ConManufactureDepartment conManufactureDepartment);

    /**
     * 变更生产操作部门
     *
     * @param conManufactureDepartment 生产操作部门
     * @return 结果
     */
    public int changeConManufactureDepartment(ConManufactureDepartment conManufactureDepartment);

    /**
     * 批量删除生产操作部门
     * 
     * @param sids 需要删除的生产操作部门ID
     * @return 结果
     */
    public int deleteConManufactureDepartmentByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conManufactureDepartment
    * @return
    */
    int changeStatus(ConManufactureDepartment conManufactureDepartment);

    /**
     * 更改确认状态
     * @param conManufactureDepartment
     * @return
     */
    int check(ConManufactureDepartment conManufactureDepartment);

}
