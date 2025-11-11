package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.platform.ems.domain.TecLinePosition;
import com.platform.ems.domain.TecLinePositionGroup;
import com.platform.ems.domain.TecLinePositionGroupItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecLinePositionGroupItemMapper;
import com.platform.ems.mapper.TecLinePositionGroupMapper;
import com.platform.ems.mapper.TecLinePositionMapper;
import com.platform.ems.service.ITecLinePositionGroupService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 线部位组档案Service业务层处理
 *
 * @author hjj
 * @date 2021-08-19
 */
@Service
@SuppressWarnings("all")
public class TecLinePositionGroupServiceImpl extends ServiceImpl<TecLinePositionGroupMapper, TecLinePositionGroup> implements ITecLinePositionGroupService {
    @Autowired
    private TecLinePositionGroupMapper tecLinePositionGroupMapper;
    @Autowired
    private TecLinePositionGroupItemMapper tecLinePositionGroupItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TecLinePositionMapper tecLinePositionMapper;


    private static final String TITLE = "线部位组档案";

    /**
     * 查询线部位组档案
     *
     * @param groupSid 线部位组档案ID
     * @return 线部位组档案
     */
    @Override
    public TecLinePositionGroup selectTecLinePositionGroupById(Long groupSid) {
        TecLinePositionGroup tecLinePositionGroup = tecLinePositionGroupMapper.selectTecLinePositionGroupById(groupSid);
        if (tecLinePositionGroup == null) {
            return null;
        }
        List<TecLinePositionGroupItem> tecLinePositionGroupItemList =
                tecLinePositionGroupItemMapper.selectTecLinePositionGroupItemList(new TecLinePositionGroupItem().setGroupSid(groupSid));
        tecLinePositionGroup.setTecLinePositionGroupItemList(tecLinePositionGroupItemList);
        MongodbUtil.find(tecLinePositionGroup);
        return tecLinePositionGroup;
    }

    /**
     * 查询线部位组档案列表
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 线部位组档案
     */
    @Override
    public List<TecLinePositionGroup> selectTecLinePositionGroupList(TecLinePositionGroup tecLinePositionGroup) {
        return tecLinePositionGroupMapper.selectTecLinePositionGroupList(tecLinePositionGroup);
    }

    /**
     * 新增线部位组档案
     * 需要注意编码重复校验
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecLinePositionGroup(TecLinePositionGroup tecLinePositionGroup) {
        List<TecLinePositionGroup> list2 = tecLinePositionGroupMapper.selectList(new QueryWrapper<TecLinePositionGroup>().lambda()
                .eq(TecLinePositionGroup::getGroupName, tecLinePositionGroup.getGroupName()));
        if (CollectionUtil.isNotEmpty(list2)) {
            throw new BaseException("线部位组名称已存在！");
        }
        setConfirmInfo(tecLinePositionGroup);
        int row = tecLinePositionGroupMapper.insert(tecLinePositionGroup);
        if (row > 0) {
            //线部位组-明细
            List<TecLinePositionGroupItem> tecLinePositionGroupItemList = tecLinePositionGroup.getTecLinePositionGroupItemList();
            if(tecLinePositionGroup.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())){
                if (CollectionUtil.isEmpty(tecLinePositionGroupItemList)) {
                    throw new BaseException("线部位组：" + tecLinePositionGroup.getGroupCode() + ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }else{
                    List<String> stopList = new ArrayList<>();
                    tecLinePositionGroupItemList.forEach(item->{
                        TecLinePosition linePosition = tecLinePositionMapper.selectTecLinePositionById(item.getLinePositionSid());
                        if(linePosition.getStatus().equals(Status.DISABLE.getCode())){
                            stopList.add(item.getLinePositionName());
                        }
                    });
                    if(stopList.size()>0){
                        throw new BaseException("档案："+stopList.toString()+"已停用，请核实！");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(tecLinePositionGroupItemList)) {
                tecLinePositionGroupItemList.forEach(item -> {
                    item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    item.setCreateDate(new Date());
                });
                addTecLinePositionGroupItem(tecLinePositionGroup, tecLinePositionGroupItemList);
            }
            TecLinePositionGroup positionGroup = tecLinePositionGroupMapper.selectTecLinePositionGroupById(tecLinePositionGroup.getGroupSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecLinePositionGroup.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_LINE_POSITION_GROUP)
                        .setDocumentSid(tecLinePositionGroup.getGroupSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("线部位组档案：" + positionGroup.getGroupCode() + " 当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(positionGroup.getGroupCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecLinePositionGroup);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecLinePositionGroup.getGroupSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecLinePositionGroup tecLinePositionGroup) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecLinePositionGroup.getGroupSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecLinePositionGroup.getGroupSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(TecLinePositionGroup o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            if (CollectionUtil.isEmpty(o.getTecLinePositionGroupItemList())) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 线部位组-明细对象
     */
    private void addTecLinePositionGroupItem(TecLinePositionGroup tecLinePositionGroup, List<TecLinePositionGroupItem> tecLinePositionGroupItemList) {
        deleteItem(tecLinePositionGroup);
        tecLinePositionGroupItemList.forEach(o -> {
            o.setGroupSid(tecLinePositionGroup.getGroupSid());
        });
        tecLinePositionGroupItemMapper.inserts(tecLinePositionGroupItemList);
    }

