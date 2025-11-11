package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepPurchaseStatusOutsourceProcess;

/**
 * 采购状况-外发加工结算Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepPurchaseStatusOutsourceProcessService extends IService<RepPurchaseStatusOutsourceProcess> {
    /**
     * 查询采购状况-外发加工结算
     *
     * @param dataRecordSid 采购状况-外发加工结算ID
     * @return 采购状况-外发加工结算
     */
    public RepPurchaseStatusOutsourceProcess selectRepPurchaseStatusOutsourceProcessById(Long dataRecordSid);

    /**
     * 查询采购状况-外发加工结算列表
     *
     * @param repPurchaseStatusOutsourceProcess 采购状况-外发加工结算
     * @return 采购状况-外发加工结算集合
     */
    public List<RepPurchaseStatusOutsourceProcess> selectRepPurchaseStatusOutsourceProcessList(RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess);

    /**
     * 新增采购状况-外发加工结算
     *
     * @param repPurchaseStatusOutsourceProcess 采购状况-外发加工结算
     * @return 结果
     */
    public int insertRepPurchaseStatusOutsourceProcess(RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess);

    /**
     * 批量删除采购状况-外发加工结算
     *
     * @param dataRecordSids 需要删除的采购状况-外发加工结算ID
     * @return 结果
     */
    public int deleteRepPurchaseStatusOutsourceProcessByIds(List<Long> dataRecordSids);

}
