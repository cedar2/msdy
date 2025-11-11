package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatus;

/**
 * 财务状况Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusService extends IService<RepFinanceStatus> {
    /**
     * 查询财务状况
     *
     * @param dataRecordSid 财务状况ID
     * @return 财务状况
     */
    public RepFinanceStatus selectRepFinanceStatusById(Long dataRecordSid);

    /**
     * 查询财务状况列表
     *
     * @param repFinanceStatus 财务状况
     * @return 财务状况集合
     */
    public List<RepFinanceStatus> selectRepFinanceStatusList(RepFinanceStatus repFinanceStatus);

    /**
     * 新增财务状况
     *
     * @param repFinanceStatus 财务状况
     * @return 结果
     */
    public int insertRepFinanceStatus(RepFinanceStatus repFinanceStatus);

    /**
     * 批量删除财务状况
     *
     * @param dataRecordSids 需要删除的财务状况ID
     * @return 结果
     */
    public int deleteRepFinanceStatusByIds(List<Long> dataRecordSids);

}
