package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatusDaiyf;

/**
 * 财务状况-供应商-待预付Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusDaiyfService extends IService<RepFinanceStatusDaiyf> {
    /**
     * 查询财务状况-供应商-待预付
     *
     * @param dataRecordSid 财务状况-供应商-待预付ID
     * @return 财务状况-供应商-待预付
     */
    public RepFinanceStatusDaiyf selectRepFinanceStatusDaiyfById(Long dataRecordSid);

    /**
     * 查询财务状况-供应商-待预付列表
     *
     * @param repFinanceStatusDaiyf 财务状况-供应商-待预付
     * @return 财务状况-供应商-待预付集合
     */
    public List<RepFinanceStatusDaiyf> selectRepFinanceStatusDaiyfList(RepFinanceStatusDaiyf repFinanceStatusDaiyf);

    /**
     * 新增财务状况-供应商-待预付
     *
     * @param repFinanceStatusDaiyf 财务状况-供应商-待预付
     * @return 结果
     */
    public int insertRepFinanceStatusDaiyf(RepFinanceStatusDaiyf repFinanceStatusDaiyf);

    /**
     * 批量删除财务状况-供应商-待预付
     *
     * @param dataRecordSids 需要删除的财务状况-供应商-待预付ID
     * @return 结果
     */
    public int deleteRepFinanceStatusDaiyfByIds(List<Long> dataRecordSids);

}
