package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeDeliveryNote;

/**
 * 业务类型_采购交货单/销售发货单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeDeliveryNoteService extends IService<ConBuTypeDeliveryNote> {
    /**
     * 查询业务类型_采购交货单/销售发货单
     *
     * @param sid 业务类型_采购交货单/销售发货单ID
     * @return 业务类型_采购交货单/销售发货单
     */
    public ConBuTypeDeliveryNote selectConBuTypeDeliveryNoteById(Long sid);

    /**
     * 查询业务类型_采购交货单/销售发货单列表
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 业务类型_采购交货单/销售发货单集合
     */
    public List<ConBuTypeDeliveryNote> selectConBuTypeDeliveryNoteList(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 新增业务类型_采购交货单/销售发货单
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 结果
     */
    public int insertConBuTypeDeliveryNote(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 修改业务类型_采购交货单/销售发货单
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 结果
     */
    public int updateConBuTypeDeliveryNote(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 变更业务类型_采购交货单/销售发货单
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 结果
     */
    public int changeConBuTypeDeliveryNote(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 批量删除业务类型_采购交货单/销售发货单
     *
     * @param sids 需要删除的业务类型_采购交货单/销售发货单ID
     * @return 结果
     */
    public int deleteConBuTypeDeliveryNoteByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeDeliveryNote
     * @return
     */
    int changeStatus(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 更改确认状态
     *
     * @param conBuTypeDeliveryNote
     * @return
     */
    int check(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 业务类型_采购交货单/销售发货单下拉列表
     */
    List<ConBuTypeDeliveryNote> getList(ConBuTypeDeliveryNote conBuTypeDeliveryNote);
}
