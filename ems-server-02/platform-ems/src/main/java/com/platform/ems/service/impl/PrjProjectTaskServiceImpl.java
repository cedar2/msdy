package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPdm;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.BasPosition;
import com.platform.ems.domain.PrjProject;
import com.platform.ems.domain.PrjProjectTask;
import com.platform.ems.domain.PrjProjectTaskAttach;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.form.PrjProjectTaskFormRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.form.PrjProjectTaskFormResponse;
import com.platform.ems.domain.dto.response.form.PrjProjectTaskPreCondition;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.BasPositionMapper;
import com.platform.ems.mapper.PrjProjectTaskAttachMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.IPrjProjectTaskService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 项目档案-任务Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-15
 */
@Service
@SuppressWarnings("all")
public class PrjProjectTaskServiceImpl extends ServiceImpl<PrjProjectTaskMapper, PrjProjectTask> implements IPrjProjectTaskService {
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private PrjProjectTaskAttachMapper prjProjectTaskAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private BasPositionMapper basPositionMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "项目档案-任务";

    /**
     * 查询项目档案-任务
     *
     * @param projectTaskSid 项目档案-任务ID
     * @return 项目档案-任务
     */
    @Override
    public PrjProjectTask selectPrjProjectTaskById(Long projectTaskSid) {
        PrjProjectTask prjProjectTask = prjProjectTaskMapper.selectPrjProjectTaskById(projectTaskSid);
        // 处理岗位
        this.getPosition(prjProjectTask);
        // 附件
        prjProjectTask.setAttachmentList(new ArrayList<>());
        List<PrjProjectTaskAttach> attachmentList = prjProjectTaskAttachMapper.selectPrjProjectTaskAttachList(
                new PrjProjectTaskAttach().setProjectTaskSid(projectTaskSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            prjProjectTask.setAttachmentList(attachmentList);
        }
        MongodbUtil.find(prjProjectTask);
        return prjProjectTask;
    }

    /**
     * 查询项目档案-任务列表
     *
     * @param prjProjectTask 项目档案-任务
     * @return 项目档案-任务
     */
    @Override
    public List<PrjProjectTask> selectPrjProjectTaskList(PrjProjectTask prjProjectTask) {
        return prjProjectTaskMapper.selectPrjProjectTaskList(prjProjectTask);
    }

    /**
     * 新增项目档案-任务
     * 需要注意编码重复校验
     *
     * @param prjProjectTask 项目档案-任务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjProjectTask(PrjProjectTask prjProjectTask) {
        int row = prjProjectTaskMapper.insert(prjProjectTask);
        if (row > 0) {
            // 写入附件
            if (CollectionUtil.isNotEmpty(prjProjectTask.getAttachmentList())) {
                prjProjectTask.getAttachmentList().forEach(item->{
                    item.setProjectSid(prjProjectTask.getProjectSid());
                    item.setProjectCode(prjProjectTask.getProjectCode());
                    item.setProjectTaskSid(prjProjectTask.getProjectTaskSid());
                });
                prjProjectTaskAttachMapper.inserts(prjProjectTask.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PrjProjectTask(), prjProjectTask);
            MongodbUtil.insertUserLog(prjProjectTask.getProjectTaskSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }
    /**
     * 批量修改附件信息
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    /**
     * 批量修改附件信息
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePrjProjectTaskAttach(PrjProjectTask prjProject) {
        // 先删后加
        prjProjectTaskAttachMapper.delete(new QueryWrapper<PrjProjectTaskAttach>().lambda()
                .eq(PrjProjectTaskAttach::getProjectTaskSid, prjProject.getProjectTaskSid()));
        if (CollectionUtil.isNotEmpty(prjProject.getAttachmentList())) {
            prjProject.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getProjectTaskAttachSid() == null) {
                    att.setProjectSid(prjProject.getProjectSid());
                    att.setProjectCode(prjProject.getProjectCode());
                    att.setProjectTaskSid(prjProject.getProjectTaskSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            prjProjectTaskAttachMapper.inserts(prjProject.getAttachmentList());
        }
    }

    /**
     * 修改项目档案-任务
     *
     * @param prjProjectTask 项目档案-任务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjProjectTask(PrjProjectTask prjProjectTask) {
        PrjProjectTask original = prjProjectTaskMapper.selectPrjProjectTaskById(prjProjectTask.getProjectTaskSid());
        // 更新岗位
        setData(prjProjectTask);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, prjProjectTask);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjProjectTask.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = prjProjectTaskMapper.updateAllById(prjProjectTask);
        if (row > 0) {
            // 修改附件
            this.updatePrjProjectTaskAttach(prjProjectTask);
            // 插入日志
            MongodbUtil.insertUserLog(prjProjectTask.getProjectTaskSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            // 插入主表的日志
            if (CollectionUtil.isNotEmpty(msgList)) {
                MongodbUtil.insertUserLog(original.getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, "任务”" + original.getTaskName() + "“数据变更");
            }
        }
        return row;
    }

    /**
     * 变更项目档案-任务
     *
     * @param prjProjectTask 项目档案-任务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePrjProjectTask(PrjProjectTask prjProjectTask) {
        PrjProjectTask response = prjProjectTaskMapper.selectPrjProjectTaskById(prjProjectTask.getProjectTaskSid());
        // 更新岗位
        setData(prjProjectTask);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, prjProjectTask);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjProjectTask.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = prjProjectTaskMapper.updateAllById(prjProjectTask);
        if (row > 0) {
            // 修改附件
            this.updatePrjProjectTaskAttach(prjProjectTask);
            //插入日志
            MongodbUtil.insertUserLog(prjProjectTask.getProjectTaskSid(), BusinessType.CHANGE.getValue(), response, prjProjectTask, TITLE);
            // 插入主表的日志
            if (CollectionUtil.isNotEmpty(msgList)) {
                MongodbUtil.insertUserLog(response.getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, "任务”" + response.getTaskName() + "“数据变更");
            }
        }
        return row;
    }

    /**
     * 批量删除项目档案-任务
     *
     * @param projectTaskSids 需要删除的项目档案-任务ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjProjectTaskByIds(List<Long> projectTaskSids) {
        List<PrjProjectTask> list = prjProjectTaskMapper.selectList(new QueryWrapper<PrjProjectTask>()
                .lambda().in(PrjProjectTask::getProjectTaskSid, projectTaskSids));
        int row = prjProjectTaskMapper.deleteBatchIds(projectTaskSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PrjProjectTask());
                MongodbUtil.insertUserLog(o.getProjectTaskSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 数据字段处理
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void setData(PrjProjectTask projectTask) {
        // 前置节点处理
        String preTask = null;
        if (ArrayUtil.isNotEmpty(projectTask.getPreTaskList())) {
            preTask = "";
            for (int i = 0; i < projectTask.getPreTaskList().length; i++) {
                preTask = preTask + projectTask.getPreTaskList()[i] + ";";
            }
        }
        projectTask.setPreTask(preTask);
        // 发起岗位处理
        String startCode = null;
        if (ArrayUtil.isNotEmpty(projectTask.getStartPositionCodeList())) {
            startCode = "";
            for (int i = 0; i < projectTask.getStartPositionCodeList().length; i++) {
                startCode = startCode + projectTask.getStartPositionCodeList()[i] + ";";
            }
            if (projectTask.getStartPositionCodeList().length > 1) {
                projectTask.setStartPositionSid(null);
            }
        }
        projectTask.setStartPositionCode(startCode);
        if (startCode == null) {
            projectTask.setStartPositionSid(null);
        }
        // 负责岗位处理
        String chargeCode = null;
        if (ArrayUtil.isNotEmpty(projectTask.getChargePositionCodeList())) {
            chargeCode = "";
            for (int i = 0; i < projectTask.getChargePositionCodeList().length; i++) {
                chargeCode = chargeCode + projectTask.getChargePositionCodeList()[i] + ";";
            }
            if (projectTask.getChargePositionCodeList().length > 1) {
                projectTask.setChargePositionSid(null);
            }
        }
        projectTask.setChargePositionCode(chargeCode);
        if (chargeCode == null) {
            projectTask.setChargePositionSid(null);
        }
        // 告知岗位处理
        String noticeCode = null;
        if (ArrayUtil.isNotEmpty(projectTask.getNoticePositionCodeList())) {
            noticeCode = "";
            for (int i = 0; i < projectTask.getNoticePositionCodeList().length; i++) {
                noticeCode = noticeCode + projectTask.getNoticePositionCodeList()[i] + ";";
            }
            if (projectTask.getNoticePositionCodeList().length > 1) {
                projectTask.setNoticePositionSid(null);
            }
        }
        projectTask.setNoticePositionCode(noticeCode);
        if (noticeCode == null) {
            projectTask.setNoticePositionSid(null);
        }
        // 处理人
        String handle = null;
        if (ArrayUtil.isNotEmpty(projectTask.getHandlerTaskList())) {
            handle = "";
            for (int i = 0; i < projectTask.getHandlerTaskList().length; i++) {
                handle = handle + projectTask.getHandlerTaskList()[i] + ";";
            }
        }
        projectTask.setHandlerTask(handle);
        // 图片
        String picture = null;
        if (ArrayUtil.isNotEmpty(projectTask.getPicturePathList())) {
            picture = "";
            for (int i = 0; i < projectTask.getPicturePathList().length; i++) {
                picture = picture + projectTask.getPicturePathList()[i] + ";";
            }
        }
        projectTask.setPicturePath(picture);
    }

    /**
     * 获取岗位名称
     *
     * @param projectTask 任务节点
     * @return 结果
     */
    @Override
    public void getPosition(PrjProjectTask projectTask) {
        if (projectTask == null) {
            return;
        }
        // 明细前置节点处理
        if (StrUtil.isNotBlank(projectTask.getPreTask())) {
            String[] preTaskList = projectTask.getPreTask().split(";");
            projectTask.setPreTaskList(preTaskList);
        }
        // 发起岗位
        if (StrUtil.isNotBlank(projectTask.getStartPositionCode())) {
            String[] starts = projectTask.getStartPositionCode().split(";");
            projectTask.setStartPositionCodeList(starts);
            List<BasPosition> startList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, starts));
            if (ArrayUtil.isNotEmpty(startList)) {
                String startName = "";
                for (int i = 0; i < startList.size(); i++) {
                    startName = startName + startList.get(i).getPositionName() + ";";
                }
                projectTask.setStartPositionName(startName);
            }
        }
        // 负责岗位
        if (StrUtil.isNotBlank(projectTask.getChargePositionCode())) {
            String[] charges = projectTask.getChargePositionCode().split(";");
            projectTask.setChargePositionCodeList(charges);
            List<BasPosition> chargeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, charges));
            if (ArrayUtil.isNotEmpty(chargeList)) {
                String chargeName = "";
                for (int i = 0; i < chargeList.size(); i++) {
                    chargeName = chargeName + chargeList.get(i).getPositionName() + ";";
                }
                projectTask.setChargePositionName(chargeName);
            }
        }
        // 告知岗位
        if (StrUtil.isNotBlank(projectTask.getNoticePositionCode())) {
            String[] notices = projectTask.getNoticePositionCode().split(";");
            projectTask.setNoticePositionCodeList(notices);
            List<BasPosition> noticeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, notices));
            if (ArrayUtil.isNotEmpty(noticeList)) {
                String noticeName = "";
                for (int i = 0; i < noticeList.size(); i++) {
                    noticeName = noticeName + noticeList.get(i).getPositionName() + ";";
                }
                projectTask.setNoticePositionName(noticeName);
            }
        }
        // 处理人
        if (StrUtil.isNotBlank(projectTask.getHandlerTask())) {
            String[] handler = projectTask.getHandlerTask().split(";");
            projectTask.setHandlerTaskList(handler);
        }
        // 图片
        if (StrUtil.isNotBlank(projectTask.getPicturePath())) {
            String[] picture = projectTask.getPicturePath().split(";");
            projectTask.setPicturePathList(picture);
        }
    }

    /**
     * 查询项目档案-任务
     *
     * @param projectSid 项目档案-主表ID
     * @return 项目档案-任务
     */
    @Override
    public List<PrjProjectTask> selectPrjProjectTaskListById(Long projectSid) {
        List<PrjProjectTask> prjProjectTaskList = prjProjectTaskMapper
                .selectPrjProjectTaskList(new PrjProjectTask()
                        .setProjectSid(projectSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(prjProjectTaskList)) {
            prjProjectTaskList.forEach(item->{
                // 处理岗位
                this.getPosition(item);
                // 明细前置节点处理
                if (StrUtil.isNotBlank(item.getPreTask())) {
                    String[] preTaskList = item.getPreTask().split(";");
                    item.setPreTaskList(preTaskList);
                }
                MongodbUtil.find(item);
            });
        }
        return prjProjectTaskList;
    }

    /**
     * 批量新增项目档案-任务
     *
     * @param project 项目档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjProjectTaskList(PrjProject project) {
        int row = 0;
        List<PrjProjectTask> list = project.getTaskList();
        if (CollectionUtil.isNotEmpty(list)) {
            PrjProjectTask item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setProjectSid(project.getProjectSid());
                item.setProjectCode(Long.parseLong(project.getProjectCode()));
                // 处理岗位
                this.setData(item);
                row += insertPrjProjectTask(item);
            }
        }
        return row;
    }

    /**
     * 批量修改项目档案-任务
     *
     * @param project 项目档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjProjectTaskList(PrjProject project) {
        int row = 0;
        List<PrjProjectTask> list = project.getTaskList();
        // 原本明细
        List<PrjProjectTask> oldList = prjProjectTaskMapper.selectList(new QueryWrapper<PrjProjectTask>()
                .lambda().eq(PrjProjectTask::getProjectSid, project.getProjectSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<PrjProjectTask> newList = list.stream().filter(o -> o.getProjectTaskSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                project.setTaskList(newList);
                insertPrjProjectTaskList(project);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<PrjProjectTask> updateList = list.stream().filter(o -> o.getProjectTaskSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(PrjProjectTask::getProjectTaskSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, PrjProjectTask> map = oldList.stream().collect(Collectors.toMap(PrjProjectTask::getProjectTaskSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getProjectTaskSid())) {
                            // 处理岗位
                            this.setData(item);
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getProjectTaskSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            prjProjectTaskMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getProjectTaskSid(), project.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<PrjProjectTask> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getProjectTaskSid())).collect(Collectors.toList());
                    deletePrjProjectTaskByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deletePrjProjectTaskByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除项目档案-任务
     *
     * @param itemList 需要删除的项目档案-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjProjectTaskByList(List<PrjProjectTask> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> prjProjectTaskSidList = itemList.stream().filter(o -> o.getProjectTaskSid() != null)
                .map(PrjProjectTask::getProjectTaskSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(prjProjectTaskSidList)) {
            row = prjProjectTaskMapper.deleteBatchIds(prjProjectTaskSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new PrjProjectTask());
                    MongodbUtil.insertUserLog(o.getProjectTaskSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除项目档案-任务 根据主表sids
     *
     * @param prjProjectSidList 需要删除的项目档案sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjProjectTaskByProject(List<Long> prjProjectSidList) {
        List<PrjProjectTask> itemList = prjProjectTaskMapper.selectList(new QueryWrapper<PrjProjectTask>()
                .lambda().in(PrjProjectTask::getProjectSid, prjProjectSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deletePrjProjectTaskByList(itemList);
        }
        return row;
    }

    /**
     * 查询项目任务明细报表
     *
     * @param prjProjectTask 项目任务明细报表请求
     * @return 项目任务明细报表返回
     */
    @Override
    public List<PrjProjectTaskFormResponse> selectPrjProjectTaskForm(PrjProjectTaskFormRequest prjProjectTask) {
        return prjProjectTaskMapper.selectPrjProjectTaskForm(prjProjectTask);
    }

    /**
     * 查询项目前置任务完成状况报表
     *
     * @param prjProjectTask 查询项目前置任务完成状况报表请求
     * @return 查询项目前置任务完成状况报表返回
     */
    @Override
    public List<PrjProjectTaskPreCondition> selectPrjProjectTaskPreCondition(PrjProjectTaskPreCondition prjProjectTask) {
        return prjProjectTaskMapper.selectPrjProjectTaskPreCondition(prjProjectTask);
    }

    /**
     * 设置即将到期提醒天数
     * @param prjProjectTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(PrjProjectTask prjProjectTask) {
        if (prjProjectTask.getProjectTaskSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<PrjProjectTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(PrjProjectTask::getProjectTaskSid, prjProjectTask.getProjectTaskSidList());
        updateWrapper.set(PrjProjectTask::getToexpireDaysTask, prjProjectTask.getToexpireDaysTask());
        row = prjProjectTaskMapper.update(new PrjProjectTask(), updateWrapper);
        return prjProjectTask.getProjectTaskSidList().length;
    }

    /**
     * 项目任务执行提醒天数
     * @param prjProjectTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireNoticeDays(PrjProjectTask prjProjectTask) {
        if (prjProjectTask.getProjectTaskSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        if (prjProjectTask.getToexecuteNoticeDaysPrjTask() == null) {
            throw new BaseException("任务执行提醒天数不能为空！");
        }
        LambdaUpdateWrapper<PrjProjectTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //项目任务执行提醒天数
        updateWrapper.in(PrjProjectTask::getProjectTaskSid, prjProjectTask.getProjectTaskSidList());
        updateWrapper.set(PrjProjectTask::getToexecuteNoticeDaysPrjTask, prjProjectTask.getToexecuteNoticeDaysPrjTask());
        row = prjProjectTaskMapper.update(new PrjProjectTask(), updateWrapper);
        return prjProjectTask.getProjectTaskSidList().length;
    }

    /**
     * 设置任务状态
     * @param projectTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity setTaskStatus(PrjProjectTask projectTask) {
        if (projectTask.getProjectTaskSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProjectTask> projectTaskList = prjProjectTaskMapper.selectPrjProjectTaskList(new PrjProjectTask().setProjectTaskSidList(projectTask.getProjectTaskSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProjectTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(projectTask.getTaskStatus())) {
            projectTask.setTaskStatus(null);
        }
        // 任务状态
        updateWrapper.in(PrjProjectTask::getProjectTaskSid, projectTask.getProjectTaskSidList());
        if (ConstantsEms.YES.equals(projectTask.getTaskStatusIsUpdate())) {
            // 前置任务未完成的提示或者报错
            String noticeType = ApiThreadLocalUtil.get().getSysUser().getClient().getNoticeTypePreTaskIncomplete();
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            List<Long> todoSidList = new ArrayList<>();
            for (int i = 0; i < projectTask.getProjectTaskSidList().length; i++) {
                if (projectTask.getIsContinue() == null || true != projectTask.getIsContinue()
                        || !ConstantsPdm.PROJECT_TASK_WKS.equals(projectTask.getTaskStatus())) {
                    PrjProjectTask task = this.selectPrjProjectTaskById(projectTask.getProjectTaskSidList()[i]);
                    // 设置任务状态时，任务状态改为非“未开始”，点击确定按钮时，进行如下校验：
                    // 根据对应任务的项目SID和“前置任务节点”（前置任务节点可能存在多个），
                    // 从数据库表结构（s_prj_project_task）中找到对应任务明细的任务状态，若任务状态不都是“已完成”，
                    // 提示：前置任务未完成，是否确认修改？点击是则执行修改操作，点击否则关闭弹窗
                    if (projectTask.getIsContinue() == null || true != projectTask.getIsContinue() && StrUtil.isNotBlank(noticeType)) {
                        if (ArrayUtil.isNotEmpty(task.getPreTaskList())) {
                            List<String> preTaskList = Arrays.asList(task.getPreTaskList());
                            List<PrjProjectTask> taskList = prjProjectTaskMapper.selectList(new QueryWrapper<PrjProjectTask>()
                                    .lambda().eq(PrjProjectTask::getProjectSid, task.getProjectSid()));
                            List<PrjProjectTask> notComplete = taskList.stream().filter(o -> preTaskList.contains(o.getTaskCode())
                                    && !ConstantsPdm.PROJECT_TASK_YWC.equals(o.getTaskStatus())).collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(notComplete)) {
                                if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(noticeType)) {
                                    msgList.add(new CommonErrMsgResponse().setMsg("项目编号" + task.getProjectCode() + "的任务节点" + task.getTaskName() + "的前置任务未完成，是否确认修改？"));
                                }
                                else if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(noticeType)) {
                                    msgList.add(new CommonErrMsgResponse().setMsg("项目编号" + task.getProjectCode() + "的任务节点" + task.getTaskName() + "的前置任务未完成，无法修改！"));
                                }
                            }
                        }
                    }
                    // 项目任务明细报表，设置任务状态时，任务状态改为非“未开始”，删除此任务对应的待开始的待办提示信息
                    if (!ConstantsPdm.PROJECT_TASK_WKS.equals(projectTask.getTaskStatus()) && CollectionUtil.isEmpty(msgList)) {
                        // 删除项目档案任务明细的待办
                        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                                .eq(SysTodoTask::getDocumentSid, task.getProjectSid())
                                .eq(SysTodoTask::getDocumentItemSid, task.getProjectTaskSid())
                                .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
                        if (CollectionUtil.isNotEmpty(todoTaskList)) {
                            todoSidList.addAll(todoTaskList.stream().map(SysTodoTask::getTodoTaskSid).collect(Collectors.toList()));
                        }
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(msgList)) {
                if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(noticeType)) {
                    return EmsResultEntity.warning(msgList);
                }
                else if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(noticeType)) {
                    return EmsResultEntity.error(msgList);
                }
            }
            updateWrapper.set(PrjProjectTask::getTaskStatus, projectTask.getTaskStatus());
            // 删除待办
            if (!ConstantsPdm.PROJECT_TASK_WKS.equals(projectTask.getTaskStatus()) && CollectionUtil.isNotEmpty(todoSidList)){
                sysTodoTaskMapper.deleteBatchIds(todoSidList);
            }
        }
        if (ConstantsEms.YES.equals(projectTask.getActualEndDateIsUpdate())) {
            updateWrapper.set(PrjProjectTask::getActualEndDate, projectTask.getActualEndDate());
        }
        if (ConstantsEms.YES.equals(projectTask.getTaskStatusIsUpdate())
            || ConstantsEms.YES.equals(projectTask.getActualEndDateIsUpdate())) {
            row = prjProjectTaskMapper.update(new PrjProjectTask(), updateWrapper);
            // 操作日志
            List<DictData> taskStatusList = sysDictDataService.selectDictData("s_project_task_status");
            taskStatusList = taskStatusList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> taskStatusMaps = taskStatusList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
            for (int i = 0; i < projectTaskList.size(); i++) {
                PrjProjectTask nowData = new PrjProjectTask();
                BeanUtil.copyProperties(projectTaskList.get(i), nowData);
                // 任务状态
                if (ConstantsEms.YES.equals(projectTask.getTaskStatusIsUpdate())) {
                    if (projectTaskList.get(i).getTaskStatus() == null || !projectTaskList.get(i).getTaskStatus().equals(projectTask.getTaskStatus())) {
                        nowData.setTaskStatus(projectTask.getTaskStatus());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectTaskList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectTaskList.get(i).getTaskStatus() == null ? "" : taskStatusMaps.get(projectTaskList.get(i).getTaskStatus());
                            String newCode = nowData.getTaskStatus() == null ? "" : taskStatusMaps.get(nowData.getTaskStatus());
                            String remark = "任务”" + projectTaskList.get(i).getTaskName() + "“任务状态变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
                // 实际完成日期
                if (ConstantsEms.YES.equals(projectTask.getActualEndDateIsUpdate())) {
                    if (projectTaskList.get(i).getActualEndDate() == null || !projectTaskList.get(i).getActualEndDate().equals(projectTask.getActualEndDate())) {
                        // 上面任务状态有改到，这里改回旧的，值判断实际完成日期
                        nowData.setTaskStatus(projectTaskList.get(i).getTaskStatus());
                        nowData.setActualEndDate(projectTask.getActualEndDate());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectTaskList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectTaskList.get(i).getActualEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(projectTaskList.get(i).getActualEndDate());
                            String newCode = nowData.getActualEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(nowData.getActualEndDate());
                            String remark = "任务”" + projectTaskList.get(i).getTaskName() + "“实际完成日期变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
            }
        }
        return EmsResultEntity.success(projectTask.getProjectTaskSidList().length);
    }

    /**
     * 设置计划日期
     * @param projectTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTaskPlanDate(PrjProjectTask projectTask) {
        if (projectTask.getProjectTaskSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProjectTask> projectTaskList = prjProjectTaskMapper.selectPrjProjectTaskList(new PrjProjectTask().setProjectTaskSidList(projectTask.getProjectTaskSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProjectTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 计划开始日期
        updateWrapper.in(PrjProjectTask::getProjectTaskSid, projectTask.getProjectTaskSidList());
        if (ConstantsEms.YES.equals(projectTask.getPlanStartDateIsUpdate())) {
            updateWrapper.set(PrjProjectTask::getPlanStartDate, projectTask.getPlanStartDate());
        }
        // 计划完成日期
        if (ConstantsEms.YES.equals(projectTask.getPlanEndDateIsUpdate())) {
            updateWrapper.set(PrjProjectTask::getPlanEndDate, projectTask.getPlanEndDate());
        }
        if (ConstantsEms.YES.equals(projectTask.getPlanStartDateIsUpdate())
                || ConstantsEms.YES.equals(projectTask.getPlanEndDateIsUpdate())) {
            row = prjProjectTaskMapper.update(new PrjProjectTask(), updateWrapper);
            // 操作日志记录
            for (int i = 0; i < projectTaskList.size(); i++) {
                PrjProjectTask nowData = new PrjProjectTask();
                BeanUtil.copyProperties(projectTaskList.get(i), nowData);
                // 计划开始日期
                if (ConstantsEms.YES.equals(projectTask.getPlanStartDateIsUpdate())) {
                    if (projectTaskList.get(i).getPlanStartDate() == null || !projectTaskList.get(i).getPlanStartDate().equals(projectTask.getPlanStartDate())) {
                        nowData.setPlanStartDate(projectTask.getPlanStartDate());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectTaskList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectTaskList.get(i).getPlanStartDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(projectTaskList.get(i).getPlanStartDate());
                            String newCode = nowData.getPlanStartDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(nowData.getPlanStartDate());
                            String remark = "任务”" + projectTaskList.get(i).getTaskName() + "“计划开始日期变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
                // 计划完成日期
                if (ConstantsEms.YES.equals(projectTask.getPlanEndDateIsUpdate())) {
                    if (projectTaskList.get(i).getPlanEndDate() == null || !projectTaskList.get(i).getPlanEndDate().equals(projectTask.getPlanEndDate())) {
                        // 上面计划开始日期有改到，这里改回旧的，值判断计划完成日期
                        nowData.setPlanStartDate(projectTaskList.get(i).getPlanStartDate());
                        nowData.setPlanEndDate(projectTask.getPlanEndDate());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectTaskList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectTaskList.get(i).getPlanEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(projectTaskList.get(i).getPlanEndDate());
                            String newCode = nowData.getPlanEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(nowData.getPlanEndDate());
                            String remark = "任务”" + projectTaskList.get(i).getTaskName() + "“计划完成日期变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
            }
        }
        return projectTask.getProjectTaskSidList().length;
    }

    /**
     * 项目任务明细报表分配任务处理人
     * @param projectTask 入参
     * @return 出参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTaskHandler(PrjProjectTask projectTask) {
        if (projectTask.getProjectTaskSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProjectTask> projectTaskList = prjProjectTaskMapper.selectPrjProjectTaskList(new PrjProjectTask().setProjectTaskSidList(projectTask.getProjectTaskSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProjectTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 处理人
        updateWrapper.in(PrjProjectTask::getProjectTaskSid, projectTask.getProjectTaskSidList());
        // 新的处理人
        List<SysUser> userList = new ArrayList<>();
        // 带分号的用户名
        String handlerTask = null;
        // 带分号的用户昵称
        String handlerTaskName = "";
        if (ArrayUtil.isNotEmpty(projectTask.getHandlerTaskList())) {
            handlerTask = "";
            List<String> handlerTaskArrayList = Arrays.asList(projectTask.getHandlerTaskList());
            // 按照用户名排序
            handlerTaskArrayList = handlerTaskArrayList.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < handlerTaskArrayList.size(); i++) {
                if (i > 0) {
                    handlerTask = handlerTask + ";" + handlerTaskArrayList.get(i);
                } else {
                    handlerTask = handlerTaskArrayList.get(i);
                }
            }
            // 保存数据库的字段
            projectTask.setHandlerTask(handlerTask);
            // 获取对应的昵称
            userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda().in(SysUser::getUserName, projectTask.getHandlerTaskList()));
            if (CollectionUtil.isNotEmpty(userList)) {
                userList = userList.stream().sorted(Comparator.comparing(SysUser::getUserName)).collect(Collectors.toList());
                for (int i = 0; i < userList.size(); i++) {
                    if (i > 0) {
                        handlerTaskName = handlerTaskName + ";" + userList.get(i).getNickName();
                    } else {
                        handlerTaskName = userList.get(i).getNickName();
                    }

                }
            }
        }
        // 修改数据库中的 处理人字段
        updateWrapper.set(PrjProjectTask::getHandlerTask, projectTask.getHandlerTask());
        row = prjProjectTaskMapper.update(new PrjProjectTask(), updateWrapper);
        // 操作日志记录
        for (int i = 0; i < projectTaskList.size(); i++) {
            PrjProjectTask nowData = new PrjProjectTask();
            BeanUtil.copyProperties(projectTaskList.get(i), nowData);
            // 处理人
            if ((projectTaskList.get(i).getHandlerTask() != null && !projectTaskList.get(i).getHandlerTask().equals(projectTask.getHandlerTask()))
                    || (projectTaskList.get(i).getHandlerTask() == null && projectTask.getHandlerTask() != null)) {
                nowData.setHandlerTask(projectTask.getHandlerTask());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(projectTaskList.get(i), nowData);
                if (CollectionUtil.isNotEmpty(msgList)) {
                    String oldCode = StrUtil.isBlank(projectTaskList.get(i).getHandlerTaskName()) ? "" : projectTaskList.get(i).getHandlerTaskName();
                    String remark = "任务”" + projectTaskList.get(i).getTaskName() + "“处理人变更，变更前：" + oldCode + "；变更后：" + handlerTaskName;
                    MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                    MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                }
            }
        }
        return projectTask.getProjectTaskSidList().length;
    }

    /**
     * 设置项目任务优先级
     * @param projectTask 入参
     * @return 出餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTaskPriority(PrjProjectTask projectTask) {
        if (projectTask.getProjectTaskSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProjectTask> projectTaskList = prjProjectTaskMapper.selectPrjProjectTaskList(new PrjProjectTask().setProjectTaskSidList(projectTask.getProjectTaskSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProjectTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 优先级
        updateWrapper.in(PrjProjectTask::getProjectTaskSid, projectTask.getProjectTaskSidList());
        if (ConstantsEms.YES.equals(projectTask.getPriorityTaskIsUpd())) {
            updateWrapper.set(PrjProjectTask::getPriorityTask, projectTask.getPriorityTask());
        }
        if (ConstantsEms.YES.equals(projectTask.getPriorityTaskIsUpd())) {
            row = prjProjectTaskMapper.update(new PrjProjectTask(), updateWrapper);
            // 数据字典
            List<DictData> priorityList = sysDictDataService.selectDictData("s_urgency_type");
            priorityList = priorityList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> priorityMaps = priorityList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
            // 操作日志记录
            for (int i = 0; i < projectTaskList.size(); i++) {
                PrjProjectTask nowData = new PrjProjectTask();
                BeanUtil.copyProperties(projectTaskList.get(i), nowData);
                // 优先级
                if (ConstantsEms.YES.equals(projectTask.getPriorityTaskIsUpd())) {
                    if ((projectTask.getPriorityTask() == null && projectTaskList.get(i).getPriorityTask() != null)
                            || (projectTask.getPriorityTask() != null && !projectTask.getPriorityTask().equals(projectTaskList.get(i).getPriorityTask()))) {
                        nowData.setPriorityTask(projectTask.getPriorityTask());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectTaskList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectTaskList.get(i).getPriorityTask() == null ? "" : priorityMaps.get(projectTaskList.get(i).getPriorityTask());
                            String newCode = nowData.getPriorityTask() == null ? "" : priorityMaps.get(projectTaskList.get(i).getPriorityTask());
                            String remark = "任务”" + projectTaskList.get(i).getTaskName() + "“任务优先级变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectTaskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
            }
        }
        return projectTask.getProjectTaskSidList().length;
    }
}
