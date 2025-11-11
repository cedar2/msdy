package com.platform.ems.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.ManManufactureOrderConcernTaskSetRequest;
import com.platform.ems.domain.dto.response.form.ManManuOrderConcernTracking;
import com.platform.ems.mapper.ManManufactureOrderConcernTaskAttachMapper;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.util.data.ComUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManManufactureOrderConcernTaskMapper;
import com.platform.ems.service.IManManufactureOrderConcernTaskService;

/**
 * 生产订单-关注事项Service业务层处理
 *
 * @author chenkw
 * @date 2022-08-02
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderConcernTaskServiceImpl extends ServiceImpl<ManManufactureOrderConcernTaskMapper, ManManufactureOrderConcernTask> implements IManManufactureOrderConcernTaskService {
    @Autowired
    private ManManufactureOrderConcernTaskMapper manManufactureOrderConcernTaskMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ManManufactureOrderConcernTaskAttachMapper manManufactureOrderConcernTaskAttachMapper;
    @Autowired
    private IBasStaffService basStaffService;

    private static final String TITLE = "生产订单-关注事项";


    /**
     * 即将到期报表-按事项
     * @param manManufactureOrderConcernTask 生产订单-关注事项对象
     * @return
     */
    @Override
    public List<ManManufactureOrderConcernTask> selectExpiringTaskForm(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskMapper.selectToexpireList(manManufactureOrderConcernTask);
        list.forEach(item -> {
            // 图片视频预警灯
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddSx()));
        });
        return list;
    }

    /**
     * 已逾期生产报表-按事项
     * @param manManufactureOrderConcernTask 生产订单-关注事项对象
     * @return
     */
    @Override
    public List<ManManufactureOrderConcernTask> selectOverdueTaskForm(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskMapper.selectOverdueList(manManufactureOrderConcernTask);
        list.forEach(item ->{
            // 图片视频
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddSx()));
        });
        return list;
    }

    /**
     * 查询生产订单-关注事项
     *
     * @param manufactureOrderConcernTaskSid 生产订单-关注事项ID
     * @return 生产订单-关注事项
     */
    @Override
    public ManManufactureOrderConcernTask selectManManufactureOrderConcernTaskById(Long manufactureOrderConcernTaskSid) {
        ManManufactureOrderConcernTask concernTask = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskById(manufactureOrderConcernTaskSid);
        // 图片视频
        concernTask.setPicturePathList(ComUtil.strToArr(concernTask.getPicturePath()));
        concernTask.setVideoPathList(ComUtil.strToArr(concernTask.getVideoPath()));
        MongodbUtil.find(concernTask);
        //生产订单关注事项-附件
        concernTask.setAttachmentList(new ArrayList<>());
        List<ManManufactureOrderConcernTaskAttach> attachList =
                manManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachList(
                        new ManManufactureOrderConcernTaskAttach().setManufactureOrderConcernTaskSid(manufactureOrderConcernTaskSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            concernTask.setAttachmentList(attachList);
        }
        return concernTask;
    }

    /**
     * 生产订单关注事项-附件对象
     */
    private void addManManufactureOrderConcernTaskAttach(ManManufactureOrderConcernTask manManufactureOrderConcernTask, List<ManManufactureOrderConcernTaskAttach> attachList) {
        attachList.forEach(o -> {
            o.setManufactureOrderConcernTaskSid(manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid());
        });
        manManufactureOrderConcernTaskAttachMapper.inserts(attachList);
    }

    private void deleteAttach(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        manManufactureOrderConcernTaskAttachMapper.delete(
                new UpdateWrapper<ManManufactureOrderConcernTaskAttach>()
                        .lambda()
                        .eq(ManManufactureOrderConcernTaskAttach::getManufactureOrderConcernTaskSid, manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid())
        );
    }

    /**
     * 生产订单关注事项-附件
     */
    private void operateAttach(ManManufactureOrderConcernTask manManufactureOrderConcernTask, List<ManManufactureOrderConcernTaskAttach> attachList) {
        if (CollectionUtil.isNotEmpty(attachList)) {
            //新增
            List<ManManufactureOrderConcernTaskAttach> addList = attachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOrderConcernTaskAttach(manManufactureOrderConcernTask, addList);
            }
            //编辑
            List<ManManufactureOrderConcernTaskAttach> editList = attachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureOrderConcernTaskAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureOrderConcernTaskAttach> itemList = manManufactureOrderConcernTaskAttachMapper.selectList(new QueryWrapper<ManManufactureOrderConcernTaskAttach>().lambda()
                    .eq(ManManufactureOrderConcernTaskAttach::getManufactureOrderConcernTaskSid, manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOrderConcernTaskAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = attachList.stream().map(ManManufactureOrderConcernTaskAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOrderConcernTaskAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manManufactureOrderConcernTask);
        }
    }
    /**
     * 查询生产订单-关注事项列表
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 生产订单-关注事项
     */
    @Override
    public List<ManManufactureOrderConcernTask> selectManManufactureOrderConcernTaskList(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(manManufactureOrderConcernTask);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                // 图片视频
                item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                //生产订单关注事项-附件
                item.setAttachmentList(new ArrayList<>());
                List<ManManufactureOrderConcernTaskAttach> attachList =
                        manManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachList(
                                new ManManufactureOrderConcernTaskAttach().setManufactureOrderConcernTaskSid(item.getManufactureOrderConcernTaskSid()));
                if (CollectionUtil.isNotEmpty(attachList)) {
                    item.setAttachmentList(attachList);
                }
            });
        }
        return list;
    }

    /**
     * 新增生产订单-关注事项
     * 需要注意编码重复校验
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOrderConcernTask(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        int row = manManufactureOrderConcernTaskMapper.insert(manManufactureOrderConcernTask);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManManufactureOrderConcernTask(), manManufactureOrderConcernTask);
            MongodbDeal.insert(manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid(), manManufactureOrderConcernTask.getEndStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改生产订单-关注事项
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrderConcernTask(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        ManManufactureOrderConcernTask original = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskById(manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid());
        //生产订单-附件对象
        List<ManManufactureOrderConcernTaskAttach> attachList = manManufactureOrderConcernTask.getAttachmentList();
        operateAttach(manManufactureOrderConcernTask, attachList);
        if (!ConstantsEms.SAVA_STATUS.equals(manManufactureOrderConcernTask.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER)
                    .eq(SysTodoTask::getDocumentSid, manManufactureOrderConcernTask.getManufactureOrderSid()));
        }
        int row = manManufactureOrderConcernTaskMapper.updateById(manManufactureOrderConcernTask);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manManufactureOrderConcernTask);
            MongodbDeal.update(manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid(), original.getEndStatus(), manManufactureOrderConcernTask.getEndStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产订单-关注事项
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureOrderConcernTask(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        ManManufactureOrderConcernTask response = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskById(manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid());
        //生产订单-附件对象
        List<ManManufactureOrderConcernTaskAttach> attachList = manManufactureOrderConcernTask.getAttachmentList();
        operateAttach(manManufactureOrderConcernTask, attachList);
        SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
        sysBusinessBcst.setTitle("生产订单" + manManufactureOrderConcernTask.getManufactureOrderCode() + "已更新")
                .setDocumentSid(manManufactureOrderConcernTask.getManufactureOrderSid())
                .setDocumentCode(manManufactureOrderConcernTask.getManufactureOrderCode())
                .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
        sysBusinessBcstMapper.insert(sysBusinessBcst);
        int row = manManufactureOrderConcernTaskMapper.updateAllById(manManufactureOrderConcernTask);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manManufactureOrderConcernTask.getManufactureOrderConcernTaskSid(), BusinessType.CHANGE.getValue(), response, manManufactureOrderConcernTask, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产订单-关注事项
     *
     * @param manufactureOrderConcernTaskSids 需要删除的生产订单-关注事项ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOrderConcernTaskByIds(List<Long> manufactureOrderConcernTaskSids) {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskMapper.selectList(new QueryWrapper<ManManufactureOrderConcernTask>()
                .lambda().in(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid, manufactureOrderConcernTaskSids));
        //删除生产订单-附件对象
        manManufactureOrderConcernTaskAttachMapper.delete(new UpdateWrapper<ManManufactureOrderConcernTaskAttach>().lambda()
                .in(ManManufactureOrderConcernTaskAttach::getManufactureOrderConcernTaskSid, manufactureOrderConcernTaskSids));
        ManManufactureOrderConcernTask manManufactureOrderConcernTask = new ManManufactureOrderConcernTask();
        int row = manManufactureOrderConcernTaskMapper.deleteBatchIds(manufactureOrderConcernTaskSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManManufactureOrderConcernTask());
                MongodbUtil.insertUserLog(o.getManufactureOrderConcernTaskSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }



    /**
     * 查询生产订单-关注事项报表
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 生产订单-关注事项集合
     */
    @Override
    public List<ManManufactureOrderConcernTask> selectManManufactureOrderConcernTaskForm(ManManufactureOrderConcernTask manManufactureOrderConcernTask) {
        List<ManManufactureOrderConcernTask> list = manManufactureOrderConcernTaskMapper.selectManManufactureOrderConcernTaskList(manManufactureOrderConcernTask);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                // 图片视频预警灯
                item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
                item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddSx()));
                //生产订单关注事项-附件
                item.setAttachmentList(new ArrayList<>());
                List<ManManufactureOrderConcernTaskAttach> attachList =
                        manManufactureOrderConcernTaskAttachMapper.selectManManufactureOrderConcernTaskAttachList(
                                new ManManufactureOrderConcernTaskAttach().setManufactureOrderConcernTaskSid(item.getManufactureOrderConcernTaskSid()));
                if (CollectionUtil.isNotEmpty(attachList)) {
                    item.setAttachmentList(attachList);
                }
            });
        }
        return list;
    }


    /**
     * 设置计划信息和进度信息
     * @param request
     * @return
     */
    @Override
    public int concernSet(ManManufactureOrderConcernTaskSetRequest request) {
        LambdaUpdateWrapper<ManManufactureOrderConcernTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0, flag = 0;
        updateWrapper.in(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid, request.getManufactureOrderConcernTaskSidList());
        if ("JH".equals(request.getSetType())) {
            if ("Y".equals(request.getHandlerSidIsUpd())) {
                flag = 1;
                // wp 2022-10-11 优化提示信息
                if (request.getHandlerSid() == 0) {
                    throw new CustomException("负责人不允许为空");
                }
                //---------------------------------------------
                updateWrapper.set(ManManufactureOrderConcernTask::getHandlerSid, request.getHandlerSid());
                if (request.getHandlerSid() != null) {
                    BasStaff basStaff = basStaffService.selectCodeNameById(request.getHandlerSid());
                    if (basStaff != null) {
                        updateWrapper.set(ManManufactureOrderConcernTask::getHandlerCode, basStaff.getStaffCode());
                    }
                }
            }
            if ("Y".equals(request.getPlanStartDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderConcernTask::getPlanStartDate, request.getPlanStartDate());
            }
            if ("Y".equals(request.getPlanEndDateIsUpd())) {
                flag = 1;
                // wp 2022-10-11 优化提示信息
                if (request.getPlanEndDate() == null) {
                    throw new CustomException("计划完成日期不允许为空");
                }
                //---------------------------------------------
                updateWrapper.set(ManManufactureOrderConcernTask::getPlanEndDate, request.getPlanEndDate());
            }
            // wp 新增-修改计划完成量
            if ("Y".equals(request.getPlanQuantityIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderConcernTask::getPlanQuantity, request.getPlanQuantity());
            }
        }
        else if ("JD".equals(request.getSetType())) {
            // 2022-10-11 wp 优化提示信息，修改完成状态
            if ("Y".equals(request.getEndStatusIsUpd())) {
                flag = 1;
                if (request.getEndStatus() == null) {
                    throw new CustomException("完成状态不允许为空");
                }
                updateWrapper.set(ManManufactureOrderConcernTask::getEndStatus, request.getEndStatus());
            }
            //---------------------------------------------
            if ("Y".equals(request.getActualStartDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderConcernTask::getActualStartDate, request.getActualStartDate());
            }
            if ("Y".equals(request.getActualEndDateIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderConcernTask::getActualEndDate, request.getActualEndDate());
            }
            if ("Y".equals(request.getActualQuantityIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderConcernTask::getActualQuantity, request.getActualQuantity());
            }
            if ("Y".equals(request.getHandleCommentIsUpd())) {
                flag = 1;
                updateWrapper.set(ManManufactureOrderConcernTask::getHandleComment, request.getHandleComment());
            }
            //---------------------------------------------
            if ("Y".equals(request.getPicturePathListIsUpd())) {
                flag = 1;
                String picture = null;
                if (ArrayUtil.isNotEmpty(request.getPicturePathList())) {
                    picture = "";
                    for (int i = 0; i < request.getPicturePathList().length; i++) {
                        picture = picture + request.getPicturePathList()[i] + ";";
                    }
                }
                updateWrapper.set(ManManufactureOrderConcernTask::getPicturePath, picture);
            }
            if ("Y".equals(request.getVideoPathListIsUpd())) {
                flag = 1;
                String video = null;
                if (ArrayUtil.isNotEmpty(request.getVideoPathList())) {
                    video = "";
                    for (int i = 0; i < request.getVideoPathList().length; i++) {
                        video = video + request.getVideoPathList()[i] + ";";
                    }
                }
                updateWrapper.set(ManManufactureOrderConcernTask::getVideoPath, video);
            }
        }
        else {}
        if (flag == 1) {
            if (updateWrapper.getSqlSet() != null) {
                row = manManufactureOrderConcernTaskMapper.update(null, updateWrapper);
            }
        }
        return row;
    }

    /**
     * 设置即将到期提醒天数
     * @param concernTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(ManManufactureOrderConcernTask concernTask) {
        if (concernTask.getManufactureOrderConcernTaskSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderConcernTask> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid, concernTask.getManufactureOrderConcernTaskSidList());
        updateWrapper.set(ManManufactureOrderConcernTask::getToexpireDaysScddSx, concernTask.getToexpireDaysScddSx());
        row = manManufactureOrderConcernTaskMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 进度反馈按钮
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProcessStatus(ManManufactureOrderConcernTask request) {
        int row = 0;
        if (request.getManufactureOrderConcernTaskSid() == null){
            throw new BaseException("请选择行！");
        }
        if (request.getEndStatus() == null){
            throw new BaseException("完成状态不能为空！");
        }
        String picture = null, video = null;
        if (ArrayUtil.isNotEmpty(request.getPicturePathList())) {
            picture = String.join(";", Arrays.asList(request.getPicturePathList()));
        }
        if (ArrayUtil.isNotEmpty(request.getVideoPathList())) {
            video = String.join(";", Arrays.asList(request.getVideoPathList()));
        }
        //生产订单-附件对象
        List<ManManufactureOrderConcernTaskAttach> attachList = request.getAttachmentList();
        operateAttach(request, attachList);
        if (!ConstantsEms.SAVA_STATUS.equals(request.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER)
                    .eq(SysTodoTask::getDocumentSid, request.getManufactureOrderSid()));
        }
        LambdaUpdateWrapper<ManManufactureOrderConcernTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ManManufactureOrderConcernTask::getManufactureOrderConcernTaskSid, request.getManufactureOrderConcernTaskSid());
        updateWrapper.set(ManManufactureOrderConcernTask::getEndStatus, request.getEndStatus());
        updateWrapper.set(ManManufactureOrderConcernTask::getHandleComment, request.getHandleComment());
        updateWrapper.set(ManManufactureOrderConcernTask::getPicturePath, picture);
        updateWrapper.set(ManManufactureOrderConcernTask::getVideoPath, video);
        row = manManufactureOrderConcernTaskMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 生产进度跟踪报表（事项）
     *
     * @param request
     * @return
     */
    @Override
    public List<ManManuOrderConcernTracking> selectManufactureOrderConcernTrackingList(ManManuOrderConcernTracking request) {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<ManManuOrderConcernTracking> list = manManufactureOrderConcernTaskMapper.selectManufactureOrderConcernTrackingList(request);
        list.forEach(item -> {
            // 图片视频预警灯
            item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
            item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            item.setLight(ComUtil.lightValue(item.getEndStatus(), item.getPlanEndDate(), item.getToexpireDaysScddSx()));
        });
        return list;
    }
}
