package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.constant.ConstantsMsg;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPdm;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.PrjMatterListAttachMapper;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.IPrjMatterListService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PrjMatterListMapper;

/**
 * 事项清单Service业务层处理
 *
 * @author platform
 * @date 2023-11-20
 */
@Service
@SuppressWarnings("all")
public class PrjMatterListServiceImpl extends ServiceImpl<PrjMatterListMapper, PrjMatterList> implements IPrjMatterListService {
    @Autowired
    private PrjMatterListMapper prjMatterListMapper;
    @Autowired
    private PrjMatterListAttachMapper prjMatterListAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingMapper;

    private static final String TITLE = "事项清单";

    /**
     * 查询事项清单
     *
     * @param matterListSid 事项清单ID
     * @return 事项清单
     */
    @Override
    public PrjMatterList selectPrjMatterListById(Long matterListSid) {
        PrjMatterList prjMatterList = prjMatterListMapper.selectPrjMatterListById(matterListSid);
        // 读数据
        prjMatterList.setPicturePathList(new String[]{});
        prjMatterList.setVideoPathList(new String[]{});
        getListData(prjMatterList);
        // 附件
        prjMatterList.setAttachmentList(new ArrayList<>());
        List<PrjMatterListAttach> attachmentList = prjMatterListAttachMapper.selectPrjMatterListAttachList(
                new PrjMatterListAttach().setMatterListSid(matterListSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            prjMatterList.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(prjMatterList);
        return prjMatterList;
    }

    /**
     * 查询事项清单列表
     *
     * @param prjMatterList 事项清单
     * @return 事项清单
     */
    @Override
    public List<PrjMatterList> selectPrjMatterListList(PrjMatterList prjMatterList) {
        return prjMatterListMapper.selectPrjMatterListList(prjMatterList);
    }

    /**
     * 从数据库中读取转换为数组给前端
     *
     * @param prjMatterList
     */
    public void getListData(PrjMatterList prjMatterList) {
        if (prjMatterList == null) {
            return;
        }
        // 明细前置事项处理
        if (StrUtil.isNotBlank(prjMatterList.getPreMatter())) {
            String[] preMatterList = prjMatterList.getPreMatter().split(";");
            prjMatterList.setPreMatterList(preMatterList);
        }
        // 告知人(事项)
        if (StrUtil.isNotBlank(prjMatterList.getPersonNoticeMatter())) {
            String[] personNoticeMatter = prjMatterList.getPersonNoticeMatter().split(";");
            prjMatterList.setPersonNoticeMatterList(personNoticeMatter);
        }
        // 关注人
        if (StrUtil.isNotBlank(prjMatterList.getPersonAttent())) {
            String[] personAttent = prjMatterList.getPersonAttent().split(";");
            prjMatterList.setPersonAttentList(personAttent);
        }
        // 图片
        if (StrUtil.isNotBlank(prjMatterList.getPicturePath())) {
            String[] picture = prjMatterList.getPicturePath().split(";");
            prjMatterList.setPicturePathList(picture);
        }
        // 视频
        if (StrUtil.isNotBlank(prjMatterList.getVideoPath())) {
            String[] video = prjMatterList.getVideoPath().split(";");
            prjMatterList.setVideoPathList(video);
        }
    }

    /**
     * 将前端传多个值存入数据库中
     *
     * @param prjMatterList
     */
    public void setListData(PrjMatterList prjMatterList) {
        // 前置节点处理
        String preMatter = null;
        if (ArrayUtil.isNotEmpty(prjMatterList.getPreMatterList())) {
            preMatter = "";
            for (int i = 0; i < prjMatterList.getPreMatterList().length; i++) {
                preMatter = preMatter + prjMatterList.getPreMatterList()[i] + ";";
            }
        }
        prjMatterList.setPreMatter(preMatter);
        // 告知人(任务)
        String personNoticeMatterName = null;
        if (ArrayUtil.isNotEmpty(prjMatterList.getPersonNoticeMatterList())) {
            personNoticeMatterName = "";
            for (int i = 0; i < prjMatterList.getPersonNoticeMatterList().length; i++) {
                personNoticeMatterName = personNoticeMatterName + prjMatterList.getPersonNoticeMatterList()[i] + ";";
            }
        }
        prjMatterList.setPersonNoticeMatterName(personNoticeMatterName);
        // 关注人
        String personAttent = null;
        if (ArrayUtil.isNotEmpty(prjMatterList.getPersonAttentList())) {
            personAttent = "";
            for (int i = 0; i < prjMatterList.getPersonAttentList().length; i++) {
                personAttent = personAttent + prjMatterList.getPersonAttentList()[i] + ";";
            }
        }
        prjMatterList.setPersonAttent(personAttent);
        // 图片
        String picture = null;
        if (ArrayUtil.isNotEmpty(prjMatterList.getPicturePathList())) {
            picture = "";
            for (int i = 0; i < prjMatterList.getPicturePathList().length; i++) {
                picture = picture + prjMatterList.getPicturePathList()[i] + ";";
            }
        }
        prjMatterList.setPicturePath(picture);
        // 视频
        String video = null;
        if (ArrayUtil.isNotEmpty(prjMatterList.getVideoPathList())) {
            video = "";
            for (int i = 0; i < prjMatterList.getVideoPathList().length; i++) {
                video = video + prjMatterList.getVideoPathList()[i] + ";";
            }
        }
        prjMatterList.setVideoPath(video);
    }

    /**
     * 是否默认提醒天数
     *
     * @param prjMatterList
     */
    public void setDays(PrjMatterList prjMatterList) {
        SysDefaultSettingClient defaultSetting = defaultSettingMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().last("limit 1"));
        if (prjMatterList.getToexpireDaysMatter() == null) {
            if (defaultSetting != null && defaultSetting.getToexpireDaysPrjTask() != null) {
                prjMatterList.setToexpireDaysMatter(defaultSetting.getToexpireDaysPrjTask());
            } else {
                prjMatterList.setToexpireDaysMatter(15);
            }
        }
        if (prjMatterList.getTodoDaysMatter() == null) {
            if (defaultSetting != null && defaultSetting.getTodoDaysPrjTask() != null) {
                prjMatterList.setTodoDaysMatter(defaultSetting.getTodoDaysPrjTask());
            } else {
                prjMatterList.setTodoDaysMatter(7);
            }
        }
    }

    /**
     * 新增事项清单
     * 需要注意编码重复校验
     *
     * @param prjMatterList 事项清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjMatterList(PrjMatterList prjMatterList) {
        // 写数据
        setDays(prjMatterList);
        setListData(prjMatterList);
        int row = prjMatterListMapper.insert(prjMatterList);
        if (row > 0) {
            // 写入附件
            if (CollectionUtil.isNotEmpty(prjMatterList.getAttachmentList())) {
                prjMatterList.getAttachmentList().forEach(item -> {
                    item.setMatterListSid(prjMatterList.getMatterListSid());
                });
                prjMatterListAttachMapper.inserts(prjMatterList.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PrjMatterList(), prjMatterList);
            MongodbDeal.insert(prjMatterList.getMatterListSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param prjMatterList
     * @return 结果
     */
    public void updatePrjMatterListAttach(PrjMatterList prjMatterList) {
        // 先删后加
        prjMatterListAttachMapper.delete(new QueryWrapper<PrjMatterListAttach>().lambda()
                .eq(PrjMatterListAttach::getMatterListSid, prjMatterList.getMatterListSid()));
        if (CollectionUtil.isNotEmpty(prjMatterList.getAttachmentList())) {
            prjMatterList.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getMatterListSid() == null) {
                    att.setMatterListSid(prjMatterList.getMatterListSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            prjMatterListAttachMapper.inserts(prjMatterList.getAttachmentList());
        }
    }

    /**
     * 修改事项清单
     *
     * @param prjMatterList 事项清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjMatterList(PrjMatterList prjMatterList) {
        PrjMatterList original = prjMatterListMapper.selectPrjMatterListById(prjMatterList.getMatterListSid());
        // 写数据
        setDays(prjMatterList);
        setListData(prjMatterList);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, prjMatterList);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjMatterList.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = prjMatterListMapper.updateAllById(prjMatterList);
        if (row > 0) {
            // 查询页面，设置事项状态时，事项状态改为非未开始，删除此事项对应待办
            if (!ConstantsPdm.PROJECT_STATUS_WKS.equals(prjMatterList.getMatterStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getBusinessType, ConstantsEms.TODO_BUSINESS_TYPE_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_MATTER_LIST)
                        .eq(SysTodoTask::getDocumentSid, prjMatterList.getMatterListSid())
                        .likeLeft(SysTodoTask::getTitle, ConstantsMsg.TODO_DEAL_SUFFIX));
            }
            // 修改附件清单
            updatePrjMatterListAttach(prjMatterList);
            // 插入日志
            MongodbDeal.insert(prjMatterList.getMatterListSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            // 删除相关待办
            if (StrUtil.isBlank(original.getMatterHandler()) && StrUtil.isNotBlank(prjMatterList.getMatterHandler())) {
                prjMatterList.setMatterListSidList(new Long[]{prjMatterList.getMatterListSid()});
                deleteHandlerTodoTask(prjMatterList);
            }
        }
        return row;
    }

    /**
     * 删除待分配处理人待办任务
     * @param prjMatterList
     */
    public void deleteHandlerTodoTask(PrjMatterList prjMatterList) {
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, prjMatterList.getMatterListSidList())
                .eq(SysTodoTask::getBusinessType, ConstantsEms.TODO_BUSINESS_TYPE_FPSXCLR)
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_MATTER_LIST));
    }

    /**
     * 变更事项清单
     *
     * @param prjMatterList 事项清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePrjMatterList(PrjMatterList prjMatterList) {
        PrjMatterList response = prjMatterListMapper.selectPrjMatterListById(prjMatterList.getMatterListSid());
        // 写数据
        setDays(prjMatterList);
        setListData(prjMatterList);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, prjMatterList);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjMatterList.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = prjMatterListMapper.updateAllById(prjMatterList);
        if (row > 0) {
            // 查询页面，设置事项状态时，事项状态改为非未开始，删除此事项对应待办
            if (!ConstantsPdm.PROJECT_STATUS_WKS.equals(prjMatterList.getMatterStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getBusinessType, ConstantsEms.TODO_BUSINESS_TYPE_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_MATTER_LIST)
                        .eq(SysTodoTask::getDocumentSid, prjMatterList.getMatterListSid())
                        .likeLeft(SysTodoTask::getTitle, ConstantsMsg.TODO_DEAL_SUFFIX));
            }
            // 修改附件清单
            updatePrjMatterListAttach(prjMatterList);
            // 插入日志
            MongodbUtil.insertUserLog(prjMatterList.getMatterListSid(), BusinessType.CHANGE.getValue(), response, prjMatterList, TITLE);
            // 删除相关待办
            if (StrUtil.isBlank(response.getMatterHandler()) && StrUtil.isNotBlank(prjMatterList.getMatterHandler())) {
                prjMatterList.setMatterListSidList(new Long[]{prjMatterList.getMatterListSid()});
                deleteHandlerTodoTask(prjMatterList);
            }
        }
        return row;
    }

    /**
     * 批量删除事项清单
     *
     * @param matterListSids 需要删除的事项清单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjMatterListByIds(List<Long> matterListSids) {
        List<PrjMatterList> list = prjMatterListMapper.selectList(new QueryWrapper<PrjMatterList>()
                .lambda().in(PrjMatterList::getMatterListSid, matterListSids));
        int row = prjMatterListMapper.deleteBatchIds(matterListSids);
        if (row > 0) {
            // 附件清单
            prjMatterListAttachMapper.delete(new QueryWrapper<PrjMatterListAttach>().lambda()
                    .in(PrjMatterListAttach::getMatterListSid, matterListSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PrjMatterList());
                MongodbUtil.insertUserLog(o.getMatterListSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 设置事项状态
     *
     * @param prjMatterList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setMatterStatus(PrjMatterList prjMatterList) {
        if (prjMatterList.getMatterListSidList().length == 0) {
            throw new BaseException("参数丢失！");
        }
        if (StrUtil.isBlank(prjMatterList.getMatterStatus())) {
            throw new BaseException("事项状态不能为空！");
        }
        if (ConstantsPdm.PROJECT_STATUS_YWC.equals(prjMatterList.getMatterStatus()) && prjMatterList.getActualEndDate() == null) {
            throw new BaseException("实际完成日期不能为空！");
        }
        // 原数据
        List<PrjMatterList> matterListList = prjMatterListMapper.selectBatchIds(Arrays.asList(prjMatterList.getMatterListSidList()));
        // 修改
        LambdaUpdateWrapper<PrjMatterList> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(prjMatterList.getMatterStatus())) {
            prjMatterList.setMatterStatus(null);
        }
        if (ConstantsPdm.PROJECT_STATUS_YWC.equals(prjMatterList.getMatterStatus())) {
            updateWrapper.set(PrjMatterList::getActualEndDate, prjMatterList.getActualEndDate());
        }
        // 事项状态
        updateWrapper.in(PrjMatterList::getMatterListSid, prjMatterList.getMatterListSidList()).set(PrjMatterList::getMatterStatus, prjMatterList.getMatterStatus());
        row = prjMatterListMapper.update(new PrjMatterList(), updateWrapper);
        // 查询页面，设置事项状态时，事项状态改为非未开始，删除此事项对应待办
        if (!ConstantsPdm.PROJECT_STATUS_WKS.equals(prjMatterList.getMatterStatus())) {
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getBusinessType, ConstantsEms.TODO_BUSINESS_TYPE_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_MATTER_LIST)
                    .in(SysTodoTask::getDocumentSid, prjMatterList.getMatterListSidList())
                    .likeLeft(SysTodoTask::getTitle, ConstantsMsg.TODO_DEAL_SUFFIX));
        }
        // 操作日志
        for (Long sid : prjMatterList.getMatterListSidList()) {
            MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), null, TITLE, "设置事项状态");
        }
        return row;
    }

    /**
     * 分配事项处理人
     *
     * @param prjMatterList 入参
     * @return 出参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setMatterHandler(PrjMatterList prjMatterList) {
        if (prjMatterList.getMatterListSidList().length == 0) {
            throw new BaseException("参数丢失！");
        }
        // 原数据
        List<PrjMatterList> projectTaskList = prjMatterListMapper.selectPrjMatterListList(new PrjMatterList()
                .setMatterListSidList(prjMatterList.getMatterListSidList()));
        // 修改
        LambdaUpdateWrapper<PrjMatterList> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 处理人
        updateWrapper.in(PrjMatterList::getMatterListSid, prjMatterList.getMatterListSidList());
        // 修改数据库中的 处理人字段
        updateWrapper.set(PrjMatterList::getMatterHandler, prjMatterList.getMatterHandler());
        row = prjMatterListMapper.update(new PrjMatterList(), updateWrapper);
        // 操作日志
        for (PrjMatterList id : projectTaskList) {
            MongodbUtil.insertUserLog(id.getMatterListSid(), BusinessType.CHANGE.getValue(), null, TITLE, "分配事项处理人");
        }
        if (StrUtil.isNotBlank(prjMatterList.getMatterHandler())) {
            deleteHandlerTodoTask(prjMatterList);
        }
        return prjMatterList.getMatterListSidList().length;
    }

    /**
     * 设置日期
     *
     * @param prjMatterList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPlanDate(PrjMatterList prjMatterList) {
        if (prjMatterList.getMatterListSidList().length == 0) {
            throw new BaseException("参数丢失！");
        }
        // 原数据
        List<PrjMatterList> projectTaskList = prjMatterListMapper.selectPrjMatterListList(new PrjMatterList().setMatterListSidList(prjMatterList.getMatterListSidList()));
        // 修改
        LambdaUpdateWrapper<PrjMatterList> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 计划开始日期
        updateWrapper.in(PrjMatterList::getMatterListSid, prjMatterList.getMatterListSidList());
        if (ConstantsEms.YES.equals(prjMatterList.getPlanStartDateIsUpdate())) {
            updateWrapper.set(PrjMatterList::getPlanStartDate, prjMatterList.getPlanStartDate());
        }
        // 计划完成日期
        if (ConstantsEms.YES.equals(prjMatterList.getPlanEndDateIsUpdate())) {
            updateWrapper.set(PrjMatterList::getPlanEndDate, prjMatterList.getPlanEndDate());
        }
        if (ConstantsEms.YES.equals(prjMatterList.getPlanStartDateIsUpdate())
                || ConstantsEms.YES.equals(prjMatterList.getPlanEndDateIsUpdate())) {
            row = prjMatterListMapper.update(new PrjMatterList(), updateWrapper);
            // 操作日志
            for (PrjMatterList id : projectTaskList) {
                MongodbUtil.insertUserLog(id.getMatterListSid(), BusinessType.CHANGE.getValue(), null, TITLE, "设置日期");
            }
        }
        return prjMatterList.getMatterListSidList().length;
    }

    /**
     * 设置即将到期提醒天数
     *
     * @param prjMatterList 入参
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(PrjMatterList prjMatterList) {
        if (prjMatterList.getMatterListSidList().length == 0) {
            throw new BaseException("参数丢失！");
        }
        LambdaUpdateWrapper<PrjMatterList> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(PrjMatterList::getMatterListSid, prjMatterList.getMatterListSidList());
        updateWrapper.set(PrjMatterList::getToexpireDaysMatter, prjMatterList.getToexpireDaysMatter());
        row = prjMatterListMapper.update(new PrjMatterList(), updateWrapper);
        // 操作日志
        for (Long sid : prjMatterList.getMatterListSidList()) {
            MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), null, TITLE, "设置到期提醒天数");
        }
        return row;
    }

    /**
     * 设置待办提醒天数
     *
     * @param prjMatterList 入参
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTodoDays(PrjMatterList prjMatterList) {
        if (prjMatterList.getMatterListSidList().length == 0) {
            throw new BaseException("参数丢失！");
        }
        LambdaUpdateWrapper<PrjMatterList> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 待办天数
        updateWrapper.in(PrjMatterList::getMatterListSid, prjMatterList.getMatterListSidList());
        updateWrapper.set(PrjMatterList::getTodoDaysMatter, prjMatterList.getTodoDaysMatter());
        row = prjMatterListMapper.update(new PrjMatterList(), updateWrapper);
        // 操作日志
        for (Long sid : prjMatterList.getMatterListSidList()) {
            MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), null, TITLE, "设置待办提醒天数");
        }
        return row;
    }

    /**
     * 设置优先级
     *
     * @param prjMatterList 入参
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPriority(PrjMatterList prjMatterList) {
        if (prjMatterList.getMatterListSidList().length == 0) {
            throw new BaseException("参数丢失！");
        }
        // 原数据
        List<PrjMatterList> projectList = prjMatterListMapper.selectBatchIds(Arrays.asList(prjMatterList.getMatterListSidList()));
        // 修改
        LambdaUpdateWrapper<PrjMatterList> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(prjMatterList.getPriorityMatter())) {
            prjMatterList.setPriorityMatter(null);
        }
        // 事项优先级
        updateWrapper.in(PrjMatterList::getMatterListSid, prjMatterList.getMatterListSidList()).set(PrjMatterList::getPriorityMatter, prjMatterList.getPriorityMatter());
        row = prjMatterListMapper.update(new PrjMatterList(), updateWrapper);
        // 操作日志
        for (Long sid : prjMatterList.getMatterListSidList()) {
            MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), null, TITLE, "设置优先级");
        }
        return row;
    }

    @Override
    public DataTotal<MatterTraceTableVo> matterTraceTable(PrjProjectQuery query) {
        List<MatterTraceTableVo> list = prjMatterListMapper.matterTraceTable(query);
        int count = prjMatterListMapper.matterTraceTableCount(query);
        DataTotal<MatterTraceTableVo> dataTotal = new DataTotal();
        dataTotal.setList(list);
        dataTotal.setTotal(count);
        return dataTotal;
    }

    @Override
    public TargetVo matterTraceTarget(PrjProjectQuery query) {
        return prjMatterListMapper.matterTraceTarget(query);
    }


}
