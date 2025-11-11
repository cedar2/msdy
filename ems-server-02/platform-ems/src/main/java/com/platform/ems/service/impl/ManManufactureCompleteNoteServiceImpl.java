package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IManManufactureCompleteNoteService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteUserService;
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
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生产完工确认单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManManufactureCompleteNoteServiceImpl extends ServiceImpl<ManManufactureCompleteNoteMapper, ManManufactureCompleteNote> implements IManManufactureCompleteNoteService {
    @Autowired
    private ManManufactureCompleteNoteMapper manManufactureCompleteNoteMapper;
    @Autowired
    private ManManufactureCompleteNoteItemMapper manManufactureCompleteNoteItemMapper;
    @Autowired
    private ManManufactureCompleteNoteAttachMapper manManufactureCompleteNoteAttachMapper;
    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private InvInventoryDocumentServiceImpl InvInventoryDocumentimpl;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemUserService sysUserService;


    private static final String TITLE = "生产完工确认单";

    /**
     * 查询生产完工确认单
     *
     * @param manufactureCompleteNoteSid 生产完工确认单ID
     * @return 生产完工确认单
     */
    @Override
    public ManManufactureCompleteNote selectManManufactureCompleteNoteById(Long manufactureCompleteNoteSid) {
        ManManufactureCompleteNote manManufactureCompleteNote = manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(manufactureCompleteNoteSid);
        if (manManufactureCompleteNote == null) {
            return null;
        }
        completeQuantity(manManufactureCompleteNote);
        ManManufactureCompleteNoteItem manManufactureCompleteNoteItem = new ManManufactureCompleteNoteItem();
        manManufactureCompleteNoteItem.setManufactureCompleteNoteSid(manufactureCompleteNoteSid);
        List<ManManufactureCompleteNoteItem> manManufactureCompleteNoteItemList =
                manManufactureCompleteNoteItemMapper.selectManManufactureCompleteNoteItemList(manManufactureCompleteNoteItem);
        ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach = new ManManufactureCompleteNoteAttach();
        manManufactureCompleteNoteAttach.setManufactureCompleteNoteSid(manufactureCompleteNoteSid);
        List<ManManufactureCompleteNoteAttach> manManufactureCompleteNoteAttachList =
                manManufactureCompleteNoteAttachMapper.selectManManufactureCompleteNoteAttachList(manManufactureCompleteNoteAttach);

        manManufactureCompleteNote.setManManufactureCompleteNoteItemList(manManufactureCompleteNoteItemList);
        manManufactureCompleteNote.setManManufactureCompleteNoteAttachList(manManufactureCompleteNoteAttachList);
        MongodbUtil.find(manManufactureCompleteNote);
        return manManufactureCompleteNote;
    }

    private void completeQuantity(ManManufactureCompleteNote completeNote) {
        List<ManManufactureCompleteNoteItem> itemList = manManufactureCompleteNoteItemMapper.selectList(new QueryWrapper<ManManufactureCompleteNoteItem>().lambda()
                .eq(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, completeNote.getManufactureCompleteNoteSid()));
        if (CollectionUtil.isNotEmpty(itemList)) {
            BigDecimal completeQuantity = itemList.stream().filter(o -> o.getCompleteQuantity() != null && o.getCompleteQuantity().compareTo(BigDecimal.ZERO) == 1)
                    .map(ManManufactureCompleteNoteItem::getCompleteQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            completeNote.setCompleteQuantity(completeQuantity);
        } else {
            completeNote.setCompleteQuantity(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));
        }
    }

    /**
     * 查询生产完工确认单列表
     *
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 生产完工确认单
     */
    @Override
    public List<ManManufactureCompleteNote> selectManManufactureCompleteNoteList(ManManufactureCompleteNote manManufactureCompleteNote) {
        return manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteList(manManufactureCompleteNote);
    }

    /**
     * 新增生产完工确认单
     * 需要注意编码重复校验
     *
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureCompleteNote(ManManufactureCompleteNote manManufactureCompleteNote) {
        setConfirmInfo(manManufactureCompleteNote);
        int row = manManufactureCompleteNoteMapper.insert(manManufactureCompleteNote);
        if (row > 0) {
//            inventoryDocument(manManufactureCompleteNote);
            //生产完工确认单-明细对象
            List<ManManufactureCompleteNoteItem> manManufactureCompleteNoteItemList = manManufactureCompleteNote.getManManufactureCompleteNoteItemList();
            if (CollectionUtils.isNotEmpty(manManufactureCompleteNoteItemList)) {
                addManManufactureCompleteNoteItem(manManufactureCompleteNote, manManufactureCompleteNoteItemList);
                //业务动态通知
                businessBcst(manManufactureCompleteNote, manManufactureCompleteNoteItemList);
            }
            //生产完工确认单-附件对象
            List<ManManufactureCompleteNoteAttach> manManufactureCompleteNoteAttachList = manManufactureCompleteNote.getManManufactureCompleteNoteAttachList();
            if (CollectionUtils.isNotEmpty(manManufactureCompleteNoteAttachList)) {
                addManManufactureCompleteNoteAttach(manManufactureCompleteNote, manManufactureCompleteNoteAttachList);
            }
            ManManufactureCompleteNote completeNote =
                    manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(manManufactureCompleteNote.getManufactureCompleteNoteSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(manManufactureCompleteNote.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_MANUFACTURE_COMPLETE_NOTE)
                        .setDocumentSid(manManufactureCompleteNote.getManufactureCompleteNoteSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("生产完工确认单" + completeNote.getManufactureCompleteNoteCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(completeNote.getManufactureCompleteNoteCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(manManufactureCompleteNote);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(manManufactureCompleteNote.getManufactureCompleteNoteSid(), manManufactureCompleteNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManManufactureCompleteNote manManufactureCompleteNote) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manManufactureCompleteNote.getManufactureCompleteNoteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manManufactureCompleteNote.getManufactureCompleteNoteSid()));
        }
    }

    private void inventoryDocument(ManManufactureCompleteNote manManufactureCompleteNote) {
        if (HandleStatus.CONFIRMED.getCode().equals(manManufactureCompleteNote.getHandleStatus())) {

            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            invInventoryDocument.setReferDocCategory("MCN");//关联单据类别
            invInventoryDocument.setDocumentCategory("RK"); //库存凭证类别
            invInventoryDocument.setAccountDate(new Date());//入库日期
            invInventoryDocument.setType("2");
            invInventoryDocument.setCreateDate(new Date());
            invInventoryDocument.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            BeanCopyUtils.copyProperties(manManufactureCompleteNote, invInventoryDocument);

//                invInventoryDocumentMapper.insert(invInventoryDocument);
            List<ManManufactureCompleteNoteItem> completeNoteItemList = manManufactureCompleteNote.getManManufactureCompleteNoteItemList();
            List<InvInventoryDocumentItem> inventoryDocumentItemList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(completeNoteItemList)) {
                SalSalesOrderItem salesOrderItem = new SalSalesOrderItem();
                for (ManManufactureCompleteNoteItem completeNoteItem : completeNoteItemList) {
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    invInventoryDocumentItem.setInventoryDocumentSid(invInventoryDocument.getInventoryDocumentSid());
                    //关联业务单sid
                    invInventoryDocumentItem.setReferDocumentSid(manManufactureCompleteNote.getManufactureCompleteNoteSid());
                    //关联业务单code
                    invInventoryDocumentItem.setReferDocumentCode(String.valueOf(manManufactureCompleteNote.getManufactureCompleteNoteCode()));
                    //关联业务单行sid
                    invInventoryDocumentItem.setReferDocumentItemSid(completeNoteItem.getManufactureCompleteNoteItemSid());
                    //关联业务单行code
                    invInventoryDocumentItem.setReferDocumentItemNum(completeNoteItem.getItemNum().intValue());
                    //数量
                    invInventoryDocumentItem.setQuantity(completeNoteItem.getCompleteQuantity());
                    invInventoryDocumentItem.setCreateDate(new Date());
                    //价格
                    BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById((completeNoteItem.getMaterialSid()));
                    if (basMaterial != null) {
                        if (basMaterial.getRetailPrice() != null && (basMaterial.getRetailPrice().compareTo(BigDecimal.ZERO)) == 1) {
                            invInventoryDocumentItem.setPrice(basMaterial.getRetailPrice());
                        }
                    }
                    BeanCopyUtils.copyProperties(completeNoteItem, invInventoryDocumentItem);
                    inventoryDocumentItemList.add(invInventoryDocumentItem);
                }
            }

            invInventoryDocument.setInvInventoryDocumentItemList(inventoryDocumentItemList);
            InvInventoryDocumentimpl.insertInvInventoryDocument(invInventoryDocument);
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManManufactureCompleteNote o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            List<ManManufactureCompleteNoteItem> itemList = o.getManManufactureCompleteNoteItemList();
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 生产完工确认单-明细对象
     */
    private void addManManufactureCompleteNoteItem(ManManufactureCompleteNote manManufactureCompleteNote, List<ManManufactureCompleteNoteItem> manManufactureCompleteNoteItemList) {
//        deleteItem(manManufactureCompleteNote);
        long i = 1;
        Long maxItemNum = manManufactureCompleteNote.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (ManManufactureCompleteNoteItem noteItem : manManufactureCompleteNoteItemList) {
            noteItem.setManufactureCompleteNoteSid(manManufactureCompleteNote.getManufactureCompleteNoteSid());
            noteItem.setItemNum(i);
            i++;
        }
        manManufactureCompleteNoteItemMapper.inserts(manManufactureCompleteNoteItemList);
    }

    private void deleteItem(ManManufactureCompleteNote manManufactureCompleteNote) {
        manManufactureCompleteNoteItemMapper.delete(
                new UpdateWrapper<ManManufactureCompleteNoteItem>()
                        .lambda()
                        .eq(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, manManufactureCompleteNote.getManufactureCompleteNoteSid())
        );
    }

    /**
     * 业务动态通知
     */
    private void businessBcst(ManManufactureCompleteNote manManufactureCompleteNote, List<ManManufactureCompleteNoteItem> manManufactureCompleteNoteItemList) {
        if (ConstantsEms.CHECK_STATUS.equals(manManufactureCompleteNote.getHandleStatus())) {
            manManufactureCompleteNoteItemList.forEach(o -> {
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(o.getMaterialSid());
                ManManufactureCompleteNote completeNote =
                        manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(manManufactureCompleteNote.getManufactureCompleteNoteSid());
//                R<LoginUser> userInfo = remoteUserService.getUserInfo(completeNote.getCreatorAccount());
//                Long userId = userInfo.getData().getSysUser().getUserId();
                List<SysUser> sysUsers = sysUserService.selectSysUserList(new SysUser().setUserName(completeNote.getCreatorAccount()));
                if (CollectionUtil.isNotEmpty(sysUsers)) {
                    sysUsers.forEach(user -> {
                        sysBusinessBcst.setUserId(user.getUserId());
                    });
                }
                sysBusinessBcst.setTitle("商品款号" + basMaterial.getMaterialCode() + "新增完工量" + o.getCompleteQuantity())
                        .setDocumentSid(manManufactureCompleteNote.getManufactureCompleteNoteSid())
                        .setDocumentCode(String.valueOf(completeNote.getManufactureCompleteNoteCode()))
                        .setNoticeDate(new Date());
                sysBusinessBcstMapper.insert(sysBusinessBcst);
            });
        }
    }

    /**
     * 生产完工确认单-附件对象
     */
    private void addManManufactureCompleteNoteAttach(ManManufactureCompleteNote manManufactureCompleteNote, List<ManManufactureCompleteNoteAttach> manManufactureCompleteNoteAttachList) {
//        deleteAttach(manManufactureCompleteNote);
        manManufactureCompleteNoteAttachList.forEach(o -> {
            o.setManufactureCompleteNoteSid(manManufactureCompleteNote.getManufactureCompleteNoteSid());
            manManufactureCompleteNoteAttachMapper.insert(o);
        });
    }

    private void deleteAttach(ManManufactureCompleteNote manManufactureCompleteNote) {
        manManufactureCompleteNoteAttachMapper.delete(
                new UpdateWrapper<ManManufactureCompleteNoteAttach>()
                        .lambda()
                        .eq(ManManufactureCompleteNoteAttach::getManufactureCompleteNoteSid, manManufactureCompleteNote.getManufactureCompleteNoteSid())
        );
    }

    /**
     * 修改生产完工确认单
     *
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureCompleteNote(ManManufactureCompleteNote manManufactureCompleteNote) {
        setConfirmInfo(manManufactureCompleteNote);
        ManManufactureCompleteNote response = manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(manManufactureCompleteNote.getManufactureCompleteNoteSid());
        manManufactureCompleteNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manManufactureCompleteNoteMapper.updateAllById(manManufactureCompleteNote);
        if (row > 0) {
//            inventoryDocument(manManufactureCompleteNote);
            //生产完工确认单-明细对象
            List<ManManufactureCompleteNoteItem> noteItemList = manManufactureCompleteNote.getManManufactureCompleteNoteItemList();
            operateItem(manManufactureCompleteNote, noteItemList);
            //生产完工确认单-附件对象
            List<ManManufactureCompleteNoteAttach> attachmentList = manManufactureCompleteNote.getManManufactureCompleteNoteAttachList();
            operateAttachment(manManufactureCompleteNote, attachmentList);
            if (!ConstantsEms.SAVA_STATUS.equals(manManufactureCompleteNote.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(manManufactureCompleteNote);
            }
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manManufactureCompleteNote);
            MongodbDeal.update(manManufactureCompleteNote.getManufactureCompleteNoteSid(), response.getHandleStatus(), manManufactureCompleteNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 生产完工确认单-明细
     */
    private void operateItem(ManManufactureCompleteNote manManufactureCompleteNote, List<ManManufactureCompleteNoteItem> noteItemList) {
        if (CollectionUtil.isNotEmpty(noteItemList)) {
            //最大行号
            List<Long> itemNums = noteItemList.stream().filter(o -> o.getItemNum() != null).map(ManManufactureCompleteNoteItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manManufactureCompleteNote.setMaxItemNum(maxItemNum);
            }
            //新增
            List<ManManufactureCompleteNoteItem> addList = noteItemList.stream().filter(o -> o.getManufactureCompleteNoteItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureCompleteNoteItem(manManufactureCompleteNote, addList);
            }
            //编辑
            List<ManManufactureCompleteNoteItem> editList = noteItemList.stream().filter(o -> o.getManufactureCompleteNoteItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureCompleteNoteItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureCompleteNoteItem> itemList = manManufactureCompleteNoteItemMapper.selectList(new QueryWrapper<ManManufactureCompleteNoteItem>().lambda()
                    .eq(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, manManufactureCompleteNote.getManufactureCompleteNoteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureCompleteNoteItem::getManufactureCompleteNoteItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = noteItemList.stream().map(ManManufactureCompleteNoteItem::getManufactureCompleteNoteItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureCompleteNoteItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(manManufactureCompleteNote);
        }
    }

    /**
     * 生产完工确认单-附件
     */
    private void operateAttachment(ManManufactureCompleteNote manManufactureCompleteNote, List<ManManufactureCompleteNoteAttach> attachmentList) {
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            //新增
            List<ManManufactureCompleteNoteAttach> addList = attachmentList.stream().filter(o -> o.getManufactureCompleteNoteAttachSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureCompleteNoteAttach(manManufactureCompleteNote, addList);
            }
            //编辑
            List<ManManufactureCompleteNoteAttach> editList = attachmentList.stream().filter(o -> o.getManufactureCompleteNoteAttachSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureCompleteNoteAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureCompleteNoteAttach> itemList =
                    manManufactureCompleteNoteAttachMapper.selectList(new QueryWrapper<ManManufactureCompleteNoteAttach>().lambda()
                            .eq(ManManufactureCompleteNoteAttach::getManufactureCompleteNoteSid, manManufactureCompleteNote.getManufactureCompleteNoteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureCompleteNoteAttach::getManufactureCompleteNoteAttachSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = attachmentList.stream().map(ManManufactureCompleteNoteAttach::getManufactureCompleteNoteAttachSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureCompleteNoteAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manManufactureCompleteNote);
        }
    }

    /**
     * 变更生产完工确认单
     *
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureCompleteNote(ManManufactureCompleteNote manManufactureCompleteNote) {
        setConfirmInfo(manManufactureCompleteNote);
        ManManufactureCompleteNote response = manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(manManufactureCompleteNote.getManufactureCompleteNoteSid());
        int row = manManufactureCompleteNoteMapper.updateAllById(manManufactureCompleteNote);
        if (row > 0) {
            //生产完工确认单-明细对象
            List<ManManufactureCompleteNoteItem> noteItemList = manManufactureCompleteNote.getManManufactureCompleteNoteItemList();
            operateItem(manManufactureCompleteNote, noteItemList);
            //生产完工确认单-附件对象
            List<ManManufactureCompleteNoteAttach> attachmentList = manManufactureCompleteNote.getManManufactureCompleteNoteAttachList();
            operateAttachment(manManufactureCompleteNote, attachmentList);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manManufactureCompleteNote);
            MongodbDeal.update(manManufactureCompleteNote.getManufactureCompleteNoteSid(), response.getHandleStatus(), manManufactureCompleteNote.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除生产完工确认单
     *
     * @param manufactureCompleteNoteSids 需要删除的生产完工确认单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureCompleteNoteByIds(List<Long> manufactureCompleteNoteSids) {
        Integer count = manManufactureCompleteNoteMapper.selectCount(new QueryWrapper<ManManufactureCompleteNote>().lambda()
                .eq(ManManufactureCompleteNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManManufactureCompleteNote::getManufactureCompleteNoteSid, manufactureCompleteNoteSids));
        if (count != manufactureCompleteNoteSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        //删除生产进度日报
        manManufactureCompleteNoteMapper.deleteBatchIds(manufactureCompleteNoteSids);
        //删除生产进度日报明细
        manManufactureCompleteNoteItemMapper.delete(new UpdateWrapper<ManManufactureCompleteNoteItem>().lambda()
                .in(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, manufactureCompleteNoteSids));
        //删除生产进度日报附件
        manManufactureCompleteNoteAttachMapper.delete(new UpdateWrapper<ManManufactureCompleteNoteAttach>().lambda()
                .in(ManManufactureCompleteNoteAttach::getManufactureCompleteNoteSid, manufactureCompleteNoteSids));
        ManManufactureCompleteNote manufactureCompleteNote = new ManManufactureCompleteNote();
        manufactureCompleteNoteSids.forEach(manufactureCompleteNoteSid -> {
            manufactureCompleteNote.setManufactureCompleteNoteSid(manufactureCompleteNoteSid);
            //校验是否存在待办
            checkTodoExist(manufactureCompleteNote);
        });
        return manufactureCompleteNoteSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param manManufactureCompleteNote
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManManufactureCompleteNote manManufactureCompleteNote) {
        int row = 0;
        Long[] sids = manManufactureCompleteNote.getManufactureCompleteNoteSidList();
        if (sids != null && sids.length > 0) {
            Integer count = manManufactureCompleteNoteMapper.selectCount(new QueryWrapper<ManManufactureCompleteNote>().lambda()
                    .eq(ManManufactureCompleteNote::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(ManManufactureCompleteNote::getManufactureCompleteNoteSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = manManufactureCompleteNoteMapper.update(null, new UpdateWrapper<ManManufactureCompleteNote>().lambda()
                    .set(ManManufactureCompleteNote::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(ManManufactureCompleteNote::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(ManManufactureCompleteNote::getConfirmDate, new Date())
                    .in(ManManufactureCompleteNote::getManufactureCompleteNoteSid, sids));
            for (Long id : sids) {
                ManManufactureCompleteNote completeNote = manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(id);
                List<ManManufactureCompleteNoteItem> itemList =
                        manManufactureCompleteNoteItemMapper.selectList(new QueryWrapper<ManManufactureCompleteNoteItem>().lambda()
                                .eq(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, id));
                completeNote.setManManufactureCompleteNoteItemList(itemList);
                if (CollectionUtil.isNotEmpty(itemList)) {
//                    inventoryDocument(completeNote);
                    //业务动态通知
                    businessBcst(completeNote, itemList);
                } else {
                    throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }
                manManufactureCompleteNote.setManufactureCompleteNoteSid(id);
                //校验是否存在待办
                checkTodoExist(manManufactureCompleteNote);
                //插入日志
                /*List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, manManufactureCompleteNote.getHandleStatus(), msgList, TITLE);*/
            }
        }
        return row;
    }

    /**
     * 提交
     *
     * @param manManufactureCompleteNote
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approval(ManManufactureCompleteNote manManufactureCompleteNote) {
        int row = 0;
        Long[] sids = manManufactureCompleteNote.getManufactureCompleteNoteSidList();
        if (sids != null && sids.length > 0) {
            List<String> handleStatusList = Arrays.asList(manManufactureCompleteNote.getHandleStatusList());
            handleStatusList.add(ConstantsEms.SAVA_STATUS);
            handleStatusList.add(HandleStatus.RETURNED.getCode());
            Integer count = manManufactureCompleteNoteMapper.selectCount(new QueryWrapper<ManManufactureCompleteNote>().lambda()
                    .in(ManManufactureCompleteNote::getHandleStatus, handleStatusList)
                    .in(ManManufactureCompleteNote::getManufactureCompleteNoteSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
            }
            row = manManufactureCompleteNoteMapper.update(null, new UpdateWrapper<ManManufactureCompleteNote>().lambda()
                    .set(ManManufactureCompleteNote::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(ManManufactureCompleteNote::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(ManManufactureCompleteNote::getConfirmDate, new Date())
                    .in(ManManufactureCompleteNote::getManufactureCompleteNoteSid, sids));
            for (Long id : sids) {
                ManManufactureCompleteNote completeNote = manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(id);
                List<ManManufactureCompleteNoteItem> itemList =
                        manManufactureCompleteNoteItemMapper.selectList(new QueryWrapper<ManManufactureCompleteNoteItem>().lambda()
                                .eq(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, id));
                if (CollectionUtil.isNotEmpty(itemList)) {
                    completeNote.setManManufactureCompleteNoteItemList(itemList);
//                    inventoryDocument(completeNote);
                    //业务动态通知
                    businessBcst(completeNote, itemList);
                } else {
                    throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
                }
                manManufactureCompleteNote.setManufactureCompleteNoteSid(id);
                //校验是否存在待办
                checkTodoExist(manManufactureCompleteNote);
            }
        }
        return row;
    }

    /**
     * 作废-生产完工确认单
     */
    @Override
    public int cancellationManufactureCompleteNoteById(Long manufactureCompleteNoteSid) {
        ManManufactureCompleteNote manManufactureCompleteNote = manManufactureCompleteNoteMapper.selectManManufactureCompleteNoteById(manufactureCompleteNoteSid);
        if (!ConstantsEms.CHECK_STATUS.equals(manManufactureCompleteNote.getHandleStatus())) {
            throw new BaseException(ConstantsEms.CONFIRM_CANCELLATION);
        }
        //插入日志
        MongodbUtil.insertUserLog(manufactureCompleteNoteSid, BusinessType.CANCEL.getValue(), manManufactureCompleteNote, manManufactureCompleteNote, TITLE);
        manManufactureCompleteNote.setHandleStatus(ConstantsEms.HANDLE_IM);
        manManufactureCompleteNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manManufactureCompleteNote.setUpdateDate(new Date());
        return manManufactureCompleteNoteMapper.updateById(manManufactureCompleteNote);
    }

    /**
     * 提交前校验-生产完工确认单
     */
    @Override
    public int verify(Long manufactureCompleteNoteSid, String handleStatus) {
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus) || ConstantsEms.BACK_STATUS.equals(handleStatus)) {
            List<ManManufactureCompleteNoteItem> itemList =
                    manManufactureCompleteNoteItemMapper.selectList(new QueryWrapper<ManManufactureCompleteNoteItem>().lambda()
                            .eq(ManManufactureCompleteNoteItem::getManufactureCompleteNoteSid, manufactureCompleteNoteSid));
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
            }
            //校验是否存在待办
            checkTodoExist(new ManManufactureCompleteNote().setManufactureCompleteNoteSid(manufactureCompleteNoteSid));
        } else {
            throw new BaseException(ConstantsEms.SUBMIT_PROMPT_STATEMENT);
        }
        return 1;
    }
}
