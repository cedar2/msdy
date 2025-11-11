package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAccountMethodGroup;

/**
 * 收付款方式组合Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConAccountMethodGroupService extends IService<ConAccountMethodGroup>{
    /**
     * 查询收付款方式组合
     * 
     * @param sid 收付款方式组合ID
     * @return 收付款方式组合
     */
    public ConAccountMethodGroup selectConAccountMethodGroupById(Long sid);

    /**
     * 查询收付款方式组合列表
     * 
     * @param conAccountMethodGroup 收付款方式组合
     * @return 收付款方式组合集合
     */
    public List<ConAccountMethodGroup> selectConAccountMethodGroupList(ConAccountMethodGroup conAccountMethodGroup);

    /**
     * 新增收付款方式组合
     * 
     * @param conAccountMethodGroup 收付款方式组合
     * @return 结果
     */
    public int insertConAccountMethodGroup(ConAccountMethodGroup conAccountMethodGroup);

    /**
     * 修改收付款方式组合
     * 
     * @param conAccountMethodGroup 收付款方式组合
     * @return 结果
     */
    public int updateConAccountMethodGroup(ConAccountMethodGroup conAccountMethodGroup);

    /**
     * 变更收付款方式组合
     *
     * @param conAccountMethodGroup 收付款方式组合
     * @return 结果
     */
    public int changeConAccountMethodGroup(ConAccountMethodGroup conAccountMethodGroup);

    /**
     * 批量删除收付款方式组合
     * 
     * @param sids 需要删除的收付款方式组合ID
     * @return 结果
     */
    public int deleteConAccountMethodGroupByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conAccountMethodGroup
    * @return
    */
    int changeStatus(ConAccountMethodGroup conAccountMethodGroup);

    /**
     * 更改确认状态
     * @param conAccountMethodGroup
     * @return
     */
    int check(ConAccountMethodGroup conAccountMethodGroup);

}
