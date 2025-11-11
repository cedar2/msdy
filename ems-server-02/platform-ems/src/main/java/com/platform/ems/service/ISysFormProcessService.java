package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysFormProcess;

/**
 * 单据关联流程实例Service接口
 *
 * @author qhq
 * @date 2021-09-06
 */
public interface ISysFormProcessService extends IService<SysFormProcess>{
    /**
     * 查询单据关联流程实例
     *
     * @param id 单据关联流程实例ID
     * @return 单据关联流程实例
     */
    public SysFormProcess selectSysFormProcessById(Long id);

    /**
     * 查询单据关联流程实例列表
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 单据关联流程实例集合
     */
    public List<SysFormProcess> selectSysFormProcessList(SysFormProcess sysFormProcess);

    /**
     * 新增单据关联流程实例
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 结果
     */
    public int insertSysFormProcess(SysFormProcess sysFormProcess);

    /**
     * 修改单据关联流程实例
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 结果
     */
    public int updateSysFormProcess(SysFormProcess sysFormProcess);

    /**
     * 变更单据关联流程实例
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 结果
     */
    public int changeSysFormProcess(SysFormProcess sysFormProcess);

    /**
     * 批量删除单据关联流程实例
     *
     * @param ids 需要删除的单据关联流程实例ID
     * @return 结果
     */
    public int deleteSysFormProcessByIds(List<Long>  ids);


}
