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
import com.platform.ems.domain.BasVendorRegisterMachine;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterMachineMapper;
import com.platform.ems.service.IBasVendorRegisterMachineService;

/**
 * 供应商注册-设备信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterMachineServiceImpl extends ServiceImpl<BasVendorRegisterMachineMapper, BasVendorRegisterMachine> implements IBasVendorRegisterMachineService {
    @Autowired
    private BasVendorRegisterMachineMapper basVendorRegisterMachineMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-设备信息";

    /**
     * 查询供应商注册-设备信息
     *
     * @param vendorRegisterMachineSid 供应商注册-设备信息ID
     * @return 供应商注册-设备信息
     */
    @Override
    public BasVendorRegisterMachine selectBasVendorRegisterMachineById(Long vendorRegisterMachineSid) {
        BasVendorRegisterMachine basVendorRegisterMachine = basVendorRegisterMachineMapper.selectBasVendorRegisterMachineById(vendorRegisterMachineSid);
        MongodbUtil.find(basVendorRegisterMachine);
        return basVendorRegisterMachine;
    }

    /**
     * 查询供应商注册-设备信息列表
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 供应商注册-设备信息
     */
    @Override
    public List<BasVendorRegisterMachine> selectBasVendorRegisterMachineList(BasVendorRegisterMachine basVendorRegisterMachine) {
        return basVendorRegisterMachineMapper.selectBasVendorRegisterMachineList(basVendorRegisterMachine);
    }

    /**
     * 新增供应商注册-设备信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterMachine(BasVendorRegisterMachine basVendorRegisterMachine) {
        int row = basVendorRegisterMachineMapper.insert(basVendorRegisterMachine);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterMachine(), basVendorRegisterMachine);
            MongodbUtil.insertUserLog(basVendorRegisterMachine.getVendorRegisterMachineSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-设备信息
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterMachine(BasVendorRegisterMachine basVendorRegisterMachine) {
        BasVendorRegisterMachine response = basVendorRegisterMachineMapper.selectBasVendorRegisterMachineById(basVendorRegisterMachine.getVendorRegisterMachineSid());
        int row = basVendorRegisterMachineMapper.updateById(basVendorRegisterMachine);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterMachine.getVendorRegisterMachineSid(), BusinessType.UPDATE.ordinal(), response, basVendorRegisterMachine, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-设备信息
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterMachine(BasVendorRegisterMachine basVendorRegisterMachine) {
        BasVendorRegisterMachine response = basVendorRegisterMachineMapper.selectBasVendorRegisterMachineById(basVendorRegisterMachine.getVendorRegisterMachineSid());
        int row = basVendorRegisterMachineMapper.updateAllById(basVendorRegisterMachine);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterMachine.getVendorRegisterMachineSid(), BusinessType.CHANGE.ordinal(), response, basVendorRegisterMachine, TITLE);
        }
        return row;
    }


    /**
     * 批量删除供应商注册-设备信息
     *
     * @param vendorRegisterMachineSids 需要删除的供应商注册-设备信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterMachineByIds(List<Long> vendorRegisterMachineSids) {
        int row = 0;
        for (Long sid : vendorRegisterMachineSids) {
            BasVendorRegisterMachine response = basVendorRegisterMachineMapper.selectById(sid);
            row += basVendorRegisterMachineMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterMachine());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表查询供应商注册-设备信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-设备信息集合
     */
    @Override
    public List<BasVendorRegisterMachine> selectBasVendorRegisterMachineListById(Long vendorRegisterSid) {
        List<BasVendorRegisterMachine> response = basVendorRegisterMachineMapper.selectBasVendorRegisterMachineList
                (new BasVendorRegisterMachine().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterMachine -> {
            MongodbUtil.find(basVendorRegisterMachine);
        });
        return response;
    }


    /**
     * 新增供应商注册-设备信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterMachine 供应商注册-设备信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterMachine(List<BasVendorRegisterMachine> basVendorRegisterMachineList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterMachineList)) {
            return 0;
        }
        basVendorRegisterMachineList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterMachineMapper.inserts(basVendorRegisterMachineList);
        if (row > 0) {
            //插入日志
            basVendorRegisterMachineList.forEach(basVendorRegisterMachine -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterMachine(), basVendorRegisterMachine);
                MongodbUtil.insertUserLog(basVendorRegisterMachine.getVendorRegisterMachineSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-设备信息
     *
     * @param basVendorRegisterMachineList 供应商注册-设备信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterMachine(List<BasVendorRegisterMachine> basVendorRegisterMachineList) {
        int row = 0;
        for (BasVendorRegisterMachine basVendorRegisterMachine : basVendorRegisterMachineList) {
            BasVendorRegisterMachine response = basVendorRegisterMachineMapper.selectBasVendorRegisterMachineById(basVendorRegisterMachine.getVendorRegisterMachineSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterMachine);
            if (msgList.size() > 0) {
                row += basVendorRegisterMachineMapper.updateById(basVendorRegisterMachine);
                MongodbUtil.insertUserLog(basVendorRegisterMachine.getVendorRegisterMachineSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-设备信息
     *
     * @param basVendorRegisterMachineList 供应商注册-设备信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterMachine(List<BasVendorRegisterMachine> response, List<BasVendorRegisterMachine> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterMachine::getVendorRegisterMachineSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterMachine> updateMachineList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateMachineList = request.stream().filter(item -> item.getVendorRegisterMachineSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateMachineList)) {
                this.deleteBasVendorRegisterMachineByIds(oldIds);
            } else {
                List<Long> updateIds = updateMachineList.stream().map(BasVendorRegisterMachine::getVendorRegisterMachineSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterMachineByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterMachine(updateMachineList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterMachine> newMachineList = request.stream().filter(item -> item.getVendorRegisterMachineSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newMachineList)) {
            row += this.insertBasVendorRegisterMachine(newMachineList, vendorRegisterSid);
        }
        return row;
    }

    /**
     * 由主表批量删除供应商注册-设备信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterMachineListByIds(List<Long> vendorRegisterSids){
        List<BasVendorRegisterMachine> MachineList = basVendorRegisterMachineMapper.selectList(new QueryWrapper<BasVendorRegisterMachine>().lambda()
                .in(BasVendorRegisterMachine::getVendorRegisterSid,vendorRegisterSids));
        List<Long> MachineSids = MachineList.stream().map(BasVendorRegisterMachine::getVendorRegisterMachineSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterMachineByIds(MachineSids);
    }
}
