package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.DelDeliveryNote;
import com.platform.ems.domain.DelDeliveryNoteItem;
import com.platform.ems.domain.InvInventoryDocument;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.dto.request.DelDeliveryNoteCreateRequest;
import com.platform.ems.domain.dto.response.DelDeliveryNoteOutResponse;

import java.util.List;

/**
 * 交货单Service接口
 *
 * @author linhongwei
 * @date 2021-04-21
 */
public interface IDelDeliveryNoteService extends IService<DelDeliveryNote>{
    /**
     * 查询交货单
     *
     * @param deliveryNoteSid 交货单ID
     * @return 交货单
     */
    public DelDeliveryNote selectDelDeliveryNoteById(Long deliveryNoteSid, String deliveryType);
    //交货单打印
    public DelDeliveryNote getPrint(Long[] sids);
    /**
     * 生成二维码
     */
    public List<DelDeliveryNote> getQr(List<DelDeliveryNote> list);
    public List<DelDeliveryNoteItem> sort(List<DelDeliveryNoteItem> orderItemList,String type);
    /**
     * 查询交货单列表
     *
     * @param delDeliveryNote 交货单
     * @return 交货单集合
     */
    public List<DelDeliveryNote> selectDelDeliveryNoteList(DelDeliveryNote delDeliveryNote);
    /**
     * 根据单号获取对应的商品编码
     */
    public List<String> getMaterialCode(Long code,String type);
    //采购交货单-创建销售订单
    public SalSalesOrder createSaleOrder(DelDeliveryNoteCreateRequest request);
    //采购交货单-创建常特转移单
    public InvInventoryDocument createInvSpec(List<Long> sidList);

    /**
     * 新增交货单
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    public int insertDelDeliveryNote(DelDeliveryNote delDeliveryNote);
    /**
     *
     * 外部系统获取交货单
     */
    public List<DelDeliveryNoteOutResponse> getOutDelDeliveryNote(Long sid);
    /**
     * 修改交货单
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    public int updateDelDeliveryNote(DelDeliveryNote delDeliveryNote);

    /**
     * 撤回保存前的校验
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    public int backSaveVerify(DelDeliveryNote delDeliveryNote);

    /**
     * 撤回保存
     *
     * @param delDeliveryNote 交货单
     * @return 结果
     */
    public int backSave(DelDeliveryNote delDeliveryNote);

    /**
     * 维护物流信息
     *
     * @param delDeliveryNote 订单
     * @return 结果
     */
    public int setCarrier(DelDeliveryNote delDeliveryNote);

    /**
     * 批量删除交货单
     *
     * @param deliveryNoteSids 需要删除的交货单ID
     * @return 结果
     */
    public int deleteDelDeliveryNoteByIds(Long[] deliveryNoteSids);

    public int processCheck(List<Long> sids);
    //出库
    public AjaxResult invCK(Long sid);
    //出库 按销售发货单（直发）
    public int invCkDirect(Long sid);
    /**
     * 交货单确认
     */
    int confirm(DelDeliveryNote delDeliveryNote);
    //明细报表生成预留库存
    public int reportCreateInv(List<Long> sids);
    //明细报表释放预留库存
    public int reportFreeInv(List<Long> sids);
    //冲销生成库存预留
    public  void xcCreateInv(Long sid);

    /**
     * 交货单变更
     */
    int change(DelDeliveryNote delDeliveryNote);
    /**
     * 校验“新订单量”与“订单已发货量”逻辑
     */
    public AjaxResult checkProcess(DelDeliveryNote delDeliveryNote);
}
