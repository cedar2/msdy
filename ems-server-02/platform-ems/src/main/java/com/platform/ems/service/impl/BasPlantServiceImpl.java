package com.platform.ems.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.platform.common.exception.base.BaseException;
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
import com.platform.ems.service.IBasPlantService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工厂档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-15
 */
@Service
@SuppressWarnings("all")
public class BasPlantServiceImpl extends ServiceImpl<BasPlantMapper,BasPlant> implements IBasPlantService {
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasPlantAddrMapper basPlantAddrMapper;
    @Autowired
    private BasPlantCapacityMapper basPlantCapacityMapper;
    @Autowired
    private BasPlantCategoryMapper basPlantCategoryMapper;
    @Autowired
    private BasPlantProdLineMapper basPlantProdLineMapper;
    @Autowired
    private BasPlantAttachMapper basPlantAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private BasDepartmentServiceImpl basDepartmentService;


    private static final String TITLE = "工厂档案";

    /**
     * 查询工厂档案
     *
     * @param plantSid 工厂档案ID
     * @return 工厂档案
     */
    @Override
    public BasPlant selectBasPlantById(Long plantSid) {
        BasPlant basPlant = basPlantMapper.selectBasPlantById(plantSid);
        if (basPlant == null){
            return null;
        }
        //工厂-联系方式信息
        BasPlantAddr basPlantAddr = new BasPlantAddr();
        basPlantAddr.setPlantSid(plantSid);
        List<BasPlantAddr> plantAddrList = basPlantAddrMapper.selectBasPlantAddrList(basPlantAddr);
        //工厂-擅长品类信息
        BasPlantCategory basPlantCategory = new BasPlantCategory();
        basPlantCategory.setPlantSid(plantSid);
        List<BasPlantCategory> basPlantCategoryList = basPlantCategoryMapper.selectBasPlantCategoryList(basPlantCategory);
        //工厂-生产线信息
        BasPlantProdLine basPlantProdLine = new BasPlantProdLine();
        basPlantProdLine.setPlantSid(plantSid);
        List<BasPlantProdLine> basPlantProdLineList = basPlantProdLineMapper.selectBasPlantProdLineList(basPlantProdLine);

        basPlant.setBasPlantAddrList(plantAddrList);
        basPlant.setBasPlantCategoryList(basPlantCategoryList);
        basPlant.setBasPlantProdLineList(basPlantProdLineList);
        //工厂-附件对象
        BasPlantAttach basPlantAttach = new BasPlantAttach();
        List<BasPlantAttach> basPlantAttachList = basPlantAttachMapper.selectList(new QueryWrapper<BasPlantAttach>()
                .lambda().eq(BasPlantAttach::getPlantSid, plantSid));
        basPlant.setBasPlantAttachList(basPlantAttachList);
        //查询日志信息
        MongodbUtil.findString(basPlant);
        return basPlant;
    }

    /**
     * 查询工厂档案的编码和名称
     *
     * @param plantSid 工厂档案ID
     * @return 工厂档案
     */
    @Override
    public BasPlant selectCodeNameById(Long plantSid) {
        return basPlantMapper.selectById(plantSid);
    }

    /**
     * 查询工厂档案列表
     *
     * @param basPlant 工厂档案
     * @return 工厂档案
     */
    @Override
    public List<BasPlant> selectBasPlantList(BasPlant basPlant) {
        return basPlantMapper.selectBasPlantList(basPlant);
    }

