package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.dto.request.BasVendorAddrAddRequest;
import com.platform.ems.domain.dto.request.BasVendorAddrDeleteRequest;
import com.platform.ems.domain.dto.request.BasVendorAddrUpdateRequest;
import com.platform.ems.domain.dto.response.CustomerAddrResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.BasVendorAddrMapper;
import com.platform.ems.domain.BasVendorAddr;
import com.platform.ems.service.IBasVendorAddrService;

/**
 * 供应商-联系方式信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-01-31
 */
@Service
public class BasVendorAddrServiceImpl implements IBasVendorAddrService {

    private Random random = new Random(System.currentTimeMillis());

    @Autowired
    private BasVendorAddrMapper basVendorAddrMapper;

    /**
     * 查询供应商-联系方式信息
     *
     * @param clientId 供应商-联系方式信息ID
     * @return 供应商-联系方式信息
     */
    @Override
    public BasVendorAddr selectBasVendorAddrById(String clientId) {
        return basVendorAddrMapper.selectBasVendorAddrById(clientId);
    }

    /**
     * 查询供应商-联系方式信息列表
     *
     * @param basVendorAddr 供应商-联系方式信息
     * @return 供应商-联系方式信息
     */
    @Override
    public List<BasVendorAddr> selectBasVendorAddrList(BasVendorAddr basVendorAddr) {
        return basVendorAddrMapper.selectBasVendorAddrList(basVendorAddr);
    }

    /**
     * 新增供应商-联系方式信息
     *
     * @param basVendorAddrAddRequest 供应商-联系方式信息
     * @return 结果
     */
    @Override
    public AjaxResult insertBasVendorAddr(BasVendorAddrAddRequest basVendorAddrAddRequest) {

//        Long sid = genID();
        BasVendorAddr basVendorAddr = new BasVendorAddr();
        basVendorAddr.setVendorSid(basVendorAddrAddRequest.getVendorSid());
//        basVendorAddr.setVendorContactSid(sid);
//        basVendorAddr.setClientId(ConstantsEms.CLIENT_ID);
        basVendorAddr.setCreatorAccount(basVendorAddrAddRequest.getCreatorAccount());
        basVendorAddr.setCreateDate(new Date());
        BeanUtils.copyProperties(basVendorAddrAddRequest.getVendorAddrRequest(), basVendorAddr);
        int row = basVendorAddrMapper.insertBasVendorAddr(basVendorAddr);

        if(row>0) {
//            BasVendorAddr basVendorAddrRes = basVendorAddrMapper.selectBasVendorAddrById(sid);
//            CustomerAddrResponse response = new CustomerAddrResponse();
//            BeanUtils.copyProperties(basVendorAddrRes, response);
//            response.setCustomerContactSid(basVendorAddrRes.getVendorContactSid());
//            response.setCustomerSid(basVendorAddrRes.getVendorSid());
//            response.setCreateDate(DateFormatUtils.format(basVendorAddrRes.getCreateDate(), "yyyy-MM-dd"));
            return AjaxResult.success(1);
        }else {
            return AjaxResult.error("添加供应商-联系方式失败");
        }
    }

    /**
     * 修改供应商-联系方式信息
     *
     * @param request 供应商-联系方式信息
     * @return 结果
     */
    @Override
    public int updateBasVendorAddr(BasVendorAddrUpdateRequest request) {

        BasVendorAddr basVendorAddr = new BasVendorAddr();
        basVendorAddr.setVendorSid(request.getVendorSid());
        basVendorAddr.setVendorContactSid(request.getCustomerContactSid());
        basVendorAddr.setUpdaterAccount(request.getUpdaterAccount());
        basVendorAddr.setUpdateDate(new Date());
        BeanUtils.copyProperties(request.getVendorAddrRequest(), basVendorAddr);
        return basVendorAddrMapper.updateBasVendorAddr(basVendorAddr);
    }

    /**
     * 批量删除供应商-联系方式信息
     *
     * @param request 需要删除的供应商-联系方式信息ID
     * @return 结果
     */
    @Override
    public int deleteBasVendorAddrByIds(BasVendorAddrDeleteRequest request) {
        if(request == null || request.getVendorAddrSids().length() == 0){
            return 0;
        }
        String[] clientIds = request.getVendorAddrSids().split(";");
        return basVendorAddrMapper.deleteBasVendorAddrByIds(clientIds);
    }

    /**
     * 删除供应商-联系方式信息信息
     *
     * @param clientId 供应商-联系方式信息ID
     * @return 结果
     */
    @Override
    public int deleteBasVendorAddrById(String clientId) {
        return basVendorAddrMapper.deleteBasVendorAddrById(clientId);
    }

    private String genID(){
        return String.format("%d%03d", System.currentTimeMillis(), random.nextInt(1000));
    }
}
