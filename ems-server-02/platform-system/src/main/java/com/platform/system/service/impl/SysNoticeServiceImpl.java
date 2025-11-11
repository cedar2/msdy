package com.platform.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.constant.ConstantsEms;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.MongodbUtil;
import com.platform.system.domain.*;
import com.platform.system.mapper.*;
import org.springframework.stereotype.Service;
import com.platform.system.service.ISysNoticeService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 公告 服务层实现
 *
 * @author platform
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {
    @Resource
    private SysNoticeMapper noticeMapper;
    @Resource
    private SysNoticeAttachMapper attachMapper;
    @Resource
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Resource
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Resource
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Resource
    private SysBusinessBcstMapper sysBusinessBcstMapper;

    private static final String TITLE = "通知公告";

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }


    /**
     * 查询通知公告
     *
     * @param noticeSid 通知公告ID
     * @return 通知公告
     */
    @Override
    public SysNotice selectSysNoticeById(Long noticeSid) {
        SysNotice sysNotice = noticeMapper.selectSysNoticeById(noticeSid);
        sysNotice.setAttachmentList(new ArrayList<>());
        List<SysNoticeAttach> attachList = attachMapper.selectSysNoticeAttachList(new SysNoticeAttach().setNoticeSid(noticeSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            sysNotice.setAttachmentList(attachList);
        }
        MongodbUtil.find(sysNotice);
        return sysNotice;
    }


    /**
     * 查询通知公告列表
     *
     * @param sysNotice 通知公告
     * @return 通知公告
     */
    @Override
    public List<SysNotice> selectSysNoticeList(SysNotice sysNotice) {
        return noticeMapper.selectSysNoticeList(sysNotice);
    }

    /**
     * 新增通知公告
     * 需要注意编码重复校验
     *
     * @param sysNotice 通知公告
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysNotice(SysNotice sysNotice) {
        setConfirmInfo(sysNotice);
        int row = noticeMapper.insert(sysNotice);
        if (row > 0) {
            List<SysNoticeAttach> attachList = sysNotice.getAttachmentList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                insertAttach(sysNotice.getNoticeSid(), attachList);
            }
            //插入日志
            MongodbUtil.insertUserLog(sysNotice.getNoticeSid(), BusinessType.INSERT.getValue(), TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(SysNotice o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    private void insertAttach(Long noticeSid, List<SysNoticeAttach> attachList) {
        attachList.forEach(a -> {
            a.setNoticeSid(noticeSid);
        });
        attachMapper.inserts(attachList);
    }

    private void updateAttach(Long noticeSid, List<SysNoticeAttach> attachList) {
        List<Long> noticeSids = new ArrayList<>();
        noticeSids.add(noticeSid);
        deleteAttach(noticeSids);
        insertAttach(noticeSid, attachList);
    }

    private void deleteAttach(List<Long> noticeSids) {
        attachMapper.delete(new QueryWrapper<SysNoticeAttach>().lambda().in(SysNoticeAttach::getNoticeSid, noticeSids));
    }

    /**
     * 修改通知公告
     *
     * @param sysNotice 通知公告
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysNotice(SysNotice sysNotice) {
        setConfirmInfo(sysNotice);
        SysNotice response = noticeMapper.selectSysNoticeById(sysNotice.getNoticeSid());
        int row = noticeMapper.updateById(sysNotice);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(sysNotice.getAttachmentList())) {
                updateAttach(sysNotice.getNoticeSid(), sysNotice.getAttachmentList());
            }else {
                List<Long> noticeSids = new ArrayList<>();
                noticeSids.add(sysNotice.getNoticeSid());
                deleteAttach(noticeSids);
            }
            //插入日志
            MongodbUtil.insertUserLog(sysNotice.getNoticeSid(), BusinessType.UPDATE.getValue(), response, sysNotice, TITLE);
        }
        return row;
    }

    /**
     * 变更通知公告
     *
     * @param sysNotice 通知公告
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysNotice(SysNotice sysNotice) {
        SysNotice response = noticeMapper.selectSysNoticeById(sysNotice.getNoticeSid());
        setConfirmInfo(sysNotice);
        sysNotice.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = noticeMapper.updateAllById(sysNotice);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(sysNotice.getAttachmentList())) {
                updateAttach(sysNotice.getNoticeSid(), sysNotice.getAttachmentList());
            }else {
                List<Long> noticeSids = new ArrayList<>();
                noticeSids.add(sysNotice.getNoticeSid());
                deleteAttach(noticeSids);
            }
            //插入日志
            MongodbUtil.insertUserLog(sysNotice.getNoticeSid(), BusinessType.CHANGE.getValue(), response, sysNotice, TITLE);
        }
        return row;
    }

    /**
     * 批量删除通知公告
     *
     * @param noticeSids 需要删除的通知公告ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysNoticeByIds(List<Long> noticeSids) {
        Integer count = noticeMapper.selectCount(new QueryWrapper<SysNotice>().lambda()
                .eq(SysNotice::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(SysNotice::getNoticeSid, noticeSids));
        if (count != noticeSids.size()){
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        deleteAttach(noticeSids);
        return noticeMapper.deleteBatchIds(noticeSids);
    }

    /**
     * 启用/停用
     *
     * @param sysNotice
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysNotice sysNotice) {
        int row = 0;
        Long[] sids = sysNotice.getNoticeSidList();
        if (sids != null && sids.length > 0) {
            row = noticeMapper.update(null, new UpdateWrapper<SysNotice>().lambda().set(SysNotice::getStatus, sysNotice.getStatus())
                    .in(SysNotice::getNoticeSid, sids));
            for (Long id : sids) {
                sysNotice.setNoticeSid(id);
                row = noticeMapper.updateById(sysNotice);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = sysNotice.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(sysNotice.getNoticeSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param sysNotice
     * @return
     */
    @Override
    public int check(SysNotice sysNotice) {
        int row = 0;
        Long[] sids = sysNotice.getNoticeSidList();
        if (sids != null && sids.length > 0) {
            Integer count = noticeMapper.selectCount(new QueryWrapper<SysNotice>().lambda()
                    .eq(SysNotice::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(SysNotice::getNoticeSid, sids));
            if (count != sids.length){
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = noticeMapper.update(null, new UpdateWrapper<SysNotice>().lambda()
                    .set(SysNotice::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(SysNotice::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(SysNotice::getConfirmDate, new Date())
                    .in(SysNotice::getNoticeSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 待办、预警消息条数
     */
    @Override
    public SysNotice countMessage(SysNotice sysNotice) {
        Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
        Integer dbTodoTasks = sysTodoTaskMapper.selectCount(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getUserId, userId)
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
        Integer dpTodoTasks = sysTodoTaskMapper.selectCount(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getUserId, userId)
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DP));
        Integer toexpires = sysToexpireBusinessMapper.selectCount(new QueryWrapper<SysToexpireBusiness>().lambda()
                .eq(SysToexpireBusiness::getUserId, userId));
        Integer overdues = sysOverdueBusinessMapper.selectCount(new QueryWrapper<SysOverdueBusiness>().lambda()
                .eq(SysOverdueBusiness::getUserId, userId));
        Integer businessBcsts = sysBusinessBcstMapper.selectCount(new QueryWrapper<SysBusinessBcst>().lambda()
                .eq(SysBusinessBcst::getUserId, userId));
        sysNotice.setStatus(ConstantsEms.ENABLE_STATUS);
        sysNotice.setHandleStatus(ConstantsEms.CHECK_STATUS);
        sysNotice.setToday(new Date());
        List<SysNotice> sysNotices = noticeMapper.selectSysNoticeList(sysNotice);
        Integer notices = sysNotices.size();
        SysNotice notice = new SysNotice();
        notice.setDbTodoTasks(dbTodoTasks)
                .setDpTodoTasks(dpTodoTasks)
                .setToexpires(toexpires)
                .setOverdues(overdues)
                .setBusinessBcsts(businessBcsts)
                .setNotices(notices);
        return notice;
    }

}
