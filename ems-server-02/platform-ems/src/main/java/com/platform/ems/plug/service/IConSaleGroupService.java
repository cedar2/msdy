package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConSaleGroup;
import com.platform.ems.plug.domain.ConSaleOrg;

/**
 * 销售组Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConSaleGroupService extends IService<ConSaleGroup>{
    /**
     * 查询销售组
     *
     * @param sid 销售组ID
     * @return 销售组
     */
    public ConSaleGroup selectConSaleGroupById(Long sid);

    /**
     * 查询销售组列表
     *
     * @param conSaleGroup 销售组
     * @return 销售组集合
     */
    public List<ConSaleGroup> selectConSaleGroupList(ConSaleGroup conSaleGroup);

    /**
     * 新增销售组
     *
     * @param conSaleGroup 销售组
     * @return 结果
     */
    public int insertConSaleGroup(ConSaleGroup conSaleGroup);

    /**
     * 修改销售组
     *
     * @param conSaleGroup 销售组
     * @return 结果
     */
    public int updateConSaleGroup(ConSaleGroup conSaleGroup);

    /**
     * 变更销售组
     *
     * @param conSaleGroup 销售组
     * @return 结果
     */
    public int changeConSaleGroup(ConSaleGroup conSaleGroup);

    /**
     * 批量删除销售组
     *
     * @param sids 需要删除的销售组ID
     * @return 结果
     */
    public int deleteConSaleGroupByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conSaleGroup
    * @return
    */
    int changeStatus(ConSaleGroup conSaleGroup);

    /**
     * 更改确认状态
     * @param conSaleGroup
     * @return
     */
    int check(ConSaleGroup conSaleGroup);

    /**  获取下拉列表 */
    List<ConSaleGroup> getConSaleGroupList();
}
