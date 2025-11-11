package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ITecModelLineService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版型线Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-19
 */
@Service
@SuppressWarnings("all")
public class TecModelLineServiceImpl extends ServiceImpl<TecModelLineMapper, TecModelLine> implements ITecModelLineService {
    @Autowired
    private TecModelLineMapper tecModelLineMapper;
    @Autowired
    private TecModelLinePosMapper tecModelLinePosMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private TecModelMapper tecModelMapper;
    @Autowired
    private TecLinePositionMapper tecLinePositionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "版型线";

    /**
     * 查询版型线
     *
     * @param modelSid 版型ID
     * @return 版型线
     */
    @Override
    public TecModelLine selectTecModelLineById(Long modelSid) {
        TecModelLine tecModelLine = tecModelLineMapper.selectTecModelLineById(modelSid);
        if (tecModelLine == null) {
            return null;
        }
        List<TecModelLinePos> tecModelLinePosList =
                tecModelLinePosMapper.selectTecModelLinePosList(new TecModelLinePos().setModelLineSid(tecModelLine.getModelLineSid()));
        tecModelLine.setTecModelLinePosList(tecModelLinePosList);
        MongodbUtil.find(tecModelLine);
        return tecModelLine;
    }

    /**
     * 查询版型线列表
     *
     * @param tecModelLine 版型线
     * @return 版型线
     */
    @Override
    public List<TecModelLine> selectTecModelLineList(TecModelLine tecModelLine) {
        return tecModelLineMapper.selectTecModelLineList(tecModelLine);
    }

