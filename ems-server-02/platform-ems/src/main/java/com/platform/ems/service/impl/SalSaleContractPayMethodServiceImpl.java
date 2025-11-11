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
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.SalSaleContractPayMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.SalSaleContractPayMethodMapper;
import com.platform.ems.service.ISalSaleContractPayMethodService;

/**
 * 销售合同信息-支付方式Service业务层处理
 *
 * @author chenkw
 * @date 2022-05-17
 */
@Service
@SuppressWarnings("all")
public class SalSaleContractPayMethodServiceImpl extends ServiceImpl<SalSaleContractPayMethodMapper, SalSaleContractPayMethod> implements ISalSaleContractPayMethodService {
    @Autowired
    private SalSaleContractPayMethodMapper salSaleContractPayMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "销售合同信息-支付方式";

    /**
     * 查询销售合同信息-支付方式
     *
     * @param contractPayMethodSid 销售合同信息-支付方式ID
     * @return 销售合同信息-支付方式
     */
    @Override
    public SalSaleContractPayMethod selectSalSaleContractPayMethodById(Long contractPayMethodSid) {
        SalSaleContractPayMethod salSaleContractPayMethod = salSaleContractPayMethodMapper.selectSalSaleContractPayMethodById(contractPayMethodSid);
        MongodbUtil.find(salSaleContractPayMethod);
        return salSaleContractPayMethod;
    }

    /**
     * 查询销售合同信息-支付方式列表
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 销售合同信息-支付方式
     */
    @Override
    public List<SalSaleContractPayMethod> selectSalSaleContractPayMethodList(SalSaleContractPayMethod salSaleContractPayMethod) {
        return salSaleContractPayMethodMapper.selectSalSaleContractPayMethodList(salSaleContractPayMethod);
    }