    /**
     * 新增工厂档案
     * 需要注意编码重复校验
     * @param request 工厂档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPlant(BasPlant basPlant) {
        //验证工厂编码、名称是否重复
        checkCodeUnique(basPlant);
        checkNameUnique(basPlant);
        //设置确认信息
        setConfirmInfo(basPlant);
        //验证工厂简称是否重复
        List<BasPlant> basPlants = basPlantMapper.selectList(new QueryWrapper<BasPlant>().lambda()
                .eq(BasPlant::getShortName, basPlant.getShortName()));
        if(CollectionUtils.isNotEmpty(basPlants)){
            throw new BaseException("工厂简称已存在，请核实！");
        }
        LoginUser loginUser = ApiThreadLocalUtil.get();
        basPlant.setCreateDate(new Date()).setCreatorAccount(loginUser.getUsername());

        basPlantMapper.insert(basPlant);

        //工厂-联系方式信息
        List<BasPlantAddr> basPlantAddrList = basPlant.getBasPlantAddrList();
        if (CollectionUtils.isNotEmpty(basPlantAddrList)){
            addBasPlantAddr(basPlant, basPlantAddrList);
        }
        //工厂-擅长品类信息
        List<BasPlantCategory> basPlantCategoryList = basPlant.getBasPlantCategoryList();
        if (CollectionUtils.isNotEmpty(basPlantCategoryList)){
            addBasPlantCategory(basPlant, basPlantCategoryList);
        }
        //工厂-生产线信息
        List<BasPlantProdLine> basPlantProdLineList = basPlant.getBasPlantProdLineList();
        if (CollectionUtils.isNotEmpty(basPlantProdLineList)){
            addBasPlantProdLine(basPlant, basPlantProdLineList);
            //工厂-富余产能明细
            for (BasPlantProdLine basPlantProdLine : basPlantProdLineList) {
                List<BasPlantCapacity> basPlantCapacityList = basPlantProdLine.getBasPlantCapacityList();
                if (CollectionUtils.isNotEmpty(basPlantCapacityList)){
                    addPlantProdLine(basPlantProdLine, basPlantCapacityList);
                }
            }
        }
        //工厂-附件对象
        addBasPlantAttachment(basPlant);
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(basPlant.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_plant")
                    .setDocumentSid(Long.parseLong(basPlant.getPlantSid()));
            sysTodoTask.setTitle("工厂档案: " + basPlant.getPlantCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(basPlant.getPlantCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbDeal.insert(Long.valueOf(basPlant.getPlantSid()), basPlant.getHandleStatus(), msgList, TITLE,null);
        return 1;
    }

    /**
     * 工厂-联系方式信息
     */
    private void addBasPlantAddr(BasPlant basPlant, List<BasPlantAddr> basPlantAddrList) {
        basPlantAddrMapper.delete(
                new UpdateWrapper<BasPlantAddr>()
                        .lambda()
                        .eq(BasPlantAddr::getPlantSid, basPlant.getPlantSid())
        );
        basPlantAddrList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setPlantContactSid(IdWorker.getId());
            o.setPlantSid(Long.parseLong(basPlant.getPlantSid()));
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            o.setCreateDate(new Date());
            basPlantAddrMapper.insert(o);
        });
    }

    /**
     * 工厂-擅长品类信息
     */
    private void addBasPlantCategory(BasPlant basPlant, List<BasPlantCategory> basPlantCategoryList) {
        basPlantCategoryMapper.delete(
                new UpdateWrapper<BasPlantCategory>()
                        .lambda()
                        .eq(BasPlantCategory::getPlantSid, basPlant.getPlantSid())
        );
        basPlantCategoryList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setPlantCategorySid(IdWorker.getId());
            o.setPlantSid(Long.parseLong(basPlant.getPlantSid()));
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            o.setCreateDate(new Date());
            basPlantCategoryMapper.insert(o);
        });
    }

    /**
     * 工厂-生产线信息
     */
    private void addBasPlantProdLine(BasPlant basPlant, List<BasPlantProdLine> basPlantProdLineList) {
        basPlantProdLineMapper.delete(
                new UpdateWrapper<BasPlantProdLine>()
                        .lambda()
                        .eq(BasPlantProdLine::getPlantSid, basPlant.getPlantSid())
        );
        basPlantProdLineList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setProductLineSid(IdWorker.getId());
            o.setPlantSid(Long.parseLong(basPlant.getPlantSid()));
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            o.setCreateDate(new Date());
            basPlantProdLineMapper.insert(o);
        });
    }

    /**
     * 工厂-富余产能明细
     */
    private void addPlantProdLine(BasPlantProdLine basPlantProdLine, List<BasPlantCapacity> basPlantCapacityList) {
        basPlantCapacityMapper.delete(
                new UpdateWrapper<BasPlantCapacity>()
                        .lambda()
                        .eq(BasPlantCapacity::getProductLineSid, basPlantProdLine.getProductLineSid())
        );
        basPlantCapacityList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setProductLineSid(IdWorker.getId());
            o.setProductLineSid(basPlantProdLine.getProductLineSid());
            o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            o.setCreateDate(new Date());
            basPlantCapacityMapper.insert(o);
        });
    }

    /**
     * 验证工厂编码是否重复
     */
    private void checkCodeUnique(BasPlant basPlant) {
        if (basPlantMapper.checkCodeUnique(basPlant.getPlantCode()) > 0){
            throw new BaseException("工厂编码已存在，请核实！");
        }
    }

    /**
     * 验证工厂名称是否重复 简称
     */
    private void checkNameUnique(BasPlant basPlant) {
        if (basPlantMapper.checkNameUnique(basPlant.getPlantName()) > 0){
            throw new BaseException("工厂名称已存在，请核实！");
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasPlant o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改工厂档案
     *
     * @param request 工厂档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPlant(BasPlant basPlant) {
        BasPlant plant = basPlantMapper.selectBasPlantById(Long.parseLong(basPlant.getPlantSid()));
        //验证工厂名称是否修改
        if (!basPlant.getPlantName().equals(plant.getPlantName())){
            //验证工厂名称是否重复
            checkNameUnique(basPlant);
        }
        if(!basPlant.getShortName().equals(plant.getShortName())){
            //验证工厂简称是否重复
            List<BasPlant> basPlants = basPlantMapper.selectList(new QueryWrapper<BasPlant>().lambda()
                    .eq(BasPlant::getShortName, basPlant.getShortName()));
            if(CollectionUtils.isNotEmpty(basPlants)){
                throw new BaseException("工厂简称已存在，请核实！");
            }
        }
        basPlant.setCreatorAccount(plant.getCreatorAccount());
        basPlant.setCreateDate(plant.getCreateDate());
        basPlant.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        basPlant.setUpdateDate(new Date());
        //设置确认信息
        setConfirmInfo(basPlant);
        basPlantMapper.updateAllById(basPlant);

        //工厂-联系方式信息
        List<BasPlantAddr> basPlantAddrList = basPlant.getBasPlantAddrList();
        if (CollectionUtils.isNotEmpty(basPlantAddrList)){
            addBasPlantAddr(basPlant, basPlantAddrList);
        }
        //工厂-擅长品类信息
        List<BasPlantCategory> basPlantCategoryList = basPlant.getBasPlantCategoryList();
        if (CollectionUtils.isNotEmpty(basPlantCategoryList)){
            addBasPlantCategory(basPlant, basPlantCategoryList);
        }
        //工厂-生产线信息
        List<BasPlantProdLine> basPlantProdLineList = basPlant.getBasPlantProdLineList();
        if (CollectionUtils.isNotEmpty(basPlantProdLineList)){
            addBasPlantProdLine(basPlant, basPlantProdLineList);
            //工厂-富余产能明细
            for (BasPlantProdLine basPlantProdLine : basPlantProdLineList) {
                List<BasPlantCapacity> basPlantCapacityList = basPlantProdLine.getBasPlantCapacityList();
                if (CollectionUtils.isNotEmpty(basPlantCapacityList)){
                    addPlantProdLine(basPlantProdLine, basPlantCapacityList);
                }
            }
        }
        //工厂-附件对象
        addBasPlantAttachment(basPlant);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basPlant.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basPlant.getPlantSid()));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(plant, basPlant);
        MongodbDeal.update(Long.parseLong(basPlant.getPlantSid()), plant.getHandleStatus(), basPlant.getHandleStatus(), msgList, TITLE, null);
        return 1;
    }

    /**
     * 批量删除工厂档案
     *
     * @param plantSids 需要删除的工厂档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPlantByIds(String[] plantSids) {
        if (ArrayUtil.isEmpty(plantSids)){
            throw new BaseException("请选择行");
        }
        BasPlant params = new BasPlant();
        params.setPlantSid(Joiner.on(";").skipNulls().join(plantSids));
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = basPlantMapper.countByDomain(params);
        if (count != plantSids.length){
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除工厂档案
        basPlantMapper.deleteBasPlantByIds(plantSids);
        //删除工厂-联系方式信息
        basPlantAddrMapper.deleteBasPlantAddrByIds(plantSids);
        //删除工厂-擅长品类信息
        basPlantCategoryMapper.deletePlantCategoryByIds(plantSids);
        //删除工厂-富余产能明细
        basPlantProdLineMapper.delete(new QueryWrapper<BasPlantProdLine>().lambda().in(BasPlantProdLine::getPlantSid, plantSids));
        //删除工厂-附件清单
        basPlantAttachMapper.delete(new QueryWrapper<BasPlantAttach>().lambda().in(BasPlantAttach::getPlantSid, plantSids));
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, plantSids));
        //插入日志
        for (String sid : plantSids){
            MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
        }
        return plantSids.length;
    }

    /**
     * 批量确认工厂档案
     *
     * @param basPlant 工厂档案IDS、确认状态
     * @return 结果
     */
    @Override
    public int confirm(BasPlant basPlant) {
        //工厂档案sids
        String[] plantSidList = basPlant.getPlantSidList();
        if (ArrayUtil.isEmpty(plantSidList)){
            throw new BaseException("请选择行");
        }
        BasPlant params = new BasPlant();
        params.setPlantSid(Joiner.on(";").skipNulls().join(plantSidList));
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = basPlantMapper.countByDomain(params);
        if (count != plantSidList.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        basPlant.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        basPlant.setConfirmDate(new Date());
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basPlant.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, plantSidList));
        }
        //插入日志
        for (String sid : plantSidList){
            //插入日志
            MongodbDeal.check(Long.parseLong(sid), basPlant.getHandleStatus(), null,TITLE, null);
        }
        return basPlantMapper.confirm(basPlant);
    }

    /**
     * 变更工厂档案
     *
     * @param basPlant 工厂档案
     * @return 结果
     */
    @Override
    public int change(BasPlant basPlant) {
        String plantSid = basPlant.getPlantSid();
        BasPlant plant = basPlantMapper.selectBasPlantById(Long.parseLong(plantSid));
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(plant.getHandleStatus())){
            throw new BaseException("仅确认状态才允许变更");
        }
        //验证工厂名称是否修改
        if (!basPlant.getPlantName().equals(plant.getPlantName())){
            //验证工厂名称是否重复
            checkNameUnique(basPlant);
        }
        if(!basPlant.getShortName().equals(plant.getShortName())){
            //验证工厂简称是否重复
            List<BasPlant> basPlants = basPlantMapper.selectList(new QueryWrapper<BasPlant>().lambda()
                    .eq(BasPlant::getShortName, basPlant.getShortName()));
            if(CollectionUtils.isNotEmpty(basPlants)){
                throw new BaseException("工厂简称已存在，请核实！");
            }
        }
        basPlantMapper.updateAllById(basPlant);
        //工厂-联系方式信息
        List<BasPlantAddr> basPlantAddrList = basPlant.getBasPlantAddrList();
        if (CollectionUtils.isNotEmpty(basPlantAddrList)){
            addBasPlantAddr(basPlant, basPlantAddrList);
        }
        //工厂-擅长品类信息
        List<BasPlantCategory> basPlantCategoryList = basPlant.getBasPlantCategoryList();
        if (CollectionUtils.isNotEmpty(basPlantCategoryList)){
            addBasPlantCategory(basPlant, basPlantCategoryList);
        }
        //工厂-生产线信息
        List<BasPlantProdLine> basPlantProdLineList = basPlant.getBasPlantProdLineList();
        if (CollectionUtils.isNotEmpty(basPlantProdLineList)){
            addBasPlantProdLine(basPlant, basPlantProdLineList);
            //工厂-富余产能明细
            for (BasPlantProdLine basPlantProdLine : basPlantProdLineList) {
                List<BasPlantCapacity> basPlantCapacityList = basPlantProdLine.getBasPlantCapacityList();
                if (CollectionUtils.isNotEmpty(basPlantCapacityList)){
                    addPlantProdLine(basPlantProdLine, basPlantCapacityList);
                }
            }
        }
        //工厂-附件对象
        addBasPlantAttachment(basPlant);
        //插入日志
        MongodbUtil.insertUserLog(Long.valueOf(plantSid), BusinessType.CHANGE.getValue(), TITLE);
        return 1;
    }

    /**
     * 批量启用/停用工厂档案
     *
     * @param basPlant 工厂档案IDS、启用/停用状态
     * @return 结果
     */
    @Override
    public int status(BasPlant basPlant) {
        //工厂档案sids
        String[] plantSidList = basPlant.getPlantSidList();

        //启用
        if (Status.ENABLE.getCode().equals(basPlant.getStatus())){
            BasPlant params = new BasPlant();
            params.setPlantSid(Joiner.on(";").skipNulls().join(plantSidList));
            params.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            int count = basPlantMapper.countByDomain(params);
            if (count != plantSidList.length){
                throw new BaseException("仅确认状态才允许启用");
            }
        }
        //插入日志
        for (String sid : plantSidList){
            String remark = StrUtil.isEmpty(basPlant.getDisableRemark()) ? null : basPlant.getDisableRemark();
            MongodbDeal.status(Long.valueOf(sid), basPlant.getStatus(), null, TITLE, remark);
        }
        return basPlantMapper.confirm(basPlant);
    }

    /**
     * 工厂-附件对象
     */
    private void addBasPlantAttachment(BasPlant basPlant) {
        basPlantAttachMapper.delete(
                new UpdateWrapper<BasPlantAttach>()
                        .lambda()
                        .eq(BasPlantAttach::getPlantSid, basPlant.getPlantSid())
        );
        if (CollectionUtils.isNotEmpty(basPlant.getBasPlantAttachList())) {
            basPlant.getBasPlantAttachList().forEach(item -> {
                item.setPlantSid(Long.parseLong(basPlant.getPlantSid()));
                basPlantAttachMapper.insert(item);
            });
        }
    }

    /**
     * 工厂档案下拉框列表
     * @return 结果
     */
    @Override
    public List<BasPlant> getPlantList(BasPlant basPlant) {
        return basPlantMapper.getPlantList(basPlant);
    }

    @Override
    public List<BasDepartment> getDepartmentList(BasPlant basPlant) {
        BasPlant plant = basPlantMapper.selectBasPlantById(Long.parseLong(basPlant.getPlantSid()));
        if (plant == null) {
            return null;
        }
        List<BasDepartment> departmentList = basDepartmentService.getCompanyDept(Long.parseLong(plant.getCompanySid()));
        departmentList = departmentList.stream().sorted(Comparator.comparing(BasDepartment::getDepartmentName)).collect(Collectors.toList());
        return departmentList;
    }
}