    /**
     * 新增版型线
     * 需要注意编码重复校验
     *
     * @param tecModelLine 版型线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelLine(TecModelLine tecModelLine) {
        List<TecModelLine> tecModelLines =
                tecModelLineMapper.selectList(new QueryWrapper<TecModelLine>().lambda().eq(TecModelLine::getModelSid, tecModelLine.getModelSid()));
        if (CollectionUtil.isNotEmpty(tecModelLines)) {
            throw new BaseException("该版型已维护线用量，不能重复维护");
        }
        setConfirmInfo(tecModelLine);
        int row = tecModelLineMapper.insert(tecModelLine);
        if (row > 0) {
            //更新版型档案的是否创建版型线用量
            LambdaUpdateWrapper<TecModel> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TecModel::getModelSid, tecModelLine.getModelSid()).set(TecModel::getIsCreateModelLine, ConstantsEms.YES);
            tecModelMapper.update(null, updateWrapper);
            //版型-线部位
            List<TecModelLinePos> tecModelLinePosList = tecModelLine.getTecModelLinePosList();
            if (CollectionUtil.isNotEmpty(tecModelLinePosList)) {
                addTecModelLinePos(tecModelLine, tecModelLinePosList);
                /*if (ConstantsEms.CHECK_STATUS.equals(tecModelLine.getHandleStatus())) {
                    //确认时回写线部位
                    collbackLinePosition(tecModelLinePosList);
                }*/
                //回写线部位
                collbackLinePosition(tecModelLinePosList);
            }
            TecModelLine modelLine = tecModelLineMapper.selectTecModelLineById(tecModelLine.getModelLineSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecModelLine.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_MODEL_LINE)
                        .setDocumentSid(tecModelLine.getModelLineSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    TecModel tecModel = tecModelMapper.selectTecModelById(tecModelLine.getModelSid());
                    sysTodoTask.setTitle("版型线用量" + tecModel.getModelCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(tecModel.getModelCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecModelLine);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecModelLine.getModelLineSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 回写线部位
     */
    private void collbackLinePosition(List<TecModelLinePos> tecModelLinePosList) {
        List<TecModelLinePos> itemList = tecModelLinePosList.stream().filter(item -> item.getLinePositionSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(itemList)) {
            TecLinePosition tecLinePosition = new TecLinePosition();
            for (TecModelLinePos item : itemList) {
                List<TecLinePosition> list = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                        .eq(TecLinePosition::getLinePositionName, item.getLinePositionName()));
                if (CollectionUtil.isNotEmpty(list)) {
                    TecLinePosition linePosition = list.get(0);
                    //已存在相同线部位，则更新编码、sid、线部位类别、度量方法说明
                    BeanUtil.copyProperties(linePosition, item, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate", "remark"});
                    tecModelLinePosMapper.updateById(item);
                } else {
                    //不存在相同线部位，则插入一笔新数据，并回写编码及sid
                    BeanUtil.copyProperties(item, tecLinePosition, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate"});
                    tecLinePosition.setConfirmDate(new Date())
                            .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                            .setLinePositionCategory(ConstantsEms.ZY)
                            .setStatus(ConstantsEms.ENABLE_STATUS)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS);
                    tecLinePositionMapper.insert(tecLinePosition);
                    TecLinePosition position = new TecLinePosition();
                    position.setLinePositionName(item.getLinePositionName());
                    List<TecLinePosition> linePositionList = tecLinePositionMapper.selectTecLinePositionList(position);
                    tecModelLinePosMapper.updateById(new TecModelLinePos().setModelLinePosSid(item.getModelLinePosSid())
                            .setLinePositionSid(linePositionList.get(0).getLinePositionSid())
                            .setLinePositionCode(linePositionList.get(0).getLinePositionCode()));
                }
            }
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecModelLine tecModelLine) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecModelLine.getModelLineSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecModelLine.getModelLineSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(TecModelLine o) {
        if (o == null) {
            return;
        }
        if (CollectionUtil.isEmpty(o.getTecModelLinePosList())) {
            throw new BaseException("明细不能为空");
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
            List<TecModelLinePos> itemList = o.getTecModelLinePosList();
            List<TecModelLinePos> list = itemList.stream().filter(item -> item.getQuantity() != null).collect(Collectors.toList());
            if (itemList.size() != list.size()) {
                throw new BaseException("用量不能为空");
            }
        }
    }

    /**
     * 版型-线部位
     */
    private void addTecModelLinePos(TecModelLine tecModelLine, List<TecModelLinePos> tecModelLinePosList) {
        deleteItem(tecModelLine);
        tecModelLinePosList.forEach(o -> {
            o.setModelLineSid(tecModelLine.getModelLineSid());
            o.setModelSid(tecModelLine.getModelSid());
            tecModelLinePosMapper.insert(o);
        });
    }

    private void deleteItem(TecModelLine tecModelLine) {
        tecModelLinePosMapper.delete(
                new UpdateWrapper<TecModelLinePos>()
                        .lambda()
                        .eq(TecModelLinePos::getModelLineSid, tecModelLine.getModelLineSid())
        );
    }

    /**
     * 修改版型线
     *
     * @param tecModelLine 版型线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelLine(TecModelLine tecModelLine) {
        setConfirmInfo(tecModelLine);
        TecModelLine response = tecModelLineMapper.selectTecModelLineById(tecModelLine.getModelLineSid());
        int row = tecModelLineMapper.updateById(tecModelLine);
        if (row > 0) {
            //版型-线部位
            List<TecModelLinePos> tecModelLinePosList = tecModelLine.getTecModelLinePosList();
            if (CollectionUtil.isNotEmpty(tecModelLinePosList)) {
                tecModelLinePosList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecModelLinePos(tecModelLine, tecModelLinePosList);
                /*if (ConstantsEms.CHECK_STATUS.equals(tecModelLine.getHandleStatus())) {
                    //确认时回写线部位
                    collbackLinePosition(tecModelLinePosList);
                }*/
                //回写线部位
                collbackLinePosition(tecModelLinePosList);
            } else {
                deleteItem(tecModelLine);
            }
            if (!ConstantsEms.SAVA_STATUS.equals(tecModelLine.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(tecModelLine);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecModelLine.getModelLineSid(), BusinessType.UPDATE.getValue(), response, tecModelLine, TITLE);
        }
        return row;
    }

    /**
     * 变更版型线
     *
     * @param tecModelLine 版型线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecModelLine(TecModelLine tecModelLine) {
        setConfirmInfo(tecModelLine);
        tecModelLine.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        TecModelLine response = tecModelLineMapper.selectTecModelLineById(tecModelLine.getModelLineSid());
        int row = tecModelLineMapper.updateAllById(tecModelLine);
        if (row > 0) {
            //版型-线部位
            List<TecModelLinePos> tecModelLinePosList = tecModelLine.getTecModelLinePosList();
            if (CollectionUtil.isNotEmpty(tecModelLinePosList)) {
                tecModelLinePosList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecModelLinePos(tecModelLine, tecModelLinePosList);
                //回写线部位
                collbackLinePosition(tecModelLinePosList);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecModelLine.getModelLineSid(), BusinessType.CHANGE.getValue(), response, tecModelLine, TITLE);
        }
        return row;
    }

    /**
     * 批量删除版型线
     *
     * @param modelLineSids 需要删除的版型线ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelLineByIds(List<Long> modelLineSids) {
        Integer count = tecModelLineMapper.selectCount(new QueryWrapper<TecModelLine>().lambda()
                .eq(TecModelLine::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(TecModelLine::getModelLineSid, modelLineSids));
        if (count != modelLineSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        TecModelLine tecModelLine = new TecModelLine();
        modelLineSids.forEach(modelLineSid -> {
            tecModelLine.setModelLineSid(modelLineSid);
            //校验是否存在待办
            checkTodoExist(tecModelLine);
            TecModelLine modelLine = tecModelLineMapper.selectOne(new QueryWrapper<TecModelLine>().lambda()
                    .eq(TecModelLine::getModelLineSid, modelLineSid));
            tecModelMapper.update(null, new UpdateWrapper<TecModel>().lambda().set(TecModel::getIsCreateModelLine, ConstantsEms.NO).eq(TecModel::getModelSid, modelLine.getModelSid()));
        });
        tecModelLinePosMapper.delete(new UpdateWrapper<TecModelLinePos>().lambda().in(TecModelLinePos::getModelLineSid, modelLineSids));
        return tecModelLineMapper.deleteBatchIds(modelLineSids);
    }

    /**
     * 更改确认状态
     *
     * @param tecModelLine
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecModelLine tecModelLine) {
        int row = 0;
        Long[] sids = tecModelLine.getModelLineSidList();
        if (sids != null && sids.length > 0) {
            Integer count = tecModelLineMapper.selectCount(new QueryWrapper<TecModelLine>().lambda()
                    .eq(TecModelLine::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(TecModelLine::getModelLineSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            row = tecModelLineMapper.update(null, new UpdateWrapper<TecModelLine>().lambda()
                    .set(TecModelLine::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(TecModelLine::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecModelLine::getConfirmDate, new Date())
                    .in(TecModelLine::getModelLineSid, sids));
            for (Long id : sids) {
                tecModelLine.setModelLineSid(id);
                //校验是否存在待办
                checkTodoExist(tecModelLine);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 添加线部位时校验名称是否重复
     */
    @Override
    public TecModelLinePos verifyPosition(TecModelLinePos tecModelLinePos) {
        if (tecModelLinePos == null) {
            return null;
        }
        List<TecLinePosition> list = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                .eq(TecLinePosition::getLinePositionName, tecModelLinePos.getLinePositionName()));
        //已存在相同线部位，则回写编码及sid
        if (CollectionUtil.isNotEmpty(list)) {
            TecLinePosition linePosition = list.get(0);
            BeanUtil.copyProperties(linePosition, tecModelLinePos, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate", "remark", "unit"});
            tecModelLinePos.setLinePositionCategory(linePosition.getLinePositionCategory());
        }
        return tecModelLinePos;
    }
}
