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
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.TecRecordTechtransfer;
import com.platform.ems.domain.TecRecordTechtransferAttach;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecRecordTechtransferAttachMapper;
import com.platform.ems.mapper.TecRecordTechtransferMapper;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.service.ITecRecordTechtransferService;
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
 * 技术转移记录Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@Service
@SuppressWarnings("all")
public class TecRecordTechtransferServiceImpl extends ServiceImpl<TecRecordTechtransferMapper, TecRecordTechtransfer> implements ITecRecordTechtransferService {
    @Autowired
    private TecRecordTechtransferMapper tecRecordTechtransferMapper;
    @Autowired
    private TecRecordTechtransferAttachMapper tecRecordTechtransferAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "技术转移记录";

    /**
     * 查询技术转移记录
     *
     * @param recordTechtransferSid 技术转移记录ID
     * @return 技术转移记录
     */
    @Override
    public TecRecordTechtransfer selectTecRecordTechtransferById(Long recordTechtransferSid) {
        TecRecordTechtransfer tecRecordTechtransfer = tecRecordTechtransferMapper.selectTecRecordTechtransferById(recordTechtransferSid);
        if (tecRecordTechtransfer == null) {
            return null;
        }
        List<TecRecordTechtransferAttach> attachList =
                tecRecordTechtransferAttachMapper.selectTecRecordTechtransferAttachList(new TecRecordTechtransferAttach().setRecordTechtransferSid(recordTechtransferSid));
        tecRecordTechtransfer.setAttachList(attachList);
        MongodbUtil.find(tecRecordTechtransfer);
        return tecRecordTechtransfer;
    }

    /**
     * 查询技术转移记录列表
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 技术转移记录
     */
    @Override
    public List<TecRecordTechtransfer> selectTecRecordTechtransferList(TecRecordTechtransfer tecRecordTechtransfer) {
        return tecRecordTechtransferMapper.selectTecRecordTechtransferList(tecRecordTechtransfer);
    }

    /**
     * 新增技术转移记录
     * 需要注意编码重复校验
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TecRecordTechtransfer insertTecRecordTechtransfer(TecRecordTechtransfer tecRecordTechtransfer) {
        setConfirmInfo(tecRecordTechtransfer);
        int row = tecRecordTechtransferMapper.insert(tecRecordTechtransfer);
        TecRecordTechtransfer techtransfer = new TecRecordTechtransfer();
        if (row > 0) {
            //技术转移记录-附件
            List<TecRecordTechtransferAttach> attachList = tecRecordTechtransfer.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                addTecRecordTechtransferAttach(tecRecordTechtransfer, attachList);
            }
            techtransfer = tecRecordTechtransferMapper.selectTecRecordTechtransferById(tecRecordTechtransfer.getRecordTechtransferSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecRecordTechtransfer.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_RECORD_TECHTRANSFER)
                        .setDocumentSid(tecRecordTechtransfer.getRecordTechtransferSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("技术转移" + techtransfer.getRecordTechtransferCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(techtransfer.getRecordTechtransferCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecRecordTechtransfer);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecRecordTechtransfer.getRecordTechtransferSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return techtransfer;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecRecordTechtransfer tecRecordTechtransfer) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecRecordTechtransfer.getRecordTechtransferSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecRecordTechtransfer.getRecordTechtransferSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(TecRecordTechtransfer o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 技术转移记录-附件
     */
    private void addTecRecordTechtransferAttach(TecRecordTechtransfer tecRecordTechtransfer, List<TecRecordTechtransferAttach> tecRecordTechtransferAttachList) {
        deleteAttach(tecRecordTechtransfer);
        tecRecordTechtransferAttachList.forEach(o -> {
            o.setRecordTechtransferSid(tecRecordTechtransfer.getRecordTechtransferSid());
        });
        tecRecordTechtransferAttachMapper.inserts(tecRecordTechtransferAttachList);
    }

    private void deleteAttach(TecRecordTechtransfer tecRecordTechtransfer) {
        tecRecordTechtransferAttachMapper.delete(
                new UpdateWrapper<TecRecordTechtransferAttach>()
                        .lambda()
                        .eq(TecRecordTechtransferAttach::getRecordTechtransferSid, tecRecordTechtransfer.getRecordTechtransferSid())
        );
    }

