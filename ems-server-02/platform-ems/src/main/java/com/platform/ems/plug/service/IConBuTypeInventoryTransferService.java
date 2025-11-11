package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.plug.domain.ConBuTypeInventoryTransfer;

/**
 * 业务类型_调拨单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeInventoryTransferService extends IService<ConBuTypeInventoryTransfer>{
    /**
     * 查询业务类型_调拨单
     *
     * @param sid 业务类型_调拨单ID
     * @return 业务类型_调拨单
     */
    public ConBuTypeInventoryTransfer selectConBuTypeInventoryTransferById(Long sid);
    public List<ConBuTypeInventoryTransfer> getList();
    /**
     * 查询业务类型_调拨单列表
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 业务类型_调拨单集合
     */
    public List<ConBuTypeInventoryTransfer> selectConBuTypeInventoryTransferList(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);

    /**
     * 新增业务类型_调拨单
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 结果
     */
    public int insertConBuTypeInventoryTransfer(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);

    /**
     * 修改业务类型_调拨单
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 结果
     */
    public int updateConBuTypeInventoryTransfer(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);

    /**
     * 变更业务类型_调拨单
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 结果
     */
    public int changeConBuTypeInventoryTransfer(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);

    /**
     * 批量删除业务类型_调拨单
     *
     * @param sids 需要删除的业务类型_调拨单ID
     * @return 结果
     */
    public int deleteConBuTypeInventoryTransferByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeInventoryTransfer
    * @return
    */
    int changeStatus(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);

    /**
     * 更改确认状态
     * @param conBuTypeInventoryTransfer
     * @return
     */
    int check(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);

}
