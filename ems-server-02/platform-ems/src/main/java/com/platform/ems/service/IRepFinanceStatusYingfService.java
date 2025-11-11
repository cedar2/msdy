package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepFinanceStatusYingf;

/**
 * 财务状况-供应商-应付Service接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface IRepFinanceStatusYingfService extends IService<RepFinanceStatusYingf> {
    /**
     * 查询财务状况-供应商-应付
     *
     * @param dataRecordSid 财务状况-供应商-应付ID
     * @return 财务状况-供应商-应付
     */
    public RepFinanceStatusYingf selectRepFinanceStatusYingfById(Long dataRecordSid);

    /**
     * 查询财务状况-供应商-应付列表
     *
     * @param repFinanceStatusYingf 财务状况-供应商-应付
     * @return 财务状况-供应商-应付集合
     */
    public List<RepFinanceStatusYingf> selectRepFinanceStatusYingfList(RepFinanceStatusYingf repFinanceStatusYingf);

    /**
     * 新增财务状况-供应商-应付
     *
     * @param repFinanceStatusYingf 财务状况-供应商-应付
     * @return 结果
     */
    public int insertRepFinanceStatusYingf(RepFinanceStatusYingf repFinanceStatusYingf);

    /**
     * 批量删除财务状况-供应商-应付
     *
     * @param dataRecordSids 需要删除的财务状况-供应商-应付ID
     * @return 结果
     */
    public int deleteRepFinanceStatusYingfByIds(List<Long> dataRecordSids);

}
