package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.plug.domain.ConBuTypeInventoryAdjust;

/**
 * 业务类型_库存调整单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeInventoryAdjustService extends IService<ConBuTypeInventoryAdjust>{
    /**
     * 查询业务类型_库存调整单
     *
     * @param sid 业务类型_库存调整单ID
     * @return 业务类型_库存调整单
     */
    public ConBuTypeInventoryAdjust selectConBuTypeInventoryAdjustById(Long sid);
    public List<ConBuTypeInventoryAdjust> getList();
    /**
     * 查询业务类型_库存调整单列表
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 业务类型_库存调整单集合
     */
    public List<ConBuTypeInventoryAdjust> selectConBuTypeInventoryAdjustList(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);

    /**
     * 新增业务类型_库存调整单
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 结果
     */
    public int insertConBuTypeInventoryAdjust(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);

    /**
     * 修改业务类型_库存调整单
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 结果
     */
    public int updateConBuTypeInventoryAdjust(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);

    /**
     * 变更业务类型_库存调整单
     *
     * @param conBuTypeInventoryAdjust 业务类型_库存调整单
     * @return 结果
     */
    public int changeConBuTypeInventoryAdjust(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);

    /**
     * 批量删除业务类型_库存调整单
     *
     * @param sids 需要删除的业务类型_库存调整单ID
     * @return 结果
     */
    public int deleteConBuTypeInventoryAdjustByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeInventoryAdjust
    * @return
    */
    int changeStatus(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);

    /**
     * 更改确认状态
     * @param conBuTypeInventoryAdjust
     * @return
     */
    int check(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);

}
