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
import com.platform.ems.domain.BasVendorRegisterSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterSupplierMapper;
import com.platform.ems.service.IBasVendorRegisterSupplierService;

/**
 * 供应商注册-主要供应商信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterSupplierServiceImpl extends ServiceImpl<BasVendorRegisterSupplierMapper, BasVendorRegisterSupplier> implements IBasVendorRegisterSupplierService {
    @Autowired
    private BasVendorRegisterSupplierMapper basVendorRegisterSupplierMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-主要供应商信息";

    /**
     * 查询供应商注册-主要供应商信息
     *
     * @param vendorRegisterSupplierSid 供应商注册-主要供应商信息ID
     * @return 供应商注册-主要供应商信息
     */
    @Override
    public BasVendorRegisterSupplier selectBasVendorRegisterSupplierById(Long vendorRegisterSupplierSid) {
        BasVendorRegisterSupplier basVendorRegisterSupplier = basVendorRegisterSupplierMapper.selectBasVendorRegisterSupplierById(vendorRegisterSupplierSid);
        MongodbUtil.find(basVendorRegisterSupplier);
        return basVendorRegisterSupplier;
    }

    /**
     * 查询供应商注册-主要供应商信息列表
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 供应商注册-主要供应商信息
     */
    @Override
    public List<BasVendorRegisterSupplier> selectBasVendorRegisterSupplierList(BasVendorRegisterSupplier basVendorRegisterSupplier) {
        return basVendorRegisterSupplierMapper.selectBasVendorRegisterSupplierList(basVendorRegisterSupplier);
    }

    /**
     * 新增供应商注册-主要供应商信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterSupplier(BasVendorRegisterSupplier basVendorRegisterSupplier) {
        int row = basVendorRegisterSupplierMapper.insert(basVendorRegisterSupplier);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterSupplier(), basVendorRegisterSupplier);
            MongodbUtil.insertUserLog(basVendorRegisterSupplier.getVendorRegisterSupplierSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterSupplier(BasVendorRegisterSupplier basVendorRegisterSupplier) {
        BasVendorRegisterSupplier response = basVendorRegisterSupplierMapper.selectBasVendorRegisterSupplierById(basVendorRegisterSupplier.getVendorRegisterSupplierSid());
        int row = basVendorRegisterSupplierMapper.updateById(basVendorRegisterSupplier);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterSupplier.getVendorRegisterSupplierSid(), BusinessType.UPDATE.ordinal(), response, basVendorRegisterSupplier, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterSupplier(BasVendorRegisterSupplier basVendorRegisterSupplier) {
        BasVendorRegisterSupplier response = basVendorRegisterSupplierMapper.selectBasVendorRegisterSupplierById(basVendorRegisterSupplier.getVendorRegisterSupplierSid());
        int row = basVendorRegisterSupplierMapper.updateAllById(basVendorRegisterSupplier);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterSupplier.getVendorRegisterSupplierSid(), BusinessType.CHANGE.ordinal(), response, basVendorRegisterSupplier, TITLE);
        }
        return row;
    }


    /**
     * 批量删除供应商注册-主要供应商信息
     *
     * @param vendorRegisterSupplierSids 需要删除的供应商注册-主要供应商信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterSupplierByIds(List<Long> vendorRegisterSupplierSids) {
        int row = 0;
        for (Long sid : vendorRegisterSupplierSids) {
            BasVendorRegisterSupplier response = basVendorRegisterSupplierMapper.selectById(sid);
            row += basVendorRegisterSupplierMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterSupplier());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表查询供应商注册-主要供应商信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-主要供应商信息集合
     */
    @Override
    public List<BasVendorRegisterSupplier> selectBasVendorRegisterSupplierListById(Long vendorRegisterSid) {
        List<BasVendorRegisterSupplier> response = basVendorRegisterSupplierMapper.selectBasVendorRegisterSupplierList
                (new BasVendorRegisterSupplier().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterSupplier -> {
            MongodbUtil.find(basVendorRegisterSupplier);
        });
        return response;
    }


    /**
     * 新增供应商注册-主要供应商信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterSupplier 供应商注册-主要供应商信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterSupplier(List<BasVendorRegisterSupplier> basVendorRegisterSupplierList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterSupplierList)) {
            return 0;
        }
        basVendorRegisterSupplierList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterSupplierMapper.inserts(basVendorRegisterSupplierList);
        if (row > 0) {
            //插入日志
            basVendorRegisterSupplierList.forEach(basVendorRegisterSupplier -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterSupplier(), basVendorRegisterSupplier);
                MongodbUtil.insertUserLog(basVendorRegisterSupplier.getVendorRegisterSupplierSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplierList 供应商注册-主要供应商信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterSupplier(List<BasVendorRegisterSupplier> basVendorRegisterSupplierList) {
        int row = 0;
        for (BasVendorRegisterSupplier basVendorRegisterSupplier : basVendorRegisterSupplierList) {
            BasVendorRegisterSupplier response = basVendorRegisterSupplierMapper.selectBasVendorRegisterSupplierById(basVendorRegisterSupplier.getVendorRegisterSupplierSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterSupplier);
            if (msgList.size() > 0) {
                row += basVendorRegisterSupplierMapper.updateById(basVendorRegisterSupplier);
                MongodbUtil.insertUserLog(basVendorRegisterSupplier.getVendorRegisterSupplierSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-主要供应商信息
     *
     * @param basVendorRegisterSupplierList 供应商注册-主要供应商信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterSupplier(List<BasVendorRegisterSupplier> response, List<BasVendorRegisterSupplier> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterSupplier::getVendorRegisterSupplierSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterSupplier> updateSupplierList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateSupplierList = request.stream().filter(item -> item.getVendorRegisterSupplierSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateSupplierList)) {
                this.deleteBasVendorRegisterSupplierByIds(oldIds);
            } else {
                List<Long> updateIds = updateSupplierList.stream().map(BasVendorRegisterSupplier::getVendorRegisterSupplierSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterSupplierByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterSupplier(updateSupplierList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterSupplier> newSupplierList = request.stream().filter(item -> item.getVendorRegisterSupplierSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newSupplierList)) {
            row += this.insertBasVendorRegisterSupplier(newSupplierList, vendorRegisterSid);
        }
        return row;
    }

    /**
     * 由主表批量删除供应商注册-主要供应商信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterSupplierListByIds(List<Long> vendorRegisterSids){
        List<BasVendorRegisterSupplier> addrList = basVendorRegisterSupplierMapper.selectList(new QueryWrapper<BasVendorRegisterSupplier>().lambda()
                .in(BasVendorRegisterSupplier::getVendorRegisterSid,vendorRegisterSids));
        List<Long> addrSids = addrList.stream().map(BasVendorRegisterSupplier::getVendorRegisterSupplierSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterSupplierByIds(addrSids);
    }
}
