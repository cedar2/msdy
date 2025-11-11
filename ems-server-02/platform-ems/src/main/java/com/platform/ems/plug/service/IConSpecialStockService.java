package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConReasonTypeStorage;
import com.platform.ems.plug.domain.ConSpecialStock;

/**
 * 特殊库存Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConSpecialStockService extends IService<ConSpecialStock>{
    /**
     * 查询特殊库存
     * 
     * @param sid 特殊库存ID
     * @return 特殊库存
     */
    public ConSpecialStock selectConSpecialStockById(Long sid);
    public List<ConSpecialStock> getList();
    /**
     * 查询特殊库存列表
     * 
     * @param conSpecialStock 特殊库存
     * @return 特殊库存集合
     */
    public List<ConSpecialStock> selectConSpecialStockList(ConSpecialStock conSpecialStock);

    /**
     * 新增特殊库存
     * 
     * @param conSpecialStock 特殊库存
     * @return 结果
     */
    public int insertConSpecialStock(ConSpecialStock conSpecialStock);

    /**
     * 修改特殊库存
     * 
     * @param conSpecialStock 特殊库存
     * @return 结果
     */
    public int updateConSpecialStock(ConSpecialStock conSpecialStock);

    /**
     * 变更特殊库存
     *
     * @param conSpecialStock 特殊库存
     * @return 结果
     */
    public int changeConSpecialStock(ConSpecialStock conSpecialStock);

    /**
     * 批量删除特殊库存
     * 
     * @param sids 需要删除的特殊库存ID
     * @return 结果
     */
    public int deleteConSpecialStockByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conSpecialStock
    * @return
    */
    int changeStatus(ConSpecialStock conSpecialStock);

    /**
     * 更改确认状态
     * @param conSpecialStock
     * @return
     */
    int check(ConSpecialStock conSpecialStock);

}
