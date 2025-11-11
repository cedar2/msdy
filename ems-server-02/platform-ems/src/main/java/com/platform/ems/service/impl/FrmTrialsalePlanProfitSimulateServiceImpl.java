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
import com.platform.ems.domain.FrmTrialsalePlanCategoryAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FrmTrialsalePlanProfitSimulateMapper;
import com.platform.ems.domain.FrmTrialsalePlanProfitSimulate;
import com.platform.ems.service.IFrmTrialsalePlanProfitSimulateService;

/**
 * 新品试销计划单-利润模拟Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsalePlanProfitSimulateServiceImpl extends ServiceImpl<FrmTrialsalePlanProfitSimulateMapper, FrmTrialsalePlanProfitSimulate> implements IFrmTrialsalePlanProfitSimulateService {
    @Autowired
    private FrmTrialsalePlanProfitSimulateMapper frmTrialsalePlanProfitSimulateMapper;

    private static final String TITLE = "新品试销计划单-利润模拟";

    /**
     * 查询新品试销计划单-利润模拟
     *
     * @param trialsalePlanProfitSimulationSid 新品试销计划单-利润模拟ID
     * @return 新品试销计划单-利润模拟
     */
    @Override
    public FrmTrialsalePlanProfitSimulate selectFrmTrialsalePlanProfitSimulateById(Long trialsalePlanProfitSimulationSid) {
        FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate = frmTrialsalePlanProfitSimulateMapper.selectFrmTrialsalePlanProfitSimulateById(trialsalePlanProfitSimulationSid);
        MongodbUtil.find(frmTrialsalePlanProfitSimulate);
        return frmTrialsalePlanProfitSimulate;
    }

    /**
     * 查询新品试销计划单-利润模拟列表
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 新品试销计划单-利润模拟
     */
    @Override
    public List<FrmTrialsalePlanProfitSimulate> selectFrmTrialsalePlanProfitSimulateList(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate) {
        return frmTrialsalePlanProfitSimulateMapper.selectFrmTrialsalePlanProfitSimulateList(frmTrialsalePlanProfitSimulate);
    }

    /**
     * 新增新品试销计划单-利润模拟
     * 需要注意编码重复校验
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanProfitSimulate(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate) {
        frmTrialsalePlanProfitSimulate.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        int row = frmTrialsalePlanProfitSimulateMapper.insert(frmTrialsalePlanProfitSimulate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsalePlanProfitSimulate(), frmTrialsalePlanProfitSimulate);
            MongodbUtil.insertUserLog(frmTrialsalePlanProfitSimulate.getTrialsalePlanProfitSimulationSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改新品试销计划单-利润模拟
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanProfitSimulate(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate) {
        FrmTrialsalePlanProfitSimulate original = frmTrialsalePlanProfitSimulateMapper.selectFrmTrialsalePlanProfitSimulateById(frmTrialsalePlanProfitSimulate.getTrialsalePlanProfitSimulationSid());
        int row = frmTrialsalePlanProfitSimulateMapper.updateById(frmTrialsalePlanProfitSimulate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsalePlanProfitSimulate);
            MongodbUtil.insertUserLog(frmTrialsalePlanProfitSimulate.getTrialsalePlanProfitSimulationSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更新品试销计划单-利润模拟
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsalePlanProfitSimulate(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate) {
        FrmTrialsalePlanProfitSimulate response = frmTrialsalePlanProfitSimulateMapper.selectFrmTrialsalePlanProfitSimulateById(frmTrialsalePlanProfitSimulate.getTrialsalePlanProfitSimulationSid());
        int row = frmTrialsalePlanProfitSimulateMapper.updateAllById(frmTrialsalePlanProfitSimulate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsalePlanProfitSimulate.getTrialsalePlanProfitSimulationSid(), BusinessType.CHANGE.getValue(), response, frmTrialsalePlanProfitSimulate, TITLE);
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-利润模拟
     *
     * @param trialsalePlanProfitSimulationSids 需要删除的新品试销计划单-利润模拟ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanProfitSimulateByIds(List<Long> trialsalePlanProfitSimulationSids) {
        List<FrmTrialsalePlanProfitSimulate> list = frmTrialsalePlanProfitSimulateMapper.selectList(new QueryWrapper<FrmTrialsalePlanProfitSimulate>()
                .lambda().in(FrmTrialsalePlanProfitSimulate::getTrialsalePlanProfitSimulationSid, trialsalePlanProfitSimulationSids));
        int row = frmTrialsalePlanProfitSimulateMapper.deleteBatchIds(trialsalePlanProfitSimulationSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsalePlanProfitSimulate());
                MongodbUtil.insertUserLog(o.getTrialsalePlanProfitSimulationSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 查询新品试销计划单-利润模拟
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-新品试销计划单-主表ID
     * @return 新品试销计划单-利润模拟
     */
    @Override
    public List<FrmTrialsalePlanProfitSimulate> selectFrmTrialsalePlanProfitSimulateListById(Long newproductTrialsalePlanSid) {
        List<FrmTrialsalePlanProfitSimulate> trialsalePlanCategoryAnalysisList = frmTrialsalePlanProfitSimulateMapper
                .selectFrmTrialsalePlanProfitSimulateList(new FrmTrialsalePlanProfitSimulate()
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
     * 批量新增新品试销计划单-利润模拟
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanProfitSimulateList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanProfitSimulate> list = newproductTrialsalePlan.getProfitSimulateList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsalePlanProfitSimulate item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setNewproductTrialsalePlanSid(newproductTrialsalePlan.getNewproductTrialsalePlanSid());
                row += insertFrmTrialsalePlanProfitSimulate(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销计划单-利润模拟
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanProfitSimulateList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanProfitSimulate> list = newproductTrialsalePlan.getProfitSimulateList();
        // 原本明细
        List<FrmTrialsalePlanProfitSimulate> oldList = frmTrialsalePlanProfitSimulateMapper.selectList(new QueryWrapper<FrmTrialsalePlanProfitSimulate>()
                .lambda().eq(FrmTrialsalePlanProfitSimulate::getNewproductTrialsalePlanSid, newproductTrialsalePlan.getNewproductTrialsalePlanSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsalePlanProfitSimulate> newList = list.stream().filter(o -> o.getTrialsalePlanProfitSimulationSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                newproductTrialsalePlan.setProfitSimulateList(newList);
                insertFrmTrialsalePlanProfitSimulateList(newproductTrialsalePlan);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsalePlanProfitSimulate> updateList = list.stream().filter(o -> o.getTrialsalePlanProfitSimulationSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsalePlanProfitSimulate::getTrialsalePlanProfitSimulationSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsalePlanProfitSimulate> map = oldList.stream().collect(Collectors.toMap(FrmTrialsalePlanProfitSimulate::getTrialsalePlanProfitSimulationSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsalePlanProfitSimulationSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsalePlanProfitSimulationSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsalePlanProfitSimulateMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsalePlanProfitSimulationSid(), newproductTrialsalePlan.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsalePlanProfitSimulate> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsalePlanProfitSimulationSid())).collect(Collectors.toList());
                    deleteFrmTrialsalePlanProfitSimulateByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsalePlanProfitSimulateByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-利润模拟
     *
     * @param itemList 需要删除的新品试销计划单-新品试销计划单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanProfitSimulateByList(List<FrmTrialsalePlanProfitSimulate> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsalePlanProfitSimulationSid() != null)
                .map(FrmTrialsalePlanProfitSimulate::getTrialsalePlanProfitSimulationSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsalePlanProfitSimulateMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsalePlanProfitSimulate());
                    MongodbUtil.insertUserLog(o.getTrialsalePlanProfitSimulationSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-利润模拟 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单-新品试销计划单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanProfitSimulateByPlan(List<Long> newproductTrialsalePlanSidList) {
        List<FrmTrialsalePlanProfitSimulate> itemList = frmTrialsalePlanProfitSimulateMapper.selectList(new QueryWrapper<FrmTrialsalePlanProfitSimulate>()
                .lambda().in(FrmTrialsalePlanProfitSimulate::getNewproductTrialsalePlanSid, newproductTrialsalePlanSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsalePlanProfitSimulateByList(itemList);
        }
        return row;
    }
}
