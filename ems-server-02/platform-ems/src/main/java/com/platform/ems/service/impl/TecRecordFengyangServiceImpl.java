package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SysFormProcess;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.TecRecordFengyang;
import com.platform.ems.domain.TecRecordFengyangAttach;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecRecordFengyangAttachMapper;
import com.platform.ems.mapper.TecRecordFengyangMapper;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.service.ITecRecordFengyangService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封样记录(标准封样、产前封样)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@Service
@SuppressWarnings("all")
public class TecRecordFengyangServiceImpl extends ServiceImpl<TecRecordFengyangMapper, TecRecordFengyang> implements ITecRecordFengyangService {
    @Autowired
    private TecRecordFengyangMapper tecRecordFengyangMapper;
    @Autowired
    private TecRecordFengyangAttachMapper tecRecordFengyangAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "封样记录(标准封样、产前封样)";

    /**
     * 查询封样记录(标准封样、产前封样)
     *
     * @param recordFengyangSid 封样记录(标准封样、产前封样)ID
     * @return 封样记录(标准封样 、 产前封样)
     */
    @Override
    public TecRecordFengyang selectTecRecordFengyangById(Long recordFengyangSid) {
        TecRecordFengyang tecRecordFengyang = tecRecordFengyangMapper.selectTecRecordFengyangById(recordFengyangSid);
        if (tecRecordFengyang == null) {
            return null;
        }
        List<TecRecordFengyangAttach> attachList =
                tecRecordFengyangAttachMapper.selectTecRecordFengyangAttachList(new TecRecordFengyangAttach().setRecordFengyangSid(recordFengyangSid));
        tecRecordFengyang.setAttachList(attachList);

        MongodbUtil.find(tecRecordFengyang);
        return tecRecordFengyang;
    }

    /**
     * 查询封样记录(标准封样、产前封样)列表
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 封样记录(标准封样 、 产前封样)
     */
    @Override
    public List<TecRecordFengyang> selectTecRecordFengyangList(TecRecordFengyang tecRecordFengyang) {
        return tecRecordFengyangMapper.selectTecRecordFengyangList(tecRecordFengyang);
    }

    /**
     * 新增封样记录(标准封样、产前封样)
     * 需要注意编码重复校验
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TecRecordFengyang insertTecRecordFengyang(TecRecordFengyang tecRecordFengyang) {
        setConfirmInfo(tecRecordFengyang);
        int row = tecRecordFengyangMapper.insert(tecRecordFengyang);
        TecRecordFengyang fengyang = new TecRecordFengyang();
        if (row > 0) {
            //封样记录-附件
            List<TecRecordFengyangAttach> attachList = tecRecordFengyang.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                addTecRecordFengyangAttach(tecRecordFengyang, attachList);
            }
            fengyang = tecRecordFengyangMapper.selectTecRecordFengyangById(tecRecordFengyang.getRecordFengyangSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecRecordFengyang.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_RECORD_FENGYANG)
                        .setDocumentSid(tecRecordFengyang.getRecordFengyangSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    if (ConstantsEms.FENGYANG_TYPE_BZFY.equals(tecRecordFengyang.getFengyangType())) {
                        sysTodoTask.setTitle("标准封样" + fengyang.getRecordFengyangCode() + "当前是保存状态，请及时处理！");
                    } else {
                        sysTodoTask.setTitle("产前封样" + fengyang.getRecordFengyangCode() + "当前是保存状态，请及时处理！");
                    }
                    sysTodoTask.setDocumentCode(String.valueOf(fengyang.getRecordFengyangCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecRecordFengyang);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecRecordFengyang.getRecordFengyangSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return fengyang;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecRecordFengyang tecRecordFengyang) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecRecordFengyang.getRecordFengyangSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecRecordFengyang.getRecordFengyangSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(TecRecordFengyang o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 封样记录-附件
     */
    private void addTecRecordFengyangAttach(TecRecordFengyang tecRecordFengyang, List<TecRecordFengyangAttach> attachList) {
        deleteAttach(tecRecordFengyang);
        attachList.forEach(o -> {
            o.setRecordFengyangSid(tecRecordFengyang.getRecordFengyangSid());
        });
        tecRecordFengyangAttachMapper.inserts(attachList);
    }

    private void deleteAttach(TecRecordFengyang tecRecordFengyang) {
        tecRecordFengyangAttachMapper.delete(
                new UpdateWrapper<TecRecordFengyangAttach>()
                        .lambda()
                        .eq(TecRecordFengyangAttach::getRecordFengyangSid, tecRecordFengyang.getRecordFengyangSid())
        );
    }

