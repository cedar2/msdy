package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeInventoryTransfer;

/**
 * 单据类型_调拨单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeInventoryTransferService extends IService<ConDocTypeInventoryTransfer>{
    /**
     * 查询单据类型_调拨单
     * 
     * @param sid 单据类型_调拨单ID
     * @return 单据类型_调拨单
     */
    public ConDocTypeInventoryTransfer selectConDocTypeInventoryTransferById(Long sid);

    /**
     * 查询单据类型_调拨单列表
     * 
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 单据类型_调拨单集合
     */
    public List<ConDocTypeInventoryTransfer> selectConDocTypeInventoryTransferList(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);

    /**
     * 新增单据类型_调拨单
     * 
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 结果
     */
    public int insertConDocTypeInventoryTransfer(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);

    /**
     * 修改单据类型_调拨单
     * 
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 结果
     */
    public int updateConDocTypeInventoryTransfer(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);

    /**
     * 变更单据类型_调拨单
     *
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 结果
     */
    public int changeConDocTypeInventoryTransfer(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);

    /**
     * 批量删除单据类型_调拨单
     * 
     * @param sids 需要删除的单据类型_调拨单ID
     * @return 结果
     */
    public int deleteConDocTypeInventoryTransferByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeInventoryTransfer
    * @return
    */
    int changeStatus(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);

    /**
     * 更改确认状态
     * @param conDocTypeInventoryTransfer
     * @return
     */
    int check(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);

}
