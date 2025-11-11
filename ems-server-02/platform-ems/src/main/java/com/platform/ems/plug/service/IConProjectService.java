package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConProject;

/**
 * 项目Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConProjectService extends IService<ConProject>{
    /**
     * 查询项目
     * 
     * @param sid 项目ID
     * @return 项目
     */
    public ConProject selectConProjectById(Long sid);

    /**
     * 查询项目列表
     * 
     * @param conProject 项目
     * @return 项目集合
     */
    public List<ConProject> selectConProjectList(ConProject conProject);

    /**
     * 新增项目
     * 
     * @param conProject 项目
     * @return 结果
     */
    public int insertConProject(ConProject conProject);

    /**
     * 修改项目
     * 
     * @param conProject 项目
     * @return 结果
     */
    public int updateConProject(ConProject conProject);

    /**
     * 变更项目
     *
     * @param conProject 项目
     * @return 结果
     */
    public int changeConProject(ConProject conProject);

    /**
     * 批量删除项目
     * 
     * @param sids 需要删除的项目ID
     * @return 结果
     */
    public int deleteConProjectByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conProject
    * @return
    */
    int changeStatus(ConProject conProject);

    /**
     * 更改确认状态
     * @param conProject
     * @return
     */
    int check(ConProject conProject);

}
