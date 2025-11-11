package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeInventoryAdjust;

/**
 * 单据类型_库存调整单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeInventoryAdjustService extends IService<ConDocTypeInventoryAdjust>{
    /**
     * 查询单据类型_库存调整单
     * 
     * @param sid 单据类型_库存调整单ID
     * @return 单据类型_库存调整单
     */
    public ConDocTypeInventoryAdjust selectConDocTypeInventoryAdjustById(Long sid);

    /**
     * 查询单据类型_库存调整单列表
     * 
     * @param conDocTypeInventoryAdjust 单据类型_库存调整单
     * @return 单据类型_库存调整单集合
     */
    public List<ConDocTypeInventoryAdjust> selectConDocTypeInventoryAdjustList(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

    /**
     * 新增单据类型_库存调整单
     * 
     * @param conDocTypeInventoryAdjust 单据类型_库存调整单
     * @return 结果
     */
    public int insertConDocTypeInventoryAdjust(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

    /**
     * 修改单据类型_库存调整单
     * 
     * @param conDocTypeInventoryAdjust 单据类型_库存调整单
     * @return 结果
     */
    public int updateConDocTypeInventoryAdjust(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

    /**
     * 变更单据类型_库存调整单
     *
     * @param conDocTypeInventoryAdjust 单据类型_库存调整单
     * @return 结果
     */
    public int changeConDocTypeInventoryAdjust(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

    /**
     * 批量删除单据类型_库存调整单
     * 
     * @param sids 需要删除的单据类型_库存调整单ID
     * @return 结果
     */
    public int deleteConDocTypeInventoryAdjustByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeInventoryAdjust
    * @return
    */
    int changeStatus(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

    /**
     * 更改确认状态
     * @param conDocTypeInventoryAdjust
     * @return
     */
    int check(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

}
