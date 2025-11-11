package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterAddrMapper;
import com.platform.ems.domain.BasVendorRegisterAddr;
import com.platform.ems.service.IBasVendorRegisterAddrService;

/**
 * 供应商注册-联系方式信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterAddrServiceImpl extends ServiceImpl<BasVendorRegisterAddrMapper, BasVendorRegisterAddr> implements IBasVendorRegisterAddrService {
    @Autowired
    private BasVendorRegisterAddrMapper basVendorRegisterAddrMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-联系方式信息";

    /**
     * 查询供应商注册-联系方式信息
     *
     * @param vendorRegisterContactSid 供应商注册-联系方式信息ID
     * @return 供应商注册-联系方式信息
     */
    @Override
    public BasVendorRegisterAddr selectBasVendorRegisterAddrById(Long vendorRegisterContactSid) {
        BasVendorRegisterAddr basVendorRegisterAddr = basVendorRegisterAddrMapper.selectBasVendorRegisterAddrById(vendorRegisterContactSid);
        MongodbUtil.find(basVendorRegisterAddr);
        return basVendorRegisterAddr;
    }

    /**
     * 查询供应商注册-联系方式信息列表
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 供应商注册-联系方式信息
     */
    @Override
    public List<BasVendorRegisterAddr> selectBasVendorRegisterAddrList(BasVendorRegisterAddr basVendorRegisterAddr) {
        return basVendorRegisterAddrMapper.selectBasVendorRegisterAddrList(basVendorRegisterAddr);
    }

    /**
     * 新增供应商注册-联系方式信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterAddr(BasVendorRegisterAddr basVendorRegisterAddr) {
        int row = basVendorRegisterAddrMapper.insert(basVendorRegisterAddr);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterAddr(), basVendorRegisterAddr);
            MongodbUtil.insertUserLog(basVendorRegisterAddr.getVendorRegisterContactSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterAddr(BasVendorRegisterAddr basVendorRegisterAddr) {
        BasVendorRegisterAddr response = basVendorRegisterAddrMapper.selectBasVendorRegisterAddrById(basVendorRegisterAddr.getVendorRegisterContactSid());
        int row = basVendorRegisterAddrMapper.updateById(basVendorRegisterAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterAddr.getVendorRegisterContactSid(), BusinessType.UPDATE.getValue(), response, basVendorRegisterAddr, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterAddr(BasVendorRegisterAddr basVendorRegisterAddr) {
        BasVendorRegisterAddr response = basVendorRegisterAddrMapper.selectBasVendorRegisterAddrById(basVendorRegisterAddr.getVendorRegisterContactSid());
        int row = basVendorRegisterAddrMapper.updateAllById(basVendorRegisterAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterAddr.getVendorRegisterContactSid(), BusinessType.CHANGE.getValue(), response, basVendorRegisterAddr, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商注册-联系方式信息
     *
     * @param vendorRegisterContactSids 需要删除的供应商注册-联系方式信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterAddrByIds(List<Long> vendorRegisterContactSids) {
        int row = 0;
        for (Long sid : vendorRegisterContactSids) {
            BasVendorRegisterAddr response = basVendorRegisterAddrMapper.selectById(sid);
            row += basVendorRegisterAddrMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterAddr());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表查询供应商注册-联系方式信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-联系方式信息集合
     */
    @Override
    public List<BasVendorRegisterAddr> selectBasVendorRegisterAddrListById(Long vendorRegisterSid) {
        List<BasVendorRegisterAddr> response = basVendorRegisterAddrMapper.selectBasVendorRegisterAddrList
                (new BasVendorRegisterAddr().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterAddr -> {
            MongodbUtil.find(basVendorRegisterAddr);
        });
        return response;
    }


    /**
     * 新增供应商注册-联系方式信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterAddr 供应商注册-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterAddr(List<BasVendorRegisterAddr> basVendorRegisterAddrList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterAddrList)) {
            return 0;
        }
        basVendorRegisterAddrList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterAddrMapper.inserts(basVendorRegisterAddrList);
        if (row > 0) {
            //插入日志
            basVendorRegisterAddrList.forEach(basVendorRegisterAddr -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterAddr(), basVendorRegisterAddr);
                MongodbUtil.insertUserLog(basVendorRegisterAddr.getVendorRegisterContactSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddrList 供应商注册-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterAddr(List<BasVendorRegisterAddr> basVendorRegisterAddrList) {
        int row = 0;
        for (BasVendorRegisterAddr basVendorRegisterAddr : basVendorRegisterAddrList) {
            BasVendorRegisterAddr response = basVendorRegisterAddrMapper.selectBasVendorRegisterAddrById(basVendorRegisterAddr.getVendorRegisterContactSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterAddr);
            if (msgList.size() > 0) {
                row += basVendorRegisterAddrMapper.updateById(basVendorRegisterAddr);
                MongodbUtil.insertUserLog(basVendorRegisterAddr.getVendorRegisterContactSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-联系方式信息
     *
     * @param basVendorRegisterAddrList 供应商注册-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterAddr(List<BasVendorRegisterAddr> response, List<BasVendorRegisterAddr> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterAddr::getVendorRegisterContactSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterAddr> updateAddrList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateAddrList = request.stream().filter(item -> item.getVendorRegisterContactSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateAddrList)) {
                this.deleteBasVendorRegisterAddrByIds(oldIds);
            } else {
                List<Long> updateIds = updateAddrList.stream().map(BasVendorRegisterAddr::getVendorRegisterContactSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterAddrByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterAddr(updateAddrList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterAddr> newAddrList = request.stream().filter(item -> item.getVendorRegisterContactSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newAddrList)) {
            row += this.insertBasVendorRegisterAddr(newAddrList, vendorRegisterSid);
        }
        return row;
    }

    /**
     * 由主表批量删除供应商注册-联系方式信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterAddrListByIds(List<Long> vendorRegisterSids){
        List<BasVendorRegisterAddr> addrList = basVendorRegisterAddrMapper.selectList(new QueryWrapper<BasVendorRegisterAddr>().lambda()
                .in(BasVendorRegisterAddr::getVendorRegisterSid,vendorRegisterSids));
        List<Long> addrSids = addrList.stream().map(BasVendorRegisterAddr::getVendorRegisterContactSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterAddrByIds(addrSids);
    }
}
