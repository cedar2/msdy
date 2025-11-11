package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatusDaiys;

/**
 * 财务状况-客户-待预收Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusDaiysService extends IService<RepFinanceStatusDaiys> {
    /**
     * 查询财务状况-客户-待预收
     *
     * @param dataRecordSid 财务状况-客户-待预收ID
     * @return 财务状况-客户-待预收
     */
    public RepFinanceStatusDaiys selectRepFinanceStatusDaiysById(Long dataRecordSid);

    /**
     * 查询财务状况-客户-待预收列表
     *
     * @param repFinanceStatusDaiys 财务状况-客户-待预收
     * @return 财务状况-客户-待预收集合
     */
    public List<RepFinanceStatusDaiys> selectRepFinanceStatusDaiysList(RepFinanceStatusDaiys repFinanceStatusDaiys);

    /**
     * 新增财务状况-客户-待预收
     *
     * @param repFinanceStatusDaiys 财务状况-客户-待预收
     * @return 结果
     */
    public int insertRepFinanceStatusDaiys(RepFinanceStatusDaiys repFinanceStatusDaiys);

    /**
     * 批量删除财务状况-客户-待预收
     *
     * @param dataRecordSids 需要删除的财务状况-客户-待预收ID
     * @return 结果
     */
    public int deleteRepFinanceStatusDaiysByIds(List<Long> dataRecordSids);

}
