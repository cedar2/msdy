package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.SalSaleContractPayMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.PurPurchaseContractPayMethodMapper;
import com.platform.ems.domain.PurPurchaseContractPayMethod;
import com.platform.ems.service.IPurPurchaseContractPayMethodService;

/**
 * 采购合同信息-支付方式Service业务层处理
 *
 * @author chenkw
 * @date 2022-05-17
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseContractPayMethodServiceImpl extends ServiceImpl<PurPurchaseContractPayMethodMapper, PurPurchaseContractPayMethod> implements IPurPurchaseContractPayMethodService {
    @Autowired
    private PurPurchaseContractPayMethodMapper purPurchaseContractPayMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购合同信息-支付方式";

    /**
     * 查询采购合同信息-支付方式
     *
     * @param contractPayMethodSid 采购合同信息-支付方式ID
     * @return 采购合同信息-支付方式
     */
    @Override
    public PurPurchaseContractPayMethod selectPurPurchaseContractPayMethodById(Long contractPayMethodSid) {
        PurPurchaseContractPayMethod purPurchaseContractPayMethod = purPurchaseContractPayMethodMapper.selectPurPurchaseContractPayMethodById(contractPayMethodSid);
        MongodbUtil.find(purPurchaseContractPayMethod);
        return purPurchaseContractPayMethod;
    }

    /**
     * 查询采购合同信息-支付方式列表
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 采购合同信息-支付方式
     */
    @Override
    public List<PurPurchaseContractPayMethod> selectPurPurchaseContractPayMethodList(PurPurchaseContractPayMethod purPurchaseContractPayMethod) {
        return purPurchaseContractPayMethodMapper.selectPurPurchaseContractPayMethodList(purPurchaseContractPayMethod);
    }

    /**
     * 新增采购合同信息-支付方式
     * 需要注意编码重复校验
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseContractPayMethod(PurPurchaseContractPayMethod purPurchaseContractPayMethod) {
        int row = purPurchaseContractPayMethodMapper.insert(purPurchaseContractPayMethod);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PurPurchaseContractPayMethod(), purPurchaseContractPayMethod);
            MongodbUtil.insertUserLog(purPurchaseContractPayMethod.getContractPayMethodSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改采购合同信息-支付方式
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseContractPayMethod(PurPurchaseContractPayMethod purPurchaseContractPayMethod) {
        PurPurchaseContractPayMethod response = purPurchaseContractPayMethodMapper.selectPurPurchaseContractPayMethodById(purPurchaseContractPayMethod.getContractPayMethodSid());
        int row = purPurchaseContractPayMethodMapper.updateById(purPurchaseContractPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purPurchaseContractPayMethod.getContractPayMethodSid(), BusinessType.UPDATE.getValue(), response, purPurchaseContractPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 变更采购合同信息-支付方式
     *
     * @param purPurchaseContractPayMethod 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurPurchaseContractPayMethod(PurPurchaseContractPayMethod purPurchaseContractPayMethod) {
        PurPurchaseContractPayMethod response = purPurchaseContractPayMethodMapper.selectPurPurchaseContractPayMethodById(purPurchaseContractPayMethod.getContractPayMethodSid());
        int row = purPurchaseContractPayMethodMapper.updateAllById(purPurchaseContractPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purPurchaseContractPayMethod.getContractPayMethodSid(), BusinessType.CHANGE.getValue(), response, purPurchaseContractPayMethod, TITLE);
        }
        return row;
    }


    /**
     * 批量删除采购合同信息-支付方式
     *
     * @param contractPayMethodSids 需要删除的采购合同信息-支付方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseContractPayMethodByIds(List<Long> contractPayMethodSids) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(contractPayMethodSids)) {
            List<PurPurchaseContractPayMethod> oldList = purPurchaseContractPayMethodMapper.selectList(new QueryWrapper<PurPurchaseContractPayMethod>()
                    .lambda().in(PurPurchaseContractPayMethod::getContractPayMethodSidList, contractPayMethodSids));
            row = purPurchaseContractPayMethodMapper.deleteBatchIds(contractPayMethodSids);
            //插入日志
            oldList.forEach(item -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(item, new PurPurchaseContractPayMethod());
                MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 通过合同查询采购合同信息-支付方式
     *
     * @param contractSid 采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PurPurchaseContractPayMethod> selectPurPurchaseContractPayMethodListByContract(Long contractSid) {
        List<PurPurchaseContractPayMethod> list = purPurchaseContractPayMethodMapper.selectPurPurchaseContractPayMethodList(new PurPurchaseContractPayMethod().setPurchaseContractSid(contractSid));
        if (CollectionUtil.isNotEmpty(list)){
            list.forEach(item->{
                item.setRate(new BigDecimal(100).multiply(item.getRate()==null?new BigDecimal(0):item.getRate()).setScale(2, RoundingMode.HALF_UP));
                MongodbUtil.find(item);
            });
        }
        return list;
    }

    /**
     * 验证数据
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    private void verify(Long contractSid, List<PurPurchaseContractPayMethod> list) {
        BigDecimal yus = BigDecimal.ZERO;
        BigDecimal zhongq = BigDecimal.ZERO;
        BigDecimal weiq = BigDecimal.ZERO;
        for (int i = 0; i < list.size(); i++) {
            if (null == list.get(i).getRate()){
                throw new BaseException("占比不能为空");
            }
            if (BigDecimal.ZERO.compareTo(list.get(i).getRate()) == 0){
                throw new BaseException("占比不可以为0");
            }
            if (ConstantsFinance.ACCOUNT_CAT_YSFK.equals(list.get(i).getAccountCategory())) {
                yus = yus.add(list.get(i).getRate());
            } else if (ConstantsFinance.ACCOUNT_CAT_ZQK.equals(list.get(i).getAccountCategory())) {
                zhongq = zhongq.add(list.get(i).getRate());
            } else if (ConstantsFinance.ACCOUNT_CAT_WK.equals(list.get(i).getAccountCategory())) {
                weiq = weiq.add(list.get(i).getRate());
            } else {
            }
            // 处理数据
            list.get(i).setPurchaseContractSid(contractSid);
            if (list.get(i).getRate() != null){
                list.get(i).setRate(list.get(i).getRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
            }
        }
        String s = "";
        if (yus.compareTo(new BigDecimal(100)) > 0) {
            s = "预付款，";
        }
        if (zhongq.compareTo(new BigDecimal(100)) > 0) {
            s = s + "中期款，";
        }
        if (weiq.compareTo(new BigDecimal(100)) > 0) {
            s = s + "尾款，";
        }
        if (StrUtil.isNotBlank(s) && s.endsWith("，")) {
            s = s.substring(0, s.length() - 1);
            s = s + "占比之和大于1，请核实";
            throw new BaseException(s);
        }
    }

    /**
     * 批量新增采购合同信息-支付方式
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseContractPayMethodList(Long contractSid, List<PurPurchaseContractPayMethod> list) {
        int row = 0;
        if (list.size() == 0) { return row; }
        verify(contractSid, list);
        return insertPurPurchaseContractPayMethodList(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseContractPayMethodList(List<PurPurchaseContractPayMethod> list) {
        int row = 0;
        if (list.size() == 0) {
            return row;
        }
        row = purPurchaseContractPayMethodMapper.inserts(list);
        if (row > 0) {
            //插入日志
            list.forEach(item -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new PurPurchaseContractPayMethod(), item);
                MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 通过合同批量删除采购合同信息-支付方式
     *
     * @param contractSids 需要删除的采购合同信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseContractPayMethodByContract(List<Long> contractSidList) {
        int row = 0;
        List<PurPurchaseContractPayMethod> oldList = purPurchaseContractPayMethodMapper.selectList(new QueryWrapper<PurPurchaseContractPayMethod>()
                .lambda().in(PurPurchaseContractPayMethod::getPurchaseContractSid, contractSidList));
        row = purPurchaseContractPayMethodMapper.delete(new QueryWrapper<PurPurchaseContractPayMethod>()
                .lambda().in(PurPurchaseContractPayMethod::getPurchaseContractSid, contractSidList));
        //插入日志
        oldList.forEach(item -> {
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(item, new PurPurchaseContractPayMethod());
            MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return row;
    }

    /**
     * 通过合同批量修改采购合同信息-支付方式
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurPurchaseContractPayMethodList(Long contractSid, List<PurPurchaseContractPayMethod> list) {
        if (contractSid == null) {
            return;
        }
        int del = 0, add = 0, upd = 0;
        List<Long> contractSidList = new ArrayList<Long>() {{
            add(contractSid);
        }};
        if (CollectionUtil.isEmpty(list)) {
            del = deletePurPurchaseContractPayMethodByContract(contractSidList);
            return;
        }
        verify(contractSid,list);
        List<PurPurchaseContractPayMethod> oldList = purPurchaseContractPayMethodMapper.selectList(new QueryWrapper<PurPurchaseContractPayMethod>()
                .lambda().in(PurPurchaseContractPayMethod::getPurchaseContractSid, contractSidList));
        Map<Long, PurPurchaseContractPayMethod> oldMaps = oldList.stream().collect(Collectors.toMap(PurPurchaseContractPayMethod::getContractPayMethodSid, Function.identity()));
        if (CollectionUtils.isNotEmpty(oldList)) {
            //原有数据ids
            List<Long> originalIds = oldList.stream().map(PurPurchaseContractPayMethod::getContractPayMethodSid).collect(Collectors.toList());
            //还存在的数据
            List<PurPurchaseContractPayMethod> updateList = list.stream().filter(o -> o.getContractPayMethodSid() != null).collect(Collectors.toList());
            //更改还存在的数据
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> currentIds = updateList.stream().map(PurPurchaseContractPayMethod::getContractPayMethodSid).collect(Collectors.toList());
                //清空删除的数据
                List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(result)) {
                    del = purPurchaseContractPayMethodMapper.deleteBatchIds(result);
                    //插入日志
                    result.forEach(sid -> {
                        PurPurchaseContractPayMethod temp = oldMaps.get(sid);
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(temp, new PurPurchaseContractPayMethod());
                        MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
                    });
                }
                //找出与旧的不同
                updateList.forEach(item -> {
                    PurPurchaseContractPayMethod temp = oldMaps.get(item.getContractPayMethodSid());
                    if (temp != null) {
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(temp, item);
                        if (msgList.size() > 0) {
                            item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            item.setUpdateDate(new Date());
                            purPurchaseContractPayMethodMapper.updateAllById(item);
                            MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, null);
                        }
                    }
                });
                upd = updateList.size();
            } else {
                del = deletePurPurchaseContractPayMethodByContract(contractSidList);
            }
            //新增数据
            List<PurPurchaseContractPayMethod> newList = list.stream().filter(o -> o.getContractPayMethodSid() == null).collect(Collectors.toList());
            add = insertPurPurchaseContractPayMethodList(newList);
        } else {
            add = insertPurPurchaseContractPayMethodList(list);
        }
    }


    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    public String submitVerifyById(Long contractSid){
        String s = "";
        List<PurPurchaseContractPayMethod> listYusd = purPurchaseContractPayMethodMapper.selectList(new QueryWrapper<PurPurchaseContractPayMethod>()
                .lambda().eq(PurPurchaseContractPayMethod::getPurchaseContractSid, contractSid).eq(PurPurchaseContractPayMethod::getAccountCategory, ConstantsFinance.ACCOUNT_CAT_YSFK));
        if (CollectionUtil.isNotEmpty(listYusd)){
            s = s + submitVerify2(listYusd, ConstantsFinance.ACCOUNT_CAT_YSFK);
        }
        List<PurPurchaseContractPayMethod> listZq = purPurchaseContractPayMethodMapper.selectList(new QueryWrapper<PurPurchaseContractPayMethod>()
                .lambda().eq(PurPurchaseContractPayMethod::getPurchaseContractSid, contractSid).eq(PurPurchaseContractPayMethod::getAccountCategory, ConstantsFinance.ACCOUNT_CAT_ZQK));
        if (CollectionUtil.isNotEmpty(listZq)){
            s = s + submitVerify2(listZq, ConstantsFinance.ACCOUNT_CAT_ZQK);
        }
        List<PurPurchaseContractPayMethod> listWq = purPurchaseContractPayMethodMapper.selectList(new QueryWrapper<PurPurchaseContractPayMethod>()
                .lambda().eq(PurPurchaseContractPayMethod::getPurchaseContractSid, contractSid).eq(PurPurchaseContractPayMethod::getAccountCategory, ConstantsFinance.ACCOUNT_CAT_WK));
        if (CollectionUtil.isNotEmpty(listWq)){
            s = s + submitVerify2(listWq, ConstantsFinance.ACCOUNT_CAT_WK);
        }
        if (StrUtil.isNotBlank(s) && s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
            s = s + "支付方式的明细和不为100%";
        }
        return s;
    }

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    public String submitVerify(List<PurPurchaseContractPayMethod> list, String category){
        String name = "";
        if (CollectionUtil.isNotEmpty(list)){
            BigDecimal amountTax = BigDecimal.ZERO;
            for (int i = 0; i < list.size(); i++) {
                amountTax = amountTax.add(list.get(i).getRate());
            }
            if (amountTax.compareTo(new BigDecimal(100)) != 0) {
                if (ConstantsFinance.ACCOUNT_CAT_YSFK.equals(category)){
                    name = "预付款,";
                }
                else if (ConstantsFinance.ACCOUNT_CAT_ZQK.equals(category)){
                    name = "中期款,";
                }
                else if (ConstantsFinance.ACCOUNT_CAT_WK.equals(category)){
                    name = "尾款,";
                }
            }
        }
        return name;
    }

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param list 采购合同信息-支付方式
     * @return 结果
     */
    @Override
    public String submitVerify2(List<PurPurchaseContractPayMethod> list, String category){
        String name = "";
        if (CollectionUtil.isNotEmpty(list)){
            BigDecimal amountTax = BigDecimal.ZERO;
            for (int i = 0; i < list.size(); i++) {
                amountTax = amountTax.add(list.get(i).getRate());
            }
            if (amountTax.compareTo(new BigDecimal(1)) != 0) {
                if (ConstantsFinance.ACCOUNT_CAT_YSFK.equals(category)){
                    name = "预付款,";
                }
                else if (ConstantsFinance.ACCOUNT_CAT_ZQK.equals(category)){
                    name = "中期款,";
                }
                else if (ConstantsFinance.ACCOUNT_CAT_WK.equals(category)){
                    name = "尾款,";
                }
            }
        }
        return name;
    }
}
