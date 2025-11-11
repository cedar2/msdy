package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepBusinessRemindPo;
import com.platform.ems.domain.dto.response.RepBusinessRemindRepResponse;

/**
 * 已逾期/即将到期-采购订单Service接口
 *
 * @author linhongwei
 * @date 2022-02-24
 */
public interface IRepBusinessRemindPoService extends IService<RepBusinessRemindPo> {
    /**
     * 查询已逾期/即将到期-采购订单
     *
     * @param dataRecordSid 已逾期/即将到期-采购订单ID
     * @return 已逾期/即将到期-采购订单
     */
    public RepBusinessRemindPo selectRepBusinessRemindPoById(Long dataRecordSid);

    /**
     * 查询已逾期/即将到期-采购订单列表
     *
     * @param repBusinessRemindPo 已逾期/即将到期-采购订单
     * @return 已逾期/即将到期-采购订单集合
     */
    public List<RepBusinessRemindPo> selectRepBusinessRemindPoList(RepBusinessRemindPo repBusinessRemindPo);

    public List<RepBusinessRemindRepResponse> getReport();
    public List<RepBusinessRemindPo> getYYQHead(RepBusinessRemindPo repBusinessRemindPo);
    public List<RepBusinessRemindPo> getYYQItem(RepBusinessRemindPo repBusinessRemindPo);
    public List<RepBusinessRemindPo> sort(List<RepBusinessRemindPo> salSalesOrderItemList);
    /**
     * 新增已逾期/即将到期-采购订单
     *
     * @param repBusinessRemindPo 已逾期/即将到期-采购订单
     * @return 结果
     */
    public int insertRepBusinessRemindPo(RepBusinessRemindPo repBusinessRemindPo);

    /**
     * 批量删除已逾期/即将到期-采购订单
     *
     * @param dataRecordSids 需要删除的已逾期/即将到期-采购订单ID
     * @return 结果
     */
    public int deleteRepBusinessRemindPoByIds(List<Long> dataRecordSids);

}
