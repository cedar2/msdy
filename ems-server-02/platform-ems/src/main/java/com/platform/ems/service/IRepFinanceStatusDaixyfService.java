package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatusDaixyf;

/**
 * 财务状况-供应商-待销已付Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusDaixyfService extends IService<RepFinanceStatusDaixyf> {
    /**
     * 查询财务状况-供应商-待销已付
     *
     * @param dataRecordSid 财务状况-供应商-待销已付ID
     * @return 财务状况-供应商-待销已付
     */
    public RepFinanceStatusDaixyf selectRepFinanceStatusDaixyfById(Long dataRecordSid);

    /**
     * 查询财务状况-供应商-待销已付列表
     *
     * @param repFinanceStatusDaixyf 财务状况-供应商-待销已付
     * @return 财务状况-供应商-待销已付集合
     */
    public List<RepFinanceStatusDaixyf> selectRepFinanceStatusDaixyfList(RepFinanceStatusDaixyf repFinanceStatusDaixyf);

    /**
     * 新增财务状况-供应商-待销已付
     *
     * @param repFinanceStatusDaixyf 财务状况-供应商-待销已付
     * @return 结果
     */
    public int insertRepFinanceStatusDaixyf(RepFinanceStatusDaixyf repFinanceStatusDaixyf);

    /**
     * 批量删除财务状况-供应商-待销已付
     *
     * @param dataRecordSids 需要删除的财务状况-供应商-待销已付ID
     * @return 结果
     */
    public int deleteRepFinanceStatusDaixyfByIds(List<Long> dataRecordSids);

}
