package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatusYings;

/**
 * 财务状况-客户-应收Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusYingsService extends IService<RepFinanceStatusYings> {
    /**
     * 查询财务状况-客户-应收
     *
     * @param dataRecordSid 财务状况-客户-应收ID
     * @return 财务状况-客户-应收
     */
    public RepFinanceStatusYings selectRepFinanceStatusYingsById(Long dataRecordSid);

    /**
     * 查询财务状况-客户-应收列表
     *
     * @param repFinanceStatusYings 财务状况-客户-应收
     * @return 财务状况-客户-应收集合
     */
    public List<RepFinanceStatusYings> selectRepFinanceStatusYingsList(RepFinanceStatusYings repFinanceStatusYings);

    /**
     * 新增财务状况-客户-应收
     *
     * @param repFinanceStatusYings 财务状况-客户-应收
     * @return 结果
     */
    public int insertRepFinanceStatusYings(RepFinanceStatusYings repFinanceStatusYings);

    /**
     * 批量删除财务状况-客户-应收
     *
     * @param dataRecordSids 需要删除的财务状况-客户-应收ID
     * @return 结果
     */
    public int deleteRepFinanceStatusYingsByIds(List<Long> dataRecordSids);

}
