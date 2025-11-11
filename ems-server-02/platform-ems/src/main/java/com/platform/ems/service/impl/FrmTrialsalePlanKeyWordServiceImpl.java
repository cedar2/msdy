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
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanCategoryAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FrmTrialsalePlanKeyWordMapper;
import com.platform.ems.domain.FrmTrialsalePlanKeyWord;
import com.platform.ems.service.IFrmTrialsalePlanKeyWordService;

/**
 * 新品试销计划单-关键词分析Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsalePlanKeyWordServiceImpl extends ServiceImpl<FrmTrialsalePlanKeyWordMapper, FrmTrialsalePlanKeyWord> implements IFrmTrialsalePlanKeyWordService {
    @Autowired
    private FrmTrialsalePlanKeyWordMapper frmTrialsalePlanKeyWordMapper;

    private static final String TITLE = "新品试销计划单-关键词分析";

    /**
     * 查询新品试销计划单-关键词分析
     *
     * @param trialsalePlanKeyWordSid 新品试销计划单-关键词分析ID
     * @return 新品试销计划单-关键词分析
     */
    @Override
    public FrmTrialsalePlanKeyWord selectFrmTrialsalePlanKeyWordById(Long trialsalePlanKeyWordSid) {
        FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord = frmTrialsalePlanKeyWordMapper.selectFrmTrialsalePlanKeyWordById(trialsalePlanKeyWordSid);
        MongodbUtil.find(frmTrialsalePlanKeyWord);
        return frmTrialsalePlanKeyWord;
    }

    /**
     * 查询新品试销计划单-关键词分析列表
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 新品试销计划单-关键词分析
     */
    @Override
    public List<FrmTrialsalePlanKeyWord> selectFrmTrialsalePlanKeyWordList(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord) {
        return frmTrialsalePlanKeyWordMapper.selectFrmTrialsalePlanKeyWordList(frmTrialsalePlanKeyWord);
    }

    /**
     * 新增新品试销计划单-关键词分析
     * 需要注意编码重复校验
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanKeyWord(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord) {
        int row = frmTrialsalePlanKeyWordMapper.insert(frmTrialsalePlanKeyWord);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsalePlanKeyWord(), frmTrialsalePlanKeyWord);
            MongodbUtil.insertUserLog(frmTrialsalePlanKeyWord.getTrialsalePlanKeyWordSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改新品试销计划单-关键词分析
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanKeyWord(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord) {
        FrmTrialsalePlanKeyWord original = frmTrialsalePlanKeyWordMapper.selectFrmTrialsalePlanKeyWordById(frmTrialsalePlanKeyWord.getTrialsalePlanKeyWordSid());
        int row = frmTrialsalePlanKeyWordMapper.updateById(frmTrialsalePlanKeyWord);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, frmTrialsalePlanKeyWord);
            MongodbUtil.insertUserLog(frmTrialsalePlanKeyWord.getTrialsalePlanKeyWordSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更新品试销计划单-关键词分析
     *
     * @param frmTrialsalePlanKeyWord 新品试销计划单-关键词分析
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsalePlanKeyWord(FrmTrialsalePlanKeyWord frmTrialsalePlanKeyWord) {
        FrmTrialsalePlanKeyWord response = frmTrialsalePlanKeyWordMapper.selectFrmTrialsalePlanKeyWordById(frmTrialsalePlanKeyWord.getTrialsalePlanKeyWordSid());
        int row = frmTrialsalePlanKeyWordMapper.updateAllById(frmTrialsalePlanKeyWord);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmTrialsalePlanKeyWord.getTrialsalePlanKeyWordSid(), BusinessType.CHANGE.getValue(), response, frmTrialsalePlanKeyWord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-关键词分析
     *
     * @param trialsalePlanKeyWordSids 需要删除的新品试销计划单-关键词分析ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanKeyWordByIds(List<Long> trialsalePlanKeyWordSids) {
        List<FrmTrialsalePlanKeyWord> list = frmTrialsalePlanKeyWordMapper.selectList(new QueryWrapper<FrmTrialsalePlanKeyWord>()
                .lambda().in(FrmTrialsalePlanKeyWord::getTrialsalePlanKeyWordSid, trialsalePlanKeyWordSids));
        int row = frmTrialsalePlanKeyWordMapper.deleteBatchIds(trialsalePlanKeyWordSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsalePlanKeyWord());
                MongodbUtil.insertUserLog(o.getTrialsalePlanKeyWordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 查询新品试销计划单-关键词分析
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-新品试销计划单-主表ID
     * @return 新品试销计划单-关键词分析
     */
    @Override
    public List<FrmTrialsalePlanKeyWord> selectFrmTrialsalePlanKeyWordListById(Long newproductTrialsalePlanSid) {
        List<FrmTrialsalePlanKeyWord> trialsalePlanCategoryAnalysisList = frmTrialsalePlanKeyWordMapper
                .selectFrmTrialsalePlanKeyWordList(new FrmTrialsalePlanKeyWord()
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
     * 批量新增新品试销计划单-关键词分析
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsalePlanKeyWordList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanKeyWord> list = newproductTrialsalePlan.getKeyWordList();
        if (CollectionUtil.isNotEmpty(list)) {
            FrmTrialsalePlanKeyWord item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setNewproductTrialsalePlanSid(newproductTrialsalePlan.getNewproductTrialsalePlanSid());
                row += insertFrmTrialsalePlanKeyWord(item);
            }
        }
        return row;
    }

    /**
     * 批量修改新品试销计划单-关键词分析
     *
     * @param newproductTrialsalePlan 新品试销计划单-新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsalePlanKeyWordList(FrmNewproductTrialsalePlan newproductTrialsalePlan) {
        int row = 0;
        List<FrmTrialsalePlanKeyWord> list = newproductTrialsalePlan.getKeyWordList();
        // 原本明细
        List<FrmTrialsalePlanKeyWord> oldList = frmTrialsalePlanKeyWordMapper.selectList(new QueryWrapper<FrmTrialsalePlanKeyWord>()
                .lambda().eq(FrmTrialsalePlanKeyWord::getNewproductTrialsalePlanSid, newproductTrialsalePlan.getNewproductTrialsalePlanSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<FrmTrialsalePlanKeyWord> newList = list.stream().filter(o -> o.getTrialsalePlanKeyWordSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                newproductTrialsalePlan.setKeyWordList(newList);
                insertFrmTrialsalePlanKeyWordList(newproductTrialsalePlan);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FrmTrialsalePlanKeyWord> updateList = list.stream().filter(o -> o.getTrialsalePlanKeyWordSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FrmTrialsalePlanKeyWord::getTrialsalePlanKeyWordSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FrmTrialsalePlanKeyWord> map = oldList.stream().collect(Collectors.toMap(FrmTrialsalePlanKeyWord::getTrialsalePlanKeyWordSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTrialsalePlanKeyWordSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTrialsalePlanKeyWordSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            frmTrialsalePlanKeyWordMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTrialsalePlanKeyWordSid(), newproductTrialsalePlan.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<FrmTrialsalePlanKeyWord> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTrialsalePlanKeyWordSid())).collect(Collectors.toList());
                    deleteFrmTrialsalePlanKeyWordByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteFrmTrialsalePlanKeyWordByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-关键词分析
     *
     * @param itemList 需要删除的新品试销计划单-新品试销计划单-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanKeyWordByList(List<FrmTrialsalePlanKeyWord> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> trialsalePlanCategoryAnalysisSidList = itemList.stream().filter(o -> o.getTrialsalePlanKeyWordSid() != null)
                .map(FrmTrialsalePlanKeyWord::getTrialsalePlanKeyWordSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(trialsalePlanCategoryAnalysisSidList)) {
            row = frmTrialsalePlanKeyWordMapper.deleteBatchIds(trialsalePlanCategoryAnalysisSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new FrmTrialsalePlanKeyWord());
                    MongodbUtil.insertUserLog(o.getTrialsalePlanKeyWordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单-关键词分析 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单-新品试销计划单sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsalePlanKeyWordByPlan(List<Long> newproductTrialsalePlanSidList) {
        List<FrmTrialsalePlanKeyWord> itemList = frmTrialsalePlanKeyWordMapper.selectList(new QueryWrapper<FrmTrialsalePlanKeyWord>()
                .lambda().in(FrmTrialsalePlanKeyWord::getNewproductTrialsalePlanSid, newproductTrialsalePlanSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteFrmTrialsalePlanKeyWordByList(itemList);
        }
        return row;
    }
}
