package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDepositArea;

/**
 * 投料区域Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConDepositAreaService extends IService<ConDepositArea>{
    /**
     * 查询投料区域
     * 
     * @param sid 投料区域ID
     * @return 投料区域
     */
    public ConDepositArea selectConDepositAreaById(Long sid);

    /**
     * 查询投料区域列表
     * 
     * @param conDepositArea 投料区域
     * @return 投料区域集合
     */
    public List<ConDepositArea> selectConDepositAreaList(ConDepositArea conDepositArea);

    /**
     * 新增投料区域
     * 
     * @param conDepositArea 投料区域
     * @return 结果
     */
    public int insertConDepositArea(ConDepositArea conDepositArea);

    /**
     * 修改投料区域
     * 
     * @param conDepositArea 投料区域
     * @return 结果
     */
    public int updateConDepositArea(ConDepositArea conDepositArea);

    /**
     * 变更投料区域
     *
     * @param conDepositArea 投料区域
     * @return 结果
     */
    public int changeConDepositArea(ConDepositArea conDepositArea);

    /**
     * 批量删除投料区域
     * 
     * @param sids 需要删除的投料区域ID
     * @return 结果
     */
    public int deleteConDepositAreaByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conDepositArea
    * @return
    */
    int changeStatus(ConDepositArea conDepositArea);

    /**
     * 更改确认状态
     * @param conDepositArea
     * @return
     */
    int check(ConDepositArea conDepositArea);

}
