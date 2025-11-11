package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocBuTypeGroupPo;

/**
 * 采购订单单据类型与业务类型组合关系Service接口
 * 
 * @author chenkw
 * @date 2021-12-24
 */
public interface IConDocBuTypeGroupPoService extends IService<ConDocBuTypeGroupPo>{
    /**
     * 查询采购订单单据类型与业务类型组合关系
     * 
     * @param sid 采购订单单据类型与业务类型组合关系ID
     * @return 采购订单单据类型与业务类型组合关系
     */
    public ConDocBuTypeGroupPo selectConDocBuTypeGroupPoById(Long sid);

    /**
     * 查询采购订单单据类型与业务类型组合关系列表
     * 
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 采购订单单据类型与业务类型组合关系集合
     */
    public List<ConDocBuTypeGroupPo> selectConDocBuTypeGroupPoList(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

    /**
     * 新增采购订单单据类型与业务类型组合关系
     * 
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 结果
     */
    public int insertConDocBuTypeGroupPo(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

    /**
     * 修改采购订单单据类型与业务类型组合关系
     * 
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 结果
     */
    public int updateConDocBuTypeGroupPo(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

    /**
     * 变更采购订单单据类型与业务类型组合关系
     *
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 结果
     */
    public int changeConDocBuTypeGroupPo(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

    /**
     * 批量删除采购订单单据类型与业务类型组合关系
     * 
     * @param sids 需要删除的采购订单单据类型与业务类型组合关系ID
     * @return 结果
     */
    public int deleteConDocBuTypeGroupPoByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocBuTypeGroupPo
    * @return
    */
    int changeStatus(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

    /**
     * 更改确认状态
     * @param conDocBuTypeGroupPo
     * @return
     */
    int check(ConDocBuTypeGroupPo conDocBuTypeGroupPo);

}
