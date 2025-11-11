package com.platform.ems.plug.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.plug.domain.ConDataobjectCategory;
import com.platform.ems.plug.mapper.ConDataobjectCategoryMapper;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConDataobjectCodeRuleMapper;
import com.platform.ems.plug.domain.ConDataobjectCodeRule;
import com.platform.ems.plug.service.IConDataobjectCodeRuleService;

/**
 * 数据对象类别编码规则Service业务层处理
 *
 * @author chenkw
 * @date 2021-11-25
 */
@Service
@SuppressWarnings("all")
public class ConDataobjectCodeRuleServiceImpl extends ServiceImpl<ConDataobjectCodeRuleMapper,ConDataobjectCodeRule>  implements IConDataobjectCodeRuleService {
    @Autowired
    private ConDataobjectCodeRuleMapper conDataobjectCodeRuleMapper;
    @Autowired
    private ConDataobjectCategoryMapper conDataobjectCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "数据对象类别编码规则";
    /**
     * 查询数据对象类别编码规则
     *
     * @param sid 数据对象类别编码规则ID
     * @return 数据对象类别编码规则
     */
    @Override
    public ConDataobjectCodeRule selectConDataobjectCodeRuleById(Long sid) {
        ConDataobjectCodeRule conDataobjectCodeRule = conDataobjectCodeRuleMapper.selectConDataobjectCodeRuleById(sid);
        MongodbUtil.find(conDataobjectCodeRule);
        return  conDataobjectCodeRule;
    }

    /**
     * 查询数据对象类别编码规则列表
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 数据对象类别编码规则
     */
    @Override
    public List<ConDataobjectCodeRule> selectConDataobjectCodeRuleList(ConDataobjectCodeRule conDataobjectCodeRule) {
        return conDataobjectCodeRuleMapper.selectConDataobjectCodeRuleList(conDataobjectCodeRule);
    }

