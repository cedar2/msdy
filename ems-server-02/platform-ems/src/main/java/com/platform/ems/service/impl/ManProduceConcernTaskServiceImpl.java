package com.platform.ems.service.impl;

import java.sql.Struct;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProduceConcernTaskMapper;
import com.platform.ems.domain.ManProduceConcernTask;
import com.platform.ems.service.IManProduceConcernTaskService;

/**
 * 生产关注事项Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-08-01
 */
@Service
@SuppressWarnings("all")
public class ManProduceConcernTaskServiceImpl extends ServiceImpl<ManProduceConcernTaskMapper, ManProduceConcernTask> implements IManProduceConcernTaskService {
    @Autowired
    private ManProduceConcernTaskMapper manProduceConcernTaskMapper;

    private static final String TITLE = "生产关注事项";

    /**
     * 查询生产关注事项
     *
     * @param concernTaskSid 生产关注事项ID
     * @return 生产关注事项
     */
    @Override
    public ManProduceConcernTask selectManProduceConcernTaskById(Long concernTaskSid) {
        ManProduceConcernTask manProduceConcernTask = manProduceConcernTaskMapper.selectManProduceConcernTaskById(concernTaskSid);
        MongodbUtil.find(manProduceConcernTask);
        return manProduceConcernTask;
    }

    /**
     * 查询生产关注事项列表
     *
     * @param manProduceConcernTask 生产关注事项
     * @return 生产关注事项
     */
    @Override
    public List<ManProduceConcernTask> selectManProduceConcernTaskList(ManProduceConcernTask manProduceConcernTask) {
        return manProduceConcernTaskMapper.selectManProduceConcernTaskList(manProduceConcernTask);
    }

