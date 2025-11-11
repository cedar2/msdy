package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookVendorAccountBalance;

/**
 * 财务流水账-供应商账互抵Service接口
 *
 * @author linhongwei
 * @date 2021-06-18
 */
public interface IFinBookVendorAccountBalanceService extends IService<FinBookVendorAccountBalance>{

    /**
     * 查流水
     * @param entity
     * @return
     */
    List<FinBookVendorAccountBalance> getReportForm(FinBookVendorAccountBalance entity);

}