    /**
     * 新增数据对象类别编码规则
     * 需要注意编码重复校验
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDataobjectCodeRule(ConDataobjectCodeRule conDataobjectCodeRule) {
        //简单校验区间
        conDataobjectCodeRule = setConfirm(conDataobjectCodeRule);
        int row= conDataobjectCodeRuleMapper.insert(conDataobjectCodeRule);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDataobjectCodeRule.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改数据对象类别编码规则
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDataobjectCodeRule(ConDataobjectCodeRule conDataobjectCodeRule) {
        //简单校验区间
        conDataobjectCodeRule = setConfirm(conDataobjectCodeRule);
        ConDataobjectCodeRule response = conDataobjectCodeRuleMapper.selectConDataobjectCodeRuleById(conDataobjectCodeRule.getSid());
        int row=conDataobjectCodeRuleMapper.updateById(conDataobjectCodeRule);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDataobjectCodeRule.getSid(), BusinessType.UPDATE.ordinal(), response,conDataobjectCodeRule,TITLE);
        }
        return row;
    }

    /**
     * 变更数据对象类别编码规则
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDataobjectCodeRule(ConDataobjectCodeRule conDataobjectCodeRule) {
        //简单校验区间
        conDataobjectCodeRule = setConfirm(conDataobjectCodeRule);
        ConDataobjectCodeRule response = conDataobjectCodeRuleMapper.selectConDataobjectCodeRuleById(conDataobjectCodeRule.getSid());
        int row=conDataobjectCodeRuleMapper.updateAllById(conDataobjectCodeRule);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDataobjectCodeRule.getSid(), BusinessType.CHANGE.ordinal(), response,conDataobjectCodeRule,TITLE);
        }
        return row;
    }

    /**
     * 批量删除数据对象类别编码规则
     *
     * @param sids 需要删除的数据对象类别编码规则ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDataobjectCodeRuleByIds(List<Long> sids) {
        List<ConDataobjectCodeRule> list = conDataobjectCodeRuleMapper.selectList(new QueryWrapper<ConDataobjectCodeRule>()
                .lambda().in(ConDataobjectCodeRule::getSid,sids));
        int row = conDataobjectCodeRuleMapper.deleteBatchIds(sids);
        if (row > 0){
            list.forEach(item->{
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(item, new ConDataobjectCodeRule());
                MongodbUtil.insertUserLog(item.getSid(),BusinessType.DELETE.getValue(), msgList,TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     * @param conDataobjectCodeRule
     * @return
     */
    @Override
    public int changeStatus(ConDataobjectCodeRule conDataobjectCodeRule){
        int row=0;
        //如果是启用
        if (conDataobjectCodeRule.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
            List<ConDataobjectCodeRule> list = conDataobjectCodeRuleMapper.selectList(new QueryWrapper<ConDataobjectCodeRule>()
                    .lambda().eq(ConDataobjectCodeRule::getDataobjectCategorySid,conDataobjectCodeRule.getDataobjectCategorySid())
                    .eq(ConDataobjectCodeRule::getBusinessCategory,conDataobjectCodeRule.getBusinessCategory())
                    .eq(ConDataobjectCodeRule::getStatus,ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(list)){
                throw new BaseException("更改状态失败,同一个业务类别下只能存在一个启用规则");
            }
        }
        Long[] sids=conDataobjectCodeRule.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDataobjectCodeRuleMapper.update(null, new UpdateWrapper<ConDataobjectCodeRule>().lambda().set(ConDataobjectCodeRule::getStatus ,conDataobjectCodeRule.getStatus() )
                    .in(ConDataobjectCodeRule::getSid,sids));
            for(Long id:sids){
                conDataobjectCodeRule.setSid(id);
                row=conDataobjectCodeRuleMapper.updateById( conDataobjectCodeRule);
                if(row==0){
                    throw new BaseException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.status(conDataobjectCodeRule.getSid(), conDataobjectCodeRule.getStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDataobjectCodeRule
     * @return
     */
    @Override
    public int check(ConDataobjectCodeRule conDataobjectCodeRule){
        int row=0;
        Long[] sids=conDataobjectCodeRule.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDataobjectCodeRuleMapper.update(null,new UpdateWrapper<ConDataobjectCodeRule>().lambda().set(ConDataobjectCodeRule::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConDataobjectCodeRule::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, conDataobjectCodeRule.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 根据条件查询当前编码值
     * @param entity ConDataobjectCodeRule
     * @return String
     */
    @Override
    public ConDataobjectCodeRule selectCurrentNumberByRule(ConDataobjectCodeRule conDataobjectCodeRule) {
        try {
            ConDataobjectCodeRule codeRule = conDataobjectCodeRuleMapper.selectCurrentNumberByRule(conDataobjectCodeRule);
            return codeRule;
        }catch (Exception e){
            throw new CustomException("编码规则配置重复，请联系管理员");
        }
    }

    /**
     * 修改当前编码值
     * @param entity ConDataobjectCodeRule
     * @return Integer
     */
    @Override
    public int addCurrentNumber(ConDataobjectCodeRule entity) {
        return conDataobjectCodeRuleMapper.addCurrentNumber(entity);
    }

    /**
     * 简单校验
     * @param entity
     * @return
     */
    private ConDataobjectCodeRule setConfirm(ConDataobjectCodeRule entity){
        if (entity.getSerialNumberFrom().longValue() > entity.getSerialNumberTo().longValue()){
            throw new BaseException("流水号(至) 不能小于流水号(起)");
        }
        List<ConDataobjectCodeRule> list = conDataobjectCodeRuleMapper.selectList(new QueryWrapper<ConDataobjectCodeRule>()
                .lambda().eq(ConDataobjectCodeRule::getDataobjectCategorySid,entity.getDataobjectCategorySid())
                .eq(ConDataobjectCodeRule::getBusinessCategory,entity.getBusinessCategory()));
        //过滤自身
        if (entity.getSid() != null){
            list = list.stream().filter(item->!item.getSid().equals(entity.getSid())).collect(Collectors.toList());
        }else {
            entity.setSerialNumberCurrent(entity.getSerialNumberFrom());
        }
        list.forEach(item->{
            if (StrUtil.isNotBlank(entity.getPrefix())){
                if (item.getPrefix() == null){
                    return;
                }
                //寻找前缀相同
                if (entity.getPrefix().equals(item.getPrefix())){
                    if (!judgeRangeOverlap(entity.getSerialNumberFrom(), entity.getSerialNumberTo(), item.getSerialNumberFrom(), item.getSerialNumberTo())){
                        throw new BaseException("本次流水号与已存在的流水号存在交集");
                    }
                }
            }else {
                //匹配没有前缀
                if (item.getPrefix() == null){
                    if (!judgeRangeOverlap(entity.getSerialNumberFrom(), entity.getSerialNumberTo(), item.getSerialNumberFrom(), item.getSerialNumberTo())){
                        throw new BaseException("本次流水号与已存在的流水号存在交集");
                    }
                }
            }
        });
        ConDataobjectCategory conDataobjectCategory = conDataobjectCategoryMapper.selectById(entity.getDataobjectCategorySid());
        if (conDataobjectCategory != null){
            entity.setHandleStatus(conDataobjectCategory.getHandleStatus())
                    .setStatus(ConstantsEms.DISENABLE_STATUS)
                    .setConfirmerAccount(conDataobjectCategory.getConfirmerAccount())
                    .setConfirmDate(conDataobjectCategory.getConfirmDate());
        }
        return entity;
    }

    /**
     * 校验两个区间范围是否重叠
     * first_start 第一区间的起
     * first_end 第一区间的止
     * second_start 第二区间的起
     * second_end 第二区间的止
     * ---前提起要小于止---
     * @return
     */
    private boolean judgeRangeOverlap(Long first_start,Long first_end,Long second_start,Long second_end){
        if (first_end.longValue() < second_start.longValue()){
            return true;
        }
        else if (first_start.longValue() > second_end.longValue()){
            return true;
        }
        else {
            return false;
        }
    }


}
