package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.service.IConTaxRateService;

/**
 * 税率配置Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConTaxRateServiceImpl extends ServiceImpl<ConTaxRateMapper, ConTaxRate> implements IConTaxRateService {
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "税率配置" ;

    /**
     * 查询税率配置
     *
     * @param taxRateSid 税率配置ID
     * @return 税率配置
     */
    @Override
    public ConTaxRate selectConTaxRateById(Long taxRateSid) {
        ConTaxRate conTaxRate = conTaxRateMapper.selectConTaxRateById(taxRateSid);
        MongodbUtil.find(conTaxRate);
        return conTaxRate;
    }

    /**
     * 查询税率配置列表
     *
     * @param conTaxRate 税率配置
     * @return 税率配置
     */
    @Override
    public List<ConTaxRate> selectConTaxRateList(ConTaxRate conTaxRate) {
        return conTaxRateMapper.selectConTaxRateList(conTaxRate);
    }

    /**
     * 新增税率配置
     * 需要注意编码重复校验
     *
     * @param conTaxRate 税率配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConTaxRate(ConTaxRate conTaxRate) {
        List<ConTaxRate> codeList = conTaxRateMapper.selectList(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getTaxRateCode, conTaxRate.getTaxRateCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConTaxRate> nameList = conTaxRateMapper.selectList(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getTaxRateName, conTaxRate.getTaxRateName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        if ("Y".equals(conTaxRate.getIsDefault()) && getIsDefault()){
            throw new CustomException("已存在默认税率");
        }
        int row = conTaxRateMapper.insert(conTaxRate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conTaxRate.getTaxRateSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改税率配置
     *
     * @param conTaxRate 税率配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConTaxRate(ConTaxRate conTaxRate) {
        ConTaxRate response = conTaxRateMapper.selectConTaxRateById(conTaxRate.getTaxRateSid());
        ConTaxRate tempCode = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda().eq(ConTaxRate::getTaxRateCode,conTaxRate.getTaxRateCode()));
        if (tempCode != null && !conTaxRate.getTaxRateSid().equals(tempCode.getTaxRateSid())){
            throw new CustomException(conTaxRate.getTaxRateCode()+"：税率编码已存在");
        }
        ConTaxRate tempName = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda().eq(ConTaxRate::getTaxRateName,conTaxRate.getTaxRateName()));
        if (tempName != null && !conTaxRate.getTaxRateSid().equals(tempName.getTaxRateSid())){
            throw new CustomException(conTaxRate.getTaxRateName()+"：税率名称已存在");
        }
        if ("Y".equals(conTaxRate.getIsDefault()) && getIsDefault()){
            throw new CustomException("已存在默认税率");
        }
        int row = conTaxRateMapper.updateById(conTaxRate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conTaxRate.getTaxRateSid(), BusinessType.UPDATE.getValue(), response, conTaxRate, TITLE);
        }
        return row;
    }

    /**
     * 变更税率配置
     *
     * @param conTaxRate 税率配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConTaxRate(ConTaxRate conTaxRate) {
        ConTaxRate response = conTaxRateMapper.selectConTaxRateById(conTaxRate.getTaxRateSid());
        List<ConTaxRate> nameList = conTaxRateMapper.selectList(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getTaxRateName, conTaxRate.getTaxRateName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            nameList.forEach(o ->{
                if (!conTaxRate.getTaxRateSid().equals(o.getTaxRateSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        if ("Y".equals(conTaxRate.getIsDefault()) && getIsDefault()){
            throw new CustomException("已存在默认税率");
        }
        conTaxRate.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conTaxRateMapper.updateAllById(conTaxRate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conTaxRate.getTaxRateSid(), BusinessType.CHANGE.getValue(), response, conTaxRate, TITLE);
        }
        return row;
    }

    /**
     * 批量删除税率配置
     *
     * @param taxRateSids 需要删除的税率配置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConTaxRateByIds(List<Long> taxRateSids) {
        return conTaxRateMapper.deleteBatchIds(taxRateSids);
    }

    /**
     * 启用/停用
     *
     * @param conTaxRate
     * @return
     */
    @Override
    public int changeStatus(ConTaxRate conTaxRate) {
        int row = 0;
        Long[] sids = conTaxRate.getTaxRateSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conTaxRate.setTaxRateSid(id);
                row = conTaxRateMapper.updateById(conTaxRate);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conTaxRate.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用" ;
                MongodbUtil.insertUserLog(conTaxRate.getTaxRateSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conTaxRate
     * @return
     */
    @Override
    public int check(ConTaxRate conTaxRate) {
        int row = 0;
        Long[] sids = conTaxRate.getTaxRateSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conTaxRate.setTaxRateSid(id);
                row = conTaxRateMapper.updateById(conTaxRate);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conTaxRate.getTaxRateSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConTaxRate> getConTaxRateList() {
        return conTaxRateMapper.getConTaxRateList();
    }

    //获取下拉框
    @Override
    public List<ConTaxRate> getList(ConTaxRate conTaxRate) {
        return conTaxRateMapper.getList(conTaxRate);
    }

    public Boolean getIsDefault(){
        List<ConTaxRate> conTaxRateList = conTaxRateMapper.getConTaxRateList();
        for (ConTaxRate item : conTaxRateList){
            if ("Y".equals(item.getIsDefault())){
                return true;
            }
        }
        return false;
    }
}
