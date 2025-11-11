package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConManufactureMilestoneListItem;
import com.platform.ems.plug.mapper.ConManufactureMilestoneListItemMapper;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.plug.mapper.ConManufactureMilestoneListMapper;
import com.platform.ems.plug.domain.ConManufactureMilestoneList;
import com.platform.ems.plug.service.IConManufactureMilestoneListService;

/**
 * 生产里程碑清单Service业务层处理
 *
 * @author platform
 * @date 2024-03-14
 */
@Service
@SuppressWarnings("all")
public class ConManufactureMilestoneListServiceImpl extends ServiceImpl<ConManufactureMilestoneListMapper, ConManufactureMilestoneList> implements IConManufactureMilestoneListService {
    @Autowired
    private ConManufactureMilestoneListMapper conManufactureMilestoneListMapper;
    @Autowired
    private ConManufactureMilestoneListItemMapper itemMapper;

    private static final String TITLE = "生产里程碑清单";

    /**
     * 查询生产里程碑清单
     *
     * @param manufactureMilestoneListSid 生产里程碑清单ID
     * @return 生产里程碑清单
     */
    @Override
    public ConManufactureMilestoneList selectConManufactureMilestoneListById(Long manufactureMilestoneListSid) {
        ConManufactureMilestoneList conManufactureMilestoneList = conManufactureMilestoneListMapper.selectConManufactureMilestoneListById(manufactureMilestoneListSid);
        // 明细
        conManufactureMilestoneList.setItemList(new ArrayList<>());
        List<ConManufactureMilestoneListItem> itemList = itemMapper.selectConManufactureMilestoneListItemList
                (new ConManufactureMilestoneListItem().setManufactureMilestoneListSid(manufactureMilestoneListSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            conManufactureMilestoneList.setItemList(itemList);
        }
        // 操作日志
        MongodbUtil.find(conManufactureMilestoneList);
        return conManufactureMilestoneList;
    }

    /**
     * 查询生产里程碑清单列表
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 生产里程碑清单
     */
    @Override
    public List<ConManufactureMilestoneList> selectConManufactureMilestoneListList(ConManufactureMilestoneList conManufactureMilestoneList) {
        return conManufactureMilestoneListMapper.selectConManufactureMilestoneListList(conManufactureMilestoneList);
    }

    /**
     * 校验
     */
    public void judge(ConManufactureMilestoneList milestoneList) {
        //
        if (ConstantsEms.CHECK_STATUS.equals(milestoneList.getHandleStatus())) {
            if (CollectionUtil.isEmpty(milestoneList.getItemList())) {
                throw new BaseException("明细项不能为空！");
            }
        }
        //
        QueryWrapper<ConManufactureMilestoneList> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConManufactureMilestoneList::getMilestoneType, milestoneList.getMilestoneType());
        if (milestoneList.getManufactureMilestoneListSid() != null) {
            queryWrapper.lambda().ne(ConManufactureMilestoneList::getManufactureMilestoneListSid, milestoneList.getManufactureMilestoneListSid());
        }
        List<ConManufactureMilestoneList> list = conManufactureMilestoneListMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("里程碑类型已存在！");
        }
    }

    /**
     * 新增生产里程碑清单
     * 需要注意编码重复校验
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConManufactureMilestoneList(ConManufactureMilestoneList conManufactureMilestoneList) {
        judge(conManufactureMilestoneList);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(conManufactureMilestoneList.getHandleStatus())) {
            conManufactureMilestoneList.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conManufactureMilestoneListMapper.insert(conManufactureMilestoneList);
        if (row > 0) {
            // 明细
            if (CollectionUtil.isNotEmpty(conManufactureMilestoneList.getItemList())) {
                conManufactureMilestoneList.getItemList().forEach(item->{
                    item.setManufactureMilestoneListSid(conManufactureMilestoneList.getManufactureMilestoneListSid());
                });
                itemMapper.inserts(conManufactureMilestoneList.getItemList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConManufactureMilestoneList(), conManufactureMilestoneList);
            MongodbDeal.insert(conManufactureMilestoneList.getManufactureMilestoneListSid(), conManufactureMilestoneList.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateItemList(ConManufactureMilestoneList milestoneList) {
        // 先删后加
        itemMapper.delete(new QueryWrapper<ConManufactureMilestoneListItem>().lambda()
                .eq(ConManufactureMilestoneListItem::getManufactureMilestoneListSid, milestoneList.getManufactureMilestoneListSid()));
        if (CollectionUtil.isNotEmpty(milestoneList.getItemList())) {
            milestoneList.getItemList().forEach(att -> {
                // 如果是新的
                if (att.getManufactureMilestoneListItemSid() == null) {
                    att.setManufactureMilestoneListSid(milestoneList.getManufactureMilestoneListSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            itemMapper.inserts(milestoneList.getItemList());
        }
    }

    /**
     * 修改生产里程碑清单
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConManufactureMilestoneList(ConManufactureMilestoneList conManufactureMilestoneList) {
        judge(conManufactureMilestoneList);
        ConManufactureMilestoneList original = conManufactureMilestoneListMapper.selectConManufactureMilestoneListById(conManufactureMilestoneList.getManufactureMilestoneListSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(conManufactureMilestoneList.getHandleStatus())) {
            conManufactureMilestoneList.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, conManufactureMilestoneList);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conManufactureMilestoneList.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conManufactureMilestoneListMapper.updateAllById(conManufactureMilestoneList);
        if (row > 0) {
            // 明细
            updateItemList(conManufactureMilestoneList);
            //插入日志
            MongodbDeal.update(conManufactureMilestoneList.getManufactureMilestoneListSid(), original.getHandleStatus(),
                    conManufactureMilestoneList.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产里程碑清单
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConManufactureMilestoneList(ConManufactureMilestoneList conManufactureMilestoneList) {
        judge(conManufactureMilestoneList);
        ConManufactureMilestoneList response = conManufactureMilestoneListMapper.selectConManufactureMilestoneListById(conManufactureMilestoneList.getManufactureMilestoneListSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, conManufactureMilestoneList);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conManufactureMilestoneList.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conManufactureMilestoneListMapper.updateAllById(conManufactureMilestoneList);
        if (row > 0) {
            // 明细
            updateItemList(conManufactureMilestoneList);
            //插入日志
            MongodbUtil.insertUserLog(conManufactureMilestoneList.getManufactureMilestoneListSid(), BusinessType.CHANGE.getValue(), response, conManufactureMilestoneList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产里程碑清单
     *
     * @param manufactureMilestoneListSids 需要删除的生产里程碑清单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConManufactureMilestoneListByIds(List<Long> manufactureMilestoneListSids) {
        List<ConManufactureMilestoneList> list = conManufactureMilestoneListMapper.selectList(new QueryWrapper<ConManufactureMilestoneList>()
                .lambda().in(ConManufactureMilestoneList::getManufactureMilestoneListSid, manufactureMilestoneListSids));
        int row = conManufactureMilestoneListMapper.deleteBatchIds(manufactureMilestoneListSids);
        if (row > 0) {
            // 附件
            itemMapper.delete(new QueryWrapper<ConManufactureMilestoneListItem>()
                    .lambda().in(ConManufactureMilestoneListItem::getManufactureMilestoneListSid, manufactureMilestoneListSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConManufactureMilestoneList());
                MongodbUtil.insertUserLog(o.getManufactureMilestoneListSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param conManufactureMilestoneList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConManufactureMilestoneList conManufactureMilestoneList) {
        Long[] sids = conManufactureMilestoneList.getManufactureMilestoneListSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<ConManufactureMilestoneList> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ConManufactureMilestoneList::getManufactureMilestoneListSid, sids);
        updateWrapper.set(ConManufactureMilestoneList::getHandleStatus, conManufactureMilestoneList.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(conManufactureMilestoneList.getHandleStatus())) {
            updateWrapper.set(ConManufactureMilestoneList::getConfirmDate, new Date());
            updateWrapper.set(ConManufactureMilestoneList::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = conManufactureMilestoneListMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, conManufactureMilestoneList.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

}