    private void deleteItem(TecLinePositionGroup tecLinePositionGroup) {
        tecLinePositionGroupItemMapper.delete(
                new UpdateWrapper<TecLinePositionGroupItem>()
                        .lambda()
                        .eq(TecLinePositionGroupItem::getGroupSid, tecLinePositionGroup.getGroupSid())
        );
    }

    /**
     * 修改线部位组档案
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecLinePositionGroup(TecLinePositionGroup tecLinePositionGroup) {
        //校验名称是否重复
        checkNameUnique(tecLinePositionGroup);
        setConfirmInfo(tecLinePositionGroup);
        TecLinePositionGroup response = tecLinePositionGroupMapper.selectTecLinePositionGroupById(tecLinePositionGroup.getGroupSid());
        int row = tecLinePositionGroupMapper.updateById(tecLinePositionGroup);
        if (row > 0) {
            //线部位组-明细
            List<TecLinePositionGroupItem> tecLinePositionGroupItemList = tecLinePositionGroup.getTecLinePositionGroupItemList();
            if(tecLinePositionGroup.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())){
                if (CollectionUtil.isEmpty(tecLinePositionGroupItemList)) {
                    throw new BaseException("线部位组：" + tecLinePositionGroup.getGroupCode() + ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }else{
                    List<String> stopList = new ArrayList<>();
                    tecLinePositionGroupItemList.forEach(item->{
                        TecLinePosition linePosition = tecLinePositionMapper.selectTecLinePositionById(item.getLinePositionSid());
                        if(linePosition.getStatus().equals(Status.DISABLE.getCode())){
                            stopList.add(item.getLinePositionName());
                        }
                    });
                    if(stopList.size()>0){
                        throw new BaseException("档案："+stopList.toString()+"已停用，请核实！");
                    }
                }
                //校验是否存在待办
                checkTodoExist(tecLinePositionGroup);
            }
            if (CollectionUtils.isNotEmpty(tecLinePositionGroupItemList)) {
                tecLinePositionGroupItemList.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addTecLinePositionGroupItem(tecLinePositionGroup, tecLinePositionGroupItemList);
            } else {
                deleteItem(tecLinePositionGroup);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecLinePositionGroup.getGroupSid(), BusinessType.UPDATE.getValue(), response, tecLinePositionGroup, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(TecLinePositionGroup tecLinePositionGroup) {
        List<TecLinePositionGroup> list = tecLinePositionGroupMapper.selectList(new QueryWrapper<TecLinePositionGroup>().lambda()
                .eq(TecLinePositionGroup::getGroupName, tecLinePositionGroup.getGroupName()));
        for (TecLinePositionGroup positionGroup : list) {
            if (!tecLinePositionGroup.getGroupSid().equals(positionGroup.getGroupSid())) {
                throw new BaseException("线部位组名称已存在！");
            }
        }
    }

    /**
     * 变更线部位组档案
     *
     * @param tecLinePositionGroup 线部位组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecLinePositionGroup(TecLinePositionGroup tecLinePositionGroup) {
        //校验名称是否重复
        checkNameUnique(tecLinePositionGroup);
        setConfirmInfo(tecLinePositionGroup);
        tecLinePositionGroup.setUpdaterAccount(ApiThreadLocalUtil.get()
                .getUsername()).setUpdateDate(new Date());
        TecLinePositionGroup response = selectTecLinePositionGroupById(tecLinePositionGroup.getGroupSid());
        int row = tecLinePositionGroupMapper.updateAllById(tecLinePositionGroup);
        if (row > 0) {
            //线部位组-明细
            List<TecLinePositionGroupItem> tecLinePositionGroupItemList = tecLinePositionGroup.getTecLinePositionGroupItemList();
            List<TecLinePositionGroupItem> oldList = response.getTecLinePositionGroupItemList();
            if (CollectionUtil.isEmpty(tecLinePositionGroupItemList)) {
                throw new BaseException("线部位组：" + tecLinePositionGroup.getGroupCode() + ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }else{
                List<String> stopList = new ArrayList<>();
                tecLinePositionGroupItemList.forEach(item->{
                    TecLinePosition linePosition = tecLinePositionMapper.selectTecLinePositionById(item.getLinePositionSid());
                    if(linePosition.getStatus().equals(Status.DISABLE.getCode())){
                        stopList.add(item.getLinePositionName());
                    }
                });
                if(stopList.size()>0){
                    throw new BaseException("档案："+stopList.toString()+"已停用，请核实！");
                }
            }
            if (tecLinePositionGroupItemList.size() == oldList.size()) {
                tecLinePositionGroupItemList.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addTecLinePositionGroupItem(tecLinePositionGroup, tecLinePositionGroupItemList);
            } else {
                List<Long> ids = new ArrayList<>();
                for (TecLinePositionGroupItem item : tecLinePositionGroupItemList) {
                    if (item.getGroupItemSid() == null) {
                        item.setGroupSid(tecLinePositionGroup.getGroupSid());
                        item.setUpdateDate(new Date());
                        item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        tecLinePositionGroupItemMapper.insert(item);
                        ids.add(item.getGroupItemSid());
                    } else {
                        item.setUpdateDate(new Date());
                        item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        tecLinePositionGroupItemMapper.updateAllById(item);
                        ids.add(item.getGroupItemSid());
                    }
                }
                for (TecLinePositionGroupItem item : oldList) {
                    if (!ids.contains(item.getGroupItemSid())) {
                        QueryWrapper<TecLinePositionGroupItem> qw = new QueryWrapper<>();
                        qw.eq("group_item_sid", item.getGroupItemSid());
                        tecLinePositionGroupItemMapper.delete(qw);
                    }
                }
            }
            //插入日志
            MongodbUtil.insertUserLog(tecLinePositionGroup.getGroupSid(), BusinessType.CHANGE.getValue(), response, tecLinePositionGroup, TITLE);
        }
        return row;
    }

    /**
     * 批量删除线部位组档案
     *
     * @param groupSids 需要删除的线部位组档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecLinePositionGroupByIds(List<Long> groupSids) {
        Integer count = tecLinePositionGroupMapper.selectCount(new QueryWrapper<TecLinePositionGroup>().lambda()
                .eq(TecLinePositionGroup::getHandleStatus, HandleStatus.SAVE.getCode())
                .in(TecLinePositionGroup::getGroupSid, groupSids));
        if (groupSids.size() != count) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        tecLinePositionGroupItemMapper.delete(new UpdateWrapper<TecLinePositionGroupItem>().lambda()
                .in(TecLinePositionGroupItem::getGroupSid, groupSids));
        TecLinePositionGroup tecLinePositionGroup = new TecLinePositionGroup();
        groupSids.forEach(groupSid -> {
            tecLinePositionGroup.setGroupSid(groupSid);
            //校验是否存在待办
            checkTodoExist(tecLinePositionGroup);
        });
        return tecLinePositionGroupMapper.deleteBatchIds(groupSids);
    }

    /**
     * 启用/停用
     *
     * @param tecLinePositionGroup
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(TecLinePositionGroup tecLinePositionGroup) {
        int row = 0;
        Long[] sids = tecLinePositionGroup.getGroupSidList();
        if (sids != null && sids.length > 0) {
            row = tecLinePositionGroupMapper.update(null, new UpdateWrapper<TecLinePositionGroup>().lambda().set(TecLinePositionGroup::getStatus, tecLinePositionGroup.getStatus())
                    .in(TecLinePositionGroup::getGroupSid, sids));
            for (Long id : sids) {
                tecLinePositionGroup.setGroupSid(id);
                row = tecLinePositionGroupMapper.updateById(tecLinePositionGroup);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = StrUtil.isEmpty(tecLinePositionGroup.getDisableRemark()) ? null : tecLinePositionGroup.getDisableRemark();
                MongodbDeal.status(tecLinePositionGroup.getGroupSid(), tecLinePositionGroup.getStatus(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param tecLinePositionGroup
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecLinePositionGroup tecLinePositionGroup) {
        int row = 0;
        Long[] sids = tecLinePositionGroup.getGroupSidList();
        if (sids != null && sids.length > 0) {
            Integer count = tecLinePositionGroupMapper.selectCount(new QueryWrapper<TecLinePositionGroup>().lambda()
                    .eq(TecLinePositionGroup::getHandleStatus, HandleStatus.SAVE.getCode())
                    .in(TecLinePositionGroup::getGroupSid, sids));
            if (sids.length != count) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = tecLinePositionGroupMapper.update(null, new UpdateWrapper<TecLinePositionGroup>().lambda()
                    .set(TecLinePositionGroup::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(TecLinePositionGroup::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecLinePositionGroup::getConfirmDate, new Date())
                    .in(TecLinePositionGroup::getGroupSid, sids));
            for (Long id : sids) {
                TecLinePositionGroup linePositionGroup = selectTecLinePositionGroupById(id);
                if (CollectionUtil.isEmpty(linePositionGroup.getTecLinePositionGroupItemList())) {
                    throw new BaseException("线部位组：" + linePositionGroup.getGroupCode() + ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }else{
                    List<String> stopList = new ArrayList<>();
                    linePositionGroup.getTecLinePositionGroupItemList().forEach(item->{
                        TecLinePosition linePosition = tecLinePositionMapper.selectTecLinePositionById(item.getLinePositionSid());
                        if(linePosition.getStatus().equals(Status.DISABLE.getCode())){
                            stopList.add(item.getLinePositionName());
                        }
                    });
                    if(stopList.size()>0){
                        throw new BaseException("档案："+stopList.toString()+"已停用，请核实！");
                    }
                }
                tecLinePositionGroup.setGroupSid(id);
                //校验是否存在待办
                checkTodoExist(tecLinePositionGroup);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
