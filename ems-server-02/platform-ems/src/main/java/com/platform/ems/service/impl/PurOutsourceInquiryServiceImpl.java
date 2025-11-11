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
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPurOutsourceInquiryItemService;
import com.platform.ems.service.IPurOutsourceInquiryVendorService;
import com.platform.ems.util.MongodbDeal;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.IPurOutsourceInquiryService;

/**
 * 加工询价单主Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Service
@SuppressWarnings("all")
public class PurOutsourceInquiryServiceImpl extends ServiceImpl<PurOutsourceInquiryMapper, PurOutsourceInquiry> implements IPurOutsourceInquiryService {
    @Autowired
    private PurOutsourceInquiryMapper purOutsourceInquiryMapper;
    @Autowired
    private PurOutsourceInquiryItemMapper purOutsourceInquiryItemMapper;
    @Autowired
    private IPurOutsourceInquiryItemService purOutsourceInquiryItemService;
    @Autowired
    private PurOutsourceInquiryAttachMapper purOutsourceInquiryAttachMapper;
    @Autowired
    private PurOutsourceInquiryVendorMapper purOutsourceInquiryVendorMapper;
    @Autowired
    private IPurOutsourceInquiryVendorService purOutsourceInquiryVendorService;
    @Autowired
    private PurOutsourceQuoteBargainItemMapper purOutsourceQuoteBargainItemMapper;
    @Autowired
    private PurOutsourceQuoteBargainMapper purOutsourceQuoteBargainMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "加工询价单";

    private static final String ATTACH_TITLE = "加工询价单-附件";

    private static final String BARGAIN_ITEM_TITLE = "加工报核议价单明细";

    private static final String BARGAIN_TITLE = "加工报核议价单";


    /**
     * 查询加工询价单主
     *
     * @param outsourceInquirySid 加工询价单主ID
     * @return 加工询价单主
     */
    @Override
    public PurOutsourceInquiry selectPurOutsourceInquiryById(Long outsourceInquirySid) {
        PurOutsourceInquiry purOutsourceInquiry = purOutsourceInquiryMapper.selectPurOutsourceInquiryById(outsourceInquirySid);
        if (purOutsourceInquiry == null){
            throw new CustomException("单据数据丢失，请联系管理员");
        }
        List<PurOutsourceInquiryItem> itemList = purOutsourceInquiryItemService.selectPurOutsourceInquiryItemListById(outsourceInquirySid);
        List<PurOutsourceInquiryVendor> vendorList = purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorListById(outsourceInquirySid);
        List<PurOutsourceInquiryAttach> attachmentList = purOutsourceInquiryAttachMapper.selectPurOutsourceInquiryAttachList(new PurOutsourceInquiryAttach().setOutsourceInquirySid(outsourceInquirySid));
        purOutsourceInquiry.setItemList(itemList).setVendorList(vendorList).setAttachmentList(attachmentList);
        MongodbUtil.find(purOutsourceInquiry);
        return purOutsourceInquiry;
    }

    /**
     * 查询加工询价单主列表
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 加工询价单主
     */
    @Override
    public List<PurOutsourceInquiry> selectPurOutsourceInquiryList(PurOutsourceInquiry purOutsourceInquiry) {
        return purOutsourceInquiryMapper.selectPurOutsourceInquiryList(purOutsourceInquiry);
    }

    /**
     * 新增加工询价单主
     * 需要注意编码重复校验
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceInquiry(PurOutsourceInquiry purOutsourceInquiry) {
        if (CollectionUtil.isEmpty(purOutsourceInquiry.getVendorList())){
            throw new BaseException("供应商列表不允许为空");
        }
        int row = purOutsourceInquiryMapper.insert(purOutsourceInquiry);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(purOutsourceInquiry.getItemList())) {
                setItemNum(purOutsourceInquiry.getItemList());
                int itemRow = purOutsourceInquiryItemService.insertPurOutsourceInquiryItemList(purOutsourceInquiry.getItemList(), purOutsourceInquiry);
            }
            if (CollectionUtil.isNotEmpty(purOutsourceInquiry.getVendorList())){
                int vendorRow = purOutsourceInquiryVendorService.insertPurOutsourceInquiryVendorList(purOutsourceInquiry.getVendorList(), purOutsourceInquiry);
            }
            if (CollectionUtil.isNotEmpty(purOutsourceInquiry.getAttachmentList())) {
                purOutsourceInquiry.getAttachmentList().forEach(item->{
                    item.setOutsourceInquirySid(purOutsourceInquiry.getOutsourceInquirySid());
                });
                int attachRow = purOutsourceInquiryAttachMapper.inserts(purOutsourceInquiry.getAttachmentList());
            }
            //插入待办通知
            addTodoTask(purOutsourceInquiry);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(purOutsourceInquiry.getOutsourceInquirySid(), purOutsourceInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改加工询价单主
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceInquiry(PurOutsourceInquiry purOutsourceInquiry) {
        if (CollectionUtil.isEmpty(purOutsourceInquiry.getVendorList())){
            throw new BaseException("供应商列表不允许为空");
        }
        PurOutsourceInquiry response = purOutsourceInquiryMapper.selectPurOutsourceInquiryById(purOutsourceInquiry.getOutsourceInquirySid());
        purOutsourceInquiry.setUpdateDate(null).setUpdaterAccount(null);
        int row = purOutsourceInquiryMapper.updateById(purOutsourceInquiry);
        if (row > 0) {
            updateItemList(purOutsourceInquiry);
            updateVendorList(purOutsourceInquiry);
            addAttachmentList(purOutsourceInquiry);
            //插入待办通知
            if (!ConstantsEms.SAVA_STATUS.equals(purOutsourceInquiry.getHandleStatus())){
                addTodoTask(purOutsourceInquiry);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, purOutsourceInquiry);
            MongodbDeal.update(purOutsourceInquiry.getOutsourceInquirySid(), response.getHandleStatus(), purOutsourceInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更加工询价单主
     *
     * @param purOutsourceInquiry 加工询价单主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourceInquiry(PurOutsourceInquiry purOutsourceInquiry) {
        if (CollectionUtil.isEmpty(purOutsourceInquiry.getVendorList())){
            throw new BaseException("供应商列表不允许为空");
        }
        PurOutsourceInquiry response = purOutsourceInquiryMapper.selectPurOutsourceInquiryById(purOutsourceInquiry.getOutsourceInquirySid());
        purOutsourceInquiry.setUpdateDate(null).setUpdaterAccount(null);
        int row = purOutsourceInquiryMapper.updateAllById(purOutsourceInquiry);
        if (row > 0) {
            updateItemList(purOutsourceInquiry);
            updateVendorList(purOutsourceInquiry);
            addAttachmentList(purOutsourceInquiry);
            //插入待办通知
            if (!ConstantsEms.SAVA_STATUS.equals(purOutsourceInquiry.getHandleStatus())){
                addTodoTask(purOutsourceInquiry);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, purOutsourceInquiry);
            MongodbDeal.update(purOutsourceInquiry.getOutsourceInquirySid(), response.getHandleStatus(), purOutsourceInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除加工询价单主
     *
     * @param outsourceInquirySids 需要删除的加工询价单主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceInquiryByIds(List<Long> outsourceInquirySids) {
        int row = purOutsourceInquiryMapper.deleteBatchIds(outsourceInquirySids);
        if (row > 0) {
            int itemRow = purOutsourceInquiryItemService.deletePurOutsourceInquiryItemByInquirySids(outsourceInquirySids);
            int vendorRow = purOutsourceInquiryVendorService.deletePurOutsourceInquiryVendorByInquirySids(outsourceInquirySids);
            int attachRow = purOutsourceInquiryAttachMapper.delete(new QueryWrapper<PurOutsourceInquiryAttach>()
                    .lambda().in(PurOutsourceInquiryAttach::getOutsourceInquirySid, outsourceInquirySids));
            int taskRow = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,outsourceInquirySids));
            outsourceInquirySids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param purOutsourceInquiry
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PurOutsourceInquiry purOutsourceInquiry) {
        int row = 0;
        Long[] sids = purOutsourceInquiry.getOutsourceInquirySidList();
        if (sids.length <= 0){
            return row;
        }
        //作废走这里
        if (HandleStatus.INVALID.getCode().equals(purOutsourceInquiry.getHandleStatus())) {
            List<PurOutsourceInquiry> purOutsourceInquiryListCheck = purOutsourceInquiryMapper.selectList(new QueryWrapper<PurOutsourceInquiry>()
                    .lambda().in(PurOutsourceInquiry::getOutsourceInquirySid, purOutsourceInquiry.getOutsourceInquirySidList())
                    .ne(PurOutsourceInquiry::getHandleStatus, ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(purOutsourceInquiryListCheck)) {
                throw new CustomException("请选择已确认且报价单中还未进行报价的加工询价单");
            }
            List<PurOutsourceQuoteBargainItem> outsourceQuoteBargainItemList = purOutsourceQuoteBargainItemMapper.selectList(new QueryWrapper<PurOutsourceQuoteBargainItem>()
                    .lambda()
                    .in(PurOutsourceQuoteBargainItem::getOutsourceInquirySid,purOutsourceInquiry.getOutsourceInquirySidList())
                    .ne(PurOutsourceQuoteBargainItem::getHandleStatus,ConstantsEms.SAVA_STATUS));
            if (CollectionUtil.isNotEmpty(outsourceQuoteBargainItemList)) {
                throw new CustomException("此询价单已进行报价，无法作废");
            }
            LambdaUpdateWrapper<PurOutsourceInquiry> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PurOutsourceInquiry::getOutsourceInquirySid, purOutsourceInquiry.getOutsourceInquirySidList())
                    .set(PurOutsourceInquiry::getHandleStatus, HandleStatus.INVALID.getCode());
            row = purOutsourceInquiryMapper.update(null, updateWrapper);
            //删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,purOutsourceInquiry.getOutsourceInquirySidList()));
            //已存在保存状态的报价单（明细表，主表）一起作废
            List<PurOutsourceQuoteBargainItem> bargainItemList = purOutsourceQuoteBargainItemMapper.selectList(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda()
                    .in(PurOutsourceQuoteBargainItem::getOutsourceInquirySid, purOutsourceInquiry.getOutsourceInquirySidList()));
            if (CollectionUtil.isNotEmpty(bargainItemList)){
                LambdaUpdateWrapper<PurOutsourceQuoteBargainItem> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.in(PurOutsourceQuoteBargainItem::getOutsourceInquirySid, purOutsourceInquiry.getOutsourceInquirySidList())
                        .set(PurOutsourceQuoteBargainItem::getHandleStatus, HandleStatus.INVALID.getCode());
                purOutsourceQuoteBargainItemMapper.update(null, updateWrapper2);
                //插入报价单明细表的操作日志
                bargainItemList.forEach(item->{
                    MongodbUtil.insertUserLog(item.getOutsourceQuoteBargainItemSid(), BusinessType.CANCEL.getValue(), null, BARGAIN_ITEM_TITLE, "由询价单作废");
                });
                List<Long> bargainSidList = bargainItemList.stream().map(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainSid).collect(Collectors.toList());
                LambdaUpdateWrapper<PurOutsourceQuoteBargain> updateWrapper3 = new LambdaUpdateWrapper<>();
                updateWrapper3.in(PurOutsourceQuoteBargain::getOutsourceQuoteBargainSid, bargainSidList).set(PurOutsourceQuoteBargain::getHandleStatus, HandleStatus.INVALID.getCode());
                purOutsourceQuoteBargainMapper.update(null, updateWrapper3);
                //删除保存状态下的报价单的通知
                List<Long> sidList = bargainItemList.stream().map(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainSid).collect(Collectors.toList());
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,sidList));
                //插入报价单的操作日志
                bargainSidList.forEach(sid->{
                    MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, BARGAIN_TITLE, "由询价单作废");
                });
            }
        } else {
            //提交和确认，审批通过走这里
            List<Long> itemSids = purOutsourceInquiryItemService.selectPurOutsourceInquiryItemSidListById(sids);
            if (CollectionUtil.isEmpty(itemSids)){
                throw new CustomException("该操作明细不能为空");
            }
            if (HandleStatus.CONFIRMED.getCode().equals(purOutsourceInquiry.getHandleStatus())){
                row = purOutsourceInquiryMapper.update(null, new UpdateWrapper<PurOutsourceInquiry>().lambda()
                        .set(PurOutsourceInquiry::getHandleStatus, purOutsourceInquiry.getHandleStatus())
                        .set(PurOutsourceInquiry::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername()).set(PurOutsourceInquiry::getConfirmDate, new Date())
                        .in(PurOutsourceInquiry::getOutsourceInquirySid, sids));
            } else {
                row = purOutsourceInquiryMapper.update(null, new UpdateWrapper<PurOutsourceInquiry>().lambda()
                        .set(PurOutsourceInquiry::getHandleStatus, purOutsourceInquiry.getHandleStatus())
                        .in(PurOutsourceInquiry::getOutsourceInquirySid, sids));
            }
        }
        //插入待办通知
        if (!ConstantsEms.SAVA_STATUS.equals(purOutsourceInquiry.getHandleStatus())){
            addTodoTask(purOutsourceInquiry);
        }
        for (Long id : sids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(id, purOutsourceInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 推送
     *
     * @param purOutsourceInquiry
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sent(List<Long> outsourceInquirySids) {
        int row = 0;
        PurOutsourceInquiry purOutsourceInquiry = new PurOutsourceInquiry();
        Long[] sids = new Long[outsourceInquirySids.size()];
        outsourceInquirySids.toArray(sids);
        purOutsourceInquiry.setOutsourceInquirySidList(sids).setHandleStatus(HandleStatus.CONFIRMED.getCode());
        if (sids.length <= 0) {
            return row;
        }
        List<Long> itemSids = purOutsourceInquiryItemService.selectPurOutsourceInquiryItemSidListById(sids);
        if (CollectionUtil.isEmpty(itemSids)){
            throw new CustomException("该操作明细不能为空");
        }
        List<Long> vendorSids = purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorSidListById(sids);
        if (CollectionUtil.isEmpty(itemSids)){
            throw new CustomException("该操作供应商明细不能为空");
        }
        row = purOutsourceInquiryMapper.update(null, new UpdateWrapper<PurOutsourceInquiry>().lambda()
                .set(PurOutsourceInquiry::getDateRequest, new Date()).set(PurOutsourceInquiry::getHandleStatus, purOutsourceInquiry.getHandleStatus())
                .set(PurOutsourceInquiry::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername()).set(PurOutsourceInquiry::getConfirmDate, new Date())
                .in(PurOutsourceInquiry::getOutsourceInquirySid, sids));
        //删除保存待办通知
        addTodoTask(purOutsourceInquiry);
        //新增通知供应商的待办任务
        List<SysTodoTask> sysTodoTaskList = new ArrayList<>();
        List<PurOutsourceInquiryVendor> vendorList = purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorList(new PurOutsourceInquiryVendor()
                .setOutsourceInquirySidList(sids));
        if (CollectionUtil.isNotEmpty(vendorList)){
            vendorList.forEach(vendor->{
                List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda()
                        .eq(SysUser::getAccountType,ConstantsEms.USER_ACCOUNT_TYPE_GYS)
                        .in(SysUser::getVendorSid,vendor.getVendorSid()));
                if (CollectionUtil.isNotEmpty(userList)) {
                    userList.forEach(user->{
                        SysTodoTask sysTodoTask = new SysTodoTask();
                        sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                .setTableName("s_pur_outsource_inquiry")
                                .setNoticeDate(new Date());
                        sysTodoTask.setDocumentSid(vendor.getOutsourceInquirySid());
                        sysTodoTask.setTitle("您有新的加工询价单" + vendor.getOutsourceInquiryCode() + "待处理，请及时报价")
                                .setDocumentCode(String.valueOf(vendor.getOutsourceInquiryCode()));
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
            MongodbDeal.check(id, purOutsourceInquiry.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 更新附件清单
     *
     * @param purOutsourceInquiry
     * @return
     */
    private int addAttachmentList(PurOutsourceInquiry purOutsourceInquiry) {
        int i = 0;
        purOutsourceInquiry.getAttachmentList().forEach(item->{
            item.setClientId(ApiThreadLocalUtil.get().getClientId());
            item.setOutsourceInquirySid(purOutsourceInquiry.getOutsourceInquirySid());
        });
        //先得到老附件
        List<PurOutsourceInquiryAttach> oldAttach = purOutsourceInquiryAttachMapper.selectList(new QueryWrapper<PurOutsourceInquiryAttach>()
                .lambda().eq(PurOutsourceInquiryAttach::getOutsourceInquirySid, purOutsourceInquiry.getOutsourceInquirySid()));
        if (CollectionUtil.isEmpty(oldAttach)) {
            //如果原来附件就是空的
            //新增附件
            if (CollectionUtil.isNotEmpty(purOutsourceInquiry.getAttachmentList())) {
                i = purOutsourceInquiryAttachMapper.inserts(purOutsourceInquiry.getAttachmentList());
                //新增附件的操作日志
                purOutsourceInquiry.getAttachmentList().forEach(item -> {
                    MongodbDeal.insert(item.getOutsourceInquiryAttachmentSid(), purOutsourceInquiry.getHandleStatus(),
                            null, ATTACH_TITLE, TITLE + ":" + purOutsourceInquiry.getOutsourceInquiryCode().toString());
                });
            }
            return i;
        } else {
            purOutsourceInquiryAttachMapper.delete(new QueryWrapper<PurOutsourceInquiryAttach>()
                    .lambda().eq(PurOutsourceInquiryAttach::getOutsourceInquirySid, purOutsourceInquiry.getOutsourceInquirySid()));
            if (CollectionUtil.isEmpty(purOutsourceInquiry.getAttachmentList())) {
                return i;
            } else {
                //老附件-新附件 得到 被删除附件 用来记录日志
                List<Long> oldIds = oldAttach.stream().map(PurOutsourceInquiryAttach::getOutsourceInquiryAttachmentSid).collect(Collectors.toList());
                List<Long> newIds = purOutsourceInquiry.getAttachmentList().stream().map(PurOutsourceInquiryAttach::getOutsourceInquiryAttachmentSid).collect(Collectors.toList());
                List<Long> delIds = oldIds.stream().filter(o -> !newIds.contains(o)).collect(Collectors.toList());
                //筛选是新增还是编辑
                List<PurOutsourceInquiryAttach> updateItem = purOutsourceInquiry.getAttachmentList().stream()
                        .filter(o -> o.getOutsourceInquiryAttachmentSid() != null).collect(Collectors.toList());
                List<PurOutsourceInquiryAttach> addItem = purOutsourceInquiry.getAttachmentList().stream()
                        .filter(o -> o.getOutsourceInquiryAttachmentSid() == null).collect(Collectors.toList());
                //更新附件
                if (CollectionUtil.isNotEmpty(updateItem)) {
                    updateItem.forEach(item -> {
                        item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    });
                    i = purOutsourceInquiryAttachMapper.inserts(updateItem) + i;
                }
                //新增附件
                if (CollectionUtil.isNotEmpty(addItem)) {
                    i = purOutsourceInquiryAttachMapper.inserts(addItem);
                    //新增附件的操作日志
                    addItem.forEach(item -> {
                        MongodbDeal.insert(item.getOutsourceInquiryAttachmentSid(), purOutsourceInquiry.getHandleStatus(),
                                null, ATTACH_TITLE, TITLE + ":" + purOutsourceInquiry.getOutsourceInquiryCode().toString());
                    });
                }
                //删除附件的操作日志
                delIds.forEach(sid -> {
                    MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, ATTACH_TITLE, TITLE + ":" + purOutsourceInquiry.getOutsourceInquiryCode().toString());
                });
            }
        }
        return i;
    }

    /**
     * 更新明细信息
     *
     * @param purOutsourceInquiry
     * @return
     */
    private int updateItemList(PurOutsourceInquiry purOutsourceInquiry) {
        int i = 0;
        //如果没有明细了就查出挂在主表下的所有明细sid全部删除
        if (CollectionUtil.isEmpty(purOutsourceInquiry.getItemList())) {
            List<Long> sidList = new ArrayList<>();
            sidList.add(purOutsourceInquiry.getOutsourceInquirySid());
            i = purOutsourceInquiryItemService.deletePurOutsourceInquiryItemByInquirySids(sidList);
        } else {
            setItemNum(purOutsourceInquiry.getItemList());
            //老明细 - 新明细 得到 被删除明细
            List<Long> oldIds = purOutsourceInquiryItemService.selectPurOutsourceInquiryItemSidListById(new Long[]{purOutsourceInquiry.getOutsourceInquirySid()});
            if (CollectionUtil.isEmpty(oldIds)) {
                //如果原来就没有明细
                i = purOutsourceInquiryItemService.insertPurOutsourceInquiryItemList(purOutsourceInquiry.getItemList(), purOutsourceInquiry);
            } else {
                List<Long> newIds = purOutsourceInquiry.getItemList().stream().map(PurOutsourceInquiryItem::getOutsourceInquiryItemSid).collect(Collectors.toList());
                List<Long> delIds = oldIds.stream().filter(o -> !newIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    purOutsourceInquiryItemService.deletePurOutsourceInquiryItemByIds(delIds);
                }
                //筛选 新增明细 和 修改明细
                List<PurOutsourceInquiryItem> updateItem = purOutsourceInquiry.getItemList().stream().filter(o -> o.getOutsourceInquiryItemSid() != null).collect(Collectors.toList());
                List<PurOutsourceInquiryItem> addItem = purOutsourceInquiry.getItemList().stream().filter(o -> o.getOutsourceInquiryItemSid() == null).collect(Collectors.toList());
                updateItem.forEach(item->{
                    item.setHandleStatus(purOutsourceInquiry.getHandleStatus());
                });
                i = purOutsourceInquiryItemService.updatePurOutsourceInquiryItemList(updateItem, purOutsourceInquiry);
                i = purOutsourceInquiryItemService.insertPurOutsourceInquiryItemList(addItem, purOutsourceInquiry) + i;
            }
        }
        return i;
    }

    /**
     * 更新供应商明细信息
     *
     * @param purOutsourceInquiry
     * @return
     */
    private int updateVendorList(PurOutsourceInquiry purOutsourceInquiry) {
        int i = 0;
        //如果没有明细了就查出挂在主表下的所有明细sid全部删除
        if (CollectionUtil.isEmpty(purOutsourceInquiry.getVendorList())) {
            List<Long> sidList = new ArrayList<>();
            sidList.add(purOutsourceInquiry.getOutsourceInquirySid());
            i = purOutsourceInquiryVendorService.deletePurOutsourceInquiryVendorByInquirySids(sidList);
        } else {
            //老明细 - 新明细 得到 被删除明细
            List<Long> oldIds = purOutsourceInquiryVendorService.selectPurOutsourceInquiryVendorSidListById(new Long[]{purOutsourceInquiry.getOutsourceInquirySid()});
            if (CollectionUtil.isEmpty(oldIds)) {
                //如果原来就没有明细
                i = purOutsourceInquiryVendorService.insertPurOutsourceInquiryVendorList(purOutsourceInquiry.getVendorList(), purOutsourceInquiry);
            } else {
                List<Long> newIds = purOutsourceInquiry.getVendorList().stream().map(PurOutsourceInquiryVendor::getOutsourceInquiryVendorSid).collect(Collectors.toList());
                List<Long> delIds = oldIds.stream().filter(o -> !newIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    purOutsourceInquiryVendorService.deletePurOutsourceInquiryVendorByIds(delIds);
                }
                //筛选 新增明细 和 修改明细
                List<PurOutsourceInquiryVendor> updateItem = purOutsourceInquiry.getVendorList().stream().filter(o -> o.getOutsourceInquiryVendorSid() != null).collect(Collectors.toList());
                List<PurOutsourceInquiryVendor> addItem = purOutsourceInquiry.getVendorList().stream().filter(o -> o.getOutsourceInquiryVendorSid() == null).collect(Collectors.toList());
                i = purOutsourceInquiryVendorService.updatePurOutsourceInquiryVendorList(updateItem, purOutsourceInquiry);
                i = purOutsourceInquiryVendorService.insertPurOutsourceInquiryVendorList(addItem, purOutsourceInquiry) + i;
            }
        }
        return i;
    }

    /**
     * 更新待办通知信息
     *
     * @param purOutsourceInquiry
     * @return
     */
    private int addTodoTask(PurOutsourceInquiry purOutsourceInquiry){
        int i = 0;
        if (ConstantsEms.CHECK_STATUS.equals(purOutsourceInquiry.getHandleStatus())){
            if (purOutsourceInquiry.getOutsourceInquirySid() != null){
                //走单笔
                i = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid,purOutsourceInquiry.getOutsourceInquirySid()));
            } else {
                //走批量
                i = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,purOutsourceInquiry.getOutsourceInquirySidList()));
            }
            return i;
        }
        else if (ConstantsEms.SAVA_STATUS.equals(purOutsourceInquiry.getHandleStatus())){
            PurOutsourceInquiry request = purOutsourceInquiryMapper.selectById(purOutsourceInquiry.getOutsourceInquirySid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_pur_outsource_inquiry")
                    .setDocumentSid(purOutsourceInquiry.getOutsourceInquirySid());
            sysTodoTask.setTitle("加工询价单: " + request.getOutsourceInquiryCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(request.getOutsourceInquiryCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            i = sysTodoTaskMapper.insert(sysTodoTask);
        }
        return i;
    }


    /**
     * 查询加工物料询价单主
     *
     * @param outsourcePurInquiry 加工物料询价单主ID 和供应商sid
     * @return
     */
    @Override
    public Object checkQuote(PurOutsourceInquiry purOutsourceInquiry) {
        //查询该询价单是否已报价
        PurOutsourceQuoteBargainItem quoteOutsourceBargainItem = new PurOutsourceQuoteBargainItem();
        quoteOutsourceBargainItem.setOutsourceInquirySid(purOutsourceInquiry.getOutsourceInquirySid());
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }else {
                purOutsourceInquiry.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
            }
        }else {
            if (purOutsourceInquiry.getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }
        }
        BasVendor vendor = basVendorMapper.selectById(purOutsourceInquiry.getVendorSid());
        if (ConstantsEms.DISENABLE_STATUS.equals(vendor.getStatus())){
            throw new BaseException("供应商非“启用”状态，请核实！");
        }
        quoteOutsourceBargainItem.setVendorSid(purOutsourceInquiry.getVendorSid());
        List<PurOutsourceQuoteBargainItem> quoteOutsourceBargainItemList = purOutsourceQuoteBargainItemMapper.selectPurOutsourceRequestQuotationItemList(quoteOutsourceBargainItem);
        if (CollectionUtil.isNotEmpty(quoteOutsourceBargainItemList)){
            //返回报价单的sid
            String bargainSid = quoteOutsourceBargainItemList.get(0).getOutsourceQuoteBargainSid().toString();
            return bargainSid;
        }
        return null;
    }

    /**
     * 查询加工物料询价单主 - 走报价
     *
     * @param outsourceInquirySid 加工物料询价单主ID 和 供应商 sid
     * @return 加工物料报价单主
     */
    @Override
    public PurOutsourceQuoteBargain toQuote(PurOutsourceInquiry purOutsourceInquiry) {
        //查询该询价单是否已报价
        PurOutsourceQuoteBargainItem quoteBargainItem = new PurOutsourceQuoteBargainItem();
        quoteBargainItem.setOutsourceInquirySid(purOutsourceInquiry.getOutsourceInquirySid());
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }else {
                purOutsourceInquiry.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
            }
        }else {
            if (purOutsourceInquiry.getVendorSid() == null){
                throw new BaseException("请选择供应商");
            }
        }
        BasVendor vendor = basVendorMapper.selectById(purOutsourceInquiry.getVendorSid());
        if (ConstantsEms.DISENABLE_STATUS.equals(vendor.getStatus())){
            throw new BaseException("供应商非“启用”状态，请核实！");
        }
        quoteBargainItem.setVendorSid(purOutsourceInquiry.getVendorSid());
        List<PurOutsourceQuoteBargainItem> quoteBargainItemList = purOutsourceQuoteBargainItemMapper.selectPurOutsourceRequestQuotationItemList(quoteBargainItem);
        if (CollectionUtil.isNotEmpty(quoteBargainItemList)){
            PurOutsourceQuoteBargain quotation = new PurOutsourceQuoteBargain();
            quotation.setOutsourceQuoteBargainSid(quoteBargainItemList.get(0).getOutsourceQuoteBargainSid());
            return quotation;
        }
        //获取加工询价单主信息
        purOutsourceInquiry = purOutsourceInquiryMapper.selectPurOutsourceInquiryById(purOutsourceInquiry.getOutsourceInquirySid());
        //拷贝到加工报核议价对象
        PurOutsourceQuoteBargain quotation = new PurOutsourceQuoteBargain();
        BeanUtil.copyProperties(purOutsourceInquiry, quotation);
        quotation.setRemarkRequest(purOutsourceInquiry.getRemark());
        quotation.setRemark(null);
        quotation.setVendorSid(quoteBargainItem.getVendorSid());
        quotation.setCreateDate(null).setCreatorAccount(null).setUpdaterAccount(null).setUpdateDate(null).setConfirmDate(null).setConfirmerAccount(null);
        //获取加工询价明细
        List<PurOutsourceInquiryItem> itemList = purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemListById(purOutsourceInquiry.getOutsourceInquirySid());
        //拷贝加工询价单明细到报价单明细
        List<PurOutsourceQuoteBargainItem> quoteItemList = BeanCopyUtils.copyListProperties(itemList, PurOutsourceQuoteBargainItem::new);
        quoteItemList.forEach(item->{
            item.setRemarkRequest(item.getRemark());
            item.setRemark(null);
            item.setCreateDate(null).setCreatorAccount(null).setUpdaterAccount(null).setUpdateDate(null).setConfirmDate(null).setConfirmerAccount(null);
        });
        quotation.setItemList(quoteItemList);
        return quotation;
    }

    /**
     * 给明细行赋行号
     *
     * @param inquirySid 物料询价单主ID
     * @return 物料询价单主
     */
    private void setItemNum(List<PurOutsourceInquiryItem> itemList){
        List<PurOutsourceInquiryItem> newItem = itemList.stream().filter(li -> li.getItemNum() == null).collect(Collectors.toList());
        List<PurOutsourceInquiryItem> oldItem = itemList.stream().filter(li -> li.getItemNum() != null).collect(Collectors.toList());
        //以下可以直接得出结论：oldItem 是空的
        if (newItem.size() == itemList.size()){
            Long max = new Long(1);
            for (PurOutsourceInquiryItem item : itemList) {
                item.setItemNum(max);
                max = max + new Long(1);
            }
        }
        else if (CollectionUtil.isNotEmpty(newItem) && CollectionUtil.isNotEmpty(oldItem)){
            Long max = oldItem.stream().mapToLong(li -> li.getItemNum()).max().getAsLong();
            max = max + new Long(1);
            for (PurOutsourceInquiryItem item : newItem) {
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
     * 复制加工询价单
     *
     * @param purOutsourceInquirySid 加工询价单主ID
     * @return 加工询价单
     */
    @Override
    public PurOutsourceInquiry copy(Long purOutsourceInquirySid) {
        PurOutsourceInquiry response = new PurOutsourceInquiry();
        response = this.selectPurOutsourceInquiryById(purOutsourceInquirySid);
        String creatorAccountName = ApiThreadLocalUtil.get().getSysUser().getNickName();
        //主表信息清洗
        response.setClientId(null).setOutsourceInquirySid(null).setOutsourceInquiryCode(null)
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
                item.setClientId(null).setOutsourceInquirySid(null).setOutsourceInquiryItemSid(null);
                item.setCreatorAccount(null).setCreateDate(null)
                        .setCreatorAccountName(creatorAccountName)
                        .setUpdaterAccount(null).setUpdateDate(null);
                item.setOperLogList(null);
            });
        }
        //供应商明细清洗
        if (CollectionUtil.isNotEmpty(response.getVendorList())){
            response.getVendorList().forEach(vendor->{
                vendor.setClientId(null).setOutsourceInquirySid(null).setOutsourceInquiryVendorSid(null);
                vendor.setCreatorAccount(null).setCreateDate(null)
                        .setCreatorAccountName(creatorAccountName);
                vendor.setQuoteStatus(null);
                vendor.setOperLogList(null);
            });
        }
        //附件明细清洗
        if (CollectionUtil.isNotEmpty(response.getAttachmentList())){
            response.getAttachmentList().forEach(attach->{
                attach.setClientId(null).setOutsourceInquirySid(null).setOutsourceInquiryAttachmentSid(null);
                attach.setCreatorAccount(null).setCreateDate(null)
                        .setCreatorAccountName(creatorAccountName)
                        .setUpdaterAccount(null).setUpdateDate(null);
                attach.setOperLogList(null);
            });
        }
        return response;
    }
}
