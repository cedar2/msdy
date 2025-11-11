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
import com.platform.ems.mapper.FrmTrialsalePlanCpcSimulateMapper;
import com.platform.ems.domain.FrmTrialsalePlanCpcSimulate;
import com.platform.ems.service.IFrmTrialsalePlanCpcSimulateService;

/**
 * 新品试销计划单-CPC模拟数据Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsalePlanCpcSimulateServiceImpl extends ServiceImpl<FrmTrialsalePlanCpcSimulateMapper, FrmTrialsalePlanCpcSimulate> implements IFrmTrialsalePlanCpcSimulateService {
    @Autowired
    private FrmTrialsalePlanCpcSimulateMapper frmTrialsalePlanCpcSimulateMapper;

    private static final String TITLE = "新品试销计划单-CPC模拟数据";

    /**
     * 查询新品试销计划单-CPC模拟数据
     *
     * @param trialsalePlanCpcSimulationSid 新品试销计划单-CPC模拟数据ID
     * @return 新品试销计划单-CPC模拟数据
     */
    @Override
    public FrmTrialsalePlanCpcSimulate selectFrmTrialsalePlanCpcSimulateById(Long trialsalePlanCpcSimulationSid) {
        FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate = frmTrialsalePlanCpcSimulateMapper.selectFrmTrialsalePlanCpcSimulateById(trialsalePlanCpcSimulationSid);
        MongodbUtil.find(frmTrialsalePlanCpcSimulate);
        return frmTrialsalePlanCpcSimulate;
    }

    /**
     * 查询新品试销计划单-CPC模拟数据列表
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 新品试销计划单-CPC模拟数据
     */
    @Override
    public List<FrmTrialsalePlanCpcSimulate> selectFrmTrialsalePlanCpcSimulateList(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate) {
        return frmTrialsalePlanCpcSimulateMapper.selectFrmTrialsalePlanCpcSimulateList(frmTrialsalePlanCpcSimulate);
    }

    /**
     * 新增新品试销计划单-CPC模拟数据
     * 需要注意编码重复校验
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanCpcSimulate(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate) {
        frmTrialsalePlanCpcSimulate.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        int row = frmTrialsalePlanCpcSimulateMapper.insert(frmTrialsalePlanCpcSimulate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsalePlanCpcSimulate(), frmTrialsalePlanCpcSimulate);
            MongodbUtil.insertUserLog(frmTrialsalePlanCpcSimulate.getTrialsalePlanCpcSimulationSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改新品试销计划单-CPC模拟数据
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanCpcSimulate(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate) {
        FrmTrialsalePlanCpcSimulate original = frmTrialsalePlanCpcSimulateMapper.selectFrmTrialsalePlanCpcSimulateById(frmTrialsalePlanCpcSimulate.getTrialsalePlanCpcSimulationSid());
        int row = frmTrialsalePlanCpcSimulateMapper.updateById(frmTrialsalePlanCpcSimulate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsalePlanCpcSimulate);
            MongodbUtil.insertUserLog(frmTrialsalePlanCpcSimulate.getTrialsalePlanCpcSimulationSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更新品试销计划单-CPC模拟数据
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsalePlanCpcSimulate(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate) {
        FrmTrialsalePlanCpcSimulate response = frmTrialsalePlanCpcSimulateMapper.selectFrmTrialsalePlanCpcSimulateById(frmTrialsalePlanCpcSimulate.getTrialsalePlanCpcSimulationSid());
        int row = frmTrialsalePlanCpcSimulateMapper.updateAllById(frmTrialsalePlanCpcSimulate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsalePlanCpcSimulate.getTrialsalePlanCpcSimulationSid(), BusinessType.CHANGE.getValue(), response, frmTrialsalePlanCpcSimulate, TITLE);
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-CPC模拟数据
     *
     * @param trialsalePlanCpcSimulationSids 需要删除的新品试销计划单-CPC模拟数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanCpcSimulateByIds(List<Long> trialsalePlanCpcSimulationSids) {
        List<FrmTrialsalePlanCpcSimulate> list = frmTrialsalePlanCpcSimulateMapper.selectList(new QueryWrapper<FrmTrialsalePlanCpcSimulate>()
                .lambda().in(FrmTrialsalePlanCpcSimulate::getTrialsalePlanCpcSimulationSid, trialsalePlanCpcSimulationSids));
        int row = frmTrialsalePlanCpcSimulateMapper.deleteBatchIds(trialsalePlanCpcSimulationSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsalePlanCpcSimulate());
                MongodbUtil.insertUserLog(o.getTrialsalePlanCpcSimulationSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 查询新品试销计划单-CPC模拟数据
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-新品试销计划单-主表ID
     * @return 新品试销计划单-CPC模拟数据
     */
    @Override
    public List<FrmTrialsalePlanCpcSimulate> selectFrmTrialsalePlanCpcSimulateListById(Long newproductTrialsalePlanSid) {
        List<FrmTrialsalePlanCpcSimulate> trialsalePlanCategoryAnalysisList = frmTrialsalePlanCpcSimulateMapper
                .selectFrmTrialsalePlanCpcSimulateList(new FrmTrialsalePlanCpcSimulate()
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
     * 批量新增新品试销计划单-CPC模拟数据
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanCpcSimulateList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanCpcSimulate> list = newproductTrialsalePlan.getCpcSimulateList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsalePlanCpcSimulate item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setNewproductTrialsalePlanSid(newproductTrialsalePlan.getNewproductTrialsalePlanSid());
                row += insertFrmTrialsalePlanCpcSimulate(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销计划单-CPC模拟数据
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanCpcSimulateList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanCpcSimulate> list = newproductTrialsalePlan.getCpcSimulateList();
        // 原本明细
        List<FrmTrialsalePlanCpcSimulate> oldList = frmTrialsalePlanCpcSimulateMapper.selectList(new QueryWrapper<FrmTrialsalePlanCpcSimulate>()
                .lambda().eq(FrmTrialsalePlanCpcSimulate::getNewproductTrialsalePlanSid, newproductTrialsalePlan.getNewproductTrialsalePlanSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsalePlanCpcSimulate> newList = list.stream().filter(o -> o.getTrialsalePlanCpcSimulationSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                newproductTrialsalePlan.setCpcSimulateList(newList);
                insertFrmTrialsalePlanCpcSimulateList(newproductTrialsalePlan);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsalePlanCpcSimulate> updateList = list.stream().filter(o -> o.getTrialsalePlanCpcSimulationSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsalePlanCpcSimulate::getTrialsalePlanCpcSimulationSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsalePlanCpcSimulate> map = oldList.stream().collect(Collectors.toMap(FrmTrialsalePlanCpcSimulate::getTrialsalePlanCpcSimulationSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsalePlanCpcSimulationSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsalePlanCpcSimulationSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsalePlanCpcSimulateMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsalePlanCpcSimulationSid(), newproductTrialsalePlan.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsalePlanCpcSimulate> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsalePlanCpcSimulationSid())).collect(Collectors.toList());
                    deleteFrmTrialsalePlanCpcSimulateByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsalePlanCpcSimulateByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-CPC模拟数据
     *
     * @param itemList 需要删除的新品试销计划单-新品试销计划单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanCpcSimulateByList(List<FrmTrialsalePlanCpcSimulate> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsalePlanCpcSimulationSid() != null)
                .map(FrmTrialsalePlanCpcSimulate::getTrialsalePlanCpcSimulationSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsalePlanCpcSimulateMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsalePlanCpcSimulate());
                    MongodbUtil.insertUserLog(o.getTrialsalePlanCpcSimulationSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-CPC模拟数据 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单-新品试销计划单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanCpcSimulateByPlan(List<Long> newproductTrialsalePlanSidList) {
        List<FrmTrialsalePlanCpcSimulate> itemList = frmTrialsalePlanCpcSimulateMapper.selectList(new QueryWrapper<FrmTrialsalePlanCpcSimulate>()
                .lambda().in(FrmTrialsalePlanCpcSimulate::getNewproductTrialsalePlanSid, newproductTrialsalePlanSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsalePlanCpcSimulateByList(itemList);
        }
        return row;
    }

}
