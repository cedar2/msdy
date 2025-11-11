package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeDeliveryNote;

/**
 * 单据类型_采购交货单/销售发货单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeDeliveryNoteService extends IService<ConDocTypeDeliveryNote>{
    /**
     * 查询单据类型_采购交货单/销售发货单
     * 
     * @param sid 单据类型_采购交货单/销售发货单ID
     * @return 单据类型_采购交货单/销售发货单
     */
    public ConDocTypeDeliveryNote selectConDocTypeDeliveryNoteById(Long sid);

    /**
     * 查询单据类型_采购交货单/销售发货单列表
     * 
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 单据类型_采购交货单/销售发货单集合
     */
    public List<ConDocTypeDeliveryNote> selectConDocTypeDeliveryNoteList(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 新增单据类型_采购交货单/销售发货单
     * 
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 结果
     */
    public int insertConDocTypeDeliveryNote(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 修改单据类型_采购交货单/销售发货单
     * 
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 结果
     */
    public int updateConDocTypeDeliveryNote(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 变更单据类型_采购交货单/销售发货单
     *
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 结果
     */
    public int changeConDocTypeDeliveryNote(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 批量删除单据类型_采购交货单/销售发货单
     * 
     * @param sids 需要删除的单据类型_采购交货单/销售发货单ID
     * @return 结果
     */
    public int deleteConDocTypeDeliveryNoteByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeDeliveryNote
    * @return
    */
    int changeStatus(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 更改确认状态
     * @param conDocTypeDeliveryNote
     * @return
     */
    int check(ConDocTypeDeliveryNote conDocTypeDeliveryNote);

    /**
     * 单据类型_采购交货单/销售发货单下拉列表
     */
    List<ConDocTypeDeliveryNote> getList(ConDocTypeDeliveryNote conDocTypeDeliveryNote);
}