    /**
     * 新增生产关注事项
     * 需要注意编码重复校验
     *
     * @param manProduceConcernTask 生产关注事项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProduceConcernTask(ManProduceConcernTask manProduceConcernTask) {
        if (validConcernTaskCode(manProduceConcernTask) >= 1) {
            throw new CustomException("生产关注事项编码已存在，请核实!");
        }

        if (validConcernTaskName(manProduceConcernTask) >= 1) {
            throw new CustomException("生产关注事项名称已存在，请核实!");
        }
        manProduceConcernTask.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());

        if (StrUtil.equals(manProduceConcernTask.getHandleStatus() , ConstantsEms.CHECK_STATUS)) {
            manProduceConcernTask.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }

        int row = manProduceConcernTaskMapper.insert(manProduceConcernTask);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProduceConcernTask(), manProduceConcernTask);
            MongodbDeal.insert(manProduceConcernTask.getConcernTaskSid(), manProduceConcernTask.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    private int validConcernTaskCode (ManProduceConcernTask manProduceConcernTask) {

        List<ManProduceConcernTask> manProduceConcernTaskList = manProduceConcernTaskMapper
                                                                    .selectList(Wrappers.lambdaQuery(ManProduceConcernTask.class)
                                                                        .eq(ManProduceConcernTask::getConcernTaskCode, manProduceConcernTask.getConcernTaskCode()));

        return CollectionUtil.isEmpty(manProduceConcernTaskList) ? 0 : manProduceConcernTaskList.size();

    }

    private int validConcernTaskName (ManProduceConcernTask manProduceConcernTask) {

        List<ManProduceConcernTask> manProduceConcernTaskList = manProduceConcernTaskMapper
                                                                    .selectList(Wrappers.lambdaQuery(ManProduceConcernTask.class)
                                                                        .eq(ManProduceConcernTask::getConcernTaskName, manProduceConcernTask.getConcernTaskName()));

        return CollectionUtil.isEmpty(manProduceConcernTaskList) ? 0 : manProduceConcernTaskList.size();

    }

    /**
     * 修改生产关注事项
     *
     * @param manProduceConcernTask 生产关注事项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProduceConcernTask(ManProduceConcernTask manProduceConcernTask) {

        ManProduceConcernTask dbManProduceConcernTask = manProduceConcernTaskMapper.selectOne(Wrappers.lambdaQuery(ManProduceConcernTask.class)
                .eq(ManProduceConcernTask::getConcernTaskSid, manProduceConcernTask.getConcernTaskSid()));

        if (!StrUtil.equals(dbManProduceConcernTask.getConcernTaskName() , manProduceConcernTask.getConcernTaskName())) {
            int exitCount = validConcernTaskName(manProduceConcernTask);
            if (exitCount >= 1) {
                throw new CustomException("生产关注事项名称已存在，请核实!");
            }
        }

        manProduceConcernTask.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());

        int row = manProduceConcernTaskMapper.updateById(manProduceConcernTask);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(dbManProduceConcernTask, manProduceConcernTask);
            MongodbDeal.update(manProduceConcernTask.getConcernTaskSid(), dbManProduceConcernTask.getHandleStatus(), manProduceConcernTask.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产关注事项
     *
     * @param manProduceConcernTask 生产关注事项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProduceConcernTask(ManProduceConcernTask manProduceConcernTask) {

        ManProduceConcernTask response = manProduceConcernTaskMapper.selectManProduceConcernTaskById(manProduceConcernTask.getConcernTaskSid());

        if (!StrUtil.equals(response.getConcernTaskName() , manProduceConcernTask.getConcernTaskName())) {
            int exitCount = validConcernTaskName(manProduceConcernTask);
            if (exitCount >= 1) {
                throw new CustomException("生产关注事项名称已存在，请核实!");
            }
        }
        if (StrUtil.equals(manProduceConcernTask.getHandleStatus() , ConstantsEms.CHECK_STATUS)) {
            manProduceConcernTask.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        }
        manProduceConcernTask.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());

        int row = manProduceConcernTaskMapper.updateAllById(manProduceConcernTask);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manProduceConcernTask.getConcernTaskSid(), BusinessType.CHANGE.getValue(), response, manProduceConcernTask, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产关注事项
     *
     * @param concernTaskSids 需要删除的生产关注事项ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProduceConcernTaskByIds(List<Long> concernTaskSids) {
        List<ManProduceConcernTask> list = manProduceConcernTaskMapper.selectList(new QueryWrapper<ManProduceConcernTask>()
                .lambda().in(ManProduceConcernTask::getConcernTaskSid, concernTaskSids));
        int row = manProduceConcernTaskMapper.deleteBatchIds(concernTaskSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProduceConcernTask());
                MongodbUtil.insertUserLog(o.getConcernTaskSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param manProduceConcernTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ManProduceConcernTask manProduceConcernTask) {
        int row = 0;
        Long[] sids = manProduceConcernTask.getConcernTaskSidList();
        if (sids != null && sids.length > 0) {

            row = manProduceConcernTaskMapper.update(null, Wrappers.lambdaUpdate(ManProduceConcernTask.class)
                    .set(ManProduceConcernTask::getStatus, manProduceConcernTask.getStatus())
                    .set(ManProduceConcernTask::getUpdateDate , new Date())
                    .set(ManProduceConcernTask::getUpdaterAccount , ApiThreadLocalUtil.get().getUsername())
                    .in(ManProduceConcernTask::getConcernTaskSid, sids));

            for (Long id : sids) {
                manProduceConcernTask.setConcernTaskSid(id);
                row = manProduceConcernTaskMapper.updateById(manProduceConcernTask);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.status(manProduceConcernTask.getConcernTaskSid(), manProduceConcernTask.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manProduceConcernTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManProduceConcernTask manProduceConcernTask) {
        int row = 0;
        Long[] sids = manProduceConcernTask.getConcernTaskSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ManProduceConcernTask> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ManProduceConcernTask::getConcernTaskSid, sids);
            updateWrapper.set(ManProduceConcernTask::getHandleStatus, manProduceConcernTask.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manProduceConcernTask.getHandleStatus())) {
                updateWrapper.set(ManProduceConcernTask::getConfirmDate, new Date());
                updateWrapper.set(ManProduceConcernTask::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = manProduceConcernTaskMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, manProduceConcernTask.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
