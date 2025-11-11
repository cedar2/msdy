package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocBuTypeGroupSo;

/**
 * 销售订单单据类型与业务类型组合关系Service接口
 * 
 * @author chenkw
 * @date 2021-12-24
 */
public interface IConDocBuTypeGroupSoService extends IService<ConDocBuTypeGroupSo>{
    /**
     * 查询销售订单单据类型与业务类型组合关系
     * 
     * @param sid 销售订单单据类型与业务类型组合关系ID
     * @return 销售订单单据类型与业务类型组合关系
     */
    public ConDocBuTypeGroupSo selectConDocBuTypeGroupSoById(Long sid);

    /**
     * 查询销售订单单据类型与业务类型组合关系列表
     * 
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 销售订单单据类型与业务类型组合关系集合
     */
    public List<ConDocBuTypeGroupSo> selectConDocBuTypeGroupSoList(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

    /**
     * 新增销售订单单据类型与业务类型组合关系
     * 
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 结果
     */
    public int insertConDocBuTypeGroupSo(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

    /**
     * 修改销售订单单据类型与业务类型组合关系
     * 
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 结果
     */
    public int updateConDocBuTypeGroupSo(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

    /**
     * 变更销售订单单据类型与业务类型组合关系
     *
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 结果
     */
    public int changeConDocBuTypeGroupSo(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

    /**
     * 批量删除销售订单单据类型与业务类型组合关系
     * 
     * @param sids 需要删除的销售订单单据类型与业务类型组合关系ID
     * @return 结果
     */
    public int deleteConDocBuTypeGroupSoByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocBuTypeGroupSo
    * @return
    */
    int changeStatus(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

    /**
     * 更改确认状态
     * @param conDocBuTypeGroupSo
     * @return
     */
    int check(ConDocBuTypeGroupSo conDocBuTypeGroupSo);

}
