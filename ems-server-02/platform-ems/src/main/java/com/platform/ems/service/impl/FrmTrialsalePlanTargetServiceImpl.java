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
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanProfitSimulate;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FrmTrialsalePlanTargetMapper;
import com.platform.ems.domain.FrmTrialsalePlanTarget;
import com.platform.ems.service.IFrmTrialsalePlanTargetService;

/**
 * 新品试销计划单-目标预定Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsalePlanTargetServiceImpl extends ServiceImpl<FrmTrialsalePlanTargetMapper, FrmTrialsalePlanTarget> implements IFrmTrialsalePlanTargetService {
    @Autowired
    private FrmTrialsalePlanTargetMapper frmTrialsalePlanTargetMapper;

    private static final String TITLE = "新品试销计划单-目标预定";

    /**
     * 查询新品试销计划单-目标预定
     *
     * @param trialsalePlanTargetSid 新品试销计划单-目标预定ID
     * @return 新品试销计划单-目标预定
     */
    @Override
    public FrmTrialsalePlanTarget selectFrmTrialsalePlanTargetById(Long trialsalePlanTargetSid) {
        FrmTrialsalePlanTarget frmTrialsalePlanTarget = frmTrialsalePlanTargetMapper.selectFrmTrialsalePlanTargetById(trialsalePlanTargetSid);
        MongodbUtil.find(frmTrialsalePlanTarget);
        return frmTrialsalePlanTarget;
    }

    /**
     * 查询新品试销计划单-目标预定列表
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 新品试销计划单-目标预定
     */
    @Override
    public List<FrmTrialsalePlanTarget> selectFrmTrialsalePlanTargetList(FrmTrialsalePlanTarget frmTrialsalePlanTarget) {
        return frmTrialsalePlanTargetMapper.selectFrmTrialsalePlanTargetList(frmTrialsalePlanTarget);
    }

    /**
     * 新增新品试销计划单-目标预定
     * 需要注意编码重复校验
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanTarget(FrmTrialsalePlanTarget frmTrialsalePlanTarget) {
        frmTrialsalePlanTarget.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        int row = frmTrialsalePlanTargetMapper.insert(frmTrialsalePlanTarget);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsalePlanTarget(), frmTrialsalePlanTarget);
            MongodbUtil.insertUserLog(frmTrialsalePlanTarget.getTrialsalePlanTargetSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改新品试销计划单-目标预定
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanTarget(FrmTrialsalePlanTarget frmTrialsalePlanTarget) {
        FrmTrialsalePlanTarget original = frmTrialsalePlanTargetMapper.selectFrmTrialsalePlanTargetById(frmTrialsalePlanTarget.getTrialsalePlanTargetSid());
        int row = frmTrialsalePlanTargetMapper.updateById(frmTrialsalePlanTarget);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsalePlanTarget);
            MongodbUtil.insertUserLog(frmTrialsalePlanTarget.getTrialsalePlanTargetSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更新品试销计划单-目标预定
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsalePlanTarget(FrmTrialsalePlanTarget frmTrialsalePlanTarget) {
        FrmTrialsalePlanTarget response = frmTrialsalePlanTargetMapper.selectFrmTrialsalePlanTargetById(frmTrialsalePlanTarget.getTrialsalePlanTargetSid());
        int row = frmTrialsalePlanTargetMapper.updateAllById(frmTrialsalePlanTarget);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsalePlanTarget.getTrialsalePlanTargetSid(), BusinessType.CHANGE.getValue(), response, frmTrialsalePlanTarget, TITLE);
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-目标预定
     *
     * @param trialsalePlanTargetSids 需要删除的新品试销计划单-目标预定ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanTargetByIds(List<Long> trialsalePlanTargetSids) {
        List<FrmTrialsalePlanTarget> list = frmTrialsalePlanTargetMapper.selectList(new QueryWrapper<FrmTrialsalePlanTarget>()
                .lambda().in(FrmTrialsalePlanTarget::getTrialsalePlanTargetSid, trialsalePlanTargetSids));
        int row = frmTrialsalePlanTargetMapper.deleteBatchIds(trialsalePlanTargetSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsalePlanTarget());
                MongodbUtil.insertUserLog(o.getTrialsalePlanTargetSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 查询新品试销计划单-目标预定
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-新品试销计划单-主表ID
     * @return 新品试销计划单-目标预定
     */
    @Override
    public List<FrmTrialsalePlanTarget> selectFrmTrialsalePlanTargetListById(Long newproductTrialsalePlanSid) {
        List<FrmTrialsalePlanTarget> trialsalePlanCategoryAnalysisList = frmTrialsalePlanTargetMapper
                .selectFrmTrialsalePlanTargetList(new FrmTrialsalePlanTarget()
                        .setNewproductTrialsalePlanSid(newproductTrialsalePlanSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisList)) {
            trialsalePlanCategoryAnalysisList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return trialsalePlanCategoryAnalysisList;
    }

    /**
     * 批量新增新品试销计划单-目标预定
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanTargetList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanTarget> list = newproductTrialsalePlan.getTargetList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsalePlanTarget item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setNewproductTrialsalePlanSid(newproductTrialsalePlan.getNewproductTrialsalePlanSid());
                row += insertFrmTrialsalePlanTarget(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销计划单-目标预定
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanTargetList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanTarget> list = newproductTrialsalePlan.getTargetList();
        // 原本明细
        List<FrmTrialsalePlanTarget> oldList = frmTrialsalePlanTargetMapper.selectList(new QueryWrapper<FrmTrialsalePlanTarget>()
                .lambda().eq(FrmTrialsalePlanTarget::getNewproductTrialsalePlanSid, newproductTrialsalePlan.getNewproductTrialsalePlanSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsalePlanTarget> newList = list.stream().filter(o -> o.getTrialsalePlanTargetSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                newproductTrialsalePlan.setTargetList(newList);
                insertFrmTrialsalePlanTargetList(newproductTrialsalePlan);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsalePlanTarget> updateList = list.stream().filter(o -> o.getTrialsalePlanTargetSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsalePlanTarget::getTrialsalePlanTargetSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsalePlanTarget> map = oldList.stream().collect(Collectors.toMap(FrmTrialsalePlanTarget::getTrialsalePlanTargetSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsalePlanTargetSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsalePlanTargetSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsalePlanTargetMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsalePlanTargetSid(), newproductTrialsalePlan.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsalePlanTarget> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsalePlanTargetSid())).collect(Collectors.toList());
                    deleteFrmTrialsalePlanTargetByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsalePlanTargetByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-目标预定
     *
     * @param itemList 需要删除的新品试销计划单-新品试销计划单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanTargetByList(List<FrmTrialsalePlanTarget> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsalePlanTargetSid() != null)
                .map(FrmTrialsalePlanTarget::getTrialsalePlanTargetSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsalePlanTargetMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsalePlanTarget());
                    MongodbUtil.insertUserLog(o.getTrialsalePlanTargetSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-目标预定 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单-新品试销计划单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanTargetByPlan(List<Long> newproductTrialsalePlanSidList) {
        List<FrmTrialsalePlanTarget> itemList = frmTrialsalePlanTargetMapper.selectList(new QueryWrapper<FrmTrialsalePlanTarget>()
                .lambda().in(FrmTrialsalePlanTarget::getNewproductTrialsalePlanSid, newproductTrialsalePlanSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsalePlanTargetByList(itemList);
        }
        return row;
    }
}
