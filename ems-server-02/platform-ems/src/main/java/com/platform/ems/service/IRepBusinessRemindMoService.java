package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepBusinessRemindMo;

/**
 * 已逾期/即将到期-生产订单Service接口
 *
 * @author chenkw
 * @date 2022-04-26
 */
public interface IRepBusinessRemindMoService extends IService<RepBusinessRemindMo> {
    /**
     * 查询已逾期/即将到期-生产订单
     *
     * @param dataRecordSid 已逾期/即将到期-生产订单ID
     * @return 已逾期/即将到期-生产订单
     */
    public RepBusinessRemindMo selectRepBusinessRemindMoById(Long dataRecordSid);

    /**
     * 查询已逾期/即将到期-生产订单列表
     *
     * @param repBusinessRemindMo 已逾期/即将到期-生产订单
     * @return 已逾期/即将到期-生产订单集合
     */
    public List<RepBusinessRemindMo> selectRepBusinessRemindMoList(RepBusinessRemindMo repBusinessRemindMo);

    /**
     * 查询已逾期/即将到期生产订单统计报表
     *
     * @param repBusinessRemindMo 已逾期/即将到期生产订单
     * @return 已逾期/即将到期生产订单集合
     */
    public List<RepBusinessRemindMo> getCountForm(RepBusinessRemindMo repBusinessRemindMo);

    /**
     * 新增已逾期/即将到期-生产订单
     *
     * @param repBusinessRemindMo 已逾期/即将到期-生产订单
     * @return 结果
     */
    public int insertRepBusinessRemindMo(RepBusinessRemindMo repBusinessRemindMo);

    /**
     * 批量删除已逾期/即将到期-生产订单
     *
     * @param dataRecordSids 需要删除的已逾期/即将到期-生产订单ID
     * @return 结果
     */
    public int deleteRepBusinessRemindMoByIds(List<Long> dataRecordSids);

}
