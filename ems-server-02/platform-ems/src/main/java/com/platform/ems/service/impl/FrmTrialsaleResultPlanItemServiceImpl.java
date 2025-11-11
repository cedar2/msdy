package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FrmTrialsaleResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FrmTrialsaleResultPlanItemMapper;
import com.platform.ems.domain.FrmTrialsaleResultPlanItem;
import com.platform.ems.service.IFrmTrialsaleResultPlanItemService;

/**
 * 试销结果单-计划项Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsaleResultPlanItemServiceImpl extends ServiceImpl<FrmTrialsaleResultPlanItemMapper, FrmTrialsaleResultPlanItem> implements IFrmTrialsaleResultPlanItemService {
    @Autowired
    private FrmTrialsaleResultPlanItemMapper frmTrialsaleResultPlanItemMapper;

    private static final String TITLE = "试销结果单-计划项";

    /**
     * 查询试销结果单-计划项
     *
     * @param trialsaleResultPlanItemSid 试销结果单-计划项ID
     * @return 试销结果单-计划项
     */
    @Override
    public FrmTrialsaleResultPlanItem selectFrmTrialsaleResultPlanItemById(Long trialsaleResultPlanItemSid) {
        FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem = frmTrialsaleResultPlanItemMapper.selectFrmTrialsaleResultPlanItemById(trialsaleResultPlanItemSid);
        MongodbUtil.find(frmTrialsaleResultPlanItem);
        return frmTrialsaleResultPlanItem;
    }

    /**
     * 查询试销结果单-计划项列表
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 试销结果单-计划项
     */
    @Override
    public List<FrmTrialsaleResultPlanItem> selectFrmTrialsaleResultPlanItemList(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem) {
        return frmTrialsaleResultPlanItemMapper.selectFrmTrialsaleResultPlanItemList(frmTrialsaleResultPlanItem);
    }

    /**
     * 新增试销结果单-计划项
     * 需要注意编码重复校验
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResultPlanItem(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem) {
        frmTrialsaleResultPlanItem.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        int row = frmTrialsaleResultPlanItemMapper.insert(frmTrialsaleResultPlanItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsaleResultPlanItem(), frmTrialsaleResultPlanItem);
            MongodbUtil.insertUserLog(frmTrialsaleResultPlanItem.getTrialsaleResultPlanItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改试销结果单-计划项
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResultPlanItem(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem) {
        FrmTrialsaleResultPlanItem original = frmTrialsaleResultPlanItemMapper.selectFrmTrialsaleResultPlanItemById(frmTrialsaleResultPlanItem.getTrialsaleResultPlanItemSid());
        int row = frmTrialsaleResultPlanItemMapper.updateById(frmTrialsaleResultPlanItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsaleResultPlanItem);
            MongodbUtil.insertUserLog(frmTrialsaleResultPlanItem.getTrialsaleResultPlanItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更试销结果单-计划项
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsaleResultPlanItem(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem) {
        FrmTrialsaleResultPlanItem response = frmTrialsaleResultPlanItemMapper.selectFrmTrialsaleResultPlanItemById(frmTrialsaleResultPlanItem.getTrialsaleResultPlanItemSid());
        int row = frmTrialsaleResultPlanItemMapper.updateAllById(frmTrialsaleResultPlanItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsaleResultPlanItem.getTrialsaleResultPlanItemSid(), BusinessType.CHANGE.getValue(), response, frmTrialsaleResultPlanItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除试销结果单-计划项
     *
     * @param trialsaleResultPlanItemSids 需要删除的试销结果单-计划项ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultPlanItemByIds(List<Long> trialsaleResultPlanItemSids) {
        List<FrmTrialsaleResultPlanItem> list = frmTrialsaleResultPlanItemMapper.selectList(new QueryWrapper<FrmTrialsaleResultPlanItem>()
                .lambda().in(FrmTrialsaleResultPlanItem::getTrialsaleResultPlanItemSid, trialsaleResultPlanItemSids));
        int row = frmTrialsaleResultPlanItemMapper.deleteBatchIds(trialsaleResultPlanItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsaleResultPlanItem());
                MongodbUtil.insertUserLog(o.getTrialsaleResultPlanItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 查询新品试销结果单-计划项
     *
     * @param trialsaleResultSid 新品试销结果单-新品试销结果单-主表ID
     * @return 新品试销结果单-计划项
     */
    @Override
    public List<FrmTrialsaleResultPlanItem> selectFrmTrialsaleResultPlanItemListById(Long trialsaleResultSid) {
        List<FrmTrialsaleResultPlanItem> trialsalePlanCategoryAnalysisList = frmTrialsaleResultPlanItemMapper
                .selectFrmTrialsaleResultPlanItemList(new FrmTrialsaleResultPlanItem()
                        .setTrialsaleResultSid(trialsaleResultSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisList)) {
            trialsalePlanCategoryAnalysisList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return trialsalePlanCategoryAnalysisList;
    }

    /**
     * 批量新增新品试销结果单-计划项
     *
     * @param trialsaleResult 新品试销结果单-新品试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResultPlanItemList(FrmTrialsaleResult trialsaleResult) {
        int row = 0;
        List<FrmTrialsaleResultPlanItem> list = trialsaleResult.getPlanItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsaleResultPlanItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setTrialsaleResultSid(trialsaleResult.getTrialsaleResultSid());
                row += insertFrmTrialsaleResultPlanItem(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销结果单-计划项
     *
     * @param trialsaleResult 新品试销结果单-新品试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResultPlanItemList(FrmTrialsaleResult trialsaleResult) {
        int row = 0;
        List<FrmTrialsaleResultPlanItem> list = trialsaleResult.getPlanItemList();
        // 原本明细
        List<FrmTrialsaleResultPlanItem> oldList = frmTrialsaleResultPlanItemMapper.selectList(new QueryWrapper<FrmTrialsaleResultPlanItem>()
                .lambda().eq(FrmTrialsaleResultPlanItem::getTrialsaleResultSid, trialsaleResult.getTrialsaleResultSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsaleResultPlanItem> newList = list.stream().filter(o -> o.getTrialsaleResultPlanItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                trialsaleResult.setPlanItemList(newList);
                insertFrmTrialsaleResultPlanItemList(trialsaleResult);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsaleResultPlanItem> updateList = list.stream().filter(o -> o.getTrialsaleResultPlanItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsaleResultPlanItem::getTrialsaleResultPlanItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsaleResultPlanItem> map = oldList.stream().collect(Collectors.toMap(FrmTrialsaleResultPlanItem::getTrialsaleResultPlanItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsaleResultPlanItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsaleResultPlanItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsaleResultPlanItemMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsaleResultPlanItemSid(), trialsaleResult.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsaleResultPlanItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsaleResultPlanItemSid())).collect(Collectors.toList());
                    deleteFrmTrialsaleResultPlanItemByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsaleResultPlanItemByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销结果单-计划项
     *
     * @param itemList 需要删除的新品试销结果单-新品试销结果单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultPlanItemByList(List<FrmTrialsaleResultPlanItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsaleResultPlanItemSid() != null)
                .map(FrmTrialsaleResultPlanItem::getTrialsaleResultPlanItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsaleResultPlanItemMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsaleResultPlanItem());
                    MongodbUtil.insertUserLog(o.getTrialsaleResultPlanItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销结果单-计划项 根据主表sids
     *
     * @param trialsaleResultSidList 需要删除的新品试销结果单-新品试销结果单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultPlanItemByPlan(List<Long> trialsaleResultSidList) {
        List<FrmTrialsaleResultPlanItem> itemList = frmTrialsaleResultPlanItemMapper.selectList(new QueryWrapper<FrmTrialsaleResultPlanItem>()
                .lambda().in(FrmTrialsaleResultPlanItem::getTrialsaleResultSid, trialsaleResultSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsaleResultPlanItemByList(itemList);
        }
        return row;
    }

}
