package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConCheckStandardItem;

import java.util.List;

/**
 * 检测标准/项目关联Service接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface IConCheckStandardItemService extends IService<ConCheckStandardItem>{
    /**
     * 查询检测标准/项目关联
     * 
     * @param checkStandardItemSid 检测标准/项目关联ID
     * @return 检测标准/项目关联
     */
    public ConCheckStandardItem selectConCheckStandardItemById (Long checkStandardItemSid);

    /**
     * 查询检测标准/项目关联列表
     * 
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 检测标准/项目关联集合
     */
    public List<ConCheckStandardItem> selectConCheckStandardItemList (ConCheckStandardItem conCheckStandardItem);

    /**
     * 新增检测标准/项目关联
     * 
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 结果
     */
    public int insertConCheckStandardItem (ConCheckStandardItem conCheckStandardItem);

    /**
     * 修改检测标准/项目关联
     * 
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 结果
     */
    public int updateConCheckStandardItem (ConCheckStandardItem conCheckStandardItem);

    /**
     * 变更检测标准/项目关联
     *
     * @param conCheckStandardItem 检测标准/项目关联
     * @return 结果
     */
    public int changeConCheckStandardItem (ConCheckStandardItem conCheckStandardItem);

    /**
     * 批量删除检测标准/项目关联
     * 
     * @param checkStandardItemSids 需要删除的检测标准/项目关联ID
     * @return 结果
     */
    public int deleteConCheckStandardItemByIds (List<Long> checkStandardItemSids);

    /**
    * 启用/停用
    * @param conCheckStandardItem
    * @return
    */
//    int changeStatus (ConCheckStandardItem conCheckStandardItem);

    /**
     * 更改确认状态
     * @param conCheckStandardItem
     * @return
     */
//    int check (ConCheckStandardItem conCheckStandardItem);

}
