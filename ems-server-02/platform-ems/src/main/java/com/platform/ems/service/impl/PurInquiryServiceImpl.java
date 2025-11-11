package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPurInquiryItemService;
import com.platform.ems.service.IPurInquiryVendorService;
import com.platform.ems.util.MongodbDeal;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.IPurInquiryService;

/**
 * 物料询价单主Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Service
@SuppressWarnings("all")
public class PurInquiryServiceImpl extends ServiceImpl<PurInquiryMapper, PurInquiry> implements IPurInquiryService {
    @Autowired
    private PurInquiryMapper purInquiryMapper;
    @Autowired
    private IPurInquiryItemService purInquiryItemService;
    @Autowired
    private PurInquiryItemMapper purInquiryItemMapper;
    @Autowired
    private PurInquiryAttachMapper purInquiryAttachMapper;
    @Autowired
    private PurInquiryVendorMapper purInquiryVendorMapper;
    @Autowired
    private IPurInquiryVendorService purInquiryVendorService;
    @Autowired
    private PurQuoteBargainItemMapper purQuoteBargainItemMapper;
    @Autowired
    private PurQuoteBargainMapper purQuoteBargainMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;


    private static final String TITLE = "采购询价单";

    private static final String BARGAIN_ITEM_TITLE = "采购报核议价单明细";

    private static final String BARGAIN_TITLE = "采购报核议价单";

    private static final String ATTACH_TITLE = "采购询价单-附件";

    /**
     * 查询物料询价单主
     *
     * @param inquirySid 物料询价单主ID
     * @return 物料询价单主
     */
    @Override
    public PurInquiry selectPurInquiryById(Long inquirySid) {
        PurInquiry purInquiry = purInquiryMapper.selectPurInquiryById(inquirySid);
        if (purInquiry == null){
            throw new CustomException("单据数据丢失，请联系管理员");
        }
        List<PurInquiryItem> itemList = purInquiryItemService.selectPurInquiryItemListById(inquirySid);
        List<PurInquiryVendor> vendorList = purInquiryVendorService.selectPurInquiryVendorListById(inquirySid);
        List<PurInquiryAttach> attachmentList = purInquiryAttachMapper.selectPurInquiryAttachList(new PurInquiryAttach().setInquirySid(inquirySid));
        purInquiry.setItemList(itemList).setVendorList(vendorList).setAttachmentList(attachmentList);
        MongodbUtil.find(purInquiry);
        return purInquiry;
    }

    /**
     * 查询物料询价单主列表
     *
     * @param purInquiry 物料询价单主
     * @return 物料询价单主
     */
    @Override
    public List<PurInquiry> selectPurInquiryList(PurInquiry purInquiry) {
        return purInquiryMapper.selectPurInquiryList(purInquiry);
    }

    /**
     * 新增物料询价单主
     * 需要注意编码重复校验
     *
     * @param purInquiry 物料询价单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurInquiry(PurInquiry purInquiry) {
        if (CollectionUtil.isEmpty(purInquiry.getVendorList())){
            throw new BaseException("供应商列表不允许为空");
        }
        int row = purInquiryMapper.insert(purInquiry);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(purInquiry.getItemList())) {
                setItemNum(purInquiry.getItemList());
                int itemRow = purInquiryItemService.insertPurInquiryItemList(purInquiry.getItemList(), purInquiry);
            }
            if (CollectionUtil.isNotEmpty(purInquiry.getVendorList())){
                int vendorRow = purInquiryVendorService.insertPurInquiryVendorList(purInquiry.getVendorList(), purInquiry);
            }
            if (CollectionUtil.isNotEmpty(purInquiry.getAttachmentList())) {
                purInquiry.getAttachmentList().forEach(item->{
                    item.setInquirySid(purInquiry.getInquirySid());
                });
                int attachRow = purInquiryAttachMapper.inserts(purInquiry.getAttachmentList());
            }
            //插入待办通知
            addTodoTask(purInquiry);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(purInquiry.getInquirySid(), purInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改物料询价单主
     *
     * @param purInquiry 物料询价单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurInquiry(PurInquiry purInquiry) {
        if (CollectionUtil.isEmpty(purInquiry.getVendorList())){
            throw new BaseException("供应商列表不允许为空");
        }
        PurInquiry response = purInquiryMapper.selectPurInquiryById(purInquiry.getInquirySid());
        purInquiry.setUpdateDate(null).setUpdaterAccount(null);
        int row = purInquiryMapper.updateById(purInquiry);
        if (row > 0) {
            updateItemList(purInquiry);
            updateVendorList(purInquiry);
            addAttachmentList(purInquiry);
            //插入待办通知
            if (!ConstantsEms.SAVA_STATUS.equals(purInquiry.getHandleStatus())){
                addTodoTask(purInquiry);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, purInquiry);
            MongodbDeal.update(purInquiry.getInquirySid(), response.getHandleStatus(), purInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更物料询价单主
     *
     * @param purInquiry 物料询价单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurInquiry(PurInquiry purInquiry) {
        if (CollectionUtil.isEmpty(purInquiry.getVendorList())){
            throw new BaseException("供应商列表不允许为空");
        }
        PurInquiry response = purInquiryMapper.selectPurInquiryById(purInquiry.getInquirySid());
        purInquiry.setUpdateDate(null).setUpdaterAccount(null);
        int row = purInquiryMapper.updateAllById(purInquiry);
        if (row > 0) {
            updateItemList(purInquiry);
            updateVendorList(purInquiry);
            addAttachmentList(purInquiry);
            //插入待办通知
            if (!ConstantsEms.SAVA_STATUS.equals(purInquiry.getHandleStatus())){
                addTodoTask(purInquiry);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, purInquiry);
            MongodbDeal.update(purInquiry.getInquirySid(), response.getHandleStatus(), purInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除物料询价单主
     *
     * @param inquirySids 需要删除的物料询价单主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurInquiryByIds(List<Long> inquirySids) {
        int row = purInquiryMapper.deleteBatchIds(inquirySids);
        if (row > 0) {
            int itemRow = purInquiryItemService.deletePurInquiryItemByInquirySids(inquirySids);
            int vendorRow = purInquiryVendorService.deletePurInquiryVendorByInquirySids(inquirySids);
            int attachRow = purInquiryAttachMapper.delete(new QueryWrapper<PurInquiryAttach>()
                    .lambda().in(PurInquiryAttach::getInquirySid, inquirySids));
            int taskRow = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,inquirySids));
            inquirySids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param purInquiry
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PurInquiry purInquiry) {
        int row = 0;
        Long[] sids = purInquiry.getInquirySidList();
        if (sids.length <= 0){
            return row;
        }
        //作废走这里
        if (HandleStatus.INVALID.getCode().equals(purInquiry.getHandleStatus())) {
            List<PurInquiry> purInquiryListCheck = purInquiryMapper.selectList(new QueryWrapper<PurInquiry>()
                    .lambda().in(PurInquiry::getInquirySid, purInquiry.getInquirySidList())
                    .ne(PurInquiry::getHandleStatus, ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(purInquiryListCheck)) {
                throw new CustomException("请选择已确认且报价单中还未进行报价的询价单");
            }
            List<PurQuoteBargainItem> quoteBargainItemList = purQuoteBargainItemMapper.selectList(new QueryWrapper<PurQuoteBargainItem>()
                    .lambda()
                    .in(PurQuoteBargainItem::getInquirySid,purInquiry.getInquirySidList())
                    .ne(PurQuoteBargainItem::getHandleStatus,ConstantsEms.SAVA_STATUS));
            if (CollectionUtil.isNotEmpty(quoteBargainItemList)) {
                throw new CustomException("此询价单已进行报价，无法作废");
            }
            LambdaUpdateWrapper<PurInquiry> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PurInquiry::getInquirySid, purInquiry.getInquirySidList()).set(PurInquiry::getHandleStatus, HandleStatus.INVALID.getCode());
            row = purInquiryMapper.update(null, updateWrapper);
            //删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,purInquiry.getInquirySidList()));
            //已存在保存状态的报价单（明细表，主表）一起作废
            List<PurQuoteBargainItem> bargainItemList = purQuoteBargainItemMapper.selectList(new QueryWrapper<PurQuoteBargainItem>().lambda()
                    .in(PurQuoteBargainItem::getInquirySid,purInquiry.getInquirySidList()));
            if (CollectionUtil.isNotEmpty(bargainItemList)){
                LambdaUpdateWrapper<PurQuoteBargainItem> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.in(PurQuoteBargainItem::getInquirySid, purInquiry.getInquirySidList()).set(PurQuoteBargainItem::getHandleStatus, HandleStatus.INVALID.getCode());
                purQuoteBargainItemMapper.update(null, updateWrapper2);
                //插入报价单明细表的操作日志
                bargainItemList.forEach(item->{
                    MongodbUtil.insertUserLog(item.getQuoteBargainItemSid(), BusinessType.CANCEL.getValue(), null, BARGAIN_ITEM_TITLE, "由询价单作废");
                });
                List<Long> bargainSidList = bargainItemList.stream().map(PurQuoteBargainItem::getQuoteBargainSid).collect(Collectors.toList());
                LambdaUpdateWrapper<PurQuoteBargain> updateWrapper3 = new LambdaUpdateWrapper<>();
                updateWrapper3.in(PurQuoteBargain::getQuoteBargainSid, bargainSidList).set(PurQuoteBargain::getHandleStatus, HandleStatus.INVALID.getCode());
                purQuoteBargainMapper.update(null, updateWrapper3);
                //删除保存状态下的报价单的通知
                List<Long> sidList = bargainItemList.stream().map(PurQuoteBargainItem::getQuoteBargainSid).collect(Collectors.toList());
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,sidList));
                //插入报价单的操作日志
                bargainSidList.forEach(sid->{
                    MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, BARGAIN_TITLE, "由询价单作废");
                });
            }
        } else {
            //提交和确认，审批通过走这里
            List<Long> itemSids = purInquiryItemService.selectPurInquiryItemSidListById(sids);
            if (CollectionUtil.isEmpty(itemSids)){
                throw new CustomException("该操作明细不能为空");
            }
            if (HandleStatus.CONFIRMED.getCode().equals(purInquiry.getHandleStatus())){
                row = purInquiryMapper.update(null, new UpdateWrapper<PurInquiry>().lambda().set(PurInquiry::getHandleStatus, purInquiry.getHandleStatus())
                        .set(PurInquiry::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername()).set(PurInquiry::getConfirmDate, new Date())
                        .in(PurInquiry::getInquirySid, sids));
            } else {
                row = purInquiryMapper.update(null, new UpdateWrapper<PurInquiry>().lambda().set(PurInquiry::getHandleStatus, purInquiry.getHandleStatus())
                        .in(PurInquiry::getInquirySid, sids));
            }
        }
        //插入待办通知
        if (!ConstantsEms.SAVA_STATUS.equals(purInquiry.getHandleStatus())){
            addTodoTask(purInquiry);
        }
        for (Long id : sids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(id, purInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 推送
     *
     * @param purInquiry
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sent(List<Long> inquirySids) {
        int row = 0;
        PurInquiry purInquiry = new PurInquiry();
        Long[] sids = new Long[inquirySids.size()];
        inquirySids.toArray(sids);
        purInquiry.setInquirySidList(sids).setHandleStatus(HandleStatus.CONFIRMED.getCode());
        if (sids.length <= 0) {
            return row;
        }
        List<Long> itemSids = purInquiryItemService.selectPurInquiryItemSidListById(sids);
        if (CollectionUtil.isEmpty(itemSids)){
            throw new CustomException("该操作明细不能为空");
        }
        List<Long> vendorSids = purInquiryVendorService.selectPurInquiryVendorSidListById(sids);
        if (CollectionUtil.isEmpty(vendorSids)){
            throw new CustomException("该操作供应商明细不能为空");
        }
        row = purInquiryMapper.update(null, new UpdateWrapper<PurInquiry>().lambda()
                .set(PurInquiry::getDateRequest, new Date()).set(PurInquiry::getHandleStatus, purInquiry.getHandleStatus())
                .set(PurInquiry::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername()).set(PurInquiry::getConfirmDate, new Date())
                .in(PurInquiry::getInquirySid, sids));
        //删除保存待办通知
        addTodoTask(purInquiry);
        //新增通知供应商的待办任务
        List<SysTodoTask> sysTodoTaskList = new ArrayList<>();
        List<PurInquiryVendor> vendorList = purInquiryVendorMapper.selectPurInquiryVendorList(new PurInquiryVendor().setInquirySidList(sids));
        if (CollectionUtil.isNotEmpty(vendorList)){
            vendorList.forEach(vendor->{
                List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda()
                        .eq(SysUser::getClientId,ApiThreadLocalUtil.get().getClientId())
                        .eq(SysUser::getAccountType,ConstantsEms.USER_ACCOUNT_TYPE_GYS)
                        .eq(SysUser::getVendorSid,vendor.getVendorSid()));
                if (CollectionUtil.isNotEmpty(userList)){
                    userList.forEach(user->{
                        SysTodoTask sysTodoTask = new SysTodoTask();
                        sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                .setTableName("s_pur_inquiry")
                                .setNoticeDate(new Date());
                        sysTodoTask.setDocumentSid(vendor.getInquirySid());
                        sysTodoTask.setTitle("您有新的采购询价单" + vendor.getInquiryCode() + "待处理，请及时报价")
                                .setDocumentCode(String.valueOf(vendor.getInquiryCode()));
                        sysTodoTask.setTodoTaskSid(null);
                        sysTodoTask.setUserId(user.getUserId());
                        sysTodoTaskList.add(sysTodoTask);
                    });
                }
            });
        }
        if (CollectionUtil.isNotEmpty(sysTodoTaskList)){
            sysTodoTaskMapper.inserts(sysTodoTaskList);
        }
        for (Long id : sids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(id, purInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 更新附件清单
     *
     * @param purInquiry
     * @return
     */
    private int addAttachmentList(PurInquiry purInquiry) {
        int i = 0;
        purInquiry.getAttachmentList().forEach(item->{
            item.setClientId(ApiThreadLocalUtil.get().getClientId());
            item.setInquirySid(purInquiry.getInquirySid());
        });
        //先得到老附件
        List<PurInquiryAttach> oldAttach = purInquiryAttachMapper.selectList(new QueryWrapper<PurInquiryAttach>()
                .lambda().eq(PurInquiryAttach::getInquirySid, purInquiry.getInquirySid()));
        if (CollectionUtil.isEmpty(oldAttach)) {
            //如果原来附件就是空的
            //新增附件
            if (CollectionUtil.isNotEmpty(purInquiry.getAttachmentList())) {
                i = purInquiryAttachMapper.inserts(purInquiry.getAttachmentList());
                //新增附件的操作日志
                purInquiry.getAttachmentList().forEach(item -> {
                    MongodbDeal.insert(item.getInquiryAttachmentSid(), purInquiry.getHandleStatus(), null, ATTACH_TITLE, TITLE + ":" + purInquiry.getInquiryCode().toString());
                });
            }
            return i;
        } else {
            purInquiryAttachMapper.delete(new QueryWrapper<PurInquiryAttach>()
                    .lambda().eq(PurInquiryAttach::getInquirySid, purInquiry.getInquirySid()));
            if (CollectionUtil.isEmpty(purInquiry.getAttachmentList())) {
                return i;
            } else {
                //老附件-新附件 得到 被删除附件 用来记录日志
                List<Long> oldIds = oldAttach.stream().map(PurInquiryAttach::getInquiryAttachmentSid).collect(Collectors.toList());
                List<Long> newIds = purInquiry.getAttachmentList().stream().map(PurInquiryAttach::getInquiryAttachmentSid).collect(Collectors.toList());
                List<Long> delIds = oldIds.stream().filter(o -> !newIds.contains(o)).collect(Collectors.toList());
                //筛选是新增还是编辑
                List<PurInquiryAttach> updateItem = purInquiry.getAttachmentList().stream().filter(o -> o.getInquiryAttachmentSid() != null).collect(Collectors.toList());
                List<PurInquiryAttach> addItem = purInquiry.getAttachmentList().stream().filter(o -> o.getInquiryAttachmentSid() == null).collect(Collectors.toList());
                //更新附件
                if (CollectionUtil.isNotEmpty(updateItem)) {
                    updateItem.forEach(item -> {
                        item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    });
                    i = purInquiryAttachMapper.inserts(updateItem) + i;
                }
                //新增附件
                if (CollectionUtil.isNotEmpty(addItem)) {
                    i = purInquiryAttachMapper.inserts(addItem);
                    //新增附件的操作日志
                    addItem.forEach(item -> {
                        MongodbDeal.insert(item.getInquiryAttachmentSid(), purInquiry.getHandleStatus(), null, ATTACH_TITLE, TITLE + ":" + purInquiry.getInquiryCode().toString());
                    });
                }
                //删除附件的操作日志
                delIds.forEach(sid -> {
                    MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, ATTACH_TITLE, TITLE + ":" + purInquiry.getInquiryCode().toString());
                });
            }
        }
        return i;
    }

    /**
     * 更新明细信息
     *
     * @param purInquiry
     * @return
     */
    private int updateItemList(PurInquiry purInquiry) {
        int i = 0;
        //如果没有明细了就查出挂在主表下的所有明细sid全部删除
        if (CollectionUtil.isEmpty(purInquiry.getItemList())) {
            List<Long> sidList = new ArrayList<>();
            sidList.add(purInquiry.getInquirySid());
            i = purInquiryItemService.deletePurInquiryItemByInquirySids(sidList);
        } else {
            setItemNum(purInquiry.getItemList());
            //老明细 - 新明细 得到 被删除明细
            List<Long> oldIds = purInquiryItemService.selectPurInquiryItemSidListById(new Long[]{purInquiry.getInquirySid()});
            if (CollectionUtil.isEmpty(oldIds)) {
                //如果原来就没有明细
                i = purInquiryItemService.insertPurInquiryItemList(purInquiry.getItemList(), purInquiry);
            } else {
                List<Long> newIds = purInquiry.getItemList().stream().map(PurInquiryItem::getInquiryItemSid).collect(Collectors.toList());
                List<Long> delIds = oldIds.stream().filter(o -> !newIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    purInquiryItemService.deletePurInquiryItemByIds(delIds);
                }
                //筛选 新增明细 和 修改明细
                List<PurInquiryItem> updateItem = purInquiry.getItemList().stream().filter(o -> o.getInquiryItemSid() != null).collect(Collectors.toList());
                List<PurInquiryItem> addItem = purInquiry.getItemList().stream().filter(o -> o.getInquiryItemSid() == null).collect(Collectors.toList());
                updateItem.forEach(item->{
                    item.setHandleStatus(purInquiry.getHandleStatus());
                });
                i = purInquiryItemService.updatePurInquiryItemList(updateItem, purInquiry);
                i = purInquiryItemService.insertPurInquiryItemList(addItem, purInquiry) + i;
            }
        }
        return i;
    }

    /**
     * 更新供应商明细信息
     *
     * @param purInquiry
     * @return
     */
    private int updateVendorList(PurInquiry purInquiry) {
        int i = 0;
        //如果没有明细了就查出挂在主表下的所有明细sid全部删除
        if (CollectionUtil.isEmpty(purInquiry.getVendorList())) {
            List<Long> sidList = new ArrayList<>();
            sidList.add(purInquiry.getInquirySid());
            i = purInquiryVendorService.deletePurInquiryVendorByInquirySids(sidList);
        } else {
            //老明细 - 新明细 得到 被删除明细
            List<Long> oldIds = purInquiryVendorService.selectPurInquiryVendorSidListById(new Long[]{purInquiry.getInquirySid()});
            if (CollectionUtil.isEmpty(oldIds)) {
                //如果原来就没有明细
                i = purInquiryVendorService.insertPurInquiryVendorList(purInquiry.getVendorList(), purInquiry);
            } else {
                List<Long> newIds = purInquiry.getVendorList().stream().map(PurInquiryVendor::getInquiryVendorSid).collect(Collectors.toList());
                List<Long> delIds = oldIds.stream().filter(o -> !newIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    purInquiryVendorService.deletePurInquiryVendorByIds(delIds);
                }
                //筛选 新增明细 和 修改明细
                List<PurInquiryVendor> updateVendor = purInquiry.getVendorList().stream().filter(o -> o.getInquiryVendorSid() != null).collect(Collectors.toList());
                List<PurInquiryVendor> addVendor = purInquiry.getVendorList().stream().filter(o -> o.getInquiryVendorSid() == null).collect(Collectors.toList());
                i = purInquiryVendorService.updatePurInquiryVendorList(updateVendor, purInquiry);
                i = purInquiryVendorService.insertPurInquiryVendorList(addVendor, purInquiry) + i;
            }
        }
        return i;
    }


    /**
     * 更新待办通知信息
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    private int addTodoTask(PurInquiry purInquiry){
        int i = 0;
        if (ConstantsEms.CHECK_STATUS.equals(purInquiry.getHandleStatus())){
            if (purInquiry.getInquirySid() != null){
                //走单笔
                i = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid,purInquiry.getInquirySid()));
            } else {
                //走批量
                i = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,purInquiry.getInquirySidList()));
            }
            return i;
        }
        else if (ConstantsEms.SAVA_STATUS.equals(purInquiry.getHandleStatus())){
            PurInquiry request = purInquiryMapper.selectById(purInquiry.getInquirySid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_pur_inquiry")
                    .setDocumentSid(purInquiry.getInquirySid());
            sysTodoTask.setTitle("采购询价单: " + request.getInquiryCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(request.getInquiryCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            i = sysTodoTaskMapper.insert(sysTodoTask);
        }
        return i;
    }

    /**
     * 查询物料询价单主
     *
     * @param inquirySid 物料询价单主ID 和供应商sid
     * @return
     */
    @Override
    public Object checkQuote(PurInquiry purInquiry) {
        //查询该询价单是否已报价
        PurQuoteBargainItem quoteBargainItem = new PurQuoteBargainItem();
        quoteBargainItem.setInquirySid(purInquiry.getInquirySid());
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }else {
                purInquiry.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
            }
        }else {
            if (purInquiry.getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }
        }
        BasVendor vendor = basVendorMapper.selectById(purInquiry.getVendorSid());
        if (ConstantsEms.DISENABLE_STATUS.equals(vendor.getStatus())){
            throw new BaseException("供应商非“启用”状态，请核实！");
        }
        quoteBargainItem.setVendorSid(purInquiry.getVendorSid());
        List<PurQuoteBargainItem> quoteBargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(quoteBargainItem);
        if (CollectionUtil.isNotEmpty(quoteBargainItemList)){
            //返回报价单的sid
            String bargainSid = quoteBargainItemList.get(0).getQuoteBargainSid().toString();
            return bargainSid;
        }
        return null;
    }

    /**
     * 查询物料询价单主
     *
     * @param inquirySid 物料询价单主ID 和供应商sid
     * @return 物料询价单主
     */
    @Override
    public PurQuoteBargain toQuote(PurInquiry purInquiry) {
        //查询该询价单是否已报价
        PurQuoteBargainItem quoteBargainItem = new PurQuoteBargainItem();
        quoteBargainItem.setInquirySid(purInquiry.getInquirySid());
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }else {
                purInquiry.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
            }
        }else {
            if (purInquiry.getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }
        }
        BasVendor vendor = basVendorMapper.selectById(purInquiry.getVendorSid());
        if (ConstantsEms.DISENABLE_STATUS.equals(vendor.getStatus())){
            throw new BaseException("供应商非“启用”状态，请核实！");
        }
        quoteBargainItem.setVendorSid(purInquiry.getVendorSid());
        List<PurQuoteBargainItem> quoteBargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(quoteBargainItem);
        if (CollectionUtil.isNotEmpty(quoteBargainItemList)){
            PurQuoteBargain quotation = new PurQuoteBargain();
            quotation.setQuoteBargainSid(quoteBargainItemList.get(0).getQuoteBargainSid());
            return quotation;
        }
        //获取询价单主信息
        purInquiry = purInquiryMapper.selectPurInquiryById(purInquiry.getInquirySid());
        //拷贝到报核议价对象
        PurQuoteBargain quotation = new PurQuoteBargain();
        BeanUtil.copyProperties(purInquiry, quotation);
        quotation.setRemarkRequest(purInquiry.getRemark());
        quotation.setRemark(null);
        quotation.setVendorSid(quoteBargainItem.getVendorSid());
        quotation.setCreateDate(null).setCreatorAccount(null).setUpdaterAccount(null).setUpdateDate(null).setConfirmDate(null).setConfirmerAccount(null);
        //获取询价明细
        List<PurInquiryItem> itemList = purInquiryItemMapper.selectPurInquiryItemListById(purInquiry.getInquirySid());
        //拷贝询价单明细到报价单明细
        List<PurQuoteBargainItem> quoteItemList = BeanCopyUtils.copyListProperties(itemList, PurQuoteBargainItem::new);
        quoteItemList.forEach(item->{
            item.setRemarkRequest(item.getRemark());
            item.setRemark(null);
            item.setCreateDate(null).setCreatorAccount(null).setUpdaterAccount(null).setUpdateDate(null).setConfirmDate(null).setConfirmerAccount(null);
        });
        quotation.setPurRequestQuotationItemList(quoteItemList);
        return quotation;
    }

    /**
     * 给明细行赋行号
     *
     * @param inquirySid 物料询价单主ID
     * @return 物料询价单主
     */
    private void setItemNum(List<PurInquiryItem> itemList){
        List<PurInquiryItem> newItem = itemList.stream().filter(li -> li.getItemNum() == null).collect(Collectors.toList());
        List<PurInquiryItem> oldItem = itemList.stream().filter(li -> li.getItemNum() != null).collect(Collectors.toList());
        //以下可以直接得出结论：oldItem 是空的
        if (newItem.size() == itemList.size()){
            Long max = new Long(1);
            for (PurInquiryItem item : itemList) {
                item.setItemNum(max);
                max = max + new Long(1);
            }
        }
        else if (CollectionUtil.isNotEmpty(newItem) && CollectionUtil.isNotEmpty(oldItem)){
            Long max = oldItem.stream().mapToLong(li -> li.getItemNum()).max().getAsLong();
            max = max + new Long(1);
            for (PurInquiryItem item : newItem) {
                item.setItemNum(max);
                max = max + new Long(1);
            }
            itemList.clear();
            itemList.addAll(newItem);
            itemList.addAll(oldItem);
        }else {
            return;
        }
    }

    /**
     * 加工物料询价单
     *
     * @param inquirySid 物料询价单主ID
     * @return 物料询价单
     */
    @Override
    public PurInquiry copy(Long purInquirySid) {
        PurInquiry response = new PurInquiry();
        response = this.selectPurInquiryById(purInquirySid);
        String creatorAccountName = ApiThreadLocalUtil.get().getSysUser().getNickName();
        //主表信息清洗
        response.setClientId(null).setInquirySid(null).setInquiryCode(null)
                .setHandleStatus(ConstantsEms.SAVA_STATUS);
        response.setCreatorAccount(null).setCreateDate(null)
                .setCreatorAccountName(creatorAccountName)
                .setUpdaterAccount(null).setUpdateDate(null)
                .setConfirmerAccount(null).setConfirmDate(null);
        response.setDateRequest(new Date());
        response.setOperLogList(null);
        //明细表清洗
        if (CollectionUtil.isNotEmpty(response.getItemList())){
            response.getItemList().forEach(item->{
                item.setClientId(null).setInquirySid(null).setInquiryItemSid(null);
                item.setCreatorAccount(null).setCreateDate(null)
                        .setCreatorAccountName(creatorAccountName)
                        .setUpdaterAccount(null).setUpdateDate(null);
                item.setOperLogList(null);
            });
        }
        //供应商明细清洗
        if (CollectionUtil.isNotEmpty(response.getVendorList())){
            response.getVendorList().forEach(vendor->{
                vendor.setClientId(null).setInquirySid(null).setInquiryVendorSid(null);
                vendor.setCreatorAccount(null).setCreateDate(null)
                        .setCreatorAccountName(creatorAccountName);
                vendor.setQuoteStatus(null);
                vendor.setOperLogList(null);
            });
        }
        //附件明细清洗
        if (CollectionUtil.isNotEmpty(response.getAttachmentList())){
            response.getAttachmentList().forEach(attach->{
                attach.setClientId(null).setInquirySid(null).setInquiryAttachmentSid(null);
                attach.setCreatorAccount(null).setCreateDate(null)
                        .setCreatorAccountName(creatorAccountName)
                        .setUpdaterAccount(null).setUpdateDate(null);
                attach.setOperLogList(null);
            });
        }
        return response;
    }

}
