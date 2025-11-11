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
import com.platform.ems.domain.FrmTrialsaleResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FrmTrialsaleResultAdviceMapper;
import com.platform.ems.domain.FrmTrialsaleResultAdvice;
import com.platform.ems.service.IFrmTrialsaleResultAdviceService;

/**
 * 试销结果单-优化建议Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsaleResultAdviceServiceImpl extends ServiceImpl<FrmTrialsaleResultAdviceMapper, FrmTrialsaleResultAdvice> implements IFrmTrialsaleResultAdviceService {
    @Autowired
    private FrmTrialsaleResultAdviceMapper frmTrialsaleResultAdviceMapper;

    private static final String TITLE = "试销结果单-优化建议";

    /**
     * 查询试销结果单-优化建议
     *
     * @param trialsaleResultAdviceSid 试销结果单-优化建议ID
     * @return 试销结果单-优化建议
     */
    @Override
    public FrmTrialsaleResultAdvice selectFrmTrialsaleResultAdviceById(Long trialsaleResultAdviceSid) {
        FrmTrialsaleResultAdvice frmTrialsaleResultAdvice = frmTrialsaleResultAdviceMapper.selectFrmTrialsaleResultAdviceById(trialsaleResultAdviceSid);
        MongodbUtil.find(frmTrialsaleResultAdvice);
        return frmTrialsaleResultAdvice;
    }

    /**
     * 查询试销结果单-优化建议列表
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 试销结果单-优化建议
     */
    @Override
    public List<FrmTrialsaleResultAdvice> selectFrmTrialsaleResultAdviceList(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice) {
        return frmTrialsaleResultAdviceMapper.selectFrmTrialsaleResultAdviceList(frmTrialsaleResultAdvice);
    }

    /**
     * 新增试销结果单-优化建议
     * 需要注意编码重复校验
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResultAdvice(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice) {
        int row = frmTrialsaleResultAdviceMapper.insert(frmTrialsaleResultAdvice);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsaleResultAdvice(), frmTrialsaleResultAdvice);
            MongodbUtil.insertUserLog(frmTrialsaleResultAdvice.getTrialsaleResultAdviceSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改试销结果单-优化建议
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResultAdvice(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice) {
        FrmTrialsaleResultAdvice original = frmTrialsaleResultAdviceMapper.selectFrmTrialsaleResultAdviceById(frmTrialsaleResultAdvice.getTrialsaleResultAdviceSid());
        int row = frmTrialsaleResultAdviceMapper.updateById(frmTrialsaleResultAdvice);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsaleResultAdvice);
            MongodbUtil.insertUserLog(frmTrialsaleResultAdvice.getTrialsaleResultAdviceSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更试销结果单-优化建议
     *
     * @param frmTrialsaleResultAdvice 试销结果单-优化建议
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsaleResultAdvice(FrmTrialsaleResultAdvice frmTrialsaleResultAdvice) {
        FrmTrialsaleResultAdvice response = frmTrialsaleResultAdviceMapper.selectFrmTrialsaleResultAdviceById(frmTrialsaleResultAdvice.getTrialsaleResultAdviceSid());
        int row = frmTrialsaleResultAdviceMapper.updateAllById(frmTrialsaleResultAdvice);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsaleResultAdvice.getTrialsaleResultAdviceSid(), BusinessType.CHANGE.getValue(), response, frmTrialsaleResultAdvice, TITLE);
        }
        return row;
    }

    /**
     * 批量删除试销结果单-优化建议
     *
     * @param trialsaleResultAdviceSids 需要删除的试销结果单-优化建议ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultAdviceByIds(List<Long> trialsaleResultAdviceSids) {
        List<FrmTrialsaleResultAdvice> list = frmTrialsaleResultAdviceMapper.selectList(new QueryWrapper<FrmTrialsaleResultAdvice>()
                .lambda().in(FrmTrialsaleResultAdvice::getTrialsaleResultAdviceSid, trialsaleResultAdviceSids));
        int row = frmTrialsaleResultAdviceMapper.deleteBatchIds(trialsaleResultAdviceSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsaleResultAdvice());
                MongodbUtil.insertUserLog(o.getTrialsaleResultAdviceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 查询新品试销结果单-优化建议
     *
     * @param trialsaleResultSid 新品试销结果单-新品试销结果单-主表ID
     * @return 新品试销结果单-优化建议
     */
    @Override
    public List<FrmTrialsaleResultAdvice> selectFrmTrialsaleResultAdviceListById(Long trialsaleResultSid) {
        List<FrmTrialsaleResultAdvice> trialsalePlanCategoryAnalysisList = frmTrialsaleResultAdviceMapper
                .selectFrmTrialsaleResultAdviceList(new FrmTrialsaleResultAdvice()
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
     * 批量新增新品试销结果单-优化建议
     *
     * @param trialsaleResult 新品试销结果单-新品试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResultAdviceList(FrmTrialsaleResult trialsaleResult) {
        int row = 0;
        List<FrmTrialsaleResultAdvice> list = trialsaleResult.getAdviceList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsaleResultAdvice item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setTrialsaleResultSid(trialsaleResult.getTrialsaleResultSid());
                row += insertFrmTrialsaleResultAdvice(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销结果单-优化建议
     *
     * @param trialsaleResult 新品试销结果单-新品试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResultAdviceList(FrmTrialsaleResult trialsaleResult) {
        int row = 0;
        List<FrmTrialsaleResultAdvice> list = trialsaleResult.getAdviceList();
        // 原本明细
        List<FrmTrialsaleResultAdvice> oldList = frmTrialsaleResultAdviceMapper.selectList(new QueryWrapper<FrmTrialsaleResultAdvice>()
                .lambda().eq(FrmTrialsaleResultAdvice::getTrialsaleResultSid, trialsaleResult.getTrialsaleResultSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsaleResultAdvice> newList = list.stream().filter(o -> o.getTrialsaleResultAdviceSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                trialsaleResult.setAdviceList(newList);
                insertFrmTrialsaleResultAdviceList(trialsaleResult);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsaleResultAdvice> updateList = list.stream().filter(o -> o.getTrialsaleResultAdviceSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsaleResultAdvice::getTrialsaleResultAdviceSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsaleResultAdvice> map = oldList.stream().collect(Collectors.toMap(FrmTrialsaleResultAdvice::getTrialsaleResultAdviceSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsaleResultAdviceSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsaleResultAdviceSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsaleResultAdviceMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsaleResultAdviceSid(), trialsaleResult.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsaleResultAdvice> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsaleResultAdviceSid())).collect(Collectors.toList());
                    deleteFrmTrialsaleResultAdviceByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsaleResultAdviceByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销结果单-优化建议
     *
     * @param itemList 需要删除的新品试销结果单-新品试销结果单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultAdviceByList(List<FrmTrialsaleResultAdvice> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsaleResultAdviceSid() != null)
                .map(FrmTrialsaleResultAdvice::getTrialsaleResultAdviceSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsaleResultAdviceMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsaleResultAdvice());
                    MongodbUtil.insertUserLog(o.getTrialsaleResultAdviceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销结果单-优化建议 根据主表sids
     *
     * @param trialsaleResultSidList 需要删除的新品试销结果单-新品试销结果单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultAdviceByPlan(List<Long> trialsaleResultSidList) {
        List<FrmTrialsaleResultAdvice> itemList = frmTrialsaleResultAdviceMapper.selectList(new QueryWrapper<FrmTrialsaleResultAdvice>()
                .lambda().in(FrmTrialsaleResultAdvice::getTrialsaleResultSid, trialsaleResultSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsaleResultAdviceByList(itemList);
        }
        return row;
    }
}
