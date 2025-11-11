package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConShipmentMode;

/**
 * 配送方式Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConShipmentModeService extends IService<ConShipmentMode>{
    /**
     * 查询配送方式
     * 
     * @param sid 配送方式ID
     * @return 配送方式
     */
    public ConShipmentMode selectConShipmentModeById(Long sid);

    /**
     * 查询配送方式列表
     * 
     * @param conShipmentMode 配送方式
     * @return 配送方式集合
     */
    public List<ConShipmentMode> selectConShipmentModeList(ConShipmentMode conShipmentMode);

    /**
     * 新增配送方式
     * 
     * @param conShipmentMode 配送方式
     * @return 结果
     */
    public int insertConShipmentMode(ConShipmentMode conShipmentMode);

    /**
     * 修改配送方式
     * 
     * @param conShipmentMode 配送方式
     * @return 结果
     */
    public int updateConShipmentMode(ConShipmentMode conShipmentMode);

    /**
     * 变更配送方式
     *
     * @param conShipmentMode 配送方式
     * @return 结果
     */
    public int changeConShipmentMode(ConShipmentMode conShipmentMode);

    /**
     * 批量删除配送方式
     * 
     * @param sids 需要删除的配送方式ID
     * @return 结果
     */
    public int deleteConShipmentModeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conShipmentMode
    * @return
    */
    int changeStatus(ConShipmentMode conShipmentMode);

    /**
     * 更改确认状态
     * @param conShipmentMode
     * @return
     */
    int check(ConShipmentMode conShipmentMode);

    /**
     * 配送方式下拉框
     */
    List<ConShipmentMode> getList();
}
