package com.platform.ems.service.impl;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.form.DevDevelopPlanForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SysUserMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IDevDevelopPlanService;

import static java.util.stream.Collectors.toList;

/**
 * 开发计划Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-08
 */
@Service
@SuppressWarnings("all")
public class DevDevelopPlanServiceImpl extends ServiceImpl<DevDevelopPlanMapper, DevDevelopPlan> implements IDevDevelopPlanService {
    @Autowired
    private DevDevelopPlanMapper devDevelopPlanMapper;
    @Autowired
    private DevDevelopPlanAttachMapper devDevelopPlanAttachMapper;
    @Autowired
    private DevCategoryPlanItemMapper categoryPlanItemMapper;
    @Autowired
    private BasProductSeasonMapper productSeasonMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private PrjProjectMapper projectMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;

    private static final String TITLE = "开发计划";

    /**
     * 查询开发计划
     *
     * @param developPlanSid 开发计划ID
     * @return 开发计划
     */
    @Override
    public DevDevelopPlan selectDevDevelopPlanById(Long developPlanSid) {
        DevDevelopPlan devDevelopPlan = devDevelopPlanMapper.selectDevDevelopPlanById(developPlanSid);
        devDevelopPlan.setAttachmentList(new ArrayList<>());
        // 附件
        List<DevDevelopPlanAttach> attachmentList = devDevelopPlanAttachMapper.selectDevDevelopPlanAttachList(
                new DevDevelopPlanAttach().setDevelopPlanSid(developPlanSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            devDevelopPlan.setAttachmentList(attachmentList);
        }
        // 项目信息
        List<PrjProject> projectList = projectMapper.selectPrjProjectList(new PrjProject().setDevelopPlanSid(developPlanSid));
        devDevelopPlan.setProjectList(new ArrayList<>());
        if (CollectionUtil.isNotEmpty(projectList)) {
            // 按“项目类型名称+所属阶段名称+项目编码”升序排序
            projectList = projectList.stream().sorted(
                    Comparator.comparing(PrjProject::getProjectType, Comparator.nullsLast(String::compareTo))
                            .thenComparing(PrjProject::getProjectPhase, Comparator.nullsLast(String::compareTo))
                            .thenComparing(PrjProject::getProjectCode, Comparator.nullsLast(String::compareTo).thenComparing(Comparator.comparingLong(Long::parseLong)))
            ).collect(toList());
            devDevelopPlan.setProjectList(projectList);
        }
        MongodbUtil.find(devDevelopPlan);
        return devDevelopPlan;
    }

    /**
     * 复制开发计划
     *
     * @param developPlanSid 开发计划ID
     * @return 开发计划
     */
    @Override
    public DevDevelopPlan copyDevDevelopPlanById(Long developPlanSid) {
        DevDevelopPlan devDevelopPlan = devDevelopPlanMapper.selectDevDevelopPlanById(developPlanSid);
        if (devDevelopPlan != null) {
            devDevelopPlan.setCreateDate(null).setCreatorAccount(null).setCreatorAccountName(null)
                    .setUpdaterAccount(null).setUpdaterAccountName(null).setUpdateDate(null)
                    .setConfirmDate(null).setConfirmerAccount(null).setConfirmerAccountName(null);
            devDevelopPlan.setDevelopPlanSid(null).setDevelopPlanCode(null).setHandleStatus(ConstantsEms.SAVA_STATUS);
        }
        return devDevelopPlan;
    }

    /**
     * 查询开发计划列表
     *
     * @param devDevelopPlan 开发计划
     * @return 开发计划
     */
    @Override
    public List<DevDevelopPlan> selectDevDevelopPlanList(DevDevelopPlan devDevelopPlan) {
        return devDevelopPlanMapper.selectDevDevelopPlanList(devDevelopPlan);
    }

    /**
     * 校验编号不能重复
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    private void judgeCode(DevDevelopPlan devDevelopPlan) {
        QueryWrapper<DevDevelopPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DevDevelopPlan::getDevelopPlanCode, devDevelopPlan.getDevelopPlanCode());
        if (devDevelopPlan.getDevelopPlanSid() != null) {
            queryWrapper.lambda().ne(DevDevelopPlan::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid());
        }
        List<DevDevelopPlan> planCodeList = devDevelopPlanMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(planCodeList)) {
            throw new BaseException("开发计划号已存在！");
        }
    }

    /**
     * 校验名称不能重复
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    private void judgeName(DevDevelopPlan devDevelopPlan) {
        QueryWrapper<DevDevelopPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DevDevelopPlan::getDevelopPlanName, devDevelopPlan.getDevelopPlanName());
        if (devDevelopPlan.getDevelopPlanSid() != null) {
            queryWrapper.lambda().ne(DevDevelopPlan::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid());
        }
        List<DevDevelopPlan> planNameList = devDevelopPlanMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(planNameList)) {
            throw new BaseException("开发计划名称已存在！");
        }
    }

    /**
     * 写入字段数据
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    private void setData(DevDevelopPlan devDevelopPlan) {
        if (devDevelopPlan.getDevelopPlanSid() == null) {
            devDevelopPlan.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        }
        // 设置负责人字段
        if (devDevelopPlan.getCategoryPlanItemSid() != null) {
            DevCategoryPlanItem categoryPlanItem = categoryPlanItemMapper.selectById(devDevelopPlan.getCategoryPlanItemSid());
            if (categoryPlanItem != null) {
                devDevelopPlan.setLeaderSid(categoryPlanItem.getNextReceiverSid())
                        .setLeaderCode(categoryPlanItem.getNextReceiverCode());
            }
        }
    }

    /**
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(DevDevelopPlan oldPlan, DevDevelopPlan newPlan) {
        // 产品季
        if (newPlan.getProductSeasonSid() != null && !newPlan.getProductSeasonSid().equals(oldPlan.getProductSeasonSid())) {
            BasProductSeason season = productSeasonMapper.selectById(newPlan.getProductSeasonSid());
            if (season != null) {
                newPlan.setProductSeasonCode(season.getProductSeasonCode());
            } else {
                newPlan.setProductSeasonCode(null);
            }
        } else if (newPlan.getProductSeasonSid() == null) {
            newPlan.setProductSeasonCode(null);
        }
    }

    /**
     * 新增开发计划
     * 需要注意编码重复校验
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevDevelopPlan(DevDevelopPlan devDevelopPlan) {
        //处理编码
        if (StrUtil.isNotBlank(devDevelopPlan.getDevelopPlanCode())){
            String code = devDevelopPlan.getDevelopPlanCode().replaceAll("\\s*", "");
            devDevelopPlan.setDevelopPlanCode(code);
        }
        // 校验编号是否重复
        this.judgeCode(devDevelopPlan);
        // 校验名称是否重复
        this.judgeName(devDevelopPlan);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(devDevelopPlan.getHandleStatus())) {
            devDevelopPlan.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段数据
        this.setData(devDevelopPlan);
        // 写入部分字段的code
        this.setData(new DevDevelopPlan(), devDevelopPlan);
        int row = devDevelopPlanMapper.insert(devDevelopPlan);
        if (row > 0) {
            DevDevelopPlan plan = devDevelopPlanMapper.selectById(devDevelopPlan.getDevelopPlanSid());
            // 写入附件
            if (CollectionUtil.isNotEmpty(devDevelopPlan.getAttachmentList())) {
                devDevelopPlan.getAttachmentList().forEach(item->{
                    item.setDevelopPlanSid(devDevelopPlan.getDevelopPlanSid());
                    item.setDevelopPlanCode(devDevelopPlan.getDevelopPlanCode());
                });
                devDevelopPlanAttachMapper.inserts(devDevelopPlan.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(devDevelopPlan.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_DEV_DEVELOP_PLAN)
                        .setDocumentSid(devDevelopPlan.getDevelopPlanSid());
                sysTodoTask.setTitle("开发计划" + plan.getDevelopPlanName() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(plan.getDevelopPlanCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_DEV_DEVELOP_PLAN);
                try {
                    menu = remoteMenuService.getInfoByName(menu).getData();
                } catch (Exception e){
                    log.warn(ConstantsWorkbench.TODO_DEV_DEVELOP_PLAN + "菜单获取失败！");
                }
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new DevDevelopPlan(), devDevelopPlan);
            MongodbDeal.insert(devDevelopPlan.getDevelopPlanSid(), devDevelopPlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改开发计划
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevDevelopPlan(DevDevelopPlan devDevelopPlan) {
        DevDevelopPlan original = devDevelopPlanMapper.selectDevDevelopPlanById(devDevelopPlan.getDevelopPlanSid());
        // 校验编号不能重复
        if (!devDevelopPlan.getDevelopPlanCode().equals(original.getDevelopPlanCode())) {
            this.judgeCode(devDevelopPlan);
        }
        // 校验名称不能重复
        if (!devDevelopPlan.getDevelopPlanName().equals(original.getDevelopPlanName())) {
            this.judgeName(devDevelopPlan);
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(devDevelopPlan.getHandleStatus())) {
            devDevelopPlan.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, devDevelopPlan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            devDevelopPlan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(original, devDevelopPlan);
        // 更新主表
        int row = devDevelopPlanMapper.updateAllById(devDevelopPlan);
        if (row > 0) {
            // 修改附件
            this.updateDevDevelopPlanAttach(devDevelopPlan);
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(devDevelopPlan.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, devDevelopPlan.getDevelopPlanSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_DEV_DEVELOP_PLAN));
            }
            // 若当前登录账号为租户管理员，则开发计划变更页面，点击确认按钮，若开发计划号修改成功，
            // 根据开发计划SID同步更新项目档案中该开发计划的开发计划号（后端）
            if (!original.getDevelopPlanCode().equals(devDevelopPlan.getDevelopPlanCode())) {
                LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PrjProject::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid());
                updateWrapper.set(PrjProject::getDevelopPlanCode, devDevelopPlan.getDevelopPlanCode());
                projectMapper.update(null, updateWrapper);
            }
            //插入日志
            MongodbDeal.update(devDevelopPlan.getDevelopPlanSid(), original.getHandleStatus(), devDevelopPlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更开发计划
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevDevelopPlan(DevDevelopPlan devDevelopPlan) {
        DevDevelopPlan response = devDevelopPlanMapper.selectDevDevelopPlanById(devDevelopPlan.getDevelopPlanSid());
        // 校验编号不能重复
        if (!devDevelopPlan.getDevelopPlanCode().equals(response.getDevelopPlanCode())) {
            this.judgeCode(devDevelopPlan);
        }
        // 校验名称不能重复
        if (!devDevelopPlan.getDevelopPlanName().equals(response.getDevelopPlanName())) {
            this.judgeName(devDevelopPlan);
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, devDevelopPlan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            devDevelopPlan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(response, devDevelopPlan);
        // 更新主表
        int row = devDevelopPlanMapper.updateAllById(devDevelopPlan);
        if (row > 0) {
            // 修改附件
            this.updateDevDevelopPlanAttach(devDevelopPlan);
            // 若当前登录账号为租户管理员，则开发计划变更页面，点击确认按钮，若开发计划号修改成功，
            // 根据开发计划SID同步更新项目档案中该开发计划的开发计划号（后端）
            if (!response.getDevelopPlanCode().equals(devDevelopPlan.getDevelopPlanCode())) {
                LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PrjProject::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid());
                updateWrapper.set(PrjProject::getDevelopPlanCode, devDevelopPlan.getDevelopPlanCode());
                projectMapper.update(null, updateWrapper);
            }
            //插入日志
            MongodbUtil.insertUserLog(devDevelopPlan.getDevelopPlanSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDevDevelopPlanAttach(DevDevelopPlan devDevelopPlan) {
        // 先删后加
        devDevelopPlanAttachMapper.delete(new QueryWrapper<DevDevelopPlanAttach>().lambda()
                .eq(DevDevelopPlanAttach::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid()));
        if (CollectionUtil.isNotEmpty(devDevelopPlan.getAttachmentList())) {
            devDevelopPlan.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getDevelopPlanAttachSid() == null) {
                    att.setDevelopPlanSid(devDevelopPlan.getDevelopPlanSid());
                    att.setDevelopPlanCode(devDevelopPlan.getDevelopPlanCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            devDevelopPlanAttachMapper.inserts(devDevelopPlan.getAttachmentList());
        }
    }

    /**
     * 批量删除开发计划
     *
     * @param developPlanSids 需要删除的开发计划ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevDevelopPlanByIds(List<Long> developPlanSids) {
        List<DevDevelopPlan> list = devDevelopPlanMapper.selectList(new QueryWrapper<DevDevelopPlan>()
                .lambda().in(DevDevelopPlan::getDevelopPlanSid, developPlanSids));
        // 删除校验
        list = list.stream().filter(o-> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(o.getHandleStatus()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = devDevelopPlanMapper.deleteBatchIds(developPlanSids);
        if (row > 0) {
            // 删除附件
            devDevelopPlanAttachMapper.delete(new QueryWrapper<DevDevelopPlanAttach>().lambda()
                    .in(DevDevelopPlanAttach::getDevelopPlanSid, developPlanSids));
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, developPlanSids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_DEV_DEVELOP_PLAN));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new DevDevelopPlan());
                MongodbUtil.insertUserLog(o.getDevelopPlanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param devDevelopPlan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(DevDevelopPlan devDevelopPlan) {
        int row = 0;
        Long[] sids = devDevelopPlan.getDevelopPlanSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<DevDevelopPlan> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(DevDevelopPlan::getDevelopPlanSid, sids);
            updateWrapper.set(DevDevelopPlan::getHandleStatus, devDevelopPlan.getHandleStatus());
            // 确认人
            if (ConstantsEms.CHECK_STATUS.equals(devDevelopPlan.getHandleStatus())) {
                updateWrapper.set(DevDevelopPlan::getConfirmDate, new Date());
                updateWrapper.set(DevDevelopPlan::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                row = devDevelopPlanMapper.update(null, updateWrapper);
            }
            // 作废说明
            else if (ConstantsEms.INVALID_STATUS.equals(devDevelopPlan.getHandleStatus())) {
                List<DevDevelopPlan> list = devDevelopPlanMapper.selectList(new QueryWrapper<DevDevelopPlan>().lambda()
                        .in(DevDevelopPlan::getDevelopPlanSid, sids)
                        .eq(DevDevelopPlan::getHandleStatus, ConstantsEms.CHECK_STATUS));
                if (CollectionUtil.isNotEmpty(list) && list.size() == sids.length) {
                    updateWrapper.set(DevDevelopPlan::getCancelRemark, devDevelopPlan.getCancelRemark());
                    row = devDevelopPlanMapper.update(null, updateWrapper);
                }
                else {
                    throw new BaseException("所选数据非'已确认'状态，无法作废！");
                }
            }
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(devDevelopPlan.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sids)
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_DEV_DEVELOP_PLAN));
                }
                if (ConstantsEms.INVALID_STATUS.equals(devDevelopPlan.getHandleStatus())) {
                    for (Long id : sids) {
                        //插入日志
                        MongodbUtil.insertUserLog(id, BusinessType.CANCEL.getValue(), null, TITLE, devDevelopPlan.getCancelRemark());
                    }
                }
                else {
                    for (Long id : sids) {
                        //插入日志
                        MongodbDeal.check(id, devDevelopPlan.getHandleStatus(), null, TITLE, null);
                    }
                }
            }
        }
        return row;
    }

    /**
     * 设置开发计划负责人
     * @param devDevelopPlan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setLeader(DevDevelopPlan devDevelopPlan) {
        if (devDevelopPlan.getDevelopPlanSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<DevDevelopPlan> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (devDevelopPlan.getLeaderSid() == null) {
            devDevelopPlan.setLeaderCode(null);
        }
        else {
            SysUser user = sysUserMapper.selectById(devDevelopPlan.getLeaderSid());
            if (user != null) {
                devDevelopPlan.setLeaderCode(user.getUserName());
            }
        }
        // 开发计划负责人
        updateWrapper.in(DevDevelopPlan::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSidList())
                .set(DevDevelopPlan::getLeaderSid, devDevelopPlan.getLeaderSid())
                .set(DevDevelopPlan::getLeaderCode, devDevelopPlan.getLeaderCode());
        row = devDevelopPlanMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 查询开发计划报表
     *
     * @param devDevelopPlanForm 开发计划
     * @return 开发计划集合
     */
    @Override
    public List<DevDevelopPlanForm> selectDevDevelopPlanForm(DevDevelopPlanForm devDevelopPlanForm) {
        return devDevelopPlanMapper.selectDevDevelopPlanForm(devDevelopPlanForm);
    }

    /**
     * 修改品类规划
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCategoryPlan(DevDevelopPlan devDevelopPlan) {
        int row = 0;
        if (devDevelopPlan.getDevelopPlanSid() == null) {
            throw new BaseException("请选择行！");
        }
        if (devDevelopPlan.getCategoryPlanSid() == null || devDevelopPlan.getCategoryPlanItemSid() == null) {
            throw new BaseException("请选择品类规划！");
        }
        // 获取原数据
        DevDevelopPlan origin = devDevelopPlanMapper.selectById(devDevelopPlan.getDevelopPlanSid());
        // 修改品类规划
        LambdaUpdateWrapper<DevDevelopPlan> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DevDevelopPlan::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid());
        updateWrapper.set(DevDevelopPlan::getCategoryPlanSid, devDevelopPlan.getCategoryPlanSid());
        updateWrapper.set(DevDevelopPlan::getCategoryPlanCode, devDevelopPlan.getCategoryPlanCode());
        updateWrapper.set(DevDevelopPlan::getCategoryPlanItemSid, devDevelopPlan.getCategoryPlanItemSid());
        updateWrapper.set(DevDevelopPlan::getCategoryPlanItemNum, devDevelopPlan.getCategoryPlanItemNum());
        row = devDevelopPlanMapper.update(new DevDevelopPlan(), updateWrapper);
        if (row == 0 && origin.getCategoryPlanItemSid().equals(devDevelopPlan.getCategoryPlanItemSid())) {
            row = 1;
        }
        // 更新项目档案
        LambdaUpdateWrapper<PrjProject> updateProject = new LambdaUpdateWrapper<>();
        updateProject.eq(PrjProject::getDevelopPlanSid, devDevelopPlan.getDevelopPlanSid());
        updateProject.set(PrjProject::getCategoryPlanSid, devDevelopPlan.getCategoryPlanSid());
        updateProject.set(PrjProject::getCategoryPlanCode, devDevelopPlan.getCategoryPlanCode());
        projectMapper.update(null, updateProject);
        // 操作日志
        String oldCode = origin.getCategoryPlanCode() == null ? "" : origin.getCategoryPlanCode();
        String oldItemNum = origin.getCategoryPlanItemNum() == null ? "" : origin.getCategoryPlanItemNum().toString();
        String newItemNum = devDevelopPlan.getCategoryPlanItemNum() == null ? "" : devDevelopPlan.getCategoryPlanItemNum().toString();
        String comment = "品类规划变更，变更前：" + oldCode + "，行号" + oldItemNum + "；变更后："
                + devDevelopPlan.getCategoryPlanCode() + "，行号" + newItemNum;
        // 更新人更新日期
        DevDevelopPlan newData = new DevDevelopPlan();
        BeanCopyUtils.copyProperties(origin, newData);
        newData.setCategoryPlanSid(devDevelopPlan.getCategoryPlanSid());
        newData.setCategoryPlanCode(devDevelopPlan.getCategoryPlanCode());
        newData.setCategoryPlanItemSid(devDevelopPlan.getCategoryPlanItemSid());
        newData.setCategoryPlanItemNum(devDevelopPlan.getCategoryPlanItemNum());
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(origin, newData);
        MongodbUtil.insertUserLog(devDevelopPlan.getDevelopPlanSid(), BusinessType.QITA.getValue(), msgList, TITLE, comment);
        return 1;
    }

}
