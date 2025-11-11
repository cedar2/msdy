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
import com.platform.ems.mapper.FrmTrialsaleResultPriceSchemeMapper;
import com.platform.ems.domain.FrmTrialsaleResultPriceScheme;
import com.platform.ems.service.IFrmTrialsaleResultPriceSchemeService;

/**
 * 试销结果单-定价方案Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsaleResultPriceSchemeServiceImpl extends ServiceImpl<FrmTrialsaleResultPriceSchemeMapper, FrmTrialsaleResultPriceScheme> implements IFrmTrialsaleResultPriceSchemeService {
    @Autowired
    private FrmTrialsaleResultPriceSchemeMapper frmTrialsaleResultPriceSchemeMapper;

    private static final String TITLE = "试销结果单-定价方案";

    /**
     * 查询试销结果单-定价方案
     *
     * @param trialsaleResultPriceSchemeSid 试销结果单-定价方案ID
     * @return 试销结果单-定价方案
     */
    @Override
    public FrmTrialsaleResultPriceScheme selectFrmTrialsaleResultPriceSchemeById(Long trialsaleResultPriceSchemeSid) {
        FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme = frmTrialsaleResultPriceSchemeMapper.selectFrmTrialsaleResultPriceSchemeById(trialsaleResultPriceSchemeSid);
        MongodbUtil.find(frmTrialsaleResultPriceScheme);
        return frmTrialsaleResultPriceScheme;
    }

    /**
     * 查询试销结果单-定价方案列表
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 试销结果单-定价方案
     */
    @Override
    public List<FrmTrialsaleResultPriceScheme> selectFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme) {
        return frmTrialsaleResultPriceSchemeMapper.selectFrmTrialsaleResultPriceSchemeList(frmTrialsaleResultPriceScheme);
    }

    /**
     * 新增试销结果单-定价方案
     * 需要注意编码重复校验
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResultPriceScheme(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme) {
        frmTrialsaleResultPriceScheme.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        int row = frmTrialsaleResultPriceSchemeMapper.insert(frmTrialsaleResultPriceScheme);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsaleResultPriceScheme(), frmTrialsaleResultPriceScheme);
            MongodbUtil.insertUserLog(frmTrialsaleResultPriceScheme.getTrialsaleResultPriceSchemeSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改试销结果单-定价方案
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResultPriceScheme(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme) {
        FrmTrialsaleResultPriceScheme original = frmTrialsaleResultPriceSchemeMapper.selectFrmTrialsaleResultPriceSchemeById(frmTrialsaleResultPriceScheme.getTrialsaleResultPriceSchemeSid());
        int row = frmTrialsaleResultPriceSchemeMapper.updateById(frmTrialsaleResultPriceScheme);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsaleResultPriceScheme);
            MongodbUtil.insertUserLog(frmTrialsaleResultPriceScheme.getTrialsaleResultPriceSchemeSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更试销结果单-定价方案
     *
     * @param frmTrialsaleResultPriceScheme 试销结果单-定价方案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsaleResultPriceScheme(FrmTrialsaleResultPriceScheme frmTrialsaleResultPriceScheme) {
        FrmTrialsaleResultPriceScheme response = frmTrialsaleResultPriceSchemeMapper.selectFrmTrialsaleResultPriceSchemeById(frmTrialsaleResultPriceScheme.getTrialsaleResultPriceSchemeSid());
        int row = frmTrialsaleResultPriceSchemeMapper.updateAllById(frmTrialsaleResultPriceScheme);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsaleResultPriceScheme.getTrialsaleResultPriceSchemeSid(), BusinessType.CHANGE.getValue(), response, frmTrialsaleResultPriceScheme, TITLE);
        }
        return row;
    }

    /**
     * 批量删除试销结果单-定价方案
     *
     * @param trialsaleResultPriceSchemeSids 需要删除的试销结果单-定价方案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultPriceSchemeByIds(List<Long> trialsaleResultPriceSchemeSids) {
        List<FrmTrialsaleResultPriceScheme> list = frmTrialsaleResultPriceSchemeMapper.selectList(new QueryWrapper<FrmTrialsaleResultPriceScheme>()
                .lambda().in(FrmTrialsaleResultPriceScheme::getTrialsaleResultPriceSchemeSid, trialsaleResultPriceSchemeSids));
        int row = frmTrialsaleResultPriceSchemeMapper.deleteBatchIds(trialsaleResultPriceSchemeSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsaleResultPriceScheme());
                MongodbUtil.insertUserLog(o.getTrialsaleResultPriceSchemeSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 查询新品试销结果单-定价方案
     *
     * @param trialsaleResultSid 新品试销结果单-新品试销结果单-主表ID
     * @return 新品试销结果单-定价方案
     */
    @Override
    public List<FrmTrialsaleResultPriceScheme> selectFrmTrialsaleResultPriceSchemeListById(Long trialsaleResultSid) {
        List<FrmTrialsaleResultPriceScheme> trialsalePlanCategoryAnalysisList = frmTrialsaleResultPriceSchemeMapper
                .selectFrmTrialsaleResultPriceSchemeList(new FrmTrialsaleResultPriceScheme()
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
     * 批量新增新品试销结果单-定价方案
     *
     * @param trialsaleResult 新品试销结果单-新品试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResult trialsaleResult) {
        int row = 0;
        List<FrmTrialsaleResultPriceScheme> list = trialsaleResult.getPriceSchemeList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsaleResultPriceScheme item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setTrialsaleResultSid(trialsaleResult.getTrialsaleResultSid());
                row += insertFrmTrialsaleResultPriceScheme(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销结果单-定价方案
     *
     * @param trialsaleResult 新品试销结果单-新品试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResultPriceSchemeList(FrmTrialsaleResult trialsaleResult) {
        int row = 0;
        List<FrmTrialsaleResultPriceScheme> list = trialsaleResult.getPriceSchemeList();
        // 原本明细
        List<FrmTrialsaleResultPriceScheme> oldList = frmTrialsaleResultPriceSchemeMapper.selectList(new QueryWrapper<FrmTrialsaleResultPriceScheme>()
                .lambda().eq(FrmTrialsaleResultPriceScheme::getTrialsaleResultSid, trialsaleResult.getTrialsaleResultSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsaleResultPriceScheme> newList = list.stream().filter(o -> o.getTrialsaleResultPriceSchemeSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                trialsaleResult.setPriceSchemeList(newList);
                insertFrmTrialsaleResultPriceSchemeList(trialsaleResult);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsaleResultPriceScheme> updateList = list.stream().filter(o -> o.getTrialsaleResultPriceSchemeSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsaleResultPriceScheme::getTrialsaleResultPriceSchemeSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsaleResultPriceScheme> map = oldList.stream().collect(Collectors.toMap(FrmTrialsaleResultPriceScheme::getTrialsaleResultPriceSchemeSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsaleResultPriceSchemeSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsaleResultPriceSchemeSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsaleResultPriceSchemeMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsaleResultPriceSchemeSid(), trialsaleResult.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsaleResultPriceScheme> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsaleResultPriceSchemeSid())).collect(Collectors.toList());
                    deleteFrmTrialsaleResultPriceSchemeByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsaleResultPriceSchemeByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销结果单-定价方案
     *
     * @param itemList 需要删除的新品试销结果单-新品试销结果单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultPriceSchemeByList(List<FrmTrialsaleResultPriceScheme> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsaleResultPriceSchemeSid() != null)
                .map(FrmTrialsaleResultPriceScheme::getTrialsaleResultPriceSchemeSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsaleResultPriceSchemeMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsaleResultPriceScheme());
                    MongodbUtil.insertUserLog(o.getTrialsaleResultPriceSchemeSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销结果单-定价方案 根据主表sids
     *
     * @param trialsaleResultSidList 需要删除的新品试销结果单-新品试销结果单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultPriceSchemeByPlan(List<Long> trialsaleResultSidList) {
        List<FrmTrialsaleResultPriceScheme> itemList = frmTrialsaleResultPriceSchemeMapper.selectList(new QueryWrapper<FrmTrialsaleResultPriceScheme>()
                .lambda().in(FrmTrialsaleResultPriceScheme::getTrialsaleResultSid, trialsaleResultSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsaleResultPriceSchemeByList(itemList);
        }
        return row;
    }
}
