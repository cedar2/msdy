package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IBasStorehouseService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仓库档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@Service
@SuppressWarnings("all")
public class BasStorehouseServiceImpl extends ServiceImpl<BasStorehouseMapper,BasStorehouse> implements IBasStorehouseService {
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private BasStorehouseAddrMapper basStorehouseAddrMapper;
    @Autowired
    private BasStorehouseAttachMapper basStorehouseAttachMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;


    private static final String TITLE = "仓库档案";

    /**
     * 查询仓库档案
     *
     * @param storehouseSid 仓库档案ID
     * @return 仓库档案
     */
    @Override
    public BasStorehouse selectBasStorehouseById(Long storehouseSid) {
        BasStorehouse basStorehouse = basStorehouseMapper.selectBasStorehouseById(storehouseSid);
        if (basStorehouse == null){
            return null;
        }
        BasStorehouseLocation storehouseLocation = new BasStorehouseLocation();
        storehouseLocation.setStorehouseSid(storehouseSid);
        List<BasStorehouseLocation> basStorehouseLocationList = basStorehouseLocationMapper.selectBasStorehouseLocationList(storehouseLocation);
        basStorehouse.setBasStorehouseLocationList(basStorehouseLocationList);
        //仓库-附件对象
        BasStorehouseAttach basStorehouseAttach = new BasStorehouseAttach();
        List<BasStorehouseAttach> basStorehouseAttachList = basStorehouseAttachMapper.selectBasStorehouseAttachList(new BasStorehouseAttach().setStorehouseSid(storehouseSid));
        basStorehouse.setBasStorehouseAttachList(basStorehouseAttachList);
        //仓库-联系方式对象
        BasStorehouseAddr basStorehouseAddr = new BasStorehouseAddr();
        List<BasStorehouseAddr> basStorehouseAddrList = basStorehouseAddrMapper.selectBasStorehouseAddrList(new BasStorehouseAddr().setStorehouseSid(storehouseSid));
        basStorehouse.setAddrList(basStorehouseAddrList);
        //查询日志信息
        MongodbUtil.findString(basStorehouse);
        return basStorehouse;
    }

    /**
     * 查询仓库档案列表
     *
     * @param basStorehouse 仓库档案
     * @return 仓库档案
     */
    @Override
    public List<BasStorehouse> selectBasStorehouseList(BasStorehouse basStorehouse) {
        return basStorehouseMapper.selectBasStorehouseList(basStorehouse);
    }

    /**
     * 新增仓库档案
     * 需要注意编码重复校验
     * @param request 仓库档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStorehouse(BasStorehouse basStorehouse) {
        BasStorehouse storehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                .eq(BasStorehouse::getStorehouseCode, basStorehouse.getStorehouseCode()));
        if (storehouse != null){
            throw new BaseException("仓库编码已存在，请核实！");
        }
        //验证仓库名称是否重复
        checkNameUnique(basStorehouse);
        checkIsVirtual(basStorehouse);
        //设置确认信息
        setConfirmInfo(basStorehouse);
        basStorehouse.setPlantCode(getPlantCode(basStorehouse.getPlantSid()));
        basStorehouseMapper.insert(basStorehouse);
        //仓库-库位信息对象
        List<BasStorehouseLocation> basStorehouseLocationList = basStorehouse.getBasStorehouseLocationList();
        addBasStorehouseLocation(basStorehouse, basStorehouseLocationList);
        //仓库-联系方式对象
        if (CollectionUtils.isNotEmpty(basStorehouse.getAddrList())) {
            addBasStorehouseAddr(Long.parseLong(basStorehouse.getStorehouseSid()), basStorehouse.getAddrList());
        }
        //仓库-附件对象
        addBasStorehouseAttachment(basStorehouse);
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(basStorehouse.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_storehouse")
                    .setDocumentSid(Long.parseLong(basStorehouse.getStorehouseSid()));
            sysTodoTask.setTitle("仓库档案: " + basStorehouse.getStorehouseCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(basStorehouse.getStorehouseCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbDeal.insert(Long.valueOf(basStorehouse.getStorehouseSid()), basStorehouse.getHandleStatus(), null, TITLE,null);
        return 1;
    }

    /**
     * 得到工厂编码
     */
    private String getPlantCode(Long plantSid){
        if (plantSid == null){
            return null;
        }
        BasPlant plant = basPlantMapper.selectById(plantSid);
        if (plant == null){
            return null;
        }else {
            return plant.getPlantCode();
        }
    }

