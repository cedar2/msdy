package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookVendorAccountAdjust;
import com.platform.ems.mapper.FinBookVendorAccountAdjustMapper;
import com.platform.ems.service.IFinBookVendorAccountAdjustService;

/**
 * 财务流水账-供应商调账Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@Service
@SuppressWarnings("all")
public class FinBookVendorAccountAdjustServiceImpl extends ServiceImpl<FinBookVendorAccountAdjustMapper, FinBookVendorAccountAdjust> implements IFinBookVendorAccountAdjustService {
    @Autowired
    private FinBookVendorAccountAdjustMapper finBookVendorAccountAdjustMapper;

    /**
     * 查报表
     *
     * @param entity
     * @return
     */
    public List<FinBookVendorAccountAdjust> getReportForm(FinBookVendorAccountAdjust entity) {
        List<FinBookVendorAccountAdjust> responseList = finBookVendorAccountAdjustMapper.getReportForm(entity);
        return responseList;
    }
}
