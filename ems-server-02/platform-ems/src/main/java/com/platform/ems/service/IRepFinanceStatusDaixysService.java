package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatusDaixys;

/**
 * 财务状况-客户-待销已收Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusDaixysService extends IService<RepFinanceStatusDaixys> {
    /**
     * 查询财务状况-客户-待销已收
     *
     * @param dataRecordSid 财务状况-客户-待销已收ID
     * @return 财务状况-客户-待销已收
     */
    public RepFinanceStatusDaixys selectRepFinanceStatusDaixysById(Long dataRecordSid);

    /**
     * 查询财务状况-客户-待销已收列表
     *
     * @param repFinanceStatusDaixys 财务状况-客户-待销已收
     * @return 财务状况-客户-待销已收集合
     */
    public List<RepFinanceStatusDaixys> selectRepFinanceStatusDaixysList(RepFinanceStatusDaixys repFinanceStatusDaixys);

    /**
     * 新增财务状况-客户-待销已收
     *
     * @param repFinanceStatusDaixys 财务状况-客户-待销已收
     * @return 结果
     */
    public int insertRepFinanceStatusDaixys(RepFinanceStatusDaixys repFinanceStatusDaixys);

    /**
     * 批量删除财务状况-客户-待销已收
     *
     * @param dataRecordSids 需要删除的财务状况-客户-待销已收ID
     * @return 结果
     */
    public int deleteRepFinanceStatusDaixysByIds(List<Long> dataRecordSids);

}