    /**
     * 仓库-库位信息对象
     */
    private void addBasStorehouseLocation(BasStorehouse basStorehouse, List<BasStorehouseLocation> basStorehouseLocationList) {
        basStorehouseLocationMapper.delete(
                new UpdateWrapper<BasStorehouseLocation>()
                        .lambda()
                        .eq(BasStorehouseLocation::getStorehouseSid, basStorehouse.getStorehouseSid())
        );
        if (CollectionUtils.isNotEmpty(basStorehouseLocationList)) {
            basStorehouseLocationList.forEach(o -> {
                o.setStorehouseSid(Long.parseLong(basStorehouse.getStorehouseSid()));
                o.setHandleStatus(basStorehouse.getHandleStatus());
                basStorehouseLocationMapper.insert(o);
            });
        }
    }

    /**
     * 仓库-联系人信息对象
     */
    private void addBasStorehouseAddr(Long Sid, List<BasStorehouseAddr> basStorehouseAddrList) {
        basStorehouseAddrMapper.delete(
                new UpdateWrapper<BasStorehouseAddr>()
                        .lambda()
                        .eq(BasStorehouseAddr::getStorehouseSid, Sid)
        );
        basStorehouseAddrList.forEach(o -> {
            o.setStorehouseSid(Sid);
            basStorehouseAddrMapper.insert(o);
        });
    }