    /**
     * 新增销售合同信息-支付方式
     * 需要注意编码重复校验
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSaleContractPayMethod(SalSaleContractPayMethod salSaleContractPayMethod) {
        int row = salSaleContractPayMethodMapper.insert(salSaleContractPayMethod);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new SalSaleContractPayMethod(), salSaleContractPayMethod);
            MongodbUtil.insertUserLog(salSaleContractPayMethod.getContractPayMethodSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改销售合同信息-支付方式
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSaleContractPayMethod(SalSaleContractPayMethod salSaleContractPayMethod) {
        SalSaleContractPayMethod response = salSaleContractPayMethodMapper.selectSalSaleContractPayMethodById(salSaleContractPayMethod.getContractPayMethodSid());
        int row = salSaleContractPayMethodMapper.updateById(salSaleContractPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(salSaleContractPayMethod.getContractPayMethodSid(), BusinessType.UPDATE.getValue(), response, salSaleContractPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 变更销售合同信息-支付方式
     *
     * @param salSaleContractPayMethod 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalSaleContractPayMethod(SalSaleContractPayMethod salSaleContractPayMethod) {
        SalSaleContractPayMethod response = salSaleContractPayMethodMapper.selectSalSaleContractPayMethodById(salSaleContractPayMethod.getContractPayMethodSid());
        int row = salSaleContractPayMethodMapper.updateAllById(salSaleContractPayMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(salSaleContractPayMethod.getContractPayMethodSid(), BusinessType.CHANGE.getValue(), response, salSaleContractPayMethod, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售合同信息-支付方式
     *
     * @param contractPayMethodSids 需要删除的销售合同信息-支付方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSaleContractPayMethodByIds(List<Long> contractPayMethodSids) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(contractPayMethodSids)) {
            List<SalSaleContractPayMethod> oldList = salSaleContractPayMethodMapper.selectList(new QueryWrapper<SalSaleContractPayMethod>()
                    .lambda().in(SalSaleContractPayMethod::getContractPayMethodSidList, contractPayMethodSids));
            row = salSaleContractPayMethodMapper.deleteBatchIds(contractPayMethodSids);
            //插入日志
            oldList.forEach(item -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(item, new SalSaleContractPayMethod());
                MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 通过合同查询销售合同信息-支付方式
     *
     * @param contractSid 销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SalSaleContractPayMethod> selectSalSaleContractPayMethodListByContract(Long contractSid) {
        List<SalSaleContractPayMethod> list = salSaleContractPayMethodMapper.selectSalSaleContractPayMethodList(new SalSaleContractPayMethod().setSaleContractSid(contractSid));
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
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    private void verify(Long contractSid, List<SalSaleContractPayMethod> list) {
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
            list.get(i).setSaleContractSid(contractSid);
            if (list.get(i).getRate() != null){
                list.get(i).setRate(list.get(i).getRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
            }
        }
        String s = "";
        if (yus.compareTo(new BigDecimal(100)) > 0) {
            s = "预收款，";
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
     * 批量新增销售合同信息-支付方式
     *
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSaleContractPayMethodList(Long contractSid, List<SalSaleContractPayMethod> list) {
        int row = 0;
        if (list.size() == 0) { return row; }
        verify(contractSid, list);
        return insertSalSaleContractPayMethodList(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public int insertSalSaleContractPayMethodList(List<SalSaleContractPayMethod> list) {
        int row = 0;
        if (list.size() == 0) {
            return row;
        }
        row = salSaleContractPayMethodMapper.inserts(list);
        if (row > 0) {
            //插入日志
            list.forEach(item -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new SalSaleContractPayMethod(), item);
                MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 通过合同批量删除销售合同信息-支付方式
     *
     * @param contractSids 需要删除的销售合同信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSaleContractPayMethodByContract(List<Long> contractSidList) {
        int row = 0;
        List<SalSaleContractPayMethod> oldList = salSaleContractPayMethodMapper.selectList(new QueryWrapper<SalSaleContractPayMethod>()
                .lambda().in(SalSaleContractPayMethod::getSaleContractSid, contractSidList));
        row = salSaleContractPayMethodMapper.delete(new QueryWrapper<SalSaleContractPayMethod>()
                .lambda().in(SalSaleContractPayMethod::getSaleContractSid, contractSidList));
        //插入日志
        oldList.forEach(item -> {
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(item, new SalSaleContractPayMethod());
            MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return row;
    }

    /**
     * 通过合同批量修改销售合同信息-支付方式
     *
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSalSaleContractPayMethodList(Long contractSid, List<SalSaleContractPayMethod> list) {
        if (contractSid == null) {
            return;
        }
        int del = 0, add = 0, upd = 0;
        List<Long> contractSidList = new ArrayList<Long>() {{
            add(contractSid);
        }};
        if (CollectionUtil.isEmpty(list)) {
            del = deleteSalSaleContractPayMethodByContract(contractSidList);
            return;
        }
        verify(contractSid,list);
        List<SalSaleContractPayMethod> oldList = salSaleContractPayMethodMapper.selectList(new QueryWrapper<SalSaleContractPayMethod>()
                .lambda().in(SalSaleContractPayMethod::getSaleContractSid, contractSidList));
        Map<Long, SalSaleContractPayMethod> oldMaps = oldList.stream().collect(Collectors.toMap(SalSaleContractPayMethod::getContractPayMethodSid, Function.identity()));
        if (CollectionUtils.isNotEmpty(oldList)) {
            //原有数据ids
            List<Long> originalIds = oldList.stream().map(SalSaleContractPayMethod::getContractPayMethodSid).collect(Collectors.toList());
            //还存在的数据
            List<SalSaleContractPayMethod> updateList = list.stream().filter(o -> o.getContractPayMethodSid() != null).collect(Collectors.toList());
            //更改还存在的数据
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> currentIds = updateList.stream().map(SalSaleContractPayMethod::getContractPayMethodSid).collect(Collectors.toList());
                //清空删除的数据
                List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(result)) {
                    del = salSaleContractPayMethodMapper.deleteBatchIds(result);
                    //插入日志
                    result.forEach(sid -> {
                        SalSaleContractPayMethod temp = oldMaps.get(sid);
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(temp, new SalSaleContractPayMethod());
                        MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
                    });
                }
                //找出与旧的不同
                updateList.forEach(item -> {
                    SalSaleContractPayMethod temp = oldMaps.get(item.getContractPayMethodSid());
                    if (temp != null) {
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(temp, item);
                        if (msgList.size() > 0) {
                            item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            item.setUpdateDate(new Date());
                            salSaleContractPayMethodMapper.updateAllById(item);
                            MongodbUtil.insertUserLog(item.getContractPayMethodSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, null);
                        }
                    }
                });
                upd = updateList.size();
            } else {
                del = deleteSalSaleContractPayMethodByContract(contractSidList);
            }
            //新增数据
            List<SalSaleContractPayMethod> newList = list.stream().filter(o -> o.getContractPayMethodSid() == null).collect(Collectors.toList());
            add = insertSalSaleContractPayMethodList(newList);
        } else {
            add = insertSalSaleContractPayMethodList(list);
        }
    }

    /**
     * 合同提交前校验-支付方式占比只能等于1,没有则跳过
     *
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    public String submitVerifyById(Long contractSid){
        String s = "";
        List<SalSaleContractPayMethod> listYusd = salSaleContractPayMethodMapper.selectList(new QueryWrapper<SalSaleContractPayMethod>()
                .lambda().eq(SalSaleContractPayMethod::getSaleContractSid, contractSid).eq(SalSaleContractPayMethod::getAccountCategory, ConstantsFinance.ACCOUNT_CAT_YSFK));
        if (CollectionUtil.isNotEmpty(listYusd)){
            s = s + submitVerify2(listYusd, ConstantsFinance.ACCOUNT_CAT_YSFK);
        }
        List<SalSaleContractPayMethod> listZq = salSaleContractPayMethodMapper.selectList(new QueryWrapper<SalSaleContractPayMethod>()
                .lambda().eq(SalSaleContractPayMethod::getSaleContractSid, contractSid).eq(SalSaleContractPayMethod::getAccountCategory, ConstantsFinance.ACCOUNT_CAT_ZQK));
        if (CollectionUtil.isNotEmpty(listZq)){
            s = s + submitVerify2(listZq, ConstantsFinance.ACCOUNT_CAT_ZQK);
        }
        List<SalSaleContractPayMethod> listWq = salSaleContractPayMethodMapper.selectList(new QueryWrapper<SalSaleContractPayMethod>()
                .lambda().eq(SalSaleContractPayMethod::getSaleContractSid, contractSid).eq(SalSaleContractPayMethod::getAccountCategory, ConstantsFinance.ACCOUNT_CAT_WK));
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
     * @param list 销售合同信息-支付方式
     * @return 结果
     */
    @Override
    public String submitVerify(List<SalSaleContractPayMethod> list, String category){
        String name = "";
        if (CollectionUtil.isNotEmpty(list)){
            BigDecimal amountTax = BigDecimal.ZERO;
            for (int i = 0; i < list.size(); i++) {
                amountTax = amountTax.add(list.get(i).getRate());
            }
            if (amountTax.compareTo(new BigDecimal(100)) != 0) {
                if (ConstantsFinance.ACCOUNT_CAT_YSFK.equals(category)){
                    name = "预收款,";
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

    @Override
    public String submitVerify2(List<SalSaleContractPayMethod> list, String category){
        String name = "";
        if (CollectionUtil.isNotEmpty(list)){
            BigDecimal amountTax = BigDecimal.ZERO;
            for (int i = 0; i < list.size(); i++) {
                amountTax = amountTax.add(list.get(i).getRate());
            }
            if (amountTax.compareTo(new BigDecimal(1)) != 0) {
                if (ConstantsFinance.ACCOUNT_CAT_YSFK.equals(category)){
                    name = "预收款,";
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
