package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasLaboratoryAddr;
import com.platform.ems.mapper.BasLaboratoryAddrMapper;
import com.platform.ems.service.IBasLaboratoryAddrService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验室-联系方式信息Service业务层处理
 *
 * @author c
 * @date 2022-03-31
 */
@Service
@SuppressWarnings("all")
public class BasLaboratoryAddrServiceImpl extends ServiceImpl<BasLaboratoryAddrMapper, BasLaboratoryAddr> implements IBasLaboratoryAddrService {
    @Autowired
    private BasLaboratoryAddrMapper basLaboratoryAddrMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "实验室-联系方式信息";

    /**
     * 查询实验室-联系方式信息
     *
     * @param laboratoryContactSid 实验室-联系方式信息ID
     * @return 实验室-联系方式信息
     */
    @Override
    public BasLaboratoryAddr selectBasLaboratoryAddrById(Long laboratoryContactSid) {
        BasLaboratoryAddr basLaboratoryAddr = basLaboratoryAddrMapper.selectBasLaboratoryAddrById(laboratoryContactSid);
        MongodbUtil.find(basLaboratoryAddr);
        return basLaboratoryAddr;
    }

    /**
     * 查询实验室-联系方式信息列表
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 实验室-联系方式信息
     */
    @Override
    public List<BasLaboratoryAddr> selectBasLaboratoryAddrList(BasLaboratoryAddr basLaboratoryAddr) {
        return basLaboratoryAddrMapper.selectBasLaboratoryAddrList(basLaboratoryAddr);
    }

    /**
     * 新增实验室-联系方式信息
     * 需要注意编码重复校验
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasLaboratoryAddr(BasLaboratoryAddr basLaboratoryAddr) {
        int row = basLaboratoryAddrMapper.insert(basLaboratoryAddr);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basLaboratoryAddr.getLaboratoryContactSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改实验室-联系方式信息
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasLaboratoryAddr(BasLaboratoryAddr basLaboratoryAddr) {
        BasLaboratoryAddr response = basLaboratoryAddrMapper.selectBasLaboratoryAddrById(basLaboratoryAddr.getLaboratoryContactSid());
        int row = basLaboratoryAddrMapper.updateById(basLaboratoryAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basLaboratoryAddr.getLaboratoryContactSid(), BusinessType.UPDATE.ordinal(), response, basLaboratoryAddr, TITLE);
        }
        return row;
    }

    /**
     * 变更实验室-联系方式信息
     *
     * @param basLaboratoryAddr 实验室-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasLaboratoryAddr(BasLaboratoryAddr basLaboratoryAddr) {
        BasLaboratoryAddr response = basLaboratoryAddrMapper.selectBasLaboratoryAddrById(basLaboratoryAddr.getLaboratoryContactSid());
        int row = basLaboratoryAddrMapper.updateAllById(basLaboratoryAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basLaboratoryAddr.getLaboratoryContactSid(), BusinessType.CHANGE.ordinal(), response, basLaboratoryAddr, TITLE);
        }
        return row;
    }

    /**
     * 批量删除实验室-联系方式信息
     *
     * @param laboratoryContactSids 需要删除的实验室-联系方式信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasLaboratoryAddrByIds(List<Long> laboratoryContactSids) {
        return basLaboratoryAddrMapper.deleteBatchIds(laboratoryContactSids);
    }

}
