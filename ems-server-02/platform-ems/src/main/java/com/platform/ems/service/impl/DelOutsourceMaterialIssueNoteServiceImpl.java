package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IDelOutsourceMaterialIssueNoteService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外发加工发料单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class DelOutsourceMaterialIssueNoteServiceImpl extends ServiceImpl<DelOutsourceMaterialIssueNoteMapper, DelOutsourceMaterialIssueNote> implements IDelOutsourceMaterialIssueNoteService {
    @Autowired
    private DelOutsourceMaterialIssueNoteMapper delOutsourceMaterialIssueNoteMapper;
    @Autowired
    private DelOutsourceMaterialIssueNoteItemMapper delOutsourceMaterialIssueNoteItemMapper;
    @Autowired
    private DelOutsourceMaterialIssueNoteAttachmentMapper delOutsourceMaterialIssueNoteAttachmentMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工发料单";

    /**
     * 查询外发加工发料单
     *
     * @param issueNoteSid 外发加工发料单ID
     * @return 外发加工发料单
     */
    @Override
    public DelOutsourceMaterialIssueNote selectDelOutsourceMaterialIssueNoteById(Long issueNoteSid) {
        DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote = delOutsourceMaterialIssueNoteMapper.selectDelOutsourceMaterialIssueNoteById(issueNoteSid);
        if (delOutsourceMaterialIssueNote == null) {
            return null;
        }
        //外发加工发料单-明细
        DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem = new DelOutsourceMaterialIssueNoteItem();
        delOutsourceMaterialIssueNoteItem.setIssueNoteSid(issueNoteSid);
        List<DelOutsourceMaterialIssueNoteItem> delOutsourceMaterialIssueNoteItemList =
                delOutsourceMaterialIssueNoteItemMapper.selectDelOutsourceMaterialIssueNoteItemList(delOutsourceMaterialIssueNoteItem);
        //外发加工发料单-附件
        DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment = new DelOutsourceMaterialIssueNoteAttachment();
        delOutsourceMaterialIssueNoteAttachment.setIssueNoteSid(issueNoteSid);
        List<DelOutsourceMaterialIssueNoteAttachment> delOutsourceMaterialIssueNoteAttachmentList =
                delOutsourceMaterialIssueNoteAttachmentMapper.selectDelOutsourceMaterialIssueNoteAttachmentList(delOutsourceMaterialIssueNoteAttachment);
        //外发加工发料单-合作伙伴
        //TODO

        MongodbUtil.find(delOutsourceMaterialIssueNote);

        delOutsourceMaterialIssueNote.setDelOutsourceMaterialIssueNoteItemList(delOutsourceMaterialIssueNoteItemList);
        delOutsourceMaterialIssueNote.setDelOutsourceMaterialIssueNoteAttachmentList(delOutsourceMaterialIssueNoteAttachmentList);
        return delOutsourceMaterialIssueNote;
    }

    /**
     * 查询外发加工发料单列表
     *
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 外发加工发料单
     */
    @Override
    public List<DelOutsourceMaterialIssueNote> selectDelOutsourceMaterialIssueNoteList(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        return delOutsourceMaterialIssueNoteMapper.selectDelOutsourceMaterialIssueNoteList(delOutsourceMaterialIssueNote);
    }

    /**
     * 新增外发加工发料单
     * 需要注意编码重复校验
     *
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelOutsourceMaterialIssueNote(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        setConfirmInfo(delOutsourceMaterialIssueNote);
        int row = delOutsourceMaterialIssueNoteMapper.insert(delOutsourceMaterialIssueNote);
        if (row > 0) {
            //外发加工发料单-明细对象
            List<DelOutsourceMaterialIssueNoteItem> delOutsourceMaterialIssueNoteItemList =
                    delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteItemList();
            if (CollectionUtils.isNotEmpty(delOutsourceMaterialIssueNoteItemList)) {
                addDelOutsourceMaterialIssueNoteItem(delOutsourceMaterialIssueNote, delOutsourceMaterialIssueNoteItemList);
            }
            //外发加工发料单-附件对象
            List<DelOutsourceMaterialIssueNoteAttachment> delOutsourceMaterialIssueNoteAttachmentList =
                    delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteAttachmentList();
            if (CollectionUtils.isNotEmpty(delOutsourceMaterialIssueNoteAttachmentList)) {
                addDelOutsourceMaterialIssueNoteAttachment(delOutsourceMaterialIssueNote, delOutsourceMaterialIssueNoteAttachmentList);
            }
            //外发加工发料单-合作伙伴对象
            //TODO
            DelOutsourceMaterialIssueNote issueNote =
                    delOutsourceMaterialIssueNoteMapper.selectDelOutsourceMaterialIssueNoteById(delOutsourceMaterialIssueNote.getIssueNoteSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(delOutsourceMaterialIssueNote.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_OUTSOURCE_ISSUE_NOTE)
                        .setDocumentSid(delOutsourceMaterialIssueNote.getIssueNoteSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("外发加工发料单" + issueNote.getIssueNoteCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(issueNote.getIssueNoteCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(delOutsourceMaterialIssueNote);
            }
            //动态通知
            businessBcst(delOutsourceMaterialIssueNote);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(delOutsourceMaterialIssueNote.getIssueNoteSid(), delOutsourceMaterialIssueNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 动态通知
     */
    private void businessBcst(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        if (ConstantsEms.CHECK_STATUS.equals(delOutsourceMaterialIssueNote.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            DelOutsourceMaterialIssueNote issueNote =
                    delOutsourceMaterialIssueNoteMapper.selectDelOutsourceMaterialIssueNoteById(delOutsourceMaterialIssueNote.getIssueNoteSid());
            SysUser sysUser = sysUserService.selectSysUserByName(issueNote.getCreatorAccount());
            BasVendor basVendor = basVendorMapper.selectBasVendorById(delOutsourceMaterialIssueNote.getVendorSid());
            //通知人
            if (sysUser != null) {
                sysBusinessBcst.setUserId(sysUser.getUserId());
            }
            List<DelOutsourceMaterialIssueNoteItem> itemList = delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteItemList();
            //款数量
            List<Long> materialSidList = itemList.stream().map(DelOutsourceMaterialIssueNoteItem::getMaterialSid).distinct().collect(Collectors.toList());
            //工序数量
            List<Long> processSidList = itemList.stream().map(DelOutsourceMaterialIssueNoteItem::getManufactureOrderProcessSid).distinct().collect(Collectors.toList());
            //收料量
            BigDecimal quantity = itemList.stream().map(DelOutsourceMaterialIssueNoteItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            quantity = new BigDecimal(quantity.stripTrailingZeros().toPlainString());
            sysBusinessBcst.setDocumentSid(issueNote.getIssueNoteSid())
                    .setDocumentCode(issueNote.getIssueNoteCode().toString())
                    .setNoticeDate(new Date())
                    .setTitle("加工商" + basVendor.getShortName() + "外发加工发料单" + issueNote.getIssueNoteCode() + "已发料，共" +
                            materialSidList.size() + "个款" + processSidList.size() + "个工序" + quantity + "件!");
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(DelOutsourceMaterialIssueNote o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            List<DelOutsourceMaterialIssueNoteItem> itemList = o.getDelOutsourceMaterialIssueNoteItemList();
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 外发加工发料单-明细对象
     */
    private void addDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote, List<DelOutsourceMaterialIssueNoteItem> delOutsourceMaterialIssueNoteItemList) {
//        deleteItem(delOutsourceMaterialIssueNote);
        long i = 1;
        Long maxItemNum = delOutsourceMaterialIssueNote.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (DelOutsourceMaterialIssueNoteItem issueNoteItem : delOutsourceMaterialIssueNoteItemList) {
            issueNoteItem.setIssueNoteSid(delOutsourceMaterialIssueNote.getIssueNoteSid());
            issueNoteItem.setItemNum(i);
            i++;
        }
        delOutsourceMaterialIssueNoteItemMapper.inserts(delOutsourceMaterialIssueNoteItemList);
    }

    private void deleteItem(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        delOutsourceMaterialIssueNoteItemMapper.delete(
                new UpdateWrapper<DelOutsourceMaterialIssueNoteItem>()
                        .lambda()
                        .eq(DelOutsourceMaterialIssueNoteItem::getIssueNoteSid, delOutsourceMaterialIssueNote.getIssueNoteSid())
        );
    }

    /**
     * 外发加工发料单-附件对象
     */
    private void addDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote, List<DelOutsourceMaterialIssueNoteAttachment> delOutsourceMaterialIssueNoteAttachmentList) {
//        deleteAttachment(delOutsourceMaterialIssueNote);
        delOutsourceMaterialIssueNoteAttachmentList.forEach(o -> {
            o.setIssueNoteSid(delOutsourceMaterialIssueNote.getIssueNoteSid());
            delOutsourceMaterialIssueNoteAttachmentMapper.insert(o);
        });
    }

    private void deleteAttachment(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        delOutsourceMaterialIssueNoteAttachmentMapper.delete(
                new UpdateWrapper<DelOutsourceMaterialIssueNoteAttachment>()
                        .lambda()
                        .eq(DelOutsourceMaterialIssueNoteAttachment::getIssueNoteSid, delOutsourceMaterialIssueNote.getIssueNoteSid())
        );
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, delOutsourceMaterialIssueNote.getIssueNoteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, delOutsourceMaterialIssueNote.getIssueNoteSid()));
        }
    }

    /**
     * 修改外发加工发料单
     *
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelOutsourceMaterialIssueNote(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        setConfirmInfo(delOutsourceMaterialIssueNote);
        DelOutsourceMaterialIssueNote response = delOutsourceMaterialIssueNoteMapper.selectDelOutsourceMaterialIssueNoteById(delOutsourceMaterialIssueNote.getIssueNoteSid());
        delOutsourceMaterialIssueNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = delOutsourceMaterialIssueNoteMapper.updateAllById(delOutsourceMaterialIssueNote);
        if (row > 0) {
            //外发加工发料单-明细对象
            List<DelOutsourceMaterialIssueNoteItem> noteItemList =
                    delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteItemList();
            operateItem(delOutsourceMaterialIssueNote, noteItemList);
            //外发加工发料单-附件对象
            List<DelOutsourceMaterialIssueNoteAttachment> attachmentList =
                    delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteAttachmentList();
            operateAttachment(delOutsourceMaterialIssueNote, attachmentList);
            //外发加工发料单-合作伙伴对象
            //TODO
            if (!ConstantsEms.SAVA_STATUS.equals(delOutsourceMaterialIssueNote.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(delOutsourceMaterialIssueNote);
            }
            //动态通知
            businessBcst(delOutsourceMaterialIssueNote);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, delOutsourceMaterialIssueNote);
            MongodbDeal.update(delOutsourceMaterialIssueNote.getIssueNoteSid(), response.getHandleStatus(), delOutsourceMaterialIssueNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 外发加工发料单-明细
     */
    private void operateItem(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote, List<DelOutsourceMaterialIssueNoteItem> noteItemList) {
        if (CollectionUtil.isNotEmpty(noteItemList)) {
            //最大行号
            List<Long> itemNums = noteItemList.stream().filter(o -> o.getItemNum() != null).map(DelOutsourceMaterialIssueNoteItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                delOutsourceMaterialIssueNote.setMaxItemNum(maxItemNum);
            }
            //新增
            List<DelOutsourceMaterialIssueNoteItem> addList = noteItemList.stream().filter(o -> o.getIssueNoteItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addDelOutsourceMaterialIssueNoteItem(delOutsourceMaterialIssueNote, addList);
            }
            //编辑
            List<DelOutsourceMaterialIssueNoteItem> editList = noteItemList.stream().filter(o -> o.getIssueNoteItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    delOutsourceMaterialIssueNoteItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<DelOutsourceMaterialIssueNoteItem> itemList = delOutsourceMaterialIssueNoteItemMapper.selectList(new QueryWrapper<DelOutsourceMaterialIssueNoteItem>().lambda()
                    .eq(DelOutsourceMaterialIssueNoteItem::getIssueNoteSid, delOutsourceMaterialIssueNote.getIssueNoteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(DelOutsourceMaterialIssueNoteItem::getIssueNoteItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = noteItemList.stream().map(DelOutsourceMaterialIssueNoteItem::getIssueNoteItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                delOutsourceMaterialIssueNoteItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(delOutsourceMaterialIssueNote);
        }
    }

    /**
     * 外发加工发料单-附件
     */
    private void operateAttachment(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote, List<DelOutsourceMaterialIssueNoteAttachment> attachmentList) {
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            //新增
            List<DelOutsourceMaterialIssueNoteAttachment> addList = attachmentList.stream().filter(o -> o.getIssueNoteAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addDelOutsourceMaterialIssueNoteAttachment(delOutsourceMaterialIssueNote, addList);
            }
            //编辑
            List<DelOutsourceMaterialIssueNoteAttachment> editList = attachmentList.stream().filter(o -> o.getIssueNoteAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    delOutsourceMaterialIssueNoteAttachmentMapper.updateAllById(o);
                });
            }
            //原有数据
            List<DelOutsourceMaterialIssueNoteAttachment> itemList =
                    delOutsourceMaterialIssueNoteAttachmentMapper.selectList(new QueryWrapper<DelOutsourceMaterialIssueNoteAttachment>().lambda()
                            .eq(DelOutsourceMaterialIssueNoteAttachment::getIssueNoteSid, delOutsourceMaterialIssueNote.getIssueNoteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(DelOutsourceMaterialIssueNoteAttachment::getIssueNoteAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = attachmentList.stream().map(DelOutsourceMaterialIssueNoteAttachment::getIssueNoteAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                delOutsourceMaterialIssueNoteAttachmentMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttachment(delOutsourceMaterialIssueNote);
        }
    }

    /**
     * 变更外发加工发料单
     *
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDelOutsourceMaterialIssueNote(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        DelOutsourceMaterialIssueNote response = delOutsourceMaterialIssueNoteMapper.selectDelOutsourceMaterialIssueNoteById(delOutsourceMaterialIssueNote.getIssueNoteSid());
        setConfirmInfo(delOutsourceMaterialIssueNote);
        delOutsourceMaterialIssueNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        delOutsourceMaterialIssueNote.setUpdateDate(new Date());
        int row = delOutsourceMaterialIssueNoteMapper.updateAllById(delOutsourceMaterialIssueNote);
        if (row > 0) {
            //外发加工发料单-明细对象
            List<DelOutsourceMaterialIssueNoteItem> noteItemList =
                    delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteItemList();
            operateItem(delOutsourceMaterialIssueNote, noteItemList);
            //外发加工发料单-附件对象
            List<DelOutsourceMaterialIssueNoteAttachment> attachmentList =
                    delOutsourceMaterialIssueNote.getDelOutsourceMaterialIssueNoteAttachmentList();
            operateAttachment(delOutsourceMaterialIssueNote, attachmentList);
            //外发加工发料单-合作伙伴对象
            //TODO
            //动态通知
            businessBcst(delOutsourceMaterialIssueNote);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, delOutsourceMaterialIssueNote);
            MongodbDeal.update(delOutsourceMaterialIssueNote.getIssueNoteSid(), response.getHandleStatus(), delOutsourceMaterialIssueNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除外发加工发料单
     *
     * @param issueNoteSids 需要删除的外发加工发料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelOutsourceMaterialIssueNoteByIds(List<Long> issueNoteSids) {
        Integer count = delOutsourceMaterialIssueNoteMapper.selectCount(new QueryWrapper<DelOutsourceMaterialIssueNote>().lambda()
                .eq(DelOutsourceMaterialIssueNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(DelOutsourceMaterialIssueNote::getIssueNoteSid, issueNoteSids));
        if (count != issueNoteSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        //删除外发加工发料单
        delOutsourceMaterialIssueNoteMapper.deleteBatchIds(issueNoteSids);
        //删除外发加工发料单明细
        delOutsourceMaterialIssueNoteItemMapper.delete(new UpdateWrapper<DelOutsourceMaterialIssueNoteItem>().lambda()
                .in(DelOutsourceMaterialIssueNoteItem::getIssueNoteSid, issueNoteSids));
        //删除外发加工发料单附件
        delOutsourceMaterialIssueNoteAttachmentMapper.delete(new UpdateWrapper<DelOutsourceMaterialIssueNoteAttachment>().lambda()
                .in(DelOutsourceMaterialIssueNoteAttachment::getIssueNoteSid, issueNoteSids));
        //删除外发加工发料单合作伙伴
        //TODO
        DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote = new DelOutsourceMaterialIssueNote();
        issueNoteSids.forEach(issueNoteSid -> {
            delOutsourceMaterialIssueNote.setIssueNoteSid(issueNoteSid);
            //校验是否存在待办
            checkTodoExist(delOutsourceMaterialIssueNote);
        });
        return issueNoteSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param delOutsourceMaterialIssueNote
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        int row = 0;
        Long[] sids = delOutsourceMaterialIssueNote.getIssueNoteSidList();
        if (sids != null && sids.length > 0) {
            Integer count = delOutsourceMaterialIssueNoteMapper.selectCount(new QueryWrapper<DelOutsourceMaterialIssueNote>().lambda()
                    .eq(DelOutsourceMaterialIssueNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(DelOutsourceMaterialIssueNote::getIssueNoteSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            for (Long id : sids) {
                DelOutsourceMaterialIssueNote issueNote = selectDelOutsourceMaterialIssueNoteById(id);
                List<DelOutsourceMaterialIssueNoteItem> itemList = issueNote.getDelOutsourceMaterialIssueNoteItemList();
                if (CollectionUtil.isEmpty(itemList)) {
                    throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }
                delOutsourceMaterialIssueNote.setIssueNoteSid(id);
                //校验是否存在待办
                checkTodoExist(delOutsourceMaterialIssueNote);
                row = delOutsourceMaterialIssueNoteMapper.updateById(delOutsourceMaterialIssueNote);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //动态通知
                issueNote.setHandleStatus(delOutsourceMaterialIssueNote.getHandleStatus());
                businessBcst(issueNote);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, delOutsourceMaterialIssueNote.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 提交前校验-外发加工发料单
     */
    @Override
    public int verify(Long issueNoteSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<DelOutsourceMaterialIssueNoteItem> itemList =
                    delOutsourceMaterialIssueNoteItemMapper.selectList(new QueryWrapper<DelOutsourceMaterialIssueNoteItem>().lambda()
                            .eq(DelOutsourceMaterialIssueNoteItem::getIssueNoteSid, issueNoteSid));
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
            }
            //校验是否存在待办
            checkTodoExist(new DelOutsourceMaterialIssueNote().setIssueNoteSid(issueNoteSid));
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(issueNoteSid, BusinessType.SUBMIT.getValue(), msgList, TITLE);
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return 1;
    }
}
