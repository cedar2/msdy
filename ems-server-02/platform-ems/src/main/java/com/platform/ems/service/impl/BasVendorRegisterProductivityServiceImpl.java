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
import com.platform.ems.domain.BasVendorRegisterProductivity;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterProductivityMapper;
import com.platform.ems.service.IBasVendorRegisterProductivityService;

/**
 * 供应商注册-产能信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterProductivityServiceImpl extends ServiceImpl<BasVendorRegisterProductivityMapper, BasVendorRegisterProductivity> implements IBasVendorRegisterProductivityService {
    @Autowired
    private BasVendorRegisterProductivityMapper basVendorRegisterProductivityMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-产能信息";

    /**
     * 查询供应商注册-产能信息
     *
     * @param vendorRegisterProductivitySid 供应商注册-产能信息ID
     * @return 供应商注册-产能信息
     */
    @Override
    public BasVendorRegisterProductivity selectBasVendorRegisterProductivityById(Long vendorRegisterProductivitySid) {
        BasVendorRegisterProductivity basVendorRegisterProductivity = basVendorRegisterProductivityMapper.selectBasVendorRegisterProductivityById(vendorRegisterProductivitySid);
        MongodbUtil.find(basVendorRegisterProductivity);
        return basVendorRegisterProductivity;
    }

    /**
     * 查询供应商注册-产能信息列表
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 供应商注册-产能信息
     */
    @Override
    public List<BasVendorRegisterProductivity> selectBasVendorRegisterProductivityList(BasVendorRegisterProductivity basVendorRegisterProductivity) {
        return basVendorRegisterProductivityMapper.selectBasVendorRegisterProductivityList(basVendorRegisterProductivity);
    }

    /**
     * 新增供应商注册-产能信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterProductivity(BasVendorRegisterProductivity basVendorRegisterProductivity) {
        int row = basVendorRegisterProductivityMapper.insert(basVendorRegisterProductivity);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterProductivity(), basVendorRegisterProductivity);
            MongodbUtil.insertUserLog(basVendorRegisterProductivity.getVendorRegisterProductivitySid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-产能信息
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterProductivity(BasVendorRegisterProductivity basVendorRegisterProductivity) {
        BasVendorRegisterProductivity response = basVendorRegisterProductivityMapper.selectBasVendorRegisterProductivityById
                (basVendorRegisterProductivity.getVendorRegisterProductivitySid());
        int row = basVendorRegisterProductivityMapper.updateById(basVendorRegisterProductivity);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterProductivity.getVendorRegisterProductivitySid(),
                    BusinessType.UPDATE.ordinal(), response, basVendorRegisterProductivity, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-产能信息
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterProductivity(BasVendorRegisterProductivity basVendorRegisterProductivity) {
        BasVendorRegisterProductivity response = basVendorRegisterProductivityMapper.selectBasVendorRegisterProductivityById
                (basVendorRegisterProductivity.getVendorRegisterProductivitySid());
        int row = basVendorRegisterProductivityMapper.updateAllById(basVendorRegisterProductivity);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterProductivity.getVendorRegisterProductivitySid(),
                    BusinessType.CHANGE.ordinal(), response, basVendorRegisterProductivity, TITLE);
        }
        return row;
    }


    /**
     * 批量删除供应商注册-产能信息
     *
     * @param vendorRegisterProductivitySids 需要删除的供应商注册-产能信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterProductivityByIds(List<Long> vendorRegisterProductivitySids) {
        int row = 0;
        for (Long sid : vendorRegisterProductivitySids) {
            BasVendorRegisterProductivity response = basVendorRegisterProductivityMapper.selectById(sid);
            row += basVendorRegisterProductivityMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterProductivity());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表查询供应商注册-产能信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-产能信息集合
     */
    @Override
    public List<BasVendorRegisterProductivity> selectBasVendorRegisterProductivityListById(Long vendorRegisterSid) {
        List<BasVendorRegisterProductivity> response = basVendorRegisterProductivityMapper.selectBasVendorRegisterProductivityList
                (new BasVendorRegisterProductivity().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterProductivity -> {
            MongodbUtil.find(basVendorRegisterProductivity);
        });
        return response;
    }


    /**
     * 新增供应商注册-产能信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterProductivity 供应商注册-产能信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterProductivity(List<BasVendorRegisterProductivity> basVendorRegisterProductivityList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterProductivityList)) {
            return 0;
        }
        basVendorRegisterProductivityList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterProductivityMapper.inserts(basVendorRegisterProductivityList);
        if (row > 0) {
            //插入日志
            basVendorRegisterProductivityList.forEach(basVendorRegisterProductivity -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterProductivity(), basVendorRegisterProductivity);
                MongodbUtil.insertUserLog(basVendorRegisterProductivity.getVendorRegisterProductivitySid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-产能信息
     *
     * @param basVendorRegisterProductivityList 供应商注册-产能信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterProductivity(List<BasVendorRegisterProductivity> basVendorRegisterProductivityList) {
        int row = 0;
        for (BasVendorRegisterProductivity basVendorRegisterProductivity : basVendorRegisterProductivityList) {
            BasVendorRegisterProductivity response = basVendorRegisterProductivityMapper.selectBasVendorRegisterProductivityById
                    (basVendorRegisterProductivity.getVendorRegisterProductivitySid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterProductivity);
            if (msgList.size() > 0) {
                row += basVendorRegisterProductivityMapper.updateById(basVendorRegisterProductivity);
                MongodbUtil.insertUserLog(basVendorRegisterProductivity.getVendorRegisterProductivitySid(),
                        BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-产能信息
     *
     * @param basVendorRegisterProductivityList 供应商注册-产能信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterProductivity(List<BasVendorRegisterProductivity> response, List<BasVendorRegisterProductivity> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterProductivity::getVendorRegisterProductivitySid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterProductivity> updateProductivityList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateProductivityList = request.stream()
                        .filter(item -> item.getVendorRegisterProductivitySid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateProductivityList)) {
                this.deleteBasVendorRegisterProductivityByIds(oldIds);
            } else {
                List<Long> updateIds = updateProductivityList.stream()
                        .map(BasVendorRegisterProductivity::getVendorRegisterProductivitySid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterProductivityByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterProductivity(updateProductivityList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterProductivity> newProductivityList = request.stream()
                .filter(item -> item.getVendorRegisterProductivitySid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newProductivityList)) {
            row += this.insertBasVendorRegisterProductivity(newProductivityList, vendorRegisterSid);
        }
        return row;
    }


    /**
     * 由主表批量删除供应商注册-产能信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterProductivityListByIds(List<Long> vendorRegisterSids){
        List<BasVendorRegisterProductivity> ProductivityList = basVendorRegisterProductivityMapper.selectList(new QueryWrapper<BasVendorRegisterProductivity>().lambda()
                .in(BasVendorRegisterProductivity::getVendorRegisterSid,vendorRegisterSids));
        List<Long> ProductivitySids = ProductivityList.stream().map(BasVendorRegisterProductivity::getVendorRegisterProductivitySid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterProductivityByIds(ProductivitySids);
    }
}
