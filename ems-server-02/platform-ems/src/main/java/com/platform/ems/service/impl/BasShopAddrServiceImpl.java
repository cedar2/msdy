package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasShopAddr;
import com.platform.ems.mapper.BasShopAddrMapper;
import com.platform.ems.service.IBasShopAddrService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺-联系方式信息Service业务层处理
 *
 * @author c
 * @date 2022-03-31
 */
@Service
@SuppressWarnings("all")
public class BasShopAddrServiceImpl extends ServiceImpl<BasShopAddrMapper, BasShopAddr> implements IBasShopAddrService {
    @Autowired
    private BasShopAddrMapper basShopAddrMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "店铺-联系方式信息";

    /**
     * 查询店铺-联系方式信息
     *
     * @param shopContactSid 店铺-联系方式信息ID
     * @return 店铺-联系方式信息
     */
    @Override
    public BasShopAddr selectBasShopAddrById(Long shopContactSid) {
        BasShopAddr basShopAddr = basShopAddrMapper.selectBasShopAddrById(shopContactSid);
        MongodbUtil.find(basShopAddr);
        return basShopAddr;
    }

    /**
     * 查询店铺-联系方式信息列表
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 店铺-联系方式信息
     */
    @Override
    public List<BasShopAddr> selectBasShopAddrList(BasShopAddr basShopAddr) {
        return basShopAddrMapper.selectBasShopAddrList(basShopAddr);
    }

    /**
     * 新增店铺-联系方式信息
     * 需要注意编码重复校验
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasShopAddr(BasShopAddr basShopAddr) {
        int row = basShopAddrMapper.insert(basShopAddr);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basShopAddr.getShopContactSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改店铺-联系方式信息
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasShopAddr(BasShopAddr basShopAddr) {
        BasShopAddr response = basShopAddrMapper.selectBasShopAddrById(basShopAddr.getShopContactSid());
        int row = basShopAddrMapper.updateById(basShopAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basShopAddr.getShopContactSid(), BusinessType.UPDATE.ordinal(), response, basShopAddr, TITLE);
        }
        return row;
    }

    /**
     * 变更店铺-联系方式信息
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasShopAddr(BasShopAddr basShopAddr) {
        BasShopAddr response = basShopAddrMapper.selectBasShopAddrById(basShopAddr.getShopContactSid());
        int row = basShopAddrMapper.updateAllById(basShopAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basShopAddr.getShopContactSid(), BusinessType.CHANGE.ordinal(), response, basShopAddr, TITLE);
        }
        return row;
    }

    /**
     * 批量删除店铺-联系方式信息
     *
     * @param shopContactSids 需要删除的店铺-联系方式信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasShopAddrByIds(List<Long> shopContactSids) {
        return basShopAddrMapper.deleteBatchIds(shopContactSids);
    }
}
