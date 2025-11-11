package com.platform.ems.service;

import java.util.List;

import com.platform.system.domain.SysTodoTask;

/**
 * 待办事项列Service接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface ISysTodoTaskService {
    /**
     * 查询待办事项列
     *
     * @param id 待办事项列ID
     * @return 待办事项列
     */
    public SysTodoTask selectSysTodoTaskListById(String id);

    /**
     * 查询待办事项列列表 (用户工作台)
     *
     * @param sysTodoTask 待办事项列
     * @return 待办事项列集合
     */
    public List<SysTodoTask> selectSysTodoTaskListTable(SysTodoTask sysTodoTask);

    /**
     * 查询待办事项列报表
     *
     * @param sysTodoTask 待办事项列
     * @return 待办事项列集合
     */
    public List<SysTodoTask> selectSysTodoTaskReport(SysTodoTask sysTodoTask);

    /**
     * 查询待办列表
     * @param sysTodoTaskList
     * @return
     */
     List<SysTodoTask>  selectSysTodoTaskLists(SysTodoTask sysTodoTaskList);
    /**
     * 新增待办事项列
     *
     * @param sysTodoTaskList 待办事项列
     * @return 结果
     */
    public int insertSysTodoTaskList(SysTodoTask sysTodoTaskList);

    /**
     * 修改待办事项列
     *
     * @param sysTodoTaskList 待办事项列
     * @return 结果
     */
    public int updateSysTodoTaskList(SysTodoTask sysTodoTaskList);


    /**
     * 批量删除待办事项列
     *
     * @param ids 需要删除的待办事项列ID
     * @return 结果
     */
    public int deleteSysTodoTaskListByIds(List<String> ids);

    /**
     * 新增待办事项
     *
     * @param sysTodoTask 待办事项
     * @return 结果
     */
    public int insertSysTodoTask(SysTodoTask sysTodoTask);

    /**
     * 新增待办事项
     */
    public int insertSysTodoTaskMenu(SysTodoTask sysTodoTask, String tableName);

    /**
     * 根据条件批量删除待办事项
     *
     * @param sids， handleStatus， tableName
     * @return 结果
     */
    public int deleteSysTodoTaskList(Long[] sids, String handleStatus, String tableName);

}
