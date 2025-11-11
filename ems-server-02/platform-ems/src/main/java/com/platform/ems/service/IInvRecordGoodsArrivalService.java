package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvRecordGoodsArrival;
import com.platform.ems.domain.dto.request.InvRecordGoodsArrivalRequest;
import com.platform.ems.domain.dto.response.InvRecordGoodsArrivalResponse;

/**
 * 采购到货台账Service接口
 * 
 * @author linhongwei
 * @date 2022-06-27
 */
public interface IInvRecordGoodsArrivalService extends IService<InvRecordGoodsArrival>{
    /**
     * 查询采购到货台账
     * 
     * @param goodsArrivalSid 采购到货台账ID
     * @return 采购到货台账
     */
    public InvRecordGoodsArrival selectInvRecordGoodsArrivalById(Long goodsArrivalSid);

    /**
     * 查询采购到货台账列表
     * 
     * @param invRecordGoodsArrival 采购到货台账
     * @return 采购到货台账集合
     */
    public List<InvRecordGoodsArrival> selectInvRecordGoodsArrivalList(InvRecordGoodsArrival invRecordGoodsArrival);
    /**
     * 查询采购到货台账 明细报表
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 采购到货台账
     */
    public List<InvRecordGoodsArrivalResponse> getReport(InvRecordGoodsArrivalRequest invRecordGoodsArrival);
    /**
     * 新增采购到货台账
     * 
     * @param invRecordGoodsArrival 采购到货台账
     * @return 结果
     */
    public int insertInvRecordGoodsArrival(InvRecordGoodsArrival invRecordGoodsArrival);

    /**
     * 修改采购到货台账
     * 
     * @param invRecordGoodsArrival 采购到货台账
     * @return 结果
     */
    public int updateInvRecordGoodsArrival(InvRecordGoodsArrival invRecordGoodsArrival);

    /**
     * 变更采购到货台账
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 结果
     */
    public int changeInvRecordGoodsArrival(InvRecordGoodsArrival invRecordGoodsArrival);

    /**
     * 批量删除采购到货台账
     * 
     * @param goodsArrivalSids 需要删除的采购到货台账ID
     * @return 结果
     */
    public int deleteInvRecordGoodsArrivalByIds(List<Long>  goodsArrivalSids);

    /**
    * 启用/停用
    * @param invRecordGoodsArrival
    * @return
    */
    int changeStatus(InvRecordGoodsArrival invRecordGoodsArrival);

    /**
     * 更改确认状态
     * @param invRecordGoodsArrival
     * @return
     */
    int check(InvRecordGoodsArrival invRecordGoodsArrival);

    /**
     * 作废
     *
     * @param invRecordGoodsArrival
     * @return
     */
    public int invalid(InvRecordGoodsArrival invRecordGoodsArrival);

}
