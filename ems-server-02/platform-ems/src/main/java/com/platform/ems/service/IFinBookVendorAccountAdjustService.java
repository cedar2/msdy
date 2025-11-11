package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookVendorAccountAdjust;

/**
 * 财务流水账-供应商调账Service接口
 *
 * @author linhongwei
 * @date 2021-06-02
 */
public interface IFinBookVendorAccountAdjustService extends IService<FinBookVendorAccountAdjust> {

    /**
     * 查报表
     *
     * @param entity
     * @return
     */
    List<FinBookVendorAccountAdjust> getReportForm(FinBookVendorAccountAdjust entity);

}
