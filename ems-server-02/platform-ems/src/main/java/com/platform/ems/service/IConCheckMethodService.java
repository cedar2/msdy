package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConCheckMethod;

import java.util.List;

/**
 * 检测方法Service接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface IConCheckMethodService extends IService<ConCheckMethod>{
    /**
     * 查询检测方法
     * 
     * @param sid 检测方法ID
     * @return 检测方法
     */
    public ConCheckMethod selectConCheckMethodById (Long sid);

    /**
     * 查询检测方法列表
     * 
     * @param conCheckMethod 检测方法
     * @return 检测方法集合
     */
    public List<ConCheckMethod> selectConCheckMethodList (ConCheckMethod conCheckMethod);

    /**
     * 新增检测方法
     * 
     * @param conCheckMethod 检测方法
     * @return 结果
     */
    public int insertConCheckMethod (ConCheckMethod conCheckMethod);

    /**
     * 修改检测方法
     * 
     * @param conCheckMethod 检测方法
     * @return 结果
     */
    public int updateConCheckMethod (ConCheckMethod conCheckMethod);

    /**
     * 变更检测方法
     *
     * @param conCheckMethod 检测方法
     * @return 结果
     */
    public int changeConCheckMethod (ConCheckMethod conCheckMethod);

    /**
     * 批量删除检测方法
     * 
     * @param sids 需要删除的检测方法ID
     * @return 结果
     */
    public int deleteConCheckMethodByIds (List<Long> sids);

    /**
    * 启用/停用
    * @param conCheckMethod
    * @return
    */
    int changeStatus (ConCheckMethod conCheckMethod);

    /**
     * 更改确认状态
     * @param conCheckMethod
     * @return
     */
    int check (ConCheckMethod conCheckMethod);

}
