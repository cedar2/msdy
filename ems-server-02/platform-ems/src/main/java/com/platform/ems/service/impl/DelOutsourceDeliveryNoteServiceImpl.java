package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import com.platform.ems.mapper.*;
import com.platform.ems.service.IDelOutsourceDeliveryNoteService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外发加工收料单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class DelOutsourceDeliveryNoteServiceImpl extends ServiceImpl<DelOutsourceDeliveryNoteMapper, DelOutsourceDeliveryNote> implements IDelOutsourceDeliveryNoteService {
    @Autowired
    private DelOutsourceDeliveryNoteMapper delOutsourceDeliveryNoteMapper;
    @Autowired
    private DelOutsourceDeliveryNoteItemMapper delOutsourceDeliveryNoteItemMapper;
    @Autowired
    private DelOutsourceDeliveryNoteAttachmentMapper delOutsourceDeliveryNoteAttachmentMapper;
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


    private static final String TITLE = "外发加工收料单";

    /**
     * 查询外发加工收料单
     *
     * @param deliveryNoteSid 外发加工收料单ID
     * @return 外发加工收料单
     */
    @Override
    public DelOutsourceDeliveryNote selectDelOutsourceDeliveryNoteById(Long deliveryNoteSid) {
        DelOutsourceDeliveryNote delOutsourceDeliveryNote = delOutsourceDeliveryNoteMapper.selectDelOutsourceDeliveryNoteById(deliveryNoteSid);
        if (delOutsourceDeliveryNote == null) {
            return null;
        }
        //外发加工收料单-明细
        DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem = new DelOutsourceDeliveryNoteItem();
        delOutsourceDeliveryNoteItem.setDeliveryNoteSid(deliveryNoteSid);
        List<DelOutsourceDeliveryNoteItem> delOutsourceDeliveryNoteItemList =
                delOutsourceDeliveryNoteItemMapper.selectDelOutsourceDeliveryNoteItemList(delOutsourceDeliveryNoteItem);
        //外发加工收料单-附件
        DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment = new DelOutsourceDeliveryNoteAttachment();
        delOutsourceDeliveryNoteItem.setDeliveryNoteSid(deliveryNoteSid);
        List<DelOutsourceDeliveryNoteAttachment> delOutsourceDeliveryNoteAttachmentList =
                delOutsourceDeliveryNoteAttachmentMapper.selectDelOutsourceDeliveryNoteAttachmentList(delOutsourceDeliveryNoteAttachment);
        //外发加工收料单-合作伙伴
        //TODO

        MongodbUtil.find(delOutsourceDeliveryNote);

        delOutsourceDeliveryNote.setDelOutsourceDeliveryNoteItemList(delOutsourceDeliveryNoteItemList);
        delOutsourceDeliveryNote.setDelOutsourceDeliveryNoteAttachmentList(delOutsourceDeliveryNoteAttachmentList);
        return delOutsourceDeliveryNote;
    }

    /**
     * 查询外发加工收料单列表
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 外发加工收料单
     */
    @Override
    public List<DelOutsourceDeliveryNote> selectDelOutsourceDeliveryNoteList(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        /*if (StrUtil.isNotEmpty(delOutsourceDeliveryNote.getManufactureOrderCode())) {
            DelOutsourceDeliveryNoteItem item = new DelOutsourceDeliveryNoteItem();
            item.setManufactureOrderCode(delOutsourceDeliveryNote.getManufactureOrderCode());
            List<DelOutsourceDeliveryNoteItem> itemList = delOutsourceDeliveryNoteItemMapper.getItemList(item);
            if (CollUtil.isEmpty(itemList)) {
                return new ArrayList<>();
            }
            List<Long> deliveryNoteSidList = new ArrayList<>();
            itemList.forEach(i -> {
                deliveryNoteSidList.add(i.getDeliveryNoteSid());
            });
            Long[] sidList = deliveryNoteSidList.toArray(new Long[deliveryNoteSidList.size()]);
            delOutsourceDeliveryNote.setDeliveryNoteSidList(sidList);
        }*/
        return delOutsourceDeliveryNoteMapper.selectDelOutsourceDeliveryNoteList(delOutsourceDeliveryNote);
    }

    /**
     * 新增外发加工收料单
     * 需要注意编码重复校验
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelOutsourceDeliveryNote(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        setConfirmInfo(delOutsourceDeliveryNote);
        int row = delOutsourceDeliveryNoteMapper.insert(delOutsourceDeliveryNote);
        if (row > 0) {
            //外发加工收料单-明细对象
            List<DelOutsourceDeliveryNoteItem> delOutsourceDeliveryNoteItemList = delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteItemList();
            if (CollectionUtils.isNotEmpty(delOutsourceDeliveryNoteItemList)) {
                addDelOutsourceDeliveryNoteItem(delOutsourceDeliveryNote, delOutsourceDeliveryNoteItemList);
            }
            //外发加工收料单-附件对象
            List<DelOutsourceDeliveryNoteAttachment> delOutsourceDeliveryNoteAttachmentList =
                    delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteAttachmentList();
            if (CollectionUtils.isNotEmpty(delOutsourceDeliveryNoteAttachmentList)) {
                addDelOutsourceDeliveryNoteAttachment(delOutsourceDeliveryNote, delOutsourceDeliveryNoteAttachmentList);
            }
            //外发加工收料单-合作伙伴对象
            //TODO
            DelOutsourceDeliveryNote deliveryNote = delOutsourceDeliveryNoteMapper.selectDelOutsourceDeliveryNoteById(delOutsourceDeliveryNote.getDeliveryNoteSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(delOutsourceDeliveryNote.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_OUTSOURCE_DELIVERY_NOTE)
                        .setDocumentSid(delOutsourceDeliveryNote.getDeliveryNoteSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("外发加工收料单" + deliveryNote.getDeliveryNoteCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(deliveryNote.getDeliveryNoteCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(delOutsourceDeliveryNote);
            }
            //动态通知
            businessBcst(delOutsourceDeliveryNote);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(delOutsourceDeliveryNote.getDeliveryNoteSid(), delOutsourceDeliveryNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 动态通知
     */
    private void businessBcst(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        if (ConstantsEms.CHECK_STATUS.equals(delOutsourceDeliveryNote.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            DelOutsourceDeliveryNote note =
                    delOutsourceDeliveryNoteMapper.selectDelOutsourceDeliveryNoteById(delOutsourceDeliveryNote.getDeliveryNoteSid());
            SysUser sysUser = sysUserService.selectSysUserByName(note.getCreatorAccount());
            BasVendor basVendor = basVendorMapper.selectBasVendorById(delOutsourceDeliveryNote.getVendorSid());
            //通知人
            if (sysUser != null) {
                sysBusinessBcst.setUserId(sysUser.getUserId());
            }
            List<DelOutsourceDeliveryNoteItem> itemList = delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteItemList();
            //款数量
            List<Long> materialSidList = itemList.stream().map(DelOutsourceDeliveryNoteItem::getMaterialSid).distinct().collect(Collectors.toList());
            //工序数量
            List<Long> processSidList = itemList.stream().map(DelOutsourceDeliveryNoteItem::getManufactureOrderProcessSid).distinct().collect(Collectors.toList());
            //收料量
            BigDecimal quantity = itemList.stream().map(DelOutsourceDeliveryNoteItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            quantity = new BigDecimal(quantity.stripTrailingZeros().toPlainString());
            sysBusinessBcst.setDocumentSid(note.getDeliveryNoteSid())
                    .setDocumentCode(note.getDeliveryNoteCode().toString())
                    .setNoticeDate(new Date())
                    .setTitle("加工商" + basVendor.getShortName() + "外发加工收料单" + note.getDeliveryNoteCode() + "已收料，共" +
                            materialSidList.size() + "个款" + processSidList.size() + "个工序" + quantity + "件!");
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, delOutsourceDeliveryNote.getDeliveryNoteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, delOutsourceDeliveryNote.getDeliveryNoteSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(DelOutsourceDeliveryNote o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            List<DelOutsourceDeliveryNoteItem> itemList = o.getDelOutsourceDeliveryNoteItemList();
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 外发加工收料单-明细对象
     */
    private void addDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNote delOutsourceDeliveryNote, List<DelOutsourceDeliveryNoteItem> delOutsourceDeliveryNoteItemList) {
//        deleteItem(delOutsourceDeliveryNote);
        long i = 1;
        Long maxItemNum = delOutsourceDeliveryNote.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (DelOutsourceDeliveryNoteItem noteItem : delOutsourceDeliveryNoteItemList) {
            noteItem.setDeliveryNoteSid(delOutsourceDeliveryNote.getDeliveryNoteSid());
            noteItem.setItemNum(i);
            i++;
        }
        delOutsourceDeliveryNoteItemMapper.inserts(delOutsourceDeliveryNoteItemList);
    }

    private void deleteItem(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        delOutsourceDeliveryNoteItemMapper.delete(
                new UpdateWrapper<DelOutsourceDeliveryNoteItem>()
                        .lambda()
                        .eq(DelOutsourceDeliveryNoteItem::getDeliveryNoteSid, delOutsourceDeliveryNote.getDeliveryNoteSid())
        );
    }

    /**
     * 外发加工收料单-附件对象
     */
    private void addDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNote delOutsourceDeliveryNote, List<DelOutsourceDeliveryNoteAttachment> delOutsourceDeliveryNoteAttachmentList) {
//        deleteAttachment(delOutsourceDeliveryNote);
        delOutsourceDeliveryNoteAttachmentList.forEach(o -> {
            o.setDeliveryNoteSid(delOutsourceDeliveryNote.getDeliveryNoteSid());
            delOutsourceDeliveryNoteAttachmentMapper.insert(o);
        });
    }

    private void deleteAttachment(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        delOutsourceDeliveryNoteAttachmentMapper.delete(
                new UpdateWrapper<DelOutsourceDeliveryNoteAttachment>()
                        .lambda()
                        .eq(DelOutsourceDeliveryNoteAttachment::getDeliveryNoteSid, delOutsourceDeliveryNote.getDeliveryNoteSid())
        );
    }

    /**
     * 修改外发加工收料单
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelOutsourceDeliveryNote(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        DelOutsourceDeliveryNote response = delOutsourceDeliveryNoteMapper.selectDelOutsourceDeliveryNoteById(delOutsourceDeliveryNote.getDeliveryNoteSid());
        setConfirmInfo(delOutsourceDeliveryNote);
        delOutsourceDeliveryNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = delOutsourceDeliveryNoteMapper.updateAllById(delOutsourceDeliveryNote);
        if (row > 0) {
            //外发加工收料单-明细对象
            List<DelOutsourceDeliveryNoteItem> noteItemList = delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteItemList();
            operateItem(delOutsourceDeliveryNote, noteItemList);
            //外发加工收料单-附件对象
            List<DelOutsourceDeliveryNoteAttachment> attachmentList =
                    delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteAttachmentList();
            operateAttachment(delOutsourceDeliveryNote, attachmentList);
            //外发加工收料单-合作伙伴对象
            //TODO
            if (!ConstantsEms.SAVA_STATUS.equals(delOutsourceDeliveryNote.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(delOutsourceDeliveryNote);
            }
            //动态通知
            businessBcst(delOutsourceDeliveryNote);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, delOutsourceDeliveryNote);
            MongodbDeal.update(delOutsourceDeliveryNote.getDeliveryNoteSid(), response.getHandleStatus(), delOutsourceDeliveryNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 外发加工收料单-明细
     */
    private void operateItem(DelOutsourceDeliveryNote delOutsourceDeliveryNote, List<DelOutsourceDeliveryNoteItem> noteItemList) {
        if (CollectionUtil.isNotEmpty(noteItemList)) {
            //最大行号
            List<Long> itemNums = noteItemList.stream().filter(o -> o.getItemNum() != null).map(DelOutsourceDeliveryNoteItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                delOutsourceDeliveryNote.setMaxItemNum(maxItemNum);
            }
            //新增
            List<DelOutsourceDeliveryNoteItem> addList = noteItemList.stream().filter(o -> o.getDeliveryNoteItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addDelOutsourceDeliveryNoteItem(delOutsourceDeliveryNote, addList);
            }
            //编辑
            List<DelOutsourceDeliveryNoteItem> editList = noteItemList.stream().filter(o -> o.getDeliveryNoteItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    delOutsourceDeliveryNoteItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<DelOutsourceDeliveryNoteItem> itemList = delOutsourceDeliveryNoteItemMapper.selectList(new QueryWrapper<DelOutsourceDeliveryNoteItem>().lambda()
                    .eq(DelOutsourceDeliveryNoteItem::getDeliveryNoteSid, delOutsourceDeliveryNote.getDeliveryNoteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(DelOutsourceDeliveryNoteItem::getDeliveryNoteItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = noteItemList.stream().map(DelOutsourceDeliveryNoteItem::getDeliveryNoteItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                delOutsourceDeliveryNoteItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(delOutsourceDeliveryNote);
        }
    }

    /**
     * 外发加工收料单-附件
     */
    private void operateAttachment(DelOutsourceDeliveryNote delOutsourceDeliveryNote, List<DelOutsourceDeliveryNoteAttachment> attachmentList) {
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            //新增
            List<DelOutsourceDeliveryNoteAttachment> addList = attachmentList.stream().filter(o -> o.getDeliveryNoteAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addDelOutsourceDeliveryNoteAttachment(delOutsourceDeliveryNote, addList);
            }
            //编辑
            List<DelOutsourceDeliveryNoteAttachment> editList = attachmentList.stream().filter(o -> o.getDeliveryNoteAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    delOutsourceDeliveryNoteAttachmentMapper.updateAllById(o);
                });
            }
            //原有数据
            List<DelOutsourceDeliveryNoteAttachment> itemList =
                    delOutsourceDeliveryNoteAttachmentMapper.selectList(new QueryWrapper<DelOutsourceDeliveryNoteAttachment>().lambda()
                            .eq(DelOutsourceDeliveryNoteAttachment::getDeliveryNoteSid, delOutsourceDeliveryNote.getDeliveryNoteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(DelOutsourceDeliveryNoteAttachment::getDeliveryNoteAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = attachmentList.stream().map(DelOutsourceDeliveryNoteAttachment::getDeliveryNoteAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                delOutsourceDeliveryNoteAttachmentMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttachment(delOutsourceDeliveryNote);
        }
    }

    /**
     * 变更外发加工收料单
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDelOutsourceDeliveryNote(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        DelOutsourceDeliveryNote response = delOutsourceDeliveryNoteMapper.selectDelOutsourceDeliveryNoteById(delOutsourceDeliveryNote.getDeliveryNoteSid());
        setConfirmInfo(delOutsourceDeliveryNote);
        delOutsourceDeliveryNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        delOutsourceDeliveryNote.setUpdateDate(new Date());
        int row = delOutsourceDeliveryNoteMapper.updateAllById(delOutsourceDeliveryNote);
        if (row > 0) {
            //外发加工收料单-明细对象
            List<DelOutsourceDeliveryNoteItem> noteItemList = delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteItemList();
            operateItem(delOutsourceDeliveryNote, noteItemList);
            //外发加工收料单-附件对象
            List<DelOutsourceDeliveryNoteAttachment> attachmentList =
                    delOutsourceDeliveryNote.getDelOutsourceDeliveryNoteAttachmentList();
            operateAttachment(delOutsourceDeliveryNote, attachmentList);
            //外发加工收料单-合作伙伴对象
            //TODO
            //动态通知
            businessBcst(delOutsourceDeliveryNote);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, delOutsourceDeliveryNote);
            MongodbDeal.update(delOutsourceDeliveryNote.getDeliveryNoteSid(), response.getHandleStatus(), delOutsourceDeliveryNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除外发加工收料单
     *
     * @param deliveryNoteSids 需要删除的外发加工收料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelOutsourceDeliveryNoteByIds(List<Long> deliveryNoteSids) {
        Integer count = delOutsourceDeliveryNoteMapper.selectCount(new QueryWrapper<DelOutsourceDeliveryNote>().lambda()
                .eq(DelOutsourceDeliveryNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(DelOutsourceDeliveryNote::getDeliveryNoteSid, deliveryNoteSids));
        if (count != deliveryNoteSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        //删除外发加工收料单
        delOutsourceDeliveryNoteMapper.deleteBatchIds(deliveryNoteSids);
        //删除外发加工收料单明细
        delOutsourceDeliveryNoteItemMapper.delete(new UpdateWrapper<DelOutsourceDeliveryNoteItem>().lambda()
                .in(DelOutsourceDeliveryNoteItem::getDeliveryNoteSid, deliveryNoteSids));
        //删除外发加工收料单附件
        delOutsourceDeliveryNoteAttachmentMapper.delete(new UpdateWrapper<DelOutsourceDeliveryNoteAttachment>().lambda()
                .in(DelOutsourceDeliveryNoteAttachment::getDeliveryNoteSid, deliveryNoteSids));
        //删除外发加工收料单合作伙伴
        //TODO
        DelOutsourceDeliveryNote delOutsourceDeliveryNote = new DelOutsourceDeliveryNote();
        deliveryNoteSids.forEach(deliveryNoteSid -> {
            delOutsourceDeliveryNote.setDeliveryNoteSid(deliveryNoteSid);
            //校验是否存在待办
            checkTodoExist(delOutsourceDeliveryNote);
        });
        return deliveryNoteSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param delOutsourceDeliveryNote
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        int row = 0;
        Long[] sids = delOutsourceDeliveryNote.getDeliveryNoteSidList();
        if (sids != null && sids.length > 0) {
            Integer count = delOutsourceDeliveryNoteMapper.selectCount(new QueryWrapper<DelOutsourceDeliveryNote>().lambda()
                    .eq(DelOutsourceDeliveryNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(DelOutsourceDeliveryNote::getDeliveryNoteSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            for (Long id : sids) {
                DelOutsourceDeliveryNote deliveryNote = selectDelOutsourceDeliveryNoteById(id);
                List<DelOutsourceDeliveryNoteItem> itemList = deliveryNote.getDelOutsourceDeliveryNoteItemList();
                if (CollectionUtil.isEmpty(itemList)) {
                    throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }
                delOutsourceDeliveryNote.setDeliveryNoteSid(id);
                //校验是否存在待办
                checkTodoExist(delOutsourceDeliveryNote);
                row = delOutsourceDeliveryNoteMapper.updateById(delOutsourceDeliveryNote);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //动态通知
                deliveryNote.setHandleStatus(delOutsourceDeliveryNote.getHandleStatus());
                businessBcst(deliveryNote);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, delOutsourceDeliveryNote.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 提交前校验-外发加工收料单
     */
    @Override
    public int verify(Long deliveryNoteSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<DelOutsourceDeliveryNoteItem> itemList =
                    delOutsourceDeliveryNoteItemMapper.selectList(new QueryWrapper<DelOutsourceDeliveryNoteItem>().lambda()
                            .eq(DelOutsourceDeliveryNoteItem::getManufactureOrderSid, deliveryNoteSid));
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
            }
            //校验是否存在待办
            checkTodoExist(new DelOutsourceDeliveryNote().setDeliveryNoteSid(deliveryNoteSid));
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(deliveryNoteSid, BusinessType.SUBMIT.getValue(), msgList, TITLE);
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return 1;
    }
}