    /**
     * 验证仓库名称是否重复
     */
    private void checkNameUnique(BasStorehouse basStorehouse) {
        if (basStorehouseMapper.checkNameUnique(basStorehouse.getStorehouseName()) > 0){
            throw new BaseException("仓库名称已存在，请核实！");
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasStorehouse o) {
        if (ConstantsEms.DISENABLE_STATUS.equals(o.getStatus())){
            List<InvInventoryLocation> invInventoryLocationList = new ArrayList<>();
            invInventoryLocationList = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getStorehouseSid,Long.parseLong(o.getStorehouseSid())));
            invInventoryLocationList.forEach(list->{
                BigDecimal sun = list.getUnlimitedQuantity().add(list.getVendorConsignQuantity())
                        .add(list.getVendorSubcontractQuantity()
                                .add(list.getCustomerConsignQuantity()
                                        .add(list.getCustomerSubcontractQuantity())));
                if (sun.compareTo(BigDecimal.ZERO) > 0){
                    throw new BaseException("提示：仓库库存量不为0，不可停用!");
                }
            });
        }
        o.getBasStorehouseLocationList().forEach(item->{
            if (ConstantsEms.DISENABLE_STATUS.equals(item.getStatus())){
                List<InvInventoryLocation> invInventoryLocationList = new ArrayList<>();
                invInventoryLocationList = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getStorehouseLocationSid,item.getStorehouseLocationSid()));
                invInventoryLocationList.forEach(list->{
                    BigDecimal sun = list.getUnlimitedQuantity().add(list.getVendorConsignQuantity())
                            .add(list.getVendorSubcontractQuantity()
                                    .add(list.getCustomerConsignQuantity()
                                            .add(list.getCustomerSubcontractQuantity())));
                    if (sun.compareTo(BigDecimal.ZERO) > 0){
                        throw new BaseException("提示：仓库库存量不为0，不可停用");
                    }
                });
            }
        });
        if (o == null) {
            return;
        }
        o.getBasStorehouseLocationList().forEach(item->{
            if (item.getStorehouseLocationSid() != null){
                BasStorehouseLocation location = basStorehouseLocationMapper.selectById(item.getStorehouseLocationSid());
                item.setCreateDate(location.getCreateDate()).setCreatorAccount(location.getCreatorAccount());
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(location, item);
                if (CollectionUtils.isNotEmpty(msgList)){
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                }
            }
        });
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
            o.getBasStorehouseLocationList().forEach(item->{
                item.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
            });
        }else if (HandleStatus.SAVE.getCode().equals(o.getHandleStatus())) {
            o.setUpdateDate(new Date());
            o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            o.getBasStorehouseLocationList().forEach(item->{
                item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
            });
        }
    }

    /**
     * 修改仓库档案
     *
     * @param request 仓库档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasStorehouse(BasStorehouse basStorehouse) {
        checkIsVirtual(basStorehouse);
        BasStorehouse storehouse = basStorehouseMapper.selectBasStorehouseById(Long.parseLong(basStorehouse.getStorehouseSid()));
        //验证仓库名称是否修改
        if (!basStorehouse.getStorehouseName().equals(storehouse.getStorehouseName())){
            //验证仓库名称是否重复
            checkNameUnique(basStorehouse);
        }
        if (storehouse.getPlantSid() ==null || !storehouse.getPlantSid().equals(basStorehouse.getPlantCode())){
            basStorehouse.setPlantCode(getPlantCode(basStorehouse.getPlantSid()));
        }
        Map<String,Object> queryParams=new HashMap<>();
        queryParams.put("storehouse_code", basStorehouse.getStorehouseCode());
        List<BasStorehouse> queryResult=basStorehouseMapper.selectByMap(queryParams);
        if(queryResult.size()>0){
            for(BasStorehouse item:queryResult){
                if(item.getStorehouseCode().equals(basStorehouse.getStorehouseCode())&&!item.getStorehouseSid().equals(basStorehouse.getStorehouseSid())){
                    throw new CustomException("编码已存在");
                }
            }
        }
        //设置确认信息
        setConfirmInfo(basStorehouse);
        basStorehouseMapper.updateAllById(basStorehouse);
        //仓库-库位信息对象
        List<BasStorehouseLocation> basStorehouseLocationList = basStorehouse.getBasStorehouseLocationList();
        if (CollectionUtils.isNotEmpty(basStorehouseLocationList)) {
            addBasStorehouseLocation(basStorehouse, basStorehouseLocationList);
        }
        //仓库-联系方式对象
        if (CollectionUtils.isNotEmpty(basStorehouse.getAddrList())) {
            addBasStorehouseAddr(Long.parseLong(basStorehouse.getStorehouseSid()), basStorehouse.getAddrList());
        }
        //仓库-附件对象
        addBasStorehouseAttachment(basStorehouse);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basStorehouse.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basStorehouse.getStorehouseSid()));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(storehouse, basStorehouse);
        MongodbDeal.update(Long.valueOf(basStorehouse.getStorehouseSid()), storehouse.getHandleStatus(), basStorehouse.getHandleStatus(), msgList, TITLE, null);
        return 1;
    }

    /**
     * 批量删除仓库档案
     *
     * @param storehouseSids 需要删除的仓库档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasStorehouseByIds(String[] storehouseSids) {
        if (ArrayUtil.isEmpty(storehouseSids)){
            throw new BaseException("请选择行");
        }
        BasStorehouse params = new BasStorehouse();
        params.setStorehouseSid(Joiner.on(";").skipNulls().join(storehouseSids));
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = basStorehouseMapper.countByDomain(params);
        if (count != storehouseSids.length) {
            throw new BaseException("仅保存状态才允许删除");
        }
        //批量删除仓库档案
        basStorehouseMapper.deleteBasStorehouseByIds(storehouseSids);
        //批量删除库位信息
        basStorehouseLocationMapper.deleteBasStorehouseLocationByIds(storehouseSids);
        //批量删除联系人信息
        basStorehouseAddrMapper.delete(new UpdateWrapper<BasStorehouseAddr>().lambda().in(BasStorehouseAddr::getStorehouseSid, storehouseSids));
        //删除仓库-附件清单
        basStorehouseAttachMapper.delete(new QueryWrapper<BasStorehouseAttach>().lambda().in(BasStorehouseAttach::getStorehouseSid, storehouseSids));
        //确认状态后删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, storehouseSids));
        //插入日志
        for (String sid : storehouseSids){
            MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
        }
        return storehouseSids.length;
    }

    /**
     * 虚拟仓的库位必须为虚拟库，请核实！
     *
     * @param BasStorehouse basStorehouse
     * @return 结果
     */
    public void checkIsVirtual(BasStorehouse basStorehouse){
        if (ConstantsEms.YES.equals(basStorehouse.getIsVirtual())){
            if (CollectionUtil.isNotEmpty(basStorehouse.getBasStorehouseLocationList())){
                List<BasStorehouseLocation> locationList = basStorehouse.getBasStorehouseLocationList().stream().filter(o->ConstantsEms.NO.equals(o.getIsVirtual())).collect(Collectors.toList());
                if (locationList != null && locationList.size() > 0){
                    throw new BaseException("虚拟仓的库位必须为虚拟库，请核实！");
                }
            }
        }
    }

    /**
     * 批量确认仓库档案
     *
     * @param basPlant 仓库档案IDS、确认状态
     * @return 结果
     */
    @Override
    public int confirm(BasStorehouse basStorehouse) {
        //仓库档案sids
        String[] storehouseSidList = basStorehouse.getStorehouseSidList();
        if (ArrayUtil.isEmpty(storehouseSidList)){
            throw new BaseException("请选择行");
        }
        BasStorehouse params = new BasStorehouse();
        params.setStorehouseSid(Joiner.on(";").skipNulls().join(storehouseSidList));
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = basStorehouseMapper.countByDomain(params);
        if (count != storehouseSidList.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        for (String storehouseSid : storehouseSidList) {
            BasStorehouse storehouse = this.selectBasStorehouseById(Long.parseLong(storehouseSid));
            checkIsVirtual(storehouse);
        }
        basStorehouse.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        basStorehouse.setConfirmDate(new Date());
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basStorehouse.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, storehouseSidList));
        }
        //插入日志
        for (String sid : storehouseSidList){
            //插入日志
            MongodbDeal.check(Long.parseLong(sid), basStorehouse.getHandleStatus(), null, TITLE, null);
        }
        return basStorehouseMapper.confirm(basStorehouse);
    }

    /**
     * 变更仓库档案
     *
     * @param basPlant 仓库档案
     * @return 结果
     */
    @Override
    public int change(BasStorehouse basStorehouse) {
        checkIsVirtual(basStorehouse);
        setConfirmInfo(basStorehouse);
        String storehouseSid = basStorehouse.getStorehouseSid();
        BasStorehouse storehouse = basStorehouseMapper.selectBasStorehouseById(Long.parseLong(storehouseSid));
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(storehouse.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        //验证仓库名称是否修改
        if (!basStorehouse.getStorehouseName().equals(storehouse.getStorehouseName())){
            checkNameUnique(basStorehouse);
        }
        Map<String,Object> queryParams=new HashMap<>();
        queryParams.put("storehouse_code", basStorehouse.getStorehouseCode());
        List<BasStorehouse> queryResult=basStorehouseMapper.selectByMap(queryParams);
        if(queryResult.size()>0){
            for(BasStorehouse item:queryResult){
                if(item.getStorehouseCode().equals(basStorehouse.getStorehouseCode())&&!item.getStorehouseSid().equals(basStorehouse.getStorehouseSid())){
                    throw new CustomException("编码已存在");
                }
            }
        }
        if (storehouse.getPlantSid() ==null || !storehouse.getPlantSid().equals(basStorehouse.getPlantCode())){
            basStorehouse.setPlantCode(getPlantCode(basStorehouse.getPlantSid()));
        }
        basStorehouseMapper.updateAllById(basStorehouse);
        //仓库-库位信息对象
        List<BasStorehouseLocation> basStorehouseLocationList = basStorehouse.getBasStorehouseLocationList();
        addBasStorehouseLocation(basStorehouse, basStorehouseLocationList);
        //仓库-联系方式对象
        if (CollectionUtils.isNotEmpty(basStorehouse.getAddrList())) {
            addBasStorehouseAddr(Long.parseLong(basStorehouse.getStorehouseSid()), basStorehouse.getAddrList());
        }
        //仓库-附件对象
        addBasStorehouseAttachment(basStorehouse);
        //操作日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(storehouse, basStorehouse);
        MongodbDeal.update(Long.valueOf(storehouseSid), storehouse.getHandleStatus(), basStorehouse.getHandleStatus(), msgList, TITLE, null);
        return 1;
    }

    /**
     * 批量启用/停用仓库档案
     *
     * @param basPlant 仓库档案IDS、启用/停用状态
     * @return 结果
     */
    @Override
    public int status(BasStorehouse basStorehouse) {
        //工厂档案sids
        String[] storehouseSidList = basStorehouse.getStorehouseSidList();

        String remark = StrUtil.isEmpty(basStorehouse.getDisableRemark()) ? null : basStorehouse.getDisableRemark();
        //启用
        if (Status.ENABLE.getCode().equals(basStorehouse.getStatus())){
            BasStorehouse params = new BasStorehouse();
            params.setStorehouseSid(Joiner.on(";").skipNulls().join(storehouseSidList));
            params.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            int count = basStorehouseMapper.countByDomain(params);
            if (count != storehouseSidList.length){
                throw new BaseException("仅确认状态才允许启用");
            }
            //插入日志
            for (String sid : storehouseSidList){
                MongodbDeal.status(Long.valueOf(sid), basStorehouse.getStatus(), null, TITLE, remark);
            }
        }else {
            for (String s : storehouseSidList) {
                List<InvInventoryLocation> invInventoryLocationList = new ArrayList<>();
                invInventoryLocationList = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getStorehouseSid,Long.parseLong(s)));
                invInventoryLocationList.forEach(list->{
                    BigDecimal sun = list.getUnlimitedQuantity().add(list.getVendorConsignQuantity())
                            .add(list.getVendorSubcontractQuantity()
                                    .add(list.getCustomerConsignQuantity()
                                            .add(list.getCustomerSubcontractQuantity())));
                    if (sun.compareTo(BigDecimal.ZERO) > 0){
                        throw new BaseException("提示：仓库库存量不为0，不可停用!");
                    }
                });
            }
            //插入日志
            for (String sid : storehouseSidList) {
                MongodbDeal.status(Long.valueOf(sid), basStorehouse.getStatus(), null, TITLE, remark);
            }
        }
        return basStorehouseMapper.confirm(basStorehouse);
    }

    /**
     * 仓库-附件对象
     */
    private void addBasStorehouseAttachment(BasStorehouse basStorehouse) {
        basStorehouseAttachMapper.delete(
                new UpdateWrapper<BasStorehouseAttach>()
                        .lambda()
                        .eq(BasStorehouseAttach::getStorehouseSid, basStorehouse.getStorehouseSid())
        );
        if (CollectionUtils.isNotEmpty(basStorehouse.getBasStorehouseAttachList())) {
            basStorehouse.getBasStorehouseAttachList().forEach(item -> {
                item.setStorehouseSid(Long.parseLong(basStorehouse.getStorehouseSid()));
                basStorehouseAttachMapper.insert(item);
            });
        }
    }

    /**
     * 仓库档案下拉框列表
     * @return 结果
     */
    @Override
    public List<BasStorehouse> getStorehouseList() {

        BasStorehouse basStorehouse = new BasStorehouse().setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return basStorehouseMapper.getStorehouseList(basStorehouse);
    }

    /**
     * 仓库档案下拉框列表
     * @return 结果
     */
    @Override
    public List<BasStorehouse> getList(BasStorehouse basStorehouse) {
        return basStorehouseMapper.getStorehouseList(basStorehouse);
    }

    /**
     * 获取仓库下库位列表
     *
     * @param storehouseSid 仓库档案ID
     * @return 结果
     */
    @Override
    public List<BasStorehouseLocation> getStorehouseLocationListById(Long storehouseSid) {
        return basStorehouseMapper.getStorehouseLocationListById(storehouseSid);
    }

    /**
     * 获取仓库下库位列表
     *
     * @param storehouseSid 仓库档案ID
     * @return 结果
     */
    @Override
    public List<BasStorehouseLocation> getLocationList(BasStorehouse basStorehouse) {
        return basStorehouseMapper.getLocationList(basStorehouse);
    }

    /**
     * 查询仓库档案列表
     *
     * @param basStorehouse 仓库档案
     * @return 仓库档案
     */
    @Override
    public List<BasStorehouseAddr> selectBasStorehouseAddrList(BasStorehouseAddr addr) {
        return basStorehouseAddrMapper.selectBasStorehouseAddrList(addr);
    }
}
