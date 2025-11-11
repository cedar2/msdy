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
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FrmTrialsalePlanCategoryAnalysisMapper;
import com.platform.ems.domain.FrmTrialsalePlanCategoryAnalysis;
import com.platform.ems.service.IFrmTrialsalePlanCategoryAnalysisService;

/**
 * 新品试销计划单-类目分析Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsalePlanCategoryAnalysisServiceImpl extends ServiceImpl<FrmTrialsalePlanCategoryAnalysisMapper, FrmTrialsalePlanCategoryAnalysis> implements IFrmTrialsalePlanCategoryAnalysisService {
    @Autowired
    private FrmTrialsalePlanCategoryAnalysisMapper frmTrialsalePlanCategoryAnalysisMapper;

    private static final String TITLE = "新品试销计划单-类目分析";

    /**
     * 查询新品试销计划单-类目分析
     *
     * @param trialsalePlanCategoryAnalysisSid 新品试销计划单-类目分析ID
     * @return 新品试销计划单-类目分析
     */
    @Override
    public FrmTrialsalePlanCategoryAnalysis selectFrmTrialsalePlanCategoryAnalysisById(Long trialsalePlanCategoryAnalysisSid) {
        FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis = frmTrialsalePlanCategoryAnalysisMapper.selectFrmTrialsalePlanCategoryAnalysisById(trialsalePlanCategoryAnalysisSid);
        MongodbUtil.find(frmTrialsalePlanCategoryAnalysis);
        return frmTrialsalePlanCategoryAnalysis;
    }

    /**
     * 查询新品试销计划单-类目分析列表
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 新品试销计划单-类目分析
     */
    @Override
    public List<FrmTrialsalePlanCategoryAnalysis> selectFrmTrialsalePlanCategoryAnalysisList(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis) {
        return frmTrialsalePlanCategoryAnalysisMapper.selectFrmTrialsalePlanCategoryAnalysisList(frmTrialsalePlanCategoryAnalysis);
    }

    /**
     * 新增新品试销计划单-类目分析
     * 需要注意编码重复校验
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanCategoryAnalysis(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis) {
        frmTrialsalePlanCategoryAnalysis.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        int row = frmTrialsalePlanCategoryAnalysisMapper.insert(frmTrialsalePlanCategoryAnalysis);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsalePlanCategoryAnalysis(), frmTrialsalePlanCategoryAnalysis);
            MongodbUtil.insertUserLog(frmTrialsalePlanCategoryAnalysis.getTrialsalePlanCategoryAnalysisSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改新品试销计划单-类目分析
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanCategoryAnalysis(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis) {
        FrmTrialsalePlanCategoryAnalysis original = frmTrialsalePlanCategoryAnalysisMapper.selectFrmTrialsalePlanCategoryAnalysisById(frmTrialsalePlanCategoryAnalysis.getTrialsalePlanCategoryAnalysisSid());
        int row = frmTrialsalePlanCategoryAnalysisMapper.updateById(frmTrialsalePlanCategoryAnalysis);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsalePlanCategoryAnalysis);
            MongodbUtil.insertUserLog(frmTrialsalePlanCategoryAnalysis.getTrialsalePlanCategoryAnalysisSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更新品试销计划单-类目分析
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsalePlanCategoryAnalysis(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis) {
        FrmTrialsalePlanCategoryAnalysis response = frmTrialsalePlanCategoryAnalysisMapper.selectFrmTrialsalePlanCategoryAnalysisById(frmTrialsalePlanCategoryAnalysis.getTrialsalePlanCategoryAnalysisSid());
        int row = frmTrialsalePlanCategoryAnalysisMapper.updateAllById(frmTrialsalePlanCategoryAnalysis);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsalePlanCategoryAnalysis.getTrialsalePlanCategoryAnalysisSid(), BusinessType.CHANGE.getValue(), response, frmTrialsalePlanCategoryAnalysis, TITLE);
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-类目分析
     *
     * @param trialsalePlanCategoryAnalysisSids 需要删除的新品试销计划单-类目分析ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanCategoryAnalysisByIds(List<Long> trialsalePlanCategoryAnalysisSids) {
        List<FrmTrialsalePlanCategoryAnalysis> list = frmTrialsalePlanCategoryAnalysisMapper.selectList(new QueryWrapper<FrmTrialsalePlanCategoryAnalysis>()
                .lambda().in(FrmTrialsalePlanCategoryAnalysis::getTrialsalePlanCategoryAnalysisSid, trialsalePlanCategoryAnalysisSids));
        int row = frmTrialsalePlanCategoryAnalysisMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsalePlanCategoryAnalysis());
                MongodbUtil.insertUserLog(o.getTrialsalePlanCategoryAnalysisSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 查询新品试销计划单-类目分析
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-新品试销计划单-主表ID
     * @return 新品试销计划单-类目分析
     */
    @Override
    public List<FrmTrialsalePlanCategoryAnalysis> selectFrmTrialsalePlanCategoryAnalysisListById(Long newproductTrialsalePlanSid) {
        List<FrmTrialsalePlanCategoryAnalysis> trialsalePlanCategoryAnalysisList = frmTrialsalePlanCategoryAnalysisMapper
                .selectFrmTrialsalePlanCategoryAnalysisList(new FrmTrialsalePlanCategoryAnalysis()
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
     * 批量新增新品试销计划单-类目分析
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanCategoryAnalysisList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanCategoryAnalysis> list = newproductTrialsalePlan.getAnalysisList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsalePlanCategoryAnalysis item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setNewproductTrialsalePlanSid(newproductTrialsalePlan.getNewproductTrialsalePlanSid());
                row += insertFrmTrialsalePlanCategoryAnalysis(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销计划单-类目分析
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanCategoryAnalysisList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanCategoryAnalysis> list = newproductTrialsalePlan.getAnalysisList();
        // 原本明细
        List<FrmTrialsalePlanCategoryAnalysis> oldList = frmTrialsalePlanCategoryAnalysisMapper.selectList(new QueryWrapper<FrmTrialsalePlanCategoryAnalysis>()
                .lambda().eq(FrmTrialsalePlanCategoryAnalysis::getNewproductTrialsalePlanSid, newproductTrialsalePlan.getNewproductTrialsalePlanSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsalePlanCategoryAnalysis> newList = list.stream().filter(o -> o.getTrialsalePlanCategoryAnalysisSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                newproductTrialsalePlan.setAnalysisList(newList);
                insertFrmTrialsalePlanCategoryAnalysisList(newproductTrialsalePlan);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsalePlanCategoryAnalysis> updateList = list.stream().filter(o -> o.getTrialsalePlanCategoryAnalysisSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsalePlanCategoryAnalysis::getTrialsalePlanCategoryAnalysisSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsalePlanCategoryAnalysis> map = oldList.stream().collect(Collectors.toMap(FrmTrialsalePlanCategoryAnalysis::getTrialsalePlanCategoryAnalysisSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsalePlanCategoryAnalysisSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsalePlanCategoryAnalysisSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsalePlanCategoryAnalysisMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsalePlanCategoryAnalysisSid(), newproductTrialsalePlan.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsalePlanCategoryAnalysis> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsalePlanCategoryAnalysisSid())).collect(Collectors.toList());
                    deleteFrmTrialsalePlanCategoryAnalysisByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsalePlanCategoryAnalysisByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-类目分析
     *
     * @param itemList 需要删除的新品试销计划单-新品试销计划单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanCategoryAnalysisByList(List<FrmTrialsalePlanCategoryAnalysis> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsalePlanCategoryAnalysisSid() != null)
                .map(FrmTrialsalePlanCategoryAnalysis::getTrialsalePlanCategoryAnalysisSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsalePlanCategoryAnalysisMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsalePlanCategoryAnalysis());
                    MongodbUtil.insertUserLog(o.getTrialsalePlanCategoryAnalysisSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-类目分析 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单-新品试销计划单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanCategoryAnalysisByPlan(List<Long> newproductTrialsalePlanSidList) {
        List<FrmTrialsalePlanCategoryAnalysis> itemList = frmTrialsalePlanCategoryAnalysisMapper.selectList(new QueryWrapper<FrmTrialsalePlanCategoryAnalysis>()
                .lambda().in(FrmTrialsalePlanCategoryAnalysis::getNewproductTrialsalePlanSid, newproductTrialsalePlanSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsalePlanCategoryAnalysisByList(itemList);
        }
        return row;
    }

}
