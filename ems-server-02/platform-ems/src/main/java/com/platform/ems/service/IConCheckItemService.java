package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConCheckItem;

import java.util.List;

/**
 * 检测项目Service接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface IConCheckItemService extends IService<ConCheckItem>{
    /**
     * 查询检测项目
     * 
     * @param sid 检测项目ID
     * @return 检测项目
     */
    public ConCheckItem selectConCheckItemById (Long sid);

    /**
     * 查询检测项目列表
     * 
     * @param conCheckItem 检测项目
     * @return 检测项目集合
     */
    public List<ConCheckItem> selectConCheckItemList (ConCheckItem conCheckItem);

    /**
     * 新增检测项目
     * 
     * @param conCheckItem 检测项目
     * @return 结果
     */
    public int insertConCheckItem (ConCheckItem conCheckItem);

    /**
     * 修改检测项目
     * 
     * @param conCheckItem 检测项目
     * @return 结果
     */
    public int updateConCheckItem (ConCheckItem conCheckItem);

    /**
     * 变更检测项目
     *
     * @param conCheckItem 检测项目
     * @return 结果
     */
    public int changeConCheckItem (ConCheckItem conCheckItem);

    /**
     * 批量删除检测项目
     * 
     * @param sids 需要删除的检测项目ID
     * @return 结果
     */
    public int deleteConCheckItemByIds (List<Long> sids);

    /**
    * 启用/停用
    * @param conCheckItem
    * @return
    */
    int changeStatus (ConCheckItem conCheckItem);

    /**
     * 更改确认状态
     * @param conCheckItem
     * @return
     */
    int check (ConCheckItem conCheckItem);

}
