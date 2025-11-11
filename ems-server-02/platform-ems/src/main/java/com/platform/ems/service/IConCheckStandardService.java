package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConCheckStandard;
import com.platform.ems.domain.ConCheckStandardItem;

import java.util.List;

/**
 * 检测标准Service接口
 *
 * @author qhq
 * @date 2021-11-01
 */
public interface IConCheckStandardService extends IService<ConCheckStandard>{
    /**
     * 查询检测标准
     *
     * @param sid 检测标准ID
     * @return 检测标准
     */
    public ConCheckStandard selectConCheckStandardById (Long sid);

    /**
     * 查询检测标准列表
     *
     * @param conCheckStandard 检测标准
     * @return 检测标准集合
     */
    public List<ConCheckStandard> selectConCheckStandardList (ConCheckStandard conCheckStandard);

    /**
     * 新增检测标准
     *
     * @param conCheckStandard 检测标准
     * @return 结果
     */
    public int insertConCheckStandard (ConCheckStandard conCheckStandard);

    /**
     * 修改检测标准
     *
     * @param conCheckStandard 检测标准
     * @return 结果
     */
    public int updateConCheckStandard (ConCheckStandard conCheckStandard);

    /**
     * 变更检测标准
     *
     * @param conCheckStandard 检测标准
     * @return 结果
     */
    public int changeConCheckStandard (ConCheckStandard conCheckStandard);

    /**
     * 批量删除检测标准
     *
     * @param sids 需要删除的检测标准ID
     * @return 结果
     */
    public int deleteConCheckStandardByIds (List<Long> sids);

    /**
    * 启用/停用
    * @param conCheckStandard
    * @return
    */
    int changeStatus (ConCheckStandard conCheckStandard);

    /**
     * 更改确认状态
     * @param conCheckStandard
     * @return
     */
    int check (ConCheckStandard conCheckStandard);

    /**
     * 分配项目
     * @param conCheckStandard
     * @return
     */
    public int addStandardItem(ConCheckStandard conCheckStandard);

    /**
     * 分配方法
     * @param conCheckStandardItem
     * @return
     */
    public int addStandardItemMethod(ConCheckStandardItem conCheckStandardItem);
}
