package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConReferenceProject;

/**
 * 业务归属项目Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConReferenceProjectService extends IService<ConReferenceProject>{
    /**
     * 查询业务归属项目
     * 
     * @param sid 业务归属项目ID
     * @return 业务归属项目
     */
    public ConReferenceProject selectConReferenceProjectById(Long sid);

    /**
     * 查询业务归属项目列表
     * 
     * @param conReferenceProject 业务归属项目
     * @return 业务归属项目集合
     */
    public List<ConReferenceProject> selectConReferenceProjectList(ConReferenceProject conReferenceProject);

    /**
     * 新增业务归属项目
     * 
     * @param conReferenceProject 业务归属项目
     * @return 结果
     */
    public int insertConReferenceProject(ConReferenceProject conReferenceProject);

    /**
     * 修改业务归属项目
     * 
     * @param conReferenceProject 业务归属项目
     * @return 结果
     */
    public int updateConReferenceProject(ConReferenceProject conReferenceProject);

    /**
     * 变更业务归属项目
     *
     * @param conReferenceProject 业务归属项目
     * @return 结果
     */
    public int changeConReferenceProject(ConReferenceProject conReferenceProject);

    /**
     * 批量删除业务归属项目
     * 
     * @param sids 需要删除的业务归属项目ID
     * @return 结果
     */
    public int deleteConReferenceProjectByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conReferenceProject
    * @return
    */
    int changeStatus(ConReferenceProject conReferenceProject);

    /**
     * 更改确认状态
     * @param conReferenceProject
     * @return
     */
    int check(ConReferenceProject conReferenceProject);

}
