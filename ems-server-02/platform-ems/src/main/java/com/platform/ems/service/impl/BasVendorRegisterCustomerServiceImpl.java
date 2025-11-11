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
import com.platform.ems.domain.BasVendorRegisterCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterCustomerMapper;
import com.platform.ems.service.IBasVendorRegisterCustomerService;

/**
 * 供应商注册-主要客户信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterCustomerServiceImpl extends ServiceImpl<BasVendorRegisterCustomerMapper, BasVendorRegisterCustomer> implements IBasVendorRegisterCustomerService {
    @Autowired
    private BasVendorRegisterCustomerMapper basVendorRegisterCustomerMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "供应商注册-主要客户信息";

    /**
     * 查询供应商注册-主要客户信息
     *
     * @param vendorRegisterCustomerSid 供应商注册-主要客户信息ID
     * @return 供应商注册-主要客户信息
     */
    @Override
    public BasVendorRegisterCustomer selectBasVendorRegisterCustomerById(Long vendorRegisterCustomerSid) {
        BasVendorRegisterCustomer basVendorRegisterCustomer = basVendorRegisterCustomerMapper.selectBasVendorRegisterCustomerById(vendorRegisterCustomerSid);
        MongodbUtil.find(basVendorRegisterCustomer);
        return basVendorRegisterCustomer;
    }

    /**
     * 查询供应商注册-主要客户信息列表
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 供应商注册-主要客户信息
     */
    @Override
    public List<BasVendorRegisterCustomer> selectBasVendorRegisterCustomerList(BasVendorRegisterCustomer basVendorRegisterCustomer) {
        return basVendorRegisterCustomerMapper.selectBasVendorRegisterCustomerList(basVendorRegisterCustomer);
    }

    /**
     * 新增供应商注册-主要客户信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterCustomer(BasVendorRegisterCustomer basVendorRegisterCustomer) {
        int row = basVendorRegisterCustomerMapper.insert(basVendorRegisterCustomer);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterCustomer(), basVendorRegisterCustomer);
            MongodbUtil.insertUserLog(basVendorRegisterCustomer.getVendorRegisterCustomerSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterCustomer(BasVendorRegisterCustomer basVendorRegisterCustomer) {
        BasVendorRegisterCustomer response = basVendorRegisterCustomerMapper.selectBasVendorRegisterCustomerById(basVendorRegisterCustomer.getVendorRegisterCustomerSid());
        int row = basVendorRegisterCustomerMapper.updateById(basVendorRegisterCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterCustomer.getVendorRegisterCustomerSid(), BusinessType.UPDATE.ordinal(), response, basVendorRegisterCustomer, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterCustomer(BasVendorRegisterCustomer basVendorRegisterCustomer) {
        BasVendorRegisterCustomer response = basVendorRegisterCustomerMapper.selectBasVendorRegisterCustomerById(basVendorRegisterCustomer.getVendorRegisterCustomerSid());
        int row = basVendorRegisterCustomerMapper.updateAllById(basVendorRegisterCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterCustomer.getVendorRegisterCustomerSid(), BusinessType.CHANGE.ordinal(), response, basVendorRegisterCustomer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商注册-主要客户信息
     *
     * @param vendorRegisterCustomerSids 需要删除的供应商注册-主要客户信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterCustomerByIds(List<Long> vendorRegisterCustomerSids) {
        int row = 0;
        for (Long sid : vendorRegisterCustomerSids) {
            BasVendorRegisterCustomer response = basVendorRegisterCustomerMapper.selectById(sid);
            row += basVendorRegisterCustomerMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterCustomer());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表查询供应商注册-主要客户信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-主要客户信息集合
     */
    @Override
    public List<BasVendorRegisterCustomer> selectBasVendorRegisterCustomerListById(Long vendorRegisterSid) {
        List<BasVendorRegisterCustomer> response = basVendorRegisterCustomerMapper.selectBasVendorRegisterCustomerList
                (new BasVendorRegisterCustomer().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterCustomer -> {
            MongodbUtil.find(basVendorRegisterCustomer);
        });
        return response;
    }

    /**
     * 新增供应商注册-主要客户信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterCustomer 供应商注册-主要客户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterCustomer(List<BasVendorRegisterCustomer> basVendorRegisterCustomerList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterCustomerList)) {
            return 0;
        }
        basVendorRegisterCustomerList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterCustomerMapper.inserts(basVendorRegisterCustomerList);
        if (row > 0) {
            //插入日志
            basVendorRegisterCustomerList.forEach(basVendorRegisterCustomer -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterCustomer(), basVendorRegisterCustomer);
                MongodbUtil.insertUserLog(basVendorRegisterCustomer.getVendorRegisterCustomerSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomerList 供应商注册-主要客户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterCustomer(List<BasVendorRegisterCustomer> basVendorRegisterCustomerList) {
        int row = 0;
        for (BasVendorRegisterCustomer basVendorRegisterCustomer : basVendorRegisterCustomerList) {
            BasVendorRegisterCustomer response = basVendorRegisterCustomerMapper.selectBasVendorRegisterCustomerById(basVendorRegisterCustomer.getVendorRegisterCustomerSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterCustomer);
            if (msgList.size() > 0) {
                row += basVendorRegisterCustomerMapper.updateById(basVendorRegisterCustomer);
                MongodbUtil.insertUserLog(basVendorRegisterCustomer.getVendorRegisterCustomerSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-主要客户信息
     *
     * @param basVendorRegisterCustomerList 供应商注册-主要客户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterCustomer(List<BasVendorRegisterCustomer> response, List<BasVendorRegisterCustomer> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterCustomer::getVendorRegisterCustomerSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterCustomer> updateCustomerList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateCustomerList = request.stream().filter(item -> item.getVendorRegisterCustomerSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateCustomerList)) {
                this.deleteBasVendorRegisterCustomerByIds(oldIds);
            } else {
                List<Long> updateIds = updateCustomerList.stream().map(BasVendorRegisterCustomer::getVendorRegisterCustomerSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterCustomerByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterCustomer(updateCustomerList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterCustomer> newCustomerList = request.stream().filter(item -> item.getVendorRegisterCustomerSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newCustomerList)) {
            row += this.insertBasVendorRegisterCustomer(newCustomerList, vendorRegisterSid);
        }
        return row;
    }

    /**
     * 由主表批量删除供应商注册-主要客户信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterCustomerListByIds(List<Long> vendorRegisterSids) {
        List<BasVendorRegisterCustomer> addrList = basVendorRegisterCustomerMapper.selectList(new QueryWrapper<BasVendorRegisterCustomer>().lambda()
                .in(BasVendorRegisterCustomer::getVendorRegisterSid, vendorRegisterSids));
        List<Long> addrSids = addrList.stream().map(BasVendorRegisterCustomer::getVendorRegisterCustomerSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterCustomerByIds(addrSids);
    }
}