    /**
     * 修改封样记录(标准封样、产前封样)
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecRecordFengyang(TecRecordFengyang tecRecordFengyang) {
        setConfirmInfo(tecRecordFengyang);
        TecRecordFengyang response = tecRecordFengyangMapper.selectTecRecordFengyangById(tecRecordFengyang.getRecordFengyangSid());
        int row = tecRecordFengyangMapper.updateAllById(tecRecordFengyang);
        if (row > 0) {
            //封样记录-附件
            List<TecRecordFengyangAttach> attachList = tecRecordFengyang.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecRecordFengyangAttach(tecRecordFengyang, attachList);
            } else {
                deleteAttach(tecRecordFengyang);
            }
            if (!ConstantsEms.SAVA_STATUS.equals(tecRecordFengyang.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(tecRecordFengyang);
            }
            /*//插入日志
            MongodbUtil.insertUserLog(tecRecordFengyang.getRecordFengyangSid(), BusinessType.UPDATE.getValue(), response, tecRecordFengyang, TITLE);*/
        }
        return row;
    }

    /**
     * 变更封样记录(标准封样、产前封样)
     *
     * @param tecRecordFengyang 封样记录(标准封样、产前封样)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecRecordFengyang(TecRecordFengyang tecRecordFengyang) {
        setConfirmInfo(tecRecordFengyang);
        tecRecordFengyang.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        TecRecordFengyang response = tecRecordFengyangMapper.selectTecRecordFengyangById(tecRecordFengyang.getRecordFengyangSid());
        int row = tecRecordFengyangMapper.updateAllById(tecRecordFengyang);
        if (row > 0) {
            //封样记录-附件
            List<TecRecordFengyangAttach> attachList = tecRecordFengyang.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecRecordFengyangAttach(tecRecordFengyang, attachList);
            } else {
                deleteAttach(tecRecordFengyang);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecRecordFengyang.getRecordFengyangSid(), BusinessType.CHANGE.getValue(), response, tecRecordFengyang, TITLE);
        }
        return row;
    }

    /**
     * 批量删除封样记录(标准封样、产前封样)
     *
     * @param recordFengyangSids 需要删除的封样记录(标准封样、产前封样)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecRecordFengyangByIds(List<Long> recordFengyangSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = tecRecordFengyangMapper.selectCount(new QueryWrapper<TecRecordFengyang>().lambda()
                .in(TecRecordFengyang::getHandleStatus, handleStatusList)
                .in(TecRecordFengyang::getRecordFengyangSid, recordFengyangSids));
        if (count != recordFengyangSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        TecRecordFengyang tecRecordFengyang = new TecRecordFengyang();
        recordFengyangSids.forEach(recordFengyangSid -> {
            tecRecordFengyang.setRecordFengyangSid(recordFengyangSid);
            //校验是否存在待办
            checkTodoExist(tecRecordFengyang);
        });
        tecRecordFengyangAttachMapper.delete(new UpdateWrapper<TecRecordFengyangAttach>().lambda()
                .in(TecRecordFengyangAttach::getRecordFengyangSid, recordFengyangSids));
        return tecRecordFengyangMapper.deleteBatchIds(recordFengyangSids);
    }

    /**
     * 更改确认状态
     *
     * @param tecRecordFengyang
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecRecordFengyang tecRecordFengyang) {
        int row = 0;
        Long[] sids = tecRecordFengyang.getRecordFengyangSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            List<TecRecordFengyang> list = tecRecordFengyangMapper.selectList(new QueryWrapper<TecRecordFengyang>().lambda()
                    .in(TecRecordFengyang::getRecordFengyangSid, sids));
            List<TecRecordFengyang> result = list.stream().filter(o -> StrUtil.isNotBlank(o.getFengyangResult())).collect(Collectors.toList());
            if (list.size() != result.size()) {
                throw new BaseException("封样结果不能为空");
            }
            row = tecRecordFengyangMapper.update(null, new UpdateWrapper<TecRecordFengyang>().lambda()
                    .set(TecRecordFengyang::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(TecRecordFengyang::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecRecordFengyang::getUpdateDate, new Date())
                    .set(TecRecordFengyang::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecRecordFengyang::getConfirmDate, new Date())
                    .in(TecRecordFengyang::getRecordFengyangSid, sids));
        }
        return row;
    }

    /**
     * 单据提交校验
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verify(TecRecordFengyang tecRecordFengyang) {
        if (null == tecRecordFengyang.getRecordFengyangSid() || StrUtil.isBlank(tecRecordFengyang.getHandleStatus())) {
            throw new CustomException("参数错误");
        }
        //校验是否存在待办
        checkTodoExist(tecRecordFengyang);
        return tecRecordFengyangMapper.updateById(tecRecordFengyang);
    }
}
