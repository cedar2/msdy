package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepBusinessRemindSo;
import com.platform.ems.domain.dto.response.RepBusinessRemindRepResponse;

/**
 * 已逾期/即将到期-销售订单Service接口
 *
 * @author linhongwei
 * @date 2022-02-24
 */
public interface IRepBusinessRemindSoService extends IService<RepBusinessRemindSo> {
    /**
     * 查询已逾期/即将到期-销售订单
     *
     * @param dataRecordSid 已逾期/即将到期-销售订单ID
     * @return 已逾期/即将到期-销售订单
     */
    public RepBusinessRemindSo selectRepBusinessRemindSoById(Long dataRecordSid);

    /**
     * 查询已逾期/即将到期-销售订单列表
     *
     * @param repBusinessRemindSo 已逾期/即将到期-销售订单
     * @return 已逾期/即将到期-销售订单集合
     */
    public List<RepBusinessRemindSo> selectRepBusinessRemindSoList(RepBusinessRemindSo repBusinessRemindSo);

    /**
     * 查询已逾期-销售订单报表
     */
    public List<RepBusinessRemindSo> yyqReport(RepBusinessRemindSo repBusinessRemindSo);

    /**
     * 查询已逾期或即将到期报表明细
     */
    public List<RepBusinessRemindSo> reportItem(RepBusinessRemindSo repBusinessRemindSo);

    public List<RepBusinessRemindSo> sort(List<RepBusinessRemindSo> salSalesOrderItemList);
    public List<RepBusinessRemindRepResponse> getReport();

    /**
     * 新增已逾期/即将到期-销售订单
     *
     * @param repBusinessRemindSo 已逾期/即将到期-销售订单
     * @return 结果
     */
    public int insertRepBusinessRemindSo(RepBusinessRemindSo repBusinessRemindSo);

    /**
     * 批量删除已逾期/即将到期-销售订单
     *
     * @param dataRecordSids 需要删除的已逾期/即将到期-销售订单ID
     * @return 结果
     */
    public int deleteRepBusinessRemindSoByIds(List<Long> dataRecordSids);

}
