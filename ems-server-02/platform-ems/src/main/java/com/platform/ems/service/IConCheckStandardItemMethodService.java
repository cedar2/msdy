package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConCheckStandardItemMethod;

import java.util.List;

/**
 * 检测标准/项目/方法关联Service接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface IConCheckStandardItemMethodService extends IService<ConCheckStandardItemMethod>{
    /**
     * 查询检测标准/项目/方法关联
     * 
     * @param checkStandardItemMethodSid 检测标准/项目/方法关联ID
     * @return 检测标准/项目/方法关联
     */
    public ConCheckStandardItemMethod selectConCheckStandardItemMethodById (Long checkStandardItemMethodSid);

    /**
     * 查询检测标准/项目/方法关联列表
     * 
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 检测标准/项目/方法关联集合
     */
    public List<ConCheckStandardItemMethod> selectConCheckStandardItemMethodList (ConCheckStandardItemMethod conCheckStandardItemMethod);

    /**
     * 新增检测标准/项目/方法关联
     * 
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 结果
     */
    public int insertConCheckStandardItemMethod (ConCheckStandardItemMethod conCheckStandardItemMethod);

    /**
     * 修改检测标准/项目/方法关联
     * 
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 结果
     */
    public int updateConCheckStandardItemMethod (ConCheckStandardItemMethod conCheckStandardItemMethod);

    /**
     * 变更检测标准/项目/方法关联
     *
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 结果
     */
    public int changeConCheckStandardItemMethod (ConCheckStandardItemMethod conCheckStandardItemMethod);

    /**
     * 批量删除检测标准/项目/方法关联
     * 
     * @param checkStandardItemMethodSids 需要删除的检测标准/项目/方法关联ID
     * @return 结果
     */
    public int deleteConCheckStandardItemMethodByIds (List<Long> checkStandardItemMethodSids);

    /**
    * 启用/停用
    * @param conCheckStandardItemMethod
    * @return
    */
//    int changeStatus (ConCheckStandardItemMethod conCheckStandardItemMethod);

    /**
     * 更改确认状态
     * @param conCheckStandardItemMethod
     * @return
     */
//    int check (ConCheckStandardItemMethod conCheckStandardItemMethod);

}
