package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
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
import com.platform.ems.mapper.BasVendorRecommendAddrMapper;
import com.platform.ems.domain.BasVendorRecommendAddr;
import com.platform.ems.service.IBasVendorRecommendAddrService;

/**
 * 供应商推荐-联系方式信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRecommendAddrServiceImpl extends ServiceImpl<BasVendorRecommendAddrMapper, BasVendorRecommendAddr> implements IBasVendorRecommendAddrService {
    @Autowired
    private BasVendorRecommendAddrMapper basVendorRecommendAddrMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商推荐-联系方式信息";

    /**
     * 查询供应商推荐-联系方式信息
     *
     * @param vendorRecommendContactSid 供应商推荐-联系方式信息ID
     * @return 供应商推荐-联系方式信息
     */
    @Override
    public BasVendorRecommendAddr selectBasVendorRecommendAddrById(Long vendorRecommendContactSid) {
        BasVendorRecommendAddr basVendorRecommendAddr = basVendorRecommendAddrMapper.selectBasVendorRecommendAddrById(vendorRecommendContactSid);
        MongodbUtil.find(basVendorRecommendAddr);
        return basVendorRecommendAddr;
    }

    /**
     * 查询供应商推荐-联系方式信息列表
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 供应商推荐-联系方式信息
     */
    @Override
    public List<BasVendorRecommendAddr> selectBasVendorRecommendAddrList(BasVendorRecommendAddr basVendorRecommendAddr) {
        return basVendorRecommendAddrMapper.selectBasVendorRecommendAddrList(basVendorRecommendAddr);
    }

    /**
     * 新增供应商推荐-联系方式信息
     * 需要注意编码重复校验
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRecommendAddr(BasVendorRecommendAddr basVendorRecommendAddr) {
        int row = basVendorRecommendAddrMapper.insert(basVendorRecommendAddr);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRecommendAddr(), basVendorRecommendAddr);
            MongodbUtil.insertUserLog(basVendorRecommendAddr.getVendorRecommendContactSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商推荐-联系方式信息
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRecommendAddr(BasVendorRecommendAddr basVendorRecommendAddr) {
        BasVendorRecommendAddr response = basVendorRecommendAddrMapper.selectBasVendorRecommendAddrById(basVendorRecommendAddr.getVendorRecommendContactSid());
        int row = basVendorRecommendAddrMapper.updateById(basVendorRecommendAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRecommendAddr.getVendorRecommendContactSid(), BusinessType.UPDATE.getValue(), response, basVendorRecommendAddr, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商推荐-联系方式信息
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRecommendAddr(BasVendorRecommendAddr basVendorRecommendAddr) {
        BasVendorRecommendAddr response = basVendorRecommendAddrMapper.selectBasVendorRecommendAddrById(basVendorRecommendAddr.getVendorRecommendContactSid());
        int row = basVendorRecommendAddrMapper.updateAllById(basVendorRecommendAddr);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRecommendAddr.getVendorRecommendContactSid(), BusinessType.CHANGE.getValue(), response, basVendorRecommendAddr, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商推荐-联系方式信息
     *
     * @param vendorRecommendContactSids 需要删除的供应商推荐-联系方式信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRecommendAddrByIds(List<Long> vendorRecommendContactSids) {
        int row = 0;
        for (Long sid : vendorRecommendContactSids) {
            BasVendorRecommendAddr basVendorRecommendAddr = basVendorRecommendAddrMapper.selectById(sid);
            row += basVendorRecommendAddrMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(basVendorRecommendAddr, new BasVendorRecommendAddr());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 查询主表下的联系方式信息
     *
     * @param vendorRecommendSid 供应商推荐ID
     * @return 供应商推荐-联系方式信息
     */
    @Override
    public List<BasVendorRecommendAddr> selectBasVendorRecommendAddrListById(Long vendorRecommendSid) {
        List<BasVendorRecommendAddr> basVendorRecommendAddrList = basVendorRecommendAddrMapper.selectBasVendorRecommendAddrList
                (new BasVendorRecommendAddr().setVendorRecommendSid(vendorRecommendSid));
        basVendorRecommendAddrList.forEach(basVendorRecommendAddr->{
            MongodbUtil.find(basVendorRecommendAddr);
        });
        return basVendorRecommendAddrList;
    }

    /**
     * 由主表批量新增供应商推荐-联系方式
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRecommendAddr(List<BasVendorRecommendAddr> basVendorRecommendAddrList, Long vendorRecommendSid) {
        if (CollectionUtil.isEmpty(basVendorRecommendAddrList)){
            return 0;
        }
        basVendorRecommendAddrList.forEach(basVendorRecommendAddr->{
            basVendorRecommendAddr.setClientId(ConstantsEms.CLIENT_ID_10001);
            basVendorRecommendAddr.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            basVendorRecommendAddr.setVendorRecommendSid(vendorRecommendSid);
        });
        int row = basVendorRecommendAddrMapper.inserts(basVendorRecommendAddrList);
        if (row > 0) {
            //插入日志
            basVendorRecommendAddrList.forEach(basVendorRecommendAddr->{
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRecommendAddr(), basVendorRecommendAddr);
                MongodbUtil.insertUserLog(basVendorRecommendAddr.getVendorRecommendContactSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 修改供应商推荐-联系方式信息
     *
     * @param basVendorRecommendAddr 供应商推荐-联系方式信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRecommendAddr(List<BasVendorRecommendAddr> basVendorRecommendAddrList) {
        int row = 0 ;
        for (BasVendorRecommendAddr basVendorRecommendAddr : basVendorRecommendAddrList) {
            BasVendorRecommendAddr response = basVendorRecommendAddrMapper.selectBasVendorRecommendAddrById(basVendorRecommendAddr.getVendorRecommendContactSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRecommendAddr);
            if (msgList.size() > 0){
                row += basVendorRecommendAddrMapper.updateById(basVendorRecommendAddr);
                MongodbUtil.insertUserLog(basVendorRecommendAddr.getVendorRecommendContactSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改联系方式明细信息
     *
     * @param list 旧的信息
     * @param request 新的请求信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRecommendAddr(List<BasVendorRecommendAddr> list, List<BasVendorRecommendAddr> request,Long vendorRecommendSid){
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)){
            oldIds = list.stream().map(BasVendorRecommendAddr::getVendorRecommendContactSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)){
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRecommendAddr> updateAddrList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateAddrList = request.stream().filter(item-> item.getVendorRecommendContactSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateAddrList)){
                this.deleteBasVendorRecommendAddrByIds(oldIds);
            }else {
                List<Long> updateIds = updateAddrList.stream().map(BasVendorRecommendAddr::getVendorRecommendContactSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)){
                    row += this.deleteBasVendorRecommendAddrByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRecommendAddr(updateAddrList);
            }
        }
        //新增加的明细
        List<BasVendorRecommendAddr> newAddrList = request.stream().filter(item-> item.getVendorRecommendContactSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newAddrList)){
            row += this.insertBasVendorRecommendAddr(newAddrList,vendorRecommendSid);
        }
        return row;
    }

}