    /**
     * 修改技术转移记录
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecRecordTechtransfer(TecRecordTechtransfer tecRecordTechtransfer) {
        setConfirmInfo(tecRecordTechtransfer);
        TecRecordTechtransfer response = tecRecordTechtransferMapper.selectTecRecordTechtransferById(tecRecordTechtransfer.getRecordTechtransferSid());
        int row = tecRecordTechtransferMapper.updateAllById(tecRecordTechtransfer);
        if (row > 0) {
            //技术转移记录-附件
            List<TecRecordTechtransferAttach> attachList = tecRecordTechtransfer.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecRecordTechtransferAttach(tecRecordTechtransfer, attachList);
            } else {
                deleteAttach(tecRecordTechtransfer);
            }
            if (!ConstantsEms.SAVA_STATUS.equals(tecRecordTechtransfer.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(tecRecordTechtransfer);
            }
            /*//插入日志
            MongodbUtil.insertUserLog(tecRecordTechtransfer.getRecordTechtransferSid(), BusinessType.UPDATE.getValue(), response, tecRecordTechtransfer, TITLE);*/
        }
        return row;
    }

    /**
     * 变更技术转移记录
     *
     * @param tecRecordTechtransfer 技术转移记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecRecordTechtransfer(TecRecordTechtransfer tecRecordTechtransfer) {
        setConfirmInfo(tecRecordTechtransfer);
        TecRecordTechtransfer response = tecRecordTechtransferMapper.selectTecRecordTechtransferById(tecRecordTechtransfer.getRecordTechtransferSid());
        int row = tecRecordTechtransferMapper.updateAllById(tecRecordTechtransfer);
        if (row > 0) {
            //技术转移记录-附件
            List<TecRecordTechtransferAttach> attachList = tecRecordTechtransfer.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecRecordTechtransferAttach(tecRecordTechtransfer, attachList);
            } else {
                deleteAttach(tecRecordTechtransfer);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecRecordTechtransfer.getRecordTechtransferSid(), BusinessType.CHANGE.getValue(), response, tecRecordTechtransfer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除技术转移记录
     *
     * @param recordTechtransferSids 需要删除的技术转移记录ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecRecordTechtransferByIds(List<Long> recordTechtransferSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = tecRecordTechtransferMapper.selectCount(new QueryWrapper<TecRecordTechtransfer>().lambda()
                .in(TecRecordTechtransfer::getHandleStatus, handleStatusList)
                .in(TecRecordTechtransfer::getRecordTechtransferSid, recordTechtransferSids));
        if (count != recordTechtransferSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        TecRecordTechtransfer tecRecordTechtransfer = new TecRecordTechtransfer();
        recordTechtransferSids.forEach(recordTechtransferSid -> {
            tecRecordTechtransfer.setRecordTechtransferSid(recordTechtransferSid);
            //校验是否存在待办
            checkTodoExist(tecRecordTechtransfer);
        });
        tecRecordTechtransferAttachMapper.delete(new UpdateWrapper<TecRecordTechtransferAttach>().lambda()
                .in(TecRecordTechtransferAttach::getRecordTechtransferSid, recordTechtransferSids));
        return tecRecordTechtransferMapper.deleteBatchIds(recordTechtransferSids);
    }

    /**
     * 更改确认状态
     *
     * @param tecRecordTechtransfer
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecRecordTechtransfer tecRecordTechtransfer) {
        int row = 0;
        Long[] sids = tecRecordTechtransfer.getRecordTechtransferSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            List<TecRecordTechtransfer> list = tecRecordTechtransferMapper.selectList(new QueryWrapper<TecRecordTechtransfer>().lambda()
                    .in(TecRecordTechtransfer::getRecordTechtransferSid, sids));
            List<TecRecordTechtransfer> result = list.stream().filter(o -> StrUtil.isNotBlank(o.getTechtransferResult())).collect(Collectors.toList());
            if (list.size() != result.size()) {
                throw new BaseException("技术转移结果不能为空");
            }
            row = tecRecordTechtransferMapper.update(null, new UpdateWrapper<TecRecordTechtransfer>().lambda()
                    .set(TecRecordTechtransfer::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(TecRecordTechtransfer::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecRecordTechtransfer::getUpdateDate, new Date())
                    .set(TecRecordTechtransfer::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecRecordTechtransfer::getConfirmDate, new Date())
                    .in(TecRecordTechtransfer::getRecordTechtransferSid, sids));
        }
        return row;
    }

    /**
     * 单据提交
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verify(TecRecordTechtransfer tecRecordTechtransfer) {
        if (null == tecRecordTechtransfer.getRecordTechtransferSid() || StrUtil.isBlank(tecRecordTechtransfer.getHandleStatus())) {
            throw new CustomException("参数错误");
        }
        //校验是否存在待办
        checkTodoExist(tecRecordTechtransfer);
        return tecRecordTechtransferMapper.updateById(tecRecordTechtransfer);
    }
}
